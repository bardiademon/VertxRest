package com.bardiademon.data.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class BaseDto {
    public static final String PARAM_KEY_DESCRIPTION = "description";

    private final String description;
}
