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

public class BulletinsActivity extends ListActivity {
	Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Scraper scraper = Scraper.getInstance();
        setListAdapter(new BulletinsAdapter(this, scraper.cachedBulletins));
        
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.listDivider)));
        lv.setDividerHeight(1);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mContext = view.getContext();
				
				Scraper scraper = Scraper.getInstance();
				Bulletin b = scraper.cachedBulletins.get(position);
				
				Intent myIntent = new Intent(mContext, ViewBulletinActivity.class);
				myIntent.putExtra("in.calv.myusask.BulletinTitle", b.title);
        		myIntent.putExtra("in.calv.myusask.BulletinText", b.body);
            	startActivity(myIntent);
			}
		});
    }
}
