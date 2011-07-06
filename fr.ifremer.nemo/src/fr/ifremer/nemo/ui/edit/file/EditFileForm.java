package fr.ifremer.nemo.ui.edit.file;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.file.listeners.FileTypeSelectionListener;
import fr.ifremer.nemo.ui.edit.file.listeners.InputFileBrowseListener;
import fr.ifremer.nemo.ui.edit.file.listeners.InputFileCheckMultistationsListener;
import fr.ifremer.nemo.ui.edit.file.listeners.InputFileRadioSelectionListener;
import fr.ifremer.nemo.ui.edit.file.listeners.OutputFormatRadioSelectionListener;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;

/**
 * Description: editeur de l'onglet File
 * 
 * @author sbregent
 */
public class EditFileForm extends EditForm
{
	// variables publiques **********************************************
	public String nomFichier;
	public String nomRepertoire;
	// nom du repertoire choisi par le user (on le garde car le nomRepertoire
	// peut être ecrasé par PF_nomRepertoire,
	// et on a besoin du vrai nom pour ecrire la commande batch)
	public String nomRepertoireOrigin;
	public String nomFichierOrigin;

	// sections
	private FileSection sectionFileHeader;
	private FileStationHeader sectionStationHeader;
	private FileSection sectionEndOfStation;
	private FileEndData sectionDataTerminator;

	// zone de texte pour l'affichage des données en entrée
	private StyledText textAreaFile;

	// choix pour Cruise: fichier ou répertoire
	protected Button radioFichier = null;
	protected Button radioDossier = null;

	// choix du type de collection
	protected Button radioCollectGroupedByCruise = null; // cas 1 fichier
	protected Button radioCollectNotGroupedByCruise = null;// cas N fichiers
	protected Button chkMultiStations;

	// choix pour File type: profile, time series ou trajectory
	private Button radProfilVertical, radSerieTempo, radTrajectoire;

	// choix du format d'export
	private Button radMedatlas;
	private Button radODV;
	private Button radODVMonoFichier;
	private Button radODVMonoStation;
	private Button chkForcerTri = null;

	// choix du séparateur
	private Button radioTabulation, radioPointVirgule, radioVirgule, radioEspace, radioSeparateurAutre = null;
	private Text textSeparateur = null;
	private Button chkSeparateur = null;

	// disposition IHM
	protected Group grpODV;
	protected Composite compositeSeparateur;
	protected boolean fileLoaded;

	// validation
	private Button buttonValidateAll;

	/**
	 * constructeur
	 */
	public EditFileForm()
	{
		super();
		myValidator = new FileFormValidator();

	}

	/**
	 * initialisation à partir des valeurs par défaut (fixes ou definies dans
	 * les settings)
	 */
	public void init()
	{
		fileLoaded = false;
		setLoaded(false);
		textAreaFile.setText(Global.EMPTY_STRING);
		// separator
		enableSeparatorChoice(true);
		Nemo.getInstance().getViewMain().getMyRulesManager().resetFileForm();
	}

	public void reinitAllButProcess()
	{
		setFileLoaded(false);
		reinitTextArea();
		Nemo.getInstance().getViewMain().setAffiche(0);
		// RAZ global
		Utils.razGlobal();
		// reset de l'etape de conversion
		Nemo.getInstance().getViewMain().setEtape(0);
		// lancement du processus sur le premier onglet et
		// effacement preventif
		// si besoin
		// fillDesc(true);
		Nemo.getInstance().getViewMain().getEditFileForm().afficheEdition();
		// reinitialisation du tableau de paramètres
		Nemo.getInstance().getViewMain().getEditDataForm().reinitTableData();

		Nemo.getInstance().getViewMain().getEditDataForm().reinitParameters();
		ViewLog.styledTextLogErreurs.setText(Global.EMPTY_STRING);
	}

	public void resetDefaultEnableConversionTypes()
	{
		RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
		radMedatlas.setEnabled(rulesMgr.DEFAULT_MEDATLAS_ENABLED);
		radODV.setEnabled(rulesMgr.DEFAULT_ODVMULTISTATION_ENABLED || rulesMgr.DEFAULT_ODVMULTISTATION_ENABLED);
		radODVMonoFichier.setEnabled(rulesMgr.DEFAULT_ODVMULTISTATION_ENABLED);
		radODVMonoStation.setEnabled(rulesMgr.DEFAULT_ODVMONOSTATION_ENABLED);
	}

	public void updateEnableConversionTypes()
	{
		RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
		radMedatlas.setEnabled(rulesMgr.isMedatlasEnabled());
		radODV.setEnabled(rulesMgr.isOdvMonostationEnabled() || rulesMgr.isOdvMultistationEnabled());
		radODVMonoFichier.setEnabled(rulesMgr.isOdvMultistationEnabled());
		radODVMonoStation.setEnabled(rulesMgr.isOdvMonostationEnabled());
	}

