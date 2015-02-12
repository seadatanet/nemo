/*
 * ============================================================================
 * Nom du fichier : StationDomain.java
 * Application : NEMO version 1.2.20
 * Auteur : Delphine BOULANGER
 * Date de crÃ©ation : 30 nov. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.InputModel;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.StationModel;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.medatlas.Elts;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;

/**
 * Class StationDomain.java Description: classe pour la description des données
 * de l'onglet station
 * 
 * @author Delphine BOULANGER
 */
public class StationDomain {
	/**
	 * Informations descrivant l'onglet Station
	 */
	protected StationModel _model;
	final private Formatter form;

	private Station _currentStation;

	protected HashMap<String, List<Station>> _listStations;

	protected boolean _someInData;

	protected int _ref = -1;

	protected boolean _computed;

	protected HashSet<String> stationsLocalCDIList;

	public StationDomain() {
		form = Formatter.getSingleton();
		initValues();
	}

	public StationDomain(InputModel model) {
		form = Formatter.getSingleton();
		initValues();
		update(model);
	}

	public void resetCurrent() {
		_ref = -1;
		// _currentStationValues = new HashMap<String, Object>();
		// _currentStationMetaFlags = new HashMap<String, String>();
	}

	public void initValues() {
		_model = new StationModel();
		_listStations = new HashMap<String, List<Station>>();
		_ref = -1;
		_someInData = false;
		stationsLocalCDIList = new HashSet<String>();
		// _currentStationValues = new HashMap<String, Object>();
		// _currentStationMetaFlags = new HashMap<String, String>();
	}

	public void update(InputModel model) {
		updateStation(model.getStationModel());

	}

	public void updateStation(StationModel model) {
		_model = model;
		_listStations = new HashMap<String, List<Station>>();
		updateRef();
		_someInData = false;
		for (PropertyModel pModel : _model.getProperties()) {
			if (pModel.isInData()) {
				_someInData = true;
				break;
			}
		}
	}

	public void updateRef() {
		if (_model.getReferenceProperty() != null
				&& _model.getReferenceProperty().isUserProvided()
				&& !_model.getReferenceProperty().getUserValue().trim()
						.equals(Global.EMPTY_STRING))
			_ref = Integer.parseInt(_model.getReferenceProperty()
					.getUserValue());
		else
			_ref = -1;
	}

	@SuppressWarnings("nls")
	public StringBuffer getOdvStationHeaderLabels() {
		StringBuffer odvStationHeader = new StringBuffer();
		odvStationHeader.append("Cruise");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("Station");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("Type");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("yyyy-mm-ddThh:mm:ss.sss");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("Longitude [degrees_east]");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("Latitude [degrees_north]");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("LOCAL_CDI_ID");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("EDMO_code");
		odvStationHeader.append(Global.TAB);
		odvStationHeader.append("Bot. Depth [m]");
		odvStationHeader.append(Global.TAB);
		return odvStationHeader;
	}

	public StringBuffer getOdvEmptyStationHeader() {
		StringBuffer odvStationHeader = new StringBuffer();
		for (int i = 0; i < 9; i++) {
			odvStationHeader.append(Global.TAB);
		}
		return odvStationHeader;
	}

