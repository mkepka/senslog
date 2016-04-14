package cz.hsrs.servlet.security;

import java.sql.SQLException;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.UserUtil;

public class LoginUser {
    final HttpServletRequest req;
    private String userName;
    private UserUtil util;
    private String userLang;
    private boolean audio;
    
    //List operations = new ArrayList();

    /*
     * private final String IP; private final String role; private final String
     * password;
     */

    public LoginUser(HttpServletRequest request) {
        req = request;
        util = new UserUtil();
    }

    /**
     * Method authenticates user if given user name and password are correct
     * @param userName of user trying to authenticate
     * @param password of user trying to authenticate
     * @return true if user was successfully authenticate, false elsewhere
     */
    public boolean athenticate(String userName, String password) {
        String right_pass = getPassword(userName);
        if(right_pass != null){
            if (right_pass.equals(password)) {
                try {
                    this.userName = userName;
                    this.userLang = getLanguage();
                    this.audio = hasAudio();
                    util.setUserSession(userName, req.getSession().getId(), req.getRemoteHost());
                    return true;
                } catch (SQLException e) {
                    //** session is already in the database - so lets do the same.
                    SQLExecutor.logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
                    return true;
                }
            } else{
                /* password is not correct */
                return false;
            }
        } else {
            /* user wasn't found */
            return false;
        }
    }

    public boolean isAudio() {
        return audio;
    }

    public boolean logOut(HttpServletRequest request) {
        try{
            request.getSession().invalidate();
            return true;
            
        } catch (Exception e) {
            return false;
        } 
    }

    public boolean isAuthenticated() {
        return (userName != null);
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLanguage() {        
        return userLang;
    }
    
    public void setUserLanguage(String newLang){
        if(setLanguage(newLang)==true){
            this.userLang = newLang;
        }
    }
    
    private boolean setLanguage(String newLang){
        UserUtil uUtil = new UserUtil();        
        try {
            uUtil.setUserLanguage(userName, newLang);
            return true;
        } catch (SQLException e) {
            SQLExecutor.logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
            return false;    
        }
    }

    private String getLanguage() throws SQLException{
        UserUtil util = new UserUtil();
        try {
            return util.getUserLanguage(userName);
        } catch (NoItemFoundException e) {
            throw new SQLException(e);
        }
    }
    
    private boolean hasAudio() throws SQLException{
        UserUtil util = new UserUtil();
        try {
            return util.getAudio(userName);
        } catch (NoItemFoundException e) {
            throw new SQLException(e);        
        } 
    }
    
    protected String getPassword(String un) {
        try {
            //UserUtil util = new UserUtil();
            return util.getUserPassword(un);
        } catch (Exception e) {
             SQLExecutor.logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
        }
        return null;
    }
}