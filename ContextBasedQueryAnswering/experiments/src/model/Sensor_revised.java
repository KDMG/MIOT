package model;

public class Sensor_revised {

	Context_revised context;
	Properties_revised properties;
	
	public Sensor_revised() {		
	}
	
	public Sensor_revised(Properties_revised properties) {
		this.properties=properties;
	}
	
	public void setProperties(Properties_revised p) {
		this.properties=p;
	}
	
	public Properties_revised getProperties() {
		return properties;
	}
	
	public Context_revised getContext() {
		return context;
	}
	
	public void setContext(Context_revised c) {
		this.context=c;
	}
	
}
