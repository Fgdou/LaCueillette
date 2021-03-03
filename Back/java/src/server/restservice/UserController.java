package server.restservice;

import org.springframework.web.bind.annotation.*;
import server.sql.User;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class UserController {

    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/user")
    public User userGetByEmail(@RequestParam(value = "email") String email) throws Exception {
        return server.sql.User.getByEmail(email);
    }

    /**
     * Create a user account
     * @param requestParams Map of Strings : name, surname, tel, email et password required for user account creation
     * @return The user created
     * @throws Exception The email is already used
     */
    @PostMapping("/user/new")
    public User createNewUser(@RequestParam Map<String,String> requestParams) throws Exception{
        String userName = requestParams.get("name");
        String userSurname = requestParams.get("surname");
        String userTel = requestParams.get("tel");
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        try {
            return server.sql.User.register(userName, userSurname, userTel, userEmail, userPassword, false);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Change the user's password
     * @param requestParams Map of Strings : email, old_password, new_password_first, new_password_second
     * @return  True if and only if the password has been successfully changed
     * @throws Exception The email is not present in the database
     */
    @PostMapping("/user/change/password")
    public boolean changePassword(@RequestParam Map<String,String> requestParams) throws Exception {
        String userEmail = requestParams.get("email");
        String userPasswordOld = requestParams.get("old_password");
        String userPasswordNew1 = requestParams.get("new_password_first");
        String userPasswordNew2 = requestParams.get("new_password_second");
        //TODO Vérifier si l'ancien mot de passe est le bon
        //TODO Utiliser la bonne fonction
        User user = server.sql.User.getByEmail(userEmail);
        if (userPasswordNew2.equals(userPasswordNew1)){
            try{
                user.setPassword(userPasswordNew1);
                return true;
            } catch (Exception e){
                return false;
            }
        }
        else
            return false;
    }

    /**
     * Change email of an account
     * @param requestParams Map of Strings : old_email, new_email
     * @return True if and only if the email has been successfully changed
     * @throws Exception ???
     */
    @PostMapping("/user/change/email")
    public boolean changeEmail(@RequestParam Map<String,String> requestParams) throws Exception{
        String userEmailOld = requestParams.get("old_email");
        String userEmailNew = requestParams.get("new_email");
        User user = server.sql.User.getByEmail(userEmailOld);
        if (userEmailNew.equals(userEmailOld))
            return false;
        user.setEmailVerified(false);
        //TODO Modifier l'email
        return true;
    }

    /**
     * Change one or more user informations
     * @param requestParams Map of Strings : name, surname, tel
     * @return The user (with actualized infos)
     * @throws Exception ???
     */
    @PostMapping("/user/change")
    public User changeUserInfos(@RequestParam Map<String,String> requestParams) throws Exception{
        String userName = requestParams.get("new_name");
        String userSurname = requestParams.get("new_surname");
        String userTel = requestParams.get("new_tel");
        String userEmail = requestParams.get("email"); //Not changed !
        User user = server.sql.User.getByEmail(userEmail);
        try{
            user.setName(userName);
            user.setSurname(userSurname);
            user.setTel(userTel);
        }
        catch (Exception e){
            return null;
        }
        return user;
    }

    /**
     * Delete an account
     * @param requestParams Map of Strings : email, password
     * @return True if and only if the user has been successfully deleted
     * @throws Exception ???
     */
    @PutMapping("/user/delete")
    public boolean deleterUser(@RequestParam Map<String,String> requestParams) throws Exception{
        String userEmail = requestParams.get("email");
        String userPassword = requestParams.get("password");
        User user = server.sql.User.getByEmail(userEmail);
        //TODO vérifier si le password est le bon
        if(false /* Vérifier ici */) {
            user.delete();
            return true;
        }
        else
            return false;
    }

}
