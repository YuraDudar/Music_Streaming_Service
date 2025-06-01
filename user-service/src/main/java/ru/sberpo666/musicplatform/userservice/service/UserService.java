package ru.sberpo666.musicplatform.userservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<UUID, Object> kafkaTemplate;

    @Transactional(readOnly = true)
    public UserDto findById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDto add(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        User addedUser = userRepository.saveAndFlush(user);

        kafkaTemplate.send(KafkaConfig.USER_CREATED_TOPIC, addedUser.getUserId(), new UserCreatedEvent(
                addedUser.getUserId(),
                addedUser.getEmail(),
                addedUser.getPasswordHash(),
                addedUser.getRoles().stream().map(Role::getName).toArray(String[]::new)
        ));

        return userMapper.toDto(addedUser);
    }

    @Transactional
    public UserDto update(UUID userId, UserDto userDto) {
        User user = userMapper.updateFromDto(userDto, userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        user.setUserId(userId);
        user.setUpdatedAt(OffsetDateTime.now());

        User updatedUser = userRepository.saveAndFlush(user);

        kafkaTemplate.send(KafkaConfig.USER_UPDATED_TOPIC, userId, new UserUpdatedEvent(
                user.getUserId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRoles().stream().map(Role::getName).toArray(String[]::new)
        ));

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteById(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
        kafkaTemplate.send(KafkaConfig.USER_DELETED_TOPIC, userId, new UserDeletedEvent(userId));
    }

    @Transactional
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    @Transactional
    public List<Role> getRolesByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getRoles();
    }
}