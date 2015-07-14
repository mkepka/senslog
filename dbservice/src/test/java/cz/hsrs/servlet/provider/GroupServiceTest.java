package cz.hsrs.servlet.provider;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.testing.HttpTester;
import org.mortbay.jetty.testing.ServletTester;
import org.mortbay.util.ajax.JSON;

import cz.hsrs.db.pool.SQLExecutor;
import cz.hsrs.db.util.DBHelper;
import cz.hsrs.main.Start;

public class GroupServiceTest {
	private ServletTester tester;
    private HttpTester request;
    private HttpTester response;

	private static int superId =  9999999;
	private static int subId =  9999998;
	private static String userName = "Test";
	private static String userPas = "Test";

	// public static Server server = new Server();

	private static URL login ;
	private static String sid;
	
/*@Before
	public void setUpJetty() throws Exception {
		 
		 
		 this.request = new HttpTester();
		 this.response = new HttpTester();
		 this.request.setMethod("GET");
		 this.request.setHeader("Host", "tester");
		 this.request.setVersion("HTTP/1.0");
	}*/
	
	
	
	@BeforeClass
	public static void setUp() throws Exception {
		Start.start();
		DBHelper.setConnection();				
		String insertGroup = "INSERT INTO groups(id, group_name) VALUES (" +
				superId +
				", 'test group1')";
		SQLExecutor.executeUpdate( insertGroup);
		String insertSubGroup = "INSERT INTO groups(id, group_name, parent_group_id) VALUES (" +
				subId +
				", 'test group2',9999999)";
		SQLExecutor.executeUpdate( insertSubGroup);
		
		String adduser = "INSERT INTO system_users(user_name, group_id, user_password) VALUES ('" +
		            userName+ "'," +
					superId +",'"+userPas+"')";
					
		SQLExecutor.executeUpdate( adduser);
		
	

	}
	
	@Test
	public void testGetGroups() throws Exception {	
			
		
		URL result = new URL(
				ServletTestHelper.APP_URL+"GroupService?Operation=" +
				GroupService.GET_GROUPS +
				"&user=" +
				userName);	
		
		simpleJsonTester(result.openStream(),subId);
	//	simpleJsonTester(result.openStream(),superId);
	}
	
	@Test
	public void testSubGroups() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"GroupService?Operation=" +
				GroupService.GET_SUB_GROUPS +
				"&parent_group=" +
				superId+
				"&user=" +
				userName);							
		simpleJsonTester(result.openStream(),subId);
	}
	
	@Test
	public void testSuperGroup() throws Exception {
		URL result = new URL(
				ServletTestHelper.APP_URL+"GroupService?Operation=" +
				GroupService.GET_SUPER_GROUPS +
				"&user=" +
				userName);			
		simpleJsonTester(result.openStream(),superId);
	}

	private void simpleJsonTester(InputStream stream, int shouldhave) throws Exception {

		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String inputLine;	
		String result = "";
		while ((inputLine = in.readLine()) != null) {
			try {
				result = result + inputLine;
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail();
			}
		}
		Object[] res = (Object[]) JSON.parse(result);
		Map m = null;
		for (int i = 0; i < res.length; i++) {	
			//System.out.println((Map)res[i]);
			if  ( (Long)((Map)res[i]).get("id") == shouldhave){
				 m = ((Map) res[i]);							
			}					
		}
		Assert.assertNotNull(m);		
	}

	@AfterClass
	public static void tearDown() throws Exception {
	
		String delUser = "DELETE FROM system_users WHERE user_name = '" +
				userName+"'";
		SQLExecutor.executeUpdate( delUser);
		String delGroup = "DELETE FROM groups WHERE id = " + subId ;
		SQLExecutor.executeUpdate( delGroup);
		String delSuperGroup = "DELETE FROM groups WHERE id = " + superId ;
		SQLExecutor.executeUpdate( delSuperGroup);
		Start.stop();
	
	}

}
