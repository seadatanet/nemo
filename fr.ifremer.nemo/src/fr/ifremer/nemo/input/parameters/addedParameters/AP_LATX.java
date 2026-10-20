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
import fr.ifremer.nemo.util.format.LatitudeFormat;

/**
 * Description: description du paramètre LATX
 * 
 * @author sbregent
 * 
 */
public class AP_LATX extends AddedParam
{

	public AP_LATX()
	{
		super();
		_code = "LATX";
		_default = "+99.999999";
		_format = "+DD.dddddd";
		_fullName = "LATITUDE";
		_unit = "decimal degree";
		propertyCode = Intern.S_LAT;
		isMesured = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlag(Station currentStation, String inputLine)
	{
		return currentStation.getMetaFlags().get(Intern.S_LAT);
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
		PropertyModel prop = Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_LAT);
		String format = prop.getFormat().toString();
		LatitudeFormat formatLAT = null;
		if (prop.getFragment() instanceof CharFragment) {
			formatLAT = new LatitudeFormat(format, ((CharFragment)prop.getFragment()).isVariable());
		} else if (prop.getFragment() instanceof CsvFragment) {
			formatLAT = new LatitudeFormat(format, false);
		}
		DecimalFormat decimalFormat = new DecimalFormat("#0.000000", symbols);
		Object parsedLat = formatLAT.parseObject(inputLine);
		stringValue = decimalFormat.format(parsedLat);
		stringValue = Formatter.getSingleton().padStringLeft(stringValue, getFormat().length());

		return stringValue;
	}
}
