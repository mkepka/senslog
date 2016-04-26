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

public class SensorService extends DBServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String GET_SENSORS = "GetSensors";
    public static final String GET_OBSERVATIONS = "GetObservations";

    private UtilFactory db;

    public void init() throws ServletException {
        super.init();
        
        try {
            db = new UtilFactory();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        RequestParameters params = new RequestParameters(request);
        
        /** standard authentication */
        LoginUser loggedUser = null; 
        try {
            loggedUser = getAuthenticatedLoginUser(request);
            //userName = loggedUser.getUserName();
            //params.setUSER(userName);
        } catch (AuthenticationException e1) {
            throw new ServletException("Authentication failure for request "+ request.getQueryString());
        }
        
        /**
         * 
         */
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        
        PrintWriter out = response.getWriter();
        try {
            if (request.getParameter(ServiceParameters.OPERATION).equals(GET_SENSORS)) {
                DBJsonUtils.writeJSON(out, db.sensorUtil.getUnitsSensors(params.getUnit_id()));
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_OBSERVATIONS)) {
                /**
                 * Pokud je dan trunc
                 */
                if (params.trunc != null) {
                    DBJsonUtils.writeJSON(out, db.sensorUtil.getSensorObservationsTrunc(
                                    params.getUnit_id(),
                                    params.getSensor_id(), 
                                    params.from,
                                    params.to, 
                                    params.trunc));
                } else {
                    DBJsonUtils.writeJSON(out, db.sensorUtil.getSensorObservations(
                                    params.getUnit_id(), 
                                    params.getSensor_id(), 
                                    params.from, 
                                    params.to));
                }
            } else {
                throw new ServletException("Wrong request "+ request.getQueryString());
            }
        } catch (SQLException e) {
            super.solveGetException(e, out);
        }
    }

    static class RequestParameters {

        private long unit_id;
        private long sensor_id;
        private String from;
        private String to;
        private String trunc;

        RequestParameters(HttpServletRequest request)throws NullPointerException {
            Object uid = request.getParameter("unit_id");
            if (uid != null) {
                unit_id = new Long(uid.toString());
            }
            Object sid = request.getParameter("sensor_id");
            if (sid != null) {
                sensor_id = new Long(sid.toString());
            }

            Object tr = request.getParameter("trunc");
            if (tr != null) {
                trunc = tr.toString();
            }

            Object fro =  request.getParameter("from");
            if (fro!= null){
                from = fro.toString();
            } else {
                from = "1900-01-01 00:00:00+01";
            }
            
            Object too =  request.getParameter("to");
            if (too!= null){
                to = too.toString();
            } else {
                to = "3000-01-01 00:00:00+01";
            }
        }

        public long getUnit_id() {
            return unit_id;
        }

        public long getSensor_id() {
            return sensor_id;
        }

        public String getTrunc() {
            return trunc;
        }
    }
}