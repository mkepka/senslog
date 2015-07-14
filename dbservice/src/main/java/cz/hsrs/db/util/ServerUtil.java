package cz.hsrs.db.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServerUtil extends DBUtil {

	public ServerUtil() throws SQLException {
		
	}

	public List<URL> getBackupUrl() throws SQLException, MalformedURLException {
		LinkedList<URL> urls = new LinkedList<URL>();
		String query = "SELECT * FROM server.backup_server;";
		ResultSet res = stmt.executeQuery(query);
		while (res.next()) {
			urls.add(new URL(res.getString("url")));
		}
		return urls;
	}

	public void callServers(List<URL> urls, final String query)
			throws MalformedURLException {		
		for (Iterator<URL> i = urls.iterator(); i.hasNext();) {
			final URL url = i.next();
			final URL urlreq = new URL(url.toString() + "?" + query);
			 (new CallServer(urlreq, System.out)).start();

		}		

	}

	private class CallServer extends Thread {
		private URL url;
		private PrintStream ps;
		private Map<String, String> resp = new HashMap<String, String>();

		CallServer(URL url, PrintStream ps) {
			this.url = url;
			this.ps = ps;
		}

		public void run() {
			try {
				final BufferedReader in = new BufferedReader(
						new InputStreamReader(url.openStream()));
				ps.println(url.toString()+ "..." + in.readLine());
			} catch (IOException e) {
				ps.println(url.toString()+ "..." + e.getMessage());;				
			}
		}

		public Map<String, String> getResp() {
			return resp;
		}				
	}
}
