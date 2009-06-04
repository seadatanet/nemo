/* ============================================================================
 * Nom du fichier	: ViewMain.java
 * Auteur			: dboulang
 * Date				: 3 juin 2009
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.ui.view;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import fr.ifremer.nemo.ui.ApplicationWorkbenchWindowAdvisor;
import fr.ifremer.nemo.ui.FiltrageTableType;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.conversion.EditConversionForm;
import fr.ifremer.nemo.ui.edit.cruise.EditCruiseForm;
import fr.ifremer.nemo.ui.edit.data.EditDataForm;
import fr.ifremer.nemo.ui.edit.data.TableData;
import fr.ifremer.nemo.ui.edit.file.EditFileForm;
import fr.ifremer.nemo.ui.edit.station.EditStationForm;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;

/**
 * Description: TODO
 * 
 * @author dboulang
 */
public class ViewMain extends ViewPart
{
    public static final String ID = "fr.ifremer.nemo.ui.view.ViewMain"; //$NON-NLS-1$

    // Formulaires d'édition
    public static EditFileForm editFileForm;
    public static EditCruiseForm editCruiseForm;
    public static EditStationForm editStationForm;
    public static EditDataForm editDataForm;
    public static EditConversionForm editConversionForm;
    public static Color backgroungEdit;

    // ViewFile
    public static int etape, affiche, tailleCompositeData;
    public static boolean dossierCampagne, fileLoaded;
    public static boolean blnForcerTri;
    public static boolean writeResume;
    public static boolean collectionCas1;
    public static boolean collectionCasN;

    protected static Composite compositeSeparateur, textFileCruise;
    protected static Group grpODV;

    public static TabFolder tabFolder;
    public static Composite compositeData;
    public static TabItem tabFile, tabCruise, tabStation, tabData,
            tabConversion;

    public static Button radProfilVertical, radSerieTempo, radTrajectoire;
    public static Button radMedatlas, radODV;
    public static Button radMonoFichier, radMonoStation = null;

    public static Button radioFichier = null;
    public static Button radioDossier = null;

    public static Button chkSeparateur = null;
    public static Button chkForcerTri = null;

    public static Button radioTabulation, radioPointVirgule, radioVirgule,
            radioEspace, radioSeparateurAutre = null;

    public static Button btnTestWS = null;

    public static Text textSeparateur = null;

    public static Button radioCollectCas1 = null;
    public static Button radioCollectCasN = null;

    public static StyledText textAreaCruise, textAreaFile, textAreaStation,
            textAreaData;

    public static String nomFichier, nomRepertoire;

