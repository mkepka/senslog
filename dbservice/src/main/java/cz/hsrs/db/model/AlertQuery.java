/**
 * 
 */
package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;

/**
 * @author mkepka
 *
 */
public class AlertQuery implements DBObject{

	private int queryId;
	private String query;
	private int alertId;
	
	/*private boolean lastStatus;
	private Date lastStatusTimeStamp;
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");*/
	
	public AlertQuery(int queryId, String query, int alertId) {
		this.queryId=queryId;
		this.query = query;
		this.alertId = alertId;
	}
	
	/*public AlertQuery(int queryId, String query, int alertId, boolean lastStatus, Date timeStamp) {
		this.queryId=queryId;
		this.query = query;
		this.alertId = alertId;
		this.lastStatus = lastStatus;
		this.lastStatusTimeStamp = timeStamp;
	}*/
	
	public AlertQuery(ResultSet set) throws SQLException{
		this.queryId = set.getInt("query_id");
		this.query = set.getString("query_string");
		this.alertId = set.getInt("alert_id");
	}
	
	public AlertQuery() throws SQLException{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the queryId
	 */
	public int getQueryId() {
		return queryId;
	}
	public String getQuery(){
		return query;
	}
	public int getAlertId(){
		return alertId;
	}

	/*public boolean getLastStatus(){
		return lastStatus;
	}
	
	public Date getLastStatusTimeStamp(){
		return lastStatusTimeStamp;
	}*/
	
	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new AlertQuery(set);
	}

}