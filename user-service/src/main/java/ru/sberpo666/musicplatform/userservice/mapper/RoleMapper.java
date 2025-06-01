package ru.sberpo666.musicplatform.userservice.mapper;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.sberpo666.musicplatform.userservice.entity.Role;
import ru.sberpo666.musicplatform.userservice.repository.RoleRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class RoleMapper {
    @Autowired
    protected RoleRepository roleRepository;


    public List<Role> map(Integer[] roleIds) {
        if (roleIds == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(roleIds).map(this::map).collect(Collectors.toList());
    }


    public Role map(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }


    public Integer[] map(List<Role> roles) {
        if (roles == null) {
            return new Integer[0];
        }
        return roles.stream().map(Role::getRoleId).toArray(Integer[]::new);
    }
}
