package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Properties_revised {

	ArrayList<Resource_revised> properties;
	
	public Properties_revised() {
		properties=new ArrayList<Resource_revised>();
		
	}
	
//	public Properties() {
//		properties=new HashMap<String, Object>();
//		
//	}
	
//	public Set<String> getKeySet(){
//		return properties.keySet();
//	}
	
	
//	public int getSize() {
//		return properties.keySet().size();
//	}
	
	
	public int getSize() {
		return properties.size();
	}
	
//	public void put(String key, Object value) {
//		properties.put(key,value);
//	}
	
	public void add(Resource_revised r) {
		properties.add(r);
	}
	
	public void update(ResourceType type,Resource_revised r) {
		for(Resource_revised i:properties) {
			if(i.getType().equals(type))
				{properties.remove(i);
				properties.add(r);}
		}
	}
	
	public Resource_revised get(String type) {
		for(Resource_revised i:properties) {
			if(i.getType().equals(type))
				return i;
		}
		return null;
	}
	
	public Resource_revised get(int index) {
		return properties.get(index);
	}
}