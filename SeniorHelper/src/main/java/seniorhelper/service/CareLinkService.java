package seniorhelper.service;

import seniorhelper.entities.CareLink;
import seniorhelper.entities.User;
import seniorhelper.enums.CareLinkStatus;
import seniorhelper.enums.Role;
import seniorhelper.repository.CareLinkRepository;
import seniorhelper.repository.UserRepository;
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
    public CareLink requestLink(Integer caregiverId, Integer seniorId) {
        if (links.existsByCaregiver_IdAndSenior_Id(caregiverId, seniorId)) {
            throw new EntityExistsException("Link already exists for caregiverId=" + caregiverId + " seniorId=" + seniorId);
        }

        User caregiver = load(caregiverId);
        User senior = load(seniorId);

        // Validate roles (Only caregiver can act as a caregiver)
        Role cr = caregiver.getRole();
        Role sr = senior.getRole();

        if (!(cr == Role.CAREGIVER)) { // || cr == Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User " + caregiverId + " is not CAREGIVER");
        }
        if (sr != Role.SENIOR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User " + seniorId + " is not SENIOR");
        }

        CareLink link = new CareLink();
        link.setCaregiver(caregiver);
        link.setSenior(senior);
        link.setStatus(CareLinkStatus.PENDING);
        return links.save(link);
    }

    @Transactional
    public CareLink approve(Integer caregiverId, Integer seniorId) {
        CareLink link = links.findByCaregiver_IdAndSenior_Id(caregiverId, seniorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Link not found for caregiverId=" + caregiverId + " seniorId=" + seniorId
                ));

        link.setStatus(CareLinkStatus.ACCEPTED);
        return links.save(link);
    }

    public List<CareLink> forCaregiver(Integer caregiverId) {
        return links.findAllByCaregiver_IdAndStatus(caregiverId, CareLinkStatus.ACCEPTED);
    }

    public List<CareLink> pendingForCaregiver(Integer caregiverId) {
        return links.findAllByCaregiver_IdAndStatus(caregiverId, CareLinkStatus.PENDING);
    }

    public List<CareLink> forSenior(Integer seniorId) {
        return links.findAllBySenior_IdAndStatus(seniorId, CareLinkStatus.ACCEPTED);
    }

    @Transactional
    public long remove(Integer caregiverId, Integer seniorId) {
        return links.deleteByCaregiver_IdAndSenior_Id(caregiverId, seniorId);
    }
}
