package cz.hsrs.servlet.provider;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.HttpHeaders;

import cz.hsrs.db.DBJsonUtils;
import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.util.UtilFactory;
import cz.hsrs.servlet.feeder.ServiceParameters;

public class SensorService extends DBServlet {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
     *      
     * SensorService?Operation=GetSensors&unit_id=
     *  
     * SensorService?Operation=GetObservations&unit_id=&sensor_id=&from=&to=
     * SensorService?Operation=GetObservations&unit_id=&sensor_id=&from=&to=&trunc=
     * 
     * SensorService?Operation=GetLastObservations&group=
     * SensorService?Operation=GetLastObservations&unit_id=
     * SensorService?Operation=GetLastObservations&unit_id=&sensor_id=
     * SensorService?Operation=GetLastObservations&group=&sensor_id=
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        RequestParameters params = new RequestParameters(request);
        
        /** standard authentication */
        /*
        LoginUser loggedUser = null; 
        try {
            loggedUser = getAuthenticatedLoginUser(request);
            userName = loggedUser.getUserName();
            params.setUSER(userName);
        } catch (AuthenticationException e1) {
            throw new ServletException("Authentication failure for request "+ request.getQueryString());
        }*/
        
        /** For SDI4Apps purpose only temporary */
        String user = params.getUSER();
        if(user == null){
            throw new ServletException("Authentication failure, no user specified for request: "+ request.getQueryString());
        }
        else{
            try {
                String testLang = db.userUtil.getUserLanguage(user);
                if(testLang.isEmpty()){
                    throw new ServletException("Authentication failure for request "+ request.getQueryString());
                }
            } catch (SQLException e1) {
                throw new ServletException("Authentication failure for request "+ request.getQueryString());
            } catch (NoItemFoundException e1) {
                throw new ServletException("Authentication failure for request "+ request.getQueryString());
            }
        }
        
        /**
         * 
         */
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        
        PrintWriter out = response.getWriter();
        try {
            if (request.getParameter(ServiceParameters.OPERATION).equals(ServiceParameters.GET_SENSORS)) {
                boolean isAffiliated = db.analystUtil.checkUnitAffiliation2User(params.getUSER(), params.getUnit_id());
                if(isAffiliated){
                    DBJsonUtils.writeJSON(out, db.sensorUtil.getUnitsSensors(params.getUnit_id()));
                }
                else{
                    throw new SQLException("Specified unit does not exist in any group of given user!");
                }
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(ServiceParameters.GET_OBSERVATIONS)) {
                boolean isAffiliated = db.analystUtil.checkUnitAffiliation2User(params.getUSER(), params.getUnit_id());
                if(isAffiliated){
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
                }
                else{
                    throw new SQLException("Specified unit does not exist in any group of given user!");
                }
            } else if(request.getParameter(ServiceParameters.OPERATION).equals(ServiceParameters.GET_LAST_OBSERVATIONS)){
                if(params.getGroup() == null && params.getUnit_id() != 0 && params.getSensor_id() != 0){
                    boolean isAffiliated = db.analystUtil.checkUnitAffiliation2User(params.getUSER(), params.getUnit_id());
                    if(isAffiliated){
                        DBJsonUtils.writeJSON(out, db.sensorUtil.getSensorLastObservation(params.getUnit_id(), params.getSensor_id()));
                    }
                    else{
                        throw new SQLException("Specified unit does not exist in any group of given user!");
                    }
                } else if(params.getGroup() == null && params.getUnit_id() != 0 && params.getSensor_id() == 0){
                    boolean isAffiliated = db.analystUtil.checkUnitAffiliation2User(params.getUSER(), params.getUnit_id());
                    if(isAffiliated){
                        DBJsonUtils.writeJSON(out, db.sensorUtil.getUnitSensorsLastObservations(params.getUnit_id()));
                    }
                    else{
                        throw new SQLException("Specified unit does not exist in any group of given user!");
                    }
                } else if(params.getGroup() != null && params.getUnit_id() == 0 && params.getSensor_id() == 0){
                    boolean isAffiliated = db.groupUtil.checkGroupAffiliation2User(params.getUSER(), params.getGroup());
                    if(isAffiliated){
                        DBJsonUtils.writeJSON(out, db.sensorUtil.getUnitsSensorsLastObservations(params.getGroup()));
                    }
                    else{
                        throw new SQLException("Specified unit does not exist in any group of given user!");
                    }
                } else if(params.getGroup() != null && params.getUnit_id() == 0 && params.getSensor_id() != 0){
                    boolean isAffiliated = db.groupUtil.checkGroupAffiliation2User(params.getUSER(), params.getGroup());
                    if(isAffiliated){
                        DBJsonUtils.writeJSON(out, db.sensorUtil.getUnitsSensorsLastObservations(params.getGroup(), params.getSensor_id()));
                    }
                    else{
                        throw new SQLException("Specified unit does not exist in any group of given user!");
                    }
                } else{
                    throw new ServletException("Wrong combination of parameters "+ request.getQueryString());
                }
            } else {
                throw new ServletException("Wrong request "+ request.getQueryString());
            }
        } catch (SQLException e) {
            response.addHeader(HttpHeaders.CONTENT_TYPE, "plain/text;charset=UTF-8");
            super.solveGetException(e, out);
        }
    }

    /**
     * 
     * @author mkepka
     *
     */
    static class RequestParameters {
        private String USER;
        private String group;
        private long unit_id = 0;
        private long sensor_id = 0;
        private String from;
        private String to;
        private String trunc;

        RequestParameters(HttpServletRequest request)throws NullPointerException {
            Object uid = request.getParameter(ServiceParameters.UNIT_ID);
            if (uid != null) {
                unit_id = new Long(uid.toString());
            }
            Object sid = request.getParameter(ServiceParameters.SENSOR_ID);
            if (sid != null) {
                sensor_id = new Long(sid.toString());
            }
            Object tr = request.getParameter(ServiceParameters.TRUNC);
            if (tr != null) {
                trunc = tr.toString();
            }
            Object fro =  request.getParameter(ServiceParameters.FROM);
            if (fro!= null){
                from = fro.toString();
            } else {
                from = "1900-01-01 00:00:00+01";
            }
            
            Object too =  request.getParameter(ServiceParameters.TO);
            if (too!= null){
                to = too.toString();
            } else {
                to = "3000-01-01 00:00:00+01";
            }
            
            Object userO = request.getParameter(ServiceParameters.USER);
            if(userO != null){
                USER = userO.toString();
            }
            Object groupO = request.getParameter(ServiceParameters.GROUP);
            if(groupO != null){
                group = groupO.toString();
            }
        }

        public long getUnit_id() {
            return unit_id;
        }

        public long getSensor_id() {
            return sensor_id;
        }

        public String getFromTime() {
            return from;
        }
        public String getToTime() {
            return to;
        }
        public String getTrunc() {
            return trunc;
        }
        
        public String getUSER() {
            return USER;
        }
        
        public String getGroup() {
            return group;
        }
    }
}