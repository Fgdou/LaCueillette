package serveur.restservice;

import org.springframework.web.bind.annotation.*;
import serveur.DateTime;
import serveur.sql.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
public class ProductController {

    /**
     * Create a new product in the DataBase
     *
     * @param requestParam Parameters requested : token, name, price, price_kg, category_id, store_id, canBePicked, canBeDelivered, tva, time_start, time_stop, expiration, description, parent_id
     * @return Response : error or log with the id
     * @throws Exception
     */
    @PostMapping("/product/new")
    public Response addProduct(@RequestParam Map<String, String> requestParam) throws Exception {
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
        ProductCategory productCategory = ProductCategory.getById(Integer.parseInt(requestParam.get("parent_id")));

        if (!store.getSeller().equals(user) || !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        Product product = Product.create(name, (float) price, price_kg, productCategory, store, canBePicked, canBeDelivered, (float) tva, new DateTime(time_start), new DateTime(time_stop), new DateTime(expiration), description);
        return new ResponseLog("product created: " + product.getId());
    }

    /**
     * Modify a product in the DataBase
     *
     * @param requestParam Parameters requested : token, product_id, name, price, price_kg, category_id, store_id, canBePicked, canBeDelivered, tva, time_start, time_stop, expiration, description
     * @return Product modified
     * @throws Exception
     */
    @PostMapping("/product/modify")
    public Product modifyProduct(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("token");
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        int product_id = Integer.parseInt(requestParam.get("product_id"));
        String name = requestParam.get("name");
        double price = Double.parseDouble(requestParam.get("price"));
        double tva = Double.parseDouble(requestParam.get("tva"));
        String description = requestParam.get("description");
        boolean canBeDelivered = requestParam.get("canBeDelivered").toLowerCase().equals("true") ? true : false;
        boolean canBePicked = requestParam.get("canBePicked").toLowerCase().equals("true") ? true : false;
        String time_start = requestParam.get("time_start");
        String time_stop = requestParam.get("time_start");
        String expiration = requestParam.get("expiration");
        User user = User.getByToken(token);
        Store store = Store.getById(store_id);

        if (!store.getSeller().equals(user) || !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        Product product = Product.getById(product_id);

        if (!name.equals(""))
            product.setName(name);
        product.setPrice((float) price);
        product.setTva((float) tva);
        if (!description.equals(""))
            product.setDescription(description);
        product.setCanBeDelivered(canBeDelivered);
        product.setCanBePicked(canBePicked);
        if (!time_start.equals(""))
            product.setTime_start(new DateTime(time_start));
        if (!time_stop.equals(""))
            product.setTime_stop(new DateTime(time_stop));
        if (!expiration.equals(""))
            product.setExpiration(new DateTime(expiration));
        return product;
    }

    /**
     * Delete a product from database
     *
     * @param requestParam Parameters required : token, store_id, product_id
     * @return Response : error or log
     * @throws Exception
     */
    @PostMapping("/product/delete")
    public Response deleteProduct(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("token");
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        int product_id = Integer.parseInt(requestParam.get("product_id"));
        User user = User.getByToken(token);
        Store store = Store.getById(store_id);

        if (!store.getSeller().equals(user) || !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        Product product = Product.getById(product_id);
        product.delete();
        return new ResponseLog("product deleted");
    }

    @PostMapping("/product/searchTag")
    public List<Product> searchByTag(@RequestParam Map<String, String> requestParam) throws Exception{

        String[] tags = requestParam.get("tags").split(" ");
        String city = requestParam.get("city");
        int postalcode = Integer.parseInt(requestParam.get("postalcode"));

        List<Tag> list = new LinkedList<>();

        for(String s : tags){
            try{
                list.add(Tag.getByName(s));
            }catch (Exception e){

            }
        }

        return Product.searchByCity(city, postalcode, list);
    }

    /**
     * Getting every product for a given store
     *
     * @param requestParam Parameters requested : store_id
     * @return List of products
     * @throws Exception
     */
    @PostMapping("/product/get/byStore")
    public List<Product> getProductByStore(@RequestParam Map<String, String> requestParam) throws Exception {
        int store_id = Integer.parseInt(requestParam.get("store_id"));
        Store store = Store.getById(store_id);
        return store.getProducts();
    }

    /**
     * Get infos about a product
     *
     * @param requestParam Parameter requested : product_id
     * @return Error or product
     * @throws Exception
     */
    @PostMapping("/product/get/infos")
    public Product getProductInfos(@RequestParam Map<String, String> requestParam) throws Exception {
        int product_id = Integer.parseInt(requestParam.get("product_id"));
        return Product.getById(product_id);
    }

    /**
     * Get all categories in the database
     *
     * @param requestParam Parameter requested : user_token
     * @return List of all categories
     * @throws Exception
     */
    @PostMapping("/product/category/getAll")
    public List<ProductCategory> getAllCategories(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        return ProductCategory.getAll();
    }

    /**
     * Create a new ProductCategory in the database
     *
     * @param requestParam Parameter requested : user_token, store_id, name, parent_id
     * @return The new category
     * @throws Exception
     */
    @PostMapping("/product/category/new")
    public ProductCategory createCategory(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Store store = Store.getById(Integer.parseInt(requestParam.get("store_id")));
        String name = requestParam.get("name");

        if (!store.getSeller().equals(user) || !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        ProductCategory parent = (!requestParam.get("parent_id").equals("")) ? ProductCategory.getById(Integer.parseInt(requestParam.get("parent_id"))) : null;

        return ProductCategory.create(name, parent);
    }


}
