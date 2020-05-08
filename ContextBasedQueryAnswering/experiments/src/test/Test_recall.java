package test;

import java.util.concurrent.ThreadLocalRandom;

import manager.ContextManager_revised;
import manager.SearchFunctions_revised_gennaio2020;
import manager.SearchFunctions_revised_gennaio2020.ResultInformation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import model.Context_revised;
import model.KnowledgeBase_revised;
import model.MIoT_revised;
import model.Properties_revised;
import model.Resource_revised;
import model.Sensor_revised;
import utility.MySQLQuery;

public class Test9_revised_gennaio2020 {

	private static MIoT_revised myNet;
	private static HashMap<Integer,ArrayList<Resource_revised>> model;
	private static KnowledgeBase_revised kb;
	private static int NUM_DIMENSIONS=3;
	private static int NUM_LEVELS=3;
	private static int BRANCHING_FACTOR=5;
	
	private static Integer[] listaNodi= {12119,10851,11716,9743,11099,9734,9268,10171,9322,9524};//,9389,9326};
	
	private static ArrayList<Context_revised> leaves;
	
	private static MySQLQuery db;
	private static SearchFunctions_revised_gennaio2020 sf;
	private static ContextManager_revised cm;
	
	/**
	 * @param args
	 * @throws SQLException
	 */
	
	public static void main(String[] args) throws SQLException {
	
		initializeKB();
		cm=new ContextManager_revised();
		Context_revised[] allContexts=createAllContext(model);
		Context_revised[] subset20=createSubset20(allContexts);
		System.out.println("created subset 20%");
		Context_revised[] subset80=createSubset80(allContexts,subset20);
		System.out.println("created subset 80%");
	
		System.out.println("Starting the test");
		System.out.println("Contesti: "+allContexts.length);
		//for each test net=10 times
		for(int i=0;i<listaNodi.length;i++) {
			sf=new SearchFunctions_revised_gennaio2020();
			ArrayList<SearchFunctions_revised_gennaio2020> inners=new ArrayList<SearchFunctions_revised_gennaio2020>();
			for(int k=0;k<10;k++)
				inners.add(new SearchFunctions_revised_gennaio2020());
			
			//create contexts
			assignContexts(listaNodi[i],subset20,subset80);
			//Create the query
			Resource_revised resourceQuery=findResource("temperature_C");
			Properties_revised propsQuery=new Properties_revised();
			propsQuery.add(resourceQuery);			
			
			int howManyRewritings=0;
			int numConsiderati=0;
			for(int k=0;k<allContexts.length;k++) {
				numConsiderati++;
				
				Context_revised queryContext=allContexts[k];
			
				//::::LAUNCH::::
				HashMap<Sensor_revised,Double> rank=sf.supervised(queryContext,myNet,propsQuery,0);
				
				if(rank.size()==0) {
					sf.includedUpQuery(queryContext, myNet, allContexts, propsQuery, 0, inners);
				}
				
			}
			
			//::::POST-ELABORAZIONE::::
			double avgIdentici=0;
			double percIdentici=0;
			double avgInclusiDown=0;
			double percInclusiDown=0;
			double avgCompatible=0;
			double percCompatible=0;
			if(sf.numExecutionWithAtLeastIdentical!=0) {
				 avgIdentici = (sf.numRetrievedSensors_Identical*1.0/sf.numExecutionWithAtLeastIdentical);
				 //percIdentici=sf.numExecutionWithAtLeastIdentical*1.0/allContexts.length;
				 percIdentici=sf.numExecutionWithAtLeastIdentical*1.0/numConsiderati;
			}
			if(sf.numExecutionWithNoIdenticalButIncluded!=0) {
				avgInclusiDown = (sf.numRetrievedSensors_Included*1.0/sf.numExecutionWithNoIdenticalButIncluded);
				percInclusiDown= sf.numExecutionWithNoIdenticalButIncluded*1.0/numConsiderati;
			}
			if(sf.numExecutionWithNoIdenticalButCompatible!=0) {
				avgCompatible = (sf.numRetrievedSensors_Compatible*1.0/sf.numExecutionWithNoIdenticalButCompatible);
				percCompatible=sf.numExecutionWithNoIdenticalButCompatible*1.0/numConsiderati;
			}
			
				//:::PRINT RESULTS:::
				System.out.print(myNet.size()+",");
				System.out.print(percIdentici+","+percInclusiDown+","+percCompatible+",,");
				System.out.print(sf.numRetrievedSensors_Identical*1.0/numConsiderati+","+sf.numRetrievedSensors_Included*1.0/numConsiderati+","+sf.numRetrievedSensors_Compatible*1.0/numConsiderati+",");
					
				//Stampa i risultati dei rewriting
				for(int j=1;j<=5;j++) {
					Set<Context_revised> contextOutput=inners.get(j).outputDetail.keySet();
					double pidentical=0;
					double pincluded=0;
					double pcompatible=0;

					double percIdenticiInners=inners.get(j).numExecId*1.0/numConsiderati;
					double percIncludedInners=inners.get(j).numExecInc*1.0/numConsiderati;
					double percCompatibleInners=inners.get(j).numExecCpt*1.0/numConsiderati;
					
					for(Context_revised q:contextOutput) {
						ResultInformation ri=inners.get(j).outputDetail.get(q);
						pidentical+=(ri.getIdentical()*1.0/ri.getNumCandidates());
						pincluded+=(ri.getIncluded()*1.0/ri.getNumCandidates());
						pcompatible+=(ri.getCompatible()*1.0/ri.getNumCandidates());
						
					}
					//pidentical=pidentical/allContexts.length;
					pidentical=pidentical/numConsiderati;
					//pincluded=pincluded/allContexts.length;
					pincluded=pincluded/numConsiderati;
					//pcompatible=pcompatible/allContexts.length;
					pcompatible=pcompatible/numConsiderati;
					
					System.out.print((pidentical+pincluded+pcompatible)+","+percIdenticiInners+","+percIncludedInners+","+percCompatibleInners+",,");

				}
			
				System.out.println("");
			}
			db.closeConnection();
	}
	
