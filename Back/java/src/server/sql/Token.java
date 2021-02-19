package server.sql;

import server.DataBase;
import server.DateTime;
import server.Log;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * A token can be used to validate an email, or to be used on a cookie for connection.
 * The use() method will disable a token.
 */
public class Token {
    public static final int TOKEN_LENGTH = 255;
    public static final int TOKEN_TYPE_LOGIN = 0;
    public static final int TOKEN_TYPE_EMAIL = 1;

    private int id;
    private int type;
    private int userId;
    private String value;
    private DateTime expiration;
    private boolean used;

    private Token(){}

    /**
     * Create a token for email confirmation or login
     * @param rs            A query input
     */
    public Token(ResultSet rs) throws Exception {
        if(!rs.next())
            throw new Exception("No token in query response");

        id = rs.getInt(1);
        type = rs.getInt(2);
        userId = rs.getInt(3);
        value = rs.getString(4);
        expiration = new DateTime(rs.getString(5));
        used = rs.getShort(6) == 1;
    }

    /**
     * Create a token in database
     * @param tokenType     TOKEN_TYPE_* constant int to describe
     * @param user          The user to link
     * @param expiration    When the token will expire
     * @return              The token created
     */
    public static Token create(int tokenType, User user, DateTime expiration) throws Exception {

        String value;
        do {
            value = generateValue();
        }while(Token.exist(value));

        String userid = (user == null) ? "0" : String.valueOf(user.getId());

        String sql = "INSERT INTO Tokens (type, user_id, value, expiration, used) VALUES (?, ?, ?, ?, ?)";
        String[] tab = new String[]{
                String.valueOf(tokenType),
                userid,
                value,
                expiration.toString(),
                "0"
        };
        try {
            DataBase.getInstance().query(sql, tab);
            Token t = Token.getByValue(value);
            Log.info("Token "+tokenType+" created" + ((user == null) ? "" : " by " +user.getMail()) + "\n");
            return t;
        } catch (Exception e) {
            Log.error("Cannot create token\n" + e.getMessage());
            throw new Exception("Connot create token");
        }
    }

    /**
     * Return a token based on the value
     * @param value         The string of the value
     * @return              The token
     */
    public static Token getByValue(String value) throws Exception {
        return new Token(DataBase.getInstance().getByCondition("Tokens", "value", value));
    }
    /**
     * Return the token by id
     * @param id        The id of the token
     * @return          The token
     */
    public static Token getById(int id) throws Exception {
        return new Token(DataBase.getInstance().getByCondition("Tokens", "id", String.valueOf(id)));
    }
    /**
     * Check if the token exist
     * @param value     The value to search
     * @return          If the token exist
     */
    public static boolean exist(String value) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tokens", "value", value);
        return rs.next();
    }
    /**
     * Check if the token is valid with the date and the usage of the token
     * @return      if it is valid
     */
    public boolean isValid(){
        return !used && ((new DateTime()).compareTo(expiration) <= 0);
    }

    public int getId() {
        return id;
    }
    public int getType() {
        return type;
    }
    public String getValue() {
        return value;
    }
    public DateTime getExpiration() {
        return expiration;
    }
    public User getUser() throws Exception {
        if(userId == 0) {
            Log.warn("Get user is null in token " + id);
            return null;
        }
        return User.getById(userId);
    }

    /**
     * Use the token. This token won't be valid after this call
     */
    public void use() throws Exception {
        used = true;
        DataBase.getInstance().changeValue("Tokens", "used", "1", id);
        Log.info("Token "+id+" used");
    }

    /**
     * Delete the token on the database.
     * Avoid this call
     */
    public void delete() throws Exception {
        try {
            DataBase.getInstance().delete("Tokens", id);
            Log.info("Token "+id+" deleted");
        } catch (Exception e) {
            Log.error("Cannot delete Token " + id + "\n" + e.getMessage());
            throw new Exception("Cannot delete Token");
        }
    }

    /**
     * Generate a value for the token
     * @return  A string with the static defined length in the class
     */
    private static String generateValue(){
        String str = "AZERTYUIOPQSDFGHJKLMWXCVBN123456789azertyuiopqsdfghjklmwxcvbn";
        StringBuilder res = new StringBuilder(TOKEN_LENGTH);

        for(int i=0; i<TOKEN_LENGTH; i++){
            int n = (int)(Math.random()*(str.length()-1));
            res.append(str.charAt(n));
        }

        return res.toString();
    }

    public String toString(){
        String response = "";
        response += "Token("+id+"){\n";
        response += "   user        : " + userId + "\n";
        response += "   type        : " + type + "\n";
        response += "   value       : " + value.substring(0, 20) + "...\n";
        response += "   expiration  : " + expiration.toString() + "\n";
        response += "   used        : " + used + "\n";
        response += "}";
        return response;
    }
}
