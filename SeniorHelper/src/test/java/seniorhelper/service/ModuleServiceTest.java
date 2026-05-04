package seniorhelper.service;

import seniorhelper.entities.Module;
import seniorhelper.error.NotFoundException;
import seniorhelper.model.ModuleDto;
import seniorhelper.repository.ModuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ModuleServiceTest {

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private ModuleService moduleService;

    @Test   // findAll()
    void findAll_ShouldReturnModuleList() {
        Module module1 = new Module();
        module1.setId(1);
        module1.setTitle("Recognizing Common Scam Tactics");

        Module module2 = new Module();
        module2.setId(2);
        module2.setTitle("Password Safety and Account Protection");

        when(moduleRepository.findAll()).thenReturn(Arrays.asList(module1, module2));

        // When
        List<ModuleDto> result = moduleService.findAll();

        // Then
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getTitle())
                .isEqualTo("Recognizing Common Scam Tactics");
        verify(moduleRepository, times(1)).findAll();
    }

    @Test   // save(Module)
    void save_ShouldPersistModuleEntity() {
        // Given
        ModuleDto moduleToSave = new ModuleDto();
        moduleToSave.setTitle("How to Secure Digital Accounts");
        moduleToSave.setDescription("Exploring various ways to secure your digital accounts.");

        Module savedModule = new Module();
        savedModule.setId(1);
        savedModule.setTitle(moduleToSave.getTitle());
        savedModule.setDescription(moduleToSave.getDescription());

        when(moduleRepository.save(any(Module.class))).thenReturn(savedModule);

        // When
        ModuleDto result = moduleService.createModule(moduleToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("How to Secure Digital Accounts");
        verify(moduleRepository, times(1)).save(any(Module.class));
    }

    @Test   // Retrieval by ID
    void findById_ShouldReturnModuleDTO_WhenModuleExists() {
        // Given
        Integer moduleId = 1;
        Module module = new Module();
        module.setId(moduleId);
        module.setTitle("Test Module");
        module.setDescription("Test Module Description");

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        // When
        ModuleDto result = moduleService.findById(moduleId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Module");
        assertThat(result.getDescription()).isEqualTo("Test Module Description");
        verify(moduleRepository, times(1)).findById(moduleId);
    }

    @Test   // deleteById()
    void deleteById_ShouldDeleteModule() {
        //Given
        Integer moduleId = 1;
        Module module = new Module();
        module.setId(moduleId);
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));

        // When
        moduleService.delete(moduleId);

        // Then
        verify(moduleRepository, times(1)).deleteById(moduleId);
    }

    @Test   // deleteById() throws Exception when module doesn't exist
    void deleteById_ShouldThrow_WhenModuleDoesNotExist() {
        // Given
        Integer moduleId = 99;
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> moduleService.delete(moduleId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.valueOf(moduleId));

        verify(moduleRepository, never()).deleteById(moduleId);
    }
}
