package tamaizum.automanner;

import java.util.List;

import tamaizum.automanner.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


public class ContentAdapter extends ArrayAdapter<Content> {
	private LayoutInflater mLayoutInflatter;
	
	LaunchActivity mContext;
	
	public ContentAdapter(Context context, int resource,List<Content> objects){
		super(context, resource, objects);
		
		mLayoutInflatter = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		

		mContext = (LaunchActivity) context;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView , ViewGroup parent){
		Content contentData = (Content) getItem(position);
		
		if(convertView == null){
			convertView = mLayoutInflatter.inflate(R.layout.listview_launch_content,null);
		}
		
		TextView idView = (TextView) convertView.findViewById(R.id.listview_number);
		idView.setText(Integer.toString(contentData.getId()));

		
		TextView titleView = (TextView) convertView.findViewById(R.id.listview_title);
		titleView.setText(contentData.getTitle());
		
		Button deleteButton = (Button) convertView.findViewById(R.id.listview_delete);
		deleteButton.setOnClickListener(mContext);
		deleteButton.setFocusableInTouchMode(false);
		deleteButton.setFocusable(false);
		
		return convertView;
	}

}
