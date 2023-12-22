package org.antechrestos.springboot.tracingissue.domain;

import java.util.UUID;

public record User(String id, String firstname, String name) {

    public static User newUser(String firstname, String name) {
        return new User(
                UUID.randomUUID().toString(),
                firstname,
                name
        );
    }
}

