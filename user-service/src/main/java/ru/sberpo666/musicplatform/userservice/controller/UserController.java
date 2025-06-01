package ru.sberpo666.musicplatform.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.sberpo666.musicplatform.userservice.dto.UserDto;
import ru.sberpo666.musicplatform.userservice.service.UserService;

import java.util.UUID;


@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/register")
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @GetMapping("/users/me")
    public UserDto getUserProfile(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new IllegalStateException("User not authenticated");
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        return userService.findById(userId);
    }

    @GetMapping("/users")
    public Page<UserDto> getAllUsers(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        checkIsAdminOrThrow(jwt);

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return userService.findAll(pageable);
    }

    @PutMapping("/users/me")
    public UserDto updateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserDto userDto) {
        if (jwt == null) {
            throw new IllegalStateException("User not authenticated");
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        return userService.update(userId, userDto);
    }

    @PutMapping("/users/{userId}")
    public UserDto updateUserForAdmin(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId, @RequestBody UserDto userDto) {
        checkIsAdminOrThrow(jwt);

        return userService.update(userId, userDto);
    }

    @PatchMapping("/users/me")
    public UserDto partialUpdateUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserDto userDto) {
        if (jwt == null) {
            throw new IllegalStateException("User not authenticated");
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        return userService.update(userId, userDto);
    }

    @GetMapping("/users/{userId}")
    public UserDto getUserByIdForAdmin(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId) {
        checkIsAdminOrThrow(jwt);

        return userService.findById(userId);
    }

    @DeleteMapping("/users/me")
    public void deleteUserProfile(@AuthenticationPrincipal Jwt jwt) {
        if (jwt == null) {
            throw new IllegalStateException("User not authenticated");
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("user_id"));
        userService.deleteById(userId);
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId) {
        checkIsAdminOrThrow(jwt);

        userService.deleteById(userId);
    }

    private void checkIsAdminOrThrow(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalStateException("User not authenticated");
        }

        UUID loggedUserId = UUID.fromString(jwt.getClaimAsString("user_id"));
        boolean isAdmin = userService.getRolesByUserId(loggedUserId).stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));
        if (!isAdmin) {
            throw new IllegalStateException("User does not have admin permission to view all users");
        }
    }
}
