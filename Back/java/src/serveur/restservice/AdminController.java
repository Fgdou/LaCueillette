package serveur.restservice;

import org.springframework.web.bind.annotation.*;
import serveur.sql.Address;
import serveur.sql.Token;
import serveur.sql.User;

import java.util.Map;

/**
 * Controller for user when the current user is admin
 */
@RestController
public class AdminController {

    /**
     * Create a user account with an admin account
     *
     * @param requestParams Map of Strings : user_admin_token, name, surname, tel, email, will_be_admin(boolean) and password required for user account creation
     * @return Response of error or "user created: " + id
     * @throws Exception The email is already used
     */
    @PostMapping("/user/admin/newUser")
    public Response createNewUser(@RequestParam Map<String, String> requestParams) throws Exception {
        String user_admin_token = requestParams.get("user_admin_token");
        User user_admin = User.getByToken(user_admin_token);
        if (!user_admin.isAdmin())
            throw new Exception("You are not admin");
        String userName = requestParams.get("name");
        String userSurname = requestParams.get("surname");
        String userTel = requestParams.get("tel");
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        boolean willBeAdmin = requestParams.get("will_be_admin").equals("true") ? true : false;
        User newUser = serveur.sql.User.register(userName, userSurname, userTel, userEmail, userPassword, willBeAdmin);
        //TODO Send mail for verification
        return new ResponseLog("user created: " + newUser.getId());
    }

    /**
     * Change one or more user informations with an admin account
     *
     * @param requestParams Map of Strings : user_admin_token, name, surname, tel, adresse, ville, code_postal, turn_it_admin
     * @return Response of error or log
     */
    @PostMapping("/user/admin/changeUser")
    public Response changeUserInfos(@RequestParam Map<String, String> requestParams) throws Exception {
        String user_admin_token = requestParams.get("user_admin_token");
        User user_admin = User.getByToken(user_admin_token);
        if (!user_admin.isAdmin())
            throw new Exception("You are not admin");

        int userId = Integer.parseInt(requestParams.get("user_id"));
        User user = User.getById(userId);

        String userName = requestParams.get("new_name");
        String userSurname = requestParams.get("new_surname");
        String userTel = requestParams.get("new_tel");
        int userNumber = Integer.parseInt(requestParams.get("number"));
        String userWay = requestParams.get("way");
        String userTown = requestParams.get("ville");
        int userCP = Integer.parseInt(requestParams.get("code_postal"));
        boolean turnAdmin = requestParams.get("turn_it_admin").equals("true") ? true : false;

        //Change if and only if not empty
        if (!userName.equals(""))
            user.setName(userName);
        if (!userSurname.equals(""))
            user.setSurname(userSurname);
        if (!userTel.equals(""))
            user.setTel(userTel);
        //TODO update address without creating one ?
        if (!userWay.equals("") || !userTown.equals("")) {
            user.getAddresses().add(Address.create(userNumber, userWay, userTown, userCP, "France", user));
        }
        user.setAdmin(turnAdmin);
        return new ResponseLog(true);
    }

    /**
     * Delete an account with an admin account
     *
     * @param requestParams Map of Strings : user_admin_token, user_id
     * @return Response True if and only if the user has been successfully deleted
     */
    @PostMapping("/user/admin/deleteUser")
    public Response deleterUser(@RequestParam Map<String, String> requestParams) throws Exception {
        String user_admin_token = requestParams.get("user_admin_token");
        User user_admin = User.getByToken(user_admin_token);
        if (!user_admin.isAdmin())
            throw new Exception("You are not admin");

        int userId = Integer.parseInt(requestParams.get("user_id"));
        User user = User.getById(userId);
        user.delete();
        //TODO Send mail for verification
        return new ResponseLog(true);
    }
}
