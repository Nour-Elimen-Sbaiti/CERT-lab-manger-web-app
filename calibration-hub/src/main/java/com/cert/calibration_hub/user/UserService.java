package com.cert.calibration_hub.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import com.cert.calibration_hub.user.dto.UserResponseDTO;
import com.cert.calibration_hub.user.dto.RoleUpdateDTO;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       VerificationTokenRepository verificationTokenRepository,
                       LoginHistoryRepository loginHistoryRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }



    @Transactional
    public User registerUser(String username, String rawPassword, String nom,
                             String prenom, String email, Role role) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Cette adresse email est déjà utilisée.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setRole(role);
        user.setEnabled(false);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                savedUser, token, LocalDateTime.now().plusHours(24)
        );
        verificationTokenRepository.save(verificationToken);

        emailService.sendVerificationEmail(savedUser.getEmail(), token);

        return savedUser;
    }

    @Transactional
    public void activateAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de vérification invalide."));

        if (verificationToken.isExpired()) {
            throw new IllegalArgumentException("Ce lien d'activation a expiré.");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }
    @Transactional
    public LoginResponse login(String username, String rawPassword, String ipAddress) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            if (user != null) {
                loginHistoryRepository.save(new LoginHistory(user, ipAddress, false));
            }
            throw new IllegalArgumentException("Nom d'utilisateur ou mot de passe incorrect.");
        }

        if (!user.isEnabled()) {
            loginHistoryRepository.save(new LoginHistory(user, ipAddress, false));
            throw new IllegalArgumentException("Ce compte n'est pas encore activé. Vérifiez votre email.");
        }

        loginHistoryRepository.save(new LoginHistory(user, ipAddress, true));

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getUsername(), user.getRole().name());
    }
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                user, token, LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de réinitialisation invalide."));

        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Ce lien de réinitialisation a expiré ou a déjà été utilisé.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    public List<UserResponseDTO> lister() {
        return userRepository.findAll().stream().map(this::versDTO).toList();
    }

    public UserResponseDTO trouverParId(Long id) {
        return versDTO(getOrThrow(id));
    }

    @Transactional
    public UserResponseDTO changerRole(Long id, Role nouveauRole) {
        User u = getOrThrow(id);
        u.setRole(nouveauRole);
        return versDTO(userRepository.save(u));
    }

    @Transactional
    public UserResponseDTO activerDesactiver(Long id, boolean enabled) {
        User u = getOrThrow(id);
        u.setEnabled(enabled);
        return versDTO(userRepository.save(u));
    }

    @Transactional
    public void supprimer(Long id) {
        User u = getOrThrow(id);
        userRepository.delete(u);
    }

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable, id = " + id));
    }

    private UserResponseDTO versDTO(User u) {
        return new UserResponseDTO(
                u.getId(), u.getUsername(), u.getNom(), u.getPrenom(), u.getEmail(),
                u.getRole(), u.isEnabled(), u.isEmailVerified(), u.getCreatedAt()
        );
    }
}

