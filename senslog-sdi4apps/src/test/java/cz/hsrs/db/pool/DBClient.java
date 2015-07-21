package cz.hsrs.db.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBClient  extends Thread {
	//JDCConnection conn;	
	String sql;
	int num;
	ResultSet res;
	
	public DBClient(String sql, int num) throws SQLException {
				
		this.sql = sql;
		this.num = num;
		
	}

	@Override
	public void run() {
		try {
		
			for (int i = 0; i < num; i++) {
				//JDCConnection conn = (JDCConnection)cp.getConnection();
				//Statement st = conn.createStatement();					
				res = SQLExecutor.getInstance().executeQuery(sql);							
				printResultSet(res);			
			}			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet getResult(){
		return res;
	}
	private void printResultSet(ResultSet res) throws SQLException {	
		int r = 0;
		while (res.next()) {
			for (int i = 1; i <= res.getMetaData().getColumnCount(); i++) {
				//System.out.print(res.getString(i)+ " ");
				r++;				
			}
			
		}
		//System.out.println("Thread "+this.getId() + " - radky "+r+ " sloupce "+res.getMetaData().getColumnCount());
		 //System.out.println(res.getFetchSize());
	}
}

