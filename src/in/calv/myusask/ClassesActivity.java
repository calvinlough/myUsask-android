package in.calv.myusask;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ClassesActivity extends ListActivity {
	Context mContext;
	ProgressDialog dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Scraper scraper = Scraper.getInstance();
        setListAdapter(new ClassesAdapter(this, scraper.cachedClassList));
        
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.listDivider)));
        lv.setDividerHeight(1);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialog = ProgressDialog.show(ClassesActivity.this, "", "Loading...", true);
				
				mContext = view.getContext();
				
				Scraper scraper = Scraper.getInstance();
				Class c = scraper.cachedClassList.get(position);
            	new ClassTask().execute(c);
			}
		});
    }
    
    private class ClassTask extends AsyncTask<Class, Void, Void> {
        protected Void doInBackground(Class... params) {
        	Class c = params[0];
        	Scraper scraper = Scraper.getInstance();
        	
        	ArrayList<String> courseMembers = scraper.fetchClassMembers(c);
        	
        	if (courseMembers != null) {
        		Intent myIntent = new Intent(mContext, ViewClassActivity.class);
        		myIntent.putExtra("in.calv.myusask.CourseMembers", courseMembers);
            	startActivity(myIntent);
        	}
        	
        	dialog.dismiss();
        	
        	return null;
        }
    }
}

