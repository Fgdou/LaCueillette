package serveur.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import serveur.Common;
import serveur.DataBase;
import serveur.DateTime;
import serveur.Log;

import java.sql.ResultSet;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String surname;
    private String tel;
    private String mail;
    private String password;
    private boolean admin;
    private boolean emailVerified;
    private DateTime created;
    private DateTime lastConnection;

    private User(){}

    /**
     * Create user in database
     * @param name      The first name (nom)
     * @param surname   The last name  (prenom)
     * @param tel       The phone number (up to 10 chars)
     * @param mail      The email
     * @param password  The un-encoded password
     * @param admin     If this is an admin
     * @return          The user created
     */
    public static User register(String name, String surname, String tel, String mail, String password, boolean admin) throws Exception {
        if(exist(mail))
            throw new Exception("User already exist");

        String sql = "INSERT INTO Users (name, surname, tel, mail, password, admin, created, email_verified, last_connection) VALUES (?, ?, ?, ?, ?, ?, ?, ?, null)";
        String[] tab = new String[]{
                name,
                surname,
                tel,
                mail,
                Common.hash(password),
                (admin) ? "1" : "0",
                new DateTime().toString(),
                "0"
        };

        DataBase db = DataBase.getInstance();
        try {
            db.query(sql, tab);
            Log.info("User " + mail + " created");
        } catch (Exception e) {
            Log.error("Unable to create user\n" + e.getMessage());
            throw new Exception("Creating user");
        }

        return User.getByEmail(mail);
    }
    private User(ResultSet queryResult) throws Exception {
        id = queryResult.getInt(1);
        name = queryResult.getString(2);
        surname = queryResult.getString(3);
        tel = queryResult.getString(4);
        mail = queryResult.getString(5);
        password = queryResult.getString(6);
        admin = queryResult.getShort(7) == 1;
        created = new DateTime(queryResult.getString(8));
        emailVerified = queryResult.getShort(9) == 1;
        lastConnection = new DateTime(queryResult.getString(10));
    }

    /**
     * @param email identifier of the user
     * @return      If the user exist
     */
    public static boolean exist(String email) throws Exception {
        ResultSet result = DataBase.getInstance().getByCondition("Users", "mail", email);
        return result.next();
    }

    /**
     * @param email the identifier of the user
     * @return      The user
     * @throws Exception if not found
     */
    public static User getByEmail(String email) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Users", "mail", email);
        if(!rs.next())
            throw new Exception("User not found");
        return new User(rs);
    }

    /**
     * @param id of the user
     * @return  the user in database
     * @throws Exception    if not found
     */
    public static User getById(int id) throws Exception{
        ResultSet rs = DataBase.getInstance().getByCondition("Users", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("User not found");
        return new User(rs);
    }

    /**
     * @param token a login token
     * @return  the user
     * @throws Exception if token is not valid
     */
    public static User getByToken(String token) throws Exception {
        Token t = Token.getByValue(token);
        if(t.getType() != Token.TOKEN_TYPE_LOGIN)
            throw new Exception("Wrong token type");
        return t.getUser();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getTel() {
        return tel;
    }
    public String getMail() {
        return mail;
    }
    public boolean isAdmin() {
        return admin;
    }
    public boolean isEmailVerified() {
        return emailVerified;
    }
    public DateTime getCreated() {
        return created;
    }
    @JsonIgnore
    public DateTime getLastConnection() {
        return lastConnection;
    }
    @JsonIgnore
    public List<Address> getAddresses() throws Exception {
        return Address.getByUser(this);
    }
    @JsonIgnore
    public List<Store> getStores() throws Exception{
        return Store.getByUser(this);
    }

    public void setName(String name) throws Exception {
        this.name = name;
        DataBase.getInstance().changeValue("Users", "name", name, id);
    }
    public void setSurname(String surname) throws Exception {
        this.surname = surname;
        DataBase.getInstance().changeValue("Users", "surname", surname, id);
    }
    public void setTel(String tel) throws Exception {
        this.tel = tel;
        DataBase.getInstance().changeValue("Users", "tel", tel, id);
    }
    public void setAdmin(boolean admin) throws Exception {
        this.admin = admin;
        DataBase.getInstance().changeValue("Users", "admin", (admin)?"1":"0", id);
    }
    public void setEmailVerified(boolean emailVerified) throws Exception {
        this.emailVerified = emailVerified;
        DataBase.getInstance().changeValue("Users", "email_verified", (emailVerified)? "1":"0", id);
    }
    public void changePassword(String password) throws Exception {
        if(password == null)
            throw new Exception("No password");
        password = Common.hash(password);

        DataBase.getInstance().changeValue("Users", "password", password, id);

        Log.info("User " + mail + " changed his password");
    }

    /**
     * Delete the user on database
     */
    public void delete() throws Exception {
        for(Address a : getAddresses())
            a.delete();
        for(Token t : getTokens())
            t.delete();
        DataBase.getInstance().delete("Users", id);
        Log.info("User "+mail+" deleted");
    }

    public boolean equals(Object e){
        if(e instanceof User)
            return equals((User)e);
        return false;
    }
    public boolean equals(User other){
        return mail.equals(other.mail);
    }

    /**
     * @param password the unencoded password
     * @param PCname   the name of the connection / pc for the logs
     * @return         the new token is the password was correct
     */
    public Token login(String password, String PCname) throws Exception {
        if(password == null)
            throw new Exception("No password");

        password = Common.hash(password);

        if(!password.equals(this.password))
            throw new Exception("Wrong password");

        //if(!emailVerified)
        //    throw new Exception("Email not verified");

        Token t = Token.create(Token.TOKEN_TYPE_LOGIN, this, new DateTime().add(0, 0, 1, 0, 0, 0), PCname);

        lastConnection = new DateTime();
        DataBase.getInstance().changeValue("Users", "last_connection", lastConnection.toString(), id);
        Log.info("User " + mail + " connected with token " + t.getId());

        return t;
    }

    /**
     * @param mail      identifier of the user
     * @param password  password unencoded
     * @param PCname    name of the connection / pc for the logs
     * @return          a token is the login infos are correct
     */
    public static Token login(String mail, String password, String PCname) throws Exception {
        User us = User.getByEmail(mail);
        return us.login(password, PCname);
    }

    /**
     * Check on database the email
     * @param token the email token to verify
     * @throws Exception if the token is not valid
     */
    public static void verifyEmail(String token) throws Exception {
        Token t = Token.getByValue(token);
        if(t.getType() != Token.TOKEN_TYPE_EMAIL)
            throw new Exception("Wrong token type");
        User u = t.getUser();
        u.setEmailVerified(true);
        t.use();
    }

    /**
     * Delete the login token
     * @param t the token
     */
    public void logout(Token t) throws Exception {
        t.use();
    }

    /**
     * @return all the valid tokens
     */
    @JsonIgnore
    public List<Token> getTokens() throws Exception {
        return Token.getByUser(this);
    }

    /**
     * Delete all the tokens (email and login)
     */
    public void removeTokens() throws Exception {
        List<Token> tokens = Token.getByUser(this);
        for(Token t : tokens)
            t.delete();
    }
    @JsonIgnore
    public List<Order> getOrders() throws Exception {
        return Order.getByUser(this);
    }

    public String toString(){
        String response = "";

        response += "user("+id+"): {\n";
        response += "    name           : " + name + "\n";
        response += "    surname        : " + surname + "\n";
        response += "    tel            : " + tel + "\n";
        response += "    mail           : " + mail + "\n";
        response += "    admin          : " + admin + "\n";
        response += "    emailVerified  : " + emailVerified + "\n";
        response += "    created        : " + created + "\n";
        response += "    lastConnection : " + lastConnection + "\n";
        response += "}";

        return response;
    }
}
