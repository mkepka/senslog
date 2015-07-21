package cz.hsrs.track;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.TrackUtil;
import cz.hsrs.db.util.UnitUtil;


public class RunningTrackThread extends Thread {	
	
	int i = 0;
	final long unit_id;
	final int interval;
	UnitUtil util;
	TrackUtil utilTrack;
	Logger logger =  SQLExecutor.logger;
	
	public RunningTrackThread(long unit_id) throws SQLException{
		super(String.valueOf(unit_id));
        this.unit_id = unit_id;
		try {
			util = new UnitUtil();
			utilTrack = new TrackUtil();
			interval = util.getUnitConfTimeById(unit_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SQLException(e);
		}
	
	}

	@Override
	public synchronized void run() {
		for (i=0 ; i < interval; ){
			try {
				//System.out.println(i);	
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			i++;
		}	
		try {
			utilTrack.deleteRunningTrack(unit_id);
			logger.log(Level.FINE,"closing track for "+unit_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.log(Level.WARNING,e.getMessage());
		}
		System.out.println("closing track for "+unit_id);	
	}
	
	public void stillGoing(){
		//System.out.println(this.getState());
		//System.out.println(unit_id+" still going");	
		i=0;
		
	}
	
	public long getUnitId(){
		return unit_id;
	}
	

}
