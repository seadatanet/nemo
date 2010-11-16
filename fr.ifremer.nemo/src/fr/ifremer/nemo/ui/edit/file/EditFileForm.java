package fr.ifremer.nemo.ui.edit.file;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;


import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.MediaModel;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.EditForm;
import fr.ifremer.nemo.ui.edit.Etape;
import fr.ifremer.nemo.ui.edit.NemoSection;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.ui.view.ViewMain;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;

public class EditFileForm extends EditForm
{
    
    
    private  boolean isCruiseInADirectory;
    
    // sections    
    private FileSection sectionEnteteFichier;
    private FileSection sectionFinStation;
    
    public FileStationHeader sectionEnteteStation;
    public FileEndData sectionRuptureDonnees;
    public  StyledText  textAreaFile;
    public  String nomFichier, nomRepertoire;
    public  Button radioFichier = null;
    public  Button radioDossier = null;
    private Button buttonValidateAll;
    public  Button radioCollectCas1 = null;
    public  Button radioCollectCasN = null;
    public  boolean collectionCas1;
    public  boolean collectionCasN;
    public  Button radProfilVertical, radSerieTempo, radTrajectoire;
    private  Button radMedatlas;
    private  Button radODV;
    public  Button radODVMonoFichier;
    public  Button radODVMonoStation ;
    public  Button radioTabulation, radioPointVirgule, radioVirgule,
    radioEspace, radioSeparateurAutre = null;
    public  Text textSeparateur = null;
    public  Button chkSeparateur = null;
    public  Button chkForcerTri = null;
    public  Button chkMultiStations;
    protected  Group grpODV;
    protected  Composite compositeSeparateur;
    public  boolean blnForcerTri;
    private  boolean  fileLoaded;
    
    
    
    public EditFileForm()
    {
        super();
    }
    public void init()
    {
        collectionCas1 = false;
        collectionCasN = false;
        fileLoaded = false;
        setLoaded (false);
        textAreaFile.setText(Global.EMPTY_STRING);
        enableChoice(true);
        // reinitialisation des selections
        chkSeparateur.setSelection(false);
        radioTabulation.setSelection(true);
        radioPointVirgule.setSelection(false);
        radioVirgule.setSelection(false);
        radioEspace.setSelection(false);
        radioSeparateurAutre.setSelection(false);
        textSeparateur.setText(Global.EMPTY_STRING);
        setSeparatorVisible();
        razOutputChoice();
    }
    public void createLeftForm(Composite parent)
    {
        Composite textFile = new Composite(parent, SWT.NONE);
        textFile.setLayout(new FillLayout());
        textAreaFile =
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                | SWT.V_SCROLL);
        textAreaFile.setFont(Utils.fontCourier);

        FormToolkit toolkitViewFile = new FormToolkit(parent.getDisplay());
        toolkitViewFile.setBackground(parent.getBackground());
        ScrolledForm formViewFile = toolkitViewFile.createScrolledForm(parent);
        formViewFile.setBackground(parent.getBackground());
        formViewFile.getBody().setLayout(new GridLayout());

        createGroupChoixFichierInFile(formViewFile.getBody());
        
        // create subGroup for Separator and FileType
//        Group grpSeparateurAndFileType = new Group(parent, SWT.NONE);
//        GridLayoutFactory.fillDefaults().applyTo(grpSeparateurAndFileType);
//        GridDataFactory.fillDefaults().grab(false, false)
//        .applyTo(grpSeparateurAndFileType);
        
        createGroupSeparateurInFile(formViewFile.getBody());
        createGroupTypeFichierInFile(formViewFile.getBody());
        //BGT test ajout ici choix sortie***************************************************
        createGroupChooseOutputFormat(formViewFile.getBody());
        
