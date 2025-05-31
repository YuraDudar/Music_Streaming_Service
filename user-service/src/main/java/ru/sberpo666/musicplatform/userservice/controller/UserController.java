package ru.sberpo666.musicplatform.userservice.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.sberpo666.musicplatform.userservice.dto.UserDto;
import ru.sberpo666.musicplatform.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) {
        return userService.add(userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @PutMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto partialUpdateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.findById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteById(userId);
    }
}
