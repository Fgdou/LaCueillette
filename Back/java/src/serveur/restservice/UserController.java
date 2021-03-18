package serveur.restservice;

import org.springframework.web.bind.annotation.*;
import serveur.Time;
import serveur.sql.Address;
import serveur.sql.Token;
import serveur.sql.User;

import java.util.Map;

@RestController
public class UserController {

    @PostMapping("/user")
    public User userGetByEmail(@RequestParam(value = "user_token") String token) throws Exception {
        return serveur.sql.User.getByToken(token);
    }

    /**
     * Create a user account
     *
     * @param requestParams Map of Strings : name, surname, tel, email et password required for user account creation
     * @return Response of error or "user created"
     * @throws Exception The email is already used
     */
    @PostMapping("/user/new")
    public Response createNewUser(@RequestParam Map<String, String> requestParams) throws Exception {
        String userName = requestParams.get("name");
        String userSurname = requestParams.get("surname");
        String userTel = requestParams.get("tel");
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        User newUser = serveur.sql.User.register(userName, userSurname, userTel, userEmail, userPassword, false);
        //TODO Send mail for verification
        return new ResponseLog(true);
    }

    /**
     * Change the user's password
     *
     * @param requestParams Map of Strings : email, old_password, new_password
     * @return Response with error or log
     * @throws Exception The email is not present in the database
     */
    @PostMapping("/user/change/password")
    public Response changePassword(@RequestParam Map<String, String> requestParams) throws Exception {
        String token = requestParams.get("user_token");
        String userPasswordNew = requestParams.get("password");
        User user = User.getByToken(token);
        user.changePassword(userPasswordNew);
        return new ResponseLog(true);

    }

    @PostMapping("/user/forgotPassword")
    public Response forgotPassword(@RequestParam Map<String, String> requestParams) throws Exception {
        String email = requestParams.get("email");

        User us = User.getByEmail(email);

        Token t = Token.create(Token.TOKEN_TYPE_FORGOT_PASSWORD,
                us,
                new Time(0, 10, 0),
                email);

        //TODO send mail with token

        return new ResponseLog("Mail sent to " + email);
    }

    /**
     * Change one or more user informations
     *
     * @param requestParams Map of Strings : name, surname, tel, adresse, ville, code_postal, address_id
     * @return Response of error or log
     */
    @PostMapping("/user/change")
    public Response changeUserInfos(@RequestParam Map<String, String> requestParams) throws Exception {
        String userName = requestParams.get("new_name");
        String userSurname = requestParams.get("new_surname");
        String userTel = requestParams.get("new_tel");
        int userNumber = Integer.parseInt(requestParams.get("number"));
        String userWay = requestParams.get("way");
        String userTown = requestParams.get("ville");
        int userCP = Integer.parseInt(requestParams.get("code_postal"));
        String userToken = requestParams.get("user_token");
        Address address = Address.getById(Integer.parseInt(requestParams.get("address_id")));

        User user = User.getByToken(userToken);
        //Change if and only if not empty
        if (!userName.equals(""))
            user.setName(userName);
        if (!userSurname.equals(""))
            user.setSurname(userSurname);
        if (!userTel.equals(""))
            user.setTel(userTel);
        if (!userWay.equals("") || !userTown.equals("")) {
            address.setNumber(userNumber);
            address.setWay(userWay);
            address.setPostalcode(userCP);
            address.setCity(userTown);
        }
        return new ResponseLog(true);
    }

    /**
     * Delete an account
     *
     * @param requestParams Map of Strings : email, password
     * @return Response True if and only if the user has been successfully deleted
     */
    @PostMapping("/user/delete")
    public Response deleterUser(@RequestParam Map<String, String> requestParams) throws Exception {
        String token = requestParams.get("user_token");
        User user = User.getByToken(token);
        user.delete();
        //TODO Send mail for verification
        return new ResponseLog(true);
    }

    /**
     * Login
     *
     * @param requestParams Paramètre requis
     * @return Token créé
     */
    @PostMapping("/user/login")
    public Token login(@RequestParam Map<String, String> requestParams) throws Exception {
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        return User.login(userEmail, userPassword, userEmail);
    }

    /**
     * Logout
     *
     * @param requestParams Paramètre requis
     * @return Response booléen
     */
    @PostMapping("/user/logout")
    public Response logout(@RequestParam Map<String, String> requestParams) throws Exception {
        String token = requestParams.get("user_token");
        User user = serveur.sql.User.getByToken(token);
        user.logout(Token.getByValue(token));
        return new ResponseLog(true);
    }

}
