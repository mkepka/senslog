package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.Unit;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.composite.LastPosition;
import cz.hsrs.db.pool.SQLExecutor;

public class UserUtil extends GroupUtil {

    private final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    public UserUtil() {
        
    }

    public String getUserLanguage(String user_name) throws SQLException, NoItemFoundException {
        String select = "SELECT lang FROM system_users WHERE user_name = '"+user_name+"'";
        ResultSet res = stmt.executeQuery(select);
        if (res.next()) {
            return res.getString(1);
        } else
            throw new NoItemFoundException("getUserLanguage " + user_name + " not found.");
    }
    public int setUserLanguage(String user_name, String newLang) throws SQLException{
        String query = "UPDATE system_users SET lang = '"+newLang+"' WHERE user_name = '"+user_name+"';";
        return SQLExecutor.executeUpdate(query);
    }
    
    public boolean getAudio(String user_name) throws SQLException, NoItemFoundException {
        String select = "SELECT audio FROM system_users WHERE user_name = '"+user_name+"'";
        ResultSet res = stmt.executeQuery(select);
        if (res.next()) {
            return res.getBoolean(1);
        } else
            throw new NoItemFoundException("getAudio " + user_name + " not found.");
    }
    @SuppressWarnings("unchecked")
    public List<UnitPosition> getLastPositionsByUserName(String user_name)
            throws SQLException {
        return (List<UnitPosition>) generateObjectList(new UnitPosition(),
                getLastPositionsByUserNameRes(user_name));
    }
    
    @Deprecated
    public String getRole(String user_name) throws SQLException, NoItemFoundException{
        String query = "SELECT user_role FROM rights, system_users WHERE " +
                "system_users.user_name = '"+user_name + "' AND "+
                "system_users.rights_id = rights.rights_id";
        ResultSet res = stmt.executeQuery(query);
        if (res.next()) {
            return res.getString(1);
        } else
            throw new NoItemFoundException("getUserPassword " + user_name
                    + " not found.");
    }
    
    
/*
    public List<String> getUserOperation(String role){
        List oper = new ArrayList();
        String query = "SELECT * from ";
        
        return oper;
    }
*/
    @SuppressWarnings("unchecked")
	public List<LastPosition> getLastPositionWithStatus(String user_name)
            throws SQLException {
        List<LastPosition> lastPositions = new ArrayList<LastPosition>();

        List<UnitPosition> positions = (List<UnitPosition>) generateObjectList(
                new UnitPosition(), getLastPositionsByUserNameRes(user_name));

        for (UnitPosition pos : positions) {
            LastPosition lp = (new UnitUtil()).getLastPositionWithStatus(pos);
            lastPositions.add(lp);
        }
        return lastPositions;
    }

    public ResultSet getLastPositionsByUserNameRes(String user_name)
            throws SQLException {
        /**
         * select last_units_positions.gid, st_astext(the_geom),
         * last_units_positions.unit_id, last_units_positions.time_stamp from
         * last_units_positions, units_to_groups,system_users WHERE
         * system_users.user_name = 'pepa' AND system_users.group_id=
         * units_to_groups.group_id AND units_to_groups.unit_id =
         * last_units_positions.unit_id ;
         */
        String last_pos_table = SQLExecutor.getUnitsLastPositions_table();
        String queryObservations = "SELECT gid, st_x(the_geom), st_y(the_geom), st_srid(the_geom), speed, "
                + last_pos_table
                + ".unit_id, time_stamp, units_to_groups.group_id "
                + "FROM "
                + last_pos_table
                + ", "
                + "units_to_groups "
                + " WHERE ("
                + this.getWhereStatemant(user_name, "units_to_groups.group_id")
                + ") AND units_to_groups.unit_id = "
                + last_pos_table
                + ".unit_id; ";

        ResultSet res = stmt.executeQuery(queryObservations);
        
        return res;

    }

