package fr.ifremer.nemo.ui.edit.cruise;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xml.sax.SAXException;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.ui.FiltrageTableType;
import fr.ifremer.nemo.ui.SWTProcessWS;
import fr.ifremer.nemo.ui.SWTProgressWS;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSDataType;

public class EditCruiseForm extends EditForm
{
    //public ArrayList<NemoSection> listeSectionsSurvey;
    public Button buttonInitServeur, buttonXML;
    public CruiseSection sectionReference, sectionLocalisation, sectionSource,
            sectionCentre, sectionCommentaire;
    public CruiseDatatypeSection sectionType;

    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_cruise_title);

        // init composants IHM
        initSections();

        // bouton validate
        createButtonValidate();
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    Etape.validationSurvey();
                }
            });

        // bouton serveur
        buttonInitServeur = new Button(formEdition.getBody(), SWT.NONE);
        buttonInitServeur.setBackground(ViewMain.backgroungEdit);
        buttonInitServeur.setText(Messages.button_serverIni);
        buttonInitServeur
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    razText();
                    init_webService();
                }
            });

        // bouton XML
        buttonXML = new Button(formEdition.getBody(), SWT.NONE);
        buttonXML.setBackground(ViewMain.backgroungEdit);
        buttonXML.setText(Messages.button_xmlIni);
        buttonXML
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
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
    public void afficheEdition()
    {
        super.afficheEdition();
        
        ViewLog.aideSurvey();
        
        // affichage du texte dans la zone de gauche
        ViewMain.textAreaCruise.setText(Utils.texteBrut);

        if (!loaded)
        {
            // INFOS sur les fichiers utilises
            if (ViewMain.radioFichier.getSelection()){
                // fichier campagne
                Utils.messageInfoAppend(Messages.view_file_cruiseFile);
            }
            else{
                // dossier campagne
                Utils.messageInfoAppend(Messages.view_file_cruiseDir
                        + ViewMain.nomRepertoire);
            }

            if (ViewMain.radSerieTempo.getSelection()){
                // Série temporelle
                Utils.messageInfoAppend(Messages.typeFile_timeseries);
            }
            else if (ViewMain.radTrajectoire.getSelection()){
                // Trajectoire
                Utils.messageInfoAppend(Messages.typeFile_trajectory);
            }
        }
        loaded = true;
        ViewMain.affiche = 1;

        // remplissage de l'IHM par le modèle chargé
        if (DataModel.ouverture){
            DataModel.fillSurvey(null);
            filled = true;
        }

        final Text textCommentaire = sectionCommentaire.getItem(Global.EMPTY_STRING).getValueText();
        textCommentaire
            .addFocusListener(new org.eclipse.swt.events.FocusListener() {
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
                            tamponOrigine =
                                tamponOrigine.substring(lastIndex + 2);
                        }
                        else
                        {
                            lastIndex = lastIndexOfSpace;
                            tamponOrigine =
                                tamponOrigine.substring(lastIndex + 1);
                        }

                        tamponSortie +=
                            tampon80.substring(0, lastIndex).trim() + Global.RN;

                    }

                    tamponSortie += tamponOrigine;
                    textCommentaire.setText(tamponSortie);
                    textCommentaire.pack();

                }
            });

        formEdition.redraw();
        formEdition.reflow(true);
    }
    
    @Override
    public void actualiseEdition(){
        // pas d'actualisation nécessaire pour cet onglet
    }

    public void init_webService()
    {
        // selection de la ref campagne dans une liste
        FiltrageTableType.go(Global.WSSURVEY);
    }

    public void webService(String reference)
    {
        // remplissage du dataModel e partir des valeurs du ws

        TableDataSurvey.types.clear();

        SWTProcessWS.referenceWS = reference;
        SWTProcessWS.fichiersConstantes = false;

        if (ViewMain.dossierCampagne){
            SWTProcessWS.dossier = true;
        }
        else{
            SWTProcessWS.dossier = false;
        }

        new SWTProgressWS().run();

        // mise e jour de l'onglet courant
        majApresWS();

    }

    protected void ouvertureDescriptionXML()
    {
        String nomXML;
        File fich;

        // remplissage du dataModel e partir des valeurs du fichier XML de
        // description

        // verifier que un fichier est charge avant d'ouvrir un modele
        if ((!ViewMain.editFileForm.validee) && (!loaded)){
            Utils.messageErreurAppend(Messages.error_cruise_openXml);
        }
        else{
            FileDialog dialog =
                new FileDialog(new Shell(), SWT.OPEN | SWT.APPLICATION_MODAL);
            dialog.setText(Messages.edit_cruise_openXML);
            String extensions[] = new String[1];
            extensions[0] = Global.STAR + Global.EXT_XML;
            dialog.setFilterExtensions(extensions);
            nomXML = dialog.open();
            if (nomXML != null){
                fich = new File(nomXML);
                Utils.messageErreurAppend("fiche:" + fich);//$NON-NLS-1$  
                try{                    
                    chargementXML(fich);
                    Utils.messageOKAppend(Messages.success_openSurveyDesc);  
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    protected void chargementXML(File fichier) throws Exception
    {
        // reset de la liste des dataTypes
        TableDataSurvey.types.clear();

        Survey survey;
        if (ViewMain.dossierCampagne){
            DataModel.dirSurvey.releaseModel();

            survey = importXML(fichier);

            DataModel.dirSurvey.setModel(survey);
        }
        else{
            DataModel.singleSurvey.releaseModel();

            survey = importXML(fichier);

            DataModel.singleSurvey.setModel(survey);
            DataModel.singleSurvey.getModel();
        }

        // mise e jour de l'onglet courant
        majApresWS();
    }
    
    protected Survey importXML(File fichier) throws Exception{

        // Liste des capteurs
        ArrayList<String> listSensor = new ArrayList<String>();

        WSDataType[] listWSDT = NemoLists.getDataTypes();
        for (int i = 0; i < listWSDT.length; i++){
            if (listWSDT[i].isSensor()){
                listSensor.add(listWSDT[i].getCode());
            }
        }

        Survey survey = new Survey();
        survey.importFromXML(fichier);
        survey.setDataCentre(Utils.configuration.getDataCenter());

        // Mej des types de donnees capteurs
        for (int index = 0; index < survey.getDataTypes().length; index++)
        {
            survey.dataTypesAt(index).setSensor(
                listSensor.contains(survey.dataTypesAt(index).getCode()
                    .substring(0, 3)));
        }
        return survey;
    }
    

    protected void majApresWS()
    {
        Survey survey;
        if (ViewMain.dossierCampagne) {
            survey = DataModel.dirSurvey.getModel();
        } else {
            survey = DataModel.singleSurvey.getModel();
        }
        DataModel.fillSurvey(survey);
    }

    @Override
    public void drawingBack()
    {
        super.drawingBack();
        
        ViewLog.aideSurvey();
        ViewMain.affiche = 1;
    }

    @Override
    public void initSections()
    {

        sectionReference =
            new CruiseSection(NemoSection.C_REFERENCE,
                Messages.edit_cruise_sectionReference,
                Messages.edit_cruise_sectionReference_lib);
        sectionReference.addItem(Messages.edit_cruise_cruiseRef,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionReference.addItem(Messages.edit_cruise_cruiseName,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionReference.addItem(Messages.edit_cruise_ship, ComboType.SHIP,
            Global.EMPTY_STRING);
        listeSections.add(sectionReference);

        sectionLocalisation =
            new CruiseSection(NemoSection.C_LOCATION,
                Messages.edit_cruise_sectionLocation,
                Messages.edit_cruise_sectionLocation_lib);
        sectionLocalisation.addItem(Messages.edit_cruise_startDate,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionLocalisation.addItem(Messages.edit_cruise_endDate,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionLocalisation.addItem(Messages.edit_cruise_region,
            ComboType.REGION, Global.EMPTY_STRING);
        listeSections.add(sectionLocalisation);

        sectionSource =
            new CruiseSection(NemoSection.C_DATASOURCE,
                Messages.edit_cruise_sectionDataSource,
                Messages.edit_cruise_sectionDataSource_lib);
        sectionSource.addItem(Messages.edit_cruise_country, ComboType.COUNTRY,
            Global.EMPTY_STRING);
        sectionSource.addItem(Messages.edit_cruise_lab, ComboType.NONE,
            Global.EMPTY_STRING);
        sectionSource.addItem(Messages.edit_cruise_chief, ComboType.NONE,
            Global.EMPTY_STRING);
        sectionSource.addItem(Messages.edit_cruise_project, ComboType.NONE,
            Global.EMPTY_STRING);
        listeSections.add(sectionSource);

        sectionCentre =
            new CruiseSection(NemoSection.C_ARCHIVING,
                Messages.edit_cruise_sectionArchiving,
                Messages.edit_cruise_sectionArchiving_lib);
        sectionCentre.addItem(Messages.edit_cruise_dataCenter,
            ComboType.DATACENTER, Utils.configuration.getDataCenter());
        sectionCentre.addItem(Messages.edit_cruise_confident,
            ComboType.CONFIDENT, "P"); //$NON-NLS-1$
        listeSections.add(sectionCentre);

        sectionType =
            new CruiseDatatypeSection(NemoSection.C_DATATYPE,
                Messages.edit_cruise_sectionDataTypes,
                Messages.edit_cruise_sectionDataTypes_lib);
        listeSections.add(sectionType);

        sectionCommentaire =
            new CruiseSection(NemoSection.C_COMMENTS,
                Messages.edit_cruise_sectionComments,
                Messages.edit_cruise_sectionComments_lib);
        sectionCommentaire.addItem(Global.EMPTY_STRING, ComboType.BLOCK,
            Global.EMPTY_STRING);
        listeSections.add(sectionCommentaire);

    }
    
    @Override
    protected void disposeElements(){
        if (buttonInitServeur != null) buttonInitServeur.dispose();
        if (buttonXML != null) buttonXML.dispose();
    }

    public void invalidEtape()
    {
        if (!ViewMain.tabCruise.isDisposed())
        {
            super.invalidEtape(1);
            ViewMain.tabCruise.setText(Messages.onglet_cruise);
        }
    }

}
