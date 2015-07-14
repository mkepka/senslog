package cz.hsrs.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.UnitSensor;
import cz.hsrs.track.TrackIgnitionSolver;

public class TrackUtil extends UnitUtil {
	
	
	public TrackUtil() {
		super();
	}
	SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	
	
	public int deleteRunningTrack(long unit_id) throws SQLException {
		String queryConf = "delete from running_tracks where unit_id ="
				+ unit_id;
		return stmt.executeUpdate(queryConf);
	}

	public TrackData getNewerTrack(long unit_id, Date date) throws SQLException , NoItemFoundException {

		String query = "SELECT " +
				TrackData.SELECT +
				" FROM units_tracks WHERE unit_id="
				+ unit_id + " AND track_start > '" + formater.format(date)
				+ "' ORDER BY track_start LIMIT 1; ";
		ResultSet res = stmt.executeQuery(query);
		if (res.next()){
			return new TrackData(res);
			} else throw new NoItemFoundException("getNewerTrack for "+unit_id+" "+date +" does not exist");
	 	

	}
	
	public TrackData getOlderTrack(long unit_id, Date date) throws SQLException, NoItemFoundException {		
		String query = "SELECT " +
		 		TrackData.SELECT + 
				" FROM units_tracks WHERE unit_id="
				+ unit_id + " AND track_end < '" + formater.format(date) 			
				+ "' ORDER BY track_end  DESC  LIMIT 1; ";
		ResultSet res = stmt.executeQuery(query);
		if (res.next()){
		return new TrackData(res);
		} else throw new NoItemFoundException("getOlderTrack for "+unit_id+" "+date +" does not exist");
 
	}
	
	public UnitPosition getPositionByTime(long unit_id, Date date) throws SQLException, NoItemFoundException {

		String query = "SELECT " +
				UnitPosition.SELECT +
				" FROM units_positions WHERE " +
				"unit_id =" + unit_id + " AND " +
				"time_stamp = '" +formater.format(date)+ "';";
		ResultSet res = stmt.executeQuery(query);
		if (res.next()){
		return new UnitPosition(res);
		} else throw new NoItemFoundException("getPositionByTime for "+unit_id+" "+date +" does not exist");
		
	}
	
	public boolean addToTrack(TrackData track, UnitPosition p) throws SQLException {
		// zjisti pocet pozic pred v tracku 
		
		String query = "SELECT count(gid) FROM units_positions where time_stamp < '" +
		p.internalGetTime_stamp() +
		"' AND time_stamp >= '" +
		track.getStart() +
		"' AND unit_id = " +
		p.getUnit_id();  
		ResultSet res = stmt.executeQuery(query);
		res.next();
		
		//System.out.println();
		//vloz na itou pozici 				
		String update = "SELECT addPositionToTrack("+track.getGid() +","+ p.getGid()+","+ (res.getInt(1)) +");";
		ResultSet res2 = stmt.executeQuery(update);
		res2.next();
		//System.out.println(res2.getBoolean(1));
		return res2.getBoolean(1);
	}
	
	public boolean joinTrack(TrackData t1, TrackData t2) throws SQLException {

		String qer = "SELECT mergetracks(" +
				t1.getGid()+"," +
				t2.getGid()+");";
		ResultSet res = stmt.executeQuery(qer);
		res.next();
		return res.getBoolean(1);
	}
	

	public int startTrack(UnitPosition pos1, UnitPosition pos2) throws SQLException {
		String getGID = "SELECT nextval('units_tracks_gid_seq'::regclass)";
		ResultSet resg = stmt.executeQuery(getGID);
		resg.next();
		int gid = resg.getInt(1);
		
		String  qer= " INSERT INTO units_tracks(gid, the_geom, unit_id, track_start, track_end) " +
				"values (" +
				gid+
				", st_geomfromtext('LINESTRING("+ pos1.getX()+ " "+pos1.getY()+", "
											 + pos2.getX()+ " "+pos2.getY()+")',4326), "
				+ pos1.getUnit_id()+", "
				+"timestamp with time zone '"+pos1.getTime_stamp()+"', "
				+"timestamp with time zone '"+pos2.getTime_stamp()+"')";	
		
		int res = stmt.executeUpdate(qer);		
		return gid;
		//INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
		//values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);
		
	}
	
	public boolean startTrack(UnitPosition pos) throws SQLException {

		String  qer= "SELECT startTrack(timestamp with time zone'"+pos.internalGetTime_stamp() +"',"+pos.getUnit_id()+")";
		ResultSet res = stmt.executeQuery(qer);
		res.next();
		return res.getBoolean(1);
		//INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
		//values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);
		
	}
	
	
	public boolean splitTrack(UnitPosition pos, TrackData track) throws SQLException {

		String  qer= "SELECT _split_track("+ pos.getPostgisString()+", "+
						track.getGid()+
						", timestamp with time zone '"+pos.internalGetTime_stamp() +"')";
		ResultSet res = stmt.executeQuery(qer);
		res.next();
		return res.getBoolean(1);
		//INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
		//values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);
		
	}
	
