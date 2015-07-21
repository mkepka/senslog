package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Unit;
import cz.hsrs.db.model.UnitDriver;
import cz.hsrs.db.model.UnitHolder;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.custom.DBItemInfo;
import cz.hsrs.db.util.UtilFactory;

public class RealUnit implements DBObject{

	private final Unit unit;
	private final UnitPosition position ;
	private final LastPosition lastpos;
	private final List<UnitSensor> sensors;
	private final UnitHolder holder;
	private final List<UnitDriver> drivers;
	
	private final List<DBItemInfo> generalInfo;
	
	public RealUnit(){
		unit = null;
		sensors = null;
		position = null;
		holder = null;
		lastpos = null;
		generalInfo = null;
		drivers = null;
	}
	
	public RealUnit(ResultSet set) throws SQLException {
		generalInfo = new ArrayList<DBItemInfo>();
		
		position = new UnitPosition(set);
		UtilFactory fact = null;		
		try {
			fact = new UtilFactory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 		
		sensors = fact.sensorUtil.getUnitsSensors(position.getUnit_id());	
		lastpos = fact.unitUtil.getLastPositionWithStatus(position);
		UnitHolder h = null;
		try {
			h = fact.unitUtil.getUnitHolder(position.getUnit_id());
		} catch (NoItemFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		holder = h;
		
		 try {
			DBItemInfo info = new DBItemInfo("custom", "cars", position.getUnit_id(), "unit_id");
			 generalInfo.add(info);
		} catch (NoItemFoundException e) {
			//ignore - leave infos empty
		}
		
		drivers = fact.unitUtil.getUnitDrivers(position.getUnit_id());
		unit = fact.unitUtil.getUnit(position.getUnit_id());
	}


	public LastPosition getLastpos() {
		return lastpos;
	}

	private UnitPosition getPosition() {
		return position;
	}

	public List<UnitSensor> getSensors() {
		return sensors;
	}

	public UnitHolder getHolder() {
		return holder;
	}

	public List<DBItemInfo> getGeneralInfo() {
		return generalInfo;
	}
	@Override	
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new RealUnit(set);
	}

	public List<UnitDriver> getDrivers() {
		return drivers;
	}

	public Unit getUnit(){
		return unit;
	}
}
