package com.bardiademon.controller.adapter;

import com.bardiademon.data.enums.DateTimeFormatPattern;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class LocalDateTimeGsonAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(final JsonWriter jsonWriter , final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(DateTimeFormatPattern.DATE_TIME_PATTERN.pattern.format(localDateTime));
        }
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.hasNext()) {
            final String value = jsonReader.nextString();
            if (value != null) {
                return LocalDateTime.parse(value , DateTimeFormatPattern.DATE_TIME_PATTERN.pattern);
            }
        }
        return null;
    }
}
