package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.Lesson;
import edu.fscj.cop3024c.seniorhelper.entities.Module;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.LessonDto;
import edu.fscj.cop3024c.seniorhelper.model.ModuleDto;
import edu.fscj.cop3024c.seniorhelper.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final LessonService lessonService;
    private final QuizService quizService;

    public ModuleService(ModuleRepository moduleRepository, LessonService lessonService,
                         QuizService quizService) {
        this.moduleRepository = moduleRepository;
        this.lessonService = lessonService;
        this.quizService = quizService;
    }
    // Find all modules.
    @Transactional(readOnly = true)
    public List<ModuleDto> findAll() {
        return moduleRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    // Find one module.
    @Transactional(readOnly = true)
    public ModuleDto findById(Integer id) {
        return moduleRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() ->
                        new NotFoundException("Unable to locate 'Module' associated with ID: " + id));
    }
    // Create new module.
    @Transactional
    public ModuleDto createModule(ModuleDto moduleDto) {
        Module module = new Module();
        module.setTitle(moduleDto.getTitle());
        module.setDescription(moduleDto.getDescription());

        if (moduleDto.getLessons() != null) {
            List<Lesson> lessons = new ArrayList<>();
            for(LessonDto lessonDto : moduleDto.getLessons()){
                Lesson lesson = new Lesson();
                lesson.setTitle(lessonDto.getTitle());
                lesson.setDescription(lessonDto.getDescription());
                lessons.add(lesson);
            }
            module.setLessons(lessons);
        }
        Module saved = moduleRepository.save(module);
        return convertToDto(saved);
    }
    // Update module.
    @Transactional
    public ModuleDto updateModule(Integer moduleId, ModuleDto moduleDto) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found with ID: " + moduleId));

        if (moduleDto.getTitle() != null) { module.setTitle(moduleDto.getTitle()); }
        if (moduleDto.getDescription() != null) { module.setDescription(moduleDto.getDescription()); }

        Module saved = moduleRepository.save(module);
        return convertToDto(saved);
    }
    // Delete module.
    @Transactional
    public ModuleDto delete(Integer id) {
        Module toDelete = moduleRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Unable to locate 'Module' associated with ID: " + id));
        ModuleDto deletedDto = convertToDto(toDelete);
        moduleRepository.deleteById(id);
        return deletedDto;
    }
    // Helper methods
    private ModuleDto convertToDto(Module module) {
        ModuleDto dto = new ModuleDto();
        dto.setId(module.getId());
        dto.setTitle(module.getTitle());
        dto.setDescription(module.getDescription());
        dto.setLessons(
                module.getLessons()
                        .stream()
                        .map(lessonService::convertToDto)
                        .collect(Collectors.toList())
        );

        if (module.getQuiz() != null) {
            dto.setQuiz(quizService.convertToDto(module.getQuiz()));
        }
        return dto;
    }
}