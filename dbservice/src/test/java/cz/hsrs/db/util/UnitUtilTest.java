package cz.hsrs.db.util;

//import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.IgnitionStatus;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.UnitDriver;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.LastPosition;
import cz.hsrs.db.model.custom.DBItemInfo;
import cz.hsrs.db.pool.SQLExecutor;

public class UnitUtilTest {
	
	static UnitUtil util;
	static TrackUtil utilTrack;
	
	private static double lat = 10;
	private static double lon = 14;
	private static long unit_id = 111;
	private static int alert_id = -111;
	private static long sensor1_id = 111;
	private static long sensor2_id = 112;
	private static int holder_id;
	private static int driver_1;
	private static String dr1_fname = "Lada";
	private static String dr1_lname = "Novakxx";
	private static int driver_2;
	private static String dr2_fname = "Ivan";
	private static String dr2_lname = "Hroznyxx";	
	private static int comp_id;
	private static String user_name= "testUser";
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");
	private static AlertUtil au;
	
	private static SQLExecutor stmt ;
	
	@BeforeClass
	public static void  setUp() throws Exception {
		 DBHelper.setConnection();
		 stmt = SQLExecutor.getInstance();
			util = new UnitUtil();
			au = new AlertUtil();		
		 
			//WiaSession ses = WiaSession.get();
		 
		DBHelper.setConnection();
		//Connection con = ConnectionManager.getConnection();
	// = con.createStatement();
		DatabaseFeedOperation.insertPosition(unit_id, lat, lon, 0,  format.parse("2001-07-15 10:00+0100"));
		DatabaseFeedOperation.insertPosition(unit_id, lat+1, lon, 0,  format.parse("2001-07-15 10:05+0100"));
			
			//pridame holdera
			String hodlerId = "SELECT nextval('users_user_id_seq'::regclass)";
			ResultSet res = stmt.executeQuery(hodlerId);
			res.next();
			holder_id = res.getInt(1);
			String iHolder="INSERT INTO unit_holders(holder_id, holder_name, phone,icon_id) VALUES(" +
					+holder_id +",'testholder', 123,123)";
			stmt.executeUpdate(iHolder);
			
			//pridame company
		/*	String comId = "SELECT nextval('company_company_id_seq'::regclass)";
			ResultSet res1 = stmt.executeQuery(comId);
			res1.next();
			comp_id = res1.getInt(1);
			String iComp= "INSERT INTO company(company_id, www) VALUES("+comp_id+", 'www')";
			stmt.execute(iComp);*/
			
			//pridame drivery
			//driver 1
			String driverId = "SELECT nextval('units_to_drivers_id_seq'::regclass)";
			ResultSet res2 = stmt.executeQuery(driverId);
			res2.next();
			driver_1 = res2.getInt(1);			
			String iDriver1= "INSERT INTO unit_drivers (driver_id, holder_id, fname, lname) VALUES ("+driver_1+", "+holder_id+", '"+dr1_fname+ "', '"+dr1_lname+"');";
			stmt.executeUpdate(iDriver1);			
			String iDriverToUnit= "INSERT INTO units_to_drivers (unit_id, driver_id) " +
								  "VALUES("+unit_id+","+driver_1+")";
			stmt.executeUpdate(iDriverToUnit);
			// driver 2
			driverId = "SELECT nextval('units_to_drivers_id_seq'::regclass)";
			res2 = stmt.executeQuery(driverId);
			res2.next();
			driver_2 = res2.getInt(1);
			String iDriver2= "INSERT INTO unit_drivers(driver_id, holder_id, fname, lname) " +
			"VALUES("+driver_2+","+holder_id+", '"+dr2_fname+"', '"+dr2_lname+"');";
			stmt.executeUpdate(iDriver2);
			iDriverToUnit= "INSERT INTO units_to_drivers(unit_id, driver_id) " +
			  "VALUES("+unit_id+","+driver_2+")";
			stmt.executeUpdate(iDriverToUnit);			
			
			//pridame jednotky do holderovi					
			String iUnit2Holder = "UPDATE units SET holder_id = "+holder_id+" WHERE unit_id = "+unit_id;
			stmt.executeUpdate(iUnit2Holder);
			
			DatabaseFeedOperation.insertObservation(format.parse("2001-07-15 10:01+0100"), unit_id, sensor1_id, 20);
			DatabaseFeedOperation.insertObservation(format.parse("2001-07-15 10:01+0100"), unit_id, sensor2_id, 20);
		 
	}
	
	@AfterClass
	public static void delData() throws Exception{
		util.deleteUnit(unit_id);
		util.deleteHolder("testholder");
		util.deleteDriver(dr1_fname, dr1_lname);
		util.deleteDriver(dr2_fname, dr2_lname);
		au.deleteAlert(alert_id);
		//util.deleteCompany(comp_id);
	}
	
