package com.cert.calibration_hub.user;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

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
    public ResponseEntity<Void> verify(@RequestParam String token, HttpServletRequest httpRequest) {
        try {
            LoginResponse loginResponse = userService.activateAccountAndLogin(token, httpRequest.getRemoteAddr());

            String redirectUrl = frontendUrl + "/"
                    + "?token=" + encode(loginResponse.getToken())
                    + "&username=" + encode(loginResponse.getUsername())
                    + "&role=" + encode(loginResponse.getRole());

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        } catch (IllegalArgumentException e) {
            String redirectUrl = frontendUrl + "/?verificationError=" + encode(e.getMessage());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, redirectUrl)
                    .build();
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
        try {
            return ResponseEntity.ok(userService.monProfil(principal.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping(value = "/me/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyPhoto(java.security.Principal principal,
                                            @RequestParam("photo") MultipartFile photo) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non authentifié.");
        }
        try {
            return ResponseEntity.ok(userService.updatePhoto(principal.getName(), photo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> photo(@PathVariable Long id) {
        try {
            User user = userService.getUserForPhoto(id);
            if (user.getPhotoPath() == null) {
                return ResponseEntity.notFound().build();
            }
            byte[] donnees = Files.readAllBytes(Paths.get(user.getPhotoPath()));
            MediaType type;
            try {
                type = MediaType.parseMediaType(
                        user.getPhotoContentType() != null ? user.getPhotoContentType() : "image/jpeg");
            } catch (Exception e) {
                type = MediaType.IMAGE_JPEG;
            }
            return ResponseEntity.ok().contentType(type).body(donnees);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        userService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Si cet email existe dans notre système, un lien de réinitialisation a été envoyé.");
    }

    @GetMapping(value = "/reset-password", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> resetPasswordForm(@RequestParam String token) {
        if (!userService.isPasswordResetTokenValid(token)) {
            return ResponseEntity.badRequest().body(pageErreur(
                    "Lien invalide ou expiré",
                    "Ce lien de réinitialisation est invalide, expiré ou déjà utilisé. Veuillez refaire une demande de mot de passe oublié."
            ));
        }

        return ResponseEntity.ok(pageFormulaireReset(token, null));
    }

    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@Valid @RequestBody NewPasswordRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès. Vous pouvez maintenant vous connecter.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> resetPasswordDepuisFormulaire(@RequestParam String token,
                                                                 @RequestParam String newPassword,
                                                                 @RequestParam String confirmPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.badRequest().body(pageFormulaireReset(token,
                    "Le mot de passe doit contenir au moins 8 caractères."));
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(pageFormulaireReset(token,
                    "Les deux mots de passe ne correspondent pas."));
        }

        try {
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(pageSucces());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(pageErreur("Réinitialisation impossible", e.getMessage()));
        }
    }

    private String pageFormulaireReset(String token, String erreur) {
        String messageErreur = erreur == null ? "" : "<div class=\"alert\">" + echapperHtml(erreur) + "</div>";
        String tokenEchappe = echapperHtml(token);

        return """
                <!doctype html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Réinitialisation du mot de passe - Calibration Hub</title>
                    <style>
                        * { box-sizing: border-box; }
                        body {
                            margin: 0;
                            font-family: Arial, Helvetica, sans-serif;
                            background: linear-gradient(135deg, #0f4c81, #1f7a8c);
                            min-height: 100vh;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            padding: 24px;
                            color: #1f2937;
                        }
                        .card {
                            width: 100%;
                            max-width: 460px;
                            background: #ffffff;
                            border-radius: 16px;
                            box-shadow: 0 20px 45px rgba(0,0,0,0.18);
                            padding: 32px;
                        }
                        .logo {
                            width: 56px;
                            height: 56px;
                            border-radius: 14px;
                            background: #0f4c81;
                            color: white;
                            display: flex;
                            align-items: center;
                            justify-content: center;
                            font-weight: 700;
                            margin-bottom: 18px;
                        }
                        h1 { margin: 0 0 10px; font-size: 24px; color: #0f4c81; }
                        p { margin: 0 0 22px; color: #4b5563; line-height: 1.5; }
                        label { display: block; font-weight: 700; margin: 14px 0 8px; }
                        input {
                            width: 100%;
                            border: 1px solid #d1d5db;
                            border-radius: 10px;
                            padding: 12px 14px;
                            font-size: 15px;
                            outline: none;
                        }
                        input:focus { border-color: #0f4c81; box-shadow: 0 0 0 3px rgba(15,76,129,0.12); }
                        button {
                            width: 100%;
                            border: 0;
                            border-radius: 10px;
                            padding: 13px 18px;
                            margin-top: 22px;
                            background: #0f4c81;
                            color: #ffffff;
                            font-weight: 700;
                            font-size: 15px;
                            cursor: pointer;
                        }
                        button:hover { background: #0b3d68; }
                        .alert {
                            background: #fee2e2;
                            color: #991b1b;
                            border: 1px solid #fecaca;
                            border-radius: 10px;
                            padding: 12px;
                            margin-bottom: 16px;
                        }
                        .hint { font-size: 13px; color: #6b7280; margin-top: 10px; }
                    </style>
                </head>
                <body>
                    <main class="card">
                        <div class="logo">CERT</div>
                        <h1>Nouveau mot de passe</h1>
                        <p>Veuillez saisir votre nouveau mot de passe pour finaliser la réinitialisation.</p>
                        __MESSAGE_ERREUR__
                        <form method="post" action="/api/auth/reset-password">
                            <input type="hidden" name="token" value="__TOKEN__">
                            <label for="newPassword">Nouveau mot de passe</label>
                            <input id="newPassword" name="newPassword" type="password" minlength="8" required autocomplete="new-password">
                            <label for="confirmPassword">Confirmer le mot de passe</label>
                            <input id="confirmPassword" name="confirmPassword" type="password" minlength="8" required autocomplete="new-password">
                            <div class="hint">Minimum 8 caractères.</div>
                            <button type="submit">Réinitialiser le mot de passe</button>
                        </form>
                    </main>
                </body>
                </html>
                """
                .replace("__MESSAGE_ERREUR__", messageErreur)
                .replace("__TOKEN__", tokenEchappe);
    }

    private String pageSucces() {
        return """
                <!doctype html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Mot de passe réinitialisé</title>
                    <style>
                        body { margin:0; font-family:Arial,Helvetica,sans-serif; background:#f3f7fb; min-height:100vh; display:flex; align-items:center; justify-content:center; padding:24px; }
                        .card { max-width:480px; background:white; border-radius:16px; box-shadow:0 16px 40px rgba(15,76,129,.14); padding:32px; text-align:center; }
                        h1 { color:#0f4c81; margin-top:0; }
                        p { color:#4b5563; line-height:1.5; }
                        .ok { width:58px; height:58px; border-radius:50%; background:#dcfce7; color:#166534; display:flex; align-items:center; justify-content:center; margin:0 auto 18px; font-size:30px; font-weight:700; }
                    </style>
                </head>
                <body>
                    <main class="card">
                        <div class="ok">✓</div>
                        <h1>Mot de passe réinitialisé</h1>
                        <p>Votre mot de passe a été modifié avec succès. Vous pouvez maintenant vous connecter avec votre nouveau mot de passe.</p>
                    </main>
                </body>
                </html>
                """;
    }

    private String pageErreur(String titre, String message) {
        return """
                <!doctype html>
                <html lang="fr">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body { margin:0; font-family:Arial,Helvetica,sans-serif; background:#fff7f7; min-height:100vh; display:flex; align-items:center; justify-content:center; padding:24px; }
                        .card { max-width:520px; background:white; border-radius:16px; box-shadow:0 16px 40px rgba(153,27,27,.12); padding:32px; text-align:center; }
                        h1 { color:#991b1b; margin-top:0; }
                        p { color:#4b5563; line-height:1.5; }
                    </style>
                </head>
                <body>
                    <main class="card">
                        <h1>%s</h1>
                        <p>%s</p>
                    </main>
                </body>
                </html>
                """.formatted(echapperHtml(titre), echapperHtml(titre), echapperHtml(message));
    }

    private String echapperHtml(String valeur) {
        if (valeur == null) {
            return "";
        }
        return valeur
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
