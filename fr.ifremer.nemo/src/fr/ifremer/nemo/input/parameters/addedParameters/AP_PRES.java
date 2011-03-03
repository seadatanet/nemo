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
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.presdepth.CalcPres;

/**
 * Description: description du paramètre PRES
 * 
 * @author sbregent
 * 
 */
public class AP_PRES extends AddedParam
{

	public AP_PRES()
	{
		super();
		_code = "PRES";
		_default = "-999.9";
		_format = "%6.1f";
		_fullName = "SEA PRESSURE sea surface=0";
		_unit = "decibar=10000 pascals";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
	{
		// FAE 6959
		hasDefaultValue = false;
		boolean dephHasNaNOrDefaultValue = false;
		boolean psalHasNaNOrDefaultValue = false;
		boolean tempHasNaNOrDefaultValue = false;

		InputDomain iDomain = Nemo.getInstance().getDomain();
		StationDomain sDomain = iDomain.getStation();
		String stringValue = null;
		DataModel dataModel = iDomain.getData().getModel();
		// récupération de la valeur de la profondeur
		ParameterModel pModelDEPTH = dataModel.getParameterModel(Intern.paramDEPH);
		dephHasNaNOrDefaultValue = pModelDEPTH.hasNaNValue(inputLine) || pModelDEPTH.hasDefaultValue(inputLine);
		pModelDEPTH.computeParameterValue(inputLine);
		ParameterValue valueDEPTH = pModelDEPTH.getLineValue();

		// récupération de la valeur de la salinite
		ParameterModel pModelPSAL = dataModel.getParameterModel(Intern.paramPSAL);
		psalHasNaNOrDefaultValue = pModelPSAL.hasNaNValue(inputLine) || pModelPSAL.hasDefaultValue(inputLine);
		pModelPSAL.computeParameterValue(inputLine);
		ParameterValue valuePSAL = pModelPSAL.getLineValue();

		// récupération de la valeur de la temperature
		ParameterModel pModelTEMP = dataModel.getParameterModel(Intern.paramTEMP);
		tempHasNaNOrDefaultValue = pModelTEMP.hasNaNValue(inputLine) || pModelTEMP.hasDefaultValue(inputLine);
		pModelTEMP.computeParameterValue(inputLine);
		ParameterValue valueTEMP = pModelTEMP.getLineValue();

		if (dephHasNaNOrDefaultValue || psalHasNaNOrDefaultValue || tempHasNaNOrDefaultValue)
		{
			// valeur par defaut
			stringValue = getDefault();
			hasDefaultValue = true;
		} else
		{
			// calcul

			// récupération de la valeur de la latitude
			double valueLAT = sDomain.getCurrentStation().getLatitude();

			double pres = CalcPres.calcPres(valueLAT, valuePSAL.getValue(), valueDEPTH.getValue(), valueTEMP.getValue());
			stringValue = Formatter.getSingleton().formatDouble(getFormat(), pres);
		}
		return stringValue;
	}
}
