package cz.hsrs.db.model.insert;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Sensor;
import cz.hsrs.db.model.Unit;
import cz.hsrs.db.util.UnitUtil;

/**
 * 
 * @author mkepka
 *
 */
public class UnitInsert {

    private Long unitId;
    private String description;
    private List<Sensor> sensors;
    
    /**
     * Constructor creates object from attributes
     * mainly used to insert new unit into DB
     * @param description
     * @param sensors
     */
    public UnitInsert(String description, List<Sensor> sensors) {
        this.unitId = null;
        this.description = description;
        this.sensors = sensors;
    }
    
    /**
     * Constructor creates object from all attributes
     * 
     * @param description
     * @param sensors
     */
    public UnitInsert(Long unitId, String description, List<Sensor> sensors) {
        this.setUnitId(unitId);
        this.description = description;
        this.sensors = sensors;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getDescription() {
        return description;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }
    
    @Override
    public String toString() {
        return "[unitId=" + unitId + ", description=" + description
                + ", sensors=" + sensors + "]";
    }
    
    /**
     * Method creates JSONObject representing Unit object
     * @return
     */
    public JSONObject toJSON(){
        JSONObject unit = new JSONObject();
        unit.element("unit_id", this.unitId);
        unit.element("description", this.description);
        JSONArray sensArr = new JSONArray();
        for(int i = 0; i<this.sensors.size(); i++){
            sensArr.add(this.sensors.get(i));
        }
        unit.element("sensors", sensArr);
        return unit;
    }

    /**
     * Method inserts new unit to the DB
     * @param groupId - id of group to be paired with
     * @return UnitInsert object that were inserted in DB
     * @throws SQLException
     * @throws NoItemFoundException 
     */
    public UnitInsert insertUnitToDB(int groupId) throws SQLException, NoItemFoundException{
        UnitUtil uUtil = new UnitUtil();
        Unit unitDB = null;
        Unit unitDBinG = null;
        if(this.unitId == null){
            this.unitId = uUtil.getNextUnitID();
        }
        /** check if there is same unit already in DB */
        else{
            unitDB = uUtil.getUnit(this.unitId);
            unitDBinG = uUtil.getUnitByGroup(this.unitId, groupId);
        }
        /** there is not same unit in DB */
        if(unitDBinG == null && unitDB == null){
            uUtil.insertUnit(this.unitId, this.description);        
            uUtil.pairUnitToGroup(this.unitId, groupId);
            
            List<Sensor> insSensors = new LinkedList<Sensor>();        
            for(int s = 0; s < this.sensors.size(); s++){
                Sensor insSen = sensors.get(s).insertToDb(this.unitId);
                insSensors.add(insSen);
            }
            this.sensors = insSensors;
            return this;
        /** there is same unit in DB but not paired with this user */    
        } else if(unitDBinG == null && unitDB != null){
            uUtil.pairUnitToGroup(this.unitId, groupId);
            List<Sensor> insSensors = new LinkedList<Sensor>();        
            for(int s = 0; s < this.sensors.size(); s++){
                Sensor insSen = sensors.get(s).insertToDb(this.unitId);
                insSensors.add(insSen);
            }
            this.sensors = insSensors;
            return this;
        /** there is same unit in DB and it is paired to this user */     
        } else{
            List<Sensor> insSensors = new LinkedList<Sensor>();        
            for(int s = 0; s < this.sensors.size(); s++){
                Sensor insSen = sensors.get(s).insertToDb(this.unitId);
                insSensors.add(insSen);
            }
            this.sensors = insSensors;
            return this;
        }
    }
}