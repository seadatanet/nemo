package fr.ifremer.nemo.ui;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.config.UpdateStatus.EtatUpdate;
import fr.ifremer.nemo.config.UpdateStatusList;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.ws.NemoWS;

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
			ManageLists.resetUpdatedLists();

			if (!monitor.isCanceled())
			{
				// appel au WebService NEMO
				monitor.subTask(Messages.connect_nemo);
				NemoWS service = NemoLists.callNemoWS();
				UpdateStatusList updateNemo = (UpdateStatusList) ManageLists.getUpdate(Intern.id_nemo);

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

				if (!monitor.isCanceled())
				{
					monitor.subTask(Messages.getString(Intern.id_lists));
					VocabLists.updateVocabLists(monitor);
				}

				if (!monitor.isCanceled())
				{
					monitor.subTask(Messages.getString(Intern.id_maps));
					VocabLists.updateMapLists(monitor);
				}

				ManageLists.updateDataTypes();
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
