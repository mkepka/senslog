package cz.hsrs.track;

import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.util.Util;
import cz.hsrs.servlet.feeder.FeederServlet;

public class TrackIgnitionSolver {
	private Util util;
	private final int timeConst;
	public static final int IGNITION_SENSOR_ID = 330040000;

	private final Observation observation;
	private UnitPosition position;
	private UnitPosition positionBefore;
	private Observation observationBefore;

	/**
	 * Constructs solver
	 * 
	 * @param obs
	 * @throws SQLException
	 */
	public TrackIgnitionSolver(Observation obs) throws SQLException {
		this.observation = obs;
		observation.insertToDb();
		util = new Util();
		try {
			timeConst = util.getUnitConfTimeById(observation.getUnitId());
		} catch (NoItemFoundException e) {
			throw new SQLException(e);
		}

		try {
			this.position = util.getExactObservationPosition(observation
					.getTimeStamp(), observation.getUnitId());
	/*	} catch (NoItemFoundException e) {
			
			// V pripade ze neni presna bere se posledni
			
			Logger logger = Logger.getLogger(ServiceParameters.LOGGER_ID);
			// TODO Auto-generated catch block
			try {
				logger.log(Level.WARNING,"Presna pozice nenalezena, unit_id = "+observation.getUnitId());
				UnitPosition pos = util.getLastUnitPosition(observation
						.getUnitId());
				if (pos.internalGetTime_stamp().before(observation.getTimeStamp())
				&& (observation.getTimeStamp().getTime() - pos.internalGetTime_stamp().getTime()) < (1000 * 60 * 5)) {
					this.position = pos;
					logger.log(Level.WARNING,"Vzata posledni pozice, gid = " +pos.getGid()+
							", unit_id = "+observation.getUnitId());
				} else {
					throw new SQLException(e);
				}
				
				*/
			} catch (NoItemFoundException e1) {
				// TODO Auto-generated catch block
				throw new SQLException(e1);				
			}
		//}
	}

	private boolean initPositionBefore(Date timestamp, long unit_id) throws SQLException {
		try {						
			positionBefore = util.getExactObservationPosition(timestamp, unit_id);
			
			return true;
		} catch (NoItemFoundException e) {
			return false;
		}				
	}
	/**
	 * Initilize observation and position before this observation
	 * 
	 * @return
	 * @throws SQLException
	 */
	private boolean initObservationBefore() throws SQLException {
		try {			
			observationBefore = util.getObservationBefore(position,
					IGNITION_SENSOR_ID);
					
			return true;
		} catch (NoItemFoundException e) {
			return false;
		}						
	}

	
	/**
	 * 
	 */
	public void solve() throws SQLException {
		if (!initObservationBefore()) {
			/**neexistuje predesla observace -> pokud je nastoartovano zaloz track*/
			if (observation.getObservedValue() == 1)
				util.startTrack(position);
			FeederServlet.logger.log(Level.INFO, "Zakladam track - neni predchozi observace,"+ position.getUnit_id());
			return;
		}				
		
		if (!initPositionBefore(observationBefore.getTimeStamp(),observationBefore.getUnitId() )) {
			solveMissingPreviousPosition();
			return;
		} 
		
		
		switch ((int) observation.getObservedValue()) {
		case 1:
			solveEngineOn();
			break;
		case 0:
			solveEngineOff();
			break;
		}

	}

	/**
	 * Pripad kdy neexistuje predesla observace s pozici
	 * @throws SQLException
	 */
	private void solveMissingPreviousPosition() throws SQLException {
		if  (observation.getObservedValue()==1){
			switch ((int) observationBefore.getObservedValue()) {
			case 1:
				solveEngineWasOn();
				break;
			case 0:
				solveEngineWasOff();
				break;
			}
			
		}
	}
	
	private void solveEngineWasOn() throws SQLException {
		
		try {
			TrackData td = getOlderTrack(position.getUnit_id(), position.internalGetTime_stamp());
			if (!util.wasEngineOn(position.getUnit_id(), td.getEnd(), observationBefore.getTimeStamp())){
				util.startTrack(position);
			} else {
				solveInsertToTrack();
			}
		} catch (NoItemFoundException e) {
			// TODO Auto-generated catch block
			util.startTrack(position);
		}
	}
	
	private void solveEngineWasOff() throws SQLException {
		util.startTrack(position);
	}
	
	private void solveEngineOn() throws SQLException {
		switch ((int) observationBefore.getObservedValue()) {
		case 1:		
			//if (observationBefore.getTimeStamp().equals(positionBefore.internalGetTime_stamp())){
			solveInsertToTrack();
		/*	} else {
				System.out.println("k posledni observaci je stara pozice");
				/**pokud bylo v mezicase chcipnuto zaloz novej*/
				
				/**pokud nebylo v mezica chcipnuto zaloz pokracuj ve starym*/
				/**
				 * 
				 * tady se musi dodelat jestli mezi pozicema nebylo chipnuto!!!
				 */
			//}*/
			break;
		case 0:
			solveStartTrack();
			break;
		}
	}

	private void solveEngineOff() throws SQLException {
		switch ((int) observationBefore.getObservedValue()) {
		case 1:
			solveFinishTrack();
			break;
		case 0:
			solveOff();
		}
	}

	private void solveOff() throws SQLException {
		try {
			TrackData track = util.getTrack(observation.getUnitId(), observation.getTimeStamp());
			util.splitTrack(position, track);
		} catch (NoItemFoundException e1) {
			return;
		}
	}

	/**
	 * Insert to old or running track
	 */
	private void solveInsertToTrack() throws SQLException {

		TrackData track;
		try {
			track = getProperTrack(observation.getUnitId(), observation.getTimeStamp());
			util.addToTrack(track, position);
		} catch (NoItemFoundException e) {
			util.startTrack(position);
			FeederServlet.logger.log(Level.INFO,
					"Zakladam track - starej track nenalezen "
							+ position.getUnit_id());
		}
	}

	private void solveStartTrack() throws SQLException {
		
		util.startTrack(positionBefore, position);
	}

	private void solveFinishTrack() throws SQLException {
		try {
			TrackData track = getOlderTrack(observation.getUnitId(), observation.getTimeStamp());
			util.addToTrack(track, position);
		} catch (NoItemFoundException e) {
			try {
				TrackData track = util.getTrack(observation.getUnitId(), observation.getTimeStamp());
				util.splitTrack(position, track);
			} catch (NoItemFoundException e1) {
				throw new SQLException(e1);
			}
		}

	}

	private TrackData getOlderTrack(long unit_id, Date time)
			throws SQLException, NoItemFoundException {

		TrackData track = util.getOlderTrack(unit_id, time);
		long dif = (time.getTime() - track.getEnd().getTime()) / 1000;
		/*
		 * if ((dif > this.timeConst)){
		 * 
		 * throw new NoItemFoundException(" pozice dlouho neprisly"); }
		 */
		return track;
	}

	private TrackData getProperTrack(long unit_id, Date time)
			throws SQLException, NoItemFoundException {
		TrackData track;
		try {
			try {
				/** try to insert in middle */
				track = util.getTrack(unit_id, time);
			} catch (NoItemFoundException e) {
				/** insert to end */
				track = util.getOlderTrack(unit_id, time);

				long dif = (time.getTime() - track.getEnd().getTime()) / 1000;
				/*
				 * if ((dif > this.timeConst*2)){
				 * 
				 * throw new NoItemFoundException(" pozice dlouho neprisly "); }
				 */
			}
		} catch (NoItemFoundException e) {
			throw new SQLException(e);
		}

		return track;
	}

	
	
}
