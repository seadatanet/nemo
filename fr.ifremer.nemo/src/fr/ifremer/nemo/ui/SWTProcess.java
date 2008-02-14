package fr.ifremer.nemo.ui;

import java.io.File;
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
import fr.ifremer.nemo.input.SurveyMedia;
import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.util.Messages;

public class SWTProcess implements IRunnableWithProgress {
	public static ScrolledForm formEdition;

	public static Button buttonConversion;

	public static int validee, loaded;

	public static String nomFichierSortie;

	public static Shell shell;

	public static ProgressNotification notifier;

	public static Survey survey;

	public static ODVSurveyWriter writerODV;

	public static MedatlasSurveyWriter writerMed;

	public static Boolean odv, interrompu;

	public static IProgressMonitor monitor2;

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		if (ViewFile.isCollection()) {
			
			try{
			if (ViewFile.collectionCas1)
				runCollectionCas1(monitor);
			else
			
				runCollectionCasN(monitor);
			}
			catch (ParseException e)
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			}
			catch (NumberFormatException e)
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			}
			catch (StringIndexOutOfBoundsException e)
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			}
		} else {

			try {
				runCampagne(monitor);
			}
			catch (ParseException e)
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			}
			catch (NumberFormatException e)
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			}
			catch (StringIndexOutOfBoundsException e)
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			} 
			catch (GenerationException e) 
			{
				throw new InterruptedException(Messages.getString("SWTProcess.5") + e.getMessage());
			}
		}

	}

	private void runCampagne(IProgressMonitor monitor) throws NumberFormatException, ParseException, InterruptedException, GenerationException {
		monitor.beginTask(Messages.getString("SWTProcess.1"),
				IProgressMonitor.UNKNOWN);
		monitor2 = monitor;
		interrompu = false;
		SurveyMedia.interrompu = false;
		EditConversion.interrompu = false;

		// NOTIFIER
		notifier = new ProgressNotification() {
			public void progress(final int val) {
				try {
					Application.display.syncExec(new Runnable() {
						public void run() {
							monitor2.subTask(Messages.getString("SWTProcess.2")
									+ val + "%");
							if (monitor2.isCanceled()) {
								SurveyMedia.interrompu = true;

							}
						}
					});
				} catch (Exception e) {
				}
			}
		};

		// TACHE A EFFECTUER

		try {
			notifier.progress(0);
			monitor.setTaskName(Messages.getString("SWTProcess.3"));
			survey = DataModel.inputModel.convert(notifier);
		}
		catch (InterruptedException e){
			throw e;
		}
		catch (FileNotFoundException e) {
			//
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
			throw e;
		}
	 catch (NumberFormatException e) {
		throw e;
	}
	 catch (StringIndexOutOfBoundsException e) {
			throw e;
		}
		writerODV = new fr.ifremer.nemo.odv.ODVSurveyWriter();
		writerMed = new fr.ifremer.nemo.medatlas.MedatlasSurveyWriter();

		try {
			monitor2.setTaskName(Messages.getString("SWTProcess.4"));
			if (!monitor2.isCanceled()) {
				if (!odv) {
					// MEDATLAS
					writerMed.writeSurvey(survey, new java.io.File(
							EditConversion.nomFichierSortie), notifier);
				} else {
					// ODV
					writerODV.writeSurvey(survey, new java.io.File(
							EditConversion.nomFichierSortie), notifier);

				}
			} else {
				// interruption
				interrompu = true;
			}
		} catch (IOException e) {
			// 
			e.printStackTrace();
		} catch (GenerationException e) {
			// 
			e.printStackTrace();
			throw new GenerationException(e);
		}

		// nettoyage memoire
		if (odv) {
			writerODV.release();
		} else {
			writerMed.release();
		}
		if (!interrompu) {
			survey.release();
		}

		monitor.done();
	}

	private void runCollectionCas1 (IProgressMonitor monitor) throws NumberFormatException, ParseException, InterruptedException {
		monitor.beginTask(Messages.getString("SWTProcess.1"),
				IProgressMonitor.UNKNOWN);
		monitor2 = monitor;
		interrompu = false;
		SurveyMedia.interrompu = false;
		EditConversion.interrompu = false;

		int numDossier = 0;
		
		String[] dossiers = new File(ViewFile.nomRepertoire).list();

		for (String dossier : dossiers) {
			numDossier++;
			DataModel.dirSurvey.setDirectory(ViewFile.nomRepertoire + "/"
					+ dossier);

			// NOTIFIER
			notifier = new ProgressNotification() {
				public void progress(final int val) {
					try {
						Application.display.syncExec(new Runnable() {
							public void run() {
								monitor2.subTask(Messages
										.getString("SWTProcess.2")
										+ val + "%");
								if (monitor2.isCanceled()) {
									SurveyMedia.interrompu = true;

								}
							}
						});
					} catch (Exception e) {
					}
				}
			};

			// TACHE A EFFECTUER

			try {
				notifier.progress(0);
				monitor.setTaskName(Messages.getString("SWTProcess.3") + " : " + dossier + " ( " + numDossier + " / " + dossiers.length + " ) ");
				survey = DataModel.inputModel.convert(notifier);

				survey.setName(dossier);

			} catch (InterruptedException e) {
				//
				throw e;
			} 
			catch (FileNotFoundException e) {
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

			try {
				monitor2.setTaskName(Messages.getString("SWTProcess.4"));
				if (!monitor2.isCanceled()) {

					File fichierSortie = new java.io.File(
							EditConversion.nomDossierSortie + "/" + dossier
							+ ".txt");
					monitor2.setTaskName(Messages.getString("SWTProcess.4") + " : " + dossier + ".txt" + " ( " + numDossier + " / " + dossiers.length + " ) ");
					// ODV
					writerODV.writeSurvey(survey, fichierSortie, notifier);

					survey.setDataSize((float)fichierSortie.length()/(float)1000000);
					
					if (ViewFile.writeResume) {
						CDIResumeWriter.getInstance().addSurvey(survey);
					}
				} else {
					// interruption
					interrompu = true;
				}
			} catch (IOException e) {
				// 
				e.printStackTrace();
			} catch (GenerationException e) {
				// 
				e.printStackTrace();
			}

			// nettoyage memoire
			writerODV.release();

			if (!interrompu) {
				if (!ViewFile.writeResume) {
					survey.release();
				}
			}

		}
		monitor.done();

	}

	private void runCollectionCasN (IProgressMonitor monitor) throws NumberFormatException, ParseException, InterruptedException{
		monitor.beginTask(Messages.getString("SWTProcess.1"),
				IProgressMonitor.UNKNOWN);
		monitor2 = monitor;
		interrompu = false;
		SurveyMedia.interrompu = false;
		EditConversion.interrompu = false;
		
		int numFichier = 0;

		String[] fichiers = new File(ViewFile.nomRepertoire).list();

		for (String fichier : fichiers) {
			DataModel.singleSurvey.setFile(ViewFile.nomRepertoire + "/"
					+ fichier);
			
			numFichier++;

			// NOTIFIER
			notifier = new ProgressNotification() {
				public void progress(final int val) {
					try {
						Application.display.syncExec(new Runnable() {
							public void run() {
								monitor2.subTask(Messages
										.getString("SWTProcess.2")
										+ val + "%");
								if (monitor2.isCanceled()) {
									SurveyMedia.interrompu = true;

								}
							}
						});
					} catch (Exception e) {
					}
				}
			};

			// TACHE A EFFECTUER

			try {
				notifier.progress(0);
				monitor.setTaskName(Messages.getString("SWTProcess.3") + " : " + fichier + " ( " + numFichier + " / " + fichiers.length + " ) ");
				survey = DataModel.inputModel.convert(notifier);

				survey.setName("UNKNOWN");

			}catch (InterruptedException e)
			{
				throw e;
			}
			catch (FileNotFoundException e) {
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

			try {
				monitor2.setTaskName(Messages.getString("SWTProcess.4"));
				if (!monitor2.isCanceled()) {

					// ODV
					
					File fichierSortie = new java.io.File(
							EditConversion.nomDossierSortie
							+ "/"
							+ fichier.substring(0, fichier
									.lastIndexOf('.')) + ".txt");

					monitor2.setTaskName(Messages.getString("SWTProcess.4") + " : " + fichier + ".txt" + " ( " + numFichier + " / " + fichiers.length + " ) ");
					
					writerODV.writeSurvey(survey, fichierSortie,
							notifier);
					
					survey.setDataSize(fichierSortie.length());
					
					if (ViewFile.writeResume) {
						CDIResumeWriter.getInstance().addSurvey(survey);
					}
				} else {
					// interruption
					interrompu = true;
				}
			} catch (IOException e) {
				// 
				e.printStackTrace();
			} catch (GenerationException e) {
				// 
				e.printStackTrace();
			}

			// nettoyage memoire
			writerODV.release();

			if (!interrompu) {
				if (!ViewFile.writeResume) {
					survey.release();
				}
			}
		}

		monitor.done();

	}
}
