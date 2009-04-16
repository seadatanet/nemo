package fr.ifremer.nemo.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

import fr.ifremer.nemo.ui.Utils;

public class Messages {
    private static final String BUNDLE_NAME = "fr.ifremer.nemo.util.messages"; //$NON-NLS-1$
    
    public static String application_directoryTitle;
    public static String application_fileTitle;
    public static String application_modelTitle;
    
    public static String success;
    public static String error;
    
    public static String menu_model;
    public static String menu_couplingTable;
    public static String menu_options;
    public static String menu_help;
    public static String menu_quit;
    
    public static String onglet_file;
    public static String onglet_cruise;
    public static String onglet_station;
    public static String onglet_data;
    public static String onglet_convert;
    
    public static String button_browse;
    public static String button_validateStep;
    public static String button_serverIni;
    public static String button_xmlIni;
    public static String button_reset;
    public static String button_set;
    public static String button_convert;
    
    public static String typeFile_profile;
    public static String typeFile_timeseries;
    public static String typeFile_trajectory;
    
    public static String lib_file_fileType;
    public static String lib_file_processCruise;
    public static String lib_file_cruiseFile;
    public static String lib_file_cruiseDir;
    public static String lib_file_processCollection;
    public static String lib_file_collectionCas1;
    public static String lib_file_collectionCasN;
    public static String lib_file_separator;
    public static String lib_file_sepTab;
    public static String lib_file_sepSemi;
    public static String lib_file_sepComma;
    public static String lib_file_sepSpace;
    public static String lib_file_sepOther;

    public static String lib_convert_title;
    public static String lib_convert_monoFichier;
    public static String lib_convert_monoStation;
    public static String lib_convert_sort;
    
    public static String table_label_meta;
    public static String table_label_code;
    public static String table_label_testIn;
    public static String table_label_testOut;
    public static String table_label_unit;
    public static String table_label_valueIn;
    public static String table_label_valueOut;
    public static String table_label_testFlag;
    public static String table_label_startFlag;
    public static String table_label_endFlag;
    
    public static String menu_label_setValue;
    public static String menu_label_updateTest;
    public static String menu_label_paramList;
    public static String menu_label_upType;
    public static String menu_label_downType;
    public static String menu_label_removeType;
    public static String menu_label_setFlag;
    public static String menu_label_deleteFlag;
    public static String menu_label_addFlag;
    public static String menu_label_removeFlag;
    
    public static String label_manual_01;
    public static String label_auto_01;
    public static String label_auto_02;
    public static String label_position;
    public static String label_format;
    public static String label_conversion;
    public static String label_formule;
    public static String label_line;
    public static String label_start;
    public static String label_end;
    public static String label_variable;
    public static String label_test;
    public static String label_file;
    public static String label_string;
    
    public static String confirm_saveModel;
    public static String confirm_quit;
    public static String confirm_eraseFile;
    public static String confirm_useDir;
    
    // EDIT
    public static String edit_file_title;
    public static String edit_file_enteteFichier;
    public static String edit_file_enteteStation;
    public static String edit_file_interLines;
    public static String edit_file_finStation;
    public static String edit_file_indicFinDonnees;
    public static String edit_file_idcEOF;
    public static String edit_file_idcEmptyLine;
    public static String edit_file_idcNextStation;
    public static String edit_file_idcManual;
    
    public static String edit_cruise_title;
    public static String edit_cruise_sectionReference;
    public static String edit_cruise_sectionReference_lib;
    public static String edit_cruise_cruiseRef;
    public static String edit_cruise_cruiseName;
    public static String edit_cruise_ship;
    public static String edit_cruise_sectionLocation;
    public static String edit_cruise_sectionLocation_lib;
    public static String edit_cruise_startDate;
    public static String edit_cruise_endDate;
    public static String edit_cruise_region;
    public static String edit_cruise_sectionDataSource;
    public static String edit_cruise_sectionDataSource_lib;
    public static String edit_cruise_country;
    public static String edit_cruise_lab;
    public static String edit_cruise_chief;
    public static String edit_cruise_project;
    public static String edit_cruise_sectionArchiving;
    public static String edit_cruise_sectionArchiving_lib;
    public static String edit_cruise_dataCenter;
    public static String edit_cruise_confident;
    public static String edit_cruise_sectionDataTypes;
    public static String edit_cruise_sectionDataTypes_lib;
    public static String edit_cruise_sectionComments;
    public static String edit_cruise_sectionComments_lib;
    public static String edit_cruise_openXML;
    
