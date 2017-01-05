package cz.hsrs.rest.vgi;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import cz.hsrs.db.vgi.util.VgiParams;
import cz.hsrs.rest.beans.VgiObservationRdfBean;
import cz.hsrs.rest.beans.VgiObservationsRdfBean;
import cz.hsrs.rest.util.ExportVgiRestUtil;

/**
 * Class with services for exporting of VGIObservations
 * @author mkepka
 *
 */
@Path("/vgi/export")
public class ExportVgiRest {

    /**
     * Default 
     */
    public ExportVgiRest(){
        super();
    }
    
    @Path("/observation")
    @GET
    public Response getObservationsExport(
            @QueryParam(VgiParams.USER_NAME) String username,
            @QueryParam(VgiParams.FORMAT_NAME) String format,
            @QueryParam(VgiParams.DATASET_ID_NAME) Integer datasetId,
            @QueryParam(VgiParams.CATEGORY_ID_NAME) Integer categoryId,
            @QueryParam(VgiParams.FROM_TIME_NAME) String fromTime,
            @QueryParam(VgiParams.TO_TIME_NAME) String toTime,
            @QueryParam(VgiParams.EXTENT_NAME) String extent,
            @QueryParam(VgiParams.UNIT_ID_NAME) Long unitId,
            @Context HttpServletRequest request){
        if(username != null){
            ExportVgiRestUtil expUtil = new ExportVgiRestUtil();
            try{
                if(format != null && format.equalsIgnoreCase(VgiParams.FORMAT_RDF_XML_NAME)){
                    List<VgiObservationRdfBean> obsList = expUtil.processGetVgiObservationsExport(username, categoryId, datasetId, fromTime, toTime, extent, unitId);
                    
                    return Response.ok(new VgiObservationsRdfBean(obsList))
                            .header(HttpHeaders.CONTENT_TYPE, "application/rdf+xml;charset=utf-8")
                            .header("Content-Disposition", "attachment; filename=SensLog-export-"+new Date().getTime()+".rdf")
                            .build();
                } else{
                    return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).entity("Unsupported export format.")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                            .build();
                }
            } catch(SQLException e){
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } else{
            return Response.serverError().entity("VGIObservation ID has to be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    /**
     * 
     * for testing: http://localhost:8080/rest/vgi/export/observation/646?user_name=tester&format=rdf%2Bxml
     * @param obsId
     * @param format
     * @param username
     * @param request
     * @return
     */
    @Path("/observation/{"+VgiParams.OBS_VGI_ID_NAME+"}")
    @GET
    public Response getObservationExport(
            @PathParam(VgiParams.OBS_VGI_ID_NAME) Integer obsId,
            @QueryParam(VgiParams.FORMAT_NAME) String format,
            @QueryParam(VgiParams.USER_NAME) String username,
            @Context HttpServletRequest request,
            @Context HttpServletResponse response){
        if(obsId != null && username != null){
            ExportVgiRestUtil expUtil = new ExportVgiRestUtil();
            try{
                if(format != null && format.equalsIgnoreCase(VgiParams.FORMAT_RDF_XML_NAME)){
                    List<VgiObservationRdfBean> obsList = expUtil.processGetObservationExport(obsId, username);
                    
                    return Response.ok(new VgiObservationsRdfBean(obsList))
                            .header(HttpHeaders.CONTENT_TYPE, "application/rdf+xml;charset=utf-8")
                            .header("Content-Disposition", "attachment; filename=SensLog-export-"+new Date().getTime()+".rdf")
                            .build();
                } else{
                    return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).entity("Unsupported export format.")
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                            .build();
                }
            } catch(SQLException e){
                return Response.serverError().entity(e.getMessage())
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                        .build();
            }
        } else{
            return Response.serverError().entity("VGIObservation ID has to be given!")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                    .build();
        }
    }
    
    public static class FeedReturnStreamingOutput implements StreamingOutput {
        private Document doc;
        
        public FeedReturnStreamingOutput(Document sourceDoc){
            this.doc = sourceDoc;
        }
        
        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException {
            try {
                // XML to Response
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(this.doc);
                StreamResult result =  new StreamResult(output);
                transformer.transform(source, result);
                output.flush();
                
            } catch (TransformerConfigurationException e) {
                throw new IOException(e.getMessage());
            } catch (TransformerException e) {
                throw new IOException(e.getMessage());
            }
        }
    }
}