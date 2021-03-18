package serveur.restservice;

import org.springframework.web.bind.annotation.*;
import serveur.sql.*;

import java.util.*;

@RestController
public class CartController {

    /**
     * Add a product to user's cart
     *
     * @param requestParam Parameters requested : user_token, subproduct_id, quantity (g or unity)
     * @return The cart
     * @throws Exception
     */
    @PostMapping("/cart/add")
    public Cart addProduct(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
        User user = User.getByToken(token);

        int subproduct_id = Integer.parseInt(requestParam.get("subproduct_id"));
        int quantity = Integer.parseInt(requestParam.get("quantity"));

        Cart cart = Cart.getByUser(user);
        cart.addProduct(SubProduct.getById(subproduct_id), quantity);

        return cart;
    }

    /**
     * Modify or delete a product in user's cart
     *
     * @param requestParam Parameters requested : user_token, subproduct_id, quantity (g or unity)
     * @return The cart
     * @throws Exception
     */
    @PostMapping("/cart/modify")
    public Cart modifyProduct(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
        User user = User.getByToken(token);
        Cart cart = Cart.getByUser(user);

        int subproduct_id = Integer.parseInt(requestParam.get("subproduct_id"));
        int quantity = Integer.parseInt(requestParam.get("quantity"));

        if (quantity == 0)
            cart.removeProduct(SubProduct.getById(subproduct_id));
        else
            cart.changeQuantity(SubProduct.getById(subproduct_id), quantity);

        return cart;
    }

    /**
     * Validate a cart : create order for each store
     *
     * @param requestParam Parameters requested : user_token, address_id
     * @return List of the order generated
     * @throws Exception
     */
    public List<Order> validateCart(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Cart cart = Cart.getByUser(user);
        Address address = Address.getById(Integer.parseInt(requestParam.get("address_id")));

        if (!address.getUser().equals(user))
            throw new Exception("The address not belong to the user");

        return cart.buy(address);
    }

}
