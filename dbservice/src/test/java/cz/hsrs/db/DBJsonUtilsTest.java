package cz.hsrs.db;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.model.UnitPosition;
import cz.hsrs.db.model.UnitTrack;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.GroupUtil;
import cz.hsrs.db.util.UnitUtil;
import cz.hsrs.db.util.UserUtil;

public class DBJsonUtilsTest extends DBHelper {

	static GroupUtil util;
	static UnitUtil unitutil;
	static long unitid =111;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBHelper.setConnection();
		util = new GroupUtil();
		unitutil = new UnitUtil();
		unitutil.insertUnit( unitid , "test..");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		unitutil.deleteUnit(unitid);
	}


	public void testJsonPoint() throws Exception {

		// (new
		// DBJsonUtils(conManager.getPooledConnection())).getDataByUserId("12",
		// System.out);
		UserUtil db = new UserUtil();
	
		DBJsonUtils.writeJSON(
				new PrintWriter(System.out), 
				(DBObject)new UnitPosition(), 
				db.getPositionsByUserName("admin",15));
		//DBJsonUtils....writeJSON(new PrintWriter(System.out), db.getPositionsByUserName("admin",15));

	}

	public void testJsonTrack() throws Exception {

		// (new
		// DBJsonUtils(conManager.getPooledConnection())).getDataByUserId("12",
		// System.out);
		UserUtil db = new UserUtil();
		//DBJsonUtils.writeJSON(new PrintWriter(System.out), db.getLastPositionsByUserName("admin"));
		//db.writeTracksByUserName("admin", new PrintWriter(System.out));
		DBJsonUtils.writeJSON(new PrintWriter(System.out), new UnitTrack(), db.getTracksByUserName("admin",5));

	}

	public void testJsonLastPosition() throws Exception {

		// (new
		// DBJsonUtils(conManager.getPooledConnection())).getDataByUserId("12",
		// System.out);
		UserUtil db = new UserUtil();
		DBJsonUtils.writeJSON(new PrintWriter(System.out), db.getLastPositionsByUserName("admin"));

	}

	public void testJsonGroups() throws Exception {

		// (new
		// DBJsonUtils(conManager.getPooledConnection())).getDataByUserId("12",
		// System.out);
		GroupUtil db = new GroupUtil();
		DBJsonUtils.writeJSON(new PrintWriter(System.out), db
				.getSuperGroups("admin"));
		// db.writeSuperGroups("admin", new PrintWriter(System.out));

	}

	public void testSubGroups() throws Exception {

		// (new
		// DBJsonUtils(conManager.getPooledConnection())).getDataByUserId("12",
		// System.out);
		GroupUtil db = new GroupUtil();
		List<Integer> ids = db.getGroupIds("admin");
		for (Iterator<Integer> i = ids.iterator(); i.hasNext();) {
			System.out.println(i.next());
		}

	}

	@Test
	public void testGroups() throws Exception {

		// (new
		// DBJsonUtils(conManager.getPooledConnection())).getDataByUserId("12",
		// System.out);
		GroupUtil db = new GroupUtil();
		DBJsonUtils.writeJSON(new PrintWriter(System.out), db
				.getGroups("admin"));

	}
}
