package cz.hsrs.db;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.hsrs.db.model.AlertQuery;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.AlertUtil;
import cz.hsrs.track.TrackIgnitionSolver;
import cz.hsrs.track.TrackSolver;

public class DatabaseFeedOperation {

    private static Logger logger = Logger.getLogger(SQLExecutor.LOGGER_ID);

    // private boolean transactions = true;

    private static int transaction_size = 1;
    private static int t = 0;
    private static String insertTransaction = "BEGIN;";

    /**
     * Method inserts new Observation to DB
     * @param date when the observations was measured
     * @param unit_id - id of unit
     * @param sensor_id - id of sensor
     * @param value - observed value, can be NaN
     * @return true if observations was successfully inserted, false if it wasn't
     * @throws SQLException
     */
    public static synchronized boolean insertObservation(Date date, long unit_id, long sensor_id, Double value) throws SQLException {
        Observation o = new Observation(date, value, sensor_id, unit_id);
        boolean inserted = false;
        if (sensor_id == TrackIgnitionSolver.IGNITION_SENSOR_ID) {
            TrackIgnitionSolver solver = new TrackIgnitionSolver(o);
            solver.solve();
            inserted = o.insertToDb();
        } else {
            inserted = o.insertToDb();
        }
        return inserted;
    }

    /**
     * Method inserts new position into DB 
     * @param unit_id - id of unit
     * @param lat - latitude
     * @param lon - longitude
     * @param alt - altitude
     * @param date - when positions was measured
     * @return true is positions was successfully inserted, false elsewhere 
     * @throws Exception
     */
    public static synchronized boolean insertPosition(long unit_id, double lat, double lon, double alt, Date date) throws Exception {
        boolean inserted = insertPosition(unit_id, lat, lon, alt, date, Double.NaN);
        return inserted;
    }

    /**
     * Method inserts new position into DB
     * @param unit_id - id of unit
     * @param lat - latitude
     * @param lon - longitude
     * @param date - when positions was measured
     * @return true is positions was successfully inserted, false elsewhere
     * @throws Exception
     */
    public static synchronized boolean insertPosition(long unit_id, double lat, double lon, Date date) throws Exception {
        boolean inserted = insertPosition(unit_id, lat, lon, Double.NaN, date, Double.NaN);
        return inserted;
    }

    /**
     * Method inserts new position into DB 
     * @param unit_id - id of unit
     * @param lat - latitude
     * @param lon - longitude
     * @param alt - altitude
     * @param date - when positions was measured
     * @param speed - current speed of the unit
     * @return true is positions was successfully inserted, false elsewhere
     * @throws SQLException
     */
    public static synchronized boolean insertPosition(long unit_id, double lat, double lon, double alt, Date date, double speed) throws SQLException {
        /**
         * zde se ztrati alt!
         */
        boolean useTracks = true;
        boolean inserted = false;
        
        UnitPosition p = new UnitPosition(unit_id, lon, lat, date, speed, "");
        if(useTracks){
            inserted = solve(p);
        }
        else{
            inserted = p.insertToDb();
        }
        checkAlertQueries(p);
        return inserted;
    }

    /**
     * Method inserts new position into DB 
     * @param unit_id - id of unit
     * @param lat - latitude
     * @param lon - longitude
     * @param alt - altitude
     * @param dop - dilution of precision of the position
     * @param date - when positions was measured
     * @param speed - current speed of the unit
     * @return true is positions was successfully inserted, false elsewhere
     * @throws SQLException
     */
    public static synchronized boolean insertPosition(long unit_id, double lat, double lon, double alt, double dop, Date date, double speed) throws SQLException {
        boolean useTracks = true;
        boolean inserted = false;
        
        UnitPosition p = new UnitPosition(unit_id, lon, lat, date, speed, "");
        if (useTracks){
            inserted = solve(p);
        }
        else{
            inserted = p.insertToDb();
        }
        checkAlertQueries(p);
        return inserted;
    }

