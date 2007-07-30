package fr.ifremer.nemo.domain;

import fr.ifremer.nemo.util.DateTimeFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class HorodatedValue
 * 
 * Sous-classe de ParameterValue spécifique aux série temporelle. Elle permet
 * d'horodater une mesure.
 * 
 * @author Gerard BUNEL
 */
public class HorodatedValue extends ParameterValue implements Cloneable {
	/**
	 * Date/Heure de la mesure
	 */
	public long _date;

	/**
	 * Flag qualité assoccié à la date de la mesure
	 */
	public String _qc2;

	public HorodatedValue() {
		super();
		_qc2 = "";
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		HorodatedValue copy = null;
		copy = (HorodatedValue) super.clone();
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	public void release() {
		super.release();
	}

	/**
	 * Read accessor (getter) for attribute _date
	 * 
	 * @return long
	 * @see HorodatedValue#_date
	 */
	public long getDate() {
		return _date;
	}

	/**
	 * Write accessor (setter) for attribute _date
	 * 
	 * @param x
	 *            new value for _date
	 * @see HorodatedValue#_date
	 */
	public void setDate(long x) {
		_date = x;
	}

	/**
	 * Write accessor (setter) for attribute _date
	 * 
	 * @param a_String
	 *            new value for _date as String
	 * @see HorodatedValue#_date
	 */
	public void setDate(String a_String) {
		try {
			setDate(Long.parseLong(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Read accessor (getter) for attribute _qc2
	 * 
	 * @return String
	 * @see HorodatedValue#_qc2
	 */
	public String getQc2() {
		return _qc2;
	}

	/**
	 * Write accessor (setter) for attribute _qc2
	 * 
	 * @param x
	 *            new value for _qc2
	 * @see HorodatedValue#_qc2
	 */
	public void setQc2(String x) {
		_qc2 = x;
	}

	/**
	 * 
	 * @return int
	 */
	public int getDay() {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(_date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * @return int
	 */
	public int getMonth() {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(_date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 
	 * @return int
	 */
	public int getYear() {
		TimeZone tz = TimeZone.getTimeZone("GMT");
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(_date);
		return cal.get(Calendar.YEAR);
	}

} /* End of class: HorodatedValue */
