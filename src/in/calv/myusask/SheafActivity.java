package in.calv.myusask;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SheafActivity extends ListActivity {
	Context mContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Scraper scraper = Scraper.getInstance();
        setListAdapter(new SheafAdapter(this, scraper.cachedSheaf));
        
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.listDivider)));
        lv.setDividerHeight(1);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mContext = view.getContext();
				
				Scraper scraper = Scraper.getInstance();
				Sheaf sheaf = scraper.cachedSheaf.get(position);
				Uri uri = Uri.parse(sheaf.link);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});
    }
}
