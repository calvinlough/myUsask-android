package in.calv.myusask;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AnnouncementsActivity extends ListActivity {
	Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Scraper scraper = Scraper.getInstance();
        setListAdapter(new AnnouncementsAdapter(this, scraper.cachedAnnouncements));
        
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.listDivider)));
        lv.setDividerHeight(1);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mContext = view.getContext();
				
				Scraper scraper = Scraper.getInstance();
				Announcement a = scraper.cachedAnnouncements.get(position);
				
				Intent myIntent = new Intent(mContext, ViewAnnouncementActivity.class);
        		myIntent.putExtra("in.calv.myusask.AnnouncementTitle", a.title);
        		myIntent.putExtra("in.calv.myusask.AnnouncementText", a.body);
            	startActivity(myIntent);
			}
		});
    }
}
