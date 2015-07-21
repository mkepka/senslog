package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.model.Unit;
import cz.hsrs.db.util.SensorUtil;

public class UnitObservation implements DBObject {
	
	private final Unit unit;
	private final UnitSensor sensor;
	private final List<ObservationValue> values;
	

	private UnitObservation() {
		super();
		this.unit = null;
		this.sensor = null;
		this.values = null;
	}


	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		Unit unit = new Unit(0, 0,"");
		UnitSensor sens = new UnitSensor();
		SensorUtil util = new SensorUtil();
		util.getSensorObservations(unit.getUnitId(), sens.getSensorId());
		
		//unit.get
		// TODO Auto-generated method stub
		return null;
	}

}
