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
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;
import java.util.Set;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

/**
 * Description: description des paramètres ajoutés automatiquement par Nemo.
 * Voir les classes AP_xxx héritant de celle-ci pour chaque paramètre.
 * 
 * @author sbregent
 * 
 */
public class AddedParam extends Parameter {

	@SuppressWarnings("nls")
	public static enum IdParameter {
		generic_depthBSSurf_FromPressure, generic_depthBSSurf_FromSensor, generic_heightASLvl_FromSensor, YEAR, MNTH, DAYX, TIME, CJDY, DTUT, LATX, LONX, BATH, PRES, DEPH, DEPHPR01, ADEPZZ01, ;
	}

	DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

	protected Conversion cible;
	protected String flag;
	protected String propertyCode;
	protected String value;
	protected boolean isMesured;
	// FAE 6959
	protected boolean hasDefaultValue;

	public AddedParam() {
		super();
		// attribut herites de la classe Parameter
		_default = Global.ZERO;
		_format = Global.EMPTY_STRING;
		_fullName = Global.EMPTY_STRING;
		_unit = Global.EMPTY_STRING;
		cible = Conversion.MEDATLAS;
		flag = Nemo.getInstance().getConfig().getDefaultFlag();
		propertyCode = null;
		value = null;
		isMesured = false;
		hasDefaultValue = false;
		_P011 = false;

	}

	@SuppressWarnings("nls")
	public String compute(String fragmnt) throws StringIndexOutOfBoundsException, ParseException, UnsupportedEncodingException {

		String paramValue = null;

		if (fragmnt.equals(Global.EMPTY_STRING)) {
			paramValue = Global.EMPTY_STRING;
		} else {
			if (_default.equals(fragmnt)) {
				paramValue = _default;
			} else {
				paramValue = getValue(null, fragmnt, 0);
			}
		}
		return paramValue;
	}

	public String getFlag(Station currentStation, String inputLine) {
		String flagQc;
		if (hasDefaultValue) {
			// FAE 6959
			flagQc = Global.DEFAULT_FLAG;
		} else {
			flagQc = flag;
		}
		return flagQc;
	}

	public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException, UnsupportedEncodingException {
		return value;
	}

	public PropertyModel getPropertyModel(StationModel stationModel) {
		PropertyModel propertyModel = null;
		if (propertyCode != null) {
			propertyModel = stationModel.propertiesAt(propertyCode);
		}
		return propertyModel;
	}

	public String getPropertyCode() {
		return propertyCode;
	}

	public Conversion getCible() {
		return cible;
	}

	public boolean isMesured() {
		return isMesured;
	}
	
	/**
	 * fill an AddedParam.
	 * @param aParam
	 * @param conv
	 */
	public void fillDataFromBean(DynamicAddedParamBean aParam, Conversion conv) {
		DynamicAddedParamByConversionBean addedParamsByConversionBeans;
		Set<String> keys = aParam.getDynamicAddedParamByConversionBeans().keySet();
		for (String key : keys) {
			
			addedParamsByConversionBeans = aParam.getDynamicAddedParamByConversionBeans().get(key);
				
			try {
				Conversion foundConversion = Conversion.valueOf(key);
				
				if (foundConversion == conv) {
					
					// match, get data
					fillDataFromBeanByConversion(aParam.getCode(), addedParamsByConversionBeans);
					return;
				}
			} catch (IllegalArgumentException e) {
				// No match found.
			}
		}
		
		// here, we couldn't find the conversion, switch to default
		addedParamsByConversionBeans = aParam.getDynamicAddedParamByConversionBeans().get("default");
		fillDataFromBeanByConversion(aParam.getCode(), addedParamsByConversionBeans);
	}

	/**
	 * Fill an AddedParam with a bean get from YAML configuration.
	 * 
	 * @param aParam a DynamicAddedParamByConversionBean.
	 */
	public void fillDataFromBeanByConversion(String code, DynamicAddedParamByConversionBean aParam) {
		_code = code;
		_fullName = aParam.getFullName();
		_unit = aParam.getUnit();
		cible = aParam.getCible();
		_P011 = aParam.getP011();
		_codeP011 = aParam.getCodeP011();
		_nameP011 = aParam.getNameP011();
		_unitCode = aParam.getUnitCode();
		_default = aParam.getDefaultValue();
	}
}
