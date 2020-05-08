package model;

import java.util.ArrayList;

public class Resource_revised {

	ArrayList<Resource_revised> includes;
	ArrayList<Resource_revised> compatible;
	String value;
	ResourceType type;
	Integer level; 
	
	
	public Resource_revised(String value,ResourceType type) {
		includes=new ArrayList<Resource_revised>(); //hasTheseInstances
		compatible=new ArrayList<Resource_revised>();
		this.value=value;
		this.type=type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value= value;
	}
	
	public ResourceType getType() {
		return type;
	}
	
	public ArrayList<Resource_revised> getIncludes() {
		return includes;
	}
	
	public void addToIncludes(Resource_revised r) {
		if(!includes.contains(r))
			includes.add(r);
	}
	
	public void removeFromIncludes(Resource_revised r) {
		if(includes.contains(r))
			includes.remove(r);
	}
	
	/**
	 * Recursive function to determine if a resource r is included
	 * @param r
	 * @return
	 */
	public boolean includes(Resource_revised r) {
		//System.out.println("QUERY START: distance "+getValue()+" - "+r.getValue()+"="+distance(r, 0));
		//se c'è un collegamento diretto OK
		if(this.value.equals(r.getValue()) ||  includes.contains(r))
			return true;
		else {
			
			//per ogni risorsa inclusa in vedi se r è incluso da in
			
				for(Resource_revised in:includes) {
					if(in.includes(r))
						return true;
				}
			
			
		}
		return false;
	}
	
	public boolean includes_nonrecursive(Resource_revised r) {
		//se c'è un collegamento diretto o se è immediatamente incluso OK
		if(this.value.equals(r.getValue()) ||  includes.contains(r))
			return true;
		return false;
	}
	
	
	
public boolean includes2(Resource_revised r, Integer level,int max_level) {
		
		//se c'è un collegamento diretto OK
		if((this.value.equals(r.getValue()) ||  includes.contains(r))&&(level<max_level))
			return true;
		else {
			level = level+1;
			//per ogni risorsa inclusa in vedi se r è incluso da in
			if(level<=max_level) {
				for(Resource_revised in:includes) {
					if(in.includes2(r,level,max_level))
						return true;
				}
			}
			
		}
		return false;
	}
	
	public ArrayList<Resource_revised> getCompatible() {
		return compatible;
	}
	
	public void addToCompatible(Resource_revised r) {
		if(!compatible.contains(r))
			compatible.add(r);
	}
	
	public void removeFromCompatible(Resource_revised r) {
		if(compatible.contains(r))
			compatible.remove(r);
	}
	
	public boolean compatible(Resource_revised r) {
		return compatible.contains(r);
	}
	
	public boolean equals(Resource_revised r) {
		if(type.equals(r.getType()) && value.equals(r.getValue()))
			return true;
		else
			return false;
	}


public int distance(Resource_revised r, int level) {
		//System.out.println("\t from "+value+" to "+r.getValue());
		if(this.value.equals(r.getValue()))
				return level;
		else {
			//for each included context, check the distance 
			int risultato;
			for(Resource_revised rr: includes) {
				risultato=rr.distance(r,level+1);
				if(risultato>=0)
					return risultato;
			}
		}
		return -1;
}

}