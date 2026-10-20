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

public class AP_DEPHPR01 extends AP_DEPH {

	public AP_DEPHPR01() {
		super();
		_code = "DEPHPR01";
		_fullName = "Depth below surface of the water body by profiling pressure sensor and converted to seawater depth using UNESCO algorithm";
		_unit = "m";
		cible = Conversion.ODV;
		_P011 = true;
		_codeP011 = "DEPHPR01";
		_unitCode = "ULAA";
	}

	public AP_DEPHPR01(Conversion conv) {
		super();
		if (conv == Conversion.ODV)
			new AP_DEPHPR01();
		else {
			_code = "DEPHPR01";
			_fullName = "Depth";
			_unit = "Metres";
			cible = Conversion.ODV;
			_P011 = true;
			_codeP011 = "DEPHPR01";
			_nameP011 = "Depth below surface of the water body by profiling pressure sensor and converted to seawater depth using UNESCO algorithm";
			_unitCode = "ULAA";
			_default = Global.EMPTY_STRING;

		}
	}

}
