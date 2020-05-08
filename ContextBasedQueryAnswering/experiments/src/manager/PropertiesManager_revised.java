package manager;

import model.Properties_revised;
import model.Resource_revised;

public class PropertiesManager_revised {

	
	static public int getNum_ID(Properties_revised p1, Properties_revised p2) {
		int count=0;
		for(int i=0;i<p1.getSize();i++) {
			for(int j=0;j<p2.getSize();j++)
			if(equalProperties(p1.get(i),p2.get(j)))
				count++;
		}
		return count;
	}
	
	static public int getNum_CPT(Properties_revised p1, Properties_revised p2) {
		int count=0;
		int size=p1.getSize();
		for(int i=0;i<size;i++) {
			for(int j=0;j<size;j++)
			if(cptProperties(p1.get(i),p2.get(j)))
				count++;
		}
		return count;
	}
	
	private static boolean equalProperties(Resource_revised r1,Resource_revised r2) {
		if(r1.getType().equals(r2.getType()))
			if(r1.getValue().equals(r2.getValue()))
				return true;
		
		return false;		
	}

	private static boolean cptProperties(Resource_revised r1, Resource_revised r2) {
		if(r1.getType().equals(r2.getType()))
			if(r1.compatible(r2))
			return true;
		
		return false;
	}
}
