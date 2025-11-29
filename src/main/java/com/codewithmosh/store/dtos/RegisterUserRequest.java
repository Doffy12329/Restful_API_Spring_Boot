//RegisterUserRequest in DTO
package com.codewithmosh.store.dtos;

import com.codewithmosh.store.validation.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Name must be required!")
    private String name;
    @NotBlank(message = "Email is required!")
    @Email(message = "Must be valid email!")
    @Lowercase(message = "Email must be in lowercase ")
    private String email;
    @NotBlank(message = "Password id required!")
    @Size(min = 7,max = 25, message = "Password must be 7 up to 25 characters")
    private String password;


}
