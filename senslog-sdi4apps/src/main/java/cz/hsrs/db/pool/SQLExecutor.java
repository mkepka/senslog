package cz.hsrs.db.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Class for executing sql commands. This class is responsible for proper
 * Connections handling. This class is singleton.
 * 
 * @author jezekjan
 * 
 */
public class SQLExecutor {

    private static String UnitsPositions_table;
    private static String UnitsTracks_table;
    private static String UnitsLastPositions_table;
    
    private static String Brand_picture;
    private static String configfile = null;

    private static ConnectionPool mycp;
    private static Properties prop;
    private static SQLExecutor SQLEXEC;
    public static final String LOGGER_ID = "database_logger";
    public static Logger logger = Logger.getLogger(LOGGER_ID);

    /*private SQLExecutor() {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            ObjectName name = new ObjectName("cz.hsrs.db:type=ConnectionPool"); 
            ConnectionPool mbean = SQLExecutor.getCoonectionPool();
            mbs.registerMBean(mbean, name);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        if (prop == null) {
            throw new NullPointerException(
                    "You have to setProperties before getting Connection");
        }
    }*/
    
    private SQLExecutor() {
        if (prop == null) {
            throw new NullPointerException(
                    "You have to setProperties before getting Connection");
        }
    }

    /**
     * Method returns instance of the SQLExecutor class
     * if the instance doesn't exist, it creates new instance 
     * @return instance of SQLExecutor class
     */
    public static synchronized SQLExecutor getInstance() {
        if (SQLEXEC == null) {
            SQLEXEC = new SQLExecutor();
        }
        return SQLEXEC;
    }

    /**
     * Method sets properties from Properties object 
     * @param proper instance of Properties class with settings
     */
    public static void setProperties(Properties proper) {

        prop = proper;
        mycp = new ConnectionPool((String) prop.get("Address"),
                (String) prop.get("Username"), (String) prop.get("Password"));
        mycp.setMaxPoolSize(5);
        mycp.setMinPoolSize(1);
        
        UnitsPositions_table = prop.getProperty("UnitsPositions_table");
        UnitsTracks_table = prop.getProperty("UnitsTracks_table");
        UnitsLastPositions_table = prop.getProperty("UnitsLastPositions_table");
        Brand_picture = prop.getProperty("Brand_picture");
    }

    /**
     * Executes SELECT
     * 
     * @param sql
     * @return a ResultSet object that contains the data produced by the given query; never null 
     * @throws SQLException
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        PooledConnection con = mycp.getPooledConnection();
        ResultSet rs = null;
        Statement st= null;
        try {
            st = con.createStatement();
            rs = st.executeQuery(sql);
            con.release();    
        } catch (SQLException e) {
            mycp.removeConnection(con);
            throw new SQLException(e);
        }
        
        return rs;
    }
    
    /**
     * Executes UPDATE
     * 
     * @param sql
     * @return either (1) the row count for SQL DML statements
     *         or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public static synchronized int executeUpdate(String sql)
            throws SQLException {
        PooledConnection con = mycp.getPooledConnection();
        int rs;
        Statement st= null;
        try {
            st = con.createStatement();
            rs = st.executeUpdate(sql);
            con.release();
        } catch (SQLException e) {
            mycp.removeConnection(con);
            throw new SQLException(e);
        }
        return rs;
    }
    
    public static void close() {
        mycp.closeConnections();
    }

    public static int getNumberOfConnection() {
        return mycp.getNumConnections();
    }

    public static int getNumberOfUsedConnection() {
        try {
            return mycp.getNumUsedConnections();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getConfigfile() {
        return configfile;
    }

    public static void setConfigfile(String configfile) {
        SQLExecutor.configfile = configfile;
    }

    protected static ConnectionPool getCoonectionPool() {
        return mycp;
    }

    public static String getUnitsPositions_table() {
        return UnitsPositions_table;
    }

    public static String getUnitsTracks_table() {
        return UnitsTracks_table;
    }

    public static String getUnitsLastPositions_table() {
        return UnitsLastPositions_table;
    }
    
    /**
     * Method returns name of the picture for head of web pages
     * @return name of the picture as String
     */
    public static String getBrand_picture_name(){
        return Brand_picture;
    }
}