/**
 * 
 */
package cz.hsrs.servlet.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.util.ajax.JSON;

import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.UnitUtil;
import cz.hsrs.main.Start;

/**
 * @author mkepka
 *
 */
public class AlertServiceTest {
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	private static long unit_id = 111;
	private static String descU1 = "testovaci unit - MiKe";
	private static int alert_1;
	private static String descA1 = "Pokusny Alert - MiKe";
	private static String user_name= "test";
		
	@BeforeClass
	public static void setUp() {
		try {
		Start.start();
		DBHelper.setConnection();
		
		// insert unit
		String queryConf = "INSERT into units(unit_id, description) values ("
			+ unit_id + ",'" + descU1 + "');";
		SQLExecutor.executeUpdate(queryConf);
	    // select new alert_id
		String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getAlertId);
		res.next();
		alert_1 = res.getInt(1);
		//insert new alert 
		String insAlert = "INSERT into alerts(alert_id, alert_description) values ("+ alert_1 + ",'" + descA1 + "');";
		SQLExecutor.executeUpdate(insAlert);
		//insert new event
		Date time1 = format.parse("2001-07-15 18:00");
		String insEvent = " INSERT INTO alert_events (time_stamp, unit_id, alert_id) VALUES ("
			+ "'" + time1 + "', " + unit_id	+ ",'"+ alert_1 + "');";
		SQLExecutor.executeUpdate(insEvent);
		
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		
	(new UnitUtil()).deleteUnit(unit_id); 		
		//delete alert
		String delete_alert = "DELETE FROM alerts WHERE alert_description = '" + descA1 + "';";
		SQLExecutor.executeUpdate(delete_alert);
		
		Start.stop();
	}
	
	//Test 
	public void testGetAlertEventsByTime() throws Exception {
		String dateBeforeObs = "2001-07-15+02%3A15";
		String dateAfterObs = "2001-07-16+02%3A45";
		URL result = new URL(
				ServletTestHelper.APP_URL+"AlertService?Operation=" +
				AlertService.GET_ALERT_EVENTS_BY_TIME +
				"&unit_id=" +
				unit_id +
				"&from="+ 
				dateBeforeObs +
				"&to="+
				dateAfterObs +
				"&user="+user_name);			
		
		simpleJsonTester(result.openStream(),"solved",false);
	}
	
	//@Test
	public void testGetAlerts() throws Exception{
		URL result = new URL(
				ServletTestHelper.APP_URL+"AlertService?Operation=" +
				AlertService.GET_ALERTS +
				"&unit_id=" +
				unit_id+
				"&user="+user_name);
		simpleJsonTester(result.openStream(),"alertId",alert_1);	
	}
	
	private void simpleJsonTester(InputStream stream, String item, int shouldhave) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				result = result + inputLine;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		Object[] res = (Object[]) JSON.parse(result);
		Map m = null;
		for (int i = 0; i < res.length; i++) {	
			//System.out.println((Map)res[i]);
			if  ( (Long)((Map)res[i]).get(item) == shouldhave){
				 m = ((Map) res[i]);							
			}					
		}
		Assert.assertNotNull(m);		
	}
	private void simpleJsonTester(InputStream stream, String item, Object shouldhave) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				result = result + inputLine;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		//System.out.println(result);
		Object[] res = (Object[]) JSON.parse(result);
		Map m = null;
		for (int i = 0; i < res.length; i++) {	
			//System.out.println((Map)res[i]);
			if  ( ((Map)res[i]).get(item).equals(shouldhave) == true){
				 m = ((Map) res[i]);							
			}					
		}
		Assert.assertNotNull(m);		
	}
}
