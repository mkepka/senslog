package cz.hsrs.track;

import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;

public class TrackSolverTest {

	static long unitid = 112;

	static UnitUtil utils;
	static TrackUtil trackUtils;

	// GregorianCalendar cal = new GregorianCalendar();
	
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	static Date d0A;
	static Date d0B;
	static Date d1A;
	static Date d1B;
	static Date d1C;
	static Date d1D;
	static Date d2A;
	static Date d2B;
	static Date d1E;
	static Date d1F;

	static UnitPosition pos0A;
	static UnitPosition pos0B;

	static UnitPosition pos1A;
	static UnitPosition pos1B;
	static UnitPosition pos1C;
	static UnitPosition pos1D;
	static UnitPosition pos1E;
	static UnitPosition pos1F;

	static UnitPosition pos2A;
	static UnitPosition pos2B;

	@BeforeClass
	public static void set() {
		DBHelper.setConnection();
	}

	@Before
	public void genData() throws Exception {

		d0A = format.parse("2001-07-15 10:00");
		d0B = format.parse("2001-07-15 10:10");

		d1A = format.parse("2001-07-16 10:00");
		d1B = format.parse("2001-07-16 10:05");
		d1C = format.parse("2001-07-16 10:10");
		d1D = format.parse("2001-07-16 10:15");
		d1E = format.parse("2001-07-16 10:21");
		d1F = format.parse("2001-07-16 10:22");

		d2A = format.parse("2001-07-17 10:00");
		d2B = format.parse("2001-07-17 10:10");
		pos0A = new UnitPosition(unitid, 14.0, 15.0, d0A);
		pos0B = new UnitPosition(unitid, 15, 15, d0B);

		pos1A = new UnitPosition(unitid, 14, 15, d1A);
		pos1B = new UnitPosition(unitid, 14, 16, d1B);
		pos1C = new UnitPosition(unitid, 14, 17, d1C);
		pos1D = new UnitPosition(unitid, 14, 18, d1D);
		pos1E = new UnitPosition(unitid, 14, 19, d1E);
		pos1F = new UnitPosition(unitid, 14, 20, d1F);

		pos2A = new UnitPosition(unitid, 18, 15, d2A);
		pos2B = new UnitPosition(unitid, 19, 15, d2B);
		
		 setUnit();
	}

	private void setUnit() throws Exception {
		    String insert = "INSERT into units(unit_id) VALUES ("+unitid+")";
			String sql = "UPDATE units_conf SET max_time_span='00:15:00'::interval WHERE unit_id = "
					+ unitid;		
			SQLExecutor.executeUpdate(insert);
			SQLExecutor.executeUpdate(sql);
	
	}

	@Test
	public void testAsynchroTrack()  throws Exception {
		solve(pos1A);				
		solve(pos1C);
		solve(pos1B);
		solve(pos1D);
		

	}
 
	@Test
	public void testCreteNewAndInsertTrack() throws Exception {

		// Manually insert position
		solve(pos1A);				
		solve(pos1C);
		solve(pos1D);
		solve(pos1B);

	
		TrackUtil util = new TrackUtil();
		try {
			TrackData t1 = util
					.getTrack(unitid, pos1A.internalGetTime_stamp());
			TrackData t2 = util
					.getTrack(unitid, pos1B.internalGetTime_stamp());
			TrackData t3 = util
					.getTrack(unitid, pos1C.internalGetTime_stamp());
			TrackData t4 = util
					.getTrack(unitid, pos1D.internalGetTime_stamp());
			Assert.assertEquals(t1.getGid(), t2.getGid());
			Assert.assertEquals(t3.getGid(), t4.getGid());
		} catch (NoItemFoundException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testCreteNewAndOldTrack() throws Exception {

		// Manually insert position
		solve(pos1A);
		solve(pos1B);

		solve(pos0A);
		solve(pos0B);

		TrackUtil util = new TrackUtil();
		try {
			TrackData t0A = util.getTrack(unitid, pos0A
					.internalGetTime_stamp());
			TrackData t0B = util.getTrack(unitid, pos0B
					.internalGetTime_stamp());
			TrackData t1A = util.getTrack(unitid, pos1A
					.internalGetTime_stamp());
			TrackData t1B = util.getTrack(unitid, pos1B
					.internalGetTime_stamp());

			Assert.assertEquals(t0A.getGid(), t0B.getGid());
			Assert.assertEquals(t1A.getGid(), t1B.getGid());

			Assert.assertNotSame(t0A.getGid(), t1A.getGid());
		} catch (NoItemFoundException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void solve(UnitPosition p) throws Exception {
		// p.insertToDb(ConnectionManager.getConnection());
		TrackSolver solver = new TrackSolver(p);
		solver.solve();
	}

	@After
	public void delData() throws Exception {
		// DBHelper.setConnection();
		UnitUtil util = new UnitUtil();
		util.deleteUnit(unitid);
	}

	@Test
	public void testJoin() throws Exception {
		TrackUtil tu = new TrackUtil();
		solve(pos1A);
		solve(pos1B);

		solve(pos1E);
		solve(pos1F);

		solve(pos1C);
		solve(pos1D);

		TrackData td = tu.getTrack(unitid, pos1D.internalGetTime_stamp());
		int l = tu.getTrackLenght(td.getGid());
		Assert.assertEquals(6, l);
	}
}
