package cz.hsrs.db.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.TrackData;

public class TrackUtilTest {

	static long unitid = 111;

	static UnitUtil utils;
	static TrackUtil trackUtils;
	static String start = "2001-07-15 01:00+0100";
	static String end = "2001-07-15 03:00+0100";
	static String pointbefore = "2001-07-14 01:00+0100";
	static String pointafter = "2001-07-16 03:00+0100";
	
	/** track before */
	static String startb = "2001-07-15 00:00+0100";
	static String endb = "2001-07-15 00:15+0100";

	/** track after */
	static String starta = "2001-07-15 5:00+0100";
	static String enda = "2001-07-15 5:15+0100";

	static String dateIn = "2001-07-15 02:00+0100";
	static String dateBefore = "2001-07-15 00:30+0100";
	static String dateAfter = "2001-07-15 03:30+0100";
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mmZ");

	@BeforeClass
	public static void genData() {
		DBHelper.setConnection();
		try {

			
			// make testing unit
			utils = new UnitUtil();
			trackUtils = new TrackUtil();
			utils.insertUnit(unitid, "Test unit");

			DatabaseFeedOperation.insertPosition(unitid, 15, 50, 0, format.parse(startb));
			DatabaseFeedOperation.insertPosition(unitid, 16, 50, 0, format.parse(endb));
			DatabaseFeedOperation.insertPosition(unitid, 16, 50, 0, format.parse(dateIn));
			
			DatabaseFeedOperation.insertPosition(unitid, 13, 50, 0, format.parse(pointbefore));
			DatabaseFeedOperation.insertPosition(unitid, 12, 50, 0, format.parse(pointafter));				
			
			DatabaseFeedOperation.insertPosition(unitid, 17, 50, 0, format.parse(start));
			DatabaseFeedOperation.insertPosition(unitid, 18, 50, 0, format.parse(end));						
			
			DatabaseFeedOperation.insertPosition(unitid, 19, 50, 0, format.parse(starta));
			DatabaseFeedOperation.insertPosition(unitid, 20, 50, 0, format.parse(enda));					
	
								
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}

	@AfterClass
	public static void delData() throws Exception {
		utils.deleteUnit(unitid);
	}

	@Test
	public void testSQLGetTrack() throws Exception {

		Assert.assertNotNull(trackUtils.getTrack(unitid, format.parse(dateIn)));
	}

	@Test
	public void testGetNewerTrack() throws Exception {	
		Date res = trackUtils.getNewerTrack(unitid, format.parse(dateBefore)).getStart();
		Assert.assertEquals(format.parse(start), res);

	}

	@Test
	public void testGetOlderTrack() throws Exception {		
		Date res = trackUtils.getOlderTrack(unitid, format.parse(dateAfter)).getEnd();
		Assert.assertEquals(format.parse(end), res);

	}
	
	@Test
	public void testAddToTrack() throws Exception {		
		TrackData track = trackUtils.getOlderTrack(unitid, format.parse(dateAfter));
		//UnitPosition p = new UnitPosition(unitid, 123456, 14, 15, dateIn, 1);
		Assert.assertTrue(trackUtils.addToTrack(track, trackUtils.getPositionByTime(unitid, format.parse(startb))));
		Assert.assertTrue(trackUtils.addToTrack(track, trackUtils.getPositionByTime(unitid, format.parse(endb))));
		
		//Assert.assertEquals(format.parse(end), res);

	}
	

	
	

}
