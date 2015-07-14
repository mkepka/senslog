package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;

public class UnitHolder implements DBObject{
	private final int holder_id;	
	private final String holder_name;
	private final String phone;
	private final String icon_id;
	private final String address;
	private final String email;
	private final String www;
	
	public static String SELECT = " unit_holders.holder_id, unit_holders.holder_name, unit_holders.phone, unit_holders.icon_id, unit_holders.address, unit_holders.email, unit_holders.www ";


	public UnitHolder(ResultSet set) throws SQLException {
		holder_id = set.getInt("holder_id");		
		holder_name = set.getString("holder_name");
		phone = set.getString("phone");
		icon_id = set.getString("icon_id");
		address = set.getString("address");
		email = set.getString("email");
		www = set.getString("www");
	}
	
	public String getHolderName() {
		return holder_name;
	}
	public String getPhone() {
		return phone;
	}
	public String getIcon_id() {
		return icon_id;
	}
	public int getHolder_id() {
		return holder_id;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the www
	 */
	public String getWww() {
		return www;
	}

	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		return new UnitHolder(set);
	}

}
