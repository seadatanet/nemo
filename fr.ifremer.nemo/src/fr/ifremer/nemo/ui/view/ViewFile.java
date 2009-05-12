package fr.ifremer.nemo.ui.view;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.FiltrageTableType;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.data.EditData;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.desc.EditDesc;
import fr.ifremer.nemo.ui.edit.station.EditStation;
import fr.ifremer.nemo.ui.edit.survey.EditSurvey;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

public class ViewFile extends ViewPart
{

    public static final String ID = "fr.ifremer.nemo.ui.ViewFile"; // //$NON-NLS-1$

    public static int etape, affiche, tailleCompositeData;

    public static boolean dossierCampagne, fileLoaded;

    public static Composite compositeData;

    public static Composite top = null;

    public static TabFolder tabFolder = null;

    private Composite composite1 = null;

    protected Composite composite2 = null;

    private Composite composite3 = null;

    public static Composite composite4 = null;

    private Composite composite11 = null;

    private Composite composite12 = null;

    private static Composite compositeSeparateur = null;

    private Composite compositeChoixFichier = null;

    public static Button radProfilVertical, radSerieTempo, radTrajectoire;

    public static Button radMedatlas;

    public static Button radODV;

    public static Button radMonoFichier, radMonoStation = null;

    public static Button radioFichier = null;

    public static Button radioDossier = null;

    public static Button chkSeparateur = null;

    public static Button chkForcerTri = null;

    public static boolean blnForcerTri;

    public static Button radioTabulation, radioPointVirgule, radioVirgule,
            radioEspace, radioSeparateurAutre = null;

    public static Button btnTestWS = null;

    public static Text textSeparateur = null;

    public static Label labelCampagne;

    public static Label labelCollection;

    public static Button radioCollectCas1 = null;

    public static Button radioCollectCasN = null;

    public static boolean collectionCas1;

    public static boolean collectionCasN;

    private Button buttonParcourir = null;

    public static List listeData;

    // private Text textAreaDescription = null;

    public static StyledText styledTextCampagne, textAreaDescription,
            styledTextStation, styledTextData;

    public static TabItem tabItem, tabItem1, tabItem2, tabItem3, tabItem4,
            tabItem9;

    public static Composite compositeConversion = null;

    public static Group grpFichier = null;

    public static String nomFichier, nomRepertoire;

    public static Table tableData;

    private static Group grpSeparateur, grpODV;

    public static boolean writeResume;

    @Override
    public void createPartControl(Composite parent)
    {
        // 
        top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        // invalidation de chaque etape
        EditDesc.validee = false;
        EditDesc.loaded = false;
        EditSurvey.validee = false;
        EditSurvey.loaded = false;
        EditStation.validee = false;
        EditStation.loaded = false;
        EditData.validee = false;
        EditData.loaded = false;

        // chargement de la configuration
        Utils.initConfig();

        // ouverture de modele e false
        DataModel.ouverture = false;

        // etape de depart
        etape = 0;
        DataModel.reset = false;

        createTabFolder();
    }

    @Override
    public void setFocus()
    {
        // 

    }

