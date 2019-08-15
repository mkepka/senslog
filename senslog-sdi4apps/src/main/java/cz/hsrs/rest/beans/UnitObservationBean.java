/**
 * 
 */
package cz.hsrs.rest.beans;

import java.util.List;

import cz.hsrs.db.model.composite.SensorObservation;

/**
 * @author mkepka
 *
 */
public class UnitObservationBean {
	
	public long unitId;
	public String timeString;
	public List<SensorObservation> sensors;
	
	public UnitObservationBean() {
	}

	public UnitObservationBean(long unitId, String timeString, List<SensorObservation> sensors) {
		this.unitId = unitId;
		this.timeString = timeString;
		this.sensors = sensors;
	}
}