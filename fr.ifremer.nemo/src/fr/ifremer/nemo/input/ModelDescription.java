/* ============================================================================
 * Nom du fichier	: ModelDescription.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date de création	: 26 nov. 09
 * Client           : Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Class ModelDescription.java Description: arborescence XML d'un modèle
 * 
 * @author Delphine BOULANGER
 */
public class ModelDescription extends NLS {
	private static final String BUNDLE_NAME = "fr.ifremer.nemo.input.model_desc"; //$NON-NLS-1$

	public static String node_formattedEndDate;

	public static String node_formattedStartDate;

	public static String nodetitle_default;

	public static String node_root;
	public static String node_mediaModel;
	public static String node_process;
	public static String node_separator;
	public static String node_type;
	public static String node_fileHeader;
	public static String node_stationHeader;
	public static String node_endStation;
	public static String node_endData;
	public static String node_cruiseModel;
	public static String node_stationModel;
	public static String node_cref;
	public static String node_localCSRIdentifier;
	public static String node_cname;
	public static String node_shipcode;
	public static String node_shipname;
	public static String node_startdate;
	public static String node_enddate;
	public static String node_region;
	public static String node_regionCode;
	public static String node_country;
	public static String node_countryName;
	public static String node_laboratories;
	public static String node_chief;
	public static String node_project;
	public static String node_datacenter;
	public static String node_availability;
	public static String node_comment;
	public static String node_listDatatypes;
	public static String node_datatypeModel;
	public static String node_nbprofiles;
	public static String node_qc;
	public static String node_sensor;
	public static String node_propertyModel;
	public static String node_uservalue;
	public static String node_format;
	public static String node_fDate;
	public static String node_fLatitude;
	public static String node_fLongitude;
	public static String node_fInteger;
	public static String node_fFloat;
	public static String node_fragment;
	public static String node_flag;
	public static String node_firstline;
	public static String node_lastline;
	public static String node_firstoffset;
	public static String node_lastoffset;
	public static String node_parameterModel;
	public static String node_formula;
	public static String node_isText;
	public static String node_indefault;
	public static String node_dataFragment;
	public static String node_parameter;
	public static String node_fullname;
	public static String node_codeP011;
	public static String node_nameP011;
	public static String node_P011;
	public static String node_unit;
	public static String node_unitCode;
	public static String node_instrument;
	public static String node_fallrate;
	public static String node_outdefault;
	public static String node_reference;
	public static String node_listProperties;
	public static String node_listParameters;
	public static String node_verticalParametersList;
	public static String node_p_depthBSBed;
	public static String node_p_depthBSSurf;
	public static String node_p_pressure;
	public static String node_p_heightASLvl;

	public static String node_dataModel;
	public static String node_conversionModel;
	public static String node_listPermissions;
	public static String node_sort;
	public static String node_defaultselected;
	public static String node_defaultvalue;

	public static String node_outputFormat;
	public static String node_dataListType;
	public static String node_mesureType;

	public static String a_version;
	public static String a_isValid;
	public static String a_sourcepath;
	public static String a_multistations;
	public static String a_pattern;
	public static String a_patternIsRegexp;
	public static String a_code;
	public static String a_property;
	public static String a_userprovided;
	public static String a_singleline;
	public static String a_variable;
	public static String a_firstoffset;
	public static String a_lastoffset;
	public static String a_enable;
	public static String a_string;
	public static String a_vari;

	public static String setter_formattedEndDate;

	public static String setter_formattedStartDate;

	public static String setter_process;
	public static String setter_separator;
	public static String setter_type;
	public static String setter_fileHeader;
	public static String setter_stationHeader;
	public static String setter_endStation;
	public static String setter_endData;
	public static String setter_cref;
	public static String setter_localCSRIdentifier;
	public static String setter_cname;
	public static String setter_shipcode;
	public static String setter_shipname;
	public static String setter_startdate;
	public static String setter_enddate;
	public static String setter_region;
	public static String setter_country;
	public static String setter_countryName;
	public static String setter_laboratories;
	public static String setter_chief;
	public static String setter_project;
	public static String setter_datacenter;
	public static String setter_availability;
	public static String setter_comment;
	public static String setter_nbprofiles;
	public static String setter_qc;
	public static String setter_sensor;
	public static String setter_uservalue;
	public static String setter_firstline;
	public static String setter_lastline;
	public static String setter_firstoffset;
	public static String setter_lastoffset;
	public static String setter_formula;
	public static String setter_isText;
	public static String setter_indefault;
	public static String setter_fullname;
	public static String setter_codeP011;
	public static String setter_nameP011;
	public static String setter_P011;
	public static String setter_unit;
	public static String setter_unitCode;
	public static String setter_instrument;
	public static String setter_fallrate;
	public static String setter_outdefault;
	public static String setter_format;
	public static String setter_sort;
	public static String setter_defaultselected;
	public static String setter_defaultvalue;
	public static String setter_outputFormat;
	public static String setter_dataListType;
	public static String setter_mesureType;
	public static String setter_verticalParametersList;
	public static String setter_p_depthBSBed;
	public static String setter_p_depthBSSurf;
	public static String setter_p_pressure;
	public static String setter_p_heightASLvl;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ModelDescription.class);
	}

	private ModelDescription() {
	}

	/**
	 * @param key
	 * @return message corresponding to key
	 */
	public static String getString(String key) throws MissingResourceException {
		ResourceBundle resource = ResourceBundle.getBundle(BUNDLE_NAME);
		return resource.getString(key);
	}

}
