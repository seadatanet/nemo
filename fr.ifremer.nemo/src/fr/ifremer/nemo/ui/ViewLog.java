package fr.ifremer.nemo.ui;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.RowLayout;

public class ViewLog extends ViewPart {

	public static final String ID = "fr.ifremer.nemo.ui.ViewLog"; //$NON-NLS-1$

	private Composite top = null;

	public static TabFolder tabFolder = null;

	public static TabFolder tabFolder1 = null;

	public static TabFolder tabFolder2 = null;

	public static TabFolder tabFolder3 = null;

	

	private Composite compositeLogGauche = null;

	private Composite compositeLogDroite = null;

	private Button buttonLogBack = null;

	private Button buttonLogNext = null;

	private Button buttonLogConversion = null;

	public static TabFolder tabFolderLog = null;

	public static StyledText styledTextLogErreurs, styledTextLogInfos, styledTextLogHelp = null;

	

	@Override
	public void createPartControl(Composite parent) {
		// TODO Raccord de méthode auto-généré
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		createCompositeLogGauche();
		//createCompositeLogDroite();
	
	}

	@Override
	public void setFocus() {
		// TODO Raccord de méthode auto-généré

	}

	/**
	 * This method initializes compositeLogGauche	
	 *
	 */
	private void createCompositeLogGauche() {
		compositeLogGauche = new Composite(top, SWT.NONE);
		compositeLogGauche.setLayout(new FillLayout());
		createTabFolderLog();
	}

	/*
	
	
	private void createCompositeLogDroite() {
		compositeLogDroite = new Composite(top, SWT.NONE);
		compositeLogDroite.setLayout(new RowLayout());
		buttonLogBack = new Button(compositeLogDroite, SWT.NONE);
		buttonLogBack.setText("Précédent");
		buttonLogBack.setEnabled(false);
		buttonLogNext = new Button(compositeLogDroite, SWT.NONE);
		buttonLogNext.setText("Suivant");
		buttonLogConversion = new Button(compositeLogDroite, SWT.NONE);
		buttonLogConversion.setText("Conversion");
		buttonLogConversion.setEnabled(false);
	}

	*/
	
	/**
	 * This method initializes tabFolderLog	
	 *
	 */
	private void createTabFolderLog() {
		tabFolderLog = new TabFolder(compositeLogGauche, SWT.NONE);
		tabFolderLog
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("widgetSelected()"); // TODO Auto-generated Event stub widgetSelected() //$NON-NLS-1$
					}
				});
		TabItem tabItem = new TabItem(tabFolderLog, SWT.NONE);
		tabItem.setText(Messages.getString("ViewLog.2")); //$NON-NLS-1$
		TabItem tabItem1 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem1.setText(Messages.getString("ViewLog.3")); //$NON-NLS-1$
		TabItem tabItem2 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem2.setText(Messages.getString("ViewLog.4")); //$NON-NLS-1$
		styledTextLogErreurs = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL |SWT.READ_ONLY);
		styledTextLogInfos = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL |SWT.READ_ONLY);
		styledTextLogHelp = new StyledText(tabFolderLog, SWT.MULTI | SWT.V_SCROLL |SWT.WRAP|SWT.READ_ONLY);
		tabItem2.setControl(styledTextLogHelp);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

