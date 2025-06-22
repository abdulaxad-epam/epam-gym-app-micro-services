package epam.service;

import epam.dto.TrainerWorkloadRequestDTO;
import epam.dto.TrainerWorkloadResponseDTO;
import epam.entity.DailyWorkload;
import epam.entity.MonthlyWorkload;
import epam.entity.TrainerWorkload;
import epam.entity.YearlyWorkload;
import epam.enums.ActionType;
import epam.exception.DailyTrainingDurationExceededException;
import epam.exception.TrainerWorkloadNotFoundException;
import epam.repostiory.TrainerWorkloadRepository;
import epam.service.impl.TrainerWorkloadServiceImpl;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private TrainerWorkloadSummaryService trainerWorkloadSummaryService;

    @InjectMocks
    private TrainerWorkloadServiceImpl trainerWorkloadService;

    private static final String TRAINER_USERNAME = "john.doe";
    private static final String TRAINER_FIRSTNAME = "John";
    private static final String TRAINER_LASTNAME = "Doe";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 6, 20);
    private static final int MAX_DAILY_MINUTES = 480;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(trainerWorkloadService, "maxDailyWorkingHours", MAX_DAILY_MINUTES);
    }

    private TrainerWorkload createTrainerWorkloadWithDailyDuration(LocalDate date, int currentDailyDuration, boolean isActive) {
        return Instancio.of(TrainerWorkload.class)
                .set(Select.field("trainerUsername"), TrainerWorkloadServiceTest.TRAINER_USERNAME)
                .set(Select.field("trainerFirstName"), TrainerWorkloadServiceTest.TRAINER_FIRSTNAME)
                .set(Select.field("trainerLastName"), TrainerWorkloadServiceTest.TRAINER_LASTNAME)
                .set(Select.field("isActive"), isActive)
                .set(Select.field("years"), Collections.singletonList(
                        Instancio.of(YearlyWorkload.class)
                                .set(Select.field("year"), date.getYear())
                                .set(Select.field("months"), Collections.singletonList(
                                        Instancio.of(MonthlyWorkload.class)
                                                .set(Select.field("month"), date.getMonthValue())
                                                .set(Select.field("monthlyTrainingDuration"), currentDailyDuration)
                                                .set(Select.field("days"), Collections.singletonList(
                                                        Instancio.of(DailyWorkload.class)
                                                                .set(Select.field("day"), date.getDayOfMonth())
                                                                .set(Select.field("dailyTrainingDuration"), currentDailyDuration)
                                                                .create()
                                                ))
                                                .create()
                                ))
                                .create()
                ))
                .create();
    }

    private TrainerWorkloadRequestDTO createRequestDTO(String username, String firstName, String lastName, LocalDate date, int duration, ActionType actionType, boolean isActive) {
        return Instancio.of(TrainerWorkloadRequestDTO.class)
                .set(Select.field("trainerUsername"), username)
                .set(Select.field("trainerFirstName"), firstName)
                .set(Select.field("trainerLastName"), lastName)
                .set(Select.field("trainingDate"), date)
                .set(Select.field("trainingDurationInMinutes"), duration)
                .set(Select.field("actionType"), actionType.name())
                .set(Select.field("isActive"), isActive)
                .create();
    }


    @Test
    @DisplayName("actionOn should delegate to actionOnADD for ADD action type")
    void actionOn_shouldDelegateToAddForAddAction() {
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.ADD, true);

        TrainerWorkloadServiceImpl spyService = spy(trainerWorkloadService);
        doReturn(Instancio.create(TrainerWorkloadResponseDTO.class))
                .when(spyService).actionOnADD(requestDTO);

        TrainerWorkloadResponseDTO response = spyService.actionOn(requestDTO);

        assertNotNull(response);
        verify(spyService, times(1)).actionOnADD(requestDTO);
        verify(spyService, never()).actionOnDELETE(any(TrainerWorkloadRequestDTO.class));
    }

    @Test
    @DisplayName("actionOn should delegate to actionOnDELETE for DELETE action type")
    void actionOn_shouldDelegateToDeleteForDeleteAction() {
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.DELETE, true);

        TrainerWorkloadServiceImpl spyService = spy(trainerWorkloadService);
        doReturn(Instancio.create(TrainerWorkloadResponseDTO.class))
                .when(spyService).actionOnDELETE(requestDTO);

        TrainerWorkloadResponseDTO response = spyService.actionOn(requestDTO);

        assertNotNull(response);
        verify(spyService, times(1)).actionOnDELETE(requestDTO);
        verify(spyService, never()).actionOnADD(any(TrainerWorkloadRequestDTO.class));
    }

    @Test
    @DisplayName("actionOnADD should create new workload if trainer does not exist")
    void actionOnADD_shouldCreateNewWorkload_ifTrainerDoesNotExist() {
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(null);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.actionOnADD(requestDTO);

        assertNotNull(response);
        assertEquals(TRAINER_USERNAME, response.getTrainerUsername());
        assertEquals(TRAINING_DATE, response.getTrainingDate());
        assertEquals(60, response.getTrainingDurationInMinutes());
        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME);
        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
        verify(trainerWorkloadSummaryService, times(1)).produce(any(TrainerWorkloadResponseDTO.class), eq(true));
    }

    @Test
    @DisplayName("actionOnADD should update existing workload if trainer exists")
    void actionOnADD_shouldUpdateExistingWorkload_ifTrainerExists() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, 120, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.actionOnADD(requestDTO);

        assertNotNull(response);
        assertEquals(TRAINER_USERNAME, response.getTrainerUsername());
        assertEquals(TRAINING_DATE, response.getTrainingDate());
        assertEquals(180, response.getTrainingDurationInMinutes());
        verify(trainerWorkloadRepository, times(2)).findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME);
        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
        verify(trainerWorkloadSummaryService, times(1)).produce(any(TrainerWorkloadResponseDTO.class), eq(true));
    }

    // --- Test actionOnDELETE Method ---
    @Test
    @DisplayName("actionOnDELETE should throw TrainerWorkloadNotFoundException if trainer does not exist")
    void actionOnDELETE_shouldThrowNotFoundException_ifTrainerDoesNotExist() {
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.DELETE, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(null);

        assertThrows(TrainerWorkloadNotFoundException.class, () -> trainerWorkloadService.actionOnDELETE(requestDTO));

        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME);
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
        verify(trainerWorkloadSummaryService, never()).produce(any(TrainerWorkloadResponseDTO.class), anyBoolean());
    }

    @Test
    @DisplayName("actionOnDELETE should update existing workload if trainer exists")
    void actionOnDELETE_shouldUpdateExistingWorkload_ifTrainerExists() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, 120, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.DELETE, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.actionOnDELETE(requestDTO);

        assertNotNull(response);
        assertEquals(TRAINER_USERNAME, response.getTrainerUsername());
        assertEquals(TRAINING_DATE, response.getTrainingDate());
        assertEquals(60, response.getTrainingDurationInMinutes());
        verify(trainerWorkloadRepository, times(2)).findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME);
        verify(trainerWorkloadRepository, times(1)).save(any(TrainerWorkload.class));
        verify(trainerWorkloadSummaryService, times(1)).produce(any(TrainerWorkloadResponseDTO.class), eq(true));
    }


    @Test
    @DisplayName("updateExistingTrainerWorkload should throw TrainerWorkloadNotFoundException if trainer not found")
    void updateExistingTrainerWorkload_throwsException_ifTrainerNotFound() {
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD));

        verify(trainerWorkloadRepository, times(1)).findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME);
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should add duration to existing day")
    void updateExistingTrainerWorkload_addDuration_toExistingDay() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, 100, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 50, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        assertNotNull(response);
        assertEquals(150, response.getTrainingDurationInMinutes());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(150, savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
        assertEquals(150, savedWorkload.getYears().get(0).getMonths().get(0).getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should decrement duration from existing day")
    void updateExistingTrainerWorkload_decrementDuration_fromExistingDay() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, 100, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 30, ActionType.DELETE, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.DELETE);

        assertNotNull(response);
        assertEquals(70, response.getTrainingDurationInMinutes());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(70, savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
        assertEquals(70, savedWorkload.getYears().get(0).getMonths().get(0).getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should cap daily duration at 0 when decrementing below zero")
    void updateExistingTrainerWorkload_capDailyDurationAtZero() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, 20, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 50, ActionType.DELETE, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.DELETE);

        assertNotNull(response);
        assertEquals(0, response.getTrainingDurationInMinutes());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(0, savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
        assertEquals(0, savedWorkload.getYears().get(0).getMonths().get(0).getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should throw DailyTrainingDurationExceededException when adding above limit")
    void updateExistingTrainerWorkload_throwsExceededException_whenAddingAboveLimit() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, MAX_DAILY_MINUTES - 30, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 50, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);

        DailyTrainingDurationExceededException exception = assertThrows(DailyTrainingDurationExceededException.class, () ->
                trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD));

        assertTrue(exception.getMessage().contains("cannot be more than 8 hours (" + MAX_DAILY_MINUTES + " minutes)"));
        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should handle adding exactly to the limit")
    void updateExistingTrainerWorkload_addsExactlyToLimit() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, MAX_DAILY_MINUTES - 60, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 60, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        assertNotNull(response);
        assertEquals(MAX_DAILY_MINUTES, response.getTrainingDurationInMinutes());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(MAX_DAILY_MINUTES, savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
        assertEquals(MAX_DAILY_MINUTES, savedWorkload.getYears().get(0).getMonths().get(0).getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should add a new day, month, and year if non-existent")
    void updateExistingTrainerWorkload_addNewYearMonthDay() {
        LocalDate newDate = LocalDate.of(2026, 1, 15);
        TrainerWorkload existingWorkload = Instancio.of(TrainerWorkload.class)
                .set(Select.field("trainerUsername"), TRAINER_USERNAME)
                .set(Select.field("trainerFirstName"), TRAINER_FIRSTNAME)
                .set(Select.field("trainerLastName"), TRAINER_LASTNAME)
                .set(Select.field("isActive"), true)
                .set(Select.field("years"), null)
                .create();
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, newDate, 90, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        assertNotNull(response);
        assertEquals(90, response.getTrainingDurationInMinutes());
        assertEquals(newDate, response.getTrainingDate());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(1, savedWorkload.getYears().size());
        assertEquals(newDate.getYear(), savedWorkload.getYears().get(0).getYear());
        assertEquals(1, savedWorkload.getYears().get(0).getMonths().size());
        assertEquals(newDate.getMonthValue(), savedWorkload.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(1, savedWorkload.getYears().get(0).getMonths().get(0).getDays().size());
        assertEquals(newDate.getDayOfMonth(), savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDay());
        assertEquals(90, savedWorkload.getYears().get(0).getMonths().get(0).getDays().get(0).getDailyTrainingDuration());
        assertEquals(90, savedWorkload.getYears().get(0).getMonths().get(0).getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should add a new month and day if non-existent within an existing year")
    void updateExistingTrainerWorkload_addNewMonthDay() {
        LocalDate newDate = LocalDate.of(2025, 7, 10);

        TrainerWorkload existingWorkload = Instancio.of(TrainerWorkload.class)
                .set(Select.field("trainerUsername"), TRAINER_USERNAME)
                .set(Select.field("trainerFirstName"), TRAINER_FIRSTNAME)
                .set(Select.field("trainerLastName"), TRAINER_LASTNAME)
                .set(Select.field("isActive"), true)
                .set(Select.field("years"), null)
                .create();

        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, newDate, 75, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        assertNotNull(response);
        assertEquals(75, response.getTrainingDurationInMinutes());
        assertEquals(newDate, response.getTrainingDate());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(1, savedWorkload.getYears().size());
        assertEquals(newDate.getYear(), savedWorkload.getYears().get(0).getYear());
        assertEquals(1, savedWorkload.getYears().get(0).getMonths().size());

        MonthlyWorkload newMonthlyWorkload = savedWorkload.getYears().get(0).getMonths().stream()
                .filter(m -> m.getMonth().equals(newDate.getMonthValue())).findFirst().orElseThrow();

        assertEquals(newDate.getMonthValue(), newMonthlyWorkload.getMonth());
        assertEquals(1, newMonthlyWorkload.getDays().size());
        assertEquals(newDate.getDayOfMonth(), newMonthlyWorkload.getDays().get(0).getDay());
        assertEquals(75, newMonthlyWorkload.getDays().get(0).getDailyTrainingDuration());
        assertEquals(75, newMonthlyWorkload.getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should add a new day if non-existent within an existing month")
    void updateExistingTrainerWorkload_addNewDay() {
        LocalDate newDate = LocalDate.of(2025, 6, 25);

        TrainerWorkload existingWorkload = Instancio.of(TrainerWorkload.class)
                .set(Select.field("trainerUsername"), TRAINER_USERNAME)
                .set(Select.field("trainerFirstName"), TRAINER_FIRSTNAME)
                .set(Select.field("trainerLastName"), TRAINER_LASTNAME)
                .set(Select.field("isActive"), true)
                .set(Select.field("years"), null)
                .create();

        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, newDate, 45, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerWorkloadResponseDTO response = trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        assertNotNull(response);
        assertEquals(45, response.getTrainingDurationInMinutes());
        assertEquals(newDate, response.getTrainingDate());

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertEquals(1, savedWorkload.getYears().size());
        assertEquals(newDate.getYear(), savedWorkload.getYears().get(0).getYear());
        assertEquals(1, savedWorkload.getYears().get(0).getMonths().size());
        assertEquals(newDate.getMonthValue(), savedWorkload.getYears().get(0).getMonths().get(0).getMonth());
        assertEquals(1, savedWorkload.getYears().get(0).getMonths().get(0).getDays().size());

        DailyWorkload newDailyWorkload = savedWorkload.getYears().get(0).getMonths().get(0).getDays().stream()
                .filter(d -> d.getDay().equals(newDate.getDayOfMonth())).findFirst().orElseThrow();
        assertEquals(newDate.getDayOfMonth(), newDailyWorkload.getDay());
        assertEquals(45, newDailyWorkload.getDailyTrainingDuration());
        assertEquals(45, savedWorkload.getYears().get(0).getMonths().get(0).getMonthlyTrainingDuration());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should update isActive status if changed")
    void updateExistingTrainerWorkload_updateIsActive() {

        TrainerWorkload existingWorkload = Instancio.of(TrainerWorkload.class)
                .set(Select.field("trainerUsername"), TRAINER_USERNAME)
                .set(Select.field("trainerFirstName"), TRAINER_FIRSTNAME)
                .set(Select.field("trainerLastName"), TRAINER_LASTNAME)
                .set(Select.field("isActive"), true)
                .set(Select.field("years"), null)
                .create();

        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 30, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertTrue(savedWorkload.getIsActive());
    }

    @Test
    @DisplayName("updateExistingTrainerWorkload should not change isActive status if same")
    void updateExistingTrainerWorkload_notChangeIsActive_ifSame() {
        TrainerWorkload existingWorkload = createTrainerWorkloadWithDailyDuration(TRAINING_DATE, 60, true);
        TrainerWorkloadRequestDTO requestDTO = createRequestDTO(TRAINER_USERNAME, TRAINER_FIRSTNAME, TRAINER_LASTNAME, TRAINING_DATE, 30, ActionType.ADD, true);

        when(trainerWorkloadRepository.findTrainerWorkloadByTrainerUsername(TRAINER_USERNAME)).thenReturn(existingWorkload);
        when(trainerWorkloadRepository.save(any(TrainerWorkload.class))).thenAnswer(invocation -> invocation.getArgument(0));

        trainerWorkloadService.updateExistingTrainerWorkload(requestDTO, ActionType.ADD);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());
        TrainerWorkload savedWorkload = captor.getValue();

        assertTrue(savedWorkload.getIsActive());
    }
}