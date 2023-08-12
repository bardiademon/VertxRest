package com.bardiademon.controller.adapter;

import com.bardiademon.data.enums.DateTimeFormatPattern;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class TimeGsonAdapter extends TypeAdapter<Time> {

    @Override
    public void write(final JsonWriter jsonWriter , final Time time) throws IOException {
        if (time == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(DateTimeFormatPattern.TIME_PATTERN.pattern.format(time.toLocalTime()));
        }
    }

    @Override
    public Time read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.hasNext()) {
            final String value = jsonReader.nextString();
            if (value != null) {
                return Time.valueOf(value);
            }
        }
        return null;
    }
}
