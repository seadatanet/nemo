/*
 * ============================================================================
 * Nom du fichier : Station.java
 * Application : NEMO version 1.2.20
 * Auteur : dboulang
 * Date de création : 17 déc. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.cdi_summary.curve.LatLon;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Intern;

/**
 * Class Station.java Description: description d'une station
 * 
 * @author dboulang
 */
public class Station implements Comparable {


	private String local_cdi_id;
	// need to keep associate cruise ref and name for collections, because model consider only one cruise, and value is override
	// if we do not keep them, we can not write CDI summary
	private String cruiseRef;
	private String cruiseName;

	protected String _reference;
	protected int _nbRecords;

	protected double _latitude;
	protected double _longitude;
	protected long _date;
	protected long _time;
	protected float _offsetTU;

	// pour Timeseries et Trajectory
	protected float _sensorDepth;
	protected float _minDepth;
	protected float _maxDepth;
	protected float _samplingRate;

	// pour Trajectory
	protected double _endlatitude;
	protected double _endlongitude;
	protected double _northlatitude;
	protected double _southlatitude;
	protected double _westlongitude;
	protected double _eastlongitude;
	protected float _enddepth;
	protected List<LatLon> latLonList;

	protected HashMap<String, String> _metaFlags;
	protected HashMap<String, String> _globalParamQc;
	protected HashMap<String, Boolean> _missingValueParams;

	protected String _fileName;

	protected int startLine;
	// 39448
	protected long youngerDate=0;
	protected long olderDate=0;
	
	protected String[] comments;


