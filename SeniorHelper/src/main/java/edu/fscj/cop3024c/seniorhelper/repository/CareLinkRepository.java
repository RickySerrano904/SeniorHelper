package edu.fscj.cop3024c.seniorhelper.repository;

import edu.fscj.cop3024c.seniorhelper.entities.CareLink;
import edu.fscj.cop3024c.seniorhelper.enums.CareLinkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareLinkRepository extends JpaRepository<CareLink, Integer> {

    boolean existsByCaregiver_IdAndSenior_Id(Integer caregiverId, Integer seniorId);
    boolean existsByCaregiver_IdAndSenior_IdAndStatus(Integer caregiverId, Integer seniorId, CareLinkStatus status);
    List<CareLink> findAllByCaregiver_IdAndStatus(Integer caregiverId, CareLinkStatus status);
    List<CareLink> findAllBySenior_IdAndStatus(Integer seniorId, CareLinkStatus status);
    Optional<CareLink> findByCaregiver_IdAndSenior_Id(Integer caregiverId, Integer seniorId);
    long deleteByCaregiver_IdAndSenior_Id(Integer caregiverId, Integer seniorId);
}