	public void selectConversion(E_TypeConversion conversionType)
	{
		radMedatlas.setSelection(false);
		radODV.setSelection(false);
		radODVMonoFichier.setSelection(false);
		radODVMonoStation.setSelection(false);
		if (conversionType == E_TypeConversion.MEDATLAS)
		{
			radMedatlas.setSelection(true);
		} else if (conversionType == E_TypeConversion.ODV_MONOSTATION)
		{
			radODV.setSelection(true);
			radODVMonoStation.setSelection(true);
		} else if (conversionType == E_TypeConversion.ODV_MULTISTATIONS)
		{
			radODV.setSelection(true);
			radODVMonoFichier.setSelection(true);
		}
		// else, conversion=UNKNOWN
		afficheGrpODV();
	}

	public void resetDefaultSelectionProcess()
	{
		RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
		radioFichier.setSelection(rulesMgr.DEFAULT_PROCESSTYPE == E_ProcessType.CRUISE_FILE);
		radioDossier.setSelection(rulesMgr.DEFAULT_PROCESSTYPE == E_ProcessType.CRUISE_DIR);
		radioCollectGroupedByCruise.setSelection(rulesMgr.DEFAULT_PROCESSTYPE == E_ProcessType.COLLECTION_DIR);
		radioCollectNotGroupedByCruise.setSelection(rulesMgr.DEFAULT_PROCESSTYPE == E_ProcessType.COLLECTION_FILES);
	}

	public void resetDefaultSelectionSeparator()
	{
		// 6414 reinitialisation enable
		chkSeparateur.setEnabled(true);
		radioTabulation.setEnabled(true);
		radioPointVirgule.setEnabled(true);
		radioVirgule.setEnabled(true);
		radioEspace.setEnabled(true);
		radioSeparateurAutre.setEnabled(true);
		textSeparateur.setEnabled(true);
		// reinitialisation des selections
		chkSeparateur.setSelection(false);
		radioTabulation.setSelection(true);
		radioPointVirgule.setSelection(false);
		radioVirgule.setSelection(false);
		radioEspace.setSelection(false);
		radioSeparateurAutre.setSelection(false);
		textSeparateur.setText(Global.EMPTY_STRING);
		setSeparatorVisible();
	}

	public void resetDefaultSelectionFileType()
	{
		enableFileTypeChoice(true);
		RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
		radSerieTempo.setSelection(rulesMgr.DEFAULT_FILETYPE == E_InputType.TIMESERIES);
		radTrajectoire.setSelection(rulesMgr.DEFAULT_FILETYPE == E_InputType.TRAJECTORY);
		radProfilVertical.setSelection(rulesMgr.DEFAULT_FILETYPE == E_InputType.PROFILE);
	}

	public void createLeftForm(Composite parent)
	{

		// composant text (affichage du fichier en entrée)
		Composite textFile = new Composite(parent, SWT.NONE);
		textFile.setLayout(new FillLayout());
		textAreaFile = new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		textAreaFile.setFont(Utils.fontCourier);

		FormToolkit toolkitViewFile = new FormToolkit(parent.getDisplay());
		toolkitViewFile.setBackground(parent.getBackground());
		ScrolledForm formViewFile = toolkitViewFile.createScrolledForm(parent);
		formViewFile.setBackground(parent.getBackground());
		formViewFile.getBody().setLayout(new GridLayout());

		// input
		createGroupInputParameters(formViewFile.getBody());

		// output
		createGroupOutputParameters(formViewFile.getBody());

		((SashForm) parent).setWeights(new int[]
		{ 38, 62 });

	}

