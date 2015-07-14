package cz.hsrs.db;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import net.sf.json.JSONObject;

public class DBJsonUtils {

	

	public static void writeJSON(PrintWriter writer, List<? extends DBObject> res ) throws SQLException{
		
			writer.print("[");
			boolean first=true;
			Iterator i = res.iterator();
			while (i.hasNext()) {		    
			    	if (!first){
						writer.println(",");
						first = false;
						}
			    	else {
			    		first=false;
			    	}
				//DBObject gr = (element.getClass().newInstance()).getDBObject(res);
				JSONObject jsonObj = JSONObject.fromObject(i.next());
				writer.print(jsonObj);
				//System.out.println(jsonObj);
			
			}
			writer.println("]");
			//writer.println("Nekomprimovano!");
		
	}
	
	/*public static void writeJSONCompressed(GZIPOutputStream gzOut, List<? extends DBObject> res ) throws SQLException{
		String row="";
		row +="[";
		boolean first=true;
		Iterator i = res.iterator();
		while (i.hasNext()) {		    
		    if (!first){
		   		row +=",";
		   		row +="\n";
				first = false;
			}
	    	else {
	    		first = false;
		    }
			JSONObject jsonObj = JSONObject.fromObject(i.next());
			row += jsonObj;		
		}
		row +="]";
		try {
			gzOut.write(row.getBytes());
			//String zprava = "\n Komprimovano!";
			//gzOut.write(zprava.getBytes());
			gzOut.close();
		} catch (IOException e) {
			throw new SQLException(e);
		}		
	}*/
	
	public static void writeJSONCompressed(GZIPOutputStream gzOut, List<? extends DBObject> res ) throws SQLException{
		boolean first=true;
		Iterator i = res.iterator();
		String row="";
		row += "[";
		try {
			while (i.hasNext()){
				
				
				JSONObject jsonObj = JSONObject.fromObject(i.next());
				row += jsonObj;
				if (i.hasNext()==true){
			   		row +=",\n";
				}
				else{
					row +="]";
				}
				first=false;
				
				gzOut.write(row.getBytes(), 0, row.getBytes().length);
				gzOut.flush();
			}
			//String zprava = "\n Komprimovano!";
			//gzOut.write(zprava.getBytes());
			gzOut.close();
		} catch (IOException e) {
			throw new SQLException(e);
		}	
	}
	
	public static void writeJSON(PrintWriter writer, DBObject element, ResultSet res ) throws SQLException{
		try {
			writer.print("[");
			boolean first=true;
			while (res.next()) {		    
			    	if (!first){
						writer.println(",");
						first = false;
						}
			    	else {
			    		first=false;
			    	}
				DBObject gr = (element.getClass().newInstance()).getDBObject(res);
				JSONObject jsonObj = JSONObject.fromObject(gr);
				writer.print(jsonObj);
				//System.out.println(jsonObj);
			
			}
			writer.println("]");
		} catch (InstantiationException e) {
			throw new SQLException(e);
		} catch (IllegalAccessException e) {
			throw new SQLException(e);
		}
	}
}
