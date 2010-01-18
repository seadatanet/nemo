package fr.ifremer.nemo.util;

public interface Global
{

    public static final int SIZE_SECTION = 270;
    
    public static final String ZERO = "0"; //$NON-NLS-1$
    public static final String DEFAULT_FLAG = "9"; //$NON-NLS-1$
    public static final String WORSE_FLAG = "Z"; //$NON-NLS-1$
    // FAE 0001131 : remplacer "\n" par "\r\n" pour retour chariot sous bloc-note
    public static final String RN = "\r\n"; //$NON-NLS-1$
    public static final String STR_CONVERT = "x"; //$NON-NLS-1$
    public static final String UNDERSCORE = "_"; //$NON-NLS-1$
    public static final String SLASH = "/"; //$NON-NLS-1$
    public static final String SEP = " - "; //$NON-NLS-1$
    public static final String DBL_PT = ":"; //$NON-NLS-1$
    public static final String POINT = "."; //$NON-NLS-1$
    public static final String POURCENT = "%"; //$NON-NLS-1$
    public static final String ET_CO = "&"; //$NON-NLS-1$
     
    public static final String NO = "N"; //$NON-NLS-1$
    public static final String YES = "Y"; //$NON-NLS-1$
    public static final String PLUS = "+"; //$NON-NLS-1$
    public static final String MOINS = "-"; //$NON-NLS-1$
    public static final String STAR = "*"; //$NON-NLS-1$
    
    public static final String NORD = "N"; //$NON-NLS-1$
    public static final String SUD = "S"; //$NON-NLS-1$
    public static final String EST = "E"; //$NON-NLS-1$
    public static final String OUEST = "O"; //$NON-NLS-1$
    public static final String OUEST_EN = "W"; //$NON-NLS-1$

    public static final String POURCENT_S = "%s"; //$NON-NLS-1$
    public static final String EXT_XML = ".xml"; //$NON-NLS-1$
    public static final String EXT_TXT = ".txt"; //$NON-NLS-1$

    // Séparateurs
    // TODO passer en Enum ?
    public static final String TAB = "\t"; //$NON-NLS-1$
    public static final String SEMICOLON = ";"; //$NON-NLS-1$
    public static final String COMMA = ","; //$NON-NLS-1$
    public static final String SPACE = " "; //$NON-NLS-1$


    // Chaîne vide
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$

    // Types de table
    public static final String WSSURVEY = "WSSURVEY"; //$NON-NLS-1$
    public static final String IDEDMO = "IDEDMO"; //$NON-NLS-1$
    public static final String PARAMETER = "PARAMETER"; //$NON-NLS-1$
    public static final String DATATYPE = "DATATYPE"; //$NON-NLS-1$
    public static final String SHIP = "SHIP"; //$NON-NLS-1$
    public static final String DATACENTRE = "DATACENTRE"; //$NON-NLS-1$
    public static final String REGION = "REGION"; //$NON-NLS-1$
    public static final String SUBPARAMETER = "subParameter"; //$NON-NLS-1$    
    
    /*
    // Types de metadonnee
    public static final String DATETIME = "dateTime"; //$NON-NLS-1$
    public static final String ENDDATETIME = "endDateTime"; //$NON-NLS-1$
    // (Type de metadonnee calcules)
    public static final String DURATION = "duration"; //$NON-NLS-1$    
    public static final String DISTANCETOBOTTOM = "distanceToBottom"; //$NON-NLS-1$      
*/
    public enum E_Availability {
        publicly, limited, confidential;
        
        public String getCode(){
            String code = Global.EMPTY_STRING;
            switch (this){
                case confidential :
                    code = "C";//$NON-NLS-1$
                    break;
                case limited :
                    code = "L";//$NON-NLS-1$
                    break;
                default :
                    code = "P";//$NON-NLS-1$
                    break;
            }
            return code;
        }
    }
    
    public enum E_EndData{
        EOF, EMPTY_LINE, NEXT_START_WITH, END_WITH;
    }
    
    //  Types de fichier
    public enum E_InputType{
        PROFILE, TIMESERIES, TRAJECTORY;
    }
    
    public enum E_ProcessType{
        CRUISE_FILE, CRUISE_DIR, COLLECTION_DIR, COLLECTION_FILES;
    }
    
    public enum E_TypeConversion{
        MEDALAS, ODV_MULTISTATIONS, ODV_MONOSTATION
    }
}