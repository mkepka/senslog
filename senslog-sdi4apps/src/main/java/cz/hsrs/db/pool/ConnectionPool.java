package cz.hsrs.db.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of Connection pool.
 * 
 * @author jezekjan
 * 
 */
class ConnectionPool implements ConnectionPoolMBean {

	/**
	 * List of open connections
	 */
	private final static List<PooledConnection> connections = new CopyOnWriteArrayList<PooledConnection>();

	private String url, user, password;

	public static final String LOGGER_ID = "database_logger";

	protected static Logger logger = Logger.getLogger(LOGGER_ID);

	/**
	 * Time to close unused connection (if the pool has bigger size then
	 * minpoolsize)
	 */
	private long timeout = 5000;

	/**
	 * maximal pool size (Default 8)
	 */
	private int maxpoolsize = 8;

	/**
	 * time to wait if the pool is full and all connections are busy (Default
	 * 20)
	 */
	private int waittime = 20;

	/**
	 * Minimal number of opened connections in the pool. These are still open
	 * even if they are not busy. (Default 3).
	 */
	private int minpoolsize = 3;

	private final ConnectionReaper reaper;

	protected ConnectionPool(String url, String user, String password) {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		this.url = url;
		this.user = user;
		this.password = password;
		reaper = new ConnectionReaper(this);
		reaper.start();
	}

	public int getNumConnections() {
		return connections.size();
	}

	public int getNumUsedConnections() throws InterruptedException {
		int count = 0;

		for (Iterator<PooledConnection> it = connections.iterator(); it
				.hasNext();) {
			PooledConnection c = it.next();
			if (c.inUse()) {
				count++;
			}
		}

		return count;
	}

	protected void setWaittime(int waittime) {
		this.waittime = waittime;
	}

	protected synchronized void reapConnections() {

		long stale = System.currentTimeMillis() - timeout;
		for (Iterator<PooledConnection> it = connections.iterator(); it
				.hasNext();) {
			PooledConnection conn = it.next();

			if (!conn.inUse() && (connections.size() > minpoolsize)
					&& (stale > conn.getLastUse())) {
				logger.log(Level.INFO,
						"Reaper is closing unused connection - number was: "
								+ connections.size());
				removeConnection(conn);
			}
		}

	}

	protected synchronized void closeConnections() {

		for (Iterator<PooledConnection> it = connections.iterator(); it
				.hasNext();) {
			removeConnection(it.next());
		}

	}
		

	protected synchronized void removeConnection(PooledConnection conn) {
		try {
			conn.close();
			if (conn.isClosed()) {
				connections.remove(conn);
				logger.log(Level.INFO, "Zavreno pripojeni. Pocet pripojeni: "
						+ connections.size());
			}
		} catch (SQLException e) {
			logger.log(Level.INFO, e.getMessage());
		}
	}

	protected synchronized PooledConnection getPooledConnection()
			throws SQLException {

		PooledConnection c;
		/** provide free connection */
		for (Iterator<PooledConnection> it = connections.iterator(); it
				.hasNext();) {
			c = it.next();
			if (c.lease()) {			
				return c;
			}
		}

		if (connections.size() < maxpoolsize) {

			/** open new connection if possible */

			Connection conn = DriverManager.getConnection(url, user, password);
			conn.setAutoCommit(true);
			c = new PooledConnection(conn);			
			logger.log(Level.INFO, "Otevreno nove pripojeni cislo "
					+ connections.size());
			c.lease();
			connections.add(c);
			return c;

			/** all connections are busy */
		} else {
			logger.log(Level.INFO, "pripojeni jsou zaneprazdeny - cekam");
			for (int j = 0; j < waittime; j++) {			
				for (Iterator<PooledConnection> it = connections.iterator(); it
						.hasNext();) {
					c = it.next();
					if (!c.inUse()) {
						logger.log(Level.INFO, "Nalezeno uvolnene pripojeni");
						c.lease();
						return c;
					}
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Nalezeno uvolnene pripojeni");
				}
			}

			throw new SQLException(
					"Maximum number of connection exceeded - try again later");
		}
	}

	protected long getTimeout() {
		return timeout;
	}

	protected void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	protected int getMaxPoolSize() {
		return maxpoolsize;
	}

	protected void setMaxPoolSize(int poolsize) {
		this.maxpoolsize = poolsize;
	}

	protected int getMinPoolSize() {
		return minpoolsize;
	}

	protected void setMinPoolSize(int minpoolsize) {
		this.minpoolsize = minpoolsize;
	}

	protected int getWaittime() {
		return waittime;
	}

	class ConnectionReaper extends Thread {
		private final ConnectionPool pool;

		/**
		 * Delay between checking of unused connections
		 */
		private final long delay = 1000;

		ConnectionReaper(ConnectionPool pool) {
			this.pool = pool;
		}

		public void run() {
			while (true) {
				try {
					sleep(delay);
				} catch (InterruptedException e) {
				}
				pool.reapConnections();
			}
		}
	}
}
