package in.calv.myusask;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AnnouncementsAdapter extends BaseAdapter {
	private static ArrayList<Announcement> list;
	private LayoutInflater mInflater;
	
	public AnnouncementsAdapter(Context context, ArrayList<Announcement> l) {
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
		
		Announcement announcement = list.get(position);
		
		TextView storyTitle = (TextView)v.findViewById(R.id.StoryTitle);
		storyTitle.setText(announcement.title);
		
		TextView storySummary = (TextView)v.findViewById(R.id.StorySummary);
		storySummary.setText(Helpers.extractSummary(announcement.body));
		
		return v;
	}
}
