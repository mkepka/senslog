/**
 * 
 */
package cz.hsrs.db.util;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Phenomenon;
import cz.hsrs.db.model.Sensor;
import cz.hsrs.db.model.insert.UnitInsert;


/**
 * @author mkepka
 *
 */
public class ManagementUtil extends DBUtil{

    public UnitInsert insertUnit(JSONObject payload, String username) throws NoItemFoundException, SQLException {
        if(payload.containsKey("unit")){
            JSONObject unitO = payload.getJSONObject("unit");
            Long unitIdVal = null;
            if(unitO.containsKey("unit_id")){
                unitIdVal = unitO.getLong("unit_id");
            }
            String descVal = null;
            if(unitO.containsKey("description")){
                descVal = unitO.getString("description");
            }
            List<Sensor> sensors = null;
            if(payload.containsKey("sensors")){
                JSONArray sensorsArr = payload.getJSONArray("sensors");
                sensors = processSensors(sensorsArr);
            }
            if(unitIdVal == null && descVal == null){
                throw new NoItemFoundException("Unit must be defined by ID or by description!");
            }
            else{
                UnitInsert unit = new UnitInsert(unitIdVal, descVal, sensors);
                Integer groupId = UserUtil.getUserGroupId(username);
                UnitInsert insertedUnit = unit.insertUnitToDB(groupId);
                //return insertedUnit.toJSON();
                return insertedUnit;
            }
        }
        else {
            throw new NoItemFoundException("Payload doesn't contain unit part!");
        }
    }
    
    private List<Sensor> processSensors(JSONArray sensArr) throws NoItemFoundException{
        List<Sensor> sensorsList = new LinkedList<Sensor>();
        for(int i = 0;i < sensArr.size(); i++){
            JSONObject sensorObj = sensArr.getJSONObject(i);
            Long sensorId = null;
            if(sensorObj.containsKey("sensor_id")){
                sensorId = sensorObj.getLong("sensor_id");
            }
            String sensorName = null;
            if(sensorObj.containsKey("sensor_name")){
                sensorName = sensorObj.getString("sensor_name");
            }
            String sensorType = null;
            if(sensorObj.containsKey("sensor_type")){
                sensorType = sensorObj.getString("sensor_type");
            }
            Phenomenon phen = null;
            if(sensorObj.containsKey("phenomenon")){ 
                JSONObject phenO = sensorObj.getJSONObject("phenomenon");
                String phenId = null;
                if(phenO.containsKey("phenomenon_id")){
                    phenId = phenO.getString("phenomenon_id");
                }
                String phenName = null;
                if(phenO.containsKey("phenomenon_name")){
                    phenName = phenO.getString("phenomenon_name");
                }
                String uom = null;
                if(phenO.containsKey("uom")){
                    uom = phenO.getString("uom");
                }
                if((phenName == null && uom != null) || (phenName != null && uom == null)){
                    throw new NoItemFoundException("Phenomenon must be defined by ID or by name and UoM!");
                } 
                else{
                    phen = new Phenomenon(phenId, phenName, uom, null);
                }
            }
            if(sensorName != null && sensorType != null && phen != null){
                if(sensorId != null){
                    sensorsList.add(new Sensor(sensorId, sensorName, sensorType, phen));
                }
                else{
                    sensorsList.add(new Sensor(sensorName, sensorType, phen));
                }
            }
            else{
                if(sensorId != null){
                    sensorsList.add(new Sensor(sensorId, null, null, null));
                }
                else{
                    throw new NoItemFoundException("Sensor must be defined by ID or by sensor_name, sensor_type and phenomenon!");
                }
            }
        }
        return sensorsList;
    }
}