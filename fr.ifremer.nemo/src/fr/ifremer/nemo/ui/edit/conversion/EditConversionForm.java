package fr.ifremer.nemo.ui.edit.conversion;

import java.io.File;
import java.util.Arrays;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.ui.ApplicationActionBarAdvisor;
import fr.ifremer.nemo.ui.SWTProgress;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.ws.WSDataType;

public class EditConversionForm extends EditForm
{
    public Button buttonConversion;

    //public String nomFichierSortie, nomDossierSortie;
    public ProgressNotification notifier;

    //private String _dataSetRef;

    /**
     * Nom du fichier lu à retourner en cas d'erreur
     */
    protected String _erreurFichier;

    /**
     * Nombre de lignes lues à retourner en cas d'erreur
     */
    protected int _erreurLigne;

    /**
     * Chaine lue à retourner en cas d'erreur
     */
    protected String _erreurChaine;

    /**
     * Nombre de lignes du fichier lu
     */
    protected int _totalLigne;

    /**
     * Liste des stations triees
     */
    /*protected LinkedHashMap<String, Integer> _sortedStations =
        new LinkedHashMap<String, Integer>();*/

    /**
     * messages d'avertissement de formats grisés car données non conformae
     */
    private String warningFile = Global.EMPTY_STRING;
    private String warningCampagne = Global.EMPTY_STRING;
    private String warningStation = Global.EMPTY_STRING;
    private String warningData = Global.EMPTY_STRING;


