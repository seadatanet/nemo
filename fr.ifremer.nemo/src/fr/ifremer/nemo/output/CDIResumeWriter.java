package fr.ifremer.nemo.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Messages;

public class CDIResumeWriter {

	private static CDIResumeWriter instance;

	private String nomFichierSortie;

	private List<Survey> surveys;

	public CDIResumeWriter() {

		surveys = new ArrayList<Survey>();
	}

	public static CDIResumeWriter getInstance() {

		if (instance == null) {
			instance = new CDIResumeWriter();
		}

		return instance;

	}

	public void reset() {
		if (surveys != null)
			for (Survey survey : surveys)
				survey.release();
		
		surveys = new ArrayList<Survey>();
	}

	public void writeResume() throws IOException {
		choixFichierSortie();

		File output = new File(nomFichierSortie);

		FileWriter writer = null;

		try {

			writer = new FileWriter(output);

			Formatter formatter = Formatter.getSingleton();

			int cdiId = Integer.parseInt(ViewFile.txtCompteurCDI.getText());

			writer.write("CDI ID;" +
					"EDMO AUTHOR;" +
					"AREA TYPE;" +
					"DATASET NAME;" +
					"DATASET ID;" +
					"DATASET REV DATE;" +
					"EDMO ORIGINATOR;" +
					"DATASET ABS;" +
					"EDMO CUSTODIAN;" +
					"PDV CODE;" +
					"PLATFORM TYPE;" +
					"DATASET ACCESS;" +
					"CRUISE NAME;" +
					"STATION NAME;" +
					"STATION LATITUDE;" +
					"STATION LONGITUDE;" +
					"STATION DATE;" +
					"EDMO DISTRIBUTOR;" +
					"DATA SIZE;" +
					"DIST WEBSITE;" +
					"DIST METHODE");
			
			for (Survey survey : surveys) {
				for (Station station : survey.getStations()) {
					
					writer.write("\r\n");
					
					// CDI ID
//					writer.write(Utils.configuration.getDataCenter() + "_"
//							+ String.valueOf(cdiId++));
					writer.write(Configuration.getSingleton().getIdEdmoRespCdi() + "_"
							+ String.valueOf(cdiId++));

					// EDMO author
// 					writer.write(";" + Utils.configuration.getIdEdmoRespCdi());
					writer.write(";" + Configuration.getSingleton().getIdEdmoRespCdi());

					// Area Type
					writer.write(";" + "Point");

					// Dataset name
					writer.write(";" + "Not Specified");

					// Dataset ID
					writer.write(";" + "Not Specified");

					// Dataset Rev Date
					Calendar calendrier = Calendar.getInstance();
					Date dateEcriture = calendrier.getTime();
					SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy:MM:dd'T'HH:mm:ss");
					String stringDate = dateFormat.format(dateEcriture);
					writer.write(";" + stringDate);

					// Edmo originator
//					writer.write(";"
//									+ Utils.configuration.getCodeEdmoDonnees());
					writer.write(";"
							+ Configuration.getSingleton().getCodeEdmoDonnees());

					// Dataset ABS
					writer.write(";" + "Not Specified");

					// EDMO Custodian
//					writer.write(";"
//							+ Utils.configuration.getIdEdmoRespDonnees());
					writer.write(";"
							+ Configuration.getSingleton().getIdEdmoRespDonnees());
					
					// PDV code
					writer.write(";" + "");

					// Platform Type
					writer.write(";" + ViewFile.comboTypePlateForme.getText());

					// Dataset Access
					writer.write(";" + ViewFile.comboAccesDonnees.getText());

					// Cruise name
					if (ViewFile.collectionCas1)
						writer.write(";" + survey.getName());
					else
						writer.write(";" + "Not specified");

					// Station name
					writer.write(";" + station.getReference().trim());
					
					// station latitude
					writer.write(";"
							+ formatter.formatLatitude("+DD.ddd ", station
									.getLatitude()));

					// station longitude
					writer.write(";"
							+ formatter.formatLongitude("+DDD.ddd", station
									.getLongitude()));

					// station date
					Formatter format = new Formatter();
					SimpleDateFormat dateFormat2 = new  SimpleDateFormat("yyyy:MM:dd'T'");
					writer.write(";"
							+ dateFormat2.format(new Date(station.getDate())) + format.formatDateTime("hh24:mm:ss", station.getCorrectedTime()));
		
					station.getOffsetTU();
					station.getParameters()[0].getStation().getTime();
					// edmo distributor
//					writer.write(";"
//							+ Utils.configuration.getIdEdmoDistDonnees());
					writer.write(";"
							+ Configuration.getSingleton().getIdEdmoDistDonnees());
					
					// data size
					writer.write(";" + String.format("%.3f",survey.getDataSize()));

					// DIST website
//					writer.write(";" + Utils.configuration.getCdiURL());
					writer.write(";" + Configuration.getSingleton().getCdiURL());

					// DIST methode
//					writer.write(";" + Utils.configuration.getCdiMethod());
					writer.write(";" + Configuration.getSingleton().getCdiMethod());

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			for (Survey survey : surveys)
				survey.release();
			writer.close();

		}

	}

	public void choixFichierSortie() {
		nomFichierSortie = "";

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("CDIResumeWriter.choixFichier")); //$NON-NLS-1$
//		dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
		dialog.setFilterPath(Configuration.getSingleton().getCruisesDirectory());
		String extensions[] = new String[1];
		extensions[0] = "*.csv"; //$NON-NLS-1$
		dialog.setFilterExtensions(extensions);
		nomFichierSortie = dialog.open();

		if (nomFichierSortie != null) {

			// System.out.println("Fichier = " + nomFichierSortie);
			File fich = new File(nomFichierSortie);
			if (fich.exists()) {
				// fichier existe
			//	shell.close();
				Shell shell2 = PlatformUI.getWorkbench().getDisplay().getActiveShell();

				MessageBox mb = new MessageBox(shell2, SWT.ICON_WARNING
						| SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);

				mb.setMessage(Messages.getString("EditConversion.14")); //$NON-NLS-1$

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixFichierSortie();
				} else {
					// on écrase le fichier
					Utils.messageErreur("");
				}
			} else {
				// le fichier n'existe pas, on le cree
				Utils.messageErreur("");
			}
		}
	}

	public void addSurvey(Survey survey) {

		surveys.add(survey);

	}
}
