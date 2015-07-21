package cz.hsrs.db;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.hsrs.db.pool.SQLExecutor;

class ConnectionManager {

	private static Connection conn = null;
	private static Connection connFeeder = null;
	private static Properties prop;	
	
	private static String UnitsPositions_table;
	private static String UnitsTracks_table;
	private static String UnitsLastPositions_table;
		
	
	public static Logger logger = Logger.getLogger(SQLExecutor.LOGGER_ID);
	
	@Deprecated
	private ConnectionManager() {
		
	}

	public static void setProperties(Properties proper) throws SQLException {

			prop = proper;		
			
			UnitsPositions_table = prop.getProperty("UnitsPositions_table");
			UnitsTracks_table = prop.getProperty("UnitsTracks_table");
			UnitsLastPositions_table = prop.getProperty("UnitsLastPositions_table");
		

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
	
  
	public static synchronized Connection getConnectionnn() throws SQLException  {
		if (conn == null || conn.isClosed()) {


			// String propFile =
			// "/home/jezekjan/code/workspace-web2/DBFeederService/WebContent/WEB-INF/database.properties";
			// = new Properties();
			// prop.load(new FileInputStream(propFile));
			try {			
				try {
					Class.forName("org.postgresql.Driver").newInstance();
				} catch (InstantiationException e) {
					throw new SQLException(e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					throw new SQLException(e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					throw new SQLException(e);
				}
				
				conn = DriverManager
						.getConnection((String) prop.get("Address"),
								(String) prop.get("Username"), (String) prop
										.get("Password"));					
				return conn;
			} catch (NullPointerException e) {
				throw new NullPointerException(
						"You have to setProperties before getting Connection");
			}

		} else
			return conn;
	}
  
	
	public static synchronized Connection getConnectionFeeder() throws SQLException  {
		if (connFeeder == null || connFeeder.isClosed()) {
			try {			
				try {
					Class.forName("org.postgresql.Driver").newInstance();
				} catch (InstantiationException e) {
					throw new SQLException(e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					throw new SQLException(e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					throw new SQLException(e);
				}
				
				connFeeder = DriverManager
						.getConnection((String) prop.get("Address"),
								(String) prop.get("Username"), (String) prop
										.get("Password"));					
				return connFeeder;
			} catch (NullPointerException e) {
				throw new NullPointerException(
						"You have to setProperties before getting Connection");
			}

		} else
			return connFeeder;
	}
	
	public static void closeConnection() throws SQLException {
		conn.close();		
	}
	
	public static void closeConnectionFeeder() throws SQLException {
		conn.close();		
	}
}
