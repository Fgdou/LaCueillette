package server.sql;

import server.DataBase;
import server.Log;

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

    public User(String name, String surname, String tel, String mail, String password, boolean admin) {
        this.name = name;
        this.surname = surname;
        this.tel = tel;
        this.mail = mail;
        this.password = password;
        this.admin = admin;
        this.emailVerified = false;
        this.created = new Date();
        this.lastConnection = null;

        String sql = "INSERT INTO Users (name, surname, tel, mail, password, admin, created, email_verified, last_connection) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String[] tab = new String[]{
                name,
                surname,
                tel,
                mail,
                password,
                (admin) ? "1" : "0",
                "0",
                created.toString(),
                "null"
        };

        DataBase db = DataBase.getInstance();
        try {
            db.query(sql, tab);
            Log.info("User " + mail + " created");
        } catch (Exception e) {
            Log.error("Unable to create user\n" + e.getMessage());
            e.printStackTrace();
        }

    }

    public User(ResultSet queryResult) throws SQLException {
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
