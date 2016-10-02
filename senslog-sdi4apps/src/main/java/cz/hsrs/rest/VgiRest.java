package cz.hsrs.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.VgiCategory;
import cz.hsrs.db.model.vgi.VgiDataset;
import cz.hsrs.db.model.vgi.VgiMedia;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.main.ApplicationParams;
import cz.hsrs.rest.util.BasicAuth;
import cz.hsrs.rest.util.RestUtil;

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
        Date result = DateUtil.parseTimestamp(testValue);
        return result.toString();
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
            @FormDataParam("unitId") Long unitIdValue,
            @FormDataParam("lon") String lonValue,
            @FormDataParam("lat") String latValue,
            @FormDataParam("media") InputStream fileInStream,
            @FormDataParam("media") FormDataContentDisposition fileDetail
            ){
        RestUtil rUtil = new RestUtil();
        try {
            String userName = "tester";
            if(obsId == null){
                int newObsId = rUtil.processVgiObs(timestampValue, catValue, descValue, attsValue,
                        unitIdValue, userName, datasetIdValue, lonValue, latValue, fileInStream);
                return Response.ok(String.valueOf(newObsId), MediaType.TEXT_PLAIN)
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .build();
            }
            else{
                boolean inserted = rUtil.updateVgiObs(obsId, timestampValue, catValue, descValue, attsValue,
                        unitIdValue, userName, datasetIdValue, lonValue, latValue, fileInStream);
                return Response.ok(String.valueOf(inserted), MediaType.TEXT_PLAIN)
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .build();
        }
    }
    
    /**
     * Method processes service to get all VGIObservations associated to given user
     * @param userName - username of given user (mandatory)
     * @param format - format of the response (optional), geojson or json
     * @return
     */
    @Path("/observations/select")
    @GET
    public Response selectVgiObservations(
            @QueryParam("user_name") String userName,
            @QueryParam("format") String format,
            @QueryParam("dataset_id") Integer datasetId,
            @QueryParam("category_id") Integer categoryId,
            @QueryParam("fromTime") String fromTime,
            @QueryParam("toTime") String toTime){
        RestUtil rUtil = new RestUtil();
        try{
            if(userName != null){
                if(format != null && format.equalsIgnoreCase("geojson")){
                    JSONObject featureColl = rUtil.getVgiObservationBeansByUser(userName, fromTime, toTime, datasetId, categoryId);
                    return Response.ok(featureColl, MediaType.APPLICATION_JSON+";charset=utf-8")
                            .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                            .build();
                } 
                else{
                    List<VgiObservation> obsList = rUtil.getVgiObservationsByUser(userName);
                    return Response.ok(obsList)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                            .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                            .build();
                }
            }
            else{
                return Response.status(Status.BAD_REQUEST)
                        .entity("Parameter user_name has to be given!")
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        } catch (NoItemFoundException e) {
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        } catch (ParseException e) {
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * 
     * @param obsId
     * @param username
     * @return
     */
    @Path("/observation/{obs_vgi_id}")
    @GET
    public Response getVgiObservation(@PathParam("obs_vgi_id") Integer obsId, @QueryParam("user_name") String username) {
        RestUtil rUtil = new RestUtil();
        try{
            JSONObject feature = rUtil.getVgiObservation(obsId, username);
            return Response.ok(feature, MediaType.APPLICATION_JSON+";charset=utf-8")
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .build();
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        } catch (NoItemFoundException e) {
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    /**
     * 
     * @param obsId
     * @return
     * @throws SQLException
     */
    @Path("/media/select")
    @GET
    public Response selectVgiMedia(@QueryParam("obs_id") Integer obsId) throws SQLException{
        RestUtil rUtil = new RestUtil();
        List<VgiMedia> media = rUtil.getVgiMedia(obsId);
        return Response.ok(new ByteArrayInputStream(media.get(0).getObservedMedia()), media.get(0).getMediaDatatype())
                //.header("Content-Disposition", "attachment; filename="+media.get(0).internalGetTimeReceivedMilis()+".png")
                .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                .build();
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
    @Path("/category/select")
    @GET
    public Response selectCategories(){
        RestUtil rUtil = new RestUtil();
        try{
            List<VgiCategory> catList = rUtil.getVgiCategories();
            return Response.ok(catList)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .build();
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * 
     * @return List of datasets associated to the user
     */
    @Path("/dataset/select")
    @GET
    public Response selectDatasets(@QueryParam("user_name") String userName){
        RestUtil rUtil = new RestUtil();
        if(userName != null && !userName.isEmpty()){
            try{
                List<VgiDataset> dataList = rUtil.getVgiDatasets(userName);
                return Response.ok(dataList)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .build();
            } catch(SQLException e){
                return Response.serverError().entity(e.getMessage())
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        else{
            return Response.serverError().entity("No user was given!")
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }

    }
    
    /**
     * /rest/vgi/dataset/insert?user_name=
     * 
     * @param payload
     * @param userName
     * @return
     */
    @Path("/dataset/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON+";charset=utf-8")
    public Response insertDataset(String payload, @QueryParam("user_name") String userName){
        try{
            JSONObject dataset = JSONObject.fromObject(payload);
            RestUtil rUtil = new RestUtil();
            int datasetId = rUtil.insertVgiDataset(dataset, userName);
            JSONObject resp = new JSONObject();
            resp.accumulate("dataset_id", datasetId);
            
            return Response.ok(resp)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .build();
        } catch(JSONException e){
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service deletes dataset by given dataset_id
     * @param datasetId
     * @param userName
     * @return
     */
    @Path("/dataset/delete/{dataset_id}")
    @DELETE
    public Response deleteDataset(@PathParam("dataset_id") Integer datasetId, @QueryParam("user_name") String userName){
        try{
            RestUtil rUtil = new RestUtil();
            if(userName != null && !userName.isEmpty()){
                boolean isDeleted = rUtil.deleteVgiDataset(datasetId, userName);
                return Response.ok(String.valueOf(isDeleted))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .build();
            }
            else{
                return Response.status(Status.BAD_REQUEST)
                        .entity("Parameter user_name has to be given!")
                        .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(ApplicationParams.CORSHeaderName, ApplicationParams.CORSHeaderValue)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}