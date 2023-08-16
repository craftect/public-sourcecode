package com.lifehackaid.netincomesimulator.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DateUUIDGeneratorUtil {

    public static String generateDateUUID() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String datePart = now.format(formatter);
        String uuidPart = UUID.randomUUID().toString().replace("-", "");

        return datePart + uuidPart;
    }
	
}
