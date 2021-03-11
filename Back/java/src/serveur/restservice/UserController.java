package serveur.restservice;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import serveur.sql.User;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {

    //private final AtomicLong counter = new AtomicLong();

    @GetMapping("/user")
    public User userGetByEmail(@RequestParam(value = "email") String email) throws Exception {
        return serveur.sql.User.getByEmail(email);
    }

    /**
     * Create a user account
     * @param requestParams Map of Strings : name, surname, tel, email et password required for user account creation
     * @return JSONObject of error or "user created"
     * @throws Exception The email is already used
     */
    @PostMapping("/user/new")
    public JSONObject createNewUser(@RequestParam Map<String,String> requestParams) throws Exception{
        String userName = requestParams.get("name");
        String userSurname = requestParams.get("surname");
        String userTel = requestParams.get("tel");
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        try {
            User newUser = serveur.sql.User.register(userName, userSurname, userTel, userEmail, userPassword, false);
            return new JSONObject().put("log", "user created");
        }catch (Exception e){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", e.toString());
            return jsonObject;
        }
    }

    /**
     * Change the user's password
     * @param requestParams Map of Strings : email, old_password, new_password
     * @return  JSONObject with error or log
     * @throws Exception The email is not present in the database
     */
    @PostMapping("/user/change/password")
    public JSONObject changePassword(@RequestParam Map<String,String> requestParams) throws Exception {
        String userEmail = requestParams.get("email");
        String userPasswordOld = requestParams.get("old_password");
        String userPasswordNew = requestParams.get("new_password");
        //TODO Vérifier si l'ancien mot de passe est le bon
        //TODO Utiliser la bonne fonction
        User user = serveur.sql.User.getByEmail(userEmail);
        try{
            user.changePassword(userPasswordNew);
            return new JSONObject().put("log", "password successfully changed");
        } catch (Exception e) {
            return new JSONObject().put("error", e.toString());
        }
    }

    /**
     * Change one or more user informations
     * @param requestParams Map of Strings : name, surname, tel, adresse, ville, code_postal
     * @return JSONObject of error or log
     * @throws Exception ???
     */
    @PostMapping("/user/change")
    public JSONObject changeUserInfos(@RequestParam Map<String,String> requestParams) throws Exception{
        String userName = requestParams.get("new_name");
        String userSurname = requestParams.get("new_surname");
        String userTel = requestParams.get("new_tel");
        String userEmail = requestParams.get("email"); //Not changed !
        String userAddress = requestParams.get("adresse");
        String userTown = requestParams.get("ville");
        String userCP = requestParams.get("code_postal");
        User user = serveur.sql.User.getByEmail(userEmail);
        try{
            //TODO Change if and only if not empty
            user.setName(userName);
            user.setSurname(userSurname);
            user.setTel(userTel);
            return new JSONObject().put("log", "infos changed");
        }
        catch (Exception e){
            return new JSONObject().put("error", e.toString());
        }
    }

    /**
     * Delete an account
     * @param requestParams Map of Strings : email, password
     * @return True if and only if the user has been successfully deleted
     * @throws Exception ???
     */
    @PostMapping("/user/delete")
    public boolean deleterUser(@RequestParam Map<String,String> requestParams) throws Exception{
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        User user = serveur.sql.User.getByEmail(userEmail);
        //TODO vérifier si le password est le bon
        if(false /* Vérifier ici */) {
            user.delete();
            return true;
        }
        else
            return false;
    }

}
