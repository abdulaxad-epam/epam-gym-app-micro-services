package epam.controller.integration_test;


import epam.client.dto.TrainerWorkloadSummaryResponseDTO;
import epam.client.service.TrainerWorkloadService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class  TrainerWorkloadControllerIntegrationTest {

    @Autowired
    private TrainerWorkloadService trainerWorkloadService;

    @Autowired(required = false)
    private AuthenticationManager authenticationManager;

    @Test
    public void givenUnauthenticated_whenCallService_thenThrowsException() {
        assertThrows(BadCredentialsException.class, () ->
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("unauthenticated", "unauthenticated"))
        );
    }

//    @WithMockUser(username = "robert.brown", roles = {"TRAINER"})
//    @Test
//    public void givenAuthenticated_whenCallServiceWithSecured_thenOk() {
//
//        Integer year = 2025, month = 2;
//        UserDetails mockUserDetails = mock(UserDetails.class);
//        when(mockUserDetails.getUsername()).thenReturn("robert.brown");
//
//        Authentication authenticatedMockUser = new UsernamePasswordAuthenticationToken(
//                mockUserDetails, "password4", List.of(new SimpleGrantedAuthority("TRAINER")));
//
//        TrainerWorkloadSummaryResponseDTO trainerWorkloadSummary =
//                trainerWorkloadService.getTrainerWorkloadSummary(year, month, authenticatedMockUser);
//
//        assertNotNull(trainerWorkloadSummary);
//    }
//
//    @WithMockUser(username = "robert.brown", roles = {"TRAINER"})
//    @Test
//    public void givenAuthenticated_whenCallServiceWithSecured_thenNotFoundMessage() {
//
//        Integer year = 2025, month = 1;
//        UserDetails mockUserDetails = mock(UserDetails.class);
//        when(mockUserDetails.getUsername()).thenReturn("robert.brown");
//
//        Authentication authenticatedMockUser = new UsernamePasswordAuthenticationToken(
//                mockUserDetails, "password4", List.of(new SimpleGrantedAuthority("TRAINER")));
//
//        TrainerWorkloadSummaryResponseDTO trainerWorkloadSummary =
//                trainerWorkloadService.getTrainerWorkloadSummary(year, month, authenticatedMockUser);
//
//        assertNull(trainerWorkloadSummary);
//    }
}


