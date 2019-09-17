/*
 * ============================================================================
 * Nom du fichier : RulesManager.java
 * Auteur : sbregent
 * Date : 29 nov. 10
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui;

import org.eclipse.jface.dialogs.MessageDialog;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.ui.edit.data.GroupVerticalParameters;
import fr.ifremer.nemo.ui.edit.data.TypeFallRate;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Global.E_Variant;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: cette classe centralise les règles NEMO et leur gestion pour
 * l'IHM (ne concerne pas le mode batch)
 * 
 * @author sbregent
 */
public class RulesManager {

	private String modelVersion;
	/**
	 * references sur les onglets
	 */
	EditFileForm myEditFileForm;
	EditDataForm myEditDataForm;
	EditCruiseForm myEditCruiseForm;
	EditStationForm myEditStationForm;
	/**
	 * description des paramètres Input
	 */
	private E_ProcessType myProcessType;
	private E_TypeConversion myConversionType;
	private E_InputType myFileType;
	private E_Variant myInputVariant;

	private DataListTypeEnum myDataListType;
	private E_MesureType myMesureType;
	private VerticalParametersList myVerticalParametersList;
	/**
	 * description des autorisations d'après les règles
	 */
	private boolean cruiseEnabled;
	private boolean medatlasEnabled;
	private boolean odvMonostationEnabled;
	private boolean odvMultistationEnabled;
	private boolean netCDFMonostationEnabled;
	private boolean netCDFMultistationEnabled;
//	private boolean dataList_P011viaP021_Enabled = false;
//	private boolean dataList_P091_Enabled = false;

	private boolean m_aboveSeaLevel_Enabled = false;

	private boolean p_depthBSSurf_enabled;
	private boolean p_depthBSBed_enabled;
	private boolean p_pressure_enabled;
	private boolean p_heightASLvl_enabled;

	private String separator=null;

	/**
	 * valeurs par défaut (à l'ouverture)
	 */
	public static boolean DEFAULT_CRUISE_ENABLED = true;
	public static boolean DEFAULT_MEDATLAS_ENABLED = true;
	public static boolean DEFAULT_ODVMONOSTATION_ENABLED = true;
	public static boolean DEFAULT_ODVMULTISTATION_ENABLED = true;
	public static boolean DEFAULT_NETCDFMONOSTATION_ENABLED = true;
	public static boolean DEFAULT_NETCDFMULTISTATION_ENABLED = true;
	public static boolean DEFAULT_DATALIST_P011viaP021_ENABLED = false;
	public static boolean DEFAULT_DATALIST_P91_ENABLED = false;

	public static E_ProcessType DEFAULT_PROCESSTYPE = E_ProcessType.CRUISE_FILE;
	public static E_TypeConversion DEFAULT_CONVERSIONTYPE =
			E_TypeConversion.UNKNOWN;
	public static E_InputType DEFAULT_FILETYPE = E_InputType.UNKNOWN;
	public static E_Variant DEFAULT_VARIANT = E_Variant.UNKNOWN;
	public static DataListTypeEnum DEFAULT_DATALIST = DataListTypeEnum.UNKNOWN;
	public static E_MesureType DEFAULT_MESURETYPE = E_MesureType.BSSURF;

	private String messageLog = Global.EMPTY_STRING;

	private static RulesManager instance;

	public static RulesManager getInstance() {
		if (null == instance) {
			instance = new RulesManager();
		}
		return instance;
	}
	private RulesManager() {
		if (!Nemo.getInstance().isBatchMode()){
			myEditFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
			myEditDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
			myEditCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
			myEditStationForm = Nemo.getInstance().getViewMain().getEditStationForm();
		}
		setDefaultValues();
	}

	public void init() {
		setDefaultValues();
	}

	//	public void defineSectionMandatory() {
	//		myEditCruiseForm.defineMandatorySections();
	//		myEditFileForm.defineMandatorySections();
	//		myEditStationForm.defineMandatorySections();
	//	}

	private void setDefaultValues() {
		messageLog = Global.EMPTY_STRING;
		myProcessType = DEFAULT_PROCESSTYPE;
		myFileType = DEFAULT_FILETYPE;
		medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
		odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
		odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;

		netCDFMonostationEnabled = DEFAULT_NETCDFMONOSTATION_ENABLED;
		netCDFMultistationEnabled = DEFAULT_NETCDFMULTISTATION_ENABLED;
		// en dernier, on recupère la conversion choisie dans les settings
		// on l'applique si elle est compatible avec les autres parametres,
		// sinon on la met à UNKNOWN
		myConversionType = getDefaultConversionTypeWithRules();
		// FAE 6739
		cruiseEnabled = DEFAULT_CRUISE_ENABLED;//!isConversionToODV();
		myDataListType = DEFAULT_DATALIST;
//		dataList_P011viaP021_Enabled = isConversionToODV() || isConversionToNetCDF();
//		dataList_P091_Enabled = !isConversionToNetCDF() && myInputVariant == E_Variant.PHYSICOCHEMICAL;
		m_aboveSeaLevel_Enabled = !isConversionToMedatlas();
		myVerticalParametersList = new VerticalParametersList();
		myMesureType = DEFAULT_MESURETYPE;
		rule_mesureType();

	}

