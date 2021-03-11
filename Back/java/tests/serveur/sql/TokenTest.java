package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import serveur.DataBase;
import serveur.DateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {
    static int id;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();

        List<Token> tokens = Token.getByUser(null);
        for(Token t : tokens)
            t.delete();

        DateTime date = new DateTime();
        date = date.add(0, 0, 0, 2, 0, 0);

        Token t = Token.create(0, null, date, "test");
        id = Token.getByValue(t.getValue()).getId();
    }
    @AfterAll
    static void delete() throws Exception {
        List<Token> tokens = Token.getByUser(null);
        for(Token t : tokens)
            t.delete();
    }

    @Test
    void create() throws Exception {
        DateTime date = new DateTime();
        date = date.add(0, 0, 0, 2, 0, 0);

        Token t = Token.create(0, null, date, "test");

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

        t.delete();

        DateTime date = new DateTime();
        date = date.add(0, 0, 0, 2, 0, 0);

        t = Token.create(0, null, date, "test");
        id = t.getId();
    }

    @Test
    void getTokens() throws Exception {
        Token t1 = Token.getById(id);

        List<Token> tokens = Token.getByUser(null);
        assertEquals(1, tokens.size());
        assertEquals(t1, tokens.get(0));

        Token t2 = Token.create(0, null, new DateTime(), "test");

        tokens = Token.getByUser(null);
        assertEquals(2, tokens.size());
        assertTrue(tokens.contains(t1));
        assertTrue(tokens.contains(t2));

        t2.delete();
    }
}