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
import java.text.DecimalFormat;
import java.text.ParseException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.fragment.stationfragment.CharFragment;
import fr.ifremer.nemo.input.fragment.stationfragment.CsvFragment;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Description: description du paramètre LONX
 * 
 * @author sbregent
 * 
 */
public class AP_LONX extends AddedParam
{

	public AP_LONX()
	{
		super();
		_code = "LONX";
		_default = "+999.999999";
		_format = "+DDD.dddddd";
		_fullName = "LONGITUDE";
		_unit = "decimal degree";
		propertyCode = Intern.S_LON;
		isMesured = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlag(Station currentStation, String inputLine)
	{
		return currentStation.getMetaFlags().get(Intern.S_LON);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws UnsupportedEncodingException
	 */
	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException, UnsupportedEncodingException
	{
		String stringValue = null;
		// 7339 on lit le format de parsing dans les valeurs du modele saisies
		// depuis l'onglet station
		PropertyModel prop = Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_LON);
		String format = prop.getFormat().toString();
		LongitudeFormat formatLON = null;
		if (prop.getFragment() instanceof CharFragment) {
			formatLON = new LongitudeFormat(format, ((CharFragment)prop.getFragment()).isVariable());
		} else if (prop.getFragment() instanceof CsvFragment) {
			formatLON = new LongitudeFormat(format, false);
		}
		DecimalFormat decimalFormat = new DecimalFormat("#0.000000", symbols);
		Object parsedLong = formatLON.parseObject(inputLine);
		stringValue = decimalFormat.format(parsedLong);
		stringValue = Formatter.getSingleton().padStringLeft(stringValue, getFormat().length());
		return stringValue;
	}
}
