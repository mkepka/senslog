/**
 * 
 */
package cz.hsrs.db.model.vgi;

/**
 * @author mkepka
 *
 */
public class VgiDataset {

    private int datasetId;
    private String datasetName;
    private String description;
    private int user_id;
    
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
    public VgiDataset(int datasetId, String datasetName, String description,
            int user_id) {
        this.datasetId = datasetId;
        this.datasetName = datasetName;
        this.description = description;
        this.user_id = user_id;
    }

    /**
     * @param datasetName
     * @param description
     * @param user_id
     */
    public VgiDataset(String datasetName, String description, int user_id) {
        this.datasetName = datasetName;
        this.description = description;
        this.user_id = user_id;
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
        return user_id;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VgiDataset [datasetId=" + datasetId + ", datasetName="
                + datasetName + ", description=" + description + ", user_id="
                + user_id + "]";
    }
}