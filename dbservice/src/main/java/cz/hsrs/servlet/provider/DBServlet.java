package cz.hsrs.servlet.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.servlet.security.JSPHelper;
import cz.hsrs.servlet.security.LoginUser;

public abstract class DBServlet extends HttpServlet {

	public static String VERSION;
	public static String BUILD;

	protected static Logger logger = Logger
			.getLogger(SQLExecutor.LOGGER_ID);

	// public ConnectionManager conManager = ConnectionManager;
	@Override
	public void destroy() {		
		try {
			 SQLExecutor.close();
			// ConnectionManager.getConnection().commit();
			// ConnectionManager.getConnection().close();
		} catch (Exception e) {			
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		super.destroy();
	}

	/**
	 * Solve Exception what to do with Exceptions during get requestes
	 * 
	 * @param e
	 * @param out
	 * @throws ServletException
	 */
	public void solveGetException(Exception e, PrintWriter out)
			throws ServletException {
		if (e instanceof SQLException) {
			out.print(e.getMessage());
		} 
		
			logger.log(Level.WARNING, e.getMessage(), e);
			try {
				//logger.log(Level.WARNING, "Going to destroy connections...");
				logger.log(Level.WARNING,e.getMessage(), e);
				// ConnectionManager.destConnection();
				
				throw new ServletException("Wrong request ", e);
				// setDataBaseConnection();
				// ConnectionManager.getPooledConnection();
				// db = new UtilFactory();
				// doGet(request, response);
			} catch (Exception e1) {			
				e.printStackTrace();
				logger.log(Level.WARNING, e.getMessage(), e);
				throw new ServletException(e);
			}
		
	}

	/*
	 * protected String getUserFromSession(){ return WiaSession.get().getUser();
	 * }
	 */

	@Override
	public void init() throws ServletException {
		setDataBaseConnection();

		// Locale locale = new Locale("cz", "CZ");
		// Locale.setDefault(locale);

		/** Initialize logging properties */
		/*
		 * System.setProperty("java.util.logging.config.file",
		 * getServletContext() .getRealPath("WEB-INF/logging.properties"));
		 */

		try {
			String conffile = "logging.properties";
			if (SQLExecutor.getConfigfile()!=null) {
				conffile  = SQLExecutor.getConfigfile();
			}
			FileInputStream fstrem = new FileInputStream(new File(
					getServletContext().getRealPath("WEB-INF/"+conffile)));
			LogManager.getLogManager().readConfiguration(fstrem);
			LogManager.getLogManager().addLogger(logger);
		

			// LogManager.getLogManager().getProperty()
		} catch (SecurityException e1) {
			logger.log(Level.INFO, e1.getMessage());
		} catch (IOException e2) {
			logger.log(Level.INFO, e2.getMessage());
		}
		
		logger.log(Level.INFO, "Logging inialized Succesefully!");
		

		/*
		 * logger.log(Level.INFO, "Properties: " +
		 * System.getProperty("java.util.logging.config.file"));
		 */

		
		/**
		 * get version number
		 */
		try {
			String appServerHome = getServletContext().getRealPath("/");

			File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");

			Manifest mf = new Manifest();
			mf.read(new FileInputStream(manifestFile));

			Attributes atts = mf.getMainAttributes();

			VERSION = atts.getValue("Implementation-Version");
			BUILD = atts.getValue("Implementation-Build");
		} catch (FileNotFoundException e) {		
			logger.log(Level.INFO, e.getMessage());
		} catch (IOException e) {			
			logger.log(Level.INFO, e.getMessage());
		}

		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
	}

	protected void setDataBaseConnection() {
		String propFile = getServletContext().getRealPath(
				"WEB-INF/database.properties");
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(propFile));
			SQLExecutor.setProperties(prop);
		} catch (Exception e) {

			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws AuthenticationException
	 */
	@Deprecated
	protected String getAuthenticatedUser(HttpServletRequest request)
			throws AuthenticationException {
		// LoginUser user = getAuthenticatedLoginUser(request);
		LoginUser user = ((LoginUser) request.getSession().getAttribute(
				JSPHelper.USERATTRIBUTE));
		if (user == null) {
			if (request.getRemoteHost().equals("127.0.0.1")
					&& request.getParameter("user") != null) {
				return request.getParameter("user");
			} else
				throw new AuthenticationException(
						"Authentication fairlure for request "
								+ request.getQueryString());
		}
		if (user.isAuthenticated()) {
			return user.getUserName();
		} else {
			throw new AuthenticationException(
					"Authentication fairlure for request "
							+ request.getQueryString());
		}

	}

	protected LoginUser getAuthenticatedLoginUser(HttpServletRequest request)
			throws AuthenticationException {
		LoginUser user = ((LoginUser) request.getSession().getAttribute(
				JSPHelper.USERATTRIBUTE));
		if (user.isAuthenticated()) {
			return user;
		} else {
			throw new AuthenticationException(
					"Authentication fairlure for request "
							+ request.getQueryString());
		}
	}

}
