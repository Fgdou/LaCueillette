package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Tag {
    private int id;
    private String name;

    private Tag(){}
    private Tag(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        name = rs.getString(2);
    }

    public static Tag create(String name) throws Exception {
        if(exist(name))
            throw new Exception("Tag already exist");

        String sql = "INSERT INTO Tags (name) VALUES (?);";
        String[] tab = new String[]{
                name
        } ;
        DataBase.getInstance().query(sql, tab);

        return getByName(name);
    }
    public static Tag getByName(String name) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tags", "name", name);
        if(!rs.next())
            throw new Exception("Tag not found");
        return new Tag(rs);
    }
    public static Tag getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tags", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Tag not found");
        return new Tag(rs);
    }
    public static boolean exist(String name) throws Exception {
        try{
            getByName(name);
            return true;
        }catch(Exception e){
            if(e.getMessage().equals("Tag not found"))
                return false;
            throw e;
        }
    }

    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }

    public void delete() throws Exception {
        DataBase.getInstance().delete("Tags", id);
    }

    public String toString(){
        return "Tag(" + id + ") : " + name;
    }
    public boolean equals(Object o){
        return (o instanceof Tag && ((Tag)o).name.equals(name));
    }
}
