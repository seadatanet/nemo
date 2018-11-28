package fr.ifremer.nemo.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.DateTime;

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
import fr.ifremer.sismer_tools.DateTools;

public class CDIResumeWriter {
	private static CDIResumeWriter instance;

	private String nomFichierSortie;
	private FileWriter writer;
	private final String sep;
	// keep FULL stations list here (list in station model is not complete, because must be reset for input directories)
	// WARNING: this list is also used to fill the coupling table!!
	protected HashMap<String, List<Station>> _listStations;


	private CDIResumeWriter() {
		sep = Intern.CDIResumeWriter_separateur;
		_listStations = new HashMap<String, List<Station>>();
	}

	public static CDIResumeWriter getInstance() {
		if (instance == null) {
			instance = new CDIResumeWriter();
		}
		return instance;
	}

	public void reset() {
		nomFichierSortie = Global.EMPTY_STRING;
		_listStations = new HashMap<String, List<Station>>();

	}
	public void addStation(String inputFilePath, Station st) {

		if (_listStations.containsKey(inputFilePath)) {
			_listStations.get(inputFilePath).add(st);
		} else {
			List<Station> stations = new ArrayList<Station>();
			stations.add(st);
			_listStations.put(inputFilePath, stations);
		}
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
					+ "DIST_METHODE" + sep + "STATION_END_DATE");

			InputDomain iDomain = Nemo.getInstance().getDomain();
			CruiseDomain cDomain = iDomain.getCruise();
			StationDomain sDomain = iDomain.getStation();
			DataDomain dataDomain = iDomain.getData();
			sDomain.resetlocalCDIList();


			ArrayList<String> oututFilesList = new ArrayList<>( iDomain.getMedia().getOutputFiles().keySet());
			Collections.sort(oututFilesList);
			
