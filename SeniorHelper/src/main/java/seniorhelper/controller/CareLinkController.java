package seniorhelper.controller;

import seniorhelper.entities.CareLink;
import seniorhelper.model.CareLinkDto;
import seniorhelper.model.CareLinkMapper;
import seniorhelper.service.CareLinkService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/care-links")
@Validated
public class CareLinkController {

    private static final Logger logger = LoggerFactory.getLogger(CareLinkController.class);
    private final CareLinkService service;

    public CareLinkController(CareLinkService service) {
        this.service = service;
    }

    // ---------- Create a caregiver–senior link ----------
    // The senior, and anyone who has permission for that senior, can create a carelink
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    public CareLinkDto createRequest(
            @RequestParam @NotNull @Positive Integer caregiverId,
            @RequestParam @NotNull @Positive Integer seniorId
    ) {
        CareLink saved = service.requestLink(caregiverId, seniorId);
        logger.info("Created care link request for caregiver {} and senior {}", caregiverId, seniorId);
        return CareLinkMapper.toDto(saved);
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #caregiverId")
    public CareLinkDto approve(
            @RequestParam @NotNull @Positive Integer caregiverId,
            @RequestParam @NotNull @Positive Integer seniorId
    ) {
        CareLink saved = service.approve(caregiverId, seniorId);
        logger.info("Approved care link between caregiver {} and senior {}", caregiverId, seniorId);
        return CareLinkMapper.toDto(saved);
    }

    // ---------- Remove a caregiver–senior link ----------
    // The senior, and anyone who has permission for that senior, can delete a carelink
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or principal.id == #caregiverId or @permissionChecker.hasPermission(principal, #seniorId)")
    public void delete(
            @RequestParam @NotNull @Positive Integer caregiverId,
            @RequestParam @NotNull @Positive Integer seniorId
    ) {
        service.remove(caregiverId, seniorId);
        logger.info("Removed link between caregiver {} and senior {}", caregiverId, seniorId);
    }

    // ---------- List seniors assigned to a caregiver ----------
    // Visible to ADMIN, or the caregiver themselves
    @GetMapping("/by-caregiver")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #caregiverId")
    public List<CareLinkDto> byCaregiver(
            @RequestParam @NotNull @Positive Integer caregiverId
    ) {
        return service.forCaregiver(caregiverId).stream()
                .map(CareLinkMapper::toDto)
                .toList();
    }

        @GetMapping("/pending/by-caregiver")
        @PreAuthorize("hasRole('ADMIN') or principal.id == #caregiverId")
        public List<CareLinkDto> pendingByCaregiver(
            @RequestParam @NotNull @Positive Integer caregiverId
        ) {
        return service.pendingForCaregiver(caregiverId).stream()
            .map(CareLinkMapper::toDto)
            .toList();
        }

    // ---------- List caregivers assigned to a senior ----------
    // The senior, and anyone who has permission for that senior, can delete a carelink
    @GetMapping("/by-senior")
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    public List<CareLinkDto> bySenior(
            @RequestParam @NotNull @Positive Integer seniorId
    ) {
        return service.forSenior(seniorId).stream()
                .map(CareLinkMapper::toDto)
                .toList();
    }
}