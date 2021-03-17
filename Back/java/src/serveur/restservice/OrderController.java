package serveur.restservice;

import org.springframework.data.util.Pair;
import org.springframework.web.bind.annotation.*;
import serveur.sql.Order;
import serveur.sql.Product;
import serveur.sql.Store;
import serveur.sql.User;

import java.util.List;
import java.util.Map;

@RestController
public class OrderController {

    /**
     * Get a list of order liked with a store
     *
     * @param requestParam Parameters required : user_token, store_id
     * @return
     * @throws Exception
     */
    @GetMapping("/order/get/byStore")
    public List<Order> getOrderByStore(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Store store = Store.getById(Integer.parseInt(requestParam.get("store_id")));

        if (!store.getSeller().equals(user) && !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        return store.getOrders();
    }

    /**
     * Get a list of products in an order
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return
     * @throws Exception
     */
    @GetMapping("/order/get/products")
    public Map<Integer, Integer> getProductsInOrder(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) || !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        //TODO getProductsInOrder

        return null;
    }

    /**
     * Get the store who prepares the order
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return Store who prepares the order
     * @throws Exception
     */
    @GetMapping("/order/get/store")
    public Store getStore(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) && !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return store;
    }

    /**
     * Get the order's buyer
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return The user who buys
     * @throws Exception
     */
    @GetMapping("/order/get/buyer")
    public User getBuyer(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) || !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return order.getUser();
    }

    /**
     * Get PriceHT and TVA of an order
     * @param requestParam Parameters required : user_token, order_id
     * @return Pair of the priceHT and priceTTC
     * @throws Exception
     */
    public Pair<Float, Float> getPriceAndTVA(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) || !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return Pair.of(order.getPriceHT(), order.getPriceTTC());
    }

    //TODO Set Paid



}
