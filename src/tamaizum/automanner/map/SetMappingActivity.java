package tamaizum.automanner.map;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tamaizum.automanner.R;
import tamaizum.automanner.datastore.DataStore;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetMappingActivity extends Activity implements GoogleMap.OnMarkerClickListener,
GoogleMap.OnMarkerDragListener{
	private static final String TAG = SetMappingActivity.class.getSimpleName();
	
	public static final String EDIT_MAP_DATA = "edit_map_data";
	public static final String EDIT_ID = "edit_id";
	
    private final static LatLng INITIAL_LOCATION = new LatLng(35.6049, 139.6826);
    private final static float INITIAL_ZOOM_LEVEL = 17;
	
    private final static double DEFAULT_RADIUS = 500; // マナーモードへ移行する範囲(中心からの半径)(m)
    
    private final static int MARKER_NUM = 1; 
    
	MapFragment mf;
	GoogleMap map;
	Marker mMarker; 
	
	MapData editingMapData; // 編集中のmapdata
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_mapping);
		Log.d(TAG, ".onCreate");
		// initialize map 
		mf = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		map = mf.getMap();
		map.setOnMarkerClickListener(this);
		map.setOnMarkerDragListener(this);
		
		Intent intent = getIntent();
		LatLng firstLocation = INITIAL_LOCATION;
		if(intent.hasExtra(EDIT_ID)){
			int id = (Integer) intent.getExtras().get(EDIT_ID);
			editingMapData = getMapData(id); 
			firstLocation = editingMapData.getLatLng();
			 
			EditText editTextView = (EditText) findViewById(R.id.address_form);
			editTextView.setText(editingMapData.getTitle());
			Log.d(TAG, "first location : lat :" + firstLocation.latitude + " lng: " + firstLocation.longitude);
		}
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, INITIAL_ZOOM_LEVEL));
		
		
		// add default marker
		MarkerOptions mo = new MarkerOptions();
		mo.position(map.getCameraPosition().target);
		mo.draggable(true);
		mo.title(Integer.toString(MARKER_NUM));
		mMarker = map.addMarker(mo);

	}
	
	@Override
	public void onResume(){
		super.onResume();

		MapsInitializer.initialize(this);

		// address to map button
		Button validateButton = (Button) findViewById(R.id.set_map);
		validateButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				validateAddressToMap();
			}
		});
		
		
		// register button
		Button okButton = (Button) findViewById(R.id.register);
		okButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				updateContent();
				finish();
			}
		});
	}
	
	/**
	 * フォームに入力されている住所に移動します
	 * @return
	 */
	private LatLng validateAddressToMap(){
		Geocoder gcoder = new Geocoder(this, Locale.getDefault());
		List<Address> lstAddr;
		EditText name = (EditText)findViewById(R.id.address_form);
		LatLng target = null;
		try {
			lstAddr = gcoder.getFromLocationName(name.getText().toString(), 1);
			if (lstAddr != null && lstAddr.size() > 0) {
				// get latlng
				Address addr = lstAddr.get(0);
				double latitude = addr.getLatitude();
				double longitude = addr.getLongitude();
				Toast.makeText(this, "位置\n緯度:" + latitude + "\n経度:" + longitude, Toast.LENGTH_LONG).show();
				// update map
				if(mf != null){
					target = new LatLng(latitude, longitude);
					CameraUpdate cu = CameraUpdateFactory.newLatLng(target);
					map.moveCamera(cu);
					map.clear();
					MarkerOptions mo = new MarkerOptions();
					mo.position(map.getCameraPosition().target);
					mo.draggable(true);
					mo.title(Integer.toString(MARKER_NUM));
					mMarker = map.addMarker(mo);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return target;
	}

	/**
	 * update or insert db using current marker position
	 */
	private void updateContent(){
		if(editingMapData == null || editingMapData.getId() == -1){
			makeNewContent();
		}else{

			EditText name = (EditText)findViewById(R.id.address_form);
			String title = name.getText().toString();
			LatLng target = mMarker.getPosition();
			double latitude = 0;
			double longitude = 0;
			if(target != null){
				latitude = target.latitude;
				longitude = target.longitude;

				DataStore ds = null;
				try{
					ds = new DataStore(this);
					ds.updateMapData(editingMapData.getId(), title, latitude, longitude);
				}finally{
					if(ds !=null){
						ds.close();		
					}
				}
			}
		}
	}
	
	

	/**
	 * insert into MAP table 
	 */
	private void makeNewContent(){
		EditText name = (EditText)findViewById(R.id.address_form);
		String title = name.getText().toString();
		LatLng target = mMarker.getPosition();
		double latitude = 0;
		double longitude = 0;
		if(target != null){
			latitude = target.latitude;
			longitude = target.longitude;

			DataStore ds = null;
			try{
				ds = new DataStore(this);
				ds.insertMapData(title, latitude, longitude, DEFAULT_RADIUS);
			}finally{
				if(ds != null){
					ds.close();	
				}
			}
		}
	}

	/**
	 * get mapping data having id
	 * @param id
	 * @return
	 */
	private MapData getMapData(int id){ 
		MapData data = null;
				
		DataStore ds = null;
		try{
			ds = new DataStore(this);
			data = ds.getMapData(id);
		}finally{
			if(ds != null){
				ds.close();				
			}
		}
		
		return data;
	}

	
	@Override
	public void onMarkerDrag(Marker arg0) { }

	@Override
	public void onMarkerDragEnd(Marker arg0) { }

	@Override
	public void onMarkerDragStart(Marker arg0) { }

	@Override
	public boolean onMarkerClick(Marker arg0) {
		return false;
	}
	
}