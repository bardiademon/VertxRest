package com.bardiademon.data.enums;

import java.util.List;

public enum UserRole {
    ANY,
    BARDIADEMON, ADMIN, USER
    //
    ;

    public static List<UserRole> admins() {
        return List.of(BARDIADEMON , ADMIN);
    }
}
