package fr.ifremer.nemo.ui.edit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.domain.TimeSeriesInformation;
import fr.ifremer.nemo.domain.Trajectory;
import fr.ifremer.nemo.input.DirectoryBasedSurvey;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.SingleFileSurvey;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.data.EditData;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.edit.desc.EditDesc;
import fr.ifremer.nemo.ui.edit.desc.FileEndData;
import fr.ifremer.nemo.ui.edit.station.EditStation;
import fr.ifremer.nemo.ui.edit.survey.EditSurvey;
import fr.ifremer.nemo.ui.edit.survey.TableDataSurvey;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.nemo.ws.WSParameter;

public class DataModel
{
    public static InputModel inputModel, inputModel_modele;

    public static StationModel stationModel, stationModel_modele;

    public static SingleFileSurvey singleSurvey, singleSurvey_modele;

    public static DirectoryBasedSurvey dirSurvey, dirSurvey_modele;

    public static Station station, station_modele;

    public static WSParameter[] listeWSParameters, listeWSParameters_modele;

    public static WSDataType[] listeWSDataTypes, listeWSDataTypes_modele;

    public static WSDataType wsDatatypeStation, wsDatatypeStation_modele;

    public static Boolean ouverture, reset;

    public static TimeSeriesInformation timeSeriesInformation,
            timeSeriesInformation_modele;

    public static Trajectory trajectory;

    public static void initModele(Boolean fichier, String typeFichier,
            String nomFich)
    {

        // ouverture = false;

        if (!fichier)
        {
            // fichier campagne
            initFichier(typeFichier, nomFich);
        }
        else
        {
            initDossier(typeFichier, nomFich);
            // dossier campagne
        }

    }

    public static void initDossier(String typeFichier, String nomDossier)
    {

        if (inputModel != null)
        {
            inputModel.release();
        }

        inputModel = new InputModel();
        inputModel.initFoDirectoryBased(typeFichier);
        dirSurvey = (DirectoryBasedSurvey) inputModel.getMedia();
        dirSurvey.setDirectory(nomDossier);

        initDesc();
    }

    public static void initFichier(String typeFichier, String nomFich)
    {
        // acceder aux proprietes de survey

        if (inputModel != null)
        {
            inputModel.release();
        }

        inputModel = new InputModel();
        inputModel.initForSingleFile(typeFichier);
        singleSurvey = (SingleFileSurvey) inputModel.getMedia();
        singleSurvey.setFile(nomFich);

        initDesc();
    }

