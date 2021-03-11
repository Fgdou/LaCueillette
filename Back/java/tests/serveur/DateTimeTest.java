package serveur;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeTest {

    @Test
    void add() throws Exception {
        DateTime d1 = new DateTime("2021-02-19 10:00:01");
        DateTime d2 = d1.add(0, 0, 0, 2, 1, 0);
        assertEquals("2021-02-19 12:01:01", d2.toString());

        d2 = d1.add(0, 0, 0, 24, 0, 0);
        assertEquals("2021-02-20 10:00:01", d2.toString());

        d2 = d1.add(0, 0, 10, 0, 0, 0);
        assertEquals("2021-03-01 10:00:01", d2.toString());
    }

    @Test
    void testToString() throws Exception {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        assertEquals(dt.format(new Date()), new DateTime().toString());
        Calendar c = Calendar.getInstance();
        c.set(2021, Calendar.FEBRUARY, 19, 9, 55, 55);
        assertEquals(dt.format(c.getTime()), new DateTime(2021, 02, 19, 9, 55, 55).toString());

        assertEquals("2021-02-19 10:00:01", new DateTime("2021-02-19 10:00:01").toString());
        assertEquals("0000-00-00 00:00:00", new DateTime("0000-00-00 00:00:00").toString());
    }

    @Test
    void testEquals() throws Exception {
        DateTime d1 = new DateTime();
        DateTime d2 = new DateTime();
        DateTime d3 = new DateTime("2021-02-19 10:00:01");

        assertTrue(d1.equals(d1));
        assertTrue(d1.equals(d2));
        assertTrue(d2.equals(d1));
        assertFalse(d1.equals(d3));
        assertFalse(d3.equals(d1));
    }
    @Test
    void compareTo() throws Exception {
        DateTime d1 = new DateTime();
        DateTime d2 = new DateTime();
        DateTime d3 = new DateTime("2021-02-19 10:00:01");

        assertEquals(0, d1.compareTo(d1));
        assertEquals(0, d1.compareTo(d2));
        assertEquals(1, d1.compareTo(d3));
        assertEquals(-1, d3.compareTo(d1));
    }

    @Test
    void getLastDayOfMonth() throws Exception {
        assertEquals(31, new DateTime("2021-01-19 10:00:01").getLastDayOfMonth());
        assertEquals(28, new DateTime("2021-02-19 10:00:01").getLastDayOfMonth());
        assertEquals(29, new DateTime("2020-02-19 10:00:01").getLastDayOfMonth());
        assertEquals(31, new DateTime("2021-03-19 10:00:01").getLastDayOfMonth());
        assertEquals(30, new DateTime("2021-04-19 10:00:01").getLastDayOfMonth());
        assertEquals(31, new DateTime("2021-05-19 10:00:01").getLastDayOfMonth());
        assertEquals(30, new DateTime("2021-06-19 10:00:01").getLastDayOfMonth());
        assertEquals(31, new DateTime("2021-07-19 10:00:01").getLastDayOfMonth());
        assertEquals(31, new DateTime("2021-08-19 10:00:01").getLastDayOfMonth());
        assertEquals(30, new DateTime("2021-09-19 10:00:01").getLastDayOfMonth());
        assertEquals(31, new DateTime("2021-10-19 10:00:01").getLastDayOfMonth());
        assertEquals(30, new DateTime("2021-11-19 10:00:01").getLastDayOfMonth());
        assertEquals(31, new DateTime("2021-12-19 10:00:01").getLastDayOfMonth());
    }

}