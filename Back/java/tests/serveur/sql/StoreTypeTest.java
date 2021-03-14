package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serveur.DataBase;

import static org.junit.jupiter.api.Assertions.*;

class StoreTypeTest {
    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        if(StoreType.exists("test"))
            StoreType.getByName("test").delete();
    }
    @AfterAll
    static void delete() throws Exception {
        if(StoreType.exists("test"))
            StoreType.getByName("test").delete();
    }

    @Test
    void createDelete() throws Exception {
        assertFalse(StoreType.exists("test"));

        StoreType t = StoreType.create("test");

        assertTrue(StoreType.exists("test"));

        assertEquals(t, StoreType.getByName("test"));
        assertEquals(t, StoreType.getById(t.getId()));

        t.delete();

        assertFalse(StoreType.exists("test"));
    }
}