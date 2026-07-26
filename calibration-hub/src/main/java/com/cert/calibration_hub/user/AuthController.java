package com.cert.calibration_hub.user;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (!EmailDomainValidator.domainAcceptsMail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body("Le domaine de cet email semble invalide ou inexistant. Vérifie l'adresse saisie.");
        }

        try {
            User user = userService.registerUser(
                    request.getUsername(),
                    request.getPassword(),
                    request.getNom(),
                    request.getPrenom(),
                    request.getEmail(),
                    request.getRole()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Compte créé pour " + user.getUsername()
                            + ". Un email d'activation a été envoyé à " + user.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        try {
            userService.activateAccount(token);
            return ResponseEntity.ok("Compte activé avec succès. Vous pouvez maintenant vous connecter.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {
        try {
            String ipAddress = httpRequest.getRemoteAddr();
            LoginResponse response = userService.login(
                    request.getUsername(),
                    request.getPassword(),
                    ipAddress
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié.");
        }
        return ResponseEntity.ok("Utilisateur connecté : " + principal.getName());
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Si cet email existe dans notre système, un lien de réinitialisation a été envoyé.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès. Vous pouvez maintenant vous connecter.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
