/* ============================================================================
 * Nom du fichier	: AP_date.java
 * Auteur			: sbregent
 * Date				: 26 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.text.ParseException;

import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: description du paramètre YEAR
 * 
 * @author sbregent
 * 
 */
public class AP_YEAR extends AddedParam
{

	public AP_YEAR()
	{
		super();
		_code = "YEAR";
		_default = "9999";
		_format = "%4d";
		_fullName = "YEAR";
		_unit = "yyyy";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlag(Station currentStation, String inputLine)
	{
		return currentStation.getMetaFlags().get(Intern.DATETIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
	{
		// $formatter.formatInt( "%4d",
		// $station.parametersAt(0).valuesAt($i).year)
		return Formatter.getSingleton().formatInt(getFormat(), DateTimeUtils.getYear(currentDate));
	}
}
