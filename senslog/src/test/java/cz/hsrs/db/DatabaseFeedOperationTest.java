package cz.hsrs.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.el.parser.ParseException;

import cz.hsrs.db.model.AlertEvent;
import cz.hsrs.db.model.AlertQuery;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.AlertUtil;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.ObservationUtil;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;

public class DatabaseFeedOperationTest {

	private static int unit_id = 111;
	private static String descA1 = "Pokusny Alert - MiKe";
	private static int alert_1;
	
	private boolean delete = true;

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	
	static UnitUtil ut;
	static AlertUtil aUtil;
	static ObservationUtil ou;
	static String descU1 = "testovaci unit - MiKe";
	private static String replacePattern= "xRPLCx";
	//private static String queryString = "SELECT ST_Contains((SELECT the_geom FROM gisdata.border_cz WHERE gid=17), (ST_Transform(st_geomfromtext(?,4326),102067)));";
	private static String queryString = "SELECT ST_DISTANCE(ST_GeomFromText(''"+replacePattern+"''), ST_GeomFromText(''POINT(15 50)''))<=1;";
	
	@BeforeClass
	public static void setUp()  {
		DBHelper.setConnection();
		try {
			ut = new UnitUtil();
		    ou = new ObservationUtil();
			// insert test unit and alert
			aUtil = new AlertUtil();	 	 
			ut.insertUnit(unit_id, descU1);
			String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
			ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertId);
			res.next();
			alert_1 = res.getInt(1);
			aUtil.insertAlert(alert_1, descA1);	  
		}catch (SQLException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}	  
	}

	@AfterClass
	public static void delData() throws Exception {	
		ut.deleteUnit(unit_id);
		aUtil.deleteAlert(descA1);		
		
	}	
	int clock = 0;

	private int insAlertQuery(long unitId, String queryStr, int alertId) throws Exception{		
		String getQueryId = "SELECT nextval('alert_queries_to_units_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getQueryId);
		res.next();
		int queryId = res.getInt(1);
		String insQuery = "INSERT INTO alert_queries (query_id, query_string, alert_id) VALUES ("+queryId+", '"+queryStr+"', "+alertId+" );";
		SQLExecutor.getInstance().executeUpdate(insQuery);
		String insQuery2Unit = "INSERT INTO alert_queries_to_units (query_id, unit_id) VALUES ("+queryId+", "+unitId+");";
		SQLExecutor.getInstance().executeUpdate(insQuery2Unit);
		
		return queryId;
	}
	private int insAlertQueryWithStatusAndTimeStamp(long unitId, String queryStr, int alertId, boolean statQuery, String timeStrQuery ) throws Exception{		
		String getQueryId = "SELECT nextval('alert_queries_to_units_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getQueryId);
		res.next();
		int queryId = res.getInt(1);
		String insQuery = "INSERT INTO alert_queries (query_id, query_string, alert_id) VALUES ("+queryId+", '"+queryStr+"', "+alertId+" );";
		SQLExecutor.getInstance().executeUpdate(insQuery);
		String insQuery2UnitStat = "INSERT INTO alert_queries_to_units (query_id, unit_id, last_status_alert_query, last_status_time_stamp) VALUES ("+queryId+", "+unitId+", "+statQuery+", '"+timeStrQuery+"');";
		SQLExecutor.getInstance().executeUpdate(insQuery2UnitStat);
		
		return queryId;
	}
	private void delAlertQuery(int queryId) throws Exception{		
		String delQuery = "DELETE FROM alert_queries WHERE query_id = "+queryId+";";
		SQLExecutor.getInstance().executeUpdate(delQuery);
	}
	
	@Test
	public void testIgnition() throws Exception{
		Date d1 = format.parse("2010-07-17 10:00:00+0100");
		Date d2 = format.parse("2010-07-17 10:05:00+0100");
		Date d3 = format.parse("2010-07-17 10:10:00+0100");
		UnitPosition p1 = new UnitPosition(unit_id, 18, 1, d1);
				
	//	DatabaseFeedOperation.insertObservation(d1, 111, TrackIgnitionSolver.IGNITION_SENSOR_ID, 0);
		DatabaseFeedOperation.insertPosition(111, 15, 1, d2);
	//	DatabaseFeedOperation.insertObservation(d2, 111, TrackIgnitionSolver.IGNITION_SENSOR_ID, 1);
		
		TrackUtil ut = new TrackUtil();
		System.out.println(ut.getTrackLenght(ut.getTrack(111, d2).getGid()));
	}
	public void nOtestInsertTracks() throws Exception {

		int number_of_units = 2;
		int number_of_positions = 10;

		int number_of_tracks = 3;
		double[] old_lat = new double[number_of_units];
		double[] old_lon = new double[number_of_units];
		double[] new_lat = new double[number_of_units];
		double[] new_lon = new double[number_of_units];

		Date date = new Date();
		for (int j = 0; j < number_of_units; j++) {
			ut.deleteUnit(j);
		}

		for (int t = 0; t < number_of_tracks; t++) {
			date.setTime(date.getTime() - 86400000 * 10);
			Random startR = new Random();
			double startLat = 14 + startR.nextDouble() * 0.01;
			double startLon = 50 + startR.nextDouble() * 0.01;

			for (int i = 0; i < number_of_units; i++) {
				old_lat[i] = startLat + (new Double(i)) / 2;
				old_lon[i] = startLon;
			}

			for (int i = 0; i < number_of_positions; i++) {

				for (int j = 0; j < number_of_units; j++) {

					Random m = new Random();
					new_lat[j] = old_lat[j] + m.nextDouble() / 1000;
					new_lon[j] = old_lon[j] + m.nextDouble() / 1000;
					old_lat[j] = new_lat[j];
					old_lon[j] = new_lon[j];

					DatabaseFeedOperation.insertPosition((long)j, new_lat[j],
							new_lon[j], 200.0, date);

				}

			}
		}

	}

	@Test
	public void testInsertClosePositions() throws Exception {
		Date d0 = new Date();
		Date d1 = new Date();
		Date d2 = new Date();
		d0.setTime(d2.getTime() - 2000 * 60);
		d1.setTime(d2.getTime() - 1000 * 60);
		
		DatabaseFeedOperation.insertPosition(unit_id, 14.0000001, 50, 200.0, d0, 0);
		
		DatabaseFeedOperation.insertPosition(unit_id, 14.0000001, 50, 200.0, d1, 0);
		
		DatabaseFeedOperation.insertPosition(unit_id, 14.0, 50.0, 200.0, d2, 0);
				

		String query = "SELECT count(*) FROM units_positions WHERE unit_id = 111" +
		" AND time_stamp >= timestamp with time zone '"+format.format(d0)+
		"' AND time_stamp <= timestamp with time zone '" +format.format(d2)+"'";
		ResultSet rs = SQLExecutor.getInstance().executeQuery(query);
		rs.next();

		Assert.assertEquals(2, rs.getInt(1));
		
		UnitUtil ut = new UnitUtil();
		ut.deleteUnit(10);

	}

	public void testInsertPositions() throws Exception {

		// public static void insertPosition( String sensName, float lat, float
		// lon, Date date)
		int number_of_units = 10;
		int number_of_positions = 5;
		int number_of_obs_per_pos = 1;
		// int number_of_phen = 20;
		int number_of_sensor = 2;
		int pos_interval = 1000;
		int cicles = 1;
		double[] old_lat = new double[number_of_units];
		double[] old_lon = new double[number_of_units];
		double[] new_lat = new double[number_of_units];
		double[] new_lon = new double[number_of_units];

		double[] time_lat = new double[cicles];
		double[] time_lon = new double[cicles];
		Date d = new Date();
		/**
		 * prepare positions
		 */
		for (int i = 0; i < number_of_units; i++) {
			old_lat[i] = 50 + (new Double(i)) / 800;
			old_lon[i] = 14;
		//	deleteUnit(i);
		}

		/**
		 * prepare sensor
		 */
	/*	for (int unit = 1; unit <= number_of_units; unit++) {
			for (int sens = 1; sens <= number_of_sensor; sens++) {
				Connection conn = ConnectionManager.getPooledConnection();
				Statement stmt = conn.createStatement();
				String sensor_id = String.valueOf((unit * number_of_sensor)
						+ sens);
				stmt
						.execute("INSERT INTO sensors(sensor_id, phenomenon_id) values('"
								+ sensor_id + "','" + sens + "');");

			}
		}*/

		for (int i = 0; i < number_of_positions; i++) {

			for (int j = 0; j < number_of_units; j++) {

				Date[] date = new Date[cicles];
				for (int p = 0; p < cicles; p++) {
					
					date[p] = new Date();
					date[p].setTime(d.getTime() + clock * pos_interval);
					clock++;
					// Thread.sleep(1000);
				}
				Random m = new Random();

				for (int p = 0; p < cicles; p++) {
					new_lat[j] = old_lat[j] + m.nextDouble() / 3000;
					new_lon[j] = old_lon[j] + m.nextDouble() / 3000;
					time_lat[p] = new_lat[j];
					time_lon[p] = new_lon[j];
					old_lat[j] = new_lat[j];
					old_lon[j] = new_lon[j];
				}

				for (int p = cicles - 1; p >= 0; p--) {
					//Date d = new Date();
					//double mm = d.getTime();
					DatabaseFeedOperation.insertPosition((long)j, time_lat[p],
							time_lon[p], 200.0, date[p]);
										
					
					//d = new Date();
					// Thread.sleep(3000);
					//mm = mm - d.getTime();

				}
				/**
				 * insert observation
				 */
				for (int obs = 1; obs <= number_of_obs_per_pos; obs++) {

					for (int sens = 1; sens <= number_of_sensor; sens++) {
						Date time = new Date();
						time.setTime(date[0].getTime() + obs
								* (pos_interval / obs));

						Random r = new Random();
						int sensor_id = (j * number_of_sensor) + sens;
						DatabaseFeedOperation.insertObservation(time, j,
								sensor_id, time_lon[0]+r.nextDouble());
					}

				}

			}
		}

		if (delete) {			
			
			for (int j = 0; j < number_of_units; j++) {				
				SQLExecutor.getInstance().executeUpdate("SELECT delete_unit(" + j + ");");
			}
			
		}
		// Connection conn = ConnectionManager.getConnection();

		// Statement stmt = conn.createStatement();

		// ResultSet rs =
		// stmt.executeQuery("SELECT * FROM units_positions WHERE time_stamp = '"+date
		// +"';");
		// rs.next();
		// Assert.assertEquals(unit_id,
		// Integer.parseInt(rs.getArray("unit_id").toString()));
		// stmt.execute("SELECT delete_unit("+unit_id+");");
	}

	@Test
	public void testInsertObservation() throws Exception {
		Date date = format.parse("2010-07-15 10:00:00+0100");
		int sensor_id = 123;
		double value = 7.0;
		// String phenomenon_id = "Test_phenomenon" ;
		// public static void insertPosition( String sensName, float lat, float
		// lon, Date date)
		DatabaseFeedOperation
				.insertObservation(date, unit_id, sensor_id, value);	
	

		String query = "SELECT * FROM observations WHERE time_stamp = '"
				+ format.format(date) + "';";
		ResultSet rs = SQLExecutor.getInstance().executeQuery(query);
		rs.next();
		Assert.assertEquals(value,rs.getDouble("observed_value"),0);
				
	}
	@Test
	public void testInsertAlertEvent() throws Exception{
		
		// select alert_id from alerts
		alert_1 = aUtil.getAlertId(descA1);
		// insert new event
		Date time1 = format.parse("2010-07-15 10:00:00+0100");
		DatabaseFeedOperation.insertAlertEvent(time1, unit_id, alert_1);
		
		// select new event		
		String eventQuery = "SELECT * FROM alert_events WHERE time_stamp ='"+time1+"' AND unit_id = "+unit_id+" AND alert_id = "+alert_1+";";
		ResultSet res = SQLExecutor.getInstance().executeQuery(eventQuery);
		res.next();
		
		Assert.assertEquals(time1, res.getTimestamp("time_stamp"));
		Assert.assertEquals(unit_id, res.getLong("unit_id"));
		Assert.assertEquals(alert_1, res.getInt("alert_id"));
		
		//delete event
		String delEvent = "DELETE FROM alert_events WHERE time_stamp = '"+time1.toString()+"';";
		SQLExecutor.getInstance().executeUpdate(delEvent);
		
		ut.deleteUnit(unit_id);
	}
	
	@Test
	public void testCheckUnexistsAlertQueries() throws ParseException, Exception{			
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, format.parse("2010-07-15 10:05:00+0100"));
		List<AlertEvent> eventList = aUtil.getUnsolvedAlertEvents(unit_id);
		Assert.assertTrue(eventList.isEmpty());
		
		ut.deleteUnit(unit_id);
	}
	
	@Test
	public void testCheckExistsAlertQueries() throws ParseException, Exception{
		//insert alertQuery
		ut.insertUnit(unit_id, descU1);
		int query_1 = insAlertQuery(unit_id, queryString, alert_1);
		
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, format.parse("2010-07-15 10:05:00+0100"));
		List<AlertEvent> eventList = aUtil.getUnsolvedAlertEvents(unit_id);
		Assert.assertTrue(eventList.isEmpty());
		
		// insert position far from test point
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, format.parse("2010-07-15 10:15:00+0100"));
		eventList = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter = eventList.iterator();
		Assert.assertTrue(eventIter.hasNext());
		AlertEvent aE = eventIter.next();
		Assert.assertEquals(alert_1,aE.getAlert().getAlertId());
		Assert.assertEquals(format.parse("2010-07-15 10:15:00+0100"),aE.internalGetTimeStamp());
		
		delAlertQuery(query_1);
		ut.deleteUnit(unit_id);
	}
	@Test
	public void testCheckAQoldFnewT() throws Exception{		
		ut.insertUnit(unit_id, descU1);
		int queryId = insAlertQuery(unit_id, queryString, alert_1);
				
		// insert position far from test point
		Date pos1Date = format.parse("2010-08-15 10:05:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, pos1Date);
		// new AlertEvent created
		List<AlertEvent> eventList1 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter1 = eventList1.iterator();
		AlertEvent aE1 = eventIter1.next();
		Assert.assertEquals(alert_1,aE1.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, aE1.internalGetTimeStamp());
		// new status false, new time of pos1
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos1 = ut.getLastUnitPosition(unit_id);
		boolean status1 = aUtil.getAlertQueryLastStatus(aq, pos1);
		Assert.assertFalse(status1);		
		Date status1Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos1);
		Assert.assertEquals(pos1Date, status1Date);
		
		// insert position near to test point
		Date pos2Date = format.parse("2010-08-15 10:15:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, pos2Date);
		// new AlertEvent not created, still old
		List<AlertEvent> eventList2 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter2 = eventList2.iterator();
		AlertEvent aE2 = eventIter2.next();
		Assert.assertEquals(alert_1,aE2.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, aE2.internalGetTimeStamp());
		// new status true, new time of pos2
		UnitPosition pos2 = ut.getLastUnitPosition(unit_id);
		boolean status2 = aUtil.getAlertQueryLastStatus(aq, pos2);
		Assert.assertTrue(status2);		
		Date status2Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos2);
		Assert.assertEquals(pos2Date, status2Date);
		
		delAlertQuery(queryId);
		ut.deleteUnit(unit_id);
	}
	
	@Test
	public void testCheckAQoldTnewF() throws Exception{
		ut.insertUnit(unit_id, descU1);
		int queryId = insAlertQuery(unit_id, queryString, alert_1);		

		//insert position near to test point
		Date pos1Date = format.parse("2010-08-15 10:05:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, pos1Date);
		// new AlertEvent not created
		List<AlertEvent> eventList1 = aUtil.getUnsolvedAlertEvents(unit_id);
		Assert.assertTrue(eventList1.isEmpty());
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos1 = ut.getLastUnitPosition(unit_id);
		boolean status1 = aUtil.getAlertQueryLastStatus(aq, pos1);
		Assert.assertTrue(status1);		
		Date status1Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos1);
		Assert.assertEquals(pos1Date, status1Date);
		
		// insert position far from test point
		Date pos2Date = format.parse("2010-08-15 10:15:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, pos2Date);
		// new AlertEvent created
		List<AlertEvent> eventList2 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter2 = eventList2.iterator();
		AlertEvent aE2 = eventIter2.next();
		Assert.assertEquals(alert_1,aE2.getAlert().getAlertId());
		Assert.assertEquals(pos2Date, aE2.internalGetTimeStamp());
		
		UnitPosition pos2 = ut.getLastUnitPosition(unit_id);
		boolean status2 = aUtil.getAlertQueryLastStatus(aq, pos2);
		Assert.assertFalse(status2);		
		Date status2Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos2);
		Assert.assertEquals(pos2Date, status2Date);	
		
		delAlertQuery(queryId);
		ut.deleteUnit(unit_id);
	}	
	@Test
	public void testCheckAQoldTnewT() throws Exception{
		ut.insertUnit(unit_id, descU1);
		int queryId = insAlertQuery(unit_id, queryString, alert_1);	
		
		//insert position near to test point
		Date pos1Date = format.parse("2010-08-15 10:05:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, pos1Date);
		// new AlertEvent not created
		List<AlertEvent> eventList1 = aUtil.getUnsolvedAlertEvents(unit_id);
		Assert.assertTrue(eventList1.isEmpty());
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos1 = ut.getLastUnitPosition(unit_id);
		boolean status1 = aUtil.getAlertQueryLastStatus(aq, pos1);
		Assert.assertTrue(status1);		
		Date status1Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos1);
		Assert.assertEquals(pos1Date, status1Date);
		
		// insert position near to test point
		Date pos2Date = format.parse("2010-08-15 10:15:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, pos2Date);
		// new AlertEvent still not created
		List<AlertEvent> eventList2 = aUtil.getUnsolvedAlertEvents(unit_id);
		Assert.assertTrue(eventList2.isEmpty());
		
		UnitPosition pos2 = ut.getLastUnitPosition(unit_id);
		boolean status2 = aUtil.getAlertQueryLastStatus(aq, pos2);
		Assert.assertTrue(status2);		
		Date status2Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos2);
		Assert.assertEquals(pos2Date, status2Date);
		
		delAlertQuery(queryId);
		ut.deleteUnit(unit_id);
	}
	@Test
	public void testCheckAQoldFnewF() throws Exception{
		ut.insertUnit(unit_id, descU1);
		int queryId = insAlertQuery(unit_id, queryString, alert_1);	
		
		// insert position far from test point
		Date pos1Date = format.parse("2010-08-15 10:05:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, pos1Date);
		// new AlertEvent created
		List<AlertEvent> eventList1 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter1 = eventList1.iterator();
		AlertEvent aE1 = eventIter1.next();
		Assert.assertEquals(alert_1,aE1.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, aE1.internalGetTimeStamp());
		
		List<AlertQuery> queryList = aUtil.getAlertQueries(unit_id);
		Iterator<AlertQuery> queryIter = queryList.iterator();
		AlertQuery aq = queryIter.next();
		UnitPosition pos1 = ut.getLastUnitPosition(unit_id);
		boolean status1 = aUtil.getAlertQueryLastStatus(aq, pos1);
		Assert.assertFalse(status1);		
		Date status1Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos1);
		Assert.assertEquals(pos1Date, status1Date);
		
		// insert position far from test point
		Date pos2Date = format.parse("2010-08-15 10:15:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, pos2Date);
		// new AlertEvent not created, still old
		List<AlertEvent> eventList2 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter2 = eventList2.iterator();
		AlertEvent aE2 = eventIter2.next();
		Assert.assertEquals(alert_1,aE2.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, aE2.internalGetTimeStamp());
		// status still false, timeStamp changed
		UnitPosition pos2 = ut.getLastUnitPosition(unit_id);
		boolean status2 = aUtil.getAlertQueryLastStatus(aq, pos2);
		Assert.assertFalse(status2);		
		Date status2Date = aUtil.getAlertQueryLastStatusTimeStamp(aq, pos2);
		Assert.assertEquals(pos2Date, status2Date);	
		
		delAlertQuery(queryId);
		ut.deleteUnit(unit_id);
	}
	@Test
	public void testCheckAQoldFnewTnewF() throws Exception{
		 ut.insertUnit(unit_id, descU1);
    	int queryId = insAlertQuery(unit_id, queryString, alert_1);	
		
		// insert position far from test point
		Date pos1Date = format.parse("2010-08-15 10:05:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, pos1Date);
		// new AlertEvent created
		List<AlertEvent> eventList1 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter1 = eventList1.iterator();
		AlertEvent aE1 = eventIter1.next();
		Assert.assertEquals(alert_1,aE1.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, aE1.internalGetTimeStamp());
		
		// insert position near to test point
		Date pos2Date = format.parse("2010-08-15 10:15:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 15, pos2Date);
		// new AlertEvent not created, still old
		List<AlertEvent> eventList2 = aUtil.getUnsolvedAlertEvents(unit_id);
		Iterator<AlertEvent> eventIter2 = eventList2.iterator();
		AlertEvent aE2 = eventIter2.next();
		Assert.assertEquals(alert_1,aE2.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, aE2.internalGetTimeStamp());
		
		// insert position far from test point
		Date pos3Date = format.parse("2010-08-15 10:30:00+0100");
		DatabaseFeedOperation.insertPosition(unit_id, 49, 11, 300, pos3Date);
		// new AlertEvent created, two events for unit
		List<AlertEvent> eventList3 = aUtil.getUnsolvedAlertEvents(unit_id);
		Assert.assertEquals(2,eventList3.size());
		Iterator<AlertEvent> eventIter3 = eventList3.iterator();
		AlertEvent eventOld = eventIter3.next();
		Assert.assertEquals(alert_1,eventOld.getAlert().getAlertId());
		Assert.assertEquals(pos1Date, eventOld.internalGetTimeStamp());
		AlertEvent eventNew = eventIter3.next();
		Assert.assertEquals(alert_1,eventNew.getAlert().getAlertId());
		Assert.assertEquals(pos3Date, eventNew.internalGetTimeStamp());
		
		delAlertQuery(queryId);
		ut.deleteUnit(unit_id);
	}
	@Test
	public void testInsObsCheckFOTandLOT() throws Exception{
		String obs1Time = "2010-08-15 10:05:00+0100";
		Date obs1Date = format.parse(obs1Time);
		String obs2Time = "2010-08-15 10:15:00+0100";
		Date obs2Date = format.parse(obs2Time);
		String obs3Time = "2010-08-15 10:30:00+0100";
		Date obs3Date = format.parse(obs3Time);
		String obs4Time = "2010-08-15 10:45:00+0100";
		Date obs4Date = format.parse(obs4Time);
		int sensor_id = 111;
		double value1 = 10.0;
		double value2 = 20.0;
		double value3 = 30.0;
		double value4 = 40.0;
		//ut.insertUnit(unit_id, descU1);
		//insert observation
		Observation obs2 = new Observation(obs2Date, value2, sensor_id, unit_id);
		ou.insertObservation(obs2);
		
		Assert.assertEquals(obs2Date, findFOT(unit_id, sensor_id));
		Assert.assertEquals(obs2Date, findLOT(unit_id, sensor_id));
		//insert newest observation		
		Observation obs4 = new Observation(obs4Date, value4, sensor_id, unit_id);
		ou.insertObservation(obs4);
		
		Assert.assertEquals(obs2Date, findFOT(unit_id, sensor_id));
		Assert.assertEquals(obs4Date, findLOT(unit_id, sensor_id));
		//insert observation in middle
		Observation obs3 = new Observation(obs3Date, value3, sensor_id, unit_id);
		ou.insertObservation(obs3);
		
		Assert.assertEquals(obs2Date, findFOT(unit_id, sensor_id));
		Assert.assertEquals(obs4Date, findLOT(unit_id, sensor_id));
		//insert oldest observation
		Observation obs1 = new Observation(obs1Date, value1, sensor_id, unit_id);
		ou.insertObservation(obs1);
		Assert.assertEquals(obs1Date, findFOT(unit_id, sensor_id));
		Assert.assertEquals(obs4Date, findLOT(unit_id, sensor_id));
		
		//ut.deleteUnit(unit_id);
	}
	
	/*private boolean insertObsSQL(String timeString, double observedValue, int sensorId, long unitId) throws SQLException{
		String ins = "INSERT INTO observations(time_stamp, observed_value, sensor_id, unit_id) VALUES (" +
		"'"+timeString+"', " +observedValue+", "+sensorId+","+unitId+");";
		int i = SQLExecutor.executeUpdate(ins);
		if (i==1){
			return false;
		}
		return true;
	}*/
	private Date findFOT(long unitId, long sensorId) throws SQLException, NoItemFoundException{
		String query = "SELECT first_obs FROM units_to_sensors WHERE unit_id = "+unitId+" AND sensor_id = "+sensorId+";";
		ResultSet res = SQLExecutor.getInstance().executeQuery(query);
		if(res.next() == true){
			Date dateFOT = new Date(res.getTimestamp("first_obs").getTime());
			boolean wasNull = res.wasNull();
			if (wasNull == false){
				return dateFOT;
			}
			else{
				throw new NoItemFoundException("First observation timestamp not found!");
			}
		}
		else{
			throw new NoItemFoundException("First observation timestamp not found!");
		}		
	}
	private Date findLOT(long unitId, long sensorId) throws SQLException, NoItemFoundException{
		String query = "SELECT last_obs FROM units_to_sensors WHERE unit_id = "+unitId+" AND sensor_id = "+sensorId+";";
		ResultSet res = SQLExecutor.getInstance().executeQuery(query);
		if(res.next() == true){
			Date dateLOT = new Date(res.getTimestamp("last_obs").getTime());
			boolean wasNull = res.wasNull();
			if (wasNull == false){
				return dateLOT;
			}
			else{
				throw new NoItemFoundException("Last Observation timestamp not found!");
			}
		}
		else{
			throw new NoItemFoundException("Last Observation timestamp not found!");
		}			
	}
}
