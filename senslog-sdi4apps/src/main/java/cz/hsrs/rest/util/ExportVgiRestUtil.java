package cz.hsrs.rest.util;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.Envelope2D;
import cz.hsrs.db.model.vgi.VgiObservationRdf;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.vgi.util.VgiObservationUtil;
import cz.hsrs.rest.beans.VgiObservationRdfBean;

/**
 * Utility class processes requests received by ExportVgiRest class
 * @author mkepka
 *
 */
public class ExportVgiRestUtil {
    
    private UserUtil userUt;
    private VgiObservationUtil oUtil;
    
    public ExportVgiRestUtil(){
        userUt = new UserUtil();
        oUtil = new VgiObservationUtil();
    }

    /**
     * 
     * @param obsId
     * @param username
     * @return
     * @throws SQLException
     */
    public List<VgiObservationRdfBean> processGetObservationExport(int obsId, String username) throws SQLException{
        try{
            int userId = userUt.getUserId(username);
            VgiObservationRdf obs = oUtil.getVgiObservationByObsIdForRdf(obsId, userId);

            List<VgiObservationRdfBean> obsList = new LinkedList<VgiObservationRdfBean>();
            obsList.add(new VgiObservationRdfBean(
                    obs.getGeom(),
                    obs.getObsVgiId(),
                    (obs.getName() != null)?obs.getName():"Observation_"+obs.getObsVgiId(),
                    obs.getUnitId(),
                    obs.getDateString(),
                    obs.getCategoryId()));
            return obsList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * 
     * @param username
     * @param categoryId
     * @param datasetId
     * @param fromTime
     * @param toTime
     * @param extent
     * @param unitId
     * @return
     * @throws SQLException 
     */
    public List<VgiObservationRdfBean> processGetVgiObservationsExport(
            String username, Integer categoryId, Integer datasetId,
            String fromTime, String toTime, String extentArr, Long unitId) throws SQLException {
        try{
            int userId = userUt.getUserId(username);
            String from = null;
            String to = null;
            Envelope2D extent = null;
            if(fromTime != null){
                Date fromDate = DateUtil.parseTimestamp(fromTime);
                from = DateUtil.formatMiliSecsTZ.format(fromDate);
            }
            if(toTime != null){
                Date toDate = DateUtil.parseTimestamp(toTime);
                to = DateUtil.formatMiliSecsTZ.format(toDate);
            }
            if(extentArr != null && !extentArr.isEmpty()){
                extent = new Envelope2D(extentArr);
            }
            
            List<VgiObservationRdf> obsList = oUtil.getVgiObservationsByUserForRdf(userId, from, to,
                    categoryId, datasetId, extent, unitId);
            List<VgiObservationRdfBean> obsBeanList = new LinkedList<VgiObservationRdfBean>();
            if(!obsList.isEmpty()){
                for(int i = 0; i < obsList.size(); i++){
                    VgiObservationRdf obs = obsList.get(i);
                    obsBeanList.add(new VgiObservationRdfBean(
                            obs.getGeom(),
                            obs.getObsVgiId(),
                            (obs.getName() != null)?obs.getName():"Observation_"+obs.getObsVgiId(),
                            obs.getUnitId(),
                            obs.getDateString(),
                            obs.getCategoryId()));
                }
            }
            return obsBeanList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        } catch (ParseException e) {
            throw new SQLException(e.getMessage());
        }
    }
}