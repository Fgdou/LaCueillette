package server.sql;

import server.Common;
import server.DataBase;
import server.DateTime;
import server.Log;

import java.sql.ResultSet;
import java.util.Calendar;

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
    public User(ResultSet queryResult) throws Exception {
        if(!queryResult.next())
            throw new Exception("No user in query response");

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

    public static boolean exist(String email) throws Exception {
        ResultSet result = DataBase.getInstance().getByCondition("Users", "mail", email);
        return result.next();
    }
    public static User getByEmail(String email) throws Exception {
        return new User(DataBase.getInstance().getByCondition("Users", "mail", email));
    }
    public static User getById(int id) throws Exception{
        return new User(DataBase.getInstance().getByCondition("Users", "id", String.valueOf(id)));
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
    public DateTime getLastConnection() {
        return lastConnection;
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
    public void setPassword(String password){
        //TODO
    }

    public void delete() throws Exception {
        try {
            DataBase.getInstance().delete("Users", id);
            Log.info("User "+mail+" deleted");
        } catch (Exception e) {
            Log.warn("Can't delete user " + mail + "\n" + e.getMessage());
            throw new Exception("Cannot remove user");
        }
    }

    public Token login(String password){
        return null; //TODO (not ask if user exist)
    }
    public boolean verifyToken(Token token){
        return false; //TODO
    }
    public void forgetPassword(){
        //TODO
    }

    //TODO orders

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
