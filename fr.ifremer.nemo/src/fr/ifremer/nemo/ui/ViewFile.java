package fr.ifremer.nemo.ui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeSet;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.JulianDate;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;
import fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType;

public class ViewFile extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewFile"; // //$NON-NLS-1$

	public static int etape, ongletCourant, affiche, tailleCompositeData;

	public static boolean dossierCampagne, fileLoaded;

	public static Composite compositeData;

	public static Composite top = null;

	public static TabFolder tabFolder = null;

	private Composite composite1 = null;

	private Composite composite2 = null;

	private Composite composite3 = null;

	public static Composite composite4 = null;

	private Composite composite11 = null;

	private Composite composite12 = null;

	private Composite compositeSeparateur = null;

	private Composite compositeChoixFichier = null;

	public static Button radProfilVertical, radSerieTempo, radTrajectoire;

	public static Button radMedatlas, radODV, checkCDI;

	public static Button radMonoFichier, radMonoStation = null;

	public static Button radioFichier = null;

	public static Button radioDossier = null;

	public static Button checkSeparateur = null;

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

	public static Group groupTempo, grpFichier = null;

	public static Text textTempoDateRef = null;

	private Label labelDateRef = null;

	private Label labelHeureRef = null;

	public static Text textTempoHeureRef = null;

	public static Combo comboTempoDate = null;

	public static Combo comboTempoHeure = null;

	public static String nomFichier, nomRepertoire;

	public static Table tableData;

	public static Label lblTypePlateForme, lblAccesDonnees;

	public static Combo comboTypePlateForme, comboAccesDonnees;

	public static Group grpCDI;

	private static Group grpSeparateur, grpODV;

	public static boolean writeResume;

	@Override
	public void createPartControl(Composite parent) {
		// 
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());

		// invalidation de chaque étape
		EditDesc.validee = 0;
		EditDesc.loaded = 0;
		EditSurvey.validee = 0;
		EditSurvey.loaded = 0;
		EditStation.validee = 0;
		EditStation.loaded = 0;
		EditData.validee = 0;
		EditData.loaded = 0;

		// chargement de la configuration
		Utils.initConfig();

		// ouverture de modèle à false
		DataModel.ouverture = false;

		// étape de départ
		etape = 0;
		DataModel.reset = false;

		createTabFolder();
	}

	@Override
	public void setFocus() {
		// 

	}

	/**
	 * This method initializes tabFolder
	 * 
	 */
	private void createTabFolder() {
		tabFolder = new TabFolder(top, SWT.NONE);
		createComposite1();
		createComposite2();
		createComposite3();
		createComposite4();
		createComposite14();

		// init des flags
		FiltrageTableType.listeOuverte = 0;
		ongletCourant = 0;
		affiche = 0;
		fileLoaded = false;

		// ////////////////////////////////////////////////////////
		// listener sur les onglets
		// ////////////////////////////////////////////////////////
		tabFolder
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						// System.out.println("affichage courant " + affiche);

						int tmp = tabFolder.getSelectionIndex();

						if (isCollection() && tabFolder.getSelectionIndex() > 0)
							tmp++;

						Utils.validationEtape(tmp);
						// System.out.println("onglet sélectionné " + tmp);

					}
				});
		tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText(Messages.getString("ViewFile.description")); //$NON-NLS-1$
		tabItem.setControl(composite1);
		tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText(Messages.getString("ViewFile.campagne")); //$NON-NLS-1$
		tabItem1.setControl(composite2);
		tabItem2 = new TabItem(tabFolder, SWT.NONE);
		tabItem2.setText(Messages.getString("ViewFile.station")); //$NON-NLS-1$
		tabItem2.setControl(composite3);
		tabItem3 = new TabItem(tabFolder, SWT.NONE);
		tabItem3.setText(Messages.getString("ViewFile.donnees")); //$NON-NLS-1$
		tabItem3.setControl(composite4);
		tabItem9 = new TabItem(tabFolder, SWT.NONE);
		tabItem9.setText(Messages.getString("ViewFile.conversion")); //$NON-NLS-1$
		tabItem9.setControl(compositeConversion);
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createComposite1() {
		composite1 = new Composite(tabFolder, SWT.NONE);
		composite1.setLayout(new FillLayout(SWT.VERTICAL));

		createComposite12();
		createComposite11();

	}

	/**
	 * This method initializes composite2
	 * 
	 */
	private void createComposite2() {
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL);
		composite2 = new Composite(tabFolder, SWT.NONE);
		composite2.setLayout(new FillLayout());
		styledTextCampagne = new StyledText(composite2, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextCampagne.setFont(font);
	}

	/**
	 * This method initializes composite3
	 * 
	 */
	private void createComposite3() {
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL); //$NON-NLS-1$
		composite3 = new Composite(tabFolder, SWT.NONE);
		composite3.setLayout(new FillLayout());
		styledTextStation = new StyledText(composite3, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextStation.setFont(font);
	}

	/**
	 * This method initializes composite4
	 * 
	 */
	private void createComposite4() {
		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL); //$NON-NLS-1$
		composite4 = new Composite(tabFolder, SWT.NONE);
		composite4.setLayout(new FillLayout(SWT.VERTICAL));
		styledTextData = new StyledText(composite4, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextData.setFont(font);
		createCompositeData(composite4);
	}

	/**
	 * This method initializes composite11
	 * 
	 */
	private void createComposite11() {

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
				.setText(Messages.getString("ViewFile.traiterCampagne") + "           "); //$NON-NLS-1$

		labelCollection = new Label(compositeChoixFichier, SWT.NONE);
		labelCollection.setText(Messages
				.getString("ViewFile.traiterCollection")); //$NON-NLS-1$

		radioFichier = new Button(compositeChoixFichier, SWT.RADIO);
		radioFichier.setSelection(true);
		radioFichier.setText(Messages.getString("ViewFile.fichierCampagne")); //$NON-NLS-1$

		radioCollectCas1 = new Button(compositeChoixFichier, SWT.RADIO);
		radioCollectCas1.setText(Messages.getString("ViewFile.collectionCas1")); //$NON-NLS-1$

		radioDossier = new Button(compositeChoixFichier, SWT.RADIO);
		radioDossier.setText(Messages.getString("ViewFile.dossierCampagne")); //$NON-NLS-1$

		radioCollectCasN = new Button(compositeChoixFichier, SWT.RADIO);
		radioCollectCasN.setText(Messages.getString("ViewFile.collectionCasN")); //$NON-NLS-1$

		radioFichier
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("fichier campagne"); //

					}
				});

		radioDossier
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("dossier campagne)"); //

					}
				});

		radioCollectCas1
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("dossier campagne)"); //

					}
				});

		radioCollectCasN
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("dossier campagne)"); //
					}
				});

		createGroupeSeparateur();

		createGroupeFichier();

		buttonParcourir = new Button(composite11, SWT.NONE);
		buttonParcourir.setText(Messages.getString("ViewFile.parcourir")); //$NON-NLS-1$

		createGroupTempo();

		buttonParcourir
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {

						try {
							Date dateOrigine = new Date();
							SimpleDateFormat datef = new SimpleDateFormat(
									"yyyyy.MM.dd");
							dateOrigine = datef.parse("2001.01.01");

							JulianDate jDate = new JulianDate(dateOrigine
									.getTime());
							System.out.println(jDate.getJour());
						} catch (Exception ex) {

						}

						// System.out.println("parcourir"); // //$NON-NLS-1$
						DataModel.ouverture = false;
						Preformat.setPreformat(false);
						// parcours des fichiers/dossiers

						// si fichier campagne
						if (radioFichier.getSelection()) {

							// Si on a supprimé l'onglet campagne pour une
							// collection on le réaffiche
							// vu qu'on selectionne maintenant fichier campagne
							if (tabItem1.isDisposed()) {
								tabItem1 = new TabItem(tabFolder, SWT.NONE, 1);
								tabItem1.setText(Messages
										.getString("ViewFile.campagne")); //$NON-NLS-1$
								tabItem1.setControl(composite2);
							}

							dossierCampagne = false;
							collectionCas1 = false;
							collectionCasN = false;

							Shell shell = PlatformUI.getWorkbench()
									.getDisplay().getActiveShell();

							FileDialog dialog = new FileDialog(shell, SWT.OPEN
									| SWT.APPLICATION_MODAL);
							dialog.setFilterPath(Utils.configuration
									.getCruisesDirectory()); //$NON-NLS-1$
							nomFichier = dialog.open();
							if (nomFichier != null) {

								fileLoaded = true;
								nomRepertoire = nomFichier.substring(0,
										nomFichier.lastIndexOf('\\') + 1);
								// init du modèle

								// affichage du fichier campagne dans onglet
								// Campagne
								try {
									Utils.insertTextFich(nomFichier,
											styledTextCampagne, true);
									// Utils.remplissageTexte(textAreaDescription);
									textAreaDescription
											.setText(Utils.texteBrut);

									// affichage etape0 dans formulaire
									// d'édition de paramètres
									Utils.razGlobal();
									EditDesc.affichageEdition();

								} catch (IOException e1) {
									// 
									e1.printStackTrace();
								}

								radMedatlas.setEnabled(true);
								radODV.setEnabled(true);

								grpCDI.setVisible(false);

							} else {
								fileLoaded = false;
								// System.out.println("pas de fichier

								ViewLog.styledTextLogErreurs
										.setText(Messages
												.getString("ViewFile.selectionnezFichier")); //$NON-NLS-1$

							}
						}

						// si dossier campagne
						if (radioDossier.getSelection()) {

							// Si on a supprimé l'onglet campagne pour une
							// colleciton on le réaffiche
							// vu qu'on selectionne maintenant dossier
							if (tabItem1.isDisposed()) {
								tabItem1 = new TabItem(tabFolder, SWT.NONE, 1);
								tabItem1.setText(Messages
										.getString("ViewFile.campagne")); //$NON-NLS-1$
								tabItem1.setControl(composite2);
							}
							dossierCampagne = true;
							collectionCas1 = false;
							collectionCasN = false;

							Shell shell = PlatformUI.getWorkbench()
									.getDisplay().getActiveShell();
							DirectoryDialog dialog = new DirectoryDialog(shell,
									SWT.OPEN | SWT.APPLICATION_MODAL);
							dialog.setFilterPath(Utils.configuration
									.getCruisesDirectory()); //$NON-NLS-1$
							nomRepertoire = dialog.open();
							if (nomRepertoire != null) {
								fileLoaded = true;

								if (Utils.findFiles(nomRepertoire, true)) {
									textAreaDescription
											.setText(Utils.texteBrut);

									// affichage etape0 dans formulaire
									// d'édition de
									// paramètres
									Utils.razGlobal();
									EditDesc.affichageEdition();

								}

								radMedatlas.setEnabled(true);
								radODV.setEnabled(true);

								grpCDI.setVisible(false);

							} else {
								fileLoaded = false;
								// System.out.println("pas de dossier
								// sélectionné"); //$NON-NLS-1$
								ViewLog.styledTextLogErreurs
										.setText(Messages
												.getString("ViewFile.selectionnezDossier")); //$NON-NLS-1$

							}

						}

						if (radioCollectCas1.getSelection()) {

							dossierCampagne = true;
							collectionCas1 = true;
							collectionCasN = false;

							Shell shell = PlatformUI.getWorkbench()
									.getDisplay().getActiveShell();
							DirectoryDialog dialog = new DirectoryDialog(shell,
									SWT.OPEN | SWT.APPLICATION_MODAL);
							dialog.setFilterPath(Utils.configuration
									.getCruisesDirectory());
							nomRepertoire = dialog.open();
							if (nomRepertoire != null) {
								fileLoaded = true;

								if (Utils.findFilesCollectCas1(nomRepertoire,
										false)) {
									textAreaDescription
											.setText(Utils.texteBrut);

								}

								// affichage etape0 dans formulaire
								// d'édition de
								// paramètres
								Utils.razGlobal();
								EditDesc.affichageEdition();

								tabItem1.dispose();

								// dans le cas d'une collection, on ne peut
								// generer que le format ODV
								radMedatlas.setEnabled(false);
								radMedatlas.setSelection(false);

								radODV.setEnabled(true);
								radODV.setSelection(true);
								afficheGrpODV();

								grpCDI.setVisible(true);
							}

							else {
								fileLoaded = false;
								// System.out.println("pas de dossier
								// sélectionné"); //$NON-NLS-1$
								ViewLog.styledTextLogErreurs
										.setText(Messages
												.getString("ViewFile.selectionnezDossier")); //$NON-NLS-1$

							}
						}

						if (radioCollectCasN.getSelection()) {

							dossierCampagne = false;
							collectionCas1 = false;
							collectionCasN = true;

							Shell shell = PlatformUI.getWorkbench()
									.getDisplay().getActiveShell();
							DirectoryDialog dialog = new DirectoryDialog(shell,
									SWT.OPEN | SWT.APPLICATION_MODAL);
							dialog.setFilterPath(Utils.configuration
									.getCruisesDirectory());
							nomRepertoire = dialog.open();
							if (nomRepertoire != null) {
								fileLoaded = true;

								if (Utils.findFiles(nomRepertoire, true)) {
									textAreaDescription
											.setText(Utils.texteBrut);

								}

								// affichage etape0 dans formulaire
								// d'édition de
								// paramètres
								Utils.razGlobal();
								EditDesc.affichageEdition();

								tabItem1.dispose();

								// dans le cas d'une collection, on ne peut
								// generer que le format ODV
								radMedatlas.setEnabled(false);
								radMedatlas.setSelection(false);

								radODV.setEnabled(true);
								radODV.setSelection(true);
								afficheGrpODV();

								grpCDI.setVisible(true);
							}

							else {
								fileLoaded = false;
								// System.out.println("pas de dossier
								// sélectionné"); //$NON-NLS-1$
								ViewLog.styledTextLogErreurs
										.setText(Messages
												.getString("ViewFile.selectionnezDossier")); //$NON-NLS-1$

							}
						}
					}

				});
	}

	/**
	 * This method initializes composite12
	 * 
	 */
	private void createComposite12() {

		Font font = new Font(Application.display, "Courier", 10, SWT.NORMAL); //$NON-NLS-1$

		composite12 = new Composite(composite1, SWT.NONE);
		composite12.setLayout(new FillLayout());
		textAreaDescription = new StyledText(composite12, SWT.MULTI
				| SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		textAreaDescription.setFont(font);

	}

	/**
	 * This method initializes composite14
	 * 
	 */
	private void createComposite14() {
		compositeConversion = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		compositeConversion.setLayout(layout);

		GridLayout layout2 = new GridLayout();
		layout.numColumns = 2;

		Group group = new Group(compositeConversion, SWT.NONE);
		group.setText(Messages.getString("ViewFile.choixFormatSortie")); //$NON-NLS-1$
		group.setLayout(layout);

		radMedatlas = new Button(group, SWT.RADIO);
		radMedatlas.setText("Medatlas"); //$NON-NLS-1$
		radMedatlas.setSelection(true);

		// Composite vide pour mise en page
		Composite compoVide1 = new Composite(group, SWT.NONE);

		radODV = new Button(group, SWT.RADIO);
		radODV.setText("ODV"); //$NON-NLS-1$
		radODV.setSelection(false);

		grpODV = new Group(group, SWT.NONE);
		grpODV.setLayout(layout2);
		afficheGrpODV();

		radMonoFichier = new Button(grpODV, SWT.RADIO);
		radMonoFichier.setText(Messages.getString("ViewFile.monoFichier"));
		radMonoFichier.setSelection(true);
		radMonoStation = new Button(grpODV, SWT.RADIO);
		radMonoStation.setText(Messages.getString("ViewFile.monoStation"));

		// if (isCollection()) {
		// radMedatlas.setEnabled(false);
		// }

		// Composite vide pour mise en page
		Composite compoVide2 = new Composite(compositeConversion, SWT.NONE);

		grpCDI = new Group(compositeConversion, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(grpCDI);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(grpCDI);

		checkCDI = new Button(grpCDI, SWT.CHECK);
		checkCDI.setText(Messages.getString("ViewFile.choixResumeCDI"));

		Composite cmpCDI = new Composite(grpCDI, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(cmpCDI);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(cmpCDI);

		lblTypePlateForme = new Label(cmpCDI, SWT.NONE);
		lblTypePlateForme
				.setText(Messages.getString("ViewFile.typePlateForme"));

		// FAE 59 CDL 09/04/08

		comboTypePlateForme = new Combo(cmpCDI, SWT.BORDER);

		CodeTableRecordType[] records = NemoLists.getVocabList("L061");

		TreeSet<String> champsMethodesCdi = new TreeSet<String>();

		for (CodeTableRecordType record : records) {
			String champCombo = "";
			// System.out.println(record.getEntryKey());
			String contenuCleDecoup[] = record.getEntryKey().split("/");
			champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
			champCombo += " - " + record.getEntryTerm();
			champsMethodesCdi.add(champCombo);
		}

		for (String champ : champsMethodesCdi)
			comboTypePlateForme.add(champ);

		GridDataFactory.fillDefaults().grab(false, false).applyTo(
				comboTypePlateForme);

		lblAccesDonnees = new Label(cmpCDI, SWT.NONE);
		lblAccesDonnees.setText(Messages.getString("ViewFile.accesDonnees"));

		comboAccesDonnees = new Combo(cmpCDI, SWT.BORDER);

		records = NemoLists.getVocabList("L081");

		champsMethodesCdi = new TreeSet<String>();

		for (CodeTableRecordType record : records) {
			String champCombo = "";
			// System.out.println(record.getEntryKey());
			String contenuCleDecoup[] = record.getEntryKey().split("/");
			champCombo += contenuCleDecoup[contenuCleDecoup.length - 1];
			champCombo += " - " + record.getEntryTerm();
			champsMethodesCdi.add(champCombo);
		}

		for (String champ : champsMethodesCdi)
			comboAccesDonnees.add(champ);

		GridDataFactory.fillDefaults().grab(false, false).applyTo(
				comboAccesDonnees);

		// Par défaut
		lblTypePlateForme.setEnabled(checkCDI.getSelection());
		comboTypePlateForme.setEnabled(checkCDI.getSelection());
		lblAccesDonnees.setEnabled(checkCDI.getSelection());
		comboAccesDonnees.setEnabled(checkCDI.getSelection());
		writeResume = checkCDI.getSelection();

		// bouton checkCDI
		checkCDI
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						lblTypePlateForme.setEnabled(checkCDI.getSelection());
						comboTypePlateForme.setEnabled(checkCDI.getSelection());
						lblAccesDonnees.setEnabled(checkCDI.getSelection());
						comboAccesDonnees.setEnabled(checkCDI.getSelection());
						writeResume = checkCDI.getSelection();
					}
				});

		// bouton radMedatlas
		radMedatlas
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						afficheGrpODV();
					}

				});

		// bouton radODV
		radODV
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						afficheGrpODV();
					}

				});

		comboAccesDonnees.pack();
		comboTypePlateForme.pack();
		grpCDI.pack();
		grpCDI.setVisible(false);
	}

	/**
	 * This method initializes groupTempo
	 * 
	 */
	private void createGroupTempo() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		groupTempo = new Group(composite11, SWT.NONE);
		groupTempo.setText(Messages.getString("ViewFile.dateHeureRelatives")); //$NON-NLS-1$
		groupTempo.setLayout(gridLayout1);
		labelDateRef = new Label(groupTempo, SWT.NONE);
		labelDateRef
				.setText(Messages.getString("ViewFile.dateRef") + ": dd/MM/yyyy"); //$NON-NLS-1$

		textTempoDateRef = new Text(groupTempo, SWT.BORDER);

		labelHeureRef = new Label(groupTempo, SWT.NONE);
		labelHeureRef
				.setText(Messages.getString("ViewFile.heureRef") + ": hh24:mm"); //$NON-NLS-1$

		textTempoHeureRef = new Text(groupTempo, SWT.BORDER);

		// on le désactive de base
		groupTempo.setVisible(false);

		// TODO Herve Taniou : ce groupe n'est jamais rendu visible et donc
		// inutile ?
	}

	/**
	 * This method initializes groupFichier
	 * 
	 */
	private void createGroupeFichier() {
		grpFichier = new Group(composite11, SWT.NONE);
		grpFichier.setText(Messages.getString("ViewFile.typeFichier"));
		GridLayoutFactory.fillDefaults().applyTo(grpFichier);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(grpFichier);

		radProfilVertical = new Button(grpFichier, SWT.RADIO);
		radProfilVertical.setText(Messages.getString("ViewFile.profilVert"));

		radSerieTempo = new Button(grpFichier, SWT.RADIO);
		radSerieTempo.setText(Messages.getString("ViewFile.serieTempo"));

		radTrajectoire = new Button(grpFichier, SWT.RADIO);
		radTrajectoire.setText(Messages.getString("ViewFile.trajectoire"));

		// Sélection du type par défaut
		radProfilVertical.setSelection(true);
	}

	private void createCompositeData(Composite compo) {
		compositeData = new Composite(compo, SWT.NONE);
		compositeData.setLayout(new FillLayout());
	}

	private void createGroupeSeparateur() {
		grpSeparateur = new Group(composite11, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(grpSeparateur);
		GridDataFactory.fillDefaults().grab(false, false)
				.applyTo(grpSeparateur);

		checkSeparateur = new Button(grpSeparateur, SWT.CHECK);
		checkSeparateur.setText(Messages.getString("Preformat.3"));

		compositeSeparateur = new Composite(grpSeparateur, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(3).applyTo(
				compositeSeparateur);
		GridDataFactory.fillDefaults().grab(false, false).applyTo(
				compositeSeparateur);

		radioTabulation = new Button(compositeSeparateur, SWT.RADIO);
		radioTabulation.setText(Messages.getString("Preformat.4"));
		radioTabulation.setSelection(true);

		radioPointVirgule = new Button(compositeSeparateur, SWT.RADIO);
		radioPointVirgule.setText(Messages.getString("Preformat.5"));

		radioVirgule = new Button(compositeSeparateur, SWT.RADIO);
		radioVirgule.setText(Messages.getString("Preformat.6"));

		radioEspace = new Button(compositeSeparateur, SWT.RADIO);
		radioEspace.setText(Messages.getString("Preformat.7"));

		// compositeSeparateurAutre = new Composite(compositeSeparateur,
		// SWT.NONE);
		// GridLayoutFactory.fillDefaults().numColumns(2).applyTo(compositeSeparateurAutre);
		// GridDataFactory.fillDefaults().grab(false,false).applyTo(compositeSeparateurAutre);

		radioSeparateurAutre = new Button(compositeSeparateur, SWT.RADIO);
		radioSeparateurAutre.setText(Messages.getString("Preformat.8"));
		textSeparateur = new Text(compositeSeparateur, SWT.BORDER);

		// Par défaut
		compositeSeparateur.setVisible(false);

		checkSeparateur
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent g) {
						compositeSeparateur.setVisible(checkSeparateur
								.getSelection());
					}
				});
	}

	public static boolean isCollection() {
		return (collectionCas1 || collectionCasN);
	}

	public static String typeFichierEntree() {
		if (radSerieTempo.getSelection()) {
			return Global.TIMESERIES;
		} else if (radTrajectoire.getSelection()) {
			return Global.TRAJECTORY;
		} else {
			return Global.PROFILE;
		}
	}

	public static void afficheGrpODV() {
		grpODV.setVisible(radODV.getSelection());
	}
}
