package fr.ifremer.nemo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.util.Messages;

public class ViewLog extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewLog"; //$NON-NLS-1$

	private Composite top = null;

	public static TabFolder tabFolder = null;

	public static TabFolder tabFolder1 = null;

	public static TabFolder tabFolder2 = null;

	public static TabFolder tabFolder3 = null;

	private Composite compositeLogGauche = null;

	public static TabFolder tabFolderLog = null;

	public static StyledText styledTextLogErreurs, styledTextLogInfos,
			styledTextLogHelp = null;

	@Override
	public void createPartControl(Composite parent) {
		// 
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		createCompositeLogGauche();
		// createCompositeLogDroite();

	}

	@Override
	public void setFocus() {
		// 

	}

	/**
	 * This method initializes compositeLogGauche
	 * 
	 */
	private void createCompositeLogGauche() {
		compositeLogGauche = new Composite(top, SWT.NONE);
		compositeLogGauche.setLayout(new FillLayout());
		createTabFolderLog();
	}

	/**
	 * This method initializes tabFolderLog
	 * 
	 */
	private void createTabFolderLog() {
		tabFolderLog = new TabFolder(compositeLogGauche, SWT.NONE);
		tabFolderLog
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
//						System.out.println("widgetSelected()");
					}
				});
		TabItem tabItem = new TabItem(tabFolderLog, SWT.NONE);
		tabItem.setText(Messages.ViewLog_2);
		TabItem tabItem1 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem1.setText(Messages.ViewLog_3);
		TabItem tabItem2 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem2.setText(Messages.ViewLog_4);
		styledTextLogErreurs = new StyledText(tabFolderLog, SWT.MULTI
				| SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextLogInfos = new StyledText(tabFolderLog, SWT.MULTI
				| SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextLogHelp = new StyledText(tabFolderLog, SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		tabItem2.setControl(styledTextLogHelp);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

	public static void aideDesc() {
		String langue = Utils.configuration.getLang();
		if (langue.equals("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==MODEL==\n"
                        + "\n"
                        + "1/ Model/Open model : loads a model only if a file or a directory containing data files has previously been opened.\n"
                        + "2/ Model/Save model : creates on the disk a XML-type file corresponding to the values entered in the different steps.\n"
                        + "    It is fonctional only once the first 4 steps have been validated.\n"
                        + "3/ Quit : shuts down the application after having been asked to confirm.\n"
                        + "\n"
                        + "==COUPLING TABLE==\n"
                        + "\n"
                        + "This menu allows the management of the coupling table to be able to generate coupling file used by SeaDataNet download manager.\n"
                        + "Coupling file is used by SeaDataNet download manager to make the mapping between a LOCAL_CDI_ID (one profile, one time-series or one trajectory) \n"
                        + "and the file containing this LOCAl_CDI_ID.\n"
                        + "1/ Coupling Table/Import: to import a coupling file into the coupling table of NEMO.\n"
                        + "2/ Coupling Table/Export: to export the coupling table of NEMO into a coupling file.\n"
                        + "3/ Coupling Table/Edit: to modify the content of the coupling table of NEMO : delete records, insert new records, update existing records.\n"
                        + "\n"
                        + "==OPTIONS==\n"
                        + "\n"
                        + "1/ Options/Settings: define the configuration of NEMO such as default values for several information, web services addresses, Qc flags mapping, ...\n"
                        + "2/ Options/Vocabulary update : update NEMO with the last versions of the common vocabulary of SeaDatanet, EDMO.\n"
                        + "\n"
                        + "==FILE==\n"
                        + "\n"
                        + "==Left part of the screen :\n"
                        + "1/ First select the type of file you want to process \n"
                        + "2/ Then 'Browse' Button  and choose a file or directory.\n"
                        + "3/ If needed, load a model via the model/open menu and by choosing the XML file of the model you want to use for the current dataset.\n"
                        + "4/ If the file contains separators, click on separator and choose the correct separator.\n"
                        + "5/ Select the type of data you are processing : vertical profiles, time-series or trajectories.\n"
                        + "\n"
                        + "==Right part of the screen :\n"
                        + "Once the type of file and the input file or directory is choosen, file description appears on the right of NEMO screen.\n"
                        + " - Validate Button : One step must be validated before moving to the next one.\n"
                        + " - Reset button : erase all the values of the current step\n"
                        + "\n"
                        + "4 sections are available : Click on each section and modify the values if necessary\n"
                        + "\n"
                        + "1/ File header : This field describes the number of lines of the file header. A file header is generally present when it is a multi-station file, \n"
                        + "    and that there are some lines at the beginning of the file which are related to all the stations. These lines are not repeated in the file.\n"
                        + "    The number of lines of the file header can be input manually or selected in the left part of the screen \n"
                        + "    in the file and then input through the \"Set\" button.\n"
                        + "\n"
                        + "2/ Station header : This field describes the number of lines of the station header. \n"
                        + "    A station header is a set of lines containing information on the station. \n"
                        + "    Theses lines are the lines before the data lines; they are repeated for each station.\n"
                        + "    The number of lines of the station header can be input manually or selected in the left part of the screen \n"
                        + "    in the file and then input through the \"Set\" button \n"
                        + "\n"
                        + "3/ End of station : This field describes the number of lines of the end of a station.\n" 
                        + "    These lines are generally present when it is a multi-station file and there are several lines between each station.\n"
                        + "    The indicator of end of data (see below) must not be taken into account in this number of lines.\n"
                        + "\n" 
                        + "4/ Indicator of end of data : This field describes the data rupture indication.\n" 
                        + "    - For single station file, most of the time the end of file (EOF) is the indicator of end of data, but it may be an empty line at the end of the file or a line with a specific text.\n"
                        + "    - For multiple stations file, NEMO needs a separator between the stations to be able to know that station is changing. This separator can be an empty line or a set of specific characters.\n"
                        + "    For example, in a MEDATLAS file, the indicator of end of data is the line starting with \"-999.9 99.999\". It is not necessary to type all the line; the first significant characters are enough.\n"
                        + "\n"
                        + "    For each of these sections the number of lines or the specific set of characters can be input manually\n" 
                        + "    or selected in the left part of the screen in the file and then input through the \"Set\" button.\n"
                        + "\n"
                        + "==Bottom part of the screen\n"
                        + "3 tabs are available\n" 
                        + "1/ Errors tab : This tab gives information about the validation of the current step.\n"
                        + "    If the information is an error, it is written in red, if not it is written in green\n"
                        + "\n"
                        + "2/ Info tab : This tab gives information on the input file or the input directory,\n" 
                        + "    and also on the used model, if a model has been loaded.\n"
                        + "\n"
                        + "3/ Help tab : displays at each step the corresponding help.\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==MODEL==\n"
                        + "\n"
                        + "1/ Model/Open model : loads a model only if a file or a directory containing data files has previously been opened.\n"
                        + "2/ Model/Save model : creates on the disk a XML-type file corresponding to the values entered in the different steps.\n"
                        + "    It is fonctional only once the first 4 steps have been validated.\n"
                        + "3/ Quit : shuts down the application after having been asked to confirm.\n"
                        + "\n"
                        + "==COUPLING TABLE==\n"
                        + "\n"
                        + "This menu allows the management of the coupling table to be able to generate coupling file used by SeaDataNet download manager.\n"
                        + "Coupling file is used by SeaDataNet download manager to make the mapping between a LOCAL_CDI_ID (one profile, one time-series or one trajectory) \n"
                        + "and the file containing this LOCAl_CDI_ID.\n"
                        + "1/ Coupling Table/Import: to import a coupling file into the coupling table of NEMO.\n"
                        + "2/ Coupling Table/Export: to export the coupling table of NEMO into a coupling file.\n"
                        + "3/ Coupling Table/Edit: to modify the content of the coupling table of NEMO : delete records, insert new records, update existing records.\n"
                        + "\n"
                        + "==OPTIONS==\n"
                        + "\n"
                        + "1/ Options/Settings: define the configuration of NEMO such as default values for several information, web services addresses, Qc flags mapping, ...\n"
                        + "2/ Options/Vocabulary update : update NEMO with the last versions of the common vocabulary of SeaDatanet, EDMO.\n"
                        + "\n"
                        + "==FILE==\n"
                        + "\n"
                        + "==Left part of the screen :\n"
                        + "1/ First select the type of file you want to process \n"
                        + "2/ Then 'Browse' Button  and choose a file or directory.\n"
                        + "3/ If needed, load a model via the model/open menu and by choosing the XML file of the model you want to use for the current dataset.\n"
                        + "4/ If the file contains separators, click on separator and choose the correct separator.\n"
                        + "5/ Select the type of data you are processing : vertical profiles, time-series or trajectories.\n"
                        + "\n"
                        + "==Right part of the screen :\n"
                        + "Once the type of file and the input file or directory is choosen, file description appears on the right of NEMO screen.\n"
                        + " - Validate Button : One step must be validated before moving to the next one.\n"
                        + " - Reset button : erase all the values of the current step\n"
                        + "\n"
                        + "4 sections are available : Click on each section and modify the values if necessary\n"
                        + "\n"
                        + "1/ File header : This field describes the number of lines of the file header. A file header is generally present when it is a multi-station file, \n"
                        + "    and that there are some lines at the beginning of the file which are related to all the stations. These lines are not repeated in the file.\n"
                        + "    The number of lines of the file header can be input manually or selected in the left part of the screen \n"
                        + "    in the file and then input through the \"Set\" button.\n"
                        + "\n"
                        + "2/ Station header : This field describes the number of lines of the station header. \n"
                        + "    A station header is a set of lines containing information on the station. \n"
                        + "    Theses lines are the lines before the data lines; they are repeated for each station.\n"
                        + "    The number of lines of the station header can be input manually or selected in the left part of the screen \n"
                        + "    in the file and then input through the \"Set\" button \n"
                        + "\n"
                        + "3/ End of station : This field describes the number of lines of the end of a station.\n" 
                        + "    These lines are generally present when it is a multi-station file and there are several lines between each station.\n"
                        + "    The indicator of end of data (see below) must not be taken into account in this number of lines.\n"
                        + "\n" 
                        + "4/ Indicator of end of data : This field describes the data rupture indication.\n" 
                        + "    - For single station file, most of the time the end of file (EOF) is the indicator of end of data, but it may be an empty line at the end of the file or a line with a specific text.\n"
                        + "    - For multiple stations file, NEMO needs a separator between the stations to be able to know that station is changing. This separator can be an empty line or a set of specific characters.\n"
                        + "    For example, in a MEDATLAS file, the indicator of end of data is the line starting with \"-999.9 99.999\". It is not necessary to type all the line; the first significant characters are enough.\n"
                        + "\n"
                        + "    For each of these sections the number of lines or the specific set of characters can be input manually\n" 
                        + "    or selected in the left part of the screen in the file and then input through the \"Set\" button.\n"
                        + "\n"
                        + "==Bottom part of the screen\n"
                        + "3 tabs are available\n" 
                        + "1/ Errors tab : This tab gives information about the validation of the current step.\n"
                        + "    If the information is an error, it is written in red, if not it is written in green\n"
                        + "\n"
                        + "2/ Info tab : This tab gives information on the input file or the input directory,\n" 
                        + "    and also on the used model, if a model has been loaded.\n"
                        + "\n"
                        + "3/ Help tab : displays at each step the corresponding help.\n");
		}
	}

	public static void aideSurvey() {
		String langue = Utils.configuration.getLang();
		if (langue.equals("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==CRUISE DESCRIPTION==\n"
                        + "\n"
                        + "The information on this screen are necessary for MEDATLAS format.\n"
                        + " - Validate Button : One step must be validated once all information have been input, before moving to the next one.\n"
                        + " - Reset button : Erase all the values of the current step\n"
                        + "\n"
                        + "3 possibilities for fulfilling this step: \n"
                        + "\n"
                        + "1/ Enter all the values manually, \n"
                        + "2/ Ask the webservices by clicking on \"Server initialisation\" (IFREMER only) or \n"
                        + "3/ Load a XML Cruise Summary Report (CSR) file by clicking on \"XML Initialisation\".\n"
                        + "\n"
                        + "6 sections are available : Click on each section and modify the values if necessary\n"
                        + "\n"
                        + "1/ Reference : cruise reference, cruise name and ship (SeaDataNet List C174).\n"
                        + "2/ Location : Data and end date, Sea area (SeaDataNet List C16)\n"
                        + "3/ Data source : source country (SeaDataNet List C180), laboratory, project and chief scientist \n"
                        + "4/ Archiving centre : MEDATLAS code of the data archiving center (define in NEMO settings), MEDATLAS code for data availability.\n"
                        + "5/ Type of data : ROSCOP code for data type (SeaDataNet List C77)\n"
                        + "6/ Comments : Free text\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==CRUISE DESCRIPTION==\n"
                        + "\n"
                        + "The information on this screen are necessary for MEDATLAS format.\n"
                        + " - Validate Button : One step must be validated once all information have been input, before moving to the next one.\n"
                        + " - Reset button : Erase all the values of the current step\n"
                        + "\n"
                        + "3 possibilities for fulfilling this step: \n"
                        + "\n"
                        + "1/ Enter all the values manually, \n"
                        + "2/ Ask the webservices by clicking on \"Server initialisation\" (IFREMER only) or \n"
                        + "3/ Load a XML Cruise Summary Report (CSR) file by clicking on \"XML Initialisation\".\n"
                        + "\n"
                        + "6 sections are available : Click on each section and modify the values if necessary\n"
                        + "\n"
                        + "1/ Reference : cruise reference, cruise name and ship (SeaDataNet List C174).\n"
                        + "2/ Location : Data and end date, Sea area (SeaDataNet List C16)\n"
                        + "3/ Data source : source country (SeaDataNet List C180), laboratory, project and chief scientist \n"
                        + "4/ Archiving centre : MEDATLAS code of the data archiving center (define in NEMO settings), MEDATLAS code for data availability.\n"
                        + "5/ Type of data : ROSCOP code for data type (SeaDataNet List C77)\n"
                        + "6/ Comments : Free text\n");
		}
	}

	public static void aideStation() {
		String langue = Utils.configuration.getLang();
		if (langue.equals("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==STATION DESCRIPTION==\n"
                        + "\n"
                        + "This step is performed to indicate to NEMO where to find the mandatory or important information in the station header(s) of the file(s). \n"
                        + "This information is changing as it deals with vertical profiles, time series or trajectories. \n"
                        + "\n"
                        + "==For vertical profiles 12 sections are available : Click on each section and modify the values if necessary\n"
                        + "Mandatory fields are: Station number, Data type, Time, Date, Latitude and Longitude. \n"
                        + "\n"
                        + "1/ Station number : The station number can be incremental starting at a number given by the user or can be in the station header and input in NEMO using the set and test buttons.\n"
                        + "          If station's number is located within the filename, use 0 as line number, and give the start and end character in the file name.   \n"
                        + "\n"
                        + "2/ Data type : It is the ROSCOP code of the instrument (H10 - Ctd for example) used for data collection; it is selected in the list of data types input during the previous step (cruise description).\n"
                        + "          If processing a collection of files, \"Data type\" field is not available, it will be asked during conversion step\n"
                        + "\n"
                        + "3/ Acquisition history : They are 2 lines for comments about method and/or instrument used for data collection. They can be manually or automatically input using the set button.\n"
                        + "\n"
                        + "4/ Comments : Comments about the station can be manually or automatically input. There is no limitation on the number of comment lines.\n"
                        + "          If the comment is spread among several lines which do not have the same length, click on \"Variable end of line position\".\n"
                        + "\n"
                        + "5/ Surface samples : Same principle as Acquisition history, 2 lines for comments about surface samples if there are some.\n"
                        + "\n"
                        + "6/ UT/Conversion : This field is to manage non universal time in the input files.\n"
                        + "          If station time in the input file is TU-5, type 5 (or read by automatic input) in the UT/Conversion field and NEMO will add 5 hours to the input time. If time in the input file is TU+5, type -5 in the field.\n"
                        + "          The default value is zero\n"
                        + "\n"
                        + "7/ Time : input the format in which time is provided in the input file and then, select time in the file and press Set and Test.\n"
                        + "          Format is either predefined but can be input by the user within the following :\n"
                        + "               hh: hour expressed in 2 digits (hh24 indicates time on 24 hours)\n"
                        + "               mm: minutes expressed in 2 digits\n"
                        + "               cc: hundredth in the time expressed in 2 digits\n"
                        + "               ss: seconds expressed in 2 digits\n"
                        + "               ttttt: seconds expressed in 1 up to 5 digits\n"
                        + "               PM: allows NEMO to read PM and AM time\n"
                        + "\n"
                        + "8/ Date : input the format in which time is provided in the input file and then, select time in the file and press Set and Test.\n"
                        + "          Format is either predefined but can be input by the user within the following :\n"
                        + "               YY or YYYY: year expressed in 2 or 4 digits\n"
                        + "               MM: month expressed in 2 digits\n"
                        + "               MMM: month expressed in 3 alphabetic characters\n"
                        + "               DD: day in the month expressed in 2 digits\n"
                        + "\n"
                        + "9-10/ Latitude and Longitude : input the format in which Latitude is provided in the input file and then, select time in the file and press Set and Test.\n"
                        + "          Format is either predefined but can be input by the user within the following :\n"
                        + "               N: expresses the hemisphere. Input values have to equal N or S in the case of latitudes and W or E in the case of longitudes\n"
                        + "               +: expresses the hemisphere. Input values have to equal ' ' or '+' to express N in the case of latitudes, E in the case of longitudes\n" 
                        + "                    and '-' to express S in the case of latitudes, W in the case of longitudes\n"
                        + "               DDD or DD or D: Degrees \n"
                        + "               DDD.ddd or DD.ddd: Degrees in decimal values. The maximum number of D must be 3 in the case of longitudes and 2 in the case of latitudes\n" 
                        + "                    The number of 'd' characters expresses the number of decimals.\n"
                        + "               MM: Minutes\n"
                        + "               MM.mm: Minutes in decimal values. The number of 'm' characters expresses the number of decimals.\n"
                        + "               SS: Seconds\n"
                        + "\n"   
                        + "               Click on \"variable end of line position\" if all latitudes and longitudes do not end at the same character in every station (this often happens with not significant zero values).\n"
                        + "\n"
                        + "11/ Depth :  Bottom depth of stations, must be given in meter in the input file. This field can be input manually or automatically.\n"
                        + "\n"
                        + "12/ QC flags : quality flags on station header information that could exists in the input file.\n"
                        + "          Header QC flags are only used in MEDATLAS format, they concerns Date/Time, Latitude, Longitude and Bottom depth.\n"
                        + "\n"
                        + "==For time-series 6 more sections are available :\n"
                        + "Mandatory fields are: Station number, Data type, Time, Date, Latitude, Longitude and Sensor's depth\n"
                        + "\n"
                        + "13-14/ End Latitude and End Longitude: latitude and longitude of the end time of time series.\n" 
                        + "          By default, if not input, the values will be the same than \"Latitude\" and \"Longitude\" which are the position at the start time of the time series.\n"
                        + "          Input in these fields is the same than for \"Latitude\" and \"Longitude\" described for vertical profiles.\n"
                        + "\n"
                        + "15/ End Depth : Bottom depth at the end time of time series.\n" 
                        + "          By default, if not input, the value will be the same than Depth which is as the bottom depth at the start time of the time series.\n"
                        + "          Input in this field is the same than for \"Depth\" described for vertical profiles.\n"
                        + "\n"
                        + "16/ Sensor Depth : Depth of the sensor measuring time series. Given in meter in the input file. This field can be input manually or automatically.\n"
                        + "\n"
                        + "17/ Magnetic declination : Magnetic declination at the location of the time series, useful for current meters.\n" 
                        + "          It is a signed integer:  W007 declination must be input as -7 and E005 as +5 or 5.\n" 
                        + "          This field can be input manually or automatically.\n"
                        + "\n"
                        + "18/ Sampling rate : Sampling rate of the sensors. This field can be input manually, or automatically. \n"
                        + "          NEMO will calculate it automatically if time and date are selected in the first line of measurements. \n"
                        + "          The unit of the sampling rate is Second. If it is not given in second in the input file, it must be converted by using the field \"Conversion formula\" \n"
                        + "          (example : if the sampling rate is given in Hour in the input file, input *3600 in the formula).\n"
                        + "\n"
                        + "== For trajectories : 14 sections are available already described for Vertical profiles and Time-series.\n"
                        + "Mandatory fields are: Station number, Data type, Time, Date, Latitude and Longitude. \n"
                        + "\n"
                        + "    Station number : In ODV, a trajectory is considered as a series of stations with one point of measurement \n"
                        + "          at each station, but the LOCAL_CDI_ID is the same for all the stations. \n"
                        + "          In MEDATLAS a trajectory is only one station within which latitude and longitude are considered as measured parameters. \n"
                        + "          The station number cannot be incremental.\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==STATION DESCRIPTION==\n"
                        + "\n"
                        + "This step is performed to indicate to NEMO where to find the mandatory or important information in the station header(s) of the file(s). \n"
                        + "This information is changing as it deals with vertical profiles, time series or trajectories. \n"
                        + "\n"
                        + "==For vertical profiles 12 sections are available : Click on each section and modify the values if necessary\n"
                        + "Mandatory fields are: Station number, Data type, Time, Date, Latitude and Longitude. \n"
                        + "\n"
                        + "1/ Station number : The station number can be incremental starting at a number given by the user or can be in the station header and input in NEMO using the set and test buttons.\n"
                        + "          If station's number is located within the filename, use 0 as line number, and give the start and end character in the file name.   \n"
                        + "\n"
                        + "2/ Data type : It is the ROSCOP code of the instrument (H10 - Ctd for example) used for data collection; it is selected in the list of data types input during the previous step (cruise description).\n"
                        + "          If processing a collection of files, \"Data type\" field is not available, it will be asked during conversion step\n"
                        + "\n"
                        + "3/ Acquisition history : They are 2 lines for comments about method and/or instrument used for data collection. They can be manually or automatically input using the set button.\n"
                        + "\n"
                        + "4/ Comments : Comments about the station can be manually or automatically input. There is no limitation on the number of comment lines.\n"
                        + "          If the comment is spread among several lines which do not have the same length, click on \"Variable end of line position\".\n"
                        + "\n"
                        + "5/ Surface samples : Same principle as Acquisition history, 2 lines for comments about surface samples if there are some.\n"
                        + "\n"
                        + "6/ UT/Conversion : This field is to manage non universal time in the input files.\n"
                        + "          If station time in the input file is TU-5, type 5 (or read by automatic input) in the UT/Conversion field and NEMO will add 5 hours to the input time. If time in the input file is TU+5, type -5 in the field.\n"
                        + "          The default value is zero\n"
                        + "\n"
                        + "7/ Time : input the format in which time is provided in the input file and then, select time in the file and press Set and Test.\n"
                        + "          Format is either predefined but can be input by the user within the following :\n"
                        + "               hh: hour expressed in 2 digits (hh24 indicates time on 24 hours)\n"
                        + "               mm: minutes expressed in 2 digits\n"
                        + "               cc: hundredth in the time expressed in 2 digits\n"
                        + "               ss: seconds expressed in 2 digits\n"
                        + "               ttttt: seconds expressed in 1 up to 5 digits\n"
                        + "               PM: allows NEMO to read PM and AM time\n"
                        + "\n"
                        + "8/ Date : input the format in which time is provided in the input file and then, select time in the file and press Set and Test.\n"
                        + "          Format is either predefined but can be input by the user within the following :\n"
                        + "               YY or YYYY: year expressed in 2 or 4 digits\n"
                        + "               MM: month expressed in 2 digits\n"
                        + "               MMM: month expressed in 3 alphabetic characters\n"
                        + "               DD: day in the month expressed in 2 digits\n"
                        + "\n"
                        + "9-10/ Latitude and Longitude : input the format in which Latitude is provided in the input file and then, select time in the file and press Set and Test.\n"
                        + "          Format is either predefined but can be input by the user within the following :\n"
                        + "               N: expresses the hemisphere. Input values have to equal N or S in the case of latitudes and W or E in the case of longitudes\n"
                        + "               +: expresses the hemisphere. Input values have to equal ' ' or '+' to express N in the case of latitudes, E in the case of longitudes\n" 
                        + "                    and '-' to express S in the case of latitudes, W in the case of longitudes\n"
                        + "               DDD or DD or D: Degrees \n"
                        + "               DDD.ddd or DD.ddd: Degrees in decimal values. The maximum number of D must be 3 in the case of longitudes and 2 in the case of latitudes\n" 
                        + "                    The number of 'd' characters expresses the number of decimals.\n"
                        + "               MM: Minutes\n"
                        + "               MM.mm: Minutes in decimal values. The number of 'm' characters expresses the number of decimals.\n"
                        + "               SS: Seconds\n"
                        + "\n"   
                        + "               Click on \"variable end of line position\" if all latitudes and longitudes do not end at the same character in every station (this often happens with not significant zero values).\n"
                        + "\n"
                        + "11/ Depth :  Bottom depth of stations, must be given in meter in the input file. This field can be input manually or automatically.\n"
                        + "\n"
                        + "12/ QC flags : quality flags on station header information that could exists in the input file.\n"
                        + "          Header QC flags are only used in MEDATLAS format, they concerns Date/Time, Latitude, Longitude and Bottom depth.\n"
                        + "\n"
                        + "==For time-series 6 more sections are available :\n"
                        + "Mandatory fields are: Station number, Data type, Time, Date, Latitude, Longitude and Sensor's depth\n"
                        + "\n"
                        + "13-14/ End Latitude and End Longitude: latitude and longitude of the end time of time series.\n" 
                        + "          By default, if not input, the values will be the same than \"Latitude\" and \"Longitude\" which are the position at the start time of the time series.\n"
                        + "          Input in these fields is the same than for \"Latitude\" and \"Longitude\" described for vertical profiles.\n"
                        + "\n"
                        + "15/ End Depth : Bottom depth at the end time of time series.\n" 
                        + "          By default, if not input, the value will be the same than Depth which is as the bottom depth at the start time of the time series.\n"
                        + "          Input in this field is the same than for \"Depth\" described for vertical profiles.\n"
                        + "\n"
                        + "16/ Sensor Depth : Depth of the sensor measuring time series. Given in meter in the input file. This field can be input manually or automatically.\n"
                        + "\n"
                        + "17/ Magnetic declination : Magnetic declination at the location of the time series, useful for current meters.\n" 
                        + "          It is a signed integer:  W007 declination must be input as -7 and E005 as +5 or 5.\n" 
                        + "          This field can be input manually or automatically.\n"
                        + "\n"
                        + "18/ Sampling rate : Sampling rate of the sensors. This field can be input manually, or automatically. \n"
                        + "          NEMO will calculate it automatically if time and date are selected in the first line of measurements. \n"
                        + "          The unit of the sampling rate is Second. If it is not given in second in the input file, it must be converted by using the field \"Conversion formula\" \n"
                        + "          (example : if the sampling rate is given in Hour in the input file, input *3600 in the formula).\n"
                        + "\n"
                        + "== For trajectories : 14 sections are available already described for Vertical profiles and Time-series.\n"
                        + "Mandatory fields are: Station number, Data type, Time, Date, Latitude and Longitude. \n"
                        + "\n"
                        + "    Station number : In ODV, a trajectory is considered as a series of stations with one point of measurement \n"
                        + "          at each station, but the LOCAL_CDI_ID is the same for all the stations. \n"
                        + "          In MEDATLAS a trajectory is only one station within which latitude and longitude are considered as measured parameters. \n"
                        + "          The station number cannot be incremental.\n");
		}
	}

	public static void aideData() {
		String langue = Utils.configuration.getLang();
		if (langue.equals("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==DATA DESCRIPTION==\n"
                        + "\n"
                        + "This step is performed to indicate NEMO where to find the data measurements in input file(s). \n"
                        + "This information concern the name and unit of the measured parameters, their default values (found in the input file \n"
                        + "when the parameter is not measured), and the format in which the parameter will be kept in the output file.\n"
                        + "\n"
                        + "- Forvertical profiles, PRES (for sea pressure in decibars) is a mandatory parameter; \n"
                        + "   it is input by default when entering the step, and it cannot be deleted (it is a MEDATLAS requirement).\n"
                        + "\n"
                        + "- For time series, date and time of measurements will be automatically put in output files,\n"
                        + "   they do not need to be described in the measurement description.\n"
                        + "\n"
                        + "- For trajectories date, time, latitude, longitude and bottom depth (if existing) of measurements \n"
                        + "   will be automatically put in output files, they do not need to be described in the measurement description.\n"
                        + "\n"
                        + "==Right click on the table of measured parameters to open the Menu with the following items :\n"
                        + "=============================================================================================\n"
                        + "- Set start/end:\n"
                        + "   To set the start and end character of the selected parameter in the data line. \n"
                        + "   The value of the parameter has to be highlighted on the first data line before using this menu.\n"
                        + "\n"
                        + "- Update test:\n"
                        + "   To insert the value in the test fields including : \"Test\" (for the measured parameters), \"Test (input)\" \n"
                        + "   for the value of the quality flag read in the input file and \"Test (output)\" for the value of the quality flag \n"
                        + "   in the output file. \n"
                        + "   To input value in the test field, right click in the table and choose \"Update test\" in the menu \n"
                        + "\n"
                        + "- Parameter list:\n"
                        + "   To add a new parameter in the table of measured parameters.\n"
                        + "\n"
                        + "- Move current type up:\n"
                        + "   To move up a parameter in the table of measured parameters.\n" 
                        + "  The order of parameters in the table defines the order of parameters in the output file.\n" 
                        + "\n"
                        + "- Move current type down:\n"
                        + "   To move down a parameter in the table of measured parameters.\n" 
                        + "   The order of parameters in the table defines the order of parameters in the output file.\n" 
                        + "\n"
                        + "- Delete current type:\n"
                        + "   To delete a parameter from the table of measured parameters.\n"
                        + "\n"
                        + "- Set flag:\n"
                        + "   To set the start and end character of the quality flag the selected parameter.\n" 
                        + "   The value of the flag has to be highlighted on the first data line before using this menu.\n"
                        + "\n"
                        + "- Delete flag:\n"
                        + "   To delete the quality flag information of the selected parameter.\n" 
                        + "\n"
                        + "==The table of measured parameters contains the following columns :\n"
                        + "===================================================================\n"
                        + "- Code : MEDATLAS code of the parameter\n"
                        + "\n"
                        + "- Unit : MEDATLAS Unit of the parameter\n"
                        + "\n"
                        + "- Conversion : to input a conversion formula which should be like x*1000+3; with x the value read in the input file(s).\n"
                        + "\n"
                        + "- Test : This field displays the value as it will be written in the output file.\n" 
                        + "     To input value in this field, right click in the table and choose \"Update test\" in the menu;\n" 
                        + "     the start and end fields must have been input before.\n"
                        + "\n"
                        + "- Start - End : These fields indicate the start character and the end character of the parameter in the data line.\n"
                        + "     To input the values in these fields, you have to select the measured parameter value on the first line of measurement, then right click in the table and choose \"Set start/end\".\n"
                        + "\n"
                        + "- Format : This field describes the output format (in the output file) of the measured parameter.\n"
                        + "     Examples of valid format : %3d for 150, %+4d for +150 or -150, %6.3f for 12.124, %+7.5f for +1.12345 or -1.12345\n" 
                        + "\n"
                        + "- Input def. value : This field describes the values found in the input file where the parameter is not measured.\n"
                        + "     This input value can be any type of characters (number, alphabetic).\n"
                        + "\n"
                        + "- Output def. value : This field describes the values written in the output file where the parameter is not measured.\n"
                        + "     The output value must be a number (often a suite of '9') at the same format than the measured parameters described in the format column.\n"
                        + "\n"
                        + "- Test (input) : This field displays the value of the QC flag in the input file.\n" 
                        + "\n"
                        + "- Test (Output) : This field displays the value of the corresponding QC flag that will be wriiten in the output file.\n"
                        + "     To input value in this field, right click in the table and choose \"Update test\" in the menu; \n"
                        + "     the start (flag) and end (flag) fields must have been input before.\n"
                        + "\n"
                        + "- Start (Flag) - End (Flag): These fields indicate the start character and the end character of the QC flag in the data line.\n"
                        + "     To input the values in these fields, you have to select the QC flag value on the first \n"
                        + "     line of measurement, then right click in the table and choose \"Set flag\"\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==DATA DESCRIPTION==\n"
                        + "\n"
                        + "This step is performed to indicate NEMO where to find the data measurements in input file(s). \n"
                        + "This information concern the name and unit of the measured parameters, their default values (found in the input file \n"
                        + "when the parameter is not measured), and the format in which the parameter will be kept in the output file.\n"
                        + "\n"
                        + "- Forvertical profiles, PRES (for sea pressure in decibars) is a mandatory parameter; \n"
                        + "   it is input by default when entering the step, and it cannot be deleted (it is a MEDATLAS requirement).\n"
                        + "\n"
                        + "- For time series, date and time of measurements will be automatically put in output files,\n"
                        + "   they do not need to be described in the measurement description.\n"
                        + "\n"
                        + "- For trajectories date, time, latitude, longitude and bottom depth (if existing) of measurements \n"
                        + "   will be automatically put in output files, they do not need to be described in the measurement description.\n"
                        + "\n"
                        + "==Right click on the table of measured parameters to open the Menu with the following items :\n"
                        + "=============================================================================================\n"
                        + "- Set start/end:\n"
                        + "   To set the start and end character of the selected parameter in the data line. \n"
                        + "   The value of the parameter has to be highlighted on the first data line before using this menu.\n"
                        + "\n"
                        + "- Update test:\n"
                        + "   To insert the value in the test fields including : \"Test\" (for the measured parameters), \"Test (input)\" \n"
                        + "   for the value of the quality flag read in the input file and \"Test (output)\" for the value of the quality flag \n"
                        + "   in the output file. \n"
                        + "   To input value in the test field, right click in the table and choose \"Update test\" in the menu \n"
                        + "\n"
                        + "- Parameter list:\n"
                        + "   To add a new parameter in the table of measured parameters.\n"
                        + "\n"
                        + "- Move current type up:\n"
                        + "   To move up a parameter in the table of measured parameters.\n" 
                        + "  The order of parameters in the table defines the order of parameters in the output file.\n" 
                        + "\n"
                        + "- Move current type down:\n"
                        + "   To move down a parameter in the table of measured parameters.\n" 
                        + "   The order of parameters in the table defines the order of parameters in the output file.\n" 
                        + "\n"
                        + "- Delete current type:\n"
                        + "   To delete a parameter from the table of measured parameters.\n"
                        + "\n"
                        + "- Set flag:\n"
                        + "   To set the start and end character of the quality flag the selected parameter.\n" 
                        + "   The value of the flag has to be highlighted on the first data line before using this menu.\n"
                        + "\n"
                        + "- Delete flag:\n"
                        + "   To delete the quality flag information of the selected parameter.\n" 
                        + "\n"
                        + "==The table of measured parameters contains the following columns :\n"
                        + "===================================================================\n"
                        + "- Code : MEDATLAS code of the parameter\n"
                        + "\n"
                        + "- Unit : MEDATLAS Unit of the parameter\n"
                        + "\n"
                        + "- Conversion : to input a conversion formula which should be like x*1000+3; with x the value read in the input file(s).\n"
                        + "\n"
                        + "- Test : This field displays the value as it will be written in the output file.\n" 
                        + "     To input value in this field, right click in the table and choose \"Update test\" in the menu;\n" 
                        + "     the start and end fields must have been input before.\n"
                        + "\n"
                        + "- Start - End : These fields indicate the start character and the end character of the parameter in the data line.\n"
                        + "     To input the values in these fields, you have to select the measured parameter value on the first line of measurement, then right click in the table and choose \"Set start/end\".\n"
                        + "\n"
                        + "- Format : This field describes the output format (in the output file) of the measured parameter.\n"
                        + "     Examples of valid format : %3d for 150, %+4d for +150 or -150, %6.3f for 12.124, %+7.5f for +1.12345 or -1.12345\n" 
                        + "\n"
                        + "- Input def. value : This field describes the values found in the input file where the parameter is not measured.\n"
                        + "     This input value can be any type of characters (number, alphabetic).\n"
                        + "\n"
                        + "- Output def. value : This field describes the values written in the output file where the parameter is not measured.\n"
                        + "     The output value must be a number (often a suite of '9') at the same format than the measured parameters described in the format column.\n"
                        + "\n"
                        + "- Test (input) : This field displays the value of the QC flag in the input file.\n" 
                        + "\n"
                        + "- Test (Output) : This field displays the value of the corresponding QC flag that will be wriiten in the output file.\n"
                        + "     To input value in this field, right click in the table and choose \"Update test\" in the menu; \n"
                        + "     the start (flag) and end (flag) fields must have been input before.\n"
                        + "\n"
                        + "- Start (Flag) - End (Flag): These fields indicate the start character and the end character of the QC flag in the data line.\n"
                        + "     To input the values in these fields, you have to select the QC flag value on the first \n"
                        + "     line of measurement, then right click in the table and choose \"Set flag\"\n");
		}
	}

	public static void aideConversion() {
		String langue = Utils.configuration.getLang();
		if (langue.equals("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==CONVERSION==\n"
                        + "\n"
                        + "Last step of NEMO; once the description of the input file(s) is completed, it is possible to run the conversion either to ODV or to MEDATLAS format.\n"
                        + "\n"
                        + "This conversion step generates:\n"
                        + "- output file(s) at the needed format\n"
                        + "- If \"Generate SeaDataNet CDI summary\" is clicked in NEMO settings, a CDI summary file will be created \n"
                        + "  while NEMO is converting the input file(s). It is a Coma Separated Values (CSV) file that can be converted\n" 
                        + "  to an Excel file directly usable by Mikado to generate a CDI XML export, corresponding to the stations that\n" 
                        + "  are converted to ODV or MEDATLAS format. \n"
                        + "- If \"Generate mapping for SeaDataNet download manager\" is clicked in NEMO settings, mapping information\n" 
                        + "  (for all the stations that are converted to ODV or MEDATLAS format) are inserted in the local database of NEMO.\n"
                        + " \n"
                        + "If both conversion to MEDATLAS and ODV are available, user can generate both formats consecutively without revalidating all the steps.\n"
                        + "\n"
                        + "Conversion is divided in two steps:\n"
                        + "1-file conversion into memory : you still can cancel all the conversion process!\n"
                        + "2-file writing on disk : you can't cancel the conversion process while this step is running!\n");

		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==CONVERSION==\n"
                        + "\n"
                        + "Last step of NEMO; once the description of the input file(s) is completed, it is possible to run the conversion either to ODV or to MEDATLAS format.\n"
                        + "\n"
                        + "This conversion step generates:\n"
                        + "- output file(s) at the needed format\n"
                        + "- If \"Generate SeaDataNet CDI summary\" is clicked in NEMO settings, a CDI summary file will be created \n"
                        + "  while NEMO is converting the input file(s). It is a Coma Separated Values (CSV) file that can be converted\n" 
                        + "  to an Excel file directly usable by Mikado to generate a CDI XML export, corresponding to the stations that\n" 
                        + "  are converted to ODV or MEDATLAS format. \n"
                        + "- If \"Generate mapping for SeaDataNet download manager” is clicked in NEMO settings, mapping information\n" 
                        + "  (for all the stations that are converted to ODV or MEDATLAS format) are inserted in the local database of NEMO.\n"
                        + " \n"
                        + "If both conversion to MEDATLAS and ODV are available, user can generate both formats consecutively without revalidating all the steps.\n"
                        + "\n"
                        + "Conversion is divided in two steps:\n"
                        + "1-file conversion into memory : you still can cancel all the conversion process!\n"
                        + "2-file writing on disk : you can't cancel the conversion process while this step is running!\n");
		}
	}

}
