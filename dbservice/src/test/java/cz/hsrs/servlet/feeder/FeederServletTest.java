package cz.hsrs.servlet.feeder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.composite.ObservationValue;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.SensorUtil;
import cz.hsrs.db.util.UnitUtil;
import cz.hsrs.main.Start;
import cz.hsrs.servlet.provider.ServletTestHelper;

public class FeederServletTest {

	private static long unit_id = 111;
	private static long sensor_id = 111;
	private static Date date = new Date();
	private static String descA1 = "Pokusny Alert - MiKe";
	private static int alert_1;

	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	@BeforeClass
	public static void setUp() throws Exception {
		DBHelper.setConnection();		
		Start.start();
		
		//DatabaseFeedOperation.insertPosition(unit_id, 15, 50, 0,  date);
		
		// insert test alert		
		String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertId);
		res.next();
		alert_1 = res.getInt(1);
		String insAlert = "INSERT into alerts(alert_id, alert_description) values ("+ alert_1 + ",'" + descA1 + "');";
		SQLExecutor.executeUpdate(insAlert);		
	}
	
	@Test
	public void testInsertPosition() throws Exception {
	
		Date time = new Date();// "2008-01-02 12:00:00");
		URL result = new URL(
				ServletTestHelper.APP_URL+"FeederServlet?" +
				ServiceParameters.OPERATION + "=" +
				ServiceParameters.INSERT_POSITION +"&" +
				ServiceParameters.LAT+"=" + 15 +"&" +
				ServiceParameters.LON+"=" + 50 +"&" +
				ServiceParameters.ALT+"=" + 0 +"&" +
				ServiceParameters.DOP+"=" + 0 +"&" +
				ServiceParameters.DATE+"=2008-01-02+12%3A00%3A00&" +
				ServiceParameters.UNIT_ID+ "=" +unit_id +"&" +
				ServiceParameters.SPEED+ "=" + "15");
				
		readResultTester("true", result.openStream());	
		UnitUtil util = new UnitUtil();
		Assert.assertEquals("POINT(50.0 15.0)",util.getLastUnitPosition(unit_id).internalGetGeom());
	}
	
	@Test
	public void testInsertObservation() throws Exception {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = new Date();// "2008-01-02 12:00:00");
		URL result = new URL(
				ServletTestHelper.APP_URL+"FeederServlet?" +
				ServiceParameters.OPERATION + "=" +
				ServiceParameters.INSERT_OBSERVATION +"&" +				
				ServiceParameters.DATE+"=2011-06-02+12%3A00%3A00%2B0100&" +
				ServiceParameters.UNIT_ID+ "=" +unit_id +"&" +
				ServiceParameters.SENSOR_ID + "=" +sensor_id +"&" +
				ServiceParameters.VALUE+ "=" + "15");
				
		readResultTester("true", result.openStream());	
		SensorUtil util = new SensorUtil();
		List<ObservationValue> obs = util.getSensorObservations(unit_id, sensor_id,
				"2011-06-02 11:59:00+01","2011-06-02 12:01:00+01");
		Assert.assertEquals(15.0, obs.get(0).getValue());
	
	}

	
	
	private void readResultTester(String equals, InputStream stream) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				System.out.println(inputLine);
				Assert.assertEquals(equals,inputLine);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}		
	}
	@Test
	public void testInsAlertEvent() throws Exception {
		URL event1 = new URL(
				ServletTestHelper.APP_URL+"FeederServlet?" +
				ServiceParameters.OPERATION + "=" +
				ServiceParameters.INSERT_ALERT_EVENT +"&" +
				ServiceParameters.DATE+"=2008-01-02+12%3A00%3A00%2B0100&" +
				ServiceParameters.UNIT_ID+ "=" +unit_id +"&" +
				ServiceParameters.ALERT_ID+ "=" + alert_1);
		String time1 = "2008-01-02 12:00:00+0100";
		
		readResultTester("true", event1.openStream());		
		String getAlertEvent = "SELECT alert_id, unit_id, time_stamp FROM alert_events WHERE unit_id="+unit_id+";";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertEvent);
		res.next();
		int ins_alert_id = res.getInt("alert_id");
		long ins_unit_id = res.getLong("unit_id");
		String ins_time = res.getString("time_stamp")+"00";
		
		Assert.assertEquals(unit_id,ins_unit_id);
		Assert.assertEquals(alert_1,ins_alert_id);
		Assert.assertEquals(formater.parse(time1),formater.parse(ins_time));
		
		URL event2 = new URL(
				ServletTestHelper.APP_URL+"FeederServlet?" +
				ServiceParameters.OPERATION + "=" +
				ServiceParameters.INSERT_ALERT_EVENT +"&" +
				ServiceParameters.DATE+"=2008-01-03+12%3A00%3A00&" +
				ServiceParameters.UNIT_ID+ "=" +unit_id +"&" +
				ServiceParameters.ALERT_ID+ "=" + alert_1);
		readResultTester("false", event2.openStream());
		String time2 = "2008-01-03 12:00:00+01";
		
		res = SQLExecutor.getInstance().executeQuery(getAlertEvent);
		res.next();
		ins_alert_id = res.getInt("alert_id");
		ins_unit_id = res.getLong("unit_id");
		ins_time = res.getString("time_stamp");
		
		Assert.assertEquals(unit_id,ins_unit_id);
		Assert.assertEquals(alert_1,ins_alert_id);
		Assert.assertNotSame(time2, ins_time);		
	}
	@Test
	public void testInsSolvingAlertEvent() throws Exception{
		//insert new event		
		String getAlertEventId = "SELECT nextval('alert_events_alert_event_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertEventId);
		res.next();
		int event_1 = res.getInt(1);
		
		String time = "2001-08-01 09:00";
		String insertEvent = " INSERT INTO alert_events (alert_event_id, time_stamp, unit_id, alert_id) VALUES ("
			+ event_1 +", '" + time + "', "+ unit_id + ", '"+ alert_1 + "');";
		SQLExecutor.executeUpdate(insertEvent);
		
		URL solvingEvent = new URL(
				ServletTestHelper.APP_URL+"FeederServlet?" +
				ServiceParameters.OPERATION + "=" +
				ServiceParameters.SOLVING_ALERT_EVENT +"&" +
				ServiceParameters.ALERT_EVENT_ID+ "=" + event_1);

		readResultTester("true", solvingEvent.openStream());
		
		String getAlertEvent = "SELECT alert_id, unit_id, solving FROM alert_events WHERE alert_event_id="+event_1+";";
		res = SQLExecutor.getInstance().executeQuery(getAlertEvent);
		res.next();
		int ins_alert_id = res.getInt("alert_id");
		long ins_unit_id = res.getLong("unit_id");
		boolean ins_solving = res.getBoolean("solving");
		
		Assert.assertEquals(unit_id,ins_unit_id);
		Assert.assertEquals(alert_1,ins_alert_id);
		Assert.assertTrue(ins_solving);		
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		UnitUtil util = new UnitUtil();
		util.deleteUnit(unit_id);
		
		//delete alert
		String delete_alert = "DELETE FROM alerts WHERE alert_id = " + alert_1 + ";";
		SQLExecutor.executeUpdate(delete_alert);
		
		//delete alert
		delete_alert = "DELETE FROM alerts WHERE alert_description = '" + descA1 + "';";
		SQLExecutor.executeUpdate(delete_alert);
		Start.stop();
		
	}
}
