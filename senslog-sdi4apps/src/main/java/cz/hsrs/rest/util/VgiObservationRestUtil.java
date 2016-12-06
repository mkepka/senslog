/**
 * 
 */
package cz.hsrs.rest.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.vgi.Envelope2D;
import cz.hsrs.db.model.vgi.VgiMedia;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.util.UnitUtil;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.util.VgiUtil;
import cz.hsrs.db.vgi.util.VgiMediaUtil;
import cz.hsrs.db.vgi.util.VgiObservationUtil;

/**
 * Utility class processes requests received by VgiObservationRest class
 * @author mkepka
 *
 */
public class VgiObservationRestUtil {
    
    private UserUtil userUt;
    private UnitUtil unitUt;
    private VgiObservationUtil oUtil;
    private VgiMediaUtil mUtil;
    
    /**
     * Constructor instances related Utility classes
     */
    public VgiObservationRestUtil(){
        userUt = new UserUtil();
        unitUt = new UnitUtil();
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
     * @param unitId - ID of unit that has produced observation, mandatory if uuid is not present
     * @param uuidValue - ID of device that has produced observation, mandatory if unitId is not present 
     * @param userName - name of user that produced the observation, mandatory
     * @param lonValue - Longitude of observation, mandatory
     * @param latValue - Latitude of observation, mandatory
     * @param altValue - Altitude of observation, optional
     * @param dopValue - Dilution of precision, optional 
     * @param fileInStream - InputStream with associated media, optional 
     * @param mediaType - Data type of associated media file, mandatory
     * @return ID of new VgiObservation object
     * @throws Exception
     */
    public int processInsertVgiObs(String timestampValue, Integer catValue, String descValue, String attsValue,
            String unitIdValue, String uuidValue, String userName, Integer datasetId, String lonValue, String latValue, String altValue,
            String dopValue, InputStream fileInStream, String mediaType) throws Exception{
        int obsId = 0;
        int newGid = 0;
        
        // get userId from userName
        int userId = userUt.getUserId(userName);
        
        // check of unitId
        Long unitId = null;
        if((unitIdValue == null || unitIdValue.isEmpty()) && (uuidValue == null || uuidValue.isEmpty())){
            throw new Exception("ID of device has to be defined!");
        }
        else if((unitIdValue != null && !unitIdValue.isEmpty()) && (uuidValue == null || uuidValue.isEmpty())){
            unitId = Long.parseLong(unitIdValue);
        }
        else if((unitIdValue == null || unitIdValue.isEmpty()) && (uuidValue != null && !uuidValue.isEmpty())){
            unitId = new BigInteger(uuidValue, 16).longValue();
        }
        else{
            throw new Exception("Correct ID of device has to be defined!");
        }
        // check mandatory attributes and the geometry
        if(lonValue != null && 
            latValue != null &&
            timestampValue != null &&
            catValue != null &&
            datasetId != null){
            
            Date posDate = DateUtil.parseTimestamp(timestampValue);
            newGid = DatabaseFeedOperation.insertPositionByGid(
                        unitId.longValue(),
                        Double.valueOf(latValue), 
                        Double.valueOf(lonValue),
                        (altValue != null && !altValue.isEmpty()) ? Double.valueOf(altValue) : Double.NaN,
                        (dopValue != null && !dopValue.isEmpty()) ? Double.valueOf(dopValue) : Double.NaN,
                        posDate, 
                        Double.NaN, 
                        "4326");
            // ins observation
            if(newGid != 0){
                obsId = VgiObservationUtil.insertVgiObs(
                            newGid, 
                            DateUtil.formatSecsTZ.format(posDate), 
                            catValue, 
                            descValue, 
                            attsValue, 
                            unitId, 
                            userId, 
                            datasetId);
                if(fileInStream != null){
                    try{
                        insertMedia(obsId, fileInStream, mediaType);
                        //insertImage(fileInStream, 0, obsId, mediaType);
                    } catch(Exception e){
                        if(!e.getMessage().equalsIgnoreCase("Any media was given!")){
                            throw new Exception (e.getMessage());
                        }
                    }
                }
                return obsId;
            }
            else{
                throw new Exception("Mandatory attributes of VGIObservation have to be given!");
            }
        }
        else{
            throw new Exception("Mandatory attributes of VGIObservation have to be given!");
        }
    }
    
    /**
     * Method processes updating of stored VgiObservation object
     * 
     * @param obsId - ID of stored VgiObservation object to be updated
     * @param timestampValue - time stamp when observation was obtained, mandatory
     * @param catValue - ID of VgiCategory, mandatory
     * @param descValue - description of VgiObservation, optional
     * @param attsValue - further attributes in JSON format as String
     * @param unitIdValue - ID of unit that has produced observation, mandatory if uuid is not present
     * @param uuidValue - ID of device that has produced observation, mandatory if unitId is not present 
     * @param userName - name of user that produced the observation, mandatory
     * @param datasetIdValue - ID of VgiDataset, mandatory
     * @param lonValue - Longitude of observation, mandatory
     * @param latValue - Latitude of observation, mandatory
     * @param altValue - Altitude of observation, optional
     * @param dopValue - Dilution of precision, optional
     * @param fileInStream - InputStream with associated media, optional 
     * @param mediaType - Data type of associated media file, mandatory
     * @return true if VgiObservation was updated
     * @throws Exception 
     */
    public boolean processUpdateVgiObs(Integer obsId, String timestampValue, Integer catValue, String descValue,
            String attsValue, String unitIdValue, String uuidValue, String userName, Integer datasetId, String lonValue, String latValue, 
            String altValue, String dopValue, InputStream fileInStream, String mediaType) throws Exception {
        // get userId from userName
        int userId = userUt.getUserId(userName);
        boolean updated = false;
        // check of ID of device
        Long unitId = null;
        if((unitIdValue == null || unitIdValue.isEmpty()) && (uuidValue == null || uuidValue.isEmpty())){
            throw new Exception("ID of device has to be defined!");
        }
        else if((unitIdValue != null && !unitIdValue.isEmpty()) && (uuidValue == null || uuidValue.isEmpty())){
            unitId = Long.parseLong(unitIdValue);
        }
        else if((unitIdValue == null || unitIdValue.isEmpty()) && (uuidValue != null && !uuidValue.isEmpty())){
            unitId = new BigInteger(uuidValue, 16).longValue();
        }
        else{
            throw new Exception("Correct ID of device has to be defined!");
        }
        if(unitId != null) {
            // check if there is VgiObservation to update
            VgiObservation oldObs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(oldObs == null){
                throw new SQLException("VGI Observation with given ID does not exist!");
            }
            else {
                // check mandatory attributes and the geometry
                if(lonValue != null && 
                    latValue != null &&
                    timestampValue != null &&
                    catValue != null &&
                    datasetId != null){
                    
                    Date posDate = DateUtil.parseTimestamp(timestampValue);
                    UnitPosition oldPos = unitUt.getPositionByGid(oldObs.getGid());
                    boolean updatedPos = false;
                    
                    Double newLon = Double.parseDouble(lonValue);
                    Double newLat = Double.parseDouble(latValue);
                    Double newAlt = (altValue != null && !altValue.isEmpty()) ? Double.parseDouble(altValue) : Double.NaN;
                    Double newDop = (dopValue != null && !dopValue.isEmpty()) ? Double.parseDouble(dopValue) : Double.NaN;
                 // same position
                    if(oldPos.getX() == newLon &&
                        oldPos.getY() == newLat &&
                        oldPos.getAlt() == newAlt &&
                        oldPos.internalGetTimestamp() == posDate){
                        // not update position
                        updatedPos = true;
                    } else{
                        UnitPosition newPos = new UnitPosition(oldPos.getGid(),
                                oldPos.getUnit_id(), 
                                newLon,
                                newLat,
                                newAlt,
                                posDate,
                                newDop,
                                Double.NaN,
                                "4326"); 
                        updatedPos = DatabaseFeedOperation.updatePositionByGid(newPos);
                    }
                    // update observation
                    if(updatedPos){
                        updated = VgiObservationUtil.updateVgiObs(
                                obsId,
                                oldPos.getGid(),
                                DateUtil.formatSecsTZ.format(posDate), 
                                catValue,
                                descValue,
                                attsValue,
                                unitId,
                                userId,
                                datasetId);
                        if(fileInStream != null){
                            try{
                                insertImage(fileInStream, 0, obsId, mediaType);
                            } catch(Exception e){
                                if(!e.getMessage().equalsIgnoreCase("Any media was given!")){
                                    throw new Exception (e.getMessage());
                                }
                            }
                        }
                    }
                    else{
                        throw new Exception("VgiObservation cannot be updated!");
                    }
                }
                else{
                    throw new Exception("Mandatory attributes of VGIObservation have to be given!");
                }
            }
        }
        return updated;
    }
    
    /**
     * Method processes get VgiObservation object by given ID
     * @param obsId - ID of VgiObservation object
     * @param username - name of user
     * @return VgiObservation object in GeoJSON format
     * @throws NoItemFoundException
     * @throws SQLException
     */
    public VgiObservation processGetVgiObservation(int obsId, String username) throws NoItemFoundException, SQLException{
        try{
            int userId = userUt.getUserId(username);
            return oUtil.getVgiObservationByObsId(obsId, userId);
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
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
    public JSONObject processGetVgiObservationAsJson(int obsId, String username) throws NoItemFoundException, SQLException{
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
     * @param unitId - Id of device that produced VgiObservations
     * @return VgiObservation objects in GeoJSON format
     * @throws SQLException
     */
    public JSONObject processGetVgiObservationsByUserAsJson(String userName, String fromTime, String toTime, 
            Integer datasetId, Integer categoryId, String extentArr, Long unitId) throws SQLException{
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
            if(datasetId == null && categoryId == null && extentArr == null && unitId == null){
                obsList = oUtil.getVgiObservationsByUserAsJSON(userId, from, to);
            }
            else if(datasetId != null && categoryId == null && extentArr == null && unitId == null){
                obsList = oUtil.getVgiObservationsByUserByDatasetAsJSON(userId, datasetId, from, to);
            }
            else if(datasetId == null && categoryId != null && extentArr == null && unitId == null){
                obsList = oUtil.getVgiObservationsByUserByCategoryAsJSON(userId, categoryId, from, to);
            }
            else if(datasetId == null && categoryId == null && extentArr != null && unitId == null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByExtentAsJSON(userId, extent, from, to);
            }
            else if(datasetId == null && categoryId == null && extentArr == null && unitId != null){
                obsList = oUtil.getVgiObservationsByUserByUnitAsJSON(userId, unitId, from, to);
            }
            else if(datasetId != null && categoryId == null && extentArr != null && unitId == null){
                Envelope2D extent = new Envelope2D(extentArr);
                obsList = oUtil.getVgiObservationsByUserByDatasetByExtentAsJSON(userId, datasetId, extent, from, to);
            }
            else if(datasetId != null && categoryId != null && extentArr != null && unitId == null){
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
     * Method inserts new VgiMedia to DB
     * @param obsId - ID of associated VgiObservation object
     * @param fileInStream - InputStream containing associated MediaFile 
     * @param mediaType - MediaType of associated file
     * @return true if VgiMedia object was inserted
     * @throws Exception
     */
    public boolean insertMedia(int obsId, InputStream fileInStream, String mediaType) throws Exception{
        if(fileInStream != null){
            try{
                byte[] mediaArr = IOUtils.toByteArray(fileInStream);
                if(mediaArr != null && mediaArr.length != 0 
                    && mediaType != null && !mediaType.isEmpty()){
                    InputStream is = new ByteArrayInputStream(mediaArr);
                    VgiMediaUtil.insertVgiMedia(obsId, is, mediaArr.length, mediaType);
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
            throw new Exception("Any file must be given!");
        }
    }
    
    /**
     * Method processes update VgiMedia object in the DB
     * @param obsId - ID of associated VgiObservation object
     * @param mediaId - ID of VgiMedia object to be updated
     * @param fileInStream - InputStream containing associated MediaFile 
     * @param mediaType - MediaType of associated file
     * @return true if VgiMedia object was updated
     * @throws Exception 
     */
    public boolean processUpdateVgiMedia(Integer obsId, Integer mediaId, InputStream fileInStream, String userName, String mediaType) throws Exception {
        try{
            // get userId from userName
            int userId = userUt.getUserId(userName);
            // check existence of master VgiObservation
            VgiObservation obs = oUtil.getVgiObservationByObsId(obsId, userId);
            if(obs != null){
                // check existence of VgiMedia to be updated
                VgiMedia med = mUtil.getVgiMedia(obsId, mediaId);
                if(med != null){
                    byte[] mediaArr = IOUtils.toByteArray(fileInStream);
                    if(mediaArr != null){
                        InputStream is = new ByteArrayInputStream(mediaArr);
                        VgiMediaUtil.updateVgiMedia(mediaId, is, mediaArr.length, mediaType);
                        return true;
                    }
                    else{
                        throw new Exception("Any media was given!");
                    }
                }
                else{
                    throw new Exception("VgiMedia with given ID was not found!");
                }
            }
            else{
                throw new Exception("VgiObservation with given ID was not found!");
            }
        } catch(NoItemFoundException e){
            throw new Exception(e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
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