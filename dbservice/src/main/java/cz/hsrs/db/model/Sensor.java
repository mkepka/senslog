package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.util.SensorUtil;

public class Sensor implements DBObject {

	private long sensorId;
	private String sensorName;
	private String sensorType;
	private Phenomenon phenomenon;

	public Sensor(long sensorId, String sensorName, String sensorType,
			Phenomenon phenomenon) {
		super();
		this.sensorId = sensorId;
		this.sensorName = sensorName;
		this.sensorType = sensorType;
		this.phenomenon = phenomenon;
	}
	
	public Sensor(){
		
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		this.sensorId = set.getLong("sensor_id");
		this.sensorName = set.getString("sensor_name");
		this.sensorType = set.getString("sensor_type");		
		SensorUtil sUtil = new SensorUtil();		
		this.phenomenon = sUtil.getPhenomenonById(set.getString("phenomenon_id"));		
		return this;
	}

	public long getSensorId() {
		return sensorId;
	}

	public String getSensorName() {
		return sensorName;
	}

	public String getSensorType() {
		return sensorType;
	}

	public Phenomenon getPhenomenon() {
		return phenomenon;
	}

}
