    package com.codewithmosh.store.controller;
    
    import com.codewithmosh.store.dtos.*;
    import com.codewithmosh.store.entities.Role;
    import com.codewithmosh.store.mappers.UserMappers;
    import com.codewithmosh.store.repositories.UserRepository;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import jakarta.validation.Valid;
    import lombok.AllArgsConstructor;
    import org.springframework.data.domain.Sort;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.util.UriComponentsBuilder;
    
    import java.util.*;
    
    @AllArgsConstructor
    @RestController
    @RequestMapping("/users")
    @Tag(name = "User-Controller")
    public class UserController {
        private final UserRepository userRepository;
        private final UserMappers userMappers;
        private final PasswordEncoder passwordEncoder;
    
    
        // Handles GET request to list all users
        @GetMapping
        public Iterable<UserDto> getAllUsers(
                @RequestParam(required = false, defaultValue = "", name = "sort") String sort // Optional "sort" query (e.g., ?sort=email)
        ) {
            // Only allow sorting by "name" or "email"; otherwise default to "name"
            if (!Set.of("name", "email").contains(sort))
                sort = "name";
    
            // Fetch all users from database, sort them, map each to UserDto, and return as a list
            return userRepository.findAll(Sort.by(sort))
                    .stream()
                    .map(userMappers::toDto)
                    .toList();
        }
    
        // Handles GET request to get one user by ID
        @GetMapping("/{id}")
        public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
            var user = userRepository.findById(id).orElse(null); // Find user by ID
            if (user == null) { // If not found, return 404
                return ResponseEntity.notFound().build();
            }
            // If found, convert to DTO and return 200 OK with user data
         var userDto = userMappers.toDto(user);
            return ResponseEntity.ok(userDto);
        }
    
        //RegisterUserRequest
        // Handles POST request to create a new user
        @PostMapping
        public ResponseEntity<?> registerUser(
                @Valid @RequestBody RegisterUserRequest request, // Validate and map JSON to RegisterUserRequest
                UriComponentsBuilder uriComponentsBuilder // Helps build the new user's URI
        ) {
           if( userRepository.existsByEmail(request.getEmail())){
               return ResponseEntity.badRequest().body(
                       new ErrorDto("Email is already registered!")
               );
           }
    
    
            var user = userMappers.toEntity(request); // Convert DTO to User entity
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            userRepository.save(user); // Save user in the database
            var userDto = userMappers.toDto(user); // Convert saved entity back to DTO
    
            var uri = uriComponentsBuilder.path("/users/{id}") // Build URI like /users/1
                    .buildAndExpand(userDto.getId())
                    .toUri();
    
            return ResponseEntity.created(uri).body(userDto); // Return 201 Created with user data
        }
    
    
        // Handles PUT request to update an existing user
        @PutMapping("/{id}")
        public ResponseEntity<UserDto> updateUser(
                  @PathVariable Long id,            // Gets user ID from the URL (e.g., /users/5)
                  @RequestBody UpdateUserRequest request        // Takes updated user data from the request body
        ) {
               var user = userRepository.findById(id).orElse(null);   // Find user by ID in database
                if (user == null){
                    return ResponseEntity.notFound().build(); // If user doesn’t exist, return 404 Not Found
    
            }
    
           userMappers.update(request,user); // Apply changes from request to existing user entity
           userRepository.save(user);// Save the updated user to database
          var userDto = userMappers.toDto(user);
          return ResponseEntity.ok(userDto); // Return updated user data (200 OK)
        }
    
        // Handles DELETE request to remove a user by ID
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
            var user = userRepository.findById(id).orElse(null); // Find user by ID
            if (user == null) { // If not found, return 404
                return ResponseEntity.notFound().build();
            }
            userRepository.delete(user); // Delete the user from database
            return ResponseEntity.ok().build(); // Return 200 OK (no body)
        }
    
    
        // Handles POST request to change a user's password
        @PostMapping("/{id}/change-password")
        public ResponseEntity<UserDto> changePassword(
                @PathVariable Long id, // Get user ID from URL
                @RequestBody  ChangePasswordRequest request // Get old and new passwords from request body
        ){
    
           var user = userRepository.findById(id).orElse(null);// Find user by ID
            if (user == null){
                 return ResponseEntity.notFound().build(); // If not found, return 404
            }
    
    
                if(!user.getPassword().equals(request.getOldPassword())){
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);// Check if old password matches the one in the database
    
                }
             user.setPassword(request.getNewPassword());   // Set the new password
             userRepository.save(user);// Save changes to the database

            var userDto = userMappers.toDto(user);
             return ResponseEntity.ok(userDto);// Return 200 OK if successful
        }
    

    
    
    
    }
