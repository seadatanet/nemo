package fr.ifremer.nemo.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;

public class SWTProcess implements IRunnableWithProgress{
	public static ScrolledForm formEdition;
	public static Button buttonConversion;
	public static int validee, loaded;
	public static String nomFichierSortie;
	public static Shell shell;
	private static final int NB_ITERATION = 100;
	public static ProgressNotification notifier;
	public static Survey survey;
	public static ODVSurveyWriter writerODV;
	public static MedatlasSurveyWriter writerMed;
	public static Boolean odv;

	  public void run(IProgressMonitor monitor) throws InvocationTargetException, 
	                                                   InterruptedException {
	    monitor.beginTask("Conversion en cours... Veuillez patienter...", IProgressMonitor.UNKNOWN);
	  
	    final IProgressMonitor monitor2;
	  
	    monitor2 = monitor;

	    //NOTIFIER
	    notifier = new ProgressNotification() {
            public void progress(final int val) {
            	try {	
            			Application.display.syncExec( new Runnable() {
                                                      public void run() {
                                                   	 // monitor2.worked((Integer)val/100);
                                                         monitor2.subTask("Avancement : "+val+"%");
                                                   	  //monitor2.worked(1);
                                                      }
                                                  });
                } catch (Exception e) {
                }
            }
        };
	    
	    
	    //TACHE A EFFECTUER
	
		try {
			monitor.setTaskName("Conversion du fichier en mémoire...");
			survey = DataModel.inputModel.convert(notifier);
		} catch (FileNotFoundException e) {
			//
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		} catch (ParseException e) {
			//
			e.printStackTrace();
		}
		
		writerODV = new fr.ifremer.nemo.odv.ODVSurveyWriter();
		writerMed = new fr.ifremer.nemo.medatlas.MedatlasSurveyWriter();
		notifier.progress(0);
		
		try {
			monitor.setTaskName("Ecriture du fichier sur le disque...");
			
			if (!odv){
				//MEDATLAS
				writerMed.writeSurvey ( survey, new java.io.File(EditConversion.nomFichierSortie), notifier);
				}
			else{
				//ODV
				writerODV.writeSurvey( survey, new java.io.File(EditConversion.nomFichierSortie), notifier);
				
			}
		} catch (IOException e) {
			// 
			e.printStackTrace();
		} catch (GenerationException e) {
			// 
			e.printStackTrace();
		}
		
	    monitor.done();
	 
	  }
	
}
