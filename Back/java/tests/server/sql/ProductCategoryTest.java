package server.sql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import server.DataBase;

import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryTest {

    @BeforeAll
    static void init() throws Exception {
        DataBase.createInstance();
        if(ProductCategory.exist("test"))
            ProductCategory.getByName("test").delete();
        if(ProductCategory.exist("testChild"))
            ProductCategory.getByName("testChild").delete();
    }
    @AfterAll
    static void delete() throws Exception {
        if(ProductCategory.exist("test"))
            ProductCategory.getByName("test").delete();
        if(ProductCategory.exist("testChild"))
            ProductCategory.getByName("testChild").delete();
    }

    @Test
    void createDelete() throws Exception {
        assertFalse(ProductCategory.exist("test"));

        ProductCategory pc1 = ProductCategory.create("test", null);

        ProductCategory pc2 = ProductCategory.getByName("test");
        ProductCategory pc3 = ProductCategory.getById(pc1.getId());
        assertTrue(ProductCategory.exist("test"));

        assertEquals(pc1, pc2);
        assertEquals(pc1, pc3);

        assertEquals(0, pc1.getChildren().size());

        ProductCategory child = ProductCategory.create("testChild", pc1);
        assertTrue(ProductCategory.exist("testChild"));

        List<ProductCategory> list = pc1.getChildren();
        assertEquals(1, list.size());
        assertEquals(child, list.get(0));

        child.delete();
        pc1.delete();
    }

}