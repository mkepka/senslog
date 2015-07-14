package cz.hsrs.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.hsrs.db.model.Phenomenon;
import cz.hsrs.db.model.composite.UnitSensor;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.DBUtil;




public class DBChartUtils extends DBUtil {
	
	

	public DBChartUtils() throws SQLException {
		
	}

	public Map<Double, Date> getObservationsByGID(int gid, Date from, Date to) {
		return null;
	}

	public Map<Double, Date> getObservationsByUnit(long unit_id, Date from,
			Date to) {
		return null;
	}

	public Map<Date, Double> getObservationsBySensor(long sensor_id, long unit_id, Date from,
			Date to) throws Exception {
		Map<Date, Double> observations = new HashMap<Date, Double>();		
		String queryObservations = "SELECT observed_value, time_stamp FROM observations WHERE "
				+ "observations.sensor_id = "
				+ sensor_id
				+ " AND "
				+ "observations.unit_id = "
				+ unit_id
				+ " AND "
				+ "observations.time_stamp >= '"
				+ format.format(from)
				+ "' AND "
				+ "observations.time_stamp <= '"
				+ format.format(to)
				+ "';";

		//System.out.println(queryObservations);
		ResultSet obs =  SQLExecutor.getInstance().executeQuery(queryObservations);

		while (obs.next()) {
		
			observations.put(obs.getTimestamp(2), obs.getDouble(1));
		}

		return observations;
	}

	public Phenomenon getPhenomenonById(int id) {
		return null;
	}

	public UnitSensor getSensorById(long id) {
		return null;
	}
}
