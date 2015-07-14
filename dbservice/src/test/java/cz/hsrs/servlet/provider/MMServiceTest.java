/**
 * 
 */
package cz.hsrs.servlet.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
public class MMServiceTest {

	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	private static long unit_id = 111;
	private static String descU1 = "testovaci unit - MiKe";
	private static int sensor_1 = 111;
	private static String descS1 = "Pokusny";
	private static int phenomenon_1 = 111;
	private static String descP1 = "Pokusny Phenomenon - MiKe";
	private static String time1 = "2001-07-15 18:00:00+0100"; 
	private static Date date1;
	private static double obs_value = 10;
	private static String user_name= "test";
	
	@BeforeClass
	public static void setUp() {
		try {
		Start.start();
		DBHelper.setConnection();
		
		// insert unit
		String queryConf = "INSERT INTO units(unit_id, description) VALUES ("
			+ unit_id + ",'" + descU1 + "');";
		SQLExecutor.executeUpdate(queryConf);
		/*//insert new phenomenon
		String insPhenomenon = "INSERT INTO phenomenons(phenomenon_id, phenomenon_name, unit) VALUES('"+phenomenon_1+"', '"+descP1+"', 'A');";
		SQLExecutor.executeUpdate(insPhenomenon);
	    //insert new sensor 
		String insSensor = "INSERT INTO sensors(sensor_id, sensor_name, phenomenon_id) VALUES ("+sensor_1 +",'"+descS1+"','"+phenomenon_1+"');";
		SQLExecutor.executeUpdate(insSensor);*/
		//insert new observation
		date1 = format.parse(time1);
		String insObservation = "INSERT INTO observations(time_stamp, observed_value, sensor_id, unit_id) VALUES ('"+date1+"', "+obs_value+", "+sensor_1+", "+unit_id+");";
		SQLExecutor.executeUpdate(insObservation);
		
		} catch (Exception e){
			e.printStackTrace();
		}		
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		UnitUtil util = new UnitUtil();
		util.deleteUnit(unit_id);
		
		Start.stop();
	}
	
	@Test 
	public void testGetObservationsUT() throws Exception {
		String dateBeforeObs = "2001-07-15+02%3A15";
		String dateAfterObs = "2001-07-16+02%3A45";
		URL result = new URL(
				ServletTestHelper.APP_URL+"MMService?Operation=" +
				MMService.GET_OBSERVATIONS +
				"&unit_from=" +
				unit_id +
				"&unit_to=" +
				unit_id +
				"&time_from="+ 
				dateBeforeObs +
				"&time_to="+
				dateAfterObs+
				"&user="+user_name);			
		
		simpleJsonTester(result.openStream(), "unitId", unit_id);
	}
	
	@Test 
	public void testGetObservationsT() throws Exception {
		String dateBeforeObs = "2001-07-15+02%3A15";
		String dateAfterObs = "2001-07-16+02%3A45";
		URL result = new URL(
				ServletTestHelper.APP_URL+"MMService?Operation=" +
				MMService.GET_OBSERVATIONS +
				"&time_from="+ 
				dateBeforeObs +
				"&time_to="+
				dateAfterObs+
				"&user="+user_name);			
		
		simpleJsonTester(result.openStream(), "unitId", unit_id);
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
