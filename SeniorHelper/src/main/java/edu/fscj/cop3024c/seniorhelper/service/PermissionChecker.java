package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import edu.fscj.cop3024c.seniorhelper.repository.CareLinkRepository;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionChecker {

    private final UserRepository users;
    private final CareLinkRepository links;

    public PermissionChecker(UserRepository users, CareLinkRepository links) {
        this.users = users;
        this.links = links;
    }

    public boolean hasPermission(User requester, Integer seniorId) {
        if (requester == null) return false;

        Role role = requester.getRole();
        return switch (role) {
            case ADMIN -> true;                                      // Admin: can view anyone
            case SENIOR -> requester.getId().equals(seniorId);       // Senior: can view self
            case CAREGIVER ->                                         // Caregiver: must be linked
                    links.existsByCaregiver_IdAndSenior_Id(requester.getId(), seniorId);
        };
    }
}
