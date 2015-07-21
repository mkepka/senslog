package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.Unit;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.LastPosition;
import cz.hsrs.db.model.composite.RealUnit;
import cz.hsrs.db.pool.SQLExecutor;

public class UserUtilTest {
	static UserUtil util;

	private static double lat = 10;
	private static double lon = 14;
	private static long unit_id = 111;
	private static long sensor_ign = 330040000;
	private static int group_id;
	private static String user_name = "testUser";
	static String userPass = "testPass";
	private static String userLang = "cz"; 
	// private static Date date = new Date();
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mmZ");

	@BeforeClass
	public static void setUp() throws Exception {
		DBHelper.setConnection();
		util = new UserUtil();
		//util.insertUser(user_name, userPass);
				
		DatabaseFeedOperation.insertPosition(unit_id, lat, lon, 0, format
				.parse("2001-07-15 10:00+0100"));
		DatabaseFeedOperation.insertPosition(unit_id, lat + 1, lon, 0, format
				.parse("2001-07-15 10:05+0100"));
		//insert ignition status
		//DatabaseFeedOperation.insertObservation(format.parse("2001-07-15 10:01"), unit_id, sensor_ign, 1);
	
		// pridame skupiny
		String getgroupId = "SELECT nextval('groups_id_seq'::regclass)";
		ResultSet res = SQLExecutor.getInstance().executeQuery(getgroupId);
		res.next();
		group_id = res.getInt(1);
		String iGroup = "INSERT INTO groups(id, group_name) VALUES ("
				+ group_id + ",'testgroup')";
		SQLExecutor.executeUpdate(iGroup);
		// PRidame uzivatel
		String iUser = "INSERT INTO system_users(user_name, user_password, group_id, lang) VALUES ('"
				+ user_name + "','"+userPass+"'," + group_id + ", '"+userLang+"');";
		SQLExecutor.executeUpdate(iUser);
		// pridame jednotky do skupiny - update units_to_groups!!!
		/*String iUnit2Group = "INSERT INTO units_to_groups(group_id, unit_id) VALUES("
				+ group_id + "," + unit_id + ");";
		stmt.execute(iUnit2Group);*/
		String uUnit2Group = "UPDATE units_to_groups SET group_id = "+group_id+" WHERE group_id = 1 AND unit_id = "+unit_id+" ;";
		SQLExecutor.executeUpdate(uUnit2Group);
	}

	@Test
	public void testGetPwd() throws Exception {
		Assert.assertEquals(userPass, util.getUserPassword(user_name));

	}

	@Test
	public void testLastPos() throws Exception {
		List<UnitPosition> pos = util.getLastPositionsByUserName(user_name);
		Assert.assertEquals(format.parse("2001-07-15 10:05+0100"), pos.get(0)
				.internalGetTime_stamp());

	}
	
	//Test
	public void testRights() throws Exception {
		String role = util.getRole(user_name);
	    System.out.println(role);

	}
	
	
	@Test
	public void testGetUnit() throws Exception {
		ResultSet res = util.getLastPositionsByUserNameRes(user_name, unit_id);
		res.next();
		RealUnit unit = new RealUnit(res);
		Assert.assertNotNull(unit);

	}
	
	@Test
	public void testSession() throws Exception {
		String session_id = "sf456sdf";
		Assert.assertEquals(1,util.setUserSession(user_name, session_id, "12.101.153.5"));
		Assert.assertEquals(1,util.delUserSession(session_id));		
	}
	
	@Test
	public void testPositionWithStatus() throws Exception {
		DatabaseFeedOperation.insertPosition(unit_id, lat + 1, lon, 0, (new Date(new Date().getTime()-100)));
	
		LastPosition lp = util.getLastPositionWithStatus(user_name).get(0);
		Assert.assertNotNull(lp);
		Assert.assertNotNull(lp.getAttributes().get(LastPosition.IS_RUNNING));
		//System.out.print(lp.getAttributes().get(LastPosition.IS_RUNNING));
		//Assert.assertEquals("true",lp.getAttributes().get(LastPosition.IS_RUNNING).toString());
	}

	@Test
	public void testGetUnitsByUser() throws Exception{
		List<Unit> units = util.getUnitsByUser(user_name);
		Assert.assertFalse(units.isEmpty());
	}
	@Test
	public void testGetUserLanguage() throws Exception{
		String select = "SELECT lang FROM system_users WHERE user_name = '"+user_name+"'";
		ResultSet res = SQLExecutor.getInstance().executeQuery(select);
		if (res.next()) {
			Assert.assertEquals(userLang, res.getString(1));
		}
		else{
			Assert.fail();
		}
	}
	@Test
	public void testSetUserLanguage() throws Exception{
		String newLang = "en";
		util.setUserLanguage(user_name, newLang);
		String select = "SELECT lang FROM system_users WHERE user_name = '"+user_name+"'";
		ResultSet res = SQLExecutor.getInstance().executeQuery(select);
		if (res.next()) {
			Assert.assertEquals(newLang, res.getString(1));
		}
		else{
			Assert.fail();
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		util.deleteUser(user_name);
		String delGroup = "DELETE FROM groups WHERE id= "+group_id;
		SQLExecutor.executeUpdate(delGroup);		
		UnitUtil ut = new UnitUtil();
		ut.deleteUnit(unit_id);
	}

}