public static void aideDesc(){
	String langue = Utils.configuration.getLang();
	if (langue.matches("fr_FR")){
ViewLog.styledTextLogHelp.setText("##MENUS##" +
		"1/Modèle/Ouvrir modèle : charge un modèle en mémoire à condition d'avoir déjà ouvert une campagne\n" +
		"2/Modèle/Enregistrer modèle : créé sur le disque un fichier modèle XML correspondant aux valeurs entrées dans les différentes étapes. N'est fonctionnel qu'après avoir validé les 4 premières étapes.\n" +
		"3/Quitter : ferme l'application après avoir demandé confirmation.\n" +
		"\n\n" +
		"##DESCRIPTION FICHIER##\n" +
		"1/ Sélection 'fichier' ou 'dossier' en cochant ou non 'Série temporelle'.\n" +
				"2/ Bouton 'Parcourir' et choisir un fichier/dossier.\n" +
						"3/ Si besoin, charger un modèle par le menu Modèle/Ouvrir un modèle et en choisissant le fichier XML du modèle correspondant à la campagne en cours.\n" +
						"4/ La zone Description de fichier s'affiche. Cliquer sur chaque section et modifier les valeurs.\n" +
						"5/ Pour indiquer un nombre de lignes, surligner avec la souris les lignes concernées dans la partie gauche de la fenêtre puis cliquer sur le bouton 'injecter' de la section considérée.\n" +
								"6/ L'indicateur de fin de données peut être entré par l'utilisateur.\n" +
								"    END sera traduit par NEMO en ^END$\n" +
								"    EOF sera traduit par NEMO en ^$\n" +
								"Ces valeurs peuvent donc se retrouver après chargement d'un modèle.\n" +
								"7/ L'onglet 'info' reprend les principales informations sur les fichiers campagne et modèle chargés.\n" +
										"8/ L'onglet 'aide' affiche à chaque étape l'aide correspondante.\n" +
												"9/ Une fois les valeurs remplies, cliquer sur 'valider' pour passer à l'étape suivante. Si des erreurs ont été commises, des messages apparaitront en rouge dans l'onglet 'erreurs'.\n" +
														"10/ Le bouton 'reset' permet d'effacer toutes les valeurs de l'étape en cours.\n");	 //$NON-NLS-1$
	}else{
		//aide anglaise
		ViewLog.styledTextLogHelp.setText("");
	}
	}	

public static void aideSurvey(){
	String langue = Utils.configuration.getLang();
	if (langue.matches("fr_FR")){
	ViewLog.styledTextLogHelp.setText("##DESCRIPTION CAMPAGNE##\n" +
			"3 possibilités de remplissage de cette étape : entrer toutes les valeurs manuellement, interroger les Webservices ou charger un fichier XML.\n" +
			"1/ Manuellement : remplir les sections 'référence', 'localisation', 'Sources des données' et 'Commentaires éventuels'. Pour la section 'Centre d'archivage', choisir la confidentialité dans la combo list. Pour la section 'types de données', un clic droit sur la table vide fait apparaitre un menu qui permet d'ajouter un nouveau type ('Sélection liste'), de déplacer ou de supprimer le type sélectionné. Ne pas tenter de cliquer gauche pour choisir une ligne puis de cliquer droit pour faire apparaitre le menu mais au contraire faire un clic droit directement. Sinon on passe en mode édition. La fenêtre de sélection des types de données permet d'effectuer une recherche à la fois sur le code et sur le nom du type.\n" +
			"2/ Webservices, bouton 'initialisation serveur' : la fenêtre de sélection des campagnes permet d'effectuer une recherche à la fois sur le code et sur le nom de la campagne.\n" +
			"3/ Fichier XML, bouton 'initialisation XML' : choisir un fichier XML de campagne.\n\n");
	}else{
		//aide anglaise
		ViewLog.styledTextLogHelp.setText("");
	}
	}

