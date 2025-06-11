package epam.mapper;

import epam.dto.request_dto.UserRequestDTO;
import epam.dto.response_dto.UserResponseDTO;
import epam.entity.User;
import epam.enums.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-10T11:42:12+0500",
    comments = "version: 1.6.0.Beta1, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.13.jar, environment: Java 17.0.14 (Homebrew)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toUserResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO.UserResponseDTOBuilder userResponseDTO = UserResponseDTO.builder();

        userResponseDTO.firstName( user.getFirstname() );
        userResponseDTO.lastName( user.getLastname() );
        userResponseDTO.isActive( user.getIsActive() );
        userResponseDTO.username( user.getUsername() );

        return userResponseDTO.build();
    }

    @Override
    public User toUser(UserRequestDTO userRequestDTO) {
        if ( userRequestDTO == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstname( userRequestDTO.getFirstName() );
        user.lastname( userRequestDTO.getLastName() );
        user.isActive( userRequestDTO.getIsActive() );
        if ( userRequestDTO.getRole() != null ) {
            user.role( Enum.valueOf( Role.class, userRequestDTO.getRole() ) );
        }
        user.password( userRequestDTO.getPassword() );

        return user.build();
    }
}
