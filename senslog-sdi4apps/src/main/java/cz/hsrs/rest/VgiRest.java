package cz.hsrs.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sf.json.JSONObject;

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
    public Response selectVgiObservations(@QueryParam("user_name") String userName, @QueryParam("format") String format, 
            @QueryParam("fromTime") String fromTime, @QueryParam("toTime") String toTime){
        RestUtil rUtil = new RestUtil();
        try{
            if(userName != null){
                if(format != null && format.equalsIgnoreCase("geojson")){
                    JSONObject featureColl = rUtil.getVgiObservationBeansByUser(userName, fromTime, toTime);
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
    
    @Path("/dataset/select")
    @GET
    public Response selectDatasets(){
        RestUtil rUtil = new RestUtil();
        try{
            List<VgiDataset> dataList = rUtil.getVgiDatasets("tester");
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
}