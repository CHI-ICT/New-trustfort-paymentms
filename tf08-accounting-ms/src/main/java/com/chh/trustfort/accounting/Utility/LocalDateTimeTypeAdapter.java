package com.chh.trustfort.accounting.Utility;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(value != null ? value.format(formatter) : null);
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String text = in.nextString();
        return (text != null && !text.isEmpty()) ? LocalDateTime.parse(text, formatter) : null;
    }
}
