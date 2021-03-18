package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


/**
 * A tag is put on products for searching them
 */
public class Tag {
    private int id;
    private String name;

    private Tag(){}
    private Tag(ResultSet rs) throws SQLException {
        id = rs.getInt(1);
        name = rs.getString(2);
    }

    /**
     * Create on database
     * @param name  the display name
     * @return  the class created
     */
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

    /**
     * @param name of the tag
     * @return  the class
     */
    public static Tag getByName(String name) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tags", "name", name);
        if(!rs.next())
            throw new Exception("Tag not found");
        return new Tag(rs);
    }
    /**
     * @param id of the tag
     * @return  the class
     */
    public static Tag getById(int id) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Tags", "id", String.valueOf(id));
        if(!rs.next())
            throw new Exception("Tag not found");
        return new Tag(rs);
    }

    /**
     * @param name of the tag
     * @return if the tag exist
     * @throws Exception
     */
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

    /**
     * Remove on the database
     */
    public void delete() throws Exception {
        DataBase.getInstance().delete("Tags", id);
    }

    public String toString(){
        return "Tag(" + id + ") : " + name;
    }
    public boolean equals(Object o){
        return (o instanceof Tag && ((Tag)o).name.equals(name));
    }

    public static List<Tag> getAll() throws Exception {
        List<Tag> list = new LinkedList<>();

        String sql = "SELECT * FROM Tags";

        ResultSet rs = DataBase.getInstance().query(sql);

        while(rs.next())
            list.add(new Tag(rs));

        return list;
    }
}
