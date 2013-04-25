package in.calv.myusask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.Html;

public class Helpers {
	public static String extractSummary(String body) {
		String summary = Helpers.decodeHtml(body);
		
		summary = summary.replaceAll("\n", " ");
		
		boolean didChange;
		
		do {
			String newSummary = summary.replaceAll("  ", " ");
			didChange = false;
			
			if (!summary.equals(newSummary)) {
				didChange = true;
			}
			
			summary = newSummary;
		} while (didChange);
		
		return Helpers.betterTrim(summary);
	}
	
	public static String decodeHtml(String input) {
		return Helpers.betterTrim(Html.fromHtml(Html.fromHtml(input).toString()).toString());
	}
	
	public static String betterTrim(String input) {
		
		input = input.trim();
		
		if (input.length() == 0) {
			return input;
		}
		
		boolean didChange;
		
		do {
			String oldInput = input;
			
			char firstChar = input.charAt(0);
			
			if (Character.isWhitespace(firstChar) || firstChar == '\u00A0' || firstChar == '\u2007' || firstChar == '\u202F') {
				input = input.substring(1);
			}
			
			if (input.length() > 0) {
				char lastChar = input.charAt(input.length() - 1);
				
				if (Character.isWhitespace(lastChar) || lastChar == '\u00A0' || lastChar == '\u2007' || lastChar == '\u202F') {
					input = input.substring(0, input.length() - 1);
				}
			}
			
			didChange = false;
			
			if (!input.equals(oldInput)) {
				didChange = true;
			}
		} while (didChange && input.length() > 0);
		
		return input;
	}
	
	public static String relativeDate(String dateString) {
		String dayMonthYear = dateString.substring(0, dateString.indexOf(" "));
		int month = Integer.parseInt(dayMonthYear.substring(0, 2));
		int day = Integer.parseInt(dayMonthYear.substring(3, 5));
		int year = Integer.parseInt(dayMonthYear.substring(6, 8));
		
		Calendar cal = Calendar.getInstance();
		int currentDay = cal.get(Calendar.DAY_OF_MONTH);
		int currentMonth = cal.get(Calendar.MONTH) + 1;
		int currentYear = cal.get(Calendar.YEAR) - 2000;
		
		String processedDate = dayMonthYear;
		
		if (day == currentDay && month == currentMonth && year == currentYear) {
			processedDate = dateString.substring(dateString.indexOf(" ") + 1).toLowerCase();
			
			if (processedDate.startsWith("0")) {
				processedDate = processedDate.substring(1);
			}
		} else if (year == currentYear) {
			Date date = new Date(year + 100, month - 1, day);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");
			processedDate = dateFormat.format(date);
		}
		
		return processedDate;
	}
}
