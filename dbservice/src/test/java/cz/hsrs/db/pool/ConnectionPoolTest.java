package cz.hsrs.db.pool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;

public class ConnectionPoolTest {

	static List<DBClient> threads = new ArrayList<DBClient>();
	static ConnectionPool cp;
	static String sql = "SELECT pg_sleep(5);";
	@BeforeClass
	public static void init() throws FileNotFoundException, IOException {
		String propFile = "./src/main/webapp/WEB-INF/database.properties";
		Properties prop = new Properties();
		prop.load(new FileInputStream(propFile));
		
		SQLExecutor.setProperties(prop);
	}

	//@Test
	public void testConenction() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
	/*	Jdbc3PoolingDataSource source = new Jdbc3PoolingDataSource();
		source.setDataSourceName("maplogxx");
		source.setServerName("sensors.lesprojekt.cz");		
		source.setDatabaseName("maplog");
		source.setUser("ami4");
		source.setPassword("ami4for");
		source.setInitialConnections(4);
		source.setMaxConnections(6);
		source.setLoginTimeout(100);
		Connection con = null;
		try {
			for (int i = 0 ; i < 10 ; i++) {
		    con = source.getConnection();
		    con.createStatement().executeQuery("SELECT pg_sleep(10);");
		    con.close();
			}
		} catch (SQLException e) {
		    // log error
		} finally {
		    if (con != null) {
		        try { con.close(); } catch (SQLException e) {}
		    }
		}
		*/
	}
	//@Test
	public void testPooledConnection() throws SQLException,
			FileNotFoundException, IOException, InterruptedException {		
		//DBHelper.setConnection();
		
		// cp = ConnectionManager.getConnection();

		for (int i = 1; i < 15; i++) {		
			try {								
				DBClient cl = new DBClient(sql, 1);				
				cl.start();
				threads.add(cl);
				Thread.sleep(500);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}				
	}

	//@Test
	public void testThreads2() throws SQLException, InterruptedException{
		while(threads.size() > 0){
			for (int i=0; i< threads.size(); i++){
				
				if (!threads.get(i).isAlive()){
					System.out.print("Konec vlakna " +threads.get(i).getName() + " slpoupce "+threads.get(i).getResult().getMetaData().getColumnCount());					
					threads.remove(i);
					System.out.println("Zbyvajici pocet vlaken: "+ threads.size());
				
					System.out.println("Pocet pripojeni   " + SQLExecutor.getNumberOfConnection());
					System.out.println("Pocet pouzivanych " + SQLExecutor.getNumberOfUsedConnection());
					System.out.println("-------------------------------");
					
				}
			}
		}
		
		for (int i = 0; i < 5; i++){
			Thread.sleep(1000);
			System.out.println("Pocet pripojeni   " + SQLExecutor.getNumberOfConnection());
			System.out.println("Pocet pouzivanych " + SQLExecutor.getNumberOfUsedConnection());
			System.out.println("-------------------------------");
		}
	}
	//@Test
	public void testThreads() throws InterruptedException {
		ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
		int tcount = rootGroup.activeCount();
		//System.out.println(tcount);
		while (rootGroup.activeCount() > 4) {
			/*System.out.println("Vlaken: " + tcount + "  Propjeni: "
					+ SQLExecutor.getConnectionPool()..getNumConnections() + " Pouzivano: "+
					SQLExecutor.getConnectionPool().getNumUsedConnections());*/
			
			if (tcount != rootGroup.activeCount()) {
			
				tcount = rootGroup.activeCount();

			}
			Thread.sleep(100);
		}
		System.out.print("");

	}
}
