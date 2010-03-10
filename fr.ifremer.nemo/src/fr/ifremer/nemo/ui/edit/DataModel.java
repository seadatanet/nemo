package fr.ifremer.nemo.ui.edit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import com.ibm.icu.text.SimpleDateFormat;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.input.Parameter;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.cruise.TableDataSurvey;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.format.DateTimeFormat;
import fr.ifremer.nemo.ws.WSDataType;

public class DataModel
{
    /*
    public static InputModel inputModel, inputModel_modele;

    public static StationModel stationModel, stationModel_modele;

    public static SingleFileSurvey singleSurvey, singleSurvey_modele;

    public static DirectoryBasedSurvey dirSurvey, dirSurvey_modele;

    public static Station station, station_modele;
    */

   /* public static WSParameter[] listeWSParameters, listeWSParameters_modele;*/

   /* public static WSDataType[] listeWSDataTypes, listeWSDataTypes_modele; */

    public static WSDataType wsDatatypeStation, wsDatatypeStation_modele;

    public static Boolean /*ouverture, */reset;
/*
    public static TimeSeriesInformation timeSeriesInformation,
            timeSeriesInformation_modele;

    public static Trajectory trajectory;
*/
    public static void init()
    {
        // on initialise les dataTypes, centers et parameters en prevision des
        // onglets Campagne et Data

     //   listeWSDataTypes = NemoLists.getDataTypes();
     //   listeWSParameters = NemoLists.getParameters();
    }

    /*
    public static void initModele(Boolean fichier, String typeFichier, String nomFich)
    {
        Nemo.getInstance().getDomain().getMedia().getModel().setSourcePath(nomFich);

        initDesc();

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
    */
/*
    public static void initDossier(String typeFichier, String nomDossier)
    {

        if (inputModel != null)
        {
            inputModel.release();
        }

        inputModel = new InputModel();
        inputModel.initFoDirectoryBased(typeFichier);
        dirSurvey = (DirectoryBasedSurvey) inputModel.getMediaModel();
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
        singleSurvey = (SingleFileSurvey) inputModel.getMediaModel();
        singleSurvey.setMedia(nomFich);

        initDesc();
    }
*/
    public static void initDesc(String nomFich)
    {
        MediaModel mediaModel = new MediaModel();
        
        // --- enregistrement du nom du fichier en entrée
        mediaModel.setSourcePath(nomFich);
        
        // --- enregistrement du formulaire de gauche (ViewMain)
        // Type de traitement
        mediaModel.setProcessType(ViewMain.getTypeTraitement());

        // Séparateur
        mediaModel.setSeparator(ViewMain.getSeparateur());

        // Type de fichier
        mediaModel.setInputType(ViewMain.getTypeFichierEntree());

        // Fichier multiStation ?
        mediaModel.setMultiStations(ViewMain.chkMultiStations.getSelection());
        
        // --- enregistrement du formulaire de droite (editFileForm)
        // Entête Fichier
        int fileHeader = 0;
        if (!ViewMain.dossierCampagne || mediaModel.isMultiStations()){
            fileHeader = Integer.parseInt(ViewMain.editFileForm.sectionEnteteFichier.getValue());
        }
        
        mediaModel.setFileHeader(fileHeader);

        // Entête Station
        if (!ViewMain.editFileForm.sectionEnteteStation.isSelectNumber()){
            mediaModel.setStationHeaderPattern(ViewMain.editFileForm.sectionEnteteStation.getIndicatorText());
            mediaModel.setStationHeader(ViewMain.editFileForm.sectionEnteteStation.getNbLinesBeforeData());
        }
        else{
            mediaModel.setStationHeader(ViewMain.editFileForm.sectionEnteteStation.getValue());
        }
        
        // Fin de Station
        mediaModel.setEndStation(ViewMain.editFileForm.sectionFinStation.getValue());
        
        // Fin des données
        E_EndData end = ViewMain.editFileForm.sectionRuptureDonnees.getEndData();
        mediaModel.setEndData(end);
        if (end == E_EndData.END_WITH){
            mediaModel.setEndDataPattern(ViewMain.editFileForm.sectionRuptureDonnees.getValue());
        }
        else if(end == E_EndData.NEXT_START_WITH){
            mediaModel.setEndDataPattern(ViewMain.editFileForm.sectionRuptureDonnees.getStartStationIndicator());
        }
        
        // Mise à jour du model
        Nemo.getInstance().getDomain().getMedia().updateMedia(mediaModel);

        // verrouillage de l'option serieTemporelle pour eviter les changements
        //ViewMain.enableChoice(false);

        // flag "validee" à 1
        //ViewMain.editFileForm.validee = true;

    }

