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
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.rest.beans.VgiObservationBean;

public class VgiUtil {
    
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
     * 
     * @param timestamp - Time stamp when observation was recorded - mandatory
     * @param categoryId - Id of category - mandatory
     * @param description - Detailed description of observation - optional
     * @param attributes - Other attributes in JSON format - optional
     * @param unitId - Id of device that recorded observation - mandatory
     * @param userId - Id of user that recorded observation - mandatory
     * @param datasetId - Id of dataset - mandatory
     * @return Id of inserted observation as integer
     * @throws SQLException
     */
    public static int insertVgiObs(String timestamp, Integer categoryId, String description, 
            String attributes, long unitId, int userId, int datasetId) throws SQLException{
        int newId = getNextVgiObsID();
        
        StringBuffer ins = new StringBuffer();
        ins.append("INSERT INTO vgi.observations_vgi(obs_vgi_id, time_stamp, category_id,"
                + " description, attributes, dataset_id, unit_id, user_id) VALUES(");
        ins.append(newId+", ");
        ins.append("'"+timestamp+"', ");
        ins.append(categoryId+", ");
        if(description == null){
            ins.append("NULL, ");
        }
        else{
            ins.append("'"+description+"', ");
        }
        if(attributes == null){
            ins.append("NULL, ");
        }
        else if(attributes != null && attributes.isEmpty()){
            ins.append("NULL, ");
        }
        else{
        	ins.append("'"+attributes+"', ");
        }
        ins.append(datasetId+", ");
        ins.append(unitId+", ");
        ins.append(userId+"); ");
        try{
            String query = ins.toString();
            SQLExecutor.executeUpdate(query);
            return newId;
        } catch (SQLException e){
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
     * 
     * @param vgiId
     * @param media
     * @param fileSize
     * @param mediaType
     * @throws SQLException
     */
    public static void insertVgiMedia(long vgiId, InputStream media, long fileSize, String mediaType) throws SQLException{
        String query = "INSERT INTO vgi.observations_vgi_media(obs_vgi_id, observed_media, media_datatype)"
                + " VALUES("+vgiId+", ?, '"+mediaType+"');";
        SQLExecutor.insertStream(query, media, fileSize);
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
     * Method selects next value of observation_vgi ID
     * @return next value of ID
     * @throws SQLException
     */
    private static int getNextVgiObsID() throws SQLException{
        try{
            String selectId = "SELECT nextval('vgi.observations_vgi_obs_vgi_id_seq'::regclass);";
            ResultSet resId = SQLExecutor.getInstance().executeQuery(selectId);
            if(resId.next()){
                return resId.getInt(1);
            }
            else{
                throw new SQLException("Observation can't get new ID!");
            }
        } catch(SQLException e){
            throw new SQLException("Observation can't get new ID!");
        }
    }
    
    /**
     * Method to get all VGI observations associated to given user
     * @param userId - ID of user
     * @return List of VGI observations 
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUser(int userId) throws SQLException{
        try{
            String query = "SELECT obs_vgi_id, gid, time_stamp, category_id,"
                    + " description, attributes, dataset_id, unit_id, user_id,"
                    + " time_received, media_count"
                    + " FROM vgi.observations_vgi WHERE user_id = "+userId+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<VgiObservation> vgiObsList = new LinkedList<VgiObservation>();
            while(res.next()){
                VgiObservation obs = new VgiObservation(
                        res.getInt("obs_vgi_id"),
                        res.getInt("gid"),
                        res.getString("time_stamp"),
                        res.getInt("category_id"),
                        res.getString("description"),
                        res.getString("attributes"),
                        res.getInt("dataset_id"),
                        res.getLong("unit_id"),
                        res.getInt("user_id"),
                        res.getString("time_received"),
                        res.getInt("media_count"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    public List<VgiObservationBean> getVgiObservationBeansByUser(int userId) throws SQLException{
        try{
            String query = "SELECT obs_vgi_id, gid, time_stamp, category_id,"
                    + " description, attributes, dataset_id, unit_id, user_id,"
                    + " time_received, media_count"
                    + " FROM vgi.observations_vgi WHERE user_id = "+userId+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<VgiObservationBean> vgiObsList = new LinkedList<VgiObservationBean>();
            while(res.next()){
            	VgiObservationBean obs = new VgiObservationBean(
                        res.getInt("obs_vgi_id"),
                        res.getInt("gid"),
                        res.getString("time_stamp"),
                        res.getInt("category_id"),
                        res.getString("description"),
                        res.getString("attributes"),
                        res.getInt("dataset_id"),
                        res.getLong("unit_id"),
                        res.getInt("user_id"),
                        res.getString("time_received"),
                        res.getInt("media_count"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
}