package cz.hsrs.db.util.factory;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.hsrs.db.model.AlertEvent;
import cz.hsrs.db.model.IgnitionStatus;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.LastPosition;
import cz.hsrs.db.util.AlertUtil;
import cz.hsrs.db.util.SensorUtil;
import cz.hsrs.db.util.UnitUtil;

/**
 * Factory class - prida infomace k UnitPosition o tom jestli jednotka jela, mela nastartovano, je stale online apod...
 * @author jezekjan
 *
 */
public class UnitPositionFactory {

    private LastPosition lp;
    private final long rfidSensorId = 680010000;

    /**
     * 
     * @param pos
     * @throws SQLException
     */
    public UnitPositionFactory(UnitPosition pos) throws SQLException{
        UnitUtil ut = new UnitUtil();
        AlertUtil aUtil = new AlertUtil();
        SensorUtil sUtil = new SensorUtil();

        int confTime = 0;
        try {
            confTime = ut.getUnitConfTimeById(pos.getUnit_id());
        } catch (NoItemFoundException e) {
            throw new SQLException(e.getMessage());
        }
        boolean isRunning = Boolean.FALSE;
        boolean isOnline = Boolean.FALSE;
        Map<String, Object> map = new HashMap<String, Object>();

        /**
         * Ma-li auto ignition tak dopln infomaci o klicku... kdyz je klicek on tak auto jede
         */
        IgnitionStatus ignition = ut.getValidIgnitionStatus(pos.getUnit_id());
        if (ignition!=null) {
            boolean ignitionOn = ignition.isIgnitionOn();
            map.put("ignition_on", ignitionOn);
            isRunning = ignitionOn;
        } 
        /**
         * Kdyz auto nema senzor na klicek urci se jestli jede podle casu posledni pozice
         */
        else {
            /** Kdyz je posledni pozice starsi nez confTime tak auto nejede */
            if ((new Date()).getTime() - pos.internalGetTimestamp().getTime() <= (confTime * 1000)) {
                /** TODO - zjistit jak moc se auto skutecne hejbe */
                //UnitPosition posbefore = ut.getPositionBefore(pos.getUnit_id(), pos.internalGetTime_stamp());
                isRunning = true;
            } else {
                isRunning = false;
            }
        }
        map.put(LastPosition.IS_RUNNING, isRunning);

        /**
         * Kdyz auto neposila po urcitou dobu pozice (5* conftime), bere se jako offline
         */
        if (((new Date()).getTime() - pos.internalGetTimestamp().getTime()) <= 24 * 3600 * 1000) {
            isOnline = true;
        } else {
            isOnline = false;
        }
        map.put("is_online", isOnline);

        /**
         * Pokud má jednotka RFID senzor, zjistit poslední připojené zařízení
         */
        /*Nutno zajistit synchronizaci mezi poslední pozicí a observací, jinak nelze vybírat podle gid*/
        /*if (sUtil.hasSensor(pos.getUnit_id(), rfidSensorId)){
            double rfidValue = ut.getObservationValueOnPosition(pos, rfidSensorId);
            boolean isNaN = Double.isNaN(rfidValue);
            if(isNaN == false){
                long rfidValueL = (long) rfidValue;
                map.put("rfid_value", String.valueOf(rfidValueL));
            }
        }*/
        /*Pouzivat pokud neni synchronizace mezi skutecnou posledni pozici a pozici u observace*/
        if (sUtil.hasSensor(pos.getUnit_id(), rfidSensorId)){
            double rfidValue = ut.getLastObservationValueInConfTime(pos, rfidSensorId, confTime);
            boolean isNaN = Double.isNaN(rfidValue);
            if(isNaN == false){
                long rfidValueL = (long) rfidValue;
                String rfidDesc = ut.getDescriptionOfRfid(rfidValueL);
                map.put("rfid_value", String.valueOf(rfidValueL));
                map.put("rfid_desc", rfidDesc);
            }
        }
        
        /**
         * alerty vlozim pouze pokud jednotka alerty poskytuje
         */
        if (aUtil.provideAlerts(pos.getUnit_id())){
            List<AlertEvent> events = aUtil.getUnsolvedAlertEvents(pos.getUnit_id());
            lp = new LastPosition(pos, map, events);
        } 
        else {
            lp = new LastPosition(pos, map, new LinkedList<AlertEvent>());
        }
    }

    public LastPosition getLastPositionWithStatus(){
        return lp;
    }
}