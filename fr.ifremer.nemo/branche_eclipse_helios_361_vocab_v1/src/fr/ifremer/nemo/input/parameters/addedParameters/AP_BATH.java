/* ============================================================================
 * Nom du fichier	: AP_date.java
 * Auteur			: sbregent
 * Date				: 26 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.text.ParseException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: description du paramètre BATH
 * 
 * @author sbregent
 * 
 */
public class AP_BATH extends AddedParam
{

	public AP_BATH()
	{
		super();
		_code = "BATH";
		_default = "-999.9";
		_format = "%6.1f";
		_fullName = "BATHYMETRIC DEPTH";
		_unit = "meter";
		propertyCode = Intern.S_DEPTH;
		isMesured = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlag(Station currentStation, String inputLine)
	{
		String flagQc;
		if (hasDefaultValue)
		{
			// FAE 6959
			flagQc = Global.DEFAULT_FLAG;
		} else
		{
			flagQc = currentStation.getMetaFlags().get(Intern.S_DEPTH);
		}
		return flagQc;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
	{
		hasDefaultValue = false;
		// si on est ici, c'est qu'on a ajoute BATH automatiquement, et qu'il
		// est calculé à partir de bottomDepth
		// 6959: il faut verifier que le valeur (inputLine) de bottomDepth n'est
		// pas la valeur par défaut
		// si oui, il faut mettre la valeur de BATH par défaut

		String stringValue = null;
		String type = getFormat().substring(getFormat().length() - 1);
		if (isBottomDepthDefaultValue(inputLine))
		{
			stringValue = getDefault();
			hasDefaultValue = true;
		} else if (type.equals("f") || type.equals("F"))
		{
			stringValue = String.format(java.util.Locale.US, getFormat(), Float.parseFloat(inputLine));
		} else if (type.equals("d") || type.equals("D"))
		{
			stringValue = String.format(java.util.Locale.US, getFormat(), Integer.parseInt(inputLine));
		}
		return stringValue;
	}

	private boolean isBottomDepthDefaultValue(String inputLine)
	{
		boolean res = false;
		String defaultBottomDepthValue = null;
		PropertyModel prop = Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DEPTH);
		if (prop.getFragment().isDefaultValueSelected())
		{
			defaultBottomDepthValue = prop.getFragment().getDefaultValue();
		}
		if (defaultBottomDepthValue != null && defaultBottomDepthValue.equals(inputLine))
		{
			res = true;
		}
		return res;
	}
}
