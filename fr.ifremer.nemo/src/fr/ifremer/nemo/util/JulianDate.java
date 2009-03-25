package fr.ifremer.nemo.util;

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
	private static final double _NB_JOURS_EPOCH = 2440587.5;

	private double _jour = 0;

	public JulianDate(int jour, double sec) {
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
}
