package com.cert.calibration_hub.user;

import com.cert.calibration_hub.user.dto.RoleUpdateDTO;
import com.cert.calibration_hub.user.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> lister() {
        return ResponseEntity.ok(userService.lister());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.trouverParId(id));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> changerRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateDTO dto) {
        return ResponseEntity.ok(userService.changerRole(id, dto.role()));
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<UserResponseDTO> activer(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activerDesactiver(id, true));
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<UserResponseDTO> desactiver(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activerDesactiver(id, false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        userService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}