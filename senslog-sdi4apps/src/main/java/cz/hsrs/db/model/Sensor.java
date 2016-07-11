package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.util.SensorUtil;

public class Sensor implements DBObject {

    private long sensorId;
    private String sensorName;
    private String sensorType;
    private Phenomenon phenomenon;

    /**
     * Constructor creates object with all attributes and Phenomenon object
     * @param sensorId
     * @param sensorName
     * @param sensorType
     * @param phenomenon
     */
    public Sensor(long sensorId, String sensorName, String sensorType, Phenomenon phenomenon) {
        super();
        this.sensorId = sensorId;
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.phenomenon = phenomenon;
    }
    
    /**
     * Empty constructor for serialization
     */
    public Sensor(){
    }
    
    /**
     * Constructor for inserting new Sensor to DB
     * @param sensorName
     * @param sensorType
     * @param phenomenon
     */
    public Sensor(String sensorName, String sensorType, Phenomenon phenomenon) {
        super();
        this.sensorName = sensorName;
        this.sensorType = sensorType;
        this.phenomenon = phenomenon;
    }

    @Override
    public DBObject getDBObject(ResultSet set) throws SQLException {
        this.sensorId = set.getLong("sensor_id");
        this.sensorName = set.getString("sensor_name");
        this.sensorType = set.getString("sensor_type");
        SensorUtil sUtil = new SensorUtil();
        this.phenomenon = sUtil.getPhenomenonById(set.getString("phenomenon_id"));
        return this;
    }

    public long getSensorId() {
        return sensorId;
    }
    public void internalSetSensorId(Long id){
        this.sensorId = id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getSensorType() {
        return sensorType;
    }

    public Phenomenon getPhenomenon() {
        return phenomenon;
    }
    
    @Override
    public String toString() {
        return "[sensorId=" + sensorId + ", sensorName=" + sensorName
                + ", sensorType=" + sensorType + ", phenomenon=" + phenomenon
                + "]";
    }

    /**
     * Method to insert new Sensor to DB
     * @return Sensor instance with generated ID
     * @throws SQLException if an error occurs during inserting
     * @throws NoItemFoundException 
     */
    public Sensor insertToDb(Long unitId) throws SQLException, NoItemFoundException {
        SensorUtil sUtil = new SensorUtil();
        if(this.sensorId != 0){
            Sensor senDB = sUtil.getSensorByIdOrName(sensorId, null);
            if(this.sensorName == null && this.sensorType == null && this.phenomenon == null){
                if(senDB == null){
                    throw new NoItemFoundException("Sensor with given ID="+this.sensorId+" was not found!");
                }
                else{
                    // there is sensor with same ID in DB
                    if(sUtil.isSensorPairedToUnit(sensorId, unitId) == false){
                        sUtil.pairUnitToSensor(unitId, sensorId);
                    }
                    return senDB;
                }
            }
            else if(this.sensorName != null && this.sensorType != null && this.phenomenon != null){
                if(senDB == null){
                    // insert new sensor + new phenomenon
                    this.phenomenon = this.phenomenon.insertToDb();
                    int i = sUtil.insertSensor(this);
                    if (i == 1){
                        sUtil.pairUnitToSensor(unitId, this.sensorId);
                        return this;
                    }
                    else{
                        throw new SQLException("Sensor was not inserted!");
                    }
                }
                else{
                    if(senDB.getSensorName().equalsIgnoreCase(sensorName) == true){
                        if(senDB.getSensorType().equalsIgnoreCase(sensorType) == true && this.phenomenon.internalGetPhenomenonId() != null){
                            if(senDB.getPhenomenon().internalGetPhenomenonId().equalsIgnoreCase(this.phenomenon.internalGetPhenomenonId())){
                                // there is sensor with same name, type and phenomenon Id in DB
                                if(sUtil.isSensorPairedToUnit(sensorId, unitId) == false){
                                    sUtil.pairUnitToSensor(unitId, sensorId);
                                }
                                return senDB;
                            }
                            else{
                                throw new SQLException("It is not possible to insert Sensor with given attributes!");
                            }
                        }
                        else{
                            throw new SQLException("It is not possible to insert Sensor with given attributes!");
                        }
                    }
                    else if(senDB.getSensorName().equalsIgnoreCase(sensorName)==false && senDB.getSensorType().equalsIgnoreCase(sensorType) == false){
                        throw new SQLException("It is not possible to insert Sensor with given attributes! There is Sensor with same ID.");
                    }
                    else{
                        throw new SQLException("It is not possible to insert Sensor with given attributes!");
                    }
                }
            }
            else{
                throw new SQLException("It is not possible to insert Sensor with given attributes!");
            }
        }
        else{
            Sensor senDB = sUtil.getSensorByIdOrName(null, sensorName);
            if(senDB == null){
                // new ID + insert new sensor + insert phenomenon
                this.phenomenon = this.phenomenon.insertToDb();
                this.sensorId = sUtil.getNextSensorId();
                int i = sUtil.insertSensor(this);
                if (i == 1){
                    sUtil.pairUnitToSensor(unitId, this.sensorId);
                    return this;
                }
                else{
                    throw new SQLException("Sensor was not inserted!");
                }
            }
            else{
                if(this.sensorType != null && this.phenomenon != null){
                // check if there is same phenomenon in DB
                    if(this.phenomenon.getPhenomenonName() != null && this.phenomenon.getUnit() != null){
                        Phenomenon phenDB = sUtil.getPhenomenonByName(this.phenomenon.getPhenomenonName());
                        if(phenDB != null){
                            if(this.phenomenon.getUnit().equalsIgnoreCase(phenDB.getUnit())){
                                this.phenomenon = phenDB;
                            }
                        }
                    }
                    if(senDB.getSensorType().equalsIgnoreCase(this.sensorType)==true && this.getPhenomenon().internalGetPhenomenonId() != null){
                        if(senDB.getPhenomenon().internalGetPhenomenonId().equalsIgnoreCase(this.phenomenon.internalGetPhenomenonId())){
                            // there is sensor with same name and type in DB
                            if(sUtil.isSensorPairedToUnit(senDB.getSensorId(), unitId) == false){
                                sUtil.pairUnitToSensor(unitId, senDB.getSensorId());
                            }
                            return senDB;
                        }
                        else{
                            throw new SQLException("It is not possible to insert Sensor with given attributes!");
                        }
                    }
                    else{
                        throw new SQLException("It is not possible to insert Sensor with given attributes!");
                    }
                }
                else{
                    throw new SQLException("It is not possible to insert Sensor with given attributes!");
                }
            }
        }
    }
}