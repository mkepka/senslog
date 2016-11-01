package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.hsrs.db.model.IgnitionStatus;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Unit;
import cz.hsrs.db.model.UnitDriver;
import cz.hsrs.db.model.UnitHolder;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.LastPosition;
import cz.hsrs.db.model.custom.DBItemInfo;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.factory.UnitPositionFactory;

/**
 * Utility class concentrates method for unit and all related objects
 * @author mkepka
 *
 */
public class UnitUtil extends DBUtil {

	private static final String SENSLOG_SCHEMA_NAME = "public";
    private final SimpleDateFormat formater = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm:ssZ");

    public UnitUtil() {
        super();
    }

    public int deleteUnit(long unit_id) throws SQLException {
        String delete_unit = "DELETE FROM units WHERE unit_id = " + unit_id + ";";
        return SQLExecutor.executeUpdate(delete_unit);
    }

    public int deleteHolder(int holder_id) throws SQLException {
        String delete_unit = "DELETE FROM unit_holders WHERE holder_id ="
                + holder_id;
        return SQLExecutor.executeUpdate(delete_unit);
    }

    public int deleteHolder(String hname) throws SQLException {
        String delete_unit = "DELETE FROM unit_holders WHERE holder_name = '"
                + hname + "'";
        return SQLExecutor.executeUpdate(delete_unit);
    }

    public int deleteDriver(int driver_id) throws SQLException {
        String delete_driver = "DELETE FROM unit_drivers WHERE driver_id = '"
                + driver_id + "'";
        return SQLExecutor.executeUpdate(delete_driver);

    }
    
    public int deleteDriver(String fname, String lname) throws SQLException {
        String delete_driver = "DELETE FROM unit_drivers WHERE fname = '"
                + fname + "' and lname = '" + lname +"'";
        return SQLExecutor.executeUpdate(delete_driver);

    }

    public int insertUnit(long unit_id, String description)
            throws SQLException {
        String queryConf = "INSERT into units(unit_id, description) values ("
                + unit_id + ",'" + description + "');";
        return SQLExecutor.executeUpdate(queryConf);
    }
    
    /**
     * Method pairs given unit to given group
     * @param unitId of unit
     * @param groupId of group
     * @return either (1) the row count for SQL DML statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public int pairUnitToGroup(long unitId, int groupId) throws SQLException{
        String insUtG = "INSERT INTO units_to_groups(unit_id, group_id) VALUES("+unitId+", "+groupId+");";
        return SQLExecutor.executeUpdate(insUtG);
    }

    public int getUnitConfTimeById(long unit_id) throws SQLException,
            NoItemFoundException {
        String queryConf = "select extract(epoch from max_time_span) from units_conf where unit_id ="
                + unit_id;

        ResultSet res = stmt.executeQuery(queryConf);

        if (res.next()) {
            return res.getInt(1);
        } else
            throw new NoItemFoundException("getUnitConfTimeById for " + unit_id
                    + " not found.");
    }

    public boolean isRunning(long unit_id) throws SQLException {
        String queryConf = "select unit_id from running_tracks where unit_id ="
                + unit_id;
        ResultSet res = stmt.executeQuery(queryConf);
        return res.next();
    }

    /**
     * 
     * @param unit_id
     * @return
     * @throws SQLException
     * @throws NoItemFoundException
     */
    public UnitPosition getLastUnitPosition(long unit_id) throws SQLException, NoItemFoundException {
        // pridat select i z units_to_groups
        String queryConf = "SELECT " + UnitPosition.SELECT
                + "FROM "+SENSLOG_SCHEMA_NAME+".last_units_positions WHERE unit_id =" + unit_id;
        ResultSet res = stmt.executeQuery(queryConf);
        if (res.next()) {
            return new UnitPosition(res);
        } else
            throw new NoItemFoundException("Last position for " + unit_id + " not found.");
    }
    
    /**
     * Method selects unit_position by given GID
     * @param gid - ID of position
     * @return UnitPosition object
     * @throws SQLException
     * @throws NoItemFoundException
     */
    public UnitPosition getPositionByGid(int gid) throws SQLException, NoItemFoundException {
        // pridat select i z units_to_groups
        String queryConf = "SELECT " + UnitPosition.SELECT
                + "FROM "+SENSLOG_SCHEMA_NAME+".units_positions WHERE gid = " + gid;
        ResultSet res = stmt.executeQuery(queryConf);
        if (res.next()) {
            return new UnitPosition(res);
        } else
            throw new NoItemFoundException("Last position for " + gid + " not found.");
    }

    public int changeUnitsTrackInterval(long unit_id, int milliseconds)
            throws SQLException {
        String queryConf = "update units_conf SET max_time_span = interval '"
                + milliseconds + " milliseconds' where unit_id=" + unit_id;
        return SQLExecutor.executeUpdate(queryConf);
    }

