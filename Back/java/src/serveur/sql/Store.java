package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;
import serveur.Log;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represent a store
 */

public class Store {
    private String name;
    private int address_id;
    private int id;
    private String ref;
    private int seller_id;
    private String tel;
    private String mail;
    private DateTime created;
    private int type_id;

    private Store(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        name = rs.getString(2);
        ref = rs.getString(3);
        address_id = rs.getInt(4);
        seller_id = rs.getInt(5);
        tel = rs.getString(6);
        mail = rs.getString(7);
        created = new DateTime(rs.getString(8));
        type_id = rs.getInt(9);
    }

    /**
     * @param ref the ref of the store
     * @return the store found
     */
    public static Store getByRef(String ref) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "ref", ref);
        if(!rs.next())
            throw new Exception("Store not found");
        return new Store(rs);
    }
    /**
     * @param id the id of the store
     * @return the store found
     */
    public static Store getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Store not found");
        return new Store(rs);
    }

    /**
     * @param type a type of store
     * @return all the store with this type
     */
    public static List<Store> getByType(StoreType type) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "type_id", String.valueOf(type.getId()));
        List<Store> list = new LinkedList<>();

        while(rs.next())
            list.add(new Store(rs));
        return list;
    }

    /**
     * @param user a user
     * @return all the store with this user
     */
    public static List<Store> getByUser(User user) throws Exception{
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "boss_id", String.valueOf(user.getId()));

        List<Store> list = new LinkedList<>();

        while(rs.next())
            list.add(new Store(rs));

        return list;
    }

    /**
     * Create a store in database
     * @param name the name fo the store
     * @param ref   the uniq reference of the store
     * @param address the address of the store
     * @param seller the owner of the store
     * @param tel the tel
     * @param mail the mail
     * @param type the type of store
     * @return the new store
     */
    public static Store create(String name, Address address, User seller, String tel, String mail, StoreType type) throws Exception {
        String sql = "INSERT INTO Stores (name, ref, address_id, boss_id, tel, mail, created, type_id) VALUES (?, null, ?, ?, ?, ?, NOW(), ?); SELECT MAX(id) FROM Stores";
        String[] tab = new String[]{
                name, String.valueOf(address.getId()), String.valueOf(seller.getId()), tel, mail, String.valueOf(type.getId())
        };

        ResultSet rs = DataBase.getInstance().query(sql, tab);

        Store store = Store.getById(rs.getInt(1));

        Log.info("Store " + store.ref + " created by " + seller.getMail());

        return store;
    }

    /**
     * Delete the store of the database
     */
    public void delete() throws Exception {
        for(Product p : getProducts())
            p.delete();
        DataBase.getInstance().delete("Stores", id);
        Log.info("Store " + ref + " deleted");
    }

    /**
     * @param ref the uniq reference
     * @return if this store exist
     */
    public static boolean exist(String ref) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "ref", ref);
        return rs.next();
    }

    public String getName() {
        return name;
    }

    public Address getAddress() throws Exception {
        return Address.getById(address_id);
    }

    public int getId() {
        return id;
    }

    public String getRef() {
        return ref;
    }

    public User getSeller() throws Exception {
        return User.getById(seller_id);
    }

    public String getTel() {
        return tel;
    }

    public String getMail() {
        return mail;
    }

    public DateTime getCreated() {
        return created;
    }

    public StoreType getType() throws Exception {
        return StoreType.getById(type_id);
    }

    public TimeTable getTimeTable() throws Exception{
        return TimeTable.getByStore(id);
    }

    public void setName(String name) throws Exception {
        this.name = name;
        DataBase.getInstance().changeValue("Stores", "name", name, id);
    }

    public void setAddress(Address address) throws Exception {
        this.address_id = address.getId();
        DataBase.getInstance().changeValue("Stores", "address_id", String.valueOf(address.getId()), id);
    }

    public void setSeller(User seller) throws Exception {
        this.seller_id = seller.getId();
        DataBase.getInstance().changeValue("Stores", "boss_id", String.valueOf(seller.getId()), id);
    }

    public void setTel(String tel) throws Exception {
        this.tel = tel;
        DataBase.getInstance().changeValue("Stores", "tel", tel, id);
    }

    public void setMail(String mail) throws Exception {
        this.mail = mail;
        DataBase.getInstance().changeValue("Stores", "mail", mail, id);
    }

    public void setRef(String ref) throws Exception{
        if(this.ref != null)
            throw new Exception("Ref is already defined");
        this.ref = ref;
        DataBase.getInstance().changeValue("Stores", "ref", ref, id);
    }

    public void setType(StoreType type) throws Exception {
        this.type_id = type.getId();
        DataBase.getInstance().changeValue("Stores", "type_id", String.valueOf(type.getId()), id);
    }

    public boolean equals(Object o){
        return (o instanceof Store && ((Store)o).ref.equals(ref));
    }

    /**
     * @return all the products of the store
     */
    public List<Product> getProducts() throws Exception {
        return Product.getByStore(this);
    }

    /**
     * @param tags
     * @return all the products founded on the store
     */
    public List<Product> search(List<Tag> tags) throws Exception {
        List<Product> list = new LinkedList<>();

        String sql = "SELECT * FROM Products JOIN TagsProducts TP on Products.id = TP.product_id WHERE store_id = ? AND TP.tag_id = ?";

        for(Tag t : tags){
            String[] tab = new String[]{String.valueOf(t.getId())};

            ResultSet rs = DataBase.getInstance().query(sql, tab);

            while(rs.next()){
                Product p = new Product(rs);

                if(!list.contains(p))
                    list.add(p);
            }
        }

        return list;
    }
    /**
     * @param category
     * @return all the products founded on the store
     */
    public List<Product> search(ProductCategory category) throws Exception {
        String sql = "SELECT * FROM Products JOIN ProductsCategory PC on Products.category_id = PC.id WHERE PC.id = ?";
        String[] tab = new String[]{String.valueOf(category.getId())};

        ResultSet rs = DataBase.getInstance().query(sql, tab);

        List<Product> list = new LinkedList<>();

        while(rs.next()){
            Product p = new Product(rs);

            if(!list.contains(p))
                list.add(p);
        }
        return list;
    }

    /**
     * @return all the orders of this store
     */
    public List<Order> getOrders() throws Exception{
        return Order.getByStore(this);
    }
}
