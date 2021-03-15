package serveur.sql;

import serveur.DataBase;

import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;

public class Cart {
    //Product_id -> quantity
    Map<Integer, Integer> products_q;
    Map<Integer, Float> products_kg;
    int user_id;

    private Cart(ResultSet rs) throws Exception {
        products_kg = new TreeMap<>();
        products_q = new TreeMap<>();

        while(rs.next()){
            user_id = rs.getInt(2);
            int product_id = rs.getInt(3);
            int quantity = rs.getInt(4);
            float kg = rs.getFloat(5);

            if(kg == 0)
                products_q.put(product_id, quantity);
            else
                products_kg.put(product_id, kg);
        }
    }

    public static Cart getByUser(User user) throws Exception {
        ResultSet rs = DataBase.getInstance().getByCondition("Cart", "user_id", String.valueOf(user.getId()));
        return new Cart(rs);
    }

    public User getUser() throws Exception{
        return User.getById(user_id);
    }

    public void addProduct(Product p, int quantity){}
    public void addProduct(Product p, float kg){}
    public void removeProduct(Product p){}
    public void changeQuantity(Product p, int quantity){}
    public void changeQuantity(Product p, float kg){}

    public float getPriceHT() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice();
        }
        for(Integer sub : products_kg.keySet()){
            float q = products_kg.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice();
        }

        return sum;
    }
    public float getTVA() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getTva()*product.getPrice();
        }
        for(Integer sub : products_kg.keySet()){
            float q = products_kg.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getTva()*product.getPrice();
        }

        return sum;
    }
    public float getPriceTTC() throws Exception {
        float sum = 0;

        for(Integer sub : products_q.keySet()){
            int q = products_q.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice() * (1 + product.getTva());
        }
        for(Integer sub : products_kg.keySet()){
            float q = products_kg.get(sub);
            SubProduct sp = SubProduct.getById(sub);
            Product product = sp.getProduct();

            sum += q*product.getPrice() * (1 + product.getTva());
        }

        return sum;
    }

    //TODO orders

    public void clear() throws Exception {
        String sql = "DELETE FROM Cart WHERE user_id = ?";
        String[] tab = new String[]{String.valueOf(user_id)};

        DataBase.getInstance().query(sql, tab);
    }
}
