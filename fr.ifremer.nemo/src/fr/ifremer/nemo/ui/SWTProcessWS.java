package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.output.ProgressNotification;

public class SWTProcessWS implements IRunnableWithProgress{
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
	public static Boolean odv, dossier, fichiersConstantes;
	public static IProgressMonitor monitor2;
	public static String referenceWS;

	  public void run(IProgressMonitor monitor) throws InvocationTargetException, 
	                                                   InterruptedException {
	    monitor.beginTask(Messages.getString("SWTProcessWS.1"), IProgressMonitor.UNKNOWN);
	    
	    
	    monitor2 = monitor;

	    //TACHES A EFFECTUER
	
	    if (fichiersConstantes){
	    	//mise à jour fichiers constantes
	    	monitor.subTask(Messages.getString("SWTProcessWS.2"));
	    	NemoLists.updateCountries();
	    	
	   	 	monitor.subTask(Messages.getString("SWTProcessWS.3"));
	    	NemoLists.updateDataTypes();
	    	
	    	monitor.subTask(Messages.getString("SWTProcessWS.4"));
	    	NemoLists.updateRegions();  
	    	
	    	monitor.subTask(Messages.getString("SWTProcessWS.5"));
	    	NemoLists.updateShips();
	    	
	    	monitor.subTask(Messages.getString("SWTProcessWS.6"));
	    	NemoLists.updateDataCentres();
	    	
	    	monitor.subTask(Messages.getString("SWTProcessWS.7"));
	    	NemoLists.updateParameters();
	    	
	    	monitor.subTask(Messages.getString("SWTProcessWS.8"));
	    	NemoLists.updateSurveys();
	    	
	    } 
	    else {
	    
	    if (dossier){
	    	//mise à jour dossier campagne
	    	try {
				DataModel.dirSurvey.getModel().fillFromSISMER(referenceWS);
			} catch (MalformedURLException e) {
				// 
				e.printStackTrace();
				Utils.messageErreur(Messages.getString("EditSurvey.48"));
			} catch (RemoteException e) {
				// 
				e.printStackTrace();
				Utils.messageErreur(Messages.getString("EditSurvey.49"));
			} catch (ServiceException e) {
				// 
				e.printStackTrace();
				Utils.messageErreur(Messages.getString("EditSurvey.50"));
			}
	    	
	    }else{
	    	//mise à jour fichier campagne
	    	try {
				DataModel.singleSurvey.getModel().fillFromSISMER(referenceWS);
			} catch (MalformedURLException e) {
				// 
				Utils.messageErreur(Messages.getString("EditSurvey.48"));
				e.printStackTrace();
			} catch (RemoteException e) {
				//
				e.printStackTrace();
				Utils.messageErreur(Messages.getString("EditSurvey.49"));
			} catch (ServiceException e) {
				// 
				e.printStackTrace();
				Utils.messageErreur(Messages.getString("EditSurvey.50"));
			}
	    	
	    }
	    }
		
	    monitor.done();
	 
	    
	  }
	
}