	public void reinitVerticalParams() {
		myVerticalParametersList.setP_depthBSBed(Global.EMPTY_STRING);
		myVerticalParametersList.setP_depthBSSurf(Global.EMPTY_STRING);
		myVerticalParametersList.setP_pressure(Global.EMPTY_STRING);
	}

	public void resetFileForm() {
		setDefaultValues();
		myEditFileForm.resetDefaultSelectionSeparator();
		myEditFileForm.resetDefaultSelectionFileType();
		myEditFileForm.resetDefaultSelectionVariant();
		myEditFileForm.resetDefaultEnableConversionTypes();
		myConversionType = getDefaultConversionTypeWithRules();
		myEditFileForm.selectConversion(myConversionType);
		myEditFileForm.resetFileDataColumnTitles(false);

	}

	public void resetDataForm() {
//		myDataListType = DEFAULT_DATALIST;
		myEditDataForm.selectDataList(myDataListType);
//		dataList_P011viaP021_Enabled = isConversionToODV() || isConversionToNetCDF();
//		dataList_P091_Enabled = !isConversionToNetCDF() && myInputVariant == E_Variant.PHYSICOCHEMICAL;
		m_aboveSeaLevel_Enabled = !isMedatlasEnabled();
		myMesureType = DEFAULT_MESURETYPE;
		myEditDataForm.selectMesureType(myMesureType);
		rule_mesureType();
		Nemo.getInstance().getViewMain().getEditDataForm().reinitParameters();
		Nemo.getInstance().getViewMain().getEditDataForm().reinitTableData();
		Nemo.getInstance().getViewMain().getEditDataForm()
		.getMyGroupeVerticalParameters().removeAllToAllCombos();
	}

	/**
	 * calcule la conversion a utiliser d'après la conversion choisie dans les
	 * settings et les choix effectués pour process et fileType
	 * 
	 * @return la configuration des settings si compatible avec les règles, sinon
	 *         E_TypeConversion.UNKNOWN
	 */
	private E_TypeConversion getDefaultConversionTypeWithRules() {
		E_TypeConversion defConversionType = Nemo.getInstance().getConfig().getDefaultConversionType();
		E_TypeConversion calc = E_TypeConversion.UNKNOWN;
		rule_process(false);
		rule_fileType(false);
		if (defConversionType == E_TypeConversion.MEDATLAS	&& !isMedatlasEnabled()) {
			calc = E_TypeConversion.UNKNOWN;
		} else {
			calc = defConversionType;
		}
		return calc;
	}

	private E_TypeConversion getCurrentConversionTypeWithRules(
			E_TypeConversion conv) {
		E_TypeConversion applicableConv = conv;

		if ((!odvMonostationEnabled && conv == E_TypeConversion.ODV_MONOSTATION)
				|| (!odvMonostationEnabled && conv == E_TypeConversion.ODV_MONOSTATION)
				|| (!medatlasEnabled && conv == E_TypeConversion.MEDATLAS)
				|| (!netCDFMonostationEnabled && conv == E_TypeConversion.NETCDF_MONOSTATION)
				|| (!netCDFMonostationEnabled && conv == E_TypeConversion.NETCDF_MONOSTATION)) {
			applicableConv = E_TypeConversion.UNKNOWN;
			messageLog +=Messages.edit_file_info_conversionNotApplicable + Global.RN;
		}

		return applicableConv;
	}

	private void reinitRules() {
		messageLog = Global.EMPTY_STRING;
		cruiseEnabled = DEFAULT_CRUISE_ENABLED;
		medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
		odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
		odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
		netCDFMonostationEnabled = DEFAULT_NETCDFMONOSTATION_ENABLED;
		netCDFMultistationEnabled = DEFAULT_NETCDFMULTISTATION_ENABLED;
	}