    public ResultSet getLastPositionsByUserNameRes(String user_name,
            Long unit_id) throws SQLException {

        if (unit_id == null) {
            return getLastPositionsByUserNameRes(user_name);
        } else {
            String last_pos_table = SQLExecutor
                    .getUnitsLastPositions_table();
            String queryObservations = "SELECT gid, st_x(the_geom), st_y(the_geom), st_srid(the_geom), speed, "
                    + last_pos_table
                    + ".unit_id, time_stamp, units_to_groups.group_id "
                    + "FROM "
                    + last_pos_table
                    + ", "
                    + "units_to_groups "
                    + " WHERE ("
                    + this.getWhereStatemant(user_name,
                            "units_to_groups.group_id")
                    + ") AND units_to_groups.unit_id = "
                    + last_pos_table
                    + ".unit_id AND "
                    + last_pos_table
                    + ".unit_id = "
                    + unit_id;

            ResultSet res = stmt.executeQuery(queryObservations);
            return res;
        }

    }

    /**
     * Get Positions by user name
     * 
     * @param user_name
     * @param writer
     * @param limit
     * @throws Exception
     */
    public ResultSet getPositionsByUserName(String user_name, Integer limit)
            throws SQLException {

        //List<UnitPosition> up = new ArrayList<UnitPosition>();
        String sqlLimit = "";
        if (limit != null) {
            /**
             * optimalization
             */
            if (limit == 1) {
                return getLastPositionsByUserNameRes(user_name);
            }
            sqlLimit = "LIMIT " + limit;
        }
        // SimpleDateFormat format = new
        // SimpleDateFormat("yyyy-MM-dd HH:MM:ss ZZ");

        String pos_table =  SQLExecutor.getUnitsPositions_table();
        String queryObservations = "select gid, st_x(the_geom), st_y(the_geom), st_srid(the_geom), speed, "
                + pos_table
                + ".unit_id, time_stamp, units_to_groups.group_id  " + "from  "
                + pos_table + ", units_to_groups " + "WHERE ("
                + this.getWhereStatemant(user_name, "units_to_groups.group_id")
                + ") AND units_to_groups.unit_id = " + pos_table + ".unit_id "
                + " ORDER BY time_stamp DESC " + sqlLimit + " ;";

        ResultSet res = stmt.executeQuery(queryObservations);
        return res; // List<UnitPosition>) generateObjectList(new
        // UnitPosition(), res);
        // writeJSON(writer, new UnitPosition(), res);
    }

    public ResultSet getTracksByUserName(String user_name, int limit)
            throws SQLException {
        /**
         * select gid, st_astext(the_geom) from units_tracks, units_to_groups,
         * system_users WHERE system_users.user_name = 'pepa' AND
         * system_users.group_id= units_to_groups.group_id AND
         * units_to_groups.unit_id = units_tracks.unit_id ;
         */

        String tracks_table =  SQLExecutor.getUnitsTracks_table();
        String queryObservations = "select gid, st_astext(the_geom), "
                + tracks_table
                + ".unit_id, track_start, track_end, units_to_groups.group_id "
                + "from  " + tracks_table + "," + " units_to_groups"
                + " WHERE ("
                + this.getWhereStatemant(user_name, "units_to_groups.group_id")
                + ") AND units_to_groups.unit_id = " + tracks_table
                + ".unit_id order by track_end desc limit " + limit + "; ";

        ResultSet res = stmt.executeQuery(queryObservations);
        return res; // (List<UnitTrack>) generateObjectList(new UnitTrack(),
        // res);

    }

    public String getUserPassword(String user_name) throws SQLException,
            NoItemFoundException {
        String queryObservations = "select user_password from system_users"
                + " WHERE user_name='" + user_name + "';";

        ResultSet res = stmt.executeQuery(queryObservations);
        if (res.next()) {
            return res.getString(1);
        } else
            throw new NoItemFoundException("getUserPassword " + user_name
                    + " not found.");
    }

    public int insertUser(String user_name, String pass)
            throws SQLException, NoItemFoundException {
        String insert = "insert into system_users(user_name, user_password) Values('"
                + user_name + "','" + pass + "');";
        return SQLExecutor.executeUpdate(insert);
    }

    public int deleteUser(String user_name) throws SQLException,
            NoItemFoundException {
        String del = "DELETE FROM system_users WHERE user_name='" + user_name
                + "';";
        return SQLExecutor.executeUpdate(del);
    }

