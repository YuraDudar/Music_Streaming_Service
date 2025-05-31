package ru.sberpo666.musicplatform.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sberpo666.musicplatform.userservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