    /**
     * Method inserts new position into DB and tries to solve track
     * @param p new position as UnitPosition object
     * @return true if position was successfully inserted, false elsewhere
     * @throws SQLException
     */
    private static synchronized boolean solve(UnitPosition p) throws SQLException {
        try {
            TrackSolver solver = new TrackSolver(p);
            boolean inserted = solver.solve();
            return inserted;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private static void addToTransaction(String query) throws SQLException,
            InstantiationException, IllegalAccessException {

        insertTransaction = insertTransaction + "\n" + query;
        t++;
        // System.out.println(i++);
        if (t >= transaction_size) {
            // insertTransaction= insertTransaction +"\n" + query;
            String insert = insertTransaction + "\n" + "COMMIT;";
            t = 0;
            insertTransaction = "BEGIN;";
            insertStatemant(insert);
        }

    }

    /**
     * Finds gid of last position according to time and date
     * 
     * @param name
     * @param date
     * @return
     * @throws Exception
     */
    

    private static synchronized void insertStatemant(String insertStmt) throws SQLException {
        try {
            SQLExecutor.executeUpdate(insertStmt);
            logger.log(Level.FINE, "SQL succesfull - " + insertStmt);
        } catch (Exception e1) {
            logger.log(Level.INFO, e1.getMessage(), "Statement = '"
                    + insertStmt);
            throw new SQLException(e1);
        }
    }

    /**
     * 
     * @param date
     * @param unit_id
     * @param alert_id
     * @throws SQLException
     */
    public static synchronized void insertAlertEvent(Date date, long unit_id, int alert_id)
            throws SQLException {
        String insertStmt = " INSERT INTO alert_events (time_stamp, unit_id, alert_id) VALUES ("
                + "'"
                + date
                + "'"
                + ", "
                + unit_id
                + ", "
                + "'"
                + alert_id
                + "');";

        insertStatemant(insertStmt);
    }

    /**
     * 
     * @param event_id
     * @throws SQLException
     */
    public static synchronized void solvingAlertEvent(int event_id) throws SQLException {
        String updateStmt = "UPDATE alert_events SET solving = 'true' WHERE alert_event_id = "
                + event_id + " ;";

        insertStatemant(updateStmt);
    }
    
    /**
     * 
     * @param pos
     * @throws SQLException
     */
    private static void checkAlertQueries(UnitPosition pos) throws SQLException{
        AlertUtil aUtil = new AlertUtil();
        List<AlertQuery> queryList = aUtil.getAlertQueries(pos.getUnit_id());
        if(queryList.isEmpty() == false){
            Iterator<AlertQuery> queryIter = queryList.iterator();
            while(queryIter.hasNext()==true){
                AlertQuery aQuery = queryIter.next();
                try{
                    boolean lastStatusAQ = aUtil.getAlertQueryLastStatus(aQuery, pos);
                    boolean newStatusAQ = aUtil.checkAlertQuery(aQuery, pos);
                    aUtil.setNewAlertQueryTimeStamp(aQuery, pos);
                    if(newStatusAQ == true && lastStatusAQ == false){
                        aUtil.setNewAlertQueryLastStatus(newStatusAQ, aQuery, pos);
                    }
                    else if(newStatusAQ == false && lastStatusAQ == true){
                        aUtil.setNewAlertQueryLastStatus(newStatusAQ, aQuery, pos);
                        insertAlertEvent(pos.internalGetTime_stamp(),pos.getUnit_id(),aQuery.getAlertId());
                    }
                }
                catch(NoItemFoundException ex){
                    boolean newStatusAQ = aUtil.checkAlertQuery(aQuery, pos);
                    aUtil.setNewAlertQueryLastStatus(newStatusAQ, aQuery, pos);
                    aUtil.setNewAlertQueryTimeStamp(aQuery, pos);
                    if(newStatusAQ == false){
                        insertAlertEvent(pos.internalGetTime_stamp(),pos.getUnit_id(),aQuery.getAlertId());
                    }
                }
            }
        }
    }
}