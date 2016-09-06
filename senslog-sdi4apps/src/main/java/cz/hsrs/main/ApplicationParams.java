/**
 * 
 */
package cz.hsrs.main;

import java.text.SimpleDateFormat;

/**
 * Class with constants
 * @author mkepka
 *
 */
public class ApplicationParams {

	/**
	 * Header name for CORS: Access-Control-Allow-Origin
	 */
	public static final String CORSHeaderName = "Access-Control-Allow-Origin";
	
	/**
	 * Header value for CORS: *
	 */
	public static final String CORSHeaderValue = "*";
	
	/**
	 * DateFormater: yyyy-MM-dd HH:mm:ssZZZ
	 */
	public static SimpleDateFormat dateFormatSecondWtimeZone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ");
	
	/**
	 * DateFormater: yyyy-MM-dd HH:mm:ss.sssZZZ
	 */
	public static SimpleDateFormat dateFormatDeciSecondWtimeZone = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sssZZZ");
	
	/**
	 * DateFormater: yyyy-MM-dd HH:mm:ssZ
	 */
	public static SimpleDateFormat dateFormatSecondUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
	
}