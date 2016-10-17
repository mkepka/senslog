package cz.hsrs.rest.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.VgiCategory;
import cz.hsrs.db.model.vgi.VgiDataset;
import cz.hsrs.db.model.vgi.VgiMedia;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.util.VgiUtil;
import cz.hsrs.db.vgi.util.VgiCategoryUtil;
import cz.hsrs.db.vgi.util.VgiDatasetsUtil;

/**
 * Utility class with methods for inserting new POI
 * @author mkepka
 *
 */
public class RestUtil {

    private static final int THUMBNAIL_RATIO = 8;
    //private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ");
    private UserUtil userUt;
    private VgiUtil vUtil;
    private VgiDatasetsUtil dUtil;
    private VgiCategoryUtil cUtil;
    
    public RestUtil(){
        this.userUt = new UserUtil();
        this.vUtil = new VgiUtil();
        this.dUtil = new VgiDatasetsUtil();
        this.cUtil = new VgiCategoryUtil();
    }
    
    public String testPoi(String testValue){
        if(testValue == null){
            return "empty";
        }
        else{
            String modif = testValue.toUpperCase();
            return modif;
        }
    }
    
    /**
     * TEST method to load image file from database
     * @param imageId id of image in database as String
     * @return return true if image was loaded from database and saved as file to disc, or false 
     */
    public boolean testPoiLoad(String imageId, String path){
        boolean loaded = false;
        File imageOut = VgiUtil.testSelectImage(Long.valueOf(imageId), path);
        VgiUtil.testSelectThumbnail(Long.valueOf(imageId), path);
        if(imageOut != null){
            if(imageOut.exists()){
                loaded = imageOut.exists();
            }
        }
        return loaded;
    }

