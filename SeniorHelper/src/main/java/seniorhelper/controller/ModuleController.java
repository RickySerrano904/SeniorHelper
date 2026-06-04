package seniorhelper.controller;

import seniorhelper.entities.Module;
import seniorhelper.model.ModuleDto;
import seniorhelper.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired private ModuleService moduleService;
    public ModuleController(ModuleService moduleService) { this.moduleService = moduleService; }

    private static final Logger logger = LoggerFactory.getLogger(ModuleController.class);

    /* ----- Module Endpoints ----- */
    // 1. Retrieve all existing modules.
    @GetMapping
    public List<ModuleDto> getAllModules() {
        return moduleService.findAll();
    }
    // 2. Retrieve an existing module.
    @GetMapping("/{id}")
    public ResponseEntity<ModuleDto> getModuleById(@PathVariable Integer id) {
        ModuleDto module = moduleService.findById(id);
        return ResponseEntity.ok(module);
    }
    // 3. Create a new module.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleDto> create(@Valid @RequestBody ModuleDto moduleDto) {
        logger.info("Attempting to create a new module with title: {}", moduleDto.getTitle());
        try {
            ModuleDto created = moduleService.createModule(moduleDto);
            logger.info("Successfully created module with ID: {}", created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating a new module, {}", e.getMessage());
            throw e;
        }
    }
    // 4. Update module by its ID.
    @PutMapping("/{moduleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModuleDto> update(@PathVariable Integer moduleId, @RequestBody ModuleDto moduleDto) {
        logger.info("Attempting to update module with ID: {}", moduleId);
        try {
            ModuleDto updated = moduleService.updateModule(moduleId, moduleDto);
            logger.info("Successfully updated module with ID: {}", updated.getId());
            return ResponseEntity.status(HttpStatus.OK).body(updated);
        } catch (Exception e) {
            logger.error("Error updating module with ID {}, {}", moduleId, e.getMessage());
            throw e;
        }
    }
    // 5. Delete module by its ID.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Module> deleteModule(@PathVariable Integer id) {
        logger.debug("Attempting to delete module with ID {}", id);
        try {
            moduleService.delete(id);
            logger.info("Successfully deleted module with ID {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting module with ID {}, {}", id, e.getMessage());
            throw e;
        }
    }
}
