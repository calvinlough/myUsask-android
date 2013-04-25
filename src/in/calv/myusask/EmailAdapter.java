package in.calv.myusask;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EmailAdapter extends BaseAdapter {
	private ArrayList<Email> emailList;
	private LayoutInflater mInflater;
	
	public EmailAdapter(Context context, ArrayList<Email> list) {
		emailList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return emailList.size();
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
			v = mInflater.inflate(R.layout.email_item, null);
		} else {
			v = convertView;
		}
		
		Email email = emailList.get(position);
		
		TextView emailFrom = (TextView)v.findViewById(R.id.EmailFrom);
		emailFrom.setText(email.from);
		
		TextView emailDate = (TextView)v.findViewById(R.id.EmailDate);
		emailDate.setText(Helpers.relativeDate(email.date));
		
		TextView emailSubject = (TextView)v.findViewById(R.id.EmailSubject);
		emailSubject.setText(email.subject);
		
		return v;
	}
}