    public UnitHolder getUnitHolder(long unit_id) throws SQLException,
            NoItemFoundException {
        String queryConf = "SELECT "
                + UnitHolder.SELECT
                + "FROM unit_holders, units WHERE units.holder_id = unit_holders.holder_id AND "
                + "units.unit_id = " + unit_id;

        ResultSet res = stmt.executeQuery(queryConf);
        if (res.next()) {
            return new UnitHolder(res);
        } else
            throw new NoItemFoundException("getUnitHolder " + unit_id
                    + " not found.");
    }

    @SuppressWarnings("unchecked")
	public List<UnitDriver> getUnitDrivers(long unit_id) throws SQLException {

        String queryObservations = "select * "
                + " from unit_drivers, units_to_drivers "
                + "WHERE units_to_drivers.unit_id = " + unit_id
                + " AND units_to_drivers.driver_id = unit_drivers.driver_id ";
        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<UnitDriver>) generateObjectList(new UnitDriver(), res);
    }

    public LastPosition getLastPositionWithStatus(UnitPosition pos)
            throws SQLException {
        UnitPositionFactory pf = new UnitPositionFactory(pos);
        return pf.getLastPositionWithStatus();
    }

    public DBItemInfo getUnitInfo(String schema, String tableName, long id,
            String forigenKeyName) throws NoItemFoundException {
        return new DBItemInfo(schema, tableName, id, forigenKeyName);

    }

    public void setProvideAlert(long unit_id, boolean provide) throws SQLException, NoItemFoundException{
        String query = "UPDATE units_conf SET provide_alerts="+provide+" where unit_id = "+unit_id;
        int i = SQLExecutor.executeUpdate(query);
        if (i==0){
            throw new NoItemFoundException(String.valueOf(unit_id));
        }

        
    }
    
    /**
     * Method gets next value for unit_id, confirms if there is not any unit with same id in DB
     * @return new unit_id if there is not same unit in DB or null if it is not possible o select new unit_id
     * @throws SQLException
     */
    public Long getNextUnitID() throws SQLException{
        boolean exists = true;
        Long newId = null;
        while(exists == true){
            try{
                String selectId = "SELECT nextval('units_unit_id'::regclass);";
                ResultSet resId = SQLExecutor.getInstance().executeQuery(selectId);
                if(resId.next()){
                    newId = resId.getLong(1);
                }
                else{
                    return null;
                }
                Unit isSame = getUnit(newId);
                if(isSame==null){
                    exists = false;
                }
            } catch(SQLException e){
                throw new SQLException("Unit can't get new ID!");
            }
        }
        return newId;
    }
    
    public Unit getUnit(long unit_id) throws SQLException {
        String query = "SELECT holder_id, description FROM units WHERE unit_id = "+ unit_id;
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return new Unit(unit_id, res.getInt("holder_id"), res
                    .getString("description"));
        } else
            return null;
    }
    
    /**
     * Method selects unit if there is unit with given unitId paired with given groupId
     * @param unitId - id of unit
     * @param groupId - id of group
     * @return Unit object if there is the unit already in DB, null if there is not
     * @throws SQLException
     */
    public Unit getUnitByGroup(long unitId, int groupId) throws SQLException {
        String query = "SELECT holder_id, description FROM units u, units_to_groups utg"
                + " WHERE u.unit_id = "+ unitId
                + " AND utg.group_id = "+groupId
                + " AND utg.unit_id = u.unit_id;";
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return new Unit(unitId, res.getInt("holder_id"), res.getString("description"));
        } else
            return null;
    }

    public int getGroupID(long unit_id) throws SQLException {
        String query = "SELECT group_id FROM units_to_groups WHERE unit_id= "
                + unit_id + ";";
        ResultSet res = stmt.executeQuery(query);
        res.next();
        return res.getInt("group_id");
    }

    /**
     * function to get last valid ignition status for unit
     * 
     * @param unit_id
     *            unit to get status
     * @return IgnitionStatus valid for unit
     * @throws SQLException
     */
    public IgnitionStatus getValidIgnitionStatus(long unit_id)
            throws SQLException {
        String query = "SELECT * FROM last_ignition_status WHERE unit_id = "
                + unit_id + " ;";
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return new IgnitionStatus(res);
        } else {
            return null;
        }
    }
    
    public UnitPosition getPositionBefore(long unit_id, Date date)throws SQLException,
            NoItemFoundException {
        // pridat select i z units_to_groups
        String dstring = formater.format(date);
        
        String queryConf = "select " + UnitPosition.SELECT
                + " from units_positions where time_stamp < timestamp with time zone'" + dstring+"' ORDER BY time_stamp DESC LIMIT 1";
        ResultSet res = stmt.executeQuery(queryConf);
        if (res.next()) {
            return new UnitPosition(res);
        } else
            throw new NoItemFoundException("getPositionBefore " + unit_id + ", " + dstring
                    + " not found.");        
    }

    /**
     * Function to get last ignition status from observations before time of
     * unitPosition
     * 
     * @param pos
     *            UnitPosition
     * @return IgnitionStatus for unit before time of UnitPosition
     * @throws SQLException
     * @throws NoItemFoundException
     */
    public IgnitionStatus getLastIgnitionStatus(UnitPosition pos)
            throws SQLException, NoItemFoundException {
        String query = "SELECT observation_id, gid, observed_value, time_stamp, unit_id FROM observations"
                + " WHERE sensor_id = 330040000"
                + " AND unit_id = "
                + pos.getUnit_id()
                + " AND time_stamp < '"
                + pos.getTime_stamp()
                + "' "
                + " ORDER BY time_stamp DESC LIMIT 1 ;";
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return new IgnitionStatus(res);
        } else {
            throw new NoItemFoundException("getLastIgnitionStatus for "
                    + pos.getUnit_id() + " and " + pos.getTime_stamp()
                    + " does not exist!");
        }
    }

    /**
     * Function to get next ignition status from observations after time of
     * unitPosition
     * 
     * @param pos
     *            UnitPosition
     * @return IgnitionStatus for unit after time of UnitPosition
     * @throws SQLException
     * @throws NoItemFoundException
     */
    public IgnitionStatus getNextIgnitionStatus(UnitPosition pos)
            throws SQLException, NoItemFoundException {
        String query = "SELECT observation_id, gid, observed_value, time_stamp, unit_id FROM observations"
                + " WHERE sensor_id = 330040000"
                + " AND unit_id = "
                + pos.getUnit_id()
                + " AND time_stamp > '"
                + pos.getTime_stamp() + "' " + " ORDER BY time_stamp LIMIT 1 ;";
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return new IgnitionStatus(res);
        } else {
            throw new NoItemFoundException("getLastIgnitionStatus for "
                    + pos.getUnit_id() + " and " + pos.getTime_stamp()
                    + " does not exist!");
        }
    }
    
    /**
     * Method selects observed value for given unit, sensor and position
     * @param pos UnitPosition provides gid, unitId
     * @param sensorId identifier of sensor as long
     * @return Observed value as double
     * @throws SQLException
     */
    public double getObservationValueOnPosition(UnitPosition pos, long sensorId) throws SQLException{
        String query = "SELECT observed_value FROM observations"
                + " WHERE unit_id = "+pos.getUnit_id()
                + " AND sensor_id = "+sensorId
                + " AND gid = "+pos.getGid()+";";
        ResultSet res = stmt.executeQuery(query);
        if (res != null){
            if (res.next()) {
                double obsValue = res.getDouble("observed_value");
                return obsValue;
            }
            else{
                return Double.NaN;
            }
        }
        else {
            throw new SQLException("An Exception occurs when executing SQL command!");
        }
    }
    
    /**
     * Method gets last observation for given unit and sensor that not older than configuration time of unit 
     * @param pos UnitPosition contains unitId and last position timestamp
     * @param sensorId identifier of particular sensor
     * @param confTime configuration time of unit
     * @return last observation value as double if exists, NaN otherwise
     * @throws SQLException
     */
    public double getLastObservationValueInConfTime(UnitPosition pos, long sensorId, int confTime) throws SQLException{
        String query = "SELECT observed_value, time_stamp FROM observations"
                +" WHERE sensor_id = "+sensorId+" AND unit_id = "+pos.getUnit_id()
                +" GROUP BY observed_value, time_stamp"
                +" HAVING (EXTRACT (epoch FROM (timestamp '"+pos.getTime_stamp()+"' - time_stamp))) < integer '"+confTime+"' "
                +" ORDER BY time_stamp DESC LIMIT 1;";
        ResultSet res = stmt.executeQuery(query);
        if (res != null){
            if (res.next()) {
                double obsValue = res.getDouble("observed_value");
                return obsValue;
            }
            else{
                return Double.NaN;
            }
        }
        else {
            throw new SQLException("An Exception occurs when executing SQL command!");
        }
    }
    
    /**
     * Method gets description from agricultural machinery table by given identifier  
     * @param deviceId - identifier of the device
     * @return description as String
     * @throws SQLException
     */
    public String getDescriptionOfRfid(long deviceId) throws SQLException{
        String query = "SELECT description_cze FROM agricultural_machinery WHERE id="+deviceId;
        ResultSet res = stmt.executeQuery(query);
        if (res != null){
            if (res.next()) {
                String deviceDesc = res.getString("description_cze");
                return deviceDesc;
            }
            else{
                return "";
            }
        }
        else {
            throw new SQLException("An Exception occurs when executing SQL command!");
        }
    }
    /*
     * public Integer getTrackByUnitAndTime(long unit_id, Date date) throws
     * SQLException, NoItemFoundException {
     * 
     * String query = "SELECT gid FROM units_tracks WHERE unit_id =" + unit_id +
     * " AND " + "track_start < " + toDateString(date) + " AND track_end >  " +
     * toDateString(date);
     * 
     * ResultSet res = stmt.executeQuery(query); if (res.next()) { return
     * res.getInt(1); } else throw new
     * NoItemFoundException("getTrackByUnitAndTime for " + unit_id +
     * " not found."); }
     */
}