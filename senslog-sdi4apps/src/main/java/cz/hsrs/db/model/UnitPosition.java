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
    private final String srid;
    // public static final String SELECT = "gid, unit_id, time_stamp, st_astext(the_geom) ";
    public static final String SELECT = "gid, unit_id, time_stamp, st_x(the_geom), st_y(the_geom), speed, st_srid(the_geom) ";
    private final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    private final SimpleDateFormat formaterSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    //"yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    /**
     * Empty constructor initializes all attributes to null or zero
     */
    public UnitPosition() {
        speed = 0;
        unit_id = 0;
        gid = 0;
        y = 0;
        x = 0;
        time_stamp = null;
        time_string = null;
        geom = null;
        srid = null;
    }

    /**
     * Constructor instantiates new object from ResultSet
     * @param set ResultSet with columns:
     * gid, unit_id, time_stamp, speed, st_x, st_y, st_srid
     * @throws SQLException
     */
    public UnitPosition(ResultSet set) throws SQLException {
        this.gid = set.getInt("gid");
        this.unit_id = set.getLong("unit_id");
        this.time_string = set.getString("time_stamp");
        this.speed = set.getDouble("speed");
        this.x = set.getDouble("st_x");
        this.y = set.getDouble("st_y");
        this.geom = createPointString(x, y);
        this.srid = set.getString("st_srid");

        UnitUtil uUtil = new UnitUtil();
        this.group_id = uUtil.getGroupID(unit_id);
    }

    /**
     * Constructor instantiates object from fields 
     * @param unitId - id of unit
     * @param x - long coordinate
     * @param y - lat coordinate
     * @param timeStamp time stamp of position
     */
    public UnitPosition(long unitId, double x, double y, Date timeStamp) {
        this(unitId, x, y, timeStamp, Double.NaN, "");
    }

    /**
     * Constructor instantiates object from fields
     * @param unitId - id of unit
     * @param x - long coordinate
     * @param y - lat coordinate
     * @param timeStamp time stamp of position
     * @param speed - current speed of position
     * @param srid - SRID of coordinates
     */
    public UnitPosition(long unitId, double x, double y, Date timeStamp, Double speed, String srid) {
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
        this.srid = srid;
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
    
    public String getSRID(){
    	return srid;
    }

    public DBObject getDBObject(ResultSet set) throws SQLException {
        return new UnitPosition(set);
    }

    public int getGid() {
        return gid;
    }

    /**
     * Method returns geom as String:
     * "POINT(x y)"
     * @return String "POINT(x y)"
     */
    public String internalGetGeom() {
        return geom;
    }

    /**
     * Method returns time stamp of position as Date
     * if Date time stamp is not set parses it from String time stamp
     * processes String time stamp in case of using microseconds from DB
     * @return timestamp of position as Date
     */
    public Date internalGetTime_stamp() {
        if (time_stamp == null) {
            try {
                /**
                 * time string contains microseconds
                 */
                if(time_string.length() > 27){
                    // parses time string with seconds only
                    String time_string_time = time_string.substring(0, 23);
                    // parses time zone
                    String time_string_timezone = time_string.substring(time_string.length()-3, time_string.length());
                    // creates time string with seconds and time zone
                    String time_string_new = ""+time_string_time+time_string_timezone;
                    
                    time_stamp = formater.parse(time_string_new + "00");
                }
                /**
                 * time string contains milliseconds
                 */
                else if(time_string.length() > 21 && time_string.length() < 25){
                    time_stamp = formaterSecond.parse(time_string + "00");
                }
                /**
                 * time string contains seconds only
                 */
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

    /**
     * Method inserts new position into database
     * @return true if positions was successfully inserted, false otherwise
     * @throws SQLException is thrown if an Exception occurs during inserting
     */
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
                + "', 4326), "
                + this.unit_id
                + ", "
                + "timestamp with time zone'"
                + this.formater.format(time_stamp)
                + "',"
                + gid + ", " + speedString + ");";
        int i = SQLExecutor.executeUpdate(ins);
        /** If there is partitioning on table units_positions, function executeUpdate returns 0.
         *  If there is only units_positions table, function executeUpdate returns 1.
         */
        if (i == 1 || i == 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Method returns String to creates geometry object in PostGIS
     * "st_geomfromtext('POINT(x y)', SRID)";
     * @return "st_geomfromtext('POINT(x y)', SRID)"; as String
     */
    public String getPostgisString() {
        return "st_geomfromtext('" + this.geom + "', "+this.srid+")";
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