package in.calv.myusask;

public class Email {
	public String msgId;
	public String from;
	public String subject;
	public String date;
	public String message;
	public String fullFrom;
	public String fullSubject;
	
	public Email(String m, String f, String s, String d) {
		msgId = m;
		from = f;
		subject = s;
		date = d;
	}
}
