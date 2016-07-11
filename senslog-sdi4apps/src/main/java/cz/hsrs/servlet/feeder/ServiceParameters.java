package cz.hsrs.servlet.feeder;

/**
 * Class with names of operations and query parameters of HttpRequests 
 * @author jezekjan
 *
 */
public class ServiceParameters {

	public static String OPERATION = "Operation";
	// --- FeederServlet ---
	public static String INSERT_OBSERVATION = "InsertObservation";
	public static String INSERT_POSITION = "InsertPosition";
	public static String INSERT_POI = "InsertPOI";
	public static String INSERT_ALERT_EVENT = "InsertAlertEvent";
	public static String SOLVING_ALERT_EVENT = "SolvingAlertEvent";
	
	// --- Query parameters of HttpRequests
	/**
	 * Name of the query parameter with ID of unit:
	 * unit_id
	 */
	public static String UNIT_ID = "unit_id";
	/**
	 * Name of the query parameter with ID of sensor:
	 * sensor_id
	 */
	public static String SENSOR_ID = "sensor_id";
	/**
	 * Name of the query parameter with ID of phenomenon:
	 * phenomenon_id
	 */
	public static String PHENOMEN_ID = "phenomenon_id";
	/**
	 * Name of the query parameter with latitude:
	 * lat
	 */
	public static String LAT = "lat";
	/**
	 * Name of the query parameter with longitude:
	 * lon
	 */
	public static String LON = "lon";
	/**
	 * Name of the query parameter with altitude:
	 * alt
	 */
	public static String ALT = "alt";
	/**
	 * Name of the query parameter with speed:
	 * speed
	 */
	public static String SPEED = "speed";
	/**
	 * Name of the query parameter with dop:
	 * dop
	 */
	public static String DOP = "dop";
	/**
	 * Name of the query parameter with value:
	 * value
	 */
	public static String VALUE = "value";
	/**
	 * Name of the query parameter with date:
	 * date
	 */
	public static String DATE = "date";
	/**
	 * Name of the query parameter with beginning time stamp:
	 * from
	 */
	public static String FROM = "from";
	/**
	 * Name of the query parameter with end time stamp:
	 * to
	 */
	public static String TO = "to";
	/**
	 * Name of the query parameter with truncate interval:
	 * trunc
	 */
	public static final String TRUNC = "trunc";
	
	//public static String LOGGER_ID = "cz.hsrs.maplog";
	/**
	 * Name of the query parameter ID of alert:
	 * alert_id
	 */
	public static String ALERT_ID = "alert_id";
	/**
	 * Name of the query parameter ID of alert event:
	 * alert_event_id
	 */
	public static String ALERT_EVENT_ID = "alert_event_id";
	/**
	 * Name of the query parameter name of user:
	 * user
	 */
	public static final String USER = "user";
	/**
	 * Name of the query parameter name of group:
	 * group
	 */
	public static final String GROUP = "group";
	
	// --- SensorService ---
    public static final String GET_SENSORS = "GetSensors";
    public static final String GET_OBSERVATIONS = "GetObservations";
    public static final String GET_LAST_OBSERVATIONS = "GetLastObservations";
}