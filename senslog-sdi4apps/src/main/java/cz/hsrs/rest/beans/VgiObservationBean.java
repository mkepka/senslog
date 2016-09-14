package cz.hsrs.rest.beans;

import com.fasterxml.jackson.annotation.JsonProperty;


public class VgiObservationBean {

	@JsonProperty("obsId")
    public int obsVgiId;
    public Integer gid;
    public String timeString;
    public int categoryId;
    public String description;
    public String attributes;
    public int datasetId;
    public long unitId;
    public int userId;
    public String timeReceived;
    public int mediaCount;
    public String geoJSON;
    
    public VgiObservationBean(){
    }
    
    public VgiObservationBean(int obsVgiId, Integer gid, String timeString,
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
     * 
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
     * @param geomJson
     */
    public VgiObservationBean(int obsVgiId, Integer gid, String timeString,
            int categoryId, String description, String attributes,
            int datasetId, long unitId, int userId, String timeReceived,
            int mediaCount, String geomJson) {
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
        this.geoJSON = geomJson;
    }
}
