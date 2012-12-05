package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.UpdateBean;
import fr.ifremer.nemo.config.UpdateBean.EtatUpdate;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;
import fr.ifremer.nemo.wsVocab.VocabStub;

public class SWTProcessWS implements IRunnableWithProgress
{

	public static Boolean dossier, fichiersConstantes;

	public static IProgressMonitor monitor2;

	public static String referenceWS;

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
	{
		monitor.beginTask(Messages.update_title, IProgressMonitor.UNKNOWN);
		monitor2 = monitor;

		if (fichiersConstantes)
		{
			// mise e jour fichiers constantes
			NemoLists.resetUpdatedLists();

			if (!monitor.isCanceled())
			{
				// appel au WebService NEMO
				monitor.subTask(Messages.connect_nemo);
				NemoWS service = NemoLists.callNemoWS();
				UpdateBean updateNemo = NemoLists.getUpdate(Intern.id_nemo);
				updateNemo.setNbTot(Integer.parseInt(Intern.nbnemolists));

				if (!monitor.isCanceled())
				{
					monitor.subTask(Messages.update_param);
					NemoLists.updateParameters(monitor, service, updateNemo);
				}

				if (!monitor.isCanceled())
				{
					monitor.subTask(Messages.update_survey);
					NemoLists.updateSurveys(monitor, service, updateNemo);
				}

				updateNemo.setNbRetrieve();

				if (monitor.isCanceled())
				{
					updateNemo.setEtat(EtatUpdate.INTERRUPTED);
					throw new InterruptedException();
				}
			}

			if (!monitor.isCanceled())
			{
				monitor.subTask(Messages.connect_vocab);
				// VocabSoapBindingStub vocabService = NemoLists.connectVocab();
				VocabStub vocabService = NemoLists.connectVocabAxis2();

				if (!monitor.isCanceled())
				{
					monitor.subTask(Messages.getString(Intern.id_lists));
					NemoLists.updateVocabListsAxis2(vocabService, monitor);
				}

				if (!monitor.isCanceled())
				{
					monitor.subTask(Messages.getString(Intern.id_maps));
					NemoLists.updateVocabMaps(vocabService, monitor);
				}

				NemoLists.updateDataTypes();
			}

			if (!monitor.isCanceled())
			{
				monitor.subTask(Messages.connect_edmo);
				NemoLists.updateEdmo(monitor);
			}

			Nemo.getInstance().getConfig().save();

			if (monitor.isCanceled())
			{
				throw new InterruptedException();
			}

		} else
		{

			// mise à jour onglet campagne (si clic sur bouton
			// "server initialisation" dans l'onglet cruise)
			try
			{
				CruiseModel cruiseModel = new CruiseModel();
				cruiseModel.fillFromSISMER(referenceWS);
				Nemo.getInstance().getDomain().getCruise().updateCruise(cruiseModel);

			} catch (MalformedURLException e)
			{
				//
				// Utils.messageErreur(Messages.error_updateSurvey_url);
				// e.printStackTrace();
				throw new InterruptedException(e.getMessage());
			} catch (RemoteException e)
			{
				//
				throw new InterruptedException(e.getMessage());
				// e.printStackTrace();
				// Utils.messageErreur(Messages.error_updateSurvey_distant);
			} catch (ServiceException e)
			{
				//
				// e.printStackTrace();
				throw new InterruptedException(e.getMessage());
				// Utils.messageErreur(Messages.error_updateSurvey_service);
			}

			/*
			 * if (dossier) {
			 * // mise e jour dossier campagne
			 * try {
			 * Application.nemo.getDomain().getCruise().getModel().fillFromSISMER
			 * (referenceWS);
			 * } catch (MalformedURLException e) {
			 * //
			 * e.printStackTrace();
			 * Utils.messageErreur(Messages.error_updateSurvey_url);
			 * } catch (RemoteException e) {
			 * //
			 * e.printStackTrace();
			 * Utils.messageErreur(Messages.error_updateSurvey_distant);
			 * } catch (ServiceException e) {
			 * //
			 * e.printStackTrace();
			 * Utils.messageErreur(Messages.error_updateSurvey_service);
			 * }
			 * 
			 * }
			 * else {
			 * // mise e jour fichier campagne
			 * try {
			 * Application.nemo.getDomain().getCruise().getModel().fillFromSISMER
			 * (
			 * referenceWS);
			 * } catch (MalformedURLException e) {
			 * //
			 * Utils.messageErreur(Messages.error_updateSurvey_url);
			 * e.printStackTrace();
			 * } catch (RemoteException e) {
			 * //
			 * e.printStackTrace();
			 * Utils.messageErreur(Messages.error_updateSurvey_distant);
			 * } catch (ServiceException e) {
			 * //
			 * e.printStackTrace();
			 * Utils.messageErreur(Messages.error_updateSurvey_service);
			 * }
			 * 
			 * }
			 */
		}

		monitor.done();
	}
}
