package cz.hsrs.db.util;


import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GroupUtilTest {

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
		unitutil.deleteUnit( unitid );
	}

	
	@Test
	public void testGetIds() throws Exception{
		Assert.assertNotNull(util.getGroupIds("admin"));
	}
	


}
