package cz.hsrs.servlet.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.util.ajax.JSON;

import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.SensorUtilTest;
import cz.hsrs.main.Start;
import cz.hsrs.servlet.feeder.ServiceParameters;

public class SensorServiceTest {

	String dateBeforeObs = "2001-07-15+00%3A15%3A00%2B0100";
	String dateAfterObs = "2001-07-15+00%3A45%3A00%2B0100";
	@BeforeClass
	public static void setUp() throws Exception {
		Start.start();
		SensorUtilTest.genData();
	}
	@AfterClass
	public static void tearDown() throws Exception {		
		Start.stop();
		DBHelper.setConnection();
		SensorUtilTest.delData();
	}
	@Test
	public void testGetSensors() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"SensorService?Operation=" +
				ServiceParameters.GET_SENSORS +
				"&unit_id=" +
				111);			
		simpleJsonTester(result.openStream(),"sensorId",111);
	}
	@Test
	public void testGetObservations1() throws Exception {
		
	
		URL result = new URL(
				ServletTestHelper.APP_URL+"SensorService?Operation=" +
				ServiceParameters.GET_OBSERVATIONS +
				"&unit_id=" +
				111 + "&sensor_id="+111 );			
		simpleJsonTester(result.openStream(), "value" ,1);		
	}
	
	@Test
	public void testGetObservations2() throws Exception {
		
	
		URL result = new URL(
				ServletTestHelper.APP_URL+"SensorService?Operation=" +
				ServiceParameters.GET_OBSERVATIONS +
				"&unit_id=" +
				111 + "&sensor_id="+111+ "&from="+ dateBeforeObs +"&to="+dateAfterObs  );			
		simpleJsonTester(result.openStream(), "value" ,1);
	}
	
	@Test
	public void testGetObservationsTrunc() throws Exception {
		
	
		URL result = new URL(
				ServletTestHelper.APP_URL+"SensorService?Operation=" +
				ServiceParameters.GET_OBSERVATIONS +
				"&unit_id=" +
				111 + "&sensor_id="+111+ "&from="+ dateBeforeObs +"&to="+dateAfterObs+"&trunc=day" );			
		simpleJsonTester(result.openStream(), "value" ,2);
	}
	
	
	
	private void simpleJsonTester(InputStream stream, String item,int shouldhave) throws Exception {

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
}
