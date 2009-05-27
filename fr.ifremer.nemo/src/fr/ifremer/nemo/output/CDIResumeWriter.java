package fr.ifremer.nemo.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.input.ParameterModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.DataModel;
import fr.ifremer.nemo.ui.edit.conversion.EditConversion;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class CDIResumeWriter {

	private static CDIResumeWriter instance;

	private String nomFichierSortie;

	private List<Survey> surveys;
    
    private FileWriter writer;
    private String sep;
    
    private String codeEdmoDonnees;
    private String idEdmoRespDonnees;
    private String idEdmoRespCdi;
    private String idEdmoDistDonnees;
    private String codeTypePlateForme;
    private String codeAccesDonnees;
    private String formatSortie;
    private String cdiUrl;
    private String cdiMethod;
    

	private CDIResumeWriter() {
		surveys = new ArrayList<Survey>();
        sep = Intern.CDIResumeWriter_separateur;
	}

	public static CDIResumeWriter getInstance() {

		if (instance == null) {
			instance = new CDIResumeWriter();
		}
		return instance;
	}


	public void reset() {
        nomFichierSortie = Global.EMPTY_STRING;
		surveys = new ArrayList<Survey>();
        
        codeEdmoDonnees = Global.EMPTY_STRING;
        idEdmoRespDonnees = Global.EMPTY_STRING;
        idEdmoRespCdi = Global.EMPTY_STRING;
        idEdmoDistDonnees = Global.EMPTY_STRING;
        codeTypePlateForme = Global.EMPTY_STRING;
        codeAccesDonnees = Global.EMPTY_STRING;
        formatSortie = Global.EMPTY_STRING;
        cdiUrl = Global.EMPTY_STRING;
        cdiMethod = Global.EMPTY_STRING;
	}

	public void writeResume(Conversion formatConversion) throws IOException 
    {
		File output = new File(nomFichierSortie);
		writer = null;
		
		try {
			writer = new FileWriter(output);

			codeEdmoDonnees = Utils.configuration.getCodeEdmoDonnees();
			idEdmoRespDonnees = Utils.configuration.getIdEdmoRespDonnees();
			idEdmoRespCdi = Utils.configuration.getIdEdmoRespCdi();
			idEdmoDistDonnees= Utils.configuration.getIdEdmoDistDonnees();
			codeTypePlateForme = Utils.configuration.getTypePlateForme();
			codeAccesDonnees = Utils.configuration.getAccesDonnees();
			formatSortie = formatConversion.toString();
			cdiUrl = Utils.configuration.getCdiURL();
			cdiMethod = Utils.configuration.getCdiMethod();
			
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
                    // seuls les paramètres mesurés sont à prendre en compte
                    for (ParameterModel param : station.getParameters()) {
                        writeLineResume(survey, station, param);
                    }   
				}
			}
		} 
        catch (Exception e) {
			e.printStackTrace();
		} 
        finally {
            reset();
			if (writer != null) writer.close();
		}
	}
    
    private void writeLineResume(Survey survey, 
                                 Station station, 
                                 ParameterModel param) throws Exception{
         
        writer.write(Global.RN);
        
        // CDI ID
        writer.write(station.getLocalCdiId(survey, formatSortie));

        // EDMO author
            writer.write(sep + idEdmoRespCdi);

        // Area Type
        if (DataModel.stationModel_modele.isTrajectory()){
            writer.write(sep + "Curve"); //$NON-NLS-1$
        }
        else{
            writer.write(sep + "Point"); //$NON-NLS-1$
        }
        
        // Dataset name
        if (survey.getName().isEmpty() || survey.getName().equals(Intern.unknown)){    
            if(EditConversion.getDataSetRef().isEmpty()){    
                writer.write(sep + Intern.CDIResumeWriter_notSpecified);
            } 
            else{
                writer.write(sep + EditConversion.getDataSetRef());                                 
            }
        } 
        else{
            writer.write(sep + survey.getName());                             
        }


        // Dataset ID
        if (survey.getReference().isEmpty()){    
            if (ViewFile.collectionCas1){
                writer.write(sep + survey.getName()); 
            }
            else if (EditConversion.getDataSetRef().isEmpty()){    
                writer.write(sep + Intern.CDIResumeWriter_notSpecified);
            } 
            else{
                writer.write(sep + EditConversion.getDataSetRef());                                 
            }
        } 
        else{
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

        // Dataset ABS
        writer.write(sep + Intern.CDIResumeWriter_notSpecified);

        // EDMO Custodian
        writer.write(sep + idEdmoRespDonnees);
        
        // PDV code
        String pdvCode = Utils.extraitCodeURN(NemoLists.getVocabMatch(Intern.mapping_subject, Intern.list_BODCpdv, param.getModel().getCode(), Intern.NemoLists_unknown));
        writer.write(sep + pdvCode + Global.EMPTY_STRING);

        // Platform Type
        writer.write(sep + codeTypePlateForme);

        // Dataset Access
        writer.write(sep + codeAccesDonnees);

        // Cruise name
        if (!survey.getName().isEmpty())
            writer.write(sep + survey.getName());
        else
            writer.write(sep + Intern.CDIResumeWriter_notSpecified);

        // Station name
        writer.write(sep + station.getReference().trim());
        
        // station latitude
        writer.write(sep
                + Formatter.getSingleton().formatLatitude("+DD.ddd ", station.getLatitude())); //$NON-NLS-1$

        // station longitude
        writer.write(sep
                + Formatter.getSingleton().formatLongitude("+DDD.ddd", station.getLongitude())); //$NON-NLS-1$

        // station date
        Formatter format = new Formatter();
        SimpleDateFormat dateFormat2 = new  SimpleDateFormat("yyyy-MM-dd'T'"); //$NON-NLS-1$
        writer.write(sep
                + dateFormat2.format(new Date(station.getDate())) + format.formatDateTime("hh24:mm:ss", station.getCorrectedTime())); //$NON-NLS-1$

        station.getOffsetTU();
        station.getTime();
        
        // edmo distributor
        writer.write(sep + idEdmoDistDonnees);
        
        // Format
        writer.write(sep + formatSortie);
        
        // data size
        float datasize = survey.getDataSize();
        if (datasize == 0){
            datasize = station.getDataSize();
        }
        else{
            datasize = datasize / survey.getStations().length;
        }
        writer.write(sep + Formatter.getSingleton().formatFloat("%.3f",datasize)); //$NON-NLS-1$

        // DIST website
        writer.write(sep + cdiUrl);

        // DIST methode
        writer.write(sep + cdiMethod);        
    }

	public void choixFichierSortie() {
		nomFichierSortie = Global.EMPTY_STRING;

		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		FileDialog dialog = new FileDialog(shell, SWT.SAVE
				| SWT.APPLICATION_MODAL);
		dialog.setText(Messages.CDIResumeWriter_choixFichier);
		dialog.setFilterPath(Utils.configuration.getCruisesDirectory());
		String extensions[] = new String[1];
		extensions[0] = Global.STAR + Global.EXT_TXT;
		dialog.setFilterExtensions(extensions);
		nomFichierSortie = dialog.open();

		if (nomFichierSortie != null) {

			// System.out.println("Fichier = " + nomFichierSortie);
			File fich = new File(nomFichierSortie);
			if (fich.exists()) {
				// fichier existe
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
