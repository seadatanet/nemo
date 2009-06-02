package fr.ifremer.nemo.ui.edit.survey;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.transform.TransformerException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.xml.sax.SAXException;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.ui.FiltrageTableType;
import fr.ifremer.nemo.ui.SWTProcessWS;
import fr.ifremer.nemo.ui.SWTProgressWS;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboType;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.edit.desc.EditDesc;
import fr.ifremer.nemo.ui.view.ViewEdit;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.WSDataType;

public class EditSurvey
{
    // fonctions de remplissage de la zone d'edition pour les campagnes

    private static Shell shell = new Shell();
    public static ScrolledForm formEdition;

    public static Boolean okApresReset;
    public static boolean expand, validee, loaded, filled;

    public static ArrayList<NemoSection> listeSectionsSurvey;
    public static Button buttonRAZ, buttonInitServeur, buttonValidate,
            buttonXML, buttonExpand;
    public static CruiseSection sectionReference, sectionLocalisation,
            sectionSource, sectionCentre, sectionCommentaire;
    public static CruiseDatatypeSection sectionType;

    public static void affichageEdition()
    {
        // initialisation zone edition pour etape description de fichier
        formEdition = ViewEdit.formEdition;

        okApresReset = true;
        expand = false;
        filled = false;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // affichage du texte dans la zone de gauche
        ViewFile.styledTextCampagne.setText(Utils.texteBrut);
        ViewLog.aideSurvey();

        if (!loaded)
        {

            // INFOS sur les fichiers utilises
            if (ViewFile.radioFichier.getSelection())
            {
                // fichier campagne
                Utils.messageInfoAppend(Messages.view_file_cruiseFile);
            }
            else
            {
                // dossier campagne
                Utils.messageInfoAppend(Messages.view_file_cruiseDir
                        + ViewFile.nomRepertoire);
            }

            if (ViewFile.radSerieTempo.getSelection())
            {
                // Série temporelle
                Utils.messageInfoAppend(Messages.typeFile_timeseries);
            }
            else if (ViewFile.radTrajectoire.getSelection())
            {
                // Trajectoire
                Utils.messageInfoAppend(Messages.typeFile_trajectory);
            }

        }
        loaded = true;
        // affichage courant
        ViewFile.affiche = 1;

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_cruise_title);

        shell.close();
        shell = new Shell();
        disposeElements();

        // init composants IHM
        listeSectionsSurvey = new ArrayList<NemoSection>();
        initBoutons();
        initSections();

