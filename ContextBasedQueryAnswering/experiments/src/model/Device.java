package model;

import java.util.ArrayList;

public class Device {

	ArrayList<Sensor> sensors;
	
	public Device() {
		sensors=new ArrayList<Sensor>();
	}
	
	public ArrayList<Sensor> getSensors() {
		return sensors;
	}
	
	public void addSensor(Sensor s) {
		sensors.add(s);
	}
	
	
}
