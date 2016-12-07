package cz.hsrs.rest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.rest.util.BasicAuth;
import cz.hsrs.rest.util.RestUtil;
import cz.hsrs.rest.util.VgiObservationRestUtil;

@Path("/vgi")
public class VgiRest {

    @Context ServletContext context;

    public VgiRest(){
        super();
    }
    
    @Path("/test")
    @GET
    @Produces("text/plain")
    /**
     * Servlet catches rest/poi/test requests to test the connection to servlet
     * @param testValue value of parameter test as String
     * @param request incoming servlet as HttpServletRequest
     * @return response of the servlet as String
     */
    public String testPoi(@QueryParam("test") String testValue, @Context HttpServletRequest request) throws ParseException{
        //RestUtil rUtil = new RestUtil();
        //String result = rUtil.testPoi(testValue);
        //Date result = DateUtil.parseTimestamp(testValue);
    	BigInteger bi = new BigInteger(testValue, 16);
    	Long biL = bi.longValue();
    	//Long resDec = Long.decode("0x "+testValue);
    	Long result = hexToLong(testValue.getBytes());
    	String original = Long.toHexString(result);
        return "Original: "+testValue+"\n"
        		+"Long parse: "+biL.toString()+"\n"
        		//+"Long decode: "+resDec+"\n"
    			+"Long: "+result.toString()+"\n"
        		+"ByteHex: "+original;
    }
    
