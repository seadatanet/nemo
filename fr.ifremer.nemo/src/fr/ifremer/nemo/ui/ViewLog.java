package fr.ifremer.nemo.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

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

	public static StyledText styledTextLogErreurs, styledTextLogInfos,
			styledTextLogHelp = null;

	@Override
	public void createPartControl(Composite parent) {
		// 
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		createCompositeLogGauche();
		// createCompositeLogDroite();

	}

	@Override
	public void setFocus() {
		// 

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

	/**
	 * This method initializes tabFolderLog
	 * 
	 */
	private void createTabFolderLog() {
		tabFolderLog = new TabFolder(compositeLogGauche, SWT.NONE);
		tabFolderLog
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						System.out.println("widgetSelected()");
					}
				});
		TabItem tabItem = new TabItem(tabFolderLog, SWT.NONE);
		tabItem.setText(Messages.getString("ViewLog.2")); //$NON-NLS-1$
		TabItem tabItem1 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem1.setText(Messages.getString("ViewLog.3")); //$NON-NLS-1$
		TabItem tabItem2 = new TabItem(tabFolderLog, SWT.NONE);
		tabItem2.setText(Messages.getString("ViewLog.4")); //$NON-NLS-1$
		styledTextLogErreurs = new StyledText(tabFolderLog, SWT.MULTI
				| SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextLogInfos = new StyledText(tabFolderLog, SWT.MULTI
				| SWT.V_SCROLL | SWT.READ_ONLY);
		styledTextLogHelp = new StyledText(tabFolderLog, SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);
		tabItem2.setControl(styledTextLogHelp);
		tabItem1.setControl(styledTextLogInfos);
		tabItem.setControl(styledTextLogErreurs);
	}

	public static void aideDesc() {
		String langue = Utils.configuration.getLang();
		if (langue.matches("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==MENUS=="
							+ "1/Modèle/Ouvrir modèle : charge un modèle en mémoire à condition d'avoir déjà ouvert une campagne\n"
							+ "2/Modèle/Enregistrer modèle : créé sur le disque un fichier modèle XML correspondant aux valeurs entrées dans les différentes étapes. N'est fonctionnel qu'après avoir validé les 4 premières étapes.\n"
							+ "3/Quitter : ferme l'application après avoir demandé confirmation.\n"
							+ "\n\n"
							+ "==DESCRIPTION FICHIER==\n"
							+ "1/ Sélection 'fichier' ou 'dossier' en cochant ou non 'Série temporelle'.\n"
							+ "2/ Bouton 'Parcourir' et choisir un fichier/dossier.\n"
							+ "3/ Si besoin, charger un modèle par le menu Modèle/Ouvrir un modèle et en choisissant le fichier XML du modèle correspondant à la campagne en cours.\n"
							+ "4/ La zone Description de fichier s'affiche. Cliquer sur chaque section et modifier les valeurs.\n"
							+ "5/ Pour indiquer un nombre de lignes, surligner avec la souris les lignes concernées dans la partie gauche de la fenêtre puis cliquer sur le bouton 'injecter' de la section considérée.\n"
							+ "6/ L'indicateur de fin de données peut être entré par l'utilisateur.\n"
							+ "    END sera traduit par NEMO en ^END$\n"
							+ "    EOF sera traduit par NEMO en ^$\n"
							+ "Ces valeurs peuvent donc se retrouver après chargement d'un modèle.\n"
							+ "7/ L'onglet 'info' reprend les principales informations sur les fichiers campagne et modèle chargés.\n"
							+ "8/ L'onglet 'aide' affiche à chaque étape l'aide correspondante.\n"
							+ "9/ Une fois les valeurs remplies, cliquer sur 'valider' pour passer à l'étape suivante. Si des erreurs ont été commises, des messages apparaitront en rouge dans l'onglet 'erreurs'.\n"
							+ "10/ Le bouton 'reset' permet d'effacer toutes les valeurs de l'étape en cours.\n"); //$NON-NLS-1$
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==MENU==\n"
							+ "1/Model/Open model : loads a model only if a cruise has previously been opened.\n"
							+ "2/Model/Save model : creates on the disk a XML-type file corresponding to the values entered in the different steps. It is fonctional only once the first 4 steps have been validated.\n"
							+ "3/Quit : shuts down the application after having been asked to confirm.\n"
							+ "\n==FILE DESCRIPTION==\n"
							+ "1/ 'file' or 'directory' Selection by ticking or not 'temporal series'.\n"
							+ "2/ 'Browse' Button  and choose a file or directory.\n"
							+ "3/ If needed, load a model via the model/ open a model menu and by choosing the XML file of the model corresponding to the current cruise.\n"
							+ "4/ The description of file zone appears. Click on each section and modify the values.\n"
							+ "5/ To indicate a number of lines, highlight with the mouse the lines concerned on the left part of the window then click on the butten 'inject' of the considered section.\n"
							+ "6/ The end of data pattern can be entered by the user.\n"
							+ "    END will be translated by NEMO into ^END$\n"
							+ "    EOF will be translated by NEMO into ^$\n"
							+ "These values can be found again after the loading of a model.\n"
							+ "7/ The 'info' tab resumes the main information of the cruise's files and the loaded model.\n"
							+ "8/ The 'help' tab displays at each step the corresponding help.\n"
							+ "9/ Once the values fulfilled, click on 'validate' to turn to the next step. If some mistakes have occured, red messages will appear in the 'error' tab.\n"
							+ "10/ The 'reset' button will erase all the values of the current step.\n");
		}
	}

	public static void aideSurvey() {
		String langue = Utils.configuration.getLang();
		if (langue.matches("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==DESCRIPTION CAMPAGNE==\n"
							+ "3 possibilités de remplissage de cette étape : entrer toutes les valeurs manuellement, interroger les Webservices ou charger un fichier XML.\n"
							+ "1/ Manuellement : remplir les sections 'référence', 'localisation', 'Sources des données' et 'Commentaires éventuels'. Pour la section 'Centre d'archivage', choisir la confidentialité dans la combo list. Pour la section 'types de données', un clic droit sur la table vide fait apparaitre un menu qui permet d'ajouter un nouveau type ('Sélection liste'), de déplacer ou de supprimer le type sélectionné. Ne pas tenter de cliquer gauche pour choisir une ligne puis de cliquer droit pour faire apparaitre le menu mais au contraire faire un clic droit directement. Sinon on passe en mode édition. La fenêtre de sélection des types de données permet d'effectuer une recherche à la fois sur le code et sur le nom du type.\n"
							+ "2/ Webservices, bouton 'initialisation serveur' : la fenêtre de sélection des campagnes permet d'effectuer une recherche à la fois sur le code et sur le nom de la campagne.\n"
							+ "3/ Fichier XML, bouton 'initialisation XML' : choisir un fichier XML de campagne.\n\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==CRUISE DESCRIPTION==\n"
							+ "3 possibilities for fulfilling this step: entering all the values manually, asking the webservices or loading a XML file.\n"
							+ "1/ Manually : filling the 'reference', 'location', 'data source '  and 'possible comments' sections. For the 'archiving centre' section , choose confidentiality in the combo list. For the 'data type' section, a right click on the blank table makes a menu appear. It will enable to add a new type ('list Selection') to move or erase the selected type.  Don't try to left-click to choose a line then to right-click to make the menu appear. On the contrary do a right-clik directly, otherwise you will start the edition mode. The type of data selection's window enables to make searches on both the code and the name of the type.\n"
							+ "2/ Webservices, 'server initialisation' button : the survey's selection window enables to make searches using the code or the name of the cruise.\n"
							+ "3/ XML file, 'XML initialisation' button : choose a cruise XML file.\n");
		}
	}

	public static void aideStation() {
		String langue = Utils.configuration.getLang();
		if (langue.matches("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==DESCRIPTION DE STATION==\n"
							+ "Même principe que les étapes précédentes à la différence que certaines valeurs à injecter demandent des valeurs sur 'ligne', 'début' et 'fin'. Dans le cas d'un fichier campagne, toujours effectuer les injections sur la première station ! Dans tous les cas, la sélection devra se limiter à une ligne unique (date, heure, latitude, profondeur, etc.) sauf pour le 'commentaire' où la possibilité de surligner plusieurs lignes est offerte."
							+ "Pour les formats d'heure, de date, de latitude et de longitude, on peut entrer ses propres formats.\n"
							+ "\nSi le numéro de station est situé dans le nom du fichier, introduire 0 comme numéro de ligne\n"
							+ "\n\n==FORMATS HEURE / DATE==\n"
							+ "*yy ou yyyy : année exprimée sur 2 ou 4 digits\n"
							+ "*MM : mois exprimé sur 2 digits\n"
							+ "*dd : jour dans le mois exprimé sur 2 digits\n"
							+ "*hh : heure exprimée sur 2 digits\n"
							+ "*mm : minutes dans l’heure exprimées sur 2 digits\n"
							+ "*ss : secondes exprimées sur 2 digits\n"
							+ "\nLes contraintes suivantes sont associées à ces formats :\n"
							+ "*Les valeurs sont complétées à gauche par un 0 si la valeur à afficher est inférieure à 10 (ex : année 2000 -> 00)\n"
							+ "*L’heure peut être absente\n"
							+ "*L’ordre entre la date et l’heure est libre (date heure, heure date)\n"
							+ "*Des caractères supplémentaires quelconques peuvent être insérés entre les différentes clés. Ceux-ci seront attendus tel quel en entrée et rendus tel quel en sortie\n"
							+ "\nExemples:\n"
							+ "yyy/MM/dd hh:mm:ss ==> 1986/07/10 09:23:10\n"
							+ "hh:mm ==> 09:23\n"
							+ "yy-MM-dd ==> 86-07-10\n"
							+ "\n\n==FORMATS LATITUDE / LONGITUDE==\n"
							+ "N : exprime un hémisphère. Sa valeur devra être égale à N ou S dans le cas des latitudes, et W ou E dans le cas des longitudes\n"
							+ "DDD ou DD : Nombre de degrés. Le nombre de D doit être 3 dans le cas des longitudes et 2 dans le cas des latitudes\n"
							+ "DDD.ddd ou DD.ddd : Degrés en valeur décimales. Le nombre de D doit être 3 dans le cas des longitudes et 2 dans le cas des latitudes. Le nombre de caractères « d » exprime la précision attendue\n"
							+ "MM : Nombre entier de minutes\n"
							+ "MM.mm : Minutes en valeur décimale. Le nombre de caractères « m » exprime la précision attendue\n"
							+ "SS : valeur entière des secondes\n"
							+ "+DDD.ddd\n"
							+ "\nLes contraintes suivantes sont associées à ces formats :\n"
							+ "Les valeurs sont complétées à gauche par un espace si la valeur entière à afficher n’occupe pas le nombre de caractères attendus\n"
							+ "L’indicateur d’hémisphère peut se trouver avant ou après la valeur numérique\n"
							+ "Dans le cas où les degrés sont exprimés en valeurs décimales, aucune minute n’est attendue\n"
							+ "Dans le cas où les minutes sont exprimées en valeurs décimales, aucune seconde n’est attendue\n"
							+ "Des caractères supplémentaires quelconques peuvent être insérés entre les différentes clés. Ceux-ci seront attendus tel quel en entrée et rendus tel quel en sortie\n"
							+ "\nExemples :\n" + "NDDD MM.mm ==> W123 10.25\n"
							+ "DDD MM SS N ==> 123 10 30 W\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==STATION DESCRIPTION==\n"
							+ "It's the same principle than for the previous steps, only here some values to inject ask values on 'line', 'start' and 'end'. In the case of a cruise file, always carry out the injections on the first station ! In any case, the selection will have to be limited to a single line (date, time, latitude, depth, etc.) with the exception of the 'comment' where the possibility of highlighting several lines is proposed.\n"
							+ "For the time, date, latitude and longitude formats, you can enter your own formats.\n"
							+ "\nIf station's number is located within the filename, use 0 as line number\n"
							+ "\n==TIME / DATE FORMATS==\n"
							+ "*yy oR yyyy : the year expressed in 2 or 4 digits\n"
							+ "*MM : month expressed in 2 digits\n"
							+ "*dd : day in the month expressed in 2 digits\n"
							+ "*hh : hour expressed in 2 digits\n"
							+ "*mm : minutes in the time expressed in 2 digits\n"
							+ "*ss : seconds expressed in 2 digits\n"
							+ "\nThe following restrictions are associated to these formats:\n"
							+ "*The values are completed on the left by a 0 if the value to indicate is under 10 (i.e. : year 2000 -> 00)\n"
							+ "*The time can be omitted\n"
							+ "*The order between the date and the time is free (date time, time date)\n"
							+ "*Any other extra characters can be inserted between the various keys. The same values will be expected at the input and at the output.\n"
							+ "Examples:\n"
							+ "yyy/MM/dd hh:mm:ss ==> 1986/07/10 09:23:10\n"
							+ "hh:mm ==> 09:23\n"
							+ "yy-MM-dd ==> 86-07-10\n"
							+ "\n==LATITUDE / LONGITUDE FORMATS==\n"
							+ "N : expresses an hemisphere. Its value will have to equal N or S in the case of latitudes and W or E in the case of longitudes\n"
							+ "DDD ou DD : Number of degrees. The number of D must be 3 in the case of longitudes and 2 in the case of latitudes\n"
							+ "DDD.ddd ou DD.ddd : Degrees in decimal values. The number of D must be 3 in the case of longitudes and 2 in the case of latitudes. The number of 'd' characters expresses the accuracy expected\n"
							+ "MM : Minutes\n"
							+ "MM.mm : Minutes in decimal values. The number of 'm' characters expresses the accuracy expected.\n"
							+ "SS : Seconds\n"
							+ "+DDD.ddd\n"
							+ "\nThe following restrictions are associated to these formats :\n"
							+ "The values are completed on the left by an empty space if the value to display doesn't amount to the number of characters expected.\n"
							+ "The hemisphere pattern can be found before or after the numeric value\n"
							+ "In the case when the degrees are expressed in decimal values, no minute is expected.\n"
							+ "In the case when the minutes are expressed in decimal values, no second is expected.\n"
							+ "Any other extra characters can be inserted between the various keys. The same values will be expected at the input and at the output.\n"
							+ "Examples :\n" + "NDDD MM.mm ==> W123 10.25\n"
							+ "DDD MM SS N ==> 123 10 30 W\n");
		}
	}

	public static void aideData() {
		String langue = Utils.configuration.getLang();
		if (langue.matches("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==DESCRIPTION DONNEES==\n"
							+ "Même principe que pour les types de données : on ajoute ici les paramètres mesurés.\n"
							+ "\nLa formule de conversion doit être du style x*1000+3 ; x étant la valeur lue dans le fichier\n\n"
							+ "Dans le cas d'une série temporelle :\n"
							+ "*les paramètres DATE, YEAR, MONTH, DAYX, TIME sont rajoutés automatiquement à la génération donc attention à ne pas les ajouter manuellement.\n"
							+ "*on peut choisir si le calcul des dates/heures est basé sur les dates/heures entrées à l'étape Station et la fréquence d'échantillonnage ou si les informations sont présentes au sein de la ligne des données (auquel cas préciser le format).\n"
							+ "\n");
		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==DATA DESCRIPTION==\n"
							+ "It's the same principle than for the type of data: you add here the measured parametres.\n"
							+ "\nConversion formula should be like x*1000+3 ; with x the value read in file\n\n"
							+ "In the case of a temporal series :\n"
							+ "*DATE, YEAR, MONTH, DAYX, TIME parameters are automatically generated so pay attention not to add them manually.\n"
							+ "*you can choose if the time/date computation is based on the date/hours entered at the Station step and the sampling rate or if the information are included into the line of data  (in which case you need to precise the format).\n"
							+ "");
		}
	}

	public static void aideConversion() {
		String langue = Utils.configuration.getLang();
		if (langue.matches("fr_FR")) {
			ViewLog.styledTextLogHelp
					.setText("==#CONVERSION==\n"
							+ "Choisir le format de sortie. On peut générer les deux formats à la suite sans devoir revalider toutes les étapes."
							+ "\n\nLa conversion est divisée en deux étapes successives :\n"
							+ "1-conversion du fichier en mémoire : on peut annuler le processus pendant cette étape !\n"
							+ "2-écriture du fichier sur le disque : on ne peut plus annuler le processus une fois engagé !");

		} else {
			// aide anglaise
			ViewLog.styledTextLogHelp
					.setText("==CONVERSION==\n"
							+ "Choose the output format. You can generate both formats consecutively without revalidating all the steps.\n"
							+ "\n\nConversion is divided in two steps:\n"
							+ "1-file conversion into memory : you still can cancel all the conversion process!\n"
							+ "2-file writing on disk : you can't cancel the conversion process no more while this step is loading!");
		}
	}

}
