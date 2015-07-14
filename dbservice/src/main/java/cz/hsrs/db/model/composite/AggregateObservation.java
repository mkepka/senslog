package cz.hsrs.db.model.composite;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.model.Observation;

public class AggregateObservation  implements DBObject{
	
	private final double value;
	private final Date time_stamp;
	private int gid;
	private final int count;

//	public static final String SELECT = " avg(observed_value) as avg_value, date_trunc('day', time_stamp) AS dtrunc, count(*) AS count ";
	
	public static final SimpleDateFormat formater = new SimpleDateFormat(
	"yyyy-MM-dd HH:mm:ssZ");
	
	public AggregateObservation() {
		this(0,null,0);
	}
	public AggregateObservation(double value,  Date timeStamp, int count) {
		super();
		this.value = value;
		this.time_stamp = timeStamp;
		this.gid = gid;
		this.count=count;
	}
	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		String time_string = set.getString("dtrunc");
		Date time;
		try {
			time = formater.parse(time_string+"00");
		} catch (ParseException e) {
			throw new SQLException(e);
		}
		
		return new AggregateObservation(set.getDouble("avg_value"),time , set.getInt("count"));
	}
	public int getCount() {
		return count;
	}
	public double getValue() {
		return value;
	}
	public Date internalGetInternalTime() {
		return time_stamp;
	}
	
	public String getTime() {
		return formater.format(time_stamp);
	}
	
	

}
