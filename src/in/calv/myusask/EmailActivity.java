package in.calv.myusask;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

@SuppressWarnings("deprecation")
public class EmailActivity extends TabActivity {
	Context mContext;
	ProgressDialog dialog;
	ViewFlipper inboxTab;
    ViewFlipper sentTab;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email);
		
		inboxTab = (ViewFlipper)findViewById(R.id.inbox_tab);
		sentTab = (ViewFlipper)findViewById(R.id.sent_tab);
		
		Scraper scraper = Scraper.getInstance();
		
		ListView inboxList = (ListView)findViewById(R.id.inbox_list);
		inboxList.setAdapter(new EmailAdapter(this, scraper.cachedInbox));
		inboxList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mContext = view.getContext();
				
				Scraper scraper = Scraper.getInstance();
				Email email = scraper.cachedInbox.get(position);
				
				if (email.message != null) {
					updateViewEmailDisplay(email, "From", R.id.inbox_subject, R.id.inbox_from, R.id.inbox_date, R.id.inbox_message);
					inboxTab.showNext();
				} else {
					dialog = ProgressDialog.show(EmailActivity.this, "", "Loading...", true);
					new InboxTask().execute(email);
				}
			}
		});
		
		ListView sentList = (ListView)findViewById(R.id.sent_list);
		sentList.setAdapter(new EmailAdapter(this, scraper.cachedSent));
		sentList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mContext = view.getContext();
				
				Scraper scraper = Scraper.getInstance();
				Email email = scraper.cachedSent.get(position);
				
				if (email.message != null) {
					updateViewEmailDisplay(email, "To", R.id.sent_subject, R.id.sent_from, R.id.sent_date, R.id.sent_message);
	        		sentTab.showNext();
				} else {
					dialog = ProgressDialog.show(EmailActivity.this, "", "Loading...", true);
					new SentTask().execute(email);
				}
			}
		});
		
		TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("inbox").setIndicator("Inbox").setContent(R.id.inbox_tab));
        tabHost.addTab(tabHost.newTabSpec("sent").setIndicator("Sent").setContent(R.id.sent_tab));
        
        tabHost.setCurrentTab(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TabHost tabHost = getTabHost();
			
			if (tabHost.getCurrentTab() == 0) {
				if (inboxTab.getDisplayedChild() == 1) {
					inboxTab.showPrevious();
					
					WebView emailWebView = (WebView) findViewById(R.id.inbox_message);
					emailWebView.loadData("", "text/html", "UTF-8");
					
					return true;
				}
			} else {
				if (sentTab.getDisplayedChild() == 1) {
					sentTab.showPrevious();
					
					WebView emailWebView = (WebView) findViewById(R.id.sent_message);
					emailWebView.loadData("", "text/html", "UTF-8");
					
					return true;
				}
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	public void updateViewEmailDisplay(Email email, String otherPersonTitle, int subjectId, int fromId, int dateId, int messageId) {
		if (email.fullFrom == null) {
			email.fullFrom = email.from;
		}
		
		if (email.fullSubject == null) {
			email.fullSubject = email.subject;
		}
		
		TextView emailSubject = (TextView) findViewById(subjectId);
        emailSubject.setText(email.fullSubject);
        
        TextView emailFrom = (TextView) findViewById(fromId);
        emailFrom.setText(otherPersonTitle + ": " + email.fullFrom);
        
        TextView emailDate = (TextView) findViewById(dateId);
        emailDate.setText("Date: " + email.date);
        
        String message = email.message;
        message = message.replace(" <BR>", " ");
        
        if (message.substring(0,4).equals("<BR>")) {
        	message = message.substring(4);
        }
        
        boolean didChange;
		
		do {
			String newMessage = message.replaceAll("  ", " ");
			didChange = false;
			
			if (!message.equals(newMessage)) {
				didChange = true;
			}
			
			message = newMessage;
		} while (didChange);
        
        String formattedMessage = "<html><style>body {margin: 0; background: black; font-size: 14px; line-height: 21px; color: rgb(190, 190, 190)} a {color: rgb(190, 190, 190)} </style>" + message + "</html>";
        
        WebView emailMessage = (WebView) findViewById(messageId);
        emailMessage.loadData(formattedMessage, "text/html", "UTF-8");
	}
	
	private class InboxTask extends AsyncTask<Email, Void, Boolean> {
		Email email;
		
		protected Boolean doInBackground(Email... params) {
			email = params[0];
			Scraper scraper = Scraper.getInstance();
			
			if (scraper.fetchEmailMessage(email, false)) {
				return Boolean.TRUE;
			}
			
			return Boolean.FALSE;
		}
		
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			
        	if (result == Boolean.TRUE) {
        		updateViewEmailDisplay(email, "From", R.id.inbox_subject, R.id.inbox_from, R.id.inbox_date, R.id.inbox_message);
				inboxTab.showNext();
        	} else {
        		Toast toast = Toast.makeText(mContext, "Failed to load message.", Toast.LENGTH_LONG);
    			toast.show();
        	}
        }
	}
	
	private class SentTask extends AsyncTask<Email, Void, Boolean> {
		Email email;
		
		protected Boolean doInBackground(Email... params) {
			email = params[0];
			Scraper scraper = Scraper.getInstance();
			
			if (scraper.fetchEmailMessage(email, true)) {
				return Boolean.TRUE;
			}
			
			return Boolean.FALSE;
		}
		
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			
        	if (result == Boolean.TRUE) {
        		updateViewEmailDisplay(email, "To", R.id.sent_subject, R.id.sent_from, R.id.sent_date, R.id.sent_message);
        		sentTab.showNext();
        	} else {
        		Toast toast = Toast.makeText(mContext, "Failed to load message.", Toast.LENGTH_LONG);
    			toast.show();
        	}
        }
	}
}

