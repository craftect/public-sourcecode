package com.lifehackaid.netincomesimulator.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DateUUIDGeneratorUtilTest {

    @Test
    void generateDateUUID_ShouldReturnValidDateUUID() {

        String dateUUID = DateUUIDGeneratorUtil.generateDateUUID();

        System.out.println(dateUUID);
        
        // 例: 20220614123456789abcdef0123456789
        Assertions.assertTrue(dateUUID.matches("^\\d{14}[a-f0-9]{32}$"));
    }
}
