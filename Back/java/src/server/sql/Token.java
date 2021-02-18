package server.sql;

import server.DataBase;
import server.Log;

import java.sql.ResultSet;
import java.util.Date;

public class Token {
    public static final int TOKEN_LENGTH = 255;
    public static final int TOKEN_TYPE_LOGIN = 0;
    public static final int TOKEN_TYPE_EMAIL = 1;

    private int id;
    private int type;
    private int userId;
    private String value;
    private Date expiration;
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
        expiration = rs.getDate(5);
        used = rs.getShort(6) == 1;
    }

    /**
     * Create a token in database
     * @param tokenType     TOKEN_TYPE_* constant int to describe
     * @param user          The user to link
     * @param expiration    When the token will expire
     * @return              The token created
     */
    public static Token create(int tokenType, User user, Date expiration) throws Exception {

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
                new java.sql.Date(expiration.getTime()).toString(),
                "0"
        };
        try {
            DataBase.getInstance().query(sql, tab);
        } catch (Exception e) {
            Log.error("Cannot create token\n" + e.getMessage());
            throw new Exception("Connot create token");
        }

        return Token.getByValue(value);
    }

    /**
     * Return a token based on the value
     * @param value         The string of the value
     * @return              The token
     */
    public static Token getByValue(String value) throws Exception {
        return new Token(DataBase.getInstance().getByCondition("Tokens", "value", value));
    }
    //TODO comment
    public static Token getById(int id) throws Exception {
        return new Token(DataBase.getInstance().getByCondition("Tokens", "id", String.valueOf(id)));
    }
    public static boolean exist(String value) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tokens", "value", value);
        return rs.next();
    }
    public boolean isValid(){
        return !used && ((new Date()).compareTo(expiration) <= 0);
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
    public Date getExpiration() {
        return expiration;
    }
    public User getUser() throws Exception {
        if(userId == 0) {
            Log.warn("Get user is null in token " + id);
            return null;
        }
        return User.getById(userId);
    }

    public void use() throws Exception {
        used = true;
        DataBase.getInstance().changeValue("Tokens", "used", "1", id);
    }
    public void delete() throws Exception {
        try {
            DataBase.getInstance().delete("Tokens", id);
        } catch (Exception e) {
            Log.error("Cannot delete Token " + id + "\n" + e.getMessage());
            throw new Exception("Cannot delete Token");
        }
    }

    private static String generateValue(){
        String str = "AZERTYUIOPQSDFGHJKLMWXCVBN123456789azertyuiopqsdfghjklmwxcvbn";
        StringBuilder res = new StringBuilder(TOKEN_LENGTH);

        for(int i=0; i<TOKEN_LENGTH; i++){
            int n = (int)(Math.random()*(str.length()-1));
            res.append(str.charAt(n));
        }

        return res.toString();
    }
}
