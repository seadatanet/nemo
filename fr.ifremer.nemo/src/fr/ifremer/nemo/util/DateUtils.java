package fr.ifremer.nemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Various date utilities
 */
public final class DateUtils {
	private static final TimeZone tzUtc = TimeZone.getTimeZone("UTC");

	/**
	 * @param date any date
	 * @param settings options on how to format the date
	 * @return the formatted date
	 */
	public static String format(java.util.Date date, FormatSettings settings) {
		String datePattern = getDatePattern(settings);
		String timePattern = getTimePattern(settings);
		
		SimpleDateFormat sdfDate = new SimpleDateFormat(datePattern);
		sdfDate.setTimeZone(settings.getTimeZone());
		String formatted = sdfDate.format(date);
		if (!timePattern.isEmpty()) {
			SimpleDateFormat sdfTime = new SimpleDateFormat(timePattern);
			sdfTime.setTimeZone(settings.getTimeZone());
			formatted += settings.getDateTimeSeparator();
			formatted += sdfTime.format(date);
		}
		if (settings.isWithTz()) {
			TimeZone tz = settings.getTimeZone();
			int offset = tz.getOffset(date.getTime());
			if (offset == 0) {
				formatted += "Z";
			} else {
				formatted += offset > 0 ? "+" : "-";
				int tzHours = Math.abs(offset) / 1000 / 3600;
				int tzMins = (Math.abs(offset) - (tzHours * 1000 * 3600)) / 60 / 1000;
				formatted += String.format("%02d", tzHours);
				formatted += settings.timeSeparator;
				formatted += String.format("%02d", tzMins);
			}
		}
		return formatted;
	}

	private static String getDatePattern(FormatSettings settings) {
		return "yyyy" + settings.dateSeparator + "MM" + settings.dateSeparator + "dd";
	}
	
	private static String getSdnDatePattern(FormatSettings settings) {
		return getDatePattern(settings).toUpperCase();
	}
	
	private static String getTimePattern(FormatSettings settings) {
		String timePattern = "";
		if (settings.getPrecision() != TimeUnit.DAYS) {
			switch (settings.getPrecision()) {
			case HOURS:
				timePattern += "HH";
				break;
			case MINUTES:
				timePattern += "HH" + settings.timeSeparator + "mm";
				break;
			case SECONDS:
				timePattern += "HH" + settings.timeSeparator + "mm" + settings.timeSeparator + "ss";
				break;
			case MILLISECONDS:
				timePattern += "HH" + settings.timeSeparator + "mm" + settings.timeSeparator + "ss.SSS";
				break;
			default:
				break;
			}
			
		}
		return timePattern;
	}
	
	private static String getSdnTimePattern(FormatSettings settings) {
		return getTimePattern(settings).toLowerCase();
	}
	
	/**
	 * Get the ISO 8601 date time pattern, given a FormatSettings.
	 * @param settings a FormatSettings object.
	 * @return the ISO 8601 pattern as a String.
	 */
	public static String getDateTimeUtcPattern(FormatSettings settings) {
		return getDatePattern(settings) + settings.getDateTimeSeparator() + getTimePattern(settings);
	}
	
	/**
	 * Get the ISO 8601 date time pattern, given a FormatSettings, using the SeaDataNet syntax.
	 * @param settings a FormatSettings object.
	 * @return the ISO 8601 pattern as a String.
	 */
	public static String getDateTimeUtcSdnPattern(FormatSettings settings) {
		return getSdnDatePattern(settings) + settings.getDateTimeSeparator() + getSdnTimePattern(settings);
	}
	
	private static boolean checkValueAndNext(StringTokenizer st, String token) {
		if (!st.hasMoreTokens()) {
			return false;
		}
		String t = st.nextToken();
		if (!t.equals(token)) {
			throw new RuntimeException("Unexpected: " + t);
		}
		if (!st.hasMoreTokens()) {
			throw new RuntimeException("Incomplete date.");
		}
		return true;
	}

