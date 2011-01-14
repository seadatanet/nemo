package fr.ifremer.nemo.ui.actions;

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
import org.eclipse.swt.layout.GridData;
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
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.BODCList;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.EdmoHandler;
import fr.ifremer.nemo.config.Flag;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.Proxy;
import fr.ifremer.nemo.config.Configuration.DataListEnum;
import fr.ifremer.nemo.domain.AddedParameter;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.domain.AddedParameter.IdParameter;
import fr.ifremer.nemo.ui.Application;
import fr.ifremer.nemo.ui.ApplicationActionBarAdvisor;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
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

    public static Label textAreaFlag;

    // FAE 4431 Flag qualité par défault configurable
    public static Combo _flagDefault;

    public final static String ID = "fr.ifremer.nemo.ui.OptionsAction"; //$NON-NLS-1$

    // vars nécessaires à la fenêtre d'options

    public static Shell shelloptions;

    private Composite /*top, */compo, compo01, compo02;
    private Group group, group01, group02;
    // choix du format d'export
    private  Button radMedatlas;
    private  Button radODV;
    private  Button radODVMonoFichier;
    private  Button radODVMonoStation ;
    protected  Group grpODV;
    // -- PROXY
    public static Button useProxy;
    private Label label, labelAdrProxy, labelPortProxy, labelLoginProxy,
            labelPassProxy;
    public static Text textAdrProxy, textPortProxy, textLoginProxy,
            textPassProxy;

    // DataList paramètres
    private Configuration.DataListEnum dataList;
    // choix du format des parametres
    private Button button_P011viaP091;
    private Button button_P091;
    private Button button_P011viaP021;

    
    // -- options necessitant un redemarrage
    private String configLangage;
    private Proxy configProxy;

    public OptionsAction(IWorkbenchWindow window)
    {
        this.window = window;
        setId(ID);
        setText(Messages.menu_settings);
        window.getSelectionService().addSelectionListener(this);
    }

    public void dispose()
    {
        window.getSelectionService().addSelectionListener(this);
    }

    @Override
    public void run()
    {
        // configuration
        Configuration config = Nemo.getInstance().getConfig();

        // sauvegarde des options necessitant un redemarrage
        configLangage = config.getLang();
        configProxy = config.getProxy();

        // lancement IHM options
        shelloptions =
            new Shell(Application.display, SWT.BORDER | SWT.TITLE
                    | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);

        shelloptions.setLocation(20, 20);
        shelloptions.setSize(620, 600);
        shelloptions.setText(Messages.settings_title);

        _flagTables.clear();
        for (int i = 0; i < config.getFlagTables().size(); i++)
        {
            FlagTable flagTable = new FlagTable(config.getFlagTables().get(i));
            _flagTables.add(flagTable);
        }

        createPartControl(shelloptions);

        shelloptions.pack();
        shelloptions.open();

        // chargement des options
        alimenteCombos();

        remplissageOptions();
    }

    private void alimenteCombos()
    {

        // CENTRES DE DONNEES
        CodeTableRecordType[] centres =
            NemoLists.getVocabList(Intern.list_dataCenter);
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
        CodeTableRecordType[] records =
            NemoLists.getVocabList(Intern.list_methodCDI);
        TreeSet<String> champsMethodesCdi = new TreeSet<String>();
        for (CodeTableRecordType record : records)
        {
            String champCombo = Global.EMPTY_STRING;
            // System.out.println(record.getEntryKey());
            String contenuCleDecoup[] =
                record.getEntryKey().split(Global.SLASH);
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += Global.SEP + record.getEntryTerm();
            champsMethodesCdi.add(champCombo);
        }
        for (String champ : champsMethodesCdi)
        {
            comboMethodeCdi.add(champ);
        }

        records = NemoLists.getVocabList(Intern.list_typePlatform);
        champsMethodesCdi = new TreeSet<String>();
        for (CodeTableRecordType record : records)
        {
            String champCombo = Global.EMPTY_STRING;
            String contenuCleDecoup[] =
                record.getEntryKey().split(Global.SLASH);
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += Global.SEP + record.getEntryTerm();
            champsMethodesCdi.add(champCombo);
        }
        for (String champ : champsMethodesCdi)
            comboTypePlateForme.add(champ);

        records = NemoLists.getVocabList(Intern.list_accesDonnees);
        champsMethodesCdi = new TreeSet<String>();
        for (CodeTableRecordType record : records)
        {
            String champCombo = Global.EMPTY_STRING;
            String contenuCleDecoup[] =
                record.getEntryKey().split(Global.SLASH);
            champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
            champCombo += Global.SEP + record.getEntryTerm();
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
            strTemp =
                currentEntry.getKey() + Global.SEP + currentEntry.getValue();
            EdmoHandler.listeOrigine.add(strTemp);
            comboIdEdmoRespCdi.add(strTemp);
            comboCodeEdmoDonnees.add(strTemp);
            comboIdEdmoRespDonnees.add(strTemp);
            comboIdEdmoDistDonnees.add(strTemp);
        }

        fillFlagTable();

        // FAE 4431 Flag qualité par défault configurable
        _flagDefault.add("0");
        _flagDefault.add("1");
    }

    protected void remplissageOptions()
    {
        // remplissage IHM avec les valeurs du fichier de config
        // configuration
        final Configuration config = Nemo.getInstance().getConfig();

        // DOSSIERS
        textDossierModele.setText(config.getModelsDirectory());
        textDossierCampagne.setText(config.getCruisesDirectory());

        // LANGUE
        if (config.getLocale().toString().equals(Intern.lang_fr))
        {
            // on met la combo langue à FR
            comboLangue.select(0);
        }
        else
        {
            // on met la combo langue à GB
            comboLangue.select(1);
        }

        // CENTRE DE DONNEES
        Utils.selectComboCode(comboCentreDonnees, config.getDataCenter());

        // WS
        textAreaNemo.setText(config.getNemoWebServiceURL());
        textAreaNautilus.setText(config.getNautilusWebServiceURL());
        textVocabURL.setText(config.getVocabURL());
        textWSEdmo.setText(config.getEdmoURL());

        Proxy proxy = config.getProxy();
        useProxy.setSelection(proxy.isUse());
        textAdrProxy.setText(proxy.getAdress());
        textPortProxy.setText(String.valueOf(proxy.getPort()));
        textLoginProxy.setText(proxy.getLogin());
        textPassProxy.setText(proxy.getPassword());
        enableProxy(proxy.isUse());

        // EDMO code
        Utils.selectComboCode(comboIdEdmoRespCdi, config.getIdEdmoRespCdi());

        // CDI
        checkCDI.setSelection(config.isWriteCdi());
        Utils
            .selectComboCode(comboCodeEdmoDonnees, config.getCodeEdmoDonnees());
        Utils.selectComboCode(comboIdEdmoRespDonnees, config
            .getIdEdmoRespDonnees());
        Utils.selectComboCode(comboIdEdmoDistDonnees, config
            .getIdEdmoDistDonnees());
        textUrlCdi.setText(config.getCdiURL());
        Utils.selectComboCode(comboMethodeCdi, config.getCdiMethod());
        Utils.selectComboCode(comboAccesDonnees, config.getAccesDonnees());
        Utils.selectComboCode(comboTypePlateForme, config.getTypePlateForme());
        CDICheckMask(config.isWriteCdi());

        // MAPPING
        checkMapping.setSelection(config.isWriteMapping());
        textFichierMapping.setText(config.getFichierMapping());
        mappingCheckMask(config.isWriteMapping());

        // Date ODV
        radDateISO.setSelection(config.isDateISO());
        radDateJulian.setSelection(!config.isDateISO());

        // Flags
        selectDefaultFlagsTab(config.getDefaultCode());

        // DataList
        switch (config.getDataListInSettings())
        {
            case P011_VIA_P091:
                button_P011viaP091.setSelection(true);
            break;
            case P091:
                button_P091.setSelection(true);
            break;
            case P011_VIA_P021:
                button_P011viaP021.setSelection(true);
            break;
        }

        // convert type
        radMedatlas.setSelection(false);
        radODV.setSelection(false);
        radODVMonoStation.setSelection(false);
        radODVMonoFichier.setSelection(false);
        switch (config.getDefaultConversionType())
        {
            case MEDATLAS:
                radMedatlas.setSelection(true);
                break;
            case ODV_MONOSTATION:
                radODV.setSelection(true);
                radODVMonoStation.setSelection(true);
                break;
            case ODV_MULTISTATIONS:
                radODV.setSelection(true);
                radODVMonoFichier.setSelection(true);
                break;
            case UNKNOWN:
                break;
        }
        afficheGrpODV();
        
        // FAE 4431 Flag qualité par défault configurable
        if (config.getDefaultFlag().equals("1"))
            _flagDefault.select(1);
        else
            _flagDefault.select(0);

        redLabels();
    }

    protected void CDICheckMask(Boolean status)
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

    protected void mappingCheckMask(Boolean enabled)
    {
        labelFichierMapping.setEnabled(enabled);
        textFichierMapping.setEnabled(enabled);

        ApplicationActionBarAdvisor.mappingDatabaseEditAction
            .setEnabled(enabled);
        ApplicationActionBarAdvisor.mappingDatabaseExportAction
            .setEnabled(enabled);
        ApplicationActionBarAdvisor.mappingDatabaseImportAction
            .setEnabled(enabled);
    }

    public void selectionChanged(IWorkbenchPart part, ISelection incoming)
    {
    }

    /**
     * This method initializes compoHaut
     */
    private void createCompoHaut(Composite parent)
    {
        compo = new Composite(parent, SWT.NONE);
        compo.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true)
            .applyTo(compo);
        GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT)
            .applyTo(compo);

        createCompoLCD(compo);
        createGroupDossier(compo);
    }

    /**
     * This method initializes compoLCD
     */
    private void createCompoLCD(Composite parent)
    {
        compo01 = new Composite(parent, SWT.NONE);
        compo01.setFont(Utils.font08);

        GridLayoutFactory.fillDefaults().applyTo(compo01);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

        createGroupLangue(compo01);
        createGroupCentreDonnees(compo01);
    }

    /**
     * This method initializes groupLangue
     */
    private void createGroupLangue(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_lang);

        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

        comboLangue = new Combo(group, SWT.READ_ONLY);
        comboLangue.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(comboLangue);

        comboLangue.add(Messages.settings_lang_FR);
        comboLangue.add(Messages.settings_lang_EN);
        comboLangue.select(0);

        group.pack();
    }

    /**
     * This method initializes groupCentreDonnees
     */
    private void createGroupCentreDonnees(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_datacenter);

        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

        comboCentreDonnees = new Combo(group, SWT.READ_ONLY);
        comboCentreDonnees.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboCentreDonnees);
        comboCentreDonnees.pack();

        Utils.createSearchMenu(comboCentreDonnees, Global.DATACENTRE);
    }

    /**
     * This method initializes groupDossier
     */
    private void createGroupDossier(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

        // groupDossier.setLayout(null);
        group.setText(Messages.settings_dir);

        group01 = new Group(group, SWT.NONE);
        group01.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group01);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group01);
        group01.setText(Messages.settings_dir_model);
        group01.pack();

        textDossierModele = new Text(group01, SWT.BORDER);
        textDossierModele.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            textDossierModele);
        buttonParcourirModele = new Button(group01, SWT.NONE);
        buttonParcourirModele.setFont(Utils.font08);
        buttonParcourirModele.setText(Messages.button_browse);
        buttonParcourirModele.pack();
        // listener sur le bouton
        buttonParcourirModele
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    String nomRepertoire;
                    Shell shell = Utils.getMainShell();
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
                        ViewLog.styledTextLogErreurs
                            .setText(Messages.error_settings_dir);
                    }
                }
            });

        group02 = new Group(group, SWT.NONE);
        group02.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group02);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(group02);
        group02.setText(Messages.settings_dir_data);
        group02.pack();

        textDossierCampagne = new Text(group02, SWT.BORDER);
        textDossierCampagne.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            textDossierCampagne);
        buttonParcourirCampagne = new Button(group02, SWT.NONE);
        buttonParcourirCampagne.setFont(Utils.font08);
        buttonParcourirCampagne.setText(Messages.button_browse);
        buttonParcourirCampagne.pack();
        // listener sur le bouton
        buttonParcourirCampagne
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    String nomRepertoire;
                    Shell shell = Utils.getMainShell();
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
                        ViewLog.styledTextLogErreurs
                            .setText(Messages.error_settings_dir);
                    }
                }
            });
    }

    /**
     * This method initializes groupWS
     */
    private void createGroupWS(Composite parent)
    {
        compo01 = new Group(parent, SWT.NONE);
        compo01.setFont(Utils.font08);
        // group.setText(Messages.settings_web);
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(compo01);
        GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT)
            .applyTo(compo01);

        compo = new Composite(compo01, SWT.NONE);
        compo.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo);

        labelNemo = new Label(compo, SWT.NONE);
        labelNemo.setFont(Utils.font08);
        labelNemo.setText(Messages.settings_web_nemo);
        labelNemo.pack();

        textAreaNemo = new Text(compo, SWT.BORDER);
        textAreaNemo.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textAreaNemo);

        labelNautilus = new Label(compo, SWT.NONE);
        labelNautilus.setFont(Utils.font08);
        labelNautilus.setText(Messages.settings_web_nautilus);
        labelNautilus.pack();

        textAreaNautilus = new Text(compo, SWT.BORDER);
        textAreaNautilus.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            textAreaNautilus);

        // WS Vocab
        labelWSVocab = new Label(compo, SWT.NONE);
        labelWSVocab.setFont(Utils.font08);
        labelWSVocab.setText(Messages.settings_web_vocab);
        textVocabURL = new Text(compo, SWT.BORDER);
        textVocabURL.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textVocabURL);

        labelWSEdmo = new Label(compo, SWT.NONE);
        labelWSEdmo.setFont(Utils.font08);
        labelWSEdmo.setText(Messages.settings_web_edmo);
        textWSEdmo = new Text(compo, SWT.BORDER);
        textWSEdmo.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textWSEdmo);

        // -- PROXY
        createProxyGroup(compo01);
    }

    private void createProxyGroup(Composite parent)
    {
        compo01 = new Composite(parent, SWT.NULL);
        compo01.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo01);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

        label = new Label(compo01, SWT.NULL);
        label.setFont(Utils.font08);
        GridDataFactory.swtDefaults().grab(false, false).hint(10, 4).applyTo(
            label);

        // PROXY
        useProxy = new Button(compo01, SWT.CHECK);
        useProxy.setFont(Utils.font08);
        useProxy.setText(Messages.settings_proxy);
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
        compo02.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(compo02);
        // GridDataFactory.fillDefaults().grab(true, true).applyTo(compoProxy);

        labelAdrProxy = new Label(compo02, SWT.NULL);
        labelAdrProxy.setFont(Utils.font08);
        labelAdrProxy.setText(Messages.settings_proxy_adr);
        GridDataFactory.fillDefaults().grab(true, false).minSize(100, 12)
            .applyTo(labelAdrProxy);

        textAdrProxy = new Text(compo02, SWT.BORDER);
        textAdrProxy.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(
            textAdrProxy);

        label = new Label(compo02, SWT.NULL);
        label.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).minSize(10, 12)
            .applyTo(label);

        labelPortProxy = new Label(compo02, SWT.NULL);
        labelPortProxy.setFont(Utils.font08);
        labelPortProxy.setText(Messages.settings_proxy_port);
        GridDataFactory.fillDefaults().grab(true, false).minSize(70, 12)
            .applyTo(labelPortProxy);

        textPortProxy = new Text(compo02, SWT.BORDER);
        textPortProxy.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(
            textPortProxy);

        compo02 = new Composite(compo01, SWT.NULL);
        GridLayoutFactory.fillDefaults().numColumns(5).applyTo(compo02);

        labelLoginProxy = new Label(compo02, SWT.NULL);
        labelLoginProxy.setFont(Utils.font08);
        labelLoginProxy.setText(Messages.settings_proxy_login);
        GridDataFactory.fillDefaults().grab(true, false).minSize(100, 12)
            .applyTo(labelLoginProxy);

        textLoginProxy = new Text(compo02, SWT.BORDER);
        textLoginProxy.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(
            textLoginProxy);

        label = new Label(compo02, SWT.NULL);
        label.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).minSize(10, 12)
            .applyTo(label);

        labelPassProxy = new Label(compo02, SWT.NULL);
        labelPassProxy.setFont(Utils.font08);
        labelPassProxy.setText(Messages.settings_proxy_pass);
        GridDataFactory.fillDefaults().grab(true, false).minSize(70, 12)
            .applyTo(labelPassProxy);

        textPassProxy =
            new Text(compo02, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
        textPassProxy.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(
            textPassProxy);

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
     */
    private void createGroupEdmo(Composite parent)
    {
        // FAE 1123 : Extraire le centre de données responsable du CDI du bloc
        // CDI.

        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_EDMO);
        GridLayoutFactory.fillDefaults().margins(4, 4).numColumns(2).applyTo(
            group);
        GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT)
            .applyTo(group);

        labelIdEdmoRespCdi = new Label(group, SWT.NONE);
        labelIdEdmoRespCdi.setFont(Utils.font08);
        labelIdEdmoRespCdi.setText(Messages.settings_EDMOauthor);
        labelIdEdmoRespCdi.pack();

        comboIdEdmoRespCdi = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
        comboIdEdmoRespCdi.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoRespCdi);

    }

    /**
     * This method initializes groupCDI
     */
    private void createGroupCDI(Composite parent)
    {
        // group = new Group(parent, SWT.NONE);
        compo = new Composite(parent, SWT.NONE);
        compo.setFont(Utils.font08);
        // group.setText(Messages.settings_cdi);
        GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(
            compo);
        GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT)
            .applyTo(compo);

        // compo vide pour mise en forme
        lblCDI = new Label(compo, SWT.NONE);
        lblCDI.setFont(Utils.font08);
        lblCDI.setText(Messages.settings_cdi_check);
        checkCDI = new Button(compo, SWT.CHECK);
        checkCDI.setFont(Utils.font08);
        // On masque/affiche les champs falcutatif en fonction de la case
        // checkCDI
        checkCDI
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    CDICheckMask(checkCDI.getSelection());
                }
            });

        labelCodeEdmoDonnees = new Label(compo, SWT.NONE);
        labelCodeEdmoDonnees.setFont(Utils.font08);
        labelCodeEdmoDonnees.setText(Messages.settings_EDMOori);
        labelCodeEdmoDonnees.pack();

        comboCodeEdmoDonnees = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        comboCodeEdmoDonnees.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboCodeEdmoDonnees);

        labelIdEdmoRespDonnees = new Label(compo, SWT.NONE);
        labelIdEdmoRespDonnees.setFont(Utils.font08);
        labelIdEdmoRespDonnees.setText(Messages.settings_EDMOcustodian);
        labelIdEdmoRespDonnees.pack();

        comboIdEdmoRespDonnees = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        comboIdEdmoRespDonnees.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoRespDonnees);

        labelIdEdmoDistDonnees = new Label(compo, SWT.NONE);
        labelIdEdmoDistDonnees.setFont(Utils.font08);
        labelIdEdmoDistDonnees.setText(Messages.settings_EDMOdist);
        labelIdEdmoDistDonnees.pack();

        comboIdEdmoDistDonnees = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        comboIdEdmoDistDonnees.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboIdEdmoDistDonnees);

        labelUrlCdi = new Label(compo, SWT.NONE);
        labelUrlCdi.setFont(Utils.font08);
        labelUrlCdi.setText(Messages.settings_dataDist_web);
        labelUrlCdi.pack();

        textUrlCdi = new Text(compo, SWT.BORDER);
        textUrlCdi.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(textUrlCdi);

        labelMethodeCdi = new Label(compo, SWT.NONE);
        labelMethodeCdi.setFont(Utils.font08);
        labelMethodeCdi.setText(Messages.settings_dataDist_met);
        labelMethodeCdi.pack();

        comboMethodeCdi = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        comboMethodeCdi.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(
            comboMethodeCdi);

        // Menu recherche combos
        Utils.createSearchMenu(comboIdEdmoRespCdi, Global.IDEDMO);
        Utils.createSearchMenu(comboCodeEdmoDonnees, Global.IDEDMO);
        Utils.createSearchMenu(comboIdEdmoRespDonnees, Global.IDEDMO);
        Utils.createSearchMenu(comboIdEdmoDistDonnees, Global.IDEDMO);

        // FAE 59 CDL 09/04/08
        lblTypePlateForme = new Label(compo, SWT.NONE);
        lblTypePlateForme.setFont(Utils.font08);
        lblTypePlateForme.setText(Messages.settings_typePlateForme);

        comboTypePlateForme = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        comboTypePlateForme.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
            comboTypePlateForme);

        lblAccesDonnees = new Label(compo, SWT.NONE);
        lblAccesDonnees.setFont(Utils.font08);
        lblAccesDonnees.setText(Messages.settings_accesDonnees);

        comboAccesDonnees = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        comboAccesDonnees.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(
            comboAccesDonnees);
    }

    /**
     * This method initializes compoBas
     */
    private void createCompoBas(Composite parent)
    {
        compo = new Composite(parent, SWT.NONE);
        compo.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
        GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT)
            .applyTo(compo);

        createGroupMapping(compo);
        createGroupDateODV(compo);
    }

    /**
     * This method initializes data list group
     */
    private void createGroupDataList(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_parametersList);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(true)
            .margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        button_P091 = new Button(group, SWT.RADIO);
        button_P091.setText(Messages.settings_parameterListP091);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P091);
        
        button_P011viaP091 = new Button(group, SWT.RADIO);
        button_P011viaP091.setText(Messages.settings_parameterListP011viaP091);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P011viaP091);
        
        button_P011viaP021 = new Button(group, SWT.RADIO);
        button_P011viaP021.setText(Messages.settings_parameterListP011viaP021);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(button_P011viaP021);
    }
    /**
     * This method initializes data list group
     */
    private void createGroupOutputFormat(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_defaultOutputFormat);
        GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(true)
            .margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        
        //****************************************************************************************
        // MEDATLAS/ODV
        GridData medOrOdvgridData = new GridData();
        medOrOdvgridData.grabExcessHorizontalSpace = true;
        medOrOdvgridData.horizontalAlignment = GridData.FILL;
        
        GridLayout medOrOdvLayout = new GridLayout();
        medOrOdvLayout.numColumns = 1;
        medOrOdvLayout.marginHeight = 0;
        
        Group medOrOdvGroup = new Group(group, SWT.NONE);
        medOrOdvGroup.setLayout(medOrOdvLayout);
        medOrOdvGroup.setLayoutData(medOrOdvgridData);
        
        radMedatlas = new Button(medOrOdvGroup, SWT.RADIO);
        radMedatlas.setText(Conversion.MEDATLAS.getName());
        radMedatlas.setSelection(false);
        addRadConvertListener(radMedatlas);
        
        radODV = new Button(medOrOdvGroup, SWT.RADIO);
        radODV.setText(Conversion.ODV.getName());
        radODV.setSelection(false);
        addRadConvertListener(radODV);
        
        //****************************************************************************************
        // ODV type
        GridData odvTypegridData = new GridData();
        odvTypegridData.grabExcessHorizontalSpace = true;
        odvTypegridData.horizontalAlignment = GridData.FILL;
        
        GridLayout odvTypeLayout = new GridLayout();
        odvTypeLayout.numColumns = 1;
        odvTypeLayout.marginHeight = 0;
        
        grpODV = new Group(group, SWT.NONE);
        grpODV.setLayout(odvTypeLayout);
        grpODV.setLayoutData(odvTypegridData);
        
        radODVMonoStation = new Button(grpODV, SWT.RADIO);
        radODVMonoStation.setText(Messages.view_convert_monoStation);
        radODVMonoStation.setSelection(false);
        addRadConvertListener(radODVMonoStation);
        radODVMonoFichier = new Button(grpODV, SWT.RADIO);
        radODVMonoFichier.setText(Messages.view_convert_monoFichier);
        addRadConvertListener(radODVMonoFichier);
        
        afficheGrpODV();
    }
    public void afficheGrpODV()
    {
        grpODV.setVisible(radODV.getSelection());
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
    // TRADUCTION DES BOUTONS RADIO
    public E_TypeConversion getSelectedConversion()
    {
        E_TypeConversion selected = null;
        if (radMedatlas.getSelection())
        {
            selected= E_TypeConversion.MEDATLAS;
        }
        else if (radODV.getSelection())
        {
            if (radODVMonoStation.getSelection())
            {
                selected = E_TypeConversion.ODV_MONOSTATION;
            }else if (radODVMonoFichier.getSelection())
            {
                selected = E_TypeConversion.ODV_MULTISTATIONS;
            }else{
               // on laisse null, ce qui provoquera une alerte au check
            }
        }else{
            selected = E_TypeConversion.UNKNOWN;
        }
        
            return selected;
    }
    
    
    /**
     * This method initializes groupMapping
     */
    private void createGroupMapping(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_mapping);
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        checkMapping = new Button(group, SWT.CHECK);
        checkMapping.setFont(Utils.font08);
        checkMapping.setText(Messages.settings_mapping_check);
        checkMapping
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    // On masque/affiche les champs falcutatif en fonction de la
                    // case checkMapping
                    mappingCheckMask(checkMapping.getSelection());
                }
            });

        compo01 = new Composite(group, SWT.NULL);
        compo01.setFont(Utils.font08);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo01);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

        labelFichierMapping = new Label(compo01, SWT.NONE);
        labelFichierMapping.setFont(Utils.font08);
        labelFichierMapping.setText(Messages.settings_mapping_fileName);
        labelFichierMapping.pack();

        textFichierMapping = new Text(compo01, SWT.BORDER);
        textFichierMapping.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(true, false).hint(50, 12).applyTo(
            textFichierMapping);
    }

    /**
     * This method initializes groupMapping
     */
    private void createGroupDateODV(Composite parent)
    {
        group = new Group(parent, SWT.NONE);
        group.setFont(Utils.font08);
        group.setText(Messages.settings_dateODV);
        GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

        AddedParameter dateISO = new AddedParameter(IdParameter.DTUT);
        radDateISO = new Button(group, SWT.RADIO);
        radDateISO.setFont(Utils.font08);
        radDateISO.setText(dateISO.getFullName()
                + " [" + dateISO.getUnit() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        radDateISO
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    radDateJulian.setSelection(!radDateISO.getSelection());
                }
            });

        AddedParameter dateJulian = new AddedParameter(IdParameter.CJDY);
        radDateJulian = new Button(group, SWT.RADIO);
        radDateJulian.setFont(Utils.font08);
        radDateJulian.setText(dateJulian.getFullName()
                + " [" + dateJulian.getUnit() + "]"); //$NON-NLS-1$//$NON-NLS-2$
        radDateJulian
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
                    radDateISO.setSelection(!radDateJulian.getSelection());
                }
            });
    }

    /**
     * This method initializes groupFlag
     */
    private void createGroupFlag(Composite parent)
    {
        compo = new Composite(parent, SWT.NONE);
        compo.setFont(Utils.font08);
        // group.setText(Messages.settings_flags);
        GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
        GridDataFactory.fillDefaults().grab(false, true).hint(600, 90).applyTo(
            compo);

        // Creation du tableau des tables de correspondaces
        createTableFlagTable(compo);

        // Creation du tableau des correspondaces
        createTableFlag(compo);

        // Creation du bouton de flag par defaut
        textAreaFlag = new Label(compo, SWT.NONE);
        textAreaFlag.setFont(Utils.font08);
        textAreaFlag.setText(Messages.settings_flagDefault);

        // FAE 4431 Flag qualité par défault configurable
        _flagDefault = new Combo(compo, SWT.BORDER | SWT.READ_ONLY);
        _flagDefault.setFont(Utils.font08);
        GridDataFactory.fillDefaults().grab(false, false).applyTo(_flagDefault);
    }

    /**
     * This method initializes groupTop3
     */
    private void createCompoBouton(Composite parent)
    {
        compo = new Composite(parent, SWT.NONE);
        compo.setFont(Utils.font08);
        compo.setBackground(parent.getBackground());
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        compo.setLayout(gridLayout);
        GridData data = new GridData();
        data.widthHint = 600;
        data.heightHint = 30;
        compo.setLayoutData(data);

        buttonRestore = new Button(compo, SWT.NONE);
        buttonRestore.setFont(Utils.font08);
        buttonRestore.setText(Messages.button_restore);
        data = new GridData();
        data.grabExcessHorizontalSpace = true;
        buttonRestore.setLayoutData(data);
        buttonRestore.pack();

        buttonValider = new Button(compo, SWT.NONE);
        buttonValider.setFont(Utils.font08);
        buttonValider.setText(Messages.button_validate);
        buttonValider.pack();

        buttonCancel = new Button(compo, SWT.NONE);
        buttonCancel.setFont(Utils.font08);
        buttonCancel.setText(Messages.button_cancel);
        buttonCancel.pack();

        compo.pack();
    }

    public void createTableFlagTable(Composite parent)
    {
        tabFlagTable = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        tabFlagTable.setFont(Utils.font08);
        tabFlagTable.setHeaderVisible(true);
        tabFlagTable.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlagTable);

        TableColumn tableColumn1 = new TableColumn(tabFlagTable, SWT.NONE);
        tableColumn1.setWidth(60);
        tableColumn1.setText(Messages.table_label_code);

        TableColumn tableColumn2 = new TableColumn(tabFlagTable, SWT.NONE);
        tableColumn2.setWidth(200);
        tableColumn2.setText(Messages.table_label_label);

        // Menu contextuel
        Menu menu = new Menu(tabFlagTable.getShell(), SWT.POP_UP);
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setText(Messages.menu_label_add);
        MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
        itemRemove.setText(Messages.menu_label_remove);
        MenuItem itemDefault = new MenuItem(menu, SWT.NONE);
        itemDefault.setText(Messages.menu_label_default);
        tabFlagTable.setMenu(menu);

        final TableEditor editor = new TableEditor(tabFlagTable);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        // final Table table = tabFlagTable;

        tabFlagTable.addMouseListener(new MouseAdapter() {
            @Override
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
                item.setText(0, Messages.menu_label_new);
                item.setText(1, Messages.menu_label_new);

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

    protected void selectDefaultFlagsTab(String defaultCode)
    {
        setDefaultCode(defaultCode);
        // Mise en gras du flagtable par defaut
        Font normal =
            new Font(Application.display, Global.EMPTY_STRING, 8, SWT.NORMAL);
        Font bold =
            new Font(Application.display, Global.EMPTY_STRING, 8, SWT.BOLD);

        for (int i = 0; i < tabFlagTable.getItemCount(); i++)
        {
            if (tabFlagTable.getItem(i).getText(0).equals(getDefaultCode()))
            {
                tabFlagTable.getItem(i).setFont(bold);
            }
            else
            {
                tabFlagTable.getItem(i).setFont(normal);
            }
        }
    }

    public void createTableFlag(Composite parent)
    {
        tabFlag = new Table(parent, SWT.FULL_SELECTION | SWT.BORDER);
        tabFlag.setFont(Utils.font08);
        tabFlag.setHeaderVisible(true);
        tabFlag.setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tabFlag);
        TableColumn flagColumn1 = new TableColumn(tabFlag, SWT.NONE);
        flagColumn1.setWidth(60);
        flagColumn1.setText(Messages.table_label_local);

        TableColumn flagColumn2 = new TableColumn(tabFlag, SWT.NONE);
        flagColumn2.setWidth(210);
        flagColumn2.setText(Messages.table_label_seadatanet);

        // Menu contextuel
        Menu menu = new Menu(tabFlag.getShell(), SWT.POP_UP);
        MenuItem itemAdd = new MenuItem(menu, SWT.NONE);
        itemAdd.setText(Messages.menu_label_add);
        MenuItem itemRemove = new MenuItem(menu, SWT.NONE);
        itemRemove.setText(Messages.menu_label_remove);

        tabFlag.setMenu(menu);

        final Table tab = tabFlag;

        final TableEditor editor = new TableEditor(tabFlag);
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;

        tabFlag.addMouseListener(new MouseAdapter() {
            @Override
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
                item.setText(0, Messages.menu_label_new);
                item.setText(1, Messages.menu_label_new);

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
        parent.setLayout(new GridLayout());

        FormToolkit toolkitOption = new FormToolkit(parent.getDisplay());
        toolkitOption.setBackground(parent.getBackground());
        final ScrolledForm formOption =
            toolkitOption.createScrolledForm(parent);
        formOption.setBackground(parent.getBackground());
        formOption.getBody().setLayout(new GridLayout());
        GridData data = new GridData();
        data.widthHint = 600;
        data.grabExcessVerticalSpace = true;
        formOption.getBody().setLayoutData(data);

        createSection(toolkitOption, formOption, Messages.settings_general,
            ExpandableComposite.EXPANDED);
        createSection(toolkitOption, formOption, Messages.settings_web,
            ExpandableComposite.COMPACT);
        createSection(toolkitOption, formOption, Messages.settings_cdi,
            ExpandableComposite.COMPACT);
        createSection(toolkitOption, formOption, Messages.settings_flags,
            ExpandableComposite.EXPANDED);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        data = new GridData();
        data.widthHint = 660;
        sep.setLayoutData(data);
        createCompoBouton(parent);

        // listener cancel
        buttonCancel
            .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                @Override
                public void widgetSelected(
                        org.eclipse.swt.events.SelectionEvent e)
                {
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
                        // message d'information pour redémarrage de
                        // l'application
                        boolean writeIni = checkRestart();

                        // sauvegarde de la config
                        saveConfig(writeIni);

                        // reinitialisation de la conversion
                        Nemo.getInstance().getDomain().getData().setAddedParameterModel();
                        // if (DataModel.inputModel != null)
                        // DataModel.inputModel.setConverted(false);

                        // fermeture de la fenêtre
                        shelloptions.close();

                    }
                    else
                    {
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
                    Nemo.getInstance().restoreDefaultConfig();
                    remplissageOptions();

                    buttonCancel.setEnabled(false);
                }
            });

    }

    private void createSection(FormToolkit toolkitOption,
            final ScrolledForm formOption, String title, int expandStyle)
    {
        Section section =
            toolkitOption.createSection(formOption.getBody(),
                ExpandableComposite.TREE_NODE | expandStyle);

        section.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e)
            {
                formOption.reflow(true);
            }
        });
        section.setText(title);

        Composite sectionClient = toolkitOption.createComposite(section);
        sectionClient.setLayout(new GridLayout());

        if (title.equals(Messages.settings_general))
        {
            createCompoHaut(sectionClient);
            createGroupEdmo(sectionClient);
            createCompoBas(sectionClient);
            createGroupDataList(sectionClient);
            createGroupOutputFormat(sectionClient);
        }
        else if (title.equals(Messages.settings_web))
        {
            createGroupWS(sectionClient);
        }
        else if (title.equals(Messages.settings_cdi))
        {
            createGroupCDI(sectionClient);
        }
        else if (title.equals(Messages.settings_flags))
        {
            createGroupFlag(sectionClient);
        }

        section.setClient(sectionClient);
        section.pack();
    }

    protected boolean checkConfig()
    {
        boolean blnOK = true;
        StringBuffer message = new StringBuffer();
        StringBuffer messageCDI = new StringBuffer();

        message.append(checkMandatory(comboIdEdmoRespCdi.getText(),
            Messages.settings_EDMOauthor, Global.SEP));
        if (useProxy.getSelection())
        {
            message.append(checkMandatory(textAdrProxy.getText(),
                Messages.settings_proxy_adr, Global.SEP));
            message.append(checkMandatory(textPortProxy.getText(),
                Messages.settings_proxy_port, Global.SEP));
        }

        if (message.length() > 0)
        {
            message.insert(0, Messages.error_settings_config);
            blnOK = false;
        }

        if (checkCDI.getSelection())
        {
            messageCDI.append(checkMandatory(comboCodeEdmoDonnees.getText(),
                Messages.settings_EDMOori, Global.SEP));
            messageCDI.append(checkMandatory(comboIdEdmoRespDonnees.getText(),
                Messages.settings_EDMOcustodian, Global.SEP));
            messageCDI.append(checkMandatory(comboIdEdmoDistDonnees.getText(),
                Messages.settings_EDMOdist, Global.SEP));
            messageCDI.append(checkMandatory(textUrlCdi.getText(),
                Messages.settings_dataDist_web, Global.SEP));
            messageCDI.append(checkMandatory(comboMethodeCdi.getText(),
                Messages.settings_dataDist_met, Global.SEP));
            messageCDI.append(checkMandatory(comboTypePlateForme.getText(),
                Messages.settings_typePlateForme, Global.SEP));

            if (messageCDI.length() > 0)
            {
                messageCDI.insert(0, Messages.error_settings_cdi);
                message.append(messageCDI);
                blnOK = false;
            }
        }

        if (checkMapping.getSelection())
        {
            String errorMapping =
                checkMandatory(textFichierMapping.getText(),
                    Messages.error_settings_mapping, Global.RN);
            if (errorMapping.length() > 0)
            {
                message.append(errorMapping);
                blnOK = false;
            }
        }

        if (getSelectedConversion()==null)
        {
            message.append(Messages.error_settings_outputFormat);
            blnOK = false;
        }
        if (!blnOK)
        {
            try
            {
                String[] buttons = new String[1];
                buttons[0] = Messages.success;
                MessageDialog dialog =
                    new MessageDialog(Utils.getMainShell(),
                        Messages.error_missing, null, message.toString(),
                        MessageDialog.ERROR, buttons, 0);
                dialog.open();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return blnOK;
    }

    protected String checkMandatory(String text, String errorMsg, String sep)
    {
        StringBuffer message = new StringBuffer();
        if (text.trim().equals(Global.EMPTY_STRING))
        {
            message.append(Global.RN);
            message.append(sep);
            message.append(errorMsg);
        }
        return message.toString();
    }

    protected void redLabels()
    {
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

    protected void redLabel(String text, Label redLabel)
    {
        if (text.trim().equals(Global.EMPTY_STRING))
        {
            redLabel.setForeground(Display.getCurrent().getSystemColor(
                SWT.COLOR_RED));
        }
        else
        {
            redLabel.setForeground(Display.getCurrent().getSystemColor(
                SWT.COLOR_BLACK));
        }
    }

    protected boolean checkRestart()
    {
        boolean writeIni = false;
        StringBuffer message = new StringBuffer();

        if (comboLangue.getText().equals(Messages.settings_lang_EN))
        {
            if (configLangage.equals(Intern.lang_fr))
                message.append(Global.RN + Global.SEP + Messages.settings_lang);
        }
        else
        {
            if (configLangage.equals(Intern.lang_en))
                message.append(Global.RN + Global.SEP + Messages.settings_lang);
        }

        if (useProxy.getSelection() != configProxy.isUse())
        {
            // proxy configuration change
            message.append(Global.RN + Global.SEP + Messages.settings_proxy);
            writeIni = true;
        }
        else
        {
            if (useProxy.getSelection())
            {
                if (!textAdrProxy.getText().trim().equals(
                    configProxy.getAdress().trim()))
                {
                    // proxy address change (can't be null)
                    message.append(Global.RN + Global.SEP
                            + Messages.settings_proxy_adr);
                    writeIni = true;
                }
                if (!textPortProxy.getText().trim().equals(
                    configProxy.getPort().trim()))
                {
                    // proxy port change (can't be null)
                    message.append(Global.RN + Global.SEP
                            + Messages.settings_proxy_port);
                    writeIni = true;
                }

                if (textLoginProxy.getText() == null)
                {
                    if (!configProxy.getLogin().equals(Global.EMPTY_STRING))
                    {
                        // proxy login change
                        message.append(Global.RN + Global.SEP
                                + Messages.settings_proxy_login);
                        writeIni = true;
                    }
                }
                else if (!textLoginProxy.getText().trim().equals(
                    configProxy.getLogin().trim()))
                {
                    // proxy login change
                    message.append(Global.RN + Global.SEP
                            + Messages.settings_proxy_login);
                    writeIni = true;
                }

                if (textPassProxy.getText() == null)
                {
                    if (!configProxy.getPassword().equals(Global.EMPTY_STRING))
                    {
                        // proxy password change
                        message.append(Global.RN + Global.SEP
                                + Messages.settings_proxy_pass);
                        writeIni = true;
                    }
                }
                else if (!textPassProxy.getText().trim().equals(
                    configProxy.getPassword().trim()))
                {
                    // proxy password change
                    message.append(Global.RN + Global.SEP
                            + Messages.settings_proxy_pass);
                    writeIni = true;
                }
            }
        }

        if (message.length() > 0)
        {
            message.insert(0, Messages.warning_settings);
            MessageDialog dialog =
                new MessageDialog(Utils.getMainShell(),
                    Messages.warning_settings_title, null, message.toString(),
                    MessageDialog.WARNING, new String[] {Messages.success}, 0);
            dialog.open();
        }

        return writeIni;
    }

    protected void saveConfig(boolean writeIni)
    {
        // configuration
        final Configuration configuration = Nemo.getInstance().getConfig();

        // sauvegarde de la config dans le fichier qui va bien
        // CONTACT
        configuration.setContact(Global.EMPTY_STRING);

        // DOSSIERS
        configuration.setModelsDirectory(textDossierModele.getText());
        configuration.setCruisesDirectory(textDossierCampagne.getText());

        // LANGUE
        if (comboLangue.getItem(comboLangue.getSelectionIndex()).equals(
            Messages.settings_lang_FR))
        {
            configuration.setLang(Intern.lang_fr);
        }
        else
        {
            configuration.setLang(Intern.lang_en);
        }

        // CENTRE DE DONNEES
        configuration.setDataCenter(Utils.extraitCode(comboCentreDonnees
            .getText()));

        // WS
        configuration.setNautilusWebServiceURL(textAreaNautilus.getText());
        configuration.setNemoWebServiceURL(textAreaNemo.getText());
        configuration.setVocabURL(textVocabURL.getText());
        configuration.setEdmoURL(textWSEdmo.getText());
        configuration.getProxy().update(writeIni);

        for (BODCList list : configuration.getVocabLists().values())
        {
            list.setUrl();
        }

        // CDI
        configuration.setWriteCdi(checkCDI.getSelection());
        configuration.setIdEdmoRespCdi(Utils.extraitCode(comboIdEdmoRespCdi
            .getText()));
        configuration.setCodeEdmoDonnees(Utils.extraitCode(comboCodeEdmoDonnees
            .getText()));
        configuration.setIdEdmoRespDonnees(Utils
            .extraitCode(comboIdEdmoRespDonnees.getText()));
        configuration.setIdEdmoDistDonnees(Utils
            .extraitCode(comboIdEdmoDistDonnees.getText()));
        configuration.setCdiURL(textUrlCdi.getText());
        configuration
            .setCdiMethod(Utils.extraitCode(comboMethodeCdi.getText()));
        configuration.setAccesDonnees(Utils.extraitCode(comboAccesDonnees
            .getText()));
        configuration.setTypePlateForme(Utils.extraitCode(comboTypePlateForme
            .getText()));

        // MAPPING
        configuration.setWriteMapping(checkMapping.getSelection());
        configuration.setFichierMapping(textFichierMapping.getText());
        // configuration.setArboMapping(textArboMapping.getText());

        // DATE ODV
        configuration.setDateISO(radDateISO.getSelection());

        // Flags qualite
        configuration.setDefaultCode(getDefaultCode());
        ArrayList<FlagTable> flagTables = new ArrayList<FlagTable>();
        for (int i = 0; i < getFlagTables().size(); i++)
        {
            FlagTable flagTable = new FlagTable(getFlagTables().get(i));
            flagTables.add(flagTable);
        }
        configuration.setFlagTables(flagTables);

        // DataList
        if (button_P011viaP091.getSelection())
        {
            dataList = DataListEnum.P011_VIA_P091;
        }
        else if (button_P091.getSelection())
        {
            dataList = DataListEnum.P091;
        }
        else if(button_P011viaP021.getSelection())
        {
            dataList = DataListEnum.P011_VIA_P021;
        }
        
        if (!dataList.equals(configuration.getDataListInSettings()))
        {
            Nemo.getInstance().getViewMain().getEditDataForm().getMyTableData().removeAllElts();
        }

        configuration.setDataListInSettings(dataList.toString());

        
        // output conversion format
        configuration.setDefaultConversionType(getSelectedConversion());
        
        
        // FAE 4431 Flag qualité par défault configurable
        configuration.setDefaultFlag(_flagDefault.getText());

        // Enregistrement de la configuration
        configuration.save();

        // On fait un reset sur les stations certains changement nécessite un
        // recalcul lors de l'écriture
        InputDomain id = Nemo.getInstance().getDomain();
        if (id != null)
        {
            CruiseDomain cd = id.getCruise();
            if (cd != null)
            {
                StationDomain sd = id.getStation();
                if (sd != null)
                {
                    sd.resetStations();
                }
            }
        }
    }

    private void fillFlagTable()
    {
        int i;
        ArrayList<FlagTable> flagTables = new ArrayList<FlagTable>();
        flagTables =
            FlagTable.getOrderedFlagTables(Nemo.getInstance().getConfig()
                .getFlagTables());

        for (i = 0; i < flagTables.size(); i++)
        {
            TableItem item = new TableItem(tabFlagTable, SWT.NONE);
            item.setText(0, flagTables.get(i).getCode());
            item.setText(1, flagTables.get(i).getLibelle());
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
