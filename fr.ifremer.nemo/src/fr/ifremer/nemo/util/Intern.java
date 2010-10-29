package fr.ifremer.nemo.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Intern application properties
 * @author IFREMER
 */
public class Intern extends NLS {
	private static final String BUNDLE_NAME = "fr.ifremer.nemo.util.intern"; //$NON-NLS-1$

    public static String application_title;
    
    public static String dir_resources;
    public static String dir_vocabLists;
    
    public static String fileName_configIni;
    public static String fileName_coupling;
    
	public static String id_nemo;
	public static String id_lists;
	public static String id_maps;
	public static String id_edmo;
    public static String nbnemolists;
    public static String vocabLists;
    public static String vocabMaps;

    public static String list_ship;
    public static String list_dataCenter;
    public static String list_countries;
    public static String list_regions;
    public static String list_ROSCOPparameters;
    public static String list_instruments;
    public static String list_methodCDI;
    public static String list_typePlatform;
    public static String list_accesDonnees;
    public static String list_flagsSDN;
    public static String list_BODCpdv;
    public static String list_nemo_parameters;
    public static String list_nemo_survey;
    public static String list_edmo;
    
    public static String lang_fr;
    public static String lang_en;
    
    public static String help_file;
    public static String help_separator;

    public static String settings_proxy;
    public static String proxy_use;
    public static String proxy_use_set;
    public static String proxy_adr;
    public static String proxy_adr_set;
    public static String proxy_port;
    public static String proxy_port_set;
    public static String proxy_login;
    public static String proxy_login_set;
    public static String proxy_pass;
    public static String proxy_pass_set;

    public static String ini_name;
    public static String ini_start;
    public static String ini_proxy_adr;
    public static String ini_proxy_port;
    public static String ini_proxy_login;
    public static String ini_proxy_pass;
    
    public static String xml_tag_start;
    public static String xml_tag_end;
    public static String xml_tag_close;
    public static String mapping_subject;
    public static String mapping_objet;
    public static String mapping_unit;
    public static String file_mapping;
    public static String file_mapping_sdn;
    public static String file_mapping_subject;
    public static String NemoLists_unknown;
    public static String CJDY;
    public static String DTUT;
    
    public static String paramPRES;
    public static String paramDEPH;
    public static String paramDPSF;
    public static String paramPSAL;
    public static String paramTEMP;
    
    public static String unknown;
    public static String CDIResumeWriter_notSpecified;
    public static String CDIResumeWriter_separateur;
    
    public static String CruiseShemaFactory;
    public static String CruiseShemaLocation;
    public static String CruiseXSL; 
    public static String ModelXSL; 
    public static String CruiseDateFormat;
    
    public static String errorParseChar;
    public static String errorParseSign;
    public static String errorParseCoordN;
    public static String errorParseCoordW;
    public static String errorParseDegres;
    public static String errorParseMinutes;
    public static String errorParseSeconds;
    
    public static String S_DCHIST;
    public static String S_COMMENT;
    public static String S_SURFACE;
    public static String S_OFFSETTU;
    public static String S_TIME;
    public static String S_DATE;
    public static String S_LAT;
    public static String S_LON;
    public static String S_DEPTH;
    public static String S_ELAT;
    public static String S_ELON;
    public static String S_EDEPTH;
    public static String S_SENSORDEPTH;
    public static String S_MAGNETIC;
    public static String S_SAMPLINGRATE;
    public static String S_EDATE;
    public static String S_ETIME;

    //public static String QC = "QC"; //$NON-NLS-1$

    public static String S_STATIONNUMBER;
    public static String S_DATATYPE;
    public static String S_QCFLAG;

    // Types de metadonnee
    public static String DATETIME;
    public static String ENDDATETIME;
    // (Type de metadonnee calcules)
    public static String DURATION;
    public static String DISTANCETOBOTTOM;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Intern.class);
	}

	private Intern() {
	}
    
    /**
     * @param key
     * @return message corresponding to key
     */
    public static String getString(String key) throws MissingResourceException
    { 
        ResourceBundle resource = ResourceBundle.getBundle(BUNDLE_NAME);
        return resource.getString(key);
    }
}
