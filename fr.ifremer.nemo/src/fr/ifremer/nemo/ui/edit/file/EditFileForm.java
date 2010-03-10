package fr.ifremer.nemo.ui.edit.file;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.Utils;
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
    
    private Button buttonValidateAll;

    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_file_title);

        // init des objets
        initSections();
        

        buttonValidateAll = new Button(formEdition.getBody(), SWT.PUSH);
        buttonValidateAll.setBackground(ViewMain.backgroungEdit);
        buttonValidateAll.setText(Messages.button_validateAllSteps);
        buttonValidateAll
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e)
            {
                // validation de l'onglet FILE
                Etape.validationFile();
                
                // Les onglets sont décalés si on on est en collection => pas d'étape cruise
                // FAE 3796
                int offset = 0;
                if (ViewMain.isCollection())
                    offset--;
                
                if (ViewMain.etape == 1){
                    // Sélection de l'onglet CRUISE
                    ViewMain.tabFolder.setSelection(ViewMain.tabFolder.getItem(1));
                    Utils.validationEtape(1);
                    // Validation de l'onglet CRUISE
                    Etape.validationCruise();
                }
                
                if (ViewMain.etape == 2){
                    // Sélection de l'onglet STATION
                    ViewMain.tabFolder.setSelection(ViewMain.tabFolder.getItem(2 + offset));
                    Utils.validationEtape(2);
                    // Validation de l'onglet STATION
                    Etape.validationStation();
                }
                
                if (ViewMain.etape == 3){
                    // Sélection de l'onglet DATA
                    ViewMain.tabFolder.setSelection(ViewMain.tabFolder.getItem(3 + offset));
                    Utils.validationEtape(3);
                    // Validation de l'onglet DATA
                    Etape.validationData();
                }
                
                if (ViewMain.etape == 4){
                    // Sélection de l'onglet CONVERSION
                    ViewMain.tabFolder.setSelection(ViewMain.tabFolder.getItem(4 + offset));
                    Utils.validationEtape(4);
                }
            }
        });
        buttonValidateAll.setEnabled(false);

        // bouton "valider etape"
        createButtonValidate();
        buttonValidate
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    Etape.validationFile();
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
        // nom du model
        //String modelName = Nemo.getInstance().getNomModele();
        
        if (ViewMain.fileLoaded)
        {
            super.afficheEdition();

            ViewLog.aideFile();
            loaded = true;            
            ViewMain.affiche = 0;

            // remplissage de l'IHM par le modèle chargé
            //if (!Global.EMPTY_STRING.equals(modelName)){
                DataModel.fillDesc(false);
                filled = true;
            //}
            actualiseEdition();

            formEdition.redraw();
            formEdition.reflow(true);

            // nom de l'application
            Nemo.refreshNomNemo();
        }
    }

    @Override
    public void drawingBack()
    {
        if (loaded){
            super.drawingBack();
            Nemo.refreshNomNemo();
            ViewLog.aideFile();
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
        sectionRuptureDonnees.addSetter(Messages.edit_file_sectionRuptureDonnees_end_with, Global.EMPTY_STRING);
        listeSections.add(sectionRuptureDonnees);
    }
    

    @Override
    protected void disposeElements()
    {
        super.disposeElements();
        buttonValidateAll.dispose();
    }

    @Override
    public void actualiseEdition()
    {
        // FAE 1810 - FileHeader accessible que pour un fichier campagne
        sectionEnteteFichier.setEnabled(ViewMain.radioFichier.getSelection() || ViewMain.chkMultiStations.getSelection());
        
        buttonValidateAll.setEnabled(!Nemo.getInstance().getNomModele().equals(Global.EMPTY_STRING));
    }

    public void invalidEtape()
    {
        super.invalidEtape(0);
        ViewMain.tabFile.setText(Messages.onglet_file);
    }
}