	public StringBuffer getOdvTrajectoryHeader(Station currentStation,
			String inputLine, int indexRecord) throws ParseException,
			UnsupportedEncodingException {
		_currentStation = currentStation;

		StringBuffer odvStationHeader = new StringBuffer();
		String stationRef = currentStation.getReference();
		// Cruise
		String cruiseName =Nemo.getInstance().getDomain().getCruise().getModel().getReference();
		odvStationHeader.append(form.limitString(cruiseName, 20));
		odvStationHeader.append(Global.TAB);

		// Station
		odvStationHeader.append(form.limitString(getRef(true), 20));
		odvStationHeader.append(Global.TAB);

		// Type
		odvStationHeader.append(getTypeBC(stationRef));
		odvStationHeader.append(Global.TAB);

		// yyyy-mm-ddThh:mm:ss.sss
		long currentDate = DateTimeUtils.getCurrentDate(currentStation,
				indexRecord);
		odvStationHeader.append(DateTimeUtils.getVariantDateTime(currentDate));
		odvStationHeader.append(Global.TAB);

		// Longitude [degrees_east]
		odvStationHeader.append(form.formatLongitude("+DDD.dddddd",
				(Double) _model.getProperty(Intern.S_LON)
						.applyOnLine(inputLine)));
		odvStationHeader.append(Global.TAB);

		// Latitude [degrees_north]
		odvStationHeader.append(form.formatLatitude("+DD.dddddd ",
				(Double) _model.getProperty(Intern.S_LAT)
						.applyOnLine(inputLine)));
		odvStationHeader.append(Global.TAB);

		// LOCAL_CDI_ID
		odvStationHeader.append(getLocalCdiId(Nemo.getInstance().getDomain()
				.getCruise(), Conversion.ODV, stationRef));
		odvStationHeader.append(Global.TAB);

		// EDMO_CODE
		odvStationHeader.append(Nemo.getInstance().getConfig()
				.getIdEdmoRespCdi());
		odvStationHeader.append(Global.TAB);

		Object botDepth;
		// Bot. Depth [m]
		// FAE 4466 => trajectory depth non renseigné
		// FAE 4615
		if (_model.getProperty(Intern.S_DEPTH).getFragment() == null
				&& Nemo.getInstance().getDomain().getMedia().getModel()
						.getInputType() == E_InputType.TRAJECTORY)
			botDepth = Float.NaN;
		else
			botDepth = _model.getProperty(Intern.S_DEPTH)
					.applyOnLine(inputLine);

		// 6998
		// if (botDepth.equals("Default"))
		// botDepth = 0f;
		if ((!botDepth.equals("Default")) && !((Float) botDepth).isNaN())
		// if (!((Float) botDepth).isNaN())
		{
			odvStationHeader.append(botDepth);
		} else {

			// odvStationHeader.append(Global.ZERO);
			odvStationHeader.append(Global.SPACE);
		}

		odvStationHeader.append(Global.TAB);

		return odvStationHeader;
	}

	public String getReference(String inputFileName,
			String[] inputStationHeader, boolean mustIncrement)
			throws ParseException {
		// STATION NUMBER
		String ref = Global.EMPTY_STRING;
		if (_model.getReferenceProperty().isUserProvided()) {
			if (Nemo.getInstance().getDomain().getMedia().getModel()
					.getInputType() == E_InputType.TRAJECTORY) {
				ref = _model.getReferenceProperty().getUserValue();
			} else {
				ref = getRef(mustIncrement);
			}
		} else if (_model.getReferenceProperty().getFragment().getFirstLine() == 0) {
			// dans le nom du fichier
			ref = (String) _model.getReferenceProperty().applyOn(
					new String[] { inputFileName });
		} else {
			// dans le Header
			ref = (String) _model.getReferenceProperty().applyOn(
					inputStationHeader);
		}
		return ref;
	}

	private String getRef(boolean mustIncrement) {
		// incrementation du numÃ©ro
		String ref = Integer.toString(_ref);
		if (mustIncrement) {
			_ref++;
		}
		return ref;
	}

	public void incrementRef() {
		// incrementation du numÃ©ro
		_ref++;
	}

	public StringBuffer getHeader(Conversion c, Station currentStation,
			String[] inputStationHeader) throws ParseException,
			GenerationException {

		_currentStation = currentStation;

		StringBuffer stationHeader = new StringBuffer();
		switch (c) {
		case ODV:
			stationHeader = getOdvHeader(currentStation, inputStationHeader);
			break;
		case MEDATLAS:
			stationHeader = getMedatlasHeader(currentStation,
					inputStationHeader);
			break;
		}
		return stationHeader;
	}

