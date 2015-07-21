package cz.hsrs.db.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.postgresql.util.PSQLException;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.TrackData;
import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.pool.SQLExecutor;

public class DataChecker  extends DBUtil{

	public DataChecker() throws SQLException {
		
	}
	
	public void duplicatedDataDelete() throws SQLException{
		String sel = "SELECT time_stamp, unit_id "+
				   " FROM ( SELECT count(units_positions.gid) AS num, units_positions.time_stamp, units_positions.unit_id "+
				 "          FROM units_positions "+
				  "        GROUP BY units_positions.time_stamp, units_positions.unit_id) wrong "+
				  " WHERE wrong.num > 1 ";
		
		ResultSet res = stmt.executeQuery(sel);
		int i = 0;
		String deltrans = "BEGIN; ";
		while (res.next()) {
			String sel2 =  "SELECT gid from units_positions where unit_id = "+ res.getLong("unit_id")
							+ " AND time_stamp = '"+res.getString("time_stamp")+"'::timestamp";
			
			ResultSet res2 = SQLExecutor.getInstance().executeQuery(sel2);	
			res2.next();
			int delete =  res2.getInt("gid");
			String del = "delete from units_positions where gid ="+delete + "; \n"; 
			
				
			
			
			if (i<0) {
				deltrans = deltrans + del;
				i++;
			} else {
				deltrans = deltrans + " COMMIT;";
				System.out.println("deleting ..." +deltrans );
				try {
					SQLExecutor.executeUpdate(deltrans);
				} catch (PSQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i = 0;
				deltrans = "BEGIN; \n ";
				
			}
			/*while (res2.next()) {
				System.out.println(res2.getInt("gid"));
			}*/
							
		}
	}
	public void checkTrack( long unit_id, Date time_stamp) throws SQLException, NoItemFoundException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		TrackData td = new TrackUtil().getTrack(unit_id, time_stamp);
		/** select point from database **/
		String sel_points  = "SELECT "+  UnitPosition.SELECT + " FROM units_positions WHERE time_stamp >= '" + formater.format(td.getStart()) + 
		"' AND time_stamp <= '"+ formater.format(td.getEnd())+"' AND unit_id = "+ unit_id +";";
		ResultSet res = stmt.executeQuery(sel_points);
		List<UnitPosition> positions = (List<UnitPosition>)generateObjectList(new UnitPosition(), res);
		
		for (Iterator<UnitPosition> i = positions.iterator() ; i.hasNext();){
			UnitPosition pos = i.next();
			System.out.println(pos.getGid() + ",  " +pos.getTime_stamp() + " "+ pos.getX()+ " "+pos.getY());
		}
		
		
	}
		
}