	/**
	 * RECEPTION MAJ PROCESSTYPE
	 */
	public void updateFromIHM(E_ProcessType processType) {

		// si modif process, on fait un reset global
		resetFileForm();

		// FAE 6414
		myEditFileForm.reinitAllButProcess();
		myProcessType = processType;
		reinitRules();
		rule_process(true);
		rule_fileType(true);

		myConversionType = getDefaultConversionTypeWithRules();
		rule_conversionType(true);
		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);

		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP091(!isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableAboveSeaLevel(!isConversionToMedatlas());
		ViewLog.styledTextLogErreurs.setText(messageLog);
		//		resetDataForm();
	}

	public void updateFromIHMSeparatorChecked(){
		if (myEditFileForm.isSeparatorChecked()){
			myEditFileForm.sectionDataColumnTitles.setEnabled(true);
			separator = myEditFileForm.getSeparateur();//40216
			
//			myEditFileForm.sectionStationHeader.getUseSeparatorInStationHeader().setEnabled(true);
		}else{
//			myEditFileForm.sectionStationHeader.getUseSeparatorInStationHeader().setSelection(false);
//			myEditFileForm.sectionStationHeader.getUseSeparatorInStationHeader().setEnabled(false);
			myEditFileForm.sectionDataColumnTitles.setEnabled(false);
		}
		updateFromIHMColumnTitlesInInputFile();
		Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);

	}
	public void updateFromIHMColumnTitlesInInputFile(){
		myEditFileForm.resetFileDataColumnTitles(myEditFileForm.isSeparatorChecked());
		Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
	}
	public void updateFromIHM(TypeFallRate typeFallrate) {
		Nemo.getInstance().getViewMain().getEditDataForm().setFallrateCode(typeFallrate.getKey());
		Nemo.getInstance().getViewMain().getEditDataForm().setFallrateLabel(typeFallrate.getTerm());
	}
	/**
	 * //40216
	 */
	public boolean isSeparatorChecked(){
		return separator!=null && !separator.isEmpty();
	}
	/**
	 * //40216
	 */
	public String getSeparator(){
		if (separator.equals("|")){ //$NON-NLS-1$
			return "\\|"; //$NON-NLS-1$
		}
		return separator;

	}
	/**
	 * RECEPTION MAJ FILETYPE
	 */

	public void updateFromIHM(E_InputType fileType) {
		medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
		odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
		odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
		netCDFMonostationEnabled = DEFAULT_NETCDFMONOSTATION_ENABLED;
		netCDFMultistationEnabled = DEFAULT_NETCDFMULTISTATION_ENABLED;

		myFileType = fileType;
		reinitRules();
		rule_process(true);
		rule_fileType(true);

		myConversionType = getCurrentConversionTypeWithRules(myConversionType);
		rule_conversionType(true);
		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);

		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP011viaP021(isDataList_P011viaP021_Enabled());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP091(isDataList_P091_Enabled());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableAboveSeaLevel(!isConversionToMedatlas());
		ViewLog.styledTextLogErreurs.setText(messageLog);

		rule_mesureType();
		//		GroupVerticalParameters verticGroup =	Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
		//		verticGroup.enable_pDepthBSBed(isP_depthBSBed_enabled());
		//		verticGroup.enable_pDepthBSSurf(isP_depthBSSurf_enabled());
		//		verticGroup.enable_pPressure(isP_pressure_enabled());
		//		verticGroup.enable_pHeightASLvl(isP_heightASLvl_enabled());

	}

	public void updateFromModel(E_InputType fileType) {
		medatlasEnabled = DEFAULT_MEDATLAS_ENABLED;
		odvMonostationEnabled = DEFAULT_ODVMONOSTATION_ENABLED;
		odvMultistationEnabled = DEFAULT_ODVMULTISTATION_ENABLED;
		netCDFMonostationEnabled = DEFAULT_NETCDFMONOSTATION_ENABLED;
		netCDFMultistationEnabled = DEFAULT_NETCDFMULTISTATION_ENABLED;

		myFileType = fileType;
		reinitRules();
		rule_fileType(true);

		myEditFileForm.selectTypeFile(myFileType);
		
		
		myConversionType = getCurrentConversionTypeWithRules(myConversionType);
		rule_conversionType(true);
		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);

		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP091(!isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableAboveSeaLevel(!isConversionToMedatlas());
		ViewLog.styledTextLogErreurs.setText(messageLog);
	}


	public void updateFromModel(E_Variant inputVariant) {
		myInputVariant = inputVariant;
		myEditFileForm.selectVariant(inputVariant);

	}


	public void updateFromIHM(E_Variant variant) {
		boolean confirm = true;


		if (myInputVariant != variant) {
			confirm =
					MessageDialog.openConfirm(null,
							Messages.edit_data_dataListDialogBox_title,
							Messages.edit_data_dataListDialogBox_text);
		}else {
			return;// nochange
		}

		if (confirm) {
			myInputVariant = variant;
			if (variant == E_Variant.PHYSICOCHEMICAL || variant == E_Variant.FLOWCYTO) {
				Nemo.getInstance().getViewMain().getEditFileForm().enableFileTypeChoice(true);
			}else {
				Nemo.getInstance().getViewMain().getEditFileForm().deselectAndDisableSelectionFileType();
				myFileType=DEFAULT_FILETYPE;
			}

			if (isBioCytoMicroVariant()) {
				myDataListType=DataListTypeEnum.P011_VIA_P021;
			}

			rule_variant(true);

			myConversionType = getCurrentConversionTypeWithRules(myConversionType);
			rule_conversionType(true);
			myEditFileForm.updateEnableConversionTypes();
			myEditFileForm.selectConversion(myConversionType);
			Nemo.getInstance().getViewMain().getEditDataForm().setMustBeReinit(true);
			Nemo.getInstance().getViewMain().getEditDataForm().setMustFillFromModel(false);
			Nemo.getInstance().getViewMain().getEditFileForm().invalidEtape();
			//*****************
		}
		ViewLog.styledTextLogErreurs.setText(messageLog);
	}


	public void updateFromModel(TypeFallRate typeFallRate) {
		if (typeFallRate != null&& isFallrateEnabled()){
			Nemo.getInstance().getViewMain().getEditDataForm().setFallrateCode(typeFallRate.getKey());
			Nemo.getInstance().getViewMain().getEditDataForm().setFallrateLabel(typeFallRate.getTerm());
		}
	}

	public boolean isFallrateEnabled() {
		return myVerticalParametersList.isVerticalReferenceSet()
				&& RulesManager.getInstance().isProfile()
				&& myMesureType==E_MesureType.BSSURF;
	}
	/**
	 * RECEPTION MAJ CONVERSION
	 */

	
	/**
	 * 
	 * @param convType: output format, including mono/multi
	 * @param isConversionChanged: indicates if the output format has changed (false if same format, only multi/mono changed)
	 */
	public void updateFromIHM(E_TypeConversion convType) {
		boolean confirm = true;
		boolean mustReinitDataForm = false;
		boolean mustChangeDatalist = false;
		E_TypeConversion tmp = myConversionType;
		//		DataListTypeEnum newDataList = myDataListType;

		if( myConversionType != convType) {
			if (isConversionFormatChanged(myConversionType, convType) ) {
				if (myDataListType == DataListTypeEnum.UNKNOWN) {
					mustChangeDatalist = true;
				}
				if (myDataListType == DataListTypeEnum.P011_VIA_P021 && convType == E_TypeConversion.MEDATLAS) {
					confirm =
							MessageDialog.openConfirm(null,
									Messages.edit_data_dataListDialogBox_title,
									Messages.edit_data_dataListDialogBox_text);
					myDataListType = DEFAULT_DATALIST;
					//				newDataList = myDataListType;
					mustReinitDataForm = true;
				}
				if (myDataListType == DataListTypeEnum.P091
						&& (convType == E_TypeConversion.NETCDF_MONOSTATION || convType == E_TypeConversion.NETCDF_MULTISTATIONS)) {
					confirm =
							MessageDialog.openConfirm(null,
									Messages.edit_data_dataListDialogBox_title,
									Messages.edit_data_dataListDialogBox_text);
					myDataListType = DEFAULT_DATALIST;
					//				newDataList = myDataListType;
					mustReinitDataForm = true;
				}
				if (confirm) {
					reinitRules();
					rule_process(true);
					rule_fileType(true);
					myConversionType = convType;
					rule_conversionType(true);
					Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
					m_aboveSeaLevel_Enabled = !isConversionToMedatlas();
					Nemo.getInstance().getViewMain().getEditDataForm().enableAboveSeaLevel(m_aboveSeaLevel_Enabled);

//					dataList_P011viaP021_Enabled = isConversionToODV() || isConversionToNetCDF();
//					Nemo.getInstance().getViewMain().getEditDataForm().enableP011viaP021(dataList_P011viaP021_Enabled);
//					dataList_P091_Enabled = !isConversionToNetCDF() && myInputVariant == E_Variant.PHYSICOCHEMICAL;
//					Nemo.getInstance().getViewMain().getEditDataForm().enableP091(dataList_P091_Enabled);

					// ensuite on selectionne si necessaire
					if (mustReinitDataForm || mustChangeDatalist) {
						Nemo.getInstance().getViewMain().getEditDataForm().setMustBeReinit(true);
						Nemo.getInstance().getViewMain().getEditDataForm().setMustFillFromModel(false);
					}

					ViewLog.styledTextLogErreurs.setText(messageLog);
				} else {
					myEditFileForm.selectConversion(tmp);
				}
			}else {
				myConversionType = convType;
			}
		}

	}

	private boolean isConversionFormatChanged(E_TypeConversion oldConv, E_TypeConversion newConv) {
		switch (oldConv) {
		case MEDATLAS:
			return newConv!= E_TypeConversion.MEDATLAS;
		case ODV_MONOSTATION:
		case ODV_MULTISTATIONS:
			return newConv!= E_TypeConversion.ODV_MONOSTATION && newConv!= E_TypeConversion.ODV_MULTISTATIONS;
		case NETCDF_MONOSTATION:
		case NETCDF_MULTISTATIONS:
			return newConv!= E_TypeConversion.NETCDF_MONOSTATION && newConv!= E_TypeConversion.NETCDF_MULTISTATIONS;
		default:
			return true;
		}
	}
	public void updateFromModel(E_TypeConversion convType) {

		reinitRules();
		rule_process(false);
		rule_variant(false);
		rule_fileType(false);


		myConversionType = getCurrentConversionTypeWithRules(convType);
		rule_conversionType(true);
		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);

		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableAboveSeaLevel(!isConversionToMedatlas());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP091(!isConversionToNetCDF());
		ViewLog.styledTextLogErreurs.setText(messageLog);

	}

	public void updateFromModel(VerticalParametersList verticalParametersList) {
		myVerticalParametersList = verticalParametersList;
		//		Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters().select(myVerticalParametersList);
		//		myEditDataForm.setFallrateGroupEnabled(isFallrateEnabled());
	}

	/**
	 * Getter sur myVerticalParametersList.
	 * 
	 * @return the myVerticalParametersList
	 */
	public VerticalParametersList getMyVerticalParametersList() {
		return myVerticalParametersList;
	}

	/**
	 * CLIC SUR BOUTON BROWSE
	 */
	public void updateFromBrowse() {
		// conservation du process courant
		E_ProcessType currentProcess = myProcessType;
		// conservation du message courant (le browse peut avoir affiché un
		// message)
		messageLog = ViewLog.styledTextLogErreurs.getText() + Global.RN;

		myProcessType = currentProcess;
		myFileType = DEFAULT_FILETYPE;
		rule_process(true);
		rule_fileType(true);
		//		myConversionType = getDefaultConversionTypeWithRules();
		//		rule_conversionType(true);
		myEditFileForm.razText();
		myEditFileForm.resetDefaultSelectionSeparator();
		myEditFileForm.selectTypeFile(myFileType);
		myEditFileForm.resetDefaultSelectionFileType();

		myConversionType = getDefaultConversionTypeWithRules();
		rule_conversionType(true);
		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);

		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		myDataListType = DEFAULT_DATALIST;
		Nemo.getInstance().getViewMain().getEditDataForm().setMustReinitInputData(true);
		ViewLog.styledTextLogErreurs.setText(messageLog);
	}

	/**
	 * RECEPTION MAJ DATALIST
	 */

	public void updateFromIHM(DataListTypeEnum dataListType) {
		boolean confirm = true;
		if (dataListType != myDataListType) {
			if (Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().getTypes().size() > 0) {
				confirm =
						MessageDialog.openConfirm(null,
								Messages.edit_data_dataListDialogBox_title,
								Messages.edit_data_dataListDialogBox_text);
			}
			if (confirm) {
				myDataListType = dataListType;
				Nemo.getInstance().getViewMain().getEditDataForm().reinitTableData();
				Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters().removeAllToAllCombos();
				Nemo.getInstance().getViewMain().getEditDataForm().resetFallRate();
				// !!! le fillTypes doit être appelé APRES le removeToAllcombos
				Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().fillTypes();
				Nemo.getInstance().getViewMain().getEditDataForm().invalidEtape();

			} else {
				// si pas confirmation, et ne touche a rien et on remet la
				// valeur de depart
				Nemo.getInstance().getViewMain().getEditDataForm().selectDataList(myDataListType);
			}
		}

	}

	public void updateFromModel(DataListTypeEnum dataListType) {

		myDataListType = dataListType;
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableP091(!isConversionToNetCDF());
		//		Nemo.getInstance().getViewMain().getEditDataForm().enableAboveSeaLevel(!isConversionToMedatlas());
		//		Nemo.getInstance().getViewMain().getEditDataForm().selectDataList(dataListType);
		ViewLog.styledTextLogErreurs.setText(messageLog);

	}

	/**
	 * RECEPTION MAJ MesureType
	 */

	public void updateFromIHM(E_MesureType mesureType) {
		myMesureType = mesureType;
		rule_mesureType();
		GroupVerticalParameters verticGroup = Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
		verticGroup.enable_pDepthBSBed(isP_depthBSBed_enabled());
		verticGroup.enable_pDepthBSSurf(isP_depthBSSurf_enabled());
		verticGroup.enable_pPressure(isP_pressure_enabled());
		verticGroup.enable_pHeightASLvl(isP_heightASLvl_enabled());

		myEditDataForm.setFallrateGroupEnabled(isFallrateEnabled());
	}



	public void updateFromModel(E_MesureType mesureType) {
		if (mesureType == E_MesureType.UNKNOWN) {
			myMesureType = DEFAULT_MESURETYPE;
		} else {
			myMesureType = mesureType;
		}
		rule_mesureType();
		GroupVerticalParameters verticGroup =Nemo.getInstance().getViewMain().getEditDataForm().getMyGroupeVerticalParameters();
		verticGroup.enable_pDepthBSBed(isP_depthBSBed_enabled());
		verticGroup.enable_pDepthBSSurf(isP_depthBSSurf_enabled());
		verticGroup.enable_pPressure(isP_pressure_enabled());
		verticGroup.enable_pHeightASLvl(isP_heightASLvl_enabled());
		Nemo.getInstance().getViewMain().getEditDataForm().selectMesureType(mesureType);

	}

	/**
	 * gère l'activation des combo pour les paramètres de reference verticale
	 * (griséees ou non)
	 */
	private void rule_mesureType() {
		setP_depthBSBed_enabled(false);
		setP_depthBSSurf_enabled(false);
		setP_pressure_enabled(false);
		setP_heightASLvl_enabled(false);

		setM_aboveSeaLevel_enabled(false);

		boolean isVariantWithZ=(myInputVariant==E_Variant.PHYSICOCHEMICAL || 	myInputVariant==E_Variant.FLOWCYTO);
		boolean p_depthBSBed =
				isVariantWithZ
				&& (isProfile() || isTrajectory())
				&& myMesureType == E_MesureType.BSBED;


		boolean p_depthBSSurf = false;
		if (isVariantWithZ) {
			p_depthBSSurf =
					(isProfile() && myMesureType == E_MesureType.BSSURF)
					|| ((isTimeSeries() || isTrajectory())
							&& (isConversionToODV() || isConversionToNetCDF()) && myMesureType == E_MesureType.BSSURF);
		}else {
			p_depthBSSurf = false;
		}


		boolean p_pressure = 	isVariantWithZ && isProfile() && myMesureType == E_MesureType.BSSURF;
		boolean p_heightASLvl = false;
		if (	isVariantWithZ) {
			p_heightASLvl = !isConversionToMedatlas() && (isProfile() || isTrajectory()) && myMesureType == E_MesureType.ASLVL;
		}else {
			p_heightASLvl = false;
		}

		setP_depthBSBed_enabled(p_depthBSBed);
		setP_depthBSSurf_enabled(p_depthBSSurf);
		setP_pressure_enabled(p_pressure);
		setP_heightASLvl_enabled(p_heightASLvl);
		setM_aboveSeaLevel_enabled(!isConversionToMedatlas());

	}
	private boolean isBioCytoMicroVariant() {
		return myInputVariant==E_Variant.BIOLOGICAL||myInputVariant==E_Variant.FLOWCYTO||myInputVariant==E_Variant.MICROLITTER;
	}
	/******************************************
	 * REGLES
	 ******************************************/
	private void rule_variant(boolean addToLog) {
		// if input is bio/cyto/micro ODV variant, Nemo can generate only ODV or CF (need P01 parameters)
		if (isBioCytoMicroVariant()) {
			medatlasEnabled = false;
			netCDFMonostationEnabled = false;
			netCDFMultistationEnabled = false;
			if (addToLog) {
				messageLog +=
						Messages.edit_file_info_allowedOutputFormatsForBioCytoMicro
						+ Global.RN;
			}
		}
		myEditFileForm.enableFileTypeChoice(myInputVariant==E_Variant.FLOWCYTO ||myInputVariant==E_Variant.PHYSICOCHEMICAL);
	}

	private void rule_process(boolean addToLog) {
		// FAE 0001134: dans le cas d'une collection, on ne peut generer que le
		// format ODV
		if (isProcessCollection()) {
			medatlasEnabled = false;
			if (addToLog) {
				messageLog +=
						Messages.edit_file_info_medatlasForbiddenBecauseCollection
						+ Global.RN;
			}
		}
	}

	private void rule_fileType(boolean addToLog) {

		// FAE1386 : desactiver "One File per station" si Trajectory
		// FAE9651: activer "One File per station" si Trajectory
	}

	private void rule_conversionType(boolean addToLog) {
		m_aboveSeaLevel_Enabled = true;
		cruiseEnabled = true;//25572

		if (myConversionType == E_TypeConversion.ODV_MONOSTATION
				|| myConversionType == E_TypeConversion.ODV_MULTISTATIONS) {
//			dataList_P011viaP021_Enabled = true;
		} else if (myConversionType == E_TypeConversion.MEDATLAS) {
			m_aboveSeaLevel_Enabled = false;
			if (addToLog) {
				messageLog +=
						Messages.edit_file_info_P011viaP021_disabled + Global.RN;
				messageLog +=
						Messages.edit_file_info_AboveSeaLevel_disabled + Global.RN;

			}
		} else if (myConversionType == E_TypeConversion.NETCDF_MONOSTATION
				|| myConversionType == E_TypeConversion.NETCDF_MULTISTATIONS) {
//			dataList_P011viaP021_Enabled = true;
//			dataList_P091_Enabled = false;
			if (addToLog) {
				messageLog += Messages.edit_file_info_P091disabled + Global.RN;
			}
		}
	}

	public boolean isP09Enabled() {
		return 	!isConversionToNetCDF()
				&& myInputVariant==E_Variant.PHYSICOCHEMICAL;
	}
	public boolean isP01viaP09Enabled() {
		return myInputVariant==E_Variant.PHYSICOCHEMICAL;
	}
	public boolean isP01viaP02Enabled() {
		return !isConversionToMedatlas();
	}
	/***********************************************************************************************************
	 * recup proprietes (raccourcis)
	 *********************************************************************************************************/
	// PROCESS

	public boolean isCruiseInADirectory() {
		return myProcessType == E_ProcessType.COLLECTION_DIR
				|| myProcessType == E_ProcessType.CRUISE_DIR || myProcessType == E_ProcessType.COLLECTION_FILES;
	}
	/**
	 * 
	 * @return true if CollectionGrouped By Cruise
	 */
	public boolean isProcessCollectionDir() {
		// CollectionGroupedByCruise
		return myProcessType == E_ProcessType.COLLECTION_DIR;
	}
	/**
	 * 
	 * @return true if Collection NOT Grouped By Cruise
	 */
	public boolean isProcessCollectionFiles() {
		// Collection NOT GroupedByCruise
		return myProcessType == E_ProcessType.COLLECTION_FILES;
	}
	/**
	 * 
	 * @return true if Collection (Grouped or NOT grouped) By Cruise
	 */
	public boolean isProcessCollection() {
		return (myProcessType == E_ProcessType.COLLECTION_DIR || myProcessType == E_ProcessType.COLLECTION_FILES);
	}

	// CONVERSION
	public boolean isConversionToODV() {
		return (myConversionType == E_TypeConversion.ODV_MONOSTATION || myConversionType == E_TypeConversion.ODV_MULTISTATIONS);
	}

	public boolean isConversionToNetCDF() {
		return (myConversionType == E_TypeConversion.NETCDF_MONOSTATION || myConversionType == E_TypeConversion.NETCDF_MULTISTATIONS);
	}

	public boolean isDataListTypeP011viaP021() {
		return myDataListType == DataListTypeEnum.P011_VIA_P021;
	}

	public boolean isDataListTypeP091() {
		return myDataListType == DataListTypeEnum.P091;
	}

	public boolean isDataListTypeP011viaP091() {
		return myDataListType == DataListTypeEnum.P011_VIA_P091;
	}

	/***********************************************************************************************************
	 * getters & setters
	 *********************************************************************************************************/
	// PROCESS
	public E_ProcessType getMyProcessType() {
		return myProcessType;
	}

	public boolean isProcessCruiseFile() {
		return myProcessType == E_ProcessType.CRUISE_FILE;
	}

	public boolean isProcessCruiseDir() {
		return myProcessType == E_ProcessType.CRUISE_DIR;
	}

	// FILETYPE
	public E_InputType getMyFileType() {
		return myFileType;
	}

	public boolean isTrajectory() {
		return myFileType == E_InputType.TRAJECTORY;
	}

	public boolean isProfile() {
		return myFileType == E_InputType.PROFILE;
	}

	public boolean isTimeSeries() {
		return myFileType == E_InputType.TIMESERIES;
	}

	// Input data type (physicoChm, bio, flowcyto, microlitter)
	public E_Variant getMyInputVariant() {
		return myInputVariant;
	}
	// CONVERSION
	public Conversion getConversion(){
		switch (myConversionType) {
		case ODV_MONOSTATION:
		case ODV_MULTISTATIONS:
			return  Conversion.ODV;
		case MEDATLAS:
			return Conversion.MEDATLAS;
		case NETCDF_MONOSTATION:
		case NETCDF_MULTISTATIONS:
			return  Conversion.NETCDF;
		default:
			return null;
		}
	}
	public boolean isConversionToODVMonoStation() {
		return myConversionType == E_TypeConversion.ODV_MONOSTATION;
	}

	public boolean isConversionToODVMultiStation() {
		return myConversionType == E_TypeConversion.ODV_MULTISTATIONS;
	}

	public boolean isConversionToNetCDFMonoStation() {
		return myConversionType == E_TypeConversion.NETCDF_MONOSTATION;
	}

	public boolean isConversionToNetCDFMultiStation() {
		return myConversionType == E_TypeConversion.NETCDF_MULTISTATIONS;
	}

	public boolean isConversionToMedatlas() {
		return myConversionType == E_TypeConversion.MEDATLAS;
	}

	public E_TypeConversion getMyConversionType() {
		return myConversionType;
	}

	public boolean isMedatlasEnabled() {
		return medatlasEnabled;
	}

	public boolean isNetCDFMonostationEnabled() {
		return netCDFMonostationEnabled;
	}

	public boolean isNetCDFMultistationEnabled() {
		return netCDFMultistationEnabled;
	}

	public boolean isOdvMonostationEnabled() {
		return odvMonostationEnabled;
	}

	public boolean isOdvMultistationEnabled() {
		return odvMultistationEnabled;
	}

	// CRUISE
	public boolean isCruiseEnabled() {
		return cruiseEnabled;
	}

