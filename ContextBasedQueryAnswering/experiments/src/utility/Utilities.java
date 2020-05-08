package utility;

import manager.PropertiesManager;
import manager.PropertiesManager_revised;
import model.Properties;
import model.Properties_revised;

public class Utilities {

	final static double alpha=0.8;
	
	public static double jaccard(Properties p1,Properties p2) {
		int num_id=PropertiesManager.getNum_ID(p1,p2);
		int num_cpt=PropertiesManager.getNum_CPT(p1,p2);
		
		double output=(num_id+alpha*num_cpt)/p1.getSize();
		
		return output;
	}
	
	public static double jaccard_revised(Properties_revised p1,Properties_revised p2) {
		int num_id=PropertiesManager_revised.getNum_ID(p1,p2);
		int num_cpt=PropertiesManager_revised.getNum_CPT(p1,p2);
		
		double output=(num_id+alpha*num_cpt)/p1.getSize();
		
		return output;
	}
}
