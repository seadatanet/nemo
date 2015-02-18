package fr.ifremer.nemo.ui.edit.cruise;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.icu.text.SimpleDateFormat;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.SWTProcessWS;
import fr.ifremer.nemo.ui.SWTProgressWS;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.Couple;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.cruise.CruiseSection.itemID;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.ws.WSDataType;

public class EditCruiseForm extends EditForm {
	private Button buttonInitServeur, buttonXML;
	private CruiseSection sectionReference;
	private CruiseSection sectionLocalisation, sectionSource, sectionCentre;
	private CruiseSection sectionCommentaire;
	private CruiseDatatypeSection sectionType;
	private Composite _parent;
	private StyledText textAreaCruise;

	public EditCruiseForm() {
		super();
		myValidator = new CruiseFormValidator();
	}

	@Override
	public void createEditionForm(Composite parent) {
		super.createEditionForm(parent);

		_parent = parent;

		formEdition.getBody().setLayout(new GridLayout());
		formEdition.setText(Messages.edit_cruise_title);

		// init composants IHM
		initSections();

		// bouton validate
		createButtonValidate();
		buttonValidate.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				// Etape.validationCruise();
				validate();
			}
		});

		// bouton serveur
		buttonInitServeur = new Button(formEdition.getBody(), SWT.NONE);
		buttonInitServeur.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		buttonInitServeur.setText(Messages.button_serverIni);
		buttonInitServeur.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				razText();
				init_webService();
			}
		});

		// bouton XML
		buttonXML = new Button(formEdition.getBody(), SWT.NONE);
		buttonXML.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
		buttonXML.setText(Messages.button_xmlIni);
		buttonXML.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
		{
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
			{
				razText();
				ouvertureDescriptionXML();
			}
		});

		// bouton reset
		createButtonRaz();

		// bouton collapse all / expand all
		createButtonExpand();

		// Draw Station Sections
		drawSections();
	}

	@Override
	public void afficheEdition() {
		super.afficheEdition();

		ViewLog.aideCruise();

		// affichage du texte dans la zone de gauche
		textAreaCruise.setText(Utils.texteBrut);
		EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();

		if (!isLoaded()) {
			// INFOS sur les fichiers utilises
			if (editFileForm.isCruiseIsAFile()) {
				// fichier campagne
				Utils.messageInfoAppend(Messages.view_file_cruiseFile);
			} else {
				// dossier campagne
				Utils.messageInfoAppend(Messages.view_file_cruiseDir + editFileForm.nomRepertoire);
			}

			if (RulesManager.getInstance().isTimeSeries()) {
				// Série temporelle
				Utils.messageInfoAppend(Messages.typeFile_timeseries);
			} else if (RulesManager.getInstance().isTrajectory()) {
				// Trajectoire
				Utils.messageInfoAppend(Messages.typeFile_trajectory);
			}
		}
		setLoaded(true);
		Nemo.getInstance().getViewMain().setAffiche(1);

		// remplissage de l'IHM par le modèle chargé
		fillFromModel();
		filled = true;

		final Text textCommentaire = sectionCommentaire.getItem(itemID.COMMENT).getValueText();
		textCommentaire.addFocusListener(new org.eclipse.swt.events.FocusListener()
		{
			public void focusGained(org.eclipse.swt.events.FocusEvent e)
			{

			}

			public void focusLost(org.eclipse.swt.events.FocusEvent e)
			{

				int lastIndex = 1;
				int lastIndexOfSpace = 1;
				int indexOfCR = 1;
				String tamponOrigine = textCommentaire.getText();
				String tampon80 = Global.EMPTY_STRING;
				String tamponSortie = Global.EMPTY_STRING;

				while (tamponOrigine.length() > 80)
				{

					tampon80 = tamponOrigine.substring(0, 80);

					lastIndexOfSpace = tampon80.lastIndexOf(Global.SPACE);
					indexOfCR = tampon80.indexOf("\r"); //$NON-NLS-1$

					if (indexOfCR > -1)
					{
						lastIndex = indexOfCR;
						tamponOrigine = tamponOrigine.substring(lastIndex + 2);
					} else
					{
						lastIndex = lastIndexOfSpace;
						tamponOrigine = tamponOrigine.substring(lastIndex + 1);
					}

					tamponSortie += tampon80.substring(0, lastIndex).trim() + Global.RN;

				}

				tamponSortie += tamponOrigine;
				textCommentaire.setText(tamponSortie);

				// textCommentaire.pack();

			}
		});

		GridDataFactory.fillDefaults().grab(true, true).hint(Global.SIZE_SECTION, 200).minSize(Global.SIZE_SECTION, 200).applyTo(textCommentaire);

		actualiseEdition();

		// refresh du formulaire
		formEdition.redraw();
		formEdition.reflow(true);
	}

	@Override
	public void actualiseEdition() {
	}
	public void defineMandatorySections() {
		Couple item;
		
		buttonInitServeur.setEnabled(RulesManager.getInstance()
				.isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
		buttonXML.setEnabled(RulesManager.getInstance()
				.isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
		
		if (sectionReference!=null){
		// REFERENCE
		sectionReference.setMandatory(true);
		item = sectionReference.getItem(itemID.REFERENCE);
		item.setMandatory(true);
		item = sectionReference.getItem(itemID.LOCAL_CSR_ID);
		item.setMandatory(false);
		item = sectionReference.getItem(itemID.NAME);
		item.setMandatory(RulesManager.getInstance().isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
		item = sectionReference.getItem(itemID.SHIP);
		item.setMandatory(RulesManager.getInstance().isConversionToMedatlas());

		// LOCALISATION
		sectionLocalisation.setEnabled(RulesManager.getInstance()
				.isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());

		sectionLocalisation.setMandatory(RulesManager.getInstance()
				.isConversionToMedatlas());
		for (int i = 0; i < sectionLocalisation.getItems().size(); i++) {
			sectionLocalisation
					.getItems()
					.get(i)
					.setMandatory(
							RulesManager.getInstance().isConversionToMedatlas());
		}

		// SOURCE
		sectionSource.setEnabled(RulesManager.getInstance()
				.isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
		sectionSource.setMandatory(RulesManager.getInstance()
				.isConversionToMedatlas());

		item = sectionSource.getItem(itemID.COUNTRY);
		item.setMandatory(RulesManager.getInstance().isConversionToMedatlas());// country

		item = sectionSource.getItem(itemID.CHIEF);
		item.setMandatory(RulesManager.getInstance().isConversionToMedatlas());// chief
		// scientist

		// ARCHIVING CENTER
		sectionCentre.setEnabled(RulesManager.getInstance()
				.isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
		sectionCentre.setMandatory(RulesManager.getInstance()
				.isConversionToMedatlas());
		item = sectionCentre.getItems().get(0);
		item.setMandatory(RulesManager.getInstance().isConversionToMedatlas());// data
		// center
		item = sectionCentre.getItems().get(1);
		sectionCentre
				.getItems()
				.get(1)
				.setMandatory(
						RulesManager.getInstance().isConversionToMedatlas());// confidentiality
		// DATA TYPE
		sectionType.setEnabled(RulesManager.getInstance()
				.isConversionToMedatlas()
				|| RulesManager.getInstance().isConversionToNetCDF());
		sectionType.setMandatory(RulesManager.getInstance()
				.isConversionToMedatlas());
		}
	}
	public void init_webService() {
		// selection de la ref campagne dans une liste
		Nemo.getInstance().getViewMain().getMyFilteredTableFactory().go(_parent, Global.WSSURVEY, null);
	}

	public boolean webService(String reference) {
		boolean ok = true;
		// remplissage du dataModel à partir des valeurs du ws

		TableDataSurvey.types.clear();

		SWTProcessWS.referenceWS = reference;
		SWTProcessWS.fichiersConstantes = false;

		/*
		 * if (ViewMain.dossierCampagne){ SWTProcessWS.dossier = true; } else{
		 * SWTProcessWS.dossier = false; }
		 */
		String error = new SWTProgressWS().run();
		Utils.messageErreur("");
		if (!error.equals("")) {
			Utils.messageErreur(error);
			ok = false;
		} else {
			// mise à jour de l'onglet courant
			// DataModel.fillCruise();
			fillFromModel();
		}
		// majApresWS();
		return ok;
	}

	protected void ouvertureDescriptionXML() {
		String nomXML;
		File fich;

		// remplissage du dataModel e partir des valeurs du fichier XML de
		// description

		// verifier que un fichier est charge avant d'ouvrir un modele
		if ((!Nemo.getInstance().getViewMain().getEditFileForm().isValidated()) && (!isLoaded())) {
			Utils.messageErreurAppend(Messages.error_cruise_openXml);
		} else {
			FileDialog dialog = new FileDialog(new Shell(), SWT.OPEN | SWT.APPLICATION_MODAL);
			dialog.setText(Messages.edit_cruise_openXML);
			String extensions[] = new String[1];
			extensions[0] = Global.STAR + Global.EXT_XML;
			dialog.setFilterExtensions(extensions);
			nomXML = dialog.open();
			if (nomXML != null) {
				fich = new File(nomXML);
				try {
					chargementXML(fich);
					Utils.messageOKAppend(Messages.success_openSurveyDesc);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void chargementXML(File fichier) throws Exception {
		// reset de la liste des dataTypes
		TableDataSurvey.types.clear();

		CruiseDomain cruiseDomain = Nemo.getInstance().getDomain().getCruise();
		// cruiseDomain.getModel().reset();

		CruiseModel cruiseModel = importXML(fichier);
		
		//chargement de la liste via le nom car le fichier ne contient que l'information par nom
		if (cruiseModel.getCountryName() != null) {
			Couple pays = sectionSource.getItem(itemID.COUNTRY);
			Utils.selectComboLibelle(pays.getValueCombo(), cruiseModel.getCountryName());
		}
		
		//transcode seulement en ISO_19115
		//19139 contient deja des nom de region en C19
		if(cruiseModel.getXmlVersion() == CruiseModel.XmlVersion.ISO_19115)
		{
			String C19RegionName = CruiseModel.getTranscodeRegionName(cruiseModel.getRegionName());
			cruiseModel.setRegionName(C19RegionName);
		}
		
		cruiseDomain.updateCruise(cruiseModel);

		// mise à jour de l'onglet courant
		fillFromModel();
	}

	protected CruiseModel importXML(File fichier) throws Exception {

		// Liste des capteurs
		ArrayList<String> listSensor = new ArrayList<String>();

		WSDataType[] listWSDT = ManageLists.getDataTypes();
		for (int i = 0; i < listWSDT.length; i++) {
			if (listWSDT[i].isSensor()) {
				listSensor.add(listWSDT[i].getCode());
			}
		}

		CruiseModel cruise = new CruiseModel();
		cruise.importFromXML(fichier);
		cruise.setDataCentre(Nemo.getInstance().getConfig().getDataCenter());

		// Mej des types de donnees capteurs
		for (int index = 0; index < cruise.getDataTypes().length; index++) {
			cruise.dataTypesAt(index).setSensor(listSensor.contains(cruise.dataTypesAt(index).getCode().substring(0, 3)));
		}

		

		return cruise;
	}

	@Override
	public void drawingBack() {
		super.drawingBack();

		ViewLog.aideCruise();
		Nemo.getInstance().getViewMain().setAffiche(1);
	}

	@Override
	public void initSections() {
		
		initReferenceSection();
		initLocalisationSection();
		initSourceSection();
		initArchivingCenterSection();
		initDataTypeSection();
		initCommentSection();

	}

	private void initReferenceSection() {
		if (sectionReference == null) {
			sectionReference = new CruiseSection(
					Messages.edit_cruise_sectionReference,
					Messages.edit_cruise_sectionReference,
					Messages.edit_cruise_sectionReference_lib, this);
			sectionReference.addItem(itemID.REFERENCE,
					Messages.edit_cruise_cruiseRef, ComboType.NONE,
					Global.EMPTY_STRING);
			sectionReference.addItem(itemID.LOCAL_CSR_ID,
					Messages.edit_cruise_cruiseLocalCsrId, ComboType.NONE,
					Global.EMPTY_STRING);
			sectionReference.addItem(itemID.NAME,
					Messages.edit_cruise_cruiseName, ComboType.NONE,
					Global.EMPTY_STRING);
			sectionReference.addItem(itemID.SHIP, Messages.edit_cruise_ship,
					ComboType.SHIP, CruiseModel.DEFAULT_SHIP_VALUE);
			listeSections.add(sectionReference);
		}
	}

	private void initLocalisationSection() {
		// LOCALISATION
		sectionLocalisation = new CruiseSection(
				Messages.edit_cruise_sectionLocation,
				Messages.edit_cruise_sectionLocation,
				Messages.edit_cruise_sectionLocation_lib, this);
		sectionLocalisation.addItem(itemID.START_DATE,
				Messages.edit_cruise_startDate, ComboType.NONE,
				Global.EMPTY_STRING);
		sectionLocalisation.addItem(itemID.END_DATE,
				Messages.edit_cruise_endDate, ComboType.NONE,
				Global.EMPTY_STRING);
		sectionLocalisation.addItem(itemID.REGION, Messages.edit_cruise_region,
				ComboType.REGION, Global.EMPTY_STRING);
		listeSections.add(sectionLocalisation);
	}

	private void initSourceSection() {
		// DATASOURCE
		sectionSource = new CruiseSection(
				Messages.edit_cruise_sectionDataSource,
				Messages.edit_cruise_sectionDataSource,
				Messages.edit_cruise_sectionDataSource_lib, this);
		sectionSource.addItem(itemID.COUNTRY, Messages.edit_cruise_country,
				ComboType.COUNTRY, Global.EMPTY_STRING);
		sectionSource.addItem(itemID.LAB, Messages.edit_cruise_lab,
				ComboType.NONE, Global.EMPTY_STRING);
		sectionSource.addItem(itemID.CHIEF, Messages.edit_cruise_chief,
				ComboType.NONE, Global.EMPTY_STRING);
		sectionSource.addItem(itemID.PROJECT, Messages.edit_cruise_project,
				ComboType.NONE, Global.EMPTY_STRING);
		listeSections.add(sectionSource);
	}

	private void initArchivingCenterSection() {
		// ARCHIVING CENTER
		sectionCentre = new CruiseSection(
				Messages.edit_cruise_sectionArchiving,
				Messages.edit_cruise_sectionArchiving,
				Messages.edit_cruise_sectionArchiving_lib, this);
		sectionCentre.addItem(itemID.DATA_CENTER,
				Messages.edit_cruise_dataCenter, ComboType.DATACENTER, Nemo
						.getInstance().getConfig().getDataCenter());
		sectionCentre.addItem(itemID.CONFIDENTIALITY,
				Messages.edit_cruise_confident, ComboType.CONFIDENT, "P"); //$NON-NLS-1$
		listeSections.add(sectionCentre);

	}

	private void initDataTypeSection() {
		// DATATYPE
		sectionType = new CruiseDatatypeSection(
				Messages.edit_cruise_sectionDataTypes,
				Messages.edit_cruise_sectionDataTypes,
				Messages.edit_cruise_sectionDataTypes_lib, this);
		listeSections.add(sectionType);
	}

	private void initCommentSection() {

		sectionCommentaire = new CruiseSection(
				Messages.edit_cruise_sectionComments,
				Messages.edit_cruise_sectionComments,
				Messages.edit_cruise_sectionComments_lib, this);
		sectionCommentaire.addItem(itemID.COMMENT, Global.EMPTY_STRING,
				ComboType.BLOCK, Global.EMPTY_STRING);
		listeSections.add(sectionCommentaire);

		// TODO 20755 change labels
		// sectionCorrespondingURL = new
		// CruiseSection(Messages.edit_cruise_sectionCorrespondingUrl,
		// Messages.edit_cruise_sectionCorrespondingUrl,
		// Messages.edit_cruise_sectionCorrespondingUrl_lib, this);
		// sectionCorrespondingURL.addItem(Global.EMPTY_STRING, ComboType.NONE,
		// Global.EMPTY_STRING, true, true, "auto"); // TODO 20755
		// listeSections.add(sectionCorrespondingURL);
	}
	public CruiseSection getSectionReference() {
		return sectionReference;
	}

	public CruiseSection getSectionLocalisation() {
		return sectionLocalisation;
	}

	public CruiseSection getSectionSource() {
		return sectionSource;
	}

	public CruiseDatatypeSection getSectionType() {
		return sectionType;
	}

	public CruiseDatatypeSection getSectionDataType() {
		return sectionType;
	}
	@Override
	protected void disposeElements() {
		if (buttonInitServeur != null)
			buttonInitServeur.dispose();
		if (buttonXML != null)
			buttonXML.dispose();
	}

	public void invalidEtape() {
		if (!Nemo.getInstance().getViewMain().tabCruise.isDisposed()) {
			super.invalidEtape(1);
			Nemo.getInstance().getViewMain().tabCruise.setText(Messages.onglet_cruise);
		}
	}

	@Override
	public void initFromNewModel() {
		// recuperation du model correspondant
		CruiseModel cruiseModel = new CruiseModel();

		// maj des dataTypes
		TableDataSurvey.majTypeData();

		// injection des valeurs de l'IHM -> datamodel
		// reference
		if (Nemo.getInstance().getViewMain().tabCruise.isDisposed()) {
			setValidated(true);
			return;
		}


		if (sectionReference.isEnabled()) {
			Couple refCampagne = sectionReference.getItem(itemID.REFERENCE);
			Couple localCsrId = sectionReference.getItem(itemID.LOCAL_CSR_ID);
			Couple nomCampagne = sectionReference.getItem(itemID.NAME);
			Couple navire = sectionReference.getItem(itemID.SHIP);
			cruiseModel.setReference(refCampagne.getText());
			cruiseModel.setLocalCSRIdentifier(localCsrId.getText());
			cruiseModel.setName(nomCampagne.getText());
			cruiseModel.setShipCode(fr.ifremer.nemo.util.Utils
					.extraitCode(navire.getText()));
			cruiseModel.setShipName(fr.ifremer.nemo.util.Utils
					.extraitValeur(navire.getText()));
		}
		// Localisation

		if (sectionLocalisation.isEnabled()) {
			Couple dateDebut = sectionLocalisation.getItem(itemID.START_DATE);
			Couple dateFin = sectionLocalisation.getItem(itemID.END_DATE);
			Couple region = sectionLocalisation.getItem(itemID.REGION);
			// DATE DE DEBUT
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
				Long dateLong = (Long) dateFormat.parseObject(dateDebut
						.getText());
				cruiseModel.setStartDate(dateLong);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// DATE DE FIN
			try {
				DateTimeFormat dateFormat = new DateTimeFormat();
				dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
				Long dateLong = (Long) dateFormat
						.parseObject(dateFin.getText());
				cruiseModel.setEndDate(dateLong);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			cruiseModel.setRegionName(fr.ifremer.nemo.util.Utils
					.extraitValeur(region.getText()));
			cruiseModel.setRegionCode(fr.ifremer.nemo.util.Utils
					.extraitCode(region.getText()));
		}
		// Source de données
		if (sectionSource.isEnabled()) {
			Couple pays = sectionSource.getItem(itemID.COUNTRY);
			Couple labo = sectionSource.getItem(itemID.LAB);
			Couple chef = sectionSource.getItem(itemID.CHIEF);
			Couple projet = sectionSource.getItem(itemID.PROJECT);
			cruiseModel.setCountryCode(fr.ifremer.nemo.util.Utils.extraitCode(pays.getText()));
			cruiseModel.setLaboratories(labo.getText());
			cruiseModel.setChiefScientist(chef.getText());
			cruiseModel.setProjectName(projet.getText());
		}
		// Centre d'archivage
		if (sectionCentre.isEnabled()) {
			Couple centreDonnee = sectionCentre.getItem(itemID.DATA_CENTER);
			Couple confidentialite = sectionCentre.getItem(itemID.CONFIDENTIALITY);

			cruiseModel.setDataCentre(fr.ifremer.nemo.util.Utils.extraitCode(centreDonnee.getText()));

			// confidentialité
			if (confidentialite.getText().equals("P - Public")) { //$NON-NLS-1$
				cruiseModel.setAvailability("publicly"); //$NON-NLS-1$
			}
			if (confidentialite.getText().equals("L - Limited")) { //$NON-NLS-1$
				cruiseModel.setAvailability("limited"); //$NON-NLS-1$
			}
			if (confidentialite.getText().equals("C - Confidential")) { //$NON-NLS-1$
				cruiseModel.setAvailability("confidential"); //$NON-NLS-1$
			}
		}
		// Commentaire
		if (sectionCommentaire.isEnabled()) {
			Couple commentaires = sectionCommentaire.getItem(itemID.COMMENT);
			cruiseModel.setComment(commentaires.getText());
		}

		// Mise à jour du model
		Nemo.getInstance().getDomain().getCruise().updateCruise(cruiseModel);

		// remplissage de la liste des dataTypes de la campagne
		if (sectionType.isEnabled()) {
			sectionType.initModel(cruiseModel);
		}
		setValidated(true);
	}

	@Override
	public void fillFromModel() {
		System.out.println("EditCruiseForm.fillFromModel");
		// recuperation du model correspondant
		CruiseModel cruiseModel = Nemo.getInstance().getDomain().getCruise()
				.getModel();
		// recupération des items
		if (sectionReference != null) {
			Couple refCampagne = sectionReference.getItem(itemID.REFERENCE);
			Couple localCsrId = sectionReference.getItem(itemID.LOCAL_CSR_ID);
			Couple nomCampagne = sectionReference.getItem(itemID.NAME);
			Couple navire = sectionReference.getItem(itemID.SHIP);

			// injection des valeurs de l'IHM -> datamodel
			// reference
			if (refCampagne != null) {
				refCampagne.setText(cruiseModel.getReference());
			}
			if (localCsrId != null) {
				localCsrId.setText(cruiseModel.getLocalCsrIdentifier());
			}
			if (nomCampagne != null) {
				nomCampagne.setText(cruiseModel.getName());
			}
			if (navire != null) {
				Utils.selectComboCodePlus(navire.getValueCombo(),
						cruiseModel.getShipCode(), cruiseModel.getShipName());
			}
		}

		if (sectionLocalisation != null) {
			Couple dateDebut = sectionLocalisation.getItem(itemID.START_DATE);
			Couple dateFin = sectionLocalisation.getItem(itemID.END_DATE);
			Couple region = sectionLocalisation.getItem(itemID.REGION);
			if (dateDebut != null && dateFin != null && region != null) {

				// DATES
				// FAE 2607
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
				Date startValue = new Date(cruiseModel.getStartDate());
				dateDebut.setText(dateFormat.format(startValue));

				Date endValue = new Date(cruiseModel.getEndDate());
				dateFin.setText(dateFormat.format(endValue));

				Utils.selectComboLibelle(region.getValueCombo(),
						cruiseModel.getRegionName());
			}

		}
		if (sectionSource != null) {
			Couple pays = sectionSource.getItem(itemID.COUNTRY);
			Couple labo = sectionSource.getItem(itemID.LAB);
			Couple chef = sectionSource.getItem(itemID.CHIEF);
			Couple projet = sectionSource.getItem(itemID.PROJECT);
			if (pays != null) {
				// Source de donnees
				if (cruiseModel.getCountryName() != null) {
					Utils.selectComboLibelle(pays.getValueCombo(),
							cruiseModel.getCountryName());
				} else {
					Utils.selectComboCode(pays.getValueCombo(),
							cruiseModel.getCountryCode());
				}
			}
			if (labo != null) {
				labo.setText(cruiseModel.getLaboratories());
			}
			if (chef != null) {
				chef.setText(cruiseModel.getChiefScientist());
			}
			if (projet != null) {
				projet.setText(cruiseModel.getProjectName());
			}

		}
		if (sectionCentre != null) {
			Couple centreDonnee = sectionCentre.getItem(itemID.DATA_CENTER);
			Couple confidentialite = sectionCentre
					.getItem(itemID.CONFIDENTIALITY);

			// Centre d'archivage
			if (centreDonnee != null) {
				Utils.selectComboCode(centreDonnee.getValueCombo(),
						cruiseModel.getDataCentre());
			}
			// confidentialite
			if (confidentialite != null) {
				String code = cruiseModel.getAvailability().getCode();
				Utils.selectComboCode(confidentialite.getValueCombo(), code);
			}

		}
		if (sectionCommentaire != null) {
			Couple commentaires = sectionCommentaire.getItem(itemID.COMMENT);
			// Commentaire
			if (commentaires != null) {
				commentaires.setText(cruiseModel.getComment());
			}
		}

		// Remplissage de la liste des dataTypes de la campagne
		if (sectionType != null) {
			sectionType.fillFromModel(cruiseModel);
		}
	}

	public StyledText getTextAreaCruise() {
		return textAreaCruise;
	}

	public void setTextAreaCruise(StyledText textAreaCruise) {
		this.textAreaCruise = textAreaCruise;
	}
}
