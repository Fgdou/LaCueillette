package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class SubProduct {
    private int id;
    private int quantity;
    private String special_tag;
    private DateTime created;
    private int product_id;
    private float kg;
    private boolean price_kg;

    protected SubProduct(ResultSet rs) throws Exception{
        id = rs.getInt(1);
        quantity = rs.getInt(2);
        special_tag = rs.getString(3);
        created = new DateTime(rs.getString(4));
        product_id = rs.getInt(5);
        kg = rs.getFloat(6);

        price_kg = Product.getById(product_id).isPrice_kg();
    }

    public static SubProduct getById(int id) throws Exception{
        ResultSet rs = DataBase.getInstance().getByCondition("SubProducts", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("SubProduct not found");
        return new SubProduct(rs);
    }

    public static SubProduct create(int quantity, String special_tag, Product p) throws Exception{
        String sql = "INSERT INTO SubProducts (quantity, special_tag, created, product_id, kg) VALUES (?, ?, NOW(), ?, 0); SELECT MAX(id) FROM SubProducts";
        String[] tab = new String[]{
                String.valueOf(quantity),
                special_tag,
                String.valueOf(p.getId())
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);
        int max = rs.getInt(1);

        return getById(max);
    }
    public static SubProduct create(float kg, String special_tag, Product p) throws Exception{
        String sql = "INSERT INTO SubProducts (quantity, special_tag, created, product_id, kg) VALUES (0, ?, NOW(), ?, ?); SELECT MAX(id) FROM SubProducts";
        String[] tab = new String[]{
                special_tag,
                String.valueOf(p.getId()),
                String.valueOf(kg)
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);
        int max = rs.getInt(1);

        return getById(max);
    }
    public static List<SubProduct> getByProduct(Product p) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("SubProducts", "product_id", String.valueOf(p.getId()));

        List<SubProduct> list = new LinkedList<>();

        while(rs.next())
            list.add(new SubProduct(rs));

        return list;
    }

    public int getQuantity() throws Exception {
        if(price_kg)
            throw new Exception("Price is kg");
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
    public float getKg() throws Exception {
        if(!price_kg)
            throw new Exception("Price is quantity");
        return kg;
    }
    public boolean isKg(){
        return (kg != 0);
    }

    public void setQuantity(int quantity) throws Exception {
        if(price_kg)
            throw new Exception("Price is kg");
        this.quantity = quantity;
        DataBase.getInstance().changeValue("SubProducts", "quantity", String.valueOf(quantity), id);
    }
    public void setSpecial_tag(String special_tag) throws Exception {
        if(price_kg)
            throw new Exception("Price is quantity");
        this.special_tag = special_tag;
        DataBase.getInstance().changeValue("SubProducts", "special_tag", special_tag, id);
    }
    public void setKg(float kg) throws Exception {
        this.kg = kg;
        DataBase.getInstance().changeValue("SubProducts", "kg", String.valueOf(kg), id);
    }

    public void buy(int quantity) throws Exception{
        if(price_kg)
            throw new Exception("Cannot buy product with kg price");
        if(quantity < this.quantity)
            throw new Exception("Not enough quantity");
        setQuantity(this.quantity-quantity);
    }
    public void buy(float kg) throws Exception{
        if(price_kg)
            throw new Exception("Cannot buy product with quantity price");
        if(kg < this.kg)
            throw new Exception("Not enough quantity");
        setKg(this.kg - kg);
    }

    public void delete() throws Exception{
        DataBase.getInstance().delete("SubProducts", id);
    }

    public boolean equals(Object o){
        return (o instanceof SubProduct && ((SubProduct)o).id == id);
    }
}
