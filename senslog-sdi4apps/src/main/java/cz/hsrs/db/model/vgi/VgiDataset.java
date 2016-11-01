/**
 * 
 */
package cz.hsrs.db.model.vgi;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.annotation.XmlRootElement;

import cz.hsrs.db.pool.SQLExecutor;

/**
 * @author mkepka
 *
 */
@XmlRootElement
public class VgiDataset {

    private int datasetId;
    private String datasetName;
    private String description;
    private int userId;
    
    /**
     * Empty constructor
     */
    public VgiDataset(){
    }
    
    /**
     * @param datasetId
     * @param datasetName
     * @param description
     * @param user_id
     */
    public VgiDataset(int datasetId, String datasetName, String description, int user_id) {
        this.datasetId = datasetId;
        this.datasetName = datasetName;
        this.description = description;
        this.userId = user_id;
    }

    /**
     * @param datasetName
     * @param description
     * @param user_id
     */
    public VgiDataset(String datasetName, String description, int user_id) {
        this.datasetName = datasetName;
        this.description = description;
        this.userId = user_id;
    }

    /**
     * @param datasetId
     * @param datasetName
     * @param description
     */
    public VgiDataset(int datasetId, String datasetName, String description) {
        this.datasetId = datasetId;
        this.datasetName = datasetName;
        this.description = description;
    }

    /**
     * @return the datasetId
     */
    public int getDatasetId() {
        return datasetId;
    }

    /**
     * @return the datasetName
     */
    public String getDatasetName() {
        return datasetName;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the user_id
     */
    public int internalGetUser_id() {
        return userId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VgiDataset [datasetId=" + datasetId + ", datasetName="
                + datasetName + ", description=" + description + ", user_id="
                + userId + "]";
    }
    
    /**
     * Method inserts new Dataset to the DB
     * @return ID of the given dataset 
     * @throws SQLException
     */
    public int insertToDB() throws SQLException{
        try{
            String newIdQuery = "SELECT nextval('vgi.vgi_datasets_dataset_id_seq'::regclass);";
            ResultSet res = SQLExecutor.getInstance().executeQuery(newIdQuery);
            int newId = 0;
            if(res.next()){
                newId = res.getInt(1);
            }
            String ins = "INSERT INTO vgi.vgi_datasets(dataset_id, dataset_name, description, user_id)"
                    + " VALUES ("+newId+", '"
                    +this.datasetName+"', '"
                    +this.description+"', "
                    +this.userId+");";
            SQLExecutor.executeUpdate(ins);
            return newId;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
        
    }
}