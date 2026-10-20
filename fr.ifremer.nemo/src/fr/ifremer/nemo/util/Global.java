package fr.ifremer.nemo.util;

public interface Global {
	public static final String LOGGER_NAME_GUI = "nemoGui"; //$NON-NLS-1$
	public static final String LOGGER_NAME_BATCH = "nemoBatch"; //$NON-NLS-1$
	public static final String NEMO_ENCODING = "ISO-8859-15"; //$NON-NLS-1$
	public static final String NEMO_ODV_ENCODING = "ISO-8859-15"; //$NON-NLS-1$
	public static final String NEMO_MEDATLAS_ENCODING = "ISO-8859-15"; //$NON-NLS-1$
	public static final String NEMO_NETCDF_ENCODING = "ISO-8859-15"; //$NON-NLS-1$
	public static final String NEMO_EDMO_ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final String NEMO_EDMERP_ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final String NEMO_CDISUM_ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final String NEMO_CURVE_ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final String NEMO_SCRIPT_ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final int SIZE_SECTION = 270;
	public static final String SDN = "SDN"; //$NON-NLS-1$
	public static final String ZERO = "0"; //$NON-NLS-1$
	public static final String DEFAULT_FLAG = "9"; //$NON-NLS-1$
	public static final String WORSE_FLAG = "Z"; //$NON-NLS-1$
	// FAE 0001131 : remplacer "\n" par "\r\n" pour retour chariot sous bloc-note
	public static final String RN = "\r\n"; //$NON-NLS-1$
	public static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String STR_CONVERT = "x"; //$NON-NLS-1$
	public static final String UNDERSCORE = "_"; //$NON-NLS-1$
	public static final String SLASH = "/"; //$NON-NLS-1$
	public static final String SEP = " - "; //$NON-NLS-1$
	public static final String DBL_PT = ":"; //$NON-NLS-1$
	public static final String POINT = "."; //$NON-NLS-1$
	public static final String POURCENT = "%"; //$NON-NLS-1$
	public static final String ET_CO = "&"; //$NON-NLS-1$
	public static final String QUOTE = "\""; //$NON-NLS-1$

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
	public static final String EXT_NC = ".nc"; //$NON-NLS-1$
	public static final String EXT_CSV = ".csv"; //$NON-NLS-1$
	public static final String SVN_DIR = ".svn";//$NON-NLS-1$
	public static final String EXT_BAT = ".bat"; //$NON-NLS-1$
	public static final String EXT_SH = ".sh"; //$NON-NLS-1$

	// Séparateurs
	public static final String TAB = "\t"; //$NON-NLS-1$
	public static final String SEMICOLON = ";"; //$NON-NLS-1$
	public static final String COMMA = ","; //$NON-NLS-1$
	public static final String SPACE = " "; //$NON-NLS-1$

	// Chaîne vide
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	public static final String ANY_STRING = ".*"; //$NON-NLS-1$

	// Chaîne vide
	public static final String NO_VALUE = "NaN"; //$NON-NLS-1$

	// Types de table
	public static final String WSSURVEY = "WSSURVEY"; //$NON-NLS-1$
	public static final String IDEDMO = "IDEDMO"; //$NON-NLS-1$
	public static final String PARAMETER = "PARAMETER"; //$NON-NLS-1$
	public static final String DATATYPE = "DATATYPE"; //$NON-NLS-1$
	public static final String SHIP = "SHIP"; //$NON-NLS-1$
	public static final String DATACENTRE = "DATACENTRE"; //$NON-NLS-1$
	public static final String REGION = "REGION"; //$NON-NLS-1$
	public static final String SUBPARAMETER = "subParameter"; //$NON-NLS-1$

	public static final String VIA = "VIA"; //$NON-NLS-1$

	// batch name
	public static final String BATCH_NAME = "nemo_batch"; //$NON-NLS-1$
	// linux arguments
	public static final String optNoSplash = "nosplash"; //$NON-NLS-1$
	public static final String optBatch = "batch"; //$NON-NLS-1$
	
