package edu.fscj.cop3024c.seniorhelper.model;

import edu.fscj.cop3024c.seniorhelper.entities.CareLink;

public final class CareLinkMapper {
    private CareLinkMapper() {}

    public static CareLinkDto toDto(CareLink link) {
        var caregiver = link.getCaregiver();
        var senior = link.getSenior();
        return new CareLinkDto(
                link.getId(),
                caregiver != null ? caregiver.getId() : null,
                caregiver != null ? caregiver.getUsername() : null,
                caregiver != null && caregiver.getRole() != null ? caregiver.getRole().name() : null,
                caregiver != null ? caregiver.getFirstName() : null,
                caregiver != null ? caregiver.getLastName() : null,
                senior != null ? senior.getId() : null,
                senior != null ? senior.getUsername() : null,
                senior != null && senior.getRole() != null ? senior.getRole().name() : null,
                senior != null ? senior.getFirstName() : null,
                senior != null ? senior.getLastName() : null,
                link.getConnectedSince(),
                link.getStatus() != null ? link.getStatus().name() : null
        );
    }
}
