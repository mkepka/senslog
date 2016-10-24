/**
 * 
 */
package cz.hsrs.rest.util;

import java.sql.SQLException;
import java.util.List;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.VgiCategory;
import cz.hsrs.db.util.UserUtil;
import cz.hsrs.db.vgi.util.VgiCategoryUtil;

/**
 * Utility class for VgiCategoryRest services
 * @author mkepka
 *
 */
public class VgiCategoryRestUtil {

    private UserUtil userUt;
    private VgiCategoryUtil cUtil;

    /**
     * Empty constructor 
     */
    public VgiCategoryRestUtil(){
        this.userUt = new UserUtil();
        this.cUtil = new VgiCategoryUtil();
    }
    
    /**
     * Method processes GetVgiCategory by given categoryId
     * @param categoryId - ID of category
     * @param userName - name of user
     * @return VgiCategory object if there is one in the DB
     * @throws SQLException
     */
    public VgiCategory processGetVgiCategory(int categoryId, String userName) throws SQLException{
        try{
            userUt.getUserId(userName);
            VgiCategory category = cUtil.getVgiCategory(categoryId);
            return category;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes GetVgiCategory by given categoryId
     * @param categoryId - ID of category
     * @param userName - name of user
     * @return VgiCategory object if there is one in the DB
     * @throws SQLException
     */
    public List<VgiCategory> processGetVgiCategoryDescendants(int categoryId, String userName) throws SQLException{
        try{
            userUt.getUserId(userName);
            List<VgiCategory> categoryList = cUtil.getVgiCategoryDescendants(categoryId);
            return categoryList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
    
    /**
     * Method processes SelectVgiCategories by given user
     * @return LinkedList of VgiCategory objects associated to given user
     * @throws SQLException
     */
    public List<VgiCategory> processGetVgiCategories(String userName) throws SQLException{
        try{
            userUt.getUserId(userName);
            List<VgiCategory> catList = cUtil.getCategoriesList();
            return catList;
        } catch(NoItemFoundException e){
            throw new SQLException(e.getMessage());
        }
    }
}