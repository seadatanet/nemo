package fr.ifremer.nemo.ui.edit.data;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.DataModel;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.VerticalParametersList;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.data.inputData.InputTableData;
import fr.ifremer.nemo.ui.edit.data.listeners.DataListRadioSelectionListener;
import fr.ifremer.nemo.ui.edit.data.listeners.MesureTypeAboveSeaLevelSelectionListener;
import fr.ifremer.nemo.ui.edit.data.listeners.MesureTypeBelowSeaBedSelectionListener;
import fr.ifremer.nemo.ui.edit.data.listeners.MesureTypeBelowSeaSurfaceSelectionListener;
import fr.ifremer.nemo.ui.tables.FallRateFilteredTable;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;

public class EditDataForm extends EditForm {

	// les données utilisées dans cette table myTable
	private TableData myTableData;
	private StyledText textAreaData;
	private InputTableData inputTableData;


	private Composite compositeData;

	// choix du format des parametres
	private Button button_P011viaP091;
	private Button button_P091;
	private Button button_P011viaP021;
	// choix du type de mesure
	private Button mesType_belowSeaSurface;
	private Button mesType_belowSeaBed;
	private Button mesType_AboveSeaLevel;

	private FallRateFilteredTable fallrateTable;


	private GroupVerticalParameters myGroupeVerticalParameters;
	private Group groupFallrate;
	private Text fallrateCode;
	private Text fallrateLabel;
	private Composite inputComposite;
	private boolean mustBeReinit;
	private boolean mustFillFromModel;
	private boolean mustReinitInputData;


	public boolean isMustReinitInputData() {
		return mustReinitInputData;
	}
	public void setMustReinitInputData(boolean mustReinitInputData) {
		this.mustReinitInputData = mustReinitInputData;
	}
	public boolean isMustFillFromModel() {
		return mustFillFromModel;
	}
	public void setMustFillFromModel(boolean mustFillFromModel) {
		this.mustFillFromModel = mustFillFromModel;
	}
	public boolean isMustBeReinit() {
		return mustBeReinit;
	}
	public void setMustBeReinit(boolean mustBeReinit) {
		this.mustBeReinit = mustBeReinit;
	}
	public void setFallrateGroupEnabled(boolean enabled){
		groupFallrate.setEnabled(enabled);
		for (Control child : groupFallrate.getChildren())
			child.setEnabled(enabled);


		if (!enabled){
			fallrateCode.setText(Global.EMPTY_STRING);
			fallrateLabel.setText(Global.EMPTY_STRING);
		}
	}
	public String getFallrateCode() {
		return fallrateCode.getText();
	}

	public void setFallrateCode(String fallrateCode) {
		this.fallrateCode.setText(fallrateCode);
	}

	public String getFallrateLabel() {
		return fallrateLabel.getText();
	}

	public void setFallrateLabel(String fallrateLabel) {
		this.fallrateLabel.setText(fallrateLabel);
	}

	public EditDataForm() {
		super();
		myValidator = new DataFormValidator();
	}

	public void init() {
		RulesManager.getInstance().resetDataForm();
	}

