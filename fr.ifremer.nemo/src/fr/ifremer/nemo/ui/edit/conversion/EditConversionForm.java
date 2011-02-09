package fr.ifremer.nemo.ui.edit.conversion;

import java.io.File;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.ui.ApplicationActionBarAdvisor;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.SWTProgress;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.conversion.listeners.ConversionSelectionListener;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditConversionForm extends EditForm
{
    private Button buttonConversion;

    public ProgressNotification notifier;


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
    
    // Résumé des infos de conversion
    private Text conversionSummary;


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
        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_convert_title);

        createSummary();
        
       
       
       
       
        // bouton conversion
        buttonConversion = new Button(formEdition.getBody(), SWT.NONE);
        buttonConversion.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        buttonConversion.setText(Messages.button_convert);
        
        ConversionSelectionListener listener = new ConversionSelectionListener();
        buttonConversion.addSelectionListener(listener);
        
    }

    @Override
    protected void initSections()
    {
        // pas de section dans cet onglet
    }

    @Override
    public void afficheEdition()
    {
        super.afficheEdition();
        
        // activation du bouton, par defaut
        buttonConversion.setEnabled(true);
        
        ViewLog.aideConversion();

        StringBuffer msg = new StringBuffer();
        msg.append(Global.RN);
        msg.append(warningFile);
        msg.append(warningCampagne);
        msg.append(warningStation);
        msg.append(warningData);
        Utils.messageErreur(msg.toString());

        // FAE 1143 : déactivation MEDATLAS
      // adaptation du code suite deplacement conversion dans 1er onglet (FAE 5683)
       if (Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToMedatlas())
        {
            boolean medOK = true;
            if (!warningFile.equals(Global.EMPTY_STRING)
                    || !warningCampagne.equals(Global.EMPTY_STRING)
                    || !warningStation.equals(Global.EMPTY_STRING)
                    || !warningData.equals(Global.EMPTY_STRING))
            {
                medOK = false;
            }
            buttonConversion.setEnabled(medOK);
        }
        setLoaded(true);
        Nemo.getInstance().getViewMain().setAffiche(4);

        formEdition.reflow(true);
        formEdition.redraw();
    }

    @Override
    public void drawingBack()
    {

        super.drawingBack();

        ViewLog.aideConversion();
        Nemo.getInstance().getViewMain().setAffiche(4);
    }

    @Override
    public void actualiseEdition()
    {
        // pas d'actualisation nécessaire pour cet onglet
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

    @Override
    public void initFromNewModel()
    {
    }
    /*
     * Méthodes de traitement du lancement de la conversion
     */

    public boolean choixFichierSortie()
    {
        
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        String nomFichierSortie = Global.EMPTY_STRING;

        FileDialog dialog =
            new FileDialog(Utils.getMainShell(), SWT.SAVE
                    | SWT.APPLICATION_MODAL);
        dialog.setText(Messages.edit_convert_outputFilename);
      
        // FAE090
          if (Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToODV())  
        {
            String[] extensions = new String[1];
            extensions[0] = Global.STAR + Global.EXT_TXT;
            dialog.setFilterExtensions(extensions);

            CruiseModel cruiseModel =
                Nemo.getInstance().getDomain().getCruise().getModel();
            String fileName = cruiseModel.getReference().trim();
            if (fileName.equals(Global.EMPTY_STRING))
            {
                fileName = cruiseModel.getName().trim();
            }
            fileName = fr.ifremer.nemo.util.Utils.validFileName(fileName);
            dialog.setFileName(fileName);
        }

        // FAE45
        if (editFileForm.isCruiseIsAFile())
        {
            dialog.setFilterPath(editFileForm.nomFichier);
        }
        else
        {
            dialog.setFilterPath(editFileForm.nomRepertoire.substring(0,
                editFileForm.nomRepertoire.lastIndexOf(File.separator)));
        }

        nomFichierSortie = dialog.open();

        if (nomFichierSortie != null)
        {
            String nomDossierSortie =
                fr.ifremer.nemo.util.Utils.extraitCheminFichier(nomFichierSortie);

            // Teste si le repertoire de sortie est different du repertoire
            // d'entree
            if (editFileForm.isCruiseIsAFile())
            {
                if (nomFichierSortie.equals(editFileForm.nomFichier))
                {
                    MessageBox mb =
                        new MessageBox(Utils.getMainShell(), SWT.ICON_ERROR
                                | SWT.OK | SWT.APPLICATION_MODAL);
                    mb.setMessage(Messages.error_convert_outputFile);
                    mb.open();

                    choixFichierSortie();
                }
            }
            else
            {
                if (nomDossierSortie.equals(editFileForm.nomRepertoire))
                {
                    MessageBox mb =
                        new MessageBox(Utils.getMainShell(), SWT.ICON_ERROR
                                | SWT.OK | SWT.APPLICATION_MODAL);
                    mb.setMessage(Messages.error_convert_outputDir);
                    mb.open();

                    choixFichierSortie();
                }
            }

            File fich = new File(nomFichierSortie);
            if (fich.exists())
            {
                // fichier existe
                MessageBox mb =
                    new MessageBox(Utils.getMainShell(), SWT.ICON_WARNING
                            | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.confirm_eraseFile);

                int reponse = mb.open();
                if (reponse == SWT.NO)
                {
                    // on n'écrase pas le fichier
                    choixFichierSortie();
                }
            }
            Nemo.getInstance().getDomain().getMedia().setOutputFileName(
                nomFichierSortie);
        }
        else
        {
            return false;
        }
        return true;
    }

    public boolean choixDossierSortie()
    {
        
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        
        // String nomFichierSortie = Global.EMPTY_STRING;
        String nomDossierSortie = Global.EMPTY_STRING;

        DirectoryDialog dialog =
            new DirectoryDialog(Utils.getMainShell(), SWT.SAVE
                    | SWT.APPLICATION_MODAL);
        dialog.setText(Messages.edit_convert_outputDir);

        if (editFileForm.isCruiseIsAFile())
        {
            dialog.setFilterPath(editFileForm.nomRepertoire);
        }
        else
        {
            dialog.setFilterPath(editFileForm.nomRepertoire.substring(0,
                editFileForm.nomRepertoire.lastIndexOf(File.separator)));
        }

        nomDossierSortie = dialog.open();

        if (nomDossierSortie != null)
        {
            // Teste si le répertoire de sortie est différent du répertoire
            // d'entrée
            if (!editFileForm.isCruiseIsAFile()
                    && nomDossierSortie.equals(editFileForm.nomRepertoire))
            {
                MessageBox mb =
                    new MessageBox(Utils.getMainShell(), SWT.ICON_ERROR
                            | SWT.OK | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.error_convert_outputDir);
                mb.open();

                choixDossierSortie();
            }

            File dir = new File(nomDossierSortie);
            if (dir.exists() && dir.listFiles().length != 0)
            {
                // fichier existe
                MessageBox mb =
                    new MessageBox(Utils.getMainShell(), SWT.ICON_WARNING
                            | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
                mb.setMessage(Messages.confirm_useDir);

                int reponse = mb.open();
                if (reponse == SWT.NO)
                {
                    // on n'écrase pas le fichier
                    choixDossierSortie();
                }
            }
            Nemo.getInstance().getDomain().getMedia().setOutputFileName(
                nomDossierSortie);
        }
        else
        {
            return false;
        }

        return true;
    }

    public boolean runSWTProgress()
    {
        Utils.messageErreur(Global.EMPTY_STRING);
        try
        {
            new SWTProgress().run();
            return true;
        }
        catch (Exception e)
        {
            Utils.messageErreur(e.getMessage());
            Utils.messageErreurAppend(Nemo.getInstance().getErrorMessageWithFileAndLineNumber());
            return false;
        }
    }

    public void afficheResultat(boolean resultOK)
    {
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        if (resultOK)
        {
            if (Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToODV())  
            {
                Utils.messageOK(Messages.edit_convert_endODV);
            }
            else
            {
                Utils.messageOK(Messages.edit_convert_endMED);
            }
        }
        else
        {
            Utils.messageErreurAppend(messageErreur());
        }

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

        if (getErreurFichier() != null)
        {
            if (!getErreurFichier().trim().equals(Global.EMPTY_STRING))
            {
                message += Messages.label_file + "\t : \t" //$NON-NLS-1$
                        + getErreurFichier() + Global.RN;
            }
        }

        if (getErreurLigne() != 0)
        {
            if (getErreurLigne() <= Nemo.getInstance().getDomain().getMedia()
                .getStationHeaderNumberLines())
            {
                // Si l'erreur se trouve dans l'en-tête de station, la ligne
                // indiquée
                // n'est pas forcément la bonne, il vaut mieux ne pas l'indiquer
                return Global.EMPTY_STRING;
            }
            message += Messages.label_line + "\t : \t" //$NON-NLS-1$
                    + getErreurLigne() + Global.RN;
        }

        if (getErreurChaine() != null)
        {
            if (!getErreurChaine().trim().equals(Global.EMPTY_STRING))
            {
                message += Messages.label_string + "\t : \t" //$NON-NLS-1$
                        + getErreurChaine();
            }
        }

        if (!message.equals(Global.EMPTY_STRING))
        {
            message =
                Global.RN + Messages.error_convert_position + Global.RN
                        + message;
        }
        return message;
    }

    public boolean askDataSetRef()
    {
        boolean asDataSetRef = false;
        CruiseDomain cruiseDomain = Nemo.getInstance().getDomain().getCruise();
        if (cruiseDomain.getDataSetRef().equals(Global.EMPTY_STRING))
        {
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
                new InputDialog(Utils.getMainShell(),
                    Messages.CDIResumeWriter_idTitle,
                    Messages.CDIResumeWriter_idMessage, strDefault, null);
            inputDataSetRef.open();

            if (inputDataSetRef.getReturnCode() == Window.OK)
            {
                cruiseDomain.setDataSetRef(inputDataSetRef.getValue());
                asDataSetRef = true;
                // _dataSetRef = inputDataSetRef.getValue();
                // return true;
            }
            else
            {
                cruiseDomain.setDataSetRef(Global.EMPTY_STRING);
                asDataSetRef = false;
                // _dataSetRef = Global.EMPTY_STRING;
                // return false;
            }
        }
        else
        {
            asDataSetRef = true;
            // return true;
        }
        return asDataSetRef;
    }

    public void definePrefixDossier()
    {
        if (Nemo.getInstance().getConfig().isWriteMapping())
        {
            File output =
                new File(Nemo.getInstance().getDomain().getMedia()
                    .getOutputFileName());
            String cheminComplet = output.getParentFile().getPath();

            // Boolean modeDossier = false;

            // if (nomFichierSortie.isEmpty()) {
            // cheminComplet = nomDossierSortie;
            // modeDossier = true;
            // }
            // else {
            // cheminComplet = nomFichierSortie;
            // }

            InputDialog inputPrefixDossier =
                new InputDialog(Utils.getMainShell(),
                    Messages.InputPrefixDossier_idTitle,
                    Messages.InputPrefixDossier_idMessage, cheminComplet, null);
            inputPrefixDossier.open();
            String nomHorsPrefix = Global.EMPTY_STRING;
            if (inputPrefixDossier.getReturnCode() == Window.OK)
            {
                if (inputPrefixDossier.getValue().endsWith(File.separator)
                        || inputPrefixDossier.getValue().equals(cheminComplet))
                {
                    nomHorsPrefix =
                        cheminComplet.replace(inputPrefixDossier.getValue(),
                            Global.EMPTY_STRING);
                }
                else
                {
                    nomHorsPrefix =
                        cheminComplet.replace(inputPrefixDossier.getValue()
                                + File.separator, Global.EMPTY_STRING);
                }
            }

            // On affiche la boite de dialogue tant que l'utilisateur saisie une
            // donnée incorrecte
            if (!cheminComplet.endsWith(nomHorsPrefix))
            {
                definePrefixDossier();
            }
            Nemo.getInstance().getDomain().getConversion()
                .setNomMappingHorsPrefix(nomHorsPrefix);
        }
    }

//    public DataType askDataType()
//    {
//        ListDialog ld = new ListDialog(Utils.getMainShell());
//        ld.setAddCancelButton(true);
//        ld.setContentProvider(new ArrayContentProvider());
//        ld.setLabelProvider(new LabelProvider());
//
//        ld.setInput(Arrays.asList(NemoLists.getDataTypesIsSensor()));
//        ld.setTitle(Messages.askDataType_Title);
//        ld.open();
//        WSDataType result = (WSDataType) ld.getResult()[0];
//
//        DataType dt = new DataType(result.getCode(), true, result.isSensor());
//
//        return dt;
//    }

    public boolean checkConfig()
    {
        boolean blnOK = true;
        String message = Messages.CDIResumeWriter_configMessage;

        if (Nemo.getInstance().getConfig().getIdEdmoRespCdi().trim().equals(
            Global.EMPTY_STRING))
        {
            message += Global.RN + Global.SEP + Messages.settings_EDMOauthor;
            blnOK = false;
        }

        if (Nemo.getInstance().getConfig().isWriteCdi())
        {
            if (Nemo.getInstance().getConfig().getCodeEdmoDonnees().trim()
                .equals(Global.EMPTY_STRING))
            {
                message += Global.RN + Global.SEP + Messages.settings_EDMOori;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getIdEdmoRespDonnees().trim()
                .equals(Global.EMPTY_STRING))
            {
                message +=
                    Global.RN + Global.SEP + Messages.settings_EDMOcustodian;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getIdEdmoDistDonnees().trim()
                .equals(Global.EMPTY_STRING))
            {
                message += Global.RN + Global.SEP + Messages.settings_EDMOdist;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getCdiURL().trim().equals(
                Global.EMPTY_STRING))
            {
                message +=
                    Global.RN + Global.SEP + Messages.settings_dataDist_web;
                blnOK = false;
            }

            if (Nemo.getInstance().getConfig().getCdiMethod().trim().equals(
                Global.EMPTY_STRING))
            {
                message +=
                    Global.RN + Global.SEP + Messages.settings_dataDist_met;
                blnOK = false;
            }
        }

        if (!blnOK)
        {
            try
            {
                String[] buttons = new String[1];
                buttons[0] = Messages.success;
                MessageDialog dialog =
                    new MessageDialog(Utils.getMainShell(),
                        Messages.error_missing, null, message,
                        MessageDialog.NONE, buttons, 0);
                dialog.open();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            ApplicationActionBarAdvisor.optionsAction.run();

            return false;
        }
        return true;
    }

    
    private void createSummary()
    {
        
        conversionSummary =  new Text(formEdition.getBody(), SWT.READ_ONLY | SWT.WRAP );
        conversionSummary.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        fillSummary();
    }
    
    public String calcBatchCommandLine()
    {
        RulesManager rm = Nemo.getInstance().getViewMain().getMyRulesManager();
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        String command="nemo_batch";
        command += Global.RN;
        
        //input 
        command += Global.MOINS + Global.optInputFile;
        command += Global.SPACE;
        command += "\"";
        if(editFileForm.isCruiseIsAFile())
        {
            command += editFileForm.nomFichier;  
        }
        if(rm.isCruiseInADirectory())
        {
            command += editFileForm.nomRepertoireOrigin;   
        }
        command += "\"";
        
        //model
        command += Global.RN;
        command += Global.MOINS + Global.optModelFile;
        command += Global.SPACE;
        command += "\"";
        command += Nemo.getInstance().getCurrentOpenModelPath();
        command += "\"";
        
        // output 
        command += Global.RN;
        command += Global.MOINS + Global.optOutputFile;
        command += Global.SPACE;
        command += "\"";
        command += Nemo.getInstance().getDomain().getMedia().getOutputFileName();
        command += "\"";
        
        // conversion
        command += Global.RN;
        command += Global.MOINS + Global.optConv;
        command += Global.SPACE;
        if (rm.isConversionToMedatlas())
        {
            command += Conversion.MEDATLAS.toString();
        }
        if (rm.isConversionToODV())
        {
            command += Conversion.ODV.toString();
        }
        
        if (rm.isConversionToODVMultiStation())
        {
            command +=Global.RN;
            command += Global.SPACE;
            command += Global.MOINS + Global.optOdvMulti;
        }

        
        //datasetRef
        String dataSetRef=Nemo.getInstance().getDomain().getCruise().getDataSetRef();
        if (!dataSetRef.equals(Global.EMPTY_STRING)){
            command +=Global.RN;
            command += Global.MOINS + Global.optDatasetRef;
            command += Global.SPACE;
            command += Nemo.getInstance().getDomain().getCruise().getDataSetRef();
        }
        
        //cdisummary
        if ( Nemo.getInstance().getConfig().isWriteCdi())
        {
            command += Global.RN;
            command += Global.MOINS + Global.optCdiSummary;
            command += Global.SPACE;
            command += "\"";
            command += CDIResumeWriter.getInstance().getNomFichierSortie();
            command += "\"";
        }
        return command; 
    }
    public void fillBatchCommandLine()
    {
        // ajout onglet erreurs
        Utils.messageOKAppend(Global.RN);
        Utils.messageOKAppend(Messages.edit_convert_batchInfoTitle);
        Utils.messageOKAppend(calcBatchCommandLine());
        
    }
    public void fillSummary()
    {
        RulesManager rm = Nemo.getInstance().getViewMain().getMyRulesManager();
        String summary=Global.EMPTY_STRING;
        
        summary += Global.RN+ Global.RN+ Messages.view_convert_summary_title+Global.RN+ Global.RN;
        
        EditFileForm editFileForm = Nemo.getInstance().getViewMain().getEditFileForm();
        
        summary += Messages.view_convert_summary_dataTypeLabel + Global.DBL_PT 
        +Global.RN + Nemo.getInstance().getDomain().getData().getModel().getDataListType().toString();  
        summary += Global.RN+ Global.RN+ Global.RN;
        
        
        if(editFileForm.isCruiseIsAFile())
        {
            summary += Messages.view_convert_summary_inputFileLabel + Global.DBL_PT 
                +Global.RN + editFileForm.nomFichier;  
        }
        if(rm.isCruiseInADirectory())
        {
            summary += Messages.view_convert_summary_inputDirectoryLabel + Global.DBL_PT 
                + Global.RN  + editFileForm.nomRepertoireOrigin;   
        }
        summary += Global.RN+ Global.RN+ Global.RN;
        summary += Messages.view_convert_summary_outputFormatLabel+ Global.DBL_PT 
            + Global.RN + rm.getMyConversionType();
        
        if (rm.isConversionToODV())  
        {
            summary += Global.COMMA + Global.SPACE;
            if (rm.isConversionToODVMonoStation())
            {
                summary += Messages.view_convert_monoStation;
            }
            if (rm.isConversionToODVMultiStation())
            {
                summary += Messages.view_convert_monoFichier;
            }
        }
        summary += Global.RN+ Global.RN+ Global.RN;
        
        conversionSummary.setText(summary);
        
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillFromModel()
    {
        throw new UnsupportedOperationException("Not Implemented yet !");
    }
}
