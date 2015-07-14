/**
 * 
 */
package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.hsrs.db.model.Alert;
import cz.hsrs.db.model.AlertEvent;
import cz.hsrs.db.model.AlertQuery;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * @author mkepka
 *
 */
public class AlertUtil extends DBUtil{
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	private static String replacePattern= "xRPLCx";
	
	public AlertUtil() {		
	}
	
	public int insertAlert(int alert_id, String description) throws SQLException {
		String queryConf = "INSERT into alerts(alert_id, alert_description) values ("+ alert_id + ",'" + description + "');";
		return SQLExecutor.executeUpdate(queryConf);
	}
	
	public int deleteAlert(int alert_id) throws SQLException {
		String delete_alert = "DELETE FROM alerts WHERE alert_id = " + alert_id + ";";
		return SQLExecutor.executeUpdate(delete_alert);
	}
	
	public int deleteAlert(String description) throws SQLException {
		String delete_alert = "DELETE FROM alerts WHERE alert_description = '" + description + "';";
		return SQLExecutor.executeUpdate(delete_alert);
	}

	public List<Alert> getAlerts(long unit_ID) throws SQLException {		
		String queryAlerts = "SELECT alerts.alert_id, alert_description"
				+ " FROM alert_events, alerts"
				+ " WHERE alert_events.unit_id =" + unit_ID 
				+ " AND alert_events.alert_id = alerts.alert_id ;";
		ResultSet res = stmt.executeQuery(queryAlerts);
       
		@SuppressWarnings("unchecked")
		List<Alert> alertList = (List<Alert>)generateObjectList(new Alert(), res);
		return alertList;
 
	}
	public int solveAlertEvent(int event_id) throws SQLException {
		String queryConf = "UPDATE alert_events SET solved = true WHERE alert_event_id=" + event_id;
		return SQLExecutor.executeUpdate(queryConf);
	}
	
	public int solveAlertEvent(String timestamp, long unit_id) throws SQLException {
		String queryConf = "UPDATE alert_events SET solved = 'true' WHERE unit_id=" + unit_id+" AND time_stamp ='"+timestamp+"';";
		return SQLExecutor.executeUpdate(queryConf);
	}
	
	public int solvingAlertEvent(String timestamp, long unit_id) throws SQLException{
		String queryConf = "UPDATE alert_events SET solving = 'true' WHERE unit_id=" + unit_id+" AND time_stamp ='"+timestamp+"';";
		return SQLExecutor.executeUpdate(queryConf);
	}
	
	public int getAlertId(String description) throws SQLException{
		String alertQuery = "SELECT alert_id FROM alerts WHERE alert_description ='"+description+"';";
		ResultSet res = SQLExecutor.getInstance().executeQuery(alertQuery);
		res.next();
		return res.getInt("alert_id");
	}
	