	public boolean startTrackByIgnition(UnitPosition pos, boolean closed) throws SQLException {
		ObservationUtil ut = new ObservationUtil();
		UnitUtil uut = new UnitUtil();
		//TrackUtil tut = new TrackUtil(conn);
		Observation before = null;
		try {
			 before = ut.getObservationBefore(pos, TrackIgnitionSolver.IGNITION_SENSOR_ID);			
		} catch (NoItemFoundException e) { }
		
		String  qer= "SELECT startTrack2(timestamp with time zone '"+pos.internalGetTime_stamp() +"',"+pos.getUnit_id()+", " + closed+ ")";
		
		ResultSet res = stmt.executeQuery(qer);
		res.next();
		
		if (before!=null && before.getObservedValue()==0){
			try {
				UnitPosition bef = uut.getPositionByGid(before.getGid());
				TrackData track = getTrack(pos.getUnit_id(), pos.internalGetTime_stamp());
				addToTrack(track, bef);
			} catch (NoItemFoundException e) {
				// TODO Auto-generated catch block
				//Should never happen
				e.printStackTrace();
			}
		}
		
		return res.getBoolean(1);
		
		//INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
		//values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);
		
	}
	
	public int setTrackIsClosedByIgnition(TrackData track, UnitPosition p, boolean closed) throws SQLException {
		
		UnitUtil u = new UnitUtil();
		int interval = 0;
		try {
			interval = u.getUnitConfTimeById(p.getUnit_id());
		} catch (NoItemFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (( p.internalGetTime_stamp().getSeconds() - track.getEnd().getSeconds())< interval){
			addToTrack(track, p);
		}
		
		String update = "UPDATE units_tracks SET is_closed ="+ closed+" WHERE gid = "+track.getGid();
		int res = stmt.executeUpdate(update);
		return res;
		
	}
	public TrackData getTrack(long unit_id, Date time_stamp) throws SQLException, NoItemFoundException {
		
		String  qer= "SELECT "+TrackData.SELECT+" FROM units_tracks where unit_id =" +
				unit_id + " AND track_start <= '"
				+ time_stamp + "' AND track_end >= '"
				+time_stamp+"'";			
		ResultSet res = stmt.executeQuery(qer);
		if (res.next()) {
		return new TrackData(res);	
	  } else throw new NoItemFoundException("getTrack for "+unit_id+" "+time_stamp +" does not exist");
		//INSERT INTO units_tracks(gid, the_geom, unit_id, track_start) 
		//values (track_gid, line_geom, NEW.unit_id, NEW.time_stamp);
		
	}	
	
	public boolean hasTrack(long unit_id) throws SQLException {

		String  qer= "SELECT gid FROM units_tracks where unit_id =" +
				unit_id ;		
		ResultSet res = stmt.executeQuery(qer);
		if (res.next()) {
		return true;	
	  } else return false;
		
	}	
	
	public boolean hasTrack(long unit_id, Date time_stamp) throws SQLException {

		String  qer= "SELECT gid FROM units_tracks where unit_id =" +
				unit_id + " AND track_start <= '"
				+ time_stamp + "' AND track_end >= '"
				+time_stamp+"'";	
		ResultSet res = stmt.executeQuery(qer);
		if (res.next()) {
		return true;	
	  } else return false;
		
	}	
	
	
	public int getTrackLenght(int gid) throws SQLException, NoItemFoundException {
		String qer = "Select ST_NumPoints(the_geom) FROM units_tracks WHERE gid = "+gid;
		ResultSet res = stmt.executeQuery(qer);
		if (res.next()) {
		return res.getInt(1);	
	  } else throw new NoItemFoundException("getTrackLenght for "+gid+" does not exist");
	}
	/**
	 * Pro test pridat metodu na poictani bodu v tracku
	 * @throws SQLException 
	 */

	public boolean wasEngineOn(long unit_id, Date from, Date to) throws SQLException{
		
		String sel = "SELECT observed_value FROM observations where unit_id =" + unit_id + " AND " +
					" sensor_id = "+ TrackIgnitionSolver.IGNITION_SENSOR_ID + " AND " +
					" time_stamp >= '" + formater.format(from) +"'::timestamp with time zone AND "+ 
					" time_stamp <= '" + formater.format(to) +"'::timestamp with time zone AND "+
					" observed_value = 1" ;
		ResultSet res = stmt.executeQuery(sel);			
		return res.next();
	}

}
