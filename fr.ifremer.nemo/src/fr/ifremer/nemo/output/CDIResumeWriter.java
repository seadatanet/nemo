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

import fr.ifremer.nemo.config.NemoLists;
import fr.ifremer.nemo.domain.Parameter;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.ui.EditConversion;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.ViewFile;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
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
//		if (surveys != null)
//			for (Survey survey : surveys)
//				survey.release();
        nomFichierSortie = Global.EMPTY_STRING;
		surveys = new ArrayList<Survey>();
	}

	public void writeResume() throws IOException {

		File output = new File(nomFichierSortie);

		FileWriter writer = null;
		
		try {
			
			writer = new FileWriter(output);

			Formatter formatter = Formatter.getSingleton();

			String sep = Messages.getString("CDIResumeWriter.separateur"); //$NON-NLS-1$
			String codeEdmoDonnees = Utils.configuration.getCodeEdmoDonnees();
			String idEdmoRespDonnees = Utils.configuration.getIdEdmoRespDonnees();
			String idEdmoRespCdi = Utils.configuration.getIdEdmoRespCdi();
			String idEdmoDistDonnees= Utils.configuration.getIdEdmoDistDonnees();
			String codeTypePlateForme = Utils.configuration.getTypePlateForme();
			String codeAccesDonnees = Utils.configuration.getAccesDonnees();
			String formatSortie = Global.EMPTY_STRING;
			if (ViewFile.radODV.getSelection()) {
				formatSortie = Messages.getString("CDIResumeWriter.odv"); //$NON-NLS-1$
			} else {
				formatSortie = Messages.getString("CDIResumeWriter.medatlas"); //$NON-NLS-1$
			}
			String cdiUrl = Utils.configuration.getCdiURL();
			String cdiMethod = Utils.configuration.getCdiMethod();
			
			writer.write(
					"LOCAL_CDI_ID" + sep + //$NON-NLS-1$
					"EDMO_AUTHOR" + sep + //$NON-NLS-1$
					"AREA_TYPE" + sep + //$NON-NLS-1$
					"DATASET_NAME" + sep + //$NON-NLS-1$
					"DATASET_ID" + sep + //$NON-NLS-1$
					"DATASET_REV_DATE" + sep + //$NON-NLS-1$
					"EDMO_ORIGINATOR" + sep + //$NON-NLS-1$
					"DATASET_ABS" + sep + //$NON-NLS-1$
					"EDMO_CUSTODIAN" + sep + //$NON-NLS-1$
					"PDV_CODE" + sep + //$NON-NLS-1$
					"PLATFORM_TYPE" + sep + //$NON-NLS-1$
					"DATASET_ACCESS" + sep + //$NON-NLS-1$
					"CRUISE_NAME" + sep + //$NON-NLS-1$
					"STATION_NAME" + sep + //$NON-NLS-1$
					"STATION_LATITUDE" + sep + //$NON-NLS-1$
					"STATION_LONGITUDE" + sep + //$NON-NLS-1$
					"STATION_DATE" + sep + //$NON-NLS-1$
					"EDMO_DISTRIBUTOR" + sep + //$NON-NLS-1$
					"FORMAT" + sep + //$NON-NLS-1$
					"DATA_SIZE" + sep + //$NON-NLS-1$
					"DIST_WEBSITE" + sep + //$NON-NLS-1$
					"DIST_METHODE"); //$NON-NLS-1$
			
			for (Survey survey : surveys) {
				for (Station station : survey.getStations()) {
					for (Parameter param : station.getParameters()) {
						writer.write(Global.RN);
						
						// CDI ID
						writer.write(EditConversion.getLocalCdiId(station, survey.getReference()));


						// EDMO author
	 					writer.write(sep + idEdmoRespCdi);

						// Area Type
                        if(ViewFile.radProfilVertical.getSelection() || ViewFile.radSerieTempo.getSelection())
                            writer.write(sep + "Point");                         //$NON-NLS-1$
                        else
                            writer.write(sep + "Curve"); //$NON-NLS-1$
                        

						// Dataset name
                        if(survey.getName().isEmpty() || survey.getName().equals("UNKNOWN")) //$NON-NLS-1$
                        {    
                            if(EditConversion.getDataSetRef().isEmpty())
                            {    
                                writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified"));                                 //$NON-NLS-1$
                            } else 
                            {
                                writer.write(sep + EditConversion.getDataSetRef());                                 
                            }
                        
                        } else
                        {
                            writer.write(sep + survey.getName());                             
                        }


						// Dataset ID
                        if(survey.getReference().isEmpty())
                        {    
                            if(EditConversion.getDataSetRef().isEmpty())
                            {    
                                writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified"));                                 //$NON-NLS-1$
                            } else 
                            {
                                writer.write(sep + EditConversion.getDataSetRef());                                 
                            }
                        
                        } else
                        {
                            writer.write(sep + survey.getReference());                             
                        }                        

						// Dataset Rev Date
						Calendar calendrier = Calendar.getInstance();
						Date dateEcriture = calendrier.getTime();
						SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss"); //$NON-NLS-1$
						String stringDate = dateFormat.format(dateEcriture);
						writer.write(sep + stringDate);

						// Edmo originator
						writer.write(sep + codeEdmoDonnees);
//						writer.write(";"
//								+ Configuration.getSingleton().getCodeEdmoDonnees());

						// Dataset ABS
						writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified")); //$NON-NLS-1$

						// EDMO Custodian
						writer.write(sep + idEdmoRespDonnees);
						
						// PDV code
						writer.write(sep + Utils.extraitCodeURN(NemoLists.getVocabMatch("P091", "P021", param.getCode(), Intern.NemoLists_unknown)) + Global.EMPTY_STRING);   //$NON-NLS-1$//$NON-NLS-2$

						// Platform Type
						writer.write(sep + codeTypePlateForme);

						// Dataset Access
						writer.write(sep + codeAccesDonnees);

						// Cruise name
						if (!survey.getName().isEmpty())
							writer.write(sep + survey.getName());
						else
							writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified")); //$NON-NLS-1$

						// Station name
						writer.write(sep + station.getReference().trim());
						
						// station latitude
						writer.write(sep
								+ formatter.formatLatitude("+DD.ddd ", station //$NON-NLS-1$
										.getLatitude()));

						// station longitude
						writer.write(sep
								+ formatter.formatLongitude("+DDD.ddd", station //$NON-NLS-1$
										.getLongitude()));

						// station date
						Formatter format = new Formatter();
						SimpleDateFormat dateFormat2 = new  SimpleDateFormat("yyyy-MM-dd'T'"); //$NON-NLS-1$
						writer.write(sep
								+ dateFormat2.format(new Date(station.getDate())) + format.formatDateTime("hh24:mm:ss", station.getCorrectedTime())); //$NON-NLS-1$
			
						station.getOffsetTU();
						station.getParameters()[0].getStation().getTime();
						
						// edmo distributor
						writer.write(sep + idEdmoDistDonnees);
						
						// Format
						writer.write(sep + formatSortie);
						
						// data size
                        float datasize = survey.getDataSize();
                        if (datasize == 0){
                            datasize = station.getDataSize();
                        }else{
                            datasize = datasize / survey.getStations().length;
                        }
						writer.write(sep + Formatter.getSingleton().formatFloat("%.3f",datasize)); //$NON-NLS-1$

						// DIST website
						writer.write(sep + cdiUrl);

						// DIST methode
						writer.write(sep + cdiMethod);
					}	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			for (Survey survey : surveys)
//				survey.release();
            reset();
			if (writer != null) writer.close();

		}

	}

	public void choixFichierSortie() {
		nomFichierSortie = Global.EMPTY_STRING;

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.getString("CDIResumeWriter.choixFichier")); //$NON-NLS-1$
		dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
		String extensions[] = new String[1];
		extensions[0] = "*.txt"; //$NON-NLS-1$
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

				mb.setMessage(Messages.confirm_eraseFile);

				int reponse = mb.open();
				if (reponse == SWT.NO) {
					// on n'écrase pas le fichier
					choixFichierSortie();
				} else {
					// on écrase le fichier
					Utils.messageErreur(Global.EMPTY_STRING);
				}
			} else {
				// le fichier n'existe pas, on le cree
				Utils.messageErreur(Global.EMPTY_STRING);
			}
		}
	}

	public void addSurvey(Survey survey) {

		surveys.add(survey);

	}
    
   
    
}
