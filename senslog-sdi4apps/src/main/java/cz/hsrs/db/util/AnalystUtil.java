package cz.hsrs.db.util;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import cz.hsrs.db.model.Unit;

/**
 * Class for running analysis implemented in the database
 * @author mkepka
 *
 */
public class AnalystUtil extends DBUtil {

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    private UserUtil userUtil;
    
    public AnalystUtil(){
        userUtil = new UserUtil();
    }
    
    /**
     * Method confirms that given unit belongs to given user
     * by selecting affiliated units from database
     * @param userName name of the user
     * @param unitId id of unit
     * @return true if given unit is in any group that user is connected to 
     * @throws SQLException
     */
    public boolean checkUnitAffiliation2User(String userName, long unitId) throws SQLException{
        boolean confirmed = false;
        List<Unit> unitsOfUser = userUtil.getUnitsByUser(userName);
        Iterator<Unit> unitsIter = unitsOfUser.iterator();
        while (unitsIter.hasNext() && confirmed == false){
            if (unitsIter.next().getUnitId() == unitId){
                confirmed = true;
            }
        }
        return confirmed;
    }
}