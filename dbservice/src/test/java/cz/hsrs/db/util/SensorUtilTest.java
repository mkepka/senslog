package cz.hsrs.db.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.composite.AggregateObservation;
import cz.hsrs.db.model.composite.ObservationValue;
import cz.hsrs.db.model.composite.UnitSensor;
import cz.hsrs.db.pool.SQLExecutor;

public class SensorUtilTest {
	private static SensorUtil utilS;
	private static UnitUtil utilU;
	private static long unit_id = 111;
	private static int sensor_id = 111;
	static String datePos = "2001-07-15 00:30:00+0100";
	static String dateObs1 = "2001-07-15 00:31:00+0100";
	static String dateObs2 = "2001-07-15 00:32:00+0100";
	static String dateObs3 = "2001-07-15 00:33:00+0100";
	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	private static int sensor_id2 = 222;

	
	@BeforeClass
	public static void genData() throws Exception{
		DBHelper.setConnection();
		
		utilS = new SensorUtil();
		DatabaseFeedOperation.insertObservation(format.parse(dateObs1), unit_id, sensor_id, 1);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs2), unit_id, sensor_id, 2);
		DatabaseFeedOperation.insertObservation(format.parse(dateObs3), unit_id, sensor_id, 3);
	}
	
	@Test
	public void testGetSensors() throws Exception{
		Assert.assertTrue(utilS.hasSensor(unit_id, sensor_id));
		Assert.assertFalse(utilS.hasSensor(unit_id, 123456));
		Iterator<UnitSensor> iterSensors = utilS.getUnitsSensors(unit_id).iterator();
		UnitSensor sensorDB = iterSensors.next();
		Assert.assertEquals(sensor_id, sensorDB.getSensorId());
		Assert.assertEquals(format.parse(dateObs1), format.parse(sensorDB.getFirstObservationTime()+"00"));
		Assert.assertEquals(format.parse(dateObs3), format.parse(sensorDB.getLastObservationTime()+"00"));						
	}
	
	@Test
	public void testGetSensorsObservations() throws Exception{
		List<ObservationValue>   obsval = utilS.getSensorObservations(unit_id, sensor_id);
		Assert.assertEquals(3, obsval.size());
		
	}
	@Test
	public void testGetAgregObservations() throws Exception{
		List<AggregateObservation>   obsval = 
			utilS.getSensorObservationsTrunc(unit_id, sensor_id,  dateObs1, dateObs3, "day");
		AggregateObservation ao = obsval.get(0);
		Assert.assertEquals(2.0, ao.getValue());
		Assert.assertEquals(3, ao.getCount());
		
		
	}
	@AfterClass
	public static void delData()throws Exception{
		utilU = new UnitUtil();
		utilU.deleteUnit(unit_id);
	}
}
