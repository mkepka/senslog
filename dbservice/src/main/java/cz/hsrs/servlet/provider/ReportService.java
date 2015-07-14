package cz.hsrs.servlet.provider;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;

public class ReportService extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		      throws ServletException, IOException
		  {
		    ServletOutputStream servletOutputStream = response.getOutputStream();
		    File reportFile = new File(getServletConfig().getServletContext()
		        .getRealPath("/reports/Simple_Report.jasper"));
		    byte[] bytes = null;

		    try
		    {
		      bytes = JasperRunManager.runReportToPdf(reportFile.getPath(),
		          new HashMap(), new JREmptyDataSource());

		      response.setContentType("application/pdf");
		      response.setContentLength(bytes.length);

		      servletOutputStream.write(bytes, 0, bytes.length);
		      servletOutputStream.flush();
		      servletOutputStream.close();
		    }
		    catch (JRException e)
		    {
		      // display stack trace in the browser
		      StringWriter stringWriter = new StringWriter();
		      PrintWriter printWriter = new PrintWriter(stringWriter);
		      e.printStackTrace(printWriter);
		      response.setContentType("text/plain");
		      response.getOutputStream().print(stringWriter.toString());
		    }
		  }
		}

