package serveur.restservice;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.sql.Address;
import serveur.sql.Token;
import serveur.sql.User;

import java.util.Map;

@RestController
public class UserController {

    @PostMapping("/user")
    public User userGetByEmail(@RequestParam(value = "token") String token) throws Exception {
        return serveur.sql.User.getByToken(token);
    }

    /**
     * Create a user account
     * @param requestParams Map of Strings : name, surname, tel, email et password required for user account creation
     * @return JSONObject of error or "user created"
     * @throws Exception The email is already used
     */
    @PostMapping("/user/new")
    public Response createNewUser(@RequestParam Map<String,String> requestParams) throws Exception{
        String userName = requestParams.get("name");
        String userSurname = requestParams.get("surname");
        String userTel = requestParams.get("tel");
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        User newUser = serveur.sql.User.register(userName, userSurname, userTel, userEmail, userPassword, false);
        return new ResponseLog(true);

    }

    /**
     * Change the user's password
     * @param requestParams Map of Strings : email, old_password, new_password
     * @return  JSONObject with error or log
     * @throws Exception The email is not present in the database
     */
    @PostMapping("/user/change/password")
    public Response changePassword(@RequestParam Map<String,String> requestParams) throws Exception {
        String token = requestParams.get("token");
        String userPasswordNew = requestParams.get("password");
        User user = User.getByToken(token);
        user.changePassword(userPasswordNew);
        return new ResponseLog(true);

    }

    /**
     * Change one or more user informations
     * @param requestParams Map of Strings : name, surname, tel, adresse, ville, code_postal
     * @return JSONObject of error or log
     */
    @PostMapping("/user/change")
    public Response changeUserInfos(@RequestParam Map<String,String> requestParams) throws Exception{
        String userName = requestParams.get("new_name");
        String userSurname = requestParams.get("new_surname");
        String userTel = requestParams.get("new_tel");
        int userNumber = Integer.parseInt(requestParams.get("number"));
        String userWay = requestParams.get("way");
        String userTown = requestParams.get("ville");
        int userCP = Integer.parseInt(requestParams.get("code_postal"));
        String userToken = requestParams.get("token");

        //TODO allow other user if this token is admin
        User user = User.getByToken(userToken);
        //Change if and only if not empty
        if (!userName.equals(""))
            user.setName(userName);
        if (!userSurname.equals(""))
            user.setSurname(userSurname);
        if (!userTel.equals(""))
            user.setTel(userTel);
        //TODO update address without creating one ?
        if (!userWay.equals("") || !userTown.equals("")){
            user.getAddresses().add(Address.create(userNumber, userWay, userTown, userCP, "France", user));
        }
        return new ResponseLog(true);

    }

    /**
     * Delete an account
     * @param requestParams Map of Strings : email, password
     * @return True if and only if the user has been successfully deleted
     */
    @PostMapping("/user/delete")
    public Response deleterUser(@RequestParam Map<String,String> requestParams) throws Exception{
        String token = requestParams.get("token");
        //TODO allow other user if admin
        User user = User.getByToken(token);
        user.delete();
        return new ResponseLog(true);
    }

    /**
     * Login
     * @param requestParams Paramètre requis
     * @return JSONObject du token créé
     */
    @PostMapping("/user/login")
    public Token login(@RequestParam Map<String,String> requestParams)throws Exception{
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        return User.login(userEmail, userPassword, userEmail);
    }

    /**
     * Logout
     * @param requestParams Paramètre requis
     * @return JSONObject du token créé
     */
    @PostMapping("/user/logout")
    public Response logout(@RequestParam Map<String,String> requestParams)throws Exception{
        String token = requestParams.get("token");
        User user = serveur.sql.User.getByToken(token);
        user.logout(Token.getByValue(token));
        return new ResponseLog(true);
    }

}
