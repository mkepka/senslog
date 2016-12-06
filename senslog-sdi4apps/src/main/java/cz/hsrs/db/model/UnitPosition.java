package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.util.UnitUtil;

public class UnitPosition implements DBObject {

    private final int gid;
    private final long unit_id;
    private final double x;
    private final double y;
    private final double alt;
    private final String time_stamp;
    private Date timeStamp;
    private final double dop;
    private final double speed;
    private int group_id;
    private final String geom;
    private final String srid;
    private final String time_received;
    private final String first_timestamp;
    
    public static final String SELECT = "gid, unit_id, time_stamp, st_x(the_geom), st_y(the_geom), speed, st_srid(the_geom) ";
    //private final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    //private final SimpleDateFormat formaterSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    private static final String SCHEMA_NAME = "public";
    //private static final String ALTITUDE_COLUMN = "altitude";
    //private static final String DATASET_TABLE_NAME = "units_positions";
    
    /**
     * Empty constructor initializes all attributes to null or zero
     */
    public UnitPosition() {
        speed = Double.NaN;
        dop = Double.NaN;
        unit_id = 0;
        gid = 0;
        y = Double.NaN;
        x = Double.NaN;
        alt = Double.NaN;
        time_stamp = null;
        geom = null;
        srid = null;
        time_received = null;
        first_timestamp = null;
    }

    /**
     * Constructor instantiates new object from ResultSet, without altitude!
     * @param set ResultSet with columns:
     * gid, unit_id, time_stamp, speed, st_x, st_y, st_srid
     * @throws SQLException
     */
    public UnitPosition(ResultSet set) throws SQLException {
        this.gid = set.getInt("gid");
        this.unit_id = set.getLong("unit_id");
        this.time_stamp = set.getString("time_stamp");
        this.speed = set.getDouble("speed");
        this.x = set.getDouble("st_x");
        this.y = set.getDouble("st_y");
        this.alt = Double.NaN;
        this.geom = createPointString(x, y);
        this.srid = set.getString("st_srid");
        this.dop = Double.NaN;
        this.time_received = null;
        this.first_timestamp = null;

        UnitUtil uUtil = new UnitUtil();
        this.group_id = uUtil.getGroupID(unit_id);
    }

    /**
     * Constructor instantiates object from fields 
     * @param unitId - id of unit
     * @param x - long coordinate
     * @param y - lat coordinate
     * @param timeStamp time stamp of position
     * @throws SQLException 
     */
    public UnitPosition(long unitId, double x, double y, Date timeStamp) throws SQLException {
        this(unitId, x, y, 0, timeStamp, Double.NaN, "");
    }
    
    /**
     * Constructor instantiates object from fields 
     * @param unitId - id of unit
     * @param x - long coordinate
     * @param y - lat coordinate
     * @param alt - altitude coordinate
     * @param timeStamp time stamp of position
     * @throws SQLException 
     */
    public UnitPosition(long unitId, double x, double y, double alt, Date timeStamp) throws SQLException {
        this(unitId, x, y, alt, timeStamp, Double.NaN, "");
    }

    /**
     * Constructor for creating UnitPosition from WebService
     * @param unitId
     * @param x
     * @param y
     * @param alt
     * @param timeStamp
     * @param speed
     * @param srid
     * @throws SQLException
     */
    public UnitPosition(long unitId, double x, double y, double alt, Date timeStamp, Double speed, String srid) throws SQLException {
        super();
        this.unit_id = unitId;
        this.gid = getNextGid();
        this.x = x;
        this.y = y;
        this.alt = alt;
        this.geom = createPointString(x, y);
        this.timeStamp = timeStamp;
        this.speed = speed;
        this.dop = Double.NaN;
        this.srid = srid;
        
        this.time_stamp = null;
        this.time_received = null;
        this.first_timestamp = null;
    }
    
    /**
     * Constructor for creating UnitPosition object from WebService
     * @param unitId
     * @param x
     * @param y
     * @param alt
     * @param timeStamp
     * @param speed
     * @param dop
     * @param srid
     * @throws SQLException
     */
    public UnitPosition(long unitId, double x, double y, double alt, Date timeStamp, Double speed, Double dop, String srid) throws SQLException {
        super();
        this.unit_id = unitId;
        this.gid = getNextGid();
        this.x = x;
        this.y = y;
        this.alt = alt;
        this.geom = createPointString(x, y);
        this.timeStamp = timeStamp;
        this.speed = speed;
        this.dop = dop;
        this.srid = srid;
        
        this.time_stamp = DateUtil.formatSecsTZ.format(timeStamp);
        this.time_received = null;
        this.first_timestamp = null;
    }
    
    /**
     * Constructor for creating UnitPosition object from DB
     * @param gid
     * @param unit_id
     * @param alt
     * @param time_string
     * @param dop
     * @param speed
     * @param geom
     * @param srid
     * @param receivedString
     * @param firstTimestampString
     */
    public UnitPosition(int gid, long unit_id, double x, double y, double alt, String time_string, double dop, double speed, 
            String srid, String receivedString, String firstTimestampString) {
        this.gid = gid;
        this.unit_id = unit_id;
        this.x = x;
        this.y = y;
        this.alt = alt;
        this.geom = null;
        this.time_stamp = time_string;
        this.dop = dop;
        this.speed = speed;
        this.srid = srid;
        
        this.time_received = receivedString;
        this.first_timestamp = firstTimestampString;
    }

