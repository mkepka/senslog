package cz.hsrs.db.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.hsrs.db.DBObject;



public class Group implements DBObject{
	 private int id ;
	 private String group_name ;
	 private int parent_group_id;
	 private boolean has_children;
	 
	 public Group(){
		 
	 };
	 public DBObject getDBObject(ResultSet set) throws SQLException{
	    /*    this.id = set.getInt("id");
	        this.group_name = set.getString("group_name");
	        this.parent_group_id = set.getInt("parent_group_id");
	        this.has_children = set.getBoolean("has_children");	*/			
	        return new Group(set.getInt("id"), 
	        		         set.getString("group_name"), 
	        		         set.getInt("parent_group_id"),
	        		         set.getBoolean("has_children"));
	 }


	public Group(int id, String group_name, int parent_group_id,
			boolean has_children) {
		super();
		this.id = id;
		this.group_name = group_name;
		this.parent_group_id = parent_group_id;
		this.has_children = has_children;
	}


	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public int getParent_group_id() {
		return parent_group_id;
	}

	public void setParent_group_id(int parent_group_id) {
		this.parent_group_id = parent_group_id;
	}

	public boolean isHas_children() {
		return has_children;
	}

	public void setHas_children(boolean has_children) {
		this.has_children = has_children;
	}
}