	public boolean provideAlerts(long unit_id) throws SQLException{
		String query = "select provide_alerts from units_conf where unit_id=" +unit_id;
		ResultSet res = SQLExecutor.getInstance().executeQuery(query);
		res.next();
		return res.getBoolean(1);
	}
	public List<AlertEvent> getUnsolvedAlertEvents(long unit_id) throws SQLException{
		String queryAlerts = "SELECT alert_event_id, time_stamp, alerts.alert_id, alert_description, solved, solving, gid, alert_events.unit_id"
			+ " FROM alert_events, alerts"
			+ " WHERE alert_events.unit_id = "+unit_id 
			+ " AND alert_events.solved = 'false'"
			+ " AND alert_events.alert_id = alerts.alert_id ;";

		ResultSet res = SQLExecutor.getInstance().executeQuery(queryAlerts);
   
		@SuppressWarnings("unchecked")
		List<AlertEvent> eventList = (List<AlertEvent>)generateObjectList(new AlertEvent(), res);
		return eventList;
		 		
	}
	public List<AlertEvent> getUnsolvingAlertEvent(long unit_id, int alert_id) throws SQLException{
		String queryAlerts = "SELECT alert_event_id, time_stamp, alerts.alert_id, alert_description, solved, solving, gid, alert_events.unit_id"
			+ " FROM alert_events, alerts"
			+ " WHERE alert_events.unit_id = "+unit_id
			+ " AND alert_events.solving = 'false'"
			+ " AND alert_events.solved = 'false'"
			+ " AND alert_events.alert_id = "+alert_id
			+ " AND alert_events.alert_id = alerts.alert_id ;";
		
		ResultSet res = SQLExecutor.getInstance().executeQuery(queryAlerts);
		
		@SuppressWarnings("unchecked")
		List<AlertEvent> eventList = (List<AlertEvent>)generateObjectList(new AlertEvent(), res);
		return eventList;  
	}
	/**
	 * @return List of AlertEvents from time to time including
	 * @throws NoItemFoundException 
	 *
	 */
	public List<AlertEvent> getAlertEventsByTime(long unit_id, String from, String to) throws SQLException{
		String queryAlerts = "SELECT alert_event_id, time_stamp, alerts.alert_id, alert_description, solved, solving, gid, alert_events.unit_id"
			+ " FROM alert_events, alerts, units "
			+ "WHERE alert_events.unit_id = " + unit_id 
			+ " AND alert_events.time_stamp >= '"+ from
			+ "' AND alert_events.time_stamp <= '"+ to
			+ "' AND alert_events.alert_id = alerts.alert_id ;";

		ResultSet res = SQLExecutor.getInstance().executeQuery(queryAlerts);
   
		@SuppressWarnings("unchecked")
		List<AlertEvent> eventList = (List<AlertEvent>)generateObjectList(new AlertEvent(), res);
		return eventList;
	}
	// without status and timeStamp
	public List<AlertQuery> getAlertQueries(long unit_id) throws SQLException{
		String query = "SELECT aq.query_id, aq.query_string, aq.alert_id FROM alert_queries aq, alert_queries_to_units aqu" +
				" WHERE aqu.unit_id = "+unit_id+" AND aqu.query_id = aq.query_id;";
		ResultSet res = SQLExecutor.getInstance().executeQuery(query);
		@SuppressWarnings("unchecked")
		List<AlertQuery> queryList =(List<AlertQuery>)generateObjectList(new AlertQuery(), res);
		return queryList;
	}
	
	// with status and timeStamp
	/*public List<AlertQuery> getAlertQueries(long unit_id) throws SQLException{
		String query = "SELECT aq.query_id, aq.query_string, aq.alert_id, aqu.last_status_alert_query, aqu.last_status_time_stamp FROM public.alert_queries aq, public.alert_queries_to_units aqu" +
				" WHERE aqu.unit_id = "+unit_id+" AND aqu.query_id = aq.query_id;";
		ResultSet res = stmt.executeQuery(query);
		List<AlertQuery> queryList =(List<AlertQuery>)generateObjectList(new AlertQuery(), res);
		return queryList;
	}*/
	/**
	 * Method checks whether given UnitPosition matches given AlertQuery
	 * @param aQuery - Object AlertQuery with query on given position
	 * @param pos - Object UnitPosition with current position geometry
	 * @return Returns boolean true if query with given position returns true
	 * @throws SQLException Throws SQLException if an exception occurs while processing SQL in DBMS
	 */
	public boolean checkAlertQuery(AlertQuery aQuery, UnitPosition pos) throws SQLException{
		String query = aQuery.getQuery();
		//JDCConnection conn = SQLExecutor.getConnection();
		//PreparedStatement pStmt = conn.prepareStatement(query);
		//pStmt.setString(1, pos.internalGetGeom());
		String completedQuery = query.replace(replacePattern, pos.internalGetGeom());
	    ResultSet res = SQLExecutor.getInstance().executeQuery(completedQuery);
	    //conn.release();
	    // conn.close();
		
		if(res.next()==true){
			return res.getBoolean(1);
		}
		else{
			throw new SQLException("Exception in executing geometry query!");
		}
	}
	
