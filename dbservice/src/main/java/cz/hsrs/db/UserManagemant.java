package cz.hsrs.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserManagemant {

	protected final Connection conn;
	protected Statement stmt;

	public UserManagemant(Connection conn) throws SQLException {

		this.conn = conn;
		stmt = conn.createStatement();

	}

	public void addSystemUser() throws SQLException {

	}

	public void addGroup() throws SQLException {

	}

	public void addUnitToGroup() throws SQLException {

	}
	
	public void addUserToGroup() throws SQLException {

	}
}
