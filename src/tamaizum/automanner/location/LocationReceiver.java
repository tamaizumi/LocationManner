package tamaizum.automanner.location;

import java.util.ArrayList;
import java.util.List;

import tamaizum.automanner.datastore.DataStore;
import tamaizum.automanner.map.DistanceCalucular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {
	private static final String TAG = LocationReceiver.class.getSimpleName();

	private Context mContext;
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,".onReceive");
		
		this.mContext = context; 
		
		String locationKey = LocationManager.KEY_LOCATION_CHANGED;
		if(intent.hasExtra(locationKey)){
			Location loc = (Location) intent.getExtras().get(locationKey);

			double latitude = loc.getLatitude();
			double longitude = loc.getLongitude();
			
			if(isLocationInner(latitude, longitude)){
				// switch to manner mode
				AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				
			}
		}
	}
	
	/**
	 * latitudeとlongitudeがel内に含まれているか返す関数
	 * @param latitude
	 * @param longitude
	 * @param el
	 * @return
	 */
	private boolean isInArea(double latitude, double longitude, EasyLocation el){
		double distance =DistanceCalucular.calcDistHubeny(latitude, longitude, el.getLatitude(), el.getLongitude());
		return distance < el.getRadius(); 
	}
	
	/**
	 * check latitude and longitude's area is in where switch to manner mode  
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private boolean isLocationInner(double latitude, double longitude){
		boolean isInner = false;
		
		DataStore ds = null;
		List<EasyLocation> areas = new ArrayList<EasyLocation>();
		try{
			ds = new DataStore(mContext);
			areas = ds.getMannerArea();
		}finally{
			if(ds != null){
				ds.close();
			}
		}
		for(EasyLocation row : areas){
			if(isInArea(latitude, longitude, row)){
				isInner = true;
				break;
			}
		}
		return isInner;
	}
}
