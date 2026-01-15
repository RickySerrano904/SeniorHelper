package edu.fscj.cop3024c.seniorhelper.repository;

import edu.fscj.cop3024c.seniorhelper.entities.CareLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareLinkRepository extends JpaRepository<CareLink, Integer> {

    boolean existsByCaregiver_IdAndSenior_Id(Integer caregiverId, Integer seniorId);
    List<CareLink> findAllByCaregiver_Id(Integer caregiverId);
    List<CareLink> findAllBySenior_Id(Integer seniorId);
    long deleteByCaregiver_IdAndSenior_Id(Integer caregiverId, Integer seniorId);
}
