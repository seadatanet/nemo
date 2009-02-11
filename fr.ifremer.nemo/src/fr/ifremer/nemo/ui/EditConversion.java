package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.input.SurveyMedia;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;

public class EditConversion extends Thread {

	public static ScrolledForm formEdition;

	public static Button buttonConversion;

	public static int validee, loaded;

	public static String nomFichierSortie, nomDossierSortie, nomHorsPrefix;

	public static Shell shell;

	// public static Boolean interrompu;

	public static ProgressNotification notifier;

	private static String _dataSetRef;

	/**
	 * Nom du fichier lu à retourner en cas d'erreur
	 */
	protected static String _erreurFichier;

	/**
	 * Nombre de lignes lues à retourner en cas d'erreur
	 */
	protected static int _erreurLigne;

	/**
	 * Chaine lue à retourner en cas d'erreur
	 */
	protected static String _erreurChaine;

	/**
	 * Nombre de lignes du fichier lu
	 */
	protected static int _totalLigne;

	/**
	 * Liste des stations triees
	 */
	protected static LinkedHashMap<String, Integer> _sortedStations = new LinkedHashMap<String, Integer>();

	public void run() {

		affichageEdition();
	}

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier
		ViewLog.aideConversion();
		formEdition = ViewEdit.formEdition;
		nomFichierSortie = "";
		nomDossierSortie = "";
		// interrompu = false;

		// raz message erreur
		Utils.messageErreur(""); //$NON-NLS-1$

		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditConversion.1")); //$NON-NLS-1$

		initBoutons();

		loaded = 1;

		// bouton conversion
		buttonConversion.setParent(formEdition.getBody());
		buttonConversion.setText(Messages.getString("EditConversion.2")); //$NON-NLS-1$

