package tamaizum.automanner.location;

public class EasyLocation {

	private double longitude;
	private double latitude;
	private int radius;
	
	public EasyLocation(double longitude, double latitude, int radius){
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius = radius;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public int getRadius(){
		return radius;
	}
}
