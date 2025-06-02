package ru.sberpo666.musicplatform.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import ru.sberpo666.musicplatform.userservice.config.KafkaConfig;
import ru.sberpo666.musicplatform.userservice.dto.UserDto;
import ru.sberpo666.musicplatform.userservice.entity.Role;
import ru.sberpo666.musicplatform.userservice.entity.User;
import ru.sberpo666.musicplatform.userservice.event.UserCreatedEvent;
import ru.sberpo666.musicplatform.userservice.event.UserDeletedEvent;
import ru.sberpo666.musicplatform.userservice.event.UserUpdatedEvent;
import ru.sberpo666.musicplatform.userservice.mapper.UserMapper;
import ru.sberpo666.musicplatform.userservice.repository.UserRepository;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaTemplate<UUID, Object> kafkaTemplate;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UUID sampleUserId;
    private UserDto sampleUserDto;

    @BeforeEach
    void setUp() {
        sampleUserId = UUID.randomUUID();
        sampleUser = new User();
        sampleUser.setUserId(sampleUserId);
        sampleUser.setEmail("test@example.com");
        sampleUser.setPasswordHash("hash");
        sampleUser.setDisplayName("Test User");
        sampleUser.setIsActive(true);
        sampleUser.setCreatedAt(OffsetDateTime.now());
        sampleUser.setUpdatedAt(OffsetDateTime.now());
        Role role = new Role();
        role.setRoleId(1);
        role.setName("USER");
        sampleUser.setRoles(Collections.singletonList(role));

        Integer[] roleIds = new Integer[]{1};
        sampleUserDto = new UserDto(
                sampleUserId,
                "test@example.com",
                "hash",
                "Test User",
                roleIds,
                true,
                sampleUser.getCreatedAt(),
                sampleUser.getUpdatedAt()
        );
    }

    @Test
    void findById_UserExists_ReturnsDto() {
        when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));
        when(userMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        UserDto result = userService.findById(sampleUserId);

        assertEquals(sampleUserDto, result);
        verify(userRepository).findById(sampleUserId);
        verify(userMapper).toDto(sampleUser);
    }

    @Test
    void findById_UserNotFound_ThrowsException() {
        when(userRepository.findById(sampleUserId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.findById(sampleUserId));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void add_ValidUser_SavesAndPublishesEvent() {
        User userToSave = new User();
        userToSave.setEmail(sampleUser.getEmail());
        userToSave.setPasswordHash(sampleUser.getPasswordHash());
        userToSave.setDisplayName(sampleUser.getDisplayName());
        userToSave.setIsActive(sampleUser.getIsActive());
        userToSave.setRoles(sampleUser.getRoles());

        when(userMapper.toUser(sampleUserDto)).thenReturn(userToSave);
        User savedUser = new User();
        savedUser.setUserId(sampleUserId);
        savedUser.setEmail(userToSave.getEmail());
        savedUser.setPasswordHash(userToSave.getPasswordHash());
        savedUser.setDisplayName(userToSave.getDisplayName());
        savedUser.setIsActive(userToSave.getIsActive());
        savedUser.setRoles(userToSave.getRoles());
        savedUser.setCreatedAt(OffsetDateTime.now());
        savedUser.setUpdatedAt(OffsetDateTime.now());

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);
        UserDto returnedDto = new UserDto(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getPasswordHash(),
                savedUser.getDisplayName(),
                new Integer[]{1},
                savedUser.getIsActive(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );
        when(userMapper.toDto(savedUser)).thenReturn(returnedDto);

        UserDto result = userService.add(sampleUserDto);

        assertEquals(returnedDto, result);
        ArgumentCaptor<UserCreatedEvent> eventCaptor = ArgumentCaptor.forClass(UserCreatedEvent.class);
        verify(kafkaTemplate).send(eq(KafkaConfig.USER_CREATED_TOPIC), eq(sampleUserId), eventCaptor.capture());
        UserCreatedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(sampleUserId, capturedEvent.userId());
        assertEquals(savedUser.getEmail(), capturedEvent.email());
        assertEquals(savedUser.getPasswordHash(), capturedEvent.password());
        assertArrayEquals(new String[]{"USER"}, capturedEvent.roles());
    }

    @Test
    void update_UserExists_UpdatesAndPublishesEvent() {
        UserDto updateDto = new UserDto(
                sampleUserId,
                "updated@example.com",
                "newhash",
                "Updated User",
                new Integer[]{1},
                true,
                sampleUser.getCreatedAt(),
                sampleUser.getUpdatedAt()
        );

        when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));


        doAnswer(invocation -> {
            UserDto dto = invocation.getArgument(0);
            User u = invocation.getArgument(1);
            u.setEmail(dto.email());
            u.setPasswordHash(dto.passwordHash());
            u.setDisplayName(dto.displayName());

            return u;
        }).when(userMapper).updateFromDto(updateDto, sampleUser);


        User updatedUser = new User();
        updatedUser.setUserId(sampleUserId);
        updatedUser.setEmail(updateDto.email());
        updatedUser.setPasswordHash(updateDto.passwordHash());
        updatedUser.setDisplayName(updateDto.displayName());
        updatedUser.setIsActive(true);
        updatedUser.setRoles(sampleUser.getRoles());
        updatedUser.setCreatedAt(sampleUser.getCreatedAt());
        updatedUser.setUpdatedAt(OffsetDateTime.now());

        when(userRepository.saveAndFlush(sampleUser)).thenReturn(updatedUser);

        UserDto returnedDto = new UserDto(
                updatedUser.getUserId(),
                updatedUser.getEmail(),
                updatedUser.getPasswordHash(),
                updatedUser.getDisplayName(),
                new Integer[]{1},
                updatedUser.getIsActive(),
                updatedUser.getCreatedAt(),
                updatedUser.getUpdatedAt()
        );
        when(userMapper.toDto(updatedUser)).thenReturn(returnedDto);

        UserDto result = userService.update(sampleUserId, updateDto);

        assertEquals(returnedDto, result);

        ArgumentCaptor<UserUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(UserUpdatedEvent.class);
        verify(kafkaTemplate).send(eq(KafkaConfig.USER_UPDATED_TOPIC), eq(sampleUserId), eventCaptor.capture());
        UserUpdatedEvent capturedEvent = eventCaptor.getValue();


        assertEquals(sampleUserId, capturedEvent.userId());
        assertEquals("updated@example.com", capturedEvent.email());
        assertEquals("newhash", capturedEvent.password());
        assertArrayEquals(new String[]{"USER"}, capturedEvent.roles());
    }


    @Test
    void update_UserNotFound_ThrowsException() {
        when(userRepository.findById(sampleUserId)).thenReturn(Optional.empty());
        UserDto updateDto = sampleUserDto;
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.update(sampleUserId, updateDto));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void deleteById_UserExists_DeletesAndPublishesEvent() {
        when(userRepository.existsById(sampleUserId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(sampleUserId);

        userService.deleteById(sampleUserId);

        verify(userRepository).deleteById(sampleUserId);
        ArgumentCaptor<UserDeletedEvent> eventCaptor = ArgumentCaptor.forClass(UserDeletedEvent.class);
        verify(kafkaTemplate).send(eq(KafkaConfig.USER_DELETED_TOPIC), eq(sampleUserId), eventCaptor.capture());
        UserDeletedEvent capturedEvent = eventCaptor.getValue();
        assertEquals(sampleUserId, capturedEvent.userId());
    }

    @Test
    void deleteById_UserNotFound_ThrowsException() {
        when(userRepository.existsById(sampleUserId)).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteById(sampleUserId));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void findAll_ReturnsPageOfDto() {
        List<User> userList = Arrays.asList(sampleUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(userList, pageable, 1);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(sampleUser)).thenReturn(sampleUserDto);

        Page<UserDto> resultPage = userService.findAll(pageable);

        assertEquals(1, resultPage.getTotalElements());
        assertEquals(sampleUserDto, resultPage.getContent().get(0));
    }

    @Test
    void getRolesByUserId_UserExists_ReturnsRoles() {
        when(userRepository.findById(sampleUserId)).thenReturn(Optional.of(sampleUser));
        List<Role> result = userService.getRolesByUserId(sampleUserId);
        assertEquals(1, result.size());
        assertEquals("USER", result.get(0).getName());
    }

    @Test
    void getRolesByUserId_UserNotFound_ThrowsException() {
        when(userRepository.findById(sampleUserId)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getRolesByUserId(sampleUserId));
        assertEquals("User not found", ex.getMessage());
    }
}
