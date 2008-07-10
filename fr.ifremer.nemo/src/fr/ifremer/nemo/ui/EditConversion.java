package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.dialogs.InputDialog;
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
import fr.ifremer.nemo.input.SingleFileSurvey;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.input.SurveyMedia;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Messages;

public class EditConversion extends Thread {

	public static ScrolledForm formEdition;

	public static Button buttonConversion;

	public static int validee, loaded;

	public static String nomFichierSortie, nomDossierSortie;

	public static Shell shell;

//	public static Boolean interrompu;

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

	public void run() {

		affichageEdition();
	}

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier
		ViewLog.aideConversion();
		formEdition = ViewEdit.formEdition;
		nomFichierSortie = "";
		nomDossierSortie = "";
//		interrompu = false;

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
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// System.out.println("conversion !");
						
						setDataSetRef("");
						Utils.messageErreur("");
						
						// RAZ du numéro de station incrémenté
						DataModel.stationModel.resetRef();
						DataModel.stationModel.setLastRef(null);
						
						if (ViewFile.radMedatlas.getSelection()) {
							NemoLists.writeSdnParameters("*");
						} else {
							NemoLists.writeSdnParameters("//");
						}
						
						try {
							if (EditDesc.okApresReset && EditSurvey.okApresReset
									&& EditStation.okApresReset
									&& EditData.okApresReset) {
								
								// test pour savoir si on est en campagne ou pas
								if (ViewFile.isCollection()) {
									if (ViewFile.checkCDI.getSelection()) {
										if (!CDIResumeWriter.getInstance().checkConfig()) {
											throw new Exception("CDI cancelled");
										}
										
										if (askDataSetRef()) {
											CDIResumeWriter.getInstance().reset();
											CDIResumeWriter.getInstance().choixFichierSortie();
										} else {
												throw new Exception("CDI cancelled");
										}
									}
									
									choixDossierSortie();

									if (ViewFile.checkCDI.getSelection() && !SurveyMedia.interrompu && !SWTProcess.interrompu) {
										try {
											CDIResumeWriter.getInstance()
													.writeResume();
										} catch (IOException ex) {
											ex.printStackTrace();
										}
									}
								} else {
									if (ViewFile.radODV.getSelection() && ViewFile.radMonoStation.getSelection()) {
										if (askDataSetRef()) {
											choixDossierSortie();
										} else {
											throw new Exception("Conversion cancelled");
										}
									} else {
										choixFichierSortie();
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
		
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
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
		// dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
		dialog.setFilterPath(ViewFile.nomFichier);
		nomFichierSortie = dialog.open();

		if (nomFichierSortie != null) {

			// System.out.println("Fichier = " + nomFichierSortie);

			File fich = new File(nomFichierSortie);
			if (fich.exists()) {
				// fichier existe
				// shell.close();
				Shell shell2 = PlatformUI.getWorkbench().getDisplay().getActiveShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.18")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixFichierSortie();
				} else {
					runSWTProgress();
				}
			} else {
				runSWTProgress();
			}

			// affichage des résultats en fin de conversion
			if (!SurveyMedia.interrompu && !SWTProcess.interrompu) {
				if (ViewFile.radODV.getSelection()) {
					Utils.messageOK(Messages.getString("EditConversion.9"));
				} else {
					Utils.messageOK(Messages.getString("EditConversion.10"));
				}
			} else {
				Utils.messageErreurAppend(messageErreur());
			// Utils.messageErreur(Messages.getString("EditConversion.16"));
			}

		}

	}

	public static void choixDossierSortie() {

		nomDossierSortie = "";

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("EditConversion.25")); //$NON-NLS-1$
		dialog.setFilterPath(ViewFile.nomRepertoire);
		nomDossierSortie = dialog.open();

		if (nomDossierSortie != null) {
			
			File fich = new File(nomDossierSortie);
			if (fich.exists() && fich.listFiles().length != 0) {
				// fichier existe
			//	shell.close();
				Shell shell2 = PlatformUI.getWorkbench().getDisplay().getActiveShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.24")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixDossierSortie();
				} else {
					runSWTProgress();
				}

			} else {
				runSWTProgress();
			}

			// affichage des résultats en fin de conversion
			if (!SurveyMedia.interrompu && !SWTProcess.interrompu) {
				if (ViewFile.radODV.getSelection()) {
					Utils.messageOK(Messages.getString("EditConversion.9"));
				} else {
					Utils.messageOK(Messages.getString("EditConversion.10"));
				}
			} else {
				//	Utils.messageErreur(Messages.getString("EditConversion.16"));
				Utils.messageErreurAppend(messageErreur());
			}

		}

	}
	
	public static void runSWTProgress() {
		Utils.messageErreur("");
		SWTProcess.interrompu = false;
		SurveyMedia.interrompu = false;

		try {
			new SWTProgress().run();
		} catch (Exception e)	{
			Utils.messageErreur(e.getMessage());
			SWTProcess.interrompu=true;
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
		_erreurLigne ++;
	}

	public static String getErreurChaine() {
		return _erreurChaine;
	}

	public static void setErreurChaine(String chaine) {
		_erreurChaine = chaine;
	}
	
	public static String messageErreur() {
		String message = "";
		
		if (getErreurFichier() != null)
			if (!getErreurFichier().trim().equals(""))
				message += Messages.getString("EditConversion.20") + "\t : \t"
				+ getErreurFichier() + "\r\n";
		
		if (getErreurLigne() != 0)
			message += Messages.getString("EditConversion.21") + "\t : \t"
			+ getErreurLigne() + "\r\n";
		
		if (getErreurChaine() != null)
			if (!getErreurChaine().trim().equals(""))
				message += Messages.getString("EditConversion.22") + "\t : \t"
				+ getErreurChaine();
		
		if (!message.equals(""))
			message = "\r\n" + Messages.getString("EditConversion.23")
				+ "\r\n" + message;
		
		return message;
	}
	
	public static String getDataSetRef() {
		return _dataSetRef;
	}
	
	public static void setDataSetRef(String value) {
		_dataSetRef = value;
	}

	public static boolean askDataSetRef() {	
		if (getDataSetRef().equals("")) {
			String strDefault = "";
			if (DataModel.dirSurvey != null) {
				strDefault = DataModel.dirSurvey.getModel().getReference();
			} else if (DataModel.singleSurvey != null) {
				strDefault = DataModel.singleSurvey.getModel().getReference();
			}
			InputDialog inputCdiId = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), Messages.getString("CDIResumeWriter.idTitle"), Messages.getString("CDIResumeWriter.idMessage"), strDefault, null);
			inputCdiId.open();
			
			if (inputCdiId.getReturnCode() == InputDialog.OK) {
				_dataSetRef = inputCdiId.getValue();
				return true;
			} else {
				_dataSetRef = "";
				return false;
			}
		} else {
			return true;
		}
	}
	
	public static String getLocalCdiId(Station station) {
		String temp = "";
		
		if (!EditConversion.getDataSetRef().equals("")) {
			temp = EditConversion.getDataSetRef();
		}
		
		if (!station.getReference().trim().equals("")) {
			if (!temp.equals("")) {
				temp += "_";
			}
			temp += station.getReference().trim();
		}
		
		if (station.getDataType() != null) {
			if (!station.getDataType().getCode().equals("")) {
				if (!temp.equals("")) {
					temp += "_";
				}
				temp += station.getDataType().getCode();
			}
		}
		
		return Utils.validFileName(temp);
	}
}
