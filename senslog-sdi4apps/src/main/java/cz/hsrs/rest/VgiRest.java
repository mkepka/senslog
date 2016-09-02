package cz.hsrs.rest;

import java.io.InputStream;

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

import cz.hsrs.rest.util.BasicAuth;
import cz.hsrs.rest.util.RestUtil;

@Path("/vgi/")
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
    public String testPoi(@QueryParam("test") String testValue, @Context HttpServletRequest request){
        RestUtil rUtil = new RestUtil();
        String result = rUtil.testPoi(testValue);
        return result;
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
    
    @Path("/insobs")
    @POST
    @Consumes("multipart/form-data; charset=UTF-8")
    public Response insertObservation(
            @FormDataParam("timestamp") String timestampValue,
            @FormDataParam("category") Integer catValue,
            @FormDataParam("themaClass") Integer themaClassValue,
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
            int newObsId = rUtil.processVgiObs(timestampValue, catValue, themaClassValue, descValue, attsValue,
                    unitIdValue, "tester", datasetIdValue, lonValue, latValue, fileInStream);
            return Response.ok(String.valueOf(newObsId), MediaType.TEXT_PLAIN).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}