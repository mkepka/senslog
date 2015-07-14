package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import cz.hsrs.db.model.Phenomenon;
import cz.hsrs.db.model.Sensor;
import cz.hsrs.db.model.composite.AggregateObservation;
import cz.hsrs.db.model.composite.ObservationValue;
import cz.hsrs.db.model.composite.UnitSensor;

public class SensorUtil extends TrackUtil {

	public SensorUtil() {
		super();
	}

	public List<UnitSensor> getUnitsSensors(long unit_id) throws SQLException {
		String queryObservations = "select *"
				+ " from sensors, units_to_sensors "
				+ "WHERE units_to_sensors.unit_id = " + unit_id
				+ " AND units_to_sensors.sensor_id = sensors.sensor_id;";

		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<UnitSensor>) generateObjectList(new UnitSensor(), res);// (List<Group>)generateObjectList(new
		// Group(),
		// res);
		// writeJSON(writer, new Group(), res);
		/** Do one object */

	}
	
	public List<Sensor> getSensors(long unit_id) throws SQLException {
		String queryObservations = "select *"
				+ " from sensors, units_to_sensors "
				+ "WHERE units_to_sensors.unit_id = " + unit_id
				+ " AND units_to_sensors.sensor_id = sensors.sensor_id order by sensors.sensor_id;";

		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<Sensor>) generateObjectList(new Sensor(), res);// (List<Group>)generateObjectList(new
		// Group(),
		// res);
		// writeJSON(writer, new Group(), res);
		/** Do one object */

	}

	public Sensor getSensorById(long sensor_id) throws SQLException {
		String queryObservations = "select *" + " from sensors "
				+ "WHERE sensor_id = " + sensor_id + ";";

		ResultSet res = stmt.executeQuery(queryObservations);

		if (res.next()) {
			return (Sensor) (new Sensor()).getDBObject(res);
		} else {
			throw new SQLException("Sensor " + sensor_id + " not faund");
		}

	}

	public boolean hasSensor(long unit_id, long sensor_id) throws SQLException {
		String queryObservations = "select *" + " from units_to_sensors "
				+ "WHERE units_to_sensors.unit_id = " + unit_id
				+ " AND units_to_sensors.sensor_id = " + sensor_id;

		ResultSet res = stmt.executeQuery(queryObservations);

		return res.next();
		/** Do one object */

	}

	public List<ObservationValue> getSensorObservations(long unit_id,
			long sensor_id, String from, String to) throws SQLException {
		if (from == null && to == null) {
			return getSensorObservations(unit_id, sensor_id);
		} else if (to == null && from != null) {
			return getSensorObservationsFrom(unit_id, sensor_id, from);
		} else if (to != null && from == null) {
			return getSensorObservationsTo(unit_id, sensor_id, to);
		} else {

			String queryObservations = "select gid, observed_value, time_stamp"
					+ " from observations " + "WHERE unit_id = " + unit_id
					+ " AND sensor_id = " + sensor_id + " AND time_stamp > '"
					+ from + "'" + " AND time_stamp < '" + to + "'";
			ResultSet res = stmt.executeQuery(queryObservations);

			return (List<ObservationValue>) generateObjectList(
					new ObservationValue(), res);
		}

	}

	public List<AggregateObservation> getSensorObservationsTrunc(long unit_id,
			long sensor_id, String from, String to, String trunc)
			throws SQLException {

		String queryObservations = "select"
				+ " avg(observed_value) as avg_value, date_trunc('" + trunc
				+ "', time_stamp) AS dtrunc, count(*) AS count "
				+ " from observations " + "WHERE unit_id = " + unit_id
				+ " AND sensor_id = " + sensor_id + " AND time_stamp >= '"
				+ from + "'" + " AND time_stamp <= '" + to + "'"
				+ " GROUP BY dtrunc ORDER BY dtrunc DESC;";

		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<AggregateObservation>) generateObjectList(
				new AggregateObservation(), res);
	}

	protected List<ObservationValue> getSensorObservationsFrom(long unit_id,
			long sensor_id, String from) throws SQLException {

		String queryObservations = "select gid, observed_value, time_stamp"
				+ " from observations " + "WHERE unit_id = " + unit_id
				+ " AND sensor_id = " + sensor_id + " AND time_stamp > '"
				+ from + "'";
		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<ObservationValue>) generateObjectList(
				new ObservationValue(), res);

	}

	protected List<ObservationValue> getSensorObservationsTo(long unit_id,
			long sensor_id, String to) throws SQLException {

		String queryObservations = "select gid, observed_value, time_stamp"
				+ " from observations " + "WHERE unit_id = " + unit_id
				+ " AND sensor_id = " + sensor_id + " AND time_stamp < '" + to
				+ "'";
		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<ObservationValue>) generateObjectList(
				new ObservationValue(), res);

	}

	public List<ObservationValue> getSensorObservations(long unit_id,
			long sensor_id) throws SQLException {
		String queryObservations = "select gid, observed_value, time_stamp"
				+ " from observations " + "WHERE unit_id = " + unit_id
				+ " AND sensor_id = " + sensor_id;

		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<ObservationValue>) generateObjectList(
				new ObservationValue(), res);// (List<Group>)generateObjectList(new
		// Group(), res);
		// writeJSON(writer, new Group(), res);
		/** Do one object */

	}

	public Phenomenon getPhenomenonById(String id) throws SQLException {
		String queryObservations = "select * " + " from phenomenons "
				+ "WHERE phenomenon_id = '" + id + "'";

		ResultSet res = stmt.executeQuery(queryObservations);

		if (res.next()) {
			return new Phenomenon(res);// (List<Group>)generateObjectList(new
			// Group(), res);
		} else
			return null;
		// writeJSON(writer, new Group(), res);
		/** Do one object */

	}
}