    public static void initCruise()
    {
        // recuperation du model correspondant
        CruiseModel cruiseModel = new CruiseModel();
        
        
        // recupération des items
        Couple refCampagne =
            ViewMain.editCruiseForm.sectionReference
                .getItem(Messages.edit_cruise_cruiseRef);
        Couple nomCampagne =
            ViewMain.editCruiseForm.sectionReference
                .getItem(Messages.edit_cruise_cruiseName);
        Couple navire =
            ViewMain.editCruiseForm.sectionReference
                .getItem(Messages.edit_cruise_ship);
        Couple dateDebut =
            ViewMain.editCruiseForm.sectionLocalisation
                .getItem(Messages.edit_cruise_startDate);
        Couple dateFin =
            ViewMain.editCruiseForm.sectionLocalisation
                .getItem(Messages.edit_cruise_endDate);
        Couple region =
            ViewMain.editCruiseForm.sectionLocalisation
                .getItem(Messages.edit_cruise_region);
        Couple pays =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_country);
        Couple labo =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_lab);
        Couple chef =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_chief);
        Couple projet =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_project);
        Couple centreDonnee =
            ViewMain.editCruiseForm.sectionCentre
                .getItem(Messages.edit_cruise_dataCenter);
        Couple confidentialite =
            ViewMain.editCruiseForm.sectionCentre
                .getItem(Messages.edit_cruise_confident);
        Couple commentaires =
            ViewMain.editCruiseForm.sectionCommentaire
                .getItem(Global.EMPTY_STRING);

        // maj des dataTypes
        TableDataSurvey.majTypeData();

        // injection des valeurs de l'IHM -> datamodel
        // reference
        if (ViewMain.tabCruise.isDisposed()){
            ViewMain.editCruiseForm.validee = true;
            return;
        }

        cruiseModel.setReference(refCampagne.getText());
        cruiseModel.setName(nomCampagne.getText());
        cruiseModel.setShipCode(Utils.extraitCode(navire.getText()));
        cruiseModel.setShipName(Utils.extraitValeur(navire.getText()));

        // Localisation

        // DATE DE DEBUT
        try
        {
            DateTimeFormat dateFormat = new DateTimeFormat();
            dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
            Long dateLong = (Long) dateFormat.parseObject(dateDebut.getText());
            cruiseModel.setStartDate(dateLong);
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
            cruiseModel.setEndDate(dateLong);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        cruiseModel.setRegionName(Utils.extraitValeur(region.getText()));

        // Source de données
        cruiseModel.setCountryCode(Utils.extraitCode(pays.getText()));
        cruiseModel.setLaboratories(labo.getText());
        cruiseModel.setChiefScientist(chef.getText());
        cruiseModel.setProjectName(projet.getText());

        // Centre d'archivage
        cruiseModel.setDataCentre(Utils.extraitCode(centreDonnee.getText()));

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

        // Commentaire
        cruiseModel.setComment(commentaires.getText());
        
        // Mise à jour du model
        Nemo.getInstance().getDomain().getCruise().updateCruise(cruiseModel);

        // remplissage de la liste des dataTypes de la campagne
        ViewMain.editCruiseForm.sectionType.initModel(cruiseModel);

        ViewMain.editCruiseForm.validee = true;
    }

    private static void initDiseabledField(StationModel sModel, String property)
    {
        PropertyModel propertyModel = new PropertyModel();
        propertyModel.setProperty(property);
        propertyModel.setFlag(ViewMain.editStationForm.qcFlags.getFlagFragment(property)); // Flag qualite
        propertyModel.setToCompute(true);
        sModel.addProperty(propertyModel);
    }

    public static void initStation()
    {
        // recuperation du model correspondant
        StationDomain sDomain = Nemo.getInstance().getDomain().getCruise().getStation();
        StationModel stationModel = new StationModel();
        E_InputType inputType = Nemo.getInstance().getDomain().getMedia().getModel().getInputType();
        
        PropertyModel propertyModel;

        // compléter ELAT / ELON et EDEPTH si nécessaire
        if (inputType == E_InputType.TIMESERIES)
        {
//            if (ViewMain.editStationForm.endLatitude.getValue().trim().equals(
//                Global.EMPTY_STRING))
//            {
//                // vide donc on recopie latitude dans latitude de fin
//                ViewMain.editStationForm.endLatitude
//                    .copyValues(ViewMain.editStationForm.latitude);
//            }
//            if (ViewMain.editStationForm.endLongitude.getValue().trim().equals(
//                Global.EMPTY_STRING))
//            {
//                // vide donc on recopie longitude dans longitude de fin
//                ViewMain.editStationForm.endLongitude
//                    .copyValues(ViewMain.editStationForm.longitude);
//            }
//            if (ViewMain.editStationForm.endDepth.getValue().trim().equals(
//                Global.EMPTY_STRING))
//            {
//                // vide donc on recopie profondeur dans profondeur de fin
//                ViewMain.editStationForm.endDepth
//                    .copyValues(ViewMain.editStationForm.depth);
//            }
        }

        // parcours des sections de la station pour initialisation
        for (NemoSection stationSection : ViewMain.editStationForm.listeSections){
            stationSection.initModel(stationModel);
        }

        // Completer les données manquantes

        // DM HISTORY 1&2
        //station.setDmHistory1(Global.EMPTY_STRING);
        //station.setDmHistory2(Global.EMPTY_STRING);

        if (inputType != E_InputType.PROFILE){
            // GESTION Time/Date in Data
            if (ViewMain.editStationForm.samplingRate.isEnabled()){
                // calcul e partir frequence echantillonnage
                sDomain.setComputed(true);
            }
            else{
                // calcul position date/heure dans les donnees
                sDomain.setComputed(false);
                // remplissage avec les données par défaut
                propertyModel = stationModel.propertiesAt(Intern.S_SAMPLINGRATE);
                propertyModel.setUserProvided(true);
                propertyModel.setConversionFormula("*1"); //$NON-NLS-1$
                //sModel.getTimeSeriesModel().setSampleRateConversion("*1"); //$NON-NLS-1$
                propertyModel.setUserValue(Global.ZERO);
                //sModel.getTimeSeriesInformation().setSamplingRate(Global.ZERO);
            }

            // DATE / HEURE DE FIN
            propertyModel = new PropertyModel();
            //stationModel.propertiesAt(Intern.S_EDATE);
            propertyModel.setProperty(Intern.S_EDATE);
            propertyModel.setUserProvided(true);
            propertyModel.setFlag(ViewMain.editStationForm.qcFlags.getFlagFragment(Intern.ENDDATETIME)); // Flag qualite
            stationModel.addProperty(propertyModel);

            propertyModel = new PropertyModel();
            //propertyModel = stationModel.propertiesAt(Intern.S_ETIME);
            propertyModel.setProperty(Intern.S_ETIME);
            propertyModel.setUserProvided(true);
            propertyModel.setFlag(ViewMain.editStationForm.qcFlags
                .getFlagFragment(Intern.ENDDATETIME)); // Flag qualite
            stationModel.addProperty(propertyModel);

            if (inputType == E_InputType.TRAJECTORY){
                initDiseabledField(stationModel, Intern.S_ELAT);
                initDiseabledField(stationModel, Intern.S_ELON);
                initDiseabledField(stationModel, Intern.S_EDEPTH);

                // DECLINAISON MAGNETIQUE : FAE 1386 - valeur par défaut pour
                // les Trajectoires
                propertyModel = new PropertyModel();
                //propertyModel = stationModel.propertiesAt( Intern.S_MAGNETIC);
                propertyModel.setProperty(Intern.S_MAGNETIC);
                propertyModel.setUserProvided(true);
                propertyModel.setUserValue("-999"); //$NON-NLS-1$
                stationModel.addProperty(propertyModel);
                //timeSeriesInformation.setMagneticDeclination("-999"); //$NON-NLS-1$
            }
        } // fin specifique SERIE TEMPO ou TRAJECTOIRE

        // Mise à jour du model
        Nemo.getInstance().getDomain().getCruise().getStation().updateStation(stationModel);

        // flag validation à 1
        ViewMain.editStationForm.validee = true;
    }

    public static void initData()
    {
        // recuperation du model correspondant
        fr.ifremer.nemo.input.DataModel dataModel = new fr.ifremer.nemo.input.DataModel();
        
        //int indexPRES = -1;

        // mise e jour des types avant validation
        TableData.majTypeData();

        // remplissage des parametres mesures
        //int nbreParam = TableData.types.size();

        // reset systematique des parametres stockes dans le dataModel
        Nemo.getInstance().getDomain().getCruise().getStation().getData().getModel().releaseParameterModels();

        for (TypeData typeCourant : TableData.types)
        {
            //TypeData typeCourant = TableData.types.get(i);

            // code / fullName / sortie / format
            //Parameter para = new Parameter();
            ParameterModel parameterModel = new ParameterModel();
            //parameterModel.setModel(para);

            // parameterModel.setModel(para);

            // split sur " - "
            Pattern p = Pattern.compile(Global.SEP);
            String mots[] = p.split(typeCourant.getCode());
            String code = mots[0];
            String nom = mots[1];

            Parameter para = new Parameter();
            // code, nom, unite
            para.setCode(code);
            para.setFullName(nom);
            para.setUnit(typeCourant.getUnite());

            // specific P011
            para.setCodeP011(typeCourant.getCodeP011());
            para.setNameP011(typeCourant.getNomP011());
            para.setP011(typeCourant.isP011());
            
            
            para.setDefault(typeCourant.getSortie());
            para.setFormat(typeCourant.getFormat());
            parameterModel.setParameter(para);

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

            //if (code.trim().equals(Intern.paramPRES)){
            //    indexPRES = i;
            //}

            dataModel.addParameterModel(parameterModel);

        }
        // enregistrement de l'indexPRES
        //dModel.setPresIndex(indexPRES);
        Nemo.getInstance().getDomain().getCruise().getStation().getData().updateData(dataModel);
        

        ViewMain.editDataForm.validee = true;

    }
    
    //public static void initConversion(){
    //}

    public static void fillDesc(boolean raz) throws RuntimeException
    {
        // remplissage auto de l'IHM Desc
        if (raz){
            Nemo.getInstance().getDomain().release();
        }
        
        // recuperation du model correspondant
        MediaModel mediaModel = Nemo.getInstance().getDomain().getMedia().getModel();

        
        // --- remplissage du formulaire de gauche (ViewMain)
        // Type de traitement - déjà fixé à l'ouverture du fichier
        //TODO sourcePath

        // Séparateur
        ViewMain.setSeparateur(mediaModel.getSeparator());

        // Type de fichier
        ViewMain.selectTypeFile(mediaModel.getInputType());

        // --- remplissage du formulaire de droite (editFileForm)
        // Entête Fichier
        if (ViewMain.dossierCampagne && !ViewMain.chkMultiStations.getSelection()){
            ViewMain.editFileForm.sectionEnteteFichier.razValues();
        }
        else{
            ViewMain.editFileForm.sectionEnteteFichier.setValue(String.valueOf(mediaModel.getFileHeader()));            
        }

        // Entête Station
        if (mediaModel.getStationHeaderPattern().equals(Global.EMPTY_STRING)){
            ViewMain.editFileForm.sectionEnteteStation.selectNumber(true);
            ViewMain.editFileForm.sectionEnteteStation.setValue(String.valueOf(mediaModel.getStationHeader()));
        }
        else{
            ViewMain.editFileForm.sectionEnteteStation.selectNumber(false);
            ViewMain.editFileForm.sectionEnteteStation.setIndicatorText(mediaModel.getStationHeaderPattern());
            ViewMain.editFileForm.sectionEnteteStation.setNbLinesBeforeData(String.valueOf(mediaModel.getStationHeader()));
        }
        
        // Fin de Station
        ViewMain.editFileForm.sectionFinStation.setValue(String.valueOf(mediaModel.getEndStation()));
        
        // Fin des données
        ViewMain.editFileForm.sectionRuptureDonnees.setEndData(mediaModel.getEndData(), mediaModel.getEndDataPattern());

        // activation option serie tempo
        ViewMain.enableChoice(true);
    }

    public static void fillCruise()
    {
        // recuperation du model correspondant
        CruiseModel cruiseModel = Nemo.getInstance().getDomain().getCruise().getModel();
        
        // recupération des items
        Couple refCampagne =
            ViewMain.editCruiseForm.sectionReference
                .getItem(Messages.edit_cruise_cruiseRef);
        Couple nomCampagne =
            ViewMain.editCruiseForm.sectionReference
                .getItem(Messages.edit_cruise_cruiseName);
        Couple navire =
            ViewMain.editCruiseForm.sectionReference
                .getItem(Messages.edit_cruise_ship);
        Couple dateDebut =
            ViewMain.editCruiseForm.sectionLocalisation
                .getItem(Messages.edit_cruise_startDate);
        Couple dateFin =
            ViewMain.editCruiseForm.sectionLocalisation
                .getItem(Messages.edit_cruise_endDate);
        Couple region =
            ViewMain.editCruiseForm.sectionLocalisation
                .getItem(Messages.edit_cruise_region);
        Couple pays =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_country);
        Couple labo =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_lab);
        Couple chef =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_chief);
        Couple projet =
            ViewMain.editCruiseForm.sectionSource
                .getItem(Messages.edit_cruise_project);
        Couple centreDonnee =
            ViewMain.editCruiseForm.sectionCentre
                .getItem(Messages.edit_cruise_dataCenter);
        Couple confidentialite =
            ViewMain.editCruiseForm.sectionCentre
                .getItem(Messages.edit_cruise_confident);
        Couple commentaires =
            ViewMain.editCruiseForm.sectionCommentaire
                .getItem(Global.EMPTY_STRING);

        // System.out.println("Remplissage onglet survey");
        /*Survey survey;
        if (a_Survey != null){
            survey = a_Survey;
        } 
        else if (ViewMain.dossierCampagne){
            survey = dirSurvey_modele.getModel();
        }
        else{
            survey = singleSurvey_modele.getModel();
        }*/

        // injection des valeurs de l'IHM -> datamodel
        // reference
        refCampagne.setText(cruiseModel.getReference());
        nomCampagne.setText(cruiseModel.getName());
        Utils.selectComboCodePlus(navire.getValueCombo(), cruiseModel.getShipCode(),
            cruiseModel.getShipName());

        // Localisation

        // DATES
        // FAE 2607
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
        Date startValue = new Date(cruiseModel.getStartDate());
        dateDebut.setText(dateFormat.format(startValue));
        

        Date endValue = new Date(cruiseModel.getEndDate());
        dateFin.setText(dateFormat.format(endValue));

        Utils
            .selectComboLibelle(region.getValueCombo(), cruiseModel.getRegionName());

        // Source de donnees
        if (cruiseModel.getCountryName() != null) {
            Utils.selectComboLibelle(pays.getValueCombo(), cruiseModel.getCountryName());
        }else{
            Utils.selectComboCode(pays.getValueCombo(), cruiseModel.getCountryCode());
        }
        labo.setText(cruiseModel.getLaboratories());
        chef.setText(cruiseModel.getChiefScientist());
        projet.setText(cruiseModel.getProjectName());

        // Centre d'archivage
        Utils.selectComboCode(centreDonnee.getValueCombo(), cruiseModel
            .getDataCentre());

        // confidentialite
        String code = cruiseModel.getAvailability().getCode();
        Utils.selectComboCode(confidentialite.getValueCombo(), code);

        // Commentaire
        commentaires.setText(cruiseModel.getComment());

        // Remplissage de la liste des dataTypes de la campagne
        ViewMain.editCruiseForm.sectionType.fillFromModel(cruiseModel);
    }

    public static void fillStation()
    {
        MediaModel mModel = Nemo.getInstance().getDomain().getMedia().getModel();
        StationModel sModel = Nemo.getInstance().getDomain().getCruise().getStation().getModel();
        
        for (NemoSection stationSection : ViewMain.editStationForm.listeSections){
            stationSection.fillFromModel(sModel);
        }

        // ENDDATETIME flag !!
        if (mModel.getInputType() != E_InputType.PROFILE){
            PropertyModel propertyModel = sModel.propertiesAt(Intern.S_EDATE);
            ViewMain.editStationForm.qcFlags.setFlagFragment(propertyModel
                .getFlag(), Intern.ENDDATETIME, Messages.getString(
                    Intern.ENDDATETIME).trim());
        }

    }