    public static Table tableData;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent)
    {
        // ouverture de modele e false
        DataModel.ouverture = false;
        DataModel.reset = false;
        DataModel.init();

        // etape de depart
        etape = 0;

        // init des flags
        FiltrageTableType.listeOuverte = 0;
        affiche = 0;
        fileLoaded = false;

        backgroungEdit = new Color(parent.getDisplay(), 255, 255, 255);

        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        tabFolder = new TabFolder(top, SWT.NONE);
        tabFolder
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    int tmp = tabFolder.getSelectionIndex();
                    if (isCollection() && tabFolder.getSelectionIndex() > 0)
                        tmp++;
                    Utils.validationEtape(tmp);
                }
            });

        Composite contentFile = new Composite(tabFolder, SWT.NONE);
        createContentFile(contentFile);

        Composite contentCruise = new Composite(tabFolder, SWT.NONE);
        createContentCruise(contentCruise);

        Composite contentStation = new Composite(tabFolder, SWT.NONE);
        createContentStation(contentStation);

        Composite contentData = new Composite(tabFolder, SWT.NONE);
        createContentData(contentData);

        Composite contentConversion = new Composite(tabFolder, SWT.NONE);
        createContentConversion(contentConversion);

        tabFile = new TabItem(tabFolder, SWT.NONE);
        tabFile.setText(Messages.onglet_file);
        tabFile.setControl(contentFile);

        tabCruise = new TabItem(tabFolder, SWT.NONE);
        tabCruise.setText(Messages.onglet_cruise);
        tabCruise.setControl(contentCruise);

        tabStation = new TabItem(tabFolder, SWT.NONE);
        tabStation.setText(Messages.onglet_station);
        tabStation.setControl(contentStation);

        tabData = new TabItem(tabFolder, SWT.NONE);
        tabData.setText(Messages.onglet_data);
        tabData.setControl(contentData);

        tabConversion = new TabItem(tabFolder, SWT.NONE);
        tabConversion.setText(Messages.onglet_convert);
        tabConversion.setControl(contentConversion);

    }

    protected void createContentFile(Composite parent)
    {
        parent.setLayout(new FillLayout());

        Composite left = new Composite(parent, SWT.BORDER);
        left.setBackground(parent.getBackground());
        left.setLayout(new FillLayout(SWT.VERTICAL));

        Composite textFile = new Composite(left, SWT.NONE);
        textFile.setLayout(new FillLayout());
        textAreaFile =
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                    | SWT.V_SCROLL);
        textAreaFile.setFont(Utils.fontCourier);

        FormToolkit toolkitViewFile = new FormToolkit(left.getDisplay());
        toolkitViewFile.setBackground(left.getBackground());
        ScrolledForm formViewFile = toolkitViewFile.createScrolledForm(left);
        formViewFile.setBackground(left.getBackground());
        formViewFile.getBody().setLayout(new GridLayout());

        createGroupChoixFichierInFile(formViewFile.getBody());
        createGroupSeparateurInFile(formViewFile.getBody());
        createGroupTypeFichierInFile(formViewFile.getBody());

        Composite right = new Composite(parent, SWT.BORDER);
        right.setBackground(backgroungEdit);
        right.setLayout(new FillLayout());

        editFileForm = new EditFileForm();
        editFileForm.createEditionForm(right);
    }

    private void createGroupChoixFichierInFile(Composite parent)
    {

        Composite compositeChoixFichier = new Composite(parent, SWT.NONE);
        // compositeChoixFichier.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(
            compositeChoixFichier);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            compositeChoixFichier);

        Label labelCampagne = new Label(compositeChoixFichier, SWT.NONE);
        labelCampagne.setText(Messages.view_file_processCruise + "           "); //$NON-NLS-1$

        Label labelCollection = new Label(compositeChoixFichier, SWT.NONE);
        labelCollection.setText(Messages.view_file_processCollection);

        radioFichier = new Button(compositeChoixFichier, SWT.RADIO);
        radioFichier.setSelection(true);
        radioFichier.setText(Messages.view_file_cruiseFile);
        addRadFileListener(radioFichier);

        radioCollectCas1 = new Button(compositeChoixFichier, SWT.RADIO);
        radioCollectCas1.setText(Messages.view_file_collectionCas1);
        addRadFileListener(radioCollectCas1);

        radioDossier = new Button(compositeChoixFichier, SWT.RADIO);
        radioDossier.setText(Messages.view_file_cruiseDir);
        addRadFileListener(radioDossier);

        radioCollectCasN = new Button(compositeChoixFichier, SWT.RADIO);
        radioCollectCasN.setText(Messages.view_file_collectionCasN);
        addRadFileListener(radioCollectCasN);

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
                        enableCruise(true);

                        Shell shell =
                            PlatformUI.getWorkbench().getDisplay()
                                .getActiveShell();

                        FileDialog dialog =
                            new FileDialog(shell, SWT.OPEN
                                    | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration
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
                                    textAreaCruise, true);
                                textAreaFile.setText(Utils.texteBrut);

                                // affichage etape0 dans formulaire d'edition
                                ViewMain.editFileForm.afficheEdition();
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

                        dossierCampagne = true;

                        // reactivation de l'onglet campagne
                        enableCruise(true);

                        Shell shell =
                            PlatformUI.getWorkbench().getDisplay()
                                .getActiveShell();

                        DirectoryDialog dialog =
                            new DirectoryDialog(shell, SWT.OPEN
                                    | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration
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
                                ViewMain.editFileForm.afficheEdition();
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

                        dossierCampagne = true;
                        collectionCas1 = true;

                        // desactivation de l'onglet campagne
                        enableCruise(false);

                        Shell shell =
                            PlatformUI.getWorkbench().getDisplay()
                                .getActiveShell();

                        DirectoryDialog dialog =
                            new DirectoryDialog(shell, SWT.OPEN
                                    | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration
                            .getCruisesDirectory());
                        nomRepertoire = dialog.open();

                        if (nomRepertoire != null)
                        {
                            fileLoaded = true;

                            if (Utils
                                .findFilesCollectCas1(nomRepertoire, false))
                            {
                                // affichage du corps du fichier
                                textAreaFile.setText(Utils.texteBrut);

                                // affichage etape0 dans formulaire d'edition
                                ViewMain.editFileForm.afficheEdition();

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
                        enableCruise(false);

                        Shell shell =
                            PlatformUI.getWorkbench().getDisplay()
                                .getActiveShell();

                        DirectoryDialog dialog =
                            new DirectoryDialog(shell, SWT.OPEN
                                    | SWT.APPLICATION_MODAL);
                        dialog.setFilterPath(Utils.configuration
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
                                ViewMain.editFileForm.afficheEdition();

                                // dans le cas d'une collection, on ne peut
                                // generer que le format ODV
                                enableCollectionMedatlas(false);
                                radMonoFichier.setEnabled(false); // FAE 0001134
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

    private void addRadFileListener(final Button radio)
    {
        radio
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent g)
                {
                    init();
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

    protected void createContentCruise(Composite parent)
    {
        parent.setLayout(new FillLayout());

        Composite left = new Composite(parent, SWT.BORDER);
        left.setBackground(parent.getBackground());
        left.setLayout(new FillLayout());

        textFileCruise = new Composite(left, SWT.NONE);
        textFileCruise.setLayout(new FillLayout());
        textAreaCruise =
            new StyledText(textFileCruise, SWT.MULTI | SWT.READ_ONLY
                    | SWT.H_SCROLL | SWT.V_SCROLL);
        textAreaCruise.setFont(Utils.fontCourier);

        Composite right = new Composite(parent, SWT.BORDER);
        right.setBackground(backgroungEdit);
        right.setLayout(new FillLayout());

        editCruiseForm = new EditCruiseForm();
        editCruiseForm.createEditionForm(right);

    }

    protected void createContentStation(Composite parent)
    {
        parent.setLayout(new FillLayout());

        Composite left = new Composite(parent, SWT.BORDER);
        left.setBackground(parent.getBackground());
        left.setLayout(new FillLayout());

        Composite textFile = new Composite(left, SWT.NONE);
        textFile.setLayout(new FillLayout());
        textAreaStation =
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                    | SWT.V_SCROLL);
        textAreaStation.setFont(Utils.fontCourier);

        Composite right = new Composite(parent, SWT.BORDER);
        right.setBackground(backgroungEdit);
        right.setLayout(new FillLayout());

        editStationForm = new EditStationForm();
        editStationForm.createEditionForm(right);

    }

    protected void createContentData(Composite parent)
    {
        parent.setLayout(new FillLayout());

        Composite left = new Composite(parent, SWT.BORDER);
        left.setBackground(parent.getBackground());
        left.setLayout(new FillLayout(SWT.VERTICAL));

        Composite textFile = new Composite(left, SWT.NONE);
        textFile.setLayout(new FillLayout());
        textAreaData =
            new StyledText(textFile, SWT.MULTI | SWT.READ_ONLY | SWT.H_SCROLL
                    | SWT.V_SCROLL);
        textAreaData.setFont(Utils.fontCourier);

        compositeData = new Composite(left, SWT.NONE);
        compositeData.setLayout(new FillLayout());
        TableData.go();
        
        Composite right = new Composite(parent, SWT.BORDER);
        right.setBackground(backgroungEdit);
        right.setLayout(new FillLayout());

        editDataForm = new EditDataForm();
        editDataForm.createEditionForm(right);


    }

    protected void createContentConversion(Composite parent)
    {
        parent.setLayout(new FillLayout());

        Composite left = new Composite(parent, SWT.BORDER);
        left.setBackground(parent.getBackground());
        left.setLayout(new FillLayout());

        Composite compositeConversion = new Composite(left, SWT.NONE);
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
        addRadConvertListener(radMedatlas);

        // Composite vide pour mise en page
        Composite compoVide1 = new Composite(group, SWT.NONE);

        radODV = new Button(group, SWT.RADIO);
        radODV.setText(Conversion.ODV.getName());
        radODV.setSelection(false);
        addRadConvertListener(radODV);

        grpODV = new Group(group, SWT.NONE);
        grpODV.setLayout(layout2);
        // afficheGrpODV();

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

        Composite right = new Composite(parent, SWT.BORDER);
        right.setBackground(backgroungEdit);
        right.setLayout(new FillLayout());

        editConversionForm = new EditConversionForm();
        editConversionForm.createEditionForm(right);

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
                }
            });
    }

    protected void init()
    {

        // reinitialisation de la conversion
        if (DataModel.inputModel != null)
            DataModel.reset();

        fileLoaded = false;
        Preformat.setPreformat(false);

        // reinitialisation de l'affichage
        // --- les formulaires
        Utils.razGlobal();
        ViewMain.editFileForm.loaded = false;

        // --- renitialisation du tableau de paramètres (onglet Data)
        if (tableData != null)
            TableData.go();

        // --- le titre de l'application
        ApplicationWorkbenchWindowAdvisor.configurer_titre
            .setTitle(Intern.application_title);

        // --- le contenu du fichier
        textAreaFile.setText(Global.EMPTY_STRING);

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

    protected void enableCruise(boolean enabled)
    {
        if (enabled)
        {
            if (tabCruise.isDisposed())
            {
                tabCruise = new TabItem(tabFolder, SWT.NONE, 1);
                tabCruise.setText(Messages.onglet_cruise);
                tabCruise.setControl(textFileCruise);
            }
        }
        else
        {
            tabCruise.dispose();
        }
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
        if (!isCollection())
        {
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

    public static void enableChoice(boolean enabled)
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

    public static void selectTypeFile(String typeFile)
    {

        if (Global.PROFILE.equals(typeFile))
        {
            radProfilVertical.setSelection(true);
            radSerieTempo.setSelection(false);
            radTrajectoire.setSelection(false);
        }
        else if (Global.TIMESERIES.equals(typeFile))
        {
            radSerieTempo.setSelection(true);
            radProfilVertical.setSelection(false);
            radTrajectoire.setSelection(false);
        }
        else if (Global.TRAJECTORY.equals(typeFile))
        {
            radProfilVertical.setSelection(false);
            radSerieTempo.setSelection(false);
            radTrajectoire.setSelection(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus()
    {
        // TODO Auto-generated method stub
    }

}