	@Override
	public void createEditionForm(Composite parent)
	{
		super.createEditionForm(parent);

		formEdition.getBody().setLayout(new GridLayout());
		formEdition.setText(Messages.edit_file_title);

		// init des objets
		initSections();

		buttonValidateAll = new Button(formEdition.getBody(), SWT.PUSH);
		buttonValidateAll.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		buttonValidateAll.setText(Messages.button_validateAllSteps);
		buttonValidateAll.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{

				ViewMain theViewMain = Nemo.getInstance().getViewMain();

				// validation de l'onglet FILE
				// Etape.validationFile();
				validate();

				// Les onglets sont décalés si on on est en collection =>
				// pas d'étape cruise
				// FAE 3796
				int offset = 0;
				// if (isCollection())
				if (!Nemo.getInstance().getViewMain().getMyRulesManager().isCruiseEnabled())
				{
					offset--;
					// on efface les erreurs sur cruise car il peut en
					// rester d'une precendente conversion
					Nemo.getInstance().getViewMain().getEditConversionForm().setWarningCampagne(Global.EMPTY_STRING);
				}

				if (theViewMain.getEtape() == 1)
				{
					// Sélection de l'onglet CRUISE
					theViewMain.selectTabByIndex(1);
					Utils.validationEtape(1);
					// Validation de l'onglet CRUISE
					// Etape.validationCruise();
					Nemo.getInstance().getViewMain().getEditCruiseForm().validate();
				}

				if (theViewMain.getEtape() == 2)
				{
					// Sélection de l'onglet STATION
					theViewMain.selectTabByIndex(2 + offset);
					Utils.validationEtape(2);
					// Validation de l'onglet STATION
					// Etape.validationStation();
					Nemo.getInstance().getViewMain().getEditStationForm().validate();
				}

				if (theViewMain.getEtape() == 3)
				{
					// Sélection de l'onglet DATA
					theViewMain.selectTabByIndex(3 + offset);
					Utils.validationEtape(3);
					// Validation de l'onglet DATA
					// Etape.validationData();
					Nemo.getInstance().getViewMain().getEditDataForm().validate();
				}

				if (theViewMain.getEtape() == 4)
				{
					// Sélection de l'onglet CONVERSION
					theViewMain.selectTabByIndex(4 + offset);
					Utils.validationEtape(4);
				}
			}
		});
		buttonValidateAll.setEnabled(false);

		// bouton "valider etape"
		createButtonValidate();
		buttonValidate.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				// Etape.validationFile();
				validate();
			}
		});

		// bouton RAZ
		createButtonRaz();

		// boutons collapse all / expand all
		createButtonExpand();

		// Draw Station Sections
		drawSections();
	}

	@Override
	public void afficheEdition()
	{

		if (fileLoaded)
		{
			super.afficheEdition();

			ViewLog.aideFile();
			setLoaded(true);
			Nemo.getInstance().getViewMain().setAffiche(0);

			// remplissage de l'IHM par le modèle chargé
			fillFromModel(false);
			filled = true;
			actualiseEdition();

			formEdition.redraw();
			formEdition.reflow(true);

			// nom de l'application
			Nemo.getInstance().refreshNomNemo();
		}
	}

	@Override
	public void drawingBack()
	{
		if (isLoaded())
		{
			super.drawingBack();
			Nemo.getInstance().refreshNomNemo();
			ViewLog.aideFile();
			Nemo.getInstance().getViewMain().setAffiche(0);
		}
	}

	@Override
	public void initSections()
	{
		sectionFileHeader = new FileSection(Messages.edit_file_enteteFichier_title, Messages.edit_file_enteteFichier_title, Messages.edit_file_enteteFichier, this);
		sectionFileHeader.addSetter(null, Global.ZERO);
		listeSections.add(sectionFileHeader);

		sectionStationHeader = new FileStationHeader(Messages.edit_file_enteteStation_title, Messages.edit_file_enteteStation_title, Messages.edit_file_enteteStation, this);
		sectionStationHeader.addSetter(Messages.edit_file_enteteStation_setter, Global.ZERO);
		listeSections.add(sectionStationHeader);

		sectionEndOfStation = new FileSection(Messages.edit_file_finStation_title, Messages.edit_file_finStation_title, Messages.edit_file_finStation, this);
		sectionEndOfStation.addSetter(null, Global.ZERO);
		listeSections.add(sectionEndOfStation);

		sectionDataTerminator = new FileEndData(Messages.edit_file_indicFinDonnees_title, Messages.edit_file_indicFinDonnees_title, Messages.edit_file_indicFinDonnees, this);
		sectionDataTerminator.addSetter(Messages.edit_file_sectionRuptureDonnees_end_with, Global.EMPTY_STRING);
		listeSections.add(sectionDataTerminator);
	}

	@Override
	protected void disposeElements()
	{
		super.disposeElements();
		buttonValidateAll.dispose();
	}

	@Override
	public void actualiseEdition()
	{
		// FAE 1810 - FileHeader accessible que pour un fichier campagne
		// TODO BGT fonctionnement ok mais a deplacer dans rules
		sectionFileHeader.setEnabled(radioFichier.getSelection() || isMultiStationFileChecked());
		buttonValidateAll.setEnabled(!Nemo.getInstance().getNomModele().equals(Global.EMPTY_STRING));
	}

	/**
	 * fill with current model values
	 */
	@Override
	public void fillFromModel()
	{
		fillFromModel(false);
	}

	/**
	 * fill with current model values
	 * 
	 * @param raz
	 *            : if true, release all domain values before filling
	 */
	public void fillFromModel(Boolean raz)
	{
		if (raz)
		{
			Nemo.getInstance().getDomain().release();
		}

		MediaModel mediaModel = Nemo.getInstance().getDomain().getMedia().getModel();
		// Séparateur
		setSeparateur(mediaModel.getSeparator());

		// Type de fichier
		Nemo.getInstance().getViewMain().getMyRulesManager().updateFromModel(mediaModel.getInputType());

		// --- remplissage du formulaire de droite (editFileForm)
		// Entête Fichier
		if (Nemo.getInstance().getViewMain().getMyRulesManager().isCruiseInADirectory() && !isMultiStationFileChecked())
		{
			sectionFileHeader.razValues();
		} else
		{
			sectionFileHeader.setValue(String.valueOf(mediaModel.getFileHeader()));
		}

		// Entête Station
		if (mediaModel.getStationHeaderPattern().equals(Global.EMPTY_STRING))
		{
			sectionStationHeader.selectNumber(true);
			sectionStationHeader.setValue(String.valueOf(mediaModel.getStationHeader()));
		} else
		{
			sectionStationHeader.selectNumber(false);
			sectionStationHeader.setIndicatorText(mediaModel.getStationHeaderPattern());
			sectionStationHeader.setNbLinesBeforeData(String.valueOf(mediaModel.getStationHeader()));
		}

		// Fin de Station
		sectionEndOfStation.setValue(String.valueOf(mediaModel.getEndStation()));

		// Fin des données
		sectionDataTerminator.setEndData(mediaModel.getEndData(), mediaModel.getEndDataPattern(), mediaModel.getEndDataPatternIsRegexp());

		// conversion:
		RulesManager rulesMgr = Nemo.getInstance().getViewMain().getMyRulesManager();
		// si un modele est ouvert, on affecte la valeur de ce modèle. Sinon on
		// affecte la valeur choisie dans les settings
		if (Nemo.getInstance().getNomModele().equals(Global.EMPTY_STRING))
		{
			rulesMgr.updateFromModel(Nemo.getInstance().getConfig().getDefaultConversionType());
		} else
		{
			rulesMgr.updateFromModel(Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat());
		}

		// activation option serie tempo
		enableSeparatorChoice(true);
		enableFileTypeChoice(true);
	}

	public void invalidEtape()
	{
		super.invalidEtape(0);
		Nemo.getInstance().getViewMain().tabFile.setText(Messages.onglet_file);
	}

	private void createGroupOutputParameters(Composite parent)
	{

		// ****************************************************************************************
		// principal composite
		GridLayout mainBoxLayout = new GridLayout();
		mainBoxLayout.numColumns = 1;
		mainBoxLayout.marginHeight = 0;
		GridData mainGridData = new GridData();
		mainGridData.horizontalAlignment = GridData.FILL;
		mainGridData.verticalAlignment = GridData.FILL;

		Group mainBoxGroup = new Group(parent, SWT.NONE);
		mainBoxGroup.setText(Messages.view_file_outputTitle);
		mainBoxGroup.setLayout(mainBoxLayout);
		mainBoxGroup.setLayoutData(mainGridData);

		// ****************************************************************************************
		// FORMAT
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		GridLayout mainFormatLayout = new GridLayout();
		mainFormatLayout.numColumns = 2;
		mainFormatLayout.marginHeight = 0;
		mainFormatLayout.makeColumnsEqualWidth = true;
		Composite mainFormatGroup = new Composite(mainBoxGroup, SWT.NONE);
		mainFormatGroup.setLayout(mainFormatLayout);
		mainFormatGroup.setLayoutData(gridData);

		// ****************************************************************************************
		// MEDATLAS/ODV

		OutputFormatRadioSelectionListener outputFormatRadioSelectionListener = new OutputFormatRadioSelectionListener(this);
		GridData medOrOdvgridData = new GridData();
		medOrOdvgridData.grabExcessHorizontalSpace = true;
		medOrOdvgridData.horizontalAlignment = GridData.FILL;

		GridLayout medOrOdvLayout = new GridLayout();
		medOrOdvLayout.numColumns = 1;
		medOrOdvLayout.marginHeight = 0;

		Group medOrOdvGroup = new Group(mainFormatGroup, SWT.NONE);
		medOrOdvGroup.setLayout(medOrOdvLayout);
		medOrOdvGroup.setLayoutData(medOrOdvgridData);

		radMedatlas = new Button(medOrOdvGroup, SWT.RADIO);
		radMedatlas.setText(Conversion.MEDATLAS.getName());
		radMedatlas.setSelection(false);
		radMedatlas.addSelectionListener(outputFormatRadioSelectionListener);

		radODV = new Button(medOrOdvGroup, SWT.RADIO);
		radODV.setText(Conversion.ODV.getName());
		radODV.setSelection(false);
		radODV.addSelectionListener(outputFormatRadioSelectionListener);

		// ****************************************************************************************
		// ODV type
		GridData odvTypegridData = new GridData();
		odvTypegridData.grabExcessHorizontalSpace = true;
		odvTypegridData.horizontalAlignment = GridData.FILL;

		GridLayout odvTypeLayout = new GridLayout();
		odvTypeLayout.numColumns = 1;
		odvTypeLayout.marginHeight = 0;

		grpODV = new Group(mainFormatGroup, SWT.NONE);
		grpODV.setLayout(odvTypeLayout);
		grpODV.setLayoutData(odvTypegridData);

		radODVMonoStation = new Button(grpODV, SWT.RADIO);
		radODVMonoStation.setText(Messages.view_convert_monoStation);
		radODVMonoStation.setSelection(false);

		radODVMonoStation.addSelectionListener(outputFormatRadioSelectionListener);
		radODVMonoFichier = new Button(grpODV, SWT.RADIO);
		radODVMonoFichier.setText(Messages.view_convert_monoFichier);
		radODVMonoFichier.addSelectionListener(outputFormatRadioSelectionListener);

		afficheGrpODV();

		// ****************************************************************************************
		// sort
		chkForcerTri = new Button(mainBoxGroup, SWT.CHECK);
		chkForcerTri.setFont(Utils.font08);
		chkForcerTri.setText(Messages.view_convert_sort);
	}

	private Composite createSubGroupSeparatorAndFileType(Composite parent)
	{
		// ****************************************************************************************
		// principal composite
		GridLayout mainBoxLayout = new GridLayout();
		mainBoxLayout.numColumns = 2;
		GridData mainGridData = new GridData();
		mainGridData.horizontalAlignment = GridData.FILL;
		mainGridData.verticalAlignment = GridData.FILL;

		Composite box = new Composite(parent, SWT.NONE);
		box.setLayout(mainBoxLayout);
		box.setLayoutData(mainGridData);

		return box;
	}

	private void createSubGroupSeparator(Composite parent)
	{
		Group grpSeparateur = new Group(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(grpSeparateur);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(grpSeparateur);

		chkSeparateur = new Button(grpSeparateur, SWT.CHECK);
		chkSeparateur.setText(Messages.view_file_separator);
		chkSeparateur.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g)
			{
				setSeparatorVisible();
				Utils.invalidEtapes();
				Utils.messageErreur(Global.EMPTY_STRING);
			}
		});

		compositeSeparateur = new Composite(grpSeparateur, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(compositeSeparateur);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(compositeSeparateur);

		radioTabulation = new Button(compositeSeparateur, SWT.RADIO);
		radioTabulation.setText(Messages.view_file_sepTab);
		radioTabulation.setSelection(true);

		radioPointVirgule = new Button(compositeSeparateur, SWT.RADIO);
		radioPointVirgule.setText(Messages.view_file_sepSemi);

		radioVirgule = new Button(compositeSeparateur, SWT.RADIO);
		radioVirgule.setText(Messages.view_file_sepComma);

		radioEspace = new Button(compositeSeparateur, SWT.RADIO);
		radioEspace.setText(Messages.view_file_sepSpace);

		radioSeparateurAutre = new Button(compositeSeparateur, SWT.RADIO);
		radioSeparateurAutre.setText(Messages.view_file_sepOther);
		textSeparateur = new Text(compositeSeparateur, SWT.BORDER);

		// Par defaut
		compositeSeparateur.setVisible(false);

	}

	/**
	 * This method initializes groupFichier
	 */
	private void createSubGroupFileType(Composite parent)
	{
		Group grpFichier = new Group(parent, SWT.NONE);
		grpFichier.setText(Messages.view_file_fileType);
		GridLayoutFactory.fillDefaults().applyTo(grpFichier);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(grpFichier);

		FileTypeSelectionListener fileTypeSelectionListener = new FileTypeSelectionListener(this);
		radProfilVertical = new Button(grpFichier, SWT.RADIO);
		radProfilVertical.setText(Messages.typeFile_profile);
		radProfilVertical.addSelectionListener(fileTypeSelectionListener);

		radSerieTempo = new Button(grpFichier, SWT.RADIO);
		radSerieTempo.setText(Messages.typeFile_timeseries);
		radSerieTempo.addSelectionListener(fileTypeSelectionListener);

		radTrajectoire = new Button(grpFichier, SWT.RADIO);
		radTrajectoire.setText(Messages.typeFile_trajectory);
		radTrajectoire.addSelectionListener(fileTypeSelectionListener);

		// Selection du type par defaut

		resetDefaultSelectionFileType();
	}

	private void createGroupInputParameters(Composite parent)
	{
		InputFileRadioSelectionListener inputFileRadioSelectionListener = new InputFileRadioSelectionListener(this);

		GridData subGridData = new GridData();
		subGridData.grabExcessHorizontalSpace = true;
		subGridData.horizontalAlignment = SWT.FILL;
		GridLayout sublayout = new GridLayout();
		sublayout.numColumns = 1;

		// ****************************************************************************************
		// principal composite
		GridLayout mainBoxLayout = new GridLayout();
		mainBoxLayout.numColumns = 1;
		mainBoxLayout.marginHeight = 1;
		mainBoxLayout.verticalSpacing = 1;
		GridData mainGridData = new GridData();
		mainGridData.horizontalAlignment = SWT.FILL;
		mainGridData.verticalAlignment = SWT.FILL;
		mainGridData.grabExcessHorizontalSpace = true;

		Group mainBoxGroup = new Group(parent, SWT.NONE);
		mainBoxGroup.setText(Messages.view_file_inputTitle);
		mainBoxGroup.setLayout(mainBoxLayout);
		mainBoxGroup.setLayoutData(mainGridData);

		// ****************************************************************************************
		// groupe process

		GridData processGridData = new GridData();
		processGridData.horizontalAlignment = SWT.FILL;
		processGridData.verticalAlignment = SWT.FILL;
		processGridData.grabExcessHorizontalSpace = true;

		GridLayout processLayout = new GridLayout();
		processLayout.numColumns = 4; // en realite 3 colonnes, mais bug si on
										// met 3
		processLayout.marginHeight = 1;

		Group processGroup = new Group(mainBoxGroup, SWT.NONE);
		processGroup.setLayout(processLayout);
		processGroup.setLayoutData(processGridData);

		// ****************************************************************************************
		// process a cruise

		Group inputFileCruiseGroup = new Group(processGroup, SWT.NONE);
		inputFileCruiseGroup.setText(Messages.view_file_processCruise);
		inputFileCruiseGroup.setLayout(sublayout);
		inputFileCruiseGroup.setLayoutData(subGridData);

		radioFichier = new Button(inputFileCruiseGroup, SWT.RADIO);
		radioFichier.setSelection(true);
		radioFichier.setText(Messages.view_file_cruiseFile);
		radioFichier.addSelectionListener(inputFileRadioSelectionListener);

		// TODO BGT: j'ai ajouté ici un faux bouton pour aligner correctement
		// trouver une solution plus propre
		radioDossier = new Button(inputFileCruiseGroup, SWT.RADIO);
		radioDossier.setText(Messages.view_file_cruiseDir);
		radioDossier.addSelectionListener(inputFileRadioSelectionListener);

		Button test = new Button(inputFileCruiseGroup, SWT.CHECK);
		test.setEnabled(false);
		test.setText(Messages.view_file_multiStations);
		test.setVisible(false);

		// ****************************************************************************************
		// process a collection

		Group inputFileCollectionGroup = new Group(processGroup, SWT.NONE);
		inputFileCollectionGroup.setText(Messages.view_file_processCollection);
		inputFileCollectionGroup.setLayout(sublayout);
		inputFileCollectionGroup.setLayoutData(subGridData);

		radioCollectGroupedByCruise = new Button(inputFileCollectionGroup, SWT.RADIO);
		radioCollectGroupedByCruise.setText(Messages.view_file_collectionCas1);
		radioCollectGroupedByCruise.addSelectionListener(inputFileRadioSelectionListener);

		radioCollectNotGroupedByCruise = new Button(inputFileCollectionGroup, SWT.RADIO);
		radioCollectNotGroupedByCruise.setText(Messages.view_file_collectionCasN);
		radioCollectNotGroupedByCruise.addSelectionListener(inputFileRadioSelectionListener);

		chkMultiStations = new Button(inputFileCollectionGroup, SWT.CHECK);
		chkMultiStations.setEnabled(false);
		chkMultiStations.setText(Messages.view_file_multiStations);
		// FAE 4181
		InputFileCheckMultistationsListener inputFileCheckMultistationsListener = new InputFileCheckMultistationsListener(this);
		chkMultiStations.addSelectionListener(inputFileCheckMultistationsListener);

		// ****************************************************************************************
		// browse
		Button buttonParcourir = new Button(processGroup, SWT.NONE);
		buttonParcourir.setText(Messages.button_browse);
		InputFileBrowseListener browseListener = new InputFileBrowseListener(this);
		buttonParcourir.addSelectionListener(browseListener);

		// ****************************************************************************************
		// separator & fileType
		Composite separatorAndFileType = createSubGroupSeparatorAndFileType(mainBoxGroup);
		createSubGroupSeparator(separatorAndFileType);
		createSubGroupFileType(separatorAndFileType);
	}

	/**
	 * initialise avec les valeurs du modèle par défaut {@inheritDoc}
	 */
	@Override
	public void initFromNewModel()
	{
		String nomFich;
		// puis remplissage du modele avec les valeurs de l'IHM
		if (radioFichier.getSelection())
		{
			nomFich = nomFichier;
		} else
		{
			nomFich = nomRepertoire;
		}

		MediaModel mediaModel = new MediaModel();

		// --- enregistrement du nom du fichier en entrée
		mediaModel.setSourcePath(nomFich);

		// --- enregistrement du formulaire de gauche (ViewMain)
		// Type de traitement
		mediaModel.setProcessType(Nemo.getInstance().getViewMain().getMyRulesManager().getMyProcessType());

		// Séparateur
		mediaModel.setSeparator(getSeparateur());

		// Type de fichier
		mediaModel.setInputType(Nemo.getInstance().getViewMain().getMyRulesManager().getMyFileType());

		// Fichier multiStation ?
		mediaModel.setMultiStations(isMultiStationFileChecked());

		// --- enregistrement du formulaire de droite (editFileForm)
		// Entête Fichier
		int fileHeader = 0;
		if (!Nemo.getInstance().getViewMain().getMyRulesManager().isCruiseInADirectory() || mediaModel.isMultiStations())
		{
			fileHeader = Integer.parseInt(getSectionFileHeader().getValue());
		}

		mediaModel.setFileHeader(fileHeader);

		// Entête Station
		if (!sectionStationHeader.isSelectNumber())
		{
			mediaModel.setStationHeaderPattern(sectionStationHeader.getIndicatorText());
			mediaModel.setStationHeader(sectionStationHeader.getNbLinesBeforeData());
		} else
		{
			mediaModel.setStationHeader(sectionStationHeader.getValue());
		}

		// Fin de Station
		mediaModel.setEndStation(getSectionEndOfStation().getValue());

		// Fin des données
		E_EndData end = sectionDataTerminator.getEndData();
		mediaModel.setEndData(end);
		if (end == E_EndData.END_WITH)
		{
			mediaModel.setEndDataPattern(sectionDataTerminator.getValue());
		} else if (end == E_EndData.NEXT_START_WITH)
		{
			mediaModel.setEndDataPattern(sectionDataTerminator.getStartStationIndicator());
			mediaModel.setEndDataPatternIsRegexp(sectionDataTerminator.getStartStationIndicatorRegexpCheckbox());
		} else if (end == E_EndData.BEGIN_LINE_CHANGES)
		{
			// TODO BGT
			// ici on bricole avec un pattern qui ne sert qu'à donner une taille
			// de string pour la suite - fonctionne mais moche
			Character[] tmpChar = new Character[Integer.valueOf(sectionDataTerminator.getBeginLineChangeIndicator())];
			String tmpString = "";
			for (int i = 0; i < tmpChar.length; i++)
			{
				tmpString += 'x';
			}

			mediaModel.setEndDataPattern(tmpString);
			mediaModel.setEndDataPatternIsRegexp(false);
		}
		Nemo.getInstance().getDomain().getConversion().setCurrentConversion(Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType());
		Nemo.getInstance().getDomain().getConversion().getModel().setOutputFormat(Nemo.getInstance().getViewMain().getMyRulesManager().getMyConversionType());

		// Mise à jour du model
		Nemo.getInstance().getDomain().getMedia().updateMedia(mediaModel);

	}

	public void afficheGrpODV()
	{
		grpODV.setVisible(radODV.getSelection());
	}

	public void enableMedatlas(boolean enable)
	{
		radMedatlas.setEnabled(enable);
	}

	public void enableSeparatorChoice(boolean enabled)
	{
		if (chkSeparateur.getSelection())
		{
			chkSeparateur.setEnabled(enabled);
			radioTabulation.setEnabled(enabled);
			radioPointVirgule.setEnabled(enabled);
			radioVirgule.setEnabled(enabled);
			radioEspace.setEnabled(enabled);
			radioSeparateurAutre.setEnabled(enabled);
			textSeparateur.setEnabled(enabled);
		}
	}

	public void enableFileTypeChoice(boolean enabled)
	{
		radProfilVertical.setEnabled(enabled);
		radSerieTempo.setEnabled(enabled);
		radTrajectoire.setEnabled(enabled);
	}

	/**
	 * select the appropriate radio choice depending on typeFile
	 * 
	 * @param typeFile
	 */
	public void selectTypeFile(E_InputType typeFile)
	{
		switch (typeFile)
		{
		case TRAJECTORY:
			radProfilVertical.setSelection(false);
			radSerieTempo.setSelection(false);
			radTrajectoire.setSelection(true);
			break;
		case TIMESERIES:
			radSerieTempo.setSelection(true);
			radProfilVertical.setSelection(false);
			radTrajectoire.setSelection(false);
			break;
		case PROFILE:
			radProfilVertical.setSelection(true);
			radSerieTempo.setSelection(false);
			radTrajectoire.setSelection(false);
			break;
		default:
			radProfilVertical.setSelection(false);
			radSerieTempo.setSelection(false);
			radTrajectoire.setSelection(false);
			break;
		}
	}

	/**
	 * gets the separator choosen by the user in the file tab
	 * 
	 * @return the separator specified by the user
	 */
	public char getSeparatorAsChar()
	{
		char separator = ' ';
		if (radioTabulation.getSelection())
		{
			separator = '\t';
		} else if (radioPointVirgule.getSelection())
		{
			separator = ';';
		} else if (radioVirgule.getSelection())
		{
			separator = ',';
		} else if (radioEspace.getSelection())
		{
			separator = ' ';
		} else if (radioSeparateurAutre.getSelection())
		{
			separator = getTextSeparateur().getText().toCharArray()[0];
		}
		return separator;
	}

	public String getSeparateur()
	{
		if (chkSeparateur.getSelection())
		{
			if (radioTabulation.getSelection())
			{
				// Tabulation
				return Global.TAB;
			} else if (radioPointVirgule.getSelection())
			{
				// Point-virgule
				return Global.SEMICOLON;
			} else if (radioVirgule.getSelection())
			{
				// Virgule
				return Global.COMMA;
			} else if (radioEspace.getSelection())
			{
				// Espace
				return Global.SPACE;
			} else if (radioSeparateurAutre.getSelection())
			{
				// Autre
				return textSeparateur.getText();
			}
		}
		return Global.EMPTY_STRING;
	}

	public void setSeparateur(String sep)
	{
		if (sep.equals(Global.EMPTY_STRING))
		{
			// pas de séparateur
			chkSeparateur.setSelection(false);
		} else
		{
			chkSeparateur.setSelection(true);

			if (sep.equals(Global.TAB))
			{
				// Tabulation
				radioTabulation.setSelection(true);
				radioPointVirgule.setSelection(false);
				radioVirgule.setSelection(false);
				radioEspace.setSelection(false);
				radioSeparateurAutre.setSelection(false);
				textSeparateur.setText(Global.EMPTY_STRING);
			} else if (sep.equals(Global.SEMICOLON))
			{
				// Point-virgule
				radioTabulation.setSelection(false);
				radioPointVirgule.setSelection(true);
				radioVirgule.setSelection(false);
				radioEspace.setSelection(false);
				radioSeparateurAutre.setSelection(false);
				textSeparateur.setText(Global.EMPTY_STRING);
			} else if (sep.equals(Global.COMMA))
			{
				// Virgule
				radioTabulation.setSelection(false);
				radioPointVirgule.setSelection(false);
				radioVirgule.setSelection(true);
				radioEspace.setSelection(false);
				radioSeparateurAutre.setSelection(false);
				textSeparateur.setText(Global.EMPTY_STRING);
			} else if (sep.equals(Global.SPACE))
			{
				// Espace
				radioTabulation.setSelection(false);
				radioPointVirgule.setSelection(false);
				radioVirgule.setSelection(false);
				radioEspace.setSelection(true);
				radioSeparateurAutre.setSelection(false);
				textSeparateur.setText(Global.EMPTY_STRING);
			} else
			{
				// Autre séparateur
				radioTabulation.setSelection(false);
				radioPointVirgule.setSelection(false);
				radioVirgule.setSelection(false);
				radioEspace.setSelection(false);
				radioSeparateurAutre.setSelection(true);
				textSeparateur.setText(sep);
			}
		}
		setSeparatorVisible();
	}

	public void setSeparatorVisible()
	{
		compositeSeparateur.setVisible(chkSeparateur.getSelection());
	}

	public String getSelectedText()
	{
		return textAreaFile.getText().substring(textAreaFile.getSelection().x, textAreaFile.getSelection().y);
	}

	public int getSelectedTextStartPosition()
	{
		return textAreaFile.getSelection().x;
	}

	public int getSelectedTextEndPosition()
	{
		return textAreaFile.getSelection().y;
	}

	public void reinitTextArea()
	{
		textAreaFile.setText(Global.EMPTY_STRING);
	}

	public StyledText getTextAreaFile()
	{
		return textAreaFile;
	}

	public void addSpaceBeforeDataTerminator()
	{
		sectionDataTerminator.addSpaceBeforeDataTerminator();
	}

	public boolean isFileLoaded()
	{
		return fileLoaded;
	}

	public void setFileLoaded(boolean fileLoaded)
	{
		this.fileLoaded = fileLoaded;
	}

	public FileSection getSectionFileHeader()
	{
		return sectionFileHeader;
	}

	public FileStationHeader getSectionStationHeader()
	{
		return sectionStationHeader;
	}

	public FileSection getSectionEndOfStation()
	{
		return sectionEndOfStation;
	}

	public boolean isCollectionGroupedByCruise()
	{
		return radioCollectGroupedByCruise.getSelection();
	}

	public boolean isCollectionNotGroupedByCruise()
	{
		return radioCollectNotGroupedByCruise.getSelection();
	}

	public boolean isCruiseIsAFile()
	{
		return radioFichier.getSelection();
	}

	public boolean isCruiseIsADir()
	{
		return radioDossier.getSelection();
	}

	public Text getTextSeparateur()
	{
		return textSeparateur;
	}

	public boolean isSeparatorChecked()
	{
		return chkSeparateur.getSelection();
	}

	public FileEndData getSectionDataTerminator()
	{
		return sectionDataTerminator;
	}

	public boolean isSortForced()
	{
		return chkForcerTri.getSelection();
	}

	public boolean isMultiStationFileChecked()
	{
		return chkMultiStations.getSelection();
	}

	public Button getRadioCollectGroupedByCruise()
	{
		return radioCollectGroupedByCruise;
	}

	public Button getRadioCollectNotGroupedByCruise()
	{
		return radioCollectNotGroupedByCruise;
	}

	public Button getRadioDossier()
	{
		return radioDossier;
	}

	public Button getRadioFichier()
	{
		return radioFichier;
	}

	public Button getChkMultiStations()
	{
		return chkMultiStations;
	}

	public Button getRadMedatlas()
	{
		return radMedatlas;
	}

	public Button getRadODV()
	{
		return radODV;
	}

	public Button getRadODVMonoFichier()
	{
		return radODVMonoFichier;
	}

	public Button getRadODVMonoStation()
	{
		return radODVMonoStation;
	}

	public Button getRadProfilVertical()
	{
		return radProfilVertical;
	}

	public Button getRadSerieTempo()
	{
		return radSerieTempo;
	}

	public Button getRadTrajectoire()
	{
		return radTrajectoire;
	}

	public String getNomFichier()
	{
		return nomFichier;
	}

	public void setNomFichier(String nomFichier)
	{
		this.nomFichier = nomFichier;
	}

	public String getNomRepertoire()
	{
		return nomRepertoire;
	}

	public void setNomRepertoire(String nomRepertoire)
	{
		this.nomRepertoire = nomRepertoire;
	}

}