/**
 * 
 */
package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.DatabaseFeedOperation;
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
public class AlertUtilTest {

	static AlertUtil aUtil;
	static UnitUtil uUtil;	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");
	private static long unit_id = 111;
	private static String descU1 = "testovaci unit - MiKe";
	private static int alert_1;
	private static int alert_2;
	private static String descA1 = "Pokusny Alert - MiKe";
	private static String descA2 = "Pokusny vystrazny Alert - MiKe";
//	private static String queryString = "SELECT ST_Contains((SELECT the_geom FROM gisdata.border_cz WHERE gid=17), (ST_Transform(st_geomfromtext(?,4326),102067)));";
	private static String replacePattern= "xRPLCx";
	private static String testQueryString = "SELECT ST_DISTANCE(st_GeomFromText(''"+replacePattern+"''), st_GeomFromText(''POINT(15.0 50.0)''))<=1;";

	private static boolean statusQuery = true;
	private static String timeQuery = "2001-07-15 10:00+0100";
	
	
	@BeforeClass
	public static void  setUp() {
		// vlozeni unit a 2 alerts
		DBHelper.setConnection();
		try {		
		
		aUtil = new AlertUtil();
		uUtil = new UnitUtil();
		uUtil.insertUnit(unit_id, descU1);		
		String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertId);
		res.next();
		alert_1 = res.getInt(1);
		aUtil.insertAlert(alert_1, descA1);
		res =  SQLExecutor.getInstance().executeQuery(getAlertId);
		res.next();
		alert_2 = res.getInt(1);
		aUtil.insertAlert(alert_2, descA2);
		//vlozeni zkusebni pozice
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@AfterClass
	public static void delData() throws Exception{
		uUtil.deleteUnit(unit_id);
	
		aUtil.deleteAlert(descA1);
		aUtil.deleteAlert(descA2);
	}
	
	private int insAlertQueryWithStatusAndTimeStamp(long unitId, String queryStr, int alertId, boolean statQuery, String timeStrQuery ) throws Exception{
	
		String getQueryId = "SELECT nextval('alert_queries_to_units_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getQueryId);
		res.next();
		int queryId = res.getInt(1);
		String insQuery = "INSERT INTO alert_queries (query_id, query_string, alert_id) VALUES ("+queryId+", '"+queryStr+"', "+alertId+" );";
		SQLExecutor.executeUpdate(insQuery);
		String insQuery2UnitStat = "INSERT INTO alert_queries_to_units (query_id, unit_id, last_status_alert_query, last_status_time_stamp) VALUES ("+queryId+", "+unitId+", "+statQuery+", '"+timeStrQuery+"');";
		SQLExecutor.executeUpdate(insQuery2UnitStat);
		
		return queryId;
	}
	
	private int insAlertQuery(long unitId, String queryStr, int alertId) throws Exception{
		
		String getQueryId = "SELECT nextval('alert_queries_to_units_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getQueryId);
		res.next();
		int queryId = res.getInt(1);
		String insQuery = "INSERT INTO alert_queries (query_id, query_string, alert_id) VALUES ("+queryId+", '"+queryStr+"', "+alertId+" );";
		SQLExecutor.executeUpdate(insQuery);
		String insQuery2Unit = "INSERT INTO alert_queries_to_units (query_id, unit_id) VALUES ("+queryId+", "+unitId+");";
		SQLExecutor.executeUpdate(insQuery2Unit);
		
		return queryId;
	}
	
	private void delAlertQuery(int queryId) throws Exception{
		
		String delQuery = "DELETE FROM alert_queries WHERE query_id = "+queryId+";";
		SQLExecutor.executeUpdate(delQuery);
	}
	
	private void insAlertEvent(String time, long unitId, int alertId) throws Exception{
		Date time1 = format.parse(time);
		DatabaseFeedOperation.insertAlertEvent(time1, unitId, alertId);
	}
	
	private void delAlertEvent(String timeStamp) throws Exception{
		
		String delEvent = "DELETE FROM alert_events WHERE time_stamp = '"+timeStamp+"';";
		SQLExecutor.executeUpdate(delEvent);	
	}
	
	@Test
	public void testInsertAlert() throws SQLException{
		
		String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertId);
		res.next();
		int alert_id = res.getInt(1);
		String descA = "Testovaci Alert II - MiKe";
		//Assert.assertFalse(aUtil.insertAlert(alert_id, descA)==0);
		aUtil.deleteAlert(alert_id);
	}
	@Test
	public void checkProvideAlerts() throws SQLException{
		Assert.assertTrue(aUtil.provideAlerts(unit_id));
	}
	@Test
	public void testDeleteAlert() throws SQLException{
		
		String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertId);
		res.next();
		int alert_id = res.getInt(1);
		String descA = "Testovaci Alert II - MiKe";
		//aUtil.insertAlert(alert_id, descA);
		Assert.assertNotNull(aUtil.deleteAlert(alert_id));
	}
	
	@Test
	public void testGetAlerts() throws Exception{
		//event to unit
		String time = "2001-07-15 10:00+0100";
		insAlertEvent(time, unit_id, alert_1);
		// find alert to unit
		List<Alert> alerts = aUtil.getAlerts(unit_id);
		Iterator<Alert> iterAlert = alerts.iterator();
		Assert.assertNotNull(iterAlert.next().getDescription());
		//delete event
		delAlertEvent(time);
	}
	
	@Test
	public void testSolveAlertEvent() throws Exception {
		String time = "2001-07-15 10:05+0100";
		insAlertEvent(time, unit_id, alert_2);
		Assert.assertNotNull(aUtil.solveAlertEvent(time, unit_id));
		//delete event
		delAlertEvent(time);
	}
	
	@Test
	public void testGetUnsolvedAlertEvents() throws Exception {
		String timeString = "2001-07-15 10:10+0100";
		insAlertEvent(timeString, unit_id, alert_1);
		
		List<AlertEvent> events = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> iterEvents = events.iterator();
		Assert.assertTrue(iterEvents.hasNext());
		AlertEvent testEvent = iterEvents.next(); 
		Assert.assertEquals(format.parse(timeString), testEvent.internalGetTimeStamp());
		Assert.assertEquals(unit_id, testEvent.getUnitId());
		//delete event
		delAlertEvent(timeString);
	}	
	@Test
	public void testGetAlertEventsByTime() throws Exception{
		String timeString ="2001-08-01 10:15+0100";
		insAlertEvent(timeString, unit_id, alert_1);
				
		String fromDate = "2001-08-01 09:00+0100";
		String toDate =  "2001-08-01 11:00+0100";
		List<AlertEvent> events = aUtil.getAlertEventsByTime(unit_id, fromDate, toDate);
		Iterator<AlertEvent> iterEvents = events.iterator();
		Assert.assertTrue(iterEvents.hasNext());
		Assert.assertEquals(format.parse(timeString),iterEvents.next().internalGetTimeStamp());
		
		//delete event
		delAlertEvent(timeString);
	}
	@Test
	public void testGetUnsolvingAlertEvents() throws Exception{
		String timeString1 ="2001-08-01 10:15+0100";
		insAlertEvent(timeString1, unit_id, alert_1);
		
		List<AlertEvent> events = aUtil.getUnsolvingAlertEvent(unit_id, alert_1);
		Iterator<AlertEvent> iterEvents = events.iterator();
		Assert.assertTrue(iterEvents.hasNext());
		Assert.assertEquals(format.parse(timeString1),iterEvents.next().internalGetTimeStamp());
		
		//delete event
		delAlertEvent(timeString1);	
	}
	@Test
	public void testGetAlertQueries()throws Exception{	
		int queryId = insAlertQuery(unit_id, testQueryString, alert_2);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		Assert.assertTrue(queryIter.hasNext());
		AlertQuery aq = queryIter.next();			
		Assert.assertEquals(alert_2, aq.getAlertId());
		String testString = testQueryString.replace("''", "'");
		Assert.assertEquals(testString, aq.getQuery());
		
		delAlertQuery(queryId);
	}
	
	@Test
	public void testGetLastStatusAlertQueryNeex() throws Exception{
		int queryId = insAlertQuery(unit_id, testQueryString, alert_2);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos = new UnitPosition(unit_id, 16, 50, format.parse("2001-07-15 09:00+0100"));
		boolean fail = false;
		try{
			boolean status = aUtil.getAlertQueryLastStatus(aq, pos);
		}
		catch(NoItemFoundException e){
			fail = true;
			Assert.assertTrue(fail);
		}
		delAlertQuery(queryId);
	}
	@Test
	public void testGetAlertQueryLastStatusTimeStampNeex() throws Exception{
		int queryId = insAlertQuery(unit_id, testQueryString, alert_2);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos = new UnitPosition(unit_id, 16, 50, format.parse("2001-07-15 09:30+0100"));
		
		boolean fail = false;
		try{
			Date timeStamp = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos);
		}
		catch(NoItemFoundException e){
			fail = true;
			Assert.assertTrue(fail);
		}
		delAlertQuery(queryId);		
	}
	
	@Test
	public void testGetLastStatusAlertQuery() throws Exception{
		int queryId = insAlertQueryWithStatusAndTimeStamp(unit_id, testQueryString, alert_2, statusQuery, timeQuery);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos = new UnitPosition(unit_id, 16, 50, format.parse(timeQuery));
		try{
			boolean status = aUtil.getAlertQueryLastStatus(aq, pos);
			Assert.assertEquals(statusQuery, status);
		}
		catch(NoItemFoundException e){
			Assert.fail();
		}
		delAlertQuery(queryId);
	}
	
	@Test
	public void testGetLastStatusAlertQueryTimeStamp() throws Exception{
		int queryId = insAlertQueryWithStatusAndTimeStamp(unit_id, testQueryString, alert_2, statusQuery, timeQuery);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos = new UnitPosition(unit_id, 16, 50, format.parse(timeQuery));
		try{			
			Date dateDB = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos);
			Date dateQuery = format.parse(timeQuery);
			Assert.assertEquals(dateQuery, dateDB);
		}
		catch(NoItemFoundException e){
			Assert.fail();
		}
		delAlertQuery(queryId);
	}
	@Test
	public void testSetNewAlertQueryLastStatus() throws Exception{
		int queryId = insAlertQueryWithStatusAndTimeStamp(unit_id, testQueryString, alert_2, statusQuery, timeQuery);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos = new UnitPosition(unit_id, 16, 50, format.parse(timeQuery));
		boolean newStatus = false;
		aUtil.setNewAlertQueryLastStatus(newStatus, aq, pos);
		boolean newStatusDB = aUtil.getAlertQueryLastStatus(aq, pos);
		Assert.assertEquals(newStatus, newStatusDB);
		
		delAlertQuery(queryId);
	}
	@Test
	public void testSetNewAlertQueryTimeStamp() throws Exception{
		int queryId = insAlertQueryWithStatusAndTimeStamp(unit_id, testQueryString, alert_2, statusQuery, timeQuery);
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		String newTime = "2001-07-16 10:00+0100";
		UnitPosition newPos = new UnitPosition(unit_id, 16, 50, format.parse(newTime));		
		aUtil.setNewAlertQueryTimeStamp(aq, newPos);
		Date newTimeDB = aUtil.getAlertQueryLastStatusTimeStamp(aq, newPos);
		Assert.assertEquals(format.parse(newTime), newTimeDB);
		
		delAlertQuery(queryId);
	}
	
	@Test
	public void testCheckAlertQuery()throws Exception{
		int queryId = insAlertQuery(unit_id, testQueryString, alert_1);
		
		UnitPosition posT = new UnitPosition(unit_id, 14, 50, format.parse("2001-07-15 10:00+0100"));
		UnitPosition posF = new UnitPosition(unit_id, 11, 49, format.parse("2001-07-15 10:05+0100"));
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		Assert.assertTrue(queryIter.hasNext());
		AlertQuery aq = queryIter.next();
		Assert.assertTrue(aUtil.checkAlertQuery(aq, posT));
		Assert.assertFalse(aUtil.checkAlertQuery(aq, posF));
		
		delAlertQuery(queryId);
	}
}
