package fr.ifremer.nemo.ui.edit.file;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.actions.OpenAction;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditFileForm extends EditForm
{
    //public ArrayList<NemoSection> listeSectionsDesc;
    public FileSection sectionEnteteFichier, sectionFinStation;
    public FileStationHeader sectionEnteteStation;
    public FileEndData sectionRuptureDonnees;

    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_file_title);

        // init des objets
        initSections();

        // bouton "valider etape"
        createButtonValidate();
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
        createButtonRaz();

        // boutons collapse all / expand all
        createButtonExpand();

        // Draw Station Sections
        drawSections();
    }

    @Override
    public void afficheEdition()
    {
        if (ViewMain.fileLoaded)
        {
            super.afficheEdition();

            ViewLog.aideDesc();
            loaded = true;            
            ViewMain.affiche = 0;

            // remplissage de l'IHM par le modèle chargé
            if (DataModel.ouverture){
                DataModel.fillDesc(false);
                filled = true;
            }
            actualiseEdition();

            formEdition.redraw();
            formEdition.reflow(true);

            // nom de l'application
            nomNemo();
        }
    }

    @Override
    public void drawingBack()
    {
        if (loaded){
            super.drawingBack();
            
            nomNemo();
            ViewLog.aideDesc();
            ViewMain.affiche = 0;
        }
    }

    @Override
    protected void createButtonRaz(){
        super.createButtonRaz();
        
        buttonRAZ
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    ViewMain.init();
                }
            });
    }
    
    @Override
    public void initSections()
    {
        sectionEnteteFichier =
            new FileSection(NemoSection.F_FILEHEADER, Messages
                .getString(NemoSection.F_FILEHEADER),
                Messages.edit_file_enteteFichier);
        sectionEnteteFichier.addSetter(null, Global.ZERO);
        listeSections.add(sectionEnteteFichier);

        sectionEnteteStation =
            new FileStationHeader(NemoSection.F_STATIONHEADER, Messages
                .getString(NemoSection.F_STATIONHEADER),
                Messages.edit_file_enteteStation);
        sectionEnteteStation.addSetter(Messages.edit_file_enteteStation_setter,
            Global.ZERO);
        listeSections.add(sectionEnteteStation);

        sectionFinStation =
            new FileSection(NemoSection.F_ENDSTATION, Messages
                .getString(NemoSection.F_ENDSTATION),
                Messages.edit_file_finStation);
        sectionFinStation.addSetter(null, Global.ZERO);
        listeSections.add(sectionFinStation);

        sectionRuptureDonnees =
            new FileEndData(NemoSection.F_ENDDATA, Messages
                .getString(NemoSection.F_ENDDATA),
                Messages.edit_file_indicFinDonnees);
        sectionRuptureDonnees.addSetter(Messages.edit_file_sectionRuptureDonnees_end_with, Global.EMPTY_STRING); //$NON-NLS-1$
        listeSections.add(sectionRuptureDonnees);
    }

    public void nomNemo()
    {
        // nom de l'application
        if (ViewMain.dossierCampagne){
            // dossier campagne
            File fich = new File(ViewMain.nomRepertoire);
            if (DataModel.ouverture)
            {
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_directoryTitle
                            + fich.getName() + Messages.application_modelTitle
                            + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$

            }
            else{
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_directoryTitle
                            + fich.getName() + " ]"); //$NON-NLS-1$ 
            }

        }
        else{
            // fichier campagne
            File fich;
            fich = new File(ViewMain.nomFichier);

            if (DataModel.ouverture){
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_fileTitle + fich.getName()
                            + Messages.application_modelTitle
                            + OpenAction.nomFichierModele + " ]"); //$NON-NLS-1$
            }
            else{
                ApplicationWorkbenchWindowAdvisor.configurer_titre
                    .setTitle(Messages.application_fileTitle + fich.getName()
                            + " ]"); //$NON-NLS-1$ 
            }
        }
    }

    @Override
    public void actualiseEdition()
    {
        // FAE 1810 - FileHeader accessible que pour un fichier campagne
        sectionEnteteFichier.setEnabled(ViewMain.radioFichier.getSelection());
    }

    public void invalidEtape()
    {
        super.invalidEtape(0);
        ViewMain.tabFile.setText(Messages.onglet_file);
    }

}
