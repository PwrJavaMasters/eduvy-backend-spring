package com.eduvy.tutoring.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampSerializer extends StdSerializer<Timestamp> {

    private static final ZoneId TARGET_ZONE = ZoneId.of("Europe/Paris");

    public TimestampSerializer() {
        this(null);
    }

    public TimestampSerializer(Class<Timestamp> t) {
        super(t);
    }

    @Override
    public void serialize(Timestamp value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        // Convert Timestamp to ZonedDateTime in the system default time zone
        ZonedDateTime zonedDateTime = value.toInstant().atZone(ZoneId.systemDefault());
        // Adjust to the target time zone
        ZonedDateTime targetZonedDateTime = zonedDateTime.withZoneSameInstant(TARGET_ZONE);
        // Define a formatter matching the default Timestamp format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        // Format the adjusted ZonedDateTime
        String formattedDateTime = targetZonedDateTime.format(formatter);
        // Write the formatted date-time string
        gen.writeString(formattedDateTime);
    }
}