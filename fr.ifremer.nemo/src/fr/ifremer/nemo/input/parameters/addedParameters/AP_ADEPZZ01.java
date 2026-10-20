/* ============================================================================
 * Nom du fichier	: AP_date.java
 * Auteur			: sbregent
 * Date				: 26 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

/**
 * Description: description du paramètre ADEPZZ01
 * 
 * @author sbregent
 * 
 */
public class AP_ADEPZZ01 extends AP_DEPH {

	public AP_ADEPZZ01() {
		super();
		_code = "ADEPZZ01";
		_fullName = "Depth below surface of the water body";
		_unit = "m";
		cible = Conversion.ODV;
		_P011 = true;
		_codeP011 = "ADEPZZ01";
		_unitCode = "ULAA";
	}

	public AP_ADEPZZ01(Conversion conv) {
		super();
		if (conv == Conversion.ODV)
			new AP_ADEPZZ01();
		else {
			_code = "ADEPZZ01";
			_fullName = "Depth";
			_unit = "Metres";
			cible = Conversion.ODV;
			_P011 = true;
			_codeP011 = "ADEPZZ01";
			_nameP011 = "Depth below surface of the water body";
			_unitCode = "ULAA";
			_default = Global.EMPTY_STRING;
		}
	}

}
