package edu.fscj.cop3024c.seniorhelper.controller;

import edu.fscj.cop3024c.seniorhelper.entities.Appointment;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.model.AppointmentDto;
import edu.fscj.cop3024c.seniorhelper.model.AppointmentMapper;
import edu.fscj.cop3024c.seniorhelper.service.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.slf4j.profiler.TimeInstrument;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointments;

    public AppointmentController(AppointmentService appointments) {
        this.appointments = appointments;
    }

    // ---------- LIST (current user's appointments) ----------
    @GetMapping("/me")
    public List<AppointmentDto> myAppointments(@AuthenticationPrincipal User me) {
        return appointments.findBySeniorId(me.getId());
    }

    @PostMapping("/me")
    public AppointmentDto createMyAppointment(@AuthenticationPrincipal User me,
                                              @RequestBody AppointmentDto body) {
        Appointment created = appointments.createAppointmentForCurrentUser(me.getId(), dtoToEntityForWrite(body));
        return AppointmentMapper.toDto(created);
    }

    // ---------- LIST (by seniorId) ----------
    // The senior, and anyone authenticated who *has permission for that senior* can list.
    @GetMapping
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    public List<AppointmentDto> list(@RequestParam Integer seniorId) {
        return appointments.findBySeniorId(seniorId); // returns DTOs
    }

    // ---------- CREATE ----------
    // The senior, and anyone authenticated who *has permission for that senior* can create an appointment.
    @PostMapping
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    public AppointmentDto create(@RequestParam Integer seniorId,
                                 @RequestBody AppointmentDto body) {
        Profiler profiler = new Profiler("createAppointment");
        profiler.start("Create Appointment");
        try {
            Appointment created = appointments.create(seniorId, dtoToEntityForWrite(body));
            logger.info("Appointment created successfully: {}", created.getId());
            return AppointmentMapper.toDto(created);
        } catch (Exception e) {
            logger.error("Error while creating appointment {}", e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- UPDATE ----------
    // The senior, and anyone authenticated who *has permission for that senior* can modify an appointment.
    @PutMapping("/{appointmentId}")
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    public AppointmentDto update(@PathVariable Integer appointmentId,
                                 @RequestParam Integer seniorId,
                                 @RequestBody AppointmentDto body) {
        Profiler profiler = new Profiler("updateAppointment");
        profiler.start("Update Appointment");
        try {
            Appointment updated = appointments.update(appointmentId, seniorId, dtoToEntityForWrite(body));
            logger.info("Appointment updated successfully: {}", updated.getId());
            return AppointmentMapper.toDto(updated);
        } catch (Exception e) {
            logger.error("Error while updating appointment {}", e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- DELETE ----------
    // The senior, and anyone authenticated who *has permission for that senior* can delete an appointment.
    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@permissionChecker.hasPermission(principal, #seniorId)")
    public void delete(@PathVariable Integer appointmentId,
                       @RequestParam Integer seniorId) {
        Profiler profiler = new Profiler("deleteAppointment");
        profiler.start("Delete Appointment");
        try {
            appointments.delete(appointmentId, seniorId);
            logger.info("Appointment deleted successfully: {}", appointmentId);
        } catch (Exception e) {
            logger.error("Error while deleting appointment {}", e.getMessage());
            throw e;
        } finally {
            TimeInstrument ti = profiler.stop();
            ti.print();
        }
    }

    // ---------- Mapping helpers ----------
    private Appointment dtoToEntityForWrite(AppointmentDto dto) {
        Appointment a = new Appointment();
        AppointmentMapper.copyUpdateFields(a, dto);
        return a;
    }
}
