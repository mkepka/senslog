/**
 * 
 */
package cz.hsrs.db.vgi.util;

import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.model.vgi.VgiMedia;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * Class concentrates methods for processing VgiMedia objects
 * @author mkepka
 *
 */
public class VgiMediaUtil {
    
    private static final String SCHEMA_NAME = "vgi";
    private static final String MEDIA_TABLE_NAME = "observations_vgi_media";

    /**
     * Method inserts new VgiMedia to the DB
     * @param obsId - ID of associated VGIObservation object, mandatory
     * @param media - InputStream with media to be inserted, mandatory
     * @param fileSize - size of media file to be inserted, mandatory 
     * @param mediaType - data type of media, mandatory
     * @throws SQLException
     */
    public static void insertVgiMedia(int obsId, InputStream media, long fileSize, String mediaType) throws SQLException{
        String query = "INSERT INTO "+SCHEMA_NAME+"."+MEDIA_TABLE_NAME+""
                + "(obs_vgi_id, observed_media, media_datatype)"
                + " VALUES("+obsId+", ?, '"+mediaType+"');";
        SQLExecutor.insertStream(query, media, fileSize);
    }
    
    /**
     * Method updates VgiMedia by given medId
     * @param medId - ID of VgiMedia
     * @param media - InputStream with media 
     * @param fileSize - size of media file in bytes
     * @param mediaType - data type of media, mandatory
     * @throws SQLException
     */
    public static void updateVgiMedia(int medId, InputStream media, long fileSize, String mediaType) throws SQLException{
        String query = "UPDATE "+SCHEMA_NAME+"."+MEDIA_TABLE_NAME+" SET"
                + " observed_media = ?,"
                + " media_datatype = "+mediaType
                + " WHERE med_id = "+medId+";";
        SQLExecutor.insertStream(query, media, fileSize);
    }
    
   /**
    * Method for selecting info of all connected VgiMedia to given master VgiObservation
    * @param obsId - ID of master VgiObservation object
    * @throws SQLException
    */
    public List<VgiMedia> getVgiMediaInfo(int obsId) throws SQLException{
        try{
            String select = "SELECT med_id, obs_vgi_id, time_received, media_datatype"
                    + " FROM "+SCHEMA_NAME+"."+MEDIA_TABLE_NAME+""
                    + " WHERE obs_vgi_id = "+obsId+";"; 
            ResultSet rs = SQLExecutor.getInstance().executeQuery(select);
            List<VgiMedia> mediaList = new LinkedList<VgiMedia>();
            while (rs.next()) {
                VgiMedia medium = new VgiMedia(
                        rs.getInt("med_id"),
                        rs.getInt("obs_vgi_id"),
                        rs.getString("time_received"),
                        rs.getString("media_datatype"));
                mediaList.add(medium);
            }
            return mediaList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method for selecting VgiMedia of given master VgiObservation
     * @param obsId - ID of master VgiObservation object
     * @param mediaId - ID of VgiMedia
     * @return VgiMedia object containing media file
     * @throws SQLException
     */
    public VgiMedia getVgiMedia(int obsId, int mediaId) throws SQLException{
        try{
            String select = "SELECT med_id, obs_vgi_id, time_received, observed_media, media_datatype"
                    + " FROM "+SCHEMA_NAME+"."+MEDIA_TABLE_NAME+""
                    + " WHERE med_id = "+mediaId+""
                    + " AND obs_vgi_id = "+obsId+";"; 
            ResultSet rs = SQLExecutor.getInstance().executeQuery(select);
            if (rs.next()) {
                VgiMedia medium = new VgiMedia(
                        rs.getInt("med_id"),
                        rs.getInt("obs_vgi_id"),
                        rs.getString("time_received"),
                        rs.getBytes("observed_media"),
                        rs.getString("media_datatype"));
                return medium;
            }
            else{
                throw new SQLException("VgiMedia with given ID cannot be selected!");
            }
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method for deleting VgiMedia of given master VgiObservation
     * @param obsId - ID of master VgiObservation object
     * @param mediaId - ID of VgiMedia object
     * @return true if VgiMedia object was deleted
     * @throws SQLException
     */
    public static boolean deleteVgiMedia(int obsId, int mediaId) throws SQLException{
        try{
            String del = "DELETE"
                    + " FROM "+SCHEMA_NAME+"."+MEDIA_TABLE_NAME+""
                    + " WHERE med_id = "+mediaId+""
                    + " AND obs_vgi_id = "+obsId+";"; 
            int i = SQLExecutor.executeUpdate(del);
            if (i == 1) {
                return true;
            }
            else{
                return false;
            }
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
}