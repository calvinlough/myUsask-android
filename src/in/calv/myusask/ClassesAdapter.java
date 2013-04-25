package in.calv.myusask;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassesAdapter extends BaseAdapter {
	private ArrayList<Class> list;
	private LayoutInflater mInflater;
	
	public ClassesAdapter(Context context, ArrayList<Class> l) {
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
			v = mInflater.inflate(R.layout.classes_list_item, null);
		} else {
			v = convertView;
		}
		
		Class c = list.get(position);
		
		TextView className = (TextView)v.findViewById(R.id.class_name);
		className.setText(c.name);
		
		TextView classNumber = (TextView)v.findViewById(R.id.class_number);
		classNumber.setText(c.courseNumber);
		
		return v;
	}
}
