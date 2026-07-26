package com.cert.calibration_hub.equipement;

import com.cert.calibration_hub.equipement.dto.PersonnelRequestDTO;
import com.cert.calibration_hub.equipement.dto.PersonnelResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @PostMapping
    public ResponseEntity<PersonnelResponseDTO> creer(@Valid @RequestBody PersonnelRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personnelService.creer(dto));
    }

    @GetMapping
    public ResponseEntity<List<PersonnelResponseDTO>> lister() {
        return ResponseEntity.ok(personnelService.lister());
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        personnelService.delete(id);
    }

}

