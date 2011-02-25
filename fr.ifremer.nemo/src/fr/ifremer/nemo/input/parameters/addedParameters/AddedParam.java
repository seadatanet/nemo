/* ============================================================================
 * Nom du fichier	: AP_date.java
 * Auteur			: sbregent
 * Date				: 26 janv. 11
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.input.parameters.addedParameters;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;


/**
 * Description: TODO
 * @author sbregent
 *
 */
public class AddedParam extends Parameter
{
    
    @SuppressWarnings("nls")
    public static enum IdParameter{
        generic_depthBSSurf_FromPressure ,
        generic_depthBSSurf_FromSensor ,
        YEAR, MNTH, DAYX, TIME, 
        CJDY, DTUT, 
        LATX, LONX, 
        BATH, 
        PRES, 
        DEPH , 
        DEPHPR01, ADEPZZ01,
        ;
    }
    
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
    
//    protected String id = Global.EMPTY_STRING;
    protected Conversion cible;
    protected String flag ;
    protected String propertyCode;
    protected String value;
    protected boolean isMesured;
   // FAE 6959
    protected boolean hasDefaultValue;
    
    public AddedParam()
    {
        super();
//        id = Global.EMPTY_STRING;
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
        hasDefaultValue=false;
        DataListTypeEnum dataListType = Nemo.getInstance().getDomain().getData().getModel().getDataListType();
        _P011 = false;//(dataListType==DataListTypeEnum.P011_VIA_P021 || dataListType==DataListTypeEnum.P011_VIA_P091);
        
    }
    
    @SuppressWarnings("nls")
    public String compute(String fragmnt)  throws StringIndexOutOfBoundsException, ParseException {

        String paramValue = null;
        
        if (fragmnt.equals(Global.EMPTY_STRING)) {
            paramValue = Global.EMPTY_STRING;
        } else {
            if (_default.equals(fragmnt)) {
                paramValue = _default;
            }else {
                paramValue = getValue(null, fragmnt, 0);
            }
        }
        return paramValue; 
    }
    
    public String getFlag(Station currentStation, String inputLine)
    {
        String flagQc;
        if (hasDefaultValue)
        {
           // FAE 6959
            flagQc = Global.DEFAULT_FLAG;
        }else{
            flagQc=flag;
        }
        return flagQc;
    }
    public String getValue(Station currentStation, String inputLine, long currentDate) throws ParseException
    {
        return value;
    }
    public PropertyModel getPropertyModel(StationModel stationModel){  
        PropertyModel propertyModel = null;
        if (propertyCode != null){
            propertyModel = stationModel.propertiesAt(propertyCode);
        }
        return propertyModel;
    }
    public String getPropertyCode(){  
     return propertyCode;
    }
    public Conversion getCible()
    {
        return cible;
    }
    public boolean isMesured(){
        return isMesured;
    }

    
}
