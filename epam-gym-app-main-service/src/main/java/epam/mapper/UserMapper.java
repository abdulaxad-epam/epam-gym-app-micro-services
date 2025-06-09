package epam.mapper;


import epam.dto.request_dto.UserRequestDTO;
import epam.dto.response_dto.UserResponseDTO;
import epam.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Named("toUserResponseDTO")
    @Mapping(source = "firstname", target = "firstName")
    @Mapping(source = "lastname", target = "lastName")
    @Mapping(source = "isActive", target = "isActive")
    @Mapping(source = "username", target = "username")
    UserResponseDTO toUserResponseDTO(User user);

    @Named("toUser")
    @Mapping(source = "userRequestDTO.firstName", target = "firstname")
    @Mapping(source = "userRequestDTO.lastName", target = "lastname")
    @Mapping(source = "userRequestDTO.isActive", target = "isActive")
    User toUser(UserRequestDTO userRequestDTO);
}
