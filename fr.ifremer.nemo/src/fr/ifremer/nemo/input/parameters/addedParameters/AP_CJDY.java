/* ============================================================================
 * Nom du fichier	: AP_date.java
 * Auteur			: sbregent
 * Date				: 26 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: description du paramètre CJDY
 * 
 * @author sbregent
 * 
 */
public class AP_CJDY extends AddedParam {

	public AP_CJDY() {
		super();
		_code = "CJDY";
		flag = Nemo.getInstance().getConfig().getDefaultFlag();
		_format = "#0.000000";
		_fullName = "Chronological Julian Date";
		_unit = "days";
		cible = Conversion.ODV;
		isMesured = false;
	}

	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException {
		// 7129: locale US
		Locale loc = Locale.getDefault();
		Locale.setDefault(Locale.US);

		//JulianDate jDate = new JulianDate(currentDate);
		String format = getFormat();
		Double jour = DateTimeUtils.getJulianDay(currentDate);
		DecimalFormat decFormat = new DecimalFormat(format);
		String res = decFormat.format(jour);
		Locale.setDefault(loc);

		return res;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlag(Station currentStation, String inputLine) {
		return currentStation.getMetaFlags().get(Intern.DATETIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalCode() {
		return _fullName;
	}

}
