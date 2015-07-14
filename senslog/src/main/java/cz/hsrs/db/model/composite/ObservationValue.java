package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;

public class ObservationValue implements DBObject {

	private final double value;
	private Date time_stamp;
	private final String time_string;
	private int gid;

	public static final SimpleDateFormat formater = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ssZ");

	public ObservationValue() {
		this(0, null, 0);
	}

	public ObservationValue(double value, String timeStamp, int gid) {
		super();
		this.value = value;
		this.time_string = timeStamp;
		this.gid = gid;
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {

		return new ObservationValue(set.getDouble("observed_value"), set
				.getString("time_stamp"), set.getInt("gid"));
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public double getValue() {
		return value;
	}

	public String getTime() {
		return time_string;
	}

	public Date internalGetTime_stamp() {
		if (time_stamp == null) {
			try {
				time_stamp = formater.parse(time_stamp + "00");
			} catch (ParseException e) {
				e.printStackTrace();
				SQLExecutor.logger.log(Level.SEVERE, e.getMessage());
			}
		}
		return time_stamp;
	}
}
