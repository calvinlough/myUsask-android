package in.calv.myusask;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class ViewAnnouncementActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.news_item);
        
        String title = getIntent().getStringExtra("in.calv.myusask.AnnouncementTitle");
        TextView newsTitle = (TextView) findViewById(R.id.news_title);
        newsTitle.setText(title);
        
        String messageText = getIntent().getStringExtra("in.calv.myusask.AnnouncementText");
        String formattedMessageText = "<html><style>body {background: black; font-size: 14px; line-height: 21px; color: rgb(210, 210, 210)} a {color: rgb(190, 190, 190)} </style>" + messageText + "</html>";
        
        WebView newsBody = (WebView) findViewById(R.id.news_body);
        newsBody.loadData(formattedMessageText, "text/html", "UTF-8");
    }
}
