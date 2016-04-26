/**
 * 
 */
package cz.hsrs.servlet.provider;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpHeaders;

import cz.hsrs.db.DBJsonUtils;
import cz.hsrs.db.model.AlertEvent;
import cz.hsrs.db.util.UtilFactory;
import cz.hsrs.servlet.feeder.ServiceParameters;
import cz.hsrs.servlet.security.LoginUser;

/**
 * Servlet handling request for alerts
 * @author mkepka
 *
 */
public class AlertService extends DBServlet{
    private static final long serialVersionUID = 1L;
    
    public static final String GET_ALERTS = "GetAlerts";
    public static final String GET_ALERT_EVENTS_BY_TIME = "GetAlertEventsByTime";
    
    private UtilFactory db;
    
    public void init() throws ServletException {
        super.init();
        try {
            db = new UtilFactory();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        RequestParameters params = new RequestParameters(request);
        String user="";
        /** standard authentication */
        LoginUser loggedUser = null; 
        try {
            loggedUser = getAuthenticatedLoginUser(request);
            user = loggedUser.getUserName();
            //params.setUSER(userName);
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
            if (request.getParameter(ServiceParameters.OPERATION).equals(GET_ALERTS)) {
                DBJsonUtils.writeJSON(out, db.alertUtil.getAlerts(params.getUnit_id()));
            }
            else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_ALERT_EVENTS_BY_TIME)) {
                List<AlertEvent> events = db.alertUtil.getAlertEventsByTime(params.getUnit_id(), params.from, params.to);
                DBJsonUtils.writeJSON(out, events);
            } else {
                throw new ServletException("Wrong request "+request.getQueryString());
            }
        } catch (SQLException e) {
            solveGetException(e, out);
        }
    }
    
    static class RequestParameters {
        private long unit_id;
        private String from;
        private String to;

        RequestParameters(HttpServletRequest request) throws NullPointerException {
            Object uid = request.getParameter("unit_id");
            if (uid != null) {
                unit_id = new Long(uid.toString());
            }
            from = request.getParameter("from");
            to = request.getParameter("to");
        }

        public long getUnit_id() {
            return unit_id;
        }
    }
}