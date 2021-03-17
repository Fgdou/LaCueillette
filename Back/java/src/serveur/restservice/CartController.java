package serveur.restservice;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.sql.Cart;
import serveur.sql.Store;
import serveur.sql.User;

import java.util.*;

@RestController
public class CartController {

    /**
     * Add a product to user's cart
     *
     * @param requestParam Parameters requested : user_token, product_id, quantity (g or unity)
     * @return The cart
     * @throws Exception
     */
    @PostMapping("/cart/add")
    public Cart addProduct(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
        User user = User.getByToken(token);

        int product_id = Integer.parseInt(requestParam.get("product_id"));
        int quantity = Integer.parseInt(requestParam.get("quantity"));

        //TODO setCart(product, quantity)
        return null;
    }

    /**
     * Modify or delete a product in user's cart
     *
     * @param requestParam Parameters requested : user_token, product_id, quantity (g or unity)
     * @return The cart
     * @throws Exception
     */
    @PostMapping("/cart/modify")
    public Cart modifyProduct(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
        User user = User.getByToken(token);

        int product_id = Integer.parseInt(requestParam.get("product_id"));
        int quantity = Integer.parseInt(requestParam.get("quantity"));

        //if (quantity == 0)
        //On supprime l'article
        //else
        //On modifie juste la quantité

        //TODO setCart -> même chose
        return null;
    }

    /**
     * Validate a cart : create order for each store
     *
     * @param requestParam Parameters requested : user_token
     * @return List of the order_id generated
     * @throws Exception
     */
    public List<Integer> validateCart(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));

        Cart cart = Cart.getByUser(user);

        //Pour chaque produit, assigner à un store puis créer les commandes en fonction des assignations

        return new ArrayList<>();
    }

}
