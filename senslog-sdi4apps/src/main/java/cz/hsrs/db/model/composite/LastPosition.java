package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.model.AlertEvent;
import cz.hsrs.db.model.UnitPosition;




public class LastPosition implements DBObject{
	
	public static final String IS_RUNNING = "is_moving";

	private final UnitPosition position;
	private final List<AlertEvent> alertEvents;
	private final Map attributes;
	
	
	public LastPosition() {
		super();
		this.position = null;
		this.attributes = null;
		this.alertEvents = null;
	}
	
	public LastPosition(UnitPosition position, Map attributes, List<AlertEvent> events) {
		super();
		this.position = position;
		this.attributes = attributes;
		this.alertEvents = events;
	}
	
	public UnitPosition getPosition() {
		return position;
	}

	public Map getAttributes() {
		return attributes;
	}

	public List<AlertEvent> getAlertEvents(){
		return alertEvents;
	}
	
	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new LastPosition();
	}
}
