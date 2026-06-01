package seniorhelper.repository;

import seniorhelper.entities.Appointment;
import seniorhelper.entities.User;
import seniorhelper.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AppointmentRepositoryTest {
    @DynamicPropertySource
    static void disableDataSql(DynamicPropertyRegistry registry) {
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("spring.datasource.initialization-mode", () -> "never");
    }
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void saveAppointment_ShouldPersistAppointmentInDatabase() {

        User dummyUser = new User();
        dummyUser.setUsername("dummyUser");
        dummyUser.setRole(Role.SENIOR);
        dummyUser.setPasswordHash("$2a$10$vlloF3RsRY9.bVuzEVXi1eMT1utDA9yz3IUATxcO2URWBtbmp2C0e");
        dummyUser = entityManager.persist(dummyUser);

        Appointment newAppointment = new Appointment();
        newAppointment.setTitle("New Appointment");
        newAppointment.setSenior(dummyUser);
        newAppointment.setNotes("New Note");
        Appointment persisted = entityManager.persistAndFlush(newAppointment);

        Optional<Appointment> found = appointmentRepository.findById(persisted.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(persisted.getId());
        assertThat(found.get().getTitle()).isEqualTo(newAppointment.getTitle());
        assertThat(found.get().getSenior()).isEqualTo(newAppointment.getSenior());
        assertThat(found.get().getNotes()).isEqualTo(newAppointment.getNotes());
    }
    @Test
    public void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Integer nonExistentID = 999;

        Optional<Appointment> foundAppointment = appointmentRepository.findById(nonExistentID);

    assertThat(foundAppointment).isEmpty();}
}
