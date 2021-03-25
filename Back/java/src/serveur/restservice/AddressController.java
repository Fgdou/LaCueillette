package serveur.restservice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import serveur.sql.Address;
import serveur.sql.User;

import java.util.List;
import java.util.Map;

//TODO controller Address
@RestController
public class AddressController {


    /**
     * Get an Address by it id
     *
     * @param requestParam user_token, address_id
     * @return The address associated with the id
     * @throws Exception Error
     */
    @PostMapping("/address/get/byId")
    public Address getById(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Address address = Address.getById(Integer.parseInt(requestParam.get("address_id")));
        if (!address.getUser().equals(user) || !user.isAdmin())
            throw new Exception("Your profile is not related with the address or you are not admin.");
        return address;
    }

    /**
     * Get a list of user's addresses
     *
     * @param requestParam user_token
     * @return List of addresses
     * @throws Exception Error
     */
    @PostMapping("/address/get/byUser")
    public List<Address> getByUser(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        return user.getAddresses();
    }

    /**
     * Create a new address
     *
     * @param requestParam user_token, number, way, postal_code, town
     * @return The Address created
     * @throws Exception Error
     */
    @PostMapping("/address/new")
    public Address createAddress(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        int number = Integer.parseInt(requestParam.get("number"));
        String way = requestParam.get("way");
        int cp = Integer.parseInt(requestParam.get("postal_code"));
        String city = requestParam.get("town");
        return Address.create(number, way, city, cp, "France", user);
    }

    /**
     * Modify a new address
     *
     * @param requestParam user_token, address_id, number, way, postal_code, town
     * @return Log : error or id
     * @throws Exception Error
     */
    @PostMapping("/address/modify")
    public Response modifyAddress(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Address address = Address.getById(Integer.parseInt(requestParam.get("address_id")));

        if (!address.getUser().equals(user) || !user.isAdmin())
            return new ResponseError<>("Your profile is not related with the address or you are not admin.");

        int number = Integer.parseInt(requestParam.get("number"));
        String way = requestParam.get("way");
        int cp = Integer.parseInt(requestParam.get("postal_code"));
        String city = requestParam.get("town");

        if (!way.equals("") && !city.equals("")) {
            address.setNumber(number);
            address.setWay(way);
            address.setPostalcode(cp);
            address.setCity(city);
        }

        return new ResponseLog<>("Address modified: " + address.getId());
    }


    /**
     * Delete an Address
     *
     * @param requestParam user_token, address_id
     * @return Log : error or id
     * @throws Exception Error
     */
    @PostMapping("/address/delete")
    public Response deleteAddress(@RequestParam Map<String, String> requestParam) throws Exception {
        User user = User.getByToken(requestParam.get("user_token"));
        Address address = Address.getById(Integer.parseInt(requestParam.get("address_id")));

        if (!address.getUser().equals(user) || !user.isAdmin())
            return new ResponseError<>("Your profile is not related with the address or you are not admin.");

        address.delete();

        return new ResponseLog<>("Address deleted successfully: " + address.getId());
    }


}
