package fr.ifremer.nemo.util;


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
    
    public String getExt(){
        String ext = Global.EMPTY_STRING;
        switch (this){
            case ODV :
                ext = Global.EXT_TXT;
                break;
            default:
                ext = ".*";//$NON-NLS-1$
                break;
        }
        return ext;
    }

 /*   
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
    */
}
