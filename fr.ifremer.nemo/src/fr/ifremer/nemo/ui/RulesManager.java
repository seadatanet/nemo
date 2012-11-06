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
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: cette classe centralise les règles NEMO et leur gestion pour
 * l'IHM (ne concerne pas le mode batch)
 * 
 * @author sbregent
 */
public class RulesManager
{

	/**
	 * reference sur le panneau file
	 */
	EditFileForm myEditFileForm;
	EditDataForm myEditDataForm;
	/**
	 * description des paramètres Input
	 */
	private E_ProcessType myProcessType;
	private E_TypeConversion myConversionType;
	private E_InputType myFileType;
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
	private boolean dataList_P011viaP021_Enabled = false;
	private boolean dataList_P091_Enabled = false;

	private boolean p_depthBSSurf_enabled;
	private boolean p_depthBSBed_enabled;
	private boolean p_pressure_enabled;

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
	public static DataListTypeEnum DEFAULT_DATALIST = DataListTypeEnum.UNKNOWN;
	public static E_MesureType DEFAULT_MESURETYPE = E_MesureType.BSSURF;

	private String messageLog = Global.EMPTY_STRING;

	public RulesManager()
	{
		myEditFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
		myEditDataForm = Nemo.getInstance().getViewMain().getEditDataForm();
		setDefaultValues();
	}

	public void init()
	{
		setDefaultValues();
	}

	public void defineSectionMandatory()
	{
		EditCruiseForm myEditCruiseForm = Nemo.getInstance().getViewMain().getEditCruiseForm();
		EditStationForm myEditStationForm = Nemo.getInstance().getViewMain().getEditStationForm();

		boolean isMandatory= !isConversionToNetCDF();

		
		// mandatory File Section
		if (myEditFileForm.sectionFileHeader != null)
		{
			myEditFileForm.sectionFileHeader.setMandatory(true);
			myEditFileForm.sectionStationHeader.setMandatory(true);
			myEditFileForm.sectionEndOfStation.setMandatory(true);
			myEditFileForm.sectionDataTerminator.setMandatory(true);
		}

		// mandatory Cruise Section
		if (myEditCruiseForm.sectionReference != null)
		{
			myEditCruiseForm.sectionReference.setMandatory(true);
			for (int i = 0; i < myEditCruiseForm.sectionReference.getItems().size(); i++)
			{
				myEditCruiseForm.sectionReference.getItems().get(i).setMandatory(true);
			}
			myEditCruiseForm.sectionLocalisation.setMandatory(isMandatory);
			for (int i = 0; i < myEditCruiseForm.sectionLocalisation.getItems().size(); i++)
			{
				myEditCruiseForm.sectionLocalisation.getItems().get(i).setMandatory(isMandatory);
			}
			myEditCruiseForm.sectionSource.setMandatory(isMandatory);
			myEditCruiseForm.sectionSource.getItems().get(0).setMandatory(isMandatory);
			myEditCruiseForm.sectionSource.getItems().get(2).setMandatory(isMandatory);

			myEditCruiseForm.sectionCentre.setMandatory(isMandatory);
			myEditCruiseForm.sectionCentre.getItems().get(0).setMandatory(isMandatory);
			myEditCruiseForm.sectionCentre.getItems().get(1).setMandatory(isMandatory);

			myEditCruiseForm.sectionType.setMandatory(true);

		}

		// mandatory Station Section
		if (myEditStationForm.stationNumber != null)
		{
			myEditStationForm.stationNumber.setMandatory(true);
			/*
			 * if(myEditFileForm.getRadMedatlas().getSelection() &&
			 * myEditFileForm.getRadProfilVertical().getSelection())
			 * myEditStationForm.dataType.setMandatory(false);
			 * else
			 */
			myEditStationForm.dataType.setMandatory(true);
			myEditStationForm.historique.setMandatory(false);
			myEditStationForm.comments.setMandatory(false);
			myEditStationForm.surfaceSample.setMandatory(false);
			myEditStationForm.utConvertion.setMandatory(false);
			myEditStationForm.time.setMandatory(true);
			myEditStationForm.date.setMandatory(true);
			myEditStationForm.latitude.setMandatory(true);
			myEditStationForm.longitude.setMandatory(true);
			myEditStationForm.depth.setMandatory(false);

			if (myEditStationForm.sensorDepth.getSection() != null && !myEditStationForm.sensorDepth.getSection().isDisposed())
			{
				if (myEditFileForm.getRadSerieTempo().getSelection())
					myEditStationForm.sensorDepth.setMandatory(true);
				else
					myEditStationForm.sensorDepth.setMandatory(false);
			}

			if (myEditStationForm.samplingRate.getSection() != null && !myEditStationForm.samplingRate.getSection().isDisposed())
			{
				if (myEditFileForm.getRadSerieTempo().getSelection() || myEditFileForm.getRadTrajectoire().getSelection())
					myEditStationForm.samplingRate.setMandatory(true);
				else
					myEditStationForm.samplingRate.setMandatory(false);
			}

		}
	}

