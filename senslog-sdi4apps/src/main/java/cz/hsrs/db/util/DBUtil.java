package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * 
 * @author jezekjan
 *
 */
public class DBUtil {

    protected SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    protected SQLExecutor stmt = SQLExecutor.getInstance();

    public DBUtil() {
    }
    
    protected String toDateString(Date date){
        return "TIMESTAMP '"+format.format(date)+"'";
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
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}