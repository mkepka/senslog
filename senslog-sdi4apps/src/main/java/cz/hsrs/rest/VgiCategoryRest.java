/**
 * 
 */
package cz.hsrs.rest;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import cz.hsrs.db.model.vgi.VgiCategory;
import cz.hsrs.rest.util.VgiCategoryRestUtil;

/**
 * Class with services managing VGICategory objects
 * URL: /rest/vgi/category
 * @author mkepka
 */
@Path("/vgi/category")
public class VgiCategoryRest {

    /**
     * Default 
     */
    public VgiCategoryRest(){
        super();
    }
    
    /**
     * Service for selecting specific VGICategory object by given ID
     * URL: /rest/vgi/category/{category_id}?user_name=
     * @param categoryId - ID of VGICategory
     * @param userName - name of user
     * @return
     */
    @Path("/{category_id}")
    @GET
    public Response getVgiCategory(@PathParam("category_id") Integer categoryId, @QueryParam("user_name") String userName){
        try{
            if(categoryId != null && userName != null){
                VgiCategoryRestUtil crUtil = new VgiCategoryRestUtil();
                VgiCategory cat = crUtil.processGetVgiCategory(categoryId, userName);
                return Response.ok(cat)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                        .build();
            }
            else{
                return Response.status(Status.BAD_REQUEST)
                        .entity("Parameters both category_id and user_name have to be given!")
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
     * Service for selecting all descendants of VGICategory object by given ID
     * URL: /rest/vgi/category/{category_id}/descendants?user_name=
     * @param categoryId - ID of parent VGICategory
     * @param userName - name of user
     * @return List of VGICategory objects that are descendants of given parent VGICategory
     */
    @Path("/{category_id}/descendants")
    @GET
    public Response getVgiCategoryDescendants(@PathParam("category_id") Integer categoryId, @QueryParam("user_name") String userName){
        try{
            if(categoryId != null && userName != null){
                VgiCategoryRestUtil crUtil = new VgiCategoryRestUtil();
                List<VgiCategory> catList = crUtil.processGetVgiCategoryDescendants(categoryId, userName);
                return Response.ok(catList)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
                        .build();
            }
            else{
                return Response.status(Status.BAD_REQUEST)
                        .entity("Parameters both category_id and user_name have to be given!")
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
     * Service for selecting all VgiCategory objects 
     * URL: /rest/vgi/category/select?user_name=
     * @param userName - name of user
     * @return List of VgiCategory objects as JSON
     */
    @Path("/select")
    @GET
    public Response selectCategories(@QueryParam("user_name") String userName){
        try{
            if(userName != null){
                VgiCategoryRestUtil crUtil = new VgiCategoryRestUtil();
                List<VgiCategory> catList = crUtil.processGetVgiCategories(userName);
                return Response.ok(catList)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+";charset=utf-8")
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
}