        ((SashForm)parent).setWeights(new int[] {45, 55});
        
    }
    @Override
    public void createEditionForm(Composite parent)
    {
        super.createEditionForm(parent);

        formEdition.getBody().setLayout(new GridLayout());
        formEdition.setText(Messages.edit_file_title);

        // init des objets
        initSections();
        

        buttonValidateAll = new Button(formEdition.getBody(), SWT.PUSH);
        buttonValidateAll.setBackground(Nemo.getInstance().getViewMain().getBackgroundEdit());
        buttonValidateAll.setText(Messages.button_validateAllSteps);
        buttonValidateAll
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e)
            {
                
                ViewMain theViewMain = Nemo.getInstance().getViewMain();
                
                // validation de l'onglet FILE
                Etape.validationFile();
                
                // Les onglets sont décalés si on on est en collection => pas d'étape cruise
                // FAE 3796
                int offset = 0;
                if (isCollection())
                {
                    offset--;
                }
                
                if (theViewMain.getEtape() == 1){
                    // Sélection de l'onglet CRUISE
                    theViewMain.selectTabByIndex(1);
                    Utils.validationEtape(1);
                    // Validation de l'onglet CRUISE
                    Etape.validationCruise();
                }
                
                if (theViewMain.getEtape() == 2){
                    // Sélection de l'onglet STATION
                    theViewMain.selectTabByIndex(2 + offset);
                    Utils.validationEtape(2);
                    // Validation de l'onglet STATION
                    Etape.validationStation();
                }
                
                if (theViewMain.getEtape() == 3){
                    // Sélection de l'onglet DATA
                    theViewMain.selectTabByIndex(3 + offset);
                    Utils.validationEtape(3);
                    // Validation de l'onglet DATA
                    Etape.validationData();
                }
                
                if (theViewMain.getEtape() == 4){
                    // Sélection de l'onglet CONVERSION
                    theViewMain.selectTabByIndex(4 + offset);
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
        
        if (fileLoaded)
        {
            super.afficheEdition();

            ViewLog.aideFile();
            setLoaded( true);            
            Nemo.getInstance().getViewMain().setAffiche(0);

            // remplissage de l'IHM par le modèle chargé
            fillFromModel(false);
            filled = true;
            actualiseEdition();

            formEdition.redraw();
            formEdition.reflow(true);

            // nom de l'application
            Nemo.getInstance().refreshNomNemo();
        }
    }

    @Override
    public void drawingBack()
    {
        if (isLoaded()){
            super.drawingBack();
            Nemo.getInstance().refreshNomNemo();
            ViewLog.aideFile();
            Nemo.getInstance().getViewMain().setAffiche(0);
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
                    Nemo.getInstance().getViewMain().init();
                }
            });
    }
    
    @Override
    public void initSections()
    {
        sectionEnteteFichier =
            new FileSection(NemoSection.F_FILEHEADER,
                Messages.getString(NemoSection.F_FILEHEADER),
                Messages.edit_file_enteteFichier,
                this);
        sectionEnteteFichier.addSetter(null, Global.ZERO);
        listeSections.add(sectionEnteteFichier);

        sectionEnteteStation =
            new FileStationHeader(NemoSection.F_STATIONHEADER, Messages
                .getString(NemoSection.F_STATIONHEADER),
                Messages.edit_file_enteteStation, this);
        sectionEnteteStation.addSetter(Messages.edit_file_enteteStation_setter,
            Global.ZERO);
        listeSections.add(sectionEnteteStation);

        sectionFinStation =
            new FileSection(NemoSection.F_ENDSTATION, Messages
                .getString(NemoSection.F_ENDSTATION),
                Messages.edit_file_finStation, this);
        sectionFinStation.addSetter(null, Global.ZERO);
        listeSections.add(sectionFinStation);

        sectionRuptureDonnees =
            new FileEndData(NemoSection.F_ENDDATA, Messages
                .getString(NemoSection.F_ENDDATA),
                Messages.edit_file_indicFinDonnees, this);
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
        sectionEnteteFichier.setEnabled(radioFichier.getSelection() || chkMultiStations.getSelection());
        
        buttonValidateAll.setEnabled(!Nemo.getInstance().getNomModele().equals(Global.EMPTY_STRING));
    }

    /**
     * fill with current model values 
     * @param raz
     */
    @Override
    public void fillFromModel()
    {
        fillFromModel(false);
    }
    /**
     * fill with current model values if raz=false (else, fill with default values)
     * @param raz
     */
    public void fillFromModel(Boolean raz)
    {
        if (raz){
            Nemo.getInstance().getDomain().release();
        }
        
        
        MediaModel mediaModel = Nemo.getInstance().getDomain().getMedia().getModel();
        // Séparateur
        setSeparateur(mediaModel.getSeparator());

        // Type de fichier
        selectTypeFile(mediaModel.getInputType());
        
        setSelectedConversion(Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat());

        // --- remplissage du formulaire de droite (editFileForm)
        // Entête Fichier
        if (Nemo.getInstance().getViewMain().getEditFileForm().isCruiseInADirectory()
                && !chkMultiStations.getSelection()){
            sectionEnteteFichier.razValues();
        }
        else{
            sectionEnteteFichier.setValue(String.valueOf(mediaModel.getFileHeader()));            
        }

        // Entête Station
        if (mediaModel.getStationHeaderPattern().equals(Global.EMPTY_STRING)){
            sectionEnteteStation.selectNumber(true);
            sectionEnteteStation.setValue(String.valueOf(mediaModel.getStationHeader()));
        }
        else{
            sectionEnteteStation.selectNumber(false);
            sectionEnteteStation.setIndicatorText(mediaModel.getStationHeaderPattern());
            sectionEnteteStation.setNbLinesBeforeData(String.valueOf(mediaModel.getStationHeader()));
        }
        
        // Fin de Station
        sectionFinStation.setValue(String.valueOf(mediaModel.getEndStation()));
        
        // Fin des données
        sectionRuptureDonnees.setEndData(mediaModel.getEndData(), mediaModel.getEndDataPattern());

        
        //BGT
        setSelectedConversion(Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat());
        // activation option serie tempo
        enableChoice(true);
    }
    
    public void invalidEtape()
    {
        super.invalidEtape(0);
        Nemo.getInstance().getViewMain().tabFile.setText(Messages.onglet_file);
    }
    private void createGroupChooseOutputFormat(Composite parent)
    {
        Composite compositeConversion = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        compositeConversion.setLayout(layout);

        GridLayout layout2 = new GridLayout();
        layout.numColumns = 1;
        compositeConversion.setLayout(layout);


        Group group = new Group(compositeConversion, SWT.NONE);
        group.setText(Messages.view_convert_title);
        group.setLayout(layout);

        radMedatlas = new Button(group, SWT.RADIO);
        radMedatlas.setText(Conversion.MEDATLAS.getName());
        radMedatlas.setSelection(true);
        addRadConvertListener(radMedatlas);

        // Composite vide pour mise en page
//        Composite compoVide1 = new Composite(group, SWT.NONE);

        radODV = new Button(group, SWT.RADIO);
        radODV.setText(Conversion.ODV.getName());
        radODV.setSelection(false);
        addRadConvertListener(radODV);

        grpODV = new Group(group, SWT.NONE);
        grpODV.setLayout(layout2);
         afficheGrpODV();

        radODVMonoStation = new Button(grpODV, SWT.RADIO);
        radODVMonoStation.setText(Messages.view_convert_monoStation);
        radODVMonoStation.setSelection(true);
        addRadConvertListener(radODVMonoStation);
        radODVMonoFichier = new Button(grpODV, SWT.RADIO);
        radODVMonoFichier.setText(Messages.view_convert_monoFichier);
        addRadConvertListener(radODVMonoFichier);
        // Composite vide pour mise en page
//        Composite compoVide2 = new Composite(compositeConversion, SWT.NONE);

        chkForcerTri = new Button(group, SWT.CHECK);
        chkForcerTri.setFont(Utils.font08);
        chkForcerTri.setText(Messages.view_convert_sort);
        chkForcerTri
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g)
            {
                blnForcerTri = chkForcerTri.getSelection();
            }
        });
    }
    private void createGroupSeparateurInFile(Composite parent)
    {
        Group grpSeparateur = new Group(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(grpSeparateur);
        GridDataFactory.fillDefaults().grab(false, false)
        .applyTo(grpSeparateur);

        chkSeparateur = new Button(grpSeparateur, SWT.CHECK);
        chkSeparateur.setText(Messages.view_file_separator);
        chkSeparateur
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g)
            {
                setSeparatorVisible();
                Utils.invalidEtapes();
                Utils.messageErreur(Global.EMPTY_STRING);
            }
        });

        compositeSeparateur = new Composite(grpSeparateur, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(
            compositeSeparateur);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
            compositeSeparateur);

        radioTabulation = new Button(compositeSeparateur, SWT.RADIO);
        radioTabulation.setText(Messages.view_file_sepTab);
        radioTabulation.setSelection(true);

        radioPointVirgule = new Button(compositeSeparateur, SWT.RADIO);
        radioPointVirgule.setText(Messages.view_file_sepSemi);

        radioVirgule = new Button(compositeSeparateur, SWT.RADIO);
        radioVirgule.setText(Messages.view_file_sepComma);

        radioEspace = new Button(compositeSeparateur, SWT.RADIO);
        radioEspace.setText(Messages.view_file_sepSpace);

        radioSeparateurAutre = new Button(compositeSeparateur, SWT.RADIO);
        radioSeparateurAutre.setText(Messages.view_file_sepOther);
        textSeparateur = new Text(compositeSeparateur, SWT.BORDER);

        // Par defaut
        compositeSeparateur.setVisible(false);
        
    }

    /**
     * This method initializes groupFichier
     */
    private void createGroupTypeFichierInFile(Composite parent)
    {
        Group grpFichier = new Group(parent, SWT.NONE);
        grpFichier.setText(Messages.view_file_fileType);
        GridLayoutFactory.fillDefaults().applyTo(grpFichier);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(grpFichier);

        radProfilVertical = new Button(grpFichier, SWT.RADIO);
        radProfilVertical.setText(Messages.typeFile_profile);

        radSerieTempo = new Button(grpFichier, SWT.RADIO);
        radSerieTempo.setText(Messages.typeFile_timeseries);

        radTrajectoire = new Button(grpFichier, SWT.RADIO);
        radTrajectoire.setText(Messages.typeFile_trajectory);

        // Selection du type par defaut
        radProfilVertical.setSelection(true);
    }
    
    private void createGroupChoixFichierInFile(Composite parent)
    {

        Composite compositeChoixFichier = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(
            compositeChoixFichier);
        // GridDataFactory.fillDefaults().grab(true,
        // false).applyTo(compositeChoixFichier);

        Composite col1 = new Composite(compositeChoixFichier, SWT.NONE);
        col1.setLayout(new FillLayout(SWT.VERTICAL));
        GridData dataCol = new GridData();
        // dataCol.grabExcessHorizontalSpace = true;
//        dataCol.grabExcessVerticalSpace = true;
        col1.setLayoutData(dataCol);

        Label labelCampagne = new Label(col1, SWT.NONE);
        FontData fontdata = labelCampagne.getFont().getFontData()[0];
        fontdata.setStyle(SWT.BOLD);
        labelCampagne.setFont(new Font(Application.display, fontdata));
        labelCampagne.setText(Messages.view_file_processCruise + "           "); //$NON-NLS-1$

        radioFichier = new Button(col1, SWT.RADIO);
        radioFichier.setSelection(true);
        radioFichier.setText(Messages.view_file_cruiseFile);

        radioFichier.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e)
            {
                chkMultiStations.setSelection(false);
                chkMultiStations.setEnabled(false);
            }

        });

        addRadFileListener(radioFichier);

        radioDossier = new Button(col1, SWT.RADIO);
        radioDossier.setText(Messages.view_file_cruiseDir);
        addRadFileListener(radioDossier);
        radioDossier.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e)
            {
                chkMultiStations.setSelection(false);
                chkMultiStations.setEnabled(false);
            }

        });

        Composite col2 = new Composite(compositeChoixFichier, SWT.NONE);
        GridData data = new GridData();
        data.widthHint = 50;
        col2.setLayoutData(data);
        // col2.setLayoutData(dataCol);
        col2.setLayout(new FillLayout(SWT.VERTICAL));
        new Label(col2, SWT.SEPARATOR | SWT.VERTICAL);

        Composite col3 = new Composite(compositeChoixFichier, SWT.NONE);
        col3.setLayout(new FillLayout(SWT.VERTICAL));
        col3.setLayoutData(dataCol);

        Label labelCollection = new Label(col3, SWT.NONE);
        labelCollection.setFont(new Font(Application.display, fontdata));
        labelCollection.setText(Messages.view_file_processCollection);

        radioCollectCas1 = new Button(col3, SWT.RADIO);
        radioCollectCas1.setText(Messages.view_file_collectionCas1);
        addRadFileListener(radioCollectCas1);
        radioCollectCas1.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e)
            {
                chkMultiStations.setEnabled(true);
            }

        });

        radioCollectCasN = new Button(col3, SWT.RADIO);
        radioCollectCasN.setText(Messages.view_file_collectionCasN);
        addRadFileListener(radioCollectCasN);
        radioCollectCasN.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e)
            {
                chkMultiStations.setEnabled(true);
            }

        });

        new Label(col3, SWT.SEPARATOR | SWT.HORIZONTAL);

        chkMultiStations = new Button(col3, SWT.CHECK);
        chkMultiStations.setEnabled(false);
        chkMultiStations.setText(Messages.view_file_multiStations);

        
        // bug 4181
        chkMultiStations.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g)
            {
                if (chkMultiStations.getSelection()){
                    sectionEnteteFichier.setEnabled(true);
                }
                else{
                   sectionEnteteFichier.setEnabled(false);
                }
            }

        });





        // FAE 0001140 : deplacement du bouton Parcourir
        Button buttonParcourir = new Button(parent, SWT.NONE);
        buttonParcourir.setText(Messages.button_browse);
        buttonParcourir
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e)
            {

                // reinitialisation de la conversion et de l'affichage
                init();

                if (radioFichier.getSelection())
                {
                    // si fichier campagne

                    // reactivation de l'onglet campagne
                    Nemo.getInstance().getViewMain().enableCruise(true);

                    Shell shell = Utils.getMainShell();

                    FileDialog dialog =
                        new FileDialog(shell, SWT.OPEN
                            | SWT.APPLICATION_MODAL);
                    dialog.setFilterPath(Nemo.getInstance().getConfig()
                        .getCruisesDirectory());
                    nomFichier = dialog.open();

                    if (nomFichier != null)
                    {

                        fileLoaded = true;
                        nomRepertoire =
                            new File(nomFichier).getParent()
                            + File.separator;

                        try
                        {
                            // affichage du corps du fichier
                            Utils.insertTextFich(nomFichier,
                                Nemo.getInstance().getViewMain().textAreaCruise, true);
                            textAreaFile.setText(Utils.texteBrut);

                            // affichage etape0 dans formulaire d'edition
                           afficheEdition();

                        }
                        catch (IOException e1)
                        {
                            ViewLog.styledTextLogErreurs
                            .setText(Messages.error);
                            e1.printStackTrace();
                        }
                    }
                    else
                    {
                        ViewLog.styledTextLogErreurs
                        .setText(Messages.error_file_selectFile);
                    }
                }
                else if (radioDossier.getSelection())
                {
                    // si dossier campagne
                   setCruiseInADirectory(true);

                    // reactivation de l'onglet campagne
                    Nemo.getInstance().getViewMain().enableCruise(true);

                    Shell shell = Utils.getMainShell();

                    DirectoryDialog dialog =
                        new DirectoryDialog(shell, SWT.OPEN
                            | SWT.APPLICATION_MODAL);
                    dialog.setFilterPath(Nemo.getInstance().getConfig()
                        .getCruisesDirectory());
                    nomRepertoire = dialog.open();

                    if (nomRepertoire != null)
                    {

                        fileLoaded = true;

                        if (Utils.findFiles(nomRepertoire, true))
                        {
                            // affichage du corps du fichier
                            textAreaFile.setText(Utils.texteBrut);

                            // affichage etape0 dans formulaire d'edition
                            afficheEdition();
                        }
                    }
                    else
                    {
                        ViewLog.styledTextLogErreurs
                        .setText(Messages.error_file_selectDir);
                    }
                }
                else if (radioCollectCas1.getSelection())
                {

                    setCruiseInADirectory (true);
                    collectionCas1 = true;

                    // desactivation de l'onglet campagne
                    Nemo.getInstance().getViewMain().enableCruise(false);

                    Shell shell = Utils.getMainShell();

                    DirectoryDialog dialog =
                        new DirectoryDialog(shell, SWT.OPEN
                            | SWT.APPLICATION_MODAL);
                    dialog.setFilterPath(Nemo.getInstance().getConfig()
                        .getCruisesDirectory());
                    nomRepertoire = dialog.open();
                    System.out.println("nom nomRepertoire: "+ nomRepertoire);
                    if (nomRepertoire != null)
                    {
                        fileLoaded = true;

                        if (Utils
                                .findFilesCollectCas1(nomRepertoire, false))
                        {
                            // affichage du corps du fichier
                            textAreaFile.setText(Utils.texteBrut);

                            // affichage etape0 dans formulaire d'edition
                            afficheEdition();

                            // dans le cas d'une collection, on ne peut
                            // generer que le format ODV
                            enableCollectionMedatlas(false);
                        }
                    }
                    else
                    {
                        ViewLog.styledTextLogErreurs
                        .setText(Messages.error_file_selectDir);
                    }
                }
                else if (radioCollectCasN.getSelection())
                {

                    collectionCasN = true;

                    // desactivation de l'onglet campagne
                    Nemo.getInstance().getViewMain().enableCruise(false);

                    Shell shell = Utils.getMainShell();

                    DirectoryDialog dialog =
                        new DirectoryDialog(shell, SWT.OPEN
                            | SWT.APPLICATION_MODAL);
                    dialog.setFilterPath(Nemo.getInstance().getConfig()
                        .getCruisesDirectory());
                    nomRepertoire = dialog.open();

                    if (nomRepertoire != null)
                    {
                        fileLoaded = true;

                        if (Utils.findFiles(nomRepertoire, true))
                        {
                            // affichage du corps du fichier
                            textAreaFile.setText(Utils.texteBrut);

                            // affichage etape0 dans formulaire d'edition
                            afficheEdition();

                            // dans le cas d'une collection, on ne peut
                            // generer que le format ODV
                            enableCollectionMedatlas(false);
                            radODVMonoFichier.setEnabled(false); // FAE
                            // 0001134
                        }
                    }
                    else
                    {
                        ViewLog.styledTextLogErreurs
                        .setText(Messages.error_file_selectDir);
                    }
                }
            }

        });
    }
    
    @Override
    public void initFromNewModel()
    {
        String nomFich;
        // puis remplissage du modele avec les valeurs de l'IHM
        if (radioFichier.getSelection())
        {
            nomFich=nomFichier;
        }
        else
        {
            nomFich=nomRepertoire;
        }
        
        MediaModel mediaModel = new MediaModel();
        
        // --- enregistrement du nom du fichier en entrée
        mediaModel.setSourcePath(nomFich);
        
        // --- enregistrement du formulaire de gauche (ViewMain)
        // Type de traitement
        mediaModel.setProcessType(getTypeTraitement());

        // Séparateur
        mediaModel.setSeparator(getSeparateur());

        // Type de fichier
        mediaModel.setInputType(getTypeFichierEntree());
        

        // Fichier multiStation ?
        mediaModel.setMultiStations(chkMultiStations.getSelection());
        
        // --- enregistrement du formulaire de droite (editFileForm)
        // Entête Fichier
        int fileHeader = 0;
        if (!Nemo.getInstance().getViewMain().getEditFileForm().isCruiseInADirectory() || mediaModel.isMultiStations()){
            fileHeader = Integer.parseInt(getSectionEnteteFichier().getValue());
        }
        
        mediaModel.setFileHeader(fileHeader);

        // Entête Station
        if (!sectionEnteteStation.isSelectNumber()){
            mediaModel.setStationHeaderPattern(sectionEnteteStation.getIndicatorText());
            mediaModel.setStationHeader(sectionEnteteStation.getNbLinesBeforeData());
        }
        else{
            mediaModel.setStationHeader(sectionEnteteStation.getValue());
        }
        
        // Fin de Station
        mediaModel.setEndStation(getSectionFinStation().getValue());
        
        // Fin des données
        E_EndData end = sectionRuptureDonnees.getEndData();
        mediaModel.setEndData(end);
        if (end == E_EndData.END_WITH){
            mediaModel.setEndDataPattern(sectionRuptureDonnees.getValue());
        }
        else if(end == E_EndData.NEXT_START_WITH){
            mediaModel.setEndDataPattern(sectionRuptureDonnees.getStartStationIndicator());
        }
        //BGT
        Nemo.getInstance().getDomain().getConversion().setCurrentConversion(getSelectedConversion());
        
        // Mise à jour du model
        Nemo.getInstance().getDomain().getMedia().updateMedia(mediaModel);

    }
 
    private void selectTypeProcess(Button radioSelected)
    {
        // les boutons radios ne sont plus dans le même conteneur :
        // il faut forcer la deselection
        if (radioSelected == radioCollectCasN
                || radioSelected == radioCollectCas1)
        {
            radioDossier.setSelection(false);
            radioFichier.setSelection(false);
        }
        else
        {
            radioCollectCasN.setSelection(false);
            radioCollectCas1.setSelection(false);
        }
    }
    public void afficheGrpODV()
    {
        grpODV.setVisible(radODV.getSelection());
    }
    private void addRadFileListener(final Button radio)
    {
        radio
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g)
            {
                selectTypeProcess(radio);
                init();
            }
        });
    }
    private void addRadConvertListener(final Button radio)
    {
        radio
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent g)
            {
                afficheGrpODV();
                //BGT
                Nemo.getInstance().getDomain().getConversion().setCurrentConversion(getSelectedConversion());
                Nemo.getInstance().getDomain().getConversion().getModel().setOutputFormat(getSelectedConversion());
            }
        });
    }
    
    
    
    public void enableMedatlas(boolean enable)
    {
        if (!isCollection())
        {
            radMedatlas.setEnabled(enable);
            radMedatlas.setSelection(enable);
            radODV.setSelection(!enable);
            afficheGrpODV();
        }else{
            System.out.println("error: cannot enable MEDATLAS for collection");
        }
    }

    public void enableCollectionMedatlas(boolean enable)
    {
        radMedatlas.setEnabled(enable);
        radMedatlas.setSelection(enable);
        radODV.setSelection(!enable);
        afficheGrpODV();
    }
    public void enableOdvMonostation(boolean enable)
    {
        radMedatlas.setSelection(!enable);
        radODV.setSelection(enable);
        radODVMonoStation.setSelection(enable);
        radODVMonoFichier.setSelection(!enable);
        afficheGrpODV(); 
    }
    public void enableOdvMultistation(boolean enable)
    {
        radMedatlas.setSelection(!enable);
        radODV.setSelection(enable);
        radODVMonoStation.setSelection(!enable);
        radODVMonoFichier.setSelection(enable);
        afficheGrpODV(); 
    }
    public void setSelectedConversion(E_TypeConversion conv)
    {
        if (conv == E_TypeConversion.MEDATLAS)
        {
            enableMedatlas(true);
        }
        else if (conv == E_TypeConversion.ODV_MONOSTATION)
        {
            enableOdvMonostation(true);
        }
        else if (conv == E_TypeConversion.ODV_MULTISTATIONS)
        {
            enableOdvMultistation(true);
        }
//        Nemo.getInstance().getDomain().getConversion().getModel().setOutputFormat(conv);
    }
    public void enableChoice(boolean enabled)
    {
        // grpFichier.setEnabled(enabled);
        radProfilVertical.setEnabled(enabled);
        radSerieTempo.setEnabled(enabled);
        radTrajectoire.setEnabled(enabled);

        if (chkSeparateur.getSelection())
        {
            // grpSeparateur.setEnabled(enabled);
            chkSeparateur.setEnabled(enabled);
            radioTabulation.setEnabled(enabled);
            radioPointVirgule.setEnabled(enabled);
            radioVirgule.setEnabled(enabled);
            radioEspace.setEnabled(enabled);
            radioSeparateurAutre.setEnabled(enabled);
            textSeparateur.setEnabled(enabled);
        }
    }
    public void razOutputChoice()
    {
        // on rend sélectionnables les radios ODV et Medatlas dans
        // l'onglet Conversion
        if (radODVMonoFichier != null)
        {
            radMedatlas.setEnabled(true);
            radMedatlas.setSelection(true);
            radODV.setEnabled(true);
            radODV.setSelection(false);
            radODVMonoStation.setEnabled(true);
            radODVMonoStation.setSelection(true);
            radODVMonoFichier.setEnabled(true);
            radODVMonoFichier.setSelection(false);
            afficheGrpODV();
        }
    }
    
    /**
     * select the appropriate radio choice depending on typeFile
     * @param typeFile
     */
    public void selectTypeFile(E_InputType typeFile)
    {
        switch (typeFile)
        {
            case TRAJECTORY:
                radProfilVertical.setSelection(false);
                radSerieTempo.setSelection(false);
                radTrajectoire.setSelection(true);
                break;
            case TIMESERIES:
                radSerieTempo.setSelection(true);
                radProfilVertical.setSelection(false);
                radTrajectoire.setSelection(false);
                break;
            default:
                radProfilVertical.setSelection(true);
            radSerieTempo.setSelection(false);
            radTrajectoire.setSelection(false);
            break;
        }
    }
    
    
    
    public boolean isConversionToMEDATLAS()
    {
        return radMedatlas.getSelection();
    }
    public boolean isConversionToODV()
    {
        return radODV.getSelection();
    }
    public boolean isConversionToODVMonoStation()
    {
        return radODVMonoStation.getSelection();
    }
    public boolean isConversionToODVMultiStation()
    {
        return radODVMonoFichier.getSelection();
    }
    
    
    // TRADUCTION DES BOUTONS RADIO
    public E_TypeConversion getSelectedConversion()
    {
        
        //*************
        if (radMedatlas.getSelection())
        {
            return E_TypeConversion.MEDATLAS;
        }
        // else
        if (radODVMonoStation.getSelection())
        {
            return E_TypeConversion.ODV_MONOSTATION;
        }
        // else{
        return E_TypeConversion.ODV_MULTISTATIONS;
    }

    public E_InputType getTypeFichierEntree()
    {
        if (radSerieTempo.getSelection())
        {
            return E_InputType.TIMESERIES;
        }
        else if (radTrajectoire.getSelection())
        {
            return E_InputType.TRAJECTORY;
        }
        else
        {
            return E_InputType.PROFILE;
        }
    }

   

    public E_ProcessType getTypeTraitement()
    {
        if (radioCollectCasN.getSelection())
        {
            return E_ProcessType.COLLECTION_FILES;
        }
        else if (radioCollectCas1.getSelection())
        {
            return E_ProcessType.COLLECTION_DIR;
        }
        else if (radioDossier.getSelection())
        {
            return E_ProcessType.CRUISE_DIR;
        }
        else
        {
            return E_ProcessType.CRUISE_FILE;
        }
    }

    public String getSeparateur()
    {
        if (chkSeparateur.getSelection())
        {
            if (radioTabulation.getSelection())
            {
                // Tabulation
                return Global.TAB;
            }
            else if (radioPointVirgule.getSelection())
            {
                // Point-virgule
                return Global.SEMICOLON;
            }
            else if (radioVirgule.getSelection())
            {
                // Virgule
                return Global.COMMA;
            }
            else if (radioEspace.getSelection())
            {
                // Espace
                return Global.SPACE;
            }
            else if (radioSeparateurAutre.getSelection())
            {
                // Autre
                return textSeparateur.getText();
            }
        }
        return Global.EMPTY_STRING;
    }

    public void setSeparateur(String sep)
    {
        if (sep.equals(Global.EMPTY_STRING))
        {
            // pas de séparateur
            chkSeparateur.setSelection(false);
        }
        else
        {
            chkSeparateur.setSelection(true);

            if (sep.equals(Global.TAB))
            {
                // Tabulation
                radioTabulation.setSelection(true);
                radioPointVirgule.setSelection(false);
                radioVirgule.setSelection(false);
                radioEspace.setSelection(false);
                radioSeparateurAutre.setSelection(false);
                textSeparateur.setText(Global.EMPTY_STRING);
            }
            else if (sep.equals(Global.SEMICOLON))
            {
                // Point-virgule
                radioTabulation.setSelection(false);
                radioPointVirgule.setSelection(true);
                radioVirgule.setSelection(false);
                radioEspace.setSelection(false);
                radioSeparateurAutre.setSelection(false);
                textSeparateur.setText(Global.EMPTY_STRING);
            }
            else if (sep.equals(Global.COMMA))
            {
                // Virgule
                radioTabulation.setSelection(false);
                radioPointVirgule.setSelection(false);
                radioVirgule.setSelection(true);
                radioEspace.setSelection(false);
                radioSeparateurAutre.setSelection(false);
                textSeparateur.setText(Global.EMPTY_STRING);
            }
            else if (sep.equals(Global.SPACE))
            {
                // Espace
                radioTabulation.setSelection(false);
                radioPointVirgule.setSelection(false);
                radioVirgule.setSelection(false);
                radioEspace.setSelection(true);
                radioSeparateurAutre.setSelection(false);
                textSeparateur.setText(Global.EMPTY_STRING);
            }
            else
            {
                // Autre séparateur
                radioTabulation.setSelection(false);
                radioPointVirgule.setSelection(false);
                radioVirgule.setSelection(false);
                radioEspace.setSelection(false);
                radioSeparateurAutre.setSelection(true);
                textSeparateur.setText(sep);
            }
        }
        setSeparatorVisible();
    }
    
    public void setSeparatorVisible()
    {
        compositeSeparateur.setVisible(chkSeparateur.getSelection());
    }

    public boolean isCollection()
    {
        return (collectionCas1 || collectionCasN);
    }
    public boolean isCruiseInADirectory()
    {
        return isCruiseInADirectory;
    }
    public void setCruiseInADirectory(boolean isCruiseInADirectory)
    {
        this.isCruiseInADirectory = isCruiseInADirectory;
    }
    public boolean isFileLoaded()
    {
        return fileLoaded;
    }
    public void setFileLoaded(boolean fileLoaded)
    {
        this.fileLoaded = fileLoaded;
    }
    public FileSection getSectionEnteteFichier()
    {
        return sectionEnteteFichier;
    }
    public FileStationHeader getSectionEnteteStation()
    {
        return sectionEnteteStation;
    }
    public FileSection getSectionFinStation()
    {
        return sectionFinStation;
    }
}