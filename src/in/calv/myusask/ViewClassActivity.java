package in.calv.myusask;

import java.util.ArrayList;

import android.app.ListActivity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewClassActivity extends ListActivity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ArrayList<String> courseMembers = getIntent().getStringArrayListExtra("in.calv.myusask.CourseMembers");
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courseMembers) {
        	public boolean isEnabled(int position) {
        		return false; 
            }
        });
        
        ListView lv = getListView();
        lv.setDivider(new ColorDrawable(this.getResources().getColor(R.color.listDivider)));
        lv.setDividerHeight(1);
    }
}
