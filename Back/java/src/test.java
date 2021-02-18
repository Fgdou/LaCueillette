import server.DataBase;
import server.sql.User;

import java.sql.ResultSet;

class test{
    public static void main(String[] args) throws Exception {
        DataBase db = new DataBase("root", "g7Nn5DkEBLCbpCTNw84FPkw3wjoDPYu4KJ2NSSkb", "LaCueillette", "localhost:8082");
        db.connect();

        if(User.userExist("fabigoardou@gmail.com"))
            User.getByEmail("fabigoardou@gmail.com").delete();
        else{
            User user = User.register("Fabien", "Goardou", "0651535931", "fabigoardou@gmail.com", "didou", true);

            System.out.println(user);
        }
    }
}