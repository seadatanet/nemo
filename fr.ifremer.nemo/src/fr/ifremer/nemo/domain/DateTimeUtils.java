/*
 * ============================================================================
 * Nom du fichier : DateTimeUtils.java
 * Application : NEMO version 1.2.20
 * Auteur : Delphine
 * Date de création : 11 janv. 10
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.format.DateTimeFormat;

/**
 * Class DateTimeUtils.java
 * Description: Classe d'outils pour la gestion des dates et heures
 * 
 * @author Delphine
 */
public class DateTimeUtils {

	@SuppressWarnings("nls")
	public static String getDateTime(Station currentStation) throws ParseException, UnsupportedEncodingException {
		StringBuffer value = new StringBuffer();
		String s_date = Formatter.getSingleton().formatDateTime("YYYY-MM-DD", getFullDate(currentStation));
		value.append(s_date);
		// Valeur par défault de l'heure FAE 3663 = -1
		if (!(currentStation.getTime() == -1)) {
			value.append("T");
			String s_time = Formatter.getSingleton().formatDateTime("hh24:mm:ss.sss", getCorrectedTime(currentStation.getTime(), currentStation.getOffsetTU()));
			value.append(s_time);
		}
		return value.toString();
	}

	public static long getFullDate(Station currentStation) {

		long date = currentStation.getDate();
		long time = currentStation.getTime();
		float offsetTU = currentStation.getOffsetTU();
		return getFullDate(date, time, offsetTU);
	}

	public static long getFullDate(long date, long time, float offsetTU) {

		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.clear();
		cal.setTimeInMillis(date);
		// la valeur par defaut de time est -1 et non NaN
		// cf. fr.ifremer.nemo.domain.DateTimeUtils.getDateTime(Station) et
		// fr.ifremer.nemo.domain.Station.Station(String, String[])
		if (!Float.isNaN(time) && time != -1) {
			cal.setTimeInMillis(date + (time));
		} else {
			cal.setTimeInMillis(date);
		}
		return getGMTDate(cal.getTimeInMillis(), offsetTU);
	}

