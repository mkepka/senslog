/**
 * 
 */
package cz.hsrs.rest.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.Envelope2D;
import cz.hsrs.db.model.vgi.VgiMedia;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.util.VgiUtil;
import cz.hsrs.db.vgi.util.VgiMediaUtil;
import cz.hsrs.db.vgi.util.VgiObservationUtil;

/**
 * @author mkepka
 *
 */
public class VgiObservationRestUtil {
    
    private UserUtil userUt;
    private VgiObservationUtil oUtil;
    private VgiMediaUtil mUtil;
    
    public VgiObservationRestUtil(){
        userUt = new UserUtil();
        oUtil = new VgiObservationUtil();
        mUtil = new VgiMediaUtil();
    }
    
    /**
     * Method processes creating of new VgiObservation object
     * and inserts it to the DB 
     * @param timestampValue - time stamp when observation was obtained, mandatory
     * @param catValue - ID of VgiCategory, mandatory
     * @param descValue - description of VgiObservation, optional
     * @param attsValue - further attributes in JSON format as String
     * @param datasetIdValue - ID of VgiDataset, mandatory
     * @param unitId - ID of unit that has produced observation, mandatory
     * @param userName - name of user that produced the observation, mandatory
     * @param lonValue - Longitude of observation, mandatory
     * @param latValue - Latitude of observation, mandatory
     * @param fileInStream - InputStream with associated media, optional 
     * @param mediaType - Data type of associated media file, mandatory
     * @return ID of new VgiObservation object
     * @throws Exception
     */
    public int processInsertVgiObs(String timestampValue, Integer catValue, String descValue, String attsValue,
            Long unitId, String userName, Integer datasetId, String lonValue, String latValue, 
            InputStream fileInStream, String mediaType) throws Exception{
        int obsId = 0;
        
        // get userId from userName
        int userId = userUt.getUserId(userName);
        
        // check of unitId
        if(unitId == null){
            throw new Exception("ID of device has to be defined!");
        }
        else{
            // check of geometry
            if(lonValue != null && latValue != null && timestampValue != null){
                Date posDate = DateUtil.parseTimestamp(timestampValue);
                DatabaseFeedOperation.insertPosition(
                        unitId.longValue(), 
                        Double.valueOf(latValue), 
                        Double.valueOf(lonValue), 
                        posDate);
                
                // ins observation
                if(catValue != null && datasetId != null){
                    obsId = VgiObservationUtil.insertVgiObs(
                            DateUtil.formatSecsTZ.format(posDate), 
                            catValue, 
                            descValue, 
                            attsValue, 
                            unitId, 
                            userId, 
                            datasetId);
                } 
            }
            else{
                throw new Exception("Geometry of VgiObservation has to be defined!");
            }
        }
        if(fileInStream != null){
            try{
                insertImage(fileInStream, 0, obsId, mediaType);
            } catch(Exception e){
                if(!e.getMessage().equalsIgnoreCase("Any media was given!")){
                    throw new Exception (e.getMessage());
                }
            }
        }
        return obsId;
    }
    
    /**
     * Method processes updating of stored VgiObservation object
     * 
     * @param obsId - ID of stored VgiObservation object to be updated
     * @param timestampValue - time stamp when observation was obtained, mandatory
     * @param catValue - ID of VgiCategory, mandatory
     * @param descValue - description of VgiObservation, optional
     * @param attsValue - further attributes in JSON format as String
     * @param unitId - ID of unit that has produced observation, mandatory
     * @param userName - name of user that produced the observation, mandatory
     * @param datasetIdValue - ID of VgiDataset, mandatory
     * @param lonValue - Longitude of observation, mandatory
     * @param latValue - Latitude of observation, mandatory
     * @param fileInStream - InputStream with associated media, optional 
     * @param mediaType - Data type of associated media file, mandatory
     * @return true if VgiObservation was updated
     * @throws Exception 
     */
    public boolean processUpdateVgiObs(Integer obsId, String timestampValue,
            Integer catValue, String descValue, String attsValue,
            Long unitId, String userName, Integer datasetId,
            String lonValue, String latValue, InputStream fileInStream, String mediaType) throws Exception {
        // get userId from userName
        int userId = userUt.getUserId(userName);
        boolean updated = false;
        // check of unitId
        if(unitId == null){
            throw new Exception("ID of device has to be defined!");
        }
        else{
            // check the geometry
            if(lonValue != null && latValue != null && timestampValue != null){
                Date posDate = DateUtil.parseTimestamp(timestampValue);
                DatabaseFeedOperation.insertPosition(unitId.longValue(), Double.valueOf(latValue), Double.valueOf(lonValue), posDate);
                
                // update observation
                if(catValue != null && datasetId != null){
                    updated = VgiObservationUtil.updateVgiObs(
                            obsId,
                            DateUtil.formatSecsTZ.format(posDate), 
                            catValue, 
                            descValue, 
                            attsValue, 
                            unitId, 
                            userId, 
                            datasetId);
                } 
            }
            else{
                throw new Exception("Geometry of VgiObservation has to be defined!");
            }
        }
        if(fileInStream != null){
            try{
                insertImage(fileInStream, 0, obsId, mediaType);
            } catch(Exception e){
                if(!e.getMessage().equalsIgnoreCase("Any media was given!")){
                    throw new Exception (e.getMessage());
                }
            }
        }
        return updated;
    }
    
