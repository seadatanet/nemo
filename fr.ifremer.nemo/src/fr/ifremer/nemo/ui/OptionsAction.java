package fr.ifremer.nemo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

import fr.ifremer.nemo.config.EdmoHandler;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class OptionsAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{

    private final IWorkbenchWindow window;

    public static Composite top, compoHaut, compoLCD, compoBouton, compoWS,
            compoWSVocab1, compoWSVocab2, compoFlag = null;

    public static Group groupLangue, groupCentreDonnees, groupWS, groupDossier,
            groupDossier1, groupDossier2, groupCDI, groupMapping, grpFlag = null;

    public static Combo comboLangue, comboCentreDonnees, comboIdEdmoRespCdi,
            comboMethodeCdi, comboCodeEdmoDonnees, comboIdEdmoRespDonnees,
            comboIdEdmoDistDonnees, comboTypePlateForme,
            comboAccesDonnees = null;

    public static Button buttonCancel, buttonValider, buttonParcourirModele,
            buttonParcourirCampagne, buttonArboMapping = null;

    public static Label labelNemo, labelNautilus, labelCampagne, lblCDI,
            labelIdEdmoRespCdi, labelUrlCdi, labelMethodeCdi,
            labelCodeEdmoDonnees, labelIdEdmoRespDonnees,
            labelIdEdmoDistDonnees, labelWSVocab, labelMapping, labelFichierMapping,
            labelArboMapping, lblTypePlateForme, lblAccesDonnees = null;

    public static Button checkCDI, checkMapping;

    public static Text textAreaNemo, textAreaNautilus, textDossierModele,
            textDossierCampagne, textUrlCdi, textVocabURL, textFichierMapping,
            textArboMapping = null;

    public static Table tabFlagTable, tabFlag;

    public static String _defaultCode = Global.EMPTY_STRING;

    public static FlagTable _flagTable = new FlagTable();

    public static ArrayList<FlagTable> _flagTables = new ArrayList<FlagTable>();

    public final static String ID = "fr.ifremer.nemo.ui.OptionsAction"; //$NON-NLS-1$

    // vars nécessaires à la fenêtre d'options

    public static Shell shelloptions;

    public OptionsAction(IWorkbenchWindow window)
    {

        this.window = window;
        setId(ID);
        setText(Messages.getString("OptionsAction.1")); //$NON-NLS-1$
        window.getSelectionService().addSelectionListener(this);
    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);

    }

    public void run()
    {
        // lancement IHM options
        // System.out.println("Menu options"); //$NON-NLS-1$

        shelloptions =
            new Shell(Application.display, SWT.BORDER | SWT.TITLE
                    | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);

        shelloptions.setSize(600, 750);
        shelloptions.setText(Messages.getString("OptionsAction.3")); //$NON-NLS-1$
        shelloptions.setLayout(new GridLayout());

        _flagTables.clear();
        for (int i = 0; i < Utils.configuration.getFlagTables().size(); i++)
        {
            FlagTable flagTable =
                new FlagTable(Utils.configuration.getFlagTables().get(i));
            _flagTables.add(flagTable);
        }

        createPartControl(shelloptions);

        // shelloptions.pack();
        shelloptions.open();

        // chargement des options
        Utils.initConfig();

        remplissageOptions();
        
        

    }

    public void remplissageOptions()
    {
        // remplissage IHM avec les valeurs du fichier de config

        // DOSSIERS
        textDossierModele.setText(Utils.configuration.getModelsDirectory());
        textDossierCampagne.setText(Utils.configuration.getCruisesDirectory());

        // LANGUE
        if (Utils.configuration.getLocale().toString().equals("fr_FR")) { //$NON-NLS-1$
            // on met la combo langue à FR
            comboLangue.select(0);
        }
        else
        {
            // on met la combo langue à GB
            comboLangue.select(1);
        }

        // CENTRE DE DONNEES
        CodeTableRecordType[] centres = NemoLists.getVocabList("L300");

        TreeMap<String, String> treeCentre = new TreeMap<String, String>();

        for (CodeTableRecordType record : centres)
        {
            treeCentre.put(Utils.extraitCodeURN(record.getEntryKey()), record
                .getEntryTerm());
        }

        NemoLists.listeCentreDonnees = new ArrayList<String>();
        String temp = "";
        for (Entry<String, String> currentEntry : treeCentre.entrySet())
        {
            temp = currentEntry.getKey() + " - " + currentEntry.getValue();
            NemoLists.listeCentreDonnees.add(temp);
            comboCentreDonnees.add(temp);
        }

        Utils.selectComboCode(comboCentreDonnees, Utils.configuration
            .getDataCenter());

        // WS
        textAreaNemo.setText(Utils.configuration.getNemoWebServiceURL());
        textAreaNautilus
            .setText(Utils.configuration.getNautilusWebServiceURL());

        textVocabURL.setText(Utils.configuration.getVocabURL());

        // CDI
        HashMap<Integer, String> edmoHash = NemoLists.getEdmo();

        TreeMap<Integer, String> edmoTree = new TreeMap<Integer, String>();

        EdmoHandler.listeOrigine = new ArrayList<String>();

        for (Entry<Integer, String> currentEntry : edmoHash.entrySet())
        {
            edmoTree.put(currentEntry.getKey(), currentEntry.getValue());
        }

        String strTemp = "";
        for (Entry<Integer, String> currentEntry : edmoTree.entrySet())
        {
            strTemp = currentEntry.getKey() + " - " + currentEntry.getValue();
            EdmoHandler.listeOrigine.add(strTemp);
            comboIdEdmoRespCdi.add(strTemp);
            comboCodeEdmoDonnees.add(strTemp);
            comboIdEdmoRespDonnees.add(strTemp);
            comboIdEdmoDistDonnees.add(strTemp);
        }

        checkCDI.setSelection(Utils.configuration.isWriteCdi());
        
        Utils.selectComboCode(comboIdEdmoRespCdi, Utils.configuration
            .getIdEdmoRespCdi());
        Utils.selectComboCode(comboCodeEdmoDonnees, Utils.configuration
            .getCodeEdmoDonnees());
        Utils.selectComboCode(comboIdEdmoRespDonnees, Utils.configuration
            .getIdEdmoRespDonnees());
        Utils.selectComboCode(comboIdEdmoDistDonnees, Utils.configuration
            .getIdEdmoDistDonnees());

        textUrlCdi.setText(Utils.configuration.getCdiURL());
        
        // FAE 58 CDL 09/04/08

        CodeTableRecordType[] records = NemoLists.getVocabList("L071");

        TreeSet<String> champsMethodesCdi = new TreeSet<String>();

        for (CodeTableRecordType record : records)
        {
            String champCombo = "";
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] = record.getEntryKey().split("/");
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += " - " + record.getEntryTerm();
            champsMethodesCdi.add(champCombo);
        }

        for (String champ : champsMethodesCdi)
        {
            comboMethodeCdi.add(champ);
        }

        Utils.selectComboCode(comboMethodeCdi, Utils.configuration
            .getCdiMethod());

        records = NemoLists.getVocabList("L061");

        champsMethodesCdi = new TreeSet<String>();

        for (CodeTableRecordType record : records)
        {
            String champCombo = "";
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] = record.getEntryKey().split("/");
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += " - " + record.getEntryTerm();
            champsMethodesCdi.add(champCombo);
        }

        for (String champ : champsMethodesCdi)
            comboTypePlateForme.add(champ);

        records = NemoLists.getVocabList("L081");

        champsMethodesCdi = new TreeSet<String>();

        for (CodeTableRecordType record : records)
        {
            String champCombo = "";
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] = record.getEntryKey().split("/");
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += " - " + record.getEntryTerm();
            champsMethodesCdi.add(champCombo);
        }

        for (String champ : champsMethodesCdi)
            comboAccesDonnees.add(champ);

        Utils.selectComboCode(comboAccesDonnees, Utils.configuration
            .getAccesDonnees());
        Utils.selectComboCode(comboTypePlateForme, Utils.configuration
            .getTypePlateForme());

        // MAPPING
        checkMapping.setSelection(Utils.configuration.isWriteMapping());
        textFichierMapping.setText(Utils.configuration.getFichierMapping());
        
        redLabels();
        
        CDICheckMask(Utils.configuration.isWriteCdi());
        mappingCheckMask(Utils.configuration.isWriteMapping());

        // FLags
        fillFlagTable();
    }
    
    protected void CDICheckMask (Boolean status)
    {
        //labelIdEdmoRespCdi.setEnabled(status);
        //comboIdEdmoRespCdi.setEnabled(status);
        labelCodeEdmoDonnees.setEnabled(status);
        comboCodeEdmoDonnees.setEnabled(status);
        labelIdEdmoRespDonnees.setEnabled(status);
        comboIdEdmoRespDonnees.setEnabled(status);
        labelIdEdmoDistDonnees.setEnabled(status);
        comboIdEdmoDistDonnees.setEnabled(status);
        labelUrlCdi.setEnabled(status);
        textUrlCdi.setEnabled(status);
        labelMethodeCdi.setEnabled(status);
        comboMethodeCdi.setEnabled(status);
        lblTypePlateForme.setEnabled(status);
        comboTypePlateForme.setEnabled(status);
        lblAccesDonnees.setEnabled(status);
        comboAccesDonnees.setEnabled(status);         
        
    }
    
    protected void mappingCheckMask(Boolean enabled){
        labelFichierMapping.setEnabled(enabled);
        textFichierMapping.setEnabled(enabled);
        
        ApplicationActionBarAdvisor.mappingDatabaseEditAction.setEnabled(enabled);
        ApplicationActionBarAdvisor.mappingDatabaseExportAction.setEnabled(enabled);
        ApplicationActionBarAdvisor.mappingDatabaseImportAction.setEnabled(enabled);
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }

    /**
     * This method initializes compoHaut
     * 
     */
    private void createCompoHaut()
    {
        compoHaut = new Composite(top, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoHaut);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compoHaut);

        createCompoLCD();
        createGroupDossier();
    }

    /**
     * This method initializes compoLCD
     * 
     */
    private void createCompoLCD()
    {
        compoLCD = new Composite(compoHaut, SWT.NONE);

        GridLayoutFactory.fillDefaults().applyTo(compoLCD);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compoLCD);

        createGroupLangue();
        createGroupCentreDonnees();
    }

    /**
     * This method initializes groupLangue
     * 
     */
    private void createGroupLangue()
    {
        groupLangue = new Group(compoLCD, SWT.NONE);
        // groupLangue.setLayout(null);
        groupLangue.setText(Messages.getString("OptionsAction.10")); //$NON-NLS-1$
        createComboLangue();
        // groupLangue.setBounds(new Rectangle(4, 10, 168, 65)); // 166
        GridLayoutFactory.fillDefaults().applyTo(groupLangue);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(groupLangue);
        groupLangue.pack();
    }

    /**
     * This method initializes comboLangue
     * 
     */
    private void createComboLangue()
    {
        comboLangue = new Combo(groupLangue, SWT.READ_ONLY);
        // comboLangue.setBounds(new Rectangle(2, 16, 160, 21));

        comboLangue.add(Messages.getString("OptionsAction.11")); //$NON-NLS-1$
        comboLangue.add(Messages.getString("OptionsAction.12")); //$NON-NLS-1$
        comboLangue.select(0);
    }

    /**
     * This method initializes groupCentreDonnees
     * 
     */
    private void createGroupCentreDonnees()
    {
        groupCentreDonnees = new Group(compoLCD, SWT.NONE);
        groupCentreDonnees.setText(Messages.getString("OptionsAction.20"));
        GridLayoutFactory.fillDefaults().applyTo(groupCentreDonnees);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(
            groupCentreDonnees);

        createComboCentreDonnees();
    }

    /**
     * This method initializes comboCentreDonnees
     * 
     */
    private void createComboCentreDonnees()
    {
        comboCentreDonnees = new Combo(groupCentreDonnees, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboCentreDonnees);
        comboCentreDonnees.pack();

        Utils.createSearchMenu(comboCentreDonnees, Global.DATACENTRE);
    }

    /**
     * This method initializes groupTop3
     * 
     */
    private void createCompoBouton()
    {
        compoBouton = new Composite(top, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoBouton);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER,
            SWT.NONE).applyTo(compoBouton);

        buttonValider = new Button(compoBouton, SWT.NONE);
        buttonValider.setText(Messages.getString("OptionsAction.17")); //$NON-NLS-1$
        buttonValider.pack();

        buttonCancel = new Button(compoBouton, SWT.NONE);
        buttonCancel.setText(Messages.getString("OptionsAction.16")); //$NON-NLS-1$
        buttonCancel.pack();

        compoBouton.pack();
    }

    /**
     * This method initializes groupDossier
     * 
     */
    private void createGroupDossier()
    {
        groupDossier = new Group(compoHaut, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(groupDossier);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(groupDossier);

        // groupDossier.setLayout(null);
        groupDossier.setText(Messages.getString("OptionsAction.5")); //$NON-NLS-1$
        // groupDossier.setBounds(new Rectangle(176, 10, 242, 150));

        /*
         * labelModele = new Label(groupDossier, SWT.NONE); //
         * labelModele.setBounds(new Rectangle(13, 15, 62, 13));
         * labelModele.setText(Messages.getString("OptionsAction.18"));
         * //$NON-NLS-1$ labelModele.pack();
         */

        groupDossier1 = new Group(groupDossier, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupDossier1);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(groupDossier1);
        groupDossier1.setText(Messages.getString("OptionsAction.18")); //$NON-NLS-1$
        groupDossier1.pack();

        textDossierModele = new Text(groupDossier1, SWT.BORDER);
        // textDossierModele.setBounds(new Rectangle(5, 31, 229, 19));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            textDossierModele);
        buttonParcourirModele = new Button(groupDossier1, SWT.NONE);
        // buttonParcourirModele.setBounds(new Rectangle(170, 53, 64, 23)); //
        // 64
        buttonParcourirModele.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
        buttonParcourirModele.pack();

        /*
         * labelCampagne = new Label(groupDossier, SWT.NONE); //
         * labelCampagne.setBounds(new Rectangle(13, 80, 62, 13));
         * labelCampagne.setText(Messages.getString("OptionsAction.19"));
         * //$NON-NLS-1$ labelCampagne.pack();
         */

        groupDossier2 = new Group(groupDossier, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupDossier2);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(groupDossier2);
        groupDossier2.setText(Messages.getString("OptionsAction.19")); //$NON-NLS-1$
        groupDossier2.pack();

        textDossierCampagne = new Text(groupDossier2, SWT.BORDER);
        // textDossierCampagne.setBounds(new Rectangle(5, 96, 229, 19));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            textDossierCampagne);
        buttonParcourirCampagne = new Button(groupDossier2, SWT.NONE);
        // buttonParcourirCampagne.setBounds(new Rectangle(170, 118, 64, 23));
        // // 64
        buttonParcourirCampagne.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
        buttonParcourirCampagne.pack();

        // listener sur le bouton
        buttonParcourirModele
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    // System.out.println(Messages
                    // .getString("OptionsAction.7")); //$NON-NLS-1$

                    String nomRepertoire;
                    Shell shell =
                        PlatformUI.getWorkbench().getDisplay().getActiveShell();
                    DirectoryDialog dialog =
                        new DirectoryDialog(shell, SWT.OPEN
                                | SWT.APPLICATION_MODAL);
                    nomRepertoire = dialog.open();
                    if (nomRepertoire != null)
                    {
                        textDossierModele.setText(nomRepertoire);

                    }
                    else
                    {

                        // System.out.println("pas de dossier selectionne");
                        // //$NON-NLS-1$
                        ViewLog.styledTextLogErreurs.setText(Messages
                            .getString("OptionsAction.9")); //$NON-NLS-1$

                    }

                }
            });

        // listener sur le bouton
        buttonParcourirCampagne
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    // System.out.println(Messages
                    // .getString("OptionsAction.7")); //$NON-NLS-1$

                    String nomRepertoire;
                    Shell shell =
                        PlatformUI.getWorkbench().getDisplay().getActiveShell();
                    DirectoryDialog dialog =
                        new DirectoryDialog(shell, SWT.OPEN
                                | SWT.APPLICATION_MODAL);
                    nomRepertoire = dialog.open();
                    if (nomRepertoire != null)
                    {
                        textDossierCampagne.setText(nomRepertoire);

                    }
                    else
                    {

                        // System.out.println("pas de dossier selectionne");
                        // //$NON-NLS-1$
                        ViewLog.styledTextLogErreurs.setText(Messages
                            .getString("OptionsAction.9")); //$NON-NLS-1$

                    }

                }
            });
    }

    /**
     * This method initializes groupWS
     * 
     */
    private void createGroupWS()
    {
        groupWS = new Group(top, SWT.NONE);
        // groupWS.setLayout(null);
        groupWS.setText("Web Services"); //$NON-NLS-1$
        // groupWS.setBounds(new Rectangle(4, 169, 414, 146));
        GridLayoutFactory.fillDefaults().applyTo(groupWS);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(groupWS);

        compoWS = new Composite(groupWS, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoWS);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compoWS);

        labelNemo = new Label(compoWS, SWT.NONE);
        // labelNemo.setBounds(new Rectangle(13, 40, 62, 13));
        labelNemo.setText("NEMO"); //$NON-NLS-1$
        labelNemo.pack();

        textAreaNemo = new Text(compoWS, SWT.BORDER);
        // textAreaNemo.setBounds(new Rectangle(85, 18, 316, 55));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(textAreaNemo);

        labelNautilus = new Label(compoWS, SWT.NONE);
        // labelNautilus.setBounds(new Rectangle(13, 100, 62, 13));
        labelNautilus.setText("NAUTILUS"); //$NON-NLS-1$
        labelNautilus.pack();

        textAreaNautilus = new Text(compoWS, SWT.BORDER);
        // textAreaNautilus.setBounds(new Rectangle(85, 82, 316, 55));
        GridDataFactory.fillDefaults().grab(true, true).applyTo(
            textAreaNautilus);

        // WS Vocab
        compoWSVocab1 = new Composite(groupWS, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoWSVocab1);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compoWSVocab1);

        labelWSVocab = new Label(compoWSVocab1, SWT.NONE);
        labelWSVocab.setText(Messages.getString("OptionsAction.28"));
        textVocabURL = new Text(compoWSVocab1, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textVocabURL);
    }

    /**
     * This method initializes groupCDI
     * 
     */
    private void createGroupCDI()
    {
        compoHaut = new Composite(top, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compoHaut);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compoHaut);
        
        // FAE 1123 : Extraire le centre de données responsable du CDI du bloc CDI.
        labelIdEdmoRespCdi = new Label(compoHaut, SWT.NONE);
        labelIdEdmoRespCdi.setText(Messages.getString("OptionsAction.21"));
        labelIdEdmoRespCdi.pack();

        comboIdEdmoRespCdi = new Combo(compoHaut, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoRespCdi);
        
        
        groupCDI = new Group(top, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupCDI);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(groupCDI);

        // compo vide pour mise en forme
        lblCDI = new Label(groupCDI, SWT.NONE);
        lblCDI.setText(Messages.getString("OptionsAction.44"));
        checkCDI = new Button(groupCDI, SWT.CHECK);

        //On masque/affiche les champs falcutatif en fonction de la case checkCDI
        checkCDI.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                CDICheckMask(checkCDI.getSelection());
            }
        });  
        
        labelCodeEdmoDonnees = new Label(groupCDI, SWT.NONE);
        labelCodeEdmoDonnees.setText(Messages.getString("OptionsAction.24"));
        labelCodeEdmoDonnees.pack();

        comboCodeEdmoDonnees = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboCodeEdmoDonnees);

        labelIdEdmoRespDonnees = new Label(groupCDI, SWT.NONE);
        labelIdEdmoRespDonnees.setText(Messages.getString("OptionsAction.25"));
        labelIdEdmoRespDonnees.pack();

        comboIdEdmoRespDonnees =
            new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoRespDonnees);

        labelIdEdmoDistDonnees = new Label(groupCDI, SWT.NONE);
        labelIdEdmoDistDonnees.setText(Messages.getString("OptionsAction.26"));
        labelIdEdmoDistDonnees.pack();

        comboIdEdmoDistDonnees =
            new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoDistDonnees);

        labelUrlCdi = new Label(groupCDI, SWT.NONE);
        labelUrlCdi.setText(Messages.getString("OptionsAction.22"));
        labelUrlCdi.pack();

        textUrlCdi = new Text(groupCDI, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textUrlCdi);

        labelMethodeCdi = new Label(groupCDI, SWT.NONE);
        labelMethodeCdi.setText(Messages.getString("OptionsAction.23"));
        labelMethodeCdi.pack();

        comboMethodeCdi = new Combo(groupCDI, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboMethodeCdi);

        // Menu recherche combos
        Utils.createSearchMenu(comboIdEdmoRespCdi, Global.IDEDMO);
        Utils.createSearchMenu(comboCodeEdmoDonnees, Global.IDEDMO);
        Utils.createSearchMenu(comboIdEdmoRespDonnees, Global.IDEDMO);
        Utils.createSearchMenu(comboIdEdmoDistDonnees, Global.IDEDMO);
        
        // FAE 59 CDL 09/04/08
        lblTypePlateForme = new Label(groupCDI, SWT.NONE);
        lblTypePlateForme
            .setText(Messages.getString("ViewFile.typePlateForme"));

        comboTypePlateForme = new Combo(groupCDI, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
            comboTypePlateForme);

        lblAccesDonnees = new Label(groupCDI, SWT.NONE);
        lblAccesDonnees.setText(Messages.getString("ViewFile.accesDonnees"));

        comboAccesDonnees = new Combo(groupCDI, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
            comboAccesDonnees);
    }
    


    /**
     * This method initializes groupMapping
     * 
     */
    private void createGroupMapping()
    {
        groupMapping = new Group(top, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(groupMapping);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(groupMapping);

        labelMapping = new Label(groupMapping, SWT.NONE);
        labelMapping.setText(Messages.getString("OptionsAction.45")); //$NON-NLS-1$
        checkMapping = new Button(groupMapping, SWT.CHECK);

        checkMapping.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                //On masque/affiche les champs falcutatif en fonction de la case checkMapping
                mappingCheckMask(checkMapping.getSelection());
            }
        }); 
        
        labelFichierMapping = new Label(groupMapping, SWT.NONE);
        labelFichierMapping.setText(Messages.getString("OptionsAction.31")); //$NON-NLS-1$
        labelFichierMapping.pack();

        textFichierMapping = new Text(groupMapping, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            textFichierMapping);
    }

    /**
     * This method initializes groupFlag
     * 
     */
    private void createGroupFlag()
    {
        grpFlag = new Group(top, SWT.NONE);
        grpFlag.setText(Messages.getString("OptionsAction.33"));
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(grpFlag);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(grpFlag);

        // Creation du tableau des tables de correspondaces
        createTableFlagTable();

        // Creation du tableau des correspondaces
        createTableFlag();
    }

    public void createTableFlagTable()
    {
        tabFlagTable = new Table(grpFlag, SWT.FULL_SELECTION | SWT.BORDER);
        tabFlagTable.setHeaderVisible(true);
        tabFlagTable.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlagTable);

        TableColumn tableColumn1 = new TableColumn(tabFlagTable, SWT.NONE);
        tableColumn1.setWidth(60);
        tableColumn1.setText(Messages.getString("OptionsAction.34")); //$NON-NLS-1$

        TableColumn tableColumn2 = new TableColumn(tabFlagTable, SWT.NONE);
        tableColumn2.setWidth(200);
        tableColumn2.setText(Messages.getString("OptionsAction.35")); //$NON-NLS-1$	

        // Menu contextuel
        Menu menu = new Menu(tabFlagTable.getShell(), SWT.POP_UP);
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setText(Messages.getString("OptionsAction.38")); //$NON-NLS-1$
        MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
        itemRemove.setText(Messages.getString("OptionsAction.39")); //$NON-NLS-1$
        MenuItem itemDefault = new MenuItem(menu, SWT.NONE);
        itemDefault.setText(Messages.getString("OptionsAction.40"));
        tabFlagTable.setMenu(menu);

        final TableEditor editor = new TableEditor(tabFlagTable);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        final Table table = tabFlagTable;

        tabFlagTable.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                // On affiche les flags correspondants
                _flagTable =
                    FlagTable.getFlagTable(_flagTables, tabFlagTable
                        .getSelection()[0].getText());
                _flagTable.fillTable(tabFlag);

                // Saisie
                if (event.button == 3)
                {
                    return;
                }
                Control old = editor.getEditor();
                if (old != null)
                    old.dispose();

                Point pt = new Point(event.x, event.y);

                final TableItem item = tabFlagTable.getItem(pt);
                if (item == null)
                {
                    return;
                }
                int column = -1;
                for (int i = 0, n = tabFlagTable.getColumnCount(); i < n; i++)
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt))
                    {
                        column = i;
                        break;
                    }
                }

                if (column == -1)
                {
                    return;
                }

                final Text text = new Text(tabFlagTable, SWT.NONE);
                text.setText(item.getText(column));
                text.setForeground(item.getForeground());
                text.selectAll();
                text.setFocus();

                editor.minimumWidth = text.getBounds().width;

                editor.setEditor(text, item, column);

                final int col = column;
                text.addModifyListener(new ModifyListener() {
                    public void modifyText(ModifyEvent event)
                    {
                        switch (col)
                        {
                            case 0:
                                _flagTable.setCode(text.getText());
                            break;
                            case 1:
                                _flagTable.setLibelle(text.getText());
                            break;
                            default:
                        }

                        item.setText(col, text.getText());
                    }
                });
            }
        });

        itemDefault.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                setDefaultCode(tabFlagTable.getSelection()[0].getText());
                // Mise en gras du flagtable par defaut

                Font normal = new Font(Application.display, "", 8, SWT.NORMAL);
                Font bold = new Font(Application.display, "", 8, SWT.BOLD);

                for (int i = 0; i < tabFlagTable.getItemCount(); i++)
                {
                    if (tabFlagTable.getItem(i).getText(0).equals(
                        getDefaultCode()))
                    {
                        tabFlagTable.getItem(i).setFont(bold);
                    }
                    else
                    {
                        tabFlagTable.getItem(i).setFont(normal);
                    }
                }
            }
        });

        itemAdd.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                TableItem item = new TableItem(tabFlagTable, SWT.NONE);
                item.setText(0, Messages.getString("OptionsAction.41"));
                item.setText(1, Messages.getString("OptionsAction.41"));

                FlagTable temp = new FlagTable();
                temp.setCode(item.getText(0));
                temp.setLibelle(item.getText(1));
                getFlagTables().add(temp);
            }
        });

        itemRemove.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                getFlagTables().remove(_flagTable);
                tabFlagTable.remove(tabFlagTable.getSelectionIndex());
                tabFlag.clearAll();
            }
        });

        Utils.forceMenu(tabFlagTable, menu);
    }

    public void createTableFlag()
    {
        tabFlag = new Table(grpFlag, SWT.FULL_SELECTION | SWT.BORDER);
        tabFlag.setHeaderVisible(true);
        tabFlag.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlag);
        TableColumn flagColumn1 = new TableColumn(tabFlag, SWT.NONE);
        flagColumn1.setWidth(60);
        flagColumn1.setText(Messages.getString("OptionsAction.36")); //$NON-NLS-1$

        TableColumn flagColumn2 = new TableColumn(tabFlag, SWT.NONE);
        flagColumn2.setWidth(210);
        flagColumn2.setText(Messages.getString("OptionsAction.37")); //$NON-NLS-1$

        // Menu contextuel
        Menu menu = new Menu(tabFlag.getShell(), SWT.POP_UP);
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setText(Messages.getString("OptionsAction.38")); //$NON-NLS-1$
        MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
        itemRemove.setText(Messages.getString("OptionsAction.39")); //$NON-NLS-1$

        tabFlag.setMenu(menu);

        final Table tab = tabFlag;

        final TableEditor editor = new TableEditor(tabFlag);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        tabFlag.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event)
            {
                if (event.button == 3)
                {
                    return;
                }
                Control old = editor.getEditor();
                if (old != null)
                    old.dispose();

                Point pt = new Point(event.x, event.y);

                final TableItem item = tab.getItem(pt);
                if (item == null)
                {
                    return;
                }
                int column = -1;
                for (int i = 0, n = tab.getColumnCount(); i < n; i++)
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(pt))
                    {
                        column = i;
                        break;
                    }
                }

                if (column == -1)
                {
                    return;
                }

                final int col = column;
                final Text text = new Text(tab, SWT.NONE);
                final CCombo combo = new CCombo(tab, SWT.READ_ONLY);

                String[] flags = Flag.flagSDN();
                for (int i = 0; i < flags.length; i++)
                {
                    combo.add(flags[i]);
                }
                if (flags.length > 20)
                {
                    combo.setVisibleItemCount(20);
                }
                else
                {
                    combo.setVisibleItemCount(flags.length);
                }

                final Flag flag = _flagTable.getFlag(item.getText());

                switch (column)
                {
                    case 0:
                        text.setForeground(item.getForeground());

                        text.setText(item.getText(column));
                        text.setForeground(item.getForeground());
                        text.selectAll();
                        text.setFocus();

                        editor.minimumWidth = text.getBounds().width;

                        editor.setEditor(text, item, column);

                        text.addModifyListener(new ModifyListener() {
                            public void modifyText(ModifyEvent event)
                            {
                                flag.setLocal(text.getText());
                                item.setText(col, text.getText());
                            }
                        });

                    break;
                    case 1:
                        combo.setForeground(item.getForeground());

                        combo.setText(item.getText(column));
                        combo.setForeground(item.getForeground());
                        // combo.selectAll();
                        // combo.setFocus();
                        editor.minimumWidth = combo.getBounds().width;

                        editor.setEditor(combo, item, column);

                        combo.addModifyListener(new ModifyListener() {
                            public void modifyText(ModifyEvent event)
                            {
                                flag.setSdn(combo.getText());
                                item.setText(col, combo.getText());
                            }
                        });
                    break;
                    default:
                        return;
                }
            }
        });

        itemAdd.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                TableItem item = new TableItem(tab, SWT.NONE);
                item.setText(0, Messages.getString("OptionsAction.41"));
                item.setText(1, Messages.getString("OptionsAction.41"));

                Flag temp = new Flag(item.getText(0), item.getText(1));
                _flagTable.getFlags().add(temp);
            }
        });

        itemRemove.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e)
            {
                _flagTable.getFlags().remove(
                    _flagTable.getFlag(tab.getSelection()[0].getText()));
                tab.remove(tab.getSelectionIndex());
            }
        });

        Utils.forceMenu(tabFlag, menu);
    }

    public void createPartControl(Composite parent)
    {

        top = new Composite(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().applyTo(top);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(top);

        // top.setLayout(null);
        /*
         * createGroupLangue(); createGroupDossier();
         */
        createCompoHaut();
        createGroupWS();
        createGroupCDI();
        createGroupMapping();
        createGroupFlag();
        createCompoBouton();


        // listener cancel
        buttonCancel
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    // System.out.println("options cancel");
                    shelloptions.close();

                }
            });

        // listener valider
        buttonValider
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    // FAE 1123 : validation de la config
                    if (checkConfig()){                        
                        // sauvegarde de la config
                        Utils.saveConfig();
                        // fermeture de la fenêtre
                        shelloptions.close();
                    }else{
                        redLabels();
                    }
                    
                }
            });
    }
    
    protected boolean checkConfig() {
        boolean blnOK = true;
        StringBuffer message = new StringBuffer();
        
        if (checkCDI.getSelection())        
        {       
            message.append(checkCombo(comboIdEdmoRespCdi, "OptionsAction.21")); //$NON-NLS-1$
            message.append(checkCombo(comboCodeEdmoDonnees, "OptionsAction.24")); //$NON-NLS-1$
            message.append(checkCombo(comboIdEdmoRespDonnees, "OptionsAction.25")); //$NON-NLS-1$
            message.append(checkCombo(comboIdEdmoDistDonnees, "OptionsAction.26")); //$NON-NLS-1$
            message.append(checkText(textUrlCdi, "OptionsAction.22", " - ")); //$NON-NLS-1$ //$NON-NLS-2$
            message.append(checkCombo(comboMethodeCdi, "OptionsAction.23")); //$NON-NLS-1$
            message.append(checkCombo(comboTypePlateForme, "ViewFile.typePlateForme")); //$NON-NLS-1$

            if (message.length() > 0){
                message.insert(0, Messages.getString("OptionsAction.checkCDI")); //$NON-NLS-1$
                blnOK = false;
            }
        }    
        
        if (checkMapping.getSelection())        
        { 
            String errorMapping = checkText(textFichierMapping, "OptionsAction.checkMapping", Global.RN); //$NON-NLS-1$
            if (errorMapping.length() > 0){
                message.append(errorMapping);
                blnOK = false;
            }
        }
        
        
        if (!blnOK) {
            try {
                String[] buttons = new String[1];
                buttons[0] = "OK"; //$NON-NLS-1$ 
                MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), Messages.getString("CDIResumeWriter.configTitle"), null, message.toString(), MessageDialog.NONE, buttons, 0); //$NON-NLS-1$
                dialog.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return blnOK;
    }
    
    protected String checkCombo(Combo combo, String keyErrorMsg)
    {
        StringBuffer message = new StringBuffer();
        if (combo.getText().trim().equals(Global.EMPTY_STRING))
        {
            message.append(Global.RN);
            message.append(" - "); //$NON-NLS-1$
            message.append(Messages.getString(keyErrorMsg)); 
        }
        return message.toString();
    }
    
    protected String checkText(Text text, String keyErrorMsg, String sep)
    {
        StringBuffer message = new StringBuffer();
        if (text.getText().trim().equals(Global.EMPTY_STRING))
        {
            message.append(Global.RN);
            message.append(sep);
            message.append(Messages.getString(keyErrorMsg)); 
        }
        return message.toString();
    }
    
    protected void redLabels(){

        // Libelles rouges si parametres manquants
        redComboLabel(comboIdEdmoRespCdi, labelIdEdmoRespCdi);
        redComboLabel(comboCodeEdmoDonnees, labelCodeEdmoDonnees);
        redComboLabel(comboIdEdmoRespDonnees, labelIdEdmoRespDonnees);
        redComboLabel(comboIdEdmoDistDonnees, labelIdEdmoDistDonnees);
        redTextLabel(textUrlCdi, labelUrlCdi);
        redComboLabel(comboTypePlateForme, lblTypePlateForme);
        redTextLabel(textFichierMapping, labelFichierMapping);
    }
    
    protected void redComboLabel(Combo combo, Label label){
        if (combo.getText().trim().equals(Global.EMPTY_STRING)){
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }else{
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
    }
    
    protected void redTextLabel(Text text, Label label){
        if (text.getText().trim().equals(Global.EMPTY_STRING)){
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }else{
            label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
    }

    private void fillFlagTable()
    {
        int i;
        ArrayList<FlagTable> flagTables = new ArrayList<FlagTable>();
        flagTables =
            FlagTable.getOrderedFlagTables(Utils.configuration.getFlagTables());

        for (i = 0; i < flagTables.size(); i++)
        {
            TableItem item = new TableItem(tabFlagTable, SWT.NONE);
            item.setText(0, flagTables.get(i).getCode());
            item.setText(1, flagTables.get(i).getLibelle());

            // Mise en gras du flagtable par defaut
            if (flagTables.get(i).getCode().equals(
                Utils.configuration.getDefaultCode()))
            {
                setDefaultCode(flagTables.get(i).getCode());
                Font font = new Font(Application.display, "", 8, SWT.BOLD);
                item.setFont(font);
            }
        }

        if (flagTables.size() > 0)
        {
            tabFlagTable.setSelection(0);
            _flagTable =
                FlagTable.getFlagTable(_flagTables,
                    tabFlagTable.getSelection()[0].getText());
            _flagTable.fillTable(tabFlag);
        }
    }

    public static ArrayList<FlagTable> getFlagTables()
    {
        return _flagTables;
    }

    public static void setFlagTables(ArrayList<FlagTable> tables)
    {
        _flagTables = tables;
    }

    public static String getDefaultCode()
    {
        return _defaultCode;
    }

    public static void setDefaultCode(String code)
    {
        _defaultCode = code;
    }
}
