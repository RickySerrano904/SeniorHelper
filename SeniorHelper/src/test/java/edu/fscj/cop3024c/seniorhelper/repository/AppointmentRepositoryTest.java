package edu.fscj.cop3024c.seniorhelper.repository;

import edu.fscj.cop3024c.seniorhelper.entities.Appointment;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
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
        dummyUser.setSalt("dummySalt");
        dummyUser.setHash("dummyHash");
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
