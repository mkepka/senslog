package cz.hsrs.db.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import cz.hsrs.db.pool.SQLExecutor;

public class VgiUtil {
	
	/*
    private static final String SCHEMA_NAME = "vgi";
    private static final String DATASET_TABLE_NAME = "vgi_datasets";
    */
    /**
     * Method inserts new POI to database and returns Id of this new POI
     * @param titleValue String with value of Title parameter - need not be NULL
     * @param descValue String with value of Description parameter
     * @param catValue String with value of Category parameter
     * @param statValue String value of Status parameter
     * @param lonValue String with value of Longitude parameter - need not be NULL
     * @param latValue String with value of Latitude parameter - need not be NULL
     * @param timestampValue String with value of Timestamp parameter - need not be NULL
     * @param startTimeValue String value of Start Timestamp parameter 
     * @param userId String with name of user -  need not be NULL
     * @return ID of new POI as long
     * @throws SQLException Throws SQLException if an exception occurs during inserting
     */
    public static long insertPoi(String titleValue, String descValue, String catValue, String statValue, String lonValue, String latValue, String timestampValue, String startTimeValue, String userId) throws SQLException{
        StringBuffer ins = new StringBuffer();
        ins.append("SELECT poi.add_poi('"+titleValue+"', ");
        if(descValue.isEmpty()){
            ins.append("NULL, ");
        }
        else{
            ins.append("'"+descValue+"', ");
        }
        ins.append("'"+catValue+"', ");
        ins.append("'"+timestampValue+"', ");
        ins.append("'"+userId+"', ");
        ins.append("'"+lonValue+"', ");
        ins.append("'"+latValue+"', ");
        if(startTimeValue.isEmpty()){
            ins.append("NULL, ");
        }
        else{
            ins.append("'"+startTimeValue+"', ");
        }
        ins.append("'"+statValue+"');");
        
        ResultSet rs = SQLExecutor.getInstance().executeQuery(ins.toString());
        if(rs.next()){ 
            return rs.getLong(1);
        }
        else{
            throw new SQLException("An error occurs during inserting of new POI!");
        }
    }
    
    /**
     * Method inserts new image connected to existing POI
     * @param poiId Id of existing POI in database as long
     * @param image InputStream with loaded image 
     * @param fileSize size of image file in bytes as long 
     * @throws SQLException Throws SQLException if an exception occurs during inserting
     */
    public static long insertPoiImage(long poiId, InputStream image, long fileSize) throws SQLException{
        Date newDate = new Date();
        long newImageId = newDate.getTime();
        String query = "INSERT INTO poi.poi_image(image_id, image, ogc_fid) VALUES("+newImageId+", ?, "+poiId+");";
        SQLExecutor.insertStream(query, image, fileSize);
        return newImageId;
    }
    
    /**
     * Method inserts new image thumbnail connected to existing POI
     * @param poiId Id of existing POI in database as long
     * @param image InputStream with loaded image 
     * @param fileSize size of image file in bytes as long 
     * @throws SQLException Throws SQLException if an exception occurs during inserting
     */
    public static void insertPoiImageThumbnail(long imageId, InputStream image, long fileSize) throws SQLException{
        String query = "UPDATE poi.poi_image SET thumbnail = ? WHERE image_id = "+imageId+";";
        SQLExecutor.insertStream(query, image, fileSize);
    }
    
    /**
     * TEST method
     * @param titleValue
     * @param descValue
     * @param catValue
     * @param lonValue
     * @param latValue
     * @param timestampValue
     * @param userId
     * @return
     * @throws SQLException
     */
    public static long testInsertPoi(String titleValue, String descValue, String catValue, String lonValue, String latValue, String timestampValue, String userId) throws SQLException{
        String query = "SELECT poi.add_poi('"+titleValue+"', " +
                "'"+descValue+"', " +
                "'"+catValue+"', " +
                "'"+timestampValue+"', " +
                "'"+userId+"', " +
                ""+lonValue+", " +
                ""+latValue+");";
        ResultSet rs = SQLExecutor.getInstance().executeQuery(query);
        if(rs.next()){
            long poiId = rs.getLong(1);
            return poiId;
        }
        else{
            throw new SQLException("An error occurs during inserting of new POI!");
        }
    }
    
