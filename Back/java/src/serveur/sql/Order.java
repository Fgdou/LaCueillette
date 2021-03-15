package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Order {

    public static int ORDER_PENDING = 0;
    public static int ORDER_PREPARING = 1;
    public static int ORDER_FINISHED = 2;
    public static int ORDER_CANCELED = -1;

    Map<Integer, Integer> products_q;
    Map<Integer, Float> products_kg;

    private int id;
    private String ref;
    private int store_id;
    private boolean paid;
    private DateTime created;
    private int address_id;
    private int state;
    private DateTime appointment;
    private int user_id;

    private Order(ResultSet rs) throws Exception {
        products_kg = new TreeMap<>();
        products_q = new TreeMap<>();

        id = rs.getInt(1);
        ref = rs.getString(2);
        store_id = rs.getInt(3);
        paid = rs.getBoolean(4);
        created = new DateTime(rs.getString(5));
        address_id = rs.getInt(6);
        state = rs.getInt(7);
        appointment = new DateTime(rs.getString(8));
        user_id = rs.getInt(9);

        String sql = "SELECT * FROM OrdersProducts WHERE order_id = ?";
        String[] tab = new String[]{String.valueOf(id)};

        ResultSet rss = DataBase.getInstance().query(sql, tab);
        while(rss.next()){
            int subproduct_id = rss.getInt(2);
            int quantity = rss.getInt(5);
            float kg = rss.getFloat(6);

            if(kg == 0)
                products_q.put(subproduct_id, quantity);
            else
                products_kg.put(subproduct_id, kg);
        }
    }

    public static Order getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Cannot found Order");
        return new Order(rs);
    }
    public static Order getByRef(String ref) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "ref", ref);
        if(!rs.next())
            throw new Exception("Cannot found Order");
        return new Order(rs);
    }
    public static List<Order> getByUser(User user) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "user_id", String.valueOf(user.getId()));

        List<Order> list = new LinkedList<>();

        while(rs.next())
            list.add(new Order(rs));

        return list;
    }
    public static List<Order> getByStore(Store store) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "store_id", String.valueOf(store.getId()));

        List<Order> list = new LinkedList<>();

        while(rs.next())
            list.add(new Order(rs));

        return list;
    }

    public static boolean exists(String ref) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "ref", ref);
        return rs.next();
    }

    public static Order create(String ref, Store store, Address address, DateTime appointment, User user) throws Exception {
        if(exists(ref))
            throw new Exception("Order already exist");

        String sql = "INSERT INTO Orders (ref, store_id, paid, created, address_id, state, appointement, user_id) VALUES (?, ?, 0, NOW(), ?, 0, ?, ?)";
        String[] tab = {
                ref,
                String.valueOf(store.getId()),
                String.valueOf(address.getId()),
                appointment.toString(),
                String.valueOf(user.getId())
        };

        DataBase.getInstance().query(sql, tab);

        return getByRef(ref);
    }

    public void startPrepare() throws Exception {
        setState(ORDER_PREPARING);
    }
    public void finishPrepare() throws Exception {
        setState(ORDER_FINISHED);
    }
    public void cancel() throws Exception {
        setState(ORDER_CANCELED);

        if(paid){
            for(int i : products_q.keySet()){
                SubProduct s = SubProduct.getById(i);
                int q = products_q.get(i);

                s.setQuantity(s.getQuantity()+q);
            }
            for(int i : products_kg.keySet()){
                SubProduct s = SubProduct.getById(i);
                float q = products_kg.get(i);

                s.setKg(s.getQuantity()+q);
            }
        }
    }

    public void pay() throws Exception {
        setPaid(true);

        for(int i : products_q.keySet()){
            SubProduct s = SubProduct.getById(i);
            int q = products_q.get(i);

            s.setQuantity(s.getQuantity()-q);
        }
        for(int i : products_kg.keySet()){
            SubProduct s = SubProduct.getById(i);
            float q = products_kg.get(i);

            s.setKg(s.getQuantity()-q);
        }
    }

    public float getPriceHT() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice();
        }
        for(Integer sub : products_kg.keySet()){
            float q = products_kg.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice();
        }

        return sum;
    }
    public float getTVA() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getTva()*product.getPrice();
        }
        for(Integer sub : products_kg.keySet()){
            float q = products_kg.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getTva()*product.getPrice();
        }

        return sum;
    }
    public float getPriceTTC() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice() * (1 + product.getTva());
        }
        for(Integer sub : products_kg.keySet()){
            float q = products_kg.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice() * (1 + product.getTva());
        }

        return sum;
    }

    public int getId() {
        return id;
    }
    public String getRef() {
        return ref;
    }
    public Store getStore() throws Exception {
        return Store.getById(store_id);
    }
    public boolean isPaid() {
        return paid;
    }
    public DateTime getCreated() {
        return created;
    }
    public Address getAddress() throws Exception {
        return Address.getById(address_id);
    }
    public int getState() {
        return state;
    }
    public DateTime getAppointment() {
        return appointment;
    }
    public User getUser() throws Exception {
        return User.getById(user_id);
    }

    private void setPaid(boolean paid) throws Exception {
        this.paid = paid;
        DataBase.getInstance().changeValue("Orders", "paid", (paid) ? "1" : "0", id);
    }
    public void setAddress(Address address) throws Exception {
        this.address_id = address.getId();
        DataBase.getInstance().changeValue("Orders", "paid", String.valueOf(address_id), id);
    }
    private void setState(int state) throws Exception {
        this.state = state;
        DataBase.getInstance().changeValue("Orders", "state", String.valueOf(state), id);
    }

    public void delete() throws Exception {
        String sql = "DELETE FROM OrdersProducts WHERE order_id = ?";
        String[] tab = new String[]{String.valueOf(id)};
        DataBase.getInstance().query(sql, tab);
        DataBase.getInstance().delete("Orders", id);
    }
}
