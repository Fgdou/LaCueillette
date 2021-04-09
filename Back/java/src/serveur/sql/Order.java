package serveur.sql;

import serveur.DataBase;
import serveur.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class hold the information of an order
 */

public class Order {

    public static int ORDER_PENDING = 0;
    public static int ORDER_PREPARING = 1;
    public static int ORDER_FINISHED = 2;
    public static int ORDER_CANCELED = -1;

    Map<Integer, Integer> products_q;

    private int id;
    private int store_id;
    private boolean paid;
    private DateTime created;
    private int address_id;
    private int state;
    private DateTime appointment;
    private int user_id;

    private Order(ResultSet rs) throws Exception {
        products_q = new TreeMap<>();

        id = rs.getInt(1);
        store_id = rs.getInt(2);
        paid = rs.getBoolean(3);
        created = new DateTime(rs.getString(4));
        address_id = rs.getInt(5);
        state = rs.getInt(6);
        appointment = new DateTime(rs.getString(7));
        user_id = rs.getInt(8);

        String sql = "SELECT * FROM OrdersProducts WHERE order_id = ?";
        String[] tab = new String[]{String.valueOf(id)};

        ResultSet rss = DataBase.getInstance().query(sql, tab);
        while(rss.next()){
            int subproduct_id = rss.getInt(2);
            int quantity = rss.getInt(5);
            float kg = rss.getFloat(6);

            products_q.put(subproduct_id, quantity);
        }
    }

    /**
     * @param id id of the order
     * @return the order
     */
    public static Order getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Cannot found Order");
        return new Order(rs);
    }

    /**
     * @param user the user
     * @return all the orders link to this user
     */
    public static List<Order> getByUser(User user) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "user_id", String.valueOf(user.getId()));

        List<Order> list = new LinkedList<>();

        while(rs.next())
            list.add(new Order(rs));

        return list;
    }

    /**
     * @param store a store
     * @return all the orders for the store
     */
    public static List<Order> getByStore(Store store) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Orders", "store_id", String.valueOf(store.getId()));

        List<Order> list = new LinkedList<>();

        while(rs.next())
            list.add(new Order(rs));

        return list;
    }

    /**
     * Create an order in database (only used by the cart)
     * @param store
     * @param address
     * @param appointment
     * @param user
     * @return the new Order
     */
    protected static Order create(Store store, Address address, DateTime appointment, User user) throws Exception {

        String sql = "INSERT INTO Orders (store_id, paid, created, address_id, state, appointement, user_id) VALUES (?, 0, NOW(), ?, 0, ?, ?); SELECT MAX(id) FROM Orders";
        String[] tab = {
                String.valueOf(store.getId()),
                String.valueOf(address.getId()),
                (appointment == null) ? "null" : appointment.toString(),
                String.valueOf(user.getId())
        };

        ResultSet rs = DataBase.getInstance().query(sql, tab);

        return getById(rs.getInt(1));
    }

    /**
     * Add product to the order
     * @param sp SubProduct
     * @param quantity quantity
     */
    protected void addSubProduct(SubProduct sp, int quantity) throws Exception {
        products_q.put(sp.getId(), quantity);

        String sql = "INSERT INTO OrdersProducts (subproduct_id, order_id, quantity, kg) VALUES (?, ?, ?, 0)";
        String[] tab = new String[]{
                String.valueOf(sp.getId()),
                String.valueOf(id),
                String.valueOf(quantity)
        };
        DataBase.getInstance().query(sql, tab);
    }

    /**
     * update the state of the order
     */
    public void startPrepare() throws Exception {
        setState(ORDER_PREPARING);
    }

    /**
     * update the state of the order
     */
    public void finishPrepare() throws Exception {
        setState(ORDER_FINISHED);
    }
    /**
     * update the state of the order
     * Will get the stock back
     */
    public void cancel() throws Exception {
        setState(ORDER_CANCELED);

        if(paid){
            for(int i : products_q.keySet()){
                SubProduct s = SubProduct.getById(i);
                int q = products_q.get(i);

                s.setQuantity(s.getQuantity()+q);
            }
        }
    }

    /**
     * Will lower the sock of the products
     */
    public void pay() throws Exception {
        setPaid(true);

        //TODO check availability
        for(int i : products_q.keySet()){
            SubProduct s = SubProduct.getById(i);
            int q = products_q.get(i);

            s.setQuantity(s.getQuantity()-q);
        }
    }

    /**
     * @return the HT price
     */
    public float getPriceHT() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            if(product.getPriceKg())
                sum += q*product.getPrice()/1000;
            else
                sum += q*product.getPrice();
        }

        return sum;
    }
    /**
     * @return the TVA price
     */
    public float getTVA() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            if(product.getPriceKg())
                sum += q*product.getTva()*product.getPrice()/1000;
            else
                sum += q*product.getTva()*product.getPrice();
        }

        return sum;
    }
    /**
     * @return the TTC price
     */
    public float getPriceTTC() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            if(product.getPriceKg())
                sum += q*product.getPrice()/1000 * (1 + product.getTva());
            else
                sum += q*product.getPrice() * (1 + product.getTva());
        }

        return sum;
    }

    public int getId() {
        return id;
    }
    public Store getStore() throws Exception {
        return Store.getById(store_id);
    }
    public int getStoreId(){
        return store_id;
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

    /**
     * Remove the order
     */
    public void delete() throws Exception {
        String sql = "DELETE FROM OrdersProducts WHERE order_id = ?";
        String[] tab = new String[]{String.valueOf(id)};
        DataBase.getInstance().query(sql, tab);
        DataBase.getInstance().delete("Orders", id);
    }

    public boolean equals(Object o){
        return (o instanceof Order && ((Order)o).id == id);
    }
}
