package com.cert.calibration_hub.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    @Value("${app.upload.dir}")
    private String uploadDir;

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

    /**
     * Active le compte a partir du lien envoye par email, puis connecte
     * directement l'utilisateur (genere un JWT), afin que le frontend
     * puisse ouvrir la session sans repasser par le formulaire de connexion.
     */
    @Transactional
    public LoginResponse activateAccountAndLogin(String token, String ipAddress) {
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

        loginHistoryRepository.save(new LoginHistory(user, ipAddress, true));

        String jwt = jwtService.generateToken(user);
        return new LoginResponse(jwt, user.getUsername(), user.getRole().name());
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

    @Transactional(readOnly = true)
    public boolean isPasswordResetTokenValid(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(PasswordResetToken::isValid)
                .orElse(false);
    }

    public List<UserResponseDTO> lister() {
        return userRepository.findAll().stream().map(this::versDTO).toList();
    }

    public UserResponseDTO trouverParUsername(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + username));
        return versDTO(u);
    }

    public UserResponseDTO trouverParId(Long id) {
        return versDTO(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO monProfil(String username) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + username));
        return versDTO(u);
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
    public UserResponseDTO updatePhoto(String username, MultipartFile fichier) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + username));

        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier reçu.");
        }

        String contentType = fichier.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image.");
        }
        if (fichier.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("L'image ne doit pas dépasser 5 Mo.");
        }

        try {
            Path dossierAvatars = Paths.get(uploadDir, "avatars");
            Files.createDirectories(dossierAvatars);

            String extension = "";
            String nomOriginal = fichier.getOriginalFilename();
            if (nomOriginal != null && nomOriginal.contains(".")) {
                extension = nomOriginal.substring(nomOriginal.lastIndexOf('.'));
            }
            String nomFichier = "user_" + u.getId() + "_" + UUID.randomUUID() + extension;
            Path cheminComplet = dossierAvatars.resolve(nomFichier);

            String ancienChemin = u.getPhotoPath();

            fichier.transferTo(cheminComplet);

            u.setPhotoPath(cheminComplet.toString());
            u.setPhotoContentType(contentType);
            UserResponseDTO reponse = versDTO(userRepository.save(u));

            if (ancienChemin != null) {
                try {
                    Files.deleteIfExists(Paths.get(ancienChemin));
                } catch (IOException ignored) {
                    // suppression best-effort de l'ancienne photo
                }
            }

            return reponse;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de la photo : " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public User getUserForPhoto(Long id) {
        return getOrThrow(id);
    }

    @Transactional
    public void supprimer(Long id) {
        User u = getOrThrow(id);

        // Un utilisateur peut être référencé par l'historique de connexion
        // et par les tokens d'activation / réinitialisation. On supprime ces
        // dépendances avant le compte pour éviter une violation de clé étrangère.
        verificationTokenRepository.deleteByUser(u);
        passwordResetTokenRepository.deleteByUser(u);
        loginHistoryRepository.deleteByUser(u);

        userRepository.delete(u);
    }

    private User getOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable, id = " + id));
    }

    private UserResponseDTO versDTO(User u) {
        return new UserResponseDTO(
                u.getId(), u.getUsername(), u.getNom(), u.getPrenom(), u.getEmail(),
                u.getRole(), u.isEnabled(), u.isEmailVerified(), u.getCreatedAt(),
                u.getPhotoPath() != null ? "/api/auth/photo/" + u.getId() : null
        );
    }
}

