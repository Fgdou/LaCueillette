package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the lower part of the product
 * The special_tag represent the information, like the size or the color
 */

public class SubProduct implements Comparable {
    private int id;
    private int quantity;
    private String special_tag;
    private DateTime created;
    private int product_id;

    protected SubProduct(ResultSet rs) throws Exception{
        id = rs.getInt(1);
        quantity = rs.getInt(2);
        special_tag = rs.getString(3);
        created = new DateTime(rs.getString(4));
        product_id = rs.getInt(5);
    }

    /**
     * @param id
     * @return the subproduct
     */
    public static SubProduct getById(int id) throws Exception{
        ResultSet rs = DataBase.getInstance().getByCondition("SubProducts", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("SubProduct not found");
        return new SubProduct(rs);
    }

    /**
     * Create the subproduct in the database
     * @param quantity  quantity in number
     * @param special_tag the tag
     * @param p the product
     * @return the new subproduct
     */
    public static SubProduct create(int quantity, String special_tag, Product p) throws Exception{

        String sql = "INSERT INTO SubProducts (quantity, special_tag, created, product_id, kg) VALUES (?, ?, NOW(), ?, 0)";
        String[] tab = new String[]{
                String.valueOf(quantity),
                special_tag,
                String.valueOf(p.getId())
        };
        DataBase.getInstance().query(sql, tab);

        return getById(DataBase.getInstance().getLastId("SubProducts"));
    }

    /**
     * @param p the product
     * @return all of the subproduct related
     */
    public static List<SubProduct> getByProduct(Product p) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("SubProducts", "product_id", String.valueOf(p.getId()));

        List<SubProduct> list = new LinkedList<>();

        while(rs.next())
            list.add(new SubProduct(rs));

        return list;
    }

    public int getQuantity() throws Exception {
        return quantity;
    }
    public int getId() {
        return id;
    }
    public String getSpecial_tag() {
        return special_tag;
    }
    public DateTime getCreated() {
        return created;
    }
    public Product getProduct() throws Exception {
        return Product.getById(product_id);
    }
    public void setQuantity(int quantity) throws Exception {
        this.quantity = quantity;
        DataBase.getInstance().changeValue("SubProducts", "quantity", String.valueOf(quantity), id);
    }
    public void setSpecial_tag(String special_tag) throws Exception {
        this.special_tag = special_tag;
        DataBase.getInstance().changeValue("SubProducts", "special_tag", special_tag, id);
    }

    /**
     * Delete from the stock the quantity
     * @param quantity
     */
    public void buy(int quantity) throws Exception{
        if(quantity < this.quantity)
            throw new Exception("Not enough quantity");
        setQuantity(this.quantity-quantity);
    }

    /**
     * Delete the subproduct from the database
     */
    public void delete() throws Exception{
        DataBase.getInstance().delete("SubProducts", id);
    }

    public boolean equals(Object o){
        return (o instanceof SubProduct && ((SubProduct)o).id == id);
    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof SubProduct))
            return -1;
        return ((SubProduct)o).id - id;
    }
}
