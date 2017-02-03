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
    private static final String SCHEMA_NAME = "public";

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
     * @param date - when positions was measured as Date
     * @param speed - current speed of the unit
     * @return true is positions was successfully inserted, false elsewhere
     * @throws SQLException
     */
    public static synchronized boolean insertPosition(long unit_id, double lat, double lon, double alt, 
            Date date, double speed) throws SQLException {
        boolean useTracks = true;
        boolean inserted = false;
        
        UnitPosition p = new UnitPosition(unit_id, lon, lat, alt, date, speed, "");
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
     * @param lat - latitude of position
     * @param lon - longitude of position
     * @param alt - altitude of position
     * @param dop - dilution of precision of the position
     * @param date - when positions was measured
     * @param speed - current speed of the unit
     * @return true is positions was successfully inserted, false elsewhere
     * @throws SQLException
     */
    public static synchronized boolean insertPosition(long unit_id, double lat, double lon, double alt, 
            double dop, Date date, double speed) throws SQLException {
        boolean useTracks = true;
        boolean inserted = false;
        
        UnitPosition p = new UnitPosition(unit_id, lon, lat, alt, date, speed, dop, "");
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
     * Method inserts new position into DB and returns ID
     * @param unit_id - id of unit
     * @param lat - latitude of position
     * @param lon - longitude of position
     * @param alt - altitude of position
     * @param dop - dilution of precision of the position
     * @param date - when positions was measured
     * @param speed - current speed of the unit
     * @return gid of new position
     * @throws SQLException
     */
    public static synchronized int insertPositionByGid(long unit_id, double lat, double lon, double alt, 
            double dop, Date date, double speed, String srid) throws SQLException {
        boolean useTracks = true;
        boolean inserted = false;
        
        UnitPosition p = new UnitPosition(unit_id, lon, lat, alt, date, speed, dop, srid);
        if (useTracks){
            inserted = solve(p);
        }
        else{
            inserted = p.insertToDb();
        }
        checkAlertQueries(p);
        if(inserted){
            return p.getGid();
        }
        else{
            throw new SQLException("Position cannot be inserted!");
        }
    }
    
    /**
     * Method updates position in DB by given UnitPosition object
     * @param p UnitPosition object containing values to be updated in DB
     * @return true if UnitPosition was updated, false if not
     * @throws SQLException
     */
    public static boolean updatePositionByGid(UnitPosition p) throws SQLException{
        StringBuffer update = new StringBuffer();
        update.append("UPDATE "+SCHEMA_NAME+".units_positions SET ");
        update.append("the_geom = "+p.getPostgisString()+", ");
        update.append("time_stamp = '"+p.getTime_stamp()+"', ");
        // ------------- SPEED ------------------
        String speedStr;
        Double speed = p.getSpeed();
        if(speed == null){
            speedStr = "NULL";
        }
        else if(speed != null && Double.isNaN(speed)){
            speedStr = "NULL";
        }
        else{
            speedStr = String.valueOf(speed);
        }
        update.append("speed = "+speedStr+", ");
        // ------------- DOP ------------------
        String dopStr;
        Double dop = p.getDop();
        if(dop == null){
            dopStr = "NULL";
        }
        else if(dop != null && Double.isNaN(dop)){
            dopStr = "NULL";
        }
        else{
            dopStr = String.valueOf(dop);
        }
        update.append("dop = "+dopStr);
        // ------------- ALT ------------------
        if(SQLExecutor.isAltitudeEnabled()){
            String altStr;
            Double alt = p.getAlt();
            if(alt == null){
                altStr = "NULL";
            }
            else if(alt != null && Double.isNaN(alt)){
                altStr = "NULL";
            }
            else{
                altStr = String.valueOf(alt);
            }
            update.append(", altitude = "+altStr+" ");
        }
        update.append("WHERE gid = "+p.getGid());
        String query = update.toString();
        
        int i = SQLExecutor.executeUpdate(query);
        if(i == 0 || i == 1){
            return true;
        } else{
            return false;
        }
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

    private static void addToTransaction(String query) throws SQLException, InstantiationException, IllegalAccessException {
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
     * Method inserts new AlertEvent object to the DB
     * @param date - when alert was detected, as Date
     * @param unit_id - ID of unit
     * @param alert_id - ID of Alert that was detected
     * @throws SQLException
     */
    public static synchronized void insertAlertEvent(Date date, long unit_id, int alert_id)
            throws SQLException {
        String insertStmt = " INSERT INTO "+SCHEMA_NAME+".alert_events (time_stamp, unit_id, alert_id) VALUES ("
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
        String updateStmt = "UPDATE "+SCHEMA_NAME+".alert_events SET solving = 'true'"
                + " WHERE alert_event_id = " + event_id + " ;";
        insertStatemant(updateStmt);
    }
    
    /**
     * Method checks new Position of unit for stored AlertQueries
     * @param pos - Current position of unit
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
                        insertAlertEvent(pos.internalGetTimestamp(),pos.getUnit_id(),aQuery.getAlertId());
                    }
                }
                catch(NoItemFoundException ex){
                    boolean newStatusAQ = aUtil.checkAlertQuery(aQuery, pos);
                    aUtil.setNewAlertQueryLastStatus(newStatusAQ, aQuery, pos);
                    aUtil.setNewAlertQueryTimeStamp(aQuery, pos);
                    if(newStatusAQ == false){
                        insertAlertEvent(pos.internalGetTimestamp(),pos.getUnit_id(),aQuery.getAlertId());
                    }
                }
            }
        }
    }
}