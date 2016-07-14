/**
 * 
 */
package cz.hsrs.servlet.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

import org.mortbay.jetty.HttpHeaders;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.insert.UnitInsert;
import cz.hsrs.db.util.UtilFactory;
import cz.hsrs.servlet.feeder.ServiceParameters;

/**
 * @author mkepka
 *
 */
public class ManagementService extends DBServlet{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String jsonContent = "application/json";

    public static final String INS_UNIT = "InsertUnit";
    public static final String INS_SENSOR = "InsertSensor";
    
    private UtilFactory db;
    
    public ManagementService(){
        super();
    }
    
    @Override
    public void init() throws ServletException {
        super.init();
        
        try {
            db = new UtilFactory();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String user="";
        
        /** standard authentication *//*
        LoginUser loggedUser = null; 
        try {
            loggedUser = getAuthenticatedLoginUser(request);
            user = loggedUser.getUserName();
        } catch (AuthenticationException e1) {
            throw new ServletException("Authentication failure for request "+ request.getQueryString());
        }
        */
        /** For SDI4Apps purpose only temporary */
        RequestParameters params = new RequestParameters(request);
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
        
        /** Setting response headers */
        response.addHeader("Access-Control-Allow-Origin", "*");
        
        PrintWriter out = response.getWriter();
        try {
            if (request.getParameter(ServiceParameters.OPERATION).equals(INS_UNIT)) {
                if(request.getContentLength() > 0){
                    String contentHeader = request.getHeader(HttpHeaders.CONTENT_TYPE);
                    if(contentHeader != null && contentHeader.toLowerCase().contains(jsonContent)){
                        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                        JSONObject payload = readPayload(request);
                        UnitInsert responseO = db.manUtil.insertUnit(payload, user);
                        
                        JsonConfig cfg = new JsonConfig();
                        cfg.setIgnoreTransientFields(true);
                        cfg.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
                        JSON json = JSONSerializer.toJSON(responseO, cfg);
                        
                        json.write(out);
                    }
                } else {
                    throw new ServletException("Request doesn't contain any content!");
                }
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(INS_SENSOR)) {
                //DBJsonUtils.writeJSON(out, new UnitTrack(), db.userUtil.getTracksByUserName(params.getUSER(),params.LIMIT));
            } else {
                throw new ServletException("No operation specified!");
            }
        } catch (Exception e) {
            response.addHeader(HttpHeaders.CONTENT_TYPE, "plain/text");
            solveGetException(e, out);
        }
    }
    /**
     * 
     * @param request
     * @return
     * @throws Exception 
     * @throws IOException 
     */
    private JSONObject readPayload(HttpServletRequest request) throws Exception{
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null){
                jb.append(line);
            }
            JSONObject jsonObject = JSONObject.fromObject(jb.toString());
            return jsonObject;
        } catch (IOException e) {
            throw new Exception(e.getMessage()); 
        } catch (JSONException e){
            throw new JSONException(e.getMessage());
        }
    }
    
    /**
     * Request Parameter subclass
     * Parses parameter from the request
     * @author mkepka
     *
     */
    class RequestParameters {
        private String USER;
        
        RequestParameters(HttpServletRequest request) throws NullPointerException{
            Object userO = request.getParameter("user");
            if(userO != null){
                USER = userO.toString();
            }
        }
        
        public String getUSER() {
            return USER;
        }
    }
}