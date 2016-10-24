package cz.hsrs.db.vgi.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.model.vgi.VgiDataset;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * Class concentrates methods for processing VGIDataset objects
 * @author mkepka
 *
 */
public class VgiDatasetsUtil {
    
    private static final String SCHEMA_NAME = "vgi";
    private static final String TABLE_NAME = "vgi_datasets";

    /**
     * Method for selecting specific VgiDataset object
     * @param userId - Id user user
     * @param datasetId - ID of VgiDataset object
     * @return VgiDataset object with given parameters
     * @throws SQLException
     */
    public VgiDataset getVgiDataset(int userId, int datasetId) throws SQLException{
        try{
            String query = "SELECT dataset_id, dataset_name, description"
                    + " FROM "+SCHEMA_NAME+"."+TABLE_NAME+""
                    + " WHERE user_id = "+userId+""
                    + " AND dataset_id = "+datasetId+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            if(res.next()){
                VgiDataset dat = new VgiDataset(
                        res.getInt("dataset_id"), 
                        res.getString("dataset_name"),
                        res.getString("description"));
                return dat;
            }
            else{
                throw new SQLException("VGIDataset cannot be selected!");
            }
        } catch (SQLException e){
            throw new SQLException("VGIDataset cannot be selected!");
        }
    }
    
    /**
     * Method gets List of VGI Datasets by given user
     * @param userId - ID of user as Integer
     * @return LinkedList of VGIDatasets
     * @throws SQLException
     */
    public List<VgiDataset> getDatasetsList(int userId) throws SQLException{
        try{
            String query = "SELECT dataset_id, dataset_name, description"
                    + " FROM "+SCHEMA_NAME+"."+TABLE_NAME+""
                    + " WHERE user_id = "+userId+""
                    + " ORDER BY dataset_id;";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<VgiDataset> datList = new LinkedList<VgiDataset>();
            while(res.next()){
                VgiDataset dat = new VgiDataset(
                        res.getInt("dataset_id"), 
                        res.getString("dataset_name"),
                        res.getString("description"));
                datList.add(dat);
            }
            return datList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method deletes VGIDataset object by given ID 
     * and associated to given user
     * @param userId - ID of user
     * @param datasetId - ID of VgiDataset object
     * @return Number of deleted rows, expected only 1
     * @throws SQLException
     */
    public int deleteVgiDataset(int userId, int datasetId) throws SQLException{
        try{
            String query = "DELETE"
                    + " FROM "+SCHEMA_NAME+"."+TABLE_NAME+""
                    + " WHERE dataset_id ="+datasetId+""
                    + " AND user_id = "+userId+";";
            int row = SQLExecutor.executeUpdate(query);
            return row;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
}