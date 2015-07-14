/**
 * 
 */
package cz.hsrs.db.model;

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
public class IgnitionStatus implements DBObject{

	private int observationId;
	private int gid;
	private Date timeStamp;
	private String timeString;
	private boolean ignitionOn;
	private long unitId;
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	
	public IgnitionStatus(int observationId, int gid, Date timeStamp, double value, long unit_id){
		this.observationId = observationId;
		this.gid = gid;
		this.timeStamp=timeStamp;
		this.timeString = format.format(timeStamp);
		if (value == 1){
			this.ignitionOn=true;
		}
		else {
			this.ignitionOn=false;
		}
		this.unitId=unit_id;		
	}
	public IgnitionStatus(ResultSet set) throws SQLException{
		this.observationId = set.getInt("observation_id");
		this.gid = set.getInt("gid");	
		this.timeStamp = new Date();
		this.timeStamp.setTime(set.getTimestamp("time_stamp").getTime());
		this.timeString = format.format(timeStamp);
		Double value;
		try{
			value = set.getDouble("value");
		}catch(Exception e){
			value = set.getDouble("observed_value");
		}
		if (value == 1){
			this.ignitionOn=true;
		}
		else {
			this.ignitionOn=false;
		}
		this.unitId = set.getLong("unit_id");		
	}
	public int getObservationId(){
		return observationId;
	}
	public int getGid(){
		return gid;
	}
	public String getTimeStamp(){
		return timeString;
	}
	public Date internalGetTimeStamp(){
		return timeStamp;
	}
	public boolean isIgnitionOn(){
		return ignitionOn;
	}
	public long getUnitId(){
		return unitId;
	}
	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new IgnitionStatus(set);
	}	
}
