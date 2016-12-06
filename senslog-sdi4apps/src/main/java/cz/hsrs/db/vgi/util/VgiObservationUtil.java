package cz.hsrs.db.vgi.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import cz.hsrs.db.model.vgi.Envelope2D;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * Class concentrates methods for processing VGIObservation objects
 * @author mkepka
 *
 */
public class VgiObservationUtil {

    private static final String VGI_SCHEMA_NAME = "vgi";
    private static final String SENSLOG_SCHEMA_NAME = "public";
    private static final String OBSERVATION_TABLE_NAME = "observations_vgi";
    
    public VgiObservationUtil(){
    }
    
    /**
     * Method inserts new VGI Observation object to the DB by given attributes
     * @param gid - ID of position
     * @param timestamp - Time stamp when observation was recorded - mandatory
     * @param categoryId - Id of VgiCategory - mandatory
     * @param description - Detailed description of observation - optional
     * @param attributes - Other attributes in JSON format - optional
     * @param unitId - Id of unit that recorded observation - mandatory
     * @param userId - Id of user that recorded observation - mandatory
     * @param datasetId - Id of VgiDataset - mandatory
     * @return ID of inserted observation as integer
     * @throws SQLException
     */
    public static int insertVgiObs(int gid, String timestamp, Integer categoryId, String description, 
            String attributes, long unitId, int userId, int datasetId) throws SQLException{
        int newId = getNextVgiObsID();
        
        StringBuffer ins = new StringBuffer();
        ins.append("INSERT INTO "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+"(obs_vgi_id, gid, time_stamp, category_id,"
                + " description, attributes, dataset_id, unit_id, user_id) VALUES(");
        ins.append(newId+", ");
        ins.append(gid+", ");
        ins.append("'"+timestamp+"', ");
        ins.append(categoryId+", ");
        ins.append(description == null ? "NULL, " : "'"+description+"', ");
        ins.append((attributes == null) || (attributes != null && attributes.isEmpty()) ? "NULL, " : "'"+attributes+"', ");
        ins.append(datasetId+", ");
        ins.append(unitId+", ");
        ins.append(userId+"); ");
        try{
            String query = ins.toString();
            SQLExecutor.executeUpdate(query);
            return newId;
        } catch (SQLException e){
            throw new SQLException("An error occurs during inserting of new VgiObservation!");
        }
    }
    
    /**
     * Method updates VGIObservation objects by given attributes
     * @param obsId - ID of VGIObservation object to be updated
     * @param gid - ID of position
     * @param timestamp - Time stamp when observation was recorded - mandatory
     * @param categoryId - Id of VgiCategory - mandatory
     * @param description - Detailed description of observation - optional
     * @param attributes - Other attributes in JSON format - optional
     * @param unitId - Id of unit that recorded observation - mandatory
     * @param userId - Id of user that recorded observation - mandatory
     * @param datasetId - Id of VgiDataset - mandatory
     * @return true if the VGIObservation object was updated
     * @throws SQLException
     */
    public static boolean updateVgiObs(int obsId, int gid, String timestamp, Integer categoryId, String description, 
            String attributes, long unitId, int userId, int datasetId) throws SQLException{
        try{
            StringBuffer ins = new StringBuffer();
            ins.append("UPDATE "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" SET ");
            ins.append("gid = "+gid+", ");
            ins.append(timestamp != null ? "time_stamp = '"+timestamp+"', " : "");
            ins.append(categoryId != null ? "category_id = "+categoryId+", " : "");
            ins.append(description != null ? "description = '"+description+"', " : "");
            ins.append((attributes != null && !attributes.isEmpty()) ? "attributes = '"+attributes+"', " : "");
            ins.append("dataset_id = "+datasetId+", ");
            ins.append("unit_id = "+unitId+", ");
            ins.append("user_id = "+userId+" ");
            ins.append("WHERE obs_vgi_id = "+obsId+";");
            
            String query = ins.toString();
            SQLExecutor.executeUpdate(query);
            return true;
        } catch (SQLException e){
            throw new SQLException("An error occurs during inserting of new POI!");
        }
    }
    
