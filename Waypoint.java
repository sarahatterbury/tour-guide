package tourguide;

public class Waypoint {
	private Annotation annotation;
	private double easting;
	private double northing;
	
	public Waypoint(Annotation annotation, double easting, double northing){
		this.annotation = annotation;
		this.easting= easting;
		this.northing = northing;
	}

	public Annotation getAnnotation() {
		return annotation;
	}
	
	public double getEasting() {
		return easting;
	}

	public double getNorthing() {
		return northing;
	}
}
