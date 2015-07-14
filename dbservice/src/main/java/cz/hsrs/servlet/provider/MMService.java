package cz.hsrs.servlet.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.zip.GZIPOutputStream;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.hsrs.db.DBJsonUtils;
import cz.hsrs.db.util.ObservationUtil;
import cz.hsrs.servlet.feeder.ServiceParameters;

/**
 * @author mkepka
 *
 */
public class MMService extends DBServlet{

	/**
	 * MMService?Operation=GetObservations&unit_from=32768&unit_to=32773&time_from=2011-01-06+00%3A00%3A05&time_to=2011-01-06+00%3A05%3A05
	 */
	private static final long serialVersionUID = 1L;	
	public static final String GET_OBSERVATIONS = "GetObservations";	
	private ObservationUtil oUtil;
	
	public void init() throws ServletException {
		super.init();

		try {
			oUtil = new ObservationUtil();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(request, response);
			
		String user="";
		try {
			user = getAuthenticatedUser(request);
		} catch (AuthenticationException e) {			
			throw new ServletException(e);
		}
			
		RequestParameters params = new RequestParameters(request);
	/*	if(canGzip(request)==true){
			response.setHeader("Content-Encoding","gzip");
			OutputStream outS = response.getOutputStream();
			GZIPOutputStream gzOutS = new GZIPOutputStream(outS);
			try {
				if (request.getParameter(ServiceParameters.OPERATION).equals(GET_OBSERVATIONS)) {
					if(params.getUnitFrom()!= null && params.getUnitTo()!= null){
						DBJsonUtils.writeJSONCompressed(gzOutS, oUtil.getObservationsMedlov(params.getUnitFrom(), params.getUnitTo(), params.getTimeFrom(), params.getTimeTo()));
					}
					else if(params.getUnitFrom()== null && params.getUnitTo()== null){
						DBJsonUtils.writeJSONCompressed(gzOutS, oUtil.getObservationsMedlov(params.getTimeFrom(), params.getTimeTo()));
					}
					else{
						throw new ServletException("Wrong request "+request.getQueryString());
					}						
				}
				else {
					throw new ServletException("Wrong request "+request.getQueryString());
				}
			} catch (SQLException e) {
				solveGetException(e, response.getWriter());
			}
		}
		else{*/
			PrintWriter out = response.getWriter();
			try {
				if (request.getParameter(ServiceParameters.OPERATION).equals(GET_OBSERVATIONS)) {
					if(params.getUnitFrom()!= null && params.getUnitTo()!= null){
						DBJsonUtils.writeJSON(out, oUtil.getObservationsMedlov(params.getUnitFrom(), params.getUnitTo(), params.getTimeFrom(), params.getTimeTo()));
					}
					else if(params.getUnitFrom()== null && params.getUnitTo()== null){
						DBJsonUtils.writeJSON(out, oUtil.getObservationsMedlov(params.getTimeFrom(), params.getTimeTo()));
					}
					else{
						throw new ServletException("Wrong request "+request.getQueryString());
					}						
				}
				else {
					throw new ServletException("Wrong request "+request.getQueryString());
				}
			} catch (SQLException e) {
				solveGetException(e, out);
			}
		}
//	}
	private boolean canGzip(HttpServletRequest request){
		boolean canGzip = false;
		String accEnc = request.getHeader("Accept-Encoding");
		if (accEnc!=null){
			if (accEnc.indexOf("gzip")>=0){
				  canGzip=true;
			  }
		}			    
		return canGzip;
	}
	
	static class RequestParameters {

		private Long unit_from;
		private Long unit_to;
		private String time_from;
		private String time_to;

		RequestParameters(HttpServletRequest request) throws NullPointerException {
			Object uIdFrom = request.getParameter("unit_from");
			if (uIdFrom != null) {
				unit_from = new Long(uIdFrom.toString());
			}
			Object uIdTo = request.getParameter("unit_from");
			if (uIdTo != null) {
				unit_to = new Long(uIdTo.toString());
			}
			time_from = request.getParameter("time_from");
			time_to = request.getParameter("time_to");
		}

		public Long getUnitFrom() {
			return unit_from;
		}
		public Long getUnitTo() {
			return unit_to;
		}
		public String getTimeFrom(){
			return time_from;			
		}
		public String getTimeTo(){
			return time_to;			
		}
	}
}
