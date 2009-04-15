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
import fr.ifremer.nemo.config.UpdateBean;
import fr.ifremer.nemo.config.UpdateBean.EtatUpdate;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;
import fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub;

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
		monitor.beginTask(Messages.update_title, IProgressMonitor.UNKNOWN);

		monitor2 = monitor;
		
		// TACHES A EFFECTUER

		if (fichiersConstantes) {
            
            NemoLists.resetUpdatedLists();
            
            // mise e jour fichiers constantes
            //int nbListesOK = 0;

            if (!monitor.isCanceled()){
                // appel au WebService NEMO
                monitor.subTask(Messages.connect_nemo); 
                NemoWS service = NemoLists.callNemoWS();
                UpdateBean updateNemo = NemoLists.getUpdate(Intern.id_nemo);
                updateNemo.setNbTot(Integer.parseInt(Intern.nbnemolists));
                
//                if (!monitor.isCanceled()){
//                    monitor.subTask(Messages.update_data);
//                    NemoLists.updateDataTypes(monitor, service, updateNemo);
//                }
                
                if (!monitor.isCanceled()){
                    monitor.subTask(Messages.update_param); 
                    NemoLists.updateParameters(monitor, service, updateNemo);
                }
                
                if (!monitor.isCanceled()){
                    monitor.subTask(Messages.update_survey);
                    NemoLists.updateSurveys(monitor, service, updateNemo);
                }
                
                updateNemo.setNbRetrieve();

                if (monitor.isCanceled()){
                    updateNemo.setEtat(EtatUpdate.INTERRUPTED);
                    throw new InterruptedException();
                }
            }
            

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.connect_vocab); 
                VocabSoapBindingStub vocabService = NemoLists.connectVocab();
                
                if (!monitor.isCanceled()){
                    monitor.subTask(Messages.getString(Intern.id_lists)); 
                    NemoLists.updateVocabLists(vocabService, monitor);
                }
                
                if (!monitor.isCanceled()){
                    monitor.subTask(Messages.getString(Intern.id_maps)); 
                    NemoLists.updateVocabMaps(vocabService, monitor);
                }		
                
                NemoLists.updateDataTypes();
            }

            if (!monitor.isCanceled()){
                monitor.subTask(Messages.connect_edmo); 
                NemoLists.updateEdmo(monitor);
            }
            
            Utils.configuration.save();
            
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
