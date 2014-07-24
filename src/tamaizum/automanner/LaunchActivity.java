package tamaizum.automanner;

import java.util.ArrayList;
import java.util.List;


import tamaizum.automanner.R;
import tamaizum.automanner.datastore.DataStore;
import tamaizum.automanner.location.LocationReceiver;
import tamaizum.automanner.map.SetMappingActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LaunchActivity extends Activity implements OnClickListener{
	static final String TAG = LaunchActivity.class.getSimpleName();
	
	static final String PREF_KEY = "pref_key";
	static final String LAST_RINGER_MODE = "last_ringer_mode";
	
	SharedPreferences mPref;
	
	
	boolean debugCount = true ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		
		mPref = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
		
		registerGPSReceiver();

		Button change = (Button) findViewById(R.id.add_button);
		change.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startEditingMap(-1);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_launch,
					container, false);
			return rootView;
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		
		refreshListView();
	}
	
	
	/**
	 * edit changing manner-mode content
	 */
	private void editDetails(int id) {
		startEditingMap(id);
	}
	
	private void startEditingMap(int id){
		Intent intent = new Intent(LaunchActivity.this , SetMappingActivity.class);
		if(id > 0){
			intent.putExtra(SetMappingActivity.EDIT_ID, id);
		}
		startActivity(intent);
	}

	/**
	 * refresh listView contents
	 */
	private void refreshListView(){
		List<Content> listContent = new ArrayList<Content>(getListContents());
		
		
		ContentAdapter contentAdapter = new ContentAdapter(this,0,listContent);
		ListView lv = (ListView) findViewById(R.id.listview_listview);
		lv.setAdapter(contentAdapter);
		
		lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Content content = (Content) arg0.getItemAtPosition(arg2);
				editDetails(content.getId());
				
			}});
	
	}
	
	/**
	 * get contents to add listview
	 * @return
	 */
	private List<Content> getListContents(){
		List<Content> contents = new ArrayList<Content>();
		
		DataStore ds = null;
		try{
			ds = new DataStore(this);
			contents = ds.getMapAreaContents();
		}finally{
			if(ds != null){
				ds.close();					
			}
		}
		return contents;
	}
	
	
	/**
	 * register Location Receiver
	 */
	private void registerGPSReceiver(){
		//---use the LocationManager class to obtain locations data---
		LocationManager lm = (LocationManager)
				getSystemService(Context.LOCATION_SERVICE);
		Intent intent = new Intent(this, LocationReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);

		//---request for location updates using GPS---
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				60000,
				100,
				pendingIntent);
		
		Log.d(TAG, "register LocationReceiver ");
	}

	@Override
	public void onClick(View v) {
		// only delete button
		if(v.getId() == R.id.listview_delete){
			LinearLayout parent = (LinearLayout) v.getParent();
			TextView idView = (TextView) parent.findViewById(R.id.listview_number);
			String id = idView.getText().toString();
			deleteContent(Integer.parseInt(id));
		}
	}

	
	/**
	 * delete form db where match id
	 * @param id
	 */
	private void deleteContent(int id){
		DataStore ds = null;
		try{
			ds = new DataStore(this);
			ds.deleteMapData(id);
		}finally{
			if(ds != null){
				ds.close();
			}
		}
		refreshListView();
	}
	
}