    public static void initDesc()
    {
        int fileHeader = 0;
        // taille du header fichier
        if (ViewFile.dossierCampagne)
        {
            dirSurvey.setDirectory(ViewFile.nomRepertoire);
            stationModel = dirSurvey.getStationModel();
        }
        else
        {
            fileHeader =
                Integer.parseInt(EditDesc.sectionEnteteFichier.getValue());
            singleSurvey.setHeaderLinesNumber(fileHeader);
            stationModel = singleSurvey.getStationModel();
        }

        // Type de fichier
        stationModel.setInputType(ViewFile.typeFichierEntree());

        // serie temporelle
        if (ViewFile.radSerieTempo.getSelection()
                || ViewFile.radTrajectoire.getSelection())
        {
            timeSeriesInformation = stationModel.getTimeSeriesInformation();
        }

        // trajectoire
        if (ViewFile.radTrajectoire.getSelection())
        {
            trajectory = stationModel.getTrajectory();
        }

        // séparateur
        if (ViewFile.chkSeparateur.getSelection())
        {
            if (ViewFile.radioTabulation.getSelection())
            {
                // Tabulation
                stationModel.setSeparator(Global.TAB);
            }
            else if (ViewFile.radioPointVirgule.getSelection())
            {
                // Point-virgule
                stationModel.setSeparator(Global.SEMICOLON);
            }
            else if (ViewFile.radioVirgule.getSelection())
            {
                // Virgule
                stationModel.setSeparator(Global.COMMA);
            }
            else if (ViewFile.radioEspace.getSelection())
            {
                // Espace
                stationModel.setSeparator(Global.SPACE);
            }
            else if (ViewFile.radioSeparateurAutre.getSelection())
            {
                // Autre
                stationModel.setSeparator(ViewFile.textSeparateur.getText());
            }
            else
            {
                stationModel.setSeparator(Global.EMPTY_STRING);
            }
        }
        else
        {
            stationModel.setSeparator(Global.EMPTY_STRING);
        }

        // nombre lignes entete station
        if (!EditDesc.sectionEnteteStation.isSelectNumber())
        {
            stationModel.setEndStationHeaderPattern(true);

            String stationPattern =
                EditDesc.sectionEnteteStation.getIndicatorText();
            stationModel.setEndStationHeader(stationPattern);

            int interLines =
                Integer.parseInt(EditDesc.sectionEnteteStation
                    .getNbLinesBeforeData());
            stationModel.setNumberLinesBetweenStationData(interLines);
            int stationHeader = interLines;

            int numLine = 0;
            if (!stationPattern.equals(Global.EMPTY_STRING))
            {
                String[] texte = Utils.texteBrut.split("\n"); //$NON-NLS-1$
                for (numLine = 0; numLine < texte.length; numLine++)
                {
                    if (texte[numLine].trim().startsWith(stationPattern.trim()))
                    {
                        numLine++;
                        break;
                    }
                }
                numLine += interLines;
                stationHeader = numLine - fileHeader;
            }
            stationModel.setNumberOfLines(stationHeader);
        }
        else
        {
            stationModel.setEndStationHeaderPattern(false);
            stationModel.setNumberOfLines(EditDesc.sectionEnteteStation
                .getValue());
        }

        // nombre de lignes de fin
        stationModel.setEndData(Integer.parseInt(EditDesc.sectionFinStation
            .getValue()));

        // attribut pattern fin donnees en acces direct
        if (EditDesc.sectionRuptureDonnees.radEOFselected())
        {
            stationModel.setEndOnEmptyLine(false);
            stationModel.setEndOnEOF(true);
            stationModel.setEndOnNextStation(false);
            stationModel.setEndOnPattern(false);
        }
        else if (EditDesc.sectionRuptureDonnees.radLigneSelected())
        {
            stationModel.setEndOnEmptyLine(true);
            stationModel.setEndOnEOF(false);
            stationModel.setEndOnNextStation(false);
            stationModel.setEndOnPattern(false);
        }
        else if (EditDesc.sectionRuptureDonnees.radNextStationSelected())
        {
            stationModel.setEndOnEmptyLine(false);
            stationModel.setEndOnEOF(false);
            stationModel.setEndOnNextStation(true);
            stationModel.setEndDataPattern(EditDesc.sectionRuptureDonnees
                .getStartStationIndicator());
            stationModel.setEndOnPattern(false);
        }
        else if (EditDesc.sectionRuptureDonnees.radManuelSelected())
        {
            stationModel.setEndOnEmptyLine(false);
            stationModel.setEndOnEOF(false);
            stationModel.setEndOnNextStation(false);
            stationModel.setEndOnPattern(true);
            stationModel.setEndDataPattern(EditDesc.sectionRuptureDonnees
                .getValue());
        }

        // on initialise les dataTypes, centers et parameters en prevision des
        // onglets Campagne et Data

        listeWSDataTypes = NemoLists.getDataTypes();
        listeWSParameters = NemoLists.getParameters();
        TableData.go();

        // verrouillage de l'option serieTemporelle pour eviter les changements
        ViewFile.enableChoice(false);

        // flag "validee" e 1
        EditDesc.validee = true;

    }

