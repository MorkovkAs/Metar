package com.antonklimakov.metar;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {
    @Test
    public void isNullOrEmptyTestForNull() {
        assertTrue(MainActivity.isNullOrEmpty(null));
    }

    @Test
    public void isNullOrEmptyTestForEmpty() {
        assertTrue(MainActivity.isNullOrEmpty(""));
    }

    @Test
    public void isNullOrEmptyTestForNotEmpty() {
        assertFalse(MainActivity.isNullOrEmpty("MorkovkA"));
    }

    @Test
    public void getMetarTestWithBadICAO() throws IOException {
        MainActivity testActivity = new MainActivity();
        String icao = "UUUU";
        Map result = testActivity.getMetar(icao);
        assertNotNull(result);
        assertEquals(result.size(), 9);
        assertEquals(result.get("conditions"), icao);
    }

    @Test
    public void getMetarTestWithGoodICAO() throws IOException {
        MainActivity testActivity = new MainActivity();
        String icao = "ULLI";
        Map result = testActivity.getMetar(icao);
        assertNotNull(result);
        assertEquals(result.size(), 9);

        assertFalse(MainActivity.isNullOrEmpty("metar"));
        assertFalse(MainActivity.isNullOrEmpty("conditions"));
        assertFalse(MainActivity.isNullOrEmpty("temperature"));
        assertFalse(MainActivity.isNullOrEmpty("dewpoint"));
        assertFalse(MainActivity.isNullOrEmpty("pressure"));
        assertFalse(MainActivity.isNullOrEmpty("winds"));
        assertFalse(MainActivity.isNullOrEmpty("visibility"));
        assertFalse(MainActivity.isNullOrEmpty("ceiling"));
        assertFalse(MainActivity.isNullOrEmpty("clouds"));
    }
}