	@SuppressWarnings("nls")
	private StringBuffer getOdvHeader(Station currentStation,
			String[] inputStationHeader) throws ParseException,
			GenerationException {
		String errorMessage = "";
		try {
			StringBuffer odvStationHeader = new StringBuffer();
			String stationRef = currentStation.getReference();
			// Cruise
			String cruiseName = Global.EMPTY_STRING;

			// 6399
			// en ODV plus d'onglet cruise donc
			// .getCruise().getModel().getName() ==
			// "" on met la référence
			errorMessage = "cruise reference";
			cruiseName = Nemo.getInstance().getDomain().getCruise().getModel().getReference();

			odvStationHeader.append(form.limitString(cruiseName, 20));
			odvStationHeader.append(Global.TAB);

			// Station
			errorMessage = "station reference";
			if (Nemo.getInstance().getDomain().getMedia().getModel()
					.getInputType() == E_InputType.TRAJECTORY) {
				if (Nemo.getInstance().getDomain().getConversion().getCurrent() != E_TypeConversion.ODV_MULTISTATIONS
						|| Nemo.getInstance().getDomain().getConversion()
								.getCurrent() != E_TypeConversion.NETCDF_MULTISTATIONS)
					_ref = 1;
				odvStationHeader.append(form.limitString(getRef(true), 20));
			} else {
				odvStationHeader.append(form.limitString(stationRef, 20));
			}
			odvStationHeader.append(Global.TAB);

			// Type
			errorMessage = "type bc";
			odvStationHeader.append(getTypeBC(stationRef));
			odvStationHeader.append(Global.TAB);

			// yyyy-mm-ddThh:mm:ss.sss
			errorMessage = "date time";
			odvStationHeader.append(DateTimeUtils.getDateTime(currentStation));
			odvStationHeader.append(Global.TAB);

			// Longitude [degrees_east]
			errorMessage = "longitude";
			odvStationHeader.append(form.formatLongitude("+DDD.dddddd",
					currentStation.getLongitude()));
			odvStationHeader.append(Global.TAB);

			// Latitude [degrees_north]
			errorMessage = "latitude";
			odvStationHeader.append(form.formatLatitude("+DD.dddddd ",
					currentStation.getLatitude()));
			odvStationHeader.append(Global.TAB);

			// LOCAL_CDI_ID
			errorMessage = "LOCAL_CDI_ID";
			odvStationHeader.append(getLocalCdiId(Nemo.getInstance()
					.getDomain().getCruise(), Conversion.ODV, stationRef));
			odvStationHeader.append(Global.TAB);

			// EDMO_CODE
			errorMessage = "EDMO_CODE";
			odvStationHeader.append(Nemo.getInstance().getConfig()
					.getIdEdmoRespCdi());
			odvStationHeader.append(Global.TAB);

			// Bot. Depth [m]
			errorMessage = Intern.S_DEPTH;
			Object botDepth = _model.getProperty(Intern.S_DEPTH).applyOn(
					inputStationHeader);

			// 6998
			// if (botDepth.equals("Default"))
			// botDepth = 0f;
			if ((!botDepth.equals("Default")) && !((Float) botDepth).isNaN()) {
				odvStationHeader.append(botDepth);
			} else {
				// odvStationHeader.append(Global.ZERO);
				odvStationHeader.append(Global.SPACE);
			}
			odvStationHeader.append(Global.TAB);

			return odvStationHeader;
		} catch (Exception e) {
			throw new GenerationException(errorMessage);
		}
	}

