package cz.hsrs.db.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Observation;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.ObservationMedlov;
import cz.hsrs.db.model.composite.UnitSensor;

public class ObservationUtil extends SensorUtil {

	private final SimpleDateFormat formater = new SimpleDateFormat(
	"yyyy-MM-dd HH:mm:ssZ");
	
	public ObservationUtil() {		
	}
	
	public boolean insertObservation(Observation obs) throws SQLException{
			
		return obs.insertToDb();
		
	}
	
	public Observation getObservation(Date date, long unit_id, long sensor_id ) throws SQLException{
		String query = "SELECT * FROM observations WHERE time_stamp = '" +formater.format(date) +"'"+
				" AND unit_id = " + unit_id +
				" AND sensor_id =" + sensor_id
				;
		ResultSet res = stmt.executeQuery(query);
		res.next();
		return new Observation(res);
		
	}
	

	public Observation getObservationBefore(UnitPosition p, long sensor_id) throws SQLException, NoItemFoundException{
		UnitUtil ut = new UnitUtil();
		int confTime = ut.getUnitConfTimeById(p.getUnit_id());
		String q = "SELECT * FROM ( SELECT * FROM observations " +
		"WHERE unit_id = " +p.getUnit_id() + " AND "+
		"sensor_id = "+ sensor_id +
		" AND time_stamp < timestamp'" + p.getTime_stamp() +"' ORDER BY time_stamp DESC LIMIT 1) AS foo" +
		" WHERE (timestamp'"+
		p.getTime_stamp()+ "' - time_stamp) < interval'"+confTime +" seconds';";	
		ResultSet res = stmt.executeQuery(q);
		if (res.next()) {
			return new Observation(res);
		} else
			throw new NoItemFoundException("Last observation for " + p.getGid()
					+ " not found.");
	}		
		
	public UnitPosition ggetExactObservationPosition(int gid, long unit_id) throws SQLException, NoItemFoundException{
		String query = "SELECT "+ UnitPosition.SELECT +" FROM last_units_positions WHERE " +
					   "unit_id = "+unit_id+" AND "+
					   "gid = " + gid;
						;
		ResultSet res = stmt.executeQuery(query);
		if (res.next()){
			/**posledni pozice existuje*/
			return new UnitPosition(res);
		} else {
			String query2 = "SELECT "+ UnitPosition.SELECT +" FROM units_positions WHERE " +
			   "unit_id = "+unit_id+" AND "+
			   "gid = " + gid;
				;
				ResultSet res2 = stmt.executeQuery(query2);
				if (res2.next()){
					return new UnitPosition(res2);
				}
		}
		
		throw new NoItemFoundException(unit_id + " " +gid);
		
	}
	public UnitPosition getExactObservationPosition(Date date, long unit_id) throws SQLException, NoItemFoundException{
		String query = "SELECT "+ UnitPosition.SELECT +" FROM last_units_positions WHERE " +
					   "unit_id = "+unit_id+" AND "+
					   "time_stamp = '" + formater.format(date)+"'";
						;
		ResultSet res = stmt.executeQuery(query);
		if (res.next()){
			/**posledni pozice existuje*/
			return new UnitPosition(res);
		} else {
			String query2 = "SELECT "+ UnitPosition.SELECT +" FROM units_positions WHERE " +
			   "unit_id = "+unit_id+" AND "+
			   "time_stamp = '" + formater.format(date)+"' ORDER BY units_positions.time_stamp DESC LIMIT 1";
				;
				ResultSet res2 = stmt.executeQuery(query2);
				if (res2.next()){
					return new UnitPosition(res2);
				}
		}
		
		throw new NoItemFoundException(unit_id + formater.format(date));
		
	}
	public List<ObservationMedlov> getObservationsMedlov(long unitIdFrom, long unitIdTo, String timeFrom, String timeTo) throws SQLException{
		String query = "SELECT o.observation_id, o.unit_id, o.sensor_id, s.sensor_name, o.time_stamp, o.observed_value, p.unit, p.phenomenon_name, p.phenomenon_id, up.the_geom "+
					   "FROM observations AS o " +
								"INNER JOIN sensors AS s ON s.sensor_id = o.sensor_id "+
					   			"INNER JOIN phenomenons AS p ON p.phenomenon_id = s.phenomenon_id "+
					   			"LEFT JOIN units_positions AS up ON o.gid = up.gid "+
					   "WHERE o.time_stamp > '"+ timeFrom +"' AND o.time_stamp < '"+timeTo+"' "+
					   "AND o.unit_id >= "+unitIdFrom+" AND o.unit_id <= "+unitIdTo +
					   " ORDER BY o.time_stamp;";
		ResultSet res = stmt.executeQuery(query);
		return (List<ObservationMedlov>)generateObjectList(new ObservationMedlov(), res);		
	}
	
	public List<ObservationMedlov> getObservationsMedlov(String timeFrom, String timeTo) throws SQLException{
		String query = "SELECT o.observation_id, o.unit_id, o.sensor_id, s.sensor_name, o.time_stamp, o.observed_value, p.unit, p.phenomenon_name, p.phenomenon_id, up.the_geom "+
					   "FROM observations AS o " +
								"INNER JOIN sensors AS s ON s.sensor_id = o.sensor_id "+
					   			"INNER JOIN phenomenons AS p ON p.phenomenon_id = s.phenomenon_id "+
					   			"LEFT JOIN units_positions AS up ON o.gid = up.gid "+
					   "WHERE o.time_stamp > '"+ timeFrom +"' AND o.time_stamp < '"+timeTo+"' "+
					   " ORDER BY o.time_stamp;";
		ResultSet res = stmt.executeQuery(query);
		return (List<ObservationMedlov>)generateObjectList(new ObservationMedlov(), res);		
	}


}
