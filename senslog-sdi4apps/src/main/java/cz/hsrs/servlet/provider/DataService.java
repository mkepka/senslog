package cz.hsrs.servlet.provider;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.hsrs.db.DBJsonUtils;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.UnitTrack;
import cz.hsrs.db.model.composite.LastPosition;
import cz.hsrs.db.model.composite.RealUnit;
import cz.hsrs.db.model.custom.UnitPositionSimple;
import cz.hsrs.db.util.UtilFactory;
import cz.hsrs.servlet.feeder.ServiceParameters;
import cz.hsrs.servlet.security.LoginUser;

/**
 * Servlet implementation class DataService
 * 
 * http://localhost:8080/DBService/DataService?Operation=GetDataByUserName&user=
 * pepa&limit=100
 */
public class DataService extends DBServlet {

    private static final long serialVersionUID = 1L;

    public static final String GET_TRACK = "GetTracks";

    public static final String GET_LAST_POSTION = "GetLastPositions";
    
    public static final String GET_LAST_POSTION_WITH_STATUS = "GetLastPositionsWithStatus";
    
    public static final String GET_UNITS = "GetUnits";

    public static final String GET_RECENT_TRACK = "GetRecentTracks";

    public static final String GET_POSITIONS = "GetPositions";

    public static final String GET_POSITIONS_RANGE = "GetPositionsDay";

    public static final String GET_UNITS_LIST = "GetUnitsList";

    private UtilFactory db;
    
//    private ConnectionManager cm = new ConnectionManager();

    //private RequestParameters params;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataService() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
        String user="";
        RequestParameters params = new RequestParameters(request);
        
        /* Deprecated authentication
        try {
            user = getAuthenticatedUser(request);
        } catch (AuthenticationException e) {
            throw new ServletException(e);
        }
        */
        
        /** standard authentication */
        LoginUser loggedUser = null; 
        try {
            loggedUser = getAuthenticatedLoginUser(request);
            String userName = loggedUser.getUserName();
            params.setUSER(userName);
        } catch (AuthenticationException e1) {
            throw new ServletException("Authentication failure for request "+ request.getQueryString());
        }

        response.addHeader("Access-Control-Allow-Origin", "*");
        
        /**
         * /DataService?Operation=GetUnits&user=<>&unit_id=356173060488215
         * /DataService?Operation=GetTracks&user=<>&limit=500
         * /DataService?Operation=GetLastPositions&user=<>
         * /DataService?Operation=GetLastPositionsWithStatus&user=<>
         * /DataService?Operation=GetRecentTracks&user=<>
         * /DataService?Operation=GetPositions&user=<>&limit=500
         * /DataService?Operation=GetPositionsDay&user=<>&unit_id=356173060488215&fromTime=2016-02-01&toTime=2016-02-04&ordering=desc
         * /DataService?Operation=GetUnitsList&user=<>
         */
        PrintWriter out = response.getWriter();
        try {
            if (request.getParameter(ServiceParameters.OPERATION).equals(GET_UNITS)) {
                DBJsonUtils.writeJSON(out, new RealUnit(), db.userUtil.getLastPositionsByUserNameRes(params.getUSER(), params.getUnit_id()));
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_TRACK)) {
                DBJsonUtils.writeJSON(out, new UnitTrack(), db.userUtil.getTracksByUserName(params.getUSER(),params.LIMIT));
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_LAST_POSTION)) {
                DBJsonUtils.writeJSON(out, db.userUtil.getLastPositionsByUserName(params.getUSER()));
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_LAST_POSTION_WITH_STATUS)) {
                List<LastPosition> posList = db.userUtil.getLastPositionWithStatus(params.getUSER());
                DBJsonUtils.writeJSON(out, posList);
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_RECENT_TRACK)) {
                DBJsonUtils.writeJSON(out, new UnitTrack(), db.userUtil.getTracksByUserName(params.getUSER(),1000));
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_POSITIONS)) {
                DBJsonUtils.writeJSON(out, new UnitPosition(), db.userUtil.getPositionsByUserName(params.getUSER(),params.LIMIT));
            } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_POSITIONS_RANGE)){
                DBJsonUtils.writeJSON(out, new UnitPositionSimple(), db.userUtil.getPositionsTimeRangeByUserName(params.getUSER(),params.fromTIME, params.toTIME, params.getUnit_id(), params.getOrdering()));
            } else if(request.getParameter(ServiceParameters.OPERATION).equals(GET_UNITS_LIST)){
                DBJsonUtils.writeJSON(out, db.userUtil.getUnitsByUser(params.getUSER()));
            } else {
                throw new NullPointerException("No operation specified.");
            }
        } catch (SQLException e) {
            solveGetException(e, out);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
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

    /**
     * Request Parameter subclass
     * Parses parameter from the request
     * @author jezekjan
     *
     */
    class RequestParameters {
        private String USER;
        private String fromTIME;
        private String toTIME;
        private Integer LIMIT;
        private Long unit_id;
    
        private String ordering;
        private final String ASC = "ASC";
        private final String DESC = "DESC";

        RequestParameters(HttpServletRequest request) throws NullPointerException{
            Object userO = request.getParameter("user");
            if(userO != null){
                USER = userO.toString();
            }
            Object limO = request.getParameter("limit");
            if (limO != null) {
                LIMIT = new Integer(limO.toString());
            }
            
            Object fromTimeO = request.getParameter("fromTime");
            if (fromTimeO != null) {
                fromTIME = fromTimeO.toString();
            }
            Object toTimeO = request.getParameter("toTime");
            if (toTimeO != null) {
                toTIME = toTimeO.toString();
            }
            Object orderingO = request.getParameter("ordering");
            if (orderingO != null) {
                String orderingS = orderingO.toString();
                if(orderingS.isEmpty()){
                    ordering = ASC;
                }
                else{
                    if(orderingS.equalsIgnoreCase(DESC)){
                        ordering = DESC;
                    }
                    else{
                        ordering = ASC;
                    }
                }
            }else{
                ordering = ASC;
            }

            Object unitO = request.getParameter("unit_id");
            if (unitO  != null) {
                unit_id  = new Long(unitO.toString());
            } else {
                unit_id=null;
            }
        }

        public Long getUnit_id() {
            return unit_id;
        }
        
        public void setUnit_id(long unitId) {
            unit_id = unitId;
        }
        
        public String getUSER() {
            return USER;
        }

        public void setUSER(String user) {
            USER = user;
        }

        public Integer getLIMIT() {
            return LIMIT;
        }

        public void setLIMIT(Integer limit) {
            LIMIT = limit;
        }
        public String getFromTIME() {
            return fromTIME;
        }

        public void setFromTIME(String fromTIME) {
            this.fromTIME = fromTIME;
        }

        public String getToTIME() {
            return toTIME;
        }

        public void setToTIME(String toTIME) {
            this.toTIME = toTIME;
        }

        /**
         * @return the ordering
         * that can be only "ASC" or "DESC"
         */
        public String getOrdering() {
            return ordering;
        }

        /**
         * @param ordering the ordering to set
         */
        public void setOrdering(String ordering) {
            this.ordering = ordering;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "RequestParameters [user=" + USER + ", fromTime=" + fromTIME
                    + ", toTime=" + toTIME + ", limit=" + LIMIT + ", unit_id="
                    + unit_id + ", ordering=" + ordering + "]";
        }
    }
}