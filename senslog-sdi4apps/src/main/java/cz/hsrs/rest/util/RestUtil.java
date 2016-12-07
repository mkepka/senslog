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

import javax.imageio.ImageIO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.util.VgiUtil;

/**
 * Utility class with methods for inserting new POI
 * @author mkepka
 *
 */
public class RestUtil {

    private static final int THUMBNAIL_RATIO = 8;
    
    public RestUtil(){
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
    
    public void processCitiSense() throws Exception{
        File jsonFile = new File("test/citi-sense_at8_07-08.json");
        BufferedReader buff = new BufferedReader(new FileReader(jsonFile));
        StringBuffer strBuff = new StringBuffer();
        String line;
        while((line = buff.readLine()) != null){
            strBuff.append(line);
        }
        VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
        String content = strBuff.toString();
        JSONArray jsonArr = JSONArray.fromObject(content);
        for(int i = 0; i < jsonArr.size(); i++){
            JSONObject meas = jsonArr.getJSONObject(i);
            JSONObject atts = new JSONObject();
            atts.accumulate("name", "AT_8 measurement");
            atts.accumulate("observedproperty", meas.getString("observedproperty"));
            atts.accumulate("value", meas.getDouble("value"));
            atts.accumulate("uom", meas.getString("uom"));
            VgiObservation obs = orUtil.processInsertVgiObs(
            		meas.getString("measure_time"),
            		2,
            		"measurement from CITI-sense",
            		atts.toString(),
            		"8L",
            		null,
            		"citi",
            		1,
            		"10.48154", 
            		"59.41901",
            		null,
            		null, 
            		null, 
            		null);
            System.out.println(obs.getObsVgiId());
        }
        buff.close();
    }
}