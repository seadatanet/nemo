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
import fr.ifremer.nemo.config.Proxy;
import fr.ifremer.nemo.domain.AddedParameter;
import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class OptionsAction extends Action implements ISelectionListener,
        ActionFactory.IWorkbenchAction
{

    private final IWorkbenchWindow window;

    public static Combo comboLangue, comboCentreDonnees, comboIdEdmoRespCdi,
            comboMethodeCdi, comboCodeEdmoDonnees, comboIdEdmoRespDonnees,
            comboIdEdmoDistDonnees, comboTypePlateForme,
            comboAccesDonnees = null;

    public static Button buttonCancel, buttonValider, buttonRestore,
                         buttonParcourirModele, buttonParcourirCampagne = null;

    public static Label labelNemo, labelNautilus, labelCampagne, lblCDI,
            labelIdEdmoRespCdi, labelUrlCdi, labelMethodeCdi,
            labelCodeEdmoDonnees, labelIdEdmoRespDonnees,
            labelIdEdmoDistDonnees, labelWSVocab, labelFichierMapping,
            labelWSEdmo, lblTypePlateForme, lblAccesDonnees = null;

    public static Button checkCDI, checkMapping, radDateISO, radDateJulian;

    public static Text textAreaNemo, textAreaNautilus, textDossierModele,
            textDossierCampagne, textUrlCdi, textVocabURL, textFichierMapping,
            textWSEdmo = null;

    public static Table tabFlagTable, tabFlag;

    public static String _defaultCode = Global.EMPTY_STRING;

    public static FlagTable _flagTable = new FlagTable();

    public static ArrayList<FlagTable> _flagTables = new ArrayList<FlagTable>();

    public final static String ID = "fr.ifremer.nemo.ui.OptionsAction"; //$NON-NLS-1$

    // vars nécessaires à la fenêtre d'options

    public static Shell shelloptions;

    private Composite top, compo, compo01, compo02;
    private Group group, group01, group02;
    
    
    // -- PROXY
    public static Button useProxy;
    private Label label, labelAdrProxy, labelPortProxy, labelLoginProxy, labelPassProxy;
    public static Text textAdrProxy, textPortProxy, textLoginProxy, textPassProxy;

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

    @Override
    public void run()
    {
        // lancement IHM options
        // System.out.println("Menu options"); //$NON-NLS-1$

        shelloptions =
            new Shell(Application.display, SWT.BORDER | SWT.TITLE
                    | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);

        shelloptions.setSize(600, 870);
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
        alimenteCombos();
        
        Utils.initConfig();
        remplissageOptions();

    }
    
    private void alimenteCombos(){

        // CENTRES DE DONNEES
        CodeTableRecordType[] centres = NemoLists.getVocabList("L300"); //$NON-NLS-1$
        TreeMap<String, String> treeCentre = new TreeMap<String, String>();
        for (CodeTableRecordType record : centres)
        {
            treeCentre.put(Utils.extraitCodeURN(record.getEntryKey()), record
                .getEntryTerm());
        }
        NemoLists.listeCentreDonnees = new ArrayList<String>();
        String temp = Global.EMPTY_STRING;
        for (Entry<String, String> currentEntry : treeCentre.entrySet())
        {
            temp = currentEntry.getKey() + Global.SEP + currentEntry.getValue();
            NemoLists.listeCentreDonnees.add(temp);
            comboCentreDonnees.add(temp);
        }
        
        // CDI
        // FAE 58 CDL 09/04/08
        CodeTableRecordType[] records = NemoLists.getVocabList("L071"); //$NON-NLS-1$
        TreeSet<String> champsMethodesCdi = new TreeSet<String>();
        for (CodeTableRecordType record : records)
        {
            String champCombo = Global.EMPTY_STRING;
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] = record.getEntryKey().split("/"); //$NON-NLS-1$
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += " - " + record.getEntryTerm(); //$NON-NLS-1$
            champsMethodesCdi.add(champCombo);
        }
        for (String champ : champsMethodesCdi)
        {
            comboMethodeCdi.add(champ);
        }

        records = NemoLists.getVocabList("L061"); //$NON-NLS-1$
        champsMethodesCdi = new TreeSet<String>();
        for (CodeTableRecordType record : records)
        {
            String champCombo = Global.EMPTY_STRING;
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] = record.getEntryKey().split("/"); //$NON-NLS-1$
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += " - " + record.getEntryTerm(); //$NON-NLS-1$
            champsMethodesCdi.add(champCombo);
        }
        for (String champ : champsMethodesCdi)
            comboTypePlateForme.add(champ);

        records = NemoLists.getVocabList("L081"); //$NON-NLS-1$
        champsMethodesCdi = new TreeSet<String>();
        for (CodeTableRecordType record : records)
        {
            String champCombo = Global.EMPTY_STRING;
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] = record.getEntryKey().split("/"); //$NON-NLS-1$
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += " - " + record.getEntryTerm(); //$NON-NLS-1$
            champsMethodesCdi.add(champCombo);
        }
        for (String champ : champsMethodesCdi)
            comboAccesDonnees.add(champ);

        
        // alimentation des combos EDMO
        HashMap<Integer, String> edmoHash = NemoLists.getEdmo();
        TreeMap<Integer, String> edmoTree = new TreeMap<Integer, String>();
        EdmoHandler.listeOrigine = new ArrayList<String>();
        for (Entry<Integer, String> currentEntry : edmoHash.entrySet())
        {
            edmoTree.put(currentEntry.getKey(), currentEntry.getValue());
        }
        String strTemp = Global.EMPTY_STRING;
        for (Entry<Integer, String> currentEntry : edmoTree.entrySet())
        {
            strTemp = currentEntry.getKey() + " - " + currentEntry.getValue(); //$NON-NLS-1$
            EdmoHandler.listeOrigine.add(strTemp);
            comboIdEdmoRespCdi.add(strTemp);
            comboCodeEdmoDonnees.add(strTemp);
            comboIdEdmoRespDonnees.add(strTemp);
            comboIdEdmoDistDonnees.add(strTemp);
        }
        
        
        // FLags
        fillFlagTable();
        
    }

    private void remplissageOptions()
    {
        // remplissage IHM avec les valeurs du fichier de config

        // DOSSIERS
        textDossierModele.setText(Utils.configuration.getModelsDirectory());
        textDossierCampagne.setText(Utils.configuration.getCruisesDirectory());

        // LANGUE
        if (Utils.configuration.getLocale().toString().equals(Intern.lang_fr)) {
            // on met la combo langue à FR
            comboLangue.select(0);
        }else{
            // on met la combo langue à GB
            comboLangue.select(1);
        }

        // CENTRE DE DONNEES
        Utils.selectComboCode(comboCentreDonnees, Utils.configuration.getDataCenter());

        // WS
        textAreaNemo.setText(Utils.configuration.getNemoWebServiceURL());
        textAreaNautilus.setText(Utils.configuration.getNautilusWebServiceURL());
        textVocabURL.setText(Utils.configuration.getVocabURL());
        textWSEdmo.setText(Utils.configuration.getEdmoURL());

        Proxy proxy = Utils.configuration.getProxy();
        useProxy.setSelection(proxy.isUse());
        textAdrProxy.setText(proxy.getAdress());
        textPortProxy.setText(String.valueOf(proxy.getPort()));
        textLoginProxy.setText(proxy.getLogin());
        textPassProxy.setText(proxy.getPassword());
        enableProxy(proxy.isUse());
        
        // EDMO code
        Utils.selectComboCode(comboIdEdmoRespCdi, Utils.configuration.getIdEdmoRespCdi());

        // CDI
        checkCDI.setSelection(Utils.configuration.isWriteCdi());
        Utils.selectComboCode(comboCodeEdmoDonnees, Utils.configuration.getCodeEdmoDonnees());
        Utils.selectComboCode(comboIdEdmoRespDonnees, Utils.configuration.getIdEdmoRespDonnees());
        Utils.selectComboCode(comboIdEdmoDistDonnees, Utils.configuration.getIdEdmoDistDonnees());
        textUrlCdi.setText(Utils.configuration.getCdiURL());
        Utils.selectComboCode(comboMethodeCdi, Utils.configuration.getCdiMethod());
        Utils.selectComboCode(comboAccesDonnees, Utils.configuration.getAccesDonnees());
        Utils.selectComboCode(comboTypePlateForme, Utils.configuration.getTypePlateForme());
        CDICheckMask(Utils.configuration.isWriteCdi());

        // MAPPING
        checkMapping.setSelection(Utils.configuration.isWriteMapping());
        textFichierMapping.setText(Utils.configuration.getFichierMapping());
        mappingCheckMask(Utils.configuration.isWriteMapping());
        
        // Date ODV
        radDateISO.setSelection(Utils.configuration.isDateISO());
        radDateJulian.setSelection(!Utils.configuration.isDateISO());
        
        // Flags
        selectDefaultFlagsTab(Utils.configuration.getDefaultCode());
        
        redLabels();
    }
    
    protected void CDICheckMask (Boolean status)
    {
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
        compo = new Composite(top, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        createCompoLCD(compo);
        createGroupDossier(compo);
    }

    /**
     * This method initializes compoLCD
     * 
     */
    private void createCompoLCD(Composite parent)
    {
        compo01 = new Composite(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().applyTo(compo01);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

        createGroupLangue(compo01);
        createGroupCentreDonnees(compo01);
    }

    /**
     * This method initializes groupLangue
     * 
     */
    private void createGroupLangue(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        // groupLangue.setLayout(null);
        group.setText(Messages.getString("OptionsAction.10")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
        
            comboLangue = new Combo(group, SWT.READ_ONLY);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(comboLangue);
            // comboLangue.setBounds(new Rectangle(2, 16, 160, 21));
    
            comboLangue.add(Messages.getString("OptionsAction.11")); //$NON-NLS-1$
            comboLangue.add(Messages.getString("OptionsAction.12")); //$NON-NLS-1$
            comboLangue.select(0);
            
        group.pack();
    }

    /**
     * This method initializes groupCentreDonnees
     * 
     */
    private void createGroupCentreDonnees(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setText(Messages.getString("OptionsAction.20")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

        comboCentreDonnees = new Combo(group, SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(comboCentreDonnees);        
        comboCentreDonnees.pack();

        Utils.createSearchMenu(comboCentreDonnees, Global.DATACENTRE);
    }

    /**
     * This method initializes groupDossier
     * 
     */
    private void createGroupDossier(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

        // groupDossier.setLayout(null);
        group.setText(Messages.getString("OptionsAction.5")); //$NON-NLS-1$

        group01 = new Group(group, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group01);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group01);
        group01.setText(Messages.getString("OptionsAction.18")); //$NON-NLS-1$
        group01.pack();

        textDossierModele = new Text(group01, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textDossierModele);
        buttonParcourirModele = new Button(group01, SWT.NONE);
        buttonParcourirModele.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
        buttonParcourirModele.pack();
        // listener sur le bouton
        buttonParcourirModele
        .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(
                    org.eclipse.swt.events.SelectionEvent e)
            {
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
                    ViewLog.styledTextLogErreurs.setText(Messages
                        .getString("OptionsAction.9")); //$NON-NLS-1$
                }
            }
        });

        group02 = new Group(group, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group02);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group02);
        group02.setText(Messages.getString("OptionsAction.19")); //$NON-NLS-1$
        group02.pack();

        textDossierCampagne = new Text(group02, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textDossierCampagne);
        buttonParcourirCampagne = new Button(group02, SWT.NONE);
        buttonParcourirCampagne.setText(Messages.getString("OptionsAction.6")); //$NON-NLS-1$
        buttonParcourirCampagne.pack();
        // listener sur le bouton
        buttonParcourirCampagne
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
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
            group = new Group(top, SWT.NONE);
            group.setText(Messages.getString("OptionsAction.grpWS")); //$NON-NLS-1$
            GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
    
            compo = new Composite(group, SWT.NONE);
            GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(compo); 
    
            labelNemo = new Label(compo, SWT.NONE);
            labelNemo.setText("NEMO"); //$NON-NLS-1$
            labelNemo.pack();
    
            textAreaNemo = new Text(compo, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(textAreaNemo);
    
            labelNautilus = new Label(compo, SWT.NONE);
            labelNautilus.setText("NAUTILUS"); //$NON-NLS-1$
            labelNautilus.pack();
    
            textAreaNautilus = new Text(compo, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(textAreaNautilus);
    
            // WS Vocab    
            labelWSVocab = new Label(compo, SWT.NONE);
            labelWSVocab.setText(Messages.getString("OptionsAction.28")); //$NON-NLS-1$
            textVocabURL = new Text(compo, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(textVocabURL);
    
            labelWSEdmo = new Label(compo, SWT.NONE);
            labelWSEdmo.setText("WS Edmo"); //$NON-NLS-1$
            textWSEdmo = new Text(compo, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(textWSEdmo);
        
        // -- PROXY
        createProxyGroup(group);
    }

    
    private void createProxyGroup(Composite parent)
    {
        compo01 = new Composite(parent, SWT.NULL);
        
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo01);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(compo01);
        
        label = new Label(compo01, SWT.NULL);
        GridDataFactory.swtDefaults().grab(false, true).hint(10, 4).applyTo(label);

                // PROXY
                useProxy = new Button(compo01, SWT.CHECK);
                useProxy.setText(Messages.getString("OptionsAction.proxy"));  //$NON-NLS-1$
                useProxy
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e)
                    {
                        // enable proxy
                        enableProxy(useProxy.getSelection());
                    }
                });
            
            compo02 = new Composite(compo01, SWT.NULL);        
            GridLayoutFactory.fillDefaults().numColumns(5).applyTo(compo02);
            //GridDataFactory.fillDefaults().grab(true, true).applyTo(compoProxy);
            
                labelAdrProxy = new Label(compo02, SWT.NULL);
                labelAdrProxy.setText(Messages.getString("OptionsAction.proxy.adr")); //$NON-NLS-1$
                GridDataFactory.fillDefaults().grab(true, false).minSize(100, 12).applyTo(labelAdrProxy);
                
                textAdrProxy = new Text(compo02, SWT.BORDER);
                GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textAdrProxy);
                
                label = new Label(compo02, SWT.NULL);
                GridDataFactory.fillDefaults().grab(true, false).minSize(10, 12).applyTo(label);
                
                labelPortProxy = new Label(compo02, SWT.NULL);
                labelPortProxy.setText(Messages.getString("OptionsAction.proxy.port")); //$NON-NLS-1$
                GridDataFactory.fillDefaults().grab(true, false).minSize(70, 12).applyTo(labelPortProxy);
                
                textPortProxy= new Text(compo02, SWT.BORDER);
                GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textPortProxy);
    
    
                compo02 = new Composite(compo01, SWT.NULL);            
            GridLayoutFactory.fillDefaults().numColumns(5).applyTo(compo02);
            
                labelLoginProxy = new Label(compo02, SWT.NULL);
                labelLoginProxy.setText(Messages.getString("OptionsAction.proxy.login")); //$NON-NLS-1$
                GridDataFactory.fillDefaults().grab(true, false).minSize(100, 12).applyTo(labelLoginProxy);
                
                textLoginProxy= new Text(compo02, SWT.BORDER);
                GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textLoginProxy);
                
                label = new Label(compo02, SWT.NULL);
                GridDataFactory.fillDefaults().grab(true, false).minSize(10, 12).applyTo(label);
                
                labelPassProxy = new Label(compo02, SWT.NULL);
                labelPassProxy.setText(Messages.getString("OptionsAction.proxy.pass")); //$NON-NLS-1$
                GridDataFactory.fillDefaults().grab(true, false).minSize(70, 12).applyTo(labelPassProxy);
                
                textPassProxy = new Text(compo02, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
                GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textPassProxy);
        
    }

    
    protected void enableProxy(boolean enabled)
    {
        labelAdrProxy.setEnabled(enabled);
        textAdrProxy.setEnabled(enabled);
        labelPortProxy.setEnabled(enabled);
        textPortProxy.setEnabled(enabled);
        labelLoginProxy.setEnabled(enabled);
        textLoginProxy.setEnabled(enabled);
        labelPassProxy.setEnabled(enabled);
        textPassProxy.setEnabled(enabled);
    }

    /**
     * This method initializes groupCDI
     * 
     */
    private void createGroupEdmo()
    {
        // FAE 1123 : Extraire le centre de données responsable du CDI du bloc CDI.
        
        group = new Group(top, SWT.NONE);
        group.setText(Messages.getString("OptionsAction.grpEdmo")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(4, 4).numColumns(2).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
        
        labelIdEdmoRespCdi = new Label(group, SWT.NONE);
        labelIdEdmoRespCdi.setText(Messages.getString("OptionsAction.21")); //$NON-NLS-1$
        labelIdEdmoRespCdi.pack();

        comboIdEdmoRespCdi = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(comboIdEdmoRespCdi);
        
        
    }

    /**
     * This method initializes groupCDI
     * 
     */
    private void createGroupCDI()
    {   
        group = new Group(top, SWT.NONE);
        group.setText(Messages.getString("OptionsAction.grpCDI")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(4, 4).numColumns(2).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        // compo vide pour mise en forme
        lblCDI = new Label(group, SWT.NONE);
        lblCDI.setText(Messages.getString("OptionsAction.44")); //$NON-NLS-1$
        checkCDI = new Button(group, SWT.CHECK);

        //On masque/affiche les champs falcutatif en fonction de la case checkCDI
        checkCDI.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                CDICheckMask(checkCDI.getSelection());
            }
        });  
        
        labelCodeEdmoDonnees = new Label(group, SWT.NONE);
        labelCodeEdmoDonnees.setText(Messages.getString("OptionsAction.24")); //$NON-NLS-1$
        labelCodeEdmoDonnees.pack();

        comboCodeEdmoDonnees = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboCodeEdmoDonnees);

        labelIdEdmoRespDonnees = new Label(group, SWT.NONE);
        labelIdEdmoRespDonnees.setText(Messages.getString("OptionsAction.25")); //$NON-NLS-1$
        labelIdEdmoRespDonnees.pack();

        comboIdEdmoRespDonnees =
            new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoRespDonnees);

        labelIdEdmoDistDonnees = new Label(group, SWT.NONE);
        labelIdEdmoDistDonnees.setText(Messages.getString("OptionsAction.26")); //$NON-NLS-1$
        labelIdEdmoDistDonnees.pack();

        comboIdEdmoDistDonnees =
            new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoDistDonnees);

        labelUrlCdi = new Label(group, SWT.NONE);
        labelUrlCdi.setText(Messages.getString("OptionsAction.22")); //$NON-NLS-1$
        labelUrlCdi.pack();

        textUrlCdi = new Text(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textUrlCdi);

        labelMethodeCdi = new Label(group, SWT.NONE);
        labelMethodeCdi.setText(Messages.getString("OptionsAction.23")); //$NON-NLS-1$
        labelMethodeCdi.pack();

        comboMethodeCdi = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(comboMethodeCdi);

        // Menu recherche combos
        Utils.createSearchMenu(comboIdEdmoRespCdi, Global.IDEDMO);
        Utils.createSearchMenu(comboCodeEdmoDonnees, Global.IDEDMO);
        Utils.createSearchMenu(comboIdEdmoRespDonnees, Global.IDEDMO);
        Utils.createSearchMenu(comboIdEdmoDistDonnees, Global.IDEDMO);
        
        // FAE 59 CDL 09/04/08
        lblTypePlateForme = new Label(group, SWT.NONE);
        lblTypePlateForme.setText(Messages.getString("ViewFile.typePlateForme")); //$NON-NLS-1$

        comboTypePlateForme = new Combo(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(comboTypePlateForme);

        lblAccesDonnees = new Label(group, SWT.NONE);
        lblAccesDonnees.setText(Messages.getString("ViewFile.accesDonnees")); //$NON-NLS-1$

        comboAccesDonnees = new Combo(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(comboAccesDonnees);
    }

    /**
     * This method initializes compoBas
     * 
     */
    private void createCompoBas()
    {
        compo = new Composite(top, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        createGroupMapping(compo);
        createGroupDateODV(compo);
    }
    


    /**
     * This method initializes groupMapping
     * 
     */
    private void createGroupMapping(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setText(Messages.getString("OptionsAction.grpMapping")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
        
        checkMapping = new Button(group, SWT.CHECK);
        checkMapping.setText(Messages.getString("OptionsAction.45")); //$NON-NLS-1$
        checkMapping.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                //On masque/affiche les champs falcutatif en fonction de la case checkMapping
                mappingCheckMask(checkMapping.getSelection());
            }
        }); 
        

        compo01 = new Composite(group, SWT.NULL);        
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo01);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);
        
            labelFichierMapping = new Label(compo01, SWT.NONE);
            labelFichierMapping.setText(Messages.getString("OptionsAction.31")); //$NON-NLS-1$
            labelFichierMapping.pack();
    
            textFichierMapping = new Text(compo01, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, false).applyTo(textFichierMapping);
    }
    
    /**
     * This method initializes groupMapping
     * 
     */
    private void createGroupDateODV(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setText(Messages.getString("OptionsAction.grpDateODV")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        AddedParameter dateISO = new AddedParameter(IdParameter.DTUT, null, null);
        radDateISO = new Button(group, SWT.RADIO);
        radDateISO.setText(dateISO.getFullName() + " [" + dateISO.getUnit() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        radDateISO.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                radDateJulian.setSelection(!radDateISO.getSelection());
            }
        }); 
        
        AddedParameter dateJulian = new AddedParameter(IdParameter.CJDY, null, null);
        radDateJulian = new Button(group, SWT.RADIO);
        radDateJulian.setText(dateJulian.getFullName() + " [" + dateJulian.getUnit() + "]"); //$NON-NLS-1$//$NON-NLS-2$
        radDateJulian.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                radDateISO.setSelection(!radDateJulian.getSelection());
            }
        });  
    }

    /**
     * This method initializes groupFlag
     * 
     */
    private void createGroupFlag()
    {
        group = new Group(top, SWT.NONE);
        group.setText(Messages.getString("OptionsAction.33")); //$NON-NLS-1$
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

        // Creation du tableau des tables de correspondaces
        createTableFlagTable(group);

        // Creation du tableau des correspondaces
        createTableFlag(group);
    }

    /**
     * This method initializes groupTop3
     * 
     */
    private void createCompoBouton()
    {
        compo = new Composite(top, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(3).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER,
            SWT.NONE).applyTo(compo);

        buttonValider = new Button(compo, SWT.NONE);
        buttonValider.setText(Messages.getString("OptionsAction.17")); //$NON-NLS-1$
        buttonValider.pack();

        buttonRestore = new Button(compo, SWT.NONE);
        buttonRestore.setText("Restore Default"); //$NON-NLS-1$
        buttonRestore.pack();

        buttonCancel = new Button(compo, SWT.NONE);
        buttonCancel.setText(Messages.getString("OptionsAction.16")); //$NON-NLS-1$
        buttonCancel.pack();

        compo.pack();
    }

    public void createTableFlagTable(Composite parent)
    {
        tabFlagTable = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
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
        itemDefault.setText(Messages.getString("OptionsAction.40")); //$NON-NLS-1$
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
                selectDefaultFlagsTab(tabFlagTable.getSelection()[0].getText());
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
    
    protected void selectDefaultFlagsTab(String defaultCode){
        setDefaultCode(defaultCode);
        // Mise en gras du flagtable par defaut

        Font normal = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.NORMAL);
        Font bold = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.BOLD);

        for (int i = 0; i < tabFlagTable.getItemCount(); i++)
        {
            if (tabFlagTable.getItem(i).getText(0).equals(getDefaultCode()))
            {
                tabFlagTable.getItem(i).setFont(bold);
            }else{
                tabFlagTable.getItem(i).setFont(normal);
            }
        }
    }

    public void createTableFlag(Composite parent)
    {
        tabFlag = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
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
        createGroupEdmo();
        createGroupCDI();
        createCompoBas();
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
                    if (checkConfig())
                    {                    
                        // message d'information pour redémarrage de l'application
                        boolean writeIni = checkRestart();
                        
                        // sauvegarde de la config
                        Utils.saveConfig(writeIni);
                        
                        // reinitialisation de la conversion
                        if (DataModel.inputModel != null) DataModel.inputModel.setConverted(false);
                        
                        // fermeture de la fenêtre
                        shelloptions.close();
                        
                    }else{
                        redLabels();
                    }
                    
                }
            });

        // listener reinitialiser
        buttonRestore
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    Utils.restoreDefaultConfig();
                    remplissageOptions();
                    
                }
            });
    }
    
    protected boolean checkConfig() {
        boolean blnOK = true;
        StringBuffer message = new StringBuffer();
        StringBuffer messageCDI = new StringBuffer();

        message.append(checkMandatory(comboIdEdmoRespCdi.getText(), "OptionsAction.21", Global.SEP)); //$NON-NLS-1$ 
        if (useProxy.getSelection()){
            message.append(checkMandatory(textAdrProxy.getText(), "OptionsAction.proxy.adr", Global.SEP)); //$NON-NLS-1$
            message.append(checkMandatory(textPortProxy.getText(), "OptionsAction.proxy.port", Global.SEP)); //$NON-NLS-1$
        }
        
        if (message.length() > 0){
            message .insert(0, Messages.getString("OptionsAction.checkConfig")); //$NON-NLS-1$
            blnOK = false;
        }
        
        if (checkCDI.getSelection())        
        {       
            messageCDI.append(checkMandatory(comboCodeEdmoDonnees.getText(), "OptionsAction.24", Global.SEP)); //$NON-NLS-1$ 
            messageCDI.append(checkMandatory(comboIdEdmoRespDonnees.getText(), "OptionsAction.25", Global.SEP)); //$NON-NLS-1$
            messageCDI.append(checkMandatory(comboIdEdmoDistDonnees.getText(), "OptionsAction.26", Global.SEP)); //$NON-NLS-1$ 
            messageCDI.append(checkMandatory(textUrlCdi.getText(), "OptionsAction.22", Global.SEP)); //$NON-NLS-1$
            messageCDI.append(checkMandatory(comboMethodeCdi.getText(), "OptionsAction.23", Global.SEP)); //$NON-NLS-1$
            messageCDI.append(checkMandatory(comboTypePlateForme.getText(), "ViewFile.typePlateForme", Global.SEP)); //$NON-NLS-1$

            if (messageCDI.length() > 0){
                messageCDI.insert(0, Messages.getString("OptionsAction.checkCDI")); //$NON-NLS-1$
                message.append(messageCDI);
                blnOK = false;
            }
        }    
        
        if (checkMapping.getSelection())        
        { 
            String errorMapping = checkMandatory(textFichierMapping.getText(), "OptionsAction.checkMapping", Global.RN); //$NON-NLS-1$
            if (errorMapping.length() > 0){
                message.append(errorMapping);
                blnOK = false;
            }
        }
        
        
        if (!blnOK) {
            try {
                String[] buttons = new String[1];
                buttons[0] = "OK"; //$NON-NLS-1$ 
                MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                         Messages.getString("CDIResumeWriter.configTitle"), null,  //$NON-NLS-1$
                                                         message.toString(), MessageDialog.ERROR, buttons, 0);
                dialog.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return blnOK;
    }
    
    protected String checkMandatory(String text, String keyErrorMsg, String sep)
    {
        StringBuffer message = new StringBuffer();
        if (text.trim().equals(Global.EMPTY_STRING))
        {
            message.append(Global.RN);
            message.append(sep);
            message.append(Messages.getString(keyErrorMsg)); 
        }
        return message.toString();
    }
    
    protected void redLabels(){

        // Libelles rouges si parametres manquants
        redLabel(comboIdEdmoRespCdi.getText(), labelIdEdmoRespCdi);
        redLabel(comboCodeEdmoDonnees.getText(), labelCodeEdmoDonnees);
        redLabel(comboIdEdmoRespDonnees.getText(), labelIdEdmoRespDonnees);
        redLabel(comboIdEdmoDistDonnees.getText(), labelIdEdmoDistDonnees);
        redLabel(textUrlCdi.getText(), labelUrlCdi);
        redLabel(comboMethodeCdi.getText(), labelMethodeCdi);
        redLabel(comboTypePlateForme.getText(), lblTypePlateForme);
        redLabel(textFichierMapping.getText(), labelFichierMapping);
        redLabel(textAdrProxy.getText(), labelAdrProxy);
        redLabel(textPortProxy.getText(), labelPortProxy);
    }
    
    protected void redLabel(String text, Label redLabel){
        if (text.trim().equals(Global.EMPTY_STRING)){
            redLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        }else{
            redLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
    }

    
    protected boolean checkRestart()
    {
        boolean writeIni = false;
        StringBuffer message = new StringBuffer();

        if (comboLangue.getText().equals(Messages.getString("OptionsAction.12"))){ //$NON-NLS-1$
            if (Utils.configuration.getLang().equals(Intern.lang_fr))
                message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.10")); //$NON-NLS-1$
        }else{
            if (Utils.configuration.getLang().equals(Intern.lang_en))
                message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.10")); //$NON-NLS-1$
        }
        
        Proxy recordedProxy = Utils.configuration.getProxy();
        if (useProxy.getSelection() != recordedProxy.isUse()){
            // proxy configuration change
            message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy")); //$NON-NLS-1$
            writeIni = true;
        }else{
            if (useProxy.getSelection())
            {
                if (!textAdrProxy.getText().trim().equals(recordedProxy.getAdress())){
                    // proxy address change (can't be null)
                    message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy.adr")); //$NON-NLS-1$
                    writeIni = true;
                }
                if (!textPortProxy.getText().trim().equals(recordedProxy.getPort())){
                    // proxy port change (can't be null)
                    message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy.port")); //$NON-NLS-1$
                    writeIni = true;
                }
                
                if (textLoginProxy.getText() == null){
                    if (!recordedProxy.getLogin().equals(Global.EMPTY_STRING)){
                        // proxy login change
                        message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy.login")); //$NON-NLS-1$
                        writeIni = true;
                    }
                }else if (!textLoginProxy.getText().trim().equals(recordedProxy.getLogin())){
                    // proxy login change
                    message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy.login")); //$NON-NLS-1$
                    writeIni = true;
                }
                
                if (textPassProxy.getText() == null){
                    if (!recordedProxy.getPassword().equals(Global.EMPTY_STRING)){
                        // proxy password change
                        message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy.pass")); //$NON-NLS-1$
                        writeIni = true;
                    }
                }else if (!textPassProxy.getText().trim().equals(recordedProxy.getPassword())){
                    // proxy password change
                    message.append(Global.RN + Global.SEP + Messages.getString("OptionsAction.proxy.pass")); //$NON-NLS-1$
                    writeIni = true;
                }
            }
        }
        

        if (message.length() > 0){
            message.insert(0, Messages.getString("OptionsAction.warning")); //$NON-NLS-1$
            MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
                                                     Messages.getString("OptionsAction.warning.title"), null, message.toString(),  //$NON-NLS-1$
                                                     MessageDialog.WARNING, new String[] { "OK" }, 0);  //$NON-NLS-1$
            dialog.open();
            
        }
        
        return writeIni;
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

//            // Mise en gras du flagtable par defaut
//            if (flagTables.get(i).getCode().equals(Utils.configuration.getDefaultCode()))
//            {
//                setDefaultCode(flagTables.get(i).getCode());
//                Font font = new Font(Application.display, Global.EMPTY_STRING, 8, SWT.BOLD);
//                item.setFont(font);
//            }
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
