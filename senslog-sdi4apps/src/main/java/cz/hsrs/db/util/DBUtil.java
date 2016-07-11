package cz.hsrs.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;

public class DBUtil {

	//protected final Connection conn;
	//protected Statement stmt;
	protected SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	protected SQLExecutor stmt = SQLExecutor.getInstance();

	public DBUtil() {		
	}
		
	
	protected String toDateString(Date date){	
		return "TIMESTAMP '"+format.format(date)+"'";
		//return "TIMESTAMP WITH TIME ZONE  'epoch' +" + date.getTime() + "* INTERVAL '1 second'";
		
	}
	
	protected List<? extends DBObject> generateObjectList(DBObject element, ResultSet res) {
			
		
		try {
			List<DBObject> result = new LinkedList<DBObject>();
			while (res.next()) {
				DBObject dbob = (element.getClass().newInstance()).getDBObject(res);
				result.add(dbob);
			}
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
		
}
