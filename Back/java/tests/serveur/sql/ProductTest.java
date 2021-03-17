package serveur.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import serveur.DataBase;
import serveur.DateTime;
import serveur.Time;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    static Store st;
    static User us;
    static StoreType storeType;
    static ProductCategory prodCat;

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        clear();
        us = User.register("testProduct", "testProduct", "", "testProduct", "testProduct", false);
        us.setEmailVerified(true);

        storeType = StoreType.create("testProduct");

        st = Store.create("testProduct",
                Address.create(0, "testProduct", "testProduct", 0, "testProduct", us),
                us,
                "",
                "testProduct",
                storeType
                );

        prodCat = ProductCategory.create("testProduct", null);
    }
    @AfterAll
    static void clear() throws Exception {
        if(User.exist("testProduct"))
            User.getByEmail("testProduct").delete();
        if(Store.exist("testProduct"))
            Store.getByRef("testProduct").delete();
        if(StoreType.exists("testProduct"))
            StoreType.getByName("testProduct").delete();
        if(ProductCategory.exist("testProduct"))
            ProductCategory.getByName("testProduct").delete();
    }

    @Test
    void test() throws Exception {
        assertFalse(Product.exist(st, "testProduct"));

        Product p = Product.create("testProduct",
                1,
                false,
                prodCat,
                st,
                false,
                false,
                .20f,
                new DateTime(0),
                new DateTime(0),
                new DateTime(0),
                "");

        assertTrue(Product.exist(st, "testProduct"));

        assertEquals(0, p.getSubProducts().size());

        SubProduct s1 = SubProduct.create(18, "S", p);
        SubProduct s2 = SubProduct.create(18, "M", p);
        SubProduct s3 = SubProduct.create(18, "L", p);

        List<SubProduct> list = p.getSubProducts();
        assertEquals(3, list.size());
        assertTrue(list.contains(s1));
        assertTrue(list.contains(s2));
        assertTrue(list.contains(s3));

        s2.delete();

        assertEquals(2, p.getSubProducts().size());

        p.delete();
    }
}