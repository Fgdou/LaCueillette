package serveur.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import serveur.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represent a category of product
 */

public class ProductCategory {
    private int id;
    private String name;
    private int parent_id;

    private ProductCategory(){}
    private ProductCategory(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        name = rs.getString(2);
    }

    /**
     * Create on database
     * @param name  the display name
     * @param parent    the parent if anyone
     * @return  the class created on database
     */
    public static ProductCategory create(String name, ProductCategory parent) throws Exception {

        if(exist(name))
            throw new Exception("Category already exist");

        int parent_id = (parent == null) ? 0 : parent.getId();

        String sql = "INSERT INTO ProductsCategory (parent_id, name) VALUES (?, ?);";
        String[] tab = new String[]{
                String.valueOf(parent_id),
                name
        };

        DataBase.getInstance().query(sql, tab);

        return getByName(name);
    }

    /**
     * @param name of the category
     * @return  if this category already exist
     */
    public static boolean exist(String name) throws Exception {
        try{
            getByName(name);
            return true;
        }catch (Exception e){
            if(e.getMessage().equals("Category not found"))
                return false;
            throw e;
        }
    }

    /**
     * @param name of the category
     * @return the class
     */
    public static ProductCategory getByName(String name) throws Exception {
        String sql = "SELECT * FROM ProductsCategory WHERE name = ?;";
        String[] tab = new String[]{
                name
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);

        if(!rs.next())
            throw new Exception("Category not found");

        return new ProductCategory(rs);
    }

    public static List<ProductCategory> getAll() throws Exception {
        String sql = "SELECT * FROM ProductsCategory";

        ResultSet rs = DataBase.getInstance().query(sql);

        List<ProductCategory> list = new LinkedList<>();

        while(rs.next())
            list.add(new ProductCategory(rs));

        return list;
    }

    /**
     * @param id    the id
     * @return the class
     * @throws Exception
     */
    public static ProductCategory getById(int id) throws Exception {
        String sql = "SELECT * FROM ProductsCategory WHERE id = ?;";
        String[] tab = new String[]{
                String.valueOf(id)
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);

        if(!rs.next())
            throw new Exception("Category not found");

        return new ProductCategory(rs);
    }

    /**
     * @return all category that have this category for parent
     */
    public List<ProductCategory> getChildren() throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("ProductsCategory", "parent_id", String.valueOf(id));

        List<ProductCategory> list = new LinkedList<>();

        while(rs.next())
            list.add(new ProductCategory(rs));

        return list;
    }
    public int getParentId(){
        return parent_id;
    }
    @JsonIgnore
    public ProductCategory getParent() throws Exception {
        return ProductCategory.getById(parent_id);
    }

    /**
     * Remove on the database
     */
    public void delete() throws Exception {
        DataBase.getInstance().delete("ProductsCategory", id);
    }

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }

    public boolean equals(Object o){
        return (o instanceof ProductCategory && ((ProductCategory)o).name.equals(name));
    }
    public String toString(){
        return "Tag("+id+"): " + name;
    }
}
