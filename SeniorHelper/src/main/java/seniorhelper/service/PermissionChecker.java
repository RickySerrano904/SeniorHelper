package seniorhelper.service;

import seniorhelper.entities.User;
import seniorhelper.enums.CareLinkStatus;
import seniorhelper.enums.Role;
import seniorhelper.repository.CareLinkRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionChecker {

    private final CareLinkRepository links;

    public PermissionChecker(CareLinkRepository links) {
        this.links = links;
    }

    public boolean hasPermission(User requester, Integer seniorId) {
        if (requester == null) return false;

        Role role = requester.getRole();
        return switch (role) {
            case ADMIN -> true;                                      // Admin: can view anyone
            case SENIOR -> requester.getId().equals(seniorId);       // Senior: can view self
            case CAREGIVER ->                                         // Caregiver: must be linked
                    links.existsByCaregiver_IdAndSenior_IdAndStatus(requester.getId(), seniorId, CareLinkStatus.ACCEPTED);
        };
    }
}
