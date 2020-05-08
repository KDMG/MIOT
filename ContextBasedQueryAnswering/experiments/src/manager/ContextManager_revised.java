package manager;

import java.util.ArrayList;
import java.util.HashMap;

import model.Context;
import model.Context_revised;
import model.Properties;
import model.Properties_revised;
import model.Resource;
import model.Sensor;
import model.Sensor_revised;
import utility.Utilities;

public class ContextManager_revised {

	public ContextManager_revised() {};

	//Retrieve a set of sensors matching the given context, together with a rank
	public HashMap<Sensor_revised,Double> match(Context_revised c, ArrayList<Sensor_revised> sensors, Properties_revised props) {
		HashMap<Sensor_revised,Double> pairs=new HashMap<Sensor_revised,Double>();
		double rank;
		
		for(Sensor_revised s: sensors) {
			if(equals(s.getContext(),c)) {
				rank=Utilities.jaccard_revised(props, s.getProperties());
				pairs.put(s,rank);
			}
		}
		return pairs;
	}
	
	//Return the sensors in match with one of the input contexts, together with a rank
	public HashMap<Sensor_revised,Double> matchAll(ArrayList<Context_revised> contexts,ArrayList<Sensor_revised> sensors, Properties_revised props) {
		HashMap<Sensor_revised,Double> pairs=new HashMap<Sensor_revised,Double>(); 	
		for(Context_revised c:contexts) {
			pairs.putAll(match(c,sensors,props));
		}
		return pairs;
				
	}
	
	//return the list of contexts that are identical to the given one
	public ArrayList<Context_revised> IC(Context_revised c, ArrayList<Context_revised> contexts){
		ArrayList<Context_revised> output=new ArrayList<Context_revised>();
		int cont = 0;
		for(Context_revised ci:contexts) {
			if(equals(ci,c)) {
				output.add(ci);
			} // se i due contesti sono uguali aggiungo il contesto all'interno della lista IC
		}
			
		return output;
	}
	
	//return the list of contexts that are included into the given one
	
		public ArrayList<Context_revised> INC(Context_revised c, ArrayList<Context_revised> contexts){
			ArrayList<Context_revised> output=new ArrayList<Context_revised>();
			for(Context_revised ci:contexts)
				if(includes(c,ci)) // se i due contesti sono legati da un legame di inclusione allora lo vado ad inserire all'interno di inc
					output.add(ci);
			return output;
		}
		
		/**
		 * Find the list of contexts that include the given one
		 * @param c
		 * @param contexts
		 * @return
		 */
		public ArrayList<Context_revised> INC_reversed(Context_revised c, ArrayList<Context_revised> contexts){
			ArrayList<Context_revised> output=new ArrayList<Context_revised>();
			for(Context_revised ci:contexts)
				if(includes(ci,c)) 
					output.add(ci);
			return output;
		}
			
		public ArrayList<Context_revised> INC_reversedGivenDistance(ArrayList<Context_revised> startingSet, ArrayList<Context_revised> contexts, int distance){
			ArrayList<Context_revised> output=new ArrayList<Context_revised>();
			for(Context_revised c:startingSet) {
				//cerco i contesti a distanza 1 da c
				for(Context_revised ci:contexts)
					if(includes(ci,c) && getDistance(ci,c)==distance)
						//lo aggiungo solo se non è già presente
						if(!output.contains(ci))
							output.add(ci);
			}
			return output;
		}
		
		
		/**
		 * Finds the context which is immediately higher than the given one
		 * @param c
		 * @param contexts
		 * @return
		 */
		public ArrayList<Context_revised> INC2(Context_revised c, ArrayList<Context_revised> contexts, int max_level){
			//System.out.println(c.getTime().getValue()+" "+c.getPlace().getValue()+" "+c.getGoal().getValue());
			ArrayList<Context_revised> output=new ArrayList<Context_revised>();
			for(Context_revised ci:contexts) {
				//ignore the same context
				if(ci.getTime().equals(c.getTime()) && ci.getPlace().equals(c.getPlace()) && ci.getGoal().equals(c.getGoal()))
					continue;
				//System.out.print("check"+ci.getTime().getValue()+" "+ci.getPlace().getValue()+" "+ci.getGoal().getValue()+"...");
				// se i due contesti sono legati da un legame di inclusione allora lo vado ad inserire all'interno di inc
				//System.out.print("cTime: "+c.getTime().getValue()+"cTime: "+c.getPlace().getValue()+"cTime: "+c.getGoal().getValue()+"  ");
				//System.out.print("ciTime: "+ci.getTime().getValue()+"ciTime: "+ci.getPlace().getValue()+"ciTime: "+ci.getGoal().getValue()+"  ");
				if(isIncludedIn_revised(c,ci,max_level)) {
				//if(includes2(c,ci)){
					output.add(ci);
	
					//System.out.println("true!!");
					//System.out.print("ok");
				}
				//else System.out.println();
				//System.out.println("");
			}
				
			return output;
		}
	
