package cz.hsrs.track;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import cz.hsrs.db.DatabaseFeedOperation;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.db.util.UnitUtil;

public class TrackTest {

	private static int start_id = 100;
	private static int last_id = 110;
	static UnitUtil util;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DBHelper.setConnection();
		util = new UnitUtil();

		/**
		 * insert units
		 */
		for (int i = start_id; i < last_id; i++) {
			util.insertUnit(i, "testig unit");
			util.changeUnitsTrackInterval(i, 3000);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		for (int i = start_id; i < last_id; i++)  {
			util.deleteUnit(i);
		}
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {

	}
	
	public void testClosingTrack() throws Exception {

		for (int i = start_id; i < last_id; i++)  {
			DatabaseFeedOperation.insertPosition(i, 14 + i, 50, 0, new Date());			
		}

		Thread.sleep(2000);
		for (int i = start_id; i < last_id; i=i+2)  {
			DatabaseFeedOperation.insertPosition(i, 14 + i, 50 + i, 0,
					new Date());
		}
		// sudy bezi, lichy jsou zavreny
		Thread.sleep(2000);
		for (int i = start_id; i < last_id; i=i+2) {
			assertNotNull((new TrackManager()).getThread(Integer.toString(i)));
			assertTrue(util.isRunning(i));
		}
		for (int i = start_id+1; i < last_id; i=i+2) {
			assertNull((new TrackManager()).getThread(Integer.toString(i)));
			assertFalse(util.isRunning(i));
		}
		/**
		 * smaz units
		 */

	}

}