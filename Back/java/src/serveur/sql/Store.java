package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

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

    public static Store getByRef(String ref) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "ref", ref);
        if(!rs.next())
            throw new Exception("Store not found");
        return new Store(rs);
    }
    public static Store getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Store not found");
        return new Store(rs);
    }
    public static List<Store> getByType(StoreType type) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Stores", "type_id", String.valueOf(type.getId()));
        List<Store> list = new LinkedList<>();

        while(rs.next())
            list.add(new Store(rs));
        return list;
    }
    public static Store create(String name, String ref, Address address, User seller, String tel, String mail, StoreType typeId) throws Exception {
        if(exist(ref))
            throw new Exception("Store already exist");

        String sql = "INSERT INTO Stores (name, ref, address_id, boss_id, tel, mail, created, type_id) VALUES (?, ?, ?, ?, ?, ?, NOW(), ?)";
        String[] tab = new String[]{
                name, ref, String.valueOf(address.getId()), String.valueOf(seller.getId()), tel, mail, String.valueOf(typeId.getId())
        };
        DataBase.getInstance().query(sql, tab);

        return Store.getByRef(ref);
    }

    public void delete() throws Exception {
        DataBase.getInstance().delete("Stores", id);
    }

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

    public void setType(StoreType type) throws Exception {
        this.type_id = type.getId();
        DataBase.getInstance().changeValue("Stores", "type_id", String.valueOf(type.getId()), id);
    }

    //TODO products
}
