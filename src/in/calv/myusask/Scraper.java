package in.calv.myusask;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.text.Html;
import android.util.Log;

@SuppressWarnings("unused")
public final class Scraper {
	static Scraper instance = null;
	DefaultHttpClient httpClient;
	boolean isLoggedIn = false;
	Pattern emailPattern;
	Pattern inboxEmailPattern;
	Pattern sentEmailPattern;
	Pattern classListPattern;
	Pattern classMembersPattern1;
	Pattern classMembersPattern2;
	Pattern announcementsPattern;
	Pattern bulletinsPattern;
	Pattern USSUPattern1;
	Pattern USSUPattern2;
	Pattern sheafPattern1;
	Pattern sheafPattern2;
	
	public String cachedHomePageText;
	public ArrayList<Email> cachedInbox;
	public ArrayList<Email> cachedSent;
	public ArrayList<Class> cachedClassList;
	public ArrayList<Announcement> cachedAnnouncements;
	public ArrayList<Bulletin> cachedBulletins;
	public ArrayList<USSU> cachedUSSU;
	public ArrayList<Sheaf> cachedSheaf;
	
	static final boolean FAKE_NETWORK_DATA = true;
	
	private Scraper() {
		httpClient = new DefaultHttpClient();
		
		emailPattern = Pattern.compile("<input name=\"checkedMsgIds\" value=\"(.*?)\" type=\"checkbox\" />.*?<span id=\"msgfrom_txt\">(.*?)</span>.*?<span id=\"msgsubject_txt\"><a .*?>(.*?)</a>.*?<span id=\"msgdate_txt\">(.*?)&nbsp;</span>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		inboxEmailPattern = Pattern.compile("id=\"from_object\" onMouseover=\"window\\.status=''; return true;\">(.*?)</a>.*?<span class=\"text12\" id=\"subject\">&nbsp;<b>(.*?)</b></span>.*?<td><span class=\"text12\" id=\"msg_txt\">(.*?)</span></td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		sentEmailPattern = Pattern.compile("id=\"to_object\" onMouseover=\"window\\.status=''; return true;\">(.*?)</a>.*?<span class=\"text12\" id=\"subject\">&nbsp;<b>(.*?)</b></span>.*?<td><span class=\"text12\" id=\"msg_txt\">(.*?)</span></td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		classListPattern = Pattern.compile("=([0-9]{5}\\.[0-9]{6})\" onClick=\"\" onMouseOver=\"window.status=''; return true;\">(.*?)</a>.*?<span id=\"sclasssection_txt\">(.*?)</span>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		classMembersPattern1 = Pattern.compile("MemberHome\\.jsp\\?groupID=([0-9]*)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		classMembersPattern2 = Pattern.compile("<span id=\"membername\"><a href=\".*?userID=([a-z0-9]+).*?>(.*?)</a></span>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		announcementsPattern = Pattern.compile("valign=\"top\">Subject:</td>\\s+<td>(.*?)</td>\\s+</tr>.*?<div class=\"uportal-background-content\" style=\"padding:10px; padding-top:2px;\">(.*?)</div>\\s+</div>\\s+</td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		bulletinsPattern = Pattern.compile("<span class=\"uportal-text\">Title:</span></td>\\s+<td align=\"Left\" valign=\"top\"><span class=\"uportal-text\" colspan=\"2\">(.*?)</span></td>\\s+</tr>\\s+</table>\\s+</div>\\s+<div class=\"uportal-text\" style=\"padding: 3px 3px 3px 3px;\">(.*?)</div>\\s+</div>\\s+</td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		USSUPattern1 = Pattern.compile("News and events from the University of Saskatchewan Students' Union</p><div class=\"news-items\"><ul style=\"clear:right;list-style-image:url\\(media/org/jasig/portal/channels/CGenericXSLT/bullet.gif\\);margin-left: 0px;\">(.*?)</ul></div></div></div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		USSUPattern2 = Pattern.compile(	"<a class=\"uportal-channel-subtitle-reversed\" href=\"(.*?)\" target=\"_blank\">(.*?)</a><br>(.*?)<li class=\"uportal-channel-text\">", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		sheafPattern1 = Pattern.compile("The Sheaf - Students' Newspaper</span></div></td></tr><tr><td><div id=\"channel_contentn390\" class=\"channel_content\"><div class=\"uportal-background-content\"><p class=\"uportal-channel-subtitle\"></p><div class=\"news-items\"><ul style=\"clear:right;list-style-image:url\\(media/org/jasig/portal/channels/CGenericXSLT/bullet.gif\\);margin-left: 0px;\">(.*?)</ul></div></div></div>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		sheafPattern2 = Pattern.compile("<a class=\"uportal-channel-subtitle-reversed\" href=\"(.*?)\" target=\"_blank\">(.*?)</a><br>(.*?)<li class=\"uportal-channel-text\">", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	}
	
	public static Scraper getInstance() {
		if (instance == null) {
			instance = new Scraper();
		}
		
		return instance;
	}
	
	public String fetchPage(String url) {
		return fetchPage(url, null);
	}
	
	public String fetchPage(String url, List <NameValuePair> postData) {
		String responseText;
		
		Log.i("paranoid", "fetching " + url);
		
		try {
			HttpUriRequest uriRequest; 
			
			if (postData == null) {
				uriRequest = new HttpGet(url);
			} else {
				uriRequest = new HttpPost(url);
				((HttpPost)uriRequest).setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
			}
			
			uriRequest.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
			uriRequest.setHeader("Accept-Language", "en");
			uriRequest.setHeader("Cache-Control", "max-age=0");
			
			HttpResponse response = httpClient.execute(uriRequest);
			HttpEntity entity = response.getEntity();
			
			responseText = EntityUtils.toString(entity);
			
			Log.i("paranoid", "Cookies:");
	        List<Cookie> cookies = httpClient.getCookieStore().getCookies();
	        if (cookies.isEmpty()) {
	        	Log.i("paranoid", "None");
	        } else {
	            for (int i = 0; i < cookies.size(); i++) {
	            	Log.i("paranoid", cookies.get(i).toString());
	            }
	        }
			
	        Log.i("paranoid", "- - - - - - - - - - - - - - - - - - ");
	        Log.i("paranoid", responseText);
	        Log.i("paranoid", "- - - - - - - - - - - - - - - - - - ");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		if (Helpers.betterTrim(responseText).equals("<html><head></head><body onload=\"window.top.location='/misc/timedout2.html'\"></body></html>")) {
			isLoggedIn = false;
		}
        
		return responseText;
	}
	
	public boolean login(String nsid, String password) {
		if (FAKE_NETWORK_DATA) {
			return true;
		}
		
		fetchPage("https://paws.usask.ca/cp/home/displaylogin");
		
		List <NameValuePair> post = new ArrayList<NameValuePair>();
		post.add(new BasicNameValuePair("user", nsid));
		post.add(new BasicNameValuePair("pass", password));
		String response = fetchPage("https://paws.usask.ca/cp/home/CaptureLogin", post);
		
		if (response.toLowerCase().indexOf("loginok") >= 0) {
			isLoggedIn = true;
			cachedHomePageText = fetchPage("http://paws.usask.ca/cp/home/next");
			cachedInbox = null;
			cachedSent = null;
			cachedClassList = null;
			cachedAnnouncements = null;
			cachedBulletins = null;
			cachedUSSU = null;
			cachedSheaf = null;
		} else {
			isLoggedIn = false;
		}
		
		return isLoggedIn;
	}
	
	public void logout() {
		isLoggedIn = false;
		cachedHomePageText = null;
		cachedInbox = null;
		cachedSent = null;
		cachedClassList = null;
		cachedAnnouncements = null;
		cachedBulletins = null;
		cachedUSSU = null;
		cachedSheaf = null;
		
		if (FAKE_NETWORK_DATA) {
			return;
		}
		
		fetchPage("http://paws.usask.ca/up/Logout?uP_tparam=frm&frm=");
	}
	
	public void addEmailsFromResponseToArrayList(String response, ArrayList<Email> list) {
		Matcher m = emailPattern.matcher(response);
		
		while (m.find()) {
			String msgId = Helpers.betterTrim(m.group(1));
			String from = Helpers.decodeHtml(m.group(2));
			String subject = Helpers.decodeHtml(m.group(3));
			String date = Helpers.betterTrim(m.group(4));
			
			// process the "from" field
			if (from.startsWith("\"")) {
				from = from.substring(1);
				
				if (from.indexOf("\"") >= 0) {
					from = from.substring(0, from.indexOf("\""));
				}
			}
			
			from = Helpers.betterTrim(from);
			
			Email email = new Email(msgId, from, subject, date);
			list.add(email);
		}
	}
	
	public boolean fetchInbox() {
		if (cachedInbox != null) {
			return true;
		}
		
		cachedInbox = new ArrayList<Email>();
		
		if (FAKE_NETWORK_DATA) {
			Email email = new Email("1", "John Smith", "testing", "01/17/12 6:23PM");
			cachedInbox.add(email);
			
			email = new Email("2", "Jane Smith", "more testing", "01/15/12 6:23PM");
			cachedInbox.add(email);
			
			email = new Email("3", "Tyler Smith", "even more testing", "06/10/11 6:23PM");
			cachedInbox.add(email);
			
			return true;
		}
		
		String response = fetchPage("http://paws.usask.ca/cp/email/messageList/accountId/1.$NmailX002eusaskX002eca/folderId/2.$NmailX002eusaskX002eca.$NINBOX0058/folderType/inbx/isStock/true/deletingOrMoving/true");
		
		addEmailsFromResponseToArrayList(response, cachedInbox);
		
		return true;
	}
	
	public boolean fetchSent() {
		if (cachedSent != null) {
			return true;
		}
		
		cachedSent = new ArrayList<Email>();
		
		if (FAKE_NETWORK_DATA) {
			Email email = new Email("1", "John Smith", "Re: testing", "01/17/12 7:05PM");
			cachedSent.add(email);
			
			email = new Email("2", "Jane Smith", "Re: more testing", "01/15/12 7:05PM");
			cachedSent.add(email);
			
			return true;
		}
		
		String response = fetchPage("http://paws.usask.ca/cp/email/messageList/accountId/1.$NmailX002eusaskX002eca/folderId/2.$NmailX002eusaskX002eca.$NSent/folderType/snt/isStock/true");
		
		addEmailsFromResponseToArrayList(response, cachedSent);
		
		return true;
	}
	
	public boolean fetchEmailMessage(Email email, boolean isSentMessage) {
		if (FAKE_NETWORK_DATA) {
			email.message = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
			
			return true;
		}
		
		String response = fetchPage("http://paws.usask.ca/cp/email/message?msgId=" + email.msgId);
		Matcher m;
		
		if (isSentMessage == false) {
			m = inboxEmailPattern.matcher(response);
		} else {
			m = sentEmailPattern.matcher(response);
		}
		
		if (m.find()) {
			email.fullFrom = Helpers.betterTrim(Html.fromHtml(m.group(1)).toString());
			email.fullSubject = Helpers.decodeHtml(m.group(2));
			email.message = Helpers.betterTrim(m.group(3));
			return true;
		}
		
		return false;
	}
	
	public boolean sendEmail(String to, String subject, String message) {
		
		// fix newlines
		message = message.replaceAll("\n", "\r\n");
		
		List <NameValuePair> post = new ArrayList<NameValuePair>();
		post.add(new BasicNameValuePair("to", to));
		post.add(new BasicNameValuePair("cc", ""));
		post.add(new BasicNameValuePair("bcc", ""));
		post.add(new BasicNameValuePair("subject", subject));
		post.add(new BasicNameValuePair("isMessageRichText", "N"));
		post.add(new BasicNameValuePair("body", message));
		post.add(new BasicNameValuePair("body_editor", ""));
		post.add(new BasicNameValuePair("pdsEmailSaveAllSent", "on"));
		post.add(new BasicNameValuePair("sentFolderName", "Sent"));
		post.add(new BasicNameValuePair("msgId", ""));
		post.add(new BasicNameValuePair("removeMsgId", ""));
		post.add(new BasicNameValuePair("draftMsgId", ""));
		post.add(new BasicNameValuePair("msgType", ""));
		post.add(new BasicNameValuePair("grouptools", "false"));
		post.add(new BasicNameValuePair("j_encoding", "UTF-8"));
		fetchPage("http://paws.usask.ca/cp/email/sendMsg", post);
		
		cachedSent = null;
		
		return true;
	}
	
	public boolean fetchCourseList() {
		if (cachedClassList != null) {
			return true;
		}
		
		cachedClassList = new ArrayList<Class>();
		
		if (FAKE_NETWORK_DATA) {
			Class c = new Class("1", "Physics", "PHYS 110");
			cachedClassList.add(c);
			
			c = new Class("1", "Calculus", "MATH 110");
			cachedClassList.add(c);
			
			return true;
		}
		
		String response = fetchPage("http://paws.usask.ca/cp/school/schedule");
		Matcher m = classListPattern.matcher(response);
		
		while (m.find()) {
			String courseId = Helpers.betterTrim(m.group(1));
			String name = Helpers.betterTrim(m.group(2));
			String courseNumber = Helpers.betterTrim(m.group(3));
			
			char thirdLastCharacter = courseNumber.charAt(courseNumber.length() - 3);
			
			// skip labs and tutorials
			if (thirdLastCharacter != 'L' && thirdLastCharacter != 'T') {
				Class c = new Class(courseId, name, courseNumber);
				cachedClassList.add(c);
			}
		}
		
		return true;
	}
	
	public ArrayList<String> fetchClassMembers(Class c) {
		ArrayList<String> classMembers = new ArrayList<String>();
		
		if (FAKE_NETWORK_DATA) {
			classMembers.add("John Smith");
			classMembers.add("Jane Smith");
			classMembers.add("Tyler Smith");
			
			return classMembers;
		}
		
		String response = fetchPage("http://paws.usask.ca/jsp/grouptools/group/GroupRedirect.jsp?courseID=" + c.courseId);
		Matcher m = classMembersPattern1.matcher(response);
		
		if (m.find()) {
			String groupId = Helpers.betterTrim(m.group(1));
			String response2 = fetchPage("http://paws.usask.ca/jsp/grouptools/member/MemberHome.jsp?groupID=" + groupId);
			Matcher m2 = classMembersPattern2.matcher(response2);
			
			while (m2.find()) {
				String name = m2.group(2);
				String firstName = name.substring(name.indexOf(", ") + 2);
				String lastName = name.substring(0, name.indexOf(", "));
				name = firstName + " " + lastName;
				name = Helpers.betterTrim(name);
				classMembers.add(name);
			}
		}
		
		return classMembers;
	}
	
	public boolean fetchAnnouncements() {
		if (cachedAnnouncements != null) {
			return true;
		}
		
		cachedAnnouncements = new ArrayList<Announcement>();
		
		if (FAKE_NETWORK_DATA) {
			Announcement a = new Announcement("important announcement", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
			cachedAnnouncements.add(a);
			
			a = new Announcement("another announcement", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
			cachedAnnouncements.add(a);
			
			return true;
		}
		
		Matcher m = announcementsPattern.matcher(cachedHomePageText);
		
		while (m.find()) {
			String title = Helpers.decodeHtml(m.group(1));
			String body = Helpers.betterTrim(m.group(2));
			
			title = title.replaceAll("\n", " ");
			title = title.replaceAll("  ", " ");
			
			title = title.replaceAll("À", "-");
			body = body.replaceAll("À", "-");
			
			Announcement a = new Announcement(title, body);
			cachedAnnouncements.add(a);
		}
		
		return true;
	}
	
	public boolean fetchBulletins() {
		if (cachedBulletins != null) {
			return true;
		}
		
		cachedBulletins = new ArrayList<Bulletin>();
		
		if (FAKE_NETWORK_DATA) {
			Bulletin b = new Bulletin("important bulletin", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
			cachedBulletins.add(b);
			
			b = new Bulletin("another bulletin", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.");
			cachedBulletins.add(b);
			
			return true;
		}
		
		Matcher m = bulletinsPattern.matcher(cachedHomePageText);
		
		while (m.find()) {
			String title = Helpers.decodeHtml(m.group(1));
			String body = Helpers.betterTrim(m.group(2));
			
			title = title.replaceAll("\n", " ");
			title = title.replaceAll("  ", " ");
			
			Bulletin b = new Bulletin(title, body);
			cachedBulletins.add(b);
		}
		
		return true;
	}
	
	public boolean fetchUSSU() {
		if (cachedUSSU != null) {
			return true;
		}
		
		cachedUSSU = new ArrayList<USSU>();
		
		if (FAKE_NETWORK_DATA) {
			USSU u = new USSU("important ussu news", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "http://google.com");
			cachedUSSU.add(u);
			
			u = new USSU("another ussu", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "http://google.com");
			cachedUSSU.add(u);
			
			return true;
		}
		
		Matcher m = USSUPattern1.matcher(cachedHomePageText);
		
		if (m.find()) {
			String USSUSnippet = Helpers.betterTrim(m.group(1)) + "<li class=\"uportal-channel-text\">";
			Matcher m2 = USSUPattern2.matcher(USSUSnippet);
			
			while (m2.find()) {
				String link = Helpers.betterTrim(m2.group(1));
				String title = Helpers.decodeHtml(m2.group(2));
				String summary = Helpers.betterTrim(m2.group(3));
				
				USSU ussu = new USSU(title, summary, link);
				cachedUSSU.add(ussu);
			}
		}
		
		return true;
	}
	
	public boolean fetchSheaf() {
		if (cachedSheaf!= null) {
			return true;
		}
		
		cachedSheaf = new ArrayList<Sheaf>();
		
		if (FAKE_NETWORK_DATA) {
			Sheaf s = new Sheaf("important sheaf news", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "http://google.com");
			cachedSheaf.add(s);
			
			s = new Sheaf("another sheaf", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "http://google.com");
			cachedSheaf.add(s);
			
			return true;
		}
		
		Matcher m = sheafPattern1.matcher(cachedHomePageText);
		
		if (m.find()) {
			String sheafSnippet = Helpers.betterTrim(m.group(1)) + "<li class=\"uportal-channel-text\">";
			Matcher m2 = sheafPattern2.matcher(sheafSnippet);
			
			while (m2.find()) {
				String link = Helpers.betterTrim(m2.group(1));
				String title = Helpers.decodeHtml(m2.group(2));
				String summary = Helpers.betterTrim(m2.group(3));
				
				Sheaf sheaf = new Sheaf(title, summary, link);
				cachedSheaf.add(sheaf);
			}
		}
		
		return true;
	}
}

