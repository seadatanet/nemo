/* ============================================================================
 * Nom du fichier	: AddedParameter.java
 * Auteur			: dboulang
 * Date				: 9 févr. 09
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

/**
 * Description: TODO
 * @author dboulang
 *
 */
public class AddedParameter extends Parameter
{
    
    public static enum IdParameter{
        DEPH, YEAR, MNTH, DAYX, TIME, CJDY, LATX, LONX, BATH;
    }
    
    private IdParameter idParam;
    private Conversion cible;

    /**
     * Returns a copy of this Object
     * 
     * @return a copy of this object
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AddedParameter copy = null;
        copy = (AddedParameter) super.clone();
        copy.idParam = null;
        copy.cible = null;
        return copy;
    }

    /**
     * 
     */
    public AddedParameter(IdParameter idParam)
    {
        this.idParam = idParam;
        _code = idParam.toString();
        
        switch (idParam){
            case DEPH :
                _default = Global.ZERO;
                _format = Global.EMPTY_STRING;   
                for (int i = 0; i < NemoLists.getParameters().length; i++) {
                    if (NemoLists.getParameters()[i].getCode().equals(this.getCode())) {
                        _format = NemoLists.getParameters()[i].getFormat();
                        break;
                    }
                }                   
                _fullName = "DEPTH BELOW SEA SURFACE";       //$NON-NLS-1$
                _unit = "m";                             //$NON-NLS-1$
                cible = Conversion.ODV;
                break;
            case YEAR :
                _default = "9999";                  //$NON-NLS-1$
                _format = "%4d";                       //$NON-NLS-1$
                _fullName = "YEAR";                          //$NON-NLS-1$
                _unit = "yyyy";                          //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case MNTH :
                _default = "99";                    //$NON-NLS-1$
                _format = "%02d";                       //$NON-NLS-1$
                _fullName = "MONTH";                         //$NON-NLS-1$
                _unit = "mm";                            //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case DAYX :
                _default = "99";                    //$NON-NLS-1$
                _format = "%02d";                       //$NON-NLS-1$
                _fullName = "DAY WITHIN MONTH";              //$NON-NLS-1$
                _unit = "dd";                            //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case TIME :
                _default = "999999";                //$NON-NLS-1$
                _format = "hh24mmss";                    //$NON-NLS-1$
                _fullName = "TIME WITHIN DAY";               //$NON-NLS-1$
                _unit = "hhmmss";                        //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case CJDY :
                _default = Global.ZERO;
                _format = "#0.000000";                   //$NON-NLS-1$
                _fullName = "Chronological Julian Date";     //$NON-NLS-1$
                _unit = "days";                          //$NON-NLS-1$
                cible = Conversion.ODV;
                break;
            case LATX :
                _default = "+99.999999";            //$NON-NLS-1$
                _format = "%+10.6f";                     //$NON-NLS-1$
                _fullName = "LATITUDE";                      //$NON-NLS-1$
                _unit = "decimal degree";                //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case LONX :
                _default = "+999.999999";           //$NON-NLS-1$
                _format = "%+11.6f";                     //$NON-NLS-1$
                _fullName = "LONGITUDE";                     //$NON-NLS-1$
                _unit = "decimal degree";                //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
            case BATH :
                _default = "-999.9";                //$NON-NLS-1$
                _format = "%6.1f";                       //$NON-NLS-1$
                _fullName = "BATHYMETRIC DEPTH";             //$NON-NLS-1$
                _unit = "meter";                         //$NON-NLS-1$
                cible = Conversion.MEDATLAS;
                break;
        }
    }

    
    public PropertyModel getPropertyModel(StationModel stationModel){  
        PropertyModel propertyModel = null;
        String propertyCode;
        switch (this.idParam){
            case LATX :
                propertyCode = NemoSection.S_LAT;
                break;
            case LONX :
                propertyCode = NemoSection.S_LON;
                break;
            case BATH :
                propertyCode = NemoSection.S_DEPTH;
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
    
    public void setStation(Station x, Conversion cible) {
        if (_station == x)
            return;
        Station old = _station;
        if (_station != null) {
            _station = null;
            old.removeParameter(this);
        }
        _station = x;
        if (x != null) {
            switch (cible){
                case ODV :
                    x.addOdvParameter(this);
                    break;
                case MEDATLAS :
                    x.addMedParameter(this);
                    break;
            }
            
        }
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
