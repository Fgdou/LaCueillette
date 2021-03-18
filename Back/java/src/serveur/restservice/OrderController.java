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
     * Get informations for an order
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return Error or the order
     * @throws Exception
     */
    public Order getInfos(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) || !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return order;
    }

    //TODO Set Paid

    /**
     * Get all the orders for a user
     *
     * @param requestParam Parameter requested : user_token
     * @return User's list of orders
     * @throws Exception
     */
    @PostMapping("/order/getAll")
    public List<Order> getByUser(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        return user.getOrders();
    }

    /**
     * Pay an order
     *
     * @param requestParam Parameters requested : user_token, order_id
     * @return Response : error or log
     * @throws Exception
     */
    @PostMapping("/order/pay")
    public Response pay(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if (!store.getSeller().equals(user) || !user.isAdmin()) //Only seller and admin can set an order paid
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        order.pay();

        return new ResponseLog("order paid");
    }

}
