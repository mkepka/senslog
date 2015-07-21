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
import cz.hsrs.db.util.UtilFactory;
import cz.hsrs.servlet.feeder.ServiceParameters;

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
        
        try {
            user = getAuthenticatedUser(request);
        } catch (AuthenticationException e) {
            throw new ServletException(e);
        }
        
        /**
         * 
         */
        PrintWriter out = response.getWriter();
        try {
            RequestParameters params = new RequestParameters(request);
            params.setUSER(user);
                if (request.getParameter(ServiceParameters.OPERATION).equals(GET_UNITS)) {
                    DBJsonUtils.writeJSON(out, new RealUnit(), db.userUtil.getLastPositionsByUserNameRes(params.getUSER(), params.getUnit_id()));
                    //db.writePositionsByUserName(params.getUSER(), out, params.LIMIT);
                } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_TRACK)) {
                    //db.userUtil.getTracksByUserName(params.getUSER())
                    DBJsonUtils.writeJSON(out, new UnitTrack(), db.userUtil.getTracksByUserName(params.getUSER(),params.LIMIT));
                    //db.writeTracksByUserName(params.etUSER(), out);
                } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_LAST_POSTION)) {
                    //db.writeLastPositionsByUserName(params.getUSER(), out);
                    DBJsonUtils.writeJSON(out, db.userUtil.getLastPositionsByUserName(params.getUSER()));
                } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_LAST_POSTION_WITH_STATUS)) {
                    //db.writeLastPositionsByUserName(params.getUSER(), out);
                    List<LastPosition> posList = db.userUtil.getLastPositionWithStatus(params.getUSER());
                    DBJsonUtils.writeJSON(out, posList);
                } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_RECENT_TRACK)) {
                    DBJsonUtils.writeJSON(out, new UnitTrack(), db.userUtil.getTracksByUserName(params.getUSER(),1000));
                } else if (request.getParameter(ServiceParameters.OPERATION).equals(GET_POSITIONS)) {
                    DBJsonUtils.writeJSON(out, new UnitPosition(), db.userUtil.getPositionsByUserName(params.getUSER(),params.LIMIT));
                    //db.writePositionsByUserName(params.getUSER(), out, params.LIMIT);
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
     * Request Parametr subclass
     * @author jezekjan
     *
     */
    class RequestParameters {
        private String USER;
        private Integer LIMIT;
        private Long unit_id;
    

        public Long getUnit_id() {
            return unit_id;
        }

        public void setUnit_id(long unitId) {
            unit_id = unitId;
        }

        RequestParameters(HttpServletRequest request) throws NullPointerException{
            //USER = request.getParameter("user").toString();
            Object limO = request.getParameter("limit");
            if (limO != null) {
                LIMIT = new Integer(limO.toString());
            }
            Object unitO = request.getParameter("unit_id");
            if (unitO  != null) {
                unit_id  = new Long(unitO.toString());
            } else {
                unit_id=null;
            }
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
    }
}