public static void aideStation(){
	String langue = Utils.configuration.getLang();
	if (langue.matches("fr_FR")){
	ViewLog.styledTextLogHelp.setText("##DESCRIPTION DE STATION##\n" +
			"Même principe que les étapes précédentes à la différence que certaines valeurs à injecter demandent des valeurs sur 'ligne', 'début' et 'fin'. Dans le cas d'un fichier campagne, toujours effectuer les injections sur la première station ! Dans tous les cas, la sélection devra se limiter à une ligne unique (date, heure, latitude, profondeur, etc.) sauf pour le 'commentaire' où la possibilité de surligner plusieurs lignes est offerte." +
			"Pour les formats d'heure, de date, de latitude et de longitude, on peut entrer ses propres formats.\n" +
			"\n\n##FORMATS HEURE / DATE##\n" +
			"*yy ou yyyy : année exprimée sur 2 ou 4 digits\n" +
			"*MM : mois exprimé sur 2 digits\n" +
			"*dd : jour dans le mois exprimé sur 2 digits\n" +
			"*hh : heure exprimée sur 2 digits\n" +
			"*mm : minutes dans l’heure exprimées sur 2 digits\n" +
			"*ss : secondes exprimées sur 2 digits\n" +
			"\nLes contraintes suivantes sont associées à ces formats :\n" +
			"*Les valeurs sont complétées à gauche par un 0 si la valeur à afficher est inférieure à 10 (ex : année 2000 -> 00)\n" +
			"*L’heure peut être absente\n" +
			"*L’ordre entre la date et l’heure est libre (date heure, heure date)\n" +
			"*Des caractères supplémentaires quelconques peuvent être insérés entre les différentes clés. Ceux-ci seront attendus tel quel en entrée et rendus tel quel en sortie\n" +
			"\nExemples:\n" +
			"yyy/MM/dd hh:mm:ss ==> 1986/07/10 09:23:10\n" +
			"hh:mm ==> 09:23\n" +
			"yy-MM-dd ==> 86-07-10\n" +
			"\n\n##FORMATS LATITUDE / LONGITUDE##\n" +
			"N : exprime un hémisphère. Sa valeur devra être égale à N ou S dans le cas des latitudes, et W ou E dans le cas des longitudes\n" +
			"DDD ou DD : Nombre de degrés. Le nombre de D doit être 3 dans le cas des longitudes et 2 dans le cas des latitudes\n" +
			"DDD.ddd ou DD.ddd : Degrés en valeur décimales. Le nombre de D doit être 3 dans le cas des longitudes et 2 dans le cas des latitudes. Le nombre de caractères « d » exprime la précision attendue\n" +
			"MM : Nombre entier de minutes\n" +
			"MM.mm : Minutes en valeur décimale. Le nombre de caractères « m » exprime la précision attendue\n" +
			"SS : valeur entière des secondes\n" +
			"+DDD.ddd\n" +
			"\nLes contraintes suivantes sont associées à ces formats :\n" +
			"Les valeurs sont complétées à gauche par un espace si la valeur entière à afficher n’occupe pas le nombre de caractères attendus\n" +
			"L’indicateur d’hémisphère peut se trouver avant ou après la valeur numérique\n" +
			"Dans le cas où les degrés sont exprimés en valeurs décimales, aucune minute n’est attendue\n" +
			"Dans le cas où les minutes sont exprimées en valeurs décimales, aucune seconde n’est attendue\n" +
			"Des caractères supplémentaires quelconques peuvent être insérés entre les différentes clés. Ceux-ci seront attendus tel quel en entrée et rendus tel quel en sortie\n" +
			"\nExemples :\n" +
			"NDDD MM.mm ==> W123 10.25\n" +
			"DDD MM SS N ==> 123 10 30 W\n");}
	else{
		//aide anglaise
		ViewLog.styledTextLogHelp.setText("");
	}
}

public static void aideData(){
	String langue = Utils.configuration.getLang();
	if (langue.matches("fr_FR")){
	ViewLog.styledTextLogHelp.setText("##DESCRIPTION DONNEES##\n" +
			"Même principe que pour les types de données : on ajoute ici les paramètres mesurés.\n" +
			"Dans le cas d'une série temporelle :\n" +
			"*les paramètres DATE, YEAR, MONTH, DAYX, TIME sont rajoutés automatiquement à la génération donc attention à ne pas les ajouter manuellement.\n" +
			"*on peut choisir si le calcul des dates/heures est basé sur les dates/heures entrées à l'étape Station et la fréquence d'échantillonnage ou si les informations sont présentes au sein de la ligne des données (auquel cas préciser le format).\n" +
			"\n");}
	else{
		//aide anglaise
		ViewLog.styledTextLogHelp.setText("");
	}
}
public static void aideConversion(){
	String langue = Utils.configuration.getLang();
	if (langue.matches("fr_FR")){
	ViewLog.styledTextLogHelp.setText("##CONVERSION##\n" +
			"Choisir le format de sortie. On peut générer les deux formats à la suite sans devoir revalider toutes les étapes.");

	}else{
		//aide anglaise
		ViewLog.styledTextLogHelp.setText("");
	}
	}

}
