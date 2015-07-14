package cz.hsrs.db.util;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.NoItemFoundException;

public class TriggerTest {

	public static int unit_id = 111;
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");	
	static double lat =50;
	static double lon = 14;
	@Test
	public void testConsiderInsertPosition() throws Exception {
		
		DBHelper.setConnection();
		TrackUtil util = new TrackUtil();
		
		Date d1 =  format.parse("2001-07-15 10:00");
		DatabaseFeedOperation.insertPosition(unit_id, 14, 50, 0, d1 ,20);
		Assert.assertTrue(testExistDate(d1));	
		
		DatabaseFeedOperation.insertPosition(unit_id, lat+0.01, lon, 0,  format.parse("2001-07-15 10:05"),0);
		Assert.assertTrue(testExistDate(format.parse("2001-07-15 10:05")));	
		DatabaseFeedOperation.insertPosition(unit_id, lat+0.01, lon, 0,  format.parse("2001-07-15 10:10"),0);
		DatabaseFeedOperation.insertPosition(unit_id, lat+0.01, lon, 0,  format.parse("2001-07-15 10:15"),1);
		DatabaseFeedOperation.insertPosition(unit_id, lat+0.01, lon, 0,  format.parse("2001-07-15 10:25"),0);
		DatabaseFeedOperation.insertPosition(unit_id, lat+0.01, lon, 0,  format.parse("2001-07-15 10:30"),5);
		
		Assert.assertTrue(testExistDate(format.parse("2001-07-15 10:25")));	
		Assert.assertFalse(testExistDate(format.parse("2001-07-15 10:15")));	
		Assert.assertTrue(testExistDate(format.parse("2001-07-15 10:30")));	
		
		
	}
	private boolean testExistDate(Date d) throws SQLException, ParseException{
		TrackUtil util = new TrackUtil();
		try {
			util.getPositionByTime(unit_id, d);
			return true;
			} catch (NoItemFoundException e) {
				return false;
			}
	}
	@After
	public void delUnit() throws Exception {
		UnitUtil utilU = new UnitUtil();
		utilU.deleteUnit(unit_id);
	}
	
}
