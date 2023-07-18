package com.bardiademon.data.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@ToString
public final class UserEntity extends BaseEntity {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
