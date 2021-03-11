package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serveur.DataBase;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    static int id = 0;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        User us;
        if(!User.exist("testJUnit@example.com"))
            us = User.register("test", "test", "", "testJUnit@example.com", "test", false);
        else
            us = User.getByEmail("testJUnit@example.com");

        id = us.getId();

        System.out.println(us);
    }
    @AfterAll
    static void delete() throws Exception {
        User us = User.getById(id);
        System.out.println(us);
        us.delete();
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

    @Test
    void login() throws Exception{
        User us = User.getById(id);
        boolean lastEmailVerified = us.isEmailVerified();


        try{
            Thread.sleep(2000);
            us.login("abcd", "test");
            fail();
        }catch (Exception e){
            assertEquals("Wrong password", e.getMessage());
        }

        us.setEmailVerified(false);
        try{
            us.login("test", "test");
            fail();
        }catch (Exception e){
            assertEquals("Email not verified", e.getMessage());
        }
        us.setEmailVerified(true);

        Token t;
        try{
            t = us.login("test", "test");

            assertTrue(t.isValid());
            User cp = t.getUser();

            assertEquals(us, cp);

            us.logout(t);

            t = Token.getByValue(t.getValue());

            assertFalse(t.isValid());

        }catch (Exception e){
            fail(e.getMessage());
        }finally {
            us.setEmailVerified(lastEmailVerified);
        }


    }
}