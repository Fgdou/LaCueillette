package server.sql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.DataBase;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
    }

    @Test
    void createDelete() throws Exception {
        assertFalse(Tag.exist("test"));

        Tag t = Tag.create("test");

        assertTrue(Tag.exist("test"));

        assertEquals(t.getName(), Tag.getById(t.getId()).getName());
        assertEquals(t.getId(), Tag.getByName(t.getName()).getId());

        t.delete();

        assertFalse(Tag.exist("test"));
    }
}