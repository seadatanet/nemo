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
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;

public class SWTProcessWS implements IRunnableWithProgress {
	public static ScrolledForm formEdition;

	public static Button buttonConversion;

	public static int validee, loaded;

	public static String nomFichierSortie;

	public static Shell shell;

	public static ProgressNotification notifier;

	public static Survey survey;

	public static ODVSurveyWriter writerODV;

	public static MedatlasSurveyWriter writerMed;

	public static Boolean odv, dossier, fichiersConstantes;

	public static IProgressMonitor monitor2;

	public static String referenceWS;

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(Messages.getString("SWTProcessWS.1"), //$NON-NLS-1$
				IProgressMonitor.UNKNOWN);

		monitor2 = monitor;
		
		// TACHES A EFFECTUER

		if (fichiersConstantes) {
            
            // mise e jour fichiers constantes
            
            // appel au WebService NEMO
            monitor.subTask("WebService NEMO"); //$NON-NLS-1$
            NemoWS service = NemoLists.callNemoWS();
            
            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.3")); //$NON-NLS-1$
                NemoLists.updateDataTypes(monitor, service);
            }

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.7")); //$NON-NLS-1$
                NemoLists.updateParameters(monitor, service);
            }

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.8")); //$NON-NLS-1$
                NemoLists.updateSurveys(monitor, service);
            }
            

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.9")); //$NON-NLS-1$
                // FAE 0001132 : precision du nom de la liste dans le message
                NemoLists.updateVocabLists(monitor);
            }
            

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.11")); //$NON-NLS-1$
                NemoLists.updateVocabMaps(monitor);
            }			

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.getString("SWTProcessWS.10")); //$NON-NLS-1$
                NemoLists.updateEdmo(monitor);
            }
            
            if (monitor.isCanceled()){
                throw new InterruptedException();
            }
            
		} else {

			if (dossier) {
				// mise e jour dossier campagne
				try {
					DataModel.dirSurvey.getModel().fillFromSISMER(referenceWS);
				} catch (MalformedURLException e) {
					// 
					e.printStackTrace();
					Utils.messageErreur(Messages.getString("EditSurvey.48")); //$NON-NLS-1$
				} catch (RemoteException e) {
					// 
					e.printStackTrace();
					Utils.messageErreur(Messages.getString("EditSurvey.49")); //$NON-NLS-1$
				} catch (ServiceException e) {
					// 
					e.printStackTrace();
					Utils.messageErreur(Messages.getString("EditSurvey.50")); //$NON-NLS-1$
				}

			} else {
				// mise e jour fichier campagne
				try {
					DataModel.singleSurvey.getModel().fillFromSISMER(
							referenceWS);
				} catch (MalformedURLException e) {
					// 
					Utils.messageErreur(Messages.getString("EditSurvey.48")); //$NON-NLS-1$
					e.printStackTrace();
				} catch (RemoteException e) {
					//
					e.printStackTrace();
					Utils.messageErreur(Messages.getString("EditSurvey.49")); //$NON-NLS-1$
				} catch (ServiceException e) {
					// 
					e.printStackTrace();
					Utils.messageErreur(Messages.getString("EditSurvey.50")); //$NON-NLS-1$
				}

			}
		}

		monitor.done();

	}

}
