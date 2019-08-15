/**
 * 
 */
package cz.hsrs.rest.provider;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cz.hsrs.db.model.composite.SensorObservation;
import cz.hsrs.db.vgi.util.VgiParams;
import cz.hsrs.main.ApplicationParams;
import cz.hsrs.rest.beans.UnitObservationBean;

/**
 * @author mkepka
 *
 */
@Path("/observation")
public class ObservationRest {

	public ObservationRest() {
		super();
	}
	
	@GET
	public Response getObservations(
			@QueryParam (VgiParams.UNIT_ID_NAME) long unitId,
			@QueryParam (VgiParams.FROM_TIME_NAME) String fromTime,
			@QueryParam (VgiParams.TO_TIME_NAME) String toTime) {
		
		
		List<UnitObservationBean> unitObs = new LinkedList<UnitObservationBean>();
		SensorObservation obs1 = new SensorObservation(1, "Temperature", 25.1);
		SensorObservation obs2 = new SensorObservation(2, "Humidity", 75);
		List<SensorObservation> obsList = new LinkedList<SensorObservation>();
		obsList.add(obs1);
		obsList.add(obs2);
		UnitObservationBean obsBean = new UnitObservationBean(unitId,
				ApplicationParams.dateFormatDeciSecondWtimeZone.format(new Date()),
				obsList);
		unitObs.add(obsBean);
		
		return Response.ok().entity(unitObs)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.build();
	}
}