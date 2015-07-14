package cz.hsrs.db.model.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.pool.SQLExecutor;

public class DBItemInfo implements DBObject {

	private final Map<String, Object> properties;

	private final Map<String, Long> fk;

	public DBItemInfo() {
		properties = null;
		fk = null;
	}
	public DBItemInfo(String schema, String tableName, long id,
			String forigenKeyName) throws NoItemFoundException{
		properties = new HashMap<String, Object>();
		fk = new HashMap<String, Long>();
		try {
			
			String query = "SELECT * FROM " + schema + "." + tableName
					+ " WHERE " + forigenKeyName + " = " + id;
			ResultSet res = SQLExecutor.getInstance().executeQuery(query);
			if (res.next()) {
				fk.put(forigenKeyName, id);
			} else {
				throw new NoItemFoundException("Can not find item for "+ query);
			}
			int count = res.getMetaData().getColumnCount();

			for (int i = 1; i < count; i++) {
				properties.put(res.getMetaData().getColumnName(i), res
						.getObject(res.getMetaData().getColumnName(i)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*public Map<String, Long> getFk() {
		return new HashMap<String, Long>(fk);
	}*/

	public Map<String, Object> getProperties() {
		return new HashMap<String, Object>(properties);
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new DBItemInfo();
	}

}