	public boolean getAlertQueryLastStatus(AlertQuery aQuery, UnitPosition pos) throws NoItemFoundException, SQLException{	
		String sql = "SELECT last_status_alert_query FROM alert_queries_to_units" +
				" WHERE query_id = " + aQuery.getQueryId() +
				" AND unit_id = "+pos.getUnit_id()+" ;";
		ResultSet res = SQLExecutor.getInstance().executeQuery(sql);
		if(res.next() == true){
			boolean status = res.getBoolean(1);
			boolean wasNull = res.wasNull();
			if (wasNull == false){
				return status;
			}
			else{
				throw new NoItemFoundException("getAlertQueryLastStatus for "
						+ pos.getUnit_id() + " and " + aQuery.getQueryId()
						+ " does not exist!");
			}
		}
		else{
			throw new NoItemFoundException("getAlertQueryLastStatus for "
					+ pos.getUnit_id() + " and " + aQuery.getQueryId()
					+ " does not exist!");
		}
	}

	/**
	 * Method gets last timestamp of status for given pair of AlertQuery and unit
	 * @param aQuery - Object AlertQuery with queryId
	 * @param pos - Object UnitPosition with unitId
	 * @return - Returns last timestamp of query status as Date
	 * @throws SQLException Throws SQLException if an exception occur while processing SQL in DBMS
	 * @throws NoItemFoundException Throws Exception if there is no pair of queryId and unitId in DBMS 
	 */
	public Date getAlertQueryLastStatusTimeStamp(AlertQuery aQuery, UnitPosition pos) throws SQLException, NoItemFoundException{
		String sql = "SELECT last_status_time_stamp FROM alert_queries_to_units" +
		" WHERE query_id = " + aQuery.getQueryId() +
		" AND unit_id = "+pos.getUnit_id()+" ;";
		ResultSet res = SQLExecutor.getInstance().executeQuery(sql);
		if(res.next()==true){
			String timeStamp = res.getString(1);
			boolean wasNull = res.wasNull();
			if (wasNull == false){
				try {
					return format.parse(timeStamp+"00");
				} catch (ParseException e) {
					throw new SQLException(e);
				}
				//return timeStamp;
			}
			else{
				throw new NoItemFoundException("getAlertQueryLastStatusTimeStamp for "
						+ pos.getUnit_id() + " and " + aQuery.getQueryId()
						+ " does not exist!");
			}
		}
		else{
			throw new NoItemFoundException("getAlertQueryLastStatusTimeStamp for "
					+ pos.getUnit_id() + " and " + aQuery.getQueryId()
					+ " does not exist!");
		}	
	}	

	/**
	 * Method sets new timestamp of last status of query
	 * @param aQuery - Object AlertQuery with query
	 * @param pos - Object UnitPosition with position geometry
	 * @return Returns int from executeQuery method
	 * @throws SQLException Throws SQLException if an exception occur while processing SQL in DBMS
	 */
	public int setNewAlertQueryTimeStamp(AlertQuery aQuery, UnitPosition pos) throws SQLException{
		String queryConf = "UPDATE alert_queries_to_units SET last_status_time_stamp = '"+pos.getTime_stamp()+"' WHERE unit_id=" + pos.getUnit_id()+" AND query_id = " + aQuery.getQueryId() +";";
		return SQLExecutor.executeUpdate(queryConf);
	}

	/**
	 * Method sets new status of AlertQuery to database
	 * @param newStatus - New status of query as boolean
	 * @param aQuery - Object AlertQuery with query
	 * @param pos - Object UnitPosition with position geometry
	 * @return Returns int from executeQuery method
	 * @throws SQLException - Throws SQLException if an exception occur while processing SQL in DBMS
	 */
	public int setNewAlertQueryLastStatus(boolean newStatus, AlertQuery aQuery, UnitPosition pos) throws SQLException{
		String queryConf = "UPDATE alert_queries_to_units SET last_status_alert_query = "+newStatus+" WHERE unit_id=" + pos.getUnit_id()+" AND query_id = " + aQuery.getQueryId() +";";
		return SQLExecutor.executeUpdate(queryConf);
	}
}