package cz.hsrs.track;

import java.sql.SQLException;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.util.SensorUtil;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;

/**
 * utility class to solve if the track should be started as new,
 * 
 * @author jezekjan
 * 
 */
public class TrackSolver {

	private TrackUtil utilT;
	private UnitUtil utilU;
	private SensorUtil utilS;
	private final UnitPosition pos;	
	int timeConst;

	public TrackSolver(UnitPosition pos) throws SQLException, NoItemFoundException  {
		super();
		try {
			//con = ConnectionManager.getConnectionFeeder();
			utilT = new TrackUtil();
			utilU = new UnitUtil();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SQLException(e);		
		}
		this.pos = pos;	
	}
	
	private void solveNewPos(){
		
	}
	

	/**
	 * 
	 * @throws SQLException
	 */
	public void solve() throws SQLException {
		utilS = new SensorUtil();
		
		/**
		 * pokud mame senzor na klicek pouzijeme jinej postup 
		 */
		if (utilS.hasSensor(pos.getUnit_id(), TrackIgnitionSolver.IGNITION_SENSOR_ID)) {
			pos.insertToDb();
			return;
		}
			
	
		UnitPosition lastPos;
		try {
			lastPos = utilU.getLastUnitPosition(pos.getUnit_id());			
		} catch (NoItemFoundException e) {
			//throw new SQLException("bug in updating last position "+ e);
			// nova pozice neexistuje - zaloz novej track 
			pos.insertToDb();
			utilT.startTrack(pos);
			return;
		}
		
		if (lastPos.internalGetTime_stamp().before(pos.internalGetTime_stamp())) {
			// pozice je novejsi -> zvazime pridane pouze ke starymu track a kdyz se pozice neprida zacneme novej track
			pos.insertToDb();
			if (!tryToAddToOld()) { utilT.startTrack(pos);}
			return;
		} else {
			// -> pozice je starsi zkusime jestli bezi ;
			try {
				TrackData runningT =utilT.getTrack(pos.getUnit_id(), pos.internalGetTime_stamp());
				pos.insertToDb();
				utilT.addToTrack( runningT, pos);
				return;
			} catch (NoItemFoundException e) {	
				pos.insertToDb();
				tryToAddOldAndNew();	
				return;
			}
			// -> pozice je starsi zkusime pridat k obema ;
							
		}				
	}

	/**
	 * Check if Old exist and try to add the position to it according to time span.
	 * @return
	 */	
	private boolean tryToAddToOld() throws SQLException {
		try {			
			timeConst = utilU.getUnitConfTimeById(pos.getUnit_id());
			
			TrackData oldTrack = utilT.getOlderTrack(pos.getUnit_id(), pos
					.internalGetTime_stamp()); 	
			long timeToOldStart = Math.abs((pos.internalGetTime_stamp().getTime() - oldTrack.getStart().getTime()) / 1000);
			long timeToOldEnd = Math.abs((pos.internalGetTime_stamp().getTime() - oldTrack.getEnd().getTime()) / 1000);
			if (timeConst >  timeToOldStart || timeConst > timeToOldEnd){
				utilT.addToTrack(oldTrack, pos);
				return true;
			} else {
				return false;
			}			
		} catch (NoItemFoundException e) {
			return false;
		}
	}
	
	/**
	 * Check if New exist and try to add the position to it according to time span.
	 * @return
	 */	
	private boolean tryToAddToNew() throws SQLException {
		try {		
			timeConst = utilU.getUnitConfTimeById(pos.getUnit_id());			
			TrackData newTrack = utilT.getNewerTrack(pos.getUnit_id(), pos
					.internalGetTime_stamp()); 	
			long timeToNewStart = Math.abs((pos.internalGetTime_stamp().getTime() - newTrack.getStart().getTime()) / 1000);
			long timeToNewEnd   = Math.abs((pos.internalGetTime_stamp().getTime() - newTrack.getStart().getTime()) / 1000);
			if (timeConst > timeToNewEnd || timeConst > timeToNewStart){
				utilT.addToTrack(newTrack, pos);
				return true;
			} else {
				return false;
			}			
		} catch (NoItemFoundException e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	private void tryToAddOldAndNew() throws SQLException {
		boolean wasAdded = false;				
		
		TrackData oldTrack = null;
		TrackData newTrack = null;
		try {
			 timeConst = utilU.getUnitConfTimeById(pos.getUnit_id());
			 oldTrack = utilT.getOlderTrack(pos.getUnit_id(), pos
					.internalGetTime_stamp()); 	
		} catch (NoItemFoundException e) {
			 if (tryToAddToNew()) {return; } 
			 else { utilT.startTrack(pos); return;} 
			 
		}	
		try {
			newTrack = utilT.getNewerTrack(pos.getUnit_id(), pos
					.internalGetTime_stamp()); 	
		} catch (NoItemFoundException e) {
			if (tryToAddToOld()) {return; } else {utilT.startTrack(pos); return;}  
		}
		
		/**Bouth track exists - */
		
		long timeToNew = (newTrack.getStart().getTime() - pos
				.internalGetTime_stamp().getTime()) / 1000;
		long timeToOld = (pos.internalGetTime_stamp().getTime() - oldTrack.getEnd()
				.getTime()) / 1000;
		
		//if (!wasAdded) utilT.startTrack(pos);		
		if (timeToNew <= timeConst && timeToOld > timeConst) {
			// add to new
			wasAdded = utilT.addToTrack(newTrack, pos);
		} else if (timeToNew > timeConst && timeToOld <= timeConst) {
			// add to old
			wasAdded = utilT.addToTrack(oldTrack, pos);
		} else if (timeToNew <= timeConst && timeToOld <= timeConst) {
			// join
			utilT.addToTrack(newTrack, pos);
			utilT.addToTrack(oldTrack, pos);
			wasAdded = utilT.joinTrack(oldTrack, newTrack);			
		} else if (timeToNew > timeConst && timeToOld > timeConst) {
			// make new track()
			wasAdded = utilT.startTrack(pos);
		} else {
			throw new SQLException("Bug....");
		}
		
		if (!wasAdded) {
			throw new SQLException("Bug....");
		}
	}	

}
