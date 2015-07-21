package cz.hsrs.servlet.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class JSPHelper {

	public static final String USERATTRIBUTE="user";
	public static final String LANGATTRIBUTE="lang";
	public static final String FEEDBACKATTRIBUTE="feedback";
	
	public static synchronized String getFeedback(HttpSession ses){
		if (ses.getAttribute(FEEDBACKATTRIBUTE)!=null) {
			return ses.getAttribute(FEEDBACKATTRIBUTE).toString();
		}
		else return "";
	}
	
	public static synchronized String getUserName(HttpSession ses){
		if (ses.getAttribute(USERATTRIBUTE)!=null) {
			return ((LoginUser)ses.getAttribute(USERATTRIBUTE)).getUserName();
		}
		else return "";
	}
	
	public static synchronized void  checkAuthentication(HttpServletRequest req, HttpServletResponse resp){
		if (req.getSession().getAttribute(USERATTRIBUTE)!= null){
			((LoginUser)req.getSession().getAttribute(USERATTRIBUTE)).isAuthenticated();
		} else {
			redirect(resp, req.getContextPath()+"/signin.jsp?coming="+req.getServletPath());			
		}
		
	}
	
	public static synchronized void  trySkipAuthentication(HttpServletRequest req, HttpServletResponse resp){
		if (req.getSession().getAttribute(USERATTRIBUTE)!= null){
			if (((LoginUser)req.getSession().getAttribute(USERATTRIBUTE)).isAuthenticated()){
				redirect(resp, req.getContextPath()+"/crossroad.jsp");		
			}
		} 
		
	}
	
	public static synchronized void redirectToAdministration(HttpServletRequest req, HttpServletResponse resp){	
		  
		  /*  System.out.println(req.getLocalAddr());
		    System.out.println(req.getRemoteAddr());
		    System.out.println(req.getRemoteHost());*
		    System.out.println(req.getServerName());*/
		    
			redirect(resp, "../maplog/admin/");
			
	}

	public static synchronized void redirectToKJ(HttpServletRequest req, HttpServletResponse resp){	
		  
		  /*  System.out.println(req.getLocalAddr());
		    System.out.println(req.getRemoteAddr());
		    System.out.println(req.getRemoteHost());*
		    System.out.println(req.getServerName());*/
		    
			redirect(resp, "../maplog/kniha_jizd");
			
	}
	
	public static synchronized void redirect(HttpServletResponse aResponse, String page) {

		
		String urlWithSessionID = aResponse.encodeURL(page);
		try {		
			aResponse.sendRedirect(page);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
