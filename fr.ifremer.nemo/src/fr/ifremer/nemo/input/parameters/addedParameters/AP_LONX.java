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
import fr.ifremer.nemo.util.format.LongitudeFormat;

/**
 * Description: TODO
 * @author sbregent
 *
 */
public class AP_LONX extends AddedParam
{

    public AP_LONX()
    {
        super();
        _code = "LONX";
        _default = "+999.999999";
        _format = "+DDD.dddddd";
        _fullName = "LONGITUDE";
        _unit = "decimal degree";
        propertyCode=Intern.S_LON;
        isMesured = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFlag(Station currentStation, String inputLine)
    {
        return currentStation.getMetaFlags().get(Intern.S_LON);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
    {
        String stringValue=null;
        LongitudeFormat formatLON = new LongitudeFormat(getFormat(), true);
        stringValue = (new DecimalFormat("#0.000000", symbols)).format(formatLON.parseObject(inputLine));
        stringValue = Formatter.getSingleton().padStringLeft(stringValue, getFormat().length());                        
       return stringValue;
    }
}
