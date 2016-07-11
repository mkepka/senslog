package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cz.hsrs.db.model.Group;

public class GroupUtil extends ObservationUtil {

    public GroupUtil() {
    }
    
    @SuppressWarnings("unchecked")
    public List<Group> getSuperGroups(String user_name)
            throws SQLException {
        String queryObservations = "SELECT groups.id, groups.group_name, groups.parent_group_id, groups.has_children"
                + " FROM groups, system_users"
                + " WHERE system_users.group_id = groups.id"
                + " AND system_users.user_name = '" + user_name + "';";

        ResultSet res = stmt.executeQuery(queryObservations);
        return (List<Group>)generateObjectList(new Group(), res);
        /** Do one object */
    }
    
    @SuppressWarnings("unchecked")
    public List<Group> getSubGroups(int parent_id)
            throws SQLException {
        String queryObservations = "SELECT groups.id, groups.group_name, groups.parent_group_id, groups.has_children"
                + " FROM groups"
                + " WHERE groups.parent_group_id = " + parent_id + ";";

        ResultSet res = stmt.executeQuery(queryObservations);
        return (List<Group>)generateObjectList(new Group(), res);
        /** Do one object */
    }

    /**
     * 
     * @param user_name
     * @param writer
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public  List<Group> getGroups(String user_name)throws SQLException {
        String queryObservations = "SELECT groups.id, groups.group_name, groups.parent_group_id, groups.has_children"
                + " FROM groups"
                + " WHERE "
                + this.getWhereStatemant(user_name, "groups.id") + ";";

        ResultSet res = stmt.executeQuery(queryObservations);
        return (List<Group>)generateObjectList(new Group(), res);
    }

    /**
     * Return all ids that are owned by user name recursively.
     * 
     * @param user_name
     * @return
     * @throws Exception
     */
    public List<Integer> getGroupIds(String user_name) throws SQLException {
        String queryGroups = "SELECT group_id FROM system_users "
                + "WHERE system_users.user_name = '" + user_name + "';";

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
                queryGroups = "SELECT groups.id FROM groups WHERE "
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
    protected String getWhereStatemant(String user_name, String id_column)throws SQLException {
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
    
    /**
     * Method gets id of group by given name
     * @param groupName - name of group
     * @return id of group represents group.id
     * @throws SQLException
     */
    public int getGroupIdByName(String groupName) throws SQLException{
        String query = "SELECT id FROM groups WHERE group_name='"+groupName+"';";
        ResultSet res = stmt.executeQuery(query);
        if(res.next()){
            return res.getInt(1);
        }
        else{
            throw new SQLException("Any group of given name doesn't exist!");
        }
    }
    
    /**
     * Method confirms that given group belongs to given user
     * by selecting affiliated groups from database
     * @param userName - name of user that is logged
     * @param groupName - name of group
     * @return true if given user is connected to given group, false otherwise
     * @throws SQLException
     */
    public boolean checkGroupAffiliation2User(String userName, String groupName) throws SQLException{
        boolean confirmed = false;
        int groupId = getGroupIdByName(groupName);
        List<Integer> groupsOfUser = getGroupIds(userName);
        Iterator<Integer> groupsIter = groupsOfUser.iterator();
        while (groupsIter.hasNext() && confirmed == false){
            if (groupsIter.next() == groupId){
                confirmed = true;
            }
        }
        return confirmed;
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