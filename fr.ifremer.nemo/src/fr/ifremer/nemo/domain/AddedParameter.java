/* ============================================================================
 * Nom du fichier	: AddedParameter.java
 * Auteur			: dboulang
 * Date				: 9 févr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Global.E_InputType;

/**
 * Description: AddedParameter - paramètres ajoutés automatiquement par NEMO
 * @author dboulang
 *
 */
public class AddedParameter extends Parameter
{
    
    public static enum IdParameter{
        DEPH, YEAR, MNTH, DAYX, TIME, CJDY, DTUT, LATX, LONX, BATH;
    }
    
    private IdParameter idParam;
    private Conversion cible;
    
    public AddedParameter(){
        super();
        initValues();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void initValues(){
        idParam = null;
        cible = null;
    }

    /**
     * 
     */
    @SuppressWarnings("nls")
    public AddedParameter(IdParameter idParam, String formatPRES, E_InputType inputType)
    {
        this.idParam = idParam;
        _code = idParam.toString();
        
        switch (idParam){
            case DEPH :
                _default = Global.ZERO;
                _format = Global.EMPTY_STRING;   
                if (inputType == E_InputType.PROFILE){
                    _format = formatPRES;
                }else{                    
                    for (int i = 0; i < NemoLists.getParameters().length; i++) {
                        if (NemoLists.getParameters()[i].getCode().equals(this.getCode())) {
                            _format = NemoLists.getParameters()[i].getFormat();
                            break;
                        }
                    }                   
                }
                _fullName = "DEPTH BELOW SEA SURFACE";
                _unit = "m";
                cible = Conversion.ODV;
                break;
            case YEAR :
                _default = "9999";
                _format = "%4d";
                _fullName = "YEAR"; 
                _unit = "yyyy";
                cible = Conversion.MEDATLAS;
                break;
            case MNTH :
                _default = "99";
                _format = "%02d";
                _fullName = "MONTH";
                _unit = "mm";
                cible = Conversion.MEDATLAS;
                break;
            case DAYX :
                _default = "99";
                _format = "%02d";
                _fullName = "DAY WITHIN MONTH";
                _unit = "dd";
                cible = Conversion.MEDATLAS;
                break;
            case TIME :
                _default = "999999";
                _format = "hh24mmss";
                _fullName = "TIME WITHIN DAY";
                _unit = "hhmmss";
                cible = Conversion.MEDATLAS;
                break;
            case CJDY :
                _default = Global.ZERO;
                _format = "#0.000000";
                _fullName = "Chronological Julian Date";
                _unit = "days";
                cible = Conversion.ODV;
                break;
            case DTUT :
                _default = Global.ZERO;
                _format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
                _fullName = "time_ISO8601";
                _unit = "yyyy-mm-ddThh:mm:ss.sss";
                cible = Conversion.ODV;
                break;
            case LATX :
                _default = "+99.999999";
                _format = "+DD.dddddd";
                _fullName = "LATITUDE";
                _unit = "decimal degree";
                cible = Conversion.MEDATLAS;
                break;
            case LONX :
                _default = "+999.999999";
                _format = "+DDD.dddddd";
                _fullName = "LONGITUDE";
                _unit = "decimal degree";
                cible = Conversion.MEDATLAS;
                break;
            case BATH :
                _default = "-999.9";
                _format = "%6.1f";
                _fullName = "BATHYMETRIC DEPTH";
                _unit = "meter";
                cible = Conversion.MEDATLAS;
                break;
        }
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AddedParameter copy = null;
        copy = (AddedParameter) super.clone();
        return copy;
    }

    
    public PropertyModel getPropertyModel(StationModel stationModel){  
        PropertyModel propertyModel = null;
        String propertyCode;
        switch (this.idParam){
            case LATX :
                propertyCode = Intern.S_LAT;
                break;
            case LONX :
                propertyCode = Intern.S_LON;
                break;
            case BATH :
                propertyCode = Intern.S_DEPTH;
                break;
            default :
                propertyCode = null;
                break;
        }
        if (propertyCode != null){
            propertyModel = stationModel.propertiesAt(propertyCode);
        }
        return propertyModel;
    }

    /**
     * Getter sur cible.
     * @return the cible
     */
    public Conversion getCible()
    {
        return cible;
    }


    /**
     * Setter sur cible.
     * @param cible the cible to set
     */
    public void setCible(Conversion cible)
    {
        this.cible = cible;
    }


    /**
     * Getter sur idParam.
     * @return the idParam
     */
    public IdParameter getIdParam()
    {
        return idParam;
    }


    /**
     * Setter sur idParam.
     * @param idParam the idParam to set
     */
    public void setIdParam(IdParameter idParam)
    {
        this.idParam = idParam;
    }
}
