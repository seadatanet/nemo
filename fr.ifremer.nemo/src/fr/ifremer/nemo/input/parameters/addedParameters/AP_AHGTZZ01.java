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
 * Description: description du paramètre DEPHPR01
 * 
 * @author sbregent
 * 
 */

public class AP_AHGTZZ01 extends AP_HEIGHT {

	public AP_AHGTZZ01() {
		super();
		_code = "AHGTZZ01";
		_fullName = "height above sea level";
		_unit = "m";
		cible = Conversion.ODV;
		_P011 = true;
		_codeP011 = "AHGTZZ01";
		_unitCode = "ULAA";
	}

	public AP_AHGTZZ01(Conversion conv) {
		super();
		if (conv == Conversion.ODV)
			new AP_AHGTZZ01();
		else {
			_code = "AHGTZZ01";
			_fullName = "Height";
			_unit = "Metres";
			cible = Conversion.ODV;
			_P011 = true;
			_codeP011 = "AHGTZZ01";
			_nameP011 = "height above sea level";
			_unitCode = "ULAA";
			_default = Global.EMPTY_STRING;

		}
	}

}
