//UserMappers in mappers interface
package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring") // MapStruct will auto-generate implementation and make it a Spring Bean
public interface UserMappers {

    UserDto toDto(User user); // Converts User entity → UserDto (for response)

    User toEntity(RegisterUserRequest request); // Converts RegisterUserRequest → User entity (for saving)

    @Mapping(target = "email", source = "email") // Maps email from request to user
    void update(UpdateUserRequest request, @MappingTarget User user); // Updates an existing User entity with new data
}
