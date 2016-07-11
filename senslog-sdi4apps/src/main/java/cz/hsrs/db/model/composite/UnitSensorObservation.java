package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * @author mkepka
 *
 */
public class UnitSensorObservation implements DBObject{

    private long sensorId;
    private String timeStamp;
    private Date time_stamp;
    private int gid;
    private long unitId;
    private Double observedValue;
    
    private final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    
    /**
     * Empty constructor
     */
    public UnitSensorObservation(){
    }
    
    /**
     * Constructor for generating object from ResultSet 
     */
    @Override
    public DBObject getDBObject(ResultSet set) throws SQLException {
        return new UnitSensorObservation(set);
    }
    
    /**
     * Constructor creates object with attributes
     * @param timeStamp - time stamp when observation was measured
     * @param observedValue - observed value, can be NaN
     * @param sensor_id - id of sensor
     * @param unit_id - id of unit
     */
    public UnitSensorObservation(String timeStamp, Double observedValue, long sensor_id, long unit_id) {
        this.sensorId = sensor_id;
        this.timeStamp = timeStamp;
        this.unitId = unit_id;
        this.observedValue = observedValue;
    }
    
    /**
     * Constructor creates object from ResultSet
     * @param set ResultSet with all mandatory attributes
     * @throws SQLException
     */
    public UnitSensorObservation(ResultSet set) throws SQLException {
        this.sensorId = set.getLong("sensor_id");
        this.timeStamp = set.getString("time_stamp");
        try {
            this.time_stamp =  formater.parse(timeStamp+"00");
        } catch (ParseException e) {
            SQLExecutor.logger.log(Level.SEVERE, e.getMessage());
        }
        this.unitId = set.getLong("unit_id");
        this.observedValue = set.getDouble("observed_value");
        this.gid = set.getInt("gid");
    }

    /**
     * @return the sensorId
     */
    public long getSensorId() {
        return sensorId;
    }

    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @return the time_stamp as Date
     */
    public Date internalGetTime_stamp() {
        return time_stamp;
    }

    /**
     * @return the gid
     */
    public int getGid() {
        return gid;
    }

    /**
     * @return the unitId
     */
    public long getUnitId() {
        return unitId;
    }

    /**
     * @return the observedValue
     */
    public Double getObservedValue() {
        return observedValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[sensorId=" + sensorId + ", timeStamp="
                + timeStamp + ", gid=" + gid + ", unitId=" + unitId
                + ", observedValue=" + observedValue + "]";
    }
}