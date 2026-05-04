package seniorhelper.service;

import seniorhelper.entities.Appointment;
import seniorhelper.entities.User;
import seniorhelper.model.AppointmentDto;
import seniorhelper.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void findAll_ShouldReturnAppointmentDTOList() {

        User user1 = new User();
        user1.setId(1);
        user1.setUsername("User1");

        Appointment appointment1 = new Appointment();
        appointment1.setId(1);
        appointment1.setSenior(user1);
        appointment1.setTitle("New Appointment1");

        Appointment appointment2 = new Appointment();
        appointment2.setId(2);               // <-- fix: set fields on appointment2
        appointment2.setSenior(user1);
        appointment2.setTitle("New Appointment2");

        // Return both appointments in the mocked repository
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appointment1, appointment2));

        List<AppointmentDto> result = appointmentService.findAll();

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("New Appointment1");
        assertThat(result.get(1).getTitle()).isEqualTo("New Appointment2");
        verify(appointmentRepository, times(1)).findAll();
    }
    @Test
    void findById_ShouldReturnAppointmentDto_WhenAppointmentExist(){
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("User1");

        Integer appointmentId = 1;
        Appointment appointment1 = new Appointment();
        appointment1.setId(appointmentId);
        appointment1.setSenior(user1);
        appointment1.setTitle("New Appointment1");

        when(appointmentRepository.findById(appointment1.getId())).thenReturn(Optional.of(appointment1));

        Appointment result = appointmentService.findById(appointmentId);
        assertThat(result).isNotNull().isEqualTo(appointment1);
        verify(appointmentRepository, times(1)).findById(appointment1.getId());
    }
    @Test
    void deleteById_ShouldDeleteAppointment_WhenAppointmentExists() {

        User user1 = new User();
        user1.setId(1);
        user1.setUsername("User1");

        Integer appointmentId = 1;
        Appointment appointment1 = new Appointment();
        appointment1.setId(appointmentId);
        appointment1.setSenior(user1);
        appointment1.setTitle("New Appointment1");

        // Mock findById to return the appointment
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment1));

        // Call service method
        appointmentService.delete(appointmentId, 1);

        // Verify that delete was called
        verify(appointmentRepository, times(1)).delete(appointment1);
    }
}
