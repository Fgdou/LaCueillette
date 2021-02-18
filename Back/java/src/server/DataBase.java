package server;

import java.sql.*;

public class DataBase {

    private static DataBase INSTANCE = null;

    private String _username;
    private String _password;
    private String _database;
    private String _host;

    private Connection _con = null;

    public DataBase(String username, String password, String database, String host){
        if(INSTANCE != null)
            throw new RuntimeException("The singleton is already instantiated");

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
            throw new Exception("Query error");
        }
        return rs;
    }

    /**
     * Check and stop the program if the database is no longuer connected
     */
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

    /**
     * Return the conditional query on the database
     * @param table     The table to search
     * @param key       The key to apply the test
     * @param value     The value
     * @return          All the rows matched
     */
    public ResultSet getByCondition(String table, String key, String value) throws Exception {
        String sql = "SELECT * FROM "+table+" WHERE ("+key+" = ?)";
        String[] tab = new String[]{
                value
        };
        return query(sql, tab);
    }

    /**
     * Change a value the id in the table
     * @param table     The table
     * @param col       The column
     * @param value     The value
     * @param id        The id to select in the table
     */
    public void changeValue(String table, String col, String value, int id) throws Exception {
        String sql = "UPDATE ? SET ? = ? WHERE id = ?";
        String[] tab = new String[]{
                table,
                col,
                value,
                String.valueOf(id)
        };

        query(sql, tab);
    }

    /**
     * Remove an element on table
     * @param table Name of the table
     * @param id    Id of the element
     */
    public void delete(String table, int id) throws Exception {
        String sql = "DELETE FROM " + table + " WHERE id = ?";
        String[] tab = new String[]{String.valueOf(id)};
        query(sql, tab);
    }

    /**
     * Print to the Log class the response of the query
     * @param rs    The result of the query
     */
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

    /**
     * Get the singleton of the database
     * @return  The database
     */
    public static DataBase getInstance(){
        return INSTANCE;
    }
}
