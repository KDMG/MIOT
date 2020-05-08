package test;

import java.util.concurrent.ThreadLocalRandom;

import manager.ContextManager_revised;
import manager.SearchFunctions_revised;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import model.Context_revised;
import model.KnowledgeBase_revised;
import model.MIoT_revised;
import model.Properties_revised;
import model.Resource_revised;
import model.Sensor_revised;
import utility.MySQLQuery;

public class Test_precision {
	
	private static HashMap<Integer,ArrayList<Resource_revised>> model;
	private static KnowledgeBase_revised kb;
	private static int NUM_DIMENSIONS=3;
	private static int NUM_LEVELS=2;
	private static int BRANCHING_FACTOR=5;
	
	private static ArrayList<Context_revised> leaves;
	
	private static SearchFunctions_revised sf;
	private static ContextManager_revised cm;
	
	public static void main(String[] args) throws SQLException {
	
		initializeKB();
		cm=new ContextManager_revised();
		sf=new SearchFunctions_revised();
		
		Context_revised[] allContexts=createAllContext(model);
		
		System.out.println("Starting the test with NUM_DIMENSION="+NUM_DIMENSIONS+", BRANCHING_FACTOR="+BRANCHING_FACTOR+", LEVEL="+NUM_LEVELS);
		//::::LAUNCH::::
		for(int numRw=1;numRw<12;numRw++) {
			System.out.println("Evaluating for rewriting "+numRw+"...");
			sf.precisionEvaluation(allContexts, numRw);
		}
}
	
	
	private static void initializeKB() {
		
		System.out.println("Initialization...");

		kb=new KnowledgeBase_revised();
		model=kb.create(NUM_DIMENSIONS, NUM_LEVELS, BRANCHING_FACTOR);		
		//model_small=kb.extractSmallerKnowledgeBase(PERC_SELECTED_FROM_MODEL);
		
		System.out.println("Knowledge base created.");	
	}


	/**
	 * Genera un contesto scegliendo in modo random i members per le 3 dimensioni, a partire da un model in input
	 * @return
	 */
	static Context_revised extractRandomContext(Context_revised[] contesti) {
		int randomNum = ThreadLocalRandom.current().nextInt(0, contesti.length-1);
		Context_revised c=contesti[randomNum];
		return c;
	}
	
	static Context_revised[] createAllContext(HashMap<Integer,ArrayList<Resource_revised>> model) {
		//System.out.println("Start createAllContext");
		leaves=new ArrayList<Context_revised>();
		int i =0 ;
		int x =0 ;
		ArrayList<Resource_revised> dim0=model.get(0);
		ArrayList<Resource_revised> dim1=model.get(1);
		ArrayList<Resource_revised> dim2=model.get(2);
		Context_revised[] contesti = new Context_revised[dim0.size()*dim1.size()*dim2.size()];
		while(i<dim0.size()) {
			Resource_revised timeContext=dim0.get(i);
			i++;
			int k =0 ;
			while(k<dim1.size()) {
				Resource_revised placeContext=dim1.get(k);
				k++;
				int j =0 ;
				while(j<dim2.size()) {
					Resource_revised goalContext=dim2.get(j);
					j++;
					contesti[x]=new Context_revised(timeContext,placeContext,goalContext);
					if(cm.isContextLeaf(contesti[x]))
						leaves.add(contesti[x]);
					x++;
				}
			}
		}
		 return contesti;
	}
	
	/*static Context[] createAllLeafContext(HashMap<Integer,ArrayList<Resource>> model) {
		int i =4 ;
		int x =0 ;
		ArrayList<Resource> dim0=model.get(0);
		ArrayList<Resource> dim1=model.get(1);
		ArrayList<Resource> dim2=model.get(2);
		Context[] contesti = new Context[729];
		while(i<13) {
			Resource timeContext=dim0.get(i);
			i++;
			int k =4 ;
			while(k<13) {
				Resource placeContext=dim1.get(k);
				k++;
				int j =4 ;
				while(j<13) {
					Resource goalContext=dim2.get(j);
					j++;
					contesti[x]=new Context(timeContext,placeContext,goalContext);
					x++;
				}
			}
		}
		 return contesti;
	}
	*/
	
	private static Resource_revised findResource(String value) {
		for(Resource_revised r:kb.getKnowledgeBaseProperties()) {
			if(r.getValue().equals(value))
				return r;
		}
		return null;
	}
}
