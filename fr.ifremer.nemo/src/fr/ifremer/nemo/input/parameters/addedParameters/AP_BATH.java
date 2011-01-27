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
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: TODO
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
        propertyCode=Intern.S_DEPTH;
        isMesured = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFlag(Station currentStation, String inputLine)
    {
        return currentStation.getMetaFlags().get(Intern.S_DEPTH);     
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
    {
        String stringValue=null;
        String type = getFormat().substring(getFormat().length() - 1);
        if (type.equals("f") || type.equals("F")){ 
            stringValue = String.format(java.util.Locale.US, getFormat(), Float.parseFloat(inputLine));
        }else if (type.equals("d") || type.equals("D")){
            stringValue = String.format(java.util.Locale.US, getFormat(), Integer.parseInt(inputLine));
        } 
        return stringValue;
    }
}
