/**
 * 
 */
package cz.hsrs.db.model.composite;

/**
 * @author mkepka
 *
 */
public class SensorObservation {
	
	public int sensorId;
	public String sensor_name;
	public double observedValue;
	
	public SensorObservation() {
	}
	
	public SensorObservation(int sensorId, String sensor_name, double observedValue) {
		this.sensorId = sensorId;
		this.sensor_name = sensor_name;
		this.observedValue = observedValue;
	}

}