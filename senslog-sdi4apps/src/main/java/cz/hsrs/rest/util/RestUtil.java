package cz.hsrs.rest.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.util.VgiUtil;

/**
 * Utility class with methods for inserting new POI
 * @author mkepka
 *
 */
public class RestUtil {

    private static final int THUMBNAIL_RATIO = 8;
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ");
    private UserUtil userUt;
    
    public RestUtil(){
        this.userUt = new UserUtil();
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
     * @param themaClassValue
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
    public int processVgiObs(String timestampValue, Integer catValue, Integer themaClassValue, String descValue, String attsValue,
            Long unitId, String userName, Integer datasetId, String lonValue, String latValue, InputStream fileInStream) throws Exception{
        //boolean isPos = false;
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
            	Date posDate = format.parse(timestampValue);
                DatabaseFeedOperation.insertPosition(unitId.longValue(), Double.valueOf(latValue), Double.valueOf(lonValue), posDate);
                
                // ins observation
                if(catValue != null && datasetId != null){
                    obsId = VgiUtil.insertVgiObs(
                            timestampValue, 
                            catValue, 
                            themaClassValue, 
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
            insertImage(fileInStream, 0, obsId);
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
}