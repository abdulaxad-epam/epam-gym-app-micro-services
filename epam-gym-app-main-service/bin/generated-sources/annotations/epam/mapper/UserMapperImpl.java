package epam.mapper;

import epam.dto.request_dto.UserRequestDTO;
import epam.dto.response_dto.UserResponseDTO;
import epam.entity.User;
import epam.enums.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-11T11:13:08+0500",
    comments = "version: 1.6.0.Beta1, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
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
        user.password( userRequestDTO.getPassword() );
        if ( userRequestDTO.getRole() != null ) {
            user.role( Enum.valueOf( Role.class, userRequestDTO.getRole() ) );
        }

        return user.build();
    }
}
