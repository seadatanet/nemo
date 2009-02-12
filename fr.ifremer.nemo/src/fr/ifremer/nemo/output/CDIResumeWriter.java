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
		// choixFichierSortie();

		File output = new File(nomFichierSortie);

		FileWriter writer = null;
		
		try {
			
			writer = new FileWriter(output);

			Formatter formatter = Formatter.getSingleton();

			String sep = Messages.getString("CDIResumeWriter.separateur");
			String codeEdmoDonnees = Utils.configuration.getCodeEdmoDonnees();
			String idEdmoRespDonnees = Utils.configuration.getIdEdmoRespDonnees();
			String idEdmoRespCdi = Utils.configuration.getIdEdmoRespCdi();
			String idEdmoDistDonnees= Utils.configuration.getIdEdmoDistDonnees();
			String codeTypePlateForme = Utils.configuration.getTypePlateForme();
			String codeAccesDonnees = Utils.configuration.getAccesDonnees();
			String formatSortie = "";
			if (ViewFile.radODV.getSelection()) {
				formatSortie = Messages.getString("CDIResumeWriter.odv");
			} else {
				formatSortie = Messages.getString("CDIResumeWriter.medatlas");
			}
			String cdiUrl = Utils.configuration.getCdiURL();
			String cdiMethod = Utils.configuration.getCdiMethod();
			
			writer.write(
					"LOCAL_CDI_ID" + sep +
					"EDMO_AUTHOR" + sep +
					"AREA_TYPE" + sep +
					"DATASET_NAME" + sep +
					"DATASET_ID" + sep +
					"DATASET_REV_DATE" + sep +
					"EDMO_ORIGINATOR" + sep +
					"DATASET_ABS" + sep +
					"EDMO_CUSTODIAN" + sep +
					"PDV_CODE" + sep +
					"PLATFORM_TYPE" + sep +
					"DATASET_ACCESS" + sep +
					"CRUISE_NAME" + sep +
					"STATION_NAME" + sep +
					"STATION_LATITUDE" + sep +
					"STATION_LONGITUDE" + sep +
					"STATION_DATE" + sep +
					"EDMO_DISTRIBUTOR" + sep +
					"FORMAT" + sep +
					"DATA_SIZE" + sep +
					"DIST_WEBSITE" + sep +
					"DIST_METHODE");
			
			for (Survey survey : surveys) {
				for (Station station : survey.getStations()) {
					for (Parameter param : station.getParameters()) {
						writer.write(Global.RN);
						
						// CDI ID
						writer.write(EditConversion.getLocalCdiId(station, survey.getReference()));
//						writer.write(/*station.getReference() + "_" +*/ Utils.configuration.getDataCenter() + "_"
//								+ String.valueOf(cdiId++));
//						writer.write(Configuration.getSingleton().getIdEdmoRespCdi() + "_"
//								+ String.valueOf(cdiId++));

						// EDMO author
	 					writer.write(sep + idEdmoRespCdi);
//						writer.write(";" + Configuration.getSingleton().getIdEdmoRespCdi());

						// Area Type
                        if(ViewFile.radProfilVertical.getSelection() || ViewFile.radSerieTempo.getSelection())
                            writer.write(sep + "Point");                        
                        else
                            writer.write(sep + "Curve");
                        

						// Dataset name
                        if(survey.getName().isEmpty())
                        {    
                            if(EditConversion.getDataSetRef().isEmpty())
                            {    
                                writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified"));                                
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
                                writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified"));                                
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
						SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
						String stringDate = dateFormat.format(dateEcriture);
						writer.write(sep + stringDate);

						// Edmo originator
						writer.write(sep + codeEdmoDonnees);
//						writer.write(";"
//								+ Configuration.getSingleton().getCodeEdmoDonnees());

						// Dataset ABS
						writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified"));

						// EDMO Custodian
						writer.write(sep + idEdmoRespDonnees);
//						writer.write(";"
//								+ Configuration.getSingleton().getIdEdmoRespDonnees());
						
						// PDV code
						writer.write(sep + Utils.extraitCodeURN(NemoLists.getVocabMatch("P091", "P011", param.getCode(), Messages.getString("CDIResumeWriter.notSpecified"))) + "");

						// Platform Type
						writer.write(sep + codeTypePlateForme);

						// Dataset Access
						writer.write(sep + codeAccesDonnees);

						// Cruise name
						if (!survey.getName().isEmpty())
							writer.write(sep + survey.getName());
						else
							writer.write(sep + Messages.getString("CDIResumeWriter.notSpecified"));

						// Station name
						writer.write(sep + station.getReference().trim());
						
						// station latitude
						writer.write(sep
								+ formatter.formatLatitude("+DD.ddd ", station
										.getLatitude()));

						// station longitude
						writer.write(sep
								+ formatter.formatLongitude("+DDD.ddd", station
										.getLongitude()));

						// station date
						Formatter format = new Formatter();
						SimpleDateFormat dateFormat2 = new  SimpleDateFormat("yyyy-MM-dd'T'");
						writer.write(sep
								+ dateFormat2.format(new Date(station.getDate())) + format.formatDateTime("hh24:mm:ss", station.getCorrectedTime()));
			
						station.getOffsetTU();
						station.getParameters()[0].getStation().getTime();
						
						// edmo distributor
						writer.write(sep + idEdmoDistDonnees);
//						writer.write(";"
//								+ Configuration.getSingleton().getIdEdmoDistDonnees());
						
						// Format
						writer.write(sep + formatSortie);
						
						// data size
						writer.write(sep + Formatter.getSingleton().formatFloat("%.3f",survey.getDataSize()));

						// DIST website
						writer.write(sep + cdiUrl);
//						writer.write(";" + Configuration.getSingleton().getCdiURL());

						// DIST methode
						writer.write(sep + cdiMethod);
//						writer.write(";" + Configuration.getSingleton().getCdiMethod());
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
		dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
//		dialog.setFilterPath(Configuration.getSingleton().getCruisesDirectory());
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
    
    public List<Survey> getSurveys() {
        
        return surveys;
    }
    
    
}
