/**
 * 
 */
package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;

/**
 * @author mkepka
 *
 */
public class ObservationMedlov implements DBObject{
	
	private int observationId;
	private long unitId;
	private long sensorId;
	private String timeStamp;
	private double observedValue;
	private String sensorName;
	private String unit;
	private String phenomenonName;
	private String phenomenonId;
	private String theGeom;

	
	
	/**
	 * @param observationId
	 * @param unitId
	 * @param sensorId
	 * @param timeStamp
	 * @param observedValue
	 * @param sensorName
	 * @param unit
	 * @param phenomenonName
	 * @param phenomenonId
	 * @param theGeom
	 */
	public ObservationMedlov(int observationId, long unitId, long sensorId,
			String timeStamp, double observedValue, String sensorName,
			String unit, String phenomenonName, String phenomenonId,
			String theGeom) {
		super();
		this.observationId = observationId;
		this.unitId = unitId;
		this.sensorId = sensorId;
		this.timeStamp = timeStamp;
		this.observedValue = observedValue;
		this.sensorName = sensorName;
		this.unit = unit;
		this.phenomenonName = phenomenonName;
		this.phenomenonId = phenomenonId;
		this.theGeom = theGeom;
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		
		return new ObservationMedlov(set.getInt("observation_id"), 
									 set.getLong("unit_id"), 
									 set.getLong("sensor_id"),
									 set.getString("time_stamp"), 
									 set.getDouble("observed_value"), 
									 set.getString("sensor_name"),
									 set.getString("unit"), 
									 set.getString("phenomenon_name"), 
									 set.getString("phenomenon_id"),
									 set.getString("the_geom"));
	}
	
	public ObservationMedlov()throws SQLException {
		
	}

	/**
	 * @return the observationId
	 */
	public int getObservationId() {
		return observationId;
	}

	/**
	 * @return the unitId
	 */
	public long getUnitId() {
		return unitId;
	}

	/**
	 * @return the sensorId
	 */
	public long getSensorId() {
		return sensorId;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @return the observedValue
	 */
	public double getObservedValue() {
		return observedValue;
	}

	/**
	 * @return the sensorName
	 */
	public String getSensorName() {
		return sensorName;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @return the phenomenonName
	 */
	public String getPhenomenonName() {
		return phenomenonName;
	}

	/**
	 * @return the phenomenonId
	 */
	public String getPhenomenonId() {
		return phenomenonId;
	}

	/**
	 * @return the theGeom
	 */
	public String getTheGeom() {
		return theGeom;
	}

	 
}
