package edu.fscj.cop3024c.seniorhelper.model;

import edu.fscj.cop3024c.seniorhelper.entities.Appointment;

    // AppointmentMapper.java
    public final class AppointmentMapper {
        private AppointmentMapper() {}

        public static AppointmentDto toDto(Appointment a) {
            return new AppointmentDto(
                    a.getId(),
                    a.getTitle(),
                    a.getNotes(),
                    a.getLocation(),
                    a.getStart(),
                    a.getEnd()
            );
        }

        // For creation/update you’ll usually set the senior in the service
        public static void copyUpdateFields(Appointment target, AppointmentDto src) {
            target.setTitle(src.getTitle());
            target.setNotes(src.getNotes());
            target.setLocation(src.getLocation());
            target.setStart(src.getStart());
            target.setEnd(src.getEnd());
        }
    }