			for (String outputPath : oututFilesList) {

				ArrayList<String> cdiList = new ArrayList<>( iDomain.getMedia().getOutputFiles().get(outputPath));
				Collections.sort(cdiList);
				
				
				
				for (String local_cdi_id : cdiList) {

					// for (Station st :
					// sDomain.getStations(outputFile.getName())){
					//					Station st = getStationByReference(ref);
					Station st = getStationByLocalCdiId(local_cdi_id);
					// seuls les paramètres mesurés sont à prendre en compte
					for (ParameterModel param : dataDomain.getModel().getParameterModels()) {
						//						String localCDIId = sDomain.getLocalCdiId(cDomain,formatConversion, st.getReference());
						writeCDILine(st, param, formatConversion, local_cdi_id, outputPath);
						if (formatConversion == Conversion.MEDATLAS) {
							writeCDILine(st, param, Conversion.ODV, local_cdi_id, outputPath);
							writeCDILine(st, param, Conversion.NETCDF, local_cdi_id, outputPath);//25816
						}
						if (formatConversion == Conversion.ODV) {
							writeCDILine(st, param, Conversion.NETCDF, local_cdi_id, outputPath);//25816
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
	public Station getStationByReference(String ref) {
		Station selected = null;
		for (String inputFilePath : _listStations.keySet()) {
			for (Station st : _listStations.get(inputFilePath)) {
				if (ref.equals(st.getReference())) {
					selected = st;
					break;
				}
			}
		}
		return selected;
	}
	public Station getStationByLocalCdiId(String local_cdi_id) {
		Station selected = null;
		for (String inputFilePath : _listStations.keySet()) {
			for (Station st : _listStations.get(inputFilePath)) {
				if (local_cdi_id.equals(st.getLocal_cdi_id())) {
					selected = st;
					break;
				}
			}
		}
		return selected;
	}
	private void writeCDILine(Station st, ParameterModel param, Conversion formatConversion, String localCDIId, String outputPath) throws IOException, ParseException {

		InputDomain iDomain = Nemo.getInstance().getDomain();
		CruiseDomain cDomain = iDomain.getCruise();
		Configuration config = Nemo.getInstance().getConfig();
		File outputFile = new File(outputPath);
		
		writer.write(Global.RN);

		// CDI ID
		writer.write(localCDIId);
		// EDMO author
		writer.write(sep + config.getIdEdmoRespCdi());

		// Area Type
		if (iDomain.getMedia().getModel().getInputType() == E_InputType.TRAJECTORY)
			writer.write(sep + "Curve");
		else
			writer.write(sep + "Point");

		// Dataset name

		if (st.getCruiseName()==null
				||st.getCruiseName().isEmpty()
				|| st.getCruiseName().equals(
						Intern.unknown)) {
			if (st.getCruiseRef()==null||st.getCruiseRef().isEmpty()) {
				writer.write(sep
						+ Intern.CDIResumeWriter_notSpecified);
			} else {
				writer.write(sep + st.getCruiseRef());
			}
		} else {
			writer.write(sep + st.getCruiseName());
		}

		// Dataset ID

		if (st.getCruiseRef()==null||st.getCruiseRef().isEmpty()) {

			// FAE 4525 (retour 03/01/2011)
			if (iDomain.getMedia().getModel().getProcessType().equals(E_ProcessType.COLLECTION_DIR)) {
				writer.write(sep + st.getCruiseName());
			} else if (st.getCruiseRef().isEmpty()) {
				writer.write(sep + Intern.CDIResumeWriter_notSpecified);
			} else {
				writer.write(sep + st.getCruiseRef());
			}
		} else {
			writer.write(sep + st.getCruiseRef());
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
		String p02Code = Global.EMPTY_STRING;
		// si on est deja en P021, on met directement le code
		// P021
		if (iDomain.getData().getModel().getDataListType() == DataListTypeEnum.P011_VIA_P021) {
			p02Code = param.getParameter().getCode();
		} else {
			// si on est en P091 ou P011viaP091, on map code
			// P091 -> P021
			// 6542
			p02Code = VocabLists.getVocabMatch(
					Intern.mapping_subject, Intern.list_P021,
					param.getParameter().getCode(),
					Intern.NemoLists_unknown);
		}
		writer.write(sep + p02Code + Global.EMPTY_STRING);

		// Platform Type
		writer.write(sep + config.getTypePlateForme());

		// Dataset Access
		writer.write(sep + config.getAccesDonnees());

		// Cruise name
		if (
//				!cDomain.getModel().getName().isEmpty())
				!(st.getCruiseName()==null || st.getCruiseName().isEmpty())){
			writer.write(sep + st.getCruiseName());
				}
		else{
			writer.write(sep
					+ Intern.CDIResumeWriter_notSpecified);
		}
		// Station name
		writer.write(sep + st.getReference().trim());

		// station latitude
		writer.write(sep
				+ Formatter.getSingleton().formatLatitude(
						"+DD.dddddd", st.getLatitude()));

		// station longitude
		writer.write(sep
				+ Formatter.getSingleton().formatLongitude(
						"+DDD.dddddd", st.getLongitude()));

		// station date
		// 39448
		String stationDateTime;
		Formatter format = new Formatter();
		SimpleDateFormat dateFormat2 =	new SimpleDateFormat("yyyy-MM-dd'T'");
		if (st.getTime()==-1) {
			stationDateTime = dateFormat2.format(new Date(st.getDate()))+"00:00:00";
		}else {
		stationDateTime = dateFormat2.format(new Date(st.getDate()))
				+ format.formatDateTime("hh24:mm:ss",DateTimeUtils.getCorrectedTime(st.getTime(), st.getOffsetTU()));
		}
		if(Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()
				&& st.getOlderDate()!=0) {
			try {
				stationDateTime=DateTools.getUTCLongAsString(Long.valueOf(st.getOlderDate()),"yyyy-MM-dd'T'HH:mm:ss");
			}catch (Exception e) {
				// TODO: handle exception
			}
		}		
		writer.write(sep + stationDateTime);

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
			datasize = datasize / iDomain.getMedia().getOutputFiles().get(outputPath).size();
		}

		writer.write(sep
				+ Formatter.getSingleton().formatFloat("%.3f",
						datasize));

		// DIST website
		writer.write(sep + config.getCdiURL());

		// DIST methode
		writer.write(sep + config.getCdiMethod());
		
		
		// STATION_END_DATE (39448)
		// for profiles: same as station date
		if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE){
//			if (st.getYoungerDate()!=0  ) {
			if(!Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()) {
				long stDate = DateTools.getUTCDateStringAsLong(stationDateTime, "yyyy-MM-dd'T'HH:mm:ss");
				DateTime dt = new DateTime(stDate);
				DateTime added = dt.plusHours(1);
				String addedStr = DateTools.getUTCLongAsString(added.getMillis(), "yyyy-MM-dd'T'HH:mm:ss");
				writer.write(sep + addedStr);
			}else {
				
				String cruiseDateTime ;
				if (st.getYoungerDate() == st.getOlderDate()) {
					DateTime dt = new DateTime(st.getYoungerDate());
					DateTime added = dt.plusHours(1);
					cruiseDateTime = DateTools.getUTCLongAsString(added.getMillis(), "yyyy-MM-dd'T'HH:mm:ss");
				}else {
					cruiseDateTime = DateTools.getUTCLongAsString(st.getYoungerDate(), "yyyy-MM-dd'T'HH:mm:ss");
				}
				writer.write(sep +cruiseDateTime);
			}
		}else
			// for timeseries and trajectories
		{
			try{
//				System.out.println(DateTools.TIMEZONE_GMT);
			String cruiseDateTime = DateTools.getUTCLongAsString(st.getYoungerDate(), "yyyy-MM-dd'T'HH:mm:ss");
			writer.write(sep +cruiseDateTime);
			}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}	
		}

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
