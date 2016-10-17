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

    /**
     * Method gets list of VGI Category objects
     * @return LinkedList of VGICategory objects
     * @throws SQLException
     */
    public List<VgiCategory> getCategoriesList() throws SQLException{
        try{
            String query = "SELECT category_id, category_name, description, parent_id, category_level, lft, rgt"
                    + " FROM vgi.observations_vgi_category ORDER BY category_id;";
            ResultSet res = SQLExecutor.getInstance().executeQuery(query);
            LinkedList<VgiCategory> catList = new LinkedList<VgiCategory>();
            while(res.next()){
                VgiCategory cat = new VgiCategory(
                        res.getInt("category_id"),
                        res.getString("category_name"),
                        res.getString("description"),
                        res.getInt("parent_id"),
                        res.getInt("category_level"),
                        res.getInt("lft"), res.getInt("rgt"));
                catList.add(cat);
            }
            return catList;
        } catch(SQLException e){
            throw new SQLException(e.getMessage());
        }
    }
}