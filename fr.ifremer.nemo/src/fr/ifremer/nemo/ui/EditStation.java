package fr.ifremer.nemo.ui;

import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditStation {
	// fonctions de remplissage de la zone d'édition pour les campagnes
	// /////////////////////////////////////////

	private static Shell shell = new Shell();

	public static Text textRefStationNumero, textRefStationLigne,
			textRefStationDebut, textRefStationFin, textHistoriqueAcquisition1,
			textHistoriqueAcquisition2, textCommentaire,
			textHistoriqueAcqLigne1, textHistoriqueAcqLigne2,
			textHistoriqueAcqDebut1, textHistoriqueAcqDebut2,
			textHistoriqueAcqFin1, textHistoriqueAcqFin2, textHistorique1,
			textHistorique2, textConversionDate, textTU, textTULigne,
			textTUDebut, textTUFin, textHeureLigne, textHeureDebut,
			textHeureFin, textSurface1, textSurface2, textSurfaceLigne1,
			textSurfaceLigne2, textSurfaceDebut1, textSurfaceDebut2,
			textSurfaceFin1, textSurfaceFin2, textDateLigne, textDateDebut,
			textDateFin, textLatitude, textLatitudeLigne, textLatitudeDebut,
			textLatitudeFin, textLongitude, textLongitudeLigne,
			textLongitudeDebut, textLongitudeFin, textProfondeur,
			textProfondeurLigne, textProfondeurDebut, textProfondeurFin,
			textCommentaireLigne, textCommentaireDebut, textCommentaireFin;

	public static Text textLatFin, textLatFinLigne, textLatFinDebut,
			textLatFinFin, textLongFin, textLongFinLigne, textLongFinDebut,
			textLongFinFin, textProfFin, textProfFinLigne, textProfFinDebut,
			textProfFinFin, textProfCapteur, textProfCapteurLigne,
			textProfCapteurDebut, textProfCapteurFin, textDeclinMagnet,
			textDeclinMagnetLigne, textDeclinMagnetDebut, textDeclinMagnetFin,
			textFreqEchant, textFreqEchantLigne, textFreqEchantDebut,
			textFreqEchantFin, textFreqEchantConv, textFreqEchantTest,
			textTestConvHeure, textTestConvDate, textHeureSaisie,
			textDateSaisie;

	// public static Text textQCDate, textQCLat, textQCLong, textQCProf;

	public static Button buttonValidate, buttonRAZ, buttonExpand,
			buttonExpand2, buttonRefStationIncremente,
			buttonHistoriqueAcqSaisie, buttonSurfaceSaisie, buttonTUSaisie,
			buttonLatSaisie, buttonLongSaisie, buttonProfondeurSaisie,
			buttonCommentaireSaisie, buttonVar, buttonVarLong, buttonVarLat,
			buttonVarLongFin, buttonVarLatFin;

	public static Button buttonHistoriqueAcqPos, buttonRefStationPos,
			buttonSurfacePos, buttonLatPos, buttonLongPos,
			buttonCommentairePos, buttonProfondeurPos;

	public static Button buttonLatFinPos, buttonLongFinPos, buttonProfFinPos,
			buttonProfCapteurPos, buttonDeclinMagnetPos, buttonFreqEchantPos,
			buttonLatFinSaisie, buttonLongFinSaisie, buttonProfFinSaisie,
			buttonProfCapteurSaisie, buttonDeclinMagnetSaisie,
			buttonFreqEchantSaisie;

	public static Button buttonHeureReference, buttonDateReference,
			buttonHeureReferencePos, buttonDateReferencePos;

	public static Button buttonHeurePos, buttonHeureSaisie, buttonHeureInject,
			buttonDatePos, buttonDateSaisie, buttonDateInject, buttonTUPos;

	public static Button btnMetaInData2;

	public static Group grpTrajectoire, grpSerieTempo;

	public static Button radMedatlas, radODV, radLigneDonnees, radFreqEchant;

	public static Combo comboHeure, comboDate, comboLatitude, comboLongitude,
			comboLatitudeFin, comboLongitudeFin;

	public static Combo comboRefStationType;

	public static Section sectionStation, sectionTypeDonnees,
			sectionHistorique, sectionCommentaire, sectionSurface,
			sectionHeure, sectionDate, sectionTU, sectionLatitude,
			sectionLongitude, sectionProfondeur;

	public static Section sectionLatFin, sectionLongFin, sectionProfFin,
			sectionProfCapteur, sectionDeclinMagnet, sectionFreqEchant,
			sectionFlagQC;

	public static Label labelConversionHeure, labelConversionDate;

	public static Boolean okApresReset;

	public static ArrayList<Section> listeSectionsStation, listeSectionsParam;

	public static ArrayList<String> listeSectionsNoms, listeTypeDonnees;

	public static ArrayList<Label> listeSectionsLabel;

	public static ArrayList<Text> listeTextStation;

	public static ArrayList<Button> listeBoutonsPos;

	public static ArrayList<Button> listeBoutonsManuel;

	public static int expand, validee, loaded, offsetDebutHeure,
			offsetFinHeure, offsetDebutDate, offsetFinDate;

	public static List listeType;

	public static ScrolledForm formEdition;

	public static Cursor cursCommentaire, cursEchant1, cursEchant2,
			cursHistorique1, cursHistorique2;

	public static Table tabCorrespondance, tabFlagQC;

	public static FlagTable flagTable;

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier

		cursCommentaire = new Cursor();
		cursEchant1 = new Cursor();
		cursEchant2 = new Cursor();
		cursHistorique1 = new Cursor();
		cursHistorique2 = new Cursor();

		formEdition = ViewEdit.formEdition;
		ViewLog.aideStation();
		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$
		okApresReset = true;

		// affichage du texte dans la zone de gauche

		// Utils.remplissageTexte(ViewFile.styledTextStation);
		ViewFile.styledTextStation.setText(Utils.texteBrut);

		loaded = 1;

		// de base les sections sont refermées
		expand = 0;

		/*
		 * recreer un formEdition sur le toolkit principal pour pouvoir faire un
		 * dispose() dessus mais garder toutes les sections afin de les
		 * réafficher par la suite dans DrawingBack.
		 */

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditStation.1")); //$NON-NLS-1$

		disposeElements();

		// init de la arrayList des sections
		listeSectionsStation = new ArrayList<Section>();
		listeSectionsNoms = new ArrayList<String>();
		listeTextStation = new ArrayList<Text>();
		listeBoutonsManuel = new ArrayList<Button>();
		listeBoutonsPos = new ArrayList<Button>();

		// init composants IHM
		initSections();
		initCombos();
		initText();
		initBoutons();
		initListeType();
		initLabels();
		initTables();

		// boutons collapse all / expand all

		formEdition.redraw();
		formEdition.reflow(true);
		// formEdition.

		// bouton validate
		buttonValidate.setParent(ViewEdit.formEdition.getBody());
		buttonValidate.setText(Messages.getString("EditStation.4")); //$NON-NLS-1$

		buttonRAZ.setParent(ViewEdit.formEdition.getBody());
		buttonRAZ.setText("Reset"); //$NON-NLS-1$

		buttonExpand.setParent(ViewEdit.formEdition.getBody());
		buttonExpand.setText("+"); //$NON-NLS-1$

		// Case à cocher "Meta In Data"
		if (ViewFile.radProfilVertical.getSelection()) {
			btnMetaInData2.setParent(ViewEdit.formEdition.getBody());
			btnMetaInData2.setText("Meta In Data");
		}

		// Selecteur de methode de trajectoire
		if (ViewFile.radTrajectoire.getSelection()) {
			grpTrajectoire.setParent(ViewEdit.formEdition.getBody());
			grpTrajectoire.setText(Messages.getString("ViewFile.trajectoire"));
			GridLayoutFactory.fillDefaults().applyTo(grpTrajectoire);
			GridDataFactory.fillDefaults().grab(false, false).applyTo(
					grpTrajectoire);

			radMedatlas.setParent(grpTrajectoire);
			radMedatlas.setText(Messages.getString("EditStation.medatlas"));
			radMedatlas.setSelection(true);

			radODV.setParent(grpTrajectoire);
			radODV.setText(Messages.getString("EditStation.odv"));
			radODV.setSelection(false);
		} else {
			grpTrajectoire.setParent(shell);
		}

		if ((ViewFile.radSerieTempo.getSelection() || (ViewFile.radTrajectoire
				.getSelection())
				&& EditStation.radMedatlas.getSelection())) {
			// série temporelle
			grpSerieTempo.setParent(ViewEdit.formEdition.getBody());
			grpSerieTempo.setText(Messages.getString("ViewFile.serieTempo"));
			GridLayoutFactory.fillDefaults().applyTo(grpSerieTempo);
			GridDataFactory.fillDefaults().grab(false, false).applyTo(
					grpSerieTempo);

			radFreqEchant.setParent(grpSerieTempo);
			radFreqEchant.setText(Messages.getString("EditData.4")); //$NON-NLS-1$
			radLigneDonnees.setParent(grpSerieTempo);
			radLigneDonnees.setText(Messages.getString("EditData.5")); //$NON-NLS-1$

			radFreqEchant.setSelection(false);
			radLigneDonnees.setSelection(true);

			// formEdition.redraw();
			// formEdition.reflow(true);
		} else {
			grpSerieTempo.setParent(shell);
		}

		// formEdition.redraw();
		// formEdition.reflow(true);

		// Initialisation du tableau de paramètres si étape onglet Campagne
		// "sautée"
		if (ViewFile.radioCollectCasN.getSelection()
				|| ViewFile.radioCollectCas1.getSelection()) {
			TableDataSurvey.initTypes();
			// TableDataSurvey.majTypeData();
		}

		// initialisation liste des sections de la partie description fichier
		boolean blnModeleOuvert = DataModel.ouverture;
		boolean blnManuel = true;

		// section reference station
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.getReferenceProperty();
			blnManuel = propertyModel.isUserProvided();
		}

		DrawingStation
				.sectionIncrementeLigneDebutFinInject(
						Messages.getString("EditStation.5"), Messages.getString("EditStation.6"), EditStation.sectionStation, textRefStationNumero, textRefStationLigne, textRefStationDebut, textRefStationFin, buttonRefStationIncremente, buttonRefStationPos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$
		textRefStationNumero.setText("1");

		if (!ViewFile.isCollection()) {
			// section reference type de données
			DrawingStation.sectionTypeDonnees(sectionTypeDonnees, Messages
					.getString("EditStation.0"), comboRefStationType);
		}

		// section historique acquisition
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("dcHistory1");
			blnManuel = propertyModel.isUserProvided();
		}

		DrawingStation
				.sectionHistoriqueAcquisition(
						Messages.getString("EditStation.7"), Messages.getString("EditStation.8"), sectionHistorique, textHistoriqueAcqLigne1, textHistoriqueAcqDebut1, textHistoriqueAcqFin1, textHistoriqueAcqLigne2, textHistoriqueAcqDebut2, textHistoriqueAcqFin2, textHistorique1, textHistorique2, buttonHistoriqueAcqSaisie, buttonHistoriqueAcqPos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

		// section commentaire
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("comment");
			blnManuel = propertyModel.isUserProvided();
		}

		DrawingStation
				.sectionCommentaireLigneDebutFinInject(
						Messages.getString("EditStation.9"), Messages.getString("EditStation.10"), sectionCommentaire, textCommentaireLigne, textCommentaireDebut, textCommentaireFin, textCommentaire, buttonCommentaireSaisie, buttonCommentairePos, buttonVar, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

		// section surface sample : identique à historique acquisition
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("surfaceSamples1");
			blnManuel = propertyModel.isUserProvided();
		}

		DrawingStation
				.sectionSurfaceSample(
						Messages.getString("EditStation.11"), Messages.getString("EditStation.12"), sectionSurface, textSurfaceLigne1, textSurfaceDebut1, textSurfaceFin1, textSurfaceLigne2, textSurfaceDebut2, textSurfaceFin2, textSurface1, textSurface2, buttonSurfaceSaisie, buttonSurfacePos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

		// section décallage TU
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("offsetTU");
			blnManuel = propertyModel.isUserProvided();
		}

		DrawingStation.sectionManuelLigneDebutFinInject(Messages
				.getString("EditStation.86"), "", sectionTU, textTULigne,
				textTUDebut, textTUFin, textTU, buttonTUSaisie, buttonTUPos,
				blnManuel);
		textTU.setText("0.0");

		// section heure
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("time");
			blnManuel = propertyModel.isUserProvided();
		} else {
			blnManuel = false;
		}

		DrawingStation
				.sectionHeure(
						Messages.getString("EditStation.13"), Messages.getString("EditStation.14"), sectionHeure, textHeureLigne, textHeureDebut, textHeureFin, textHeureSaisie, labelConversionHeure, textTestConvHeure, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

		// section date
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("date");
			blnManuel = propertyModel.isUserProvided();
		} else {
			blnManuel = false;
		}

		DrawingStation
				.sectionDate(
						Messages.getString("EditStation.15"), Messages.getString("EditStation.16"), sectionDate, textDateLigne, textDateDebut, textDateFin, textDateSaisie, labelConversionDate, textTestConvDate, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

		// flags QC
		// DrawingStation.sectionDateQC(Messages.getString("EditStation.17"),
		// Messages.getString("EditStation.18"), sectionQCDate); //$NON-NLS-1$
		// //$NON-NLS-2$

		// section Latitude
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("latitude");
			blnManuel = propertyModel.isUserProvided();
		} else {
			blnManuel = false;
		}

		DrawingStation
				.sectionManuelLigneDebutFinInjectLatLongComboFormat(
						Messages.getString("EditStation.19"), Messages.getString("EditStation.20"), sectionLatitude, textLatitudeLigne, textLatitudeDebut, textLatitudeFin, textLatitude, buttonLatSaisie, comboLatitude, buttonLatPos, buttonVarLat, blnManuel, true); //$NON-NLS-1$ //$NON-NLS-2$

		// if (ViewFile.checkBoxTempo.getSelection()) {
		if (ViewFile.radSerieTempo.getSelection()
				|| ViewFile.radTrajectoire.getSelection()) {
			// section latitude de fin
			blnManuel = true;
			if (blnModeleOuvert) {
				PropertyModel propertyModel = DataModel.stationModel_modele
						.getTimeSeriesModel().propertiesAt("endLatitude");
				blnManuel = propertyModel.isUserProvided();
			} else {
				blnManuel = false;
			}

			DrawingStation
					.sectionManuelLigneDebutFinInjectLatLongFin(
							Messages.getString("EditStation.21"), "", sectionLatFin, comboLatitudeFin, textLatFinLigne, textLatFinDebut, textLatFinFin, textLatFin, buttonLatFinSaisie, buttonLatFinPos, buttonVarLatFin, blnManuel, true); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// section longitude
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("longitude");
			blnManuel = propertyModel.isUserProvided();
		} else {
			blnManuel = false;
		}

		DrawingStation
				.sectionManuelLigneDebutFinInjectLatLongComboFormat(
						Messages.getString("EditStation.23"), Messages.getString("EditStation.24"), sectionLongitude, textLongitudeLigne, textLongitudeDebut, textLongitudeFin, textLongitude, buttonLongSaisie, comboLongitude, buttonLongPos, buttonVarLong, blnManuel, false); //$NON-NLS-1$ //$NON-NLS-2$

		// if (ViewFile.checkBoxTempo.getSelection()) {
		if (ViewFile.radSerieTempo.getSelection()
				|| ViewFile.radTrajectoire.getSelection()) {
			// section longitude de fin
			blnManuel = true;
			if (blnModeleOuvert) {
				PropertyModel propertyModel = DataModel.stationModel_modele
						.getTimeSeriesModel().propertiesAt("endLongitude");
				blnManuel = propertyModel.isUserProvided();
			} else {
				blnManuel = false;
			}

			DrawingStation
					.sectionManuelLigneDebutFinInjectLatLongFin(
							Messages.getString("EditStation.25"), "", sectionLongFin, comboLongitudeFin, textLongFinLigne, textLongFinDebut, textLongFinFin, textLongFin, buttonLongFinSaisie, buttonLongFinPos, buttonVarLongFin, blnManuel, false); //$NON-NLS-1$ //$NON-NLS-2$
		}

		// section profondeur
		blnManuel = true;
		if (blnModeleOuvert) {
			PropertyModel propertyModel = DataModel.stationModel_modele
					.propertiesAt("bottomDepth");
			blnManuel = propertyModel.isUserProvided();
		}

		DrawingStation
				.sectionManuelLigneDebutFinInject(
						Messages.getString("EditStation.27"), Messages.getString("EditStation.28"), sectionProfondeur, textProfondeurLigne, textProfondeurDebut, textProfondeurFin, textProfondeur, buttonProfondeurSaisie, buttonProfondeurPos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

		// if (ViewFile.checkBoxTempo.getSelection()) {
		if (ViewFile.radSerieTempo.getSelection()
				|| ViewFile.radTrajectoire.getSelection()) {
			// section profondeur de fin
			blnManuel = true;
			if (blnModeleOuvert) {
				PropertyModel propertyModel = DataModel.stationModel_modele
						.getTimeSeriesModel().propertiesAt("endDepth");
				blnManuel = propertyModel.isUserProvided();
			}

			DrawingStation
					.sectionManuelLigneDebutFinInject(
							Messages.getString("EditStation.29"), "", sectionProfFin, textProfFinLigne, textProfFinDebut, textProfFinFin, textProfFin, buttonProfFinSaisie, buttonProfFinPos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

			// section profondeur de capteur
			blnManuel = true;
			if (blnModeleOuvert) {
				PropertyModel propertyModel = DataModel.stationModel_modele
						.getTimeSeriesModel().propertiesAt("sensorDepth");
				blnManuel = propertyModel.isUserProvided();
			}
			DrawingStation
					.sectionManuelLigneDebutFinInject(
							Messages.getString("EditStation.31"), Messages.getString("EditStation.32"), sectionProfCapteur, textProfCapteurLigne, textProfCapteurDebut, textProfCapteurFin, textProfCapteur, buttonProfCapteurSaisie, buttonProfCapteurPos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

			// section déclinaison magnétique
			blnManuel = true;
			if (blnModeleOuvert) {
				PropertyModel propertyModel = DataModel.stationModel_modele
						.getTimeSeriesModel().propertiesAt(
								"magneticDeclination");
				blnManuel = propertyModel.isUserProvided();
			}
			DrawingStation
					.sectionManuelLigneDebutFinInject(
							Messages.getString("EditStation.33"), Messages.getString("EditStation.34"), sectionDeclinMagnet, textDeclinMagnetLigne, textDeclinMagnetDebut, textDeclinMagnetFin, textDeclinMagnet, buttonDeclinMagnetSaisie, buttonDeclinMagnetPos, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$

			// section fréquence échantillonnage
			blnManuel = true;
			if (blnModeleOuvert) {
				PropertyModel propertyModel = DataModel.stationModel_modele
						.getTimeSeriesModel().propertiesAt("samplingRate");
				blnManuel = propertyModel.isUserProvided();
			}

			DrawingStation
					.sectionManuelLigneDebutFinInjectFreqEchant(
							Messages.getString("EditStation.35"), Messages.getString("EditStation.36"), sectionFreqEchant, textFreqEchantLigne, textFreqEchantDebut, textFreqEchantFin, textFreqEchant, buttonFreqEchantSaisie, buttonFreqEchantPos, textFreqEchantConv, textFreqEchantTest, blnManuel); //$NON-NLS-1$ //$NON-NLS-2$
			textFreqEchantConv.setText("*1");
		}

		DrawingStation.sectionFlagQC(Messages.getString("EditStation.17"),
				Messages.getString("EditStation.18"), sectionFlagQC,
				tabCorrespondance, tabFlagQC);

		buttonExpand2.setText("+"); //$NON-NLS-1$

		ViewEdit.formEdition.reflow(true);

		// listeners
		buttonExpand
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("expand/collapse all");
						// raz de la valeur manuelle
						int i;

						// System.out.println("expand ? = " + expand);
						for (i = 0; i <= EditStation.listeSectionsStation
								.size() - 1; i++) {

							if (expand == 0) {
								EditStation.listeSectionsStation.get(i)
										.setExpanded(true);

							} else {
								EditStation.listeSectionsStation.get(i)
										.setExpanded(false);

							}

						}
						if (expand == 0) {
							// System.out.println("expand passe à 1");
							buttonExpand.setText("-"); //$NON-NLS-1$
							buttonExpand2.setText("-"); //$NON-NLS-1$
							expand = 1;
						} else {
							expand = 0;
							// System.out.println("expand passe à 0");
							buttonExpand.setText("+"); //$NON-NLS-1$
							buttonExpand2.setText("+"); //$NON-NLS-1$
						}

						// refresh global
						formEdition.reflow(true);

					}
				});

		buttonExpand2
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// raz de la valeur manuelle
						int i;

						// System.out.println("expand ? = " + expand);
						for (i = 0; i <= EditStation.listeSectionsStation
								.size() - 1; i++) {

							if (expand == 0) {
								EditStation.listeSectionsStation.get(i)
										.setExpanded(true);

							} else {
								EditStation.listeSectionsStation.get(i)
										.setExpanded(false);

							}

						}
						if (expand == 0) {
							// System.out.println("expand passe à 1");
							// //$NON-NLS-1$
							buttonExpand.setText("-"); //$NON-NLS-1$
							buttonExpand2.setText("-"); //$NON-NLS-1$
							expand = 1;
						} else {
							expand = 0;
							// System.out.println("expand passe à 0");
							// //$NON-NLS-1$
							buttonExpand.setText("+"); //$NON-NLS-1$
							buttonExpand2.setText("+"); //$NON-NLS-1$
						}

						// refresh global
						formEdition.reflow(true);

					}
				});

		buttonValidate
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// on passe en ppar l'étape à laquelle on se trouve
						// Etape.etape(2);

						Etape.validationStation();

						formEdition.redraw();
						formEdition.reflow(true);

					}
				});

		buttonRAZ
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						razText(listeTextStation);

					}
				});

		// listeners sur les boutons radio
		radMedatlas
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						activeGrpSerieTempo();
					}
				});

		radODV
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						activeGrpSerieTempo();
					}
				});

		radFreqEchant
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// test();
						// formEdition.reflow(true);
						activeDateHeureManuel();

					}
				});

		radLigneDonnees
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// test2();
						// formEdition.reflow(true);
						activeDateHeureManuel();
					}
				});

		// raz du message d'erreurs
		Utils.messageErreur(""); //$NON-NLS-1$

		// refresh du formulaire

		formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 2;

		// remplissage de l'IHM par le modèle chargé

		if (DataModel.ouverture) {
			DataModel.fillStation();
		} else {
			EditStation.flagTable = Utils.configuration.getDefaultFlagTable();
			EditStation.flagTable.fillTable(tabCorrespondance);
			ViewEdit.formEdition.reflow(true);
		}
	}

	public static void razEdition() {
		// reset de toute la zone d'édition après étape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		int i;

		// effacement des boutons valider/expand/init serveur
		buttonExpand.setParent(shell);
		buttonExpand2.setParent(shell);
		buttonValidate.setParent(shell);
		buttonRAZ.setParent(shell);
		btnMetaInData2.setParent(shell);
		grpTrajectoire.setParent(shell);
		grpSerieTempo.setParent(shell);

		// System.out.println("expand ? = " + expand);
		for (i = 0; i <= EditStation.listeSectionsStation.size() - 1; i++) {

			EditStation.listeSectionsStation.get(i).setExpanded(true);

			EditStation.listeSectionsStation.get(i).setParent(shell);

		}

		// titre à vide
		ViewEdit.formEdition.setText(""); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

	}

	public static void DrawingBack() {
		// reset de toute la zone d'édition après étape descriptioon de fichier
		// on retire toutes les sections et les boutons expand et validation

		ViewLog.aideStation();
		int i;

		ScrolledForm s;
		s = ViewEdit.formEdition;
		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$

		buttonValidate.setParent(s.getBody());
		buttonRAZ.setParent(s.getBody());
		buttonExpand.setParent(s.getBody());

		if (ViewFile.radProfilVertical.getSelection()) {
			btnMetaInData2.setParent(s.getBody());
		}

		if (ViewFile.radTrajectoire.getSelection()) {
			grpTrajectoire.setParent(s.getBody());
		}

		if (ViewFile.radSerieTempo.getSelection()
				|| ViewFile.radTrajectoire.getSelection()) {
			grpSerieTempo.setParent(s.getBody());
		}

		// System.out.println("expand ? = " + expand);
		for (i = 0; i <= EditStation.listeSectionsStation.size() - 1; i++) {

			EditStation.listeSectionsStation.get(i).setExpanded(false);

			EditStation.listeSectionsStation.get(i).setParent(s.getBody());

		}

		if (DataModel.station_modele != null) {
			if (DataModel.station_modele.getDataType() != null) {
				Utils.selectComboCode(EditStation.comboRefStationType,
						DataModel.station_modele.getDataType().getCode());
			}
		} else if (DataModel.station != null) {
			if (DataModel.station.getDataType() != null) {
				Utils.selectComboCode(EditStation.comboRefStationType,
						DataModel.station.getDataType().getCode());
			}
		}

		// EditStation.buttonExpand2.setParent(s.getBody());

		ViewEdit.formEdition.setText(Messages.getString("EditStation.51")); //$NON-NLS-1$

		// refresh global
		ViewEdit.formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 2;
	}

	public static void initSections() {
		sectionStation = new Section(shell, SWT.NONE);
		sectionTypeDonnees = new Section(shell, SWT.NONE);
		sectionHistorique = new Section(shell, SWT.NONE);
		sectionCommentaire = new Section(shell, SWT.NONE);
		sectionSurface = new Section(shell, SWT.NONE);
		sectionHeure = new Section(shell, SWT.NONE);
		sectionDate = new Section(shell, SWT.NONE);
		sectionTU = new Section(shell, SWT.NONE);
		sectionLatitude = new Section(shell, SWT.NONE);
		sectionLongitude = new Section(shell, SWT.NONE);
		sectionProfondeur = new Section(shell, SWT.NONE);
		sectionLatFin = new Section(shell, SWT.NONE);
		sectionLongFin = new Section(shell, SWT.NONE);
		sectionProfFin = new Section(shell, SWT.NONE);
		sectionProfCapteur = new Section(shell, SWT.NONE);
		sectionDeclinMagnet = new Section(shell, SWT.NONE);
		sectionFreqEchant = new Section(shell, SWT.NONE);
		sectionFlagQC = new Section(shell, SWT.NONE);
	}

	public static void initText() {
		// textQCDate = new Text(shell, SWT.BORDER);
		// listeTextStation.add(textQCDate);
		// textQCLat = new Text(shell, SWT.BORDER);
		// listeTextStation.add(textQCLat);
		// textQCLong = new Text(shell, SWT.BORDER);
		// listeTextStation.add(textQCLong);
		// textQCProf = new Text(shell, SWT.BORDER);
		// listeTextStation.add(textQCProf);

		textRefStationNumero = new Text(shell, SWT.BORDER);
		listeTextStation.add(textRefStationNumero);
		textRefStationLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textRefStationLigne);
		textRefStationDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textRefStationDebut);
		textRefStationFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textRefStationFin);

		textHistoriqueAcqLigne1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcqLigne1);
		textHistoriqueAcqLigne2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcqLigne2);
		textHistoriqueAcqDebut1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcqDebut1);
		textHistoriqueAcqDebut2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcqDebut2);
		textHistoriqueAcqFin1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcqFin1);
		textHistoriqueAcqFin2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcqFin2);
		textHistorique1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistorique1);
		textHistorique2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistorique2);
		textHistoriqueAcquisition1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcquisition1);
		textHistoriqueAcquisition2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHistoriqueAcquisition2);

		textCommentaire = new Text(shell, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.WRAP);
		listeTextStation.add(textCommentaire);
		textCommentaireLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textCommentaireLigne);
		textCommentaireDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textCommentaireDebut);
		textCommentaireFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textCommentaireFin);

		textConversionDate = new Text(shell, SWT.BORDER);
		listeTextStation.add(textConversionDate);
		// textConversionHeure = new Text(shell, SWT.BORDER);
		// listeTextStation.add(textConversionHeure);
		textTestConvHeure = new Text(shell, SWT.BORDER);
		listeTextStation.add(textTestConvHeure);
		textTestConvDate = new Text(shell, SWT.BORDER);
		listeTextStation.add(textTestConvDate);

		textHeureLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHeureLigne);
		textHeureDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHeureDebut);
		textHeureFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHeureFin);
		textHeureSaisie = new Text(shell, SWT.BORDER);
		listeTextStation.add(textHeureSaisie);

		textDateLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDateLigne);
		textDateDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDateDebut);
		textDateFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDateFin);
		textDateSaisie = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDateSaisie);

		textLatitude = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatitude);
		textLatitudeLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatitudeLigne);
		textLatitudeDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatitudeDebut);
		textLatitudeFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatitudeFin);
		textLatFinLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatFinLigne);
		textLatFinDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatFinDebut);
		textLatFinFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatFinFin);
		textLatFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLatFin);

		textLongitude = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongitude);
		textLongitudeLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongitudeLigne);
		textLongitudeDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongitudeDebut);
		textLongitudeFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongitudeFin);
		textLongFinLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongFinLigne);
		textLongFinDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongFinDebut);
		textLongFinFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongFinFin);
		textLongFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textLongFin);

		textProfondeur = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfondeur);
		textProfondeurLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfondeurLigne);
		textProfondeurDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfondeurDebut);
		textProfondeurFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfondeurFin);
		textProfFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfFin);
		textProfFinLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfFinLigne);
		textProfFinDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfFinDebut);
		textProfFinFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfFinFin);
		textProfCapteur = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfCapteur);
		textProfCapteurLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfCapteurLigne);
		textProfCapteurDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfCapteurDebut);
		textProfCapteurFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textProfCapteurFin);

		textDeclinMagnet = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDeclinMagnet);
		textDeclinMagnetLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDeclinMagnetLigne);
		textDeclinMagnetDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDeclinMagnetDebut);
		textDeclinMagnetFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textDeclinMagnetFin);

		textFreqEchant = new Text(shell, SWT.BORDER);
		listeTextStation.add(textFreqEchant);
		textFreqEchantLigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textFreqEchantLigne);
		textFreqEchantDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textFreqEchantDebut);
		textFreqEchantFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textFreqEchantFin);
		textFreqEchantConv = new Text(shell, SWT.BORDER);
		listeTextStation.add(textFreqEchantConv);
		textFreqEchantTest = new Text(shell, SWT.BORDER);
		listeTextStation.add(textFreqEchantTest);

		textSurface1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurface1);
		textSurface2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurface2);
		textSurfaceDebut1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurfaceDebut1);
		textSurfaceDebut2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurfaceDebut2);
		textSurfaceFin1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurfaceFin1);
		textSurfaceFin2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurfaceFin2);
		textSurfaceLigne1 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurfaceLigne1);
		textSurfaceLigne2 = new Text(shell, SWT.BORDER);
		listeTextStation.add(textSurfaceLigne2);

		textTU = new Text(shell, SWT.BORDER);
		listeTextStation.add(textTU);
		textTULigne = new Text(shell, SWT.BORDER);
		listeTextStation.add(textTULigne);
		textTUDebut = new Text(shell, SWT.BORDER);
		listeTextStation.add(textTUDebut);
		textTUFin = new Text(shell, SWT.BORDER);
		listeTextStation.add(textTUFin);

		// remplissage de la liste des textes et ajout listeners
		ajoutModifyListenerText(listeTextStation);
	}

	public static void ajoutModifyListenerText(ArrayList<Text> liste) {
		int taille = liste.size();
		for (int i = 0; i < taille; i++) {
			liste.get(i).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					// on modifie la valeur du champ texte
					// dès qu'on modifie une valeur, on invalide l'étape
					// remise du nom onglet à valeur par défaut

					// on invalide toutes les étapes suivantes avec le second
					// flag okApresReset à false
					EditStation.okApresReset = false;
					ViewFile.tabItem2.setText(Messages
							.getString("EditStation.1"));
					EditData.okApresReset = false;
					ViewFile.tabItem3.setText(Messages.getString("EditData.1"));
					Utils.messageErreur("");

				}
			});

		}
	}

	public static void initBoutons() {

		buttonRAZ = new Button(shell, SWT.NONE);
		buttonExpand = new Button(shell, SWT.NONE);
		buttonExpand2 = new Button(shell, SWT.NONE);
		buttonValidate = new Button(shell, SWT.NONE);
		btnMetaInData2 = new Button(shell, SWT.CHECK);

		grpSerieTempo = new Group(shell, SWT.NONE);
		radLigneDonnees = new Button(shell, SWT.RADIO);
		radFreqEchant = new Button(shell, SWT.RADIO);

		grpTrajectoire = new Group(shell, SWT.NONE);
		radMedatlas = new Button(shell, SWT.RADIO);
		radODV = new Button(shell, SWT.RADIO);

		buttonHistoriqueAcqSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonHistoriqueAcqSaisie);
		buttonHistoriqueAcqPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonHistoriqueAcqPos);

		buttonRefStationIncremente = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonRefStationIncremente);
		buttonRefStationPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonRefStationPos);

		buttonSurfaceSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonSurfaceSaisie);
		buttonSurfacePos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonSurfacePos);

		buttonTUSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonTUSaisie);
		buttonTUPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonTUPos);

		buttonLatSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonLatSaisie);
		buttonLatPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonLatPos);
		buttonLatFinPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonLatFinPos);
		buttonLatFinSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonLatFinSaisie);

		buttonLongSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonLongSaisie);
		buttonLongPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonLongPos);
		buttonLongFinPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonLongFinPos);
		buttonLongFinSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonLongFinSaisie);

		buttonCommentaireSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonCommentaireSaisie);
		buttonCommentairePos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonCommentairePos);

		buttonVar = new Button(shell, SWT.CHECK);
		buttonVarLat = new Button(shell, SWT.CHECK);
		buttonVarLatFin = new Button(shell, SWT.CHECK);
		buttonVarLong = new Button(shell, SWT.CHECK);
		buttonVarLongFin = new Button(shell, SWT.CHECK);

		buttonProfondeurSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonProfondeurSaisie);
		buttonProfondeurPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonProfondeurPos);
		buttonProfFinPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonProfFinPos);
		buttonProfFinSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonProfFinSaisie);

		buttonProfCapteurPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonProfCapteurPos);
		buttonProfCapteurSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonProfCapteurSaisie);

		buttonDeclinMagnetPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonDeclinMagnetPos);
		buttonDeclinMagnetSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonDeclinMagnetSaisie);

		buttonFreqEchantPos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonFreqEchantPos);
		buttonFreqEchantSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonFreqEchantSaisie);

		buttonDateReference = new Button(shell, SWT.RADIO);
		buttonHeureReference = new Button(shell, SWT.RADIO);

		buttonHeureSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonHeureSaisie);

		buttonDateSaisie = new Button(shell, SWT.RADIO);
		listeBoutonsManuel.add(buttonDateSaisie);

		buttonHeurePos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonHeurePos);

		buttonDatePos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonDatePos);

		buttonHeureInject = new Button(shell, SWT.NONE);
		listeBoutonsManuel.add(buttonHeureInject);

		buttonDateInject = new Button(shell, SWT.NONE);
		listeBoutonsManuel.add(buttonDateInject);

		buttonHeureReferencePos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonHeureReferencePos);
		buttonDateReferencePos = new Button(shell, SWT.RADIO);
		listeBoutonsPos.add(buttonDateReferencePos);
	}

	public static void initListeType() {

		listeType = new List(shell, SWT.BORDER);

	}

	public static void initTables() {
		flagTable = new FlagTable();

		tabCorrespondance = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER);
		tabFlagQC = new Table(shell, SWT.FULL_SELECTION | SWT.BORDER);
	}

	public static void initLabels() {

		labelConversionHeure = new Label(shell, SWT.NONE);
		labelConversionDate = new Label(shell, SWT.NONE);

	}

	public static void initTypes() {

		comboRefStationType.removeAll();

		// remplissage avec les dataTypes de la campagne

		int taille = TableDataSurvey.types.size();
		for (int i = 0; i < taille; i++) {
			// on n'ajoute que les sensors
			if (TableDataSurvey.types.get(i).getSensor()) {
				comboRefStationType.add(TableDataSurvey.types.get(i).getCode());
			}
		}
	}

	public static void initCombos() {

		comboRefStationType = new Combo(shell, SWT.READ_ONLY);
		// remplissage avec les dataTypes de la campagne

		// FAE022
		if (!ViewFile.isCollection()) {

			int taille = TableDataSurvey.types.size();
			for (int i = 0; i < taille; i++) {
				// on n'ajoute que les sensors
				if (TableDataSurvey.types.get(i).getSensor()) {
					comboRefStationType.add(TableDataSurvey.types.get(i)
							.getCode());
				}
			}
		}
		comboHeure = new Combo(shell, SWT.NONE);
		comboHeure.add("hh:mm PM"); //$NON-NLS-1$
		comboHeure.add("hh:mm:ss PM"); //$NON-NLS-1$
		comboHeure.add("hh24:mm"); //$NON-NLS-1$
		comboHeure.add("hh24:mm:ss"); //$NON-NLS-1$
		comboHeure.add("hh24mm"); //$NON-NLS-1$
		comboHeure.add("hh24mmss"); //$NON-NLS-1$
		comboHeure.add("hh24 mm"); //$NON-NLS-1$
		comboHeure.add("hh24:cc"); //$NON-NLS-1$
		comboHeure.add("ttttt"); //$NON-NLS-1$

		comboDate = new Combo(shell, SWT.NONE);
		comboDate.add("DD/MM/YY"); //$NON-NLS-1$
		comboDate.add("DD/MM/YYYY"); //$NON-NLS-1$
		comboDate.add("DDMMYY"); //$NON-NLS-1$
		comboDate.add("DDMMYYYY"); //$NON-NLS-1$
		comboDate.add("MMM DD YYYY"); //$NON-NLS-1$
		comboDate.add("YY/MM/DD"); //$NON-NLS-1$
		comboDate.add("YYMMDD"); //$NON-NLS-1$
		comboDate.add("YYYY/MM/DD"); //$NON-NLS-1$
		comboDate.add("YYYYMMDD"); //$NON-NLS-1$

		comboLatitude = new Combo(shell, SWT.NONE);
		comboLatitude.add("+DD MM.mm"); //$NON-NLS-1$
		comboLatitude.add("+DD.ddd"); //$NON-NLS-1$
		comboLatitude.add("+DDMMSS"); //$NON-NLS-1$
		comboLatitude.add("NDD MM.mm"); //$NON-NLS-1$
		comboLatitude.add("NDD.ddd"); //$NON-NLS-1$
		comboLatitude.add("NDDMMSS"); //$NON-NLS-1$

		comboLongitude = new Combo(shell, SWT.NONE);
		comboLongitude.add("+DDD MM.mm"); //$NON-NLS-1$
		comboLongitude.add("+DDD.ddd"); //$NON-NLS-1$
		comboLongitude.add("+DDDMMSS"); //$NON-NLS-1$
		comboLongitude.add("NDDD MM.mm"); //$NON-NLS-1$
		comboLongitude.add("NDDD.ddd"); //$NON-NLS-1$
		comboLongitude.add("NDDDMMSS"); //$NON-NLS-1$

		comboLatitudeFin = new Combo(shell, SWT.NONE);
		comboLatitudeFin.add("+DD MM.mm"); //$NON-NLS-1$
		comboLatitudeFin.add("+DD.ddd"); //$NON-NLS-1$
		comboLatitudeFin.add("+DDMMSS"); //$NON-NLS-1$
		comboLatitudeFin.add("NDD MM.mm"); //$NON-NLS-1$
		comboLatitudeFin.add("NDD.ddd"); //$NON-NLS-1$
		comboLatitudeFin.add("NDDMMSS"); //$NON-NLS-1$

		comboLongitudeFin = new Combo(shell, SWT.NONE);
		comboLongitudeFin.add("+DDD MM.mm"); //$NON-NLS-1$
		comboLongitudeFin.add("+DDD.ddd"); //$NON-NLS-1$
		comboLongitudeFin.add("+DDDMMSS"); //$NON-NLS-1$
		comboLongitudeFin.add("NDDD MM.mm"); //$NON-NLS-1$
		comboLongitudeFin.add("NDDD.ddd"); //$NON-NLS-1$
		comboLongitudeFin.add("NDDDMMSS"); //$NON-NLS-1$

	}

	public static void razText(ArrayList<Text> liste) {

		for (int i = 0; i < liste.size(); i++) {
			liste.get(i).setText(""); //$NON-NLS-1$

		}
		textTU.setText("0.0");
		EditStation.okApresReset = false;
		ViewFile.tabItem2.setText(Messages.getString("EditStation.1"));
		EditData.okApresReset = false;
		ViewFile.tabItem3.setText(Messages.getString("EditData.1"));
		Utils.messageErreur("");

		// boutons manuels sélectionnés par défaut
		int nbreManu = listeBoutonsManuel.size();
		int nbrePos = listeBoutonsPos.size();

		for (int i = 0; i < nbrePos; i++) {
			listeBoutonsPos.get(i).setSelection(false);
		}

		for (int i = 0; i < nbreManu; i++) {
			listeBoutonsManuel.get(i).setSelection(true);
		}

	}

	public static void conversionFreqEchant() {

		if (!textFreqEchantConv.getText().matches("")) { //$NON-NLS-1$
			// il y a une formule de conversion
			if ((buttonFreqEchantSaisie.getSelection())
					&& (!textFreqEchant.getText().matches(""))) { //$NON-NLS-1$
				// saisie manuelle

				org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
				parser.addStandardFunctions();
				parser.addStandardConstants();
				parser.addVariable(
						"x", Float.valueOf(textFreqEchant.getText().trim())); //$NON-NLS-1$
				parser.parseExpression("x"
						+ textFreqEchantConv.getText().trim());

				double resultJEP = parser.getValue();

				EditStation.textFreqEchantTest.setText("" + resultJEP); //$NON-NLS-1$

			}
			if ((buttonFreqEchantPos.getSelection())
					&& (Regexp.checkLigneDebutfin(textFreqEchantLigne.getText(),
							textFreqEchantDebut.getText(), textFreqEchantFin.getText(), Messages
									.getString("EditStation.85"), true))) { //$NON-NLS-1$

				// position dans fichier

				// récupérer le texte en question et creer une nouvelle heure
				String ligne = Utils.lignesBrutes
						.get(Integer.parseInt(EditStation.textFreqEchantLigne
								.getText()) - 1);
				String string = ligne.substring(
						Integer.parseInt(EditStation.textFreqEchantDebut
								.getText()) - 1, Integer
								.parseInt(EditStation.textFreqEchantFin
										.getText()));

				org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
				parser.addStandardFunctions();
				parser.addStandardConstants();
				parser.addVariable("x", Float.valueOf(string)); //$NON-NLS-1$
				parser.parseExpression("x" + textFreqEchantConv.getText());
				double resultJEP = parser.getValue();

				EditStation.textFreqEchantTest.setText("" + resultJEP); //$NON-NLS-1$

			}

		}

	}

	private static void disposeElements() {

		shell.close();

		shell = new Shell();

		if (listeBoutonsManuel != null) {
			for (Button element : listeBoutonsManuel) {
				element.dispose();
			}
		}

		if (listeBoutonsPos != null) {
			for (Button element : listeBoutonsPos) {
				element.dispose();
			}
		}

		if (listeSectionsLabel != null) {
			for (Label element : listeSectionsLabel) {
				element.dispose();
			}
		}

		if (listeSectionsParam != null) {
			for (Section element : listeSectionsParam) {
				element.dispose();
			}
		}

		if (listeSectionsStation != null) {
			for (Section element : listeSectionsStation) {
				element.dispose();
			}
		}

		if (listeTextStation != null) {
			for (Text element : listeTextStation) {
				element.dispose();
			}
		}
		
		if (tabCorrespondance != null) {
			tabCorrespondance.dispose();
		}
		
		if (tabFlagQC != null) {
			tabFlagQC.dispose();
		}

		if (comboDate != null)
			comboDate.dispose();

		if (comboHeure != null)
			comboHeure.dispose();

		if (comboLatitude != null)
			comboLatitude.dispose();

		if (comboLatitudeFin != null)
			comboLatitudeFin.dispose();

		if (comboLongitude != null)
			comboLongitude.dispose();

		if (comboLongitudeFin != null)
			comboLongitudeFin.dispose();

		if (comboRefStationType != null)
			comboRefStationType.dispose();
	}

	public static String typeFichierSortie() {
		if (radODV.getSelection()) {
			return Global.ODV;
		} else {
			return Global.MEDATLAS;
		}

	}

	public static void test() {
		int i;

		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setEnabled(false); // Parent(s);

		}

		// refresh global
		ViewEdit.formEdition.reflow(true);
	}

	public static void test2() {
		int i;

		for (i = 0; i <= EditData.listeSectionsData.size() - 1; i++) {

			EditData.listeSectionsData.get(i).setEnabled(true); // Parent(s);

		}

		// refresh global
		ViewEdit.formEdition.reflow(true);
	}

	public static void activeGrpSerieTempo() {
		if (radMedatlas.getSelection()) {
			radFreqEchant.setEnabled(true);

			radLigneDonnees.setEnabled(true);
		} else {
			radFreqEchant.setEnabled(false);
			radFreqEchant.setSelection(false);

			radLigneDonnees.setEnabled(true);
			radLigneDonnees.setSelection(true);
		}

		activeDateHeureManuel();
	}

	public static void activeDateHeureManuel() {
		if (radFreqEchant.getSelection()) {
			buttonHeureSaisie.setEnabled(true);
			buttonHeurePos.setEnabled(true);

			buttonDateSaisie.setEnabled(true);
			buttonDatePos.setEnabled(true);

			// sectionFreqEchant.setVisible(true);
			sectionFreqEchant.setEnabled(true);
			sectionFreqEchant.setTitleBarForeground(Display.getCurrent()
					.getSystemColor(SWT.COLOR_BLACK));
		} else {
			buttonHeureSaisie.setEnabled(false);
			buttonHeureSaisie.setSelection(false);
			buttonHeurePos.setEnabled(true);
			buttonHeurePos.setSelection(true);

			buttonDateSaisie.setEnabled(false);
			buttonDateSaisie.setSelection(false);
			buttonDatePos.setEnabled(true);
			buttonDatePos.setSelection(true);

			// sectionFreqEchant.setVisible(false);
			sectionFreqEchant.setEnabled(false);
			sectionFreqEchant.setExpanded(false);
			sectionFreqEchant.setTitleBarForeground(Display.getCurrent()
					.getSystemColor(SWT.COLOR_GRAY));
		}

		activeHeure();
		activeDate();
	}

	public static void activeHeure() {
		if (buttonHeureSaisie.getSelection()) {
			textHeureSaisie.setEnabled(true);
			textHeureDebut.setEnabled(false);
			textHeureFin.setEnabled(false);
			textHeureLigne.setEnabled(false);
			buttonHeureInject.setEnabled(false);
		} else {
			textHeureSaisie.setEnabled(false);
			textHeureDebut.setEnabled(true);
			textHeureFin.setEnabled(true);
			if (radFreqEchant.getSelection()) {
				textHeureLigne.setEnabled(true);
			} else {
				textHeureLigne.setEnabled(false);
			}
			buttonHeureInject.setEnabled(true);
		}
	}

	public static void activeDate() {
		if (buttonDateSaisie.getSelection()) {
			textDateSaisie.setEnabled(true);
			textDateDebut.setEnabled(false);
			textDateFin.setEnabled(false);
			textDateLigne.setEnabled(false);
			buttonDateInject.setEnabled(false);
		} else {
			textDateSaisie.setEnabled(false);
			textDateDebut.setEnabled(true);
			textDateFin.setEnabled(true);
			if (radFreqEchant.getSelection()) {
				textDateLigne.setEnabled(true);
			} else {
				textDateLigne.setEnabled(false);
			}
			buttonDateInject.setEnabled(true);
		}
	}

	public static FlagTable getFlagTable() {
		if (flagTable != null) {
			return flagTable;
		} else {
			return Utils.getBlankFlagTable();
		}
	}

	public static void setFlagTable(FlagTable table) {
		flagTable = table;
	}
}
