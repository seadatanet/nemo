/*
 * ============================================================================
 * Nom du fichier : AP_date.java
 * Auteur : sbregent
 * Date : 26 janv. 11
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: description du paramètre TIME
 * 
 * @author sbregent
 * 
 */
public class AP_TIME extends AddedParam
{

	public AP_TIME()
	{
		super();
		_code = "TIME";
		_default = "999999";
		_format = "hh24mmss";
		_fullName = "TIME WITHIN DAY";
		_unit = "hhmmss";
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
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException, UnsupportedEncodingException
	{
		// $formatter.formatDateTime( "hh24mmss",
		// $station.parametersAt(0).valuesAt($i).date )
		return Formatter.getSingleton().formatDateTime(getFormat(), currentDate);
	}
}