    public int setUserSession(String user_name, String session_id, String IP)
            throws SQLException {
        
        
            /**try to delete session if exists*/
            delUserSession(session_id);
        
        String getUsID = "SELECT user_id FROM system_users WHERE user_name = '"
                + user_name + "'";
        ResultSet res = stmt.executeQuery(getUsID);
        res.next();
        int user_id = res.getInt(1);

        String insert = "INSERT INTO sessions(session_id, system_user_id, ip) VALUES ("
                + "'" + session_id + "'," + user_id + ",'" + IP + "')";
        return SQLExecutor.executeUpdate(insert);
    }

    public int delUserSession(String session_id) throws SQLException {

        String insert = "DELETE FROM sessions WHERE session_id ='" + session_id
                + "'";
        return SQLExecutor.executeUpdate(insert);
    }
    
    @SuppressWarnings("unchecked")
	public List<Unit> getUnitsByUser(String user_name) throws SQLException{
        String query = "SELECT u.unit_id, u.holder_id, u.description " +
                "FROM units u, units_to_groups utg, system_users su " +
                "WHERE su.user_name = '"+user_name+"' " +
                "AND su.group_id = utg.group_id " +
                "AND utg.unit_id = u.unit_id;";
        ResultSet res = stmt.executeQuery(query);
        List<Unit> units = (List<Unit>)generateObjectList(new Unit(), res);
        return units;
    }

    public SimpleDateFormat getDateFormater() {
        return formater;
    }
    /**
     * Method returns positions of units in all groups for defined user collected in day defined by timeStamp parameter
     * @param user_name is String username of logged user 
     * @param timeStamp is day for which positions should be found
     * @return ResultSet object with positions during one defined day
     * @throws SQLException 
     */
    public ResultSet getPositionsByUserNameInDay2(String user_name, String timeStamp) throws SQLException {
        if(timeStamp !=null){
            String pos_table =  SQLExecutor.getUnitsPositions_table();
            String query = "SELECT gid, "+pos_table+".unit_id, units_to_groups.group_id, "
                    + "time_stamp, speed, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+", units_to_groups "
                    + "WHERE ("+this.getWhereStatemant(user_name, "units_to_groups.group_id")+") "
                        + "AND units_to_groups.unit_id = "+pos_table+".unit_id "
                        + "AND time_stamp > '"+timeStamp+"' AND time_stamp < timestamp '"+timeStamp+"' + INTERVAL '1 day' "
                        + "ORDER BY time_stamp DESC;";
            ResultSet res = stmt.executeQuery(query);
            return res;
        }
        else{
            throw new SQLException("Parameter timestamp was not defined!");
        }
    }
    
    /**
     * Method returns positions of units in all groups for defined user collected from defined timestamp parameter
     * @param user_name is String username of logged user 
     * @param fromTime is timestamp of the beginning
     * @param unitId is identifier of unit
     * @param ordering is order direction for results, can be ASC or DESC only
     * @return ResultSet object with positions from defined timestamp, number is limited by range of 1 day!!!
     * @throws SQLException
     */
    public ResultSet getPositionsByUserNameInDay(String user_name, String fromTime, long unitId, String ordering) throws SQLException {
        if(fromTime != null){
            String pos_table =  SQLExecutor.getUnitsPositions_table();
            /*String query = "SELECT gid, "+pos_table+".unit_id, units_to_groups.group_id, "
                    + "time_stamp, speed, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+", units_to_groups "
                    + "WHERE ("+this.getWhereStatemant(user_name, "units_to_groups.group_id")+") "
                        + "AND "+pos_table+".unit_id = "+unitId+" "
                        + "AND units_to_groups.unit_id = "+pos_table+".unit_id "
                        + "AND time_stamp > '"+fromTime+"' AND time_stamp < timestamp '"+fromTime+"' + INTERVAL '1 day' "
                        + "ORDER BY time_stamp DESC;";*/
            /*String query = "SELECT gid, "+pos_table+".unit_id, units_to_groups.group_id, "
                    + "time_stamp, speed, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+", units_to_groups "
                    + "WHERE ("+this.getWhereStatemant(user_name, "units_to_groups.group_id")+") "
                        + "AND "+pos_table+".unit_id = "+unitId+" "
                        + "AND units_to_groups.unit_id = "+pos_table+".unit_id "
                        + "AND time_stamp > '"+fromTime+"' AND time_stamp < timestamp '"+fromTime+"' + INTERVAL '1 month' "
                        + "ORDER BY time_stamp DESC;";*/
            
         // Simpler query for select positions
            String query = "SELECT time_stamp, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+" "
                    + "WHERE "+pos_table+".unit_id = " + unitId + " "
                        + "AND time_stamp >= '"+fromTime+"' AND time_stamp < '"+fromTime+"'::timestamp + '1 day'::interval "
                        + "ORDER BY time_stamp "+ordering+";";
            
            ResultSet res = stmt.executeQuery(query);
            return res;
        }
        else{
            throw new SQLException("Parameter fromTime was not defined!");
        }
    }
    