	// batch arguments
	public static final String optInputFile = "i"; //$NON-NLS-1$
	public static final String optOutputFile = "o"; //$NON-NLS-1$
	public static final String optModelFile = "m"; //$NON-NLS-1$
	public static final String optConv = "c"; //$NON-NLS-1$
	public static final String optOdvMono = "mono"; //$NON-NLS-1$
	public static final String optOdvMulti = "multi"; //$NON-NLS-1$
	public static final String optSortDatas = "sort"; //$NON-NLS-1$
	public static final String optDatasetRef = "datasetRef"; //$NON-NLS-1$
	public static final String optCruiseName = "cruiseName"; //$NON-NLS-1$
	public static final String optCruiseComment="cruiseComment"; //$NON-NLS-1$
	public static final String optLocalCsrIdentifier = "localCsrIdentifier"; //$NON-NLS-1$
	public static final String optBshCsrReference = "bshCsrReference"; //$NON-NLS-1$
	public static final String optShipCode = "shipCode"; //$NON-NLS-1$
	public static final String optCdiSummary = "cdiSummary"; //$NON-NLS-1$
	public static final String optCouplingTableUpdate = "couplingTableUpdate"; //$NON-NLS-1$
	public static final String optCouplingTablePrefix = "couplingTablePrefix"; //$NON-NLS-1$
	public static final String optUpdateVocabs = "updateVocabs"; //$NON-NLS-1$

	public static final String DATETIME_CHOICE_ISO= "ISO8601"; //$NON-NLS-1$
	public static final String DATETIME_CHOICE_JULIAN="JULIAN DAY";//$NON-NLS-1$
	
	// max size for a String representing EDMERP codes in MEDATLAS
	public static final int MAX_EDMERP_STRING_SIZE = 28;
	
	/*
	 * // Types de metadonnee
	 * public static final String DATETIME = "dateTime"; //$NON-NLS-1$
	 * public static final String ENDDATETIME = "endDateTime"; //$NON-NLS-1$
	 * // (Type de metadonnee calcules)
	 * public static final String DURATION = "duration"; //$NON-NLS-1$
	 * public static final String DISTANCETOBOTTOM = "distanceToBottom"; //$NON-NLS-1$
	 */
	public enum E_Availability {
		publicly, limited, confidential;

		public String getCode() {
			String code = Global.EMPTY_STRING;
			switch (this) {
			case confidential:
				code = "C";//$NON-NLS-1$
				break;
			case limited:
				code = "L";//$NON-NLS-1$
				break;
			default:
				code = "P";//$NON-NLS-1$
				break;
			}
			return code;
		}
	}

	public enum E_EndData {
		EOF, EMPTY_LINE, BEGIN_LINE_CHANGES, NEXT_START_WITH, END_WITH, COLUMNCHANGE;
	}

	// Types de fichier
	public enum E_InputType {
		PROFILE, TIMESERIES, TRAJECTORY, UNKNOWN;
	}
//	// Types de fichierdonnee
//	public enum E_Variant {
//			PHYSICOCHEMICAL, BIOLOGICAL, FLOWCYTO, MICROLITTER, UNKNOWN;
//		}
	// COLLECTION_DIR = group by cruise
	public enum E_ProcessType {
		CRUISE_FILE, CRUISE_DIR, COLLECTION_DIR, COLLECTION_FILES;
	}

	public enum E_TypeConversion {
		MEDATLAS, ODV_MULTISTATIONS, ODV_MONOSTATION, UNKNOWN, NETCDF_MONOSTATION, NETCDF_MULTISTATIONS;
	}

	public enum DataListTypeEnum {
		P011_VIA_P091, P091, P011_VIA_P021, UNKNOWN
	};

	public enum E_MesureType {
		BSSURF, BSBED, ASLVL, UNKNOWN
	};
}