    public static void initSurvey()
    {
        // recupération des items
        Couple refCampagne =
            EditSurvey.sectionReference.getItem(Messages.edit_cruise_cruiseRef);
        Couple nomCampagne =
            EditSurvey.sectionReference
                .getItem(Messages.edit_cruise_cruiseName);
        Couple navire =
            EditSurvey.sectionReference.getItem(Messages.edit_cruise_ship);
        Couple dateDebut =
            EditSurvey.sectionLocalisation
                .getItem(Messages.edit_cruise_startDate);
        Couple dateFin =
            EditSurvey.sectionLocalisation
                .getItem(Messages.edit_cruise_endDate);
        Couple region =
            EditSurvey.sectionLocalisation.getItem(Messages.edit_cruise_region);
        Couple pays =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_country);
        Couple labo =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_lab);
        Couple chef =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_chief);
        Couple projet =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_project);
        Couple centreDonnee =
            EditSurvey.sectionCentre.getItem(Messages.edit_cruise_dataCenter);
        Couple confidentialite =
            EditSurvey.sectionCentre.getItem(Messages.edit_cruise_confident);
        Couple commentaires =
            EditSurvey.sectionCommentaire.getItem(Global.EMPTY_STRING);

        Survey survey;
        if (ViewFile.dossierCampagne)
        {
            survey = dirSurvey.getModel();
        }
        else
        {
            survey = singleSurvey.getModel();
        }

        station = stationModel.getModel();

        // maj des dataTypes
        TableDataSurvey.majTypeData();

        // injection des valeurs de l'IHM -> datamodel
        // reference
        if (ViewFile.tabItem1.isDisposed())
        {
            EditSurvey.validee = true;
            return;
        }

        survey.setReference(refCampagne.getText());
        survey.setName(nomCampagne.getText());
        survey.setShipCode(Utils.extraitCode(navire.getText()));
        survey.setShipName(Utils.extraitValeur(navire.getText()));

        // Localisation

        // DATE DE DEBUT
        try
        {
            DateTimeFormat dateFormat = new DateTimeFormat();
            dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
            Long dateLong = (Long) dateFormat.parseObject(dateDebut.getText());
            survey.setStartDate(dateLong);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // DATE DE FIN
        try
        {
            DateTimeFormat dateFormat = new DateTimeFormat();
            dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
            Long dateLong = (Long) dateFormat.parseObject(dateFin.getText());
            survey.setEndDate(dateLong);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        survey.setRegionName(Utils.extraitValeur(region.getText()));

        // Source de données
        survey.setCountryCode(Utils.extraitCode(pays.getText()));
        survey.setLaboratories(labo.getText());
        survey.setChiefScientist(chef.getText());
        survey.setProjectName(projet.getText());

        // Centre d'archivage
        survey.setDataCentre(Utils.extraitCode(centreDonnee.getText()));

        // confidentialité
        if (confidentialite.getText().equals("P - Public")) { //$NON-NLS-1$
            survey.setAvailability("publicly"); //$NON-NLS-1$
        }
        if (confidentialite.getText().equals("L - Limited")) { //$NON-NLS-1$
            survey.setAvailability("limited"); //$NON-NLS-1$
        }
        if (confidentialite.getText().equals("C - Confidential")) { //$NON-NLS-1$
            survey.setAvailability("confidential"); //$NON-NLS-1$
        }

        // Commentaire
        survey.setComment(commentaires.getText());

        // remplissage de la liste des dataTypes de la campagne
        EditSurvey.sectionType.initModel(survey);

        EditSurvey.validee = true;
    }

    private static void initDiseabledField(String property)
    {
        PropertyModel propertyModel =
            stationModel.getTimeSeriesModel().propertiesAt(property);
        propertyModel.setFlag(EditStation.qcFlags.getFlagFragment(property)); // Flag
        // qualite
        propertyModel.setToCompute(true);
    }

    public static void initStation()
    {
        PropertyModel propertyModel;

        // compléter ELAT / ELON et EDEPTH si nécessaire
        if (ViewFile.radSerieTempo.getSelection())
        {
            if (EditStation.endLatitude.getValue().trim().equals(
                Global.EMPTY_STRING))
            {
                // vide donc on recopie latitude dans latitude de fin
                EditStation.endLatitude.copyValues(EditStation.latitude);
            }
            if (EditStation.endLongitude.getValue().trim().equals(
                Global.EMPTY_STRING))
            {
                // vide donc on recopie longitude dans longitude de fin
                EditStation.endLongitude.copyValues(EditStation.longitude);
            }
            if (EditStation.endDepth.getValue().trim().equals(
                Global.EMPTY_STRING))
            {
                // vide donc on recopie profondeur dans profondeur de fin
                EditStation.endDepth.copyValues(EditStation.depth);
            }
        }

        // parcours des sections de la station pour initialisation
        for (NemoSection stationSection : EditStation.listeSectionsStation)
        {
            stationSection.initModel(stationModel);
        }

        // Completer les données manquantes

        // DM HISTORY 1&2
        station.setDmHistory1(Global.EMPTY_STRING);
        station.setDmHistory2(Global.EMPTY_STRING);

        if (ViewFile.radSerieTempo.getSelection()
                || ViewFile.radTrajectoire.getSelection())
        {
            // GESTION Time/Date in Data
            if (EditStation.samplingRate.isEnabled())
            {
                // calcul e partir frequence echantillonnage
                stationModel.getTimestampModel().setComputed(true);
            }
            else
            {
                // calcul position date/heure dans les donnees
                stationModel.getTimestampModel().setComputed(false);
                // remplissage avec les données par défaut
                stationModel.getTimeSeriesModel().setSampleRateConversion("*1"); //$NON-NLS-1$
                stationModel.getTimeSeriesInformation().setSamplingRate(
                    Global.ZERO);
            }

            // DATE / HEURE DE FIN
            propertyModel =
                stationModel.getTimeSeriesModel().propertiesAt(
                    NemoSection.S_EDATE);
            propertyModel.setUserProvided(true);
            propertyModel.setFlag(EditStation.qcFlags
                .getFlagFragment(Global.ENDDATETIME)); // Flag qualite
            propertyModel =
                stationModel.getTimeSeriesModel().propertiesAt(
                    NemoSection.S_ETIME);
            propertyModel.setUserProvided(true);
            propertyModel.setFlag(EditStation.qcFlags
                .getFlagFragment(Global.ENDDATETIME)); // Flag qualite

            if (ViewFile.radTrajectoire.getSelection())
            {
                initDiseabledField(NemoSection.S_ELAT);
                initDiseabledField(NemoSection.S_ELON);
                initDiseabledField(NemoSection.S_EDEPTH);

                // DECLINAISON MAGNETIQUE : FAE 1386 - valeur par défaut pour
                // les Trajectoires
                propertyModel =
                    stationModel.getTimeSeriesModel().propertiesAt(
                        NemoSection.S_MAGNETIC);
                propertyModel.setUserProvided(true);
                timeSeriesInformation.setMagneticDeclination("-999"); //$NON-NLS-1$
            }
        } // fin specifique SERIE TEMPO ou TRAJECTOIRE

        // flag validation e 1
        EditStation.validee = true;
    }

    public static void initData()
    {
        int indexPRES = -1;

        // mise e jour des types avant validation
        TableData.majTypeData();

        // remplissage des parametres mesures
        int nbreParam = TableData.types.size();

        // reset systematique des parametres stockes dans le dataModel
        stationModel.releaseParameterModels();

        for (int i = 0; i < nbreParam; i++)
        {
            TypeData typeCourant = TableData.types.get(i);

            // code / fullName / sortie / format
            Parameter para = new Parameter();
            ParameterModel parameterModel = new ParameterModel();
            parameterModel.setModel(para);

            // parameterModel.setModel(para);

            // split sur " - "
            Pattern p = Pattern.compile(Global.SEP);
            String mots[] = p.split(typeCourant.getCode());
            String code = mots[0];
            String nom = mots[1];

            // code, nom, unite
            para.setCode(code);
            para.setFullName(nom);
            para.setUnit(typeCourant.getUnite());

            para.setDefault(typeCourant.getSortie());
            para.setFormat(typeCourant.getFormat());

            // conversion / entree
            parameterModel.setConversionFormula(typeCourant.getConversion());
            parameterModel.setInputDefault(typeCourant.getEntree());

            // fragment du parametre
            LineFragment frag = new LineFragment();
            frag.setFirstCharOffset(typeCourant.getDebut());
            frag.setLastCharOffset(typeCourant.getFin());
            parameterModel.setFragment(frag);

            if (!typeCourant.getDebutFlag().equals(Global.EMPTY_STRING)
                    && !typeCourant.getFinFlag().equals(Global.EMPTY_STRING))
            {
                LineFragment flag = new LineFragment();
                flag.setFirstCharOffset(typeCourant.getDebutFlag());
                flag.setLastCharOffset(typeCourant.getFinFlag());
                parameterModel.setFlag(flag);
            }

            if (code.trim().equals(Intern.paramPRES))
            {
                indexPRES = i;
            }

            stationModel.addParameterModel(parameterModel);

        }
        // enregistrement de l'indexPRES
        stationModel.setPresIndex(indexPRES);

        EditData.validee = true;

    }

    public static void ouvertureModele(InputModel input)
    {
        // fonction d'ouverture d'un modele de campagne

        if (inputModel_modele != null)
        {
            inputModel_modele.release();
        }

        DataModel.ouverture = true;
        inputModel_modele = new InputModel();
        inputModel_modele = input;

        // forcage affichage onglet 0
        ViewFile.tabFolder.setSelection(0);
        ViewFile.affiche = 0;
        // Utils.invalidEtapes();
        // RAZ global
        Utils.razGlobal();

        // reset de l'etape de conversion
        ViewFile.etape = 0;

        // lancement du processus sur le premier onglet et effacement preventif
        // si besoin
        fillDesc(true);
    }

    public static void fillDesc(boolean raz) throws RuntimeException
    {
        // remplissage auto de l'IHM Desc
        if (dirSurvey_modele != null)
        {
            dirSurvey_modele.releaseInputModel();
        }
        if (singleSurvey_modele != null && raz)
        {
            singleSurvey_modele.releaseInputModel();
        }

        if (ViewFile.dossierCampagne)
        {
            try
            {
                dirSurvey_modele = new DirectoryBasedSurvey();
                dirSurvey_modele =
                    (DirectoryBasedSurvey) inputModel_modele.getMedia();
            }
            catch (RuntimeException e)
            {
                Utils.messageErreurAppend(Messages.Etape_124);
                e.printStackTrace();
                throw e;
            }
            stationModel_modele = dirSurvey_modele.getStationModel();
            EditDesc.sectionEnteteFichier.razValues();
        }
        else
        {
            try
            {
                singleSurvey_modele = new SingleFileSurvey();
                singleSurvey_modele =
                    (SingleFileSurvey) inputModel_modele.getMedia();
            }
            catch (RuntimeException e)
            {
                Utils.messageErreurAppend(Messages.Etape_124);
                e.printStackTrace();
                throw e;
            }
            stationModel_modele = singleSurvey_modele.getStationModel();
            // lignes entete fichier
            EditDesc.sectionEnteteFichier.setValue(String
                .valueOf(singleSurvey_modele.getHeaderLinesNumber()));

        }

        try
        {

            // Cochage du selecteur de type de fichier
            if (stationModel_modele.getInputType().equals(Global.PROFILE)
                    && (stationModel_modele.getTimeSeriesInformation() != null))
            {
                // Pour exploiter les anciens modeles
                ViewFile.selectTypeFile(Global.TIMESERIES);
            }
            else
            {
                ViewFile.selectTypeFile(stationModel_modele.getInputType());
            }

            // séparateur
            String strSeparator = stationModel_modele.getSeparator();

            // pour prise en compte des anciens modèles (par défaut, pas de
            // séparateur)
            if (strSeparator == null)
                strSeparator = Global.EMPTY_STRING;

            boolean blnSeparator = !Global.EMPTY_STRING.equals(strSeparator);

            ViewFile.chkSeparateur.setSelection(blnSeparator);
            ViewFile.setSeparatorVisible();

            if (blnSeparator)
            {
                if (Global.TAB.equals(strSeparator))
                {
                    // Tabulation
                    ViewFile.radioTabulation.setSelection(true);
                    ViewFile.radioPointVirgule.setSelection(false);
                    ViewFile.radioVirgule.setSelection(false);
                    ViewFile.radioEspace.setSelection(false);
                    ViewFile.radioSeparateurAutre.setSelection(false);
                    ViewFile.textSeparateur.setText(Global.EMPTY_STRING);
                }
                else if (Global.SEMICOLON.equals(strSeparator))
                {
                    // Point-virgule
                    ViewFile.radioTabulation.setSelection(false);
                    ViewFile.radioPointVirgule.setSelection(true);
                    ViewFile.radioVirgule.setSelection(false);
                    ViewFile.radioEspace.setSelection(false);
                    ViewFile.radioSeparateurAutre.setSelection(false);
                    ViewFile.textSeparateur.setText(Global.EMPTY_STRING);
                }
                else if (Global.COMMA.equals(strSeparator))
                {
                    // Virgule
                    ViewFile.radioTabulation.setSelection(false);
                    ViewFile.radioPointVirgule.setSelection(false);
                    ViewFile.radioVirgule.setSelection(true);
                    ViewFile.radioEspace.setSelection(false);
                    ViewFile.radioSeparateurAutre.setSelection(false);
                    ViewFile.textSeparateur.setText(Global.EMPTY_STRING);
                }
                else if (Global.SPACE.equals(strSeparator))
                {
                    // Espace
                    ViewFile.radioTabulation.setSelection(false);
                    ViewFile.radioPointVirgule.setSelection(false);
                    ViewFile.radioVirgule.setSelection(false);
                    ViewFile.radioEspace.setSelection(true);
                    ViewFile.radioSeparateurAutre.setSelection(false);
                    ViewFile.textSeparateur.setText(Global.EMPTY_STRING);
                }
                else
                {
                    // Autre séparateur
                    ViewFile.radioTabulation.setSelection(false);
                    ViewFile.radioPointVirgule.setSelection(false);
                    ViewFile.radioVirgule.setSelection(false);
                    ViewFile.radioEspace.setSelection(false);
                    ViewFile.radioSeparateurAutre.setSelection(true);
                    ViewFile.textSeparateur.setText(strSeparator);
                }
            }

            // nombre lignes entete station
            EditDesc.sectionEnteteStation.selectNumber(!stationModel_modele
                .isEndStationHeaderPattern());
            EditDesc.sectionEnteteStation.setIndicatorText(stationModel_modele
                .getEndStationHeader());
            EditDesc.sectionEnteteStation
                .setNbLinesBeforeData(String.valueOf(stationModel_modele
                    .getNumberLinesBetweenStationData()));
            EditDesc.sectionEnteteStation.setValue(String
                .valueOf(stationModel_modele.getNumberOfLines()));

            // nombre de lignes de fin
            EditDesc.sectionFinStation.setValue(String
                .valueOf(stationModel_modele.getEndData()));

            // section EndData
            FileEndData endData = EditDesc.sectionRuptureDonnees;
            if (stationModel_modele.isEndOnEOF())
            {
                endData.selectEndData(endData.getRadEOF());
            }
            else if (stationModel_modele.isEndOnEmptyLine())
            {
                endData.selectEndData(endData.getRadLigne());
            }
            else if (stationModel_modele.isEndOnNextStation())
            {
                endData.selectEndData(endData.getRadNextStation());
                endData.setStartStationIndicator(stationModel_modele
                    .getEndDataPattern());
            }
            else if (stationModel_modele.isEndOnPattern())
            {
                endData.selectEndData(endData.getRadManuel());
                endData.setValue(stationModel_modele.getEndDataPattern());
            }

            // refresh de la zone d'edition
            EditDesc.razEdition();
            EditDesc.DrawingBack();

            DataModel.ouverture = true;

            // activation option serie tempo
            ViewFile.enableChoice(true);
        }
        catch (Exception e)
        {
            Utils.messageErreurAppend(Messages.Etape_124);
            e.printStackTrace();
        }
    }

    public static void fillSurvey()
    {
        // recupération des items
        Couple refCampagne =
            EditSurvey.sectionReference.getItem(Messages.edit_cruise_cruiseRef);
        Couple nomCampagne =
            EditSurvey.sectionReference
                .getItem(Messages.edit_cruise_cruiseName);
        Couple navire =
            EditSurvey.sectionReference.getItem(Messages.edit_cruise_ship);
        Couple dateDebut =
            EditSurvey.sectionLocalisation
                .getItem(Messages.edit_cruise_startDate);
        Couple dateFin =
            EditSurvey.sectionLocalisation
                .getItem(Messages.edit_cruise_endDate);
        Couple region =
            EditSurvey.sectionLocalisation.getItem(Messages.edit_cruise_region);
        Couple pays =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_country);
        Couple labo =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_lab);
        Couple chef =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_chief);
        Couple projet =
            EditSurvey.sectionSource.getItem(Messages.edit_cruise_project);
        Couple centreDonnee =
            EditSurvey.sectionCentre.getItem(Messages.edit_cruise_dataCenter);
        Couple confidentialite =
            EditSurvey.sectionCentre.getItem(Messages.edit_cruise_confident);
        Couple commentaires =
            EditSurvey.sectionCommentaire.getItem(Global.EMPTY_STRING);

        // System.out.println("Remplissage onglet survey");
        Survey survey;
        if (ViewFile.dossierCampagne)
        {
            survey = dirSurvey_modele.getModel();
        }
        else
        {
            survey = singleSurvey_modele.getModel();
        }

        // injection des valeurs de l'IHM -> datamodel
        // reference
        refCampagne.setText(survey.getReference());
        nomCampagne.setText(survey.getName());
        Utils.selectComboCodePlus(navire.getValueCombo(), survey.getShipCode(),
            survey.getShipName());

        // Localisation

        // DATES
        try
        {
            dateDebut.setText(Formatter.getSingleton().formatDateTime(
                "dd/MM/yyyy", survey.getStartDate())); //$NON-NLS-1$
        }
        catch (ParseException e)
        {

            e.printStackTrace();
        }

        try
        {
            dateFin.setText(Formatter.getSingleton().formatDateTime(
                "dd/MM/yyyy", survey.getEndDate())); //$NON-NLS-1$
        }
        catch (ParseException e)
        {

            e.printStackTrace();
        }

        Utils
            .selectComboLibelle(region.getValueCombo(), survey.getRegionName());

        // Source de donnees
        Utils.selectComboCode(pays.getValueCombo(), survey.getCountryCode());
        labo.setText(survey.getLaboratories());
        chef.setText(survey.getChiefScientist());
        projet.setText(survey.getProjectName());

        // Centre d'archivage
        Utils.selectComboCode(centreDonnee.getValueCombo(), survey
            .getDataCentre());

        // confidentialite
        String code = survey.getAvailabilityCode();
        Utils.selectComboCode(confidentialite.getValueCombo(), code);

        // Commentaire
        commentaires.setText(survey.getComment());

        // Remplissage de la liste des dataTypes de la campagne
        EditSurvey.sectionType.fillFromModel(survey);
    }

    public static void fillStation()
    {
        for (NemoSection stationSection : EditStation.listeSectionsStation)
        {
            stationSection.fillFromModel(stationModel_modele);
        }

        // ENDDATETIME flag !!
        if (!stationModel_modele.getInputType().equals(Global.PROFILE))
        {
            PropertyModel propertyModel =
                stationModel_modele.getTimeSeriesModel().propertiesAt(
                    NemoSection.S_EDATE);
            EditStation.qcFlags.setFlagFragment(propertyModel.getFlag(),
                Global.ENDDATETIME, Messages.getString(Global.ENDDATETIME)
                    .trim());
        }

    }

    public static void fillBackStation()
    {
        for (NemoSection stationSection : EditStation.listeSectionsStation)
        {
            stationSection.fillFromModel(stationModel);
        }

        // ENDDATETIME flag !!
        if (!stationModel.getInputType().equals(Global.PROFILE))
        {
            PropertyModel propertyModel =
                stationModel.getTimeSeriesModel().propertiesAt(
                    NemoSection.S_EDATE);
            EditStation.qcFlags.setFlagFragment(propertyModel.getFlag(),
                Global.ENDDATETIME, Messages.getString(Global.ENDDATETIME)
                    .trim());
        }

    }

    public static void main(String[] args)
    {
        try
        {

            DateTimeFormat format = new DateTimeFormat("MMM dd yyyy"); //$NON-NLS-1$
            format.toString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println(">>>>" + e.getMessage()); //$NON-NLS-1$
        }
    }

    public static void fillData()
    {
        // /////////////////////////////////
        TableData.types = null;
        TableData.types = new ArrayList<TypeData>();

        ParameterModel[] parameterModels =
            stationModel_modele.getParameterModels();

        // remplissage des parametres mesures
        int nbreParam = parameterModels.length;

        for (int i = 0; i < nbreParam; i++)
        {
            TypeData typeData = new TypeData();
            ParameterModel paraCourant = parameterModels[i];
            Parameter modeleCourant = paraCourant.getModel();
            LineFragment frag = paraCourant.getFragment();
            LineFragment flag = paraCourant.getFlag();

            // code / fullName / sortie / format
            typeData.setCode(modeleCourant.getCode() + Global.SEP
                    + modeleCourant.getFullName());
            typeData.setNom(modeleCourant.getFullName());

            if (paraCourant.getConversionFormula().equals(Global.EMPTY_STRING))
            {
                typeData.setConversion("x*1"); //$NON-NLS-1$
            }
            else
            {
                typeData.setConversion(paraCourant.getConversionFormula());
            }
            typeData.setDebut(frag.getFirstCharOffset());
            typeData.setFin(frag.getLastCharOffset());
            if (flag != null)
            {
                typeData
                    .setDebutFlag(String.valueOf(flag.getFirstCharOffset()));
                typeData.setFinFlag(String.valueOf(flag.getLastCharOffset()));
            }

            String inputDefault = paraCourant.getInputDefault();
            if (inputDefault == null || inputDefault.equals("null"))inputDefault = Global.EMPTY_STRING; //$NON-NLS-1$
            typeData.setEntree(inputDefault);

            typeData.setUnite(modeleCourant.getUnit());
            typeData.setFormat(modeleCourant.getFormat());
            typeData.setSortie(modeleCourant.getDefault());

            TableData.types.add(typeData);

        }

        // refresh de la table de l'onglet Data
        TableData.razTableData();

        // refresh de la table
        TableData.creationTable();
    }

    public static void reset()
    {
        stationModel = null;
        stationModel_modele = null;
        singleSurvey = null;
        singleSurvey_modele = null;
        dirSurvey = null;
        dirSurvey_modele = null;
        station = null;
        station_modele = null;
        timeSeriesInformation = null;
        timeSeriesInformation_modele = null;

        trajectory = null;
        ouverture = false;
        reset = false;

        // par soucis d'optimisation :
        // ne pas supprimer les listes récupérés du Web Service
    }
}
