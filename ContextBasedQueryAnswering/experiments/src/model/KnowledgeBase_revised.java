package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class KnowledgeBase_revised {

	private HashMap<Integer,ArrayList<Resource_revised>> model;
	private ArrayList<Resource_revised> knowledgeBase_properties;
	
	public HashMap<Integer,ArrayList<Resource_revised>> create(int num_dimensions, int num_levels, int branching_factor) {
		model=new HashMap<Integer,ArrayList<Resource_revised>>();
		
		ResourceType rt=new ResourceType("measure");
		Random random = new Random();
		knowledgeBase_properties=new ArrayList<Resource_revised>();
		Resource_revised temp=new Resource_revised("temperature",rt);
		Resource_revised temp1=new Resource_revised("temperature_C",rt);
		Resource_revised temp2=new Resource_revised("temperature_F",rt);
		temp1.addToCompatible(temp2);
		temp2.addToCompatible(temp1);
		temp.addToIncludes(temp1);
		temp.addToCompatible(temp2);
		Resource_revised press=new Resource_revised("pressure",rt);
		Resource_revised press1=new Resource_revised("pressure_hPa",rt);
		Resource_revised press2=new Resource_revised("pressure_mmHg",rt);
		press1.addToCompatible(press2);
		press2.addToCompatible(press1);
		press.addToIncludes(press1);
		press.addToIncludes(press2);
		Resource_revised humi=new Resource_revised("humidity",rt);
		Resource_revised humi1=new Resource_revised("humidity_RH",rt);
		humi.addToIncludes(humi1);
		knowledgeBase_properties.add(temp);
		knowledgeBase_properties.add(temp1);
		knowledgeBase_properties.add(temp2);
		knowledgeBase_properties.add(press);
		knowledgeBase_properties.add(press1);
		knowledgeBase_properties.add(press2);
		knowledgeBase_properties.add(humi);
		knowledgeBase_properties.add(humi1);

		//The multidimensonal model
		model=new HashMap<Integer,ArrayList<Resource_revised>>(); 

		//per ogni dimensione
		for(int k=0;k<num_dimensions;k++) {
			ResourceType rt_k=new ResourceType("k");
			//inizializzo il modello per la dimensione k
			ArrayList<Resource_revised> dimensionK=new ArrayList<Resource_revised>();
			model.put(k, dimensionK);
			//creo l'elemento root
			Resource_revised rootElement=new Resource_revised("D"+k+"L0_0",rt_k);
			dimensionK.add(rootElement);
		
			//lista temporanea degli elementi da visitare
			ArrayList<Resource_revised> toConsider=new ArrayList<Resource_revised>();
			toConsider.add(rootElement);
			int currentLevel=1;
			//per ogni livello
			while(currentLevel<num_levels) {
				ArrayList<Resource_revised> copy=(ArrayList<Resource_revised>)toConsider.clone();
				ArrayList<Resource_revised> newList=new ArrayList<Resource_revised>();
				//per ogni elemento da visitare
				int counter=0;
				for(Resource_revised r:copy) {
					//espando il nodo per il branchingfactor, creando un nuovo nodo e legando il precedente a questo
					for(int i=0;i<branching_factor;i++) {
						Resource_revised res=new Resource_revised("D"+k+"L"+currentLevel+"_"+counter,rt);
						r.addToIncludes(res);
						//add to the model
						dimensionK.add(res);
						//add the newList
						newList.add(res);
						counter++;
					}
				}
				currentLevel++;
				for(Resource_revised ri:newList) {
					for(Resource_revised y:newList) {
						if(!(ri.equals(y))) {
							int n= random.nextInt(99)+1;
							//select the 5% of contexts to be compatible with another one
							if(n<6) {
								ri.addToCompatible(y);
							}
						}
					}
				}
				toConsider=newList;
			}
		}
		return model;
}


	public HashMap<Integer, ArrayList<Resource_revised>> extractSmallerKnowledgeBase(double percSelectedFromModel) {
		HashMap<Integer, ArrayList<Resource_revised>> model_small=new HashMap<Integer,ArrayList<Resource_revised>>();
		for(int i=0;i<model.size();i++) {
			ArrayList<Resource_revised> selected=new ArrayList<Resource_revised>();
			ArrayList<Resource_revised> resources=model.get(i);
			while(selected.size()<percSelectedFromModel*resources.size()) {
				//pick a random value
				int randomNum = ThreadLocalRandom.current().nextInt(0, resources.size()-1);
				//add to the selected arraylist
				if(!selected.contains(resources.get(randomNum)))
						selected.add(resources.get(randomNum));
			}
			model_small.put(i, selected);
		}
		return model_small;
	
	}


	public ArrayList<Resource_revised> getKnowledgeBaseProperties() {
		return knowledgeBase_properties;
	}
}
