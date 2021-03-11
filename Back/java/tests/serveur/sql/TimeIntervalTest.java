package serveur.sql;

import org.junit.jupiter.api.Test;
import serveur.DateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeIntervalTest {

    @Test
    void isIn() throws Exception {
        TimeInterval t1 = new TimeInterval(new DateTime("2021-02-21 00:00:00"), new DateTime("2021-02-23 23:59:59"));

        assertFalse(t1.isIn(new DateTime("2021-02-19 00:00:00")));
        assertFalse(t1.isIn(new DateTime("2021-02-20 23:59:59")));
        assertTrue(t1.isIn(new DateTime("2021-02-21 00:00:00")));
        assertTrue(t1.isIn(new DateTime("2021-02-22 00:00:00")));
        assertTrue(t1.isIn(new DateTime("2021-02-23 23:59:59")));
        assertFalse(t1.isIn(new DateTime("2021-02-24 00:00:00")));
        assertFalse(t1.isIn(new DateTime("2021-02-25 23:59:59")));
    }
}