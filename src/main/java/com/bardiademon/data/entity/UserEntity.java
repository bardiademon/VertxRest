package com.bardiademon.data.entity;

import com.bardiademon.data.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder
@Getter
@Setter
@ToString
public final class UserEntity extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Set<UserRole> userRoles;
}
