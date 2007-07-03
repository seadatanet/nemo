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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;

public class EditConversion extends Thread {

	public static ScrolledForm formEdition;
	public static Button buttonConversion;
	public static int validee, loaded;
	public static String nomFichierSortie;
	public static Shell shell;
	
	public static ProgressNotification notifier;
	
	
	public void run(){
		
		affichageEdition();
	}
	
	
	
	public static void affichageEdition(){
//		 initialisation zone édition pour étape description de fichier 

		formEdition = ViewEdit.formEdition;
		nomFichierSortie="";
	
//		raz message erreur
		Utils.messageErreur("");		 //$NON-NLS-1$
		
		ViewEdit.formEdition.getBody().setLayout(new GridLayout());
		ViewEdit.formEdition.setText(Messages.getString("EditConversion.1")); //$NON-NLS-1$
		
		initBoutons();
		
		loaded = 1;
		
		//bouton conversion
		buttonConversion.setParent(formEdition.getBody());
		buttonConversion.setText(Messages.getString("EditConversion.2")); //$NON-NLS-1$
		
		//listener sur le bouton conversion
		buttonConversion
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
		public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
		//System.out.println("conversion !"); 
				
		//test pour savoir si on est en campagne ou pas
			choixFichierSortie();
		
		
			
			shell = new Shell(SWT.NONE);	
			shell.setSize(500, 20);
    		shell.setBounds(100, 100, 500, 20);
    		//shell.open();
    		
    		final ProgressBar progressbar = new ProgressBar(shell,
  				  SWT.HORIZONTAL | SWT.SMOOTH);
  				progressbar.setMinimum(0);
  				progressbar.setMaximum(100);
  				//progressbar.setSize(200,20);
  				
  				progressbar.setBounds(0,0, 500, 20);
  				shell.setText(Messages.getString("EditConversion.3")); //$NON-NLS-1$
  				shell.pack();
    		
			java.io.FileInputStream stream = null;
	
		
			Utils.progression = 0;
			
			
			
			
		new SWTProgress().run();
		
			
		
			
			
		//System.out.println("==== Fin de conversion ====");	
		if (ViewFile.odv.getSelection()){
		Utils.messageOK(Messages.getString("EditConversion.9"));} //$NON-NLS-1$
		else {Utils.messageOK(Messages.getString("EditConversion.10"));} //$NON-NLS-1$
		
		shell.close();

		
		
		
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
		ViewEdit.formEdition.setText(""); //$NON-NLS-1$
		
		
	}
	
	
	

	
public static void DrawingBack(){
		
	ScrolledForm s;
	s = ViewEdit.formEdition;
	
		buttonConversion.setParent(s.getBody());
		
		s.setText(Messages.getString("EditConversion.12")); //$NON-NLS-1$
		
		formEdition.reflow(true);
		
//		affichage courant
		ViewFile.affiche = 4;
		
		
	}
	
public static void choixFichierSortie(){
	
	Shell shell = new Shell();
	FileDialog dialog = new FileDialog(shell, SWT.SAVE |SWT.APPLICATION_MODAL);
	dialog.setText(Messages.getString("EditConversion.13")); //$NON-NLS-1$
	nomFichierSortie = dialog.open();
	
	File fich = new File(nomFichierSortie);
	if (fich.exists()){
		shell.close();
		
		Shell shell2 = new Shell();
		
		MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING |  SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
		mb.setMessage(Messages.getString("EditConversion.14")); //$NON-NLS-1$
		
		int reponse = mb.open();
		if (reponse == SWT.NO){
			choixFichierSortie();
			}
		
		
	}
	
	
	
	
}

}

