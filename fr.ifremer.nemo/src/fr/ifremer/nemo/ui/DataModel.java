package fr.ifremer.nemo.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.DataType;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.domain.TimeSeriesInformation;
import fr.ifremer.nemo.domain.Trajectory;
import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.input.DirectoryBasedSurvey;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.LineFragment;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.SingleFileSurvey;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.ui.object.FileSection;
import fr.ifremer.nemo.ui.object.NemoSection;
import fr.ifremer.nemo.util.DateTimeFormat;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
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

        // taille du header fichier
        if (ViewFile.dossierCampagne)
        {
            dirSurvey.setDirectory(ViewFile.nomRepertoire);

            stationModel = dirSurvey.getStationModel();
        }
        else
        {
            singleSurvey.setHeaderLinesNumber(Integer
                .parseInt(EditDesc.sectionEnteteFichier.getValue()));
            stationModel = singleSurvey.getStationModel();
        }

        // Type de fichier
        stationModel.setInputType(ViewFile.typeFichierEntree());

        // serie temporelle
        if (ViewFile.radSerieTempo.getSelection()
                || ViewFile.radTrajectoire.getSelection())
        {
            timeSeriesInformation = stationModel.getTimeSeriesInformation();

            if (!ViewFile.textTempoHeureRef.getText().equals(Global.EMPTY_STRING)) 
            {
                inputModel.setRefDateBased(true);

                // conversion en LONG avant injection dans dataModel
                // DATE REF
                DateTimeFormat dateFormat;
                Long dateLong = null, timeLong = null;
                try
                {
                    dateFormat = new DateTimeFormat();
                    dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
                    dateLong =
                        (Long) dateFormat.parseObject(ViewFile.textTempoDateRef
                            .getText());
                }
                catch (ParseException e)
                {
                    // 
                    e.printStackTrace();
                }

                inputModel.setRefDate(dateLong);

                // HEURE REF
                try
                {
                    DateTimeFormat timeFormat = new DateTimeFormat();
                    timeFormat.setFormatString("hh24:mm"); //$NON-NLS-1$
                    timeLong =
                        (Long) timeFormat
                            .parseObject(ViewFile.textTempoHeureRef.getText());
                }
                catch (ParseException e)
                {
                    // 
                    e.printStackTrace();
                }

                inputModel.setRefTime(timeLong);
            }
            else
            {
                // pas d'heure de ref
                inputModel.setRefDateBased(false);
            }

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
        stationModel.setNumberOfLines(Integer
            .parseInt(EditDesc.sectionEnteteStation.getValue()));

        // nombre de lignes de fin
        stationModel.setEndData(Integer.parseInt(EditDesc.sectionFinStation.getValue()));

        // attribut pattern fin donnees en acces direct
        if (EditDesc.sectionRuptureDonnees.radEOFselected())
        {
            stationModel.setEndOnEmptyLine(false);
            stationModel.setEndOnEOF(true);
            stationModel.setEndOnPattern(false);
        }
        else if (EditDesc.sectionRuptureDonnees.radLigneSelected())
        {
            stationModel.setEndOnEmptyLine(true);
            stationModel.setEndOnEOF(false);
            stationModel.setEndOnPattern(false);
        }
        else if (EditDesc.sectionRuptureDonnees.radManuelSelected())
        {
            stationModel.setEndOnEmptyLine(false);
            stationModel.setEndOnEOF(false);
            stationModel.setEndOnPattern(true);
            stationModel.setEndDataPattern(EditDesc.sectionRuptureDonnees.getValue());
        }
        
        // on initialise les dataTypes, centers et parameters en prevision des
        // onglets Campagne et Data

        listeWSDataTypes = NemoLists.getDataTypes();
        listeWSParameters = NemoLists.getParameters();
        TableData.go();

        // verrouillage de l'option serieTemporelle pour eviter les changements
        ViewFile.radProfilVertical.setEnabled(false);
        ViewFile.radSerieTempo.setEnabled(false);
        ViewFile.radTrajectoire.setEnabled(false);

        // flag "validee" e 1
        EditDesc.validee = true;

    }

    public static void initSurvey()
    {
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
        if (EditSurvey.textCampagne == null
                || EditSurvey.textCampagne.isDisposed())
        {
            EditSurvey.validee = true;
            return;
        }

        survey.setReference(EditSurvey.textCampagne.getText());
        survey.setName(EditSurvey.textNom.getText());
        survey.setShipCode(Utils.extraitCode(EditSurvey.comboNavire.getText()));
        survey.setShipName(Utils
            .extraitValeur(EditSurvey.comboNavire.getText()));

        // Localisation

        // DATE DE DEBUT
        try
        {
            DateTimeFormat dateFormat = new DateTimeFormat();
            dateFormat.setFormatString("dd/MM/yyyy"); //$NON-NLS-1$
            Long dateLong =
                (Long) dateFormat.parseObject(EditSurvey.textDateDebut
                    .getText());
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
            Long dateLong =
                (Long) dateFormat.parseObject(EditSurvey.textDateFin.getText());
            survey.setEndDate(dateLong);
        }
        catch (ParseException e)
        {

            e.printStackTrace();
        }

        survey.setRegionName(Utils.extraitValeur(EditSurvey.comboRegion.getText()));

        // Source de données
        survey.setCountryCode(Utils.extraitCode(EditSurvey.comboCountries
            .getText()));
        survey.setLaboratories(EditSurvey.textLabo.getText());
        survey.setChiefScientist(EditSurvey.textChef.getText());
        survey.setProjectName(EditSurvey.textProjet.getText());

        // Centre d'archivage
        survey.setDataCentre(Utils.extraitCode(EditSurvey.comboDataCentres
            .getText()));

        // confidentialité
        if (EditSurvey.comboConfid.getText().equals("P - Public")) //$NON-NLS-1$
        {
            survey.setAvailability("publicly"); //$NON-NLS-1$
        }
        if (EditSurvey.comboConfid.getText().equals("L - Limited")) //$NON-NLS-1$
        {
            survey.setAvailability("limited"); //$NON-NLS-1$
        }
        if (EditSurvey.comboConfid.getText().equals("C - Confidential")) //$NON-NLS-1$
        {
            survey.setAvailability("confidential"); //$NON-NLS-1$
        }

        // Commentaire
        survey.setComment(EditSurvey.textCommentaire.getText());

        // remplissage de la liste des dataTypes de la campagne
        // parcours de la liste des dataTypes de l'IHM et creation d'un DataType
        // e chaque fois
        int taille = TableDataSurvey.types.size();

        // reset préventif des dataTypes
        survey.releaseDataTypes();

        for (int i = 0; i < taille; i++)
        {
            TypeSurvey typeSurvey = TableDataSurvey.types.get(i);
            DataType dataType = new DataType();
            dataType.setCode(typeSurvey.getCode());
            dataType.setNumberOfProfiles(typeSurvey.getNombre());

            if (typeSurvey.getQc().equals("Y")) //$NON-NLS-1$
            {
                dataType.setQc(true);
            }
            else
            {
                dataType.setQc(false);
            }
            // dataType.setQc(typeSurvey.getQc());
            dataType.setSensor(typeSurvey.getSensor());

            // test par rapport aux types deja presents dans le DataModel
            // si l'etape a deje ete validee

            survey.addDataType(dataType);

        }

//        if (EditStation.loaded)
//        {
//            EditStation.initTypes();
//        }

        // flag "validee" e 1
        EditSurvey.validee = true;

    }
    
    private static void initDiseabledField(String property){
        PropertyModel propertyModel = stationModel.getTimeSeriesModel().propertiesAt(property);
        propertyModel.setFlag(EditStation.qcFlags.getFlagFragment(property)); // Flag qualite
        propertyModel.setToCompute(true);
    }

    public static void initStation()
    {
        PropertyModel propertyModel;

        // compléter ELAT / ELON et EDEPTH si nécessaire
        if (ViewFile.radSerieTempo.getSelection())
        {                                
            if (EditStation.endLatitude.getValue().trim().equals(Global.EMPTY_STRING)) {
                // vide donc on recopie latitude dans latitude de fin
                EditStation.endLatitude.copyValues(EditStation.latitude);
            }                        
            if (EditStation.endLongitude.getValue().trim().equals(Global.EMPTY_STRING)) {
                // vide donc on recopie longitude dans longitude de fin
                EditStation.endLongitude.copyValues(EditStation.longitude);
            }                       
            if (EditStation.endDepth.getValue().trim().equals(Global.EMPTY_STRING)) {
                // vide donc on recopie profondeur dans profondeur de fin
                EditStation.endDepth.copyValues(EditStation.depth);
            }
        }

        // parcours des sections de la station pour initialisation
        for (NemoSection stationSection : EditStation.listeSectionsStation){
            stationSection.initModel(stationModel);
        }
        
        // Completer les données manquantes
        
        // DM HISTORY 1&2
        station.setDmHistory1(Global.EMPTY_STRING); 
        station.setDmHistory2(Global.EMPTY_STRING); 
        
        if (ViewFile.radSerieTempo.getSelection() || ViewFile.radTrajectoire.getSelection())
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
                 stationModel.getTimeSeriesInformation().setSamplingRate(Global.ZERO);
             }
            
            // DATE / HEURE DE FIN
            propertyModel = stationModel.getTimeSeriesModel().propertiesAt(NemoSection.S_EDATE);
            propertyModel.setUserProvided(true);
            propertyModel.setFlag(EditStation.qcFlags.getFlagFragment(Global.ENDDATETIME)); // Flag qualite
            propertyModel = stationModel.getTimeSeriesModel().propertiesAt(NemoSection.S_ETIME);
            propertyModel.setUserProvided(true);
            propertyModel.setFlag(EditStation.qcFlags.getFlagFragment(Global.ENDDATETIME)); // Flag qualite
            

            if (ViewFile.radTrajectoire.getSelection())
            {
                initDiseabledField(NemoSection.S_ELAT);
                initDiseabledField(NemoSection.S_ELON);
                initDiseabledField(NemoSection.S_EDEPTH);

                // DECLINAISON MAGNETIQUE : FAE 1386 - valeur par défaut pour les Trajectoires
                propertyModel = stationModel.getTimeSeriesModel().propertiesAt(NemoSection.S_MAGNETIC);
                propertyModel.setUserProvided(true);
                timeSeriesInformation.setMagneticDeclination("-999");  //$NON-NLS-1$
            }
        } // fin specifique SERIE TEMPO ou TRAJECTOIRE        
        

        // flag validation e 1
        EditStation.validee = true;
    }

    public static void initData()
    {
        boolean existDEPH = false;
        int indexPRES = -1;
        String formatPRES = Global.EMPTY_STRING;

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

            //parameterModel.setModel(para);

            // split sur " - "
            Pattern p = Pattern.compile(" - "); //$NON-NLS-1$
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

            if (code.trim().equals("DEPH")) existDEPH = true; //$NON-NLS-1$
            if (code.trim().equals("PRES")){ //$NON-NLS-1$
                indexPRES = i;
                formatPRES = para.getFormat();
            }

            stationModel.addParameterModel(parameterModel);
           
        }
        // enregistrement de l'indexPRES
        stationModel.setPresIndex(indexPRES);

        // *** Ajouts automatiques de certains paramètres
        if (ViewFile.radProfilVertical.getSelection())
        {
            // Profile vertical
            if ((!existDEPH) && (indexPRES != -1)){
                // ajout de DEPH calculé à partir de PRES
                //parameterModel.setFlag(stationModel.parameterModelsAt(indexPRES).getFlag());
                stationModel.addFixedParameterModel(IdParameter.DEPH, formatPRES);
            }
        }
        else
        {       
            // Serie temporelle ou Trajectoire     
            // Ajout YEAR, MNTH, DAYX et TIME pour Medatlas
            stationModel.addFixedParameterModel(IdParameter.YEAR);
            stationModel.addFixedParameterModel(IdParameter.MNTH);
            stationModel.addFixedParameterModel(IdParameter.DAYX);
            stationModel.addFixedParameterModel(IdParameter.TIME);
            
            if (ViewFile.radSerieTempo.getSelection())
            {
                // Ajout de "Chronological Julian Date" pour ODV
                stationModel.addFixedParameterModel(IdParameter.CJDY);     
                
                // Serie temporelle
                // Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
                if (!existDEPH)
                    stationModel.addFixedParameterModel(IdParameter.DEPH);          
            }
            else
            {
                // Trajectoire
                // Ajout LATX et LONX pour Medatlas
                stationModel.addFixedParameterModel(IdParameter.LATX);  
                stationModel.addFixedParameterModel(IdParameter.LONX);  
                
                if (EditStation.depth.getValue() != null 
                        && !EditStation.depth.getValue().trim().equals(Global.EMPTY_STRING)
                        && EditStation.depth.isInData()){
                    // Ajout BATH si "Pofondeur" saisie dans les données pour Medatlas
                    stationModel.addFixedParameterModel(IdParameter.BATH);
                }
                
                // Ajout du paramètre DEPH ( = Sensor Depth) pour ODV
                if (!existDEPH)
                    stationModel.addFixedParameterModel(IdParameter.DEPH);  
                
            }
        }
        
        
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
        //Utils.invalidEtapes();
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
                Utils.messageErreurAppend(Messages.getString("Etape.124")); //$NON-NLS-1$
                e.printStackTrace();
                throw e;
            }
            stationModel_modele = dirSurvey_modele.getStationModel();
            EditDesc.sectionEnteteFichier.initValue();
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
                Utils.messageErreurAppend(Messages.getString("Etape.124")); //$NON-NLS-1$
                e.printStackTrace();
                throw e;
            }
            stationModel_modele = singleSurvey_modele.getStationModel();
            // lignes entete fichier
            EditDesc.sectionEnteteFichier.setValue(Global.EMPTY_STRING                     
                    + singleSurvey_modele.getHeaderLinesNumber());

        }

        try{
        
            // Cochage du selecteur de type de fichier
            if (stationModel_modele.getInputType().equals(Global.TIMESERIES))
            {
                ViewFile.radProfilVertical.setSelection(false);
                ViewFile.radSerieTempo.setSelection(true);
                ViewFile.radTrajectoire.setSelection(false);
            }
            else if (stationModel_modele.getInputType().equals(Global.TRAJECTORY))
            {
                ViewFile.radProfilVertical.setSelection(false);
                ViewFile.radSerieTempo.setSelection(false);
                ViewFile.radTrajectoire.setSelection(true);
            }
            else
            {
                if (stationModel_modele.getTimeSeriesInformation() != null)
                {
                    // Pour exploiter les anciens modeles
                    ViewFile.radProfilVertical.setSelection(false);
                    ViewFile.radSerieTempo.setSelection(true);
                    ViewFile.radTrajectoire.setSelection(false);
                }
                else
                {
                    ViewFile.radProfilVertical.setSelection(true);
                    ViewFile.radSerieTempo.setSelection(false);
                    ViewFile.radTrajectoire.setSelection(false);
                }
            }
    
            // séparateur
            String strSeparator = stationModel_modele.getSeparator();
            
            // pour prise en compte des anciens modèles (par défaut, pas de séparateur)
            if (strSeparator == null) strSeparator = Global.EMPTY_STRING;
    
            boolean blnSeparator =
                !Global.EMPTY_STRING.equals(strSeparator);
    
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
                else if (Global.COMMA
                    .equals(strSeparator))
                {
                    // Virgule
                    ViewFile.radioTabulation.setSelection(false);
                    ViewFile.radioPointVirgule.setSelection(false);
                    ViewFile.radioVirgule.setSelection(true);
                    ViewFile.radioEspace.setSelection(false);
                    ViewFile.radioSeparateurAutre.setSelection(false);
                    ViewFile.textSeparateur.setText(Global.EMPTY_STRING);
                }
                else if (Global.SPACE
                    .equals(strSeparator))
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
    
    
            // test si serie tempo
            if (stationModel_modele.getTimeSeriesInformation() != null)
            {
                // ViewFile.groupTempo.setVisible(true);
    
                // heure et date de ref
    
                if (inputModel_modele.isRefDateBased())
                {
                    // date de ref / heure de ref
    
                    // reconversion de LONG vers format normal
                    Formatter formatter = new Formatter();
    
                    try
                    {
                        ViewFile.textTempoDateRef.setText(formatter.formatDateTime(
                            "dd/MM/yyyy", inputModel_modele.getRefDate())); //$NON-NLS-1$
                    }
                    catch (ParseException e)
                    {
    
                        e.printStackTrace();
                    }
                    // le format de la comboList correspondant est en 0
                    ViewFile.comboTempoDate.select(0);
    
                    // reconversion de LONG vers format normal
                    formatter = new Formatter();
    
                    try
                    {
                        ViewFile.textTempoHeureRef.setText(formatter
                            .formatDateTime("hh24:mm", inputModel_modele //$NON-NLS-1$
                                .getRefTime()));
                    }
                    catch (ParseException e)
                    {
    
                        e.printStackTrace();
                    }
                    // le format de la comboList correspondant est en 0
                    ViewFile.comboTempoHeure.select(0);
    
                }
                else
                {
                    // pas de date de ref
    
                }
    
            }
    
            // nombre lignes entete station
    
            EditDesc.sectionEnteteStation.setValue(Global.EMPTY_STRING 
                    + stationModel_modele.getNumberOfLines());
    
            // nombre de lignes de fin
            EditDesc.sectionFinStation.setValue(Global.EMPTY_STRING 
                    + stationModel_modele.getEndData()); 
    
            // metaInData
            // stationModel.setMetaInData(false);
    
            // pattern de fin fourni ou pas
    
            // section EndData
            FileSection endData = EditDesc.sectionRuptureDonnees;
            if (stationModel_modele.isEndOnEOF()){
                endData.selectEndData(endData.getRadEOF());
            }else if (stationModel_modele.isEndOnEmptyLine()){
                endData.selectEndData(endData.getRadLigne());
            }else if (stationModel_modele.isEndOnPattern()){
                endData.selectEndData(endData.getRadManuel());
                endData.setValue(stationModel_modele.getEndDataPattern());
            }
    
            // refresh de la zone d'edition
            EditDesc.razEdition();
            EditDesc.DrawingBack();
    
            DataModel.ouverture = true;
    
            // desactivation option serie tempo
            ViewFile.radProfilVertical.setEnabled(true);
            ViewFile.radSerieTempo.setEnabled(true);
            ViewFile.radTrajectoire.setEnabled(true);
        }catch (Exception e){
            Utils.messageErreurAppend(Messages.getString("Etape.124")); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

    public static void fillSurvey()
    {
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
        EditSurvey.textCampagne.setText(survey.getReference());
        EditSurvey.textNom.setText(survey.getName());
        Utils.selectComboCodePlus(EditSurvey.comboNavire, survey.getShipCode(),
            survey.getShipName());

        // Localisation

        // DATES
        Formatter formatter = new Formatter();

        try
        {
            EditSurvey.textDateDebut.setText(formatter.formatDateTime(
                "dd/MM/yyyy", survey.getStartDate())); //$NON-NLS-1$
        }
        catch (ParseException e)
        {

            e.printStackTrace();
        }

        formatter = new Formatter();

        try
        {
            EditSurvey.textDateFin.setText(formatter.formatDateTime(
                "dd/MM/yyyy", survey.getEndDate())); //$NON-NLS-1$
        }
        catch (ParseException e)
        {

            e.printStackTrace();
        }

        Utils.selectComboLibelle(EditSurvey.comboRegion, survey.getRegionName());
        // EditSurvey.textRegion.setText(survey.getRegionName());

        // Source de donnees
        Utils.selectComboCode(EditSurvey.comboCountries, survey
            .getCountryCode());
        EditSurvey.textLabo.setText(survey.getLaboratories());
        EditSurvey.textChef.setText(survey.getChiefScientist());
        EditSurvey.textProjet.setText(survey.getProjectName());

        // Centre d'archivage
        Utils.selectComboCode(EditSurvey.comboDataCentres, survey
            .getDataCentre());

        // confidentialite
        String code = survey.getAvailabilityCode();
        if (code.equals("P")) //$NON-NLS-1$
        {
            EditSurvey.comboConfid.select(0);
        }
        if (code.equals("L")) //$NON-NLS-1$
        {
            EditSurvey.comboConfid.select(1);
        }
        if (code.equals("C")) //$NON-NLS-1$
        {
            EditSurvey.comboConfid.select(2);
        }

        // Commentaire
        EditSurvey.textCommentaire.setText(survey.getComment());

        // Remplissage de la liste des dataTypes de la campagne

        DataType[] dataTypes = survey.getDataTypes();
        int taille = dataTypes.length;

        for (int i = 0; i < taille; i++)
        {
            TypeSurvey typeSurvey = new TypeSurvey();
            typeSurvey.setCode(dataTypes[i].getCode());
            typeSurvey.setNombre(dataTypes[i].getNumberOfProfiles());

            // QC
            if (dataTypes[i].isQc())
            {
                typeSurvey.setQc("Y"); //$NON-NLS-1$
            }
            else
            {
                typeSurvey.setQc("N"); //$NON-NLS-1$
            }
            // typeSurvey.setQc(Global.EMPTY_STRING+dataTypes[i].isQc());

            typeSurvey.setSensor(dataTypes[i].isSensor());

            TableDataSurvey.types.add(typeSurvey);

            // System.out.println("TypeSurvey " +typeSurvey.getCode()+"
            // ajoute");

        }

        EditSurvey.razEdition();
        EditSurvey.DrawingBack();

        // ////////////////////////////////////////
        // refresh de la liste des dataTypes
        TableDataSurvey.razTableData();
        TableDataSurvey.creationTable();
        Composite c = EditSurvey.sectionType;
        while (c != null)
        {
            c.setRedraw(false);
            c = c.getParent();
            if (c instanceof ScrolledForm)
            {
                break;
            }
        }
        c = EditSurvey.sectionType;
        while (c != null)
        {
            c.layout(true);
            c = c.getParent();
            if (c instanceof ScrolledForm)
            {
                ((ScrolledForm) c).reflow(true);
                break;
            }
        }
        c = EditSurvey.sectionType;
        while (c != null)
        {
            c.setRedraw(true);
            c = c.getParent();
            if (c instanceof ScrolledForm)
            {
                break;
            }
        }

        // ////////////////////////////////////////

        // System.out.println("Fin remplissage onglet Survey");

    }

    public static void fillStation()
    {
        for (NemoSection stationSection : EditStation.listeSectionsStation){
            stationSection.fillFromModel(stationModel_modele);
        }
        
        // ENDDATETIME flag !!
        if(!stationModel_modele.getInputType().equals(Global.PROFILE)){
            PropertyModel propertyModel = stationModel_modele.getTimeSeriesModel().propertiesAt(NemoSection.S_EDATE);
            EditStation.qcFlags.setFlagFragment(propertyModel.getFlag(), Global.ENDDATETIME, Messages.getString(Global.ENDDATETIME).trim());
        }
        
    }

    public static void fillBackStation()
    {
        for (NemoSection stationSection : EditStation.listeSectionsStation){
            stationSection.fillFromModel(stationModel);
        }
        
        // ENDDATETIME flag !!
        if(!stationModel.getInputType().equals(Global.PROFILE)){
            PropertyModel propertyModel = stationModel.getTimeSeriesModel().propertiesAt(NemoSection.S_EDATE);
            EditStation.qcFlags.setFlagFragment(propertyModel.getFlag(), Global.ENDDATETIME, Messages.getString(Global.ENDDATETIME).trim());
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
            typeData.setCode(modeleCourant.getCode() + " - " //$NON-NLS-1$
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
            typeData.setEntree(Global.EMPTY_STRING + paraCourant.getInputDefault());
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
