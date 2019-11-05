package tourguide;

import java.util.ArrayList;

public class Tour {
   
    private String id;
	private String title;
	private Annotation annotation;
	private ArrayList<Waypoint> waypointList = new ArrayList<Waypoint>();
	private ArrayList<Leg> legList = new ArrayList<Leg>();
	
	public Tour(String id, String title, Annotation annotation) {
	    this.id = id;
	    this.title = title;
	    this.annotation = annotation;
    }
	 
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	public ArrayList<Waypoint> getWaypointList() {
		return waypointList;
	}

	public ArrayList<Leg> getLegList() {
		return legList;
	}
}

