package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.Appointment;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import edu.fscj.cop3024c.seniorhelper.model.AppointmentDto;
import edu.fscj.cop3024c.seniorhelper.model.AppointmentMapper;
import edu.fscj.cop3024c.seniorhelper.repository.AppointmentRepository;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Appointment createAppointmentForSenior(Integer seniorId, Appointment appointment) {
        User senior = userRepository.findById(seniorId)
                .orElseThrow(() -> new EntityNotFoundException("Senior not found: " + seniorId));
        if (senior.getRole() != Role.SENIOR) {
            throw new IllegalArgumentException("Target user is not SENIOR (id=" + seniorId + ")");
        }
        appointment.setSenior(senior);
        return appointmentRepository.save(appointment);
    }

    public List<AppointmentDto> findBySeniorId(Integer seniorId) {
        return appointmentRepository.findBySeniorId(seniorId)
                .stream()
                .map(AppointmentMapper::toDto)
                .toList();
    }

    public Appointment findById(Integer id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + id));
    }

    @Transactional
    public Appointment updateAppointment(Integer id, Appointment updated) {
        Appointment appointment = findById(id);
        appointment.setTitle(updated.getTitle());
        appointment.setNotes(updated.getNotes());
        appointment.setLocation(updated.getLocation());
        appointment.setStart(updated.getStart());
        appointment.setEnd(updated.getEnd());
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(Integer id) {
        Appointment appointment = findById(id);
        appointmentRepository.delete(appointment);
    }
    @Transactional
    public List<AppointmentDto> findAll() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentMapper::toDto)
                .toList();
    }

    @Transactional
    public Appointment create(Integer seniorId, Appointment body) {
        return createAppointmentForSenior(seniorId, body);
    }

    @Transactional
    public Appointment update(Integer appointmentId, Integer seniorId, Appointment body) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getSenior() == null || appointment.getSenior().getId() == null ||
                !appointment.getSenior().getId().equals(seniorId)) {
            throw new IllegalArgumentException("Appointment does not belong to seniorId=" + seniorId);
        }
        return updateAppointment(appointmentId, body);
    }

    @Transactional
    public void delete(Integer appointmentId, Integer seniorId) {
        Appointment appointment = findById(appointmentId);
        if (appointment.getSenior() == null || appointment.getSenior().getId() == null ||
                !appointment.getSenior().getId().equals(seniorId)) {
            throw new IllegalArgumentException("Appointment does not belong to seniorId=" + seniorId);
        }
        deleteAppointment(appointmentId);
    }
}