//	public boolean isDataList_P011viaP021_Enabled() {
//		return dataList_P011viaP021_Enabled;
//	}
//
//	public boolean isDataList_P091_Enabled() {
//		return dataList_P091_Enabled;
//	}

	public DataListTypeEnum getMyDataListType() {
		return myDataListType;
	}

	public String getMyDataListTypeString() {
		switch (myDataListType) {
		case P011_VIA_P021:
			return Intern.list_P011 + Global.UNDERSCORE + Global.VIA + Global.UNDERSCORE + Intern.list_P021;
		case P011_VIA_P091:
			return Intern.list_P011 + Global.SPACE + Global.VIA + Global.SPACE + Intern.list_P091;
		case P091:
			return Intern.list_P091;
		default:
			return DataListTypeEnum.UNKNOWN.toString();
		}

	}

	public boolean isM_aboveSeaLevel_enabled() {
		return m_aboveSeaLevel_Enabled;
	}

	public void setM_aboveSeaLevel_enabled(boolean m_aboveSeaLevel_enabled) {
		m_aboveSeaLevel_Enabled = m_aboveSeaLevel_enabled;
	}

	public boolean isP_depthBSBed_enabled() {
		return p_depthBSBed_enabled;
	}

	public void setP_depthBSBed_enabled(boolean bed_enabled) {
		p_depthBSBed_enabled = bed_enabled;
	}

	public boolean isP_depthBSSurf_enabled() {
		return p_depthBSSurf_enabled;
	}

	public void setP_depthBSSurf_enabled(boolean surf_enabled) {
		p_depthBSSurf_enabled = surf_enabled;
	}

	public boolean isP_pressure_enabled() {
		return p_pressure_enabled;
	}

	public void setP_pressure_enabled(boolean p_pressure_enabled) {
		this.p_pressure_enabled = p_pressure_enabled;
	}

	public boolean isP_heightASLvl_enabled() {
		return p_heightASLvl_enabled;
	}

	public void setP_heightASLvl_enabled(boolean p_heightASLvl_enabled) {
		this.p_heightASLvl_enabled = p_heightASLvl_enabled;
	}

	public E_MesureType getMyMesureType() {
		return myMesureType;
	}
	public String getMessageLog() {
		return messageLog;
	}
	public void addMessageLog(String messageLogPart) {
		this.messageLog = this.messageLog+ Global.RN+ messageLogPart;
	}
	public boolean isUsingTableForCsvData(){

		if (!Nemo.getInstance().getViewMain().getEditFileForm().getSeparateur().isEmpty() && Nemo.getInstance().getViewMain().getEditFileForm().useTableForCsvData()){
			return true;
		}
		return false;
	}
	public String getModelVersion() {
		return modelVersion;
	}
	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}
	//40216
	public void updateFromModel_separator(String separator) {
		this.separator = separator;

//		Nemo.getInstance().getViewMain().getEditFileForm().getSectionStationHeader().getUseSeparatorInStationHeader().setEnabled((separator != null && !separator.equals("")));
	}


}
