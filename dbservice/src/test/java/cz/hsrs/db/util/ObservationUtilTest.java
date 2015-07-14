package cz.hsrs.db.util;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.ObservationMedlov;

public class ObservationUtilTest {
	
	static ObservationUtil util;
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	static String timeFrom = "2001-07-16 10:00"; 
	static String t1 = "2001-07-16 10:15";
	static String t2 = "2001-07-16 11:15";
	static String timeTo = "2001-07-16 12:00";
	static Date d1;
	static Date d2;
	static UnitPosition pos1;
	static UnitPosition pos2;
	static long unitId = 111;
	static long sensorId = 111;
	
	@BeforeClass
	public static void genData() throws SQLException, ParseException {
		DBHelper.setConnection();
		util = new ObservationUtil();
		d1 = format.parse(t1);
		d2 = format.parse(t2);
		pos1 = new UnitPosition(unitId, 18, 15, d1);
		pos2 = new UnitPosition(unitId, 18, 15, d2);
		}
	
	@Test	
	public void testInsertObservatation() throws SQLException{
		Date d = new Date();
		Observation obs = new Observation(d, 20, sensorId, unitId);
		util.insertObservation(obs);
		Observation o = util.getObservation(d, sensorId, unitId);
		Assert.assertEquals(20.0, o.getObservedValue());		
	}
	
	@Test
	public void testGetGid() throws SQLException, NoItemFoundException{
		pos1.insertToDb();	
		pos2.insertToDb();	
		Observation obs1 = new Observation(d1 ,20, sensorId, unitId);
		util.insertObservation(obs1);
		Observation obs2 = new Observation(d2 ,20, sensorId, unitId);
		util.insertObservation(obs2);
			
		Assert.assertEquals(util.getExactObservationPosition(d1, unitId).getGid()+1, util.getExactObservationPosition(d2, unitId).getGid());		
		//Assert.assertEquals(20.0, o.getObservedValue());		
		
	}
	
	@Test
	public void testGetObservationsMedlov() throws SQLException{
		List<ObservationMedlov> listObservUT = util.getObservationsMedlov(unitId, unitId, timeFrom, timeTo);
		Assert.assertFalse(listObservUT.isEmpty());
		
		List<ObservationMedlov> listObservT = util.getObservationsMedlov(timeFrom, timeTo);
		Assert.assertFalse(listObservT.isEmpty());
		
	}
	
	@AfterClass
	public static void delData() throws SQLException {
		UnitUtil uUtil = new UnitUtil(); 
		uUtil.deleteUnit(111);
	}

}
