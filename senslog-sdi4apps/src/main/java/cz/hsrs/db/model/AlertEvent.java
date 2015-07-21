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
public class AlertEvent implements DBObject{
	
	private int alertEventId;
	private Date timeStamp;
	private String timeString;
	private boolean solving;
	private boolean solved;
	private long unitId;
	private Alert alert;
	private int gid;
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");	
	
	public AlertEvent(Date timeStamp, boolean solved, boolean solving, long unit_id, Alert alert, int gid){
		this.timeStamp=timeStamp;
		this.timeString = format.format(timeStamp);
		this.solved=solved;
		this.solving = solving;
		this.unitId=unit_id;
		this.alert=alert;
		this.gid = gid;
	}
	public AlertEvent(ResultSet set) throws SQLException{
		this.alertEventId = set.getInt("alert_event_id");
		this.timeStamp = new Date();
		this.timeStamp.setTime(set.getTimestamp("time_stamp").getTime());

		this.timeString = format.format(timeStamp);
		this.alert = new Alert(set.getInt("alert_id"), set.getString("alert_description"));
		this.solved=set.getBoolean("solved");
		this.solving=set.getBoolean("solving");
		this.unitId = set.getLong("unit_id");
		this.gid = set.getInt("gid");
	}
	public AlertEvent() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the alertEventId
	 */
	public int getAlertEventId() {
		return alertEventId;
	}
	
	/**
	 * @return the timeStamp as Date
	 */
	public Date internalGetTimeStamp() {
		return timeStamp;
	}
	/**
	 * @return the timeStamp as String
	 */
	public String getTimeStamp(){
		return timeString;
	}
	
	/**
	 * @return the solved
	 */
	public boolean isSolved() {
		return solved;
	}
	/**
	 * @return the solving
	 */
	public boolean isSolving() {
		return solving;
	}
	/**
	 * @return the unitId
	 */
	public long getUnitId() {
		return unitId;
	}
	
	/**
	 * @return the alertId
	 */
	public Alert getAlert() {
		return alert;
	}
	/**
	 * @return the gid
	 */
	public int getGid(){
		return gid;
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException{
		return new AlertEvent(set);
	}	
}
