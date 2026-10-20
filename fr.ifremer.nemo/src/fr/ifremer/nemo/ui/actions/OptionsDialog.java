package fr.ifremer.nemo.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.FlagTable;
import fr.ifremer.nemo.config.Proxy;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_CJDY;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DTUT;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam;
import fr.ifremer.nemo.ui.ApplicationActionBarAdvisor;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.ComboViewerFilter;
import fr.ifremer.nemo.ui.view.ViewLog;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;

public class OptionsDialog extends TitleAreaDialog  {

	private static CCombo comboLangue, comboMethodeCdi,
	comboTypePlateForme, comboAccesDonnees = null;
	private static ComboViewer dataCenterComboViewer;

	Button buttonEdmoChoiceIdEdmoRespCdi;
	Button buttonEdmoSetInModelIdEdmoRespCdi;
	Button buttonEdmoChoiceIdEdmoRespCsr;
	Button buttonEdmoSetInModelIdEdmoRespCsr;
	Button buttonEdmoChoiceIdCodeEdmoDonnees;
	Button buttonEdmoChoiceIdEdmoRespDonnees;
	Button buttonEdmoChoiceIdEdmoDistDonnees;

	private EdmoViewer edmoViewer;


	private Button editFlags;
	private FlagsViewer flagsViewer;
	private EditFlagsListener editFlagsListener;
	private FlagsChoices tmpFlagsChoices;



	private EdmoChoiceListener edmoChoiceListener;
	private EdmoChoices tmpEdmoChoices;



	// choix du format d'export
	private Button radMedatlas;
	private Button radNetCDF;
	private Button radODV;
	private Button radODVMonoFichier;
	private Button radODVMonoStation;
	private Group grpODV;

	// -- PROXY
	public static Button useProxy;
	private Label labelAdrProxy, labelPortProxy, labelLoginProxy,
	labelPassProxy;
	public static Text textAdrProxy, textPortProxy, textLoginProxy,
	textPassProxy;


	private static Label labelNemo, labelNautilus, labelCampagne, lblCDI,
	labelIdEdmoRespCdi, labelIdEdmoRespCsr, labelIdEdmoRespDonnees, labelCodeEdmoDonnees,
	labelSetInModelIdEdmoRespCdi, labelSetInModelIdEdmoRespCsr,
	labelIdEdmoDistDonnees, labelUrlCdi, labelMethodeCdi, labelWSVocab,
	labelFichierMapping, lblOdvFormatVersion, lblNetCDFFormatVersion,
	lblMedatlasFormatVersion, lblBodvFormatVersion,  lblModvFormatVersion,  lblSodvFormatVersion, labelWSEdmo, labelWSEdmerp, lblTypePlateForme,
	lblAccesDonnees = null;

	private static Button checkCDI, checkMapping, radDateISO, radDateJulian;

	private static Text textAreaNemo, textAreaNautilus;

	static Text textDossierModele;

	static Text textDossierCampagne;
	private static Text textUrlCdi;
	private static Text textFichierMapping;
	private static Text textOdvFormatVersion;
	private static Text textNetCDFFormatVersion;
	private static Text textMedatlasFormatVersion;
	private static Text textBodvFormatVersion;
	private static Text textModvFormatVersion;
	private static Text textSodvFormatVersion;
	private static Text textWSEdmo = null;
	private static Text textWSEdmerp = null;


	// -- options necessitant un redemarrage
	private String configLangage;
	private Proxy configProxy;