/*    public static void fillBackStation()
    {
        StationModel
        for (NemoSection stationSection : ViewMain.editStationForm.listeSections){
            stationSection.fillFromModel(stationModel);
        }

        // ENDDATETIME flag !!
        if (!stationModel.getInputType().equals(Global.PROFILE))
        {
            PropertyModel propertyModel =
                stationModel.getTimeSeriesModel().propertiesAt(
                    NemoSection.S_EDATE);
            ViewMain.editStationForm.qcFlags.setFlagFragment(propertyModel
                .getFlag(), Global.ENDDATETIME, Messages.getString(
                Global.ENDDATETIME).trim());
        }

    }*/

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
            Nemo.getInstance().getDomain().getCruise().getStation().getData().getModel().getParameterModels();

        // remplissage des parametres mesures
        for (ParameterModel paraCourant : parameterModels)
        {
            TypeData typeData = new TypeData();
            Parameter modeleCourant = paraCourant.getParameter();
            LineFragment frag = paraCourant.getFragment();
            LineFragment flag = paraCourant.getFlag();

            // code / fullName / sortie / format
            typeData.setCode(modeleCourant.getCode() + Global.SEP
                    + modeleCourant.getFullName());
            typeData.setNom(modeleCourant.getFullName());

            if (paraCourant.getConversionFormula().equals(Global.EMPTY_STRING)){
                typeData.setConversion(TypeData.DEFAULT_CONVERT);
            }
            else{
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
            typeData.setCodeP011(modeleCourant.getCodeP011());
            typeData.setNomP011(modeleCourant.getNameP011());
            typeData.setP011(modeleCourant.isP011());
            
            
            TableData.types.add(typeData);

        }

        // refresh de la table de l'onglet Data
        TableData.razTableData();

        // refresh de la table
        TableData.creationTable();
    }

    public static void reset()
    {
        /*stationModel = null;
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
        ouverture = false;*/
        reset = false;

        // par soucis d'optimisation :
        // ne pas supprimer les listes récupérés du Web Service
    }
}
