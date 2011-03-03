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
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: description du paramètre DEPH
 * 
 * @author sbregent
 * 
 */
public class AP_DEPH extends AddedParam
{

	public AP_DEPH()
	{
		super();
		_code = "DEPH";
		_fullName = "DEPTH BELOW SEA SURFACE";
		_unit = "m";
		cible = Conversion.ODV;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormat()
	{
		InputDomain iDomain = Nemo.getInstance().getDomain();
		if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE)
		{
			ParameterModel pModelPRES = getParameterModel();
			return pModelPRES.getParameter().getFormat();
		}
		for (int i = 0; i < NemoLists.getParameters().length; i++)
		{
			if (NemoLists.getParameters()[i].getCode().equals(this.toString()))
			{
				return NemoLists.getParameters()[i].getFormat();
			}
		}
		return Global.EMPTY_STRING;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFlag(Station currentStation, String inputLine)
	{
		InputDomain iDomain = Nemo.getInstance().getDomain();
		if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE)
		{
			ParameterModel pModelPRES = getParameterModel();
			return pModelPRES.retreiveParameterFlag(inputLine);
		}
		return currentStation.getMetaFlags().get(Intern.S_SENSORDEPTH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
	{
		String stringValue = null;
		InputDomain iDomain = Nemo.getInstance().getDomain();
		String formatString = this.getFormat();

		if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE)
		{
			// récupération de la valeur de la pression
			ParameterModel pModelPRES = getParameterModel();
			pModelPRES.computeParameterValue(inputLine);
			ParameterValue valuePRES = pModelPRES.getLineValue();

			// calculDepth
			// FAE091 : calcul de la profondeur en fonction de la pression
			double X, Gr, P, Depth;

			P = valuePRES.getValue();
			X = Math.sin(currentStation.getLatitude() / 57.29578);
			double X2 = X * X;

			Gr = 9.780318 * (1.0 + (5.2788E-3 + 2.36E-5 * X2) * X2) + 1.092E-6 * P;
			Depth = (((-1.82E-15 * P + 2.279E-10) * P - 2.2512E-5) * P + 9.72659) * P;
			Depth = Depth / Gr;

			stringValue = Formatter.getSingleton().formatDouble(formatString, Depth);
		} else
		{
			// = SensorDepth
			stringValue = String.valueOf(currentStation.getSensorDepth());
		}
		return stringValue;
	}

	protected ParameterModel getParameterModel()
	{
		InputDomain iDomain = Nemo.getInstance().getDomain();
		String pressureCode = Nemo.getInstance().getDomain().getData().getModel().getVerticalParametersList().getP_pressure();
		ParameterModel pModelPRES;
		// récupération de la valeur de la pression
		if (iDomain.getData().getModel().getDataListType() == DataListTypeEnum.P091)
		{
			pModelPRES = iDomain.getData().getModel().getParameterModel(pressureCode);
		} else
		{
			pModelPRES = iDomain.getData().getModel().getParameterModelByP011(pressureCode);
		}
		return pModelPRES;
	}
}
