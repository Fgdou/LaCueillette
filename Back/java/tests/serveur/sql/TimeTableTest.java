package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serveur.DataBase;
import serveur.Time;

import static org.junit.jupiter.api.Assertions.*;

class TimeTableTest {
    @BeforeAll
    public static void init() throws Exception {
        DataBase.createInstance();
        TimeTable t = TimeTable.getByStore(0);
        t.clear();
    }
    @AfterAll
    public static void clean() throws Exception {
        TimeTable t = TimeTable.getByStore(0);
        t.clear();
    }
    @Test
    public void test() throws Exception {
        TimeTable table = TimeTable.getByStore(0);

        Time time = new Time(10, 12, 0);
        Time time2 = new Time(16, 12, 0);
        Time time3 = new Time(18, 12, 0);

        for(int i=0; i<7; i++) {
            assertEquals(0, table.getDay(i).size());
            assertFalse(table.isOpen(time, i));
        }

        table.addInterval(new Time(8, 0, 0), new Time(12, 0, 0), 0);
        table.addInterval(new Time(14, 0, 0), new Time(18, 0, 0), 0);

        assertTrue(table.isOpen(time, 0));
        assertTrue(table.isOpen(time2, 0));
        assertFalse(table.isOpen(time3, 0));
        assertEquals(2, table.getDay(0).size());

        assertTrue(table.isOpen(new Time(8, 0, 0), 0));
        assertTrue(table.isOpen(new Time(12, 0, 0), 0));
        assertTrue(table.isOpen(new Time(14, 0, 0), 0));
        assertTrue(table.isOpen(new Time(18, 0, 0), 0));

        assertFalse(table.isOpen(new Time(7, 59, 59), 0));
        assertFalse(table.isOpen(new Time(12, 0, 1), 0));
        assertFalse(table.isOpen(new Time(13, 59, 59), 0));
        assertFalse(table.isOpen(new Time(18, 0, 1), 0));

        for(int i=1; i<7; i++) {
            assertEquals(0, table.getDay(i).size());
            assertFalse(table.isOpen(time, i));
        }
    }
}