package fr.ifremer.nemo.ui;

import java.text.ParseException;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.domain.TimeSeriesInformation;
import fr.ifremer.nemo.input.DirectoryBasedSurvey;
import fr.ifremer.nemo.input.HeaderFragment;
import fr.ifremer.nemo.input.HorodatedParameterModel;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.SingleFileSurvey;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.LatitudeFormat;
import fr.ifremer.nemo.util.LongitudeFormat;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSCountry;
import fr.ifremer.nemo.ws.WSDataCentre;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSParameter;
import fr.ifremer.nemo.ws.WSRegion;
import fr.ifremer.nemo.ws.WSShip;

public class DataModel {

	public static InputModel inputModel, inputModel_modele;

	public static StationModel stationModel, stationModel_modele;

	public static SingleFileSurvey singleSurvey, singleSurvey_modele;

	public static DirectoryBasedSurvey dirSurvey, dirSurvey_modele;

	public static Station station, station_modele;

	public static WSParameter[] listeWSParameters, listeWSParameters_modele;

	public static WSDataType[] listeWSDataTypes, listeWSDataTypes_modele;

	public static WSDataType wsDatatypeStation, wsDatatypeStation_modele;

	public static WSDataCentre[] listeWSCenters;

	public static WSRegion[] listeWSRegions;

	public static WSShip[] listeWSShips;

	public static WSCountry[] listeWSCountries;

	public static Boolean ouverture, reset;

	public static TimeSeriesInformation timeSeriesInformation,
			timeSeriesInformation_modele;

	public static void initModele(Boolean fichier, Boolean timeSeries,
			String nomFich) {

		// ouverture = false;

		if (!fichier) {
			// fichier campagne
			initFichier(timeSeries, nomFich);

		} else {
			initDossier(timeSeries, nomFich);
			// dossier campagne
		}

	}

	public static void initDossier(Boolean timeSeries, String nomDossier) {

		if (inputModel != null) {
			inputModel.release();
		}

		inputModel = new InputModel();
		inputModel.initFoDirectoryBased(timeSeries);
		dirSurvey = (DirectoryBasedSurvey) inputModel.getMedia();
		dirSurvey.setDirectory(nomDossier);

		initDesc();
	}

	public static void initFichier(Boolean timeSeries, String nomFich) {
		// acceder aux proprietes de survey

		if (inputModel != null) {
			inputModel.release();
		}

		inputModel = new InputModel();
		inputModel.initForSingleFile(timeSeries);
		singleSurvey = (SingleFileSurvey) inputModel.getMedia();
		singleSurvey.setFile(nomFich);

		initDesc();

	}

	public static void initDesc() {

		// taille du header fichier
		if (ViewFile.dossierCampagne) {
			dirSurvey.setDirectory(ViewFile.nomRepertoire);

			stationModel = dirSurvey.getStationModel();
		} else {
			singleSurvey.setHeaderLinesNumber(Integer
					.parseInt(EditDesc.lignesEnteteFichier.getText()));
			stationModel = singleSurvey.getStationModel();
		}

		// serie temporelle
		if (ViewFile.checkBoxTempo.getSelection()) {
			timeSeriesInformation = stationModel.getTimeSeriesInformation();

			if (!ViewFile.textTempoHeureRef.getText().matches("")) {
				inputModel.setRefDateBased(true);

				// conversion en LONG avant injection dans dataModel
				// DATE REF
				DateTimeFormat dateFormat;
				Long dateLong = null, timeLong = null;
				try {
					dateFormat = new DateTimeFormat();
					dateFormat.setFormatString("dd/MM/yyyy");
					dateLong = (Long) dateFormat
							.parseObject(ViewFile.textTempoDateRef.getText());
				} catch (ParseException e) {
					// 
					e.printStackTrace();
				}

				inputModel.setRefDate(dateLong);

				// HEURE REF
				try {
					DateTimeFormat timeFormat = new DateTimeFormat();
					timeFormat.setFormatString("hh24:mm");
					timeLong = (Long) timeFormat
							.parseObject(ViewFile.textTempoHeureRef.getText());
				} catch (ParseException e) {
					// 
					e.printStackTrace();
				}

				inputModel.setRefTime(timeLong);
			} else {
				// pas d'heure de ref
				inputModel.setRefDateBased(false);

			}

		}

		// nombre lignes entete station
		stationModel.setNumberOfLines(Integer
				.parseInt(EditDesc.lignesEnteteStation.getText()));

		// nombre de lignes de fin
		stationModel.setEndData(Integer.parseInt(EditDesc.lignesFinStation
				.getText()));

		// metaInData
		stationModel.setMetaInData(false);

		// attribut pattern fin donnees en accès direct

		// si manuel -> direct sinon passer par propertyModel
		if (EditDesc.radEOF.getSelection()) {
			stationModel.setEndOnEOF(true);
		} else if (EditDesc.radLigne.getSelection()) {
			stationModel.setEndOnEmptyLine(true);
		} else if (EditDesc.radManuel.getSelection()) {
			stationModel.setEndOnPattern(true);
			
			stationModel.setEndDataPattern(EditDesc.txtManuel.getText());
		}
		
//		FAE 33
//		if (EditDesc.buttonIndicFinDonnees.getSelection()) {
//
//			// pattern
//			stationModel.setEndOnPattern(true);
//			// propertyModel.setUserProvided(true);
//
//			if (EditDesc.comboIndicRuptureDonnees.getText().matches("EOF")) {
//				stationModel.setEndDataPattern("^$");
//			} else {
//
//				stationModel
//						.setEndDataPattern(EditDesc.comboIndicRuptureDonnees
//								.getText());
//			}
//
//		} else {
//			// nombre de mesures dans le fichier
//			stationModel.setEndOnPattern(false);
//			stationModel.setRowNumber(EditDesc.nbreLignesMesures.getText());
//
//		}

		// on initialise les dataTypes, centers et parameters en prévision des
		// onglets Campagne et Data

		listeWSDataTypes = NemoLists.getDataTypes();
		listeWSParameters = NemoLists.getParameters();
		listeWSCenters = NemoLists.getDataCentres();
		listeWSShips = NemoLists.getShips();
		listeWSCountries = NemoLists.getCountries();
		listeWSRegions = NemoLists.getRegions();

		// Lancement de la table des paramètres, onglet DATA, si pas deja créée
		if (ViewFile.tableData == null) {
			TableData.go();
		}

		// verrouillage de l'option serieTemporelle pour éviter les changements
		ViewFile.checkBoxTempo.setEnabled(false);

		// flag "validee" à 1
		EditDesc.validee = 1;

	}

	public static void initSurvey() {
		Survey survey;
		if (ViewFile.dossierCampagne) {
			survey = dirSurvey.getModel();
		} else {
			survey = singleSurvey.getModel();
		}

		station = stationModel.getModel();

		// maj des dataTypes
		
		TableDataSurvey.majTypeData();

		// injection des valeurs de l'IHM -> datamodel
		// référence
		survey.setReference(EditSurvey.textCampagne.getText());
		survey.setName(EditSurvey.textNom.getText());
		survey.setShipCode(EditSurvey.textCodeNavire.getText());
		survey.setShipName(EditSurvey.textNomNavire.getText());

		// Localisation

		// DATE DE DEBUT
		try {
			DateTimeFormat dateFormat = new DateTimeFormat();
			dateFormat.setFormatString("dd/MM/yyyy");
			Long dateLong = (Long) dateFormat
					.parseObject(EditSurvey.textDateDebut.getText());
			survey.setStartDate(dateLong);
		} catch (ParseException e) {

			e.printStackTrace();
		}

		// DATE DE FIN
		try {
			DateTimeFormat dateFormat = new DateTimeFormat();
			dateFormat.setFormatString("dd/MM/yyyy");
			Long dateLong = (Long) dateFormat
					.parseObject(EditSurvey.textDateFin.getText());
			survey.setEndDate(dateLong);
		} catch (ParseException e) {

			e.printStackTrace();
		}

		survey.setRegionName(EditSurvey.textRegion.getText());

		// Source de données
//		survey.setCountryCode(EditSurvey.textPays.getText());
		String country = EditSurvey.comboCountries.getText();		
		survey.setCountryCode(country.substring(0, country.indexOf(" - ")));
		
		survey.setLaboratories(EditSurvey.textLabo.getText());
		survey.setChiefScientist(EditSurvey.textChef.getText());
		survey.setProjectName(EditSurvey.textProjet.getText());

		// Centre d'archivage
		// split sur le " - "
		Pattern p = Pattern.compile(" - ");
		String mots[] = p.split(EditSurvey.comboDataCentres.getText());
		String code = mots[0];

		survey.setDataCentre(code);

		// confidentialité
		if (EditSurvey.comboConfid.getText().matches("P - Public")) {
			survey.setAvailability("publicly");
		}
		if (EditSurvey.comboConfid.getText().matches("L - Limited")) {
			survey.setAvailability("limited");
		}
		if (EditSurvey.comboConfid.getText().matches("C - Confidential")) {
			survey.setAvailability("confidential");
		}

		// Commentaire
		survey.setComment(EditSurvey.textCommentaire.getText());

		// remplissage de la liste des dataTypes de la campagne
		// parcours de la liste des dataTypes de l'IHM et création d'un DataType
		// à chaque fois
		int taille = TableDataSurvey.types.size();

		// reset préventif des dataTypes
		survey.releaseDataTypes();

		for (int i = 0; i < taille; i++) {
			TypeSurvey typeSurvey = TableDataSurvey.types.get(i);
			DataType dataType = new DataType();
			dataType.setCode(typeSurvey.getCode());
			dataType.setNumberOfProfiles(typeSurvey.getNombre());

			if (typeSurvey.getQc().matches("Y")) {
				dataType.setQc(true);
			} else {
				dataType.setQc(false);
			}
			// dataType.setQc(typeSurvey.getQc());
			dataType.setSensor(typeSurvey.getSensor());

			// test par rapport aux types deja présents dans le DataModel
			// si l'étape a déjà été validée

			survey.addDataType(dataType);

		}

		if (EditStation.loaded == 1) {
			EditStation.initTypes();
		}

		// flag "validee" à 1
		EditSurvey.validee = 1;

	}

