package cz.hsrs.servlet.provider;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpHeaders;

import cz.hsrs.db.DBJsonUtils;
import cz.hsrs.db.util.UtilFactory;
import cz.hsrs.servlet.feeder.ServiceParameters;
import cz.hsrs.servlet.security.LoginUser;

/**
 * Servlet implementation class GroupService
 */
public class GroupService extends DBServlet{
    private static final long serialVersionUID = 1L;
    
    public static final String GET_SUPER_GROUPS = "GetSuperGroups";
    public static final String GET_SUB_GROUPS = "GetSubGroups";
    public static final String GET_GROUPS = "GetGroups";

    private UtilFactory db;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GroupService() {
        super();
        
        // TODO Auto-generated constructor stub
    }

    public void init() throws ServletException {
        super.init();
        
        try {
            db = new UtilFactory();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        RequestParameters params = new RequestParameters(request);
        
        String user="";
        /** standard authentication */
        LoginUser loggedUser = null; 
        try {
            loggedUser = getAuthenticatedLoginUser(request);
            String userName = loggedUser.getUserName();
            params.setUSER(userName);
        } catch (AuthenticationException e1) {
            throw new ServletException("Authentication failure for request "+ request.getQueryString());
        }
        /*
        try {
            user = getAuthenticatedUser(request);
        } catch (AuthenticationException e) {
            throw new ServletException(e);
        }
        */
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        
        PrintWriter out = response.getWriter();
        try {
            params.setUSER(user);
            if (request.getParameter(ServiceParameters.OPERATION).equals(GET_GROUPS)) {
                DBJsonUtils.writeJSON(out, db.groupUtil.getGroups(params.getUSER()));
            }
            else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_SUPER_GROUPS)) {
                DBJsonUtils.writeJSON(out, db.groupUtil.getSuperGroups(params.getUSER()));
            }
            else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_SUB_GROUPS)) {
                DBJsonUtils.writeJSON(out, db.groupUtil.getSubGroups(params.getGroup_id()));
            } 
            else {
                throw new NullPointerException("No operation specified.");
            }
        } catch (SQLException e) {
            solveGetException(e, out);
        }
    }
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * Class parses parameters from incoming request
     * @author mkepka
     *
     */
    static class RequestParameters {
        private String USER;
        private int group_id;

        RequestParameters(HttpServletRequest request) throws NullPointerException{
            Object id = request.getParameter("parent_group");
            if (id!= null) {
                group_id = new Integer(id.toString());
            }
        }

        public String getUSER() {
            return USER;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public void setUSER(String user) {
            USER = user;
        }
    }
}