package ru.sberpo666.musicplatform.userservice.service;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sberpo666.musicplatform.userservice.config.KafkaConfig;
import ru.sberpo666.musicplatform.userservice.dto.UserDto;
import ru.sberpo666.musicplatform.userservice.entity.User;
import ru.sberpo666.musicplatform.userservice.mapper.UserMapper;
import ru.sberpo666.musicplatform.userservice.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<Long, Long> kafkaTemplate;

    @Transactional(readOnly = true)
    public UserDto findById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UserDto add(UserDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = userMapper.updateFromDto(userDto, userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
        user.setUserId(userId);

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
        kafkaTemplate.send(KafkaConfig.USER_DELETED_TOPIC, userId, userId);
    }

    @Transactional
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}