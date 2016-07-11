package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.model.Phenomenon;
import cz.hsrs.db.model.Sensor;
import cz.hsrs.db.model.composite.AggregateObservation;
import cz.hsrs.db.model.composite.ObservationValue;
import cz.hsrs.db.model.composite.UnitSensor;
import cz.hsrs.db.model.composite.UnitSensorObservation;
import cz.hsrs.db.pool.SQLExecutor;

public class SensorUtil extends TrackUtil {

    public SensorUtil() {
        super();
    }

    public List<UnitSensor> getUnitsSensors(long unit_id) throws SQLException {
        String queryObservations = "select *"
                + " from sensors, units_to_sensors "
                + "WHERE units_to_sensors.unit_id = " + unit_id
                + " AND units_to_sensors.sensor_id = sensors.sensor_id;";

        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<UnitSensor>) generateObjectList(new UnitSensor(), res);// (List<Group>)generateObjectList(new
        // Group(),
        // res);
        // writeJSON(writer, new Group(), res);
        /** Do one object */

    }
    
    public List<Sensor> getSensors(long unit_id) throws SQLException {
        String queryObservations = "select *"
                + " from sensors, units_to_sensors "
                + "WHERE units_to_sensors.unit_id = " + unit_id
                + " AND units_to_sensors.sensor_id = sensors.sensor_id order by sensors.sensor_id;";

        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<Sensor>) generateObjectList(new Sensor(), res);// (List<Group>)generateObjectList(new
        // Group(),
        // res);
        // writeJSON(writer, new Group(), res);
        /** Do one object */

    }

    /**
     * Method select Sensor from DB by given sensorId
     * @param sensor_id of Sensor to be selected
     * @return Sensor object from DB if there is in DB
     * @throws SQLException
     */
    public Sensor getSensorById(long sensor_id) throws SQLException {
        String queryObservations = "SELECT * FROM sensors WHERE sensor_id = " + sensor_id + ";";
        ResultSet res = stmt.executeQuery(queryObservations);
        if (res.next()) {
            return (Sensor) (new Sensor()).getDBObject(res);
        } else {
            throw new SQLException("Sensor " + sensor_id + " not found!");
        }
    }
    
    /**
     * Method checks if there is sensor in DB, given by Id or sensor name
     * @param sensorId - id of sensor
     * @param sensorName - name of sensor
     * @return Sensor object from DB or null if there is not sensor with given id nor name
     * @throws SQLException
     */
    public Sensor getSensorByIdOrName(Long sensorId, String sensorName) throws SQLException {
        if(sensorId == null && sensorName != null){
            String query = "SELECT * FROM sensors WHERE sensor_name = '" + sensorName + "';";
            ResultSet res = stmt.executeQuery(query);
            if (res.next()) {
                return (Sensor) (new Sensor()).getDBObject(res);
            } else {
                return null;
            }
        }
        else if(sensorId != null && sensorName == null){
            String query = "SELECT * FROM sensors WHERE sensor_id = " + sensorId + ";";
            ResultSet res = stmt.executeQuery(query);
            if (res.next()) {
                return (Sensor) (new Sensor()).getDBObject(res);
            } else {
                return null;
            }
        } else{
            throw new SQLException("Either sensor_id or sensor_name must be given!");
        }
    }
    
    /**
     * Method selects Sensor by given sensor_name, sensor_type and phenomenon_id
     * @param sensorName 
     * @param sensorType
     * @param phenId
     * @return Sensor object from DB if there is in DB, null if there is not
     * @throws SQLException
     */
    public Sensor getSensorByNameAndTypeAndPhen(String sensorName, String sensorType, String phenId) throws SQLException {
        String query = "SELECT * FROM sensors WHERE sensor_name = '" + sensorName + "'"
                + " AND sensor_type = '"+sensorType+"' AND phenomenon_id = '"+phenId+"';";
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return (Sensor) (new Sensor()).getDBObject(res);
        } else {
            return null;
        }
    }
    
    /**
     * Method gets next value of sensor_id sequence
     * @return next value of sensor_id or null if there is not possible to get next value
     * @throws SQLException
     */
    public Long getNextSensorId() throws SQLException{
        boolean exists = true;
        Long newId = null;
        while(exists == true){
            try{
                String selectId = "SELECT nextval('sensors_sensor_id_seq'::regclass);";
                ResultSet resId = SQLExecutor.getInstance().executeQuery(selectId);
                if(resId.next()){
                    newId = resId.getLong(1);
                }
                else{
                    throw new SQLException("Sensor can't get new ID!");
                }
                Sensor isSame = this.getSensorByIdOrName(newId, null);
                if(isSame == null){
                    exists = false;
                }
            } catch(SQLException e){
                throw new SQLException("Sensor can't get new ID!");
            }
        }
        return newId;
    }

    public boolean hasSensor(long unit_id, long sensor_id) throws SQLException {
        String queryObservations = "select *" + " from units_to_sensors "
                + "WHERE units_to_sensors.unit_id = " + unit_id
                + " AND units_to_sensors.sensor_id = " + sensor_id;

        ResultSet res = stmt.executeQuery(queryObservations);

        return res.next();
        /** Do one object */

    }

    public List<ObservationValue> getSensorObservations(long unit_id,
            long sensor_id, String from, String to) throws SQLException {
        if (from == null && to == null) {
            return getSensorObservations(unit_id, sensor_id);
        } else if (to == null && from != null) {
            return getSensorObservationsFrom(unit_id, sensor_id, from);
        } else if (to != null && from == null) {
            return getSensorObservationsTo(unit_id, sensor_id, to);
        } else {

            String queryObservations = "select gid, observed_value, time_stamp"
                    + " from observations WHERE unit_id = " + unit_id
                    + " AND sensor_id = " + sensor_id + " AND time_stamp > '"
                    + from + "'" + " AND time_stamp < '" + to + "'";
            ResultSet res = stmt.executeQuery(queryObservations);

            return (List<ObservationValue>) generateObjectList(
                    new ObservationValue(), res);
        }

    }

    public List<AggregateObservation> getSensorObservationsTrunc(long unit_id,
            long sensor_id, String from, String to, String trunc)
            throws SQLException {

        String queryObservations = "select"
                + " avg(observed_value) as avg_value, date_trunc('" + trunc
                + "', time_stamp) AS dtrunc, count(*) AS count "
                + " from observations " + "WHERE unit_id = " + unit_id
                + " AND sensor_id = " + sensor_id + " AND time_stamp >= '"
                + from + "'" + " AND time_stamp <= '" + to + "'"
                + " GROUP BY dtrunc ORDER BY dtrunc DESC;";

        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<AggregateObservation>) generateObjectList(
                new AggregateObservation(), res);
    }

    protected List<ObservationValue> getSensorObservationsFrom(long unit_id,
            long sensor_id, String from) throws SQLException {

        String queryObservations = "select gid, observed_value, time_stamp"
                + " from observations " + "WHERE unit_id = " + unit_id
                + " AND sensor_id = " + sensor_id + " AND time_stamp > '"
                + from + "'";
        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<ObservationValue>) generateObjectList(
                new ObservationValue(), res);

    }

    protected List<ObservationValue> getSensorObservationsTo(long unit_id,
            long sensor_id, String to) throws SQLException {

        String queryObservations = "select gid, observed_value, time_stamp"
                + " from observations " + "WHERE unit_id = " + unit_id
                + " AND sensor_id = " + sensor_id + " AND time_stamp < '" + to
                + "'";
        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<ObservationValue>) generateObjectList(
                new ObservationValue(), res);

    }

    public List<ObservationValue> getSensorObservations(long unit_id,
            long sensor_id) throws SQLException {
        String queryObservations = "select gid, observed_value, time_stamp"
                + " from observations " + "WHERE unit_id = " + unit_id
                + " AND sensor_id = " + sensor_id;

        ResultSet res = stmt.executeQuery(queryObservations);

        return (List<ObservationValue>) generateObjectList(
                new ObservationValue(), res);// (List<Group>)generateObjectList(new
        // Group(), res);
        // writeJSON(writer, new Group(), res);
        /** Do one object */

    }

    /**
     * Select phenomenon by given ID
     * @param id - phenomenonId of phenomenon to select
     * @return Phenomenon object if there is phenomenon in DB or null if there is not
     * @throws SQLException
     */
    public Phenomenon getPhenomenonById(String id) throws SQLException {
        String queryObservations = "SELECT * FROM phenomenons"
                + " WHERE phenomenon_id = '" + id + "';";
        ResultSet res = stmt.executeQuery(queryObservations);
        if (res.next()) {
            return new Phenomenon(res);
        } else
            return null;
        /** Do one object */
    }
    
    /**
     * Select phenomenon by given name
     * @param phenName - name of phenomenon to select
     * @return Phenomenon object if there is phenomenon in DB or null if there is not
     * @throws SQLException
     */
    public Phenomenon getPhenomenonByName(String phenName) throws SQLException{
        String query = "SELECT * FROM phenomenons WHERE phenomenon_name='"+phenName+"';";
        ResultSet res = SQLExecutor.getInstance().executeQuery(query);
        if(res.next()){
            return new Phenomenon(res);
        }
        else{
            return null;
        }
    }
    
    /**
     * Method get next value of phenomenonId sequence
     * @return next value of phenomenonId, null if there is not possible to get next value 
     * @throws SQLException
     */
    public String getNextPhenomenonId() throws SQLException{
        boolean exists = true;
        String newId = null;
        while(exists == true){
            try{
                String selectId = "SELECT nextval('phenomenons_id_seq'::regclass);";
                ResultSet res = SQLExecutor.getInstance().executeQuery(selectId);
                if(res.next()){
                    newId = res.getString(1);
                }
                else{
                    throw new SQLException("Phenomenon can't get new ID!");
                }
                Phenomenon isSame = getPhenomenonById(newId);
                if(isSame == null){
                    exists = false;
                }
            } catch(SQLException e){
                throw new SQLException("Phenomenon can't get new ID!");
            }
        }    
        return newId;
    }
    
    /**
     * Method checks if there is same phenomenon already in the DB
     * @param phen Phenomenon to be checked
     * @return Phenomenon object that is already in the DB, null if there is not any 
     * @throws SQLException
     */
    public Phenomenon isPhenomenonInDB(Phenomenon phen) throws SQLException{
        if(phen.internalGetPhenomenonId() != null){
            Phenomenon phenDB = getPhenomenonById(phen.internalGetPhenomenonId());
            return phenDB;
        } else if(phen.getPhenomenonName() != null && phen.getUnit() != null){
            Phenomenon phenDB = getPhenomenonByName(phen.getPhenomenonName());
            if(phenDB == null){
                return null;
            } else{
                if(phen.internalGetPhenomenonId() == null){
                    phen.internalSetPhenomenonId(phenDB.internalGetPhenomenonId());
                }
                return phenDB;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Method checks if there is already same sensor in the DB
     * @param sen Sensor to be checked
     * @return Sensor object that is already in the DB, null if there is not any
     * @throws SQLException 
     */
    public Sensor isSensorInDB(Sensor sen) throws SQLException{
        if(sen.getSensorId() != 0){
            Sensor senDB = getSensorByIdOrName(sen.getSensorId(), null);
            return senDB;
        } 
        else if(sen.getSensorName() != null 
                && sen.getSensorType() != null 
                && sen.getPhenomenon() != null){
            Sensor senDB = getSensorByIdOrName(null, sen.getSensorName());
            if(senDB != null){
                Sensor sensorSame = getSensorByNameAndTypeAndPhen(
                        sen.getSensorName(),
                        sen.getSensorType(),
                        sen.getPhenomenon().internalGetPhenomenonId());
                if(sensorSame == null){
                    throw new SQLException("Sensor with given name is already registered!");
                } else {
                    return sensorSame;
                }
            } else {
                return null;
            }
        } 
        else{
            return null;
        }
    }
    
    /**
     * Method try tests if there is same Sensor in the DB
     * @param sen Sensor object to be compared
     * @return Sensor object from DB if there is same as given, null if there is not same
     * @throws SQLException
     */
    public Sensor isSameSensorInDB(Sensor sen) throws SQLException{
        if(sen.getSensorId() != 0){
            Sensor senDB = getSensorByIdOrName(sen.getSensorId(), null);
            return senDB;
        } 
        else if(sen.getSensorName() != null 
                && sen.getSensorType() != null 
                && sen.getPhenomenon() != null){
            Sensor senDB = getSensorByIdOrName(null, sen.getSensorName());
            if(senDB != null){
                Sensor sensorSame = getSensorByNameAndTypeAndPhen(
                        sen.getSensorName(),
                        sen.getSensorType(),
                        sen.getPhenomenon().internalGetPhenomenonId());
                if(sensorSame == null){
                    throw new SQLException("Sensor with given name is already registered!");
                } else {
                    return sensorSame;
                }
            } else {
                return null;
            }
        } 
        else{
            return null;
        }
    }
    
    /**
     * Method checks if given sensor is paired with given unit
     * @param sensorId - id of sensor
     * @param unitId - id of unit
     * @return true if sensor is paired, false if not
     * @throws SQLException
     */
    public boolean isSensorPairedToUnit(long sensorId, long unitId) throws SQLException{
        String query = "SELECT sensor_id, unit_id FROM units_to_sensors WHERE unit_id ="+unitId+" AND sensor_id ="+sensorId+";";
        ResultSet res = SQLExecutor.getInstance().executeQuery(query);
        if(res.next()){
            return true;
        } else{
            return false;
        }
    }
    /**
     * Method inserts new sensor in DB
     * @param sen - Sensor to be inserted
     * @return either (1) the row count for SQL DML statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public int insertSensor(Sensor sen) throws SQLException{
        String ins = "INSERT INTO sensors (sensor_id, sensor_name, sensor_type, phenomenon_id)"
                + " VALUES ("+sen.getSensorId()
                +", '"+sen.getSensorName()
                +"', '"+sen.getSensorType()
                +"', '"+sen.getPhenomenon().internalGetPhenomenonId()+"');";
        return SQLExecutor.executeUpdate(ins);
    }
    
    /**
     * Method pairs unit to sensor 
     * @param unitId - id of unit
     * @param sensorId - id of sensor
     * @return either (1) the row count for SQL DML statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException
     */
    public int pairUnitToSensor(long unitId, long sensorId) throws SQLException{
        String insUS = "INSERT INTO units_to_sensors(unit_id, sensor_id) VALUES("+unitId+", "+sensorId+");";
        return SQLExecutor.executeUpdate(insUS);
    }
    
    /**
     * Method inserts new Phenomenon in DB
     * @param phen Phenomenon object to be inserted, all attributes must by given
     * @return either (1) the row count for SQL DML statements or (2) 0 for SQL statements that return nothing
     * @throws SQLException 
     */
    public int insertNewPhenomenon(Phenomenon phen) throws SQLException{
        String ins = "INSERT INTO phenomenons (phenomenon_id, phenomenon_name, unit)"
                + " VALUES ('"+phen.internalGetPhenomenonId()
                +"', '"+phen.getPhenomenonName()
                +"', '"+phen.getUnit()+"');";
        return SQLExecutor.executeUpdate(ins);
    }
    
    /**
     * Method gets last observation for given unit-sensor pair
     * @param unitId - identifier of unit
     * @param sensorId - identifier of sensor 
     * @return ResultSet object represents last observation for given unit-sensor pair
     * @throws SQLException
     */
    public List<ObservationValue> getSensorLastObservation(long unitId, long sensorId) throws SQLException {
        String query = "SELECT time_stamp, gid, observed_value"
                    + " FROM units_to_sensors uts"
                    + " LEFT JOIN observations o ON uts.last_obs = o.time_stamp"
                    + " WHERE uts.unit_id = "+unitId
                    + " AND uts.sensor_id = "+sensorId
                    + " AND uts.sensor_id = o.sensor_id;";
        ResultSet res = stmt.executeQuery(query);
        List<ObservationValue> obsList = new LinkedList<ObservationValue>();
        while(res.next()){
        	obsList.add(new ObservationValue(res.getDouble("observed_value"), res.getString("time_stamp"), res.getInt("gid")));
        }
        return obsList;
    }
    
    /**
     * Method gets list of last observations from all connected sensors for given unit
     * @param unitId - identifier of unit
     * @return list of UnitSensorObservation objects represents last observations from all connected sensors to given unit
     * @throws SQLException
     */
    public List<UnitSensorObservation> getUnitSensorsLastObservations(long unitId) throws SQLException{
        String query = "SELECT time_stamp, gid, observed_value, o.sensor_id, o.unit_id"
                    + " FROM units_to_sensors uts"
                    + " LEFT JOIN observations o ON uts.last_obs = o.time_stamp"
                    + " WHERE uts.unit_id = " + unitId
                    + " AND uts.sensor_id = o.sensor_id;";
        ResultSet res = stmt.executeQuery(query);
        List<UnitSensorObservation> obsList = new LinkedList<UnitSensorObservation>();
        while(res.next()){
        	obsList.add(new UnitSensorObservation(res));
        }
        return obsList;
    }
    
    /**
     * Method gets list of last observations from all connected sensors to all units belonging to given group
     * @param groupName - name of group
     * @return list of UnitSensorObservation objects represents last observations from all connected sensors to all units
     * belonging to given group
     * @throws SQLException
     */
    public List<UnitSensorObservation> getUnitsSensorsLastObservations(String groupName) throws SQLException{
        String query = "SELECT time_stamp, gid, observed_value, o.sensor_id, o.unit_id"
                    + " FROM groups g, units_to_groups utg, units_to_sensors uts"
                    + " LEFT JOIN observations o ON uts.last_obs = o.time_stamp"
                    + " WHERE g.group_name = '"+groupName+"'"
                    + " AND g.id = utg.group_id"
                    + " AND utg.unit_id = uts.unit_id"
                    + " AND uts.unit_id = o.unit_id"
                    + " AND uts.sensor_id = o.sensor_id"
                    + " ORDER BY uts.unit_id, uts.sensor_id;";
        ResultSet res = stmt.executeQuery(query);
        List<UnitSensorObservation> obsList = new LinkedList<UnitSensorObservation>();
        while(res.next()){
        	obsList.add(new UnitSensorObservation(res));
        }
        return obsList;
    }
    
    /**
     * Method gets list of last observations of given connected sensor of all units belonging to given group.
     * @param groupName - name of group
     * @param sensorId - identifier of sensor
     * @return list of UnitSensorObservation objects represents last observations for given connected sensors to all units
     * belonging to given group
     * @throws SQLException
     */
    public List<UnitSensorObservation> getUnitsSensorsLastObservations(String groupName, long sensorId) throws SQLException{
        String query = "SELECT time_stamp, gid, observed_value, o.sensor_id, o.unit_id"
                + " FROM groups g, units_to_groups utg, units_to_sensors uts"
                + " LEFT JOIN observations o ON uts.last_obs = o.time_stamp"
                + " WHERE g.group_name = '"+groupName+"'"
                + " AND g.id = utg.group_id"
                + " AND utg.unit_id = uts.unit_id"
                + " AND uts.sensor_id = "+sensorId
                + " AND uts.unit_id = o.unit_id"
                + " AND uts.sensor_id = o.sensor_id"
                + " ORDER BY uts.unit_id, uts.sensor_id;";
        ResultSet res = stmt.executeQuery(query);
        List<UnitSensorObservation> obsList = new LinkedList<UnitSensorObservation>();
        while(res.next()){
        	obsList.add(new UnitSensorObservation(res));
        }
        return obsList;
    }
}