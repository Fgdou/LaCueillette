package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;
import serveur.Log;
import serveur.Time;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Product {
    private int id;
    private String name;
    private float price;
    private boolean price_kg;
    private int category_id;
    private int store_id;
    private boolean canBePicked;
    private boolean canBeDelivered;
    private float tva;
    private DateTime time_start;
    private DateTime time_stop;
    private Time expiration;
    private String description;

    protected Product(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        name = rs.getString(2);
        price = rs.getFloat(3);
        price_kg = rs.getBoolean(4);
        category_id = rs.getInt(5);
        store_id = rs.getInt(6);
        canBePicked = rs.getBoolean(7);
        canBeDelivered = rs.getBoolean(8);
        tva = rs.getFloat(9);
        time_start = new DateTime(rs.getString(10));
        time_stop = new DateTime(rs.getString(11));
        expiration = new Time(rs.getString(12));
        description = rs.getString(13);
    }

    public static Product create(String name, float price, boolean price_kg, ProductCategory category,
                                 Store store, boolean canBePicked, boolean canBeDelivered, float tva,
                                 DateTime start, DateTime stop, Time expiration, String description) throws Exception {
        String sql = "INSERT INTO Products (name, price, price_kg, category_id, store_id, canBePicked, canBeDelivered, tva, time_start, time_stop, expiration, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] tab = new String[]{
                name,
                String.valueOf(price),
                (price_kg) ? "1" : "0",
                String.valueOf(category.getId()),
                String.valueOf(store.getId()),
                (canBePicked) ? "1" : "0",
                (canBeDelivered) ? "1" : "0",
                String.valueOf(tva),
                start.toString(),
                stop.toString(),
                expiration.toString(),
                description
        };
        DataBase.getInstance().query(sql, tab);

        Product p = getByStoreAndName(store, name);

        Log.warn("Product(" + p.id + ") " + p.name + " created on " + store.getRef());

        return p;
    }

    public static Product getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Products", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Product not found");
        return new Product(rs);
    }
    public static List<Product> getByStore(Store store) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Products", "store_id", String.valueOf(store.getId()));
        List<Product> list = new LinkedList<>();

        while(rs.next()){
            list.add(new Product(rs));
        }

        return list;
    }
    public static Product getByStoreAndName(Store store, String name) throws Exception {
        String sql = "SELECT * FROM Products WHERE store_id = ? AND name = ?";
        String[] tab = new String[]{
                String.valueOf(store.getId()),
                name
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);
        if(!rs.next())
            throw new Exception("Product not found");
        return new Product(rs);
    }
    public static boolean exist(Store store, String name) throws Exception {
        String sql = "SELECT * FROM Products WHERE store_id = ? AND name = ?";
        String[] tab = new String[]{
                String.valueOf(store.getId()),
                name
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);
        return rs.next();
    }

    public void delete() throws Exception {
        DataBase.getInstance().delete("Products", id);
        Log.info("Product " + name + " on " + getStore().getId() + " deleted");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public boolean isPrice_kg() {
        return price_kg;
    }

    public ProductCategory getCategory() throws Exception {
        return ProductCategory.getById(category_id);
    }

    public Store getStore() throws Exception {
        return Store.getById(store_id);
    }

    public boolean canBePicked() {
        return canBePicked;
    }

    public boolean canBeDelivered() {
        return canBeDelivered;
    }

    public float getTva() {
        return tva;
    }

    public DateTime getTime_start() {
        return time_start;
    }

    public DateTime getTime_stop() {
        return time_stop;
    }

    public Time getExpiration() {
        return expiration;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) throws Exception {
        this.name = name;
        DataBase.getInstance().changeValue("Products", "name", name, id);
    }

    public void setPrice(float price) throws Exception {
        this.price = price;
        DataBase.getInstance().changeValue("Products", "price", String.valueOf(price), id);
    }

    public void setPrice_kg(boolean price_kg) throws Exception {
        this.price_kg = price_kg;
        DataBase.getInstance().changeValue("Products", "price_kg", (price_kg) ? "1" : "0", id);
    }

    public void setCategory(ProductCategory category) throws Exception {
        this.category_id = category.getId();
        DataBase.getInstance().changeValue("Products", "category_id", String.valueOf(category.getId()), id);
    }

    public void setCanBePicked(boolean canBePicked) throws Exception {
        this.canBePicked = canBePicked;
        DataBase.getInstance().changeValue("Products", "canBePicked", (canBePicked) ? "1" : "0", id);
    }

    public void setCanBeDelivered(boolean canBeDelivered) throws Exception {
        this.canBeDelivered = canBeDelivered;
        DataBase.getInstance().changeValue("Products", "canBeDelivered", (canBeDelivered) ? "1" : "0", id);
    }

    public void setTva(float tva) throws Exception {
        this.tva = tva;
        DataBase.getInstance().changeValue("Products", "tva", String.valueOf(tva), id);
    }

    public void setTime_start(DateTime time_start) throws Exception {
        this.time_start = time_start;
        DataBase.getInstance().changeValue("Products", "time_start", time_start.toString(), id);
    }

    public void setTime_stop(DateTime time_stop) throws Exception {
        this.time_stop = time_stop;
        DataBase.getInstance().changeValue("Products", "time_stop", time_stop.toString(), id);
    }

    public void setExpiration(Time expiration) throws Exception {
        this.expiration = expiration;
        DataBase.getInstance().changeValue("Products", "expiration", expiration.toString(), id);
    }

    public void setDescription(String description) throws Exception {
        this.description = description;
        DataBase.getInstance().changeValue("Products", "description", description, id);
    }

    public List<Product> searchByCity(String city, int postalcode, ProductCategory category) throws Exception {
        String sql = "SELECT * FROM Products P JOIN Stores S on P.store_id = S.id JOIN Addresses A on S.address_id = A.id WHERE A.city = ? AND A.state = ? AND P.category_id = ?";
        String[] tab = new String[]{
                city,
                String.valueOf(postalcode),
                String.valueOf(category.getId())
        };

        ResultSet rs = DataBase.getInstance().query(sql, tab);

        List<Product> list = new LinkedList<>();

        while(rs.next())
            list.add(new Product(rs));

        return list;
    }
    public List<Product> searchByCity(String city, int postalcode, List<Tag> tags) throws Exception{
        List<Product> list = new LinkedList<>();

        String sql = "SELECT * FROM Products P JOIN TagsProducts TP on P.id = TP.product_id JOIN Stores S on P.store_id = S.id JOIN Addresses A on S.address_id = A.id WHERE A.city = ? AND A.postalcode = ? AND TP.tag_id = ?";

        String[] tab = new String[]{
                city,
                String.valueOf(postalcode),
                ""
        };

        for(Tag t : tags){
            tab[2] = String.valueOf(t.getId());

            ResultSet rs = DataBase.getInstance().query(sql, tab);

            while(rs.next()){
                Product p = new Product(rs);

                if(!list.contains(p))
                    list.add(p);
            }
        }

        return list;
    }

    public List<SubProduct> getSubProducts() throws Exception {
        return SubProduct.getByProduct(this);
    }

    public boolean equals(Object o){
        return (o instanceof Product && ((Product)o).getId() == id);
    }
}
