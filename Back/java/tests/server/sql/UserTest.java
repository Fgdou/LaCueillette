package server.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.DataBase;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    static int id = 0;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        if(!User.exist("testJUnit@example.com"))
            id = User.register("test", "test", "", "testJUnit@example.com", "test", false).getId();
        else
            id = User.getByEmail("testJUnit@example.com").getId();
    }
    @AfterAll
    static void delete() throws Exception {
        User.getById(id).delete();
        if(User.exist("testCreate@example.com"))
            User.getByEmail("testCreate@example.com").delete();
    }

    @Test
    void createDelete() throws Exception {
        assertFalse(User.exist("testCreate@example.com"));
        User u = User.register("testCreate", "testCreate", "", "testCreate@example.com", "testCreate", false);
        assertTrue(User.exist("testCreate@example.com"));
        u.delete();
        assertFalse(User.exist("testCreate@example.com"));
    }
    @Test
    void changes() throws Exception {
        User us = User.getById(id);

        assertEquals(id, us.getId());
        assertEquals("testJUnit@example.com", us.getMail());
        assertEquals("test", us.getName());
        assertEquals("test", us.getSurname());
        assertEquals("", us.getTel());
        assertFalse(us.isAdmin());
        assertFalse(us.isEmailVerified());

        us.setName("name");
        us.setSurname("surname");
        us.setTel("tel");
        us.setEmailVerified(true);
        us.setAdmin(true);

        us = User.getById(id);

        assertEquals(id, us.getId());
        assertEquals("testJUnit@example.com", us.getMail());
        assertEquals("name", us.getName());
        assertEquals("surname", us.getSurname());
        assertEquals("tel", us.getTel());
        assertTrue(us.isAdmin());
        assertTrue(us.isEmailVerified());

        us.setName("test");
        us.setSurname("test");
        us.setTel("");
        us.setEmailVerified(false);
        us.setAdmin(false);

        us = User.getById(id);

        assertEquals(id, us.getId());
        assertEquals("testJUnit@example.com", us.getMail());
        assertEquals("test", us.getName());
        assertEquals("test", us.getSurname());
        assertEquals("", us.getTel());
        assertFalse(us.isAdmin());
        assertFalse(us.isEmailVerified());
    }
}