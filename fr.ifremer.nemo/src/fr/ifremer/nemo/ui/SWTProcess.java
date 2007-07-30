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
import fr.ifremer.nemo.input.SurveyMedia;
import fr.ifremer.nemo.medatlas.MedatlasSurveyWriter;
import fr.ifremer.nemo.odv.ODVSurveyWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.ProgressNotification;

public class SWTProcess implements IRunnableWithProgress {
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

	public static Boolean odv, interrompu;

	public static IProgressMonitor monitor2;

	// public static

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
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

}
