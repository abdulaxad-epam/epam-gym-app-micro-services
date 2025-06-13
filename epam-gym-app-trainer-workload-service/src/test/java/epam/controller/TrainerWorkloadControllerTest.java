package epam.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.dto.TrainerWorkloadRequestDTO;
import epam.service.TrainerWorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TrainerWorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerWorkloadService trainerWorkloadService;

    private static final String BASE_URL = "/api/v1/trainer-workload";

    @BeforeEach
    void setUp() {
        reset(trainerWorkloadService);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"WRITE"})
    void testActionEndpoint_Forbidden() throws Exception {
        TrainerWorkloadRequestDTO invalidRequestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername(null)
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2023, 1, 15))
                .trainingDuration(60)
                .isActive(true)
                .actionType("ADD")
                .build();

        mockMvc.perform(post(BASE_URL + "/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isForbidden());

        verify(trainerWorkloadService, never()).actionOn(any(TrainerWorkloadRequestDTO.class));
    }

    @Test
    void testActionEndpoint_Unauthorized_NoUser() throws Exception {
        TrainerWorkloadRequestDTO requestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2023, 1, 15))
                .trainingDuration(60)
                .isActive(true)
                .actionType("ADD")
                .build();

        mockMvc.perform(post(BASE_URL + "/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());

        verify(trainerWorkloadService, never()).actionOn(any(TrainerWorkloadRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ACCEPT"})
    void testActionEndpoint_Forbidden_WrongRole() throws Exception {
        TrainerWorkloadRequestDTO requestDTO = TrainerWorkloadRequestDTO.builder()
                .trainerUsername("john.doe")
                .trainerFirstName("John")
                .trainerLastName("Doe")
                .trainingDate(LocalDate.of(2023, 1, 15))
                .trainingDuration(60)
                .isActive(true)
                .actionType("ADD")
                .build();

        mockMvc.perform(post(BASE_URL + "/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());

        verify(trainerWorkloadService, never()).actionOn(any(TrainerWorkloadRequestDTO.class));
    }
}