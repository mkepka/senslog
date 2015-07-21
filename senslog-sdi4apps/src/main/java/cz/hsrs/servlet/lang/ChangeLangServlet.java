/**
 * 
 */
package cz.hsrs.servlet.lang;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.hsrs.servlet.provider.DBServlet;
import cz.hsrs.servlet.security.JSPHelper;
import cz.hsrs.servlet.security.LoginUser;

/**
 * Servlet to change languages on pages
 * @author MiKe
 *
 */
public class ChangeLangServlet extends DBServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//super.doGet(req, resp);
		String lang = req.getParameter("lang");
		String coming =  req.getParameter("coming");
		//PrintWriter pw = resp.getWriter();
		
		Cookie[] cookies = req.getCookies();
		for(int i = 0; i<cookies.length;i++){
			if(cookies[i].getName().equalsIgnoreCase("language")==true){
				cookies[i].setValue(lang);										
				cookies[i].setPath("/");
				resp.addCookie(cookies[i]);
			}
		}	
	
		LoginUser user = (LoginUser) req.getSession().getAttribute(JSPHelper.USERATTRIBUTE);
		user.setUserLanguage(lang);
		JSPHelper.redirect(resp, req.getContextPath() + coming);
	}	
}
