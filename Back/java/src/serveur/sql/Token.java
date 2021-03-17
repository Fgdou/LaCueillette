package serveur.sql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import serveur.DataBase;
import serveur.DateTime;
import serveur.Log;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

/**
 * A token can be used to validate an email, or to be used on a cookie for connection.
 * The use() method will disable a token.
 */
public class Token {
    public static final int TOKEN_LENGTH = 255;
    public static final int TOKEN_TYPE_LOGIN = 0;
    public static final int TOKEN_TYPE_EMAIL = 1;
    public static final int TOKEN_TYPE_FORGOT_PASSWORD = 2;

    private int id;
    private int type;
    private int userId;
    private String value;
    private DateTime expiration;
    private boolean used;
    private String name;

    private Token(){}

    /**
     * Create a token for email confirmation or login
     * @param rs            A query input
     */
    private Token(ResultSet rs) throws Exception {
        id = rs.getInt(1);
        type = rs.getInt(2);
        userId = rs.getInt(3);
        value = rs.getString(4);
        expiration = new DateTime(rs.getString(5));
        used = rs.getShort(6) == 1;
        name = rs.getString(7);
    }

    /**
     * Create a token in database
     * @param tokenType     TOKEN_TYPE_* constant int to describe
     * @param user          The user to link
     * @param expiration    When the token will expire
     * @param name          The name of the PC client
     * @return              The token created
     */
    public static Token create(int tokenType, User user, DateTime expiration, String name) throws Exception {

        String value;
        do {
            value = generateValue();
        }while(Token.exist(value));

        String userid = (user == null) ? "0" : String.valueOf(user.getId());

        String sql = "INSERT INTO Tokens (type, user_id, value, expiration, used, name) VALUES (?, ?, ?, ?, ?, ?)";
        String[] tab = new String[]{
                String.valueOf(tokenType),
                userid,
                value,
                expiration.toString(),
                "0",
                name
        };
        try {
            DataBase.getInstance().query(sql, tab);
            Token t = Token.getByValue(value);
            Log.info("Token "+tokenType+" created" + ((user == null) ? "" : " by " +user.getMail()));
            return t;
        } catch (Exception e) {
            Log.error("Cannot create token\n" + e.getMessage());
            throw new Exception("Connot create token");
        }
    }

    /**
     * @param o A token
     * @return  if the object is the same token
     */
    public boolean equals(Object o){
        if(o instanceof Token)
            return equals((Token) o);
        return false;
    }

    /**
     * @param t     An other token
     * @return      If it is the same token
     */
    public boolean equals(Token t){
        return value.equals(t.value);
    }

    /**
     * Return a token based on the value
     * @param value         The string of the value
     * @return              The token
     */
    public static Token getByValue(String value) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tokens", "value", value);
        if(!rs.next())
            throw new Exception("Token not found");
        Token t = new Token(rs);
        if(!t.isValid())
            throw new Exception("Token not valid");
        return t;
    }
    /**
     * Return the token by id
     * @param id        The id of the token
     * @return          The token
     */
    public static Token getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tokens", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Token not found");
        return new Token(rs);
    }

    /**
     * @param us the user
     * @return  all the valid tokens for this user
     */
    public static List<Token> getByUser(User us) throws Exception {
        int id = (us == null) ? 0 : us.getId();
        ResultSet rs = DataBase.getInstance().getByCondition("Tokens", "user_id", String.valueOf(id));
        List<Token> tokens = new LinkedList<>();

        while(rs.next()){
            Token t = new Token(rs);
            if(t.isValid())
                tokens.add(t);
        }

        return tokens;
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
    @JsonIgnore
    public User getUser() throws Exception {
        if(userId == 0) {
            Log.warn("Get user is null in token " + id);
            return null;
        }
        return User.getById(userId);
    }
    public int getUserId(){
        return userId;
    }
    public String getName() {
        return name;
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
        response += "   name        : " + name + "\n";
        response += "   type        : " + type + "\n";
        response += "   value       : " + value.substring(0, 20) + "...\n";
        response += "   expiration  : " + expiration.toString() + "\n";
        response += "   used        : " + used + "\n";
        response += "}";
        return response;
    }
}