	public OptionsDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE |  SWT.MAX | SWT.CLOSE | SWT.APPLICATION_MODAL | SWT.BORDER);
		setBlockOnOpen(false);
	}

	@Override
	public void create() {
		super.create();
		setTitle(Messages.settings_title);
		setMessage(Messages.settings_description, IMessageProvider.INFORMATION);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(810, 860);
				newShell.setText("settings");
	}

	public void init() {
		// configuration
		Configuration config = Nemo.getInstance().getConfig();

		// sauvegarde des options necessitant un redemarrage
		configLangage = config.getLang();
		configProxy = config.getProxy();
		loadEdmoConfig();
		//		// lancement IHM options
		//		shelloptions = new Shell(Application.display, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);
		//
		initFlagsTmpChoices();

	}


	@Override
	protected Control createDialogArea(Composite parent) {
		FormToolkit toolkitOption = new FormToolkit(parent.getDisplay());
				toolkitOption.setBackground(parent.getBackground());
		final ScrolledForm formOption = toolkitOption.createScrolledForm(parent);
		formOption.setBackgroundMode(SWT.INHERIT_NONE);
		formOption.setBackground(parent.getBackground());
		formOption.setLayout(new GridLayout());
		formOption.getBody().setLayout(new GridLayout());
		
		GridData data = new GridData();
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		data.verticalAlignment = SWT.TOP;
		formOption.getBody().setLayoutData(data);
		formOption.setLayoutData(data);
		edmoChoiceListener = new EdmoChoiceListener();
		editFlagsListener = new EditFlagsListener();

		createSection(toolkitOption, formOption, Messages.settings_general, ExpandableComposite.EXPANDED);
		createSection(toolkitOption, formOption, Messages.settings_web, ExpandableComposite.COMPACT);
		createSection(toolkitOption, formOption, Messages.settings_cdi, ExpandableComposite.COMPACT);
		createSection(toolkitOption, formOption, Messages.settings_flags, ExpandableComposite.EXPANDED);

		//		Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		//		data = new GridData();
		//		data.widthHint = 660;
		//		sep.setLayoutData(data);
		//			createCompoBouton(parent);

		updateEdmo(tmpEdmoChoices);

		// chargement des options
		try {
			alimenteCombos();
		} catch (VocabularyException e) {
			Nemo.getInstance().getLogger().error(e.getMessage());
		}

		remplissageOptions();

		return formOption;
	}


	private void createSection(FormToolkit toolkitOption, final ScrolledForm formOption, String title, int expandStyle) {
		Section section = toolkitOption.createSection(formOption.getBody(), ExpandableComposite.TREE_NODE | expandStyle);

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				formOption.reflow(true);
				//					pack(); // add pack, since update to photon
			}
		});
		section.setText(title);

		Composite sectionClient = toolkitOption.createComposite(section);
		sectionClient.setLayout(new GridLayout());

		if (title.equals(Messages.settings_general)) {
			createCompoHaut(sectionClient);
			createGroupEdmo(sectionClient);
			createCompoBas(sectionClient);
			createGroupOutputFormat(sectionClient);
		} else if (title.equals(Messages.settings_web)) {
			createGroupWS(sectionClient);
		} else if (title.equals(Messages.settings_cdi)) {
			createGroupCDI(sectionClient);
		} else if (title.equals(Messages.settings_flags)) {
			createGroupFlag(sectionClient);
		}

		section.setClient(sectionClient);
		section.pack();
	}


	/**
	 * This method initializes compoHaut
	 */
	private void createCompoHaut(Composite parent) {
		Composite compo = new Composite(parent, SWT.NONE);
		compo.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(compo);
		GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT).applyTo(compo);

		createCompoLCD(compo);
		createGroupDossier(compo);
	}

	/**
	 * This method initializes compoLCD
	 */
	private void createCompoLCD(Composite parent) {
		Composite compo01 = new Composite(parent, SWT.NONE);
		compo01.setFont(Utils.font08);

		GridLayoutFactory.fillDefaults().applyTo(compo01);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

		createGroupLangue(compo01);
		createGroupCentreDonnees(compo01);
	}

	/**
	 * This method initializes groupLangue
	 */
	private void createGroupLangue(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.settings_lang);

		GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

		comboLangue = new CCombo(group, SWT.READ_ONLY);
		comboLangue.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboLangue);

		comboLangue.add(Messages.settings_lang_FR);
		comboLangue.add(Messages.settings_lang_EN);
		comboLangue.select(0);

		// Disable mouse wheel event
		comboLangue.addListener(SWT.MouseVerticalWheel, new Listener()
		{
			@Override
			public void handleEvent(Event arg0)
			{
				arg0.doit = false;
			}
		});

		group.pack();
	}

	/**
	 * This method initializes groupCentreDonnees
	 */
	private void createGroupCentreDonnees(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.settings_datacenter);

		GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);
		
		dataCenterComboViewer = new ComboViewer(group, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(dataCenterComboViewer.getControl());
		
		dataCenterComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Entry<String,String> entry = (Entry) element;
				if (element != null) {
					if (!entry.getKey().isEmpty() && !entry.getValue().isEmpty()) {
					return entry.getKey() + Global.SEP + entry.getValue();
					} else if (entry.getKey().isEmpty()) {
						return entry.getValue();
					} else if (entry.getValue().isEmpty()) {
						return entry.getKey();
					}
				}
				return "";
			}
		});

		dataCenterComboViewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                Collection<?> col = null;
                if (inputElement instanceof Map<?, ?>) {
                    col = ((Map<String, String>) inputElement).entrySet();
                }
                return col.toArray();
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            @Override
            public void dispose() {
            }
        });

		dataCenterComboViewer.addFilter(new ComboViewerFilter(dataCenterComboViewer, ComboViewerFilter.MatchTypeEnum.CONTAINS));
	
		// Disable mouse wheel event
		dataCenterComboViewer.getCombo().addListener(SWT.MouseVerticalWheel, new Listener()
		{
			@Override
			public void handleEvent(Event arg0)
			{
				arg0.doit = false;
			}
		});
	}

	/**
	 * This method initializes groupDossier
	 */
	private void createGroupDossier(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group);

		// groupDossier.setLayout(null);
		group.setText(Messages.settings_dir);

		Group group01 = new Group(group, SWT.NONE);
		group01.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group01);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group01);
		group01.setText(Messages.settings_dir_model);
		group01.pack();

		textDossierModele = new Text(group01, SWT.BORDER);
		textDossierModele.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDossierModele);
		Button buttonParcourirModele = new Button(group01, SWT.NONE);
		buttonParcourirModele.setFont(Utils.font08);
		buttonParcourirModele.setText(Messages.button_browse);
		buttonParcourirModele.pack();
		// listener sur le bouton
		buttonParcourirModele.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String nomRepertoire;
				Shell shell = Utils.getMainShell();
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
				nomRepertoire = dialog.open();
				if (nomRepertoire != null) {
					textDossierModele.setText(nomRepertoire);
				} else {
					ViewLog.styledTextLogErreurs.setText(Messages.error_settings_dir);
				}
			}
		});

		Group group02 = new Group(group, SWT.NONE);
		group02.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(group02);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(group02);
		group02.setText(Messages.settings_dir_data);
		group02.pack();

		textDossierCampagne = new Text(group02, SWT.BORDER);
		textDossierCampagne.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textDossierCampagne);
		Button buttonParcourirCampagne = new Button(group02, SWT.NONE);
		buttonParcourirCampagne.setFont(Utils.font08);
		buttonParcourirCampagne.setText(Messages.button_browse);
		buttonParcourirCampagne.pack();
		// listener sur le bouton
		buttonParcourirCampagne.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String nomRepertoire;
				Shell shell = Utils.getMainShell();
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
				nomRepertoire = dialog.open();
				if (nomRepertoire != null) {
					textDossierCampagne.setText(nomRepertoire);
				} else {
					ViewLog.styledTextLogErreurs.setText(Messages.error_settings_dir);
				}
			}
		});
	}

	/**
	 * This method initializes groupWS
	 */
	private void createGroupWS(Composite parent) {
		Group compo01 = new Group(parent, SWT.NONE);
		compo01.setFont(Utils.font08);
		// group.setText(Messages.settings_web);
		GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(compo01);
		GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT).applyTo(compo01);

		Composite compo = new Composite(compo01, SWT.NONE);
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
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textAreaNautilus);

		// la lib sdnVocabulary integre l'adresse
		// // WS Vocab
		// labelWSVocab = new Label(compo, SWT.NONE);
		// labelWSVocab.setFont(Utils.font08);
		// labelWSVocab.setText(Messages.settings_web_vocab);
		// textVocabURL = new Text(compo, SWT.BORDER);
		// textVocabURL.setFont(Utils.font08);
		// GridDataFactory.fillDefaults().grab(true,
		// false).applyTo(textVocabURL);

		labelWSEdmo = new Label(compo, SWT.NONE);
		labelWSEdmo.setFont(Utils.font08);
		labelWSEdmo.setText(Messages.settings_web_edmo);
		textWSEdmo = new Text(compo, SWT.BORDER);
		textWSEdmo.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textWSEdmo);
		
		labelWSEdmerp = new Label(compo, SWT.NONE);
		labelWSEdmerp.setFont(Utils.font08);
		labelWSEdmerp.setText(Messages.settings_web_edmerp);
		textWSEdmerp = new Text(compo, SWT.BORDER);
		textWSEdmerp.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textWSEdmerp);

		// -- PROXY
		createProxyGroup(compo01);
	}

	private void createProxyGroup(Composite parent) {
		Composite compo01 = new Composite(parent, SWT.NULL);
		compo01.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(1).applyTo(compo01);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

		Label label = new Label(compo01, SWT.NULL);
		label.setFont(Utils.font08);
		GridDataFactory.swtDefaults().grab(false, false).hint(10, 4).applyTo(label);

		// PROXY
		useProxy = new Button(compo01, SWT.CHECK);
		useProxy.setFont(Utils.font08);
		useProxy.setText(Messages.settings_proxy);
		useProxy.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// enable proxy
				enableProxy(useProxy.getSelection());
			}
		});

		Composite compo02 = new Composite(compo01, SWT.NULL);
		compo02.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(5).applyTo(compo02);
		// GridDataFactory.fillDefaults().grab(true, true).applyTo(compoProxy);

		labelAdrProxy = new Label(compo02, SWT.NULL);
		labelAdrProxy.setFont(Utils.font08);
		labelAdrProxy.setText(Messages.settings_proxy_adr);
		GridDataFactory.fillDefaults().grab(true, false).minSize(100, 12).applyTo(labelAdrProxy);

		textAdrProxy = new Text(compo02, SWT.BORDER);
		textAdrProxy.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textAdrProxy);

		label = new Label(compo02, SWT.NULL);
		label.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).minSize(10, 12).applyTo(label);

		labelPortProxy = new Label(compo02, SWT.NULL);
		labelPortProxy.setFont(Utils.font08);
		labelPortProxy.setText(Messages.settings_proxy_port);
		GridDataFactory.fillDefaults().grab(true, false).minSize(70, 12).applyTo(labelPortProxy);

		textPortProxy = new Text(compo02, SWT.BORDER);
		textPortProxy.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textPortProxy);

		compo02 = new Composite(compo01, SWT.NULL);
		GridLayoutFactory.fillDefaults().numColumns(5).applyTo(compo02);

		labelLoginProxy = new Label(compo02, SWT.NULL);
		labelLoginProxy.setFont(Utils.font08);
		labelLoginProxy.setText(Messages.settings_proxy_login);
		GridDataFactory.fillDefaults().grab(true, false).minSize(100, 12).applyTo(labelLoginProxy);

		textLoginProxy = new Text(compo02, SWT.BORDER);
		textLoginProxy.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textLoginProxy);

		label = new Label(compo02, SWT.NULL);
		label.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).minSize(10, 12).applyTo(label);

		labelPassProxy = new Label(compo02, SWT.NULL);
		labelPassProxy.setFont(Utils.font08);
		labelPassProxy.setText(Messages.settings_proxy_pass);
		GridDataFactory.fillDefaults().grab(true, false).minSize(70, 12).applyTo(labelPassProxy);

		textPassProxy = new Text(compo02, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		textPassProxy.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).hint(170, 12).applyTo(textPassProxy);

	}

	/**
	 * This method initializes groupCDI
	 */
	private void createGroupEdmo(Composite parent) {

		Group groupEdmoRespCDI = new Group(parent, SWT.NONE);
		groupEdmoRespCDI.setFont(Utils.font08);
		groupEdmoRespCDI.setText(Messages.settings_EDMO);
		GridLayoutFactory.fillDefaults().margins(4, 4).numColumns(2).applyTo(groupEdmoRespCDI);
		GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT).applyTo(groupEdmoRespCDI);

		buttonEdmoChoiceIdEdmoRespCdi = new Button(groupEdmoRespCDI, SWT.PUSH);
		buttonEdmoChoiceIdEdmoRespCdi.setText(Messages.settings_EDMO_choice_button);
		buttonEdmoChoiceIdEdmoRespCdi.addListener(SWT.Selection, edmoChoiceListener);

		labelIdEdmoRespCdi = new Label(groupEdmoRespCDI, SWT.NONE);
		labelIdEdmoRespCdi.setFont(Utils.font08);

		buttonEdmoChoiceIdEdmoRespCsr= new Button(groupEdmoRespCDI, SWT.PUSH);
		buttonEdmoChoiceIdEdmoRespCsr.setText(Messages.settings_EDMO_choice_button);
		buttonEdmoChoiceIdEdmoRespCsr.addListener(SWT.Selection, edmoChoiceListener);

		labelIdEdmoRespCsr = new Label(groupEdmoRespCDI, SWT.NONE);
		labelIdEdmoRespCsr.setFont(Utils.font08);
	}

	/**
	 * This method initializes groupCDI
	 */
	private void createGroupCDI(Composite parent) {
		// group = new Group(parent, SWT.NONE);
		Composite compo = new Composite(parent, SWT.NONE);
		compo.setFont(Utils.font08);
		// group.setText(Messages.settings_cdi);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(2).applyTo(compo);
		GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT).applyTo(compo);

		// compo vide pour mise en forme
		lblCDI = new Label(compo, SWT.NONE);
		lblCDI.setFont(Utils.font08);
		lblCDI.setText(Messages.settings_cdi_check);
		checkCDI = new Button(compo, SWT.CHECK);
		checkCDI.setFont(Utils.font08);
		// On masque/affiche les champs falcutatif en fonction de la case
		// checkCDI
		checkCDI.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CDICheckMask(checkCDI.getSelection());
			}
		});

		buttonEdmoChoiceIdCodeEdmoDonnees = new Button(compo, SWT.PUSH);
		buttonEdmoChoiceIdCodeEdmoDonnees.setText(Messages.settings_EDMO_choice_button);// TODO
		buttonEdmoChoiceIdCodeEdmoDonnees.addListener(SWT.Selection, edmoChoiceListener);
		buttonEdmoChoiceIdCodeEdmoDonnees.pack();

		labelCodeEdmoDonnees = new Label(compo, SWT.NONE);
		labelCodeEdmoDonnees.setFont(Utils.font08);
		labelCodeEdmoDonnees.pack();

		buttonEdmoChoiceIdEdmoRespDonnees = new Button(compo, SWT.PUSH);
		buttonEdmoChoiceIdEdmoRespDonnees.setText(Messages.settings_EDMO_choice_button);// TODO
		buttonEdmoChoiceIdEdmoRespDonnees.addListener(SWT.Selection, edmoChoiceListener);
		buttonEdmoChoiceIdEdmoRespDonnees.pack();

		labelIdEdmoRespDonnees = new Label(compo, SWT.NONE);
		labelIdEdmoRespDonnees.setFont(Utils.font08);
		labelIdEdmoRespDonnees.pack();

		buttonEdmoChoiceIdEdmoDistDonnees = new Button(compo, SWT.PUSH);
		buttonEdmoChoiceIdEdmoDistDonnees.setText(Messages.settings_EDMO_choice_button);// TODO
		buttonEdmoChoiceIdEdmoDistDonnees.addListener(SWT.Selection, edmoChoiceListener);
		buttonEdmoChoiceIdEdmoDistDonnees.pack();

		labelIdEdmoDistDonnees = new Label(compo, SWT.NONE);
		labelIdEdmoDistDonnees.setFont(Utils.font08);
		labelIdEdmoDistDonnees.pack();

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

		comboMethodeCdi = new CCombo(compo, SWT.BORDER | SWT.READ_ONLY);
		comboMethodeCdi.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(comboMethodeCdi);
		// Disable mouse wheel event
		comboMethodeCdi.addListener(SWT.MouseVerticalWheel, new Listener()
		{
			@Override
			public void handleEvent(Event arg0)
			{
				arg0.doit = false;
			}
		});


		// FAE 59 CDL 09/04/08
		lblTypePlateForme = new Label(compo, SWT.NONE);
		lblTypePlateForme.setFont(Utils.font08);
		lblTypePlateForme.setText(Messages.settings_typePlateForme);

		comboTypePlateForme = new CCombo(compo, SWT.BORDER | SWT.READ_ONLY);
		comboTypePlateForme.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(comboTypePlateForme);
		// Disable mouse wheel event
		comboTypePlateForme.addListener(SWT.MouseVerticalWheel, new Listener()
		{
			@Override
			public void handleEvent(Event arg0)
			{
				arg0.doit = false;
			}
		});

		lblAccesDonnees = new Label(compo, SWT.NONE);
		lblAccesDonnees.setFont(Utils.font08);
		lblAccesDonnees.setText(Messages.settings_accesDonnees);

		comboAccesDonnees = new CCombo(compo, SWT.BORDER | SWT.READ_ONLY);
		comboAccesDonnees.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(comboAccesDonnees);
		// Disable mouse wheel event
		comboAccesDonnees.addListener(SWT.MouseVerticalWheel, new Listener()
		{
			@Override
			public void handleEvent(Event arg0)
			{
				arg0.doit = false;
			}
		});

		// FAE 16993 FORMAT VERSION
		// Version Format ODV
		lblOdvFormatVersion = new Label(compo, SWT.NONE);
		lblOdvFormatVersion.setFont(Utils.font08);
		lblOdvFormatVersion.setText(Messages.settings_odvVersion);
		lblOdvFormatVersion.pack();

		textOdvFormatVersion = new Text(compo, SWT.BORDER);
		textOdvFormatVersion.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textOdvFormatVersion);

		// Version Format Medatlas
		lblMedatlasFormatVersion = new Label(compo, SWT.NONE);
		lblMedatlasFormatVersion.setFont(Utils.font08);
		lblMedatlasFormatVersion.setText(Messages.settings_medatlasVersion);
		lblMedatlasFormatVersion.pack();

		textMedatlasFormatVersion = new Text(compo, SWT.BORDER);
		textMedatlasFormatVersion.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textMedatlasFormatVersion);

		// Version Format NetCDF
		lblNetCDFFormatVersion = new Label(compo, SWT.NONE);
		lblNetCDFFormatVersion.setFont(Utils.font08);
		lblNetCDFFormatVersion.setText(Messages.settings_netcdfVersion);
		lblNetCDFFormatVersion.pack();

		textNetCDFFormatVersion = new Text(compo, SWT.BORDER);
		textNetCDFFormatVersion.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textNetCDFFormatVersion);

		// Version Format BODV
		lblBodvFormatVersion = new Label(compo, SWT.NONE);
		lblBodvFormatVersion.setFont(Utils.font08);
		lblBodvFormatVersion.setText(Messages.settings_bodvVersion);
		lblBodvFormatVersion.pack();

		textBodvFormatVersion = new Text(compo, SWT.BORDER);
		textBodvFormatVersion.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textBodvFormatVersion);

		// Version Format MODV
		lblModvFormatVersion = new Label(compo, SWT.NONE);
		lblModvFormatVersion.setFont(Utils.font08);
		lblModvFormatVersion.setText(Messages.settings_modvVersion);
		lblModvFormatVersion.pack();

		textModvFormatVersion = new Text(compo, SWT.BORDER);
		textModvFormatVersion.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textModvFormatVersion);

		// Version Format SODV
		lblSodvFormatVersion = new Label(compo, SWT.NONE);
		lblSodvFormatVersion.setFont(Utils.font08);
		lblSodvFormatVersion.setText(Messages.settings_sodvVersion);
		lblSodvFormatVersion.pack();

		textSodvFormatVersion = new Text(compo, SWT.BORDER);
		textSodvFormatVersion.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textSodvFormatVersion);
	}

	/**
	 * This method initializes compoBas
	 */
	private void createCompoBas(Composite parent) {
		Composite compo = new Composite(parent, SWT.NONE);
		compo.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
		GridDataFactory.fillDefaults().grab(false, true).hint(600, SWT.DEFAULT).applyTo(compo);

		createGroupMapping(compo);
		createGroupDateODV(compo);
	}

	/**
	 * This method initializes data list group
	 */
	private void createGroupOutputFormat(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.settings_defaultOutputFormat);
		GridLayoutFactory.fillDefaults().numColumns(3).equalWidth(true).margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

		// ****************************************************************************************
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

		radNetCDF = new Button(medOrOdvGroup, SWT.RADIO);
		radNetCDF.setText(Conversion.NETCDF.getName());
		radNetCDF.setSelection(false);
		addRadConvertListener(radNetCDF);

		// ****************************************************************************************
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

	/**
	 * This method initializes groupMapping
	 */
	private void createGroupMapping(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.settings_mapping);
		GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

		checkMapping = new Button(group, SWT.CHECK);
		checkMapping.setFont(Utils.font08);
		checkMapping.setText(Messages.settings_mapping_check);
		checkMapping.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// On masque/affiche les champs falcutatif en fonction
				// de la
				// case checkMapping
				mappingCheckMask(checkMapping.getSelection());
			}
		});

		Composite compo01 = new Composite(group, SWT.NULL);
		compo01.setFont(Utils.font08);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo01);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(compo01);

		labelFichierMapping = new Label(compo01, SWT.NONE);
		labelFichierMapping.setFont(Utils.font08);
		labelFichierMapping.setText(Messages.settings_mapping_fileName);
		labelFichierMapping.pack();

		textFichierMapping = new Text(compo01, SWT.BORDER);
		textFichierMapping.setFont(Utils.font08);
		GridDataFactory.fillDefaults().grab(true, false).hint(50, 12).applyTo(textFichierMapping);
	}

	/**
	 * This method initializes groupMapping
	 */
	private void createGroupDateODV(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setFont(Utils.font08);
		group.setText(Messages.settings_dateODV);
		GridLayoutFactory.fillDefaults().margins(4, 4).applyTo(group);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(group);

		AddedParam dateISO = new AP_DTUT();
		radDateISO = new Button(group, SWT.RADIO);
		radDateISO.setFont(Utils.font08);
		radDateISO.setText(dateISO.getFullName() + " [" + dateISO.getUnit() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		radDateISO.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				radDateJulian.setSelection(!radDateISO.getSelection());
			}
		});

		AddedParam dateJulian = new AP_CJDY();
		radDateJulian = new Button(group, SWT.RADIO);
		radDateJulian.setFont(Utils.font08);
		radDateJulian.setText(dateJulian.getFullName() + " [" + dateJulian.getUnit() + "]"); //$NON-NLS-1$//$NON-NLS-2$
		radDateJulian.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				radDateISO.setSelection(!radDateJulian.getSelection());
			}
		});
	}

	/**
	 * This method initializes groupFlag
	 */
	private void createGroupFlag(Composite parent) {
		Composite compo = new Composite(parent, SWT.NONE);
		compo.setFont(Utils.font08);
		// group.setText(Messages.settings_flags);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compo);
		GridDataFactory.fillDefaults().grab(false, true).hint(600, 90).applyTo(compo);


		editFlags = new Button(compo, SWT.PUSH);
		editFlags.setText(Messages.button_edit_flags);
		editFlags.addListener(SWT.Selection, editFlagsListener);
		editFlags.pack();



	}


	//		@Override
	//		protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
	//			return null;
	//		}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		//			Button buttonCancel, buttonValider, buttonRestore,

		Button buttonRestore = createButton(parent, IDialogConstants.RETRY_ID,
				Messages.button_restore, true);
		Button buttonValider =  createButton(parent, IDialogConstants.OK_ID,
				Messages.button_validate, true);
		Button buttonCancel =  createButton(parent, IDialogConstants.CANCEL_ID,
				Messages.button_cancel, false);

		// listener reinitialiser
		buttonRestore.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				Nemo.getInstance().restoreDefaultConfig();
				remplissageOptions();

				buttonCancel.setEnabled(false);
			}
		});




	}


	@Override
	protected void okPressed() {
		// FAE 1123 : validation de la config
		if (checkConfig()) {
			// message d'information pour redémarrage de
			// l'application
			boolean writeIni = checkRestart();

			// sauvegarde de la config
			saveConfig(writeIni);

			// reinitialisation de la conversion
			Nemo.getInstance().getDomain().getData().getMyParameterManager().setAddedParameterModel();
			// if (DataModel.inputModel != null)
			// DataModel.inputModel.setConverted(false);

			// maj Summary
			Nemo.getInstance().getViewMain().getEditConversionForm().fillSummary();

			// fermeture de la fenêtre
			close();

		} else {
			redLabels();
		}
	}

	@Override
	protected void cancelPressed() {
		tmpEdmoChoices = new EdmoChoices();
		// fermeture de la fenêtre
		close();
	}



	@Override
	protected Point getInitialSize() {
		return getShell().computeSize(450, SWT.DEFAULT, true);
	}


	private void alimenteCombos() throws VocabularyException {

		// long start = System.currentTimeMillis();

		// CENTRES DE DONNEES
		Collection<IElement> dataCenterCollection = ExternalResourcesManager.getInstance().getSdnVocabularyManager()
				.getVocabList(Intern.list_dataCenter).getElements();
		// Build inputMap
		TreeMap<String,String> dataCenterMap = new TreeMap<String,String>();		
		for (IElement record : dataCenterCollection) {
			dataCenterMap.put(record.getKey(), record.getLabel());
		}
		// Set input
		dataCenterComboViewer.setInput(dataCenterMap);
		dataCenterComboViewer.setComparator(new ViewerComparatorByKey());

		// CDI
		// FAE 58 CDL 09/04/08
		Collection<IElement> records = ExternalResourcesManager.getInstance().getSdnVocabularyManager().getVocabList(Intern.list_methodCDI).getElements();
		TreeSet<String> champsMethodesCdi = new TreeSet<String>();
		for (IElement record : records) {
			String champCombo = record.getKey() + Global.SEP + record.getLabel();
			champsMethodesCdi.add(champCombo);
		}
		for (String champ : champsMethodesCdi) {
			comboMethodeCdi.add(champ);
		}

		records = ExternalResourcesManager.getInstance().getSdnVocabularyManager().getVocabList(Intern.list_typePlatform).getElements();
		champsMethodesCdi = new TreeSet<String>();
		for (IElement record : records) {
			String champCombo = record.getKey() + Global.SEP + record.getLabel();
			champsMethodesCdi.add(champCombo);
		}
		for (String champ : champsMethodesCdi)
			comboTypePlateForme.add(champ);

		
		 String[] authorizedDataAccesCodes = Intern.authorizedAccesDonneesList.split(Global.SEMICOLON);	
		records = ExternalResourcesManager.getInstance().getSdnVocabularyManager().getVocabList(Intern.list_accesDonnees).getElements();
		champsMethodesCdi = new TreeSet<String>();
		for (IElement record : records) {					
			for (int i =0; i< authorizedDataAccesCodes.length; i++) {
					if (record.getKey().equalsIgnoreCase(authorizedDataAccesCodes[i])) {
					String champCombo = record.getKey() + Global.SEP + record.getLabel();
					champsMethodesCdi.add(champCombo);
				}
			}
		}
		for (String champ : champsMethodesCdi)
			comboAccesDonnees.add(champ);



		// long end = System.currentTimeMillis();

		// System.out
		// .println("Temps : alimenteCombos() = " + (end - start) + "ms");
	}

	protected void remplissageOptions() {

		long start = System.currentTimeMillis();

		// remplissage IHM avec les valeurs du fichier de config
		// configuration
		final Configuration config = Nemo.getInstance().getConfig();

		// DOSSIERS
		textDossierModele.setText(config.getModelsDirectory());
		textDossierCampagne.setText(config.getCruisesDirectory());

		// LANGUE
		if (config.getLocale().toString().equals(Intern.lang_fr)) {
			// on met la combo langue à FR
			comboLangue.select(0);
		} else {
			// on met la combo langue à GB
			comboLangue.select(1);
		}

		// CENTRE DE DONNEES
		Utils.setSelectionInComboViewerByKey(dataCenterComboViewer, config.getDataCenter());

		// WS
		textAreaNemo.setText(config.getNemoWebServiceURL());
		textAreaNautilus.setText(config.getNautilusWebServiceURL());
		// textVocabURL.setText(config.getVocabURL());
		textWSEdmo.setText(config.getEdmoURL());
		textWSEdmerp.setText(config.getEdmerpURL());

		Proxy proxy = config.getProxy();
		useProxy.setSelection(proxy.isUse());
		textAdrProxy.setText(proxy.getAdress());
		textPortProxy.setText(String.valueOf(proxy.getPort()));
		textLoginProxy.setText(proxy.getLogin());
		textPassProxy.setText(proxy.getPassword());
		enableProxy(proxy.isUse());

		// CDI
		checkCDI.setSelection(config.isWriteCdi());
		loadEdmoConfig();
		updateEdmo(tmpEdmoChoices);

		textUrlCdi.setText(config.getCdiURL());
		Utils.selectComboCode(comboMethodeCdi, config.getCdiMethod());
		
		// Check if dataAccescode is authorized
		String[] authorizedDataAccesCodes = Intern.authorizedAccesDonneesList.split(Global.SEMICOLON);
		boolean dataAccescodeFound = false;
		for (int i =0; i< authorizedDataAccesCodes.length; i++) {
			if (config.getAccesDonnees().equalsIgnoreCase(authorizedDataAccesCodes[i])) {
				dataAccescodeFound = true; 
			}
		}
		if (!dataAccescodeFound) {
			config.setAccesDonnees(authorizedDataAccesCodes[0]);
		}
		
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


		// convert type
		radMedatlas.setSelection(false);
		radODV.setSelection(false);
		radNetCDF.setSelection(false);
		radODVMonoStation.setSelection(false);
		radODVMonoFichier.setSelection(false);
		switch (config.getDefaultConversionType()) {
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
		case NETCDF_MONOSTATION:
			radNetCDF.setSelection(true);
			radODVMonoStation.setSelection(true);
			break;
		case NETCDF_MULTISTATIONS:
			radNetCDF.setSelection(true);
			radODVMonoFichier.setSelection(true);
			break;
		case UNKNOWN:
			break;
		}

		// FAE 16993
		textOdvFormatVersion.setText(config.getODVFormatVersion());
		textMedatlasFormatVersion.setText(config.getMedatlasVersion());
		textNetCDFFormatVersion.setText(config.getNetCDFVersion());
		textBodvFormatVersion.setText(config.getBodvVersion());
		textModvFormatVersion.setText(config.getModvVersion());
		textSodvFormatVersion.setText(config.getSodvVersion());

		afficheGrpODV();

		flagsViewer =null;
		initFlagsTmpChoices();

		redLabels();

		long end = System.currentTimeMillis();

		System.out.println("Temps : remplissageOptions() = " + (end - start) + "ms");
	}


	public void updateEdmo(EdmoChoices tmpEdmoChoicesNew) {
		tmpEdmoChoices = tmpEdmoChoicesNew;
		// TODO BGT: remplir tmpEdmoChoices avec config a l'ouverture
		labelIdEdmoRespCdi.setText(Messages.settings_EDMOauthor + ": " + tmpEdmoChoices.getIdEdmoRespCdi());
		labelIdEdmoRespCsr.setText(Messages.settings_EDMOCSRauthor + ": " + tmpEdmoChoices.getIdEdmoRespCsr());

		labelIdEdmoRespDonnees.setText(Messages.settings_EDMOcustodian + ": " + tmpEdmoChoices.getIdEdmoRespDonnees());

		labelCodeEdmoDonnees.setText(Messages.settings_EDMOori + ": " + tmpEdmoChoices.getCodeEdmoDonnees());

		labelIdEdmoDistDonnees.setText(Messages.settings_EDMOdist + ": " + tmpEdmoChoices.getIdEdmoDistDonnees());

		labelIdEdmoRespCdi.pack();
		labelIdEdmoRespCsr.pack();
		labelIdEdmoRespDonnees.pack();
		labelCodeEdmoDonnees.pack();
		labelIdEdmoDistDonnees.pack();

	}


	protected boolean checkConfig() {
		boolean blnOK = true;
		StringBuffer message = new StringBuffer();
		StringBuffer messageCDI = new StringBuffer();

		message.append(checkEdmoMandatory(tmpEdmoChoices.getIdEdmoRespCdi(), Messages.settings_EDMOauthor, Global.SEP));
		if (useProxy.getSelection()) {
			message.append(checkMandatory(textAdrProxy.getText(), Messages.settings_proxy_adr, Global.SEP));
			message.append(checkMandatory(textPortProxy.getText(), Messages.settings_proxy_port, Global.SEP));
		}

		if (message.length() > 0) {
			message.insert(0, Messages.error_settings_config);
			blnOK = false;
		}

		if (checkCDI.getSelection()) {
			messageCDI.append(checkEdmoMandatory(tmpEdmoChoices.getCodeEdmoDonnees(), Messages.settings_EDMOori, Global.SEP));
			messageCDI.append(checkEdmoMandatory(tmpEdmoChoices.getIdEdmoRespDonnees(), Messages.settings_EDMOcustodian, Global.SEP));
			messageCDI.append(checkEdmoMandatory(tmpEdmoChoices.getIdEdmoDistDonnees(), Messages.settings_EDMOdist, Global.SEP));

			messageCDI.append(checkMandatory(textUrlCdi.getText(), Messages.settings_dataDist_web, Global.SEP));
			messageCDI.append(checkMandatory(comboMethodeCdi.getText(), Messages.settings_dataDist_met, Global.SEP));
			messageCDI.append(checkMandatory(comboTypePlateForme.getText(), Messages.settings_typePlateForme, Global.SEP));

			messageCDI.append(checkMandatory(textMedatlasFormatVersion.getText(), Messages.settings_medatlasVersion, Global.SEP));
			messageCDI.append(checkMandatory(textOdvFormatVersion.getText(), Messages.settings_odvVersion, Global.SEP));
			messageCDI.append(checkMandatory(textNetCDFFormatVersion.getText(), Messages.settings_netcdfVersion, Global.SEP));
			messageCDI.append(checkMandatory(textBodvFormatVersion.getText(), Messages.settings_bodvVersion, Global.SEP));
			messageCDI.append(checkMandatory(textModvFormatVersion.getText(), Messages.settings_modvVersion, Global.SEP));
			messageCDI.append(checkMandatory(textSodvFormatVersion.getText(), Messages.settings_sodvVersion, Global.SEP));

			if (messageCDI.length() > 0) {
				messageCDI.insert(0, Messages.error_settings_cdi);
				message.append(messageCDI);
				blnOK = false;
			}
		}

		if (checkMapping.getSelection()) {
			String errorMapping = checkMandatory(textFichierMapping.getText(), Messages.error_settings_mapping, Global.RN);
			if (errorMapping.length() > 0) {
				message.append(errorMapping);
				blnOK = false;
			}
		}

		if (getSelectedConversion() == null) {
			message.append(Messages.error_settings_outputFormat);
			blnOK = false;
		}
		if (!blnOK) {
			try {
				String[] buttons = new String[1];
				buttons[0] = Messages.success;
				MessageDialog dialog = new MessageDialog(this.getShell(), Messages.error_missing, null, message.toString(), MessageDialog.ERROR, buttons, 0);
				dialog.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return blnOK;
	}

	protected String checkMandatory(String text, String errorMsg, String sep) {
		StringBuffer message = new StringBuffer();
		if (text.trim().equals(Global.EMPTY_STRING)) {
			message.append(Global.RN);
			message.append(sep);
			message.append(errorMsg);
		}
		return message.toString();
	}

	protected String checkEdmoMandatory(int value, String errorMsg, String sep) {
		StringBuffer message = new StringBuffer();
		if (value == 0) {
			message.append(Global.RN);
			message.append(sep);
			message.append(errorMsg);
		}
		return message.toString();
	}

	protected void redLabels() {
		// Libelles rouges si parametres manquants

		redEdmoLabel(tmpEdmoChoices.getIdEdmoRespCdi(), labelIdEdmoRespCdi);
		redEdmoLabel(tmpEdmoChoices.getCodeEdmoDonnees(), labelCodeEdmoDonnees);
		redEdmoLabel(tmpEdmoChoices.getIdEdmoRespDonnees(), labelIdEdmoRespDonnees);
		redEdmoLabel(tmpEdmoChoices.getIdEdmoDistDonnees(), labelIdEdmoDistDonnees);

		redLabel(textUrlCdi.getText(), labelUrlCdi);
		redLabel(textOdvFormatVersion.getText(), lblOdvFormatVersion);
		redLabel(textMedatlasFormatVersion.getText(), lblMedatlasFormatVersion);
		redLabel(textNetCDFFormatVersion.getText(), lblNetCDFFormatVersion);
		redLabel(textBodvFormatVersion.getText(), lblBodvFormatVersion);
		redLabel(textModvFormatVersion.getText(), lblModvFormatVersion);
		redLabel(textSodvFormatVersion.getText(), lblSodvFormatVersion);
		redLabel(comboMethodeCdi.getText(), labelMethodeCdi);
		redLabel(comboTypePlateForme.getText(), lblTypePlateForme);
		redLabel(textFichierMapping.getText(), labelFichierMapping);
		redLabel(textAdrProxy.getText(), labelAdrProxy);
		redLabel(textPortProxy.getText(), labelPortProxy);
	}

	protected void redEdmoLabel(int value, Label redLabel) {
		if (value == 0) {
			redLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		} else {
			redLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	protected void redLabel(String text, Label redLabel) {
		if (text.trim().equals(Global.EMPTY_STRING)) {
			redLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		} else {
			redLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	protected boolean checkRestart() {
		boolean writeIni = false;
		StringBuffer message = new StringBuffer();

		if (comboLangue.getText().equals(Messages.settings_lang_EN)) {
			if (configLangage.equals(Intern.lang_fr))
				message.append(Global.RN + Global.SEP + Messages.settings_lang);
		} else {
			if (configLangage.equals(Intern.lang_en))
				message.append(Global.RN + Global.SEP + Messages.settings_lang);
		}

		if (useProxy.getSelection() != configProxy.isUse()) {
			// proxy configuration change
			message.append(Global.RN + Global.SEP + Messages.settings_proxy);
			writeIni = true;
		} else {
			if (useProxy.getSelection()) {
				if (!textAdrProxy.getText().trim().equals(configProxy.getAdress().trim())) {
					// proxy address change (can't be null)
					message.append(Global.RN + Global.SEP + Messages.settings_proxy_adr);
					writeIni = true;
				}
				if (!textPortProxy.getText().trim().equals(configProxy.getPort().trim())) {
					// proxy port change (can't be null)
					message.append(Global.RN + Global.SEP + Messages.settings_proxy_port);
					writeIni = true;
				}

				if (textLoginProxy.getText() == null) {
					if (!configProxy.getLogin().equals(Global.EMPTY_STRING)) {
						// proxy login change
						message.append(Global.RN + Global.SEP + Messages.settings_proxy_login);
						writeIni = true;
					}
				} else if (!textLoginProxy.getText().trim().equals(configProxy.getLogin().trim())) {
					// proxy login change
					message.append(Global.RN + Global.SEP + Messages.settings_proxy_login);
					writeIni = true;
				}

				if (textPassProxy.getText() == null) {
					if (!configProxy.getPassword().equals(Global.EMPTY_STRING)) {
						// proxy password change
						message.append(Global.RN + Global.SEP + Messages.settings_proxy_pass);
						writeIni = true;
					}
				} else if (!textPassProxy.getText().trim().equals(configProxy.getPassword().trim())) {
					// proxy password change
					message.append(Global.RN + Global.SEP + Messages.settings_proxy_pass);
					writeIni = true;
				}
			}
		}

		if (message.length() > 0) {
			message.insert(0, Messages.warning_settings);
			MessageDialog dialog = new MessageDialog(Utils.getMainShell(), Messages.warning_settings_title, null, message.toString(), MessageDialog.WARNING, new String[] { Messages.success }, 0);
			dialog.open();
		}

		return writeIni;
	}

	protected void saveConfig(boolean writeIni) {
		// configuration
		final Configuration configuration = Nemo.getInstance().getConfig();

		// sauvegarde de la config dans le fichier qui va bien
		// CONTACT
		configuration.setContact(Global.EMPTY_STRING);

		// DOSSIERS
		configuration.setModelsDirectory(textDossierModele.getText());
		configuration.setCruisesDirectory(textDossierCampagne.getText());

		// LANGUE
		if (comboLangue.getItem(comboLangue.getSelectionIndex()).equals(Messages.settings_lang_FR)) {
			configuration.setLang(Intern.lang_fr);
		} else {
			configuration.setLang(Intern.lang_en);
		}

		// CENTRE DE DONNEES
		StructuredSelection selection = (StructuredSelection) dataCenterComboViewer.getSelection();
		Entry<String,String> selectedDatacenter = (Entry<String, String>) selection.getFirstElement();
		configuration.setDataCenter(selectedDatacenter.getKey());
		

		// WS
		configuration.setNautilusWebServiceURL(textAreaNautilus.getText());
		configuration.setNemoWebServiceURL(textAreaNemo.getText());
		configuration.setEdmoURL(textWSEdmo.getText());
		configuration.setEdmerpURL(textWSEdmerp.getText());
		configuration.getProxy().update(writeIni);

		// CDI
		configuration.setWriteCdi(checkCDI.getSelection());
		configuration.setIdEdmoRespCdi(String.valueOf(tmpEdmoChoices.getIdEdmoRespCdi()));
		configuration.setIdEdmoRespCsr(String.valueOf(tmpEdmoChoices.getIdEdmoRespCsr()));
		configuration.setCodeEdmoDonnees(String.valueOf(tmpEdmoChoices.getCodeEdmoDonnees()));
		configuration.setIdEdmoRespDonnees(String.valueOf(tmpEdmoChoices.getIdEdmoRespDonnees()));
		configuration.setIdEdmoDistDonnees(String.valueOf(tmpEdmoChoices.getIdEdmoDistDonnees()));

		configuration.setCdiURL(textUrlCdi.getText());
		configuration.setCdiMethod(fr.ifremer.nemo.util.Utils.extraitCode(comboMethodeCdi.getText()));
		configuration.setAccesDonnees(fr.ifremer.nemo.util.Utils.extraitCode(comboAccesDonnees.getText()));
		configuration.setTypePlateForme(fr.ifremer.nemo.util.Utils.extraitCode(comboTypePlateForme.getText()));

		configuration.setMedatlasVersion(textMedatlasFormatVersion.getText());
		configuration.setOdvVersion(textOdvFormatVersion.getText());
		configuration.setNetCDFVersion(textNetCDFFormatVersion.getText());
		configuration.setBodvVersion(textBodvFormatVersion.getText());
		configuration.setModvVersion(textModvFormatVersion.getText());
		configuration.setSodvVersion(textSodvFormatVersion.getText());

		// MAPPING
		configuration.setWriteMapping(checkMapping.getSelection());
		configuration.setFichierMapping(textFichierMapping.getText());
		// configuration.setArboMapping(textArboMapping.getText());

		// DATE ODV
		configuration.setDateISO(radDateISO.getSelection());

		// Flags qualite
		configuration.setDefaultCode(tmpFlagsChoices.getDefaultFlagTable());
		ArrayList<FlagTable> flagTables = new ArrayList<FlagTable>();
		for (int i = 0; i < tmpFlagsChoices.getFlagTables().size(); i++) {
			FlagTable flagTable = new FlagTable(tmpFlagsChoices.getFlagTables().get(i));
			flagTables.add(flagTable);
		}
		configuration.setFlagTables(flagTables);
		// FAE 4431 Flag qualité par défault configurable
		configuration.setDefaultFlag(tmpFlagsChoices.getDefaultFlag());

		// output conversion format
		configuration.setDefaultConversionType(getSelectedConversion());


		// Enregistrement de la configuration
		configuration.save();
		Nemo.getInstance().setConfig(configuration);

		// On fait un reset sur les stations certains changement nécessite un
		// recalcul lors de l'écriture
		InputDomain id = Nemo.getInstance().getDomain();
		if (id != null) {
			CruiseDomain cd = id.getCruise();
			if (cd != null) {
				StationDomain sd = id.getStation();
				if (sd != null) {
					sd.resetStations();
				}
			}
		}
	}




	public EdmoChoices getTmpEdmoChoices() {
		return tmpEdmoChoices;
	}

	public FlagsChoices getTmpFlagsChoices() {
		return tmpFlagsChoices;
	}


	/**
	 * 
	 * @param flagTables
	 * @param defaultFlagTable
	 * @param defaultFlag
	 */
	public void updateFlags( ArrayList<FlagTable> flagTables , String defaultFlagTable, String defaultFlag   ) {
		tmpFlagsChoices.setFlagTables(flagTables);
		tmpFlagsChoices.setDefaultFlag(defaultFlag);
		tmpFlagsChoices.setDefaultFlagTable(defaultFlagTable);

	}


	protected void enableProxy(boolean enabled) {
		labelAdrProxy.setEnabled(enabled);
		textAdrProxy.setEnabled(enabled);
		labelPortProxy.setEnabled(enabled);
		textPortProxy.setEnabled(enabled);
		labelLoginProxy.setEnabled(enabled);
		textLoginProxy.setEnabled(enabled);
		labelPassProxy.setEnabled(enabled);
		textPassProxy.setEnabled(enabled);
	}

	protected void CDICheckMask(Boolean status) {
		labelCodeEdmoDonnees.setEnabled(status);
		buttonEdmoChoiceIdCodeEdmoDonnees.setEnabled(status);
		labelIdEdmoRespDonnees.setEnabled(status);
		buttonEdmoChoiceIdEdmoRespDonnees.setEnabled(status);
		labelIdEdmoDistDonnees.setEnabled(status);
		buttonEdmoChoiceIdEdmoDistDonnees.setEnabled(status);
		labelUrlCdi.setEnabled(status);
		textUrlCdi.setEnabled(status);
		labelMethodeCdi.setEnabled(status);
		comboMethodeCdi.setEnabled(status);
		lblTypePlateForme.setEnabled(status);
		comboTypePlateForme.setEnabled(status);
		lblAccesDonnees.setEnabled(status);
		comboAccesDonnees.setEnabled(status);
		lblOdvFormatVersion.setEnabled(status);
		textOdvFormatVersion.setEnabled(false);
		lblNetCDFFormatVersion.setEnabled(status);
		textNetCDFFormatVersion.setEnabled(false);
		lblMedatlasFormatVersion.setEnabled(status);
		textMedatlasFormatVersion.setEnabled(false);
		lblBodvFormatVersion.setEnabled(status);
		textBodvFormatVersion.setEnabled(false);
		lblModvFormatVersion.setEnabled(status);
		textModvFormatVersion.setEnabled(false);
		lblSodvFormatVersion.setEnabled(status);
		textSodvFormatVersion.setEnabled(false);
	}



	public void afficheGrpODV() {
		grpODV.setVisible(radODV.getSelection());
	}

	private void addRadConvertListener(final Button radio) {
		radio.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			@Override
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent g) {
				afficheGrpODV();
			}
		});
	}

	// TRADUCTION DES BOUTONS RADIO
	public E_TypeConversion getSelectedConversion() {
		E_TypeConversion selected = null;
		if (radMedatlas.getSelection()) {
			selected = E_TypeConversion.MEDATLAS;
		} else if (radODV.getSelection()) {
			if (radODVMonoStation.getSelection()) {
				selected = E_TypeConversion.ODV_MONOSTATION;
			} else if (radODVMonoFichier.getSelection()) {
				selected = E_TypeConversion.ODV_MULTISTATIONS;
			} else {
				// on laisse null, ce qui provoquera une alerte au check
			}
		} else if (radNetCDF.getSelection()) {
			if (radODVMonoStation.getSelection()) {
				selected = E_TypeConversion.NETCDF_MONOSTATION;
			} else if (radODVMonoFichier.getSelection()) {
				selected = E_TypeConversion.NETCDF_MULTISTATIONS;
			} else {
				// on laisse null, ce qui provoquera une alerte au check
			}
		} else {
			selected = E_TypeConversion.UNKNOWN;
		}

		return selected;
	}

	protected void mappingCheckMask(Boolean enabled) {
		labelFichierMapping.setEnabled(enabled);
		textFichierMapping.setEnabled(enabled);

		ApplicationActionBarAdvisor.mappingDatabaseEditAction.setEnabled(enabled);
		ApplicationActionBarAdvisor.mappingDatabaseExportAction.setEnabled(enabled);
		ApplicationActionBarAdvisor.mappingDatabaseImportAction.setEnabled(enabled);
	}

	public class EdmoChoiceListener implements Listener {
		public void handleEvent(Event event) {
			if (event.widget == buttonEdmoChoiceIdEdmoRespCdi) {
				edmoViewer = new EdmoViewer(OptionsDialog.this, EdmoChoices.CHOICE_IdEdmoRespCdi);
			} else if (event.widget == buttonEdmoChoiceIdEdmoRespCsr) {
				edmoViewer = new EdmoViewer(OptionsDialog.this, EdmoChoices.CHOICE_IdEdmoRespCsr);

			}  else if (event.widget == buttonEdmoChoiceIdCodeEdmoDonnees) {
				edmoViewer = new EdmoViewer(OptionsDialog.this, EdmoChoices.CHOICE_CodeEdmoDonnees);

			} else if (event.widget == buttonEdmoChoiceIdEdmoRespDonnees) {
				edmoViewer = new EdmoViewer(OptionsDialog.this, EdmoChoices.CHOICE_IdEdmoRespDonnees);

			} else if (event.widget == buttonEdmoChoiceIdEdmoDistDonnees) {
				edmoViewer = new EdmoViewer(OptionsDialog.this, EdmoChoices.CHOICE_IdEdmoDistDonnees);
			}
		}
	}


	private void initFlagsTmpChoices(){
		Configuration config = Nemo.getInstance().getConfig();
		tmpFlagsChoices = new FlagsChoices();
		tmpFlagsChoices .setFlagTables( FlagTable.getOrderedFlagTables(Nemo.getInstance().getConfig().getFlagTables()));

		tmpFlagsChoices.setDefaultFlag(config.getDefaultFlag());
		tmpFlagsChoices.setDefaultFlagTable(config.getDefaultCode());
	}

	private void loadEdmoConfig() {

		tmpEdmoChoices = new EdmoChoices();

		String value = Nemo.getInstance().getConfig().getIdEdmoRespCdi();
		if (!value.isEmpty()) {
			tmpEdmoChoices.setIdEdmoRespCdi(Integer.valueOf(value));
		}
		value = Nemo.getInstance().getConfig().getIdEdmoRespCsr();
		if (value!=null && !value.isEmpty()) {
			tmpEdmoChoices.setIdEdmoRespCsr(Integer.valueOf(value));
		}
		value = Nemo.getInstance().getConfig().getCodeEdmoDonnees();
		if (!value.isEmpty()) {
			tmpEdmoChoices.setCodeEdmoDonnees(Integer.valueOf(value));
		}
		value = Nemo.getInstance().getConfig().getIdEdmoRespDonnees();
		if (!value.isEmpty()) {
			tmpEdmoChoices.setIdEdmoRespDonnees(Integer.valueOf(value));
		}

		value = Nemo.getInstance().getConfig().getIdEdmoDistDonnees();
		if (!value.isEmpty()) {
			tmpEdmoChoices.setIdEdmoDistDonnees(Integer.valueOf(value));
		}
	}




	public class EditFlagsListener implements Listener {
		public void handleEvent(Event event) {
			if (event.widget == editFlags) {
				flagsViewer = new FlagsViewer(OptionsDialog.this);
			} 
		}
	}
	
	
	class ViewerComparatorByKey extends ViewerComparator {
		@Override
		public int compare(Viewer viewer, Object elm1, Object elm2) {
			Entry<String,String> entry1 = (Entry<String,String>) elm1;
			Entry<String,String> entry2 = (Entry<String,String>) elm2;
			return entry1.getKey().compareToIgnoreCase(entry2.getKey());
		}
	}



}