        // bouton validate
        buttonValidate.setParent(formEdition.getBody());
        buttonValidate.setBackground(ViewEdit.background);
        buttonValidate.setText(Messages.button_validateStep);
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
        buttonInitServeur.setParent(formEdition.getBody());
        buttonInitServeur.setBackground(ViewEdit.background);
        buttonInitServeur.setText(Messages.button_serverIni);
        buttonInitServeur
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {

                    init_webService();
                }
            });

        // bouton XML
        buttonXML.setParent(formEdition.getBody());
        buttonXML.setBackground(ViewEdit.background);
        buttonXML.setText(Messages.button_xmlIni);
        buttonXML
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {

                    ouvertureDescriptionXML();
                }
            });

        // bouton reset
        buttonRAZ.setParent(formEdition.getBody());
        buttonRAZ.setBackground(ViewEdit.background);
        buttonRAZ.setText(Messages.button_reset);
        buttonRAZ
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    razText();
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();
                }
            });

        // bouton collapse all / expand all
        buttonExpand.setParent(formEdition.getBody());
        buttonExpand.setBackground(ViewEdit.background);
        buttonExpand.setText(Global.PLUS);
        buttonExpand
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    for (NemoSection section : listeSectionsSurvey)
                    {
                        if (!expand && section.isEnabled())
                        {
                            section.setExpanded(true);
                        }
                        else
                        {
                            section.setExpanded(false);
                        }
                    }
                    if (!expand)
                    {
                        expand = true;
                        buttonExpand.setText(Global.MOINS);
                    }
                    else
                    {
                        expand = false;
                        buttonExpand.setText(Global.PLUS);
                    }

                    // refresh global
                    formEdition.reflow(true);

                }
            });

        // Draw Station Sections
        for (NemoSection section : listeSectionsSurvey)
        {
            section.initSection(formEdition);
            section.draw(formEdition.getBody(), filled);
        }

        // remplissage de l'IHM par le modele charge
        if (DataModel.ouverture)
        {
            DataModel.fillSurvey();
            filled = true;
        }

        // refresh du formulaire
        ViewEdit.formEdition.reflow(true);
        formEdition.reflow(true);

    }

    public static void init_webService()
    {
        // selection de la ref campagne dans une liste
        FiltrageTableType.go(Global.WSSURVEY);

    }

    public static void webService(String reference)
    {
        // remplissage du dataModel e partir des valeurs du ws

        TableDataSurvey.types.clear();

        SWTProcessWS.referenceWS = reference;
        SWTProcessWS.fichiersConstantes = false;

        if (ViewFile.dossierCampagne)
        {
            SWTProcessWS.dossier = true;
        }
        else
        {
            SWTProcessWS.dossier = false;
        }

        new SWTProgressWS().run();

        // mise e jour de l'onglet courant
        majApresWS();

    }

    public static void ouvertureDescriptionXML()
    {

        String nomXML;
        File fich;

        // remplissage du dataModel e partir des valeurs du fichier XML de
        // description

        // verifier que un fichier est charge avant d'ouvrir un modele
        if ((!EditDesc.validee) && (!loaded))
        {
            Utils.messageErreurAppend(Messages.error_cruise_openXml);
        }
        else
        {
            FileDialog dialog =
                new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
            dialog.setText(Messages.edit_cruise_openXML);
            String extensions[] = new String[1];
            extensions[0] = Global.STAR + Global.EXT_XML;
            dialog.setFilterExtensions(extensions);
            nomXML = dialog.open();
            if (nomXML == null)
            {
            }
            else
            {

                fich = new File(nomXML);

                Utils.messageErreurAppend("fiche:" + fich);//$NON-NLS-1$
                chargementXML(fich);

            }

        }

    }

    public static void chargementXML(File fichier)
    {

        // reset de la liste des dataTypes
        TableDataSurvey.types.clear();

        // Liste des capteurs
        ArrayList<String> listSensor = new ArrayList<String>();

        WSDataType[] listWSDT = NemoLists.getDataTypes();
        for (int i = 0; i < listWSDT.length; i++)
        {
            if (listWSDT[i].isSensor())
            {
                listSensor.add(listWSDT[i].getCode());
            }
        }

        if (ViewFile.dossierCampagne)
        {

            try
            {
                DataModel.dirSurvey.releaseModel();
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

                DataModel.dirSurvey.setModel(survey);

            }
            catch (FileNotFoundException e)
            {
                // 
                e.printStackTrace();
            }
            catch (SAXException e)
            {
                // 
                e.printStackTrace();
            }
            catch (TransformerException e)
            {
                //
                e.printStackTrace();
            }

        }
        else
        {

            try
            {
                DataModel.singleSurvey.releaseModel();

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

                DataModel.singleSurvey.setModel(survey);

                DataModel.singleSurvey.getModel();
            }
            catch (FileNotFoundException e)
            {
                // 
                e.printStackTrace();
            }
            catch (SAXException e)
            {
                // 
                e.printStackTrace();
            }
            catch (TransformerException e)
            {
                // 
                e.printStackTrace();
            }

        }

        // mise e jour de l'onglet courant
        majApresWS();
    }

    public static void majApresWS()
    {
        DataModel.fillSurvey();
    }

    public static void razEdition()
    {
        // reset de toute la zone d'edition apres etape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

        // effacement des boutons valider/expand/init serveur
        buttonExpand.setParent(shell);
        buttonValidate.setParent(shell);
        buttonInitServeur.setParent(shell);
        buttonXML.setParent(shell);
        buttonRAZ.setParent(shell);

        // effacement des sections
        for (NemoSection section : listeSectionsSurvey)
        {
            section.setExpanded(true);
            section.initSection(shell);
        }

        // effacement titre formulaire
        ViewEdit.formEdition.setText(Global.EMPTY_STRING);

        // refresh global
        ViewEdit.formEdition.reflow(true);
    }

    public static void ajoutModifyListenerText(ArrayList<Text> liste)
    {
        int taille = liste.size();
        for (int i = 0; i < taille; i++)
        {
            liste.get(i).addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event)
                {
                    // des qu'on modifie une valeur,
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();

                }
            });

        }
    }

    public static void DrawingBack()
    {
        // reset de toute la zone d'edition apres etape descriptioon de fichier
        // on retire toutes les sections et les boutons expand et validation

        ViewLog.aideSurvey();
        ScrolledForm s;
        s = ViewEdit.formEdition;

        // raz du message d'erreurs
        Utils.messageErreur(Global.EMPTY_STRING);

        // reaffichage des boutons valider/expand/init serveur

        buttonValidate.setParent(s.getBody());
        buttonInitServeur.setParent(s.getBody());
        buttonXML.setParent(s.getBody());
        buttonRAZ.setParent(s.getBody());
        buttonExpand.setParent(s.getBody());
        expand = false;
        buttonExpand.setText(Global.PLUS);

        // affichage des sections
        for (NemoSection section : listeSectionsSurvey)
        {
            section.setExpanded(false);
            section.initSection(s.getBody());
        }

        ViewEdit.formEdition.setText(Messages.edit_cruise_title);

        // refresh global
        ViewEdit.formEdition.reflow(true);

        // affichage courant
        ViewFile.affiche = 1;

    }

    public static void initSections()
    {

        sectionReference =
            new CruiseSection(shell, NemoSection.C_REFERENCE,
                Messages.edit_cruise_sectionReference,
                Messages.edit_cruise_sectionReference_lib);
        sectionReference.addItem(shell, Messages.edit_cruise_cruiseRef,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionReference.addItem(shell, Messages.edit_cruise_cruiseName,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionReference.addItem(shell, Messages.edit_cruise_ship,
            ComboType.SHIP, Global.EMPTY_STRING);
        listeSectionsSurvey.add(sectionReference);

        sectionLocalisation =
            new CruiseSection(shell, NemoSection.C_LOCATION,
                Messages.edit_cruise_sectionLocation,
                Messages.edit_cruise_sectionLocation_lib);
        sectionLocalisation.addItem(shell, Messages.edit_cruise_startDate,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionLocalisation.addItem(shell, Messages.edit_cruise_endDate,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionLocalisation.addItem(shell, Messages.edit_cruise_region,
            ComboType.REGION, Global.EMPTY_STRING);
        listeSectionsSurvey.add(sectionLocalisation);

        sectionSource =
            new CruiseSection(shell, NemoSection.C_DATASOURCE,
                Messages.edit_cruise_sectionDataSource,
                Messages.edit_cruise_sectionDataSource_lib);
        sectionSource.addItem(shell, Messages.edit_cruise_country,
            ComboType.COUNTRY, Global.EMPTY_STRING);
        sectionSource.addItem(shell, Messages.edit_cruise_lab, ComboType.NONE,
            Global.EMPTY_STRING);
        sectionSource.addItem(shell, Messages.edit_cruise_chief,
            ComboType.NONE, Global.EMPTY_STRING);
        sectionSource.addItem(shell, Messages.edit_cruise_project,
            ComboType.NONE, Global.EMPTY_STRING);
        listeSectionsSurvey.add(sectionSource);

        sectionCentre =
            new CruiseSection(shell, NemoSection.C_ARCHIVING,
                Messages.edit_cruise_sectionArchiving,
                Messages.edit_cruise_sectionArchiving_lib);
        sectionCentre.addItem(shell, Messages.edit_cruise_dataCenter,
            ComboType.DATACENTER, Utils.configuration.getDataCenter());
        sectionCentre.addItem(shell, Messages.edit_cruise_confident,
            ComboType.CONFIDENT, "P"); //$NON-NLS-1$
        listeSectionsSurvey.add(sectionCentre);

        sectionType =
            new CruiseDatatypeSection(shell, NemoSection.C_DATATYPE,
                Messages.edit_cruise_sectionDataTypes,
                Messages.edit_cruise_sectionDataTypes_lib);
        listeSectionsSurvey.add(sectionType);

        sectionCommentaire =
            new CruiseSection(shell, NemoSection.C_COMMENTS,
                Messages.edit_cruise_sectionComments,
                Messages.edit_cruise_sectionComments_lib);
        sectionCommentaire.addItem(shell, Global.EMPTY_STRING, ComboType.BLOCK,
            Global.EMPTY_STRING);

        final Text textCommentaire =
            sectionCommentaire.getItem(Global.EMPTY_STRING).getValueText();
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

        listeSectionsSurvey.add(sectionCommentaire);

    }

    public static void initBoutons()
    {
        buttonRAZ = new Button(shell, SWT.NONE);
        buttonInitServeur = new Button(shell, SWT.NONE);
        buttonExpand = new Button(shell, SWT.NONE);
        buttonValidate = new Button(shell, SWT.NONE);
        buttonXML = new Button(shell, SWT.NONE);

    }

    public static void razText()
    {

        for (NemoSection section : listeSectionsSurvey)
        {
            section.razValues();
        }
    }

    public static void disposeElements()
    {

        if (listeSectionsSurvey != null)
        {
            for (NemoSection section : listeSectionsSurvey)
            {
                section.dispose();
            }
        }

        if (buttonRAZ != null)
        {
            buttonRAZ.dispose();
            buttonInitServeur.dispose();
            buttonExpand.dispose();
            buttonValidate.dispose();
            buttonXML.dispose();
        }

    }

    public static void invalidEtape()
    {
        if (!ViewFile.tabItem1.isDisposed())
        {
            okApresReset = false;
            validee = false;
            if (ViewFile.affiche != 1 && loaded)
            {
                loaded = false;
                disposeElements();
            }
            ViewFile.tabItem1.setText(Messages.onglet_cruise);
        }
    }

}