    /**
     * Method gets specific VGIObservation object by given ID
     * @param obsId - ID of VgiObservation object
     * @param userId - ID of user that owns VgiObservation object
     * @return VgiObservation object or null if any was not found
     * @throws SQLException
     */
    public VgiObservation getVgiObservationByObsId(int obsId, int userId) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.user_id = "+userId+""
                    + " AND ov.obs_vgi_id = "+obsId+""
                    + " AND ov.gid = up.gid;";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            if(res.next()){
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                return obs;
            }
            else{
                return null;
            }
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects VgiObservation object by given ID in GeoJSON format
     * @param obsId - ID of VgiObservation object
     * @param userId - ID of user that owns given VgiObservation object
     * @return VgiObservation object in GeoJSON format as JSONObject
     * @throws SQLException
     */
    public JSONObject getVgiObservationByObsIdAsGeoJSON(int obsId, int userId) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.user_id = "+userId+""
                    + " AND ov.obs_vgi_id = "+obsId+""
                    + " AND ov.gid = up.gid;";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            JSONObject vgiObs = convertVgiObservationResultSet2GeoJSON(res);
            return vgiObs;
         } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects VGI observation objects by given filter parameters
     * @param userId - user ID that has VgiObservation
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservation objects 
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUser(int userId, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+"";
            if(fromTime == null && toTime == null){
                query = query 
                        +";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects VgiObservation objects by given filter parameters
     * @param userId - user ID that has VgiObservation
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException
     */
    public List<JSONObject> getVgiObservationsByUserAsJSON(int userId, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+"";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects VgiObservation objects by given filter parameters
     * @param userId - user ID that has VgiObservation
     * @param categoryId - ID of VgiCategory
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUserByCategory(int userId, int categoryId, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " ov.user_id = "+userId+""
                    + " AND ov.category_id = "+categoryId+"";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects VgiObservation objects by given filter parameters
     * @param userId - user ID that has VgiObservation
     * @param categoryId - ID of VgiCategory
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException
     */
    public List<JSONObject> getVgiObservationsByUserByCategoryAsJSON(int userId, int categoryId, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.category_id = "+categoryId+"";
            if(fromTime == null && toTime == null){
                query = query
                        +";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects VgiObservation objects by given filter parameters
     * @param userId - user ID that has VgiObservation
     * @param datasetId - ID of VgiDataset
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUserByDataset(int userId, int datasetId, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.dataset_id = "+datasetId+"";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters
     * @param userId - ID of user that owns VgiObservation
     * @param datasetId - ID of VgiDataset
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException
     */
    public List<JSONObject> getVgiObservationsByUserByDatasetAsJSON(int userId, int datasetId, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.dataset_id = "+datasetId+"";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param extent - spatial extent that features should intersect 
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUserByExtent(int userId, Envelope2D extent, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND up.geom && ST_MakeEnvelope("+extent.getXMin()+", "+extent.getYMin()+", "+extent.getXMax()+", "+extent.getYMax()+", "+extent.getSRID()+")";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param extent - spatial extent that features should intersect 
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException 
     */
    public List<JSONObject> getVgiObservationsByUserByExtentAsJSON(int userId, Envelope2D extent, String fromTime, String toTime) throws SQLException {
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND up.geom && ST_MakeEnvelope("+extent.getXMin()+", "+extent.getYMin()+", "+extent.getXMax()+", "+extent.getYMax()+", "+extent.getSRID()+")";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query 
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param unitId - ID of unit that produced VgiObservation
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException 
     */
    public List<JSONObject> getVgiObservationsByUserByUnitAsJSON(int userId, long unitId, String fromTime, String toTime) throws SQLException {
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.unit_id = "+unitId;
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query 
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param datasetId - ID of VgiDataset 
     * @param extent - spatial extent that features should intersect 
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUserByDatasetByExtent(int userId, int datasetId, Envelope2D extent, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.dataset_id = "+datasetId+""
                    + " AND up.geom && ST_MakeEnvelope("+extent.getXMin()+", "+extent.getYMin()+", "+extent.getXMax()+", "+extent.getYMax()+", "+extent.getSRID()+")";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param datasetId - ID of VgiDataset 
     * @param extent - spatial extent that features should intersect 
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException 
     */
    public List<JSONObject> getVgiObservationsByUserByDatasetByExtentAsJSON(int userId, int datasetId, Envelope2D extent, String fromTime, String toTime) throws SQLException {
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.dataset_id = "+datasetId+""
                    + " AND up.geom && ST_MakeEnvelope("+extent.getXMin()+", "+extent.getYMin()+", "+extent.getXMax()+", "+extent.getYMax()+", "+extent.getSRID()+")";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query 
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param datasetId - ID of VgiDataset 
     * @param extent - spatial extent that features should intersect 
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations
     * @throws SQLException
     */
    public List<VgiObservation> getVgiObservationsByUserByCategoryByDatasetByExtent(int userId, int categoryId, int datasetId, Envelope2D extent, String fromTime, String toTime) throws SQLException{
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.category_id = "+categoryId+""
                    + " AND ov.dataset_id = "+datasetId+""
                    + " AND up.geom && ST_MakeEnvelope("+extent.getXMin()+", "+extent.getYMin()+", "+extent.getXMax()+", "+extent.getYMax()+", "+extent.getSRID()+")";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
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
                        res.getInt("media_count"),
                        res.getDouble("st_x"),
                        res.getDouble("st_y"),
                        res.getDouble("altitude"),
                        res.getDouble("dop"));
                vgiObsList.add(obs);
            }
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method selects List of VgiObservation objects by given filter parameters 
     * @param userId - ID of user that owns VgiObservation
     * @param datasetId - ID of VgiDataset 
     * @param extent - spatial extent that features should intersect 
     * @param fromTime - beginning of time frame, optional
     * @param toTime - end of time frame, optional
     * @return List of VgiObservations in GeoJSON format
     * @throws SQLException 
     */
    public List<JSONObject> getVgiObservationsByUserByCategoryByDatasetByExtentAsJSON(int userId, int categoryId, int datasetId, Envelope2D extent, String fromTime, String toTime) throws SQLException {
        try{
            String query = VgiObservation.SELECT_ATTRIBUTES_GEOJSON
                    + " FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+" ov, "+SENSLOG_SCHEMA_NAME+".units_positions up"
                    + " WHERE ov.gid = up.gid"
                    + " AND ov.user_id = "+userId+""
                    + " AND ov.category_id = "+categoryId+""
                    + " AND ov.dataset_id = "+datasetId+""
                    + " AND up.geom && ST_MakeEnvelope("+extent.getXMin()+", "+extent.getYMin()+", "+extent.getXMax()+", "+extent.getYMax()+", "+extent.getSRID()+")";
            if(fromTime == null && toTime == null){
                query = query
                        + ";";
            } else if(fromTime == null && toTime != null){
                query = query 
                        + " AND ov.time_stamp <= '"+toTime+"';";
            } else if(fromTime != null && toTime == null){
                query = query
                        + " AND ov.time_stamp >= '"+fromTime+"';";
            } else{
                query = query 
                        + " AND ov.time_stamp >= '"+fromTime+"'"
                        + " AND ov.time_stamp <= '"+toTime+"';";
            }
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<JSONObject> vgiObsList = convertVgiObsResultSet2GeoJSON(res);
            return vgiObsList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method deletes VGIObservation by given ID
     * @param obsId - ID of VGIObservation object to be deleted
     * @return true if VGIObs was deleted, false if not
     * @throws SQLException
     */
    public static boolean deleteVgiObservation(int obsId) throws SQLException{
        String query = "DELETE FROM "+VGI_SCHEMA_NAME+"."+OBSERVATION_TABLE_NAME+""
                + " WHERE obs_vgi_id = "+obsId+";";
        try{
            int update = SQLExecutor.executeUpdate(query);
            if(update > 0){
                return true;
            }
            else{
                return false;
            }
        } catch(SQLException e){
            throw new SQLException("VGIObservation was not deleted!");
        }
    }
    
    /**
     * Method selects next value of VgiObservation ID 
     * @return next value of ID
     * @throws SQLException when new ID can be selected
     */
    private static int getNextVgiObsID() throws SQLException{
        try{
            String selectId = "SELECT nextval('"+VGI_SCHEMA_NAME+".observations_vgi_obs_vgi_id_seq'::regclass);";
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
     * Method converts given ResultSet with selected one VgiObservation object
     * to JSONObject in GeoJSON format
     * @param res - ResultSet containing one VgiObservation object
     * @return VgiObservation object in GeoJSON format as JSONObject
     * @throws SQLException
     */
    private JSONObject convertVgiObservationResultSet2GeoJSON(ResultSet res) throws SQLException{
        if(res.next()){
            // feature
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            feature.put("geometry", res.getString("st_asgeojson"));
            // properties
            JSONObject properties = new JSONObject();
            properties.put(VgiParams.ATTRIBUTES_NAME, res.getString("attributes") == null ? "" : res.getString("attributes"));
            properties.put(VgiParams.CATEGORY_ID_NAME, res.getInt("category_id"));
            properties.put(VgiParams.DATASET_ID_NAME, res.getInt("dataset_id"));
            properties.put(VgiParams.DESCRIPTION_NAME, res.getString("description") == null ? "" : res.getString("description"));
            properties.put(VgiParams.MEDIA_COUNT_NAME, res.getInt("media_count"));
            properties.put(VgiParams.OBS_VGI_ID_NAME, res.getInt("obs_vgi_id"));
            properties.put(VgiParams.TIMESTAMP_NAME, res.getString("time_stamp"));
            properties.put(VgiParams.UNIT_ID_NAME, res.getLong("unit_id"));
            properties.put(VgiParams.TIME_RECEIVED_NAME, res.getString("time_received"));
            properties.put(VgiParams.ALT_NAME, res.getDouble("altitude"));
            properties.put(VgiParams.DOP_NAME, res.getDouble("dop"));
            feature.put("properties", properties);
            return feature;
        }
        else{
            return new JSONObject();
        }
    }
    
    /**
     * Method converts given ResultSet with selected VgiObservation objects 
     * to LinkedList of GeoJSON objects
     * @param res - ResultSet containing VgiObservation objects
     * @return LinkedList of VgiObservation objects in GeoJSON format
     * @throws SQLException
     */
    private LinkedList<JSONObject> convertVgiObsResultSet2GeoJSON(ResultSet res) throws SQLException{
        LinkedList<JSONObject> vgiObsList = new LinkedList<JSONObject>();
        while(res.next()){
            // feature
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            feature.put("geometry", res.getString("st_asgeojson"));
            // properties
            JSONObject properties = new JSONObject();
            properties.put(VgiParams.ATTRIBUTES_NAME, res.getString("attributes") == null ? "" : res.getString("attributes"));
            properties.put(VgiParams.CATEGORY_ID_NAME, res.getInt("category_id"));
            properties.put(VgiParams.DATASET_ID_NAME, res.getInt("dataset_id"));
            properties.put(VgiParams.DESCRIPTION_NAME, res.getString("description") == null ? "" : res.getString("description"));
            properties.put(VgiParams.MEDIA_COUNT_NAME, res.getInt("media_count"));
            properties.put(VgiParams.OBS_VGI_ID_NAME, res.getInt("obs_vgi_id"));
            properties.put(VgiParams.TIMESTAMP_NAME, res.getString("time_stamp"));
            properties.put(VgiParams.UNIT_ID_NAME, res.getLong("unit_id"));
            properties.put(VgiParams.TIME_RECEIVED_NAME, res.getString("time_received"));
            properties.put(VgiParams.ALT_NAME, res.getDouble("altitude"));
            properties.put(VgiParams.DOP_NAME, res.getDouble("dop"));
            feature.put("properties", properties);
            
            vgiObsList.add(feature);
        }
        return vgiObsList;
    }
    
    /**
     * Method converts List of VgiObservations as JSONObjects 
     * to one JSONObject representing FeatureCollection in GeoJSON format
     * @param obsList - List of VgiObservations as JSONObjects
     * @return JSONObject with FeatureCollection in GeoJSON format
     */
    public static JSONObject convertListVgiObservations2GeoJSON(List<JSONObject> obsList){
        JSONObject featureCollection = new JSONObject();
        // Features
        JSONArray featureList = new JSONArray();
        featureList.addAll(obsList);
        
        // FeatureCollection
        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", featureList);
        
        return featureCollection;
    }
}