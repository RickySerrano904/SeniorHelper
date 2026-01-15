package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.CareLink;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import edu.fscj.cop3024c.seniorhelper.repository.CareLinkRepository;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CareLinkService {
    private final CareLinkRepository links;
    private final UserRepository users;

    public CareLinkService(CareLinkRepository links, UserRepository users) {
        this.links = links;
        this.users = users;
    }

    private User load(Integer id) {
        return users.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found: " + id)
        );
    }

    @Transactional
    public CareLink link(Integer caregiverId, Integer seniorId) {
        if (links.existsByCaregiver_IdAndSenior_Id(caregiverId, seniorId)) {
            throw new EntityExistsException("Link already exists for caregiverId=" + caregiverId + " seniorId=" + seniorId);
        }

        User caregiver = load(caregiverId);
        User senior = load(seniorId);

        // Validate roles (Family and Admin can act as caregiver)
        Role cr = caregiver.getRole();
        Role sr = senior.getRole();

        if (!(cr == Role.CAREGIVER || cr == Role.FAMILY || cr == Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User " + caregiverId + " is not CAREGIVER/FAMILY/ADMIN");
        }
        if (sr != Role.SENIOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User " + seniorId + " is not SENIOR");
        }

        CareLink link = new CareLink();
        link.setCaregiver(caregiver);
        link.setSenior(senior);
        return links.save(link);
    }

    public List<CareLink> forCaregiver(Integer caregiverId) {
        return links.findAllByCaregiver_Id(caregiverId);
    }

    public List<CareLink> forSenior(Integer seniorId) {
        return links.findAllBySenior_Id(seniorId);
    }

    @Transactional
    public long remove(Integer caregiverId, Integer seniorId) {
        return links.deleteByCaregiver_IdAndSenior_Id(caregiverId, seniorId);
    }
}
