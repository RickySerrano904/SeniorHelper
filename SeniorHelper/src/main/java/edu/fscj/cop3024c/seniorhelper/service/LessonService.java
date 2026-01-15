package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.Lesson;
import edu.fscj.cop3024c.seniorhelper.entities.Module;
import edu.fscj.cop3024c.seniorhelper.error.NotFoundException;
import edu.fscj.cop3024c.seniorhelper.model.LessonDto;
import edu.fscj.cop3024c.seniorhelper.repository.LessonRepository;
import edu.fscj.cop3024c.seniorhelper.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    public LessonService(LessonRepository lessonRepository, ModuleRepository moduleRepository) {
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
    }
    // 1. Retrieve all lessons within a module.
    @Transactional(readOnly = true)
    public List<LessonDto> findLessonsByModuleId(Integer moduleId) {
        return lessonRepository.findByModuleId(moduleId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    // 2. Retrieve lesson within a module.
    @Transactional(readOnly = true)
    public LessonDto findLessonById(Integer moduleId, Integer lessonId) {
        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new NotFoundException(
                        "Unable to locate 'Lesson' " + lessonId + " within 'Module' " + moduleId));
        return convertToDto(lesson);
    }
    // 3. Create a new lesson.
    @Transactional
    public LessonDto createLesson(Integer moduleId, LessonDto lessonDto) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(
                        "Unable to locate 'Module' associated with ID: " + moduleId));

        Lesson newLesson = new Lesson();
        newLesson.setTitle(lessonDto.getTitle());
        newLesson.setDescription(lessonDto.getDescription());
        newLesson.setModule(module);

        Lesson savedLesson = lessonRepository.save(newLesson);
        return convertToDto(savedLesson);
    }
    // 4. Update existing lesson
    @Transactional
    public LessonDto updateLesson(Integer moduleId, Integer lessonId, LessonDto lessonDto) {
        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new NotFoundException(
                        "Unable to locate 'Lesson' " + lessonId + " within 'Module' " + moduleId));

        if (lessonDto.getTitle() != null) {
            lesson.setTitle(lessonDto.getTitle());
        }
        if (lessonDto.getDescription() != null) {
            lesson.setDescription(lessonDto.getDescription());
        }

        Lesson saved = lessonRepository.save(lesson);
        return convertToDto(saved);
    }
    // 5. Delete lesson by its ID
    @Transactional
    public void deleteLesson(Integer moduleId, Integer lessonId) {
        Lesson lesson = lessonRepository.findByIdAndModuleId(lessonId, moduleId)
                .orElseThrow(() -> new NotFoundException(
                        "Lesson not found with ID: " + lessonId + " in Module with ID: " + moduleId));
        Module module = lesson.getModule();
        module.getLessons().remove(lesson);
        lessonRepository.delete(lesson);
    }
    // 6. ConvertToDto
    public LessonDto convertToDto(Lesson lesson) {
        LessonDto dto = new LessonDto();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setDescription(lesson.getDescription());
        return dto;
    }
}