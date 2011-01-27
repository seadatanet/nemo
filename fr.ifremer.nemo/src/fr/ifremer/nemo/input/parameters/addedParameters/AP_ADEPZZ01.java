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
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.ParameterValue;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_InputType;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class AP_ADEPZZ01 extends AP_DEPH
{

    public AP_ADEPZZ01()
    {
        super();
        _code = "ADEPZZ01";
        _fullName = "Depth below surface of the water body";
        _unit = "m";
        cible = Conversion.ODV;
        _P011=true;
        _codeP011="ADEPZZ01";
    }

    
}
