/**
 * 
 */
package cz.hsrs.db.model.custom;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;

/**
 * Class for modeling simple unit_position
 * @author mkepka
 *
 */
public class UnitPositionSimple implements DBObject {

    private final String time_stamp;
    private final double x;
    private final double y;
    
    /**
     * Empty constructor for generating JSON 
     */
    public UnitPositionSimple(){
        this.time_stamp = null;
        this.x = Double.NaN;
        this.y = Double.NaN;
    }
    
    public UnitPositionSimple(ResultSet set) throws SQLException {
        this.time_stamp = set.getString("time_stamp");
        this.x = set.getDouble("st_x");
        this.y = set.getDouble("st_y");
    }
    /**
     * @param time_string
     * @param x
     * @param y
     */
    public UnitPositionSimple(String time_string, double x, double y) {
        this.time_stamp = time_string;
        this.x = x;
        this.y = y;
    }

    /* (non-Javadoc)
     * @see cz.hsrs.db.DBObject#getDBObject(java.sql.ResultSet)
     */
    @Override
    public DBObject getDBObject(ResultSet set) throws SQLException {
        return new UnitPositionSimple(set);
    }

    /**
     * @return the time_string
     */
    public String getTime_stamp() {
        return time_stamp;
    }

    /**
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * @return the y
     */
    public double getY() {
        return y;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UnitPositionSimple [time_string=" + time_stamp + ", x=" + x
                + ", y=" + y + "]";
    }
}