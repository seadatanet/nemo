package fr.ifremer.nemo.util.format;

import java.util.Calendar;

public class JulianDate {

	// La date Julienne de référence est :
	// 1er Janvier de l'année 4713 avant JC à 00:00.

	/**
	 * 3600 x 24 x 1000
	 */
	private static final long _NB_MS_PAR_JOUR = 86400000l;

	/**
	 * Epoch en DateJulienne represent un delta de 2440953 jours
	 */
	//private static final double _NB_JOURS_EPOCH = 2440587.5;
	private static final double _NB_JOURS_EPOCH = 2440587;
	private double _jour = 0;

	public JulianDate(int jour, double sec) {
		this._jour = jour;
	}

	public JulianDate(double jour, boolean direct) {
		this._jour = jour;
	}

	/**
	 * Construit une DateJulienne e partir d'un nombre de ms ecoulees depuis
	 * EPOCH (typiquement le resultat de {@link Calendar#getTimeInMillis()}).
	 * 
	 * @param msDepuisEpoch
	 *            nombre de ms ecoulees depuis EPOCH
	 */
	public JulianDate(double msDepuisEpoch) {
		this._jour = _NB_JOURS_EPOCH + (msDepuisEpoch / _NB_MS_PAR_JOUR);
	}

	public double getJour() {
		return _jour;
	}

	public void setJour(double _jour) {
		this._jour = _jour;
	}

	/**
	 * Calculate calendar date for Julian date field _jour
	 * @param cal : Calendar
	 * @return Calendar cal updated
	 */
	public Calendar toCalendarDate(Calendar cal) {
		Double jd2 = new Double(_jour);
		long I = jd2.longValue();
		double F = jd2.doubleValue() - I;
		long A = 0;
		long B = 0;

		if (I > 2299160) {
			Double a1 = new Double((I - 1867216.25) / 36524.25);
			A = a1.longValue();
			Double a3 = new Double(A / 4.0);
			B = I + 1 + A - a3.longValue();

			double C = (double) B + 1524;
			Double d1 = new Double((C - 122.1) / 365.25);

			long D = d1.longValue();
			Double e1 = new Double(365.25 * D);

			long E = e1.longValue();
			Double g1 = new Double((C - E) / 30.6001);

			long G = g1.longValue();
			Double h = new Double(G * 30.6001);

			long da = (long) C - E - h.longValue();
			int day = new Integer((int) da);

			int month, year;

			if (G < 14L) {
				month = new Integer((int) (G - 2L));
			} else {
				month = new Integer((int) (G - 14L));
			}

			if (month > 1) {
				year = new Integer((int) (D - 4716L));
			} else {
				year = new Integer((int) (D - 4715L));
			}

			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month);
			cal.set(Calendar.DAY_OF_MONTH, day);
		}

		// Calculate fractional part as hours, minutes, and seconds
		Double dhr = new Double(24.0 * F);
		int hour = new Integer(dhr.intValue());

		Double dmin = new Double((dhr.doubleValue() - dhr.longValue()) * 60.0);
		int minute = new Integer(dmin.intValue());

		Double dsec = new Double((dmin.doubleValue() - dmin.longValue()) * 60.0);
		int second = new Integer(dsec.intValue());

		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);

		return cal;
	}

}
