package fr.ifremer.nemo.ui.edit.file;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.actions.OpenAction;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditFileForm
{

    // /////////////////////////////////////////

    // private static Shell shell = new Shell();
    public ScrolledForm formEdition;
    public FormToolkit toolkitEdition;

    public Button buttonValidate, buttonRAZ, buttonExpand;
    public Boolean okApresReset;
    public boolean expand, validee, loaded, filled;
    public ArrayList<NemoSection> listeSectionsDesc;
    public FileSection sectionEnteteFichier, sectionFinStation;
    public FileStationHeader sectionEnteteStation;
    public FileEndData sectionRuptureDonnees;

    public void createEditionForm(Composite parent)
    {

        validee = false;
        loaded = false;

        toolkitEdition = new FormToolkit(parent.getDisplay());
        formEdition = toolkitEdition.createScrolledForm(parent);

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_file_title);

        // init des objets
        listeSectionsDesc = new ArrayList<NemoSection>();
        initSections();

        // bouton "valider etape"
        buttonValidate = new Button(formEdition.getBody(), SWT.NONE);
        buttonValidate.setBackground(ViewMain.backgroungEdit);
        buttonValidate.setText(Messages.button_validateStep);
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {

                    Etape.validationDesc();
                }
            });

        // bouton RAZ
        buttonRAZ = new Button(formEdition.getBody(), SWT.NONE);
        buttonRAZ.setBackground(ViewMain.backgroungEdit);
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

        // boutons collapse all / expand all
        buttonExpand = new Button(formEdition.getBody(), SWT.NONE);
        buttonExpand.setBackground(ViewMain.backgroungEdit);
        buttonExpand.setText(Global.PLUS);
        buttonExpand
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    expandSections();
                }
            });

        // Draw Station Sections
        for (NemoSection desc : listeSectionsDesc)
        {
            desc.draw(toolkitEdition, formEdition, filled);
        }

        formEdition.setVisible(false);
    }

    public void afficheEdition()
    {
        if (ViewMain.fileLoaded)
        {
            // raz message erreur
            Utils.messageErreur(Global.EMPTY_STRING);

            expand = false; // de base les sections sont refermees
            okApresReset = true;
            loaded = true;
            filled = false;
            ViewMain.affiche = 0;

            // remplissage de l'IHM par le modèle chargé
            if (DataModel.ouverture)
            {
                DataModel.fillDesc(false);
                filled = true;
            }
            actualiseEdition();

            formEdition.redraw();
            formEdition.reflow(true);

            formEdition.setVisible(true);

            // nom de l'application
            nomNemo();

        }
    }

    public void razEdition()
    {
        formEdition.setVisible(false);

    }

    public void DrawingBack()
    {

        if (loaded)
        {
            nomNemo();

            // raz message erreur
            Utils.messageErreur(Global.EMPTY_STRING);

            expand = true;
            expandSections();

            // refresh global
            formEdition.reflow(true);

            // affichage courant
            ViewMain.affiche = 0;
            formEdition.setVisible(true);
        }
    }

    protected void expandSections()
    {
        for (NemoSection section : listeSectionsDesc)
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

    public void initSections()
    {
        sectionEnteteFichier =
            new FileSection(NemoSection.F_FILEHEADER, Messages
                .getString(NemoSection.F_FILEHEADER),
                Messages.edit_file_enteteFichier);
        sectionEnteteFichier.addSetter(null, Global.ZERO);
        listeSectionsDesc.add(sectionEnteteFichier);

        sectionEnteteStation =
            new FileStationHeader(NemoSection.F_STATIONHEADER, Messages
                .getString(NemoSection.F_STATIONHEADER),
                Messages.edit_file_enteteStation);
        sectionEnteteStation.addSetter(Messages.edit_file_enteteStation_setter,
            Global.ZERO);
        listeSectionsDesc.add(sectionEnteteStation);

        sectionFinStation =
            new FileSection(NemoSection.F_ENDSTATION, Messages
                .getString(NemoSection.F_ENDSTATION),
                Messages.edit_file_finStation);
        sectionFinStation.addSetter(null, Global.ZERO);
        listeSectionsDesc.add(sectionFinStation);

        sectionRuptureDonnees =
            new FileEndData(NemoSection.F_ENDDATA, Messages
                .getString(NemoSection.F_ENDDATA),
                Messages.edit_file_indicFinDonnees);
        sectionRuptureDonnees.addSetter(null, Global.EMPTY_STRING);
        listeSectionsDesc.add(sectionRuptureDonnees);
    }

    public void ajoutModifyListenerText(ArrayList<Text> liste)
    {
        int taille = liste.size();
        for (int i = 0; i < taille; i++)
        {
            liste.get(i).addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event)
                {
                    // on modifie la valeur du champ texte

                    // des qu'on modifie une valeur,
                    // invalidation de l'étape en cours et des suivantes
                    Utils.invalidEtapes();
                }
            });

        }
    }

    public void razText()
    {

        for (NemoSection desc : listeSectionsDesc)
        {
            desc.razValues();
        }
    }

    public void nomNemo()
    {
        // nom de l'application
        if (ViewMain.dossierCampagne)
        {
            // dossier campagne

            File fich = new File(ViewMain.nomRepertoire);
            if (DataModel.ouverture)
            {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_directoryTitle
                            + fich.getName() + Messages.application_modelTitle
                            + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$

            }
            else
            {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_directoryTitle
                            + fich.getName() + " ]"); //$NON-NLS-1$ 
            }

        }
        else
        {
            // fichier campagne
            File fich;
            fich = new File(ViewMain.nomFichier);

            if (DataModel.ouverture)
            {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_fileTitle + fich.getName()
                            + Messages.application_modelTitle
                            + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$
            }
            else
            {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_fileTitle + fich.getName()
                            + " ]"); //$NON-NLS-1$ 
            }
        }

    }

    public void disposeElements()
    {
        if (listeSectionsDesc != null)
        {
            for (NemoSection section : listeSectionsDesc)
            {
                section.dispose();
            }
        }
        if (buttonRAZ != null)
            buttonRAZ.dispose();
        if (buttonExpand != null)
            buttonExpand.dispose();
        if (buttonValidate != null)
            buttonValidate.dispose();
    }

    public void actualiseEdition()
    {
        // FAE 1810 - FileHeader accessible que pour un fichier campagne
        sectionEnteteFichier.setEnabled(ViewMain.radioFichier.getSelection());
    }

    public void invalidEtape()
    {
        okApresReset = false;
        validee = false;
        if (ViewMain.affiche != 0 && loaded)
        {
            loaded = false;
            disposeElements();
        }
        ViewMain.tabFile.setText(Messages.onglet_file);
    }

}
