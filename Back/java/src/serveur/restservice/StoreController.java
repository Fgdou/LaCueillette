package serveur.restservice;

import org.springframework.web.bind.annotation.*;
import serveur.Common;
import serveur.Time;
import serveur.sql.*;

import java.util.List;
import java.util.Map;

@RestController
public class StoreController {

    /**
     * Get informations about a store
     *
     * @param id Store id
     * @return The store associated
     */
    @PostMapping("/store/get/infos")
    public Store getInformations(@RequestParam(value = "id") int id) throws Exception {
        return Store.getById(id);
    }

    /**
     * Get stores by user
     *
     * @param requestParam Parameters requested
     * @return JSONObject : error or list of id+name
     */
    @PostMapping("/store/get/byUser")
    public List<Store> getStoresByUser(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
        User user;
        user = User.getByToken(token);
        return Store.getByUser(user);
    }

    /**
     * Create a store
     *
     * @param requestParam Parameters requested
     * @return Store created
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
        String userToken = requestParam.get("user_token");
        int storeType_id = Integer.parseInt(requestParam.get("storeType_id"));
        User user = User.getByToken(userToken);
        Store store = Store.create(name, Address.create(number, way, town, cp, "France", user), user, tel, email, StoreType.getById(storeType_id));
        store.setRef(generateRef(name, cp, "France", store.getId()));
        return store;
    }

    /**
     * Create ref for the Store
     *
     * @param name  String the name of the store
     * @param cp    int the postal code of the store
     * @param State String the state of the store
     * @param id    int the id of the store
     * @return The ref of the store
     */
    private String generateRef(String name, int cp, String State, int id) {
        return ("COM" + State.charAt(0) + State.charAt(1) + Common.format(cp, 5) + name.charAt(0) + name.charAt(1) + Common.format(id, 10)).toUpperCase();
    }

    /**
     * Change infos about a store
     *
     * @param requestParam Parameters requested
     * @return Reponse : error or log
     */
    @PostMapping("/store/change")
    public Response modifyStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
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

        if (!store.getSeller().equals(user) && !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        if (!way.equals("") && !town.equals("")) {
            store.getAddress().setNumber(number);
            store.getAddress().setWay(way);
            store.getAddress().setPostalcode(cp);
            store.getAddress().setCity(town);
            r = new ResponseLog<>("address updated");
        }
        if (!name.equals("")) {
            store.setName(name);
            r = new ResponseLog<>("name updated");
        }
        if (!email.equals("")) {
            store.setMail(email);
            r = new ResponseLog<>("email updated");
        }
        if (!tel.equals("")) {
            store.setTel(tel);
            r = new ResponseLog<>("tel updated");
        }

        if (r == null)
            throw new Exception("nothing updated");

        return r;
    }

    /**
     * Delete a store
     *
     * @param requestParam Parameters requested
     * @return Response : error or log
     */
    @PostMapping("/store/delete")
    public Response deleteStore(@RequestParam Map<String, String> requestParam) throws Exception {
        String token = requestParam.get("user_token");
        int id = Integer.parseInt(requestParam.get("id"));
        User user = User.getByToken(token);
        Store store = Store.getById(id);

        if (!store.getSeller().equals(user) && !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        store.delete();
        return new ResponseLog("store deleted");
    }

    /**
     * Get stores for a user
     *
     * @param requestParam Parameters required : user_token
     * @return List of store
     * @throws Exception
     */
    @PostMapping("/store/get/byUser")
    public List<Store> getStoreByUser(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        return user.getStores();
    }

    //StoreType

    /**
     * Get all store type
     *
     * @param requestParam Parameters required : user_token
     * @return List of storetype
     * @throws Exception
     */
    @PostMapping("/store/type/getAll")
    public List<StoreType> getStoreType(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        return StoreType.getAll();
    }

    /**
     * Create a new store type
     *
     * @param requestParam Parameters required : user_token, name
     * @return Storetype created
     * @throws Exception
     */
    @PostMapping("/store/type/new")
    public StoreType createNewStoreType(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        String name = requestParam.get("name");

        return StoreType.create(name);
    }

    //TimeTable

    /**
     * Add a TimeTable to a store
     *
     * @param requestParam Required parameters : user_token, store_id, time_from (hh-mm-ss), time_to (hh-mm-ss), day (0 to 6)
     * @return Response : error or log
     * @throws Exception
     */
    @PostMapping("/store/timetable/new")
    public Response addTimeTable(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Store store = Store.getById(Integer.parseInt(requestParam.get("store_id")));

        if (!store.getSeller().equals(user) && !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        Time time_from = new Time(requestParam.get("time_from"));
        Time time_to = new Time(requestParam.get("time_to"));
        int day = Integer.parseInt(requestParam.get("day"));

        //TODO Set in TimeTable

        store.getTimeTable().addInterval(time_from, time_to, day);

        return new ResponseLog("TimeTable added to " + store.getRef());
    }

    /**
     * Modify a Store's timetable
     *
     * @param requestParam Required parameters : user_token, store_id, time_from (hh-mm-ss), time_to (hh-mm-ss), time_id
     * @return Response : error or log
     * @throws Exception
     */
    @PostMapping("/store/timetable/modify")
    public Response modifyTimeTable(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Store store = Store.getById(Integer.parseInt(requestParam.get("store_id")));

        if (!store.getSeller().equals(user) && !user.isAdmin())
            throw new Exception("You are not the owner of this store or you are not admin");

        Time time_from = new Time(requestParam.get("time_from"));
        Time time_to = new Time(requestParam.get("time_to"));
        int day = Integer.parseInt(requestParam.get("day"));

        TimeTableInterval.getById(Integer.parseInt(requestParam.get("time_id")));

        return null;
    }


}
