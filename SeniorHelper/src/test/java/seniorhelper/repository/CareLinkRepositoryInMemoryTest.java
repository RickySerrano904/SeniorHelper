package seniorhelper.repository;

import seniorhelper.entities.User;
import seniorhelper.entities.CareLink;
import seniorhelper.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {"spring.sql.init.mode=never"})
public class CareLinkRepositoryInMemoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CareLinkRepository careLinkRepository;

    @Test
    void saveCareLink_ShouldPersistCareLinkInDatabase() {

        User caregiver = new User();
        caregiver.setUsername("CaregiverName");
        caregiver.setRole(Role.CAREGIVER);
        entityManager.persist(caregiver);

        User senior = new User();
        senior.setUsername("SeniorName");
        senior.setRole(Role.SENIOR);
        entityManager.persist(senior);

        CareLink careLink = new CareLink();
        careLink.setCaregiver(caregiver);
        careLink.setSenior(senior);

        CareLink saved = careLinkRepository.save(careLink);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCaregiver().getUsername()).isEqualTo("CaregiverName");
        assertThat(saved.getSenior().getUsername()).isEqualTo("SeniorName");
    }

    @Test
    void findById_ShouldReturnCareLink_WhenExists() {

        User caregiver = new User();
        caregiver.setUsername("Caregiver1");
        caregiver.setRole(Role.CAREGIVER);
        entityManager.persist(caregiver);

        User senior = new User();
        senior.setUsername("Senior1");
        senior.setRole(Role.SENIOR);
        entityManager.persist(senior);

        CareLink careLink = new CareLink();
        careLink.setCaregiver(caregiver);
        careLink.setSenior(senior);

        CareLink saved = entityManager.persistFlushFind(careLink);

        Optional<CareLink> found = careLinkRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getCaregiver().getUsername()).isEqualTo("Caregiver1");
        assertThat(found.get().getSenior().getUsername()).isEqualTo("Senior1");
    }

}
