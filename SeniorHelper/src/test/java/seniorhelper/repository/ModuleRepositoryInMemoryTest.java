package seniorhelper.repository;

import seniorhelper.entities.Module;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})

public class ModuleRepositoryInMemoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ModuleRepository moduleRepository;

    @Test   // Save Module, should persist in the database.
    void saveModule_ShouldPersistModuleInDatabase() {
        Module module = new Module();
        module.setTitle("Save Module Title Test");
        module.setDescription("Save Module Description Test");

        Module savedModule = moduleRepository.save(module);
        Module found = entityManager.find(Module.class, savedModule.getId());
        assertThat(found).isEqualTo(savedModule);
    }

    @Test   // Find Module by id, should return if exists
    void findById_ShouldReturnModule_WhenExists() {
        Module module = new Module();
        module.setTitle("Return Module Test");
        module.setDescription("Return Module Description Test");
        entityManager.persistAndFlush(module);

        Optional<Module> found = moduleRepository.findById(module.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Return Module Test");
    }

    @Test   // Find Module by id, should return empty if it does not exist.
    void findById_ShouldReturnEmpty_WhenNotExists() {
        assertThat(moduleRepository.findById(999)).isEmpty();
    }
}