    /**
     * Method processes get specific VgiObservation object by given ID
     * in GeoJSON format
     * @param obsId - ID of VgiObservation object
     * @param username - name of user
     * @return VgiObservation object in GeoJSON format
     * @throws NoItemFoundException
     * @throws SQLException
     */
    public JSONObject processGetVgiObservationAsJson(Integer obsId, String username) throws NoItemFoundException, SQLException{
        try{
            int userId = userUt.getUserId(username);
            return oUtil.getVgiObservationByObsIdAsGeoJSON(obsId, userId);
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes get List of VgiObservation objects 
     * @param userName
     * @return
     * @throws NoItemFoundException
     * @throws SQLException
     */
    public List<VgiObservation> processGetVgiObservationsByUser(String userName, String fromTime, String toTime, 
            Integer datasetId, Integer categoryId, String extentArr) throws SQLException{
        try{
            int userId = userUt.getUserId(userName);
            String from = null;
            String to = null;
            if(fromTime != null){
                Date fromDate = DateUtil.parseTimestamp(fromTime);
                from = DateUtil.formatMiliSecsTZ.format(fromDate);
            }
            if(toTime != null){
                Date toDate = DateUtil.parseTimestamp(toTime);
                to = DateUtil.formatMiliSecsTZ.format(toDate);
            }
            List<VgiObservation> obsList = null;
            if(datasetId == null && categoryId == null){
                obsList = oUtil.getVgiObservationsByUser(userId, from, to);
            }
            else if(datasetId != null && categoryId == null){
                obsList = oUtil.getVgiObservationsByUserByDataset(userId, datasetId, from, to);
            }
            else if(datasetId == null && categoryId != null){
                obsList = oUtil.getVgiObservationsByUserByCategory(userId, categoryId, from, to);
            }
            else if(datasetId == null && categoryId == null && extentArr != null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByExtent(userId, extent, from, to);
            }
            else if(datasetId != null && categoryId == null && extentArr != null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByDatasetByExtent(userId, datasetId, extent, from, to);
            }
            else if(datasetId != null && categoryId != null && extentArr != null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByCategoryByDatasetByExtent(userId, categoryId, datasetId, extent, fromTime, toTime);
            }
            else{
                // next filters
            }
            
            return obsList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        } catch (ParseException e) {
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes select of VgiObservation objects by given filter parameters as GeoJSON
     * @param userName - name of user that owns VgiObservation objects
     * @param fromTime - beginning of time range, ISO 8601 pattern, optional
     * @param toTime - end of time range, ISO 8601 pattern, optional
     * @param datasetId - ID of VgiDataset, optional
     * @param categoryId - ID of VgiCategeory, optional
     * @return VgiObservation objects in GeoJSON format
     * @throws SQLException
     */
    public JSONObject processGetVgiObservationsByUserAsJson(String userName, String fromTime, String toTime, 
            Integer datasetId, Integer categoryId, String extentArr) throws SQLException{
        try{
            int userId = userUt.getUserId(userName);
            String from = null;
            String to = null;
            if(fromTime != null){
                Date fromDate = DateUtil.parseTimestamp(fromTime);
                from = DateUtil.formatMiliSecsTZ.format(fromDate);
            }
            if(toTime != null){
                Date toDate = DateUtil.parseTimestamp(toTime);
                to = DateUtil.formatMiliSecsTZ.format(toDate);
            }
            List<JSONObject> obsList = null;
            if(datasetId == null && categoryId == null && extentArr == null){
                obsList = oUtil.getVgiObservationsByUserAsJSON(userId, from, to);
            }
            else if(datasetId != null && categoryId == null && extentArr == null){
                obsList = oUtil.getVgiObservationsByUserByDatasetAsJSON(userId, datasetId, from, to);
            }
            else if(datasetId == null && categoryId != null && extentArr == null){
                obsList = oUtil.getVgiObservationsByUserByCategoryAsJSON(userId, categoryId, from, to);
            }
            else if(datasetId == null && categoryId == null && extentArr != null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByExtentAsJSON(userId, extent, from, to);
            }
            else if(datasetId != null && categoryId == null && extentArr != null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByDatasetByExtentAsJSON(userId, datasetId, extent, from, to);
            }
            else if(datasetId != null && categoryId != null && extentArr != null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByCategoryByDatasetByExtentAsJSON(userId, categoryId, datasetId, extent, fromTime, toTime);
            }
            else{
                // next filters
            }
            return VgiObservationUtil.convertListVgiObservations2GeoJSON(obsList);
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        } catch (JSONException e) {
            throw new SQLException(e.getMessage());
        } catch (ParseException e) {
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes deleting of given VgiObservation
     * @param obsId - ID of VgiObservation object to be deleted
     * @param userName - name of user that owns given VgiObservation object
     * @return true if VgiObservation was deleted
     * @throws Exception
     */
    public boolean processDeleteVgiObservation(int obsId, String userName) throws SQLException{
        try{
            // get userId from userName
            int userId = userUt.getUserId(userName);
            // check existence of the VgiObservation
            VgiObservation obs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(obs != null){
                boolean isDeleted = VgiObservationUtil.deleteVgiObservation(obsId);
                return isDeleted;
            }
            else{
                throw new SQLException("VGIObservation with given ID does not exist!");
            }
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method inserts new VgiMedia to DB
     * @param fileInStream
     * @param rotationAng
     * @param obsId - ID of associated VgiObservation object
     * @param mediaType - Data type of media
     * @return
     * @throws Exception
     */
    public boolean insertImage(InputStream fileInStream, int rotationAng, int obsId, String mediaType) throws Exception{
        if(fileInStream != null){
            try{
                BufferedImage photo = ImageIO.read(fileInStream);
                if(photo != null){
                    // rotate if necessary
                    BufferedImage rotatedImage;
                    if(rotationAng != 0){
                        rotatedImage = VgiUtil.rotate(photo, rotationAng);
                    }
                    else{
                        rotatedImage = photo;
                    }
                    //write picture to database
                    ByteArrayOutputStream baosRot = new ByteArrayOutputStream();
                    ImageIO.write(rotatedImage, "png", baosRot);
                    byte[] rotArr = baosRot.toByteArray();
                    InputStream isRot = new ByteArrayInputStream(rotArr);
                    VgiMediaUtil.insertVgiMedia(obsId, isRot, rotArr.length, mediaType);
                    return true;
                }
                else{
                    throw new Exception("Any media was given!");
                }
            } catch(IOException e){
                throw new Exception(e.getMessage());
            } catch (SQLException e) {
                throw new Exception(e.getMessage());
            }
        }
        else{
            throw new Exception("File must be given!");
        }
    }
    
    /**
     * Method processes inserting of additional media file to given master VgiObservation
     * @param obsId - ID of master VgiObservation
     * @param file - media file as InputStream 
     * @param mediaType - data type of media file
     * @param userName - name of user that owns master VgiObsrevation
     * @return true if media file was inserted
     * @throws SQLException
     */
    public boolean processInsertNextMedia(int obsId, InputStream file, String mediaType, String userName) throws Exception{
        try{
            // get userId from userName
            int userId = userUt.getUserId(userName);
            // check existence of master VgiObservation
            VgiObservation obs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(obs != null && file != null){
                boolean inserted = insertImage(file, 0, obsId, mediaType);
                return inserted;
            }
            else{
                throw new Exception("VgiObservation with given ID of  was not found!");
            }
        } catch(NoItemFoundException e){
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    /**
     * Method processes listing of all connected VgiMedia to given master VgiObservation
     * @param obsId - ID of master VgiObservation
     * @param userName -  name of user that owns VgiObservation
     * @return List of VgiMedia with metadata about connected VgiMedia file
     * @throws SQLException 
     */
    public List<VgiMedia> processListVgiMedia(Integer obsId, String userName) throws SQLException{
        try{
            // get userId from userName
            int userId = userUt.getUserId(userName);
            // check existence of the VgiObservation
            VgiObservation obs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(obs != null){
                List<VgiMedia> mediaList = mUtil.getVgiMediaInfo(obsId);
                return mediaList;
            }
            else{
                throw new SQLException("VGIObservation with given ID does not exist!");
            }
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes select of specified media file
     * @param obsId - ID of master VgiObservation
     * @param mediaId - ID of VgiMedia object
     * @param userName - name of user that owns master VgiObservation
     * @return VgiMedia object containing bytea with media file
     * @throws SQLException
     */
    public VgiMedia processGetVgiMedia(Integer obsId, Integer mediaId, String userName) throws SQLException{
        try{
            // get userId from userName
            int userId = userUt.getUserId(userName);
            // check existence of the VgiObservation
            VgiObservation obs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(obs != null){
                VgiMedia medium = mUtil.getVgiMedia(obsId, mediaId);
                return medium;
            }
            else{
                throw new SQLException("VGIObservation with given ID does not exist!");
            }
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }

    public boolean processDeleteVgiMedia(Integer obsId, Integer mediaId, String userName) throws SQLException {
        try{
            // get userId from userName
            int userId = userUt.getUserId(userName);
            // check existence of the VgiObservation
            VgiObservation obs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(obs != null){
                boolean isDeleted = VgiMediaUtil.deleteVgiMedia(obsId, mediaId);
                return isDeleted;
            }
            else{
                throw new SQLException("VGIObservation with given ID does not exist!");
            }
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
}