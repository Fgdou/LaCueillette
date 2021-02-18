package server.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.DataBase;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {
    static int id;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 2);

        Token t = Token.create(0, null, c.getTime());
        id = Token.getByValue(t.getValue()).getId();
    }
    @AfterAll
    static void delete() throws Exception {
        Token.getById(id).delete();
    }

    @Test
    void create() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DAY_OF_MONTH, 2);

        Token t = Token.create(0, null, c.getTime());

        assertTrue(Token.exist(t.getValue()));

        t = Token.getById(t.getId());

        assertEquals(0, t.getType());
        assertNull(t.getUser());

        t.delete();

        assertFalse(Token.exist(t.getValue()));
    }

    @Test
    void use() throws Exception {
        Token t = Token.getById(id);

        assertTrue(t.isValid());

        t.use();
        t = Token.getById(id);

        assertFalse(t.isValid());
    }
}