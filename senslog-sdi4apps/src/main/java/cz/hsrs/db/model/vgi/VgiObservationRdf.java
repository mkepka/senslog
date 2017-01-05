package cz.hsrs.db.model.vgi;

/**
 * Model class representing VgiObservation for export to RDF format
 * @author mkepka
 *
 */
public class VgiObservationRdf {

    private int obsVgiId;
    private String dateString;
    private int categoryId;
    private String description;
    private String name;
    private int datasetId;
    private long unitId;
    private int userId;
    private int mediaCount;
    private String geom;
    
    /**
     * @param obsVgiId
     * @param dateString
     * @param categoryId
     * @param description
     * @param name
     * @param datasetId
     * @param unitId
     * @param userId
     * @param mediaCount
     */
    public VgiObservationRdf(int obsVgiId, String dateString, int categoryId,
            String description, String name, int datasetId, long unitId,
            int userId, int mediaCount, String geomString) {
        this.obsVgiId = obsVgiId;
        this.dateString = dateString;
        this.categoryId = categoryId;
        this.description = description;
        this.name = name;
        this.datasetId = datasetId;
        this.unitId = unitId;
        this.userId = userId;
        this.mediaCount = mediaCount;
        this.geom = geomString;
    }

    /**
     * @return the obsVgiId
     */
    public int getObsVgiId() {
        return obsVgiId;
    }

    /**
     * @return the dateString
     */
    public String getDateString() {
        return dateString;
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
     * @return the name
     */
    public String getName() {
        return name;
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
     * @return the mediaCount
     */
    public int getMediaCount() {
        return mediaCount;
    }
    
    public String getGeom(){
    	return geom;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{\"obs_vgi_id\":"+obsVgiId+", "
                + "\"date\":\""+dateString+"\", "
                + "\"category_id\":"+categoryId+", "
                + "\"description\":\""+description+"\", "
                + "\"name\":"+name+"\", "
                + "\"dataset_id\":"+datasetId+", "
                + "\"unit_id\":"+unitId+", "
                + "\"user_id\":"+userId+", "
                + "\"media_count\":"+mediaCount+", "
                + "\"geometry\":\""+geom+"\"}";
    }
}