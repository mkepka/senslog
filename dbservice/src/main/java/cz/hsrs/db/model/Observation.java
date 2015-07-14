package cz.hsrs.db.model;
// default package
// Generated 3.6.2008 8:30:06 by Hibernate Tools 3.2.2.GA

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * Observation generated by hbm2java
 */
public class Observation implements DBObject {

	private int id;
	private long sensor_id;	
	private Date timeStamp;
	private int gid;
	private long unit_id;
	private double observedValue;
	private final SimpleDateFormat formater = new SimpleDateFormat(
	"yyyy-MM-dd HH:mm:ssZ");
	
	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
	
		return new Observation(set);
	}
	public Observation(Date timeStamp, double observedValue, long sensor_id, long unit_id) {
		super();
		this.sensor_id = sensor_id;
		this.timeStamp = timeStamp;
		this.unit_id = unit_id;
		this.observedValue = observedValue;
	}			

	public Observation(ResultSet set) throws SQLException {
		super();		
		this.sensor_id = set.getLong("sensor_id");
		String time_string =  set.getString("time_stamp");
		try {
			timeStamp =  formater.parse(time_string+"00");				
		} catch (ParseException e) {				
			// Should never happpend
			e.printStackTrace();
			SQLExecutor.logger.log(Level.SEVERE, e.getMessage());				
		}	
		
		this.unit_id = set.getLong("unit_id");
		this.observedValue = set.getDouble("observed_value");
		this.gid = set.getInt("gid");
	}
	public Observation() {
	}	

	public long getUnitId(){
		return unit_id;
	}
	
	public long getSensorId(){
		return sensor_id;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public int getGid() {
		return gid;
	}


	public double getObservedValue() {
		return observedValue;
	}

	public void setObservedValue(double observedValue) {
		this.observedValue = observedValue;
	}


	
	public boolean insertToDb() throws SQLException{
		
		String ins = "INSERT INTO observations(time_stamp, observed_value, sensor_id, unit_id) VALUES (" +
				"'"+formater.format(this.timeStamp)+ "', " + 
				this.observedValue+", " +
				this.sensor_id+"," +
				this.unit_id+")";
		int i = SQLExecutor.executeUpdate(ins);
		if (i==1) return false;
		return true;
		
	}

}
