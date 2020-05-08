package model;

import java.util.ArrayList;

public class MIoT_revised {

	private ArrayList<Sensor_revised> myNet;
	private String owner;
	
	public MIoT_revised() {
		myNet=new ArrayList<Sensor_revised>();
	}
	
	public void addSensor(Sensor_revised s) {
		if(!myNet.contains(s))
			myNet.add(s);
	}
	
	public void removeSensor(Sensor_revised s) {
		if(myNet.contains(s))
			myNet.remove(s);
	}
	
	
	public void setOwner(String owner) {
		this.owner=owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public ArrayList<Sensor_revised> getNet(){
		return myNet;
	}
	
	public int size() {
		return myNet.size();
	}
	
}
