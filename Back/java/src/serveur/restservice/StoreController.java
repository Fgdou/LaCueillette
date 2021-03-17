package serveur.restservice;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.Common;
import serveur.sql.Address;
import serveur.sql.Store;
import serveur.sql.StoreType;
import serveur.sql.User;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
public class StoreController {

    /**
     * Get informations about a store
     * @param id Store id
     * @return The store associated
     */
    @GetMapping("/store/get/infos")
    public Store getInformations(@RequestParam(value = "id") int id) throws Exception{
        return Store.getById(id);
    }

    /**
     * Get stores by user
     * @param requestParam Parameters requested
     * @return JSONObject : error or list of id+name
     */
    @GetMapping("/store/get/byUser")
    public List<Store> getStoresByUser(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("token");
        User user;
        user = User.getByToken(token);
        return Store.getByUser(user);
    }

    /**
     * Create a store
     * @param requestParam Parameters requested
     * @return JSONObject : error or log
     */
    @PostMapping("/store/new")
    public Store createStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String name = requestParam.get("name");
        String email = requestParam.get("email");
        String tel = requestParam.get("tel");
        int number = Integer.parseInt(requestParam.get("number"));
        String way = requestParam.get("way");
        int cp = Integer.parseInt(requestParam.get("postal_code"));
        String town = requestParam.get("town");
        String userToken = requestParam.get("token");
        int storeType_id = Integer.parseInt(requestParam.get("storeType_id"));
        User user = User.getByToken(userToken);
        Store store = Store.create(name, Address.create(number, way, town, cp, "France", user), user, tel, email, StoreType.getById(storeType_id));
        store.setRef(generateRef(name, cp, "France", store.getId()));
        return store;
    }

    /**
     * Create ref for the Store
     * @param name String the name of the store
     * @param cp int the postal code of the store
     * @param State String the state of the store
     * @param id int the id of the store
     * @return The ref of the store
     */
    private String generateRef(String name, int cp, String State, int id){
        return ("COM" + State.charAt(0) + State.charAt(1) + Common.format(cp, 5) + name.charAt(0) + name.charAt(1) + Common.format(id, 10)).toUpperCase();
    }

    /**
     * Change infos about a store
     * @param requestParam Parameters requested
     * @return JSONObject : error or log
     */
    @PostMapping("/store/change")
    public Response modifyStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("token");
        int id = Integer.parseInt(requestParam.get("id"));
        String name = requestParam.get("name");
        String email = requestParam.get("email");
        String tel = requestParam.get("tel");
        int number = Integer.parseInt(requestParam.get("number"));
        String way = requestParam.get("way");
        int cp = Integer.parseInt(requestParam.get("postal_code"));
        String town = requestParam.get("town");
        User user = User.getByToken(token);
        Store store = Store.getById(id);

        Response r = null;

        if(!store.getSeller().equals(user))
            throw new Exception("You are not the owner of this store");

        //TODO changer pour store.getAddress.set***() ?
        if (!way.equals("") && !town.equals("")){
            store.setAddress(Address.create(number, way, town, cp, "France", user));
            r = new ResponseLog<>("address updated");
        }
        if (!name.equals("")){
            store.setName(name);
            r = new ResponseLog<>("name updated");
        }
        if (!email.equals("")){
            store.setMail(email);
            r = new ResponseLog<>("email updated");
        }
        if (!tel.equals("")){
            store.setTel(tel);
            r = new ResponseLog<>("tel updated");
        }

        if(r == null)
            throw new Exception("nothing updated");

        return r;
    }

    /**
     * Delete a store
     * @param requestParam Parameters requested
     * @return JSONObject : error or log
     */
    @PostMapping("/store/delete")
    public Response deleteStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("token");
        int id = Integer.parseInt(requestParam.get("id"));
        User user = User.getByToken(token);
        Store store = Store.getById(id);

        if(!store.getSeller().equals(user))
            throw new Exception("You are not the owner of this store");

        store.delete();
        return new ResponseLog("store deleted");
    }

}
