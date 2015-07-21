package cz.hsrs.db.util;

import java.sql.Connection;
import java.sql.SQLException;

public class UtilFactory {
	Connection con;
	
	public TrackUtil trackUtil;
	public UnitUtil unitUtil;
	public UserUtil userUtil;
	public GroupUtil groupUtil;
	public SensorUtil sensorUtil;
	public AlertUtil alertUtil;
	
	public UtilFactory() {
		//this.con = con;
		trackUtil = new TrackUtil();
		unitUtil= new UnitUtil();
		userUtil= new UserUtil();
		groupUtil= new GroupUtil();
		sensorUtil= new SensorUtil();
		alertUtil = new AlertUtil();
	}
	
	
	


}