    @Path("/testciti")
    @GET
    @Produces("text/plain")
    /**
     * Servlet catches rest/poi/test requests to test the connection to servlet
     * @param testValue value of parameter test as String
     * @param request incoming servlet as HttpServletRequest
     * @return response of the servlet as String
     */
    public String testCitiSense(@Context HttpServletRequest request) throws Exception{
        RestUtil rUtil = new RestUtil();
        try {
            rUtil.processCitiSense();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "true";
    }
    /*
    @Path("/testauth")
    @GET
    public String testAuth(@QueryParam("email") String email, @QueryParam("pass") String pass){
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String urlGetUser = "http://portal.sdi4apps.eu/api/jsonws/user/get-user-id-by-email-address/"
                    + "company-id/10253/email-address/"+URLEncoder.encode(email, "UTF-8");
            
            HttpGet getRequest = new HttpGet(urlGetUser);
            getRequest.addHeader("accept", "plain/text");
            
            HttpHost targetHost = new HttpHost("portal.sdi4apps.eu", 80, "http");
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials(email, pass));
            
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);
            
            // Add AuthCache to the execution context
            HttpClientContext context = HttpClientContext.create();
            context.setCredentialsProvider(credsProvider);
            context.setAuthCache(authCache);
            
            CloseableHttpResponse response = httpClient.execute(getRequest, context);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            //-------------- validation --------------
            Header[] headers = response.getHeaders("Set-Cookie");
            Header head = headers[0];
            HeaderElement[] elem = head.getElements();
            String jSessionId = elem[0].getValue();
            
            //http://portal.sdi4apps.eu/api/jsonws/role/has-user-role/user-id/12901/company-id/10253/name/vgi/inherited/true
            
            HttpGet getRequestValid = new HttpGet("http://portal.sdi4apps.eu/sso-portlet/service/sso/validate/"+jSessionId+"");
            getRequestValid.setHeaders(headers);
            
            CloseableHttpResponse responseValid = httpClient.execute(getRequestValid);
            if (responseValid.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "+ responseValid.getStatusLine().getStatusCode());
            }
            BufferedReader br2 = new BufferedReader(new InputStreamReader((responseValid.getEntity().getContent())));
            String output2;
            while ((output2 = br2.readLine()) != null) {
                System.out.println(output2);
            }
            Header[] headers2 = responseValid.getHeaders("Set-Cookie");
            Header head2 = headers2[0];
            HeaderElement[] elem2 = head2.getElements();
            String jSessionId_2 = elem2[0].getValue();
            System.out.println(jSessionId_2.toString());
            
            httpClient.close();
            return "true";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
/*
        
        client.addFilter(new HTTPBasicAuthFilter(email, password));
        WebResource service = client.resource(url);
        ClientResponse response = service.accept("application/json").post(ClientResponse.class);
        if (response.getClientResponseStatus() == com.sun.jersey.api.client.ClientResponse.Status.OK) {
            //valid user
        } else {
            //invalid user
        }
    }
*/
    @Path("/testload")
    @GET
    @Produces("text/plain")
    /**
     * Servlet catches rest/poi/testload requests to load images from database according to given imageId
     * @param imageIdValue ID of image in the database
     * @return true if image exists
     */
    public String testPoiLoad(@QueryParam("imageId") String imageIdValue){
        RestUtil rUtil = new RestUtil();
        String path = context.getRealPath("images");
        boolean exists = rUtil.testPoiLoad(imageIdValue, path);
        return String.valueOf(exists);
    }
    
    /**
     * Method consumes POI insert request from client application, transfer parameters to process util and returns response TRUE 
     * if insert was successful 
     * @param titleValue String with value of Title parameter
     * @param descValue String with value of Description parameter
     * @param catValue String with value of Category parameter
     * @param statValue String with value of Status parameter
     * @param lonValue String with value of Longitude parameter
     * @param latValue String with value of Latitude parameter
     * @param timestampValue String with value of Timestamp parameter
     * @param startTimestampValue String with value of StartTimestamp parameter
     * @param fileInStream InputStream with picture file 
     * @param fileDetail FormDataContentDisposition with description of picture file
     * @param fileSize String with size of picture file in bytes
     * @param rotationangle String angle to rotate picture to position in which was taken
     * @param request 
     * @return Returns true or false as String if inserting was finished right
     */
    @Path("/insert")
    @POST
    @Consumes("multipart/form-data; charset=UTF-8")
    @Produces("text/plain")
    public String post(
            @FormDataParam("title") String titleValue,
            @FormDataParam("description") String descValue,
            @FormDataParam("category") String catValue,
            @FormDataParam("status") String statValue,
            @FormDataParam("lon") String lonValue,
            @FormDataParam("lat") String latValue,
            @FormDataParam("timestamp") String timestampValue,
            @FormDataParam("starttimestamp") String startTimestampValue,
            @FormDataParam("picture") InputStream fileInStream,
            @FormDataParam("picture") FormDataContentDisposition fileDetail,
            @FormDataParam("picturesize") String fileSize,
            @FormDataParam("rotationangle") String rotationAng,
            @Context HttpServletRequest request) {
        String[] login = BasicAuth.decode(request.getHeader("authorization"));
        String userName = login[0];
        RestUtil rUtil = new RestUtil();
        
        if(fileSize != null){
            boolean isStored = rUtil.processPoi(titleValue, descValue, catValue, statValue, lonValue, latValue, timestampValue, startTimestampValue, userName, fileInStream, Long.valueOf(fileSize), Integer.valueOf(rotationAng));
            return String.valueOf(isStored);
        }
        else{
            boolean isStored = rUtil.processPoi(titleValue, descValue, catValue, statValue, lonValue, latValue, timestampValue, startTimestampValue, userName, fileInStream, 0, 0);
            return String.valueOf(isStored);
        }
    }
    
    /**
     * 
     * @param timestampValue
     * @param catValue
     * @param descValue
     * @param attsValue
     * @param datasetIdValue
     * @param unitIdValue
     * @param lonValue
     * @param latValue
     * @param fileInStream
     * @param fileDetail
     * @return
     */
    @Path("/insobs")
    @POST
    @Consumes("multipart/form-data; charset=UTF-8")
    public Response insertObservation(
            @FormDataParam("obs_vgi_id") Integer obsId,
            @FormDataParam("timestamp") String timestampValue,
            @FormDataParam("category") Integer catValue,
            @FormDataParam("description") String descValue,
            @FormDataParam("attributes") String attsValue,
            @FormDataParam("dataset") Integer datasetIdValue,
            @FormDataParam("unitId") String unitIdValue,
            @FormDataParam("lon") String lonValue,
            @FormDataParam("lat") String latValue,
            @FormDataParam("alt") String altValue,
            @FormDataParam("dop") String dopValue,
            @FormDataParam("media") InputStream fileInStream,
            @FormDataParam("media") FormDataContentDisposition fileDetail,
            @FormDataParam("media_type") String mediaType,
            @QueryParam("user_name") String userName
            ){
        VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
        try {
        	if(userName == null){
        		userName = "tester";
        	}
            if(mediaType == null){
            	mediaType = "image/png";
            }

            if(obsId == null){
            	VgiObservation newObs = orUtil.processInsertVgiObs(timestampValue, catValue, descValue, attsValue,
                        unitIdValue, null, userName, datasetIdValue, lonValue, latValue, altValue, dopValue, 
                        fileInStream, mediaType);
                return Response.ok(String.valueOf(newObs.getObsVgiId()), MediaType.TEXT_PLAIN)
                        .build();
            }
            else{
            	VgiObservation inserted = orUtil.processUpdateVgiObs(obsId, timestampValue, catValue, descValue, attsValue,
                        unitIdValue, null, userName, datasetIdValue, lonValue, latValue, altValue, dopValue, 
                        fileInStream, mediaType);
                return Response.ok(String.valueOf(inserted.getObsVgiId()), MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage())
                    .build();
        }
    }
    /**
     * 
     * @param userName
     * @return
     * @throws NoItemFoundException
     * @throws SQLException
     * @throws ParseException 
     */
    /*
    @Path("/observations/select-geojson/")
    @GET
    public Response selectVgiObservationsAsGeoJson(@QueryParam("user_name") String userName, 
            @QueryParam("fromTime") String fromTime, @QueryParam("toTime") String toTime) throws NoItemFoundException, SQLException, ParseException{
        RestUtil rUtil = new RestUtil();
        JSONObject featureColl = rUtil.getVgiObservationBeansByUser(userName, fromTime, toTime);
        
        return Response.ok(featureColl, MediaType.APPLICATION_JSON)
                .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                .build();
    }
    */
    
    private long hexToLong(byte[] bytes) {
		if (bytes.length > 16) {
			throw new IllegalArgumentException("Byte array too long (max 16 elements)");
		}
		long v = 0;
		for (int i = 0; i < bytes.length; i += 2) {
			byte b1 = (byte) (bytes[i] & 0xFF);
			b1 -= 48;
			if (b1 > 9) b1 -= 39;
			if (b1 < 0 || b1 > 15) {
				throw new IllegalArgumentException("Illegal hex value: " + bytes[i]);
			}
			b1 <<=4;
			byte b2 = (byte) (bytes[i + 1] & 0xFF);
			b2 -= 48;
			if (b2 > 9) b2 -= 39;
			if (b2 < 0 || b2 > 15) {
				throw new IllegalArgumentException("Illegal hex value: " + bytes[i + 1]);
			}
			v |= (((b1 & 0xF0) | (b2 & 0x0F))) & 0x00000000000000FFL ;
			if (i + 2 < bytes.length) v <<= 8;
		}
		return v;
	}
	private byte[] longToHex(final long l) {
		long v = l & 0xFFFFFFFFFFFFFFFFL;
		byte[] result = new byte[16];
		Arrays.fill(result, 0, result.length, (byte)0);
		for (int i = 0; i < result.length; i += 2) {
			byte b = (byte) ((v & 0xFF00000000000000L) >> 56);
			byte b2 = (byte) (b & 0x0F);
			byte b1 = (byte) ((b >> 4) & 0x0F);
			if (b1 > 9) b1 += 39;
			b1 += 48;
			if (b2 > 9) b2 += 39;
			b2 += 48;
			result[i] = (byte) (b1 & 0xFF);
			result[i + 1] = (byte) (b2 & 0xFF);
			v <<= 8;
		}
		return result;
	}
}