package fr.ifremer.nemo.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

import fr.ifremer.nemo.Nemo;

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
    public static String onglet_file_error;
    public static String onglet_cruise_error;
    public static String onglet_station_error;
    public static String onglet_data_error;
    public static String onglet_file_valid;
    public static String onglet_cruise_valid;
    public static String onglet_station_valid;
    public static String onglet_data_valid;
    public static String success_etape;
    
    public static String button_browse;
    public static String button_validateStep;
    public static String button_validateAllSteps;
    public static String button_serverIni;
    public static String button_xmlIni;
    public static String button_reset;
    public static String button_set;
    public static String button_convert;
    public static String button_restore;
    
    public static String typeFile_profile;
    public static String typeFile_timeseries;
    public static String typeFile_trajectory;
    
    // ECRAN PRINCIPAL    
    public static String view_file_fileType;
    public static String view_file_processCruise;
    public static String view_file_cruiseFile;
    public static String view_file_cruiseDir;
    public static String view_file_processCollection;
    public static String view_file_collectionCas1;
    public static String view_file_collectionCasN;
    public static String view_file_multiStations;
    public static String view_file_separator;
    public static String view_file_sepTab;
    public static String view_file_sepSemi;
    public static String view_file_sepComma;
    public static String view_file_sepSpace;
    public static String view_file_sepOther;
    
    public static String view_convert_title;
    public static String view_convert_monoFichier;
    public static String view_convert_monoStation;
    public static String view_convert_sort;
    
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
    public static String table_label_number;
    public static String table_label_sensor;
    public static String table_label_qc;
    
    public static String menu_label_setValue;
    public static String menu_label_updateTest;
    public static String menu_label_paramList;
    public static String menu_label_upType;
    public static String menu_label_downType;
    public static String menu_label_removeType;
    public static String menu_label_upParam;
    public static String menu_label_downParam;
    public static String menu_label_removeParam;
    public static String menu_label_setFlag;
    public static String menu_label_deleteFlag;
    public static String menu_label_addFlag;
    public static String menu_label_removeFlag;
    public static String menu_label_list;
    
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
    public static String label_variable_comment;
    public static String label_test;
    public static String label_file;
    public static String label_string;
    public static String label_defaultValue;
    
    public static String confirm_saveModel;
    public static String confirm_quit;
    public static String confirm_eraseFile;
    public static String confirm_useDir;
    
    // EDIT
    public static String edit_file_title;
    public static String edit_file_enteteFichier;
    public static String edit_file_enteteStation;
    public static String edit_file_enteteStation_radNumber;
    public static String edit_file_enteteStation_setter;
    public static String edit_file_enteteStation_radIndicator;
    public static String edit_file_enteteStation_indicator;
    public static String edit_file_enteteStation_linesBeforeData;
    public static String edit_file_finStation;
    public static String edit_file_indicFinDonnees;
    public static String edit_file_idcEOF;
    public static String edit_file_idcEmptyLine;
    public static String edit_file_idcNextStation;
    public static String edit_file_idcManual;
    public static String edit_file_sectionRuptureDonnees_end_with;
    public static String edit_file_termination_start;
    
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
    public static String error_file_idcStartStationEmpty;
    public static String error_cruise_openXml;
    public static String error_cruise_invalidXML;
    public static String error_data_removePRES;
    public static String error_data_firstPRES;
    public static String error_data_firstDEPH_TRAJ;
    public static String error_convert_invalideSteps;
    public static String error_convert_cancel;
    public static String error_convert_position;
    public static String error_convert_outputDir;
    public static String error_convert_outputFile;
    
    public static String error_unknown_format;

    //ACTIONS
    public static String settings_typePlateForme;
    public static String settings_accesDonnees;
    public static String error_updateSurvey_url;
    public static String error_updateSurvey_distant;
    public static String error_updateSurvey_service;
    public static String menu_openModel;
    public static String error_openModel;
    public static String dialog_openModel;
    public static String success_openModel;
    public static String action_openSurveyDesc;
    public static String menu_settings;
    public static String settings_title;
    public static String settings_general;
    public static String settings_dir;
    public static String error_settings_dir;
    public static String settings_lang;
    public static String settings_lang_FR;
    public static String settings_lang_EN;
    public static String button_cancel;
    public static String button_validate;
    public static String settings_proxy;
    public static String settings_proxy_adr;
    public static String settings_proxy_port;
    public static String settings_proxy_login;
    public static String settings_proxy_pass;
    public static String warning_settings_title;
    public static String warning_settings;
    public static String error_settings_proxy_title;
    public static String error_settings_proxy;
    public static String settings_dir_model;
    public static String settings_dir_data;
    public static String settings_datacenter;
    public static String settings_EDMOauthor;
    public static String settings_dataDist_web;
    public static String settings_dataDist_met;
    public static String settings_EDMOori;
    public static String settings_EDMOcustodian;
    public static String settings_EDMOdist;
    public static String menu_label_search;
    public static String settings_web_vocab;
    public static String settings_mapping_fileName;
    public static String settings_flags;
    public static String table_label_label;
    public static String table_label_local;
    public static String table_label_seadatanet;
    public static String menu_label_add;
    public static String menu_label_remove;
    public static String menu_label_default;
    public static String menu_label_new;
    public static String error_table_code;
    public static String menu_label_load;
    public static String settings_cdi_check;
    public static String settings_mapping_check;
    public static String settings_web;
    public static String settings_EDMO;
    public static String settings_cdi;
    public static String settings_mapping;
    public static String settings_dateODV;
    public static String error_settings_cdi;
    public static String error_settings_mapping;
    public static String error_settings_config;
    public static String settings_web_nemo;
    public static String settings_web_nautilus;
    public static String settings_web_edmo;
    public static String error_missing;
    public static String error_table;
    public static String settings_parametersList;
    public static String settings_parameterListP091;
    public static String settings_parameterListP011;
    public static String settings_flagDefault;
    
    public static String table_edmo;
    public static String table_dataTypes;
    public static String table_parameters;
    public static String table_subparameters;
    public static String table_ships;
    public static String table_datacenter;
    public static String table_region;
    public static String table_survey;
    
    public static String table_label_filter;
    public static String button_select;
    public static String error_table_dataTypes;
    public static String error_table_parameters;
    public static String table_label_codeName;
    public static String success_openSurveyDesc;
    public static String table_label_refName;
    
    // VOCABULARY UPDATE 
    public static String connect_nemo;
    public static String connect_vocab;
    public static String connect_edmo;
    public static String update_shellTitle;
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
    
    // MAPPING
    public static String ImportMappingAction_1;
    public static String ImportMappingAction_5;
    public static String ExportMappingAction_1;
    public static String ExportMappingAction_5;
    public static String EditMappingAction_1;
    public static String InputPrefixDossier_idTitle;
    public static String InputPrefixDossier_idMessage;
    public static String CouplingTable_launch;
    public static String CouplingTable_error;
    public static String CouplingTable_warning;
    public static String CouplingTable_info;
    public static String CouplingTable_confirm;
    public static String CouplingTableAjout_localcdiid;
    public static String CouplingTableAjout_modus;
    public static String CouplingTableAjout_format;
    public static String CouplingTableAjout_filename;
    public static String CouplingTableAjout_1;
    public static String CouplingTableRefresh_1;
    public static String CouplingTableRefresh_2;
    public static String CouplingTableRefresh_3;
    public static String CouplingTableTable_localcdiid;
    public static String CouplingTableTable_modus;
    public static String CouplingTableTable_format;
    public static String CouplingTableTable_filename;
    public static String CouplingTableTable_date;
    public static String CouplingTableLabel_modus;
    public static String CouplingTableLabel_modus1;
    public static String CouplingTableLabel_modus3;
    public static String CouplingTableLabel_filename;
    public static String CouplingTableLabel_LocalCdiId;
    public static String CouplingTableLabel_format;
    public static String CouplingTableLabel_filter;
    public static String CouplingTableAction_cancel;
    public static String CouplingTableAction_save;
    public static String CouplingTableAction_exit;
    public static String CouplingTableAction_refresh;
    public static String CouplingTableAction_newrecord;
    public static String CouplingTableAction_delete;
    public static String CouplingTableImport_1;
    public static String CouplingTableImport_2;
    public static String CouplingTableImport_3;
    public static String CouplingTableImport_4;
    public static String CouplingTableImport_5;
    public static String CouplingTableImport_6;
    public static String CouplingTableImport_7;
    public static String CouplingTableImport_8;
    public static String CouplingTableExport_1;
    public static String CouplingTableExport_2;
    public static String CouplingTableExit_confirm;
    public static String askDataType_Title;
    
    // etape
    public static String Etape_24;
    public static String Etape_27;
    public static String Etape_28;
    public static String Etape_32;
    public static String Etape_33;
    public static String Etape_34;
    public static String Etape_36;
    public static String Etape_37;
    public static String Etape_38;
    public static String Etape_39;
    public static String Etape_43;
    public static String Etape_44;
    public static String Etape_52;
    public static String Etape_53;
    public static String Etape_55;
    public static String Etape_56;
    public static String Etape_58;
    public static String Etape_60;
    public static String Etape_61;
    public static String Etape_64;
    public static String Etape_67;
    public static String Etape_70;
    public static String Etape_77;
    public static String Etape_79;
    public static String Etape_81;
    public static String Etape_88;
    public static String Etape_98;
    public static String Etape_99;
    public static String Etape_102;
    public static String Etape_103;
    public static String Etape_103a;
    public static String Etape_103b;
    public static String Etape_106;
    public static String Etape_106b;
    public static String Etape_108;
    public static String Etape_109;
    public static String Etape_110;
    public static String Etape_111;
    public static String Etape_112;
    public static String Etape_113;
    public static String Etape_114;
    public static String Etape_115;
    public static String Etape_116;
    public static String Etape_117;
    public static String Etape_119;
    public static String Etape_121;
    public static String Etape_122;
    public static String Etape_124;
    public static String Etape_125;
    public static String Etape_127;
    public static String Etape_128;
    public static String Etape_129;
    public static String Etape_130;
    public static String Etape_131;
    public static String Etape_132;
    
    
    public static String SaveAction_1;
    public static String SaveAction_3;
    public static String SaveAction_4;
    public static String SaveAction_5;
    public static String SaveAction_6;
    public static String ViewLog_2;
    public static String ViewLog_3;
    public static String ViewLog_4;
    public static String ViewLog_5;
    public static String ViewLog_6;
    public static String ViewLog_7;
    public static String ViewLog_8;
    public static String Utils_4;
    public static String Utils_5;
    public static String Utils_6;
    public static String Utils_26;
    public static String Utils_27;
    public static String Utils_28;
    public static String Regexp_4;
    public static String Regexp_5;
    public static String Regexp_8;
    public static String Regexp_10;
    public static String Regexp_12;
    public static String Regexp_15;
    public static String Regexp_19;
    public static String Regexp_20;
    public static String Regexp_21;
    public static String Regexp_22;
    public static String SWTProcess_1;
    public static String SWTProcess_2;
    public static String SWTProcess_3;
    public static String SWTProcess_4;
    public static String SWTProcess_5;
    public static String SWTProcess_CDIresume;
    public static String CDIResumeWriter_choixFichier;
    public static String CDIResumeWriter_idTitle;
    public static String CDIResumeWriter_idMessage;
    public static String CDIResumeWriter_configMessage;
    public static String StationModel_1;
    public static String StationModel_2;
    public static String StationModel_3;
    public static String PropertyModel_1;
    public static String PropertyModel_2;
    public static String PropertyModel_3;
    public static String SingleFileSurvey_1;
    public static String ParameterModel_1;
    public static String ParameterModel_2;
    
    
    static {
        // check config de la langue
        String langue = Nemo.getInstance().getConfig().getLang();
        
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
            String langue = Nemo.getInstance().getConfig().getLang();
            
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
