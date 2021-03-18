package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * This represent a type of store
 */

public class StoreType {
    private int id;
    private String name;

    private StoreType(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        name = rs.getString(2);
    }

    /**
     * Create a type in database
     * @param name a name
     * @return the new type
     */
    public static StoreType create(String name) throws Exception {
        if(StoreType.exists(name))
            throw new Exception("Type already exist");

        String sql = "INSERT INTO StoreType (name) VALUES (?)";
        String[] tab = new String[]{name};
        DataBase.getInstance().query(sql, tab);

        return StoreType.getByName(name);
    }

    /**
     * @param id
     * @return the type
     */
    public static StoreType getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("StoreType", "id", String.valueOf(id));

        if(!rs.next())
            throw new Exception("Type not found");
        return new StoreType(rs);
    }

    /**
     * @param name
     * @return the type
     */
    public static StoreType getByName(String name) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("StoreType", "name", name);

        if(!rs.next())
            throw new Exception("Type not found");
        return new StoreType(rs);
    }

    /**
     * @param name
     * @return if the type exist
     */
    public static boolean exists(String name) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("StoreType", "name", name);
        return rs.next();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    /**
     * Delete the type on the database
     */
    public void delete() throws Exception {
        DataBase.getInstance().delete("StoreType", id);
    }

    public static List<StoreType> getAll() throws Exception {
        List<StoreType> list = new LinkedList<>();

        String sql = "SELECT * FROM StoreType";

        ResultSet rs = DataBase.getInstance().query(sql);

        while(rs.next())
            list.add(new StoreType(rs));

        return list;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof StoreType && ((StoreType)o).name.equals(name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