	/**
	 * adapted from
	 * http://www.java2s.com/Code/Java/Data-Type/DateparserfortheISO8601format.htm
	 *
	 * @param iso8601Date date in iso8601 format
	 * @return the corresponding Date object + details on the formatting the
	 * date was encoded in
	 */
	public static ParseDateResult parse(String iso8601Date) throws ParseException {
		FormatSettings iso8601Settings = new FormatSettings();
		// dirty fix : we might to be able to parse dates like "date<space>time"
		// instead of T as separator, which is not standard, so we replace space
		// by T on the fly
		if (iso8601Date.contains(" ")) {
			iso8601Date = iso8601Date.replaceFirst(" ", "T");
			iso8601Settings.setDateTimeSeparator(" ");
		}
		StringTokenizer st = new StringTokenizer(iso8601Date, "-T:.+Z", true);
		if (!st.hasMoreTokens()) {
			return null;
		}
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		calendar.clear();
		iso8601Settings.setPrecision(TimeUnit.DAYS);
		iso8601Settings.setWithTz(false);
		try {
			// Year
			if (st.hasMoreTokens()) {
				int year = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.YEAR, year);
			} else {
				return new ParseDateResult(calendar.getTime(), iso8601Settings);
			}
			// Month
			if (checkValueAndNext(st, "-")) {
				int month = Integer.parseInt(st.nextToken()) - 1;
				calendar.set(Calendar.MONTH, month);
			} else {
				return new ParseDateResult(calendar.getTime(), iso8601Settings);
			}
			// Day
			if (checkValueAndNext(st, "-")) {
				int day = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.DAY_OF_MONTH, day);
			} else {
				return new ParseDateResult(calendar.getTime(), iso8601Settings);
			}
			// Hour
			if (checkValueAndNext(st, "T")) {
				int hour = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.HOUR_OF_DAY, hour);
				iso8601Settings.setPrecision(TimeUnit.HOURS);
			} else {
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return new ParseDateResult(calendar.getTime(), iso8601Settings);
			}
			// Minutes
			if (checkValueAndNext(st, ":")) {
				int minutes = Integer.parseInt(st.nextToken());
				calendar.set(Calendar.MINUTE, minutes);
				iso8601Settings.setPrecision(TimeUnit.MINUTES);
			} else {
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return new ParseDateResult(calendar.getTime(), iso8601Settings);
			}
			if (!st.hasMoreTokens()) {
				return new ParseDateResult(calendar.getTime(), iso8601Settings);
			}
			// Not mandatory now
			// Seconds
			String tok = st.nextToken();
			if (tok.equals(":")) { // seconds
				if (st.hasMoreTokens()) {
					int secondes = Integer.parseInt(st.nextToken());
					calendar.set(Calendar.SECOND, secondes);
					iso8601Settings.setPrecision(TimeUnit.SECONDS);
					if (!st.hasMoreTokens()) {
						return new ParseDateResult(calendar.getTime(), iso8601Settings);
					}
					// decimal fraction of a second
					tok = st.nextToken();
					if (tok.equals(".")) {
						String nt = st.nextToken();
						while (nt.length() < 3) {
							nt += "0";
						}
						if (nt.length() > 3) {
							// check the other part from the decimal fraction to
							// be formed only from digits
							for (int i = 3; i < nt.length(); i++) {
								if (!Character.isDigit(nt.charAt(i))) {
									throw new RuntimeException(
											"Invalid digit in the decimal fraction of a second: " + nt.charAt(i));
								}
							}
						}
						nt = nt.substring(0, 3); // Cut trailing chars..
						int millis = Integer.parseInt(nt);
						calendar.set(Calendar.MILLISECOND, millis);
						iso8601Settings.setPrecision(TimeUnit.MILLISECONDS);
						if (!st.hasMoreTokens()) {
							return new ParseDateResult(calendar.getTime(), iso8601Settings);
						}
						tok = st.nextToken();
					} else {
						calendar.set(Calendar.MILLISECOND, 0);
					}
				} else {
					throw new ParseException("No seconds specified", iso8601Date.indexOf(':'));
				}
			} else {
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
			}
			iso8601Settings.setWithTz(true);
			// Time zone
			if (!tok.equals("Z")) { // UTC
				if (!(tok.equals("+") || tok.equals("-"))) {
					throw new ParseException("only Z, + or - allowed", 0);
				}
				boolean plus = tok.equals("+");
				if (!st.hasMoreTokens()) {
					throw new ParseException("Missing hour field", 0);
				}
				int tzHours = Integer.parseInt(st.nextToken());
				int tzMins;
				if (checkValueAndNext(st, ":")) {
					tzMins = Integer.parseInt(st.nextToken());
				} else {
					throw new ParseException("Missing minute field", 0);
				}
				if (plus) {
					calendar.add(Calendar.HOUR, -tzHours);
					calendar.add(Calendar.MINUTE, -tzMins);
				} else {
					calendar.add(Calendar.HOUR, tzHours);
					calendar.add(Calendar.MINUTE, tzMins);
				}
				if (tzHours != 0 || tzMins != 0) {
					TimeZone tz = TimeZone
							.getTimeZone(String.format("GMT%s%02d:%02d", plus ? "+" : "-", tzHours, tzMins));
					iso8601Settings.setTimeZone(tz);
				}
			} else {
				if (st.hasMoreTokens()) {
					throw new ParseException("Unexpected field at the end of the date field: " + st.nextToken(), 0);
				}
			}
		} catch (NumberFormatException ex) {
			throw new ParseException("[" + ex.getMessage() + "] is not an integer", 0);
		}
		return new ParseDateResult(calendar.getTime(), iso8601Settings);
	}

	public static class FormatSettings {
		private final String dateSeparator = "-";
		private final String timeSeparator = ":";
		private TimeZone timeZone = TimeZone.getTimeZone("UTC");
		private TimeUnit precision = TimeUnit.MILLISECONDS;
		private boolean withTz = false;
		private String dateTimeSeparator = "T";

		@Override
		public String toString() {
			return "Iso8601Settings{" + (withTz ? ("timeZone=" + timeZone.getDisplayName() + ", ") : "") + "precision="
					+ precision + ", withTz=" + withTz + '}';
		}

		public TimeZone getTimeZone() {
			return timeZone;
		}

		public void setTimeZone(TimeZone timeZone) {
			this.timeZone = timeZone;
		}

		public TimeUnit getPrecision() {
			return precision;
		}

		public void setPrecision(TimeUnit precision) {
			this.precision = precision;
		}

		public boolean isWithTz() {
			return withTz;
		}

		public void setWithTz(boolean withTz) {
			this.withTz = withTz;
		}

		public String getDateTimeSeparator() {
			return dateTimeSeparator;
		}

		public void setDateTimeSeparator(String dateTimeSeparator) {
			this.dateTimeSeparator = dateTimeSeparator;
		}
	}

	public static class ParseDateResult {
		private final Date date;
		private final FormatSettings settings;

		private ParseDateResult(Date date, FormatSettings settings) {
			this.date = date;
			this.settings = settings;
		}

		public Date getDate() {
			return date;
		}

		public FormatSettings getSettings() {
			return settings;
		}
	}
}