package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.UnitUtil;

public class UnitPosition implements DBObject {

	private final long unit_id;
	private final int gid;
	private final double speed;
	private final double x;
	private final double y;
	private Date time_stamp;
	private final String time_string;
	private int group_id;
	private final String geom;
	// public static final String SELECT =
	// " gid, unit_id, time_stamp, st_astext(the_geom) ";
	public static final String SELECT = " gid, unit_id, time_stamp, st_x(the_geom), st_y(the_geom), speed ";
	private final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	//"yyyy-MM-dd'T'HH:mm:ss.SSSZ"

	public UnitPosition() {
		speed = 0;
		unit_id = 0;
		gid = 0;
		y = 0;
		x = 0;
		time_stamp = null;
		time_string = null;
		geom = null;

	}

	public UnitPosition(ResultSet set) throws SQLException {
		this.gid = set.getInt("gid");
		this.unit_id = set.getLong("unit_id");
		this.time_string = set.getString("time_stamp");
		this.speed = set.getDouble("speed");
		this.x = set.getDouble("st_x");
		this.y = set.getDouble("st_y");
		this.geom = createPointString(x, y);

		UnitUtil uUtil = new UnitUtil();
		this.group_id = uUtil.getGroupID(unit_id);

	}

	public UnitPosition(long unitId, double x, double y, Date timeStamp) {
		this(unitId, x, y, timeStamp, Double.NaN);
	}

	public UnitPosition(long unitId, double x, double y, Date timeStamp,
			Double speed) {
		super();
		unit_id = unitId;
		String gidquery = "SELECT nextval('units_positions_gid_seq'::regclass)";
		int id = 0;
		try {
			ResultSet res = SQLExecutor.getInstance().executeQuery(gidquery);
			res.next();
			id = res.getInt(1);
		} catch (Exception e) {
			// Should never happend
			e.printStackTrace();
		}

		this.gid = id;
		this.x = x;
		this.y = y;
		this.geom = createPointString(x, y);
		this.time_stamp = timeStamp;
		this.time_string = formater.format(time_stamp);
		this.speed = speed;

	}

	public double getSpeed() {
		return speed;
	}

	public int getGroup_id() {
		return group_id;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new UnitPosition(set);
	}

	public int getGid() {
		return gid;
	}

	public String internalGetGeom() {
		return geom;
	}

	public Date internalGetTime_stamp() {
		if (time_stamp == null) {
			try {
				if(time_string.length()> 27){
					int len = time_string.length();
					String time_string_time = time_string.substring(0, 23);
					String time_string_timezone = time_string.substring(time_string.length()-3, time_string.length());
					
					String time_string_new = ""+time_string_time+time_string_timezone;
					
					time_stamp = formater.parse(time_string_new + "00");
				}
				else{
					time_stamp = formater.parse(time_string + "00");
				}
			} catch (ParseException e) {
				// Should never happpend
				e.printStackTrace();
				SQLExecutor.logger.log(Level.SEVERE, e.getMessage());
			}
		}
		return time_stamp;
	}

	public long getUnit_id() {
		return unit_id;
	}

	public boolean insertToDb() throws SQLException {
		String speedString = "";
		if ((new Double(speed)).equals(Double.NaN)) {
			speedString = "NULL";
		} else {
			speedString = String.valueOf(speed);
		}
		String ins = "INSERT INTO units_positions(the_geom, unit_id, time_stamp, gid, speed) VALUES ("
				+ "st_geomfromtext('"
				+ this.geom
				+ "',4326), "
				+ this.unit_id
				+ ", "
				+ "timestamp with time zone'"
				+ this.formater.format(time_stamp)
				+ "',"
				+ gid + ", " + speedString + ");";
		// Statement stmt = con.createStatement();
		int i = SQLExecutor.executeUpdate(ins);
		if (i == 0)
			return false;
		return true;

	}

	public String getPostgisString() {
		return "st_geomfromtext('" + this.geom + "',4326)";
	}

	private String createPointString(double x, double y) {
		return new String("POINT(" + x + " " + y + ")");
	}

	public String getPostgisGeomString() {
		return this.geom;
	}

	public String getTime_stamp() {
		return time_string;
	}

}
