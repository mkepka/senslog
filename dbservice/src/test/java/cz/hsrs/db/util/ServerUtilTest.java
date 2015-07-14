package cz.hsrs.db.util;


import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.db.pool.SQLExecutor;

public class ServerUtilTest {

	
	static String ServerUrl = "http://git.zcu.cz123";
	//@BeforeClass
	public static void genData() throws Exception{
		DBHelper.setConnection();
		String insert = "INSERT INTO server.backup_server (url) values ('"+ ServerUrl+"')";	
		SQLExecutor.executeUpdate(insert);
	}
	
	//@Test
	public void testGetSensors() throws Exception{
		ServerUtil ut = new ServerUtil();
		Assert.assertTrue(ut.getBackupUrl().contains(new URL(ServerUrl)));
		
	}
	
	//@Test
	public void callServers() throws Exception{
		ServerUtil ut = new ServerUtil();
		List<URL> urls = new LinkedList<URL>();
		urls.add(new URL("http://maps.googleapis.com/maps/api/geocode/json"));
		ut.callServers(urls, "address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&sensor=true");
		//Assert.assertNotNull(resp);
		
		
	}
	//@AfterClass
	public static void delData() throws Exception{
		DBHelper.setConnection();
		String insert = "DELETE FROM server.backup_server WHERE url ='"+ ServerUrl+"'";	
		SQLExecutor.executeUpdate(insert);
	}
}
