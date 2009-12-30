package fr.ifremer.nemo.domain;

import java.util.Calendar;
import java.util.TimeZone;

import fr.ifremer.nemo.util.format.DateTimeFormat;

/**
 * Class HorodatedValue
 * 
 * Sous-classe de ParameterValue specifique aux serie temporelle. Elle permet
 * d'horodater une mesure.
 * 
 * @author Gerard BUNEL
 */
public class HorodatedValue extends ParameterValue {
	/**
	 * Date/Heure de la mesure
	 */
	public long _date;


	public HorodatedValue() {
		super();
	}
    

	/**
     * {@inheritDoc}
     */
    @Override
    public void reset(){
        super.reset();
        _date = 0;
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
	 * 
	 * @return int
	 */
	public int getDay() {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(_date);
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 
	 * @return int
	 */
	public int getMonth() {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(_date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 
	 * @return int
	 */
	public int getYear() {
		TimeZone tz = TimeZone.getTimeZone(DateTimeFormat.GMT);
		Calendar cal = Calendar.getInstance(tz);
		cal.setTimeInMillis(_date);
		return cal.get(Calendar.YEAR);
	}

} /* End of class: HorodatedValue */
