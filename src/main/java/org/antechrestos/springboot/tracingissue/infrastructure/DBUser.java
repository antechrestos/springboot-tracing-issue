package org.antechrestos.springboot.tracingissue.infrastructure;

import org.springframework.data.annotation.Id;

public record DBUser(@Id String id, String firstname, String name) {
}
