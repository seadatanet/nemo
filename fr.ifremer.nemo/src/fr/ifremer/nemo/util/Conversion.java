package fr.ifremer.nemo.util;

import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.ui.DataModel;

/**
 * Description: Types de convertion
 * @author dboulang
 *
 */
public enum Conversion
{
    MEDATLAS, ODV;
    
    public String getDebut(){
        String debut = Global.EMPTY_STRING;
        switch (this){
            case MEDATLAS :
                debut = "*"; //$NON-NLS-1$
                break;
            case ODV :
                debut = "//"; //$NON-NLS-1$
                break;
        }
        return debut;
    }
    
    public ParameterModel[] getAddedParameterModels(){
        ParameterModel[]  addedParameters = null;
        switch (this){
            case MEDATLAS :
                addedParameters = DataModel.stationModel.getMedParameterModels();
                break;
            case ODV :
                addedParameters = DataModel.stationModel.getOdvParameterModels();
                break;
            default :
                break;
        }
        return addedParameters;
    }
}
