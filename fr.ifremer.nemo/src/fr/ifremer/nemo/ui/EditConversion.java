package fr.ifremer.nemo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;

public class EditConversion extends Thread {

	public static ScrolledForm formEdition;
	public static Button buttonConversion;
	public static int validee, loaded;
	public static String nomFichierSortie;
	public static Shell shell;
	
	
	public void run(){
		
		affichageEdition();
	}
	
	
	
	public static void affichageEdition(){
//		 initialisation zone édition pour étape description de fichier 

		formEdition = ViewEdit.formEdition;
	
//		raz message erreur
		Utils.messageErreur("");		
		
		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText("Conversion...");
		
		initBoutons();
		
		loaded = 1;
		
		//bouton conversion
		buttonConversion.setParent(formEdition.getBody());
		buttonConversion.setText("Lancer la conversion...");
		
		//listener sur le bouton conversion
		buttonConversion
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		System.out.println("conversion !"); 
				
		//test pour savoir si on est en campagne ou pas
			choixFichierSortie();
		
			shell = new Shell();	
			shell.setSize(200, 20);
    		shell.setBounds(50, 50, 200, 20);
    		shell.open();
    		
    		final ProgressBar progressbar = new ProgressBar(shell,
  				  SWT.HORIZONTAL | SWT.SMOOTH | SWT.APPLICATION_MODAL);
  				progressbar.setMinimum(0);
  				progressbar.setMaximum(100);
  				//progressbar.setSize(200,20);
  				
  				progressbar.setBounds(0,0, 200, 20);
  				shell.setText("Conversion en cours... Veuillez patienter...");
  				shell.pack();
    		
    		
			java.io.FileInputStream stream = null;
			ProgressNotification notifier = new ProgressNotification() {
                public void progress(final int val) {
                	try {
                		
                				Application.display.syncExec( new Runnable() {
                                                          public void run() {
                                                              progressbar.setSelection(val);
                                                              System.out.println(">>>"+ val);
                                                              shell.pack();
                                                              
                                                          }
                                                      });
                    } catch (Exception e) {
                    }
                }
            };
			try {
				Survey survey = DataModel.inputModel.convert(notifier);
				
				//survey.writeXMLOn(new FileOutputStream(new File("D:/tmp.xml")));
				
				fr.ifremer.nemo.medatlas.MedatlasSurveyWriter writerMed;
				fr.ifremer.nemo.odv.ODVSurveyWriter writerODV;
				
				if (ViewFile.medatlas.getSelection()){
				 writerMed = new fr.ifremer.nemo.medatlas.MedatlasSurveyWriter();
				 notifier.progress(0);
			    	try {
						writerMed.writeSurvey ( survey, new java.io.File(nomFichierSortie), notifier);
					} catch (GenerationException e1) {
						System.out.println("Erreur de  génération de fichier de sortie!");
						e1.printStackTrace();
					}
					}
				else {
					 writerODV = new fr.ifremer.nemo.odv.ODVSurveyWriter();
					 notifier.progress(0);
				    	try {
							writerODV.writeSurvey ( survey, new java.io.File(nomFichierSortie), notifier);
						} catch (GenerationException e1) {
							System.out.println("Erreur de  génération de fichier de sortie!");
							e1.printStackTrace();
						}
				}
		    	
			
			
			
			}
			catch (FileNotFoundException e1) {e1.printStackTrace(); System.out.println("Fichier non trouvé ! <EditConversion>");}
			catch (IOException e1) {e1.printStackTrace(); System.out.println("Fichier non trouvé ! <EditConversion>");}
			catch (ParseException e1) {e1.printStackTrace(); System.out.println("Erreur de format ! <EditConversion>");}
			
		System.out.println("==== Fin de conversion ====");	
		if (ViewFile.odv.getSelection()){
		Utils.messageErreur("==== Fin de conversion en ODV ====");}
		else {Utils.messageErreur("==== Fin de conversion en Medatlas ====");}
		
		shell.close();
		
//			//dossier de campagne
//			
//			choixFichierSortie();
//			
//			java.io.FileInputStream stream = null;
//			ProgressNotification progress = new ProgressNotification() {
//                public void progress(final int val) {
//                	try {
//                        SwingUtilities.invokeAndWait( new Runnable() {
//                                                          public void run() {
//                                                              //progress.setValue(val);
//                                                          }
//                                                      });
//                    } catch (Exception e) {
//                    }
//                }
//            };
//			try {
//				Survey survey = DataModel.inputModel.convert(progress);
//				fr.ifremer.nemo.medatlas.MedatlasSurveyWriter writer = new fr.ifremer.nemo.medatlas.MedatlasSurveyWriter();
//		    	progress.progress(0);
//		    	try {
//					writer.writeSurvey ( survey, new java.io.File(nomFichierSortie), progress);
//				} catch (GenerationException e1) {
//					System.out.println("Erreur de  génération de fichier de sortie!");
//					e1.printStackTrace();
//				}
//			
//			
//			
//			}
//			catch (FileNotFoundException e1) {e1.printStackTrace(); System.out.println("Fichier non trouvé ! <EditConversion>");}
//			catch (IOException e1) {e1.printStackTrace(); System.out.println("Fichier non trouvé ! <EditConversion>");}
//			catch (ParseException e1) {e1.printStackTrace(); System.out.println("Erreur de format ! <EditConversion>" + e1.getMessage() + "###" +e1.getCause());}
//			
//		System.out.println("==== Fin de conversion ====");	
//			
//			
//			
//			
//		}
		
		//lancement de la conversion
		
		}
		});
	
		ViewFile.affiche = 4;
		formEdition.reflow(true);	
		
	}	
	
		
	public static void initBoutons(){
		Shell shell = new Shell();
		
		buttonConversion = new Button(shell, SWT.NONE);
		
		
	}

	public static void razEdition(){
		
		Shell shell = new Shell();
		
		buttonConversion.setParent(shell);
		formEdition.reflow(true);
		ViewEdit.formEdition.setText("");
		
		
	}
	
public static void DrawingBack(){
		
	ScrolledForm s;
	s = ViewEdit.formEdition;
	
		buttonConversion.setParent(s.getBody());
		
		s.setText("Conversion...");
		
		formEdition.reflow(true);
		
//		affichage courant
		ViewFile.affiche = 4;
		
		
	}
	
public static void choixFichierSortie(){
	
	Shell shell = new Shell();
	FileDialog dialog = new FileDialog(shell, SWT.SAVE |SWT.APPLICATION_MODAL);
	dialog.setText("Entrez un nom pour le fichier de sortie");
	nomFichierSortie = dialog.open();
	
	File fich = new File(nomFichierSortie);
	if (fich.exists()){
		shell.close();
		
		Shell shell2 = new Shell();
		
		MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING |  SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
		mb.setMessage("Le fichier de sortie spécifié existe déjà ! Voulez-vous l'écraser ?");
		
		int reponse = mb.open();
		if (reponse == SWT.NO){
			choixFichierSortie();
			}
		
		
	}
	
	
	
	
}

}
