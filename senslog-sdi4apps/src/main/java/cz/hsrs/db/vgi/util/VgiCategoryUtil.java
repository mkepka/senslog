package cz.hsrs.db.vgi.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import cz.hsrs.db.model.vgi.VgiCategory;
import cz.hsrs.db.pool.SQLExecutor;

/**
 * Class concentrates method for processing VGICategory objects
 * @author mkepka
 *
 */
public class VgiCategoryUtil {

    private static final String SCHEMA_NAME = "vgi";
    private static final String CATEGORY_TABLE_NAME = "observations_vgi_category";
    
    /**
     * Method gets VGICategory object by given ID
     * @param categoryId - ID of VgiCategory object
     * @return VGICategory object
     * @throws SQLException
     */
    public VgiCategory getVgiCategory(int categoryId) throws SQLException{
        try{
            String query = "SELECT category_id, category_name, description, parent_id, category_level, lft, rgt"
                    + " FROM "+SCHEMA_NAME+"."+CATEGORY_TABLE_NAME+""
                    + " WHERE category_id = "+categoryId+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            if(res.next()){
                VgiCategory cat = new VgiCategory(
                        res.getInt("category_id"),
                        res.getString("category_name"),
                        res.getString("description"),
                        Integer.valueOf(res.getString("parent_id")),
                        Integer.valueOf(res.getString("category_level")),
                        Integer.valueOf(res.getString("lft")),
                        Integer.valueOf(res.getString("rgt")));
                return cat;
            }
            else{
                throw new SQLException("VgiCategory with given ID does not exist!");
            }
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method gets List of VGICategory objects that 
     * are descendants of VGICategory object by given ID
     * @param categoryId - ID of VgiCategory object
     * @return List of VGICategory objects
     * @throws SQLException
     */
    public List<VgiCategory> getVgiCategoryDescendants(int categoryId) throws SQLException{
        try{
            String queryCat = "SELECT category_id, category_name, description, parent_id, category_level, lft, rgt"
                    + " FROM "+SCHEMA_NAME+"."+CATEGORY_TABLE_NAME+""
                    + " WHERE category_id = "+categoryId+";";
            ResultSet res = SQLExecutor.getInstance().executeQuery(queryCat);
            if(res.next()){
                VgiCategory catParent = new VgiCategory(
                        res.getInt("category_id"),
                        res.getString("category_name"),
                        res.getString("description"),
                        Integer.valueOf(res.getString("parent_id")),
                        Integer.valueOf(res.getString("category_level")),
                        Integer.valueOf(res.getString("lft")),
                        Integer.valueOf(res.getString("rgt")));
                
                LinkedList<VgiCategory> catList = new LinkedList<VgiCategory>();
                String queryDesc;
                if(catParent.internalGetLft() != null){
                    queryDesc = "SELECT category_id, category_name, description, parent_id, category_level, lft, rgt"
                            + " FROM "+SCHEMA_NAME+"."+CATEGORY_TABLE_NAME+""
                            + " WHERE lft BETWEEN "+catParent.internalGetLft()+""
                            + " AND "+catParent.internalGetRgt()+""
                            + " ORDER BY category_id;";
                }
                else{
                    queryDesc = "SELECT category_id, category_name, description, parent_id, category_level, lft, rgt"
                            + " FROM "+SCHEMA_NAME+"."+CATEGORY_TABLE_NAME+""
                            + " WHERE parent_id = "+catParent.getCategoryId()+""
                            + " ORDER BY category_id;";
                }
                ResultSet resDesc = SQLExecutor.getInstance().executeQuery(queryDesc);
                while(resDesc.next()){
                    VgiCategory cat = new VgiCategory(
                            resDesc.getInt("category_id"),
                            resDesc.getString("category_name"),
                            resDesc.getString("description"),
                            Integer.valueOf(resDesc.getString("parent_id")),
                            Integer.valueOf(resDesc.getString("category_level")),
                            Integer.valueOf(resDesc.getString("lft")),
                            Integer.valueOf(resDesc.getString("rgt")));
                    catList.add(cat);
                }
                return catList;
            }
            else{
                throw new SQLException("VgiCategory with given ID does not exist!");
            }
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method gets list of all VGICategory objects
     * @return LinkedList of VGICategory objects
     * @throws SQLException
     */
    public List<VgiCategory> getCategoriesList() throws SQLException{
        try{
            String query = "SELECT category_id, category_name, description, parent_id, category_level, lft, rgt"
                    + " FROM "+SCHEMA_NAME+"."+CATEGORY_TABLE_NAME+" ORDER BY category_id;";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<VgiCategory> catList = new LinkedList<VgiCategory>();
            while(res.next()){
                VgiCategory cat = new VgiCategory(
                        res.getInt("category_id"),
                        res.getString("category_name"),
                        res.getString("description"),
                        (res.getString("parent_id")!= null ? Integer.parseInt(res.getString("parent_id")) : null),
                        (res.getString("category_level") != null ? Integer.valueOf(res.getString("category_level")) : null),
                        (res.getString("lft") != null ? Integer.valueOf(res.getString("lft")) : null),
                        (res.getString("rgt") != null ? Integer.valueOf(res.getString("rgt")) : null));
                catList.add(cat);
            }
            return catList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
}