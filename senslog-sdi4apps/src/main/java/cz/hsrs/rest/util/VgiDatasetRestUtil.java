/**
 * 
 */
package cz.hsrs.rest.util;

import java.sql.SQLException;
import java.util.List;

import net.sf.json.JSONObject;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.VgiDataset;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.vgi.util.VgiDatasetsUtil;

/**
 * Utility class for VgiDatasetRest services
 * @author mkepka
 *
 */
public class VgiDatasetRestUtil {

    private UserUtil userUt;
    private VgiDatasetsUtil dUtil;
    
    public VgiDatasetRestUtil(){
        this.userUt = new UserUtil();
        this.dUtil = new VgiDatasetsUtil();
    }
    
    /**
     * Method processes creating of new VgiDataset object
     * and inserts it to the DB
     * @param Dataset object as JSON
     * @return new ID of the VgiDataset in DB
     * @throws SQLException
     */
    public int processInsertVgiDataset(JSONObject dataset, String userName) throws SQLException {
        try{
            int userId = userUt.getUserId(userName);
            VgiDataset newDataset = new VgiDataset(
                    dataset.getString("dataset_name"),
                    dataset.getString("description"), 
                    userId);
            int newId = newDataset.insertToDB();
            return newId;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        } catch (NoItemFoundException e) {
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes GetVgiDataset by given datasetId
     * @param datasetId - ID of dataset
     * @param userName - name of user
     * @return VgiDataset object if there is one in the DB
     * @throws SQLException
     */
    public VgiDataset processGetVgiDataset(int datasetId, String userName) throws SQLException{
        try{
            int userId = userUt.getUserId(userName);
            VgiDataset dataset = dUtil.getVgiDataset(userId, datasetId);
            return dataset;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes SelectVgiDatasets by given user
     * @param userName - name of user
     * @return LinkedList of VGIDataset objects associated to given user
     * @throws SQLException
     */
    public List<VgiDataset> processGetVgiDatasets(String userName) throws SQLException{
        try{
            int userId = userUt.getUserId(userName);
            List<VgiDataset> datList = dUtil.getDatasetsList(userId);
            return datList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes DeleteVgiDataset by given datasetID
     * @param datasetId - ID of VgiDataset to be deleted
     * @param userName - name of user
     * @return true if VgiDataset was deleted, false if not
     * @throws SQLException
     */
    public boolean processDeleteVgiDataset(int datasetId, String userName) throws SQLException{
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
}