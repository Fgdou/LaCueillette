package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class StoreType {
    private int id;
    private String name;

    private StoreType(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        name = rs.getString(2);
    }

    public static StoreType create(String name) throws Exception {
        if(StoreType.exists(name))
            throw new Exception("Type already exist");

        String sql = "INSERT INTO StoreType (name) VALUES (?)";
        String[] tab = new String[]{name};
        DataBase.getInstance().query(sql, tab);

        return StoreType.getByName(name);
    }

    public static StoreType getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("StoreType", "id", String.valueOf(id));

        if(!rs.next())
            throw new Exception("Type not found");
        return new StoreType(rs);
    }
    public static StoreType getByName(String name) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("StoreType", "name", name);

        if(!rs.next())
            throw new Exception("Type not found");
        return new StoreType(rs);
    }

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
    public void delete() throws Exception {
        DataBase.getInstance().delete("StoreType", id);
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