    /**
     * Method processes parameters received from Servlet
     * 
     * @param titleValue String with value of Title parameter
     * @param descValue String with value of Description parameter
     * @param catValue String with value of Category parameter
     * @param statValue String with value of Status parameter
     * @param lonValue String with value of Longitude parameter
     * @param latValue String with value of Latitude parameter
     * @param timestampValue String with value of Timestamp parameter
     * @param startTimeValue String with value of StartTimestamp parameter
     * @param userName String with name of user
     * @param fileInStream InputStream with picture file 
     * @param fileSize size of picture file in bytes
     * @param rotationAng angle to rotate given picture
     * @return true if parameters were stored successfully, or false if not
     */
    public boolean processPoi(String titleValue, String descValue, String catValue, String statValue, String lonValue, String latValue, String timestampValue, String startTimeValue, String userName, InputStream fileInStream, long fileSize, int rotationAng){
        // fields of form
        try {
            if(titleValue != null && lonValue != null && latValue != null && userName != null && timestampValue != null){
                if(titleValue.isEmpty() || lonValue.isEmpty() || latValue.isEmpty() || userName.isEmpty() && timestampValue.isEmpty()){
                    return false;
                }
                else{
                    long poiId = VgiUtil.insertPoi(titleValue, descValue, catValue, statValue, lonValue, latValue, timestampValue, startTimeValue, userName);
                    System.out.println("New POI (id="+poiId+") inserted!");
                    
                    if(fileInStream != null && fileSize > 0){
                        BufferedImage photo = ImageIO.read(fileInStream);
                        
                        // rotate if necessary
                        BufferedImage rotatedImage;
                        if(rotationAng != 0){
                            rotatedImage = VgiUtil.rotate(photo, rotationAng);
                        }
                        else{
                            rotatedImage = photo;
                        }
                        
                        // create thumbnail
                        int tW = rotatedImage.getWidth()/THUMBNAIL_RATIO;
                        int tH = rotatedImage.getHeight()/THUMBNAIL_RATIO;
                        BufferedImage thumb = VgiUtil.rescale(rotatedImage, tW, tH);
                        
                        //write to database picture and its thumbnail
                        ByteArrayOutputStream baosRot = new ByteArrayOutputStream();
                        ImageIO.write(rotatedImage, "png", baosRot);
                        byte[] rotArr = baosRot.toByteArray();
                        InputStream isRot = new ByteArrayInputStream(rotArr);
                        long imageId = VgiUtil.insertPoiImage(poiId, isRot, rotArr.length);
                        
                        ByteArrayOutputStream baosThumb = new ByteArrayOutputStream();
                        ImageIO.write(thumb, "png", baosThumb);
                        byte[] thumbArr = baosThumb.toByteArray();
                        InputStream isThumb = new ByteArrayInputStream(thumbArr);
                        VgiUtil.insertPoiImageThumbnail(imageId, isThumb, thumbArr.length);
                        return true;
                    }
                    else{
                        return true;
                    }
                }
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    
    /**
     * 
     * @param timestampValue
     * @param catValue
     * @param descValue
     * @param attsValue
     * @param unitId
     * @param userName
     * @param datasetId
     * @param lonValue
     * @param latValue
     * @param fileInStream
     * @return
     * @throws NumberFormatException
     * @throws Exception
     */
    public int processVgiObs(String timestampValue, Integer catValue, String descValue, String attsValue,
            Long unitId, String userName, Integer datasetId, String lonValue, String latValue, InputStream fileInStream) throws Exception{
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
                DatabaseFeedOperation.insertPosition(unitId.longValue(), Double.valueOf(latValue), Double.valueOf(lonValue), posDate);
                
                // ins observation
                if(catValue != null && datasetId != null){
                    obsId = VgiUtil.insertVgiObs(
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
                throw new Exception("Geometry of POI has to be defined!");
            }
        }
        if(fileInStream != null){
            try{
                insertImage(fileInStream, 0, obsId);
            } catch(Exception e){
                if(!e.getMessage().equalsIgnoreCase("Any media was given!")){
                    throw new Exception (e.getMessage());
                }
            }
        }
        return obsId;
    }
    
    /**
     * 
     * @param fileInStream
     * @param rotationAng
     * @param poiId
     * @return true if image file was inserted, false otherwise
     * @throws Exception
     */
    public boolean insertImage(InputStream fileInStream, int rotationAng, int poiId) throws Exception{
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
                    //write to database picture
                    ByteArrayOutputStream baosRot = new ByteArrayOutputStream();
                    ImageIO.write(rotatedImage, "png", baosRot);
                    byte[] rotArr = baosRot.toByteArray();
                    InputStream isRot = new ByteArrayInputStream(rotArr);
                    VgiUtil.insertVgiMedia(poiId, isRot, rotArr.length, "image/png");
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
     * 
     * @param userName
     * @return
     * @throws NoItemFoundException
     * @throws SQLException
     * @throws ParseException 
     */
    public JSONObject getVgiObservationBeansByUser(String userName, String fromTime, String toTime, 
            Integer datasetId, Integer categoryId) throws NoItemFoundException, SQLException, ParseException{
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
        if(datasetId == null && categoryId == null){
            obsList = vUtil.getVgiObservationsByUserAsJSON(userId, from, to);
        }
        else if(datasetId != null && categoryId == null){
            obsList = vUtil.getVgiObservationsByUserByDatasetAsJSON(userId, datasetId, from, to);
        }
        else if(datasetId == null && categoryId != null){
            obsList = vUtil.getVgiObservationsByUserByCategoryAsJSON(userId, categoryId, from, to);
        }
        else{
        	// next filters
        }
        JSONObject featureCollection = new JSONObject();
        try {
            // Features
            JSONArray featureList = new JSONArray();
            featureList.addAll(obsList);
         // FeatureCollection
            featureCollection.put("type", "FeatureCollection");
            featureCollection.put("features", featureList);
        } catch (JSONException e) {
            throw new SQLException(e.getMessage());
        }
        return featureCollection;
    }
    
    public JSONObject getVgiObservation(Integer obsId, String username) throws NoItemFoundException, SQLException{
        int userId = userUt.getUserId(username);
    	return vUtil.getVgiObservationByObsIdAsJSON(obsId, userId);
    }
    
    /**
     * 
     * @param userName
     * @return
     * @throws NoItemFoundException
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUser(String userName) throws NoItemFoundException, SQLException{
        int userId = userUt.getUserId(userName);
        List<VgiObservation> obsList = vUtil.getVgiObservationsByUser(userId);
        return obsList;
    }
    
    /**
     * 
     * @return
     * @throws SQLException
     */
    public List<VgiCategory> getVgiCategories() throws SQLException{
        List<VgiCategory> catList = cUtil.getCategoriesList();
        return catList;
    }
    
    public List<VgiDataset> getVgiDatasets(String userName) throws SQLException{
        try{
            int userId = userUt.getUserId(userName);
            List<VgiDataset> datList = dUtil.getDatasetsList(userId);
            return datList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method creates new object of VgiDataset
     * and inserts it to the DB
     * @param dataset object as JSON
     * @return new ID of the dataset in DB
     * @throws SQLException
     */
    public int insertVgiDataset(JSONObject dataset, String userName) throws SQLException {
        try{
            int userId = userUt.getUserId(userName);
            VgiDataset newDataset = new VgiDataset(dataset.getString("dataset_name"),
                    dataset.getString("description"), userId);
            int newId = newDataset.insertToDB();
            return newId;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        } catch (NoItemFoundException e) {
            throw new SQLException(e.getMessage());
        }
    }

    public boolean deleteVgiDataset(Integer datasetId, String userName) throws SQLException{
        try{
            int userId = userUt.getUserId(userName);
            int row = dUtil.deleteVgiDataset(userId, datasetId);
            if(row == 1){
            	return true;
            }
            else{
            	return false;
            }
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        } catch (NoItemFoundException e) {
            throw new SQLException(e.getMessage());
        }
    }
    /**
     * 
     * @param obsId
     * @param timestampValue
     * @param catValue
     * @param descValue
     * @param attsValue
     * @param unitIdValue
     * @param userName
     * @param datasetIdValue
     * @param lonValue
     * @param latValue
     * @param fileInStream
     * @return
     * @throws Exception 
     */
    public boolean updateVgiObs(Integer obsId, String timestampValue,
            Integer catValue, String descValue, String attsValue,
            Long unitId, String userName, Integer datasetId,
            String lonValue, String latValue, InputStream fileInStream) throws Exception {
        // get userId from userName
        int userId = userUt.getUserId(userName);
        boolean updated = false;
        // check of unitId
        if(unitId == null){
            throw new Exception("ID of device has to be defined!");
        }
        else{
            // check of geometry
            if(lonValue != null && latValue != null && timestampValue != null){
                Date posDate = DateUtil.parseTimestamp(timestampValue);
                DatabaseFeedOperation.insertPosition(unitId.longValue(), Double.valueOf(latValue), Double.valueOf(lonValue), posDate);
                
                // ins observation
                if(catValue != null && datasetId != null){
                    updated = VgiUtil.updateVgiObs(
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
                throw new Exception("Geometry of POI has to be defined!");
            }
        }
        if(fileInStream != null){
            try{
                insertImage(fileInStream, 0, obsId);
            } catch(Exception e){
                if(!e.getMessage().equalsIgnoreCase("Any media was given!")){
                    throw new Exception (e.getMessage());
                }
            }
        }
        return updated;
    }
    
    public List<VgiMedia> getVgiMedia(int obsId) throws SQLException{
        return vUtil.getVgiMedia(obsId);
    }
    
    public void processCitiSense() throws Exception{
        File jsonFile = new File("test/citi-sense_at8_07-08.json");
        BufferedReader buff = new BufferedReader(new FileReader(jsonFile));
        StringBuffer strBuff = new StringBuffer();
        String line;
        while((line = buff.readLine()) != null){
            strBuff.append(line);
        }
        
        String content = strBuff.toString();
        JSONArray jsonArr = JSONArray.fromObject(content);
        for(int i = 0; i < jsonArr.size(); i++){
            JSONObject meas = jsonArr.getJSONObject(i);
            JSONObject atts = new JSONObject();
            atts.accumulate("name", "AT_8 measurement");
            atts.accumulate("observedproperty", meas.getString("observedproperty"));
            atts.accumulate("value", meas.getDouble("value"));
            atts.accumulate("uom", meas.getString("uom"));
            int obs_id = processVgiObs(meas.getString("measure_time"), 2, "measurement from CITI-sense", atts.toString(), 8L, "citi", 1, "10.48154", "59.41901", null);
            System.out.println(obs_id);
        }
        buff.close();
    }
}