    /**
     * This method initializes tabFolder
     * 
     */
    private void createTabFolder()
    {
        tabFolder = new TabFolder(top, SWT.NONE);
        createComposite1();
        createComposite2();
        createComposite3();
        createComposite4();
        createComposite14();

        // init des flags
        FiltrageTableType.listeOuverte = 0;
        affiche = 0;
        fileLoaded = false;

        // ////////////////////////////////////////////////////////
        // listener sur les onglets
        // ////////////////////////////////////////////////////////
        tabFolder
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {

                    // System.out.println("affichage courant " + affiche);

                    int tmp = tabFolder.getSelectionIndex();

                    if (isCollection() && tabFolder.getSelectionIndex() > 0)
                        tmp++;

                    Utils.validationEtape(tmp);
                    // System.out.println("onglet selectionne " + tmp);

                }
            });
        tabItem = new TabItem(tabFolder, SWT.NONE);
        tabItem.setText(Messages.onglet_file);
        tabItem.setControl(composite1);
        tabItem1 = new TabItem(tabFolder, SWT.NONE);
        tabItem1.setText(Messages.onglet_cruise);
        tabItem1.setControl(composite2);
        tabItem2 = new TabItem(tabFolder, SWT.NONE);
        tabItem2.setText(Messages.onglet_station);
        tabItem2.setControl(composite3);
        tabItem3 = new TabItem(tabFolder, SWT.NONE);
        tabItem3.setText(Messages.onglet_data); 
        tabItem3.setControl(composite4);
        tabItem9 = new TabItem(tabFolder, SWT.NONE);
        tabItem9.setText(Messages.onglet_convert);
        tabItem9.setControl(compositeConversion);
    }

    /**
     * This method initializes composite1
     * 
     */
    private void createComposite1()
    {
        composite1 = new Composite(tabFolder, SWT.NONE);
        composite1.setLayout(new FillLayout(SWT.VERTICAL));

        createComposite12();
        createComposite11();

    }

    /**
     * This method initializes composite2
     * 
     */
    private void createComposite2()
    {
        composite2 = new Composite(tabFolder, SWT.NONE);
        composite2.setLayout(new FillLayout());
        styledTextCampagne =
            new StyledText(composite2, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                    | SWT.READ_ONLY);
        styledTextCampagne.setFont(Utils.fontCourier);
    }

    /**
     * This method initializes composite3
     * 
     */
    private void createComposite3()
    {
        composite3 = new Composite(tabFolder, SWT.NONE);
        composite3.setLayout(new FillLayout());
        styledTextStation =
            new StyledText(composite3, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                    | SWT.READ_ONLY);
        styledTextStation.setFont(Utils.fontCourier);
    }

    /**
     * This method initializes composite4
     * 
     */
    private void createComposite4()
    {
        composite4 = new Composite(tabFolder, SWT.NONE);
        composite4.setLayout(new FillLayout(SWT.VERTICAL));
        styledTextData =
            new StyledText(composite4, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                    | SWT.READ_ONLY);
        styledTextData.setFont(Utils.fontCourier);
        createCompositeData(composite4);
    }

    /**
     * This method initializes composite11
     * 
     */
    private void createComposite11()
    {

        GridData gridData = new GridData();
        gridData.verticalSpan = 2;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        composite11 = new Composite(composite1, SWT.NONE);
        composite11.setLayout(gridLayout);

        compositeChoixFichier = new Composite(composite11, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(
            compositeChoixFichier);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            compositeChoixFichier);

        labelCampagne = new Label(compositeChoixFichier, SWT.NONE);
        labelCampagne
            .setText(Messages.view_file_processCruise + "           "); //$NON-NLS-1$

        labelCollection = new Label(compositeChoixFichier, SWT.NONE);
        labelCollection.setText(Messages.view_file_processCollection);

        radioFichier = new Button(compositeChoixFichier, SWT.RADIO);
        radioFichier.setSelection(true);
        radioFichier.setText(Messages.view_file_cruiseFile);

        radioCollectCas1 = new Button(compositeChoixFichier, SWT.RADIO);
        radioCollectCas1.setText(Messages.view_file_collectionCas1);

        radioDossier = new Button(compositeChoixFichier, SWT.RADIO);
        radioDossier.setText(Messages.view_file_cruiseDir); 

        radioCollectCasN = new Button(compositeChoixFichier, SWT.RADIO);
        radioCollectCasN.setText(Messages.view_file_collectionCasN); 

        radioFichier
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    init();
                }
            });

        radioDossier
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    init();
                }
            });

        radioCollectCas1
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    init();
                }
            });

        radioCollectCasN
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    init();
                }
            });

        // FAE 0001140 : deplacement du bouton Parcourir
        buttonParcourir = new Button(composite11, SWT.NONE);
        buttonParcourir.setText(Messages.button_browse); 

        buttonParcourir
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    // reinitialisation de la conversion et de l'affichage
                    init();

                    if (radioFichier.getSelection()){
                        // si fichier campagne
                        
                        // reactivation de l'onglet campagne
                        enableCruise(true);

                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

                        FileDialog dialog = new FileDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
                        nomFichier = dialog.open();
                        
                        if (nomFichier != null) {

                            fileLoaded = true;
                            nomRepertoire = new File(nomFichier).getParent() + File.separator;

                            try{
                                // affichage du corps du fichier
                                Utils.insertTextFich(nomFichier, styledTextCampagne, true);
                                textAreaDescription.setText(Utils.texteBrut);

                                // affichage etape0 dans formulaire d'edition
                                EditDesc.affichageEdition();
                            }
                            catch (IOException e1){
                                ViewLog.styledTextLogErreurs.setText(Messages.error);
                                e1.printStackTrace();
                            }
                        }
                        else{
                            ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectFile);
                        }
                    }
                    else if (radioDossier.getSelection()){
                        // si dossier campagne
                        
                        dossierCampagne = true;

                        // reactivation de l'onglet campagne
                        enableCruise(true);
                        
                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        
                        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration.getCruisesDirectory()); 
                        nomRepertoire = dialog.open();
                        
                        if (nomRepertoire != null){
                            fileLoaded = true;

                            if (Utils.findFiles(nomRepertoire, true)){
                                // affichage du corps du fichier
                                textAreaDescription.setText(Utils.texteBrut);

                                // affichage etape0 dans formulaire d'edition
                                EditDesc.affichageEdition();
                            }
                        }
                        else{
                            ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir); 
                        }
                    }
                    else if (radioCollectCas1.getSelection()){

                        dossierCampagne = true;
                        collectionCas1 = true;

                        // desactivation de l'onglet campagne
                        enableCruise(false);
                        
                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        
                        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
                        nomRepertoire = dialog.open();
                        
                        if (nomRepertoire != null){
                            fileLoaded = true;

                            if (Utils.findFilesCollectCas1(nomRepertoire, false)){
                                // affichage du corps du fichier
                                textAreaDescription.setText(Utils.texteBrut);

                                // affichage etape0 dans formulaire d'edition
                                EditDesc.affichageEdition();
                                
                                // dans le cas d'une collection, on ne peut
                                // generer que le format ODV
                                enableCollectionMedatlas(false);
                            }
                        }
                        else{
                            ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir);
                        }
                    }
                    else if (radioCollectCasN.getSelection()){

                        collectionCasN = true;

                        // desactivation de l'onglet campagne
                        enableCruise(false);
                        
                        Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                        
                        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
                        nomRepertoire = dialog.open();
                        
                        if (nomRepertoire != null){
                            fileLoaded = true;

                            if (Utils.findFiles(nomRepertoire, true)){
                                // affichage du corps du fichier
                                textAreaDescription.setText(Utils.texteBrut);

                                // affichage etape0 dans formulaire d'edition
                                EditDesc.affichageEdition();
                                
                                // dans le cas d'une collection, on ne peut
                                // generer que le format ODV
                                enableCollectionMedatlas(false);
                                radMonoFichier.setEnabled(false); // FAE 0001134
                            }
                        }
                        else{
                            ViewLog.styledTextLogErreurs.setText(Messages.error_file_selectDir); 
                        }
                    }
                }
            });

        createGroupeSeparateur();

        createGroupeFichier();
    }
    
    protected void enableCruise(boolean enabled){
        if (enabled){
            if (tabItem1.isDisposed()){                
                tabItem1 = new TabItem(tabFolder, SWT.NONE, 1);
                tabItem1.setText(Messages.onglet_cruise); 
                tabItem1.setControl(composite2); 
            }
        }
        else{
            tabItem1.dispose();
        }
    }
    
    protected void init(){
        
        // reinitialisation de la conversion
        if (DataModel.inputModel != null)
            DataModel.reset();

        fileLoaded = false;
        Preformat.setPreformat(false);
        
        // reinitialisation de l'affichage
        // --- les formulaires
        Utils.razGlobal();       
        EditDesc.loaded = false;
        // --- renitialisation du tableau de paramètres (onglet Data)
        if (tableData != null) TableData.go();
        // --- le titre de l'application
        ApplicationWorkbenchWindowAdvisor.configurer_titre.setTitle(Intern.application_title);
        // --- le contenu du fichier
        textAreaDescription.setText(Global.EMPTY_STRING);
        // --- l'onglet d'erreur
        Utils.messageErreur(Global.EMPTY_STRING);
        
        

        // on rend sélectionnables les radios ODV et Medatlas dans
        // l'onglet Conversion
        radMedatlas.setEnabled(true);
        radMedatlas.setSelection(true);
        radODV.setEnabled(true);
        radODV.setSelection(false);
        radMonoStation.setEnabled(true);
        radMonoStation.setSelection(true);
        radMonoFichier.setEnabled(true);
        radMonoFichier.setSelection(false);
        afficheGrpODV();
        
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

        dossierCampagne = false;
        collectionCas1 = false;
        collectionCasN = false;
    }

    /**
     * This method initializes composite12
     * 
     */
    private void createComposite12()
    {
        composite12 = new Composite(composite1, SWT.NONE);
        composite12.setLayout(new FillLayout());
        textAreaDescription =
            new StyledText(composite12, SWT.MULTI | SWT.READ_ONLY
                    | SWT.H_SCROLL | SWT.V_SCROLL);
        textAreaDescription.setFont(Utils.fontCourier);

    }

    /**
     * This method initializes composite14
     * 
     */
    private void createComposite14()
    {
        compositeConversion = new Composite(tabFolder, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        compositeConversion.setLayout(layout);

        GridLayout layout2 = new GridLayout();
        layout.numColumns = 2;

        Group group = new Group(compositeConversion, SWT.NONE);
        group.setText(Messages.view_convert_title); 
        group.setLayout(layout);

        radMedatlas = new Button(group, SWT.RADIO);
        radMedatlas.setText(Conversion.MEDATLAS.getName()); 
        radMedatlas.setSelection(true);

        // Composite vide pour mise en page
        Composite compoVide1 = new Composite(group, SWT.NONE);

        radODV = new Button(group, SWT.RADIO);
        radODV.setText(Conversion.ODV.getName());
        radODV.setSelection(false);

        grpODV = new Group(group, SWT.NONE);
        grpODV.setLayout(layout2);
        afficheGrpODV();

        radMonoStation = new Button(grpODV, SWT.RADIO);
        radMonoStation.setText(Messages.view_convert_monoStation);
        radMonoStation.setSelection(true);
        radMonoFichier = new Button(grpODV, SWT.RADIO);
        radMonoFichier.setText(Messages.view_convert_monoFichier);

        // Composite vide pour mise en page
        Composite compoVide2 = new Composite(compositeConversion, SWT.NONE);

        chkForcerTri = new Button(compositeConversion, SWT.CHECK);
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

        // bouton radMedatlas
        radMedatlas
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    afficheGrpODV();
                }

            });

        // bouton radODV
        radODV
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    afficheGrpODV();
                }

            });
    }

    /**
     * This method initializes groupFichier
     * 
     */
    private void createGroupeFichier()
    {
        grpFichier = new Group(composite11, SWT.NONE);
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

    private void createCompositeData(Composite compo)
    {
        compositeData = new Composite(compo, SWT.NONE);
        compositeData.setLayout(new FillLayout());
    }

    private void createGroupeSeparateur()
    {
        grpSeparateur = new Group(composite11, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(grpSeparateur);
        GridDataFactory.fillDefaults().grab(false, false)
            .applyTo(grpSeparateur);

        chkSeparateur = new Button(grpSeparateur, SWT.CHECK);
        chkSeparateur.setText(Messages.view_file_separator);

        compositeSeparateur = new Composite(grpSeparateur, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(compositeSeparateur);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(compositeSeparateur);

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
    }

    public static void setSeparatorVisible()
    {
        compositeSeparateur.setVisible(chkSeparateur.getSelection());
    }

    public static boolean isCollection()
    {
        return (collectionCas1 || collectionCasN);
    }

    public static String typeFichierEntree()
    {
        if (radSerieTempo.getSelection())
        {
            return Global.TIMESERIES;
        }
        else if (radTrajectoire.getSelection())
        {
            return Global.TRAJECTORY;
        }
        else
        {
            return Global.PROFILE;
        }
    }

    public static void afficheGrpODV()
    {
        grpODV.setVisible(radODV.getSelection());
    }
    
    public static void enableMedatlas(boolean enable)
    {
        if (!isCollection()){
            radMedatlas.setEnabled(enable);
            radMedatlas.setSelection(enable);
            radODV.setSelection(!enable);
            afficheGrpODV();
        }
    }
    
    public static void enableCollectionMedatlas(boolean enable)
    {
        radMedatlas.setEnabled(enable);
        radMedatlas.setSelection(enable);
        radODV.setSelection(!enable);
        afficheGrpODV();
    }
    
    public static void enableChoice(boolean enabled){
        //grpFichier.setEnabled(enabled);
        radProfilVertical.setEnabled(enabled);
        radSerieTempo.setEnabled(enabled);
        radTrajectoire.setEnabled(enabled);
        
        if (chkSeparateur.getSelection()){
            //grpSeparateur.setEnabled(enabled);
            chkSeparateur.setEnabled(enabled);
            radioTabulation.setEnabled(enabled);
            radioPointVirgule.setEnabled(enabled);
            radioVirgule.setEnabled(enabled);
            radioEspace.setEnabled(enabled);
            radioSeparateurAutre.setEnabled(enabled);
            textSeparateur.setEnabled(enabled);
        }
    }
    
    public static void selectTypeFile(String typeFile){

        if (Global.PROFILE.equals(typeFile)){
            radProfilVertical.setSelection(true);
            radSerieTempo.setSelection(false);
            radTrajectoire.setSelection(false);
        }
        else if (Global.TIMESERIES.equals(typeFile)){
            radSerieTempo.setSelection(true);
            radProfilVertical.setSelection(false);
            radTrajectoire.setSelection(false);
        }
        else if (Global.TRAJECTORY.equals(typeFile)){
            radProfilVertical.setSelection(false);
            radSerieTempo.setSelection(false);
            radTrajectoire.setSelection(true);
        }
    }
}
