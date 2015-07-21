package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;
import cz.hsrs.db.util.UnitUtil;

public class UnitDriver implements DBObject{
	
	private int id;
	private UnitHolder holder;
	private String fname;
	private String lname;
	private String title;
	private String phone;

	
	private UnitDriver(ResultSet set){
		
	}
	public UnitDriver(int id, UnitHolder holder, String fname, String lname,
			String title, String phone) {
		super();
		this.id = id;
		this.holder = holder;
		this.fname = fname;
		this.lname = lname;
		this.title = title;
		this.phone = phone;
	}

	public UnitDriver() {
		super();
		this.id = 0;
		this.holder = null;
		this.fname =  null;
		this.lname =  null;
		this.title =  null;
		this.phone =  null;
	}

	public int getId() {
		return id;
	}

	public UnitHolder getHolder() {
		return holder;
	}

	public String getFname() {
		return fname;
	}

	public String getLname() {
		return lname;
	}



	public String getTitle() {
		return title;
	}



	public String getPhone() {
		return phone;
	}



	@Override
	public DBObject getDBObject(ResultSet set) throws SQLException {
		// TODO Auto-generated method stub
		UnitHolder h;
		try {
			h = (new UnitUtil())
			.getUnitHolder(set.getLong("unit_id"));
		} catch (NoItemFoundException e) {
			// TODO Auto-generated catch block
			h = null;
		}
		  return new UnitDriver(set.getInt("driver_id"), 
                    h, 
                    set.getString("fname"), 
                    set.getString("lname"),
                    set.getString("title"),
                    set.getString("phone"));
	}
}
