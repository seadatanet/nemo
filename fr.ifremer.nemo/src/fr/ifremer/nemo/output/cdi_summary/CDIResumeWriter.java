package fr.ifremer.nemo.output.cdi_summary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.joda.time.DateTime;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.DataDomain;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.MediaDomain;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.output.cdi_summary.curve.Curve;
import fr.ifremer.nemo.output.cdi_summary.curve.CurveCreator;
import fr.ifremer.nemo.output.cdi_summary.curve.LatLon;
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
import fr.ifremer.sismer_tools.csr.CSRListManager;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class CDIResumeWriter {
	private static CDIResumeWriter instance;

	// This suffix is used to name the EDMERP file, ex: "CDI_SUMMARY_EDMERP.txt"
	public static String EDMERP_FILE_SUFFIX = "_EDMERP";
	// This suffix is used to name the CURVE file, ex: "CDI_SUMMARY_CURVE.txt"
	public static String CURVE_FILE_SUFFIX = "_CURVE";
	public static String CURVE_GPX_FILE_SUFFIX = "_CURVE_GPX";
	// String fileNameWithOutExt = FilenameUtils.removeExtension(fileNameWithExt);


	private String nomFichierSortie;
	private OutputStreamWriter resumeWriter;
	private final String sep;

	private String nomFicherEdmerp;
	private List<String> edmerpCodesList;
	private String edmerpCodesString;
	private Map<String, String> mapEdmerp;
	private String nomFicherCurve;
	private Map<String, List<LatLon>> mapLatLon;
	public DecimalFormat latLonDecimalFormat ;


	// keep FULL stations list here (list in station model is not complete, because must be reset for input directories)
	// WARNING: this list is also used to fill the coupling table!!
	protected HashMap<String, List<Station>> _listStations;

	private Set<String> lineSet;

	private CDIResumeWriter() {
		sep = Intern.CDIResumeWriter_separateur;
		_listStations = new HashMap<String, List<Station>>();

		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator('.');
		latLonDecimalFormat = new DecimalFormat("###.0000", decimalFormatSymbols);
	}

	public static CDIResumeWriter getInstance() {
		if (instance == null) {
			instance = new CDIResumeWriter();
		}
		return instance;
	}

	public void reset() {
		//		nomFichierSortie = Global.EMPTY_STRING;
		setNomFichierSortie(Global.EMPTY_STRING);
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
		OutputStream outputStream = new FileOutputStream(output);

		InputDomain iDomain = Nemo.getInstance().getDomain();

		edmerpCodesList = new ArrayList<>();
		mapEdmerp = new TreeMap<String, String>();

		mapLatLon = new HashMap<String, List<LatLon>>();

		lineSet = new HashSet<String>();

		MediaDomain mediaDomain = iDomain.getMedia();
		CruiseDomain cDomain = iDomain.getCruise();
		StationDomain sDomain = iDomain.getStation();
		DataDomain dataDomain = iDomain.getData();

		try {
			// UTF8 encoding
			resumeWriter = new OutputStreamWriter(outputStream, Global.NEMO_CDISUM_ENCODING);

			E_InputType inputType = iDomain.getMedia().getModel().getInputType();

			// EDMERP code list
			edmerpCodesString = cDomain.getModel().getEdmerpCodes();
			if (!(edmerpCodesString.isEmpty())) {

				edmerpCodesList = Arrays.asList(edmerpCodesString.split(Global.SEMICOLON));
			}

			String header = "LOCAL_CDI_ID" + sep + "EDMO_AUTHOR" + sep + "AREA_TYPE" + sep + "DATASET_NAME" + sep
					+ "DATASET_ID" + sep + "DATASET_REV_DATE" + sep + "EDMO_ORIGINATOR" + sep + "DATASET_ABS" + sep
					+ "EDMO_CUSTODIAN" + sep + "P02_CODE" + sep + "L05_CODE" + sep + "PLATFORM_TYPE" + sep;

			if (edmerpCodesList.size() <= 1) {
				header = header + "EDMERP_CODE" + sep;
			}

			header = header + "DATASET_ACCESS" + sep + "CRUISE_NAME" + sep + "CRUISE_ALT_NAME" + sep + "CRUISE_START_DATE" + sep
					+ "STATION_NAME" + sep;



			if (inputType == E_InputType.TRAJECTORY) {

				header = header + "SOUTH_LATITUDE" + sep + "NORTH_LATITUDE" + sep + "WEST_LONGITUDE" + sep + "EAST_LONGITUDE" + sep;
			}
			else {

				header = header + "STATION_LATITUDE" + sep + "STATION_LONGITUDE" + sep;
			}

			header = header + "STATION_DATE" + sep + "SAMPLING_RATE" + sep + "SAMPLING_RATE_UNIT" + sep
					+ "STATION_START_DATE" + sep + "STATION_END_DATE" + sep;

			if (inputType == E_InputType.TRAJECTORY || inputType == E_InputType.TIMESERIES || inputType == E_InputType.PROFILE) {
				header = header + "MIN_DEPTH" + sep + "MAX_DEPTH" + sep;
			}

			if (inputType != E_InputType.TRAJECTORY) {
				header = header + "BOTTOM_DEPTH" + sep;
			}

			header = header + "EDMO_DISTRIBUTOR" + sep + "FORMAT" + sep + "FORMAT_VERSION" + sep + "DATA_SIZE" + sep
					+ "DIST_WEBSITE" + sep + "DIST_METHODE" + sep + "L22_CODE" + sep + "CENTRAL_CSR_REFERENCE";

			resumeWriter.write(header);

			sDomain.resetlocalCDIList();

			ArrayList<String> oututFilesList = new ArrayList<>( mediaDomain.getOutputFiles().keySet());
			Collections.sort(oututFilesList);

			for (String outputPath : oututFilesList) {

				ArrayList<String> cdiList = new ArrayList<>( mediaDomain.getOutputFiles().get(outputPath));
				Collections.sort(cdiList);

				for (String local_cdi_id : cdiList) {

					// store the local_cdi_id
					mapEdmerp.put(local_cdi_id, edmerpCodesString);

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

					// Get latLonList map if traj
					if (inputType == E_InputType.TRAJECTORY) {
						List<Station> stationList = getStationListByLocalCdiId(local_cdi_id);
						List<LatLon> latLonList = new ArrayList<>();
						for (Station station : stationList) {
							latLonList.addAll(station.getLatLonList());
						}
						mapLatLon.put(local_cdi_id, latLonList);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// reset();
			if (resumeWriter != null) {

				resumeWriter.flush();
				resumeWriter.close();
			}

			// empty the line collection.
			lineSet.clear();
		}


		// Write the LOCAL_CDI_ID / EDMERP file
		writeEdmerpFileIfNeeded(mapEdmerp);

		// Write cruve file if trajectory
		if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TRAJECTORY) {
			writeCurveFile(mapLatLon);
		}
	}

	/**
	 * Creates and writes the additional EDMERP file.
	 * 
	 * This file is a CSV containing pairs localCdiId/EDMERP code.
	 * 
	 * If the file already exists, it is deleted (in case no EDMERP file is needed.)
	 * 
	 * @param edmerpCodesString the map used to fill the file.
	 * @throws IOException
	 */
	private void writeEdmerpFileIfNeeded(Map<String, String> mapEdmerp) throws IOException {
		File output = new File(nomFicherEdmerp);

		// delete file if exists
		if (output.exists()) {
			output.delete();
		}

		// this file is created only if there's multiple EDMERP per localCdiId. 
		if (edmerpCodesList.size() > 1) {

			OutputStreamWriter edmerpWriter = null;
			try {
				OutputStream outputStream = new FileOutputStream(output);
				edmerpWriter = new OutputStreamWriter(outputStream, Global.NEMO_EDMERP_ENCODING);

				String header = "LOCAL_CDI_ID" + sep + "EDMERP_CODE";
				edmerpWriter.write(header);

				// Write a line for each couple LOCAL_CDI_ID / EDMERP
				for (String key : mapEdmerp.keySet()) {

					writeListOfEdmerp(key, edmerpCodesList, edmerpWriter);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// reset();
				if (edmerpWriter != null) {
					edmerpWriter.flush();
					edmerpWriter.close();
				}
			}
		}
	}

	/**
	 * Write a list of couples localCdiId/EDMERP.
	 * 
	 * @param localcdiId The localCdiId.
	 * @param edmerpList the list of EDMERP codes for the given localCdiId.
	 * @param writer The FileWriter used.
	 * @throws IOException
	 */
	private void writeListOfEdmerp(String localcdiId, List<String> edmerpList, OutputStreamWriter writer) throws IOException {

		if (!(edmerpList.isEmpty())) {

			for (String edmerpCode : edmerpList) {
				writer.write(Global.RN);
				writer.write(localcdiId + sep + edmerpCode);
			}
		}
	}

	private void writeCurveFile(Map<String, List<LatLon>> latLonListMap) throws IOException {
		File output = new File(nomFicherCurve);
		OutputStreamWriter curveWriter = null;
		List<List<Curve>> curveListofList = new ArrayList<List<Curve>>();
		try {
			OutputStream outputStream = new FileOutputStream(output);
			curveWriter = new OutputStreamWriter(outputStream, Global.NEMO_CURVE_ENCODING);

			String header = "LOCAL_CDI_ID" + sep + "CURVES_DESCRIPTION" + sep + "CURVES_NAME" + sep + "CURVES_COORDINATES";
			curveWriter.write(header);

			for (Entry<String, List<LatLon>> set : latLonListMap.entrySet()) {

				String local_cdi_id = set.getKey();
				List<LatLon> latLonList = set.getValue();
				List<Curve> curveList = CurveCreator.compuiteCurvesFromLatLonList(latLonList);
				writeListOfCurve(local_cdi_id, curveList, curveWriter);
				curveListofList.add(curveList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// reset();
			if (curveWriter != null) {
				curveWriter.flush();
				curveWriter.close();
			}
		}

		// Write GPX file for test
		writeGPXCurveFile(curveListofList);
	}

	private void writeListOfCurve(String localcdiId, List<Curve> curveList, OutputStreamWriter writer) throws IOException {

		if (!(curveList.isEmpty())) {
			for (Curve curve : curveList) {
				writer.write(Global.RN);
				writer.write(localcdiId + sep + curve.getName() + sep + curve.getDescription() + sep + curve.getLatLonString());
			}
		}
	}

	public void writeGPXCurveFile(List<List<Curve>> curveListofList) throws IOException {
		File output = new File(getGPXCurveFilename(nomFichierSortie));
		
		// ouvrir un fichier en écriture
		OutputStream outputStream = new FileOutputStream(output);
		// UTF8 encoding
		OutputStreamWriter gpxCurveWriter = new OutputStreamWriter(outputStream, Global.NEMO_CURVE_ENCODING);

		// Ecrire les en-têtes du fichier GPX
		gpxCurveWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		gpxCurveWriter.write("<gpx version=\"1.1\" creator=\"NEMO\">\n");

		// Ecrire chaque liste de coordonnées comme un segment de piste
		int latLonInc = 0;
		for (List<Curve> curveList : curveListofList) {
			for (Curve curve : curveList) {
				gpxCurveWriter.write("<rte>");
				gpxCurveWriter.write("<name>"+curve.getName()+"</name>\n");
				//			for (LatLon coordinate : curve.getLatLonList()) {
				for (int i = 0; i <curve.getLatLonList().size(); i++) {
					LatLon latlon = curve.getLatLonList().get(i);
					latLonInc++;
					// inversion lon/lat pour comparer avec fichier genere par stefane
					//					writer.write("<rtept lat=\"" + coordinate.getLat() + "\" lon=\"" + coordinate.getLon() + "\"><name>"+latLonInc+"</name></rtept>\n");
					gpxCurveWriter.write("<rtept lon=\"" + latLonDecimalFormat.format(latlon.getLon()) + "\" lat=\"" + latLonDecimalFormat.format(latlon.getLat()) + "\"><name>"+latLonInc+"</name></rtept>\n");
				}
				gpxCurveWriter.write("</rte>\n"); 
			}
		}

		// écrire la balise de fermeture du fichier GPX
		gpxCurveWriter.write("</gpx>");

		// fermer le fichier
		gpxCurveWriter.flush();
		gpxCurveWriter.close();
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

	public List<Station> getStationListByLocalCdiId(String local_cdi_id) {
		List<Station> stationList = new ArrayList<Station>();
		for (String inputFilePath : _listStations.keySet()) {
			for (Station st : _listStations.get(inputFilePath)) {
				if (local_cdi_id.equals(st.getLocal_cdi_id())) {
					stationList.add(st);
				}
			}
		}
		return stationList;
	}

	private void writeCDILine(Station st, ParameterModel param, Conversion formatConversion, String localCDIId, String outputPath) throws Exception {

		InputDomain iDomain = Nemo.getInstance().getDomain();
		CruiseDomain cDomain = iDomain.getCruise();
		Configuration config = Nemo.getInstance().getConfig();
		File outputFile = new File(outputPath);

		String lineToWrite = "";

		E_InputType inputType = iDomain.getMedia().getModel().getInputType();

		SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");

		lineToWrite += Global.RN;

		// CDI ID
		lineToWrite += localCDIId;
		// EDMO author
		lineToWrite += sep + Nemo.getInstance().getConfig().getIdEdmoRespCdi();

		// Area Type
		if (inputType == E_InputType.TRAJECTORY) {
			lineToWrite += sep + "Curve";
		} else {
			lineToWrite += sep + "Point";
		}

		// Dataset name

		if (st.getCruiseName()==null
				||st.getCruiseName().isEmpty()
				|| st.getCruiseName().equals(
						Intern.unknown)) {
			if (st.getCruiseRef()==null||st.getCruiseRef().isEmpty()) {
				lineToWrite += sep
						+ Intern.CDIResumeWriter_notSpecified;
			} else {
				lineToWrite += sep + st.getCruiseRef();
			}
		} else {
			lineToWrite += sep + st.getCruiseName();
		}

		// Dataset ID

		if (st.getCruiseRef()==null||st.getCruiseRef().isEmpty()) {

			// FAE 4525 (retour 03/01/2011)
			if (iDomain.getMedia().getModel().getProcessType().equals(E_ProcessType.COLLECTION_DIR)) {
				lineToWrite += sep + st.getCruiseName();
			} else if (st.getCruiseRef().isEmpty()) {
				lineToWrite += sep + Intern.CDIResumeWriter_notSpecified;
			} else {
				lineToWrite += sep + st.getCruiseRef();
			}
		} else {
			lineToWrite += sep + st.getCruiseRef();
		}
		// Dataset Rev Date
		Calendar calendrier = Calendar.getInstance();
		Date dateEcriture = calendrier.getTime();
		String stringDate = dateOnlyFormat.format(dateEcriture);
		lineToWrite += sep + stringDate;

		// Edmo originator
		lineToWrite += sep + config.getCodeEdmoDonnees();

		// Dataset ABS
		String datasetAbs = cDomain.getModel().getDatasetAbstractString();

		if (datasetAbs == null || Global.EMPTY_STRING.equals(datasetAbs)) {
			datasetAbs = Intern.CDIResumeWriter_notSpecified;
		}

		lineToWrite += sep + fr.ifremer.nemo.util.Utils.replaceTabsAndNewLines(datasetAbs);

		// EDMO Custodian
		lineToWrite += sep + config.getIdEdmoRespDonnees();

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
			p02Code = ExternalResourcesManager.getInstance().getSdnVocabularyManager().getVocabMatch(
					Intern.mapping_subject, Intern.list_P021,
					param.getParameter().getCode()
					);
		}
		lineToWrite += sep + p02Code + Global.EMPTY_STRING;

		// L05_CODE
		String l05Code = Global.EMPTY_STRING;
		try {
			// get L05 from C77

			String C77_code = iDomain.getStation().getModel().getDataType().getCode();		

			l05Code = ExternalResourcesManager.getInstance().getSdnVocabularyManager()
					.getBestVocabMatchCode(SdnVocabularyManager.LIST_L05, SdnVocabularyManager.LIST_C77,
							C77_code);

		} catch (Exception e) {
			// L05 stays empty.
		}

		lineToWrite += sep + l05Code;


		// Platform Type
		lineToWrite += sep + config.getTypePlateForme();

		// EDMERP_CODE
		if (edmerpCodesList.size() <= 1) {
			lineToWrite += sep + edmerpCodesString;
		}

		//		if (mapEdmerp.containsKey(localCDIId) && mapEdmerp.get(localCDIId).size() == 1) {
		//			
		//			lineToWrite += sep + mapEdmerp.get(localCDIId).get(0);
		//		}
		//		else {
		//			// EDMERP column empty: no EDMERP code, or multiple EDMERP codes.
		//			lineToWrite += sep;
		//		}

		// Dataset Access
		lineToWrite += sep + config.getAccesDonnees();

		// Cruise name
		String cruiseName = Global.EMPTY_STRING;

		if (!(st.getCruiseName() == null || st.getCruiseName().isEmpty())) {
			cruiseName = st.getCruiseName();
		} else {
			cruiseName = Intern.CDIResumeWriter_notSpecified;
		}
		
		lineToWrite += sep + cruiseName;

		// CRUISE_ALT_NAME
		String localCsrIdentifier = iDomain.getCruise().getModel().getLocalCsrIdentifier().trim();
		String bshCsrReference = iDomain.getCruise().getModel().getBshCsrReference().trim();

		// defaults to cruise name.
		String cruiseAltName = cruiseName;

		if (!localCsrIdentifier.isEmpty()) {
			cruiseAltName = localCsrIdentifier;
		}
		else if (!bshCsrReference.isEmpty()) {
			cruiseAltName = bshCsrReference;
		}
		lineToWrite += sep + cruiseAltName;


		// CRUISE_START_DATE
		String cruiseStartDateAsString = Global.EMPTY_STRING;
		long cruiseStartDate;
		cruiseStartDate = iDomain.getCruise().getModel().getStartDate();

		if (cruiseStartDate != CruiseModel.CRUISE_START_DATE_DEFAULT) {

			cruiseStartDateAsString = dateOnlyFormat.format(new Date(cruiseStartDate));
		}

		lineToWrite += sep + cruiseStartDateAsString;

		// Station name
		lineToWrite += sep + st.getReference().trim();

		// GEO COORDINATES
		if (inputType == E_InputType.TRAJECTORY) {

			// SOUTH_LATITUDE / NORTH_LATITUDE
			lineToWrite += sep + Formatter.getSingleton().formatLatitude("+DD.dddddd", st.getSouthlatitude()) + sep
					+ Formatter.getSingleton().formatLatitude("+DD.dddddd", st.getNorthlatitude());

			// WEST_LONGITUDE / EAST_LONGITUDE
			lineToWrite += sep + Formatter.getSingleton().formatLongitude("+DDD.dddddd", st.getWestlongitude()) + sep
					+ Formatter.getSingleton().formatLongitude("+DDD.dddddd", st.getEastlongitude());
		}
		else {

			// STATION_LATITUDE
			lineToWrite += sep + Formatter.getSingleton().formatLatitude(
					"+DD.dddddd", st.getLatitude());

			// STATION_LONGITUDE
			lineToWrite += sep + Formatter.getSingleton().formatLongitude(
					"+DDD.dddddd", st.getLongitude());
		}

		// station date
		// 39448
		String stationDate;
		Formatter format = new Formatter();
		//		SimpleDateFormat dateFormat2 =	new SimpleDateFormat("yyyy-MM-dd'T'");
		SimpleDateFormat dateFormat2 =	new SimpleDateFormat("yyyy-MM-dd");

		stationDate = dateFormat2.format(new Date(st.getDate()));
		lineToWrite += sep + stationDate;

		// SAMPLING_RATE
		String samplingRate = Global.EMPTY_STRING;
		String samplingRateUnit = Global.EMPTY_STRING;
		float floatSR = st.getSamplingRate();

		if (!(Float.isNaN(floatSR)) && floatSR != 0.) {

			samplingRate = Float.toString(st.getSamplingRate());
			samplingRateUnit = "UTBB";
		}
		lineToWrite += sep + samplingRate;

		// SAMPLING_RATE_UNIT
		lineToWrite += sep + samplingRateUnit;

		// STATION_START_DATE
		String stationStartDateTime = Global.EMPTY_STRING;
		if (st.getTime() == -1) {
			stationStartDateTime = dateFormat2.format(new Date(st.getDate())) + "T00:00:00";
		} else {
			stationStartDateTime = dateFormat2.format(new Date(st.getDate())) + "T" + format.formatDateTime("hh24:mm:ss",
					DateTimeUtils.getCorrectedTime(st.getTime(), st.getOffsetTU()));
		}
		if (Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()
				&& st.getOlderDate() != 0) {
			try {
				stationStartDateTime = DateTools.getUTCLongAsString(Long.valueOf(st.getOlderDate()),
						"yyyy-MM-dd'T'HH:mm:ss");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		lineToWrite += sep + stationStartDateTime;


		// STATION_END_DATE (39448)
		// for profiles: same as station date
		if (iDomain.getMedia().getModel().getInputType() == E_InputType.PROFILE) {

			// FIXME Duplicate old STATION_DATE process to keep stationDateTime
			// variable.
			String stationDateTime;
			SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd'T'");
			if (st.getTime() == -1) {
				stationDateTime = dateFormat3.format(new Date(st.getDate())) + "00:00:00";
			} else {
				stationDateTime = dateFormat3.format(new Date(st.getDate())) + format.formatDateTime("hh24:mm:ss",
						DateTimeUtils.getCorrectedTime(st.getTime(), st.getOffsetTU()));
			}
			if (Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()
					&& st.getOlderDate() != 0) {
				try {
					stationDateTime = DateTools.getUTCLongAsString(Long.valueOf(st.getOlderDate()),
							"yyyy-MM-dd'T'HH:mm:ss");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			//			if (st.getYoungerDate()!=0  ) {
			if (!Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()) {
				long stDate = DateTools.getUTCDateStringAsLong(stationDateTime, "yyyy-MM-dd'T'HH:mm:ss");
				DateTime dt = new DateTime(stDate);
				DateTime added = dt.plusHours(1);
				String addedStr = DateTools.getUTCLongAsString(added.getMillis(), "yyyy-MM-dd'T'HH:mm:ss");
				lineToWrite += sep + addedStr;
			} else {

				String cruiseDateTime;
				if (st.getYoungerDate() == st.getOlderDate()) {
					DateTime dt = new DateTime(st.getYoungerDate());
					DateTime added = dt.plusHours(1);
					cruiseDateTime = DateTools.getUTCLongAsString(added.getMillis(), "yyyy-MM-dd'T'HH:mm:ss");
				} else {
					cruiseDateTime = DateTools.getUTCLongAsString(st.getYoungerDate(), "yyyy-MM-dd'T'HH:mm:ss");
				}
				lineToWrite += sep + cruiseDateTime;
			}
		} else
			// for timeseries and trajectories
		{
			try {
				// System.out.println(DateTools.TIMEZONE_GMT);
				String cruiseDateTime = DateTools.getUTCLongAsString(st.getYoungerDate(), "yyyy-MM-dd'T'HH:mm:ss");
				lineToWrite += sep + cruiseDateTime;
			} catch (Exception e) {
				Nemo.getInstance().getLogger().error(e.getMessage());
			}
		}

		if (inputType == E_InputType.TRAJECTORY || inputType == E_InputType.TIMESERIES || inputType == E_InputType.PROFILE) {

			// MIN_DEPTH
			lineToWrite += sep + st.getMinDepth();

			// MAX_DEPTH
			lineToWrite += sep + st.getMaxDepth();
		}


		if (inputType != E_InputType.TRAJECTORY) {
			// BOTTOM_DEPTH
			String bottomDepthToDisplay = Global.EMPTY_STRING;
			if (Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DEPTH).isUserProvided()) {
				bottomDepthToDisplay = Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DEPTH).getUserValue();
			}
			else {
				// get bottom_depth from station
				float endDepth = st.getEnddepth();
				if (!(Float.isNaN(endDepth))) {

					bottomDepthToDisplay = Float.toString(st.getEnddepth());
				}
			}
			lineToWrite += sep + bottomDepthToDisplay;
		}

		// edmo distributor
		lineToWrite += sep + config.getIdEdmoDistDonnees();

		// Format
		lineToWrite += sep + formatConversion.getFormatName();

		// Format Version
		if (formatConversion == Conversion.ODV) {
			lineToWrite += sep + config.getODVFormatVersion();
		} else if (formatConversion == Conversion.MEDATLAS) {
			lineToWrite += sep + config.getMedatlasVersion();
		} else if (formatConversion == Conversion.NETCDF) {
			lineToWrite += sep + config.getNetCDFVersion();
		} else if (formatConversion == Conversion.BODV) {
			lineToWrite += sep + config.getBodvVersion();
		} else if (formatConversion == Conversion.MODV) {
			lineToWrite += sep + config.getModvVersion();
		} else if (formatConversion == Conversion.SODV) {
			lineToWrite += sep + config.getSodvVersion();
		} else {
			lineToWrite += sep;
		}

		// data size
		float datasize = outputFile.length() / 1024f / 1024f;
		if (iDomain.getConversion().getCurrent() != E_TypeConversion.ODV_MONOSTATION && iDomain.getConversion().getCurrent() != E_TypeConversion.NETCDF_MONOSTATION) {
			datasize = datasize / iDomain.getMedia().getOutputFiles().get(outputPath).size();
		}

		lineToWrite += sep + Formatter.getSingleton().formatFloat("%.3f",
				datasize);

		// DIST website
		lineToWrite += sep + config.getCdiURL();

		// DIST methode
		lineToWrite += sep + config.getCdiMethod();

		// L22_CODE
		String instrumentCode = param.getParameter().getInstrumentCode();
		lineToWrite += sep + instrumentCode;

		// CENTRAL_CSR_REFERENCE
		if (bshCsrReference.isEmpty() && !(localCsrIdentifier.isEmpty())) {

			String idEdmoRespCsr = Nemo.getInstance().getConfig().getIdEdmoRespCsr();

			if (idEdmoRespCsr==null||idEdmoRespCsr.equals("0")) {
				throw new Exception(Messages.edit_convert_cruiseLocalCsrId_unableToCreateMissingCSREdmoId);
			}

			CSRListManager csrMgr = CSRListManager.getInstance();

			bshCsrReference = csrMgr.getCentralRefFromEdmoAndLocalId(idEdmoRespCsr, localCsrIdentifier);
			if (bshCsrReference == null) {
				bshCsrReference = Global.EMPTY_STRING;
			}
		}

		lineToWrite += sep + bshCsrReference;

		// avoid duplicates
		if (lineSet.add(lineToWrite)) {

			// new line.
			resumeWriter.write(lineToWrite);
		}
	}


	public void choixFichierSortie() {
		setNomFichierSortie(Global.EMPTY_STRING);
		//		nomFichierSortie = Global.EMPTY_STRING;

		Shell shell = Utils.getMainShell();
		FileDialog dialog =
				new FileDialog(shell, SWT.SAVE | SWT.APPLICATION_MODAL);
		dialog.setText(Messages.CDIResumeWriter_choixFichier);
		dialog.setFilterPath(Nemo.getInstance().getConfig()
				.getCruisesDirectory());
		String extensions[] = new String[1];
		extensions[0] = Global.STAR + Global.EXT_TXT;
		dialog.setFilterExtensions(extensions);
		//		nomFichierSortie = dialog.open();
		setNomFichierSortie(dialog.open());



		if (nomFichierSortie != null) {

			// il y a maintenant plusieurs fichiers de sortie (EDMERP, ...) ! Il faut vérifier leur existence !
			//			nomFicherEdmerp = getEdmerpFilename(nomFichierSortie);
			//			nomFicherCurve = getCurveFilename(nomFichierSortie);

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

		// Set CDI summary file names
		if (this.nomFichierSortie != null && this.nomFichierSortie != Global.EMPTY_STRING) {

			this.nomFicherEdmerp = getEdmerpFilename(this.nomFichierSortie);
			this.nomFicherCurve = getCurveFilename(this.nomFichierSortie);
		}
		else {
			this.nomFicherEdmerp = null;
			this.nomFicherCurve = null;
		}
	}

	public String getNomFichierSortie() {
		return nomFichierSortie;
	}

	/**
	 * Generate a filename adding a suffix in a given filename.
	 * Ex: filename = "toto.txt", suffix = "_old" => output = "toto_old.txt".
	 * 
	 * @param filename
	 * @param suffix
	 * @return the filename with the given suffix in it.
	 */
	private String getFileNameWithSuffix(String filename, String suffix) {
		String fileNameWithOutExt = FilenameUtils.removeExtension(filename);

		String extension = FilenameUtils.getExtension(filename);

		return fileNameWithOutExt + suffix + FilenameUtils.EXTENSION_SEPARATOR_STR + extension;
	}

	private String getEdmerpFilename(String filename) {

		return getFileNameWithSuffix(filename, EDMERP_FILE_SUFFIX);
	}

	private String getCurveFilename(String filename) {

		return getFileNameWithSuffix(filename, CURVE_FILE_SUFFIX);
	}
	private String getGPXCurveFilename(String filename) {
		String fileNameWithOutExt = FilenameUtils.removeExtension(filename);
		String extension = "gpx";
		return fileNameWithOutExt + CURVE_GPX_FILE_SUFFIX + FilenameUtils.EXTENSION_SEPARATOR_STR + extension;
	}
}
