package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class hold the shopping list of the user
 */

public class Cart {
    //Product_id -> quantity
    Map<Integer, Integer> products_q;
    Map<Integer, Float> products_kg;
    int user_id;

    private Cart(ResultSet rs) throws Exception {
        products_kg = new TreeMap<>();
        products_q = new TreeMap<>();

        while(rs.next()){
            user_id = rs.getInt(2);
            int subproduct_id = rs.getInt(3);
            int quantity = rs.getInt(4);
            float kg = rs.getFloat(5);

            if(kg == 0)
                products_q.put(subproduct_id, quantity);
            else
                products_kg.put(subproduct_id, kg);
        }
    }

    /**
     * @param user the user
     * @return  the cart of the user
     */
    public static Cart getByUser(User user) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Cart", "user_id", String.valueOf(user.getId()));
        return new Cart(rs);
    }

    /**
     * @return the owner of the cart
     */
    public User getUser() throws Exception{
        return User.getById(user_id);
    }

    /**
     * Add a product to the order
     * @param p the subproduct
     * @param quantity the quantity in number
     */
    public void addProduct(SubProduct p, int quantity) throws Exception {
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
     * Add a product to the order
     * @param p the subproduct
     * @param kg the quantity in kg
     */
    public void addProduct(SubProduct p, float kg) throws Exception {
        String sql = "INSERT INTO Cart (user_id, subproduct_id, quantity, kg) VALUES (?, ?, 0, ?)";
        String[] tab = new String[]{
                String.valueOf(user_id),
                String.valueOf(p.getId()),
                String.valueOf(kg)
        };
        DataBase.getInstance().query(sql, tab);

        products_kg.put(p.getId(), kg);
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
        products_kg.remove(p.getId());
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
     * Change the quantity of the product
     * @param p the product to update
     * @param kg the new quantity
     */
    public void changeQuantity(SubProduct p, float kg) throws Exception {
        //TODO check availability
        String sql = "UPDATE Cart SET kg = ? WHERE user_id = ? AND subproduct_id = ?";
        String[] tab = new String[]{
                String.valueOf(kg),
                String.valueOf(user_id),
                String.valueOf(p.getId())
        };
        DataBase.getInstance().query(sql, tab);

        products_kg.put(p.getId(), kg);

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

    /**
     * @return the price of the TVA
     */
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

    /**
     * @return the TTC price
     */
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

    /***
     * Create orders from the list
     * @param address
     * @return
     * @throws Exception
     */
    List<Order> buy(Address address) throws Exception {

        //TODO check availability
        Map<Integer, Order> orders = new TreeMap<>();

        for(int si : products_q.keySet()){
            SubProduct subProduct = SubProduct.getById(si);
            int quantity = products_q.get(si);
            Store store = subProduct.getProduct().getStore();

            Order order = orders.get(store.getId());

            if(order == null){
                order = Order.create("", store, address, null, getUser());
                orders.put(store.getId(), order);
            }

            order.addSubProduct(subProduct, quantity);
        }
        for(int si : products_kg.keySet()){
            SubProduct subProduct = SubProduct.getById(si);
            float kg = products_kg.get(si);
            Store store = subProduct.getProduct().getStore();

            Order order = orders.get(store.getId());

            if(order == null){
                order = Order.create("", store, address, null, getUser());
                orders.put(store.getId(), order);
            }

            order.addSubProduct(subProduct, kg);
        }

        return new LinkedList(orders.values());
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
