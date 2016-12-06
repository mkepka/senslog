/**
 * 
 */
package cz.hsrs.db.model.vgi;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import cz.hsrs.db.vgi.util.VgiParams;

/**
 * @author mkepka
 *
 */
@XmlRootElement
public class VgiObservation {

	@XmlElement(name = VgiParams.OBS_VGI_ID_NAME)
    private int obsVgiId;
    private Integer gid;
    private Double x;
    private Double y;
    private Double altitude;
    private Double dop;
    @XmlElement(name = VgiParams.TIMESTAMP_NAME)
    private String timeString;
    @XmlElement(name = VgiParams.CATEGORY_ID_NAME)
    private int categoryId;
    private String description;
    private String attributes;
    @XmlElement(name = VgiParams.DATASET_ID_NAME)
    private int datasetId;
    @XmlElement(name = VgiParams.UNIT_ID_NAME)
    private long unitId;
    @XmlElement(name = "user_id")
    private int userId;
    @XmlElement(name = "time_received")
    private String timeReceived;
    @XmlElement(name = "media_count")
    private int mediaCount;
    
    /**
     * Prepared list of attributes to be select from DB in following order:
     * ov.obs_vgi_id
     * ov.gid
     * ov.time_stamp
     * ov.category_id
     * ov.description
     * ov.attributes
     * ov.dataset_id
     * ov.unit_id
     * ov.user_id
     * ov.time_received
     * ov.media_count
     * st_x(up.the_geom)
     * st_y(up.the_geom)
     * up.altitude
     * up.dop
     */
    public static final String SELECT_ATTRIBUTES = "SELECT ov.obs_vgi_id, ov.gid, ov.time_stamp,"
    		+ " ov.category_id, ov.description, ov.attributes, ov.dataset_id, ov.unit_id, ov.user_id,"
            + " ov.time_received, ov.media_count, st_x(up.the_geom), st_y(up.the_geom), up.altitude, up.dop";
    /**
     * Prepared list of attributes to be select from DB for GeoJSON in following order:
     * ov.obs_vgi_id
     * ov.gid
     * ov.time_stamp
     * ov.category_id
     * ov.description
     * ov.attributes
     * ov.dataset_id
     * ov.unit_id
     * ov.user_id
     * ov.time_received
     * ov.media_count
     * st_asgeojson(up.the_geom, 10)
     * up.altitude
     * up.dop
     */
    public static final String SELECT_ATTRIBUTES_GEOJSON = "SELECT ov.obs_vgi_id, ov.gid, ov.time_stamp,"
    		+ " ov.category_id, ov.description, ov.attributes, ov.dataset_id, ov.unit_id, ov.user_id,"
            + " ov.time_received, ov.media_count, st_asgeojson(up.the_geom, 10), up.altitude, up.dop";
    
    /**
     * Empty constructor for serialization
     */
    public VgiObservation(){
    }
    
    /**
     * Constructor creates object from given fields 
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

    /**
     * Constructor creates object from given fields
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
     * @param xCoord
     * @param yCoord
     * @param altitude
     * @param dop
     */
    public VgiObservation(int obsVgiId, Integer gid, String timeString,
            int categoryId, String description, String attributes,
            int datasetId, long unitId, int userId, String timeReceived,
            int mediaCount, double xCoord, double yCoord, double altCoord, 
            double dop) {
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
        this.altitude = altCoord;
        this.dop = dop;
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
    
    /**
     * 
     * @return
     */
    public Double getAltitude(){
    	return this.altitude;
    }
    
    /**
     * 
     * @return
     */
    public Double getDOP(){
    	return this.dop;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VgiObservation [obs_vgi_id=" + obsVgiId + ", gid=" + gid
                + ", time_string=" + timeString + ", category_id=" + categoryId
                + ", description=" + description + ", attributes=" + attributes
                + ", dataset_id=" + datasetId + ", unit_id=" + unitId
                + ", user_id=" + userId + ", time_received=" + timeReceived
                + ", media_count=" + mediaCount + ", x="+x+", y="+y
                + ", altitude="+altitude+", dop="+dop+"]";
    }
}