    /**
     * TEST method
     * @param imageId
     * @return
     */
    public static File testSelectImage(long imageId, String path){
        String query = "SELECT image, ogc_fid FROM poi.poi_image WHERE image_id ="+imageId+";";
        
        //Image img = new Image("");
        File selectedImage = null;
        try {
            ResultSet rs = SQLExecutor.getInstance().executeQuery(query);
            if(rs.next()){
                InputStream is = rs.getBinaryStream("image");
                selectedImage = new File(path+"/POI_"+imageId+".jpg");
                FileOutputStream fOS = new FileOutputStream(selectedImage);
                IOUtils.copy(is, fOS);
                IOUtils.closeQuietly(fOS);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return selectedImage;
    }
    
    /**
     * TEST method
     * @param imageId
     * @return
     */
    public static File testSelectThumbnail(long imageId, String path){
        String query = "SELECT thumbnail, ogc_fid FROM poi.poi_image WHERE image_id ="+imageId+";";
        
        File selectedImage = null;
        try {
            ResultSet rs = SQLExecutor.getInstance().executeQuery(query);
            if(rs.next()){
                InputStream is = rs.getBinaryStream("thumbnail");
                selectedImage = new File(path+"/POI_"+imageId+"_thumbnail.jpg");
                FileOutputStream fOS = new FileOutputStream(selectedImage);
                IOUtils.copy(is, fOS);
                IOUtils.closeQuietly(fOS);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return selectedImage;
    }
    
    /**
     * Method rescale given image to given width and height 
     * @param masterImg
     * @param widthDest
     * @param heightDest
     * @return rescaled image
     */
    public static BufferedImage rescale(BufferedImage masterImg, int widthDest, int heightDest){
        Image resizedImg = masterImg.getScaledInstance(widthDest, heightDest, Image.SCALE_FAST);
        BufferedImage rBimg = new BufferedImage(widthDest, heightDest, masterImg.getType());
        Graphics2D g=rBimg.createGraphics();
        g.drawImage(resizedImg, 0, 0, null);
        g.dispose();
        
        return rBimg;
    }
    
    /**
     * Method rotated given image to given angle 
     * @param masterImg
     * @param angle
     * @return rotated image
     */
    public static BufferedImage rotate(BufferedImage masterImg, int angle){
        Dimension size = new Dimension(masterImg.getWidth(), masterImg.getHeight());
        int masterWidth = masterImg.getWidth();
        int masterHeight = masterImg.getHeight();
        
        double x = 0; //masterWidth / 2.0;
        double y = 0; //masterHeight / 2.0;
        switch (angle) {
            case 0:
                break;
            case 180:
                break;
            case 90:
            case 270:
                size = new Dimension(masterImg.getHeight(), masterImg.getWidth());
                x = (masterHeight - masterWidth) / 2.0;
                y = (masterWidth - masterHeight) / 2.0;
                break;
        }

        BufferedImage rotatedImg = new BufferedImage(size.width, size.height, masterImg.getTransparency());
        Graphics2D g2d = rotatedImg.createGraphics();
        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(Math.toRadians(angle), masterWidth / 2.0, masterHeight / 2.0);
        g2d.drawImage(masterImg, at, null);
        g2d.dispose();
        
        return rotatedImg;
    }
    
    /**
     * 
     * @param userId
     * @param fromTime
     * @param toTime
     * @return
     * @throws SQLException
     */
    /*
    public List<JSONObject> getVgiObservationsByUserByTimeAsJSON(int userId, String fromTime, String toTime) throws SQLException{
        try{
            String query;
            if(fromTime != null && toTime != null){
                query = "SELECT ov.obs_vgi_id, ov.gid, ov.time_stamp, ov.category_id,"
                        + " ov.description, ov.attributes, ov.dataset_id, ov.unit_id, ov.user_id,"
                        + " ov.time_received, ov.media_count, st_asgeojson(up.the_geom, 10)"
                        + " FROM vgi.observations_vgi ov, units_positions up"
                        + " WHERE ov.user_id = "+userId+""
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"'"
                        + " AND ov.gid = up.gid;";
            } else if(fromTime != null && toTime == null){
                query = "SELECT ov.obs_vgi_id, ov.gid, ov.time_stamp, ov.category_id,"
                        + " ov.description, ov.attributes, ov.dataset_id, ov.unit_id, ov.user_id,"
                        + " ov.time_received, ov.media_count, st_asgeojson(up.the_geom, 10)"
                        + " FROM vgi.observations_vgi ov, units_positions up"
                        + " WHERE ov.user_id = "+userId+""
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.gid = up.gid;";
            } else {
                query = "SELECT ov.obs_vgi_id, ov.gid, ov.time_stamp, ov.category_id,"
                        + " ov.description, ov.attributes, ov.dataset_id, ov.unit_id, ov.user_id,"
                        + " ov.time_received, ov.media_count, st_asgeojson(up.the_geom, 10)"
                        + " FROM vgi.observations_vgi ov, units_positions up"
                        + " WHERE ov.user_id = "+userId+""
                        + " AND ov.time_stamp <= '"+toTime+"'"
                        + " AND ov.gid = up.gid;";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2JSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    */
}