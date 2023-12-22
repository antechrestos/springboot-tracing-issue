package org.antechrestos.springboot.tracingissue.domain

import java.util.UUID

data class User(val id: String, val firstname: String, val name: String) {

    companion object {
        fun newUser(firstname: String, name: String) = User(
            id = UUID.randomUUID().toString(),
            firstname = firstname,
            name = name
        )
    }
}
