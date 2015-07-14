package cz.hsrs.servlet.feeder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.model.AlertEvent;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.AlertUtil;
import cz.hsrs.db.util.ServerUtil;

/**
 * Servlet implementation class for Servlet: Controller
 * 
 */
public class FeederServlet extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;	

	public static Logger logger = Logger.getLogger(SQLExecutor.LOGGER_ID);

	private static List<URL> BACKUPURLS = new LinkedList<URL>();
	
//private static String TIMEZONE;
	
	
	private static String getTimeZone(){
		Calendar cal = Calendar.getInstance();
		int z = ((cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / (60 * 1000)/60);				
		String tz = "+0"+String.valueOf(z).subSequence(0, 1)+"00";
		return tz;
	}
	//SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public FeederServlet() {
		super();

	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		/** forward request to other servers */
		try {
			ServerUtil ut = new ServerUtil();
			ut.callServers(BACKUPURLS, request.getQueryString());
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage() + " query: "
					+ request.getQueryString(), e);
		}
		/**
		 * Insert observation request
		 */

		if (request.getParameter(ServiceParameters.OPERATION).equals(
				ServiceParameters.INSERT_OBSERVATION)) {
			PrintWriter out = response.getWriter();
			try {
				insObservation(request);
				out.print(true);
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage() + " query: "
						+ request.getQueryString(), e);
				out.print(false);
			}
			/**
			 * Insert position request
			 */
		} else if (request.getParameter(ServiceParameters.OPERATION).equals(
				ServiceParameters.INSERT_POSITION)) {
			PrintWriter out = response.getWriter();
			try {
				insPosition(request);
				out.print(true);
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage() + " query: "
						+ request.getQueryString(), e);
				out.print(false);
			}
		}
		/*else if	(request.getParameter(ServiceParameters.OPERATION).equals(
					ServiceParameters.INSERT_POI)) {
				PrintWriter out = response.getWriter();
				try {
					POIUtil.insert(request);
					out.print(true);
				} catch (Exception e) {
					logger.log(Level.WARNING, e.getMessage() + " query: "
							+ request.getQueryString(), e);
					out.print(false);
				}
			}*/
			/**
			 * Insert new alert event request
			 */
		else if (request.getParameter(ServiceParameters.OPERATION).equals(
				ServiceParameters.INSERT_ALERT_EVENT)) {
			PrintWriter out = response.getWriter();
			try {
				//insAlertEvent(request);
				out.print(insAlertEvent(request)); // depends on existing older event!!!
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage() + " query: "
						+ request.getQueryString(), e);
				out.print(false);
			}
			/**
			 * Set solving parameter of alert event request
			 */
		} else if (request.getParameter(ServiceParameters.OPERATION).equals(
				ServiceParameters.SOLVING_ALERT_EVENT)) {
			PrintWriter out = response.getWriter();
			try {
				insSolvingAlertEvent(request);
				out.print(true);
			} catch (Exception e) {
				logger.log(Level.WARNING, e.getMessage() + " query: "
						+ request.getQueryString(), e);
				out.print(false);
			}
		} else if (request.getParameter(ServiceParameters.OPERATION).equals(
				"RESET")) {
			SQLExecutor.close();
			init();
			response.sendRedirect("./monitor.jsp");

		} else if (request.getParameter(ServiceParameters.OPERATION).equals(
				"SLEEP")) {
			try {
				Thread.sleep(20000);
				PrintWriter out = response.getWriter();
				out.print("uzzzz....");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 * HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * Insert position via insert.jsp page form
		 */
		if (request.getParameter(ServiceParameters.OPERATION).equals(ServiceParameters.INSERT_POSITION)) {
			PrintWriter out = response.getWriter();
			try {
				insPosition(request);
				out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"><title>Insert title here</title></head>");
				out.print(true);
				out.print("<div><a href=\"./vypis.jsp?unit_id="+request.getParameter(ServiceParameters.UNIT_ID)+"\">Back to the list of graphs</a></div>");
				out.print("</body></html>");
			} catch (Exception e) {
				//logger.log(Level.WARNING, e.getMessage() + " query: "+ request.getQueryString(), e);
				out.print(false);
			}
		}
		
	}

	/**
	 * 
	 * @param dateString  Date in form of String in pattern "yyyy-MM-dd HH:mm:ssZ"
	 * @return date as type Date
	 * @throws ParseException
	 */
	public static Date parse(String dateString) throws ParseException{
		    Date date = null;
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");	
			try {
				date = formater.parse(dateString);
			} catch (ParseException e) {
							
				date = formater.parse(dateString+getTimeZone());
			}
		return date;
	}
	
	
	protected void insObservation(HttpServletRequest request)
			throws SQLException, ParseException {
		double value = Double.parseDouble(request
				.getParameter(ServiceParameters.VALUE));
		String time = request.getParameter(ServiceParameters.DATE);
		long unit_id = Long.parseLong(request
				.getParameter(ServiceParameters.UNIT_ID));
		long sensor_id = Long.parseLong(request
				.getParameter(ServiceParameters.SENSOR_ID));

		Date date = parse(time);// "2008-01-02 12:00:00");	
		DatabaseFeedOperation.insertObservation(date, unit_id, sensor_id, value);

	}
	/**
	 * Method insert new position in database
	 * @param request HTTP request with parameters
	 * @throws SQLException while parsing request
	 */
	
	protected void insPosition(HttpServletRequest request) throws SQLException {

		double lat = Double.parseDouble(request.getParameter(ServiceParameters.LAT));
		double lon = Double.parseDouble(request.getParameter(ServiceParameters.LON));

		double alt = Double.NaN;
		if (request.getParameter(ServiceParameters.ALT) != null && request.getParameter(ServiceParameters.ALT).isEmpty() != true) {
			alt = Double.parseDouble(request.getParameter(ServiceParameters.ALT));
		}

		double speed = Double.NaN;
		if (request.getParameter(ServiceParameters.SPEED) != null && request.getParameter(ServiceParameters.SPEED).isEmpty() != true) {
			speed = Double.parseDouble(request.getParameter(ServiceParameters.SPEED));
		}
				
		long unit_id = Long.parseLong(request.getParameter(ServiceParameters.UNIT_ID));

		String date = request.getParameter(ServiceParameters.DATE);
		if(date.contains("T")){
			date = date.replace("T", " ");
			//System.out.println("date="+date);
		}
		Date time;
		try {
			time = parse(date);
		} catch (ParseException e) {
			throw new SQLException(e);
		}
		

		/**
		 * I Dop is voluntary parameter
		 */
		if (request.getParameter(ServiceParameters.DOP) != null && request.getParameter(ServiceParameters.DOP).isEmpty() != true) {
			double dop = Double.parseDouble(request.getParameter(ServiceParameters.DOP));
			DatabaseFeedOperation.insertPosition(unit_id, lat, lon, alt, dop, time, speed);
		} else {
			DatabaseFeedOperation.insertPosition(unit_id, lat, lon, alt, time, speed);
			//System.out.println(unit_id+", "+lat+", "+lon+", "+alt+", "+time+", "+speed);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
	

		String propFile = getServletContext().getRealPath(
				"WEB-INF/database.properties");
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(propFile));
			SQLExecutor.setProperties(prop);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		// TODO Auto-generated method stub

		/** Initialize logging properties */
		/*
		 * System.setProperty("java.util.logging.config.file",
		 * getServletContext().getRealPath( "WEB-INF/logging.properties"));
		 */
		// System.out.println(getServletContext().getRealPath("WEB-INF/logging.properties"));

		try {
			FileInputStream fstrem = new FileInputStream(new File(
					getServletContext().getRealPath(
							"WEB-INF/logging.properties")));
			LogManager.getLogManager().readConfiguration(fstrem);
			// LogManager.getLogManager().getProperty()		
			logger.log(Level.INFO, "Logging inialized Succesefully!");
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e1.getMessage());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e1.getMessage());
		}

		// System.setProperty("java.util.logging.FileHandler.pattern",
		// getServletContext().getRealPath("")+"/log.txt");

		// logger.log(Level.INFO,
		// "Properties: "+System.getProperty("java.util.logging.config.file"));
		// logger.log(Level.INFO, "File logging: "+
		// System.getProperty("java.util.logging.FileHandler.pattern"));

		// String propFile =
		// "/home/jezekjan/code/workspace-web2/DBFeederService/WebContent/WEB-INF/database.properties";

		SQLExecutor.setProperties(prop);

		/** load backup server **/
		try {
			ServerUtil ut = new ServerUtil();
			//BACKUPURLS = ut.getBackupUrl();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

		super.init();
	}
	/**
	 * Method processes insertAlertEvent request
	 * @param request - HTTP GET request
	 * @throws Exception throws exception while parsing time stamp 
	 */
	protected boolean insAlertEvent(HttpServletRequest request) throws Exception {
		String time = request.getParameter(ServiceParameters.DATE);
		long unit_id = Long.parseLong(request.getParameter(ServiceParameters.UNIT_ID));
		int alert_id = Integer.parseInt(request.getParameter(ServiceParameters.ALERT_ID));

		AlertUtil aUtil = new AlertUtil();
		List<AlertEvent> eventList = aUtil.getUnsolvingAlertEvent(unit_id, alert_id);

		boolean notOlderEvent = eventList.isEmpty();
		if (notOlderEvent == true) {		
			Date date = parse(time);// "2008-01-02 12:00:00");
			DatabaseFeedOperation.insertAlertEvent(date, unit_id, alert_id);
		}
		return notOlderEvent;
	}

	/**
	 * Method processes insert solving parameter request
	 * @param request HTTP get request
	 * @throws Exception while parsing alert_event_id
	 */
	protected void insSolvingAlertEvent(HttpServletRequest request)	throws Exception {
		int event_id = Integer.parseInt(request.getParameter(ServiceParameters.ALERT_EVENT_ID));

		DatabaseFeedOperation.solvingAlertEvent(event_id);
	}
}