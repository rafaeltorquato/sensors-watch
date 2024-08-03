package br.com.torquato.sensors.warehouse.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateTimeUtils {

    public static LocalDateTime toLocalDateTime(final Long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),
                TimeZone.getDefault().toZoneId());
    }
}
