package cz.hsrs.servlet.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.hsrs.db.ChartGenerator;
import cz.hsrs.db.DBChartUtils;
import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.SensorUtil;

/**
 * Servlet implementation class ChartServlet
 */
public class ChartServlet extends HttpServlet {
	static final long serialVersionUID = 1L;

	public static final String FROMTIME = "fromtime";

	public static final String TOTIME = "totime";

	public static final String PHENOMENON = "phenomenon";
	
	public static final String SENSOR_ID = "sensor_id";
	
	public static final String UNIT_ID = "unit_id";

	public static final String GID = "gid";

	public static final String OPERATION = "operation";
	
	public static final String SERVICE = "service";
	
	public static final String REQUEST = "request";

	public static final String HEIGHT = "height";

	public static final String WIDTH = "width";

	private String chartDir = "";

	private static File pngFile;

		// http://localhost:8080/DBService/ChartServlet?operation=GetPNG&sensor_id=20&width=500&height=300

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ChartServlet() {
		super();
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//super.doGet(request, response);
		processRequest(request, response);
	}

	/*
	 * (non-Java-doc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		processRequest(request, response);
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			if (request.getParameter(ChartServlet.OPERATION).equals("GetPNG")) {

				if (pngFile != null) {
					pngFile.delete();
				}
				pngFile = generateTargetLink(request);
				RequestDispatcher rd = request
				.getRequestDispatcher(chartDir + pngFile.getName());
				rd.forward(request, response);
				//response.sendRedirect(chartDir + pngFile.getName());
			} else if (request.getParameter(ChartServlet.OPERATION).equals(
					"GetObservation")) {
				/** todoo */
				throw new NullPointerException("Not yet implemented.");
							
			} else {
				throw new NullPointerException("No operation specified.");
			}
		} catch (Exception e) {
			request.setAttribute("exception", e);
			RequestDispatcher rd = request
					.getRequestDispatcher("/errorpage.jsp");
			rd.forward(request, response);
		}

	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected File generateTargetLink(HttpServletRequest request)
			throws Exception {

		String fromTime = request.getParameter(ChartServlet.FROMTIME);
		String toTime = request.getParameter(ChartServlet.TOTIME);

		//String phenid = request.getParameter(ChartServlet.PHENOMENON);
		//String[] phenomenon_ids = request.getParameterValues("phenomenon");
		//String gid = request.getParameter(ChartServlet.GID);
		String sensor_id = request.getParameter(ChartServlet.SENSOR_ID);
		String unit_id = request.getParameter(ChartServlet.UNIT_ID);


		String height = request.getParameter(ChartServlet.HEIGHT);
		String width =  request.getParameter(ChartServlet.WIDTH);

		DBChartUtils util = new DBChartUtils();

		Date dateFrom = null;
		Date dateTo = null;

		/*
		 * if time is not specified the default value is last 7 days
		 */
		if (fromTime == null && toTime == null) {
			Date d = new Date();
			dateFrom = new Date(d.getTime() - (1000 * 60 * 60 * 24 * 7));
			dateTo = new Date(d.getTime());
		} else {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd-HH-mm-ss");
			dateFrom = new Date(format.parse(fromTime).getTime());
			dateTo = new Date(format.parse(toTime).getTime());
		}
		
		int h = Integer.parseInt(height);
		int w = Integer.parseInt(width);	
		long s_id = Long.parseLong(sensor_id);
		long u_id = Long.parseLong(unit_id);
		
		String dir = getServletContext().getRealPath(chartDir);
		ChartGenerator chg = new ChartGenerator(dir);
		
		//, Integer.parseInt(gid),
			//	phens.get(0), tsFrom, tsTo);
		
		SensorUtil sens = new SensorUtil();
		File chartFile = chg.getSensorChart(sens.getSensorById(s_id), u_id, dateFrom, dateTo, w, h);
		

		return chartFile;

	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		String propFile = getServletContext().getRealPath(
				"WEB-INF/database.properties");
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(propFile));
			SQLExecutor.setProperties(prop);

		} catch (Exception e) {
			throw new ServletException(e.getMessage());
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();

	}
}