	public static long getCorrectedTime(long time, float offsetTU) {
		if (!Float.isNaN(time) && time != -1) {
			TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
			Calendar cal = Calendar.getInstance(tz);
			cal.clear();
			Calendar cal2 = Calendar.getInstance(tz);
			cal2.clear();
			cal2.setTimeInMillis(new Float(time).longValue());
			cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
			cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
			cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));
			cal.set(Calendar.MILLISECOND, cal2.get(Calendar.MILLISECOND));
			return getGMTDate(cal.getTimeInMillis(), offsetTU);
		}
		return time;
	}

	public static long getGMTDate(long a_Date, float offsetTU) {
		return a_Date + (Float.valueOf(offsetTU).longValue() * 3600 * 1000);
	}

	public static long convertTimestamp(String inputLine) throws ParseException, UnsupportedEncodingException {

		String frgmntFullDate = null;
		String formatFullDate = null;
		DateTimeFormat format = new DateTimeFormat();

		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();

		PropertyModel dateModel = stationDomain.getModel().getProperty(Intern.S_DATE);
		String dFormat = ((DateTimeFormat) dateModel.getFormat()).getFormatString();

		PropertyModel timeModel = stationDomain.getModel().getProperty(Intern.S_TIME);
		String tFormat = ((DateTimeFormat) timeModel.getFormat()).getFormatString();

		if (dFormat.contains("D.d") //$NON-NLS-1$
				&& tFormat.contains("D.d")) { //$NON-NLS-1$
			// convert Chronological Julian Date
			LineFragment fullDate = new LineFragment();
			fullDate.setFirstCharOffset(dateModel.getFragment().getFirstCharOffset());
			fullDate.setLastCharOffset(timeModel.getFragment().getLastCharOffset());
			frgmntFullDate = fullDate.fullRetreive(inputLine);

			String dateFormat = dFormat.substring(0, dFormat.indexOf(".")); //$NON-NLS-1$
			String timeFormat = tFormat.substring(tFormat.indexOf(".")); //$NON-NLS-1$
			formatFullDate = dateFormat + timeFormat;
		} else {
			String frgmntDate = dateModel.getFragment().fullRetreiveInLine(inputLine);
			String frgmntTime = timeModel.getFragment().fullRetreiveInLine(inputLine);

			// FAE 0001229 : Conversion date et heure : s'il manque des Zeros il
			// faut que NEMO les mette
			frgmntDate = frgmntDate.replace(' ', '0');
			frgmntTime = frgmntTime.replace(' ', '0');
			frgmntFullDate = frgmntDate + Global.SPACE + frgmntTime;

			formatFullDate = dFormat + Global.SPACE + tFormat;
		}
		format.setFormatString(formatFullDate);
		format.setRefDate(0);

		return (Long) format.parseObject(frgmntFullDate);
	}

	public static long computeEndTime(Station currentStation) {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.clear();

		// FAE 13195: Calcul de la date de fin d'une série temporelle
		// la dete de fin n'est plus calculer mais lu lorsque c'est possible.
		MediaDomain mediaDomain = Nemo.getInstance().getDomain().getMedia();
		boolean badConvertion = false;
		if (mediaDomain.getModel().getInputType() == E_InputType.TIMESERIES) {
			int lastFileLine = mediaDomain.getStationHeaderNumberLines() + currentStation.getNbRecords() - 1;
			String endLigne;
			String fileName = mediaDomain.getModel().getSourcePath();
			String name = currentStation.getFileName();
			if (!fileName.endsWith(name))
				fileName = fileName + File.separator + name;

			long date;

			// lecture jusqu'a la derniere ligne afin de la recuperer.
			try {
				FileInputStream fs = new FileInputStream(fileName);
				BufferedReader br = new BufferedReader(new InputStreamReader(fs));
				for (int i = 0; i < lastFileLine; ++i)
					br.readLine();
				// lecture de la derniere ligne
				endLigne = br.readLine();
				// recuperation des champs datetime et convertion en long.
				date = convertTimestamp(endLigne);
				cal.setTimeInMillis(date);
				br.close();
				fs.close();
			} catch (Exception e) {
				// convertion impossible du fichier non compatible.
				// donc passage au calcul.
				badConvertion = true;
			}
		}
		if (mediaDomain.getModel().getInputType() != E_InputType.TIMESERIES || badConvertion) {

			long rate = new Float(currentStation.getSamplingRate() * 1000.0f).longValue();
			int size = currentStation.getNbRecords() - 1;
			long date = DateTimeUtils.getFullDate(currentStation) + (size * rate);
			cal.setTimeInMillis(date);

		}
		return cal.getTimeInMillis();
	}

	public static long getCurrentDate(Station currentStation, int indexRecord) {
		long rate = new Float(currentStation.getSamplingRate() * 1000.0f).longValue();
		org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
		parser.addStandardFunctions();
		parser.addStandardConstants();
		parser.addVariable("x", rate); //$NON-NLS-1$
		parser.parseExpression("x"); //$NON-NLS-1$

		Double resultJEP = parser.getValue();
		rate = resultJEP.longValue();
		return getFullDate(currentStation) + (indexRecord * rate);
	}

	public static int getDay(long date) {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public static int getMonth(long date) {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	public static int getYear(long date) {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(date);
		return cal.get(Calendar.YEAR);
	}

	public static double getJulianDay(long date) {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		double hour = cal.get(Calendar.HOUR_OF_DAY);
		double minute = cal.get(Calendar.MINUTE);
		double second = cal.get(Calendar.SECOND);
		int isGregorianCal = 1;
		int A;
		int B;
		int C;
		int D;
		double fraction = day + ((hour + (minute / 60) + (second / 60 / 60)) / 24);

		if (year < 1582)
			isGregorianCal = 0;

		if (month < 3) {
			year = year - 1;
			month = month + 12;
		}

		A = year / 100;
		B = (2 - A + (A / 4)) * isGregorianCal;

		if (year < 0) {
			C = (int) ((365.25 * year) - 0.75);
		} else {
			C = (int) (365.25 * year);
		}

		D = (int) (30.6001 * (month + 1));

		Locale loc = Locale.getDefault();
		Locale.setDefault(Locale.US);
		DecimalFormat twoDForm = new DecimalFormat("#.######");
		Double julianDate = Double.valueOf(twoDForm.format(B + C + D + 1720995 + fraction));
		Locale.setDefault(loc);
		return julianDate;

	}

	public static String getVariantDateTime(long currentDate) throws ParseException, UnsupportedEncodingException {
		StringBuffer value = new StringBuffer();
		String date = Formatter.getSingleton().formatDateTime("YYYY-MM-DD", currentDate); //$NON-NLS-1$
		value.append(date);
		value.append("T"); //$NON-NLS-1$
		String time = Formatter.getSingleton().formatDateTime("hh24:mm:ss.sss", currentDate); //$NON-NLS-1$
		value.append(time);
		return value.toString();
	}

}
