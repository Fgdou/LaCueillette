import server.DataBase;
import server.sql.User;

import java.sql.ResultSet;

class test{
    public static void main(String[] args) throws Exception {
        DataBase.createInstance();
        DataBase.getInstance().connect();
    }
}