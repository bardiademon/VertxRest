package com.bardiademon.data.enums;

import java.time.format.DateTimeFormatter;

public enum DateTimeFormatPattern {
    DATE_TIME_PATTERN(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    TIME_PATTERN(DateTimeFormatter.ofPattern("HH:mm:ss")),
    DATE_PATTERN(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    //
    ;
    public final DateTimeFormatter pattern;

    DateTimeFormatPattern(final DateTimeFormatter pattern) {
        this.pattern = pattern;
    }
}