    /**
     * Method returns positions of units in all groups for defined user collected to defined timestamp parameter
     * @param user_name is String username of logged user 
     * @param toTime is timestamp of the end of range
     * @param unitId is identifier of unit
     * @param ordering is order direction for results, can be ASC or DESC only
     * @return ResultSet object with positions to defined timestamp, number is limited by range of 1 day ago!!!
     * @throws SQLException
     */
    public ResultSet getPositionsByUserNameDayBefore(String user_name, String toTime, long unitId, String ordering) throws SQLException {
        if(toTime !=null){
            String pos_table =  SQLExecutor.getUnitsPositions_table();
            /*String query = "SELECT gid, "+pos_table+".unit_id, units_to_groups.group_id, "
                    + "time_stamp, speed, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+", units_to_groups "
                    + "WHERE ("+this.getWhereStatemant(user_name, "units_to_groups.group_id")+") "
                        + "AND units_to_groups.unit_id = "+pos_table+".unit_id "
                        + "AND "+pos_table+".unit_id = "+ unitId + " "
                        + "AND time_stamp < '"+toTime+"' AND time_stamp > timestamp '"+toTime+"' - INTERVAL '1 month' "
                        + "ORDER BY time_stamp DESC;";*/
            // Simpler query for select positions
            String query = "SELECT time_stamp, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+" "
                    + "WHERE "+pos_table+".unit_id = " + unitId + " "
                        + "AND time_stamp >= '"+toTime+"'::timestamp - '1 day'::interval AND time_stamp < '"+toTime+"' "
                        + "ORDER BY time_stamp "+ordering+";";
            
            ResultSet res = stmt.executeQuery(query);
            return res;
        }
        else{
            throw new SQLException("Parameter toTime was not defined!");
        }
    }
    
    /**
     * Method returns positions of units in all groups for defined user collected during defined timeStamp parameters
     * @param user_name is String username of logged user 
     * @param fromTime is timestamp of the beginning
     * @param toTime is timestamp of the end of range
     * @param unitId is identifier of unit
     * @param ordering is order direction for results, can be ASC or DESC only
     * @return ResultSet object with positions between defined timestamps, number is limited by 50 000 rows!!!
     * @throws SQLException
     */
    public ResultSet getPositionsByUserNameDuringRange(String user_name, String fromTime, String toTime, long unitId, String ordering) throws SQLException {
        if(fromTime !=null && toTime !=null){
            String pos_table =  SQLExecutor.getUnitsPositions_table();
            /*String query = "SELECT gid, "+pos_table+".unit_id, units_to_groups.group_id, "
                    + "time_stamp, speed, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+", units_to_groups "
                    + "WHERE ("+this.getWhereStatemant(user_name, "units_to_groups.group_id")+") "
                        + "AND units_to_groups.unit_id = "+pos_table+".unit_id "
                        + "AND "+pos_table+".unit_id = " + unitId + " "
                        + "AND time_stamp < '"+toTime+"' AND time_stamp > '"+fromTime+"' "
                        + "ORDER BY time_stamp DESC LIMIT 50000;";*/
            // Simpler query for select positions
            String query = "SELECT time_stamp, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                    + "FROM "+pos_table+" "
                    + "WHERE "+pos_table+".unit_id = " + unitId + " "
                        + "AND time_stamp >= '"+fromTime+"' AND time_stamp < '"+toTime+"' "
                        + "ORDER BY time_stamp "+ordering+" LIMIT 50000;";
            
            ResultSet res = stmt.executeQuery(query);
            return res;
        }
        else{
            throw new SQLException("Parameters fromTime and toTime were not defined!");
        }
    }
    