    /**
     * Constructor for updating UnitPosition from webservice
     * @param gid
     * @param unit_id
     * @param x
     * @param y
     * @param alt
     * @param timestamp
     * @param dop
     * @param speed
     * @param srid
     */
    public UnitPosition(int gid, long unit_id, double x, double y, double alt, Date timestamp, double dop, double speed, String srid) {
        this.gid = gid;
        this.unit_id = unit_id;
        this.x = x;
        this.y = y;
        this.alt = alt;
        this.geom = this.createPointString(x, y);
        this.timeStamp = timestamp;
        this.dop = dop;
        this.speed = speed;
        this.srid = srid;
        
        this.time_stamp = DateUtil.formatSecsTZ.format(timestamp);
        this.time_received = null;
        this.first_timestamp = null;
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
    
    public double getAlt(){
        return alt;
    }
    
    public String getSRID(){
        return srid;
    }

    /**
     * Inherited method converts content of ResultSet to UnitPosition object
     */
    public DBObject getDBObject(ResultSet set) throws SQLException {
        return new UnitPosition(set);
    }

    /**
     * Getter returns gid of unit_position
     * @return gid
     */
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
     * @throws SQLException 
     */
    public Date internalGetTimestamp() throws SQLException {
        if (timeStamp == null) {
            try {
                return DateUtil.parseTimestamp(time_stamp);
            } catch (ParseException e) {
                SQLExecutor.logger.log(Level.SEVERE, e.getMessage());
                throw new SQLException("Timestamp of position is not present!");
            }
        }
        else{
            return timeStamp;
        }
    }

    /**
     * Getter returns Unit_ID
     * @return unit_id
     */
    public long getUnit_id() {
        return unit_id;
    }

    /**
     * Method inserts new position into database
     * @return true if positions was successfully inserted, false otherwise
     * @throws SQLException is thrown if an Exception occurs during inserting
     */
    public boolean insertToDb() throws SQLException {
        StringBuffer queryBuff= new StringBuffer();
        if(SQLExecutor.isAltitudeEnabled()){
            queryBuff.append("INSERT INTO "+SCHEMA_NAME+".units_positions(altitude, the_geom, unit_id, time_stamp, gid, speed, dop) VALUES (");
            queryBuff.append(Double.isNaN(this.alt) ? "NULL, " : String.valueOf(this.alt)+", ");
        }
        else{
            queryBuff.append("INSERT INTO "+SCHEMA_NAME+".units_positions(the_geom, unit_id, time_stamp, gid, speed, dop) VALUES (");
        }
        queryBuff.append("st_geomfromtext('"+ this.geom + "', "+(this.srid.isEmpty() ? "4326" : this.srid)+"), ");
        queryBuff.append(this.unit_id + ", ");
        queryBuff.append("timestamp with time zone '" + DateUtil.formatSecsTZ.format(this.timeStamp) + "', ");
        queryBuff.append(this.gid + ", ");
        queryBuff.append(Double.isNaN(this.speed) ? "NULL, " : String.valueOf(this.speed)+", ");
        queryBuff.append(Double.isNaN(this.dop) ? "NULL" : String.valueOf(this.dop));
        queryBuff.append(");");
        
        String ins = queryBuff.toString();
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
     * Method returns String to create geometry object in PostGIS
     * "st_geomfromtext('POINT(x y)', SRID)";
     * @return "st_geomfromtext('POINT(x y)', SRID)"; as String
     */
    public String getPostgisString() {
        if(this.srid == null){
            return "st_geomfromtext('" + this.geom + "', 4326)";
        }
        else{
            if(this.srid.isEmpty()){
                return "st_geomfromtext('" + this.geom + "', 4326)";
            }
            else{
                return "st_geomfromtext('" + this.geom + "', "+this.srid+")";
            }
        }
    }

    private String createPointString(double x, double y) {
        return new String("POINT(" + x + " " + y + ")");
    }

    /**
     * 
     * @return the_geom
     */
    public String getPostgisGeomString() {
        return this.geom;
    }

    /**
     * 
     * @return the time_stamp
     */
    public String getTime_stamp() {
        return time_stamp;
    }
    
    /**
     * @return the dop
     */
    public double getDop() {
        return dop;
    }

    /**
     * @return the srid
     */
    public String getSrid() {
        return srid;
    }

    /**
     * @return the time_received
     * @throws ParseException 
     */
    public Date internalGetTimeReceived() throws ParseException {
        return DateUtil.parseTimestampMicro(this.time_received);
    }

    /**
     * @return the time_received
     */
    public String getReceivedTimestampString() {
        return this.time_received;
    }

    /**
     * @return the first_timestamp
     * @throws ParseException 
     */
    public Date internalGetFirstTimestamp() throws ParseException {
        return DateUtil.parseTimestampMicro(this.first_timestamp);
    }

    /**
     * @return the firstTsString
     */
    public String getFirstTimesstamp() {
        return this.first_timestamp;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UnitPosition [unit_id=" + unit_id + ", gid=" + gid + ", speed="
                + speed + ", x=" + x + ", y=" + y + ", alt=" + alt
                + ", time_string=" + time_stamp + ", group_id=" + group_id
                + ", srid=" + srid + "]";
    }

    /**
     * Method get next value of sequence for GID 
     * @return next value of GID as integer
     * @throws SQLException
     */
    private int getNextGid() throws SQLException{
        try{
            String queryGid = "SELECT nextval('"+SCHEMA_NAME+".units_positions_gid_seq'::regclass);";
            ResultSet resId = SQLExecutor.getInstance().executeQuery(queryGid);
            if(resId.next()){
                return resId.getInt(1);
            }
            else{
                throw new SQLException("Next value of GID cannot be selected!");
            }
        } catch(SQLException e){
            throw new SQLException("Next value of GID cannot be selected!");
        }
    }
}