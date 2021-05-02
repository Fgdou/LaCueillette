package serveur.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import serveur.DataBase;

import java.sql.ResultSet;
import java.util.*;

/**
 * This class hold the shopping list of the user
 */

public class Cart {
    //Product_id -> quantity
    private Map<Integer, Integer> products_q;
    private int user_id;

    private Cart(ResultSet rs) throws Exception {
        products_q = new TreeMap<>();

        while(rs.next()){
            int subproduct_id = rs.getInt(3);
            int quantity = rs.getInt(4);


            products_q.put(subproduct_id, quantity);
        }
    }

    /**
     * @param user the user
     * @return  the cart of the user
     */
    public static Cart getByUser(User user) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Cart", "user_id", String.valueOf(user.getId()));
        Cart c = new Cart(rs);

        c.user_id = user.getId();

        return c;
    }

    /**
     * @return the owner of the cart
     */
    @JsonIgnore
    public User getUser() throws Exception{
        return User.getById(user_id);
    }
    public int getUserId(){
        return user_id;
    }

    /**
     * Add a product to the order
     * @param p the subproduct
     * @param quantity the quantity in number
     */
    public void addProduct(SubProduct p, int quantity) throws Exception {

        if(products_q.containsKey(p.getId())) {
            changeQuantity(p, products_q.get(p.getId()) + quantity);
            return;
        }

        String sql = "INSERT INTO Cart (user_id, subproduct_id, quantity, kg) VALUES (?, ?, ?, 0)";
        String[] tab = new String[]{
                String.valueOf(user_id),
                String.valueOf(p.getId()),
                String.valueOf(quantity)
        };
        DataBase.getInstance().query(sql, tab);

        products_q.put(p.getId(), quantity);
    }

    /**
     * Remove a product from the Cart of the user
     * @param p the subproduct to delete
     */
    public void removeProduct(SubProduct p) throws Exception {
        String sql = "DELETE FROM Cart WHERE user_id = ? AND subproduct_id = ?";
        String[] tab = new String[]{
                String.valueOf(user_id),
                String.valueOf(p.getId())
        };

        DataBase.getInstance().query(sql, tab);

        products_q.remove(p.getId());
    }
    /**
     * Change the quantity of the product
     * @param p the product to update
     * @param quantity the new quantity
     */
    public void changeQuantity(SubProduct p, int quantity) throws Exception {
        //TODO check availability
        String sql = "UPDATE Cart SET quantity = ? WHERE user_id = ? AND subproduct_id = ?";
        String[] tab = new String[]{
                String.valueOf(quantity),
                String.valueOf(user_id),
                String.valueOf(p.getId())
        };
        DataBase.getInstance().query(sql, tab);

        products_q.put(p.getId(), quantity);

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
     * @return the price of the TVA
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

    /***
     * Create orders from the list
     * @param address
     * @return
     * @throws Exception
     */
    public List<Order> buy(Address address) throws Exception {

        //TODO check availability
        Map<Integer, Order> orders = new TreeMap<>();

        for(int si : products_q.keySet()){
            SubProduct subProduct = SubProduct.getById(si);
            int quantity = products_q.get(si);
            Store store = subProduct.getProduct().getStore();

            Order order = orders.get(store.getId());

            if(order == null){
                order = Order.create(store, address, null, getUser());
                orders.put(store.getId(), order);
            }

            order.addSubProduct(subProduct, quantity);
        }

        clear();

        return new LinkedList(orders.values());
    }

    public List<Integer> getQuantities() throws Exception {
        return new LinkedList<>(products_q.values());
    }
    public List<SubProduct> getSubProducts() throws Exception{
        List<SubProduct> m = new LinkedList<>();

        for(int k : products_q.keySet())
            m.add(SubProduct.getById(k));

        return m;
    }
    public int getCount(){
        return products_q.size();
    }

    /**
     * Clear the cart of the user
     */
    public void clear() throws Exception {
        String sql = "DELETE FROM Cart WHERE user_id = ?";
        String[] tab = new String[]{String.valueOf(user_id)};

        DataBase.getInstance().query(sql, tab);
    }
}
