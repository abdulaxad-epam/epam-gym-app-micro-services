package epam.mapper;

import epam.dto.request_dto.RegisterTraineeRequestDTO;
import epam.dto.request_dto.TraineeRequestDTO;
import epam.dto.response_dto.RegisterTraineeResponseDTO;
import epam.dto.response_dto.TraineeResponseDTO;
import epam.entity.Trainee;
import epam.entity.User;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-11T11:13:08+0500",
    comments = "version: 1.6.0.Beta1, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class TraineeMapperImpl implements TraineeMapper {

    @Autowired
    private UserMapper userMapper;

    @Override
    public TraineeResponseDTO toTraineeResponseDTO(Trainee trainee) {
        if ( trainee == null ) {
            return null;
        }

        TraineeResponseDTO.TraineeResponseDTOBuilder traineeResponseDTO = TraineeResponseDTO.builder();

        if ( trainee.getDateOfBirth() != null ) {
            traineeResponseDTO.traineeDateOfBirth( DateTimeFormatter.ISO_LOCAL_DATE.format( trainee.getDateOfBirth() ) );
        }
        traineeResponseDTO.user( userMapper.toUserResponseDTO( trainee.getUser() ) );
        traineeResponseDTO.address( trainee.getAddress() );

        return traineeResponseDTO.build();
    }

    @Override
    public Trainee toTrainee(TraineeRequestDTO trainee) {
        if ( trainee == null ) {
            return null;
        }

        Trainee.TraineeBuilder trainee1 = Trainee.builder();

        trainee1.user( userMapper.toUser( trainee.getUser() ) );
        trainee1.address( trainee.getAddress() );
        trainee1.dateOfBirth( trainee.getDateOfBirth() );

        return trainee1.build();
    }

    @Override
    public Trainee toTrainee(RegisterTraineeRequestDTO userRequestDTO, User connectedUser) {
        if ( userRequestDTO == null && connectedUser == null ) {
            return null;
        }

        Trainee.TraineeBuilder trainee = Trainee.builder();

        if ( userRequestDTO != null ) {
            trainee.address( userRequestDTO.getAddress() );
            trainee.dateOfBirth( userRequestDTO.getDateOfBirth() );
        }
        trainee.user( connectedUser );

        return trainee.build();
    }

    @Override
    public RegisterTraineeResponseDTO toRegisterTraineeResponseDTO(Trainee insert) {
        if ( insert == null ) {
            return null;
        }

        RegisterTraineeResponseDTO.RegisterTraineeResponseDTOBuilder registerTraineeResponseDTO = RegisterTraineeResponseDTO.builder();

        if ( insert.getDateOfBirth() != null ) {
            registerTraineeResponseDTO.traineeDateOfBirth( DateTimeFormatter.ISO_LOCAL_DATE.format( insert.getDateOfBirth() ) );
        }
        registerTraineeResponseDTO.user( userMapper.toUserResponseDTO( insert.getUser() ) );
        registerTraineeResponseDTO.address( insert.getAddress() );

        return registerTraineeResponseDTO.build();
    }
}