		// listener sur le bouton conversion
		buttonConversion
				.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
					@Override
                    public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						try {
							// Test si la config est bien remplie
							if (!EditConversion.checkConfig()) {
								throw new Exception("CDI cancelled");
							}
							
							setDataSetRef("");
							Utils.messageErreur("");

							// RAZ du numéro de station incrémenté
							DataModel.stationModel.resetRef();
							DataModel.stationModel.setLastRef(null);

							if (ViewFile.radMedatlas.getSelection()) {
								NemoLists.writeSdnParameters(Conversion.MEDATLAS);
							} else {
								NemoLists.writeSdnParameters(Conversion.ODV);
							}

							// RAZ de la liste des stations triées
							getSortedStations().clear();
							
							if (EditDesc.okApresReset
									&& EditSurvey.okApresReset
									&& EditStation.okApresReset
									&& EditData.okApresReset) {

								if (ViewFile.radODV.getSelection()) {
									if (!askDataSetRef()) {
										throw new Exception("Cancelled");
									}
								}

                                //On demande les infos pour faire le résumé CDI, qui sera fait plus tard
                                if (Utils.configuration.getWriteCdi()) {
                                    
                                    CDIResumeWriter.getInstance().reset();
                                    CDIResumeWriter.getInstance()
                                            .choixFichierSortie();
                                }        
                                
                                // test pour savoir si on est en campagne ou pas
                                if (ViewFile.isCollection()) {

									choixDossierSortie();

	
								} else if (!ViewFile.isCollection()
										&& ViewFile.radODV.getSelection()
										&& ViewFile.radMonoStation
												.getSelection()) {
									choixDossierSortie();
								} else {
									choixFichierSortie();
								}
                                // Ecriture du resume CDI
                                if (Utils.configuration.getWriteCdi()
                                        && !SurveyMedia.interrompu
                                        && !SWTProcess.interrompu) {
                                    try {
                                        CDIResumeWriter.getInstance()
                                                .writeResume();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }                                
							} else {
								Utils.messageErreur(Messages
										.getString("EditConversion.15"));
							}
						} catch (Exception ex) {
							Utils.messageErreur(Messages
									.getString("EditConversion.16"));
						}

					}

				});

		ViewFile.affiche = 4;
		formEdition.reflow(true);

	}

	public static void initBoutons() {
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

		buttonConversion = new Button(shell, SWT.NONE);

	}

	public static void razEdition() {

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();

		buttonConversion.setParent(shell);
		formEdition.reflow(true);
		ViewEdit.formEdition.setText(""); //$NON-NLS-1$

	}

	public static void DrawingBack() {

		ScrolledForm s;
		s = ViewEdit.formEdition;
		ViewLog.aideConversion();
		buttonConversion.setParent(s.getBody());

		s.setText(Messages.getString("EditConversion.12")); //$NON-NLS-1$

		formEdition.reflow(true);

		// affichage courant
		ViewFile.affiche = 4;

	}

	public static void choixFichierSortie() {

		nomFichierSortie = "";

		Shell newShell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(newShell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("EditConversion.13")); //$NON-NLS-1$

		// FAE090
		if (ViewFile.radODV.getSelection()) {
			String[] extensions = new String[1];
			extensions[0] = "*.txt";
			dialog.setFilterExtensions(extensions);

			Survey survey = null;
			if (DataModel.singleSurvey != null) {
				survey = DataModel.singleSurvey.getModel();
			} else if (DataModel.dirSurvey != null) {
				survey = DataModel.dirSurvey.getModel();
			}

			if (survey != null) {
				String fileName = survey.getReference().trim();
				if (fileName.equals("")) {
					fileName = survey.getName().trim();
				}
				fileName = Utils.validFileName(fileName);
				dialog.setFileName(fileName);
			}
		}

		// FAE45
		if (ViewFile.radioFichier.getSelection()) {
			dialog.setFilterPath(ViewFile.nomFichier);
		} else {
			dialog.setFilterPath(ViewFile.nomRepertoire.substring(0,
					ViewFile.nomRepertoire.lastIndexOf('\\')));
		}

		nomFichierSortie = dialog.open();

		if (nomFichierSortie != null) {
			nomDossierSortie = Utils.extraitCheminFichier(nomFichierSortie);

			// Teste si le repertoire de sortie est different du repertoire
			// d'entree
			if (ViewFile.radioFichier.getSelection()) {
				if (nomFichierSortie.equals(ViewFile.nomFichier)) {
					MessageBox mb = new MessageBox(PlatformUI.getWorkbench()
							.getDisplay().getActiveShell(), SWT.ICON_ERROR
							| SWT.OK | SWT.APPLICATION_MODAL);
					mb.setMessage(Messages.getString("EditConversion.27")); //$NON-NLS-1$
					mb.open();

					choixFichierSortie();
				}
			} else {
				if (nomDossierSortie.equals(ViewFile.nomRepertoire)) {
					MessageBox mb = new MessageBox(PlatformUI.getWorkbench()
							.getDisplay().getActiveShell(), SWT.ICON_ERROR
							| SWT.OK | SWT.APPLICATION_MODAL);
					mb.setMessage(Messages.getString("EditConversion.26")); //$NON-NLS-1$
					mb.open();

					choixFichierSortie();
				}
			}

			File fich = new File(nomFichierSortie);
			if (fich.exists()) 
            {
				// fichier existe
				// shell.close();
				Shell shell2 = PlatformUI.getWorkbench().getDisplay()
						.getActiveShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.18")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixFichierSortie();
				} else {
                    definePrefixDossier();
					runSWTProgress();
				}
			} else {
                definePrefixDossier();
				runSWTProgress();
			}

			// Ecriture du fichier de mapping CDI
			/*try {
				CDIMappingWriter.getInstance().writeMapping();
			} catch (Exception e) {
				e.printStackTrace();
			}*/

			// affichage des résultats en fin de conversion
			afficheResultat();
		}

	}

	public static void choixDossierSortie() {

        nomFichierSortie = Global.EMPTY_STRING;
        nomDossierSortie = Global.EMPTY_STRING;

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("EditConversion.25")); //$NON-NLS-1$

		if (ViewFile.radioFichier.getSelection()) {
			dialog.setFilterPath(ViewFile.nomRepertoire);
		} else {
			dialog.setFilterPath(ViewFile.nomRepertoire.substring(0,
					ViewFile.nomRepertoire.lastIndexOf('\\')));
		}

		nomDossierSortie = dialog.open();

		if (nomDossierSortie != null) {

			// Teste si le répertoire de sortie est différent du répertoire
			// d'entrée
			if (!ViewFile.radioFichier.getSelection()
					&& nomDossierSortie.equals(ViewFile.nomRepertoire)) {
				MessageBox mb = new MessageBox(PlatformUI.getWorkbench()
						.getDisplay().getActiveShell(), SWT.ICON_ERROR | SWT.OK
						| SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.26")); //$NON-NLS-1$
				mb.open();

				choixDossierSortie();
			}

			File fich = new File(nomDossierSortie);
			if (fich.exists() && fich.listFiles().length != 0) {
				// fichier existe
				// shell.close();
				Shell shell2 = PlatformUI.getWorkbench().getDisplay()
						.getActiveShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.24")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixDossierSortie();
				} else {
                    definePrefixDossier();
					runSWTProgress();
				}

			} else {
                definePrefixDossier();
				runSWTProgress();
			}

			// Ecriture du fichier de mapping CDI
			/*try {
				CDIMappingWriter.getInstance().writeMapping();
			} catch (Exception e) {
				e.printStackTrace();
			}*/

			// affichage des résultats en fin de conversion
			afficheResultat();
		}

	}

	public static void runSWTProgress() {
		Utils.messageErreur(Global.EMPTY_STRING);
		SWTProcess.interrompu = false;
		SurveyMedia.interrompu = false;
		try {
			new SWTProgress().run();
		} catch (Exception e) {
			Utils.messageErreur(e.getMessage());
			SWTProcess.interrompu = true;
		}
	}

	public static void afficheResultat() {
		if (!SurveyMedia.interrompu && !SWTProcess.interrompu) {
			if (ViewFile.radODV.getSelection()) {
				Utils.messageOK(Messages.getString("EditConversion.9"));  //$NON-NLS-1$
			} else {
				Utils.messageOK(Messages.getString("EditConversion.10"));  //$NON-NLS-1$
			}
		} else {
			// Utils.messageErreur(Messages.getString("EditConversion.16"));
			Utils.messageErreurAppend(messageErreur());
		}

		// Stations triées
		if (getSortedStations().size() > 0) {
			String sortedMessage = "\n\n" + getSortedStations().size() + Global.SPACE  //$NON-NLS-1$
					+ Messages.getString("EditConversion.28") + Global.SPACE;  //$NON-NLS-1$
			
			for (Entry<String, Integer> currentEntry : getSortedStations()
					.entrySet()) {
				// Utils.messageOKAppend("\n\tstation " + currentEntry.getKey()
				// + " (" + currentEntry.getValue() + " " +
				// Messages.getString("EditConversion.29"));
				sortedMessage += currentEntry.getKey() + ", ";  //$NON-NLS-1$
			}
			
			int last = sortedMessage.lastIndexOf(",");  //$NON-NLS-1$
			if (last > 0) {
				sortedMessage = sortedMessage.substring(0, last);
			}
			Utils.messageOKAppend(sortedMessage);
		}
	}

	public static String getErreurFichier() {
		return _erreurFichier;
	}

	public static void setErreurFichier(String fichier) {
		_erreurFichier = fichier;
	}

	public static int getErreurLigne() {
		return _erreurLigne;
	}

	public static void setErreurLigne(int ligne) {
		_erreurLigne = ligne;
	}

	public static void incrementErreurLigne() {
		_erreurLigne++;
	}

	public static String getErreurChaine() {
		return _erreurChaine;
	}

	public static void setErreurChaine(String chaine) {
		_erreurChaine = chaine;
	}

	public static String messageErreur() {
		String message = Global.EMPTY_STRING;

		if (getErreurFichier() != null) {
			if (!getErreurFichier().trim().equals(Global.EMPTY_STRING)) {
				message += Messages.getString("EditConversion.20") + "\t : \t" //$NON-NLS-1$ 
						+ getErreurFichier() + Global.RN;
			}
		}

		if (getErreurLigne() != 0) {
			if (getErreurLigne() <= DataModel.stationModel.getNumberOfLines()) {
				// Si l'erreur se trouve dans l'en-tête de station, la ligne
				// indiquée
				// n'est pas forcément la bonne, il vaut mieux ne pas l'indiquer
				return Global.EMPTY_STRING;
			}
			message += Messages.getString("EditConversion.21") + "\t : \t" //$NON-NLS-1$  //$NON-NLS-2$ 
					+ getErreurLigne() + Global.RN;
		}

		if (getErreurChaine() != null) {
			if (!getErreurChaine().trim().equals(Global.EMPTY_STRING)) {
				message += Messages.getString("EditConversion.22") + "\t : \t" //$NON-NLS-1$  //$NON-NLS-2$
						+ getErreurChaine();
			}
		}

		if (!message.equals(Global.EMPTY_STRING)) {
			message = Global.RN + Messages.getString("EditConversion.23") + Global.RN //$NON-NLS-1$
					+ message;
		}

		return message;
	}

	public static String getDataSetRef() {
		return _dataSetRef;
	}

	public static void setDataSetRef(String value) {
		_dataSetRef = value;
	}

	public static boolean askDataSetRef() 
	{
		if (getDataSetRef().equals(Global.EMPTY_STRING)) 
		{
			String strDefault = Global.EMPTY_STRING;
			if (DataModel.dirSurvey != null) {
				strDefault = DataModel.dirSurvey.getModel().getReference();
				if (strDefault.equals(Global.EMPTY_STRING)) strDefault = DataModel.dirSurvey.getModel().getName();
			} else if (DataModel.singleSurvey != null) {
				strDefault = DataModel.singleSurvey.getModel().getReference();
				if (strDefault.equals(Global.EMPTY_STRING)) strDefault = DataModel.singleSurvey.getModel().getName();
			}
			InputDialog inputDataSetRef = new InputDialog(PlatformUI
					.getWorkbench().getDisplay().getActiveShell(), Messages
					.getString("CDIResumeWriter.idTitle"), Messages
					.getString("CDIResumeWriter.idMessage"), strDefault, null);
			inputDataSetRef.open();

			if (inputDataSetRef.getReturnCode() == InputDialog.OK) { 
				_dataSetRef = inputDataSetRef.getValue();
				return true;
			} else {
				_dataSetRef = Global.EMPTY_STRING;
				return false;
			}
		} 
		else 
		{
			return true;
		}
	}
    
    public static void definePrefixDossier() 
    {
            String cheminComplet;
            Boolean modeDossier=false;
            
            if(nomFichierSortie.isEmpty())
            {
                cheminComplet = nomDossierSortie;
                modeDossier = true;
            }
            else
                cheminComplet = nomFichierSortie;
        
            InputDialog inputPrefixDossier = new InputDialog(PlatformUI
                    .getWorkbench().getDisplay().getActiveShell(), Messages
                    .getString("InputPrefixDossier.idTitle"), Messages   //$NON-NLS-1$
                    .getString("InputPrefixDossier.idMessage"), nomDossierSortie, null);   //$NON-NLS-1$
            inputPrefixDossier.open();


            if (inputPrefixDossier.getReturnCode() == InputDialog.OK ) 
            { 
                if(inputPrefixDossier.getValue().endsWith(File.separator) || modeDossier)
                {
                    nomHorsPrefix = cheminComplet.replace(inputPrefixDossier.getValue(),""); //$NON-NLS-1$
                }
                else
                {
                    nomHorsPrefix = cheminComplet.replace(inputPrefixDossier.getValue()+File.separator,""); //$NON-NLS-1$
                }
            }
            
            //On affiche la boite de dialogue tant que l'utilisateur saisie une donnée incorrecte
            if(!cheminComplet.endsWith(nomHorsPrefix))
            {
                definePrefixDossier();
            }
    }

	public static String getLocalCdiId(Station station, String surveyReference) {
		// FAE 1143 : nommage du LOCAL_CDI_ID
		
		String temp = Global.EMPTY_STRING;
		
		if (SWTProcess.getOdv()) {
			// ODV      -> [Référence du jeu de données]
			temp = getDataSetRef();
		}else{
			// MEDATLAS ->  [Référence campagne]
			temp = Formatter.getSingleton().padString(surveyReference,13);
		}
		
		// [Numéro station] codé sur 5 caractères il est complété à gauche par des '0'. 
		String stationReference = station.getReference().trim();
		if (!temp.equals(Global.EMPTY_STRING)) temp += "_"; //$NON-NLS-1$
		temp += Formatter.getSingleton().padZero(stationReference, 5);
		
		// [Type de données] s'il existe
		if (station.getDataType() != null) {
			if (!station.getDataType().getCode().equals(Global.EMPTY_STRING)) {
				if (!temp.equals(Global.EMPTY_STRING)) {
					temp += "_"; //$NON-NLS-1$
				}
				temp += station.getDataType().getCode();
			}
		}

		return Utils.validFileName(temp);
	}
	
	public static boolean checkConfig() {
		boolean blnOK = true;
		String message = Messages.getString("CDIResumeWriter.configMessage"); //$NON-NLS-1$

        if (Utils.configuration.getWriteCdi())        
        {
    		if (Utils.configuration.getIdEdmoRespCdi().trim().equals(Global.EMPTY_STRING)) {
    			message += Global.RN + " - " + Messages.getString("OptionsAction.21"); //$NON-NLS-1$ //$NON-NLS-2$
    			blnOK = false;
    		}
		
    		if (Utils.configuration.getCodeEdmoDonnees().trim().equals(Global.EMPTY_STRING)) {
    			message += Global.RN + " - " + Messages.getString("OptionsAction.24"); //$NON-NLS-1$ //$NON-NLS-2$
    			blnOK = false;
    		}

    		if (Utils.configuration.getIdEdmoRespDonnees().trim().equals(Global.EMPTY_STRING)) {
    			message += Global.RN + " - " + Messages.getString("OptionsAction.25"); //$NON-NLS-1$ //$NON-NLS-2$
    			blnOK = false;
    		}
    		
    		if (Utils.configuration.getIdEdmoDistDonnees().trim().equals(Global.EMPTY_STRING)) {
    			message += Global.RN + " - " + Messages.getString("OptionsAction.26"); //$NON-NLS-1$ //$NON-NLS-2$
    			blnOK = false;
    		}
		
    		if (Utils.configuration.getCdiURL().trim().equals(Global.EMPTY_STRING)) {
    			message += Global.RN + " - " + Messages.getString("OptionsAction.22"); //$NON-NLS-1$ //$NON-NLS-2$
    			blnOK = false;
    		}
		
    		if (Utils.configuration.getCdiMethod().trim().equals(Global.EMPTY_STRING)) {
    			message += Global.RN + " - " + Messages.getString("OptionsAction.23"); //$NON-NLS-1$ //$NON-NLS-2$
    			blnOK = false;
    		}
            
        }
		
		if (Utils.configuration.getFichierMapping().trim().equals(Global.EMPTY_STRING)) {
			message += Global.RN + " - " + Messages.getString("OptionsAction.31"); //$NON-NLS-1$ //$NON-NLS-2$
			blnOK = false;
		}
		
		/*if (Utils.configuration.getArboMapping().trim().equals(Global.EMPTY_STRING)) {
			message += Global.RN + " - " + Messages.getString("OptionsAction.32"); //$NON-NLS-1$ //$NON-NLS-2$
			blnOK = false;
		}*/
		
		if (!blnOK) {
			try {
				String[] buttons = new String[1];
				buttons[0] = "OK"; //$NON-NLS-1$ 
				MessageDialog dialog = new MessageDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), Messages.getString("CDIResumeWriter.configTitle"), null, message, MessageDialog.NONE, buttons, 0);
				dialog.open();
			} catch (Exception e) {
				e.printStackTrace();
			}
						
			ApplicationActionBarAdvisor.optionsAction.run();
			
			return false;
		}
		return true;
	}

	public static int getTotalLigne() {
		return _totalLigne;
	}

	public static void setTotalLigne(int ligne) {
		_totalLigne = ligne;
	}

	public static LinkedHashMap<String, Integer> getSortedStations() {
		return _sortedStations;
	}

	public static void setSortedStations(LinkedHashMap<String, Integer> stations) {
		_sortedStations = stations;
	}
}
