package serveur.restservice;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.sql.Address;
import serveur.sql.Store;
import serveur.sql.User;

import java.util.List;
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
    public JSONObject getStoresByUser(@RequestParam Map<String, String> requestParam) throws JSONException {
        String token = requestParam.get("token");
        try{
            User user = User.getByToken(token);
            //TODO getStoresByUser(User user) : User -> List[Store]
            //List<Store> listStore = getStoresByUser(user);
            JSONObject jsonObject = new JSONObject();
            /*for (s : listStore) {
                jsonObject.put(String.valueOf(s.getInt), s.getName);
            }
            */
            return jsonObject;
        } catch (Exception e){
            return new JSONObject().put("error", e.getMessage());
        }
    }

    /**
     * Create a store
     * @param requestParam Parameters requested
     * @return JSONObject : error or log
     * @throws Exception
     */
    @PostMapping("/store/new")
    public JSONObject createStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String name = requestParam.get("name");
        String email = requestParam.get("email");
        String tel = requestParam.get("tel");
        int number = Integer.parseInt(requestParam.get("number"));
        String way = requestParam.get("way");
        int cp = Integer.parseInt(requestParam.get("postal_code"));
        String town = requestParam.get("town");
        String userToken = requestParam.get("token");
        try {
            User user = User.getByToken(userToken);
            Store store = Store.create(name, "", Address.create(number, way, town, cp, "France", user), user, tel, email, null);
            //TODO Add ref and StoreType
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("log", "Store created");
            jsonObject.put("store_id", store.getId());
            return jsonObject;
        } catch (Exception e){
            return new JSONObject().put("error", e.getMessage());
        }
    }

    /**
     * Change infos about a store
     * @param requestParam Parameters requested
     * @return JSONObject : error or log
     * @throws Exception
     */
    @PostMapping("/store/change")
    public JSONObject modifyStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("token");
        int id = Integer.parseInt(requestParam.get("id"));
        String name = requestParam.get("name");
        String email = requestParam.get("email");
        String tel = requestParam.get("tel");
        int number = Integer.parseInt(requestParam.get("number"));
        String way = requestParam.get("way");
        int cp = Integer.parseInt(requestParam.get("postal_code"));
        String town = requestParam.get("town");
        try {
            User user = User.getByToken(token);
            Store store = Store.getById(id);

            if(!store.getSeller().equals(user))
                throw new Exception("You are not the owner of this store");

            //TODO changer pour store.getAddress.set***() ?
            JSONObject jsonObject = new JSONObject();
            if (!way.equals("") && !town.equals("")){
                store.setAddress(Address.create(number, way, town, cp, "France", user));
                jsonObject.put("log", "address updated");
            }
            if (!name.equals("")){
                store.setName(name);
                jsonObject.put("log", "name updated");
            }
            if (!email.equals("")){
                store.setMail(email);
                jsonObject.put("log", "email updated");
            }
            if (!tel.equals("")){
                store.setTel(tel);
                jsonObject.put("log", "tel updated");
            }
            return jsonObject;
        } catch (Exception e){
            return new JSONObject().put("error", e.getMessage());
        }
    }

    /**
     * Delete a store
     * @param requestParam Parameters requested
     * @return JSONObject : error or log
     * @throws JSONException
     */
    @PostMapping("/store/delete")
    public JSONObject deleteStore(@RequestParam Map<String, String> requestParam) throws JSONException {
        String token = requestParam.get("token");
        int id = Integer.parseInt(requestParam.get("id"));
        try{
            User user = User.getByToken(token);
            Store store = Store.getById(id);

            if(!store.getSeller().equals(user))
                throw new Exception("You are not the owner of this store");

            store.delete();
            return new JSONObject().put("log", "store deleted");
        } catch (Exception e){
            return new JSONObject().put("error", e.getMessage());
        }
    }

}
