package cz.hsrs.servlet.provider;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.hsrs.main.Start;


/**
 * Pomocna trida pro rucni ladeni sluzeb. 
 * @author jezekjan
 *
 */
public class ServerDebugger {
	
	static Start starter;
	
	@BeforeClass
	public static void setUp() throws Exception {
		starter.start();
		
	}
	@Test
	public void testServer(){
		Assert.assertTrue(starter.server.isRunning());
	}
	@AfterClass
	public static void tearDown() throws Exception {
	//	starter.stop();
		
	}
}
