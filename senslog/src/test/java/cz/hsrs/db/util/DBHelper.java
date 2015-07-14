package cz.hsrs.db.util;

import java.io.FileInputStream;
import java.sql.Statement;
import java.util.Properties;

import cz.hsrs.db.pool.SQLExecutor;

public class DBHelper {
		
	
	public static Statement setConnection() {		
			// TODO Auto-generated method stub	.
		    Statement stmt =null;
			String propFile = "./src/main/webapp/WEB-INF/database.properties";
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream(propFile));
				SQLExecutor.setProperties(prop);
			//	ConnectionManager.setProperties(prop);
			  //  stmt = ConnectionManager.getConnection().createStatement();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stmt;
		}

		
	}