	public static void initStation() {

		// numéro de station
		String ligne, debut, fin;
		HeaderFragment frag;

		station = stationModel.getModel();

		PropertyModel propertyModel = stationModel.getReferenceProperty();

		if (EditStation.buttonRefStationIncremente.getSelection()) {
			stationModel.setIncrementRef(true);
			propertyModel.setUserProvided(true);

		} else {
			stationModel.setIncrementRef(false);
			propertyModel.setUserProvided(false);

			frag = propertyModel.getFragment();
			// frag.setSingleLine(true);

			ligne = ""
					+ (Integer.valueOf(EditStation.textRefStationLigne
							.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textRefStationDebut.getText();
			fin = EditStation.textRefStationFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);
			
		}

		// type de donnée

		// récupérer le bon datatype avant de l'affecter

		// on récupère la sélection du combo de station puis le code + libellé
		String typeCombo = EditStation.comboRefStationType.getText();

		DataType dataType = new DataType();
		String code = "";
		String libelle = "";
		// si on a ouvert un modèle, on a le code direct !
		if (DataModel.ouverture) {
			code = typeCombo;
		} else {
			Pattern p = Pattern.compile(" - ");
			String mots[] = p.split(typeCombo);

			int taille = mots.length;
			if (taille == 1) {
				code = mots[0];
			}
			if (taille > 1) {
				code = mots[0];
				libelle = mots[1];
			}

		}
		// création d'un nouveau Datatype avec les infos récupérées au dessus.

		dataType.setCode(code);

		// FAE 022
		if (!ViewFile.isCollection()) {
			for (int i = 0; i < TableDataSurvey.types.size(); i++) {

				if (typeCombo.contentEquals(TableDataSurvey.types.get(i)
						.getCode())) {
					// c'est le bon dataType et on récupère le nombre de mesures
					// et
					// le QC

					dataType.setNumberOfProfiles(TableDataSurvey.types.get(i)
							.getNombre());
					dataType.setQc(TableDataSurvey.types.get(i).getQc());
					dataType
							.setSensor(TableDataSurvey.types.get(i).getSensor());

				}

			}

			station.setDataType(dataType);
		}
		// historique acquisition1 et 2
		propertyModel = stationModel.propertiesAt("dcHistory1");
		PropertyModel propertyModel2 = stationModel.propertiesAt("dcHistory2");
		if (EditStation.buttonHistoriqueAcqSaisie.getSelection()) {
			// saisie manuelle
			station.setDcHistory1(EditStation.textHistorique1.getText());
			station.setDcHistory2(EditStation.textHistorique2.getText());
			propertyModel.setUserProvided(true);
			propertyModel2.setUserProvided(true);

		} else {
			propertyModel.setUserProvided(false);
			propertyModel2.setUserProvided(false);
			// position dans fichier
			frag = propertyModel.getFragment();
			HeaderFragment frag2 = propertyModel2.getFragment();

			ligne = ""
					+ (Integer.valueOf(EditStation.textHistoriqueAcqLigne1
							.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textHistoriqueAcqDebut1.getText();
			fin = EditStation.textHistoriqueAcqFin1.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

			if (EditStation.textHistoriqueAcqLigne2.getText().matches("")) {
				// on a injecté que la ligne 1
				propertyModel2.setUserProvided(true);
				station.setDcHistory2(EditStation.textHistorique2.getText());
			} else {

				String ligne2, debut2, fin2;
				ligne2 = ""
						+ (Integer.valueOf(EditStation.textHistoriqueAcqLigne2
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut2 = EditStation.textHistoriqueAcqDebut2.getText();
				fin2 = EditStation.textHistoriqueAcqFin2.getText();

				frag2.setFirstLine(ligne2);
				frag2.setLastLine(ligne2);
				frag2.setFirstCharOffset(debut2);
				frag2.setLastCharOffset(fin2);
			}

		}

		// FLAGS QC
		station.setDateQC(EditStation.textQCDate.getText());
		station.setDepthQC(EditStation.textQCProf.getText());
		station.setLatQC(EditStation.textQCLat.getText());
		station.setLongQC(EditStation.textQCLong.getText());

		// commentaire
		propertyModel = stationModel.propertiesAt("comment");
		if (EditStation.buttonCommentaireSaisie.getSelection()) {
			// saisie manuelle du texte
			station.setComment(EditStation.textCommentaire.getText());
			propertyModel.setUserProvided(true);

		} else {
			// surlignement et injection ligne debut fin

			propertyModel.setUserProvided(false);
			frag = propertyModel.getFragment();

			ligne = ""
					+ (Integer.valueOf(EditStation.textCommentaireLigne
							.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textCommentaireDebut.getText();
			fin = EditStation.textCommentaireFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);
			
			frag.setVariable(EditStation.buttonVar.getSelection());
			
			// Utils.calculNbreLignesPuisRemplissageFragClic(frag,
			// EditStation.posxCommentaire, EditStation.posyCommentaire);
			//Utils.calculNbreLignesPuisRemplissageFragClic(frag, EditStation.cursCommentaire);

		}

		// DM history 1&2
		/*
		 * propertyModel = stationModel.propertiesAt("dmHistory1");
		 * propertyModel2 = stationModel.propertiesAt("dmHistory2");
		 * propertyModel.setUserProvided(true);
		 * propertyModel.setUserProvided(true);
		 */
		station.setDmHistory1("");
		station.setDmHistory2("");

		// echantillon surface
		propertyModel = stationModel.propertiesAt("surfaceSamples1");
		propertyModel2 = stationModel.propertiesAt("surfaceSamples2");
		if (EditStation.buttonSurfaceSaisie.getSelection()) {
			// saisie manuelle
			station.setSurfaceSamples1(EditStation.textSurface1.getText());
			station.setSurfaceSamples2(EditStation.textSurface2.getText());
			propertyModel.setUserProvided(true);
			propertyModel2.setUserProvided(true);

		} else {
			propertyModel.setUserProvided(false);
			propertyModel2.setUserProvided(false);
			// position dans fichier
			frag = propertyModel.getFragment();
			HeaderFragment frag2 = propertyModel2.getFragment();

			ligne = ""
					+ (Integer.valueOf(EditStation.textSurfaceLigne1.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textSurfaceDebut1.getText();
			fin = EditStation.textSurfaceFin1.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

			if (EditStation.textSurfaceLigne2.getText().matches("")) {
				// si on a injecté que la ligne 1
				propertyModel2.setUserProvided(true);
				station.setSurfaceSamples2(EditStation.textSurface2.getText());
			} else {

				String ligne2, debut2, fin2;
				ligne2 = ""
						+ (Integer.valueOf(EditStation.textSurfaceLigne2
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut2 = EditStation.textSurfaceDebut2.getText();
				fin2 = EditStation.textSurfaceFin2.getText();

				frag2.setFirstLine(ligne2);
				frag2.setLastLine(ligne2);
				frag2.setFirstCharOffset(debut2);
				frag2.setLastCharOffset(fin2);
			}
		}

		// décallage TU
		propertyModel = stationModel.propertiesAt("offsetTU");

		if (EditStation.buttonTUSaisie.getSelection()) {
			// if (true){
			// mise à true pour passage obligatoire !
			// saisie manuelle
			// station.setLatitude(EditStation.textTU.getText());
			station.setOffsetTU(EditStation.textTU.getText());
			propertyModel.setUserProvided(true);

			// }
		} else {
			propertyModel.setUserProvided(false);
			// position dans fichier

			frag = propertyModel.getFragment();

			ligne = ""
					+ (Integer.valueOf(EditStation.textTULigne.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textTUDebut.getText();
			fin = EditStation.textTUFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

		}

		// frequence echantillonnage
		if (ViewFile.checkBoxTempo.getSelection()) {

			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"samplingRate");

			if (EditStation.buttonFreqEchantSaisie.getSelection()) {
				// saisie manuelle
				stationModel.getTimeSeriesInformation().setSamplingRate(
						EditStation.textFreqEchant.getText());
				propertyModel.setUserProvided(true);

			} else {
				// position dans fichier
				propertyModel.setUserProvided(false);

				frag = propertyModel.getFragment();

				ligne = ""
						+ (Integer.valueOf(EditStation.textFreqEchantLigne
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut = EditStation.textFreqEchantDebut.getText();
				fin = EditStation.textFreqEchantFin.getText();

				frag.setFirstLine(ligne);
				frag.setLastLine(ligne);
				frag.setFirstCharOffset(debut);
				frag.setLastCharOffset(fin);
			}

			// conversion JEP de la fréquence
			if (!EditStation.textFreqEchantConv.getText().matches("")) {
				// formule de conversion
				stationModel.getTimeSeriesModel().setSampleRateConversion(
						EditStation.textFreqEchantConv.getText());
			}
		}

		// HEURE
		propertyModel = stationModel.propertiesAt("time");

		if (EditStation.buttonHeureSaisie.getSelection()) {
			// saisie heure manuelle

			propertyModel.setUserProvided(true);

			try {
				DateTimeFormat timeFormat = (DateTimeFormat) propertyModel
						.getFormat();
				timeFormat.setFormatString(EditStation.comboHeure.getText());
				Long timeLong = (Long) timeFormat
						.parseObject(EditStation.textHeureSaisie.getText());
				stationModel.getModel().setTime(timeLong);
			} catch (ParseException e) {

				e.printStackTrace();
			}

		} else {
			// position heure dans fichier

			propertyModel.setUserProvided(false);

			frag = propertyModel.getFragment();
			// frag.isSingleLine();

			ligne = ""
					+ (Integer.valueOf(EditStation.textHeureLigne.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textHeureDebut.getText();
			fin = EditStation.textHeureFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

			// format heure
			DateTimeFormat timeFormat = (DateTimeFormat) propertyModel
					.getFormat();
			try {
				timeFormat.setFormatString(EditStation.comboHeure.getText());
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}
		// }//fin if serie tempo

		// date en LONG
		propertyModel = stationModel.propertiesAt("date");

		if (EditStation.buttonDateSaisie.getSelection()) {
			// saisie date manuelle
			propertyModel.setUserProvided(true);
			stationModel.getReferenceProperty();

			DateTimeFormat dateFormat = (DateTimeFormat) propertyModel
					.getFormat();
			try {
				dateFormat.setFormatString(EditStation.comboDate.getText());
				Long dateLong = (Long) dateFormat
						.parseObject(EditStation.textDateSaisie.getText());
				stationModel.getModel().setDate(dateLong);

			} catch (ParseException e) {
				// 
				e.printStackTrace();
			}

		} else {

			propertyModel.setUserProvided(false);

			// position dans fichier

			frag = propertyModel.getFragment();
			// frag.isSingleLine();

			ligne = ""
					+ (Integer.valueOf(EditStation.textDateLigne.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textDateDebut.getText();
			fin = EditStation.textDateFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

			// format date
			DateTimeFormat dateFormat = (DateTimeFormat) propertyModel
					.getFormat();
			try {
				dateFormat.setFormatString(EditStation.comboDate.getText());
			} catch (ParseException e) {
				// 
				e.printStackTrace();
			}
		}

		// latitude
		propertyModel = stationModel.propertiesAt("latitude");

		// format de la latitude à mettre dans le datamodel
		LatitudeFormat latitudeFormat = (LatitudeFormat) propertyModel
				.getFormat();
		try {
			latitudeFormat.setFormatString(EditStation.comboLatitude.getText());
		} catch (ParseException e) {
			// 
			e.printStackTrace();
		}

		if (EditStation.buttonLatSaisie.getSelection()) {
			// saisie manuelle

			// conversion en DOUBLE avant injection DataModel
			try {
				double latitudeDouble = ((Number) latitudeFormat
						.parseObject(EditStation.textLatitude.getText()))
						.doubleValue();
				station.setLatitude(latitudeDouble);
				propertyModel.setUserProvided(true);
			} catch (ParseException e) {
				// 
				e.printStackTrace();
			}

		} else {
			propertyModel.setUserProvided(false);
			// position dans fichier

			frag = propertyModel.getFragment();

			ligne = ""
					+ (Integer.valueOf(EditStation.textLatitudeLigne.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textLatitudeDebut.getText();
			fin = EditStation.textLatitudeFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

		}

		// longitude

		propertyModel = stationModel.propertiesAt("longitude");

		// format de la longitude à mettre dans le datamodel
		LongitudeFormat longitudeFormat = (LongitudeFormat) propertyModel
				.getFormat();
		try {
			longitudeFormat.setFormatString(EditStation.comboLongitude
					.getText());
		} catch (ParseException e) {
			// 
			e.printStackTrace();
		}

		if (EditStation.buttonLongSaisie.getSelection()) {
			// saisie manuelle

			// conversion en double
			try {
				double longitudeDouble = ((Number) longitudeFormat
						.parseObject(EditStation.textLongitude.getText()))
						.doubleValue();
				station.setLongitude(longitudeDouble);
				propertyModel.setUserProvided(true);
			} catch (ParseException e) {

				e.printStackTrace();
			}

		} else {
			propertyModel.setUserProvided(false);
			// position dans fichier
			frag = propertyModel.getFragment();

			ligne = ""
					+ (Integer
							.valueOf(EditStation.textLongitudeLigne.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textLongitudeDebut.getText();
			fin = EditStation.textLongitudeFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

		}

		// profondeur
		propertyModel = stationModel.propertiesAt("bottomDepth");
		if (EditStation.buttonProfondeurSaisie.getSelection()) {
			// saisie manuelle
			station.setBottomDepth(EditStation.textProfondeur.getText());
			propertyModel.setUserProvided(true);

		} else {
			propertyModel.setUserProvided(false);
			// position dans fichier
			frag = propertyModel.getFragment();

			ligne = ""
					+ (Integer.valueOf(EditStation.textProfondeurLigne
							.getText()) - Integer
							.valueOf(EditDesc.lignesEnteteFichier.getText()));
			debut = EditStation.textProfondeurDebut.getText();
			fin = EditStation.textProfondeurFin.getText();

			frag.setFirstLine(ligne);
			frag.setLastLine(ligne);
			frag.setFirstCharOffset(debut);
			frag.setLastCharOffset(fin);

		}

		if (ViewFile.checkBoxTempo.getSelection()) {
			// DECLINAISON MAGNETIQUE
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"magneticDeclination");

			if (EditStation.buttonDeclinMagnetSaisie.getSelection()) {
				propertyModel.setUserProvided(true);
				// valeur manuelle
				timeSeriesInformation
						.setMagneticDeclination(EditStation.textDeclinMagnet
								.getText());
			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				propertyModel.setUserProvided(false);

				ligne = ""
						+ (Integer.valueOf(EditStation.textDeclinMagnetLigne
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut = EditStation.textDeclinMagnetDebut.getText();
				fin = EditStation.textDeclinMagnetFin.getText();

				frag.setFirstLine(ligne);
				frag.setLastLine(ligne);
				frag.setFirstCharOffset(debut);
				frag.setLastCharOffset(fin);
			}

			// PROFONDEUR DE FIN
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"endDepth");

			if (EditStation.buttonProfFinSaisie.getSelection()) {
				propertyModel.setUserProvided(true);
				// valeur manuelle
				if (EditStation.textProfFin.getText().matches("")) {
					// on recopie profondeur dans profondeur de fin
					PropertyModel propModelProfNormale = stationModel
							.propertiesAt("bottomDepth");
					if (propModelProfNormale.isUserProvided()) {
						// on recopie la valeur entrée par l'utilisateur
						propertyModel.setUserProvided(true);
						timeSeriesInformation
								.setEndDepth(EditStation.textProfondeur
										.getText());

					} else {
						// memes valeurs de ligne/debut/fin que profondeur
						HeaderFragment fragProf = propModelProfNormale
								.getFragment();

						propertyModel.setUserProvided(false);
						frag = propertyModel.getFragment();
						frag.setFirstLine(fragProf.getFirstLine());
						frag.setLastLine(fragProf.getLastLine());
						frag.setFirstCharOffset(fragProf.getFirstCharOffset());
						frag.setLastCharOffset(fragProf.getLastCharOffset());
					}
				} else {

					timeSeriesInformation.setEndDepth(EditStation.textProfFin
							.getText());
				}
			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				propertyModel.setUserProvided(false);

				ligne = ""
						+ (Integer.valueOf(EditStation.textProfFinLigne
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut = EditStation.textProfFinDebut.getText();
				fin = EditStation.textProfFinFin.getText();

				frag.setFirstLine(ligne);
				frag.setLastLine(ligne);
				frag.setFirstCharOffset(debut);
				frag.setLastCharOffset(fin);
			}

			// LATITUDE DE FIN
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"endLatitude");

			// format de la latitude de fin à mettre dans le datamodel
			LatitudeFormat latitudeFormatFin = (LatitudeFormat) propertyModel
					.getFormat();
			try {
				latitudeFormatFin.setFormatString(EditStation.comboLatitudeFin
						.getText());
			} catch (ParseException e) {
				// 
				e.printStackTrace();
			}

			if (EditStation.buttonLatFinSaisie.getSelection()) {
				propertyModel.setUserProvided(true);
				// valeur manuelle

				if (!EditStation.textLatFin.getText().matches("")) {
					try {
						double latitudeFinDouble = ((Number) latitudeFormatFin
								.parseObject(EditStation.textLatFin.getText()))
								.doubleValue();
						// System.out.println("longitude de fin ="
						// + EditStation.textLatFin.getText());
						timeSeriesInformation.setEndLatitude(latitudeFinDouble);
					} catch (ParseException e) {

						e.printStackTrace();
					}

				} else {
					// vide donc on recopie latitude dans latitude de fin
					PropertyModel propLat = stationModel
							.propertiesAt("latitude");

					if (propLat.isUserProvided()) {
						// on récupère le double de la latitude
						propertyModel.setUserProvided(true);
						stationModel.getTimeSeriesInformation().setEndLatitude(
								station.getLatitude());
					} else {
						// on remplit le frag
						HeaderFragment fragLat = propLat.getFragment();
						frag = propertyModel.getFragment();
						propertyModel.setUserProvided(false);
						frag.setFirstCharOffset(fragLat.getFirstCharOffset());
						frag.setLastCharOffset(fragLat.getLastCharOffset());
						frag.setFirstLine(fragLat.getFirstLine());
						frag.setLastLine(fragLat.getLastLine());

					}

				}

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				propertyModel.setUserProvided(false);

				ligne = ""
						+ (Integer.valueOf(EditStation.textLatFinLigne
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut = EditStation.textLatFinDebut.getText();
				fin = EditStation.textLatFinFin.getText();

				frag.setFirstLine(ligne);
				frag.setLastLine(ligne);
				frag.setFirstCharOffset(debut);
				frag.setLastCharOffset(fin);
			}

			// LONGITUDE DE FIN
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"endLongitude");

			// format de la longitude à mettre dans le datamodel
			LongitudeFormat longitudeFormatFin = (LongitudeFormat) propertyModel
					.getFormat();
			try {
				longitudeFormatFin.setFormatString(EditStation.comboLongitudeFin
						.getText());
			} catch (ParseException e) {
				// 
				e.printStackTrace();
			}

			if (EditStation.buttonLongFinSaisie.getSelection()) {
				propertyModel.setUserProvided(true);
				// valeur manuelle
				// conversion en double
				if (!EditStation.textLongFin.getText().matches("")) {
					try {
						double longitudeFinDouble = ((Number) longitudeFormatFin
								.parseObject(EditStation.textLongFin.getText()))
								.doubleValue();
						// System.out.println("longitude de fin ="
						// + EditStation.textLongFin.getText());
						timeSeriesInformation
								.setEndLongitude(longitudeFinDouble);
					} catch (ParseException e) {

						e.printStackTrace();
					}

				} else {
					// vide donc on recopie la longitude comme longitude de fin
					PropertyModel propLong = stationModel
							.propertiesAt("longitude");

					if (propLong.isUserProvided()) {
						// on recopie le double de la longitude
						propertyModel.setUserProvided(true);

						stationModel.getTimeSeriesInformation()
								.setEndLongitude(station.getLongitude());

					} else {
						// on recopie le frag correspondant
						propertyModel.setUserProvided(false);

						HeaderFragment fragLong = propLong.getFragment();
						frag = propertyModel.getFragment();

						frag.setFirstCharOffset(fragLong.getFirstCharOffset());
						frag.setLastCharOffset(fragLong.getLastCharOffset());
						frag.setFirstLine(fragLong.getFirstLine());
						frag.setLastLine(fragLong.getLastLine());

					}

				}

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				propertyModel.setUserProvided(false);

				ligne = ""
						+ (Integer.valueOf(EditStation.textLongFinLigne
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut = EditStation.textLongFinDebut.getText();
				fin = EditStation.textLongFinFin.getText();

				frag.setFirstLine(ligne);
				frag.setLastLine(ligne);
				frag.setFirstCharOffset(debut);
				frag.setLastCharOffset(fin);
			}

			// DATE / HEURE DE FIN
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"endDate");
			propertyModel.setUserProvided(true);
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"endTime");
			propertyModel.setUserProvided(true);

			// PROFONDEUR CAPTEUR
			propertyModel = stationModel.getTimeSeriesModel().propertiesAt(
					"sensorDepth");

			if (EditStation.buttonProfCapteurSaisie.getSelection()) {
				propertyModel.setUserProvided(true);
				// valeur manuelle
				timeSeriesInformation
						.setSensorDepth(EditStation.textProfCapteur.getText());
			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				propertyModel.setUserProvided(false);

				ligne = ""
						+ (Integer.valueOf(EditStation.textProfCapteurLigne
								.getText()) - Integer
								.valueOf(EditDesc.lignesEnteteFichier.getText()));
				debut = EditStation.textProfCapteurDebut.getText();
				fin = EditStation.textProfCapteurFin.getText();

				frag.setFirstLine(ligne);
				frag.setLastLine(ligne);
				frag.setFirstCharOffset(debut);
				frag.setLastCharOffset(fin);
			}

		}

		// flag validation à 1
		EditStation.validee = 1;

	}

	public static void initData() {

		// mise à jour des types avant validation
		TableData.majTypeData();

		// remplissage des paramètres mesurés
		int nbreParam = TableData.types.size();

		// reset systématique des parametres stockés dans le dataModel
		stationModel.releaseParameterModels();

		for (int i = 0; i < nbreParam; i++) {
			TypeData typeCourant = TableData.types.get(i);

			// code / fullName / sortie / format
			Parameter para = new Parameter();
			ParameterModel parameterModel = new ParameterModel();
			parameterModel.setModel(para);

			parameterModel.setModel(para);

			// split sur " - "
			Pattern p = Pattern.compile(" - ");
			String mots[] = p.split(typeCourant.getCode());
			String code = mots[0];
			String nom = mots[1];

			// code, nom, unité
			para.setCode(code);
			para.setFullName(nom);
			para.setUnit(typeCourant.getUnite());

			para.setDefault(typeCourant.getSortie());
			para.setFormat(typeCourant.getFormat());

			// conversion / entree
			parameterModel.setConversionFormula(typeCourant.getConversion());
			parameterModel.setInputDefault(typeCourant.getEntree());

			// fragment du paramètre
			LineFragment frag = new LineFragment();
			frag.setFirstCharOffset(typeCourant.getDebut());
			frag.setLastCharOffset(typeCourant.getFin());
			parameterModel.setFragment(frag);

			// test par / au DataModel puis ajout du paramètre ou non
			// ParameterModel[] parametersModels =
			// stationModel.getParameterModels();
			// int nbre = parametersModels.length;
			// Boolean found = false;

			stationModel.addParameterModel(parameterModel);

			/*
			 * System.out.println(">>Ajout du paramètre " +
			 * typeCourant.getCode() + " ... OK");
			 */

		}

		if (ViewFile.checkBoxTempo.getSelection()) {
			// gestion des séries temporelles
			if (EditData.buttonFreqEchant.getSelection()) {
				// calcul à partir fréquence échantillonnage
				stationModel.getTimestampModel().setComputed(true);

			} else {
				// calcul position date/heure dans les données
				stationModel.getTimestampModel().setComputed(false);
				HorodatedParameterModel horoDatedParameterModel = stationModel
						.getTimestampModel();
				try {
					horoDatedParameterModel.setFormat(new DateTimeFormat(
							EditData.comboTotal.getText()));
				} catch (ParseException e) {

					e.printStackTrace();
				}
				LineFragment lineFragment = horoDatedParameterModel
						.getDateTimeFragment();
				lineFragment.setFirstCharOffset(EditData.textTotalDebut
						.getText());
				lineFragment.setLastCharOffset(EditData.textTotalFin.getText());

			}
		}

		// flag validation à 1
		EditData.validee = 1;

	}

	public static void ouvertureModele(InputModel input) {
		// fonction d'ouverture d'un modele de campagne

		if (inputModel_modele != null) {
			inputModel_modele.release();
		}

		DataModel.ouverture = true;
		inputModel_modele = new InputModel();
		inputModel_modele = input;

		// forcage affichage onglet 0
		ViewFile.tabFolder.setSelection(0);

		// RAZ global
		Utils.razGlobal();

		// reset de l'étape de conversion
		ViewFile.etape = 0;

		// lancement du processus sur le premier onglet et effacement préventif
		// si besoin
		fillDesc();
	}

	public static void fillDesc() throws RuntimeException {
		// remplissage auto de l'IHM Desc
		if (dirSurvey_modele != null) {
			dirSurvey_modele.releaseInputModel();
		}
		if (singleSurvey_modele != null) {
			singleSurvey_modele.releaseInputModel();
		}

		if (ViewFile.dossierCampagne) {

			try {
				dirSurvey_modele = new DirectoryBasedSurvey();
				dirSurvey_modele = (DirectoryBasedSurvey) inputModel_modele
						.getMedia();
			} catch (RuntimeException e) {
				Utils.messageErreurAppend(Messages.getString("Etape.124"));
				e.printStackTrace();
				throw e;
			}
			stationModel_modele = dirSurvey_modele.getStationModel();
			// lignes entete fichier = 0
			EditDesc.lignesEnteteFichier.setText("0");

		} else {
			try {
				singleSurvey_modele = new SingleFileSurvey();
				singleSurvey_modele = (SingleFileSurvey) inputModel_modele
						.getMedia();
			} catch (RuntimeException e) {
				Utils.messageErreurAppend(Messages.getString("Etape.124"));
				e.printStackTrace();
				throw e;
			}
			stationModel_modele = singleSurvey_modele.getStationModel();
			// lignes entete fichier
			EditDesc.lignesEnteteFichier.setText(""
					+ singleSurvey_modele.getHeaderLinesNumber());
			
		}

		// test si série tempo
		if (stationModel_modele.getTimeSeriesInformation() != null) {
			// c'est une série tempo donc on coche la case qui va bien
			ViewFile.checkBoxTempo.setSelection(true);
			// ViewFile.groupTempo.setVisible(true);

			// heure et date de ref

			if (inputModel_modele.isRefDateBased()) {
				// date de ref / heure de ref

				// reconversion de LONG vers format normal
				Formatter formatter = new Formatter();

				try {
					ViewFile.textTempoDateRef.setText(formatter.formatDateTime(
							"dd/MM/yyyy", inputModel_modele.getRefDate()));
				} catch (ParseException e) {

					e.printStackTrace();
				}
				// le format de la comboList correspondant est en 0
				ViewFile.comboTempoDate.select(0);

				// reconversion de LONG vers format normal
				formatter = new Formatter();

				try {
					ViewFile.textTempoHeureRef.setText(formatter
							.formatDateTime("hh24:mm", inputModel_modele
									.getRefTime()));
				} catch (ParseException e) {

					e.printStackTrace();
				}
				// le format de la comboList correspondant est en 0
				ViewFile.comboTempoHeure.select(0);

			} else {
				// pas de date de ref

			}

		}

		// nombre lignes entete station

		EditDesc.lignesEnteteStation.setText(""
				+ stationModel_modele.getNumberOfLines());

		// nombre de lignes de fin
		EditDesc.lignesFinStation
				.setText("" + stationModel_modele.getEndData());

		// metaInData
		// stationModel.setMetaInData(false);
		
		// pattern de fin fourni ou pas

		if (stationModel_modele.isEndOnEOF()) {
			EditDesc.selectComboEnd(EditDesc.radEOF);
		} else if (stationModel_modele.isEndOnEmptyLine()){
			EditDesc.selectComboEnd(EditDesc.radLigne);
		} else if (stationModel_modele.isEndOnPattern()) {
			EditDesc.selectComboEnd(EditDesc.radManuel);
			
			EditDesc.txtManuel.setText(stationModel_modele.getEndDataPattern());
		}
		
//		FAE 33
//		// pattern de fin fourni ou pas
//		if (stationModel_modele.getEndDataPattern().matches("")) {
//			// pas de pattern de fin donc nombre de mesures dans le fichier
//			EditDesc.nbreLignesMesures.setText(""
//					+ stationModel_modele.getRowNumber());
//
//		} else {
//			// pattern de fin
//			EditDesc.buttonIndicFinDonnees.setSelection(true);
//			EditDesc.buttonIndicFinDonneesPosition.setSelection(false);
//			// on rajoute met le pattern en début de liste et on le sélectionne
//			EditDesc.comboIndicRuptureDonnees.setItem(0, stationModel_modele
//					.getEndDataPattern());
//			EditDesc.comboIndicRuptureDonnees.select(0);
//		}

		// System.out.println("Fin remplissage onglet Desc");

		// refresh de la zone d'édition
		EditDesc.razEdition();
		EditDesc.DrawingBack();

		DataModel.ouverture = true;

		// désactivation option série tempo
		ViewFile.checkBoxTempo.setEnabled(false);

	}

	public static void fillSurvey() {

		// System.out.println("Remplissage onglet survey");

		Survey survey;
		if (ViewFile.dossierCampagne) {
			survey = dirSurvey_modele.getModel();
		} else {
			survey = singleSurvey_modele.getModel();
		}

		// injection des valeurs de l'IHM -> datamodel
		// référence
		EditSurvey.textCampagne.setText(survey.getReference());
		EditSurvey.textNom.setText(survey.getName());
		EditSurvey.textCodeNavire.setText(survey.getShipCode());
		EditSurvey.textNomNavire.setText(survey.getShipName());

		// Localisation

		// DATES
		Formatter formatter = new Formatter();

		try {
			EditSurvey.textDateDebut.setText(formatter.formatDateTime(
					"dd/MM/yyyy", survey.getStartDate()));
		} catch (ParseException e) {

			e.printStackTrace();
		}

		formatter = new Formatter();

		try {
			EditSurvey.textDateFin.setText(formatter.formatDateTime(
					"dd/MM/yyyy", survey.getEndDate()));
		} catch (ParseException e) {

			e.printStackTrace();
		}

		EditSurvey.textRegion.setText(survey.getRegionName());

		// Source de données
// 		EditSurvey.textPays.setText(survey.getCountryCode());
		int taille = DataModel.listeWSCountries.length;
		int k = 0;
		EditSurvey.comboCountries.select(k);
		String countryCode = survey.getCountryCode();
		int j = countryCode.length();
		while (k < taille
				&& !EditSurvey.comboCountries.getText().substring(0, j)
						.equals(countryCode)) {
			k++;
			EditSurvey.comboCountries.select(k);
		}
		
		EditSurvey.textLabo.setText(survey.getLaboratories());
		EditSurvey.textChef.setText(survey.getChiefScientist());
		EditSurvey.textProjet.setText(survey.getProjectName());

		// Centre d'archivage

		//	
		// survey.setDataCentre(code);
		taille = DataModel.listeWSCenters.length;
		int position = 0;
		for (int i = 0; i < taille; i++) {
			Pattern pat = Pattern.compile(" - ");
			String mots[] = pat.split(EditSurvey.comboDataCentres.getItem(i));
			if (survey.getDataCentre().matches(mots[0])) {
				position = i;
				break;
			}
		}
		EditSurvey.comboDataCentres.select(position);

		// EditSurvey.textCentre.setText(survey.getDataCentre());

		// confidentialité
		String code = survey.getAvailabilityCode();
		if (code.matches("P")) {
			EditSurvey.comboConfid.select(0);
		}
		if (code.matches("L")) {
			EditSurvey.comboConfid.select(1);
		}
		if (code.matches("C")) {
			EditSurvey.comboConfid.select(2);
		}

		// Commentaire
		EditSurvey.textCommentaire.setText(survey.getComment());

		// Remplissage de la liste des dataTypes de la campagne

		DataType[] dataTypes = survey.getDataTypes();
		taille = dataTypes.length;

		for (int i = 0; i < taille; i++) {
			TypeSurvey typeSurvey = new TypeSurvey();
			typeSurvey.setCode(dataTypes[i].getCode());
			typeSurvey.setNombre(dataTypes[i].getNumberOfProfiles());

			// QC
			if (dataTypes[i].isQc()) {
				typeSurvey.setQc("Y");
			} else {
				typeSurvey.setQc("N");
			}
			// typeSurvey.setQc(""+dataTypes[i].isQc());

			typeSurvey.setSensor(dataTypes[i].isSensor());

			TableDataSurvey.types.add(typeSurvey);

			// System.out.println("TypeSurvey " +typeSurvey.getCode()+"
			// ajouté");

		}

		EditSurvey.razEdition();
		EditSurvey.DrawingBack();

		// ////////////////////////////////////////
		// refresh de la liste des dataTypes
		TableDataSurvey.razTableData();
		TableDataSurvey.creationTable();
		Composite c = EditSurvey.sectionType;
		while (c != null) {
			c.setRedraw(false);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				break;
			}
		}
		c = EditSurvey.sectionType;
		while (c != null) {
			c.layout(true);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				((ScrolledForm) c).reflow(true);
				break;
			}
		}
		c = EditSurvey.sectionType;
		while (c != null) {
			c.setRedraw(true);
			c = c.getParent();
			if (c instanceof ScrolledForm) {
				break;
			}
		}

		// ////////////////////////////////////////

		// System.out.println("Fin remplissage onglet Survey");

	}

	public static void fillStation() {

		int ligne, debut, fin, real_ligne;
		boolean var;

		station_modele = stationModel_modele.getModel();

		// numéro de station
		if (stationModel_modele.isIncrementRef()) {
			EditStation.buttonRefStationIncremente.setSelection(true);
			EditStation.buttonRefStationPos.setSelection(false);
		} else {

			// EditStation.buttonRefStationIncremente.setSelection(false); //pas
			// obligatoire pour la première ligne?
			EditStation.buttonRefStationIncremente.setSelection(false);
			EditStation.buttonRefStationPos.setSelection(true);

			// valeurs de ligne/debut/fin
			PropertyModel propertyModel = stationModel_modele
					.getReferenceProperty();
			HeaderFragment frag = propertyModel.getFragment();

			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textRefStationLigne.setText("" + real_ligne);
			EditStation.textRefStationDebut.setText("" + debut);
			EditStation.textRefStationFin.setText("" + fin);

			// activation de la zone des valeurs

		}

		// type de donnée
		// FAE022
		if (!ViewFile.isCollection()) {
			DataType dataType = station_modele.getDataType();
			String code = dataType.getCode();
			// faire un split sur - au cas où et n'avoir QUE le code

			Pattern p = Pattern.compile(" - ");
			String mots[] = p.split(code);
			String vraiCode = mots[0];

			// avant de charger le type de données, vérifier qu'il existe bien
			// dans
			// la liste mise à jour!!
			int taille = TableDataSurvey.types.size();
			Boolean ok = false, capteur = false;
			for (int i = 0; i < taille; i++) {
				// check si le type existe toujours ou pas
				Pattern pat = Pattern.compile(" - ");
				String motsType[] = pat.split(TableDataSurvey.types.get(i)
						.getCode());
				String vraiCodeType = motsType[0];

				if (vraiCode.matches(vraiCodeType)) {
					ok = true;
					if (TableDataSurvey.types.get(i).getSensor()) {
						capteur = true;
					}
				}
			}

			// ajout du parametre ou non du coup
			if (ok) {
				// ajouté que si c'est un capteur
				if (capteur) {
					EditStation.comboRefStationType.add(code, 0);
					EditStation.comboRefStationType.select(0);
				}
			}
		}
		// historique acquisition1 et 2
		PropertyModel propertyModel = stationModel_modele
				.propertiesAt("dcHistory1");
		PropertyModel propertyModel2 = stationModel_modele
				.propertiesAt("dcHistory2");

		if (propertyModel.isUserProvided()) {
			// historique 1&2 tapés manuellement
			EditStation.buttonHistoriqueAcqSaisie.setSelection(true);
			EditStation.textHistorique1.setText(station_modele.getDcHistory1());
			EditStation.textHistorique2.setText(station_modele.getDcHistory2());

		} else {
			EditStation.buttonHistoriqueAcqPos.setSelection(true);
			EditStation.buttonHistoriqueAcqSaisie.setSelection(false);
			EditStation.textHistorique1.setEnabled(false);
			EditStation.textHistorique2.setEnabled(false);

			HeaderFragment frag = propertyModel.getFragment();
			HeaderFragment frag2 = propertyModel2.getFragment();

			Utils.calculPosxPosy(frag, EditStation.cursHistorique1);

			int ligne1 = frag.getFirstLine();
			int debut1 = frag.getFirstCharOffset();
			int fin1 = frag.getLastCharOffset();
			// int real_ligne1 = ligne1 +
			// Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textHistoriqueAcqLigne1.setText("" + ligne1);
			EditStation.textHistoriqueAcqDebut1.setText("" + debut1);
			EditStation.textHistoriqueAcqFin1.setText("" + fin1);

			if (propertyModel2.isUserProvided()) {
				// on a rentré que la ligne 1 donc les valeurs de ligne 2 sont
				// vides
				EditStation.textHistoriqueAcqLigne2.setText("");
				EditStation.textHistoriqueAcqDebut2.setText("");
				EditStation.textHistoriqueAcqFin2.setText("");
			} else {

				Utils.calculPosxPosy(frag2, EditStation.cursHistorique2);
				// on a aussi rentré la ligne 2
				int ligne2 = frag2.getFirstLine();
				int debut2 = frag2.getFirstCharOffset();
				int fin2 = frag2.getLastCharOffset();
				// int real_ligne2 = ligne2 +
				// Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

				EditStation.textHistoriqueAcqLigne2.setText("" + ligne2);
				EditStation.textHistoriqueAcqDebut2.setText("" + debut2);
				EditStation.textHistoriqueAcqFin2.setText("" + fin2);
			}
		}

		// commentaire
		propertyModel = stationModel_modele.propertiesAt("comment");

		if (propertyModel.isUserProvided()) {
			// entrée manuelle du commentaire
			EditStation.buttonCommentaireSaisie.setSelection(true);
			EditStation.textCommentaire.setText(station_modele.getComment());

		} else {
			// calcul du surlignement souris
			HeaderFragment frag = propertyModel.getFragment();

			Utils.calculPosxPosy(frag, EditStation.cursCommentaire);

			EditStation.buttonCommentaireSaisie.setSelection(false);
			EditStation.buttonCommentairePos.setSelection(true);

			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			var = frag.isVariable();
			
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textCommentaireLigne.setText("" + ligne);
			EditStation.textCommentaireDebut.setText("" + debut);
			EditStation.textCommentaireFin.setText("" + fin);
			EditStation.buttonVar.setSelection(var);

		}

		// FLAGS QC
		// EditStation.textQCDate.setText(""+station.getDateQC());
		// EditStation.textQCLat.setText(""+station.getLatQC());
		// EditStation.textQCLong.setText(""+station.getLongQC());
		// EditStation.textQCProf.setText(""+station.getDepthQC());

		// echantillon surface
		propertyModel = stationModel_modele.propertiesAt("surfaceSamples1");
		propertyModel2 = stationModel_modele.propertiesAt("surfaceSamples2");

		if (propertyModel.isUserProvided()) {
			// entrée manuelle echantillon surface
			EditStation.buttonSurfaceSaisie.setSelection(true);
			EditStation.textSurface1.setText(station_modele
					.getSurfaceSamples1());
			EditStation.textSurface2.setText(station_modele
					.getSurfaceSamples2());

		} else {
			// position dans fichier
			EditStation.buttonSurfaceSaisie.setSelection(false);
			EditStation.buttonSurfacePos.setSelection(true);

			HeaderFragment frag = propertyModel.getFragment();
			// Utils.calculPosxPosy(frag, EditStation.posxEchantillon1,
			// EditStation.posyEchantillon1);
			Utils.calculPosxPosy(frag, EditStation.cursEchant1);

			HeaderFragment frag2 = propertyModel2.getFragment();

			int ligne1 = frag.getFirstLine();
			int debut1 = frag.getFirstCharOffset();
			int fin1 = frag.getLastCharOffset();
			// int real_ligne1 = ligne1 +
			// Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textSurfaceLigne1.setText("" + ligne1);
			EditStation.textSurfaceDebut1.setText("" + debut1);
			EditStation.textSurfaceFin1.setText("" + fin1);

			if (propertyModel2.isUserProvided()) {
				// on a rentré que la ligne 1
				EditStation.textSurfaceLigne2.setText("");
				EditStation.textSurfaceDebut2.setText("");
				EditStation.textSurfaceFin2.setText("");
			} else {
				// on a rentré aussi ligne 2

				Utils.calculPosxPosy(frag2, EditStation.cursEchant2);

				int ligne2 = frag2.getFirstLine();
				int debut2 = frag2.getFirstCharOffset();
				int fin2 = frag2.getLastCharOffset();
				// int real_ligne2 = ligne2 +
				// Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

				EditStation.textSurfaceLigne2.setText("" + ligne2);
				EditStation.textSurfaceDebut2.setText("" + debut2);
				EditStation.textSurfaceFin2.setText("" + fin2);
			}

		}

		HeaderFragment frag;

		// frequence echantillonnage
		if (ViewFile.checkBoxTempo.getSelection()) {
			propertyModel = stationModel_modele.getTimeSeriesModel()
					.propertiesAt("samplingRate");

			EditStation.textFreqEchantConv.setText(stationModel_modele
					.getTimeSeriesModel().getSampleRateConversion());

			if (propertyModel.isUserProvided()) {
				// saisie manuelle
				EditStation.buttonFreqEchantSaisie.setSelection(true);
				EditStation.buttonFreqEchantPos.setSelection(false);

				EditStation.textFreqEchant.setText(""
						+ stationModel_modele.getTimeSeriesInformation()
								.getSamplingRate());

			} else {

				// position dans fichier
				EditStation.buttonFreqEchantSaisie.setSelection(false);
				EditStation.buttonFreqEchantPos.setSelection(true);

				frag = propertyModel.getFragment();

				ligne = frag.getFirstLine();
				debut = frag.getFirstCharOffset();
				fin = frag.getLastCharOffset();
				real_ligne = ligne
						+ Integer.valueOf(EditDesc.lignesEnteteFichier
								.getText());

				EditStation.textFreqEchantLigne.setText("" + ligne);
				EditStation.textFreqEchantDebut.setText("" + debut);
				EditStation.textFreqEchantFin.setText("" + fin);

			}

		}

		// HEURE
		propertyModel = stationModel_modele.propertiesAt("time");

		DateTimeFormat timeFormat = (DateTimeFormat) propertyModel.getFormat();
		Utils.selectComboValeur(EditStation.comboHeure, timeFormat.getFormatString());
//		EditStation.comboHeure.add(timeFormat.getFormatString(), 0);
//		EditStation.comboHeure.select(0);
		
		
		if (propertyModel.isUserProvided()) {
			// saisie manuelle

			// reconversion de LONG vers format normal
			Formatter formatter = new Formatter();
			try {
				EditStation.textHeureSaisie
						.setText(formatter.formatDateTime(timeFormat
								.getFormatString(), station_modele.getTime()));
			} catch (ParseException e) {

				e.printStackTrace();
			}

			// desactivation du champ manuel
			EditStation.buttonHeureSaisie.setSelection(true);
			EditStation.buttonHeurePos.setSelection(false);
			EditStation.textHeureLigne.setEnabled(false);
			EditStation.textHeureDebut.setEnabled(false);
			EditStation.textHeureFin.setEnabled(false);

		} else {
			// position heure dans fichier
			EditStation.buttonHeureSaisie.setSelection(false);
			EditStation.buttonHeurePos.setSelection(true);
			EditStation.textHeureLigne.setEnabled(true);
			EditStation.textHeureDebut.setEnabled(true);
			EditStation.textHeureFin.setEnabled(true);

			frag = propertyModel.getFragment();
			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textHeureLigne.setText("" + ligne);
			EditStation.textHeureDebut.setText("" + debut);
			EditStation.textHeureFin.setText("" + fin);
		}

		// date en LONG
		propertyModel = stationModel_modele.propertiesAt("date");

		DateTimeFormat dateFormat = (DateTimeFormat) propertyModel.getFormat();

		Utils.selectComboValeur(EditStation.comboDate, dateFormat.getFormatString());
//		EditStation.comboDate.add(dateFormat.getFormatString(), 0);
//		EditStation.comboDate.select(0);

		if (propertyModel.isUserProvided()) {
			// saisie manuelle

			Formatter formatter = new Formatter();
			try {
				EditStation.textDateSaisie
						.setText(formatter.formatDateTime(dateFormat
								.getFormatString(), station_modele.getDate()));
			} catch (ParseException e) {

				e.printStackTrace();
			}

			// desactivation du champ manuel
			EditStation.buttonDateSaisie.setSelection(true);
			EditStation.buttonDatePos.setSelection(false);
			EditStation.textDateLigne.setEnabled(false);
			EditStation.textDateDebut.setEnabled(false);
			EditStation.textDateFin.setEnabled(false);

		} else {
			// position date dans fichier
			frag = propertyModel.getFragment();
			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.buttonDateSaisie.setSelection(false);
			EditStation.buttonDatePos.setSelection(true);
			EditStation.textDateLigne.setEnabled(true);
			EditStation.textDateDebut.setEnabled(true);
			EditStation.textDateFin.setEnabled(true);

			EditStation.textDateLigne.setText("" + ligne);
			EditStation.textDateDebut.setText("" + debut);
			EditStation.textDateFin.setText("" + fin);
		}

		// latitude

		propertyModel = stationModel_modele.propertiesAt("latitude");
		LatitudeFormat latitudeFormat = (LatitudeFormat) propertyModel
				.getFormat();
		String formatLat = latitudeFormat.getFormatString();

		Utils.selectComboValeur(EditStation.comboLatitude, latitudeFormat.getFormatString());
//		EditStation.comboLatitude.add(latitudeFormat.getFormatString(), 0);
//		EditStation.comboLatitude.select(0);

		if (propertyModel.isUserProvided()) {
			// saisie manuelle latitude

			// reconversion double vers format normal
			Formatter formatter = new Formatter();

			try {
				EditStation.textLatitude.setText(formatter.formatLatitude(
						latitudeFormat.getFormatString(), station_modele
								.getLatitude()));
			} catch (ParseException e) {

				e.printStackTrace();
			}

			EditStation.buttonLatSaisie.setSelection(true);

		} else {
			// position latitude dans fichier
			EditStation.buttonLatSaisie.setSelection(false);
			EditStation.buttonLatPos.setSelection(true);
			frag = propertyModel.getFragment();

			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			var = frag.isVariable();
			
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textLatitudeLigne.setText("" + ligne);
			EditStation.textLatitudeDebut.setText("" + debut);
			EditStation.textLatitudeFin.setText("" + fin);
			EditStation.buttonVarLat.setSelection(var);

		}

		// longitude
		propertyModel = stationModel_modele.propertiesAt("longitude");
		LongitudeFormat longitudeFormat = (LongitudeFormat) propertyModel
				.getFormat();
		String formatLong = longitudeFormat.getFormatString();

		Utils.selectComboValeur(EditStation.comboLongitude, longitudeFormat.getFormatString());
//		EditStation.comboLongitude.add(longitudeFormat.getFormatString(), 0);
//		EditStation.comboLongitude.select(0);

		if (propertyModel.isUserProvided()) {
			// saisie manuelle latitude
			EditStation.buttonLongSaisie.setSelection(true);

			// reconversion double vers format normal
			Formatter formatter = new Formatter();

			try {
				EditStation.textLongitude.setText(formatter.formatLongitude(
						longitudeFormat.getFormatString(), station_modele
								.getLongitude()));
			} catch (ParseException e) {

				e.printStackTrace();
			}

		} else {
			// position latitude dans fichier
			EditStation.buttonLongSaisie.setSelection(false);
			EditStation.buttonLongPos.setSelection(true);
			frag = propertyModel.getFragment();

			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			var = frag.isVariable();
			
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textLongitudeLigne.setText("" + ligne);
			EditStation.textLongitudeDebut.setText("" + debut);
			EditStation.textLongitudeFin.setText("" + fin);
			EditStation.buttonVarLong.setSelection(var);
		}

		// profondeur
		propertyModel = stationModel_modele.propertiesAt("bottomDepth");

		if (propertyModel.isUserProvided()) {
			// profondeur manuelle
			EditStation.buttonProfondeurSaisie.setSelection(true);
			EditStation.textProfondeur.setText(""
					+ station_modele.getBottomDepth());

		} else {
			EditStation.buttonProfondeurSaisie.setSelection(false);
			EditStation.buttonProfondeurPos.setSelection(true);
			frag = propertyModel.getFragment();

			ligne = frag.getFirstLine();
			debut = frag.getFirstCharOffset();
			fin = frag.getLastCharOffset();
			real_ligne = ligne
					+ Integer.valueOf(EditDesc.lignesEnteteFichier.getText());

			EditStation.textProfondeurLigne.setText("" + ligne);
			EditStation.textProfondeurDebut.setText("" + debut);
			EditStation.textProfondeurFin.setText("" + fin);

		}

		// refresh de la zone d'édition
		EditStation.razEdition();
		EditStation.DrawingBack();

		// PROFONDEUR CAPTEUR
		if (ViewFile.checkBoxTempo.getSelection()) {

			propertyModel = stationModel_modele.getTimeSeriesModel()
					.propertiesAt("sensorDepth");
			if (propertyModel.isUserProvided()) {
				// saisie manuelle
				EditStation.textProfCapteur.setText(""
						+ stationModel_modele.getTimeSeriesInformation()
								.getSensorDepth());
				EditStation.buttonProfCapteurSaisie.setSelection(true);
				EditStation.buttonProfCapteurPos.setSelection(false);

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				ligne = frag.getFirstLine();
				debut = frag.getFirstCharOffset();
				fin = frag.getLastCharOffset();
				real_ligne = ligne
						+ Integer.valueOf(EditDesc.lignesEnteteFichier
								.getText());

				EditStation.buttonProfCapteurSaisie.setSelection(false);
				EditStation.buttonProfCapteurPos.setSelection(true);

				EditStation.textProfCapteurLigne.setText("" + ligne);
				EditStation.textProfCapteurDebut.setText("" + debut);
				EditStation.textProfCapteurFin.setText("" + fin);
			}

		}

		// LATITUDE DE FIN
		if (ViewFile.checkBoxTempo.getSelection()) {
			propertyModel = stationModel_modele.getTimeSeriesModel()
					.propertiesAt("endLatitude");
			if (propertyModel.isUserProvided()) {
				// saisie manuelle

				// reconversion double vers format normal
				latitudeFormat = (LatitudeFormat) propertyModel.getFormat();
				formatLat = latitudeFormat.getFormatString();
				Utils.selectComboValeur(EditStation.comboLatitudeFin, formatLat);
				
				Formatter formatter = new Formatter();

				try {
					EditStation.textLatFin.setText(formatter.formatLatitude(
							formatLat, stationModel_modele
									.getTimeSeriesInformation()
									.getEndLatitude()));
				} catch (ParseException e) {

					e.printStackTrace();
				}

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				ligne = frag.getFirstLine();
				debut = frag.getFirstCharOffset();
				fin = frag.getLastCharOffset();
				real_ligne = ligne
						+ Integer.valueOf(EditDesc.lignesEnteteFichier
								.getText());

				EditStation.textLatFinLigne.setText("" + ligne);
				EditStation.textLatFinDebut.setText("" + debut);
				EditStation.textLatFinFin.setText("" + fin);
			}

		}

		// LONGITUDE DE FIN
		if (ViewFile.checkBoxTempo.getSelection()) {

			propertyModel = stationModel_modele.getTimeSeriesModel()
					.propertiesAt("endLongitude");
			if (propertyModel.isUserProvided()) {
				// saisie manuelle

				// reconversion double vers format normal
				longitudeFormat = (LongitudeFormat) propertyModel.getFormat();
				formatLong = longitudeFormat.getFormatString();
				Utils.selectComboValeur(EditStation.comboLongitudeFin, formatLong);
				
				Formatter formatter = new Formatter();

				try {
					EditStation.textLongFin.setText(formatter.formatLongitude(
							formatLong, stationModel_modele
									.getTimeSeriesInformation()
									.getEndLongitude()));
				} catch (ParseException e) {

					e.printStackTrace();
				}

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				ligne = frag.getFirstLine();
				debut = frag.getFirstCharOffset();
				fin = frag.getLastCharOffset();
				real_ligne = ligne
						+ Integer.valueOf(EditDesc.lignesEnteteFichier
								.getText());

				EditStation.textLongFinLigne.setText("" + ligne);
				EditStation.textLongFinDebut.setText("" + debut);
				EditStation.textLongFinFin.setText("" + fin);
			}

		}

		// PROFONDEUR DE FIN
		if (ViewFile.checkBoxTempo.getSelection()) {

			propertyModel = stationModel_modele.getTimeSeriesModel()
					.propertiesAt("endDepth");
			if (propertyModel.isUserProvided()) {
				// saisie manuelle
				EditStation.textProfFin.setText(""
						+ stationModel_modele.getTimeSeriesInformation()
								.getEndDepth());

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				ligne = frag.getFirstLine();
				debut = frag.getFirstCharOffset();
				fin = frag.getLastCharOffset();
				real_ligne = ligne
						+ Integer.valueOf(EditDesc.lignesEnteteFichier
								.getText());

				EditStation.textProfFinLigne.setText("" + ligne);
				EditStation.textProfFinDebut.setText("" + debut);
				EditStation.textProfFinFin.setText("" + fin);
			}

		}

		// DECLINAISON MAGNETIQUE
		if (ViewFile.checkBoxTempo.getSelection()) {

			propertyModel = stationModel_modele.getTimeSeriesModel()
					.propertiesAt("magneticDeclination");
			if (propertyModel.isUserProvided()) {
				// saisie manuelle
				EditStation.textDeclinMagnet.setText(""
						+ stationModel_modele.getTimeSeriesInformation()
								.getMagneticDeclination());

			} else {
				// position dans fichier
				frag = propertyModel.getFragment();
				ligne = frag.getFirstLine();
				debut = frag.getFirstCharOffset();
				fin = frag.getLastCharOffset();
				real_ligne = ligne
						+ Integer.valueOf(EditDesc.lignesEnteteFichier
								.getText());

				EditStation.textDeclinMagnetLigne.setText("" + ligne);
				EditStation.textDeclinMagnetDebut.setText("" + debut);
				EditStation.textDeclinMagnetFin.setText("" + fin);
			}

		}

		// DECALLAGE TU
		propertyModel = stationModel_modele.propertiesAt("offsetTU");
		if (propertyModel.isUserProvided()) {
			// saisie utilisateur
			EditStation.buttonTUSaisie.setSelection(true);
			EditStation.buttonTUPos.setSelection(false);
			EditStation.textTUDebut.setEnabled(false);
			EditStation.textTULigne.setEnabled(false);
			EditStation.textTUFin.setEnabled(false);
			EditStation.textTU.setText("" + station_modele.getOffsetTU());

		} else {
			// position fichier
			frag = propertyModel.getFragment();

			EditStation.buttonTUSaisie.setSelection(false);
			EditStation.buttonTUPos.setSelection(true);
			EditStation.textTUDebut.setEnabled(true);
			EditStation.textTULigne.setEnabled(true);
			EditStation.textTUFin.setEnabled(true);

			EditStation.textTULigne.setText("" + frag.getFirstLine());
			EditStation.textTUDebut.setText("" + frag.getFirstCharOffset());
			EditStation.textTUFin.setText("" + frag.getLastCharOffset());

		}
		
		ViewFile.txtCompteurCDI.setText(stationModel_modele.getCdi().getCompteur());
		ViewFile.txtTypePlateForme.setText(stationModel_modele.getCdi().getTypePlateForme());
		ViewFile.txtAccesDonnees.setText(stationModel_modele.getCdi().getAccesDonnees());

		// System.out.println("Fin remplissage onglet Station");

	}

	public static void main(String[] args) {
		try {

			DateTimeFormat format = new DateTimeFormat("MMM dd yyyy");
			format.toString();
			Long dateLong = (Long) format.parseObject("JUN 02 2007");

			Formatter formatter = new Formatter();
			// System.out.println(">>>>>>"
			// + formatter.formatDateTime("MM/dd/yyyy", dateLong));

			// System.out.println(new Date( dateLong.longValue()));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(">>>>" + e.getMessage());
		}
	}

	public static void fillData() {
		// /////////////////////////////////

		ParameterModel[] parameterModels = stationModel_modele
				.getParameterModels();

		// remplissage des paramètres mesurés
		int nbreParam = parameterModels.length;

		for (int i = 0; i < nbreParam; i++) {
			TypeData typeData = new TypeData();
			ParameterModel paraCourant = parameterModels[i];
			Parameter modeleCourant = paraCourant.getModel();
			LineFragment frag = paraCourant.getFragment();

			// code / fullName / sortie / format
			typeData.setCode(modeleCourant.getCode() + " - "
					+ modeleCourant.getFullName());
			typeData.setNom(modeleCourant.getFullName());

			if (paraCourant.getConversionFormula().matches("")) {
				typeData.setConversion("x*1");
			} else {
				typeData.setConversion(paraCourant.getConversionFormula());
			}
			typeData.setDebut(frag.getFirstCharOffset());
			typeData.setFin(frag.getLastCharOffset());
			typeData.setEntree("" + paraCourant.getInputDefault());
			// typeData.setSensor(paraCourant.get);
			typeData.setUnite(modeleCourant.getUnit());
			typeData.setFormat(modeleCourant.getFormat());
			typeData.setSortie(modeleCourant.getDefault());

			TableData.types.add(typeData);

		}

		// refresh de la table de l'onglet Data
		TableData.razTableData();

		// refresh de la table
		TableData.creationTable();

		if (ViewFile.checkBoxTempo.getSelection()) {
			// série tempo
			if (stationModel_modele.getTimestampModel().isComputed()) {
				// saisie manuelle
				EditData.buttonFreqEchant.setSelection(true);
				EditData.buttonLigneDonnees.setSelection(false);
			} else {
				// position heure/date dans fichier
				EditData.buttonFreqEchant.setSelection(false);
				EditData.buttonLigneDonnees.setSelection(true);

				EditData.comboTotal.add(stationModel_modele.getTimestampModel()
						.getFormat().getFormatString(), 0);
				EditData.comboTotal.select(0);

				EditData.textTotalDebut.setText(""
						+ stationModel_modele.getTimestampModel()
								.getDateTimeFragment().getFirstCharOffset());
				EditData.textTotalFin.setText(""
						+ stationModel_modele.getTimestampModel()
								.getDateTimeFragment().getLastCharOffset());

			}

		}

	}

}
