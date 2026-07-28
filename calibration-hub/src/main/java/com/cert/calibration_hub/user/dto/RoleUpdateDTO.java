package com.cert.calibration_hub.user.dto;

import com.cert.calibration_hub.user.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateDTO(
        @NotNull Role role
) {}