		//return the list of contexts that are included into the given one
		// qui � dove vado ad inserire un contesto dentro alla lista dei compatibili di un'altro
		public ArrayList<Context_revised> CPT(Context_revised c, ArrayList<Context_revised> contexts){
			ArrayList<Context_revised> output=new ArrayList<Context_revised>();
			for(Context_revised ci:contexts)
				if(compatibleWith(ci,c)) // se i due contesti sono legati da un legame di compatible allora aggiungo il contesto all'array cpt
					output.add(ci);
			return output;
		}
				
		
	//Check if 2 contexts are identical
	public boolean equals(Context_revised c1, Context_revised c2) {
		if(c1.getTime().equals(c2.getTime()) && c1.getPlace().equals(c2.getPlace())  && c1.getGoal().equals(c2.getGoal()))
			return true;
		else
			return false;
	}

	public boolean includes(Context_revised c1, Context_revised c2) {
		if(c1.getTime().includes(c2.getTime()) && c1.getPlace().includes(c2.getPlace()) && c1.getGoal().includes(c2.getGoal()))
			return true;
		else
			return false;
	}
	
	public boolean isDirectlyIncludedIn(Context_revised c1, Context_revised c2) {
		if(c2.getTime().includes_nonrecursive(c1.getTime()) && c2.getPlace().includes_nonrecursive(c1.getPlace()) && c2.getGoal().includes_nonrecursive(c1.getGoal()))
			return true;
		else
			return false;
	}
	/**
	 * Verifica se due contesti c1 e c2 sono uno incluso nell'altro fino ad un limite max passato come parametro
	 * @param c1
	 * @param c2
	 * @param max_level
	 * @return
	 */
	public boolean isIncludedIn(Context_revised c1, Context_revised c2, int max_level) {
		if(c2.getTime().includes2(c1.getTime(),0,max_level) && c2.getPlace().includes2(c1.getPlace(),0,max_level) && c2.getGoal().includes2(c1.getGoal(),0,max_level))
			return true;
		else
			return false;
	}
	
	public boolean isIncludedIn_revised(Context_revised smaller, Context_revised larger, int max_level) {
		if(isIncludedIn(smaller,larger,max_level))
			return getDistance(larger, smaller)==max_level;
		else return false;
	}
	
	/**
	 * Returns the distance between two contexts
	 * @param c1
	 * @param c2
	 * @return
	 */
	public int getDistance(Context_revised c1, Context_revised c2) {
		int tot=c1.getTime().distance(c2.getTime(),0)+c1.getPlace().distance(c2.getPlace(),0)+c1.getGoal().distance(c2.getGoal(), 0);
		return tot;
	}
	
	public boolean compatibleWith(Context_revised c1, Context_revised c2) {
		if(c1.getTime().compatible(c2.getTime()) && c1.getPlace().compatible(c2.getPlace()) && c1.getGoal().compatible(c2.getGoal()))
			return true;
		else
			return false;
	}		
	
	//Class PairSensorRank
	public class PairSensorRank {
		Sensor_revised s;
		float rank;
		
		public PairSensorRank(Sensor_revised s, float rank) {
			this.s=s;
			this.rank=rank;
		}
		
	}
	
	public boolean isContextLeaf(Context_revised c) {
		if(c.getGoal().getIncludes().size()==0 && c.getPlace().getIncludes().size()==0 && c.getTime().getIncludes().size()==0)
			return true;
		else 
			return false;
	}
}
