package cz.hsrs.track;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;

public class TrackIgnitionTest {
	
	private static long unit_id = 111;
	
	private static int on = 1;
	private static int off = 0;

	private static TrackUtil tUtil;
	
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	static Date d1, d2, d3, d4, d5, d6, d7;		
	static UnitPosition p1, p2, p3, p4, p5, p6, p7 ;	
	static Observation o1, o2, o3, o4, o5, o6, o7;
		
	static UnitUtil util;

	@BeforeClass
	public static void set() throws SQLException, ParseException {
		DBHelper.setConnection();
	
		//stmt = nectionManager.getnection().createStatement();	
		d1 = format.parse("2001-07-17 10:00");
		d2 = format.parse("2001-07-17 10:02");
		d3 = format.parse("2001-07-17 10:04");
		d4 = format.parse("2001-07-17 10:06");
		d5 = format.parse("2001-07-17 10:08");
		d6 = format.parse("2001-07-17 10:10");
		d7 = format.parse("2001-07-17 10:12");
		
		p1 = new UnitPosition(unit_id, 18.1, 1, d1);
		p2 = new UnitPosition(unit_id, 18.1, 2, d2);
		p3 = new UnitPosition(unit_id, 18.1, 3, d3);
		p4 = new UnitPosition(unit_id, 18.1, 4, d4);
		p5 = new UnitPosition(unit_id, 18.1, 5, d5);
		p6 = new UnitPosition(unit_id, 18.1, 6, d6);
		p7 = new UnitPosition(unit_id, 18.1, 6, d7);
				
		util = new UnitUtil();
	}
	@Test
	public void testAsynchro2() throws Exception {
		TrackUtil u = new TrackUtil();
		d1 = format.parse("2001-08-17 10:00");
		d2 = format.parse("2001-08-17 10:02");
		d3 = format.parse("2001-08-17 10:04");
		d4 = format.parse("2001-08-17 10:06");
		d5 = format.parse("2001-08-17 10:08");
		d6 = format.parse("2001-08-17 10:10");
		d7 = format.parse("2001-08-17 10:12");
		
		p1 = new UnitPosition(unit_id, 18.1, 1, d1);
		p2 = new UnitPosition(unit_id, 18.1, 2, d2);
		p3 = new UnitPosition(unit_id, 18.1, 3, d3);
		p4 = new UnitPosition(unit_id, 18.1, 4, d4);
		p5 = new UnitPosition(unit_id, 18.1, 5, d5);
		p6 = new UnitPosition(unit_id, 18.1, 6, d6);
		p7 = new UnitPosition(unit_id, 18.1, 6, d7);
		
		p1.insertToDb();
		insObs(p1, 0);		
		p2.insertToDb();
		insObs(p2, 1);	
		
		p3.insertToDb();			
		insObs(p3, 0);	
		
		p4.insertToDb();
		insObs(p4, 0);	
		p5.insertToDb();		
		insObs(p5, 1);
		p6.insertToDb();		
		insObs(p6, 1);
		
		TrackData t1 = u.getTrack(unit_id, p1.internalGetTimestamp());
		Assert.assertEquals(3, u.getTrackLenght(t1.getGid()));
		
		TrackData t2 = u.getTrack(unit_id, p5.internalGetTimestamp());
		//Assert.assertEquals(t2.getEnd(), p6.internalGetTime_stamp());
		Assert.assertEquals(3, u.getTrackLenght(t2.getGid()));
	}
	
	private void insObs(UnitPosition p, double status) throws SQLException{
		Observation o = new Observation(p.internalGetTimestamp(), status , TrackIgnitionSolver.IGNITION_SENSOR_ID, p.getUnit_id());
		new TrackIgnitionSolver(o).solve();
	}
	
	@After
	public void delData() throws Exception{
		util.deleteUnit(unit_id);
		//util.deleteCompany(comp_id);
	}
}