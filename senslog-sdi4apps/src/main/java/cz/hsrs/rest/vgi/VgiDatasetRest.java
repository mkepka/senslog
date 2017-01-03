package cz.hsrs.rest.vgi;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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
import cz.hsrs.db.model.vgi.VgiDataset;
import cz.hsrs.db.util.DateUtil;
import cz.hsrs.db.vgi.util.VgiParams;
import cz.hsrs.rest.util.VgiDatasetRestUtil;

/**
 * Class with services managing VGIDataset objects
 * URL: /rest/vgi/dataset
 * @author mkepka
 *
 */
@Path("/vgi/dataset")
public class VgiDatasetRest {

    /**
     * Default 
     */
    public VgiDatasetRest(){
        super();
    }
    
    /**
     * Servlet catches rest/poi/test requests to test the connection to servlet
     * URL: /rest/vgi/dataset/test
     * @param testValue value of parameter test as String
     * @param request incoming servlet as HttpServletRequest
     * @return response of the servlet as String
     */
    @Path("/test")
    @GET
    @Produces("text/plain")
    public String testPoi(@QueryParam("test") String testValue, @Context HttpServletRequest request) throws ParseException{
        Date result = DateUtil.parseTimestamp(testValue);
        return result.toString();
    }
    
    /**
     * Service for inserting new dataset 
     * URL: /rest/vgi/dataset/insert?user_name=
     * 
     * Example of payload: 
     * {"dataset_name":"testing dataset", "description":"test dataset for testing of insertion"}
     * 
     * @param payload - String with VgiDataset parameters
     * @param userName - name of user
     * @return
     */
    //@Path("/") // not necessary to specify Path
    @POST
    @Consumes(MediaType.APPLICATION_JSON+";charset=utf-8")
    public Response insertDataset(
            String payload, 
            @QueryParam(VgiParams.USER_NAME) String userName){
        try{
            if(payload != null){
                JSONObject dataset = JSONObject.fromObject(payload);
                VgiDatasetRestUtil drUtil = new VgiDatasetRestUtil();
                int datasetId = drUtil.processInsertVgiDataset(dataset, userName);
                
                JSONObject resp = new JSONObject().accumulate(VgiParams.DATASET_ID_NAME, datasetId);
                return Response.ok(resp)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .build();
            } else{
                return Response.status(Status.BAD_REQUEST).entity("Any Dataset description was not given!").build();
            }
        } catch(JSONException e){
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        } catch (SQLException e) {
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for selecting specific VgiDataset object by given ID
     * URL: /rest/vgi/dataset/{dataset_id}?user_name=
     * @param datasetId - ID of VGIDataset object
     * @return VgiDataset object as JSON
     */
    @Path("/{"+VgiParams.DATASET_ID_NAME+"}")
    @GET
    public Response getVgiDataset(
            @PathParam(VgiParams.DATASET_ID_NAME) Integer datasetId,
            @QueryParam(VgiParams.USER_NAME) String userName){
        try{
            if(datasetId != null && userName != null){
                VgiDatasetRestUtil drUtil = new VgiDatasetRestUtil();
                VgiDataset dataset = drUtil.processGetVgiDataset(datasetId, userName);
                return Response.ok(dataset)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .build();
            }
            else{
                return Response.status(Status.BAD_REQUEST)
                        .entity("Parameters both dataset_id and user_name have to be given!")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for selecting VgiDatasets associated to given user 
     * URL: /rest/vgi/dataset/select?user_name=
     * @param userName - name of user
     * @return List of VGIDatasets associated to the user as JSON
     */
    //@Path("/") // not necessary to specify Path
    @GET
    public Response selectVgiDatasets(@QueryParam(VgiParams.USER_NAME) String userName){
        try{
            if(userName != null && !userName.isEmpty()){
                VgiDatasetRestUtil drUtil = new VgiDatasetRestUtil();
                List<VgiDataset> dataList = drUtil.processGetVgiDatasets(userName);
                return Response.ok(dataList)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .build();
            }
            else{
                return Response.status(Status.BAD_REQUEST).entity("No user was given!")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service deletes VgiDataset by given dataset_id
     * URL: DELETE /rest/vgi/dataset/{dataset_id}?user_name=
     * @param datasetId - ID of VgiDataset to be deleted
     * @param userName - name of user
     * @return HTTP OK if VgiDataset was deleted, HTTP Not Modified if it was not deleted
     */
    @Path("/{"+VgiParams.DATASET_ID_NAME+"}")
    @DELETE
    public Response deleteVgiDataset(
            @PathParam(VgiParams.DATASET_ID_NAME) Integer datasetId, 
            @QueryParam(VgiParams.USER_NAME) String userName){
        try{
            if(datasetId != null && userName != null){
                VgiDatasetRestUtil drUtil = new VgiDatasetRestUtil();
                boolean isDeleted = drUtil.processDeleteVgiDataset(datasetId, userName);
                if(isDeleted){
                    return Response.ok()
                            .build();
                } else {
                    return Response.notModified()
                            .build();
                }
            }
            else{
                return Response.status(Status.BAD_REQUEST).
                        entity("Parameter dataset_id has to be given!")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch(SQLException e){
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}