import serveur.DataBase;

class test{
    public static void main(String[] args) throws Exception {
        DataBase.createInstance();
        DataBase.getInstance().connect();
    }
}