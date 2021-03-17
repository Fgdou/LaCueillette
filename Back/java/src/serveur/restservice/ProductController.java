package serveur.restservice;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.DateTime;
import serveur.sql.Product;
import serveur.sql.Store;
import serveur.sql.User;

import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    //TODO Gestions des admins

    /**
     * Create a new product in the DataBase
     * @param requestParam Parameters requested : token, name, price, price_kg, category_id, store_id, canBePicked, canBeDelivered, tva, time_start, time_stop, expiration, description
     * @return JSONObject : error or log with the id
     * @throws Exception
     */
    @PutMapping("/product/new")
    public Response addProduct(@RequestParam Map<String, String> requestParam) throws Exception{
        String token = requestParam.get("token");
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        String name = requestParam.get("name");
        int quantity = Integer.parseInt(requestParam.get("quantity"));
        double price = Double.parseDouble(requestParam.get("price"));
        double tva = Double.parseDouble(requestParam.get("tva"));
        String description = requestParam.get("description");
        boolean price_kg = requestParam.get("price_kg").toLowerCase().equals("true") ? true : false;
        boolean canBeDelivered = requestParam.get("canBeDelivered").toLowerCase().equals("true") ? true : false;
        boolean canBePicked = requestParam.get("canBePicked").toLowerCase().equals("true") ? true : false;
        String time_start = requestParam.get("time_start");
        String time_stop = requestParam.get("time_start");
        String expiration = requestParam.get("expiration");

        User user = User.getByToken(token);
        Store store = Store.getById(store_id);

        if(!store.getSeller().equals(user))
            throw new Exception("You are not the owner of this store");
        //TODO addProductCategory
        Product product = Product.create(name, (float)price, price_kg, null, store, canBePicked, canBeDelivered, (float)tva, new DateTime(time_start), new DateTime(time_stop), new DateTime(expiration), description);
        return new ResponseLog("product created: " + product.getId());

    }

    /**
     * Modify a product in the DataBase
     * @param requestParam Parameters requested : token, product_id, name, price, price_kg, category_id, store_id, canBePicked, canBeDelivered, tva, time_start, time_stop, expiration, description
     * @return JSONObject : error or log with the id
     * @throws Exception
     */
    @PostMapping("/product/modify")
    public Response modifyProduct(@RequestParam Map<String, String> requestParam) throws Exception{
        String token = requestParam.get("token");
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        int product_id = Integer.parseInt(requestParam.get("product_id"));
        String name = requestParam.get("name");
        double price = Double.parseDouble(requestParam.get("price"));
        double tva = Double.parseDouble(requestParam.get("tva"));
        String description = requestParam.get("description");
        boolean price_kg = requestParam.get("price_kg").toLowerCase().equals("true") ? true : false;
        boolean canBeDelivered = requestParam.get("canBeDelivered").toLowerCase().equals("true") ? true : false;
        boolean canBePicked = requestParam.get("canBePicked").toLowerCase().equals("true") ? true : false;
        //TODO Date1, Date2, Expiration

        User user = User.getByToken(token);
        Store store = Store.getById(store_id);

        if(!store.getSeller().equals(user))
            throw new Exception("You are not the owner of this store");

        Product product = Product.getById(product_id);

        if(!name.equals(""))
            product.setName(name);
        product.setPrice((float) price);
        product.setTva((float) tva);
        if(!description.equals(""))
            product.setDescription(description);
        product.setPrice_kg(price_kg);
        product.setCanBeDelivered(canBeDelivered);
        product.setCanBePicked(canBePicked);
        return new ResponseLog<>("product modified: " + product.getId());
    }

    /**
     * Delete a product from database
     * @param requestParam Parameters required : token, store_id, product_id
     * @return JSONObject : error or log
     * @throws Exception
     */
    @PostMapping("/product/delete")
    public Response deleteProduct(@RequestParam Map<String, String> requestParam) throws Exception{
        String token = requestParam.get("token");
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        int product_id = Integer.parseInt(requestParam.get("product_id"));

        User user = User.getByToken(token);
        Store store = Store.getById(store_id);

        if(!store.getSeller().equals(user))
            throw new Exception("You are not the owner of this store");

        Product product = Product.getById(product_id);
        product.delete();
        return new ResponseLog("product deleted");
    }

    /**
     * Getting the quantity available of a ptoduct
     * @param requestParam Parameters required : token, id
     * @return JSONObject : error or log + the quantity available of a ptoduct
     * @throws Exception
     */
    @GetMapping("/product/get/quantityAvailable")
    public Response getQuantityAvailable(@RequestParam Map<String, String> requestParam) throws Exception{
        String token = requestParam.get("token");
        int id = Integer.parseInt(requestParam.get("id"));

        User user = User.getByToken(token);
        Product product = Product.getById(id);
        return new ResponseLog<>("quantity: " + 0);
        //TODO Product.getQuantity()
    }

    /**
     * Getting every product for a given store
     * @param requestParam Parameters requested : store_id
     * @return JSONObject : error or log + list
     * @throws Exception
     */
    @GetMapping("/product/get/byStore")
    public List<Product> getProductByStore(@RequestParam Map<String, String> requestParam) throws Exception{
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        Store store = Store.getById(store_id);
        return store.getProducts();
    }

    /**
     * Get infos about a product
     * @param requestParam Parameter requested : product_id
     * @return JSONObject : error or log + product
     * @throws Exception
     */
    @GetMapping("/product/get/infos")
    public Product getProductInfos(@RequestParam Map<String, String> requestParam) throws Exception{
        int product_id = Integer.parseInt(requestParam.get("product_id"));
        return Product.getById(product_id);
    }


}
