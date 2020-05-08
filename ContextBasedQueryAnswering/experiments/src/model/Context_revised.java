package model;

public final class Context_revised {
	private  Resource_revised time;
	private  Resource_revised place;
	private  Resource_revised goal;
	
	public Context_revised(Resource_revised time, Resource_revised place, Resource_revised goal) {
		this.time=time;
		this.place=place;
		this.goal=goal;
	}

	public Resource_revised getTime() {
		return time;
	}

	public Resource_revised getPlace() {
		return place;
	}

	public Resource_revised getGoal() {
		return goal;
	}
	
	public void setValueTime(String time) {
		this.time.setValue(time);
	}

	public void setValuePlace(String place) {
		this.place.setValue(place);
	}

	public void setValueGoal(String goal) {
		this.goal.setValue(goal);
	}
	
	public void setTime(Resource_revised time) {
		this.time=time;
	}

	public void setPlace(Resource_revised place) {
		this.place=place;
	}

	public void setGoal(Resource_revised goal) {
		this.goal=goal;
	}
	
	public String toString() {
		return this.getTime().getValue()+"_"+this.getPlace().getValue()+"_"+this.getGoal().getValue();
		
	}
	
}
