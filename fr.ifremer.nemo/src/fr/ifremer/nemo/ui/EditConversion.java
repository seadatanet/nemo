package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

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

	public static Boolean interrompu;

	public static ProgressNotification notifier;

	public void run() {

		affichageEdition();
	}

	public static void affichageEdition() {
		// initialisation zone édition pour étape description de fichier
		ViewLog.aideConversion();
		formEdition = ViewEdit.formEdition;
		nomFichierSortie = "";
		nomDossierSortie = "";
		interrompu = false;

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
						
						Utils.messageErreur("");
						
						// test pour savoir si le compteur CDI est un numérique
						try {
							if (ViewFile.checkCDI.getSelection())
								Integer.parseInt(ViewFile.txtCompteurCDI.getText());
							
							if (EditDesc.okApresReset && EditSurvey.okApresReset
									&& EditStation.okApresReset
									&& EditData.okApresReset) {
								
								// test pour savoir si on est en campagne ou pas
								if (ViewFile.isCollection()) {

									if (ViewFile.checkCDI.getSelection()) {
										CDIResumeWriter.getInstance().reset();
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
								} else
									choixFichierSortie();

							} else {
								Utils.messageErreur(Messages
										.getString("EditConversion.15"));
							}
						} catch (NumberFormatException ex) {
							Utils.messageErreur(Messages
									.getString("EditConversion.17"));							
						} catch (Exception ex) {
							Utils.messageErreur(Messages
									.getString("EditConversion.17"));	
						}
						
					}

				});

		ViewFile.affiche = 4;
		formEdition.reflow(true);

	}

	public static void initBoutons() {
		Shell shell = new Shell();

		buttonConversion = new Button(shell, SWT.NONE);

	}

	public static void razEdition() {

		Shell shell = new Shell();

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

		Shell shell = new Shell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("EditConversion.13")); //$NON-NLS-1$
		// FAE45
		// dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
		dialog.setFilterPath(ViewFile.nomFichier);
		nomFichierSortie = dialog.open();

		if (nomFichierSortie != null) {

			// System.out.println("Fichier = " + nomFichierSortie);

			File fich = new File(nomFichierSortie);
			if (fich.exists()) {
				// fichier existe
				shell.close();
				Shell shell2 = new Shell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.18")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixFichierSortie();
				} else {
					Utils.messageErreur("");
					SWTProcess.interrompu = false;
					try
					{
						new SWTProgress().run();
					}
					catch (InterruptedException e)
					{
						Utils.messageErreur(e.getMessage());
						SurveyMedia.interrompu = true;
					}
				}

			} else {
				Utils.messageErreur("");
				SWTProcess.interrompu = false;
				SurveyMedia.interrompu = false;

				try {
					new SWTProgress().run();
				} catch (InterruptedException e) {
					Utils.messageErreur(e.getMessage());
				}
			}

			// affichage des résultats en fin de conversion
			if (!SurveyMedia.interrompu && !SWTProcess.interrompu) {
				if (ViewFile.odv.getSelection()) {
					Utils.messageOK(Messages.getString("EditConversion.9"));
				} else {
					Utils.messageOK(Messages.getString("EditConversion.10"));
				}
			} else {
			//	Utils.messageErreur(Messages.getString("EditConversion.16"));
			}

		}

	}

	public static void choixDossierSortie() {

		nomDossierSortie = "";

		Shell shell = new Shell();
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("EditConversion.13")); //$NON-NLS-1$
		dialog.setFilterPath(ViewFile.nomRepertoire);
		nomDossierSortie = dialog.open();

		if (nomDossierSortie != null) {
			
			File fich = new File(nomDossierSortie);
			if (fich.exists() && fich.listFiles().length != 0) {
				// fichier existe
				shell.close();
				Shell shell2 = new Shell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
				mb.setMessage(Messages.getString("EditConversion.14")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixFichierSortie();
				} else {
					Utils.messageErreur("");
					SWTProcess.interrompu = false;
					try {
						new SWTProgress().run();
					}
					catch (Exception e)
					{
						Utils.messageErreur(e.getMessage());
						SWTProcess.interrompu = true;
					}
				}

			} else {
				Utils.messageErreur("");
				SWTProcess.interrompu = false;
				SurveyMedia.interrompu = false;

				try {
					new SWTProgress().run();
				}
				catch (Exception e)
				{
					Utils.messageErreur(e.getMessage());
					SWTProcess.interrompu=true;
				}
			}

			// affichage des résultats en fin de conversion
			if (!SurveyMedia.interrompu && !SWTProcess.interrompu) {
				if (ViewFile.odv.getSelection()) {
					Utils.messageOK(Messages.getString("EditConversion.9"));
				} else {
					Utils.messageOK(Messages.getString("EditConversion.10"));
				}
			} else {
				//	Utils.messageErreur(Messages.getString("EditConversion.16"));
			}

		}

	}
}