	@SuppressWarnings("nls")
	private String getTypeBC(String ref) {
		String typeBC = "*";
		if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.PROFILE) {
			if (Nemo.getInstance().getDomain().getData().getModel()
					.getMesureType() == E_MesureType.BSSURF) {
				DataType dataType = _model.getDataType();
				if (dataType != null && dataType.getCode() != null
						&& dataType.getCode().equals("H09")) {
					typeBC = "B";
				} else if (dataType != null
						&& dataType.getCode() != null
						&& (dataType.getCode().equals("H10") || dataType
								.getCode().equals("H13"))) {
					typeBC = "C";
				} else {
					int nbRecords = getStationByRef(ref).getNbRecords();
					if (nbRecords < 250) {
						typeBC = "B";
					} else {
						typeBC = "C";
					}
				}
			}
		} else {
			typeBC = Global.STAR;
		}
		return typeBC;
	}

	public String getLocalCdiId(CruiseDomain cDomain, Conversion c, String ref)
			throws ParseException, UnsupportedEncodingException {
		// FAE 1143 : nommage du LOCAL_CDI_ID
		String localCDIId = "";
		String cruiseRef = Global.EMPTY_STRING;
		String stationReference = ref.trim();

		switch (c) {
		case NETCDF:
		case ODV:
			// ODV -> [Reference du jeu de donnees]
			// if
			// (Nemo.getInstance().getDomain().getMedia().getModel().getProcessType()
			// == E_ProcessType.COLLECTION_FILES)
			// cruiseRef = cDomain.getModel().getName();
			// else
			cruiseRef = Nemo.getInstance().getDomain().getCruise().getModel().getReference();
			// [Numero station]si < 5 : code sur 5 caracteres il est
			// complete a 
			// gauche par des '0'.
			if (stationReference.length() < 5) {
				stationReference = form.padZero(stationReference, 5);
			}
			break;

		case MEDATLAS:
			// MEDATLAS ->
			// [RÃ©fÃ©rence campagne]
			cruiseRef = Elts.getElt(Elts.cruise_ref, cDomain.getModel()
					.getReference());
			// [NumÃ©ro station] codÃ© sur 5 caractÃ¨res il est complÃ©tÃ© Ã 
			// gauche
			// par des '0'.
			stationReference = form.padZero(stationReference, 5);
			break;
		}

		// [Reference]
		String temp = Global.EMPTY_STRING;
		temp += cruiseRef;

		// [NumÃ©ro station]
		if (!temp.equals(Global.EMPTY_STRING))
			temp += Global.UNDERSCORE;
		temp += stationReference;

		// [Type de donnees] s'il existe
		DataType dataType = _model.getDataType();
		if (dataType != null) {
			if (!dataType.getCode().equals(Global.EMPTY_STRING)) {
				if (!temp.equals(Global.EMPTY_STRING)) {
					temp += Global.UNDERSCORE;
				}
				// FAE 6536
				temp += fr.ifremer.nemo.util.Utils.extraitCode(dataType
						.getCode());
			}
		}
		localCDIId = fr.ifremer.nemo.util.Utils.validFileName(temp);

		return localCDIId;
	}

	// FAE 6143
	public String getAndCheckLocalCDI(CruiseDomain cDomain, Conversion c,
			String ref) throws ParseException, GenerationException,
			UnsupportedEncodingException {
		String localCdi = getLocalCdiId(cDomain, c, ref);
		if (stationsLocalCDIList.contains(localCdi)) {
			throw new GenerationException(
					Messages.error_convert_stationsDoubles + " " + localCdi);
		}
		stationsLocalCDIList.add(localCdi);

		return localCdi;
	}

	public void resetlocalCDIList() {
		stationsLocalCDIList = new HashSet<String>();
	}

	@SuppressWarnings("nls")
	private StringBuffer getMedatlasHeader(Station currentStation,
			String[] inputStationHeader) throws ParseException,
			GenerationException {

		String errorMessage = "";
		try {

			StringBuffer medStationHeader = new StringBuffer();
			String stationRef = currentStation.getReference();

			String start = Conversion.MEDATLAS.getDebut();
			String cruiseRef = Nemo.getInstance().getDomain().getCruise()
					.getModel().getReference();

			// *FI3519981000700001 Data Type=D09
			medStationHeader.append(start);
			errorMessage = "cruise reference";
			medStationHeader.append(Elts.getElt(Elts.cruise_ref, cruiseRef));
			errorMessage = "station reference";
			medStationHeader.append(Elts.getElt(Elts.station_ref,
					stationRef.trim()));
			medStationHeader.append(Global.SPACE);
			errorMessage = "dataType";
			medStationHeader.append(Elts.getElt(Elts.datatype_code, _model
					.getDataType().getCode()));
			medStationHeader.append(Global.RN);

			// *DATE=21071998 TIME=0930 LAT=S18 08.55 LON=E178 27.21 DEPTH=
			// QC=0000
			medStationHeader.append(start);
			errorMessage = "station date";
			medStationHeader.append(Elts.getElt(Elts.station_date,
					DateTimeUtils.getFullDate(currentStation)));
			errorMessage = "station time";
			medStationHeader.append(Elts.getElt(Elts.station_time,
					DateTimeUtils.getCorrectedTime(currentStation.getTime(),
							currentStation.getOffsetTU())));
			errorMessage = Intern.S_LAT;
			medStationHeader.append(Elts.getElt(Elts.station_lat,
					currentStation.getLatitude()));
			errorMessage = Intern.S_LON;
			medStationHeader.append(Elts.getElt(Elts.station_lon,
					currentStation.getLongitude()));
			errorMessage = Intern.S_DEPTH;
			Object value = _model.getProperty(Intern.S_DEPTH).applyOn(
					inputStationHeader);

			boolean depthIsDefault = false;
			if (value.equals("Default")) {
				value = Float.NaN;
				depthIsDefault = true;
			}

			medStationHeader.append(Elts.getElt(Elts.station_depth, value));

			medStationHeader.append(Global.SPACE);
			medStationHeader.append(Elts.getKeyword(Elts.qc));
			medStationHeader
					.append(currentStation.getMetaFlag(Intern.DATETIME));
			medStationHeader.append(currentStation.getMetaFlag(Intern.S_LAT));
			medStationHeader.append(currentStation.getMetaFlag(Intern.S_LON));

			if (depthIsDefault)
				medStationHeader.append(Global.DEFAULT_FLAG);
			else
				medStationHeader.append(currentStation
						.getMetaFlag(Intern.S_DEPTH));

			medStationHeader.append(Global.RN);

			// *NB PARAMETERS=07 RECORD LINES=08651
			DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
			int paramNumbers = dataDomain.getMyParameterManager()
					.getMedParameterModels().length
					+ dataDomain.getModel().getParameterModels().length;
			errorMessage = "number of parameters";
			medStationHeader.append(start);
			medStationHeader.append(Elts.getElt(Elts.station_param,
					Integer.toString(paramNumbers)));
			medStationHeader.append(Elts.getElt(Elts.station_records,
					currentStation.getNbRecords()));
			medStationHeader.append(Global.RN);

			medStationHeader.append(dataDomain.getMedParametersDesc());

			// *GLOBAL PROFILE QUALITY FLAG=0 GLOBAL PARAMETERS QC FLAGS=0000000
			medStationHeader.append(start);
			errorMessage = "globale profile quality flag";
			medStationHeader.append(Elts.getElt(Elts.station_gblProfile,
					currentStation.getGlobalProfileQc()));
			medStationHeader.append(Elts.getElt(Elts.station_gblParam,
					currentStation.getGlobalParamQc()));
			medStationHeader.append(Global.RN);

			// *DC HISTORY=
			medStationHeader.append(start);
			// Ajout du trim FAE 5803
			errorMessage = Intern.S_DCHIST;
			medStationHeader.append(Elts.getElt(Elts.station_dcHistory,
					((String) _model.getProperty(Intern.S_DCHIST + "1")
							.applyOn(inputStationHeader)).trim()));
			medStationHeader.append(Global.RN);

			medStationHeader.append(start);
			try {
				medStationHeader.append(Elts
						.limit((String) _model.getProperty(
								Intern.S_DCHIST + "2").applyOn(
								inputStationHeader), Elts.station_line));
			} catch (Exception nulException) {
				// this is authorized: only DCHistory1 is used, DC History2 is
				// empty
				// so this is not an error
				System.out.println("no value for " + Intern.S_DCHIST + "2");
			}

			// FAE 5803 Sortie du try catch du passage à la ligne
			medStationHeader.append(Global.RN);

			// *DM HISTORY=
			medStationHeader.append(start);
			errorMessage = "DM history";
			medStationHeader.append(Elts.getElt(Elts.station_dmHistory,
					Global.EMPTY_STRING));
			medStationHeader.append(Global.RN);

			medStationHeader.append(start);
			medStationHeader.append(Global.RN);

			// *COMMENT
			medStationHeader.append(start);
			errorMessage = "cruise comment";
			medStationHeader.append(Elts.kw_cruise_comment);
			medStationHeader.append(Global.RN);

			// pour chaque ligne de commentaires
			for (String line : getCommentLines(inputStationHeader)) {
				medStationHeader.append(start);
				medStationHeader.append(line);
				medStationHeader.append(Global.RN);
			}

			medStationHeader.append(start);
			medStationHeader.append(Global.RN);
			errorMessage = "station header";
			if (Nemo.getInstance().getDomain().getMedia().getModel()
					.getInputType() != E_InputType.PROFILE)
				medStationHeader.append(getMedTimeSeriesHeader(currentStation,
						inputStationHeader));

			// --SDN_parameter_mapping--
			errorMessage = "SDN_parameter_mapping";
			medStationHeader.append(dataDomain
					.getSDNparameters(Conversion.MEDATLAS));

			// *EDMO_CODE=1
			medStationHeader.append(start);
			errorMessage = "EDMO_CODE";
			medStationHeader.append(Elts.getElt(Elts.station_edmo, Nemo
					.getInstance().getConfig().getIdEdmoRespCdi()));
			medStationHeader.append(Global.RN);

			// *LOCAL_CDI_ID=FI35199810007_00001_D09
			medStationHeader.append(start);
			errorMessage = "LOCAL_CDI_ID";
			medStationHeader.append(Elts.getElt(
					Elts.station_localCdiId,
					getLocalCdiId(Nemo.getInstance().getDomain().getCruise(),
							Conversion.MEDATLAS, stationRef)));
			medStationHeader.append(Global.RN);

			// *SURFACE SAMPLES=
			medStationHeader.append(start);
			errorMessage = Intern.S_SURFACE;
			medStationHeader.append(Elts.getElt(
					Elts.station_surface,
					_model.getProperty(Intern.S_SURFACE + "1").applyOn(
							inputStationHeader)).trim());
			medStationHeader.append(Global.RN);

			medStationHeader.append(start);
			try {
				medStationHeader.append(Elts
						.limit((String) _model.getProperty(
								Intern.S_SURFACE + "2").applyOn(
								inputStationHeader), Elts.station_line));
			} catch (Exception nulException) {
				// this is authorized: only DCHistory1 is used, DC History2 is
				// empty
				// so this is not an error
				System.out.println("no value for " + Intern.S_SURFACE + "2");
			}

			return medStationHeader;
		}

		catch (Exception e) {
			throw new GenerationException(errorMessage + " : " + e.getMessage());
		}
	}

	/**
	 * 
	 * @return String[]
	 * @throws ParseException
	 */
	private String[] getCommentLines(String[] inputFile) throws ParseException {
		LineNumberReader reader = new LineNumberReader(new StringReader(
				(String) _model.getProperty(Intern.S_COMMENT)
						.applyOn(inputFile)));
		String line = null;
		java.util.Vector<String> lines = new java.util.Vector<String>();
		try {
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		String[] lineArray = new String[lines.size()];
		return lines.toArray(lineArray);
	}

	public String getMetaFlag(String key, String[] inputFile) {
		// FAE 4431
		String metaFlag = Nemo.getInstance().getConfig().getDefaultFlag();
		if (key.equals(Intern.DATETIME)) {
			key = Intern.S_DATE;
		} else if (key.equals(Intern.ENDDATETIME)) {
			key = Intern.S_EDATE;
		}
		Fragment flag = _model.getProperty(key).getFlag();
		if (flag != null) {
			metaFlag = _model.getFlagTable().getFlagSDN(
					flag.retreive(inputFile));
		}
		return metaFlag;
	}

	private StringBuffer getMedTimeSeriesHeader(Station currentStation,
			String[] inputFile) throws ParseException,
			UnsupportedEncodingException {
		StringBuffer timeseriesHeader = new StringBuffer();
		String start = Conversion.MEDATLAS.getDebut();

		float sensorDepth = currentStation.getSensorDepth();
		long endTime = DateTimeUtils.computeEndTime(currentStation);

		// *ADDITIONAL INFORMATION - TIME SERIES
		timeseriesHeader.append(start);
		timeseriesHeader.append(Elts.kw_station_addTimeSeries);
		timeseriesHeader.append(Global.RN);

		// *EDATE=19091998 ETIME=1110 ELAT=S18 08.55 ELON=E178 27.21 EDEPTH=
		// QC=0000
		timeseriesHeader.append(start);
		timeseriesHeader.append(Elts.getElt(Elts.station_endDate, endTime));
		timeseriesHeader.append(Elts.getElt(Elts.station_endTime, endTime));

		double endlat = currentStation.getEndlatitude();
		if (Double.isNaN(endlat)) {
			if (_model.getProperty(Intern.S_ELAT).isUserProvided()
					&& _model.getProperty(Intern.S_ELAT).getUserValue()
							.equals(Global.EMPTY_STRING)) {
				endlat = (Double) _model.getProperty(Intern.S_LAT).applyOn(
						inputFile);
			} else {
				endlat = (Double) _model.getProperty(Intern.S_ELAT).applyOn(
						inputFile);
			}
		}

		timeseriesHeader.append(Elts.getElt(Elts.station_endLat, endlat));
		double endlon = currentStation.getEndlongitude();
		if (Double.isNaN(endlon)) {
			if (_model.getProperty(Intern.S_ELON).isUserProvided()
					&& _model.getProperty(Intern.S_ELON).getUserValue()
							.equals(Global.EMPTY_STRING)) {
				endlon = (Double) _model.getProperty(Intern.S_LON).applyOn(
						inputFile);
			} else {
				endlon = (Double) _model.getProperty(Intern.S_ELON).applyOn(
						inputFile);
			}
		}

		timeseriesHeader.append(Elts.getElt(Elts.station_endLon, endlon));
		float endDepth = currentStation.getEnddepth();
		if (Float.isNaN(endDepth)) {
			if (_model.getProperty(Intern.S_EDEPTH).isUserProvided()
					&& _model.getProperty(Intern.S_EDEPTH).getUserValue()
							.equals(Global.EMPTY_STRING)) {
				endDepth = (Float) _model.getProperty(Intern.S_DEPTH).applyOn(
						inputFile);
			} else {
				if (_model.getProperty(Intern.S_EDEPTH).getFragment() == null
						&& Nemo.getInstance().getDomain().getMedia().getModel()
								.getInputType() == E_InputType.TRAJECTORY)
					endDepth = Float.NaN;
				else
					endDepth = (Float) _model.getProperty(Intern.S_EDEPTH)
							.applyOn(inputFile);
			}
		}

		if (endDepth == -1)
			timeseriesHeader.append(Elts.getElt(Elts.station_endDepth, null));
		else
			timeseriesHeader.append(Elts
					.getElt(Elts.station_endDepth, endDepth));
		timeseriesHeader.append(Global.SPACE);
		timeseriesHeader.append(Elts.getKeyword(Elts.qc));
		timeseriesHeader.append(currentStation.getMetaFlag(Intern.ENDDATETIME));
		timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_ELAT));
		timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_ELON));
		timeseriesHeader.append(currentStation.getMetaFlag(Intern.S_EDEPTH));
		timeseriesHeader.append(Global.RN);

		// *SENSOR DEPTH=17 (metre) DISTANCE TO BOTTOM= (metre) QC=00
		Float bottomDepth = Float.NaN;
		if (!Float.isNaN(endDepth) && !Float.isNaN(sensorDepth)) {
			bottomDepth = endDepth - sensorDepth;
		}
		timeseriesHeader.append(start);
		timeseriesHeader.append(Elts.getElt(Elts.station_sensor, sensorDepth));
		timeseriesHeader.append(Elts.getElt(Elts.station_bottomDepth,
				bottomDepth));
		timeseriesHeader.append(Elts.getKeyword(Elts.qc));
		timeseriesHeader.append(currentStation
				.getMetaFlag(Intern.S_SENSORDEPTH));
		timeseriesHeader.append(currentStation
				.getMetaFlag(Intern.DISTANCETOBOTTOM));
		timeseriesHeader.append(Global.RN);

		// *DURATION=60 (day) QC=0
		float duration = endTime - DateTimeUtils.getFullDate(currentStation); // duree
		// en
		// millisecondes
		duration = duration / (1000.0f * 60.0f * 60.0f * 24.0f);
		timeseriesHeader.append(start);
		timeseriesHeader.append(Elts.getElt(Elts.station_duration, duration));
		for (int i = 0; i < 50; i++) {
			timeseriesHeader.append(Global.SPACE);
		}
		timeseriesHeader.append(Elts.getElt(Elts.qc,
				currentStation.getMetaFlag(Intern.DURATION)));
		timeseriesHeader.append(Global.RN);

		// *SAMPLING RATE=600 (second) MAGNETIC DECLINATION=-999 (degree)
		timeseriesHeader.append(start);

		timeseriesHeader.append(Elts.getElt(Elts.station_sampling,
				currentStation.getSamplingRate()));
		timeseriesHeader.append(Elts.getElt(Elts.station_magnetic, _model
				.getProperty(Intern.S_MAGNETIC).applyOn(inputFile)));
		timeseriesHeader.append(Global.RN);

		return timeseriesHeader;
	}

	// RELEASE
	public void release() {
		_model.release();

	}

	/**
	 * Getter pour model.
	 * 
	 * @return StationModel
	 * @see StationDomain.java#model
	 */
	public StationModel getModel() {
		return _model;
	}

	/**
	 * Setter pour model
	 * 
	 * @param model
	 *            : nouvelle valeur pour model
	 * @see StationDomain.java#model
	 */
	public void setModel(StationModel model) {
		_model = model;
	}

	/**
	 * Getter pour _someInData.
	 * 
	 * @return boolean
	 * @see StationDomain.java#_someInData
	 */
	public boolean isSomeInData() {
		return _someInData;
	}

	/**
	 * Setter pour _someInData
	 * 
	 * @param someInData
	 *            : nouvelle valeur pour someInData
	 * @see StationDomain.java#_someInData
	 */
	public void setSomeInData(boolean someInData) {
		_someInData = someInData;
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

	public boolean stationIsRegistered(String inputFilePath,
			String inputFileName) {
		if (_listStations.containsKey(inputFilePath)) {
			for (Station st : _listStations.get(inputFilePath)) {
				if (st.getFileName().equals(inputFileName))
					return true;
			}
		}
		return false;
	}

	public List<Station> getStations(String inputFilePath) {
		return _listStations.get(inputFilePath);
	}

	public Station getStationByRef(String ref) {
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

	public void resetStations() {
		_listStations.clear();
	}

	/**
	 * Getter pour _computed.
	 * 
	 * @return boolean
	 * @see StationDomain.java#_computed
	 */
	public boolean isComputed() {
		return _computed;
	}

	/**
	 * Setter pour _computed
	 * 
	 * @param computed
	 *            : nouvelle valeur pour computed
	 * @see StationDomain.java#_computed
	 */
	public void setComputed(boolean computed) {
		_computed = computed;
	}

	/**
	 * Getter sur _currentStation.
	 * 
	 * @return the currentStation
	 */
	public Station getCurrentStation() {
		return _currentStation;
	}
}
