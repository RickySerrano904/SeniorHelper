package seniorhelper.service;

import seniorhelper.entities.CareLink;
import seniorhelper.entities.User;
import seniorhelper.enums.CareLinkStatus;
import seniorhelper.enums.Role;
import seniorhelper.repository.CareLinkRepository;
import seniorhelper.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityExistsException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void requestLink_ShouldCreatePendingCareLinkEntity() {
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
        saved.setStatus(CareLinkStatus.PENDING);

        when(careLinkRepository.save(any(CareLink.class))).thenReturn(saved);

        CareLink result = careLinkService.requestLink(1,2);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10);
        assertThat(result.getCaregiver()).isEqualTo(caregiver);
        assertThat(result.getSenior()).isEqualTo(senior);
        assertThat(result.getStatus()).isEqualTo(CareLinkStatus.PENDING);

        verify(careLinkRepository).save(argThat(link ->
                link.getCaregiver() == caregiver
                        && link.getSenior() == senior
                        && link.getStatus() == CareLinkStatus.PENDING
        ));
    }

    @Test
    void requestLink_ShouldThrow_WhenCareLinkExists() {
        when(careLinkRepository.existsByCaregiver_IdAndSenior_Id(1,2)).thenReturn(true);

        assertThatThrownBy(() -> careLinkService.requestLink(1,2)).isInstanceOf(EntityExistsException.class);
    }

    @Test
    void approve_ShouldMarkPendingLinkAsAccepted() {
        User caregiver = new User();
        caregiver.setId(1);
        caregiver.setRole(Role.CAREGIVER);

        User senior = new User();
        senior.setId(2);
        senior.setRole(Role.SENIOR);

        CareLink pending = new CareLink();
        pending.setId(10);
        pending.setCaregiver(caregiver);
        pending.setSenior(senior);
        pending.setStatus(CareLinkStatus.PENDING);

        when(careLinkRepository.findByCaregiver_IdAndSenior_Id(1, 2)).thenReturn(Optional.of(pending));
        when(careLinkRepository.save(any(CareLink.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CareLink result = careLinkService.approve(1, 2);

        assertThat(result.getStatus()).isEqualTo(CareLinkStatus.ACCEPTED);
        verify(careLinkRepository).save(pending);
    }


    @Test
    void forCaregiver_ShouldReturnList() {
        CareLink link = new CareLink();
        when(careLinkRepository.findAllByCaregiver_IdAndStatus(5, CareLinkStatus.ACCEPTED)).thenReturn(List.of(link));

        List<CareLink> result = careLinkService.forCaregiver(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(link);
        verify(careLinkRepository).findAllByCaregiver_IdAndStatus(5, CareLinkStatus.ACCEPTED);
    }

    @Test
    void pendingForCaregiver_ShouldReturnPendingList() {
        CareLink link = new CareLink();
        when(careLinkRepository.findAllByCaregiver_IdAndStatus(5, CareLinkStatus.PENDING)).thenReturn(List.of(link));

        List<CareLink> result = careLinkService.pendingForCaregiver(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(link);
        verify(careLinkRepository).findAllByCaregiver_IdAndStatus(5, CareLinkStatus.PENDING);
    }

    @Test
    void forSenior_ShouldReturnList() {
        CareLink link = new CareLink();
        when(careLinkRepository.findAllBySenior_IdAndStatus(8, CareLinkStatus.ACCEPTED)).thenReturn(List.of(link));

        List<CareLink> result = careLinkService.forSenior(8);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(link);
        verify(careLinkRepository).findAllBySenior_IdAndStatus(8, CareLinkStatus.ACCEPTED);
    }

    @Test
    void remove_ShouldDeleteCareLink() {
        when(careLinkRepository.deleteByCaregiver_IdAndSenior_Id(1,2)).thenReturn(1L);

        long deleted = careLinkService.remove(1,2);

        assertThat(deleted).isEqualTo(1L);
        verify(careLinkRepository).deleteByCaregiver_IdAndSenior_Id(1,2);
    }
}