    /**
     * Method returns positions of given unit for defined user collected during defined timestamp parameter, 
     * modified for case of identical fromTime and toTime value
     * @param user_name is String username of logged user 
     * @param fromTime is timestamp of the beginning of range
     * @param toTime is timestamp of the end of range
     * @param unitId is identifier of unit
     * @param ordering is order direction for results, can be ASC or DESC only
     * @return ResultSet object with positions between defined timestamps, number is limited by 50 000 rows!!!
     * @throws SQLException
     */
    public ResultSet getPositionsByUserNameDuringDay(String user_name, String fromTime, String toTime, long unitId, String ordering) throws SQLException {
        if(fromTime !=null && toTime !=null){
            if(fromTime.equalsIgnoreCase(toTime)){
                String pos_table =  SQLExecutor.getUnitsPositions_table();
                /*String query = "SELECT gid, "+pos_table+".unit_id, units_to_groups.group_id, "
                        + "time_stamp, speed, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                        + "FROM "+pos_table+", units_to_groups "
                        + "WHERE ("+this.getWhereStatemant(user_name, "units_to_groups.group_id")+") "
                            + "AND units_to_groups.unit_id = "+pos_table+".unit_id "
                            + "AND "+pos_table+".unit_id = " + unitId + " "
                            + "AND time_stamp >= '"+fromTime+"' AND time_stamp < '"+toTime+"'::timestamp + '1 day'::interval "
                            + "ORDER BY time_stamp DESC LIMIT 50000;";
                */
                // Simpler query for select positions
                String query = "SELECT time_stamp, st_x(the_geom), st_y(the_geom), st_srid(the_geom) "
                        + "FROM "+pos_table+" "
                        + "WHERE "+pos_table+".unit_id = " + unitId + " "
                            + "AND time_stamp >= '"+fromTime+"' AND time_stamp < '"+toTime+"'::timestamp + '1 day'::interval "
                            + "ORDER BY time_stamp "+ordering+" LIMIT 50000;";
                ResultSet res = stmt.executeQuery(query);
                return res;
            }
            else{
                throw new SQLException("Parameters was not correctly defined!");
            }
            
        }
        else{
            throw new SQLException("Parameter toTime was not defined!");
        }
    }
    
    /**
     * Method returns positions of units in all groups for defined user collected during defined timeStamp parameters
     * @param user_name user_name is String username of logged user 
     * @param fromTime is timestamp of the beginning
     * @param toTime is timestamp of the end of range
     * @param unitId is identifier of unit
     * @param ordering is order direction for results, can be ASC or DESC only
     * @return ResultSet object with positions between defined timestamps, number is limited by 50 000 rows or to 1 day of collecting!!!
     * @throws SQLException
     */
    public ResultSet getPositionsTimeRangeByUserName(String user_name, String fromTime, String toTime, Long unitId, String ordering) throws SQLException{
        ResultSet res = null;
        if(fromTime != null && toTime == null && unitId != null){
            if(!fromTime.isEmpty()){
                res = getPositionsByUserNameInDay(user_name, fromTime, unitId, ordering);
            }
            else{
                throw new SQLException("Wrong content of parameter from!");
            }
        }
        else if(fromTime == null && toTime != null && unitId != null){
            if(!toTime.isEmpty()){
                res = getPositionsByUserNameDayBefore(user_name, toTime, unitId, ordering);
            }
            else{
                throw new SQLException("Wrong content of parameter to!");
            }
        }
        else if(fromTime != null && toTime != null && unitId != null){
            if(!fromTime.isEmpty() && !toTime.isEmpty()){
                if(fromTime.equalsIgnoreCase(toTime)){
                    res = getPositionsByUserNameDuringDay(user_name, fromTime, toTime, unitId, ordering);
                }
                else{
                    res = getPositionsByUserNameDuringRange(user_name, fromTime, toTime, unitId, ordering);
                }
            }
            else{
                throw new SQLException("Wrong content of parameters from and to!");
            }
        }
        else{
            throw new SQLException("Wrong combination of parameters from and to!");
        }
        return res;
    }
}