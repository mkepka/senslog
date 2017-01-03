/**
 * 
 */
package cz.hsrs.db.vgi.util;


/**
 * Class contains names of attributes for REST methods
 * and Classes serialization
 * @author mkepka
 *
 */
public class VgiParams {

	/*
	 * VgiObservation
	 */
    public static final String OBS_VGI_ID_NAME = "obs_vgi_id";
    public static final String DESCRIPTION_NAME = "description";
    public static final String ATTRIBUTES_NAME = "attributes";
    
    /*
     * Timestamp
     */
    public static final String TIMESTAMP_NAME = "time_stamp";
    public static final String FROM_TIME_NAME = "from_time";
    public static final String TO_TIME_NAME = "to_time";
    public static final String TIME_RECEIVED_NAME = "time_received";
    
    /*
     * Category
     */
    public static final String CATEGORY_ID_NAME = "category_id";
    public static final String CATEGORY_NAME_NAME = "category_name";
    
    /*
     * Dataset
     */
    public static final String DATASET_ID_NAME = "dataset_id";
    public static final String DATASET_NAME_NAME = "dataset_name";
    
    /*
     * Unit
     */
    public static final String UNIT_ID_NAME = "unit_id";
    public static final String UUID_NAME = "uuid";
    
    /*
     * Geometry
     */
    public static final String LAT_NAME = "lat";
    public static final String LON_NAME = "lon";
    public static final String ALT_NAME = "alt";
    public static final String DOP_NAME = "dop";
    public static final String EXTENT_NAME = "extent";
    
    /*
     * Media
     */
    public static final String MEDIA_NAME = "media";
    public static final String MEDIA_ID_NAME = "media_id";
    public static final String MEDIA_TYPE_NAME = "media_type";
    public static final String MEDIA_COUNT_NAME = "media_count";
    public static final String OBSERVED_MEDIA_NAME = "observed_media";
    
    /*
     * User
     */
    public static final String USER_NAME = "user_name";
    public static final String USER_ID_NAME = "user_id";
    
    /*
     * Format
     */
    public static final String FORMAT_NAME = "format";
    public static final String FORMAT_GEOJSON_NAME = "geojson";
}