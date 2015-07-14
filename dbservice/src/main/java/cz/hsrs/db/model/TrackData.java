package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;

public class TrackData {
	private int gid;	
	private int numPts;	
	private long unit_id;
	private Date start;
	private Date end;
	private boolean is_closed;
	
	private final static String cGid="gid";
	private final static String cUnit_id = "unit_id";
	private final static String cStart = "track_start";
	private final static String cEnd = "track_end";
	private final static String cClosed = "is_closed";
	
	public final  static String SELECT = cGid +", "+ cUnit_id + ", "+ cStart +", "+ cEnd + ", " + cClosed + ", ST_NumPoints(the_geom) AS numpts ";
	

	public TrackData(){
		
	}
	//private static 	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss+ZZ");
	
	public DBObject getDBObject(ResultSet set) throws SQLException {
		return new UnitTrack(set);			
	}

	public TrackData(ResultSet set) throws SQLException{
		this.gid = set.getInt(cGid);
		this.unit_id = set.getLong(cUnit_id);
		this.is_closed = set.getBoolean(cClosed);
		this.numPts = set.getInt("numpts");
		/* set.getTimeStamp vraci Timestamp a ne Date a pak se to blbe formatuje */
		this.start = new Date();
		this.start.setTime(set.getTimestamp(cStart).getTime());
		this.end =   new Date();
		this.end.setTime(set.getTimestamp(cEnd).getTime());
		
			
	}	

	public int getGid() {
		return gid;
	}
	
	public int getNumPts() {
		return numPts;
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
	public Date getEnd() {
		return end;
	}

	public Date getStart() {
		return start;
	}
	
	public boolean isClosed(){
		return is_closed;
	}
}
