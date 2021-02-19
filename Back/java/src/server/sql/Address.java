package server.sql;

import server.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Address {
    private int id;
    private int number;
    private String way;
    private String city;
    private String postalcode;
    private String state;

    private Address(){}
    public Address(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        number = rs.getInt(2);
        way = rs.getString(3);
        city = rs.getString(4);
        postalcode = rs.getString(5);
        state = rs.getString(6);
    }

    public static Address create(int number, String way, String city, String postalcode, String state) throws Exception {
        String sql = "INSERT INTO Addresses (number, way, city, postalcode, state) VALUES (?, ?, ?, ?, ?);";
        String[] tab = new String[]{
                String.valueOf(number),
                way,
                city,
                postalcode,
                state
        };
        DataBase.getInstance().query(sql, tab);

        ResultSet rs = DataBase.getInstance().query("SELECT * FROM Addresses WHERE id = (SELECT LAST_INSERT_ID());");
        rs.next();
        return new Address(rs);
    }
    public static Address getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Addresses", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Address not found");
        return new Address(rs);
    }

    public int getId() {
        return id;
    }
    public int getNumber() {
        return number;
    }
    public String getWay() {
        return way;
    }
    public String getCity() {
        return city;
    }
    public String getPostalcode() {
        return postalcode;
    }
    public String getState() {
        return state;
    }

    public void setNumber(int number) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "number", String.valueOf(number), id);
        this.number = number;
    }
    public void setWay(String way) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "way", way, id);
        this.way = way;
    }
    public void setCity(String city) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "city", city, id);
        this.city = city;
    }
    public void setPostalcode(String postalcode) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "postalcode", postalcode, id);
        this.postalcode = postalcode;
    }
    public void setState(String state) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "state", state, id);
        this.state = state;
    }

    //TODO test this class
    //TODO distance
}
