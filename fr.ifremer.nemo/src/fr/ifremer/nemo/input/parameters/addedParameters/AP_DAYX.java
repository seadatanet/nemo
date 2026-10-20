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
 * Description: description du paramètre DAYX
 * 
 * @author sbregent
 * 
 */
public class AP_DAYX extends AddedParam
{

	public AP_DAYX()
	{
		super();
		_code = "DAYX";
		_default = "99";
		_format = "%02d";
		_fullName = "DAY WITHIN MONTH";
		_unit = "dd";
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
		return Formatter.getSingleton().formatInt(getFormat(), DateTimeUtils.getDay(currentDate));

	}
}
