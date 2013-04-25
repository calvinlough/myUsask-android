package in.calv.myusask;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SheafAdapter extends BaseAdapter {
	private ArrayList<Sheaf> list;
	private LayoutInflater mInflater;
	
	public SheafAdapter(Context context, ArrayList<Sheaf> l) {
		list = l;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		
		if (convertView == null){
			v = mInflater.inflate(R.layout.news_list_item, null);
		} else {
			v = convertView;
		}
		
		Sheaf sheaf = list.get(position);
		
		TextView storyTitle = (TextView)v.findViewById(R.id.StoryTitle);
		storyTitle.setText(sheaf.title);
		
		TextView storySummary = (TextView)v.findViewById(R.id.StorySummary);
		storySummary.setText(Helpers.extractSummary(sheaf.summary));
		
		return v;
	}
}
