package cz.hsrs.db.vgi.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.model.vgi.VgiDataset;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * Class concentrates method for processing VGI Dataset objects
 * @author mkepka
 *
 */
public class VgiDatasetsUtil {

    /**
     * Method gets List of VGI Datasets by given user
     * @param userId - ID of user as Integer
     * @return LinkedList of VGIDatasets
     * @throws SQLException
     */
    public List<VgiDataset> getDatasetsList(int userId) throws SQLException{
        try{
            String query = "SELECT dataset_id, dataset_name, description"
                    + " FROM vgi.vgi_datasets WHERE user_id = "+userId+""
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
     * @param datasetId - ID of dataset
     * @return Number of deleted rows, expected only 1
     * @throws SQLException
     */
    public int deleteVgiDataset(int userId, int datasetId) throws SQLException{
        try{
            String query = "DELETE FROM vgi.vgi_datasets"
                    + " WHERE dataset_id ="+datasetId+""
                    + " AND user_id = "+userId+";";
            int row = SQLExecutor.executeUpdate(query);
            return row;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
}