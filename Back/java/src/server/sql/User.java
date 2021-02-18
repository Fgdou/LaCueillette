package server.sql;

import server.Common;
import server.DataBase;
import server.Log;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class User {
    private int id;
    private String name;
    private String surname;
    private String tel;
    private String mail;
    private String password;
    private boolean admin;
    private boolean emailVerified;
    private Date created;
    private Date lastConnection;

    public User(){}

    public static User register(String name, String surname, String tel, String mail, String password, boolean admin) throws Exception {
        if(userExist(mail))
            throw new Exception("User already exist");

        User user = new User();

        user.name = name;
        user.surname = surname;
        user.tel = tel;
        user.mail = mail;
        user.password = Common.hash(password);
        user.admin = admin;
        user.emailVerified = false;
        user.created = new Date();
        user.lastConnection = null;

        String sql = "INSERT INTO Users (name, surname, tel, mail, password, admin, created, email_verified, last_connection) VALUES (?, ?, ?, ?, ?, ?, ?, ?, null)";
        String[] tab = new String[]{
                name,
                surname,
                tel,
                mail,
                user.password,
                (admin) ? "1" : "0",
                (new java.sql.Date(user.created.getTime())).toString(),
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
        if(queryResult.isBeforeFirst())
            if(!queryResult.next())
                throw new Exception("No user in query response");

        id = queryResult.getInt(1);
        name = queryResult.getString(2);
        surname = queryResult.getString(3);
        tel = queryResult.getString(4);
        mail = queryResult.getString(5);
        password = queryResult.getString(6);
        admin = queryResult.getShort(7) == 1;
        created = queryResult.getDate(8);
        emailVerified = queryResult.getShort(9) == 1;
        lastConnection = queryResult.getDate(10);
    }

    public static boolean userExist(String email) throws Exception {
        ResultSet result = DataBase.getInstance().getByCondition("Users", "mail", email);
        return result.next();
    }
    public static User getByEmail(String email) throws Exception {
        return new User(DataBase.getInstance().getByCondition("Users", "mail", email));
    }
    public static User getById(int id) throws Exception{
        return new User(DataBase.getInstance().getByCondition("Users", "mail", String.valueOf(id)));
    }

    public void delete() throws Exception {
        try {
            DataBase.getInstance().delete("Users", id);
        } catch (Exception e) {
            Log.warn("Can't delete user " + mail + "\n" + e.getMessage());
            throw new Exception("Cannot remove user");
        }
    }

    public String toString(){
        String response = "";

        response += "user: {\n";
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
