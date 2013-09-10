package fr.ifremer.nemo.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

public class CDIResumeWriter {
	private static CDIResumeWriter instance;

	// private final Nemo nemo;

	private String nomFichierSortie;

	private FileWriter writer;
	private final String sep;

	private CDIResumeWriter() {
		// nemo = Nemo.getInstance();
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

	}

	@SuppressWarnings("nls")
	public void writeResume(Conversion formatConversion) throws IOException {
		// 1 ligne par paramètres mesurés

		File output = new File(nomFichierSortie);
		writer = null;

		try {
			writer = new FileWriter(output);

			writer.write("LOCAL_CDI_ID" + sep + "EDMO_AUTHOR" + sep
					+ "AREA_TYPE" + sep + "DATASET_NAME" + sep + "DATASET_ID"
					+ sep + "DATASET_REV_DATE" + sep + "EDMO_ORIGINATOR" + sep
					+ "DATASET_ABS" + sep + "EDMO_CUSTODIAN" + sep + "P02_CODE"
					+ sep + "PLATFORM_TYPE" + sep + "DATASET_ACCESS" + sep
					+ "CRUISE_NAME" + sep + "STATION_NAME" + sep
					+ "STATION_LATITUDE" + sep + "STATION_LONGITUDE" + sep
					+ "STATION_DATE" + sep + "EDMO_DISTRIBUTOR" + sep
					+ "FORMAT" + sep + "FORMAT_VERSION" + sep + "DATA_SIZE" + sep + "DIST_WEBSITE" + sep
					+ "DIST_METHODE");

			InputDomain iDomain = Nemo.getInstance().getDomain();
			CruiseDomain cDomain = iDomain.getCruise();
			StationDomain sDomain = iDomain.getStation();
			DataDomain dataDomain = iDomain.getData();
			sDomain.resetlocalCDIList();
			Configuration config = Nemo.getInstance().getConfig();

			for (String outputPath : iDomain.getMedia().getOutputFiles()
					.keySet()) {

				for (String stRef : iDomain.getMedia().getOutputFiles().get(
						outputPath)) {
					// for (Station st :
					// sDomain.getStations(outputFile.getName())){
					Station st = sDomain.getStationByRef(stRef);
					// seuls les paramètres mesurés sont à prendre en compte
					for (ParameterModel param : dataDomain.getModel().getParameterModels()) {
						if (formatConversion == Conversion.MEDATLAS) {
							writeCDILine(st, param, formatConversion, outputPath);
							writeCDILine(st, param, Conversion.ODV, outputPath);
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// reset();
			if (writer != null)
				writer.close();
		}
	}

	private void writeCDILine(Station st, ParameterModel param, Conversion formatConversion, String outputPath) throws IOException, ParseException {

		InputDomain iDomain = Nemo.getInstance().getDomain();
		CruiseDomain cDomain = iDomain.getCruise();
		StationDomain sDomain = iDomain.getStation();
		DataDomain dataDomain = iDomain.getData();
		sDomain.resetlocalCDIList();
		Configuration config = Nemo.getInstance().getConfig();
		File outputFile = new File(outputPath);

		writer.write(Global.RN);

		// CDI ID
		writer.write(sDomain.getLocalCdiId(cDomain,
					formatConversion, st.getReference()));
		// EDMO author
		writer.write(sep + config.getIdEdmoRespCdi());

		// Area Type
		if (iDomain.getMedia().getModel().getInputType() == E_InputType.TRAJECTORY)
			writer.write(sep + "Curve");
		else
			writer.write(sep + "Point");

		// Dataset name
		if (cDomain.getModel().getName().isEmpty()
				|| cDomain.getModel().getName().equals(
						Intern.unknown)) {
			if (cDomain.getDataSetRef().isEmpty()) {
				writer.write(sep
						+ Intern.CDIResumeWriter_notSpecified);
			} else {
				writer.write(sep + cDomain.getDataSetRef());
			}
		} else {
			writer.write(sep + cDomain.getModel().getName());
		}

		// Dataset ID
		if (cDomain.getModel().getReference().isEmpty()) {

			// FAE 4525 (retour 03/01/2011)
			if (iDomain.getMedia().getModel().getProcessType().equals(E_ProcessType.COLLECTION_DIR)) {
				writer
						.write(sep + cDomain.getModel().getName());
			} else if (cDomain.getDataSetRef().isEmpty()) {
				writer.write(sep
						+ Intern.CDIResumeWriter_notSpecified);
			} else {
				writer.write(sep + cDomain.getDataSetRef());
			}
		} else {
			writer.write(sep
					+ cDomain.getModel().getReference());
		}

		// Dataset Rev Date
		Calendar calendrier = Calendar.getInstance();
		Date dateEcriture = calendrier.getTime();
		SimpleDateFormat dateFormat =
				new SimpleDateFormat("yyyy-MM-dd");
		String stringDate = dateFormat.format(dateEcriture);
		writer.write(sep + stringDate);

		// Edmo originator
		writer.write(sep + config.getCodeEdmoDonnees());

		// Dataset ABS
		writer.write(sep + Intern.CDIResumeWriter_notSpecified);

		// EDMO Custodian
		writer.write(sep + config.getIdEdmoRespDonnees());

		// P02 code: le code P021 du parametre (FAE 7308)
		String pdvCode = "";
		// si on est deja en P021, on met directement le code
		// P021
		if (iDomain.getData().getModel().getDataListType() == DataListTypeEnum.P011_VIA_P021) {
			pdvCode = param.getParameter().getCode();
		} else {
			// si on est en P091 ou P011viaP091, on map code
			// P091 -> P021
			// 6542
			pdvCode = VocabLists.getVocabMatch(
							Intern.mapping_subject, Intern.list_BODCpdv,
							param.getParameter().getCode(),
							Intern.NemoLists_unknown);
		}
		writer.write(sep + pdvCode + Global.EMPTY_STRING);

		// Platform Type
		writer.write(sep + config.getTypePlateForme());

		// Dataset Access
		writer.write(sep + config.getAccesDonnees());

		// Cruise name
		if (!cDomain.getModel().getName().isEmpty())
			writer.write(sep + cDomain.getModel().getName());
		else
			writer.write(sep
					+ Intern.CDIResumeWriter_notSpecified);

		// Station name
		writer.write(sep + st.getReference().trim());

		// station latitude
		writer.write(sep
				+ Formatter.getSingleton().formatLatitude(
						"+DD.dddddd ", st.getLatitude()));

		// station longitude
		writer.write(sep
				+ Formatter.getSingleton().formatLongitude(
						"+DDD.dddddd", st.getLongitude()));

		// station date
		Formatter format = new Formatter();
		SimpleDateFormat dateFormat2 =
				new SimpleDateFormat("yyyy-MM-dd'T'");
		writer.write(sep
				+ dateFormat2.format(new Date(st.getDate()))
				+ format.formatDateTime("hh24:mm:ss",
						DateTimeUtils.getCorrectedTime(
								st.getTime(), st.getOffsetTU())));

		// st.getOffsetTU();
		// st.getTime();

		// edmo distributor
		writer.write(sep + config.getIdEdmoDistDonnees());

		// Format
		writer.write(sep + formatConversion.getFormatName());

		// Format Version
		if (formatConversion == Conversion.ODV)
			writer.write(sep + config.getODVFormatVersion());
		else if (formatConversion == Conversion.MEDATLAS)
			writer.write(sep + config.getMedatlasVersion());
		else if (formatConversion == Conversion.NETCDF)
			writer.write(sep + config.getNetCDFVersion());
		else
			writer.write(sep);

		// data size
		float datasize = outputFile.length() / 1024f / 1024f;
		if (iDomain.getConversion().getCurrent() != E_TypeConversion.ODV_MONOSTATION && iDomain.getConversion().getCurrent() != E_TypeConversion.NETCDF_MONOSTATION) {
			datasize =
					datasize
							/ iDomain.getMedia().getOutputFiles()
									.get(outputPath).size();
		}

		writer.write(sep
				+ Formatter.getSingleton().formatFloat("%.3f",
						datasize));

		// DIST website
		writer.write(sep + config.getCdiURL());

		// DIST methode
		writer.write(sep + config.getCdiMethod());

	}

	public void choixFichierSortie() {
		nomFichierSortie = Global.EMPTY_STRING;

		Shell shell = Utils.getMainShell();
		FileDialog dialog =
				new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
		dialog.setText(Messages.CDIResumeWriter_choixFichier);
		dialog.setFilterPath(Nemo.getInstance().getConfig()
				.getCruisesDirectory());
		String extensions[] = new String[1];
		extensions[0] = Global.STAR + Global.EXT_TXT;
		dialog.setFilterExtensions(extensions);
		nomFichierSortie = dialog.open();

		if (nomFichierSortie != null) {

			File fich = new File(nomFichierSortie);
			if (fich.exists()) {
				// fichier existe
				Shell shell2 = Utils.getMainShell();

				MessageBox mb =
						new MessageBox(shell2, SWT.ICON_WARNING | SWT.YES | SWT.NO
								| SWT.APPLICATION_MODAL);

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

	/**
	 * Setter pour nomFichierSortie
	 * 
	 * @param nomFichierSortie
	 *            : nouvelle valeur pour nomFichierSortie
	 * @see CDIResumeWriter.java#nomFichierSortie
	 */
	public void setNomFichierSortie(String nomFichierSortie) {
		this.nomFichierSortie = nomFichierSortie;
	}

	public String getNomFichierSortie() {
		return nomFichierSortie;
	}

}
