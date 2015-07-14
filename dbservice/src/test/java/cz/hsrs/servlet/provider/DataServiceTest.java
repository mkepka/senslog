package cz.hsrs.servlet.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.util.ajax.JSON;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.AlertUtil;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.UnitUtil;
import cz.hsrs.main.Start;

public class DataServiceTest {
	
	
	private static double lat = 10;
	private static double lon = 14;
	private static long unit_id = 111;
	private static long sensor1_id = 111;
	private static long sensor2_id = 112;
	private static long sensor_ign = 330040000;
	private static int group_id;
	private static int holder_id;
	private static String user_name= "jiri";
	private static String fname= "jiricek";
	private static int driver_1;
	private static String dr1_fname = "Lada";
	private static String dr1_lname = "Novak";
	//private static Date date = new Date();
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");		
	// public static Server server = new Server();
	private static int alert_id;
	private static String descA1 = "Pokusny Alert - MiKe";
	private AlertUtil aUtil;

	@BeforeClass
	public static void setUp() throws ParseException, Exception  {
		try {
			Start.start();

			//WiaSession ses = WiaSession.get();
			 
			DBHelper.setConnection();		
			DatabaseFeedOperation.insertPosition(unit_id, lat, lon, 0,  format.parse("2001-07-15 10:00"),28);
			DatabaseFeedOperation.insertPosition(unit_id, lat+1, lon, 0,  format.parse("2001-07-15 10:05"),28);
			
			//pridame skupiny
			String getgroupId = "SELECT nextval('groups_id_seq'::regclass)";
			ResultSet res;
			
				res = SQLExecutor.getInstance().executeQuery(getgroupId);
			
			res.next();
			group_id = res.getInt(1);
			String iGroup="INSERT INTO groups(id, group_name) VALUES ("+group_id +",'testgroup')";
			SQLExecutor.executeUpdate(iGroup);
			// PRidame uzivatel
			String iUser="INSERT INTO system_users(user_name, group_id) VALUES ('"+user_name+"',"+group_id+");"; 
			SQLExecutor.executeUpdate(iUser);
			//pridame jednotky do skupiny - update units_to_groups!!!				
			/*String iUnit2Group = "INSERT INTO units_to_groups(group_id, unit_id) VALUES("+group_id+","+unit_id+");";
			stmt.execute(iUnit2Group);*/
			String uUnit2Group = "UPDATE units_to_groups SET group_id = "+group_id+" WHERE group_id = 1 AND unit_id = "+unit_id+" ;";
			SQLExecutor.executeUpdate(uUnit2Group);
			
			DatabaseFeedOperation.insertObservation(format.parse("2001-07-15 10:01"), unit_id, sensor1_id, 20);
			DatabaseFeedOperation.insertObservation(format.parse("2001-07-15 10:01"), unit_id, sensor2_id, 20);
			//insert ignition status
			//DatabaseFeedOperation.insertObservation(format.parse("2001-07-15 10:01"), unit_id, sensor_ign, 1);

			//pridame holdera
			String hodlerId = "SELECT nextval('users_user_id_seq'::regclass)";
			ResultSet resholder = SQLExecutor.getInstance().executeQuery(hodlerId);
			resholder.next();
			holder_id = resholder.getInt(1);
			String iHolder="INSERT INTO unit_holders(holder_id, holder_name, phone, icon_id, address, email, www) VALUES(" +
					+holder_id +",'"+fname+"', 123, 123, 'adresa', 'email.cz', 'www')";
			SQLExecutor.executeUpdate(iHolder);
			
			//pridame jednotky do holderovi					
			String iUnit2Holder = "UPDATE units SET holder_id = "+holder_id+" WHERE unit_id = "+unit_id;
			SQLExecutor.executeUpdate(iUnit2Holder);
			
			//driver
			String driverId = "SELECT nextval('units_to_drivers_id_seq'::regclass)";
			ResultSet res2 = SQLExecutor.getInstance().executeQuery(driverId);
			res2.next();
			driver_1 = res2.getInt(1);			
			String iDriver1= "INSERT INTO unit_drivers (driver_id, holder_id, fname, lname) VALUES ("+driver_1+", "+holder_id+", '"+dr1_fname+ "', '"+dr1_lname+"');";
			SQLExecutor.executeUpdate(iDriver1);			
			String iDriverToUnit= "INSERT INTO units_to_drivers (unit_id, driver_id) " +
								  "VALUES("+unit_id+","+driver_1+")";
			SQLExecutor.executeUpdate(iDriverToUnit);
		
			//pridame alert a alertEvent pro unit_id, event nevyresen -> je ve vypisu
			String getAlertId = "SELECT nextval('alerts_alert_id_seq'::regclass)";
			res = SQLExecutor.getInstance().executeQuery(getAlertId);
			res.next();
			alert_id = res.getInt(1);		
			DatabaseFeedOperation.insertAlertEvent(format.parse("2001-07-15 10:01"), unit_id, alert_id);
			//aUtil.solveAlertEvent("2001-07-15 10:01", unit_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Database problem - try to run one more time");
			e.printStackTrace();
			tearDown();
		}
				
	}

