package epam.controller;

import epam.dto.response_dto.TrainingTypeResponseDTO;
import epam.service.TrainingTypeService;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static epam.dto.request_dto.RegisterTraineeRequestDTOTest.settings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetTrainingTypes_ShouldReturnEmptyList() {
        // When
        when(trainingTypeService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<TrainingTypeResponseDTO>> response = trainingTypeController.getTrainingTypes();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    public void testGetTrainingTypes_ShouldReturnListOfTrainingTypes() {
        // Given
        TrainingTypeResponseDTO dto = Instancio.of(TrainingTypeResponseDTO.class).withSettings(settings).create();

        // When
        when(trainingTypeService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<TrainingTypeResponseDTO>> response = trainingTypeController.getTrainingTypes();

        // Then
        assertEquals(200, response.getStatusCode().value());
        assertEquals(List.of(dto), response.getBody());
    }
}