	public Station(String fileName, String[] inputStationHeader) throws GenerationException {
		// TODO BGT supprimer errorMsg
		String errorMessage = "";
		boolean isNetCDF = false;
		startLine=-1;
		
		//isNetCDF = Nemo.getInstance().getViewMain().getMyRulesManager().isConversionToNetCDF();
		E_TypeConversion convertion = Nemo.getInstance().getDomain().getConversion().getCurrent();
		if (convertion == E_TypeConversion.NETCDF_MONOSTATION || convertion == E_TypeConversion.NETCDF_MULTISTATIONS)
			isNetCDF = true;

		try {
			_fileName = fileName;
			_reference = null;
			_nbRecords = 0;
			_metaFlags = new HashMap<String, String>();

			_endlatitude = Double.NaN;
			_endlongitude = Double.NaN;
			_southlatitude = Double.NaN;
			_northlatitude = Double.NaN;
			_westlongitude = Double.NaN;
			_eastlongitude = Double.NaN;
			latLonList = new ArrayList<LatLon>();
			
			_enddepth = Float.NaN;
			
			StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();
			errorMessage = Intern.S_LAT;
			_latitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOn(inputStationHeader)).doubleValue();
			errorMessage = Intern.S_LON;
			_longitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOn(inputStationHeader)).doubleValue();
			errorMessage = Intern.S_DATE;
			_date = ((Long) stationDomain.getModel().getProperty(Intern.S_DATE).applyOn(inputStationHeader)).longValue();
			// The date may be in a datetime column, we extract only the date.
			_date = DateTimeUtils.dateTimeToDate(_date);			
			errorMessage = Intern.S_TIME;
			Object time = stationDomain.getModel().getProperty(Intern.S_TIME).applyOn(inputStationHeader);
			// default time = -1 voir aussi
			// fr.ifremer.nemo.domain.DateTimeUtils.getDateTime(Station)
			if (time.toString().equals("Default"))
				_time = -1;
			else
				// The time may be in a datetime column, we extract only the time.
				_time = DateTimeUtils.dateTimeToTime((Long) time);
			if (stationDomain.getModel().getProperty(Intern.S_OFFSETTU)!=null){
				errorMessage = Intern.S_OFFSETTU;
				_offsetTU = ((Float) stationDomain.getModel().getProperty(Intern.S_OFFSETTU).applyOn(inputStationHeader)).floatValue();
			}
			errorMessage = Intern.DATETIME;
			addMetaFlag(Intern.DATETIME, stationDomain.getMetaFlag(Intern.DATETIME, inputStationHeader));
			errorMessage = Intern.S_LAT;
			addMetaFlag(Intern.S_LAT, stationDomain.getMetaFlag(Intern.S_LAT, inputStationHeader));
			errorMessage = Intern.S_LON;
			addMetaFlag(Intern.S_LON, stationDomain.getMetaFlag(Intern.S_LON, inputStationHeader));
			// TODO FAE Depth par défaut (flags) si "defaut" = >
			// Global.DEFAULT_FLAG sinon getMetaFlag
			errorMessage = Intern.S_DEPTH;
			addMetaFlag(Intern.S_DEPTH, stationDomain.getMetaFlag(Intern.S_DEPTH, inputStationHeader));

//			if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() != E_InputType.PROFILE) {
			if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TRAJECTORY
				||Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES	) {
				errorMessage = Intern.S_SENSORDEPTH;
				_sensorDepth = ((Float) stationDomain.getModel().getProperty(Intern.S_SENSORDEPTH).applyOn(inputStationHeader)).floatValue();
				_minDepth = _sensorDepth;
				_maxDepth = _sensorDepth;

				errorMessage = Intern.S_SAMPLINGRATE;
				_samplingRate = ((Float) stationDomain.getModel().getProperty(Intern.S_SAMPLINGRATE).applyOn(inputStationHeader)).floatValue();

				String conversionFormula = stationDomain.getModel().getProperty(Intern.S_SAMPLINGRATE).getConversionFormula();
				errorMessage = Intern.S_SAMPLINGRATE;
				if (conversionFormula != null && conversionFormula != "") {
					org.nfunk.jep.JEP parser = new org.nfunk.jep.JEP();
					parser.addStandardFunctions();
					parser.addStandardConstants();
					parser.addVariable(Global.STR_CONVERT, _samplingRate);
					parser.parseExpression(Global.STR_CONVERT + conversionFormula);

					_samplingRate = (float) parser.getValue();
				}

				errorMessage = Intern.S_SENSORDEPTH;
				addMetaFlag(Intern.S_SENSORDEPTH, stationDomain.getSensorDepthFlag());
				errorMessage = Intern.ENDDATETIME;
				addMetaFlag(Intern.ENDDATETIME, stationDomain.getMetaFlag(Intern.ENDDATETIME, inputStationHeader));
				if (!isNetCDF) {
					errorMessage = Intern.S_ELAT;
					addMetaFlag(Intern.S_ELAT, stationDomain.getMetaFlag(Intern.S_ELAT, inputStationHeader));
					errorMessage = Intern.S_ELON;
					addMetaFlag(Intern.S_ELON, stationDomain.getMetaFlag(Intern.S_ELON, inputStationHeader));
					errorMessage = Intern.S_EDEPTH;
					addMetaFlag(Intern.S_EDEPTH, stationDomain.getMetaFlag(Intern.S_EDEPTH, inputStationHeader));
				}
				errorMessage = Intern.DISTANCETOBOTTOM;
				addMetaFlag(Intern.DISTANCETOBOTTOM, calculateMetaFlag(Intern.S_DEPTH, Intern.S_SENSORDEPTH));
				errorMessage = Intern.DURATION;
				addMetaFlag(Intern.DURATION, calculateMetaFlag(Intern.DATETIME, Intern.ENDDATETIME));
			}
			else if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.PROFILE) {
				_minDepth = Float.NaN;
				_maxDepth = Float.NaN;
			}

			initQCs();

		} catch (Exception e) {
			throw new GenerationException(errorMessage + " " + e.getMessage());
		}
	}

	public void initQCs()
	{
		_globalParamQc = new HashMap<String, String>();
		_missingValueParams = new HashMap<String, Boolean>();
		DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
		for (ParameterModel paramModel : dataDomain.getMyParameterManager().getMedParameterModels()) {
			_globalParamQc.put(paramModel.getParameter().getCode(), ((AddedParam) paramModel.getParameter()).getFlag(this, null));
			_missingValueParams.put(paramModel.getParameter().getCode(), true);
		}
		for (ParameterModel paramModel : dataDomain.getModel().getParameterModels()) {
			_globalParamQc.put(paramModel.getParameter().getCode(), Global.WORSE_FLAG);
			_missingValueParams.put(paramModel.getParameter().getCode(), true);
		}
	}

	public void updateGlobalParamQc(String inputLine) {
		DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
		// les qcFlags des paramètres ajoutés pour MEDATLAS ne varient pas (=
		// MetaFlags)
		String qcFlag = null;
		for (ParameterModel paramModel : dataDomain.getModel().getParameterModels()) {
			qcFlag = paramModel.retreiveParameterFlag(inputLine);

			if (!qcFlag.equals(Global.DEFAULT_FLAG) && !qcFlag.equals(Global.ZERO) && qcFlag.compareTo(_globalParamQc.get(paramModel.getParameter().getCode())) < 0) {
				_globalParamQc.put(paramModel.getParameter().getCode(), qcFlag);
			}

			// missingValue
			if (!qcFlag.equals(Global.DEFAULT_FLAG)) {
				_missingValueParams.put(paramModel.getParameter().getCode(), false);
			}

		}
	}

	public String getGlobalParamQc() {
		StringBuffer globalParamQc = new StringBuffer();

		DataDomain dataDomain = Nemo.getInstance().getDomain().getData();
		String globalQc = null;
		for (ParameterModel paramModel : dataDomain.getMyParameterManager().getMedParameterModels()) {
			globalQc = _globalParamQc.get(paramModel.getParameter().getCode());
			if (globalQc.equals(Global.WORSE_FLAG))
				globalQc = Global.ZERO;
			globalParamQc.append(globalQc);
		}
		for (ParameterModel paramModel : dataDomain.getModel().getParameterModels()) {
			globalQc = _globalParamQc.get(paramModel.getParameter().getCode());
			if (_missingValueParams.get(paramModel.getParameter().getCode()))
				globalQc = Global.DEFAULT_FLAG;
			else if (globalQc.equals(Global.WORSE_FLAG))
				globalQc = Global.ZERO;
			globalParamQc.append(globalQc);
		}

		return globalParamQc.toString();
	}

	public String getGlobalProfileQc() {
		String globalProfileQC = Global.WORSE_FLAG;
		boolean missingValue = true;

		for (String qc : _globalParamQc.values()) {
			if (!qc.equals(Global.ZERO) && !qc.equals(Global.DEFAULT_FLAG) && qc.compareTo(globalProfileQC) < 0)
				globalProfileQC = qc;

			if (!qc.equals(Global.DEFAULT_FLAG))
				missingValue = false;
		}

		if (globalProfileQC.equals(Global.WORSE_FLAG))
			globalProfileQC = Global.ZERO;

		if (missingValue)
			globalProfileQC = Global.DEFAULT_FLAG;

		return globalProfileQC;
	}

	public void completeMedatlasTrajectory(String inputLine) throws NumberFormatException, StringIndexOutOfBoundsException, ParseException {
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();

		// ENDLAT => LATX
		_endlatitude = (Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOnLine(inputLine);

		// ENDLON => LONX
		_endlongitude = (Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOnLine(inputLine);

		// ENDDEPTH => BATH si inData
//		if (stationDomain.getModel().getProperty(Intern.S_DEPTH).isInData()) {
//			Object botDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOnLine(inputLine);
//			if ((!botDepth.equals("Default")) && !((Float) botDepth).isNaN())
//				_enddepth = (Float) botDepth;
//		}
		setBottomDepth(inputLine);
	}
	
	/**
	 * Update bottom depth if depth is in data.
	 * @param readedLine
	 * @throws ParseException
	 */
	public void setBottomDepth(String readedLine) throws ParseException {
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();
		
		if (stationDomain.getModel().getProperty(Intern.S_DEPTH).isInData()) {
			Object botDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOnLine(readedLine);
			if ((!botDepth.equals("Default")) && !((Float) botDepth).isNaN())
				_enddepth = (Float) botDepth;
		}
	}
	
	/**
	 * Update bottom depth if depth is in header.
	 * @param stationHeader
	 * @throws ParseException
	 */
	public void setBottomDepth(String[] stationHeader) throws ParseException {
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();

		Object botDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOn(stationHeader);
		
		if ((!botDepth.equals("Default")) && !((Float) botDepth).isNaN()) {
			_enddepth = (Float) botDepth;
		}
	}

	/**
	 * Getter pour nbRecords.
	 * 
	 * @return int
	 * @see Station.java#nbRecords
	 */
	public int getNbRecords() {
		return _nbRecords;
	}

	/**
	 * Setter pour nbRecords
	 * 
	 * @param nbRecords
	 *            :
	 *            nouvelle valeur pour nbRecords
	 * @see Station.java#nbRecords
	 */
	public void setNbRecords(int nbRecords) {
		_nbRecords = nbRecords;
	}

	/**
	 * Getter pour reference.
	 * 
	 * @return String
	 * @see Station.java#reference
	 */
	public String getReference() {
		return _reference;
	}

	/**
	 * Setter pour reference
	 * 
	 * @param reference
	 *            :
	 *            nouvelle valeur pour reference
	 * @see Station.java#reference
	 */
	public void setReference(String reference) {
		_reference = reference;
	}

	/**
	 * Getter pour _date.
	 * 
	 * @return long
	 * @see Station.java#_date
	 */
	public long getDate() {
		return _date;
	}

	/**
	 * Setter pour _date
	 * 
	 * @param date
	 *            :
	 *            nouvelle valeur pour date
	 * @see Station.java#_date
	 */
	public void setDate(long date) {
		_date = date;
	}

	/**
	 * Getter pour _latitude.
	 * 
	 * @return double
	 * @see Station.java#_latitude
	 */
	public double getLatitude() {
		return _latitude;
	}
	


	/**
	 * Setter pour _latitude
	 * 
	 * @param latitude
	 *            :
	 *            nouvelle valeur pour latitude
	 * @see Station.java#_latitude
	 */
	public void setLatitude(double latitude) {
		_latitude = latitude;
	}

	/**
	 * Getter pour _longitude.
	 * 
	 * @return double
	 * @see Station.java#_longitude
	 */
	public double getLongitude() {
		return _longitude;
	}
	

	/**
	 * Setter pour _longitude
	 * 
	 * @param longitude
	 *            :
	 *            nouvelle valeur pour longitude
	 * @see Station.java#_longitude
	 */
	public void setLongitude(double longitude) {
		_longitude = longitude;
	}
	
	/**
	 * Getter pour latList.
	 * 
	 * @return List<LatLon>
	 * @see Station.java#latList
	 */
	public List<LatLon> getLatLonList() {
		return latLonList;
	}

	private String calculateMetaFlag(String initial1, String initial2) {

		String flag1 = getMetaFlag(initial1);
		String flag2 = getMetaFlag(initial2);
		String value;

		if (flag1.equals(Global.ZERO)) {
			value = flag1;
		} else if (flag2.equals(Global.ZERO)) {
			value = flag2;
		} else if (flag1.compareTo(flag2) > 0) {
			value = flag2;
		} else {
			value = flag1;
		}

		return value;
	}

	public String getMetaFlag(String key) {
		String flag = Nemo.getInstance().getConfig().getDefaultFlag(); // 25307

		if (getMetaFlags() != null) {
			if ((getMetaFlags().containsKey(key)) && (getMetaFlags().get(key) != null)) {
				flag = getMetaFlags().get(key);
			}
		}

		return flag;
	}

	/**
	 * Getter pour _metaFlags.
	 * 
	 * @return HashMap<String,String>
	 * @see Station.java#_metaFlags
	 */
	public HashMap<String, String> getMetaFlags() {
		return _metaFlags;
	}

	public void addMetaFlag(String key, String metaFlag) {
		_metaFlags.put(key, metaFlag);
	}

	/**
	 * Setter pour _metaFlags
	 * 
	 * @param metaFlags
	 *            :
	 *            nouvelle valeur pour metaFlags
	 * @see Station.java#_metaFlags
	 */
	public void setMetaFlags(HashMap<String, String> metaFlags) {
		_metaFlags = metaFlags;
	}

	/**
	 * Getter pour _offsetTU.
	 * 
	 * @return float
	 * @see Station.java#_offsetTU
	 */
	public float getOffsetTU() {
		return _offsetTU;
	}

	/**
	 * Setter pour _offsetTU
	 * 
	 * @param offsetTU
	 *            :
	 *            nouvelle valeur pour offsetTU
	 * @see Station.java#_offsetTU
	 */
	public void setOffsetTU(float offsetTU) {
		_offsetTU = offsetTU;
	}

	/**
	 * Getter pour _sensorDepth.
	 * 
	 * @return float
	 * @see Station.java#_sensorDepth
	 */
	public float getSensorDepth() {
		return _sensorDepth;
	}

	/**
	 * Setter pour _sensorDepth
	 * 
	 * @param sensorDepth
	 *            :
	 *            nouvelle valeur pour sensorDepth
	 * @see Station.java#_sensorDepth
	 */
	public void setSensorDepth(float sensorDepth) {
		_sensorDepth = sensorDepth;
	}

	/**
	 * Getter pour _time.
	 * 
	 * @return long
	 * @see Station.java#_time
	 */
	public long getTime() {
		return _time;
	}

	/**
	 * Setter pour _time
	 * 
	 * @param time
	 *            :
	 *            nouvelle valeur pour time
	 * @see Station.java#_time
	 */
	public void setTime(long time) {
		_time = time;
	}

	/**
	 * Getter pour _samplingRate.
	 * 
	 * @return float
	 * @see Station.java#_samplingRate
	 */
	public float getSamplingRate() {
		return _samplingRate;
	}

	/**
	 * Setter pour _samplingRate
	 * 
	 * @param samplingRate
	 *            :
	 *            nouvelle valeur pour samplingRate
	 * @see Station.java#_samplingRate
	 */
	public void setSamplingRate(float samplingRate) {
		_samplingRate = samplingRate;
	}

	public void calculSamplingRate(String inputLine) throws ParseException, UnsupportedEncodingException {
		long ecart = (DateTimeUtils.convertTimestamp(inputLine) - DateTimeUtils.getFullDate(this)) / 1000;
		if (ecart > 0) {
			_samplingRate = ecart;
		}
	}

	@Override
	public int compareTo(Object other) {
//		int orig=99999;
		Integer refIniInteger;
		Integer refOtherInteger;
//
//		try {
//			refIniInteger = Integer.valueOf(this.getReference());
//			refOtherInteger = Integer.valueOf(((Station) other).getReference());
//			orig=  refIniInteger.compareTo(refOtherInteger);
//		} catch (NumberFormatException e) {
//			String refIni = this.getReference();
//			String refOther = ((Station) other).getReference();
//
//			orig= refIni.compareTo(refOther);
//		}
//
//		System.out.println("orig: " + this.getReference() + " compTo " + ((Station) other).getReference() + " >> " + orig);
//		
		
		//+++++++++++++++++++++++++++++++++++++++++++++++++
		int comp = 0;
		try{
			Long thisDate = this.getDate()+this.getTime();
			Long otherDate = ((Station)other).getDate()+((Station)other).getTime();
			
			comp = thisDate.compareTo(otherDate);
		}catch (Exception e) {
			// TODO: handle exception
		}


		if (comp==0){
			try {
				refIniInteger = Integer.valueOf(this.getReference().trim());
				refOtherInteger = Integer.valueOf(((Station)other).getReference().trim());
				comp=refIniInteger.compareTo(refOtherInteger);
			} catch (NumberFormatException e) {
				String refIni = this.getReference();
				String refOther = ((Station)other).getReference();
				comp=refIni.compareTo(refOther);
			}
		}
		
//		System.out.println("new : " + this.getReference() + " compTo " + ((Station) other).getReference() + " >> " + comp);
		return comp;
	}

	/**
	 * Getter pour _fileName.
	 * 
	 * @return String
	 * @see Station.java#_fileName
	 */
	public String getFileName() {
		return _fileName;
	}

	/**
	 * Setter pour _fileName
	 * 
	 * @param fileName
	 *            : nouvelle valeur pour fileName
	 * @see Station.java#_fileName
	 */
	public void setFileName(String fileName) {
		_fileName = fileName;
	}

	/**
	 * Getter pour _enddepth.
	 * 
	 * @return float
	 * @see Station.java#_enddepth
	 */
	public float getEnddepth() {
		return _enddepth;
	}

	public float getMinDepth() {
		return _minDepth;
	}

	public void setMinDepth(float minDepth) {
		_minDepth = minDepth;
	}

	public float getMaxDepth() {
		return _maxDepth;
	}

	public void setMaxDepth(float maxDepth) {
		_maxDepth = maxDepth;
	}

	/**
	 * Getter pour _endlatitude.
	 * 
	 * @return double
	 * @see Station.java#_endlatitude
	 */
	public double getEndlatitude() {
		return _endlatitude;
	}

	/**
	 * Getter pour _endlongitude.
	 * 
	 * @return double
	 * @see Station.java#_endlongitude
	 */
	public double getEndlongitude() {
		return _endlongitude;
	}
	public double getNorthlatitude() {
		return _northlatitude;
	}

	public void setNorthlatitude(double northlatitude) {
		_northlatitude = northlatitude;
	}

	public double getSouthlatitude() {
		return _southlatitude;
	}

	public void setSouthlatitude(double southlatitude) {
		_southlatitude = southlatitude;
	}

	public double getWestlongitude() {
		return _westlongitude;
	}

	public void setWestlongitude(double westlongitude) {
		_westlongitude = westlongitude;
	}

	public double getEastlongitude() {
		return _eastlongitude;
	}

	public void setEastlongitude(double eastlongitude) {
		_eastlongitude = eastlongitude;
	}

	public String getLocal_cdi_id() {
		return local_cdi_id;
	}

	public void setLocal_cdi_id(String local_cdi_id) {
		this.local_cdi_id = local_cdi_id;
	}

	public String getCruiseRef() {
		return cruiseRef;
	}

	public void setCruiseRef(String cruiseRef) {
		this.cruiseRef = cruiseRef;
	}

	public String getCruiseName() {
		return cruiseName;
	}

	public void setCruiseName(String cruiseName) {
		this.cruiseName = cruiseName;
	}
	@Override
	public String toString(){
		return this.getReference() + " - " + this.getLocal_cdi_id() + " - "+this.getDate()+" "+this.getTime();
	}

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public long getYoungerDate() {
		return youngerDate;
	}

	public void setYoungerDate(long youngerDate) {
		this.youngerDate = youngerDate;
	}
	public long getOlderDate() {
		return olderDate;
	}

	public void setOlderDate(long olderDate) {
		this.olderDate = olderDate;
	}
	
	public String[] getComments() {
		return comments;
	}

	public void setComments(String[] comments) {
		this.comments = comments;
	}

	public void updateStationStartEnd(String readedLine, int indexRecord) {
		Long currentDate;
		try {
//				currentDate = Long.valueOf(DateTimeUtils.convertTimestamp(readedLine));
			// 33044 manage offset in end date time
//				long currentDateWithOffset = DateTimeUtils.getGMTDate(currentDate,	getOffsetTU());
			// 39448
			long currentDateWithOffset = getCurrentDate(readedLine, indexRecord);

			if (getYoungerDate() == 0 || currentDateWithOffset > getYoungerDate()) {
				setYoungerDate(currentDateWithOffset);
			}
			if (getOlderDate() == 0 || currentDateWithOffset < getOlderDate()) {
				setOlderDate(currentDateWithOffset);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//				e.printStackTrace();
		}

	}
	
	
	public void addLatLonTraj(String inputLine) throws ParseException {
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();
		
		// Read latitude
		double latitude = (Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOnLine(inputLine);
		// Read longitude
		double longitude = (Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOnLine(inputLine);
		
		LatLon latLon = new LatLon(latitude,longitude);
		latLonList.add(latLon);
		
	}
	
	/**
	 * Update station bounding box (find MIN/MAX latitude and longitude)
	 * @param readedLine
	 * @param indexRecord
	 * @throws ParseException 
	 */
	public void updateStationBoundingBox(String inputLine) throws ParseException {
		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();
		
		// Read latitude
		double currentLatitude = (Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOnLine(inputLine);
		
		if (Double.isNaN(_southlatitude)) {
			
			setSouthlatitude(currentLatitude);
		}
		else {
			setSouthlatitude(Math.min(_southlatitude, currentLatitude));
	
		}
		if (Double.isNaN(_northlatitude)) {
			
			setNorthlatitude(currentLatitude);
		}
		else {
			setNorthlatitude(Math.max(_northlatitude, currentLatitude));
	
		}

		double currentLongitude = (Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOnLine(inputLine);
		
		if (Double.isNaN(_westlongitude)) {
			
			setWestlongitude(currentLongitude);
		}
		else {
			if (Math.abs(_westlongitude - currentLongitude) > 180) {
				setWestlongitude(Math.max(_westlongitude, currentLongitude));
			}
			else {
				setWestlongitude(Math.min(_westlongitude, currentLongitude));
			}

		}
		if (Double.isNaN(_eastlongitude)) {
			
			setEastlongitude(currentLongitude);
		}
		else {
			if (Math.abs(_eastlongitude - currentLongitude) > 180) {
				setEastlongitude(Math.min(_eastlongitude, currentLongitude));
			}
			else {
				setEastlongitude(Math.max(_eastlongitude, currentLongitude));
			}

		}
	}
	
	/**
	 * Update MIN_DEPTH and MAX_DEPTH of the station during parsing.
	 * @param inputLine
	 * @throws ParseException
	 */
	public void updateMinMaxDepth(String inputLine) throws ParseException {

		StationDomain stationDomain = Nemo.getInstance().getDomain().getStation();

		if (stationDomain.getModel().getProperty(Intern.S_SENSORDEPTH).isInData()) {
			
			// Read (sensor) depth
			float currentDepth = (Float) stationDomain.getModel().getProperty(Intern.S_SENSORDEPTH)
					.applyOnLine(inputLine);

			if (Float.isNaN(_minDepth)) {
				setMinDepth(currentDepth);
			} else {
				setMinDepth(Math.min(_minDepth, currentDepth));
			}

			if (Float.isNaN(_maxDepth)) {
				setMaxDepth(currentDepth);
			} else {
				setMaxDepth(Math.max(_maxDepth, currentDepth));
			}
		}
	}
	
	public void updateMinMaxDepthFromProfile(String currentDepthOrPres) throws ParseException {
		
		try {
			float currentDepthOrPresAsFloat = Float.valueOf(currentDepthOrPres);
			if (Float.isNaN(_minDepth)) {
				setMinDepth(currentDepthOrPresAsFloat);
			} else {
				setMinDepth(Math.min(_minDepth, currentDepthOrPresAsFloat));
			}

			if (Float.isNaN(_maxDepth)) {
				setMaxDepth(currentDepthOrPresAsFloat);
			} else {
				setMaxDepth(Math.max(_maxDepth, currentDepthOrPresAsFloat));
			}
		} catch (NumberFormatException e) {
			// the Z can't be converted as float, nothing happens.
		}
	}
		
	private Long getCurrentDate( String readedLine,int indexRecord) throws ParseException,	UnsupportedEncodingException {
		
		Long currentDate=null;
		if ( Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.PROFILE
				&& !Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()){
			currentDate = DateTimeUtils.getCurrentDate(this, indexRecord); // get current date (computed with sample rate)
		}
		else  // TODO 42483: what if unknown type (bio or micro)?
//			if (Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TRAJECTORY||
//				Nemo.getInstance().getDomain().getMedia().getModel().getInputType() == E_InputType.TIMESERIES	)
		{
			// 	TIMESERIES AND TRAJECTORIES			
			if (Nemo.getInstance().getDomain().getStation().getModel().getProperty(Intern.S_DATE).isInData()){
				currentDate = Long.valueOf(DateTimeUtils.convertTimestamp(readedLine));
				long currentDateWithOffset = DateTimeUtils.getGMTDate(currentDate, this.getOffsetTU());
				currentDate = currentDateWithOffset;
			}else{
				currentDate = DateTimeUtils.getCurrentDate(this, indexRecord);
			}

		}
		return currentDate;
	}
}
