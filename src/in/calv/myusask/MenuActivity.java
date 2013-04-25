package in.calv.myusask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuActivity extends Activity {
	Context mContext;
	GridView menu;
	ProgressDialog dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        menu = (GridView)findViewById(R.id.menu);
        menu.setAdapter(new MenuAdapter());
        menu.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }
    
    public class MenuAdapter extends BaseAdapter {
    	Integer[] menuIcons = {R.drawable.email, R.drawable.compose, R.drawable.classes, R.drawable.announcements, R.drawable.bulletins, R.drawable.ussu, R.drawable.sheaf, R.drawable.logout};
    	String[] menuTitles = {"Email", "Compose", "Classes", "Announcements", "Bulletins", "USSU", "Sheaf", "Logout"};
    	
    	@Override
    	public int getCount() {
    		return menuIcons.length;
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
			final int pos = position;
			
			if (convertView == null){
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.menu_item, null);
			} else {
				v = convertView;
			}
			
			ImageView iv = (ImageView)v.findViewById(R.id.IconImage);
			iv.setImageResource(menuIcons[pos]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(8, 8, 8, 8);

			TextView tv = (TextView)v.findViewById(R.id.IconText);
			tv.setText(menuTitles[pos]);
			
			if (pos == 0) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Scraper scraper = Scraper.getInstance();
		            	mContext = view.getContext();
		            	
		            	if (scraper.cachedInbox != null && scraper.cachedSent != null) {
		            		Intent myIntent = new Intent(mContext, EmailActivity.class);
		                	startActivity(myIntent);
		            	} else {
		            		dialog = ProgressDialog.show(MenuActivity.this, "", "Loading...", true);
		            		new EmailTask().execute();
		            	}
		            }
		        });
			} else if (pos == 1) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Intent myIntent = new Intent(view.getContext(), ComposeActivity.class);
		            	startActivity(myIntent);
		            }
		        });
			} else if (pos == 2) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Scraper scraper = Scraper.getInstance();
		            	mContext = view.getContext();
		            	
		            	if (scraper.cachedClassList != null) {
		            		Intent myIntent = new Intent(mContext, ClassesActivity.class);
		                	startActivity(myIntent);
		            	} else {
		            		dialog = ProgressDialog.show(MenuActivity.this, "", "Loading...", true);
			            	new ClassesTask().execute();
		            	}
		            }
		        });
			} else if (pos == 3) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Scraper scraper = Scraper.getInstance();
		            	scraper.fetchAnnouncements();
		            	Intent myIntent = new Intent(view.getContext(), AnnouncementsActivity.class);
		            	startActivity(myIntent);
		            }
		        });
			} else if (pos == 4) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Scraper scraper = Scraper.getInstance();
		            	scraper.fetchBulletins();
		            	Intent myIntent = new Intent(view.getContext(), BulletinsActivity.class);
		            	startActivity(myIntent);
		            }
		        });
			} else if (pos == 5) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Scraper scraper = Scraper.getInstance();
		            	scraper.fetchUSSU();
		            	Intent myIntent = new Intent(view.getContext(), USSUActivity.class);
		            	startActivity(myIntent);
		            }
		        });
			} else if (pos == 6) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	Scraper scraper = Scraper.getInstance();
		            	scraper.fetchSheaf();
		            	Intent myIntent = new Intent(view.getContext(), SheafActivity.class);
		            	startActivity(myIntent);
		            }
		        });
			} else if (pos == 7) {
				v.setOnClickListener(new View.OnClickListener() {
		            public void onClick(View view) {
		            	mContext = view.getContext();
		            	dialog = ProgressDialog.show(MenuActivity.this, "", "Loading...", true);
		            	new LogoutTask().execute();
		            }
		        });
			}
			
			return v;
		}
    }
    
    private class EmailTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
        	Scraper scraper = Scraper.getInstance();
        	
        	if (scraper.fetchInbox() && scraper.fetchSent()) {
        		Intent myIntent = new Intent(mContext, EmailActivity.class);
            	startActivity(myIntent);
        	}
        	
        	dialog.dismiss();
        	
        	return null;
        }
    }
    
    private class ClassesTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
        	Scraper scraper = Scraper.getInstance();
        	
        	if (scraper.fetchCourseList()) {
        		Intent myIntent = new Intent(mContext, ClassesActivity.class);
            	startActivity(myIntent);
        	}
        	
        	dialog.dismiss();
        	
        	return null;
        }
    }
    
    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
        	Scraper scraper = Scraper.getInstance();
        	scraper.logout();
        	
        	return null;
        }
        
        protected void onPostExecute(Void result) {
			dialog.dismiss();
			finish();
        }
    }
}
