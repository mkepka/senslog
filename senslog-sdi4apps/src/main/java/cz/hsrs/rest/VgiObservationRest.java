/**
 * 
 */
package cz.hsrs.rest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sf.json.JSONObject;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import cz.hsrs.db.model.NoItemFoundException;
import cz.hsrs.db.model.vgi.VgiMedia;
import cz.hsrs.db.model.vgi.VgiObservation;
import cz.hsrs.rest.util.VgiObservationRestUtil;

/**
 * Class with services managing VgiObservation objects
 * URL: /rest/vgi/observation
 * @author mkepka
 */
@Path("/vgi/observation")
public class VgiObservationRest {

    /**
     * Default 
     */
    public VgiObservationRest(){
        super();
    }
    
    /**
     * Service for inserting or updating VgiObservation 
     * URL: /rest/vgi/observation/insert?user_name=
     * @param obsId - ID of VGIObservation, when specified UPDATE is provided
     * @param timestampValue - time stamp when observation was obtained, mandatory
     * @param catValue - ID of VgiCategory, mandatory
     * @param descValue - description of VgiObservation, optional
     * @param attsValue - further attributes in JSON format as String
     * @param datasetIdValue - ID of VgiDataset, mandatory
     * @param unitIdValue - ID of unit that has produced observation, mandatory
     * @param lonValue - Longitude of observation, mandatory
     * @param latValue - Latitude of observation, mandatory
     * @param altValue - Altitude of observation, optional
     * @param dop - Dilution of precision of position, optional
     * @param fileInStream - InputStream with associated media, optional
     * @param fileDetail - FormDataContentDisposition describing associated media, optional 
     * @param mediaType - Data type of associated media file, mandatory
     * @param userName - name of user that produced the observation, mandatory
     * @return ID of registered VgiObservation object
     */
    //@Path("/") // not necessary to specify Path
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
                // process INSERT
                int newObsId = orUtil.processInsertVgiObs(
                        timestampValue,
                        catValue,
                        descValue,
                        attsValue,
                        unitIdValue,
                        userName,
                        datasetIdValue,
                        lonValue, latValue, altValue, dopValue,
                        fileInStream, mediaType);
                return Response.ok(String.valueOf(newObsId))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
            else{
                // process UPDATE
                boolean updated = orUtil.processUpdateVgiObs(obsId,
                        timestampValue,
                        catValue,
                        descValue,
                        attsValue,
                        unitIdValue,
                        userName,
                        datasetIdValue,
                        lonValue, latValue, altValue, dopValue,
                        fileInStream, mediaType);
                return Response.ok(String.valueOf(updated))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for updating VgiObservation 
     * URL: PUT /rest/vgi/observation/{obs_vgi_id}?user_name=
     * @param obsId - ID of VGIObservation, when specified UPDATE is provided
     * @param timestampValue - time stamp when observation was obtained, mandatory
     * @param catValue - ID of VgiCategory, mandatory
     * @param descValue - description of VgiObservation, optional
     * @param attsValue - further attributes in JSON format as String
     * @param datasetIdValue - ID of VgiDataset, mandatory
     * @param unitIdValue - ID of unit that has produced observation, mandatory
     * @param lonValue - Longitude of observation, mandatory
     * @param latValue - Latitude of observation, mandatory
     * @param altValue - Altitude of observation, optional
     * @param dop - Dilution of precision of position, optional
     * @param fileInStream - InputStream with associated media, optional
     * @param fileDetail - FormDataContentDisposition describing associated media, optional 
     * @param mediaType - Data type of associated media file, mandatory
     * @param userName - name of user that produced the observation, mandatory
     * @return true if VgiObservation was updated, false if not 
     */
    @Path("/{obs_vgi_id}")
    @PUT
    @Consumes("multipart/form-data; charset=UTF-8")
    public Response updateObservation(
            @PathParam("obs_vgi_id") Integer obsId,
            @FormDataParam("timestamp") String timestampValue,
            @FormDataParam("category") Integer catValue,
            @FormDataParam("description") String descValue,
            @FormDataParam("attributes") String attsValue,
            @FormDataParam("dataset") Integer datasetIdValue,
            @FormDataParam("unitId") Long unitIdValue,
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
            if(obsId != null){
                // process UPDATE
                boolean updated = orUtil.processUpdateVgiObs(obsId,
                        timestampValue,
                        catValue,
                        descValue,
                        attsValue,
                        unitIdValue,
                        userName,
                        datasetIdValue,
                        lonValue, latValue, altValue, dopValue,
                        fileInStream, mediaType);
                return Response.ok(String.valueOf(updated))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
            else{
                return Response.serverError().entity("VgiObservation ID has to be given!")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for getting specific VgiObservation object 
     * URL: /rest/vgi/observation/{obs_vgi_id}?user_name=
     * @param obs_vgi_id - ID of the VgiObservation object 
     * @param user_name - name of user
     * @return VgiObservation object as JSON
     */
    @Path("/{obs_vgi_id}")
    @GET
    public Response getVgiObservation(@PathParam("obs_vgi_id") Integer obsId, @QueryParam("user_name") String username, @QueryParam("format") String format) {
        VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
        if(obsId != null && username != null){
            try{
                if(format != null && format.equalsIgnoreCase("geojson")){
                    JSONObject feature = orUtil.processGetVgiObservationAsJson(obsId, username);
                    return Response.ok(feature)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                            .build();
                } else{
                    VgiObservation obs = orUtil.processGetVgiObservation(obsId, username);
                    return Response.ok(obs)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                            .build();
                }
            } catch(SQLException e){
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            } catch (NoItemFoundException e) {
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        else{
            return Response.serverError().entity("VGIObservation ID has to be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for selecting VGIObservations by given filter parameters
     * URL: /rest/vgi/observation?user_name=&format=&dataset_id=&category_id=&fromTime=&toTime=&extent=&unit_id=
     * @param userName - name of user, mandatory
     * @param format - format of the response (optional), Values: geojson/json/null
     * @param datasetId - ID of VgiDataset, optional
     * @param categoryId - ID of VgiCategeory, optional
     * @param fromTime - beginning of time range, ISO 8601 pattern, optional
     * @param toTime - end of time range, ISO 8601 pattern, optional
     * @param extent - Array of coordinates representing BBOX of map window, format: [xmin, ymin, xmax, ymax, SRID]
     * @return
     */
    //@Path("/") // not necessary to specify Path
    @GET
    public Response selectVgiObservations(
            @QueryParam("user_name") String userName,
            @QueryParam("format") String format,
            @QueryParam("dataset_id") Integer datasetId,
            @QueryParam("category_id") Integer categoryId,
            @QueryParam("fromTime") String fromTime,
            @QueryParam("toTime") String toTime,
            @QueryParam("extent") String extent,
            @QueryParam("unit_id") Long unitId){
        VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
        try{
            if(userName != null){
                // response in GeoJSON
                if(format != null && format.equalsIgnoreCase("geojson")){
                    JSONObject featureColl = orUtil.processGetVgiObservationsByUserAsJson(userName, fromTime, toTime, datasetId, categoryId, extent, unitId);
                    return Response.ok(featureColl, MediaType.APPLICATION_JSON+";charset=utf-8")
                            .build();
                }
                // response in pure JSON
                else{
                    List<VgiObservation> obsList = orUtil.processGetVgiObservationsByUser(userName, fromTime, toTime, datasetId, categoryId, extent);
                    return Response.ok(obsList)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                            .build();
                }
            }
            else{
                return Response.status(Status.BAD_REQUEST)
                        .entity("Parameter user_name has to be given!")
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
     * Service for deleting VGIObservation by given ID
     * URL: /rest/vgi/observation/{obs_vgi_id}?user_name=
     * @param obsId - ID of VGIObservation to be deleted
     * @param userName - name of user which owns VGIObservation object
     * @return HTTP OK response
     */
    @Path("/{obs_vgi_id}")
    @DELETE
    public Response deleteVgiObservation(@PathParam("obs_vgi_id") Integer obsId, @QueryParam("user_name") String userName){
        VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
        if(obsId != null && userName != null){
            try{
                boolean isDeleted = orUtil.processDeleteVgiObservation(obsId, userName);
                if(isDeleted == true){
                    return Response.ok().build();
                }
                else{
                    return Response.serverError().entity("VGIObservation was not deleted!")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                            .build();
                }
            } catch(Exception e){
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        else{
            return Response.serverError().entity("VGIObservation ID must be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for inserting additional media file to master VgiObservation
     * URL: /rest/vgi/observation/{obs_vgi_id}/media?user_name=
     * @param obsId - ID of master VgiObservation
     * @param mediaType - data type of media file
     * @param fileInStream - InputStream containing media file
     * @param fileDetail - metadata of media file 
     * @param userName - name of user that owns VgiObservation
     * @return true if media file was inserted
     */
    @Path("{obs_vgi_id}/media")
    @POST
    @Consumes("multipart/form-data; charset=UTF-8")
    public Response insertVgiMedia(
            @PathParam("obs_vgi_id") Integer obsId,
            @FormDataParam("media_type") String mediaType,
            @FormDataParam("media") InputStream fileInStream, 
            @FormDataParam("media") FormDataContentDisposition fileDetail,
            @QueryParam("user_name") String userName){
        if(obsId != null && userName != null && fileInStream != null){
            try{
                VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
                boolean inserted = orUtil.processInsertNextMedia(obsId, fileInStream, userName, mediaType);
                return Response.ok(String.valueOf(inserted), MediaType.TEXT_PLAIN)
                        .build();
            } catch(Exception e){
                return Response.serverError().entity(e.getMessage())
                        .build();
            }
        }
        else{
            return Response.serverError().entity("Any media and VGIObservation ID must be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for listing description of all connected media files to given VgiObservation
     * URL: /rest/vgi/observation/{obs_vgi_id}/media?user_name=
     * @param obs_vgi_id - ID of master VgiObservation object 
     * @param userName - name of user that owns VgiObservation object
     * @return List of description of all connected VgiMedia objects
     * @throws SQLException
     */
    @Path("{obs_vgi_id}/media")
    @GET
    public Response listVgiMedia(@PathParam("obs_vgi_id") Integer obsId, @QueryParam("user_name") String userName) throws SQLException{
        if(obsId != null && userName != null){
            VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
            List<VgiMedia> media = orUtil.processListVgiMedia(obsId, userName);
            return Response.ok(media)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                    .build();
        }
        else{
            return Response.serverError().entity("VGIObservation ID must be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for getting connected media file
     * URL: /rest/vgi/observation/{obs_vgi_id}/media/{media_id}?user_name=
     * @param obs_vgi_id - ID of master VgiObservation object
     * @param media_id - ID of connected VgiMedia 
     * @param userName - name of user that owns VgiObservation object
     * @return VgiMedia object as output stream
     * @throws SQLException
     */
    @Path("{obs_vgi_id}/media/{media_id}")
    @GET
    public Response getVgiMedia(@PathParam("obs_vgi_id") Integer obsId, @PathParam("media_id") Integer mediaId, @QueryParam("user_name") String userName){
        if(obsId != null && mediaId != null && userName != null){
            try{
                VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
                VgiMedia medium = orUtil.processGetVgiMedia(obsId, mediaId, userName);
                return Response.ok(new ByteArrayInputStream(medium.getObservedMedia()))
                        .header(HttpHeaders.CONTENT_TYPE, medium.getMediaDatatype())
                        //.header("Content-Disposition", "attachment; filename="+media.get(0).internalGetTimeReceivedMilis()+".png")
                        .build();
            } catch (SQLException e){
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        else{
            return Response.serverError().entity("VGIObservation ID and VGIMedia ID must be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for updating connected media file
     * URL: PUT /rest/vgi/observation/{obs_vgi_id}/media/{media_id}?user_name=
     * @param obs_vgi_id - ID of master VgiObservation object
     * @param media_id - ID of connected VgiMedia 
     * @param userName - name of user that owns VgiObservation object
     * @return VgiMedia object as output stream
     * @throws SQLException
     */
    @Path("{obs_vgi_id}/media/{media_id}")
    @PUT
    @Consumes("multipart/form-data; charset=UTF-8")
    public Response updateVgiMedia(
            @PathParam("obs_vgi_id") Integer obsId,
            @PathParam("media_id") Integer mediaId,
            @FormDataParam("media_type") String mediaType,
            @FormDataParam("media") InputStream fileInStream, 
            @FormDataParam("media") FormDataContentDisposition fileDetail,
            @QueryParam("user_name") String userName){
        if(obsId != null && mediaId != null && userName != null && fileInStream != null){
            try{
                VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
                boolean updated = orUtil.processUpdateVgiMedia(obsId, mediaId, fileInStream, userName, mediaType);
                return Response.ok(String.valueOf(updated), MediaType.TEXT_PLAIN)
                        .build();
            } catch(Exception e){
                return Response.serverError().entity(e.getMessage())
                        .build();
            }
        }
        else{
            return Response.serverError().entity("Any media and VGIObservation ID must be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * Service for deleting connected media file
     * URL: /rest/vgi/observation/{obs_vgi_id}/media/{media_id}?user_name=
     * @param obs_vgi_id - ID of master VgiObservation object
     * @param media_id - ID of connected VgiMedia 
     * @param userName - name of user that owns VgiObservation object
     * @return VgiMedia object as output stream
     * @throws SQLException
     */
    @Path("{obs_vgi_id}/media/{media_id}")
    @DELETE
    public Response deleteVgiMedia(@PathParam("obs_vgi_id") Integer obsId, @PathParam("media_id") Integer mediaId, @QueryParam("user_name") String userName){
        if(obsId != null && mediaId != null && userName != null){
            try{
                VgiObservationRestUtil orUtil = new VgiObservationRestUtil();
                boolean isDeleted = orUtil.processDeleteVgiMedia(obsId, mediaId, userName);
                if(isDeleted){
                    return Response.ok().build();
                }
                else{
                    return Response.serverError().entity("VgiMedia was not deleted!")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                            .build();
                }
            } catch (SQLException e){
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        }
        else{
            return Response.serverError().entity("VGIObservation ID and VGIMedia ID must be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
}