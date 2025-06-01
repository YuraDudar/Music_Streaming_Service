package ru.sberpo666.musicplatform.userservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    private Integer roleId;
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;
}
