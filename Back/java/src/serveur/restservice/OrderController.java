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
     * Get the store who prepares the order
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return Store who prepares the order
     * @throws Exception
     */
    @PostMapping("/order/get/store")
    public Store getStore(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) && !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return store;
    }

    /**
     * Get order
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return The order
     * @throws Exception
     */
    @PostMapping("/order/get/byId")
    public Order getOrder(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) && !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return order;
    }

    /**
     * Get informations for an order
     *
     * @param requestParam Parameters required : user_token, order_id
     * @return Error or the order
     * @throws Exception
     */
    @PostMapping("/order/get/infos")
    public Order getInfos(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("order_id")));
        Store store = order.getStore();

        if ((!store.getSeller().equals(user) && !order.getUser().equals(user)) || !user.isAdmin())
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return order;
    }

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

        if (!store.getSeller().equals(user) && !user.isAdmin()) //Only seller and admin can set an order paid
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        order.pay();

        return new ResponseLog("order paid");
    }

    /**
     * Get orders for a store
     *
     * @param requestParam Parameters required : user_token, store_id
     * @return List of Order
     * @throws Exception
     */
    @PostMapping("/order/get/byStore")
    public List<Order> getByStore(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Store store = Store.getById(Integer.parseInt(requestParam.get("store_id")));

        if (!store.getSeller().equals(user) && !user.isAdmin()) //Only seller and admin can set an order paid
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        return store.getOrders();
    }

    @PostMapping("/order/startPrepare")
    public Response startPrepare(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("store_id")));
        Store store = order.getStore();

        if (!store.getSeller().equals(user) && !user.isAdmin()) //Only seller and admin can set an order paid
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        order.startPrepare();

        return new ResponseLog("OK");
    }
    @PostMapping("/order/FinishPrepare")
    public Response finishPrepare(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Order order = Order.getById(Integer.parseInt(requestParam.get("store_id")));
        Store store = order.getStore();

        if (!store.getSeller().equals(user) && !user.isAdmin()) //Only seller and admin can set an order paid
            throw new Exception("You are not the owner of this store, you are not the buyer or you are not admin");

        order.finishPrepare();

        return new ResponseLog("OK");
    }


}
