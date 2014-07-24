package tamaizum.automanner.map;

import com.google.android.gms.maps.model.LatLng;

public class MapData {
	private int id;
	private String title;
	private LatLng latlng;
	
	public MapData(int id,String title, LatLng latlng){
		this.id = id;
		this.title = title;
		this.latlng = latlng;
	}
	
	public int getId(){
		return id;
	}
	
	public String getTitle(){
		return title;
	}
	
	public LatLng getLatLng(){ 
		return latlng;
	}

}
