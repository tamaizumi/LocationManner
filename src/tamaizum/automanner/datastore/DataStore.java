package tamaizum.automanner.datastore;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import tamaizum.automanner.Content;
import tamaizum.automanner.location.EasyLocation;
import tamaizum.automanner.map.MapData;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;

public class DataStore {

	private SQLiteDatabase db;
	
	public DataStore(Context context){
		AutoMannerSQLiteOpenHelper hlpr = new AutoMannerSQLiteOpenHelper(context);
		db = hlpr.getWritableDatabase();
	}
	
	/**
	 * get areas where changing to manner mode 
	 * @return
	 */
	public List<EasyLocation> getMannerArea(){
		List<EasyLocation> mannerAreas = new ArrayList<EasyLocation>();
		SQLiteCursor cursor = (SQLiteCursor) db.rawQuery("SELECT longitude , latitude ,radius FROM MAP WHERE isOn = 1 ;" , null);
		
		if(cursor != null  && cursor.moveToFirst()){
			int rowCount = cursor.getCount();
			for(int i = 0 ; i < rowCount ; i++){
				double longitude = cursor.getDouble(0);
				double latitude = cursor.getDouble(1);
				int radius = cursor.getInt(2);
				mannerAreas.add(new EasyLocation(longitude, latitude, radius));
				cursor.moveToNext();
			}
		}
		
		return mannerAreas;
	}
	
	/**
	 * get listview contents list
	 * @return
	 */
	public List<Content> getMapAreaContents(){
		List<Content> contents = new ArrayList<Content>();
		SQLiteCursor cursor = (SQLiteCursor) db.rawQuery("SELECT _id, title FROM MAP WHERE isOn = 1;", null);
		
		if(cursor != null){
			int rowCount = cursor.getCount();
			cursor.moveToFirst();
			for(int i = 0; i < rowCount ; i++){
				contents.add(new Content(cursor.getInt(0), cursor.getString(1) , Content.MAP_CONTENT));
				cursor.moveToNext();
			}
		}
		return contents;
	}
	
	/**
	 * get map data 
	 * @param id
	 * @return
	 */
	public MapData getMapData(int id){
		MapData mapData = null;
		SQLiteCursor cursor = (SQLiteCursor) db.rawQuery("SELECT _id, title , longitude, latitude FROM MAP WHERE _id = " + id + " ;", null);
		
		if(cursor.moveToFirst()){
			String title = cursor.getString(1);
			double lng = cursor.getDouble(2);
			double lat = cursor.getDouble(3);
			mapData = new MapData(id, title, new LatLng(lat, lng));
		}
		
		return mapData;
	}
	
	
	/**
	 * insert into map
	 * @param title
	 * @param latitude
	 * @param longitude
	 * @param radius
	 */
	public void insertMapData(String title, double latitude, double longitude, double radius){
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("latitude", latitude);
		cv.put("longitude", longitude);
		cv.put("radius", radius);
		cv.put("isOn", true);
		db.insert("MAP", null, cv);
	}
	
	/**
	 * update map
	 * @param id
	 * @param title
	 * @param latitude
	 * @param longitude
	 */
	public void updateMapData(int id, String title, double latitude, double longitude){
		ContentValues cv = new ContentValues();
		cv.put("title", title);
		cv.put("latitude", latitude);
		cv.put("longitude", longitude);
//		cv.put("radius", radius);
		cv.put("isOn", true);
		db.update("MAP", cv, " _id = " + id, null);
	}
	
	/**
	 * delete from map
	 * @param id
	 */
	public void deleteMapData(int id){
		db.delete("MAP", "_id = " + id, null);
	}
	
	public void close(){
		db.close();
	}
}
