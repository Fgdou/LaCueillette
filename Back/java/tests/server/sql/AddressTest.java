package server.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.DataBase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    static int id = 0;
    static int user = 0;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        Address a = Address.create(1, "way", "city", 12345, "state", null);
        User us = User.register("testAdd", "testAdd", "testAdd", "testAdd", "testAdd", false);
        id = a.getId();
        user = us.getId();
    }
    @AfterAll
    static void delete() throws Exception{
        Address.getById(id).delete();
        List<Address> list = Address.getByUser(null);
        for(Address e : list){
            e.delete();
        }

        User.getById(user).delete();
    }

    @Test
    void user() throws Exception{
        User us = User.getById(user);

        assertEquals(0, us.getAddresses().size());

        Address a1 = Address.create(0, "w", "c", 0, "s", us);

        List<Address> list = us.getAddresses();
        assertEquals(1, list.size());
        assertEquals(a1, list.get(0));

        a1.delete();

        assertEquals(0, us.getAddresses().size());
    }

    @Test
    void modify() throws Exception{
        Address a = Address.getById(id);

        assertEquals("city", a.getCity());
        assertEquals(12345, a.getPostalcode());
        assertEquals("state", a.getState());
        assertEquals("way", a.getWay());
        assertEquals(1, a.getNumber());

        a.setCity("c");
        a.setNumber(2);
        a.setPostalcode(1);
        a.setState("s");
        a.setWay("w");

        a = Address.getById(id);

        assertEquals("c", a.getCity());
        assertEquals(1, a.getPostalcode());
        assertEquals("s", a.getState());
        assertEquals("w", a.getWay());
        assertEquals(2, a.getNumber());

        a.setCity("city");
        a.setNumber(1);
        a.setPostalcode(12345);
        a.setState("state");
        a.setWay("way");

        assertEquals("city", a.getCity());
        assertEquals(12345, a.getPostalcode());
        assertEquals("state", a.getState());
        assertEquals("way", a.getWay());
        assertEquals(1, a.getNumber());
    }
}