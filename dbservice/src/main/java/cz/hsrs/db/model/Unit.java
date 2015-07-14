package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;

public class Unit implements DBObject{
	private long unitId;
	private int holderId;
	private String description;	
	
	public Unit(long unitId, int userId, String description) {
		super();
		this.unitId = unitId;
		this.holderId = userId;
		this.description = description;
	}
	
	public Unit(long unitId, String description) {
		super();
		this.unitId = unitId;		
		this.description = description;
	}
	
	public Unit(){
		
	}
	
	public Unit(ResultSet set) throws SQLException {
		this.unitId = set.getLong("unit_id");
		this.description = set.getString("description");
		this.holderId = set.getInt("holder_id");
	}

	public long getUnitId() {
		return unitId;
	}
	public void setUnitId(long unitId) {
		this.unitId = unitId;
	}
	public int getHolderId() {
		return holderId;
	}
	public void setHolderId(int userId) {
		this.holderId = userId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new Unit(set);
	}


	
	

}