    public static String edit_station_title;
    public static String edit_station_sectionReference_lib;
    public static String edit_station_refIncrement;
    public static String edit_station_sectionDataType_lib;
    public static String edit_station_sectionHistory_lib;
    public static String edit_station_sectionComment_lib;
    public static String edit_station_sectionSurface_lib;
    //public static String edit_station_sectionOffsetTU_lib;
    public static String edit_station_sectionTime_lib;
    public static String edit_station_sectionDate_lib;
    public static String edit_station_sectionLat_lib;
    //public static String edit_station_sectionEndLat_lib;
    public static String edit_station_sectionLon_lib;
    //public static String edit_station_sectionEndLon_lib;
    public static String edit_station_sectionDepth_lib;
    //public static String edit_station_sectionEndDepth_lib;
    public static String edit_station_sectionSensorDepth_lib;
    public static String edit_station_sectionMagn_lib;
    public static String edit_station_sectionSampling_lib;
    public static String edit_station_sectionQCflags_lib;
    
    public static String edit_data_title;
    
    public static String edit_convert_title;
    public static String edit_convert_endODV;
    public static String edit_convert_endMED;
    public static String edit_convert_outputFilename;
    public static String edit_convert_outputDir;
    public static String edit_convert_sortStations;
    
    
    // RESULTATS
    public static String success_file_preformat;
    public static String progress_file_preformat;
    public static String progress_file_preformat_analyse;
    public static String progress_file_preformat_write;
    public static String error_notValid;
    public static String error_file_preformat;
    public static String error_file_selectFile;
    public static String error_file_selectDir;
    public static String error_file_idcEmpty;
    public static String error_cruise_openXml;
    public static String error_cruise_invalidXML;
    public static String error_data_removePRES;
    public static String error_data_firstPRES;
    public static String error_convert_invalideSteps;
    public static String error_convert_cancel;
    public static String error_convert_position;
    public static String error_convert_outputDir;
    public static String error_convert_outputFile;

    // VOCABULARY UPDATE 
    public static String connect_nemo;
    public static String connect_vocab;
    public static String connect_edmo;
    public static String update_barTitle;
    public static String update_title;
    public static String update_info;
    public static String update_nemolists;
    public static String update_data;
    public static String update_param;
    public static String update_survey;
    public static String update_vocablists;
    public static String update_edmo;
    public static String update_vocabmaps;
    public static String retrieved;
    public static String UPDATED;
    public static String CHECKED;
    public static String FAILED;
    public static String INTERRUPTED;
    
    static {
        // check config de la langue
        String langue = Utils.configuration.getLang();
        
        // initialize resource bundle
        StringBuffer bundleName = new StringBuffer(BUNDLE_NAME);
        if (langue.equals(Intern.lang_fr) || langue.equals(Intern.lang_en))
        {
            bundleName.append(Global.UNDERSCORE);
            bundleName.append(langue);
        }
        NLS.initializeMessages(bundleName.toString(), Messages.class);
    }

	private Messages() {
	}

	public static String getString(String key) 
    {
        try
        {
            // check config de la langue
            String langue = Utils.configuration.getLang();
            
            StringBuffer bundleName = new StringBuffer(BUNDLE_NAME);
            if (langue.equals(Intern.lang_fr) || langue.equals(Intern.lang_en))
            {
                bundleName.append(Global.UNDERSCORE);
                bundleName.append(langue);
            }
            ResourceBundle resource = ResourceBundle.getBundle(bundleName.toString());
            return resource.getString(key);
        } 
        catch (MissingResourceException e) 
        {
            return '!' + key + '!';
        }
	}
}