	@Test
	public void testGetPositions() throws Exception {
		URL yahoo = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_POSITIONS +
				"&user="+user_name+"&limit=1");
		simpleJsonTester(yahoo.openStream());
	}

	@Test
	public void testGetLastPositions() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_LAST_POSTION +
				"&user="+user_name);
		simpleJsonTester(result.openStream());
	}
	
	@Test
	public void testGetTrack() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_TRACK +
				"&user="+user_name+"&limit=2");
		simpleJsonTester(result.openStream());
	}
	
	@Test
	public void testGetRecentTrack() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_RECENT_TRACK +
				"&user="+user_name);
		simpleJsonTester(result.openStream());
	}
	
	@Test
	public void testUnits() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_UNITS +
				"&user="+user_name);
		simpleUnitJsonTesterLastPos(result.openStream());
	}
	
	@Test
	public void testLastPositionWithStatus() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_LAST_POSTION_WITH_STATUS +
				"&user="+user_name);
		simpleUnitJsonTester(result.openStream());
	}
	
	@Test
	public void testUnitsWithID() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"DataService?Operation=" +
				DataService.GET_UNITS +
				"&user="+user_name+"&unit_id="+unit_id);
		simpleUnitJsonTesterLastPos(result.openStream());
	}

	private void simpleJsonTester(InputStream stream) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				System.out.println(inputLine);
				result = result + inputLine;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		Object[] res = (Object[]) JSON.parse(result);
		Map m = null;
		for (int i = 0; i < res.length; i++) {	
			Map resMap = (Map)res[i];
			if  ( (Long)((Map)res[i]).get("unit_id") == unit_id){
				 m = ((Map) res[i]);				
			}					
		}
		Assert.assertNotNull(m);		
	}
	
	
	private void simpleUnitJsonTesterLastPos(InputStream stream) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				System.out.println("LastPosTest:");
				System.out.println(inputLine);
				result = result + inputLine;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		Object[] res = (Object[]) JSON.parse(result);
		Map m = null;
		for (int i = 0; i < res.length; i++) {	
			Map resMap = (Map)res[i];
			if  (  (Long)((Map)((Map)resMap.get("lastpos")).get("position")).get("unit_id") == unit_id){
				 m = ((Map) res[i]);				
			}			
		}
		Assert.assertNotNull(m);		
	}
	private void simpleUnitJsonTester(InputStream stream) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				System.out.println(inputLine);
				result = result + inputLine;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		Object[] res = (Object[]) JSON.parse(result);
		Map m = null;
		for (int i = 0; i < res.length; i++) {	
			Map resMap = (Map)res[i];
			if  (  (Long)((Map)resMap.get("position")).get("unit_id") == unit_id){
				 m = ((Map) res[i]);				
			}			
		}
		Assert.assertNotNull(m);		
	}

	@AfterClass
	public static void tearDown() throws Exception {		
		UnitUtil util = new UnitUtil();
		util.deleteUnit(unit_id);	
		util.deleteHolder(fname);			
		
		String delDriver = "DELETE FROM unit_drivers WHERE fname= '"+ dr1_fname + "' AND lname = '"+dr1_lname+"'" ;
		SQLExecutor.executeUpdate(delDriver);
		String delU = "DELETE FROM system_users WHERE user_name= '"+user_name+"'";
		String delA = "DELETE FROM alerts WHERE alert_id="+alert_id;
		SQLExecutor.executeUpdate(delU);
		SQLExecutor.executeUpdate(delA);
		String delGroup = "DELETE FROM groups WHERE id= "+group_id;
		SQLExecutor.executeUpdate(delGroup);
		Start.stop();
				
	}
}
