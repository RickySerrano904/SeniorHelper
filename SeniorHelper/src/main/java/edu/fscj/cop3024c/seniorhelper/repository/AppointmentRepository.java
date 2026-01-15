package edu.fscj.cop3024c.seniorhelper.repository;

import edu.fscj.cop3024c.seniorhelper.entities.Appointment;

@org.springframework.stereotype.Repository
public interface AppointmentRepository extends org.springframework.data.jpa.repository.JpaRepository<Appointment, Integer> {

    java.util.List<Appointment> findBySeniorId(Integer seniorId);
}
