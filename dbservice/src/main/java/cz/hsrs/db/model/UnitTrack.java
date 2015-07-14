package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.hsrs.db.DBObject;


public class UnitTrack implements DBObject{

	private int gid;
	private String geom;
	private long unit_id;
	private String start;
	private String end;
	private int group_id;
	
	public UnitTrack(){
		
	}
	//private static 	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss+ZZ");
	
	public DBObject getDBObject(ResultSet set) throws SQLException {
		return new UnitTrack(set);			
	}

	public UnitTrack(ResultSet set) throws SQLException{
		this.gid = set.getInt("gid");
			
			this.gid = set.getInt("gid");
			this.unit_id = set.getLong("unit_id");
			this.start = set.getString("track_start");
			this.end = set.getString("track_end");
			this.geom = set.getString("st_astext");
			this.group_id = set.getInt("group_id");
			
	}
	
	public int getGroup_id() {
		return group_id;
	}

	public void setGroup_id(int group_id) {
		this.group_id = group_id;
	}

	public String getGeom() {
		return geom;
	}

	public void setGeom(String geom) {
		this.geom = geom;
	}

	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}

	public long getUnit_id() {
		return unit_id;
	}
	public void setUnit_id(long unit_id) {
		this.unit_id = unit_id;
	}
	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getStart() {
		return start;
	}

	
}