    /*
     * Methodes de création de l'onglet (héritées de EditForm)
     */
    
    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);
        
        //nomFichierSortie = Global.EMPTY_STRING;
        //nomDossierSortie = Global.EMPTY_STRING;

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_convert_title);

        // bouton conversion
        buttonConversion = new Button(formEdition.getBody(), SWT.NONE);
        buttonConversion.setBackground(ViewMain.backgroungEdit);
        buttonConversion.setText(Messages.button_convert);
        buttonConversion
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    try{
                        // Test si la config est bien remplie
                        if (!checkConfig()){
                            throw new Exception("CDI cancelled"); //$NON-NLS-1$
                        }

                        // RaZ
                        Nemo.getInstance().getDomain().getCruise().setDataSetRef(Global.EMPTY_STRING);
                        Nemo.getInstance().getDomain().getMedia().initOutput();
                        Nemo.getInstance().getDomain().getConversion().setNomMappingHorsPrefix(Global.EMPTY_STRING);
                        Utils.messageErreur(Global.EMPTY_STRING);
                        // -- du numéro de station incrémenté
                        //DataModel.stationModel.resetRef();
                        // -- de la liste des stations triées
                        //getSortedStations().clear();

                        // *** Ajouts automatiques de certains paramètres
                        //DataModel.stationModel.setAddedParameterModel();

                        // type de la conversion
                        E_TypeConversion currentConversion = ViewMain.getSelectedConversion();
                        Nemo.getInstance().getDomain().getConversion().setCurrentConversion(currentConversion);
                        
                        //Conversion formatConversion = Conversion.MEDATLAS;
                        //if (ViewMain.radODV.getSelection()){
                        //    formatConversion = Conversion.ODV;
                        //}
                        //NemoLists.writeSdnParameters(formatConversion);

                        if (ViewMain.editFileForm.okApresReset
                                && ViewMain.editCruiseForm.okApresReset
                                && ViewMain.editStationForm.okApresReset
                                && ViewMain.editDataForm.okApresReset){

                            if ((currentConversion != E_TypeConversion.MEDALAS)
                                    && (!ViewMain.collectionCas1)){
                                if (!askDataSetRef()){
                                    throw new Exception("Cancelled"); //$NON-NLS-1$
                                }
                            }

                            // On demande les infos pour faire le résumé CDI,
                            // qui sera fait plus tard
                            if (Nemo.getInstance().getConfig().isWriteCdi()){
                                CDIResumeWriter.getInstance().reset();
                                CDIResumeWriter.getInstance().choixFichierSortie();
                            }

                            // test pour savoir si on est en campagne ou pas
                            if (ViewMain.isCollection()){
                                DataType dt = askDataType();
                                Nemo.getInstance().getDomain().getCruise().getStation().getModel().setDataType(dt);
                                choixDossierSortie();
                            }
                            else if (currentConversion == E_TypeConversion.ODV_MONOSTATION){
                                choixDossierSortie();
                            }
                            else{
                                choixFichierSortie();
                            }

                            definePrefixDossier();

                            // affichage des résultats en fin de conversion
                            afficheResultat(runSWTProgress());
                        }
                        else {
                            Utils.messageErreur(Messages.error_convert_invalideSteps);
                        }
                    }
                    catch (Exception ex){
                        Utils.messageErreur(Messages.error_convert_cancel);
                    }
                }
            });
    }
    
    @Override
    protected void initSections(){
        // pas de section dans cet onglet
    }

    @Override
    public void afficheEdition()
    {
        super.afficheEdition();
        
        ViewLog.aideConversion();
        
        // FAE 1143 : déactivation MEDATLAS
        StringBuffer msg = new StringBuffer();
        msg.append(Global.RN);
        msg.append(warningFile);
        msg.append(warningCampagne);
        msg.append(warningStation);
        msg.append(warningData);
        Utils.messageErreur(msg.toString());

        // FAE 1143 : déactivation MEDATLAS
        boolean medOK = true;
        if (!warningFile.equals(Global.EMPTY_STRING)
                || !warningCampagne.equals(Global.EMPTY_STRING)
                || !warningStation.equals(Global.EMPTY_STRING)
                || !warningData.equals(Global.EMPTY_STRING))
        {
            medOK = false;
        }
        ViewMain.enableMedatlas(medOK);

        loaded = true;
        ViewMain.affiche = 4;

        formEdition.reflow(true);
    }

    @Override
    public void drawingBack()
    {
       
        super.drawingBack();
        
        ViewLog.aideConversion();
        ViewMain.affiche = 4;
    }
    
    @Override
    public void actualiseEdition(){
        // pas d'actualisation nécessaire pour cet onglet
    }
    
    @Override
    public void razText(){
        // on rend sélectionnables les radios ODV et Medatlas dans
        // l'onglet Conversion
        if (ViewMain.radMonoFichier != null){
            ViewMain.radMedatlas.setEnabled(true);
            ViewMain.radMedatlas.setSelection(true);
            ViewMain.radODV.setEnabled(true);
            ViewMain.radODV.setSelection(false);
            ViewMain.radMonoStation.setEnabled(true);
            ViewMain.radMonoStation.setSelection(true);
            ViewMain.radMonoFichier.setEnabled(true);
            ViewMain.radMonoFichier.setSelection(false);
            ViewMain.afficheGrpODV();
        }
    }

    @Override
    public void disposeElements()
    {
        if (buttonConversion != null)
            buttonConversion.dispose();
    }

    public void invalidEtape()
    {
        super.invalidEtape(4);
    }
    

    /*
     * Méthodes de traitement du lancement de la conversion
     */
    
    public void choixFichierSortie()
    {
        String nomFichierSortie = Global.EMPTY_STRING;

        FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                           SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setText(Messages.edit_convert_outputFilename);

        // FAE090
        if (ViewMain.radODV.getSelection())
        {
            String[] extensions = new String[1];
            extensions[0] = Global.STAR + Global.EXT_TXT;
            dialog.setFilterExtensions(extensions);
/*
            Survey survey = null;
            if (DataModel.singleSurvey != null){
                survey = DataModel.singleSurvey.getModel();
            }
            else if (DataModel.dirSurvey != null){
                survey = DataModel.dirSurvey.getModel();
            }
            

            if (survey != null){
                String fileName = survey.getReference().trim();
                if (fileName.equals(Global.EMPTY_STRING)){
                    fileName = survey.getName().trim();
                }
                fileName = Utils.validFileName(fileName);
                dialog.setFileName(fileName);
            }
*/
            
            CruiseModel cruiseModel = Nemo.getInstance().getDomain().getCruise().getModel();
            String fileName = cruiseModel.getReference().trim();
            if (fileName.equals(Global.EMPTY_STRING)){
                fileName = cruiseModel.getName().trim();
            }
            fileName = Utils.validFileName(fileName);
            dialog.setFileName(fileName);
        }

        // FAE45
        if (ViewMain.radioFichier.getSelection()){
            dialog.setFilterPath(ViewMain.nomFichier);
        }
        else{
            dialog.setFilterPath(ViewMain.nomRepertoire.substring(0,
                ViewMain.nomRepertoire.lastIndexOf(File.separator)));
        }

        nomFichierSortie = dialog.open();

        if (nomFichierSortie != null){
            String nomDossierSortie = Utils.extraitCheminFichier(nomFichierSortie);

            // Teste si le repertoire de sortie est different du repertoire
            // d'entree
            if (ViewMain.radioFichier.getSelection()){
                if (nomFichierSortie.equals(ViewMain.nomFichier)){
                    MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                   SWT.ICON_ERROR | SWT.OK | SWT.APPLICATION_MODAL);
                    mb.setMessage(Messages.error_convert_outputFile);
                    mb.open();

                    choixFichierSortie();
                }
            }
            else{
                if (nomDossierSortie.equals(ViewMain.nomRepertoire)){
                    MessageBox mb =  new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                    SWT.ICON_ERROR | SWT.OK | SWT.APPLICATION_MODAL);
                    mb.setMessage(Messages.error_convert_outputDir);
                    mb.open();

                    choixFichierSortie();
                }
            }

            File fich = new File(nomFichierSortie);
            if (fich.exists()){
                // fichier existe
                MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                                               SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.confirm_eraseFile);

                int reponse = mb.open();
                if (reponse == SWT.NO){
                    // on n'écrase pas le fichier
                    choixFichierSortie();
                }
            }
            Nemo.getInstance().getDomain().getMedia().setOutputFileName(nomFichierSortie);
        }
    }

    public void choixDossierSortie()
    {
        //String nomFichierSortie = Global.EMPTY_STRING;
        String nomDossierSortie = Global.EMPTY_STRING;
        
        DirectoryDialog dialog = new DirectoryDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                     SWT.SAVE | SWT.APPLICATION_MODAL);
        dialog.setText(Messages.edit_convert_outputDir);

        if (ViewMain.radioFichier.getSelection()){
            dialog.setFilterPath(ViewMain.nomRepertoire);
        }
        else {
            dialog.setFilterPath(ViewMain.nomRepertoire.substring(0,
                ViewMain.nomRepertoire.lastIndexOf(File.separator)));
        }

        nomDossierSortie = dialog.open();

        if (nomDossierSortie != null) {
            // Teste si le répertoire de sortie est différent du répertoire
            // d'entrée
            if (!ViewMain.radioFichier.getSelection()
                    && nomDossierSortie.equals(ViewMain.nomRepertoire)) {
                MessageBox mb =  new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                SWT.ICON_ERROR | SWT.OK | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.error_convert_outputDir);
                mb.open();

                choixDossierSortie();
            }

            File dir = new File(nomDossierSortie);
            if (dir.exists() && dir.listFiles().length != 0) {
                // fichier existe
                MessageBox mb = new MessageBox(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                               SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.confirm_useDir);

                int reponse = mb.open();
                if (reponse == SWT.NO) {
                    // on n'écrase pas le fichier
                    choixDossierSortie();
                }
            }
            Nemo.getInstance().getDomain().getMedia().setOutputFileName(nomDossierSortie);
        }
    }

    public boolean runSWTProgress()
    {
        Utils.messageErreur(Global.EMPTY_STRING);
        try {
            new SWTProgress().run();
            return true;
        }
        catch (Exception e) {
            Utils.messageErreur(e.getMessage());
            return false;
        }
    }

    public void afficheResultat(boolean resultOK)
    {
        if (resultOK) {
            if (ViewMain.radODV.getSelection()) {
                Utils.messageOK(Messages.edit_convert_endODV);
            }
            else {
                Utils.messageOK(Messages.edit_convert_endMED);
            }
        }
        else {
            Utils.messageErreurAppend(messageErreur());
        }

        // TODO Stations triées
        /*
        if (getSortedStations().size() > 0) {
            String sortedMessage =
                "\n\n" + getSortedStations().size() + Global.SPACE //$NON-NLS-1$
                        + Messages.edit_convert_sortStations + Global.SPACE;

            for (Entry<String, Integer> currentEntry : getSortedStations()
                .entrySet()) {
                sortedMessage += currentEntry.getKey() + ", "; //$NON-NLS-1$
            }

            int last = sortedMessage.lastIndexOf(","); //$NON-NLS-1$
            if (last > 0) {
                sortedMessage = sortedMessage.substring(0, last);
            }
            Utils.messageOKAppend(sortedMessage);
        }
        */
    }

    public String getErreurFichier()
    {
        return _erreurFichier;
    }

    public void setErreurFichier(String fichier)
    {
        _erreurFichier = fichier;
    }

    public int getErreurLigne()
    {
        return _erreurLigne;
    }

    public void setErreurLigne(int ligne)
    {
        _erreurLigne = ligne;
    }

    public void incrementErreurLigne()
    {
        _erreurLigne++;
    }

    public String getErreurChaine()
    {
        return _erreurChaine;
    }

    public void setErreurChaine(String chaine)
    {
        _erreurChaine = chaine;
    }

    public String messageErreur()
    {
        String message = Global.EMPTY_STRING;

        if (getErreurFichier() != null)  {
            if (!getErreurFichier().trim().equals(Global.EMPTY_STRING)) {
                message += Messages.label_file + "\t : \t" //$NON-NLS-1$
                        + getErreurFichier() + Global.RN;
            }
        }

        if (getErreurLigne() != 0) {
            if (getErreurLigne() <= Nemo.getInstance().getDomain().getMedia().getStationHeaderNumberLines()) {
                // Si l'erreur se trouve dans l'en-tête de station, la ligne
                // indiquée
                // n'est pas forcément la bonne, il vaut mieux ne pas l'indiquer
                return Global.EMPTY_STRING;
            }
            message += Messages.label_line + "\t : \t" //$NON-NLS-1$
                    + getErreurLigne() + Global.RN;
        }

        if (getErreurChaine() != null) {
            if (!getErreurChaine().trim().equals(Global.EMPTY_STRING)) {
                message += Messages.label_string + "\t : \t" //$NON-NLS-1$
                        + getErreurChaine();
            }
        }

        if (!message.equals(Global.EMPTY_STRING)) {
            message =
                Global.RN + Messages.error_convert_position + Global.RN
                        + message;
        }
        return message;
    }

    /*public String getDataSetRef()
    {
        return _dataSetRef;
    }

    public void setDataSetRef(String value)
    {
        _dataSetRef = value;
    }*/

    public boolean askDataSetRef()
    {
        boolean asDataSetRef = false;
        CruiseDomain cruiseDomain = Nemo.getInstance().getDomain().getCruise();
        if (cruiseDomain.getDataSetRef().equals(Global.EMPTY_STRING)) {
            String strDefault = Global.EMPTY_STRING;
            strDefault = cruiseDomain.getModel().getReference();
            if (strDefault.equals(Global.EMPTY_STRING))
                strDefault = cruiseDomain.getModel().getName();
            
            /*
            if (DataModel.dirSurvey != null) {
                strDefault = DataModel.dirSurvey.getModel().getReference();
                if (strDefault.equals(Global.EMPTY_STRING))
                    strDefault = DataModel.dirSurvey.getModel().getName();
            }
            else if (DataModel.singleSurvey != null){
                strDefault = DataModel.singleSurvey.getModel().getReference();
                if (strDefault.equals(Global.EMPTY_STRING))
                    strDefault = DataModel.singleSurvey.getModel().getName();
            }
            */
            InputDialog inputDataSetRef =
                new InputDialog(PlatformUI.getWorkbench().getDisplay()
                    .getActiveShell(), Messages.CDIResumeWriter_idTitle,
                    Messages.CDIResumeWriter_idMessage, strDefault, null);
            inputDataSetRef.open();

            if (inputDataSetRef.getReturnCode() == Window.OK) {
                cruiseDomain.setDataSetRef(inputDataSetRef.getValue());
                asDataSetRef = true;
                //_dataSetRef = inputDataSetRef.getValue();
                //return true;
            }
            else {
                cruiseDomain.setDataSetRef(Global.EMPTY_STRING);
                asDataSetRef = false;
                //_dataSetRef = Global.EMPTY_STRING;
                //return false;
            }
        }
        else{
            asDataSetRef = true;
            //return true;
        }
        return asDataSetRef;
    }

    public void definePrefixDossier()
    {
        if (Nemo.getInstance().getConfig().isWriteMapping()) {
            File output = new File(Nemo.getInstance().getDomain().getMedia().getOutputFileName());
            String cheminComplet = output.getParentFile().getPath();
            
            //Boolean modeDossier = false;

            //if (nomFichierSortie.isEmpty()) {
            //    cheminComplet = nomDossierSortie;
            //    modeDossier = true;
            //}
            //else {
            //    cheminComplet = nomFichierSortie;
            //}

            InputDialog inputPrefixDossier =
                new InputDialog(PlatformUI.getWorkbench().getDisplay()
                    .getActiveShell(), Messages.InputPrefixDossier_idTitle,
                    Messages.InputPrefixDossier_idMessage, cheminComplet,
                    null);
            inputPrefixDossier.open();
            String nomHorsPrefix = Global.EMPTY_STRING;
            if (inputPrefixDossier.getReturnCode() == Window.OK) {
                if (inputPrefixDossier.getValue().endsWith(File.separator)
                        || inputPrefixDossier.getValue().equals(cheminComplet)) {
                    nomHorsPrefix =
                        cheminComplet.replace(inputPrefixDossier.getValue(),
                            Global.EMPTY_STRING);
                }
                else {
                    nomHorsPrefix =
                        cheminComplet.replace(inputPrefixDossier.getValue()
                                + File.separator, Global.EMPTY_STRING);
                }
            }

            // On affiche la boite de dialogue tant que l'utilisateur saisie une
            // donnée incorrecte
            if (!cheminComplet.endsWith(nomHorsPrefix)) {
                definePrefixDossier();
            }
            Nemo.getInstance().getDomain().getConversion().setNomMappingHorsPrefix(nomHorsPrefix);
        }
    }

    public DataType askDataType()
    {
        ListDialog ld = new ListDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
        ld.setAddCancelButton(true);
        ld.setContentProvider(new ArrayContentProvider());
        ld.setLabelProvider(new LabelProvider());

        ld.setInput(Arrays.asList(NemoLists.getDataTypesIsSensor()));
        ld.setTitle(Messages.askDataType_Title);
        ld.open();
        WSDataType result = (WSDataType) ld.getResult()[0];

        DataType dt = new DataType(result.getCode(), true, result.isSensor());

        return dt;
    }

    public boolean checkConfig()
    {
        boolean blnOK = true;
        String message = Messages.CDIResumeWriter_configMessage;

        if (Nemo.getInstance().getConfig().getIdEdmoRespCdi().trim().equals(
            Global.EMPTY_STRING)) {
            message += Global.RN + Global.SEP + Messages.settings_EDMOauthor;
            blnOK = false;
        }

        if (Nemo.getInstance().getConfig().isWriteCdi()) {
            if (Nemo.getInstance().getConfig().getCodeEdmoDonnees().trim().equals(Global.EMPTY_STRING)) {
                message += Global.RN + Global.SEP + Messages.settings_EDMOori;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getIdEdmoRespDonnees().trim().equals(Global.EMPTY_STRING)) {
                message += Global.RN + Global.SEP + Messages.settings_EDMOcustodian;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getIdEdmoDistDonnees().trim().equals(Global.EMPTY_STRING)) {
                message += Global.RN + Global.SEP + Messages.settings_EDMOdist;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getCdiURL().trim().equals(Global.EMPTY_STRING)) {
                message +=  Global.RN + Global.SEP + Messages.settings_dataDist_web;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getCdiMethod().trim().equals(Global.EMPTY_STRING)) {
                message +=  Global.RN + Global.SEP + Messages.settings_dataDist_met;
                blnOK = false;
            }
        }

        if (!blnOK) {
            try {
                String[] buttons = new String[1];
                buttons[0] = Messages.success;
                MessageDialog dialog =
                    new MessageDialog(PlatformUI.getWorkbench().getDisplay()
                        .getActiveShell(), Messages.error_missing, null,
                        message, MessageDialog.NONE, buttons, 0);
                dialog.open();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            ApplicationActionBarAdvisor.optionsAction.run();

            return false;
        }
        return true;
    }

    public int getTotalLigne()
    {
        return _totalLigne;
    }

    public void setTotalLigne(int ligne)
    {
        _totalLigne = ligne;
    }

    /*public LinkedHashMap<String, Integer> getSortedStations()
    {
        return _sortedStations;
    }

    public void setSortedStations(LinkedHashMap<String, Integer> stations)
    {
        _sortedStations = stations;
    }*/

    /**
     * Setter sur warningCampagne.
     * 
     * @param warningCampagne
     *            the warningCampagne to set
     */
    public void setWarningCampagne(String warningCampagne)
    {
        this.warningCampagne = warningCampagne;
    }

    /**
     * Setter sur warningStation.
     * 
     * @param warningStation
     *            the warningStation to set
     */
    public void setWarningStation(String warningStation)
    {
        this.warningStation = warningStation;
    }

    /**
     * Setter sur warningData.
     * 
     * @param warningData
     *            the warningData to set
     */
    public void setWarningData(String warningData)
    {
        this.warningData = warningData;
    }

    /**
     * Setter sur warningFile.
     * 
     * @param warningFile
     *            the warningFile to set
     */
    public void setWarningFile(String warningFile)
    {
        this.warningFile = warningFile;
    }

    /**
     * Getter sur warningCampagne.
     * 
     * @return the warningCampagne
     */
    public String getWarningCampagne()
    {
        return warningCampagne;
    }

    /**
     * Getter sur warningData.
     * 
     * @return the warningData
     */
    public String getWarningData()
    {
        return warningData;
    }

    /**
     * Getter sur warningFile.
     * 
     * @return the warningFile
     */
    public String getWarningFile()
    {
        return warningFile;
    }

    /**
     * Getter sur warningStation.
     * 
     * @return the warningStation
     */
    public String getWarningStation()
    {
        return warningStation;
    }
}
