package cz.hsrs.servlet.security;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
	
	List operations = new ArrayList();

	/*
	 * private final String IP; private final String role; private final String
	 * password;
	 */

	public LoginUser(HttpServletRequest request) {
		req = request;
		util = new UserUtil();
		
	}

	public boolean athenticate(String userName, String password) {
		String right_pass = getPassword(userName);
		if (password.equals(right_pass)) {

			try {
				this.userName = userName;
				this.userLang = getLanguage();
				this.audio = hasAudio();
				util.setUserSession(userName, req.getSession().getId(), req
						.getRemoteHost());
				
				return true;
			} catch (SQLException e) {
				//** session is already in the databese - so lets do the same.
				// TODO Auto-generated catch block
				 SQLExecutor.logger.log(Level.SEVERE, e.getMessage(), e
						.getStackTrace());
				
				return true;
			}

		} else {
			return false;
		}

		// this.password = password;
	}

	public boolean isAudio() {
		return audio;
	}

	public boolean logOut(HttpServletRequest request) {

	 try{
			request.getSession().invalidate();			
			return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			SQLExecutor.logger.log(Level.SEVERE, e.getMessage(), e.getStackTrace());
			return false;	
		}			 
	}


	private String getLanguage() throws SQLException{
		UserUtil util = new UserUtil();
		try {
			return util.getUserLanguage(userName);
		} catch (NoItemFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";		
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
			UserUtil util = new UserUtil();
			return util.getUserPassword(un);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 SQLExecutor.logger.log(Level.SEVERE, e.getMessage(), e
					.getStackTrace());
		}
		return null;

	}

}
