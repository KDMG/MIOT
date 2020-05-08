package manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import model.Context_revised;
import model.Device;
import model.MIoT_revised;
import model.Properties;
import model.Properties_revised;
import model.Sensor_revised;

public class SearchFunctions_revised_gennaio2020 {

	final double ALPHA_INC = 0.8;
	final double ALPHA_CPT = 0.5;
	final int MAX_LEVEL = 1000000; // + inf
	final int MAX_NUM_REWRITINGS=5;

	public SearchFunctions_revised_gennaio2020 inner;
	public int numRetrievedSensors_Identical = 0;
	public int numRetrievedSensors_Included = 0;
	public int numExecutionWithAtLeastIdentical = 0;
	public int numExecId=0;
	public int numExecInc=0;
	public int numExecCpt=0;
	public int numExecutionWithNoIdenticalButIncluded = 0;
	public int numExecutionWithNoIdenticalButCompatible = 0;
	public int numExecutionIncludedUpSuccess = 0;
	public int numRetrievedSensors_Compatible = 0;
	public int[] numExecutionWithNoIdenticalButIncludedUpByLevel;

	public HashMap<Context_revised, ResultInformation> outputDetail;

	public int numContextsUp = 0;

	/**
	 * Constructor: initialize everything
	 */
	public SearchFunctions_revised_gennaio2020() {
		numRetrievedSensors_Identical = 0;
		numRetrievedSensors_Included = 0;
		numExecutionWithAtLeastIdentical = 0;
		numExecutionWithNoIdenticalButIncluded = 0;
		numExecutionWithNoIdenticalButCompatible = 0;
		numExecutionIncludedUpSuccess = 0;
		numRetrievedSensors_Compatible = 0;
		numContextsUp = 0;
		numExecutionWithNoIdenticalButIncludedUpByLevel = new int[10];
		//Added 23 gennaio 2020
		numExecId=0;
		numExecInc=0;
		numExecCpt=0;
		/*
		 * for(int i=1;i<=10;i++) numExecutionWithNoIdenticalButIncludedUpByLevel[i-1]=
		 * 0;
		 */
		outputDetail = new HashMap<Context_revised, ResultInformation>();
	}

	/**
	 * Supervised algorithm
	 * 
	 * @param c
	 * @param myNet
	 * @return
	 */
	public HashMap<Sensor_revised, Double> supervised(Context_revised c, MIoT_revised myNet, Properties_revised props,
			int level) {

		// Stop now if the level is too high
		if (level > MAX_LEVEL)
			return new HashMap<Sensor_revised, Double>();

		// Otherwise go on!
		HashMap<Sensor_revised, Double> temp;
		ArrayList<Sensor_revised> sensors = myNet.getNet();

		// put in "contexts" all possible contexts available in sensors
		ArrayList<Context_revised> contexts = getAllContexts(sensors);

		ContextManager_revised manager = new ContextManager_revised();
		// IC

		ArrayList<Context_revised> ic_contexts = manager.IC(c, contexts);
		if (ic_contexts.size() > 0) {
			//numExecutionWithAtLeastIdentical++;
			temp = manager.matchAll(ic_contexts, sensors, props);
			if (temp.size() > 0) {
				numExecutionWithAtLeastIdentical++;
				numExecId++;
				numRetrievedSensors_Identical += temp.size();
				return temp;
			}
		}

		// INC
		ArrayList<Context_revised> inc_contexts = manager.INC(c, contexts);
		if (inc_contexts.size() > 0) {
			temp = manager.matchAll(inc_contexts, sensors, props);
			if (temp.size() > 0) {
				numExecutionWithNoIdenticalButIncluded++;
				numExecInc++;
				numRetrievedSensors_Included += temp.size();
				/*
				 * HashMap <Sensor_revised,Double> output=new HashMap<Sensor_revised,Double>();
				 * for(Sensor_revised k:temp.keySet()) { output.put(k, temp.get(k)); }
				 */

				// return output;
				return temp;
			}
		}

		// CPT
		ArrayList<Context_revised> cpt_contexts = manager.CPT(c, contexts);
		if (cpt_contexts.size() > 0) {
			temp = manager.matchAll(cpt_contexts, sensors, props);
			if (temp.size() > 0) {
				numExecutionWithNoIdenticalButCompatible++;
				numExecCpt++;
				numRetrievedSensors_Compatible += temp.size();
				HashMap<Sensor_revised, Double> output = new HashMap<Sensor_revised, Double>();
				for (Sensor_revised k : temp.keySet())
					output.put(k, ALPHA_CPT * temp.get(k));

				return output;
			}
		}

		// if nothing is found, return an empty hashmap
		return new HashMap<Sensor_revised, Double>();
	}

