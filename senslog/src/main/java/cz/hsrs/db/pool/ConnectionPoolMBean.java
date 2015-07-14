package cz.hsrs.db.pool;

public interface ConnectionPoolMBean {

	public int getNumConnections();
	public int getNumUsedConnections() throws InterruptedException ;
}
