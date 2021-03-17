package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serveur.DataBase;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {
    static User user;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        clear();
        User us = User.register("testCart", "testCart", "testCart", "testCart", "testCart", false);
        us.setEmailVerified(true);
    }
    @AfterAll
    static void clear() throws Exception {
        if(User.exist("testCart"))
            User.getByEmail("testCart").delete();
        Cart.getByUser(user).clear();
    }

    @Test
    void test() throws Exception {
        Cart c = Cart.getByUser(user);

        //TODO add products
    }
}