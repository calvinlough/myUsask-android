package in.calv.myusask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeActivity extends Activity {
	Context mContext;
	ProgressDialog dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose);
    	
        Button send = (Button) findViewById(R.id.Send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	EditText to = (EditText) findViewById(R.id.To);
            	EditText subject = (EditText) findViewById(R.id.Subject);
            	EditText message = (EditText) findViewById(R.id.Message);
            	
            	String toString = to.getText().toString();
            	String subjectString = subject.getText().toString();
            	String messageString = message.getText().toString();
            	
            	if (toString.length() > 0 && subjectString.length() > 0 && messageString.length() > 0) {
                	dialog = ProgressDialog.show(ComposeActivity.this, "", "Loading...", true);
                	
                	mContext = view.getContext();
                	new SendTask().execute(toString, subjectString, messageString);
            	} else {
            		Toast toast = Toast.makeText(mContext, "Please enter a value for all fields.", Toast.LENGTH_LONG);
        			toast.show();
            	}
            }
        });
    }
    
    private class SendTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... params) {
        	String toString = params[0];
        	String subjectString = params[1];
        	String messageString = params[2];
        	
        	Scraper scraper = Scraper.getInstance();
        	
        	if (scraper.sendEmail(toString, subjectString, messageString)) {
            	return Boolean.TRUE;
        	}
        	
        	return Boolean.FALSE;
        }
        
        protected void onPostExecute(Boolean result) {
        	dialog.dismiss();
        	
        	if (result == Boolean.TRUE) {
        		Toast toast = Toast.makeText(mContext, "Email sent.", Toast.LENGTH_LONG);
    			toast.show();
        		
	        	Intent myIntent = new Intent(mContext, MenuActivity.class);
            	startActivity(myIntent);
        	} else {
        		Toast toast = Toast.makeText(mContext, "Failed to send email. Please try again later.", Toast.LENGTH_LONG);
    			toast.show();
        	}
        }
    }
}