	private static void assignContexts(int node, Context_revised[] subset20, Context_revised[] subset80) throws SQLException {
		myNet=new MIoT_revised();
		ResultSet result=db.executeQuery("SELECT id,measure from object o,linked l where l.object1="+node+" and l.object2=o.id order by id asc");

		//for each sensor of the net
		int i=0;
		int numSensors=result.getFetchSize();
		//System.out.println("start assigning contexts to sensors");
		while(result.next()) {
			String measure_name=result.getString(2); // prendo la proprieta' del sensore 
			Sensor_revised s=new Sensor_revised();
			
			//Assegno un random context dal subset20 ai primi 80% di sensori, e dal subset80 ai restanti
			Context_revised c;
			if(i<(numSensors*0.8)) 
				c=extractRandomContext(subset20);
			else 
				c=extractRandomContext(subset80);
		
			s.setContext(c);
			Properties_revised p=new Properties_revised();
			for(Resource_revised r:kb.getKnowledgeBaseProperties()) {
				if(r.getValue().equals(measure_name)) {
					p.add(r);
					break;
					}
			}
			s.setProperties(p);
			myNet.addSensor(s);	
			i++;
		}
	}

	 * Returns the 80% of the whole set of contexts
	 * @param allContesti
	 * @param subset20
	 * @return
	 */
	private static Context_revised[] createSubset80(Context_revised[] allContesti, Context_revised[] subset20) {
		//System.out.println("Start createSubset80");
		int perc80=allContesti.length-subset20.length;
		ArrayList<Context_revised> arrayList20 = new ArrayList<Context_revised>(Arrays.asList(subset20));
		ArrayList<Context_revised> arrayListAll = new ArrayList<Context_revised>(Arrays.asList(allContesti));
		
		ArrayList<Context_revised> arrayList80=new ArrayList<Context_revised>();
		System.out.println(arrayListAll.size());
		int j=0;
		for(Context_revised c: arrayListAll) {
				if(!arrayList20.contains(c))
				arrayList80.add(c);
			j++;
		}
		//Create the output array
		Context_revised[] output=new Context_revised[perc80];
		int i=0;
		for(Context_revised c: arrayList80)
			output[i++]=c;
		
		return output;
	}

	/**
	 * Select the 20% of the whole set of contexts
	 * @param allContesti
	 * @return
	 */
	private static Context_revised[] createSubset20(Context_revised[] allContesti) {
		//System.out.println("Start createSubset20");
		int perc20=(int)Math.round(allContesti.length*0.2);
		Context_revised[] output=new Context_revised[perc20];
		//ArrayList<Integer> temp=new ArrayList<Integer>();
		int i=0;
		//prendi i primi 20% di contesti dall'arrayList delle foglie
		while(i<perc20) {
			output[i]=leaves.get(i);
			i++;
		}
		return output;
	}

	private static void initializeKB() {
		db=new MySQLQuery();
		
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
	private static Resource_revised findResource(String value) {
		for(Resource_revised r:kb.getKnowledgeBaseProperties()) {
			if(r.getValue().equals(value))
				return r;
		}
		return null;
	}
}
