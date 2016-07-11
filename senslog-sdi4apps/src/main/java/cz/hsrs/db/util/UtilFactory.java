package cz.hsrs.db.util;

/**
 * Factory class that generates instances of all util classes
 * @author jezekjan
 *
 */
public class UtilFactory {
	public TrackUtil trackUtil;
	public UnitUtil unitUtil;
	public UserUtil userUtil;
	public GroupUtil groupUtil;
	public SensorUtil sensorUtil;
	public AlertUtil alertUtil;
	public AnalystUtil analystUtil;
	public ManagementUtil manUtil;
	
	public UtilFactory() {
		trackUtil = new TrackUtil();
		unitUtil= new UnitUtil();
		userUtil= new UserUtil();
		groupUtil= new GroupUtil();
		sensorUtil= new SensorUtil();
		alertUtil = new AlertUtil();
		analystUtil = new AnalystUtil();
		manUtil = new ManagementUtil();
	}
}