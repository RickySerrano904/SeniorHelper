package edu.fscj.cop3024c.seniorhelper.service;

import edu.fscj.cop3024c.seniorhelper.entities.CareLink;
import edu.fscj.cop3024c.seniorhelper.entities.User;
import edu.fscj.cop3024c.seniorhelper.enums.Role;
import edu.fscj.cop3024c.seniorhelper.repository.CareLinkRepository;
import edu.fscj.cop3024c.seniorhelper.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityExistsException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CareLinkServiceTest {

    @Mock
    private CareLinkRepository careLinkRepository;

    @Mock
    private UserRepository userRepository;


    @InjectMocks
    private CareLinkService careLinkService;


    @Test
    void link_ShouldCreateCareLinkEntity() {
        User caregiver = new User();
        caregiver.setId(1);
        caregiver.setRole(Role.CAREGIVER);

        User senior = new User();
        senior.setId(2);
        senior.setRole(Role.SENIOR);

        when(userRepository.findById(1)).thenReturn(Optional.of(caregiver));
        when(userRepository.findById(2)).thenReturn(Optional.of(senior));
        when(careLinkRepository.existsByCaregiver_IdAndSenior_Id(1, 2)).thenReturn(false);

        CareLink saved = new CareLink();
        saved.setId(10);
        saved.setCaregiver(caregiver);
        saved.setSenior(senior);

        when(careLinkRepository.save(any(CareLink.class))).thenReturn(saved);

        CareLink result = careLinkService.link(1,2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getCaregiver()).isEqualTo(caregiver);
        assertThat(result.getSenior()).isEqualTo(senior);

        verify(careLinkRepository).save(any(CareLink.class));
    }

    @Test
    void link_ShouldThrow_WhenCareLinkExists() {
        when(careLinkRepository.existsByCaregiver_IdAndSenior_Id(1,2)).thenReturn(true);

        assertThatThrownBy(() -> careLinkService.link(1,2)).isInstanceOf(EntityExistsException.class);
    }


    @Test
    void forCaregiver_ShouldReturnList() {
        CareLink link = new CareLink();
        when(careLinkRepository.findAllByCaregiver_Id(5)).thenReturn(List.of(link));

        List<CareLink> result = careLinkService.forCaregiver(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(link);
        verify(careLinkRepository).findAllByCaregiver_Id(5);
    }

    @Test
    void forSenior_ShouldReturnList() {
        CareLink link = new CareLink();
        when(careLinkRepository.findAllBySenior_Id(8)).thenReturn(List.of(link));

        List<CareLink> result = careLinkService.forSenior(8);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(link);
        verify(careLinkRepository).findAllBySenior_Id(8);
    }

    @Test
    void remove_ShouldDeleteCareLink() {
        when(careLinkRepository.deleteByCaregiver_IdAndSenior_Id(1,2)).thenReturn(1L);

        long deleted = careLinkService.remove(1,2);

        assertThat(deleted).isEqualTo(1L);
        verify(careLinkRepository).deleteByCaregiver_IdAndSenior_Id(1,2);
    }
}
