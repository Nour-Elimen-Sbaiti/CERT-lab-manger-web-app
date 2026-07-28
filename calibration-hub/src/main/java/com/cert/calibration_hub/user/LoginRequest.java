package com.cert.calibration_hub.user;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    private String username;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}