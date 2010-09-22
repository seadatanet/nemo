/* ============================================================================
 * Nom du fichier	: ConfigDescription.java
 * Application      : NEMO version 1.2.20
 * Auteur			: Delphine BOULANGER
 * Date de création	: 30 nov. 09
 * Client           : Ifremer
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

/**
 * Class ConfigDescription.java
 * Description: arborescence XML d'un modèle
 * @author Delphine BOULANGER
 */
public class ConfigDescription extends NLS
{
    private static final String BUNDLE_NAME = "fr.ifremer.nemo.config.config_desc"; //$NON-NLS-1$

    public static String a_code;

    public static String a_label;

    public static String a_lang;

    public static String a_local;

    public static String a_name;

    public static String a_SDN;

    public static String a_value;

    public static String a_version;

    public static String node_proxy;

    public static String node_root;

    public static String node_accesDonnees;

    public static String node_cdiMethod;

    public static String node_cdiURL;

    public static String node_codeEdmoDonnees;

    public static String node_contact;

    public static String node_cruisesDirectory;

    public static String node_dataCenter;

    public static String node_dateISO;

    public static String node_defaultFlagTable;

    public static String node_edmoURL;

    public static String node_fichierMapping;

    public static String node_Flag;
    
    public static String node_FlagDefault;

    public static String node_FlagTable;

    public static String node_idEdmoDistDonnees;

    public static String node_idEdmoRespCdi;

    public static String node_idEdmoRespDonnees;

    public static String node_modelsDirectory;

    public static String node_nautilusWsURL;

    public static String node_nemoWsURL;

    public static String node_outputDirectory;

    public static String node_showRule;

    public static String node_typePlateforme;

    public static String node_VocabListURL;

    public static String node_vocabURL;

    public static String node_writeCdi;

    public static String node_writeMapping;

    public static String nodetitle_default;

    public static String setter_AccesDonnees;

    public static String setter_CdiMethod;

    public static String setter_CdiURL;

    public static String setter_CodeEdmoDonnees;

    public static String setter_Contact;

    public static String setter_CruisesDirectory;

    public static String setter_DataCenter;

    public static String setter_DateISO;
    
    public static String setter_FlagDefault;

    public static String setter_DefaultCode;

    public static String setter_Proxy;
    
    public static String setter_EdmoURL;

    public static String setter_FichierMapping;

    public static String setter_IdEdmoDistDonnees;

    public static String setter_IdEdmoRespCdi;

    public static String setter_IdEdmoRespDonnees;

    public static String setter_ModelsDirectory;

    public static String setter_NautilusWsURL;

    public static String setter_NemoWsURL;

    public static String setter_OutputDirectory;

    public static String setter_ShowRule;

    public static String setter_TypePlateForme;

    public static String setter_VocabURL;

    public static String setter_WriteCdi;

    public static String setter_WriteMapping;
    
    
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ConfigDescription.class);
    }

    private ConfigDescription() {
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