	private void setDefaultValues()
	{
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
		cruiseEnabled = !isConversionToODV();
		myDataListType = DEFAULT_DATALIST;
		dataList_P011viaP021_Enabled = isConversionToODV() || isConversionToNetCDF();
		dataList_P091_Enabled = !isConversionToNetCDF();
		myVerticalParametersList = new VerticalParametersList();
		myMesureType = DEFAULT_MESURETYPE;
		rule_mesureType();

	}

	public void reinitVerticalParams()
	{
		myVerticalParametersList.setP_depthBSBed(Global.EMPTY_STRING);
		myVerticalParametersList.setP_depthBSSurf(Global.EMPTY_STRING);
		myVerticalParametersList.setP_pressure(Global.EMPTY_STRING);
	}

	public void resetFileForm()
	{
		setDefaultValues();
		myEditFileForm.resetDefaultSelectionSeparator();
		myEditFileForm.resetDefaultSelectionFileType();
		myEditFileForm.resetDefaultEnableConversionTypes();
		myConversionType = getDefaultConversionTypeWithRules();
		myEditFileForm.selectConversion(myConversionType);

	}

	public void resetDataForm()
	{

		myDataListType = DEFAULT_DATALIST;
		myEditDataForm.selectDataList(myDataListType);
		dataList_P011viaP021_Enabled = isConversionToODV()|| isConversionToNetCDF();
		dataList_P091_Enabled = !isConversionToNetCDF();
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
	private E_TypeConversion getDefaultConversionTypeWithRules()
	{
		E_TypeConversion defConversionType =
				Nemo.getInstance().getConfig().getDefaultConversionType();
		E_TypeConversion calc = E_TypeConversion.UNKNOWN;
		rule_process(false);
		rule_fileType(false);
		if (defConversionType == E_TypeConversion.MEDATLAS
				&& !isMedatlasEnabled())
		{
			calc = E_TypeConversion.UNKNOWN;
		} else
		{
			calc = defConversionType;
		}
		return calc;
	}

	private E_TypeConversion getCurrentConversionTypeWithRules(
			E_TypeConversion conv)
	{
		E_TypeConversion applicableConv = conv;

		if ((!odvMonostationEnabled && conv == E_TypeConversion.ODV_MONOSTATION)
				|| (!odvMonostationEnabled && conv == E_TypeConversion.ODV_MONOSTATION)
				|| (!medatlasEnabled && conv == E_TypeConversion.MEDATLAS)
		        || (!netCDFMonostationEnabled && conv == E_TypeConversion.NETCDF_MONOSTATION)
                || (!netCDFMonostationEnabled && conv == E_TypeConversion.NETCDF_MONOSTATION))
		{
			applicableConv = E_TypeConversion.UNKNOWN;
			messageLog +=
					Messages.edit_file_info_conversionNotApplicable + Global.RN;
		}

		return applicableConv;
	}

	private void reinitRules()
	{
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
	public void updateFromIHM(E_ProcessType processType)
	{

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
		Nemo.getInstance().getViewMain().getEditDataForm()
				.enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
        .enableP091(!isConversionToNetCDF());
		ViewLog.styledTextLogErreurs.setText(messageLog);
		resetDataForm();
	}

	/**
	 * RECEPTION MAJ FILETYPE
	 */

	public void updateFromIHM(E_InputType fileType)
	{
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
		Nemo.getInstance().getViewMain().getEditDataForm()
				.enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
                .enableP091(!isConversionToNetCDF());
		ViewLog.styledTextLogErreurs.setText(messageLog);

		rule_mesureType();
		GroupVerticalParameters verticGroup =
				Nemo.getInstance().getViewMain().getEditDataForm()
						.getMyGroupeVerticalParameters();
		verticGroup.enable_pDepthBSBed(isP_depthBSBed_enabled());
		verticGroup.enable_pDepthBSSurf(isP_depthBSSurf_enabled());
		verticGroup.enable_pPressure(isP_pressure_enabled());

	}

	public void updateFromModel(E_InputType fileType)
	{
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
		Nemo.getInstance().getViewMain().getEditDataForm()
				.enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
                .enableP091(!isConversionToNetCDF());
		ViewLog.styledTextLogErreurs.setText(messageLog);
	}

	/**
	 * RECEPTION MAJ CONVERSION
	 */

	public void updateFromIHM(E_TypeConversion convType)
	{
		boolean confirm = true;
		boolean mustReinitDataForm = false;
		boolean mustChangeDatalist = false;
		E_TypeConversion tmp = myConversionType;
		DataListTypeEnum newDataList = myDataListType;
		if (myConversionType != convType)
		{
			if (myDataListType == DataListTypeEnum.UNKNOWN)
			{
				mustChangeDatalist = true;
			}
			if (myDataListType == DataListTypeEnum.P011_VIA_P021
					&& convType == E_TypeConversion.MEDATLAS)
			{
				confirm =
						MessageDialog.openConfirm(null,
								Messages.edit_data_dataListDialogBox_title,
								Messages.edit_data_dataListDialogBox_text);
				myDataListType = DEFAULT_DATALIST;
				newDataList = myDataListType;
				mustReinitDataForm = true;
			}
			if (confirm)
			{
				reinitRules();
				rule_process(true);
				rule_fileType(true);
				myConversionType = convType;
				rule_conversionType(true);
				Nemo.getInstance().getViewMain()
						.enableCruiseTab(isCruiseEnabled());
				dataList_P011viaP021_Enabled = isConversionToODV() || isConversionToNetCDF();
				Nemo.getInstance().getViewMain().getEditDataForm()
						.enableP011viaP021(dataList_P011viaP021_Enabled);
				dataList_P091_Enabled = !isConversionToNetCDF();
				Nemo.getInstance().getViewMain().getEditDataForm()
                        .enableP091(dataList_P091_Enabled);

				// ensuite on selectionne si necessaire
				if (mustReinitDataForm || mustChangeDatalist)
				{
					// reinit
					Nemo.getInstance().getViewMain().getEditDataForm()
							.reinitParameters();
					Nemo.getInstance().getViewMain().getEditDataForm()
							.reinitTableData();
					Nemo.getInstance().getViewMain().getEditDataForm()
							.getMyGroupeVerticalParameters().removeAllToAllCombos();
					// !!! le fillTypes doit être appelé APRES le
					// removeToAllcombos, car il peut ajouter PRES
					// automatiquement
					Nemo.getInstance().getViewMain().getEditDataForm()
							.getMyTableData().fillTypes();
					Nemo.getInstance().getViewMain().getEditDataForm()
							.selectDataList(newDataList);

				}

				ViewLog.styledTextLogErreurs.setText(messageLog);
			} else
			{
				myEditFileForm.selectConversion(tmp);
			}
		}

	}

	public void updateFromModel(E_TypeConversion convType)
	{

		reinitRules();
		rule_process(true);
		rule_fileType(true);
		myConversionType = convType;
		rule_conversionType(true);

		myConversionType = getCurrentConversionTypeWithRules(convType);

		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);

		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		Nemo.getInstance().getViewMain().getEditDataForm()
				.enableP011viaP021(isConversionToODV()|| isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
                .enableP091(!isConversionToNetCDF());
		ViewLog.styledTextLogErreurs.setText(messageLog);

	}

	public void updateFromModel(VerticalParametersList verticalParametersList)
	{
		myVerticalParametersList = verticalParametersList;
		Nemo.getInstance().getViewMain().getEditDataForm()
				.getMyGroupeVerticalParameters().select(myVerticalParametersList);
	}

	/**
	 * Getter sur myVerticalParametersList.
	 * 
	 * @return the myVerticalParametersList
	 */
	public VerticalParametersList getMyVerticalParametersList()
	{
		return myVerticalParametersList;
	}

	/**
	 * CLIC SUR BOUTON BROWSE
	 */
	public void updateFromBrowse()
	{
		// conservation du process courant
		E_ProcessType currentProcess = myProcessType;
		// conservation du message courant (le browse peut avoir affiché un
		// message)
		messageLog = ViewLog.styledTextLogErreurs.getText() + Global.RN;

		myProcessType = currentProcess;
		myFileType = DEFAULT_FILETYPE;
		rule_process(true);
		rule_fileType(true);
		myConversionType = getDefaultConversionTypeWithRules();
		rule_conversionType(true);
		myEditFileForm.razText();
		myEditFileForm.resetDefaultSelectionSeparator();
		myEditFileForm.selectTypeFile(myFileType);
		myEditFileForm.updateEnableConversionTypes();
		myEditFileForm.selectConversion(myConversionType);
		Nemo.getInstance().getViewMain().enableCruiseTab(isCruiseEnabled());
		Nemo.getInstance().getViewMain().getEditDataForm()
				.enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
        .enableP091(!isConversionToNetCDF());
		myDataListType = DEFAULT_DATALIST;
		Nemo.getInstance().getViewMain().getEditDataForm()
				.selectDataList(DataListTypeEnum.UNKNOWN);
		Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData()
				.reinitMyParameterFilteredTable();
		ViewLog.styledTextLogErreurs.setText(messageLog);
	}

	/**
	 * RECEPTION MAJ DATALIST
	 */

	public void updateFromIHM(DataListTypeEnum dataListType)
	{
		boolean confirm = true;
		if (dataListType != myDataListType)
		{
			if (Nemo.getInstance().getViewMain().getEditDataForm()
					.getMyTableData().getTypes().size() > 0)
			{
				confirm =
						MessageDialog.openConfirm(null,
								Messages.edit_data_dataListDialogBox_title,
								Messages.edit_data_dataListDialogBox_text);
			}
			if (confirm)
			{
				myDataListType = dataListType;
				Nemo.getInstance().getViewMain().getEditDataForm()
						.reinitTableData();
				Nemo.getInstance().getViewMain().getEditDataForm()
						.getMyGroupeVerticalParameters().removeAllToAllCombos();
				// !!! le fillTypes doit être appelé APRES le removeToAllcombos,
				// car il peut ajouter PRES automatiquement
				Nemo.getInstance().getViewMain().getEditDataForm()
						.getMyTableData().fillTypes();
			} else
			{
				// si pas confirmation, et ne touche a rien et on remet la
				// valeur de depart
				Nemo.getInstance().getViewMain().getEditDataForm()
						.selectDataList(myDataListType);
			}
		}

	}

	public void updateFromModel(DataListTypeEnum dataListType)
	{

		myDataListType = dataListType;
		Nemo.getInstance().getViewMain().getEditDataForm()
				.enableP011viaP021(isConversionToODV() || isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
                .enableP091(!isConversionToNetCDF());
		Nemo.getInstance().getViewMain().getEditDataForm()
				.selectDataList(dataListType);
		ViewLog.styledTextLogErreurs.setText(messageLog);

	}

	/**
	 * RECEPTION MAJ MesureType
	 */

	public void updateFromIHM(E_MesureType mesureType)
	{
		myMesureType = mesureType;
		rule_mesureType();
		GroupVerticalParameters verticGroup =
				Nemo.getInstance().getViewMain().getEditDataForm()
						.getMyGroupeVerticalParameters();
		verticGroup.enable_pDepthBSBed(isP_depthBSBed_enabled());
		verticGroup.enable_pDepthBSSurf(isP_depthBSSurf_enabled());
		verticGroup.enable_pPressure(isP_pressure_enabled());
	}

	public void updateFromModel(E_MesureType mesureType)
	{
		if (mesureType == E_MesureType.UNKNOWN)
		{
			myMesureType = DEFAULT_MESURETYPE;
		} else
		{
			myMesureType = mesureType;
		}
		rule_mesureType();
		GroupVerticalParameters verticGroup =
				Nemo.getInstance().getViewMain().getEditDataForm()
						.getMyGroupeVerticalParameters();
		verticGroup.enable_pDepthBSBed(isP_depthBSBed_enabled());
		verticGroup.enable_pDepthBSSurf(isP_depthBSSurf_enabled());
		verticGroup.enable_pPressure(isP_pressure_enabled());
		Nemo.getInstance().getViewMain().getEditDataForm()
				.selectMesureType(mesureType);

	}

	/**
	 * gère l'activation des combo pour les paramètres de reference verticale
	 * (griséees ou non)
	 */
	private void rule_mesureType()
	{
		setP_depthBSBed_enabled(false);
		setP_depthBSSurf_enabled(false);
		setP_pressure_enabled(false);

		boolean p_depthBSBed =
				(isProfile() || isTrajectory())
						&& myMesureType == E_MesureType.BSBED;

		boolean p_depthBSSurf =
				(isProfile() && myMesureType == E_MesureType.BSSURF)
						|| ((isTimeSeries() || isTrajectory())
								&& (isConversionToODV() || isConversionToNetCDF()) && myMesureType == E_MesureType.BSSURF);

		boolean p_pressure = isProfile() && myMesureType == E_MesureType.BSSURF;

		setP_depthBSBed_enabled(p_depthBSBed);
		setP_depthBSSurf_enabled(p_depthBSSurf);
		setP_pressure_enabled(p_pressure);
	}
	

	/******************************************
	 * REGLES
	 ******************************************/

	private void rule_process(boolean addToLog)
	{
		// FAE 0001134: dans le cas d'une collection, on ne peut generer que le
		// format ODV
		if (isProcessCollection())
		{
			medatlasEnabled = false;
			if (addToLog)
			{
				messageLog +=
						Messages.edit_file_info_medatlasForbiddenBecauseCollection
								+ Global.RN;
			}
		}
	}

	private void rule_fileType(boolean addToLog)
	{
		// FAE9651
		// FAE1386 : desactiver "One File per station" si Trajectory
		/*
		 * if (myFileType == E_InputType.TRAJECTORY)
		 * {
		 * odvMonostationEnabled = false;
		 * if (addToLog)
		 * {
		 * messageLog +=
		 * Messages.edit_file_info_odvMonoForbiddenBecauseTrajectory +
		 * Global.RN;
		 * }
		 * }
		 */
	}

	private void rule_conversionType(boolean addToLog)
	{
		if (myConversionType == E_TypeConversion.ODV_MONOSTATION
				|| myConversionType == E_TypeConversion.ODV_MULTISTATIONS)
		{
			cruiseEnabled = false;
			dataList_P011viaP021_Enabled = true;
			if (addToLog)
			{
				messageLog +=
						Messages.edit_file_info_cruiseDisabledBecauseODV
								+ Global.RN;
			}
		} else if (myConversionType == E_TypeConversion.MEDATLAS)
		{
			if (addToLog)
			{
				messageLog +=
						Messages.edit_file_info_P011viaP021_disabled + Global.RN;
			}
		} else if(myConversionType == E_TypeConversion.NETCDF_MONOSTATION
                || myConversionType == E_TypeConversion.NETCDF_MULTISTATIONS)
		{
		    cruiseEnabled = true;
            dataList_P011viaP021_Enabled = true;
            dataList_P091_Enabled = false;
		    if (addToLog)
            {
                messageLog += Messages.edit_file_info_P091disabled + Global.RN;
            }
		}
	}

	/***********************************************************************************************************
	 * recup proprietes (raccourcis)
	 *********************************************************************************************************/
	// PROCESS

	public boolean isCruiseInADirectory()
	{
		return myProcessType == E_ProcessType.COLLECTION_DIR
				|| myProcessType == E_ProcessType.CRUISE_DIR || myProcessType == E_ProcessType.COLLECTION_FILES;
	}

	public boolean isProcessCollectionDir()
	{
		// CollectionGroupedByCruise
		return myProcessType == E_ProcessType.COLLECTION_DIR;
	}

	public boolean isProcessCollectionFiles()
	{
		// Collection NOT GroupedByCruise
		return myProcessType == E_ProcessType.COLLECTION_FILES;
	}

	public boolean isProcessCollection()
	{
		return (myProcessType == E_ProcessType.COLLECTION_DIR || myProcessType == E_ProcessType.COLLECTION_FILES);
	}

	// CONVERSION
	public boolean isConversionToODV()
	{
		return (myConversionType == E_TypeConversion.ODV_MONOSTATION || myConversionType == E_TypeConversion.ODV_MULTISTATIONS);
	}

    public boolean isConversionToNetCDF()
    {
        return (myConversionType == E_TypeConversion.NETCDF_MONOSTATION || myConversionType == E_TypeConversion.NETCDF_MULTISTATIONS);
    }

	public boolean isDataListTypeP011viaP021()
	{
		return myDataListType == DataListTypeEnum.P011_VIA_P021;
	}

	public boolean isDataListTypeP091()
	{
		return myDataListType == DataListTypeEnum.P091;
	}

	public boolean isDataListTypeP011viaP091()
	{
		return myDataListType == DataListTypeEnum.P011_VIA_P091;
	}

	/***********************************************************************************************************
	 * getters & setters
	 *********************************************************************************************************/
	// PROCESS
	public E_ProcessType getMyProcessType()
	{
		return myProcessType;
	}

	public boolean isProcessCruiseFile()
	{
		return myProcessType == E_ProcessType.CRUISE_FILE;
	}

	public boolean isProcessCruiseDir()
	{
		return myProcessType == E_ProcessType.CRUISE_DIR;
	}

	// FILETYPE
	public E_InputType getMyFileType()
	{
		return myFileType;
	}

	public boolean isTrajectory()
	{
		return myFileType == E_InputType.TRAJECTORY;
	}

	public boolean isProfile()
	{
		return myFileType == E_InputType.PROFILE;
	}

	public boolean isTimeSeries()
	{
		return myFileType == E_InputType.TIMESERIES;
	}

	// CONVERSION
	public boolean isConversionToODVMonoStation()
	{
		return myConversionType == E_TypeConversion.ODV_MONOSTATION;
	}

	public boolean isConversionToODVMultiStation()
	{
		return myConversionType == E_TypeConversion.ODV_MULTISTATIONS;
	}
	
	public boolean isConversionToNetCDFMonoStation()
    {
        return myConversionType == E_TypeConversion.NETCDF_MONOSTATION;
    }

    public boolean isConversionToNetCDFMultiStation()
    {
        return myConversionType == E_TypeConversion.NETCDF_MULTISTATIONS;
    }

	public boolean isConversionToMedatlas()
	{
		return myConversionType == E_TypeConversion.MEDATLAS;
	}

	public E_TypeConversion getMyConversionType()
	{
		return myConversionType;
	}

	public boolean isMedatlasEnabled()
	{
		return medatlasEnabled;
	}

	public boolean isNetCDFMonostationEnabled()
	{
		return netCDFMonostationEnabled;
	}

	public boolean isNetCDFMultistationEnabled()
	{
		return netCDFMultistationEnabled;
	}
	
	public boolean isOdvMonostationEnabled()
    {
        return odvMonostationEnabled;
    }

    public boolean isOdvMultistationEnabled()
    {
        return odvMultistationEnabled;
    }

	// CRUISE
	public boolean isCruiseEnabled()
	{
		return cruiseEnabled;
	}

	public boolean isDataList_P011viaP021_Enabled()
	{
		return dataList_P011viaP021_Enabled;
	}
	
	public boolean isDataList_P091_Enabled()
    {
        return dataList_P091_Enabled;
    }

	public DataListTypeEnum getMyDataListType()
	{
		return myDataListType;
	}

	public boolean isP_depthBSBed_enabled()
	{
		return p_depthBSBed_enabled;
	}

	public void setP_depthBSBed_enabled(boolean bed_enabled)
	{
		p_depthBSBed_enabled = bed_enabled;
	}

	public boolean isP_depthBSSurf_enabled()
	{
		return p_depthBSSurf_enabled;
	}

	public void setP_depthBSSurf_enabled(boolean surf_enabled)
	{
		p_depthBSSurf_enabled = surf_enabled;
	}

	public boolean isP_pressure_enabled()
	{
		return p_pressure_enabled;
	}

	public void setP_pressure_enabled(boolean p_pressure_enabled)
	{
		this.p_pressure_enabled = p_pressure_enabled;
	}

	public E_MesureType getMyMesureType()
	{
		return myMesureType;
	}

}