	public void includedUpQuery(Context_revised c, MIoT_revised myNet, Context_revised[] allContexts,
			Properties_revised props, int level, ArrayList<SearchFunctions_revised_gennaio2020> inners) { // INC2: se non trovo
																								// nulla con le ricerche
																								// precedenti, allora
																								// espando verso l'alto
		// inner=new SearchFunctions_revised();

		ContextManager_revised manager = new ContextManager_revised();
		ArrayList<Sensor_revised> sensors = myNet.getNet();

		// put in "contexts" all possible contexts available in sensors
		// NO NO NO: ArrayList<Context_revised> contexts=getAllContexts(sensors);

		// Get all contexts
		ArrayList<Context_revised> contexts = new ArrayList<Context_revised>(Arrays.asList(allContexts));

		HashMap<Sensor_revised, Double> output = new HashMap<Sensor_revised, Double>();

		// inizializzo il set con c
		ArrayList<Context_revised> startingSet = new ArrayList<Context_revised>();
		startingSet.add(c);
		// ciclo fino alla fine % rewritings)
		for (int i = 1; i <= MAX_NUM_REWRITINGS; i++) {
			// find all existing contexts that are higher
			ArrayList<Context_revised> inc2_contexts = manager.INC_reversedGivenDistance(startingSet, contexts, 1);
			//if there is nothing above -> exit
			if (inc2_contexts.size() == 0) 
				break;
			//else proceed and exit
			else {
				int randomNum=0;
				if(inc2_contexts.size()>1)
				//get a random candidate
					randomNum = ThreadLocalRandom.current().nextInt(0, inc2_contexts.size()-1);
				//launch supervised
				output = inners.get(i).supervised(inc2_contexts.get(randomNum), myNet, props, i);
				
				if (output.size() != 0) {
					ResultInformation ri = new ResultInformation();
					ri.setNumCandidates(1);
					ri.setIdentical(inners.get(i).numRetrievedSensors_Identical);
					ri.setIncluded(inners.get(i).numRetrievedSensors_Included);
					ri.setCompatible(inners.get(i).numRetrievedSensors_Compatible);
					inners.get(i).outputDetail.put(c, ri);

					// Reset all parameters
					inners.get(i).numRetrievedSensors_Identical = 0;
					inners.get(i).numRetrievedSensors_Included = 0;
					inners.get(i).numRetrievedSensors_Compatible = 0;
					break;
				}
				else {
					startingSet.clear();
					startingSet.add(inc2_contexts.get(randomNum));
				}
				
			}
		} // end riscritture
	}

	private ArrayList<Context_revised> getAllContexts(ArrayList<Sensor_revised> sensors) {
		ArrayList<Context_revised> myContexts = new ArrayList<Context_revised>();
		for (Sensor_revised s : sensors)
			myContexts.add(s.getContext());
		return myContexts;
	}

