package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serveur.DataBase;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {
    static Address address;
    static User user;
    static StoreType type;
    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        if(Store.exist("TEST"))
            Store.getByRef("TEST").delete();

        if(Store.exist("TEST_CHANGE"))
            Store.getByRef("TEST_CHANGE").delete();

        if(User.exist("testStore")) {
            user.getAddresses().forEach(a -> {
                try {
                    a.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            User.getByEmail("testStore").delete();
        }

        if(StoreType.exists("test"))
            StoreType.getByName("test").delete();

        user = User.register("testStore", "testStore", "testStore", "testStore", "testStore", false);
        address = Address.create(1, "rue", "rennes", 35000, "France", user);
        type = StoreType.create("test");
    }
    @AfterAll
    static void delete() throws Exception {
        if(Store.exist("TEST"))
            Store.getByRef("TEST").delete();

        if(Store.exist("TEST_CHANGE"))
            Store.getByRef("TEST_CHANGE").delete();

        if(User.exist("testStore")) {
            user.getAddresses().forEach(a -> {
                try {
                    a.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            User.getByEmail("testStore").delete();
        }
    }

    @Test
    void createDelete() throws Exception {
        assertFalse(Store.exist("TEST"));
        Store s = Store.create("test", "TEST", address, user, "tel", "mail", type);
        assertTrue(Store.exist("TEST"));

        s.delete();
        assertFalse(Store.exist("TEST"));
    }

    @Test
    void change() throws Exception {
        Store s = Store.create("test", "TEST_CHANGE", address, user, "tel", "mail", type);

        assertEquals("mail", s.getMail());
        s.setMail("mymail");
        s = Store.getById(s.getId());
        assertEquals("mymail", s.getMail());
    }
}