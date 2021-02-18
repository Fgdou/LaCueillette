package server;

import java.sql.*;

public class DataBase {

    private static DataBase INSTANCE;

    private String _username;
    private String _password;
    private String _database;
    private String _host;

    private Connection _con = null;

    public DataBase(String username, String password, String database, String host){
        _username = username;
        _password = password;
        _database = database;
        _host = host;

        INSTANCE = this;
    }

    /**
     * Connect to the database
     * @return  success
     */
    public boolean connect(){
        try {
            _con = DriverManager.getConnection("jdbc:mysql://"+_host+"/" + _database,_username,_password);
        } catch (Exception e) {
            Log.error(e.getMessage());
            return false;
        }
        Log.info("Database connected");
        return true;
    }

    /**
     * @return  if the database is still connected
     */
    public boolean isConnected(){
        if(_con == null)
            return false;

        try {
            return !_con.isClosed();
        } catch (SQLException throwables) {
            Log.warn("SQL connection throw an error\n" + throwables.getMessage());
            return false;
        }
    }

    /**
     * Call the databases with the query
     * @param sql   The SQL query with ?
     * @param tab   Replace the ? with data
     * @return      The result of the query
     */
    public ResultSet query(String sql, String[] tab) throws Exception {
        checkConnected();

        ResultSet rs = null;
        try {
            CallableStatement st = _con.prepareCall(sql);

            if(tab != null)
                for(int i=0; i<tab.length; i++)
                    st.setString(i+1, tab[i]);
            st.execute();
            rs = st.getResultSet();
        } catch (SQLException throwables) {
            Log.error("SQL query error on '"+sql+"'\n"+ throwables.getMessage());
            throw new Exception(throwables);
        }
        return rs;
    }

    private void checkConnected() {
        if(!isConnected()){
            Log.fatal("Database is not connected");
        }
    }

    /**
     * Call the databases with the query
     * @param sql   The SQL query with ?
     * @return      The result of the query
     */
    public ResultSet query(String sql) throws Exception {
        return query(sql, null);
    }

    public static void printResultSet(ResultSet rs){
        try{
            ResultSetMetaData metaData = rs.getMetaData();
            int nColumns = metaData.getColumnCount();
            for(int i=1; i<=nColumns; i++){
                System.out.print(metaData.getColumnName(i) + ", ");
            }
            System.out.println("");
            while(rs.next()){
                for(int i=1; i<=nColumns; i++) {
                    System.out.print(rs.getObject(i) + ", ");
                }
                System.out.println("");
            }
        }catch (Exception e){
            Log.warn("Can't read the ResultSet\n" + e.getMessage());
        }
    }

    public static DataBase getInstance(){
        return INSTANCE;
    }
}