	public void precisionEvaluation(Context_revised[] allContexts, int L) {
		ContextManager_revised manager = new ContextManager_revised();

		ArrayList<Double> precisionArray;
		// Get all contexts as arraylist
		ArrayList<Context_revised> contexts = new ArrayList<Context_revised>(Arrays.asList(allContexts));

		// for(int i=0;i<L;i++) {

		precisionArray = new ArrayList<Double>();
		int counter = 0;

		// for(int k=0;k<100;k++) {
		for (Context_revised c : allContexts) {
			// Prendo un contesto a caso
			// int n = (int)(Math.random()*(allContexts.length-1));
			// Context_revised c=allContexts[n];

			double retrieved = -1;
			counter++;
			int level = manager.getDistance(allContexts[0], c); // distanza tra root e il nodo

			// Specific case
			if (level == 0) {
				System.out.println("\t skip");
				continue;
			}

			// Determine the number of relevant nodes
			ArrayList<Context_revised> downs = manager.IC(c, contexts); // estraggo i nodi identici o inclusi nel nodo
																		// attuale
			downs.addAll(manager.INC(c, contexts));
			Set<Context_revised> hs = new HashSet<>();
			hs.addAll(downs);
			downs.clear();
			downs.addAll(hs);
			int relevant = downs.size(); // li conto

			if (level < L)
				// retrieved=allContexts.length; // con L rewriting arrivo o supero il root
				// node, dunque perdo tutta la precision
				continue;
			else {
				ArrayList<Context_revised> ups = manager.INC2(c, contexts, L); // estraggo i nodi genitore
				ArrayList<Context_revised> downNew = new ArrayList<Context_revised>();
				for (Context_revised cc : ups)
					downNew.addAll(manager.INC(cc, contexts)); // inserisco i loro figli in downNew
				int numDown = downNew.size(); // per poi mediarli
				retrieved = (double) (numDown * 1.0 / ups.size());
			}

			double precision = (relevant * 1.0) / retrieved;// computePrecisionValue(avgNumDown,relevant,allContexts.length);
															// // calcolo la precision per il nodo in questione
			precisionArray.add(precision);
			// System.out.println("\t"+precision);
			// double avgPrecision=getAvgPrecision(precisionArray);
		} // end for allContexts
		double avgPrecision = getAvgPrecision(precisionArray);
		System.out.println("FINAL-->" + (L) + "° rewriting: " + avgPrecision);
		// end cycle i
	} // end function

	// Computes the specific precision value for this case
	private double computePrecisionValue(double avgNumDown, int numInc, int length) {
		double k = (length - numInc) * 1.0 / length; // lineare
		// double k=(numInc*numInc)*1.0/(length*length-numInc*numInc); //quadratica
		return -k * avgNumDown / length + k;
		// return (k*length*length)/(avgNumDown*avgNumDown)-k;
	}

	// Computes the average of the precision array
	private double getAvgPrecision(ArrayList<Double> precisionArray) {
		double sum = 0;
		for (double d : precisionArray)
			sum += d;
		return sum / precisionArray.size();
	}

	public class ResultInformation {
		int numCandidates;
		int identical;
		int included;
		int compatible;
		//int numExecutionIdentical;
		//public int getNumExecutionIdentical() {
		//	return numExecutionIdentical;
		//}

		/*public void setNumExecutionIdentical(int numExecutionIdentical) {
			this.numExecutionIdentical = numExecutionIdentical;
		}

		public int getNumExecutionIncluded() {
			return numExecutionIncluded;
		}

		public void setNumExecutionIncluded(int numExecutionIncluded) {
			this.numExecutionIncluded = numExecutionIncluded;
		}

		public int getNumExecutionCompatible() {
			return numExecutionCompatible;
		}

		public void setNumExecutionCompatible(int numExecutionCompatible) {
			this.numExecutionCompatible = numExecutionCompatible;
		}

		int numExecutionIncluded;
		int numExecutionCompatible;
		*/
		public int getNumCandidates() {
			return numCandidates;
		}

		public void setNumCandidates(int numCandidates) {
			this.numCandidates = numCandidates;
		}

		public int getIdentical() {
			return identical;
		}

		public void setIdentical(int identical) {
			this.identical = identical;
		}

		public int getIncluded() {
			return included;
		}

		public void setIncluded(int included) {
			this.included = included;
		}

		public int getCompatible() {
			return compatible;
		}

		public void setCompatible(int compatible) {
			this.compatible = compatible;
		}
	}
}