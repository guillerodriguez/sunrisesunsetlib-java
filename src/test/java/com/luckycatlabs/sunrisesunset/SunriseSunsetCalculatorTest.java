/*
 * Copyright 2008-2009 Mike Reedell / LuckyCatLabs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luckycatlabs.sunrisesunset;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.luckycatlabs.sunrisesunset.util.BaseTestCase;

/**
 * Unit test for the SunriseSunsetCalculator class.
 */
public class SunriseSunsetCalculatorTest extends BaseTestCase {

    private SunriseSunsetCalculator calc;

    @Before
    public void setup() {
        // November 1, 2008
        super.setup(10, 1, 2008);
        calc = new SunriseSunsetCalculator(location, "America/New_York");
    }

    @Test
    public void testComputeAstronomicalSunrise() {
        assertTimeEquals("06:01", calc.getAstronomicalSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeAstronomicalSunset() {
        assertTimeEquals("19:32", calc.getAstronomicalSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeNauticalSunrise() {
        assertTimeEquals("06:33", calc.getNauticalSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeNauticalSunset() {
        assertTimeEquals("19:00", calc.getNauticalSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeCivilSunrise() {
        assertTimeEquals("07:05", calc.getCivilSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeCivilSunset() {
        assertTimeEquals("18:28", calc.getCivilSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeOfficialSunrise() {
        assertTimeEquals("07:33", calc.getOfficialSunriseForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeOfficialSunset() {
        assertTimeEquals("18:00", calc.getOfficialSunsetForDate(eventDate), eventDate.getTime().toString());
    }

    @Test
    public void testComputeSunriseArbitraryDegrees() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");

        assertHourMinuteEquals(calc.getCivilSunriseCalendarForDate(eventDate),
                SunriseSunsetCalculator.getSunrise(39.9937, -75.7850, tz, eventDate, 6.0));
        assertHourMinuteEquals(calc.getNauticalSunriseCalendarForDate(eventDate),
                SunriseSunsetCalculator.getSunrise(39.9937, -75.7850, tz, eventDate, 12.0));
        assertHourMinuteEquals(calc.getAstronomicalSunriseCalendarForDate(eventDate),
                SunriseSunsetCalculator.getSunrise(39.9937, -75.7850, tz, eventDate, 18.0));
    }

    @Test
    public void testComputeSunsetArbitraryDegrees() {
        TimeZone tz = TimeZone.getTimeZone("America/New_York");

        assertHourMinuteEquals(calc.getCivilSunsetCalendarForDate(eventDate),
                SunriseSunsetCalculator.getSunset(39.9937, -75.7850, tz, eventDate, 6.0));
        assertHourMinuteEquals(calc.getNauticalSunsetCalendarForDate(eventDate),
                SunriseSunsetCalculator.getSunset(39.9937, -75.7850, tz, eventDate, 12.0));
        assertHourMinuteEquals(calc.getAstronomicalSunsetCalendarForDate(eventDate),
                SunriseSunsetCalculator.getSunset(39.9937, -75.7850, tz, eventDate, 18.0));
    }

    private static void assertHourMinuteEquals(Calendar expected, Calendar actual) {
        assertEquals(expected.get(Calendar.HOUR_OF_DAY), actual.get(Calendar.HOUR_OF_DAY));
        assertEquals(expected.get(Calendar.MINUTE), actual.get(Calendar.MINUTE));
    }

    @Test
    public void testSpecificDateLocationAndTimezone() {
        Location loc = new Location("55.03", "82.91");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(loc, "GMT");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2012, 4, 7);

        String officialSunriseForDate = calculator.getOfficialSunriseForDate(calendar);
        assertEquals("22:35", officialSunriseForDate);

        Calendar officialSunriseCalendarForDate = calculator.getOfficialSunriseCalendarForDate(calendar);
        assertEquals(22, officialSunriseCalendarForDate.get(Calendar.HOUR_OF_DAY));
        assertEquals(35, officialSunriseCalendarForDate.get(Calendar.MINUTE));
        assertEquals(6, officialSunriseCalendarForDate.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testNonIntegerTimezoneOffset() {
        Location loc = new Location("22.56", "88.36");
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(loc, "Asia/Kolkata");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 12, 15);

        String officialSunriseForDate = calculator.getOfficialSunriseForDate(calendar);
        assertEquals("06:19", officialSunriseForDate);
    }

    @Test
    public void testNonIntegerTimezoneOffset2() {
        // Asia/Kathmandu (+5:45, no DST)
        Location kathmandu = new Location("27.7172", "85.3240");
        SunriseSunsetCalculator calc = new SunriseSunsetCalculator(kathmandu, "Asia/Kathmandu");

        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JUNE, 15);

        assertEquals("05:08", calc.getOfficialSunriseForDate(calendar));
        assertEquals("19:01", calc.getOfficialSunsetForDate(calendar));

        // Pacific/Chatham (+12:45), outside DST window (July is southern winter)
        Location chatham = new Location("-43.8800", "176.3200");
        calc = new SunriseSunsetCalculator(chatham, "Pacific/Chatham");

        calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.JULY, 15);

        assertEquals("08:29", calc.getOfficialSunriseForDate(calendar));
        assertEquals("17:42", calc.getOfficialSunsetForDate(calendar));
    }

    @Test
    public void testSunriseOnDSTStartDay() {
        // 2024-03-10 in NYC: DST starts at 02:00 EST -> 03:00 EDT.
        // Sunrise is at ~07:30 EDT (post-transition).
        // Passing a Calendar set to 00:30 EST (pre-transition) must not
        // throw away the DST adjustment.
        Location loc = new Location("39.9937", "-75.7850");
        SunriseSunsetCalculator calc = new SunriseSunsetCalculator(loc, "America/New_York");
        Calendar calBeforeTransition = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calBeforeTransition.set(2024, Calendar.MARCH, 10, 0, 30, 0);

        // Cross-check against a "safe" Calendar (post-transition same day).
        Calendar calAfterTransition = (Calendar) calBeforeTransition.clone();
        calAfterTransition.set(Calendar.HOUR_OF_DAY, 12);

        assertEquals(calc.getOfficialSunriseForDate(calAfterTransition), calc.getOfficialSunriseForDate(calBeforeTransition));
    }

    @Test
    public void testSunriseOnDSTEndDay() {
        // 2024-11-03 in NYC: DST ends at 02:00 EDT -> 01:00 EST.
        // Sunrise is at ~06:27 EST (post-transition).
        // Passing a Calendar set to 00:30 EDT (pre-transition, DST still in
        // effect) must not add a stale DST adjustment.
        Location loc = new Location("39.9937", "-75.7850");
        SunriseSunsetCalculator calc = new SunriseSunsetCalculator(loc, "America/New_York");
        Calendar calBeforeTransition = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        calBeforeTransition.set(2024, Calendar.NOVEMBER, 3, 0, 30, 0);

        Calendar calAfterTransition = (Calendar) calBeforeTransition.clone();
        calAfterTransition.set(Calendar.HOUR_OF_DAY, 12);

        assertEquals(calc.getOfficialSunriseForDate(calAfterTransition), calc.getOfficialSunriseForDate(calBeforeTransition));
    }

    @Test
    public void testSunriseOnHalfHourDSTStartDay() {
        // 2024-10-06 in Lord_Howe: DST starts at 02:00 +1030 -> 02:30 +11
        // (savings = 30 min). Sunrise is post-transition.
        // The sub-hour savings amount must be honored when the caller's
        // Calendar straddles the transition.
        Location loc = new Location("-31.5533", "159.0820");
        SunriseSunsetCalculator calc = new SunriseSunsetCalculator(loc, "Australia/Lord_Howe");
        Calendar calBeforeTransition = Calendar.getInstance(TimeZone.getTimeZone("Australia/Lord_Howe"));
        calBeforeTransition.set(2024, Calendar.OCTOBER, 6, 0, 30, 0);

        Calendar calAfterTransition = (Calendar) calBeforeTransition.clone();
        calAfterTransition.set(Calendar.HOUR_OF_DAY, 12);

        assertEquals(calc.getOfficialSunriseForDate(calAfterTransition), calc.getOfficialSunriseForDate(calBeforeTransition));
    }

    @Test
    public void testCrossTimezoneInputDoesNotShiftDay() {
        // The calendar date is identified by the input Calendar's wall-clock
        // year/month/day in its own TZ. A UTC-tagged Calendar for 2024-03-10
        // must yield the same result as an NYC-tagged Calendar for the same
        // calendar date, and the caller's Calendar must not be mutated.
        Location nyc = new Location("40.7128", "-74.0060");
        SunriseSunsetCalculator calc = new SunriseSunsetCalculator(nyc, "America/New_York");

        Calendar nycCal = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
        nycCal.set(2024, Calendar.MARCH, 10, 0, 0, 0);

        Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCal.set(2024, Calendar.MARCH, 10, 0, 0, 0);
        // To reproduce the bug, make sure Calendar is in isTimeSet=true state,
        // otherwise, the lazy-evaluation behavior would mask the issue.
        utcCal.getTimeInMillis();
        String utcCalTzIdBefore = utcCal.getTimeZone().getID();

        String nycResult = calc.getOfficialSunriseForDate(nycCal);
        String utcResult = calc.getOfficialSunriseForDate(utcCal);

        assertEquals("cross-TZ inputs must yield same result", nycResult, utcResult);
        assertEquals("caller Calendar TZ must not be mutated", utcCalTzIdBefore, utcCal.getTimeZone().getID());
    }

    @Test
    public void testSunsetAfterCalculatorMidnightCarriesToNextDay() {
        // When the sunset lands after midnight in the calculator's TZ, the
        // returned Calendar should be on the next day.
        // Philadelphia at -75°W with a UTC calculator on 2018-05-18:
        // sunset is at ~00:12 UTC on 2018-05-19.
        Location philadelphia = new Location("39.9522222", "-75.1641667");
        SunriseSunsetCalculator calc = new SunriseSunsetCalculator(philadelphia, "UTC");

        Calendar input = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        input.set(2018, Calendar.MAY, 18, 0, 0, 0);

        Calendar sunset = calc.getOfficialSunsetCalendarForDate(input);

        assertEquals("UTC", sunset.getTimeZone().getID());
        assertEquals(2018, sunset.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, sunset.get(Calendar.MONTH));
        assertEquals(19, sunset.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, sunset.get(Calendar.HOUR_OF_DAY));
        assertEquals(12, sunset.get(Calendar.MINUTE));
    }
}
