/*
 * ============================================================================
 * Nom du fichier : AP_date.java
 * Auteur : sbregent
 * Date : 26 janv. 11
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.text.ParseException;

import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: description du paramètre DTUT
 * 
 * @author sbregent
 * 
 */
public class AP_DTUT extends AddedParam
{

	public AP_DTUT()
	{
		super();
		_code = "DTUT";
		_format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
		_fullName = "time_ISO8601";
		_unit = "yyyy-mm-ddThh:mm:ss.sss";
		cible = Conversion.ODV;
		isMesured = false;
	}

	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
	{
		// time-ISO8601
		return DateTimeUtils.getVariantDateTime(currentDate);
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
	public String getLocalCode()
	{
		return _fullName;
	}
}
