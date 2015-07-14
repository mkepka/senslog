package cz.hsrs.db.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.model.Group;

public class GroupUtil extends ObservationUtil {

	public GroupUtil() {		
	}

	public List<Group> getSuperGroups(String user_name)
			throws SQLException {
		String queryObservations = "select groups.id, groups.group_name, groups.parent_group_id, groups.has_children"
				+ " from groups, system_users "
				+ "WHERE system_users.group_id = groups.id "
				+ "AND system_users.user_name = '" + user_name + "';";

		ResultSet res = stmt.executeQuery(queryObservations);

		return (List<Group>)generateObjectList(new Group(), res);
		// writeJSON(writer, new Group(), res);
		/** Do one object */

	}

	public List<Group> getSubGroups(int parent_id)
			throws SQLException {
		String queryObservations = "select groups.id, groups.group_name, groups.parent_group_id, groups.has_children"
				+ " from groups "
				+ "WHERE groups.parent_group_id = "
				+ parent_id + ";";

		ResultSet res = stmt.executeQuery(queryObservations);
		return (List<Group>)generateObjectList(new Group(), res);
		// writeJSON(writer, new Group(), res);
		/** Do one object */

	}

	/**
	 * 
	 * @param user_name
	 * @param writer
	 * @throws Exception
	 */
	public  List<Group> getGroups(String user_name)
			throws SQLException {

		String queryObservations = "select groups.id, groups.group_name, groups.parent_group_id, groups.has_children"
				+ " from groups "
				+ "WHERE "
				+ this.getWhereStatemant(user_name, "groups.id") + ";";

		ResultSet res = stmt.executeQuery(queryObservations);
		return (List<Group>)generateObjectList(new Group(), res);
		// writeJSON(writer, new Group(), res);

	}

	/**
	 * Return all ids that are owned by user name recursively.
	 * 
	 * @param user_name
	 * @return
	 * @throws Exception
	 */
	public List<Integer> getGroupIds(String user_name) throws SQLException {
		String queryGroups = "select group_id from system_users where "
				+ "system_users.user_name = '" + user_name + "';";

		ResultSet res = stmt.executeQuery(queryGroups);
		res.next();
		int id = res.getInt("group_id");
		int this_id;

		List<Integer> oldIds = new ArrayList<Integer>();
		List<Integer> newIds = new ArrayList<Integer>();
		List<Integer> finalIds = new ArrayList<Integer>();
		oldIds.add(id);

		while (oldIds.size() != 0) {

			for (Iterator<Integer> i = oldIds.iterator(); i.hasNext();) {
				this_id = i.next();
				finalIds.add(this_id);
				queryGroups = "select groups.id from groups where "
						+ "groups.parent_group_id = " + this_id + ";";

				res = stmt.executeQuery(queryGroups);
				while (res.next()) {
					newIds.add(res.getInt("id"));
				}
			}
			oldIds.clear();
			oldIds.addAll(newIds);
			newIds.clear();
		}
		return finalIds;

	}

	/**
	 * Fills Where statemant
	 * 
	 * @param user_name
	 * @param id_column
	 * @return
	 * @throws SQLException
	 */
	protected String getWhereStatemant(String user_name, String id_column)
			throws SQLException {
		List<Integer> ids = getGroupIds(user_name);
		String subquery = "";
		for (Iterator<Integer> i = ids.iterator(); i.hasNext();) {
			subquery = subquery + id_column + " = " + i.next();
			if (i.hasNext()) {
				subquery = subquery + " OR ";
			}
		}
		return subquery;

	}

	/*protected List<? extends DBObject> generateObjectList(DBObject element, ResultSet res) {
			
		
		try {
			List<DBObject> result = new ArrayList<DBObject>();
			while (res.next()) {
				DBObject dbob = (element.getClass().newInstance()).getDBObject(res);		
				result.add(dbob);
			}
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}*/

}
