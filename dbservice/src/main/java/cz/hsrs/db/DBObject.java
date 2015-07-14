package cz.hsrs.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBObject{
	public DBObject getDBObject(ResultSet set) throws SQLException;
}

	
