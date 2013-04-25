package in.calv.myusask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("unused")
public class MyUsaskActivity extends Activity {
	Context mContext;
	ProgressDialog dialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	EditText password = (EditText) findViewById(R.id.Password);
    	password.setOnKeyListener(new OnKeyListener() {
    	    public boolean onKey(View view, int keyCode, KeyEvent event) {
    	        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
    	            submitFormUsingView(view);
    	            return true;
    	        }
    	        return false;
    	    }
    	});
    	
        Button login = (Button) findViewById(R.id.Login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	submitFormUsingView(view);
            }
        });
    }
    
    void submitFormUsingView(View view) {
    	EditText nsid = (EditText) findViewById(R.id.NSID);
    	EditText password = (EditText) findViewById(R.id.Password);
    	
    	String nsidString = nsid.getText().toString();
    	String passwordString = password.getText().toString();
    	
    	if ((nsidString.length() == 6 && passwordString.length() > 0) || Scraper.FAKE_NETWORK_DATA) {
        	dialog = ProgressDialog.show(MyUsaskActivity.this, "", "Loading...", true);
        	
        	mContext = view.getContext();
        	new LoginTask().execute(nsidString, passwordString);
    	} else if (nsidString.length() != 6 && passwordString.length() == 0) {
    		Toast toast = Toast.makeText(view.getContext(), "Please enter a valid NSID and password.", Toast.LENGTH_LONG);
    		toast.show();
    	} else if (nsidString.length() != 6) {
    		Toast toast = Toast.makeText(view.getContext(), "Please enter a valid NSID.", Toast.LENGTH_LONG);
    		toast.show();
    	} else if (passwordString.length() == 0) {
    		Toast toast = Toast.makeText(view.getContext(), "Please enter your password.", Toast.LENGTH_LONG);
    		toast.show();
    	}
    }
    
    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... params) {
        	String nsidString = params[0];
        	String passwordString = params[1];
        	
        	Scraper scraper = Scraper.getInstance();
        	
        	if (scraper.login(nsidString, passwordString)) {
            	return Boolean.TRUE;
        	}
        	
        	return Boolean.FALSE;
        }
        
        protected void onPostExecute(Boolean result) {
        	if (result == Boolean.TRUE) {
	        	Intent myIntent = new Intent(mContext, MenuActivity.class);
            	startActivityForResult(myIntent, 0);
            	
            	dialog.dismiss();
            	
            	/*EditText nsid = (EditText) findViewById(R.id.NSID);
            	EditText password = (EditText) findViewById(R.id.Password);
            	nsid.setText("");
            	nsid.requestFocus();
            	password.setText("");*/
        	} else {
        		dialog.dismiss();
        		
        		Toast toast = Toast.makeText(mContext, "Login failed. Please try again.", Toast.LENGTH_LONG);
    			toast.show();
        	}
        }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	EditText nsid = (EditText) findViewById(R.id.NSID);
    	EditText password = (EditText) findViewById(R.id.Password);
    	nsid.setText("");
    	nsid.requestFocus();
    	password.setText("");
    }
}