	@Override
	public void createEditionForm(Composite parent) {
		super.createEditionForm(parent);
		myGroupeVerticalParameters = new GroupVerticalParameters();

		myTableData = new TableData();
		myTableData.go();

		// init composants IHM
		initSections();

		formEdition.getBody().setLayout(new GridLayout());
		formEdition.setText(Messages.edit_data_title);

		// bouton validate
		createButtonValidate();
		buttonValidate
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e)
			{
				validate();
			}
		});
		// reset
		createButtonRaz();

		// bouton collapse all / expand all
		createButtonExpand();

		// groupe de selection du dataList (pour selectionner la liste à
		// utiliser: P091, P011viaP091 ou P011viaP021)
		createGroupDataList();

		// groupe de selection du type de mesure (sous l'eau ou dans le sol)
		createGroupMesureType();
		// groupe de selection des paramètres verticaux
		createGroupVerticalParametersChoices();


		createGroupFallrate();

		drawSections();
	}

	@Override
	protected void initSections() {

	}

	@Override
	public void afficheEdition() {
		super.afficheEdition();

		ViewLog.aideData();
		RulesManager rm =RulesManager.getInstance();
		if (isMustReinitInputData()) {
			if (textAreaData != null)
				textAreaData.dispose();
			if (inputTableData != null)
				inputTableData.dispose();
		}

		if (isMustBeReinit()) {
			invalidEtape();
			// reinit parameters table
			reinitTableData();
			getMyGroupeVerticalParameters().removeAllToAllCombos();
			resetFallRate();
		}

		if (isMustFillFromModel()) {
			getMyGroupeVerticalParameters().removeAllToAllCombos();
			fillFromModel();
		}
		//		else {

		setFallrateGroupEnabled(rm.isFallrateEnabled());
		selectMesureType(rm.getMyMesureType());
		selectDataList(rm.getMyDataListType());
		
		enableP091(rm.isP09Enabled());
		enableP011viaP091(rm.isP01viaP09Enabled());
		enableP011viaP021( rm.isP01viaP02Enabled());
		
		enableAboveSeaLevel(!rm.isConversionToMedatlas());


		// !!! le fillTypes doit être appelé APRES le removeToAllcombos
		if (isMustBeReinit()) {
			getMyTableData().fillTypes();
		}else {
			initFromNewModel();
		}
		if (isMustFillFromModel()) {
			getMyGroupeVerticalParameters().removeAllToAllCombos();
			fillFromModel();
		}
		getMyGroupeVerticalParameters().select(rm.getMyVerticalParametersList());
		//		}

		//*********************************
		// 33278: now we have the model -> we can display depending on separator check
		// affichage du texte dans la zone du haut
		if (isMustReinitInputData()) {
			invalidEtape();
			if (rm.isUsingTableForCsvData()){
				inputTableData = new InputTableData(this.inputComposite);
				List<String> list =  Arrays.asList(Utils.texteBrut.split("\n"));
				inputTableData.init(list);
			}else{
				// text area
				setTextAreaData(new StyledText(this.inputComposite, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL));
				getTextAreaData().setFont(Utils.fontCourier);
				textAreaData.setText(Utils.texteBrut);
			}
		}

		getMyTableData().razAndRecreateTable();
		setLoaded(true);
		Nemo.getInstance().getViewMain().setAffiche(3);

		setMustReinitInputData(false);
		setMustBeReinit(false);
	}

	@Override
	public void drawingBack() {
		super.drawingBack();

		ViewLog.aideData();
		Nemo.getInstance().getViewMain().setAffiche(3);

	}

	@Override
	public void razText() {
		boolean confirm = true;
		confirm = MessageDialog.openConfirm(null,
				Messages.edit_data_dataListDialogBox_title,
				Messages.edit_data_dataListDialogBox_text);
		if (confirm) {
			reset();
		}
	}

	public void razText(boolean askForConfirm) {
		boolean confirm = true;
		if (askForConfirm) {
			confirm = MessageDialog.openConfirm(null,
					Messages.edit_data_dataListDialogBox_title,
					Messages.edit_data_dataListDialogBox_text);
		}
		if (confirm) {
			reset();
		}
	}

	public void reset() {
		RulesManager.getInstance().resetDataForm();
		// !!! le fillTypes doit être appelé APRES le removeToAllcombos, car
		// il
		// peut ajouter PRES automatiquement
		Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().fillTypes();
	}

	@Override
	public void actualiseEdition() {
		// pas d'actualisation nécessaire pour cet onglet
	}

	public void invalidEtape() {
		super.invalidEtape(3);
		Nemo.getInstance().getViewMain().tabData.setText(Messages.onglet_data);
	}

	/**
	 * fill with current model values
	 */
	@Override
	public void fillFromModel() {

		myTableData.reinitTypes();
		DataModel dataModel = Nemo.getInstance().getDomain().getData().getModel();
		ParameterModel[] parameterModels =dataModel.getParameterModels();

		// update dataList to use
		DataListTypeEnum dataList = dataModel.getDataListType();
		E_TypeConversion conv = RulesManager.getInstance().getMyConversionType();
		if (conv == E_TypeConversion.MEDATLAS && dataList == dataList.P011_VIA_P021) {
			dataList = DataListTypeEnum.UNKNOWN;
			dataModel.setDataListType(dataList);

		}
		if ((conv == E_TypeConversion.NETCDF_MONOSTATION || conv == E_TypeConversion.NETCDF_MULTISTATIONS) && dataList == dataList.P091) {
			dataList = DataListTypeEnum.UNKNOWN;
			dataModel.setDataListType(dataList);
		}

		RulesManager.getInstance().updateFromModel(dataList);
		// update mesureType to use
		E_MesureType mesureType = Nemo.getInstance().getDomain().getData().getModel().getMesureType();
		if ((conv == E_TypeConversion.MEDATLAS) && mesureType == E_MesureType.ASLVL) {
			mesureType = E_MesureType.UNKNOWN;
			dataModel.setMesureType(mesureType);

		}
		RulesManager.getInstance().updateFromModel(mesureType);

		if (dataList != DataListTypeEnum.UNKNOWN) {
			// remplissage des parametres mesures APRES le datatype
			for (ParameterModel paraCourant : parameterModels) {
				TypeData typeData = new TypeData(paraCourant);
				myTableData.addType(typeData);
			}
		}
		// BGT: correction bug: update vertical params APRES l'ajout des
		// parameters a la table
		VerticalParametersList verticParams = dataModel.getVerticalParametersList();
		RulesManager.getInstance().updateFromModel(verticParams);

		TypeFallRate typeFallrate = new TypeFallRate(
				Nemo.getInstance().getDomain().getData().getModel().getFallrateLabel(),
				"", "", Nemo.getInstance().getDomain().getData().getModel().getFallrateCode());
		RulesManager.getInstance().updateFromModel(typeFallrate);

		// refresh de la table de l'onglet Data
		//		myTableData.razAndRecreateTable();
	}



	public void enableP011viaP021(boolean enabled) {
		button_P011viaP021.setEnabled(enabled);
	}
	public void enableP011viaP091(boolean enabled) {
		button_P011viaP091.setEnabled(enabled);
	}
	public void enableP091(boolean enabled) {
		button_P091.setEnabled(enabled);
	}

	public void enableAboveSeaLevel(boolean enabled) {
		mesType_AboveSeaLevel.setEnabled(enabled);
	}

	public void selectDataList(DataListTypeEnum theDataList) {
		button_P091.setSelection(false);
		button_P011viaP091.setSelection(false);
		button_P011viaP021.setSelection(false);

		if (theDataList == DataListTypeEnum.P091) {
			button_P091.setSelection(true);
		} else if (theDataList == DataListTypeEnum.P011_VIA_P091) {
			button_P011viaP091.setSelection(true);
		} else if (theDataList == DataListTypeEnum.P011_VIA_P021) {
			button_P011viaP021.setSelection(true);
		}

	}

	public void selectMesureType(E_MesureType mesureType) {
		mesType_belowSeaSurface.setSelection(false);
		mesType_belowSeaBed.setSelection(false);
		mesType_AboveSeaLevel.setSelection(false);

		if (mesureType == E_MesureType.BSBED) {
			mesType_belowSeaBed.setSelection(true);
		} else if (mesureType == E_MesureType.BSSURF) {
			mesType_belowSeaSurface.setSelection(true);
		} else if (mesureType == E_MesureType.ASLVL) {
			mesType_AboveSeaLevel.setSelection(true);
		}
	}

	private DataListTypeEnum getSelectedDataList() {
		DataListTypeEnum res = DataListTypeEnum.UNKNOWN;
		if (button_P091.getSelection()) {
			res = DataListTypeEnum.P091;
		} else if (button_P011viaP091.getSelection()) {
			res = DataListTypeEnum.P011_VIA_P091;
		} else if (button_P011viaP021.getSelection()) {
			res = DataListTypeEnum.P011_VIA_P021;
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initFromNewModel() {
		/**
		 * cette methode consiste à recréer un nouveau modele qu'on remplit avec
		 * les valeurs recupérées dans l'IHM
		 */

		RulesManager rulesManager = RulesManager.getInstance();
		// recuperation du model correspondant
		fr.ifremer.nemo.input.DataModel dataModel = new fr.ifremer.nemo.input.DataModel();
		dataModel.setVerticalParametersList(rulesManager.getMyVerticalParametersList());
		dataModel.setMesureType(rulesManager.getMyMesureType());
		dataModel.setFallrateCode(this.getFallrateCode());
		dataModel.setFallrateLabel(this.getFallrateLabel());

		// mise e jour des types avant validation
		myTableData.majTypeData();


		for (TypeData typeCourant : myTableData.getTypes()) {
			ParameterModel parameterModel = new ParameterModel(typeCourant);
			dataModel.addParameterModel(parameterModel);
		}
		dataModel.setDataListType(rulesManager.getMyDataListType());

		Nemo.getInstance().getDomain().getData().updateData(dataModel);
		Nemo.getInstance().getViewMain().getEditDataForm().setValidated(true);

	}

	public void createLeft(Composite left) {
		// 	WARNING: here the model is not set.
		left.setLayout(new FillLayout(SWT.VERTICAL));

		Composite textFile = new Composite(left, SWT.NONE);
		textFile.setLayout(new FillLayout());
		this.inputComposite = textFile;

		setCompositeData(new Composite(left, SWT.NONE));
		compositeData.setLayout(new FillLayout());

	}

	public void reinitTableData() {
		myTableData.reinitTable();
	}

	public void reinitParameters() {
		selectMesureType(E_MesureType.BSSURF);
		myGroupeVerticalParameters.removeAllToAllCombos();
	}

	/**
	 * This method initializes data list group
	 */
	private void createGroupDataList() {
		Group group = new Group(formEdition.getBody(), SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.edit_data_parametersList);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
		.margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

		DataListRadioSelectionListener listener = new DataListRadioSelectionListener(this);
		button_P091 = new Button(group, SWT.RADIO);
		button_P091.setText(Messages.edit_data_parameterListP091);
		button_P091.addSelectionListener(listener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P091);

		button_P011viaP091 = new Button(group, SWT.RADIO);
		button_P011viaP091.setText(Messages.edit_data_parameterListP011viaP091);
		button_P011viaP091.addSelectionListener(listener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P011viaP091);

		button_P011viaP021 = new Button(group, SWT.RADIO);
		button_P011viaP021.setText(Messages.edit_data_parameterListP011viaP021);
		button_P011viaP021.addSelectionListener(listener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P011viaP021);
	}

	/**
	 * Group pour choix du type de mesure
	 */
	private void createGroupMesureType() {
		Group group = new Group(formEdition.getBody(), SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.edit_data_mesureType_label);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
		.margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

		MesureTypeBelowSeaSurfaceSelectionListener surfListener = new MesureTypeBelowSeaSurfaceSelectionListener();
		mesType_belowSeaSurface = new Button(group, SWT.RADIO);
		mesType_belowSeaSurface.setText(Messages.edit_data_belowSeaSurface);
		mesType_belowSeaSurface.addSelectionListener(surfListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mesType_belowSeaSurface);

		MesureTypeBelowSeaBedSelectionListener bedListener = new MesureTypeBelowSeaBedSelectionListener();
		mesType_belowSeaBed = new Button(group, SWT.RADIO);
		mesType_belowSeaBed.setText(Messages.edit_data_belowSeaBed);
		mesType_belowSeaBed.addSelectionListener(bedListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mesType_belowSeaBed);

		MesureTypeAboveSeaLevelSelectionListener lvlListener = new MesureTypeAboveSeaLevelSelectionListener();
		mesType_AboveSeaLevel = new Button(group, SWT.RADIO);
		mesType_AboveSeaLevel.setText(Messages.edit_data_aboveSeaLevel);
		mesType_AboveSeaLevel.addSelectionListener(lvlListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(mesType_AboveSeaLevel);

	}

	private void createGroupVerticalParametersChoices() {

		myGroupeVerticalParameters.createGroup(formEdition);
	}

	private void createGroupFallrate(){
		groupFallrate = new Group(formEdition.getBody(), SWT.NONE);
		groupFallrate.setEnabled(false);
		groupFallrate.setFont(Utils.font08);
		groupFallrate.setText(Messages.edit_data_fallrate_grouplabel);
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true)
		.margins(4, 4).applyTo(groupFallrate);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(groupFallrate);

		SelectionListener fallrateListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				fallrateTable = new FallRateFilteredTable();
				fallrateTable.open(Nemo.getInstance().getViewMain().getEditDataForm()
						.getCompositeData().getParent());


			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);

			}
		};
		SelectionListener fallrateResetListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				resetFallRate();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				widgetSelected(arg0);

			}
		};
		Button button_chooseFallRate = new Button(groupFallrate, SWT.PUSH);
		button_chooseFallRate.setText(Messages.edit_data_fallrate_selectButtonLabel); 
		button_chooseFallRate.addSelectionListener(fallrateListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button_chooseFallRate);

		fallrateCode = new Text(groupFallrate, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(fallrateCode);
		fallrateLabel = new Text(groupFallrate, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(fallrateLabel);

		Button button_resetFallRate = new Button(groupFallrate, SWT.PUSH);
		button_resetFallRate.setText(Messages.edit_data_fallrate_resetButtonLabel); 
		button_resetFallRate.addSelectionListener(fallrateResetListener);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button_resetFallRate);


	}
	public void resetFallRate()
	{
		setFallrateCode(Global.EMPTY_STRING);
		setFallrateLabel(Global.EMPTY_STRING);
	}
	public TableData getMyTableData() {
		return myTableData;
	}

	public StyledText getTextAreaData() {
		return textAreaData;
	}

	public void setTextAreaData(StyledText textAreaData) {
		this.textAreaData = textAreaData;
	}

	public Composite getCompositeData() {
		return compositeData;
	}

	public void setCompositeData(Composite compositeData) {
		this.compositeData = compositeData;
	}

	public Button getButton_P011viaP021() {
		return button_P011viaP021;
	}

	public Button getButton_P011viaP091() {
		return button_P011viaP091;
	}

	public Button getButton_P091() {
		return button_P091;
	}

	public GroupVerticalParameters getMyGroupeVerticalParameters() {
		return myGroupeVerticalParameters;
	}
	public InputTableData getInputTableData() {
		return inputTableData;
	}


}
