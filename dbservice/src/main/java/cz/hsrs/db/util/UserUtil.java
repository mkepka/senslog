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
			throw new NoItemFoundException("getUserLanguage " + user_name
					+ " not found.");
	}
	public int setUserLanguage(String user_name, String newLang) throws SQLException{
		String query = "UPDATE system_users SET lang = '"+newLang+"' WHERE user_name = '"+user_name+"';";		
		return stmt.executeUpdate(query);
	}
	
	public boolean getAudio(String user_name) throws SQLException, NoItemFoundException {
		String select = "SELECT audio FROM system_users WHERE user_name = '"+user_name+"'";
		ResultSet res = stmt.executeQuery(select);
		if (res.next()) {
			return res.getBoolean(1);
		} else
			throw new NoItemFoundException("getAudio " + user_name
					+ " not found.");
	}
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
	
	

	public List<String> getUserOperation(String role){
		List oper = new ArrayList();
		String query = "SELECT * from ";
		
		return oper;
	}
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
		String queryObservations = "select gid, st_x(the_geom), st_y(the_geom), speed, "
				+ last_pos_table
				+ ".unit_id, time_stamp, units_to_groups.group_id "
				+ "from "
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
			String queryObservations = "SELECT gid, st_x(the_geom), st_y(the_geom), speed, "
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

		List<UnitPosition> up = new ArrayList<UnitPosition>();
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
		String queryObservations = "select gid, x(the_geom), y(the_geom), speed, "
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
		return stmt.executeUpdate(insert);
	}

	public int deleteUser(String user_name) throws SQLException,
			NoItemFoundException {
		String del = "DELETE FROM system_users WHERE user_name='" + user_name
				+ "';";
		return stmt.executeUpdate(del);
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
		return stmt.executeUpdate(insert);
	}

	public int delUserSession(String session_id) throws SQLException {

		String insert = "DELETE FROM sessions WHERE session_id ='" + session_id
				+ "'";
		return stmt.executeUpdate(insert);
	}
	
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
}
