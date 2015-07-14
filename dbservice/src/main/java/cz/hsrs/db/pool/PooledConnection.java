package cz.hsrs.db.pool;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wrapes the Connection object and provide methods for connection pooling
 * @author jezekjan
 *
 */
public class PooledConnection {    

	private Connection conn;
    private boolean inuse;
    private long timestamp;


    protected PooledConnection(Connection conn) throws SQLException {
        this.conn=conn;       
        this.conn.setAutoCommit(true);
        this.inuse=false;
        this.timestamp=0;
    }

    protected synchronized boolean lease() {
       if(inuse)  {
           return false;
       } else {
          inuse=true;        
          return true;
       }
    }
    protected boolean validate() {
	try {
            conn.getMetaData();
        }catch (Exception e) {
	    return false;
	}
	return true;
    }

    protected synchronized boolean inUse() {
        return inuse;
    }

    protected synchronized long getLastUse() {
    	if (inuse){
    		return System.currentTimeMillis();
    	}
        return timestamp;
    }

    protected synchronized void release() {   
    	  timestamp=System.currentTimeMillis();
    	  inuse=false;
    }
    
    protected synchronized void close() throws SQLException {     
  	  conn.close();
    }

    protected void expireLease() {
        inuse=false;
    }

    protected Connection getConnection() {
        return conn;
    }
    
    protected Statement createStatement() throws SQLException{    	
    	return conn.createStatement();    	
    }
  
    protected boolean isClosed() throws SQLException {
        return conn.isClosed();
    }
}

