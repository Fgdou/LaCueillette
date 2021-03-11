package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represent a Address for any usage
 */

public class Address {
    private int id;
    private int number;
    private String way;
    private String city;
    private int postalcode;
    private String state;
    private int userId;

    private Address(){}
    private Address(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        number = rs.getInt(2);
        way = rs.getString(3);
        city = rs.getString(4);
        postalcode = rs.getInt(5);
        state = rs.getString(6);
        userId = rs.getInt(7);
    }

    /**
     * Create an address into database
     * @param number
     * @param way
     * @param city
     * @param postalcode
     * @param state
     * @return      The new address
     */
    public static Address create(int number, String way, String city, int postalcode, String state, User us) throws Exception {
        String sql = "INSERT INTO Addresses (number, way, city, postalcode, state, user_id) VALUES (?, ?, ?, ?, ?, ?);";

        int user_id = (us == null) ? 0 : us.getId();

        String[] tab = new String[]{
                String.valueOf(number),
                way,
                city,
                String.valueOf(postalcode),
                state,
                String.valueOf(user_id)
        };
        DataBase.getInstance().query(sql, tab);

        ResultSet rs = DataBase.getInstance().query("SELECT * FROM Addresses WHERE id = (SELECT LAST_INSERT_ID());");
        rs.next();
        return new Address(rs);
    }

    /**
     * Get the address
     * @param id        The id
     * @return          The address
     */
    public static Address getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Addresses", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Address not found");
        return new Address(rs);
    }

    /**
     * Get all the addresses of one user
     * @param user      The user
     * @return          The addresses
     */
    public static List<Address> getByUser(User user) throws Exception{

        int user_id = (user == null) ? 0 : user.getId();

        String sql = "SELECT * FROM Addresses WHERE user_id = ?;";
        String[] tab = new String[]{
                String.valueOf(user_id)
        };
        ResultSet rs = DataBase.getInstance().query(sql, tab);

        List<Address> list = new LinkedList<>();
        while(rs.next())
            list.add(new Address(rs));

        return list;
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
    public int getPostalcode() {
        return postalcode;
    }
    public String getState() {
        return state;
    }
    public User getUser() throws Exception {
        return User.getById(userId);
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
    public void setPostalcode(int postalcode) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "postalcode", String.valueOf(postalcode), id);
        this.postalcode = postalcode;
    }
    public void setState(String state) throws Exception {
        DataBase.getInstance().changeValue("Addresses", "state", state, id);
        this.state = state;
    }

    /**
     * WARN : FUNCTION NOT IMPLEMENTED
     * @param other     The other address for calculation
     * @return          The ditance between the two points
     */
    public double distance(Address other){
        return 0; //TODO distance
    }

    /**
     * Delete on the database
     */
    public void delete() throws Exception {
        DataBase.getInstance().delete("Addresses", id);
    }


    @Override
    public String toString() {
        return "Address("+id+"){" +
                "   id=" + id +
                "   number=" + number + "\n" +
                "   way=" + way + '\n' +
                "   city=" + city + '\n' +
                "   postalcode=" + postalcode +
                "   state=" + state + '\n' +
                "   userId=" + userId + "\n" +
                '}';
    }
    public boolean equals(Object o){
        if(!(o instanceof Address))
            return false;
        Address a = (Address) o;
        return number == a.number &&
                way.equals(a.way) &&
                city.equals(a.city) &&
                postalcode == a.postalcode &&
                state.equals(a.state);
    }
}
