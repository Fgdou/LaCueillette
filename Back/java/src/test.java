import server.DataBase;
import server.sql.User;

import java.sql.ResultSet;

class test{
    public static void main(String[] args) throws InterruptedException {
        DataBase db = new DataBase("root", "g7Nn5DkEBLCbpCTNw84FPkw3wjoDPYu4KJ2NSSkb", "LaCueillette", "localhost:8082");
        db.connect();

        try {
            ResultSet rs = db.query("SELECT * FROM Users");

            while(rs.next()){
                System.out.println(new User(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}