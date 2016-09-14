/**
 * 
 */
package cz.hsrs.db.model.vgi;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author mkepka
 *
 */
@XmlRootElement
public class VgiObservation {

	@XmlElement(name = "obsID")
    private int obsVgiId;
    private Integer gid;
    private Double x;
    private Double y;
    @XmlElement(name = "time_stamp")
    private String timeString;
    @XmlElement(name = "category_id")
    private int categoryId;
    private String description;
    private String attributes;
    @XmlElement(name = "dataset_id")
    private int datasetId;
    @XmlElement(name = "unit_id")
    private long unitId;
    @XmlElement(name = "user_id")
    private int userId;
    @XmlElement(name = "time_received")
    private String timeReceived;
    @XmlElement(name = "media_count")
    private int mediaCount;
    
    /**
     * Empty constructor
     */
    public VgiObservation(){
    }
    
    /**
     * @param obsVgiId
     * @param gid
     * @param timeString
     * @param categoryId
     * @param description
     * @param attributes
     * @param datasetId
     * @param unitId
     * @param userId
     * @param timeReceived
     * @param mediaCount
     */
    public VgiObservation(int obsVgiId, Integer gid, String timeString,
            int categoryId, String description, String attributes,
            int datasetId, long unitId, int userId, String timeReceived,
            int mediaCount) {
        this.obsVgiId = obsVgiId;
        this.gid = gid;
        this.timeString = timeString;
        this.categoryId = categoryId;
        this.description = description;
        this.attributes = attributes;
        this.datasetId = datasetId;
        this.unitId = unitId;
        this.userId = userId;
        this.timeReceived = timeReceived;
        this.mediaCount = mediaCount;
    }

    public VgiObservation(int obsVgiId, Integer gid, String timeString,
            int categoryId, String description, String attributes,
            int datasetId, long unitId, int userId, String timeReceived,
            int mediaCount, double xCoord, double yCoord) {
        this.obsVgiId = obsVgiId;
        this.gid = gid;
        this.timeString = timeString;
        this.categoryId = categoryId;
        this.description = description;
        this.attributes = attributes;
        this.datasetId = datasetId;
        this.unitId = unitId;
        this.userId = userId;
        this.timeReceived = timeReceived;
        this.mediaCount = mediaCount;
        this.x = xCoord;
        this.y = yCoord;
    }
    
    /**
     * @return the obsVgiId
     */
    public int getObsVgiId() {
        return obsVgiId;
    }

    /**
     * @return the gid
     */
    public Integer getGid() {
        return gid;
    }

    /**
     * @return the timeString
     */
    public String getTimeString() {
        return timeString;
    }

    /**
     * @return the categoryId
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the attributes
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * @return the datasetId
     */
    public int getDatasetId() {
        return datasetId;
    }

    /**
     * @return the unitId
     */
    public long getUnitId() {
        return unitId;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the timeReceived
     */
    public String getTimeReceived() {
        return timeReceived;
    }

    /**
     * @return the mediaCount
     */
    public int getMediaCount() {
        return mediaCount;
    }
    
    /**
     * 
     * @return
     */
    public Double getX(){
    	return this.x;
    }

    /**
     * 
     * @return
     */
    public Double getY(){
    	return this.y;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VgiObservation [obsVgiId=" + obsVgiId + ", gid=" + gid
                + ", timeString=" + timeString + ", categoryId=" + categoryId
                + ", description=" + description + ", attributes=" + attributes
                + ", datasetId=" + datasetId + ", unitId=" + unitId
                + ", userId=" + userId + ", timeReceived=" + timeReceived
                + ", mediaCount=" + mediaCount + ", x="+x+", y="+y+"]";
    }
}