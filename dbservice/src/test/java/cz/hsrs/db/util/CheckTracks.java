package cz.hsrs.db.util;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.NoItemFoundException;

public class CheckTracks {

	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	
	@BeforeClass
	public static void setData(){
		DBHelper.setConnection();
	}
	//@Test
	public void testTrackChecker() throws ParseException, SQLException, NoItemFoundException{
	
		long unit_id = 3510291100337880L;
		Date d = format.parse("2010-05-17 09:20");
		DataChecker u = new DataChecker();
		//u.checkTrack(unit_id, d);
		u.duplicatedDataDelete();
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
