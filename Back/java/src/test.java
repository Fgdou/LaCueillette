import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

class test{
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting main service...");

        Connection cn = null;
        Statement st = null;

        try{
            cn = DriverManager.getConnection("jdbc:mysql://localhost/LaCueillette", "root", "g7Nn5DkEBLCbpCTNw84FPkw3wjoDPYu4KJ2NSSkb");
            st = cn.createStatement();
            String sql = "INSERT INTO test (id) VALUES (3)";
            st.executeUpdate(sql);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}