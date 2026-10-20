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
 * Description: description of parameter AHSLZZ01
 * 
 * @author scrouzil
 * 
 */

public class AP_AHSLZZ01 extends AP_HEIGHT {

	public AP_AHSLZZ01() {
		super();
		_code = "AHSLZZ01";
		_fullName = "Height above sea level";
		_unit = "m";
		cible = Conversion.ODV;
		_P011 = true;
		_codeP011 = "AHSLZZ01";
		_unitCode = "ULAA";
	}

	public AP_AHSLZZ01(Conversion conv) {
		super();
		if (conv == Conversion.ODV)
			new AP_AHSLZZ01();
		else {
			_code = "AHSLZZ01";
			_fullName = "Height above sea level";
			_unit = "Metres";
			cible = Conversion.ODV;
			_P011 = true;
			_codeP011 = "AHSLZZ01";
			_nameP011 = "Height (spatial coordinate) relative to sea level";
			_unitCode = "ULAA";
			_default = Global.EMPTY_STRING;
		}
	}
}
