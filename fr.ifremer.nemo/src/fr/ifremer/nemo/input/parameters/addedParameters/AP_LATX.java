/* ============================================================================
 * Nom du fichier	: AP_date.java
 * Auteur			: sbregent
 * Date				: 26 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.text.DecimalFormat;
import java.text.ParseException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.format.LatitudeFormat;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class AP_LATX extends AddedParam
{

    public AP_LATX()
    {
        super();
        _code = "LATX";
        _default = "+99.999999";
        _format = "+DD.dddddd";
        _fullName = "LATITUDE";
        _unit = "decimal degree";
        propertyCode=Intern.S_LAT;
        isMesured = true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getFlag(Station currentStation, String inputLine)
    {
        return currentStation.getMetaFlags().get(Intern.S_LAT);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
    {
        String stringValue=null;
        LatitudeFormat formatLAT = new LatitudeFormat(getFormat(), true);
        stringValue = (new DecimalFormat("#0.000000", symbols)).format(formatLAT.parseObject(inputLine));
        stringValue = Formatter.getSingleton().padStringLeft(stringValue, getFormat().length());
        return stringValue;
    }
}
