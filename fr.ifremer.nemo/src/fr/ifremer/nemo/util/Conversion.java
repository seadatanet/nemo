package fr.ifremer.nemo.util;

import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.ui.edit.DataModel;

/**
 * Description: Types de convertion
 * @author dboulang
 *
 */
public enum Conversion
{
    MEDATLAS, ODV;

    
    public String getName(){
        String name = Global.EMPTY_STRING;
        switch (this){
            case MEDATLAS :
                name = "Medatlas";//$NON-NLS-1$
                break;
            default :
                name = this.toString();
                break;
        }
        return name;
    }
    
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

    
    public String getTemplateName(){
        String name = Global.EMPTY_STRING;
        switch (this){
            case MEDATLAS :
                name = "medatlas.vtl";//$NON-NLS-1$
                break;
            default :
                name = "odv.vtl";//$NON-NLS-1$
                break;
        }
        return name;
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
