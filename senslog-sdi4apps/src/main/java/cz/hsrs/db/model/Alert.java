package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;

/**
 * @author mkepka
 *
 */
public class Alert implements DBObject{

	private int alertId;
	private String description;
	
	public Alert(int alertId, String description) {
		this.alertId = alertId;
		this.description = description;
	}
	
	public Alert(ResultSet set) throws SQLException{
		this.alertId = set.getInt("alert_id");
		this.description = set.getString("alert_description");
	}
	
	public Alert() throws SQLException{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param alertId the alertId to set
	 */
	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}
	/**
	 * @return the alertId
	 */
	public int getAlertId() {
		return alertId;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		return new Alert(set);
	}
	
}