	@Test
	public void testLastPositionWithStatus() throws Exception{
		UnitPosition pos1 = new UnitPosition(unit_id, 16, 50,  format.parse("2005-07-15 10:00+0100"));
		pos1.insertToDb();
		
		
		au.insertAlert(alert_id, "test");
		DatabaseFeedOperation.insertAlertEvent(format.parse("2005-07-15 10:00+0100"), unit_id, -111);
		
		LastPosition lp = util.getLastPositionWithStatus(pos1);
		Assert.assertEquals(lp.getAlertEvents().size(), 1);
		
		util.setProvideAlert(unit_id,false);
		lp = util.getLastPositionWithStatus(pos1);
		Assert.assertEquals(lp.getAlertEvents().size(), 0);
		
	}
	
	@Test
	public void testLastPositionWithStatus2() throws Exception{
		int time_spam = util.getUnitConfTimeById(unit_id);
		Date d = new Date(((new Date()).getTime() - (time_spam * 1000))+5000);
		UnitPosition pos1 = new UnitPosition(unit_id, 16, 50,  d);
		pos1.insertToDb();
					
		LastPosition lp = util.getLastPositionWithStatus(util.getLastUnitPosition(unit_id));
		System.out.println(lp.getAttributes().get(lp.IS_RUNNING));		
		
	}
	
	@Test
	public void testUnitHolder() throws Exception {
		Assert.assertEquals(holder_id, util.getUnitHolder(unit_id).getHolder_id());
	}

	//@Test - auto smazano!!!
	public void testUnitInfo() throws Exception {
		DBItemInfo info = util.getUnitInfo("custom", "cars", 3510291104738690l, "unit_id");
		Map map = info.getProperties();	
		//System.out.println(info.getFk());
		System.out.println(info.getProperties());
	}
	
	@Test
	public void testGetDrivers() throws Exception {
		List<UnitDriver> drivers = util.getUnitDrivers(unit_id);
		Iterator<UnitDriver> iterDriver = drivers.iterator();
		Assert.assertTrue(iterDriver.hasNext());
		Assert.assertEquals(driver_1, iterDriver.next().getId());
		Assert.assertEquals(driver_2, iterDriver.next().getId());
	}
	
	@Test
	public void testGetPositionBefore() throws Exception{
	
		UnitPosition pos1 = new UnitPosition(unit_id, 16, 50,  format.parse("2005-07-15 09:55+0100"));
		pos1.insertToDb();
		UnitPosition pos2 = new UnitPosition(unit_id, 17, 50, format.parse("2005-07-15 10:01+0100"));
		pos2.insertToDb();
		
		Assert.assertEquals(pos1.getX(), util.getPositionBefore(unit_id, format.parse("2005-07-15 10:01+0100")).getX());
		
	}
	@Test
	public void testUnitConfTimeById() throws Exception {
		Date date = new Date();
		
		
		int interval = util.getUnitConfTimeById(unit_id);
		Assert.assertNotNull(interval);
		//Assert.assertTrue(util.isRunning(unit_id));
		///Assert.assertEquals(1, utilTrack.deleteRunningTrack(unit_id));
		util.deleteUnit(unit_id);
		//Assert.assertFalse(util.isRunning(unit_id));

	}
	@Test
	public void testGetValidIgnitionStatus() throws SQLException, NoItemFoundException, ParseException{
		String time = "2001-07-15 10:00+0100";
		String insObserv = "INSERT INTO observations(time_stamp, observed_value, sensor_id, unit_id) VALUES ('"+time+"', 1, 330040000, "+unit_id+" );";
		
		stmt.executeUpdate(insObserv);
		IgnitionStatus ignStat = util.getValidIgnitionStatus(unit_id);
		Assert.assertTrue(ignStat.isIgnitionOn());
		Date d = format.parse(time);
		Assert.assertEquals(format.parse(time), ignStat.internalGetTimeStamp());
	}
	@Test 
	public void testGetLastIgnitionStatus() throws SQLException, ParseException, NoItemFoundException{
		String time1 = "2001-07-15 10:05+0100";
		String time2 = "2001-07-15 11:05+0100";
		String insObserv1 = "INSERT INTO observations(time_stamp, observed_value, sensor_id, unit_id) VALUES ('"+time1+"', 1, 330040000, "+unit_id+" );";		
		
		stmt.executeUpdate(insObserv1);
		UnitPosition pos = new UnitPosition(unit_id, 14, 50, format.parse(time2));
		IgnitionStatus last = util.getLastIgnitionStatus(pos);
		Assert.assertTrue(last.isIgnitionOn());		
		Assert.assertEquals(format.parse(time1), last.internalGetTimeStamp());
	}
	@Test 
	public void testGetNextIgnitionStatus() throws SQLException, ParseException, NoItemFoundException{
		String time1 = "2001-07-15 11:05+0100";
		String time2 = "2001-07-15 11:35+0100";
		String insObserv2 = "INSERT INTO observations(time_stamp, observed_value, sensor_id, unit_id) VALUES ('"+time2+"', 1, 330040000, "+unit_id+" );";				
		stmt.executeUpdate(insObserv2);
		UnitPosition pos = new UnitPosition(unit_id, 14, 50, format.parse(time1));
		IgnitionStatus next = util.getNextIgnitionStatus(pos);
		Assert.assertTrue(next.isIgnitionOn());		
		Assert.assertEquals(format.parse(time2), next.internalGetTimeStamp());
	}
}