package fr.ifremer.nemo.output.netCDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.domain.DateTimeUtils;
import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.CruiseModel;
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_ADEPZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_AHGTZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DEPHPR01;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Formatter;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_EndData;
import fr.ifremer.nemo.util.Global.E_InputType;
import fr.ifremer.nemo.util.Global.E_MesureType;
import fr.ifremer.nemo.util.Global.E_ProcessType;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.seadatanet.cfpoint.CFPointWriterManager;
import fr.ifremer.seadatanet.cfpoint.SdnDimensionManager;
import fr.ifremer.seadatanet.cfpoint.SdnDimensionManager.dim_ID;
import fr.ifremer.seadatanet.cfpoint.SdnGlobalAttribute;
import fr.ifremer.seadatanet.cfpoint.exceptions.CFPointException;
import fr.ifremer.seadatanet.cfpoint.util.ParameterNames;
import fr.ifremer.seadatanet.cfpoint.util.ParameterValues;
import fr.ifremer.seadatanet.cfpoint.util.UnitConversionManager;
import fr.ifremer.seadatanet.cfpoint.variable.AncillaryVariable;
import fr.ifremer.seadatanet.cfpoint.variable.GeophysicalVariable;
import fr.ifremer.seadatanet.cfpoint.variable.ISdnVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnBotDepthVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnCrs;
import fr.ifremer.seadatanet.cfpoint.variable.SdnCruiseVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnEdmoCodeVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnLocalCdiIdVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnStationVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnT;
import fr.ifremer.seadatanet.cfpoint.variable.SdnX;
import fr.ifremer.seadatanet.cfpoint.variable.SdnXLinkVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnY;
import fr.ifremer.seadatanet.cfpoint.variable.SdnZ;
import fr.ifremer.sismer_tools.seadatanet.SDNXLinkException;
import fr.ifremer.sismer_tools.seadatanet.SdnXLinkReference;

/**
 * Description: TODO
 * 
 * @author dcapitai
 * 
 */

public class NetCDFWriter extends NemoWriter {


	String unitsTranslationFileName = "resources" + File.separator + "unitsTranslation.xml";


	private enum URNtype {
		subject, object, units, fallrate, instrument;
	}
	int indiceStation = 0;
	int indexRecord;
	CFPointWriterManager myCFPointWriterManager;
	SdnDimensionManager dims;
	Map<String, Object> stationData;
	Map<String, ISdnVariable> mapVariables;

	Map<String, String> map_P011_VarName;
	int nbStations;
	private int stringMetadataLength = 0;
	private int stringLinksLength = 0;
	private int maxNbRefs = 0;
	private final boolean forceReferences = true;
	private String zVarName;
	private String currentInputFilePath;

	public NetCDFWriter() {
		super();
		myConversion = Conversion.NETCDF;
		mapVariables = new HashMap<String, ISdnVariable>();
		stationData = new HashMap<String, Object>();
		map_P011_VarName = new HashMap<String, String>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getOutputFileName(String inputFilePath) {
		outputFileName = iDomain.getMedia().getOutputFileName();
		if (isProcessType_CollectionDir) {
			cruiseDomain.getModel().setReference(new File(inputFilePath).getName());
			outputFileName += File.separator + cruiseDomain.getModel().getReference();
		}
		return outputFileName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeFile(String inputFilePath) throws Exception {
		try {
			stationDomain.updateRef();
			outputFileName = getOutputFileName(inputFilePath);
			if (RulesManager.getInstance().isProcessCollectionDir()){
				stationDomain.resetlocalCDIList();
			}
			if (isConversionTo_NetCDFMonostation) {
				//				System.out.println("convert to n MONOstation files");
				// Conversion NETCDF - fichiers monostation => N fichiers de
				// sortie
				Integer ref = 0;
				nbStations = 1;
				for (Station st : stationDomain.getStations(inputFilePath)) {


					stringMetadataLength = 0;
					stringLinksLength = 0;
					maxNbRefs = 0;


					if (isProcessType_CollectionDir)
						new File(outputFileName).mkdir();

					if (isInputType_Trajectory) {
						ref++;
						st.setReference(ref.toString());
					}

					st=stationDomain.getAndCheckLocalCDI(cruiseDomain, Conversion.NETCDF, st);
					// WARNING: CDIResumeWriter stations list is also used for coupling table -> never delete this addstation line
					CDIResumeWriter.getInstance().addStation(inputFilePath,st);
					String mOutputFileName = outputFileName + File.separator + st.getLocal_cdi_id()+ Conversion.NETCDF.getExt();

					myCFPointWriterManager = new CFPointWriterManager(mOutputFileName);
					mapVariables = new HashMap<String, ISdnVariable>();
					map_P011_VarName = new HashMap<String, String>();
					stationData = new HashMap<String, Object>();

					// écriture de l'entête campagne
					setNetCDFMetadata(inputFilePath, st);

					if (isProcessType_CruiseFile) {
						iDomain.getStation().updateRef();
					}
					//					System.out.println("write station "+indiceStation + " - "+ st.getFileName() + "["+localCdiId+"]");
					writeStation(st, inputFilePath);


					updateDimensions();
					// SORT DATA IF NEEDED 
					if (iDomain.getConversion().isSortData())
					{
						List<Station> stations = new ArrayList<>();
						stations.add(st);
						sortDataWithinStation(stations);
					}


					writeDataToNetCDFFile();



				}
			} else {
				//				System.out.println("convert to 1 MULTIstation file");
				// conversion NETCDF multistations => 1 fichier de sortie
				nbStations = stationDomain.getStations(inputFilePath).size();
				stringMetadataLength = 0;
				stringLinksLength = 0;
				maxNbRefs = 0;

				if (isProcessType_CollectionDir) {
					outputFileName += Conversion.NETCDF.getExt();
					iDomain.getStation().updateRef();
				}
				if (myCFPointWriterManager == null || !outputFileName.equals(myCFPointWriterManager.getFileName())) {
					myCFPointWriterManager = new CFPointWriterManager(outputFileName);
					// écriture de l'entête campagne
					mapVariables = new HashMap<String, ISdnVariable>();
					map_P011_VarName = new HashMap<String, String>();
					stationData = new HashMap<String, Object>();
					setNetCDFMetadata(inputFilePath, null);
				}

				for (Station st : stationDomain.getStations(inputFilePath)) {
					if (isProcessType_CruiseFile) {
						iDomain.getStation().updateRef();
					}
					st=stationDomain.getAndCheckLocalCDI(cruiseDomain, Conversion.NETCDF, st);
					CDIResumeWriter.getInstance().addStation(inputFilePath,st);

					//					System.out.println("write station "+indiceStation+ " - "+ st.getFileName() );


					writeStation(st, inputFilePath);
					indiceStation++;
				}
				updateDimensions();

				// SORT DATA IF NEEDED 
				if (iDomain.getConversion().isSortData())
				{
					sortDataWithinStation(stationDomain.getStations(inputFilePath));
				}

				writeDataToNetCDFFile();
				stationDomain.resetStations();
				indiceStation=0;

			}

		} catch (Exception e) {
			throw new GenerationException(e);
		}

	}

	private void sortDataWithinStation(List<Station> stations)
	{
		Object[][] primaryVarValues = null;
		Object[][] varValues = null;

		String primaryVarName = null;
		if (mediaModel.getInputType() == E_InputType.PROFILE) {
			primaryVarName = zVarName;
		}else{
			primaryVarName = ParameterNames.TIME;
		}

		primaryVarValues = (Object[][]) stationData.get(primaryVarName);

		int indexStation=0;
		for (Station st : stations) {

			Double[] pv = new Double[primaryVarValues[indexStation].length];
			for (int i=0; i<primaryVarValues[indexStation].length; i++){ 
				pv[i] = (Double) primaryVarValues[indexStation][i];
			}
			ArrayIndexComparator comparator = new ArrayIndexComparator(pv);
			Integer[] indexes = comparator.createIndexArray();
			Arrays.sort(indexes, comparator);
			// Now the indexes are in appropriate order.

			// sort primary var
			Object[] primaryVarValuesSorted = new Object[primaryVarValues[indexStation].length];
			for (int i=0; i<indexes.length; i++){ 
				primaryVarValuesSorted[i] = primaryVarValues[indexStation][indexes[i]];
			}
			primaryVarValues[indexStation]=primaryVarValuesSorted;

			// sort all other vars
			Object[] sortedValues =null;
			// loop on all variables
			for (Entry<String, Object> currentStationData : stationData.entrySet()) {
				ISdnVariable sdnVar = mapVariables.get(currentStationData.getKey());
				if(!sdnVar.getVarName().equals(primaryVarName)&& sdnVar.getDims().contains(dims.getDimMax())){
					varValues = (Object[][]) stationData.get(sdnVar.getVarName());
					sortedValues = new Object[varValues[indexStation].length];
					for (int i=0; i<indexes.length; i++){ 
						sortedValues[i] = varValues[indexStation][indexes[i]];
					}

					varValues[indexStation]=sortedValues;
				}
			}
			indexStation++;
		}
	}

	/**
	 * 
	 * @param stringMetadataLength
	 * @param stringLinksLength
	 * @param maxNbRefs
	 * @throws Exception
	 */
	private void updateDimensions() throws Exception {

		if (forceReferences){
			//REFMAX
			dims = myCFPointWriterManager.updateRefmaxDimension(dims, maxNbRefs);

			// STRING_SDNREF
			dims = myCFPointWriterManager.updateStringSdnRefDimension(dims, stringMetadataLength, stringLinksLength);
		}
		// STRING_METADATA
		dims = myCFPointWriterManager.updateStringMetadataDimension(dims, stringMetadataLength);

	}

	private ArrayList<SdnXLinkReference> getReferences(String localCdiId, Station currentStation) throws SDNXLinkException
	{
		ArrayList<SdnXLinkReference> references = new ArrayList<SdnXLinkReference>();
		int nbRefs=0;
		int edmoCode = Integer.valueOf(Nemo.getInstance().getConfig().getIdEdmoRespCdi());

		//CSR
		if (cruiseDomain.getModel().getLocalCsrIdentifier() !=null && !cruiseDomain.getModel().getLocalCsrIdentifier().isEmpty()){
			SdnXLinkReference csr = new SdnXLinkReference();
			csr.setAsCsr(edmoCode, cruiseDomain.getModel().getLocalCsrIdentifier());
			computeSdnXlinkdims(csr);
			references.add(csr);
			nbRefs++;
		}

		String[] shipCodeAndName = Nemo.getInstance().getDomain().getCruise().getModel().getValidShipCodeAndName();
		if ( ! shipCodeAndName[0].isEmpty()	)
		{
			SdnXLinkReference shipRef = new SdnXLinkReference();
			shipRef.setAsShipReference(shipCodeAndName[0] );

			computeSdnXlinkdims(shipRef);
			references.add(shipRef);
			nbRefs++;
		}


		SdnXLinkReference cdiRef = new SdnXLinkReference();
		try {
			cdiRef.setAsCdi(Integer.valueOf(Nemo.getInstance().getConfig()
					.getIdEdmoRespCdi()), localCdiId, false);
			references.add(cdiRef);
			computeSdnXlinkdims(cdiRef);
			nbRefs++;
		} catch (SDNXLinkException e) {
			throw new SDNXLinkException("error while setting automatic CDI reference"); // TODO
		}
		if (maxNbRefs<nbRefs){
			maxNbRefs = nbRefs;
		}
		return references;
	}

	private void computeSdnXlinkdims(SdnXLinkReference ref){
		if (stringLinksLength<ref.toString(!isConversionTo_NetCDFMonostation).length()){
			stringLinksLength = ref.toString(!isConversionTo_NetCDFMonostation).length();
		}
	}
	/**
	 * @param st
	 * @param inputFilePath
	 * @param outputFileName
	 * @throws ParseException
	 * @throws FileNotFoundException
	 */
	private boolean writeStation(Station currentStation, String inputFilePath) throws Exception {
		//System.out.println(currentStation.getReference() + "indice= " + indiceStation);
		boolean isOnNStationHeader = false;


		StationDomain station = iDomain.getStation();
		if (indiceStation==0 ||
				new File(inputFilePath).isDirectory() 
				|| !currentInputFilePath.equals(inputFilePath))
		{
			if (bufferedReader !=null){
				bufferedReader.close();
			}
			bufferedReader = getBufferReader(inputFilePath, currentStation);
			numLine=0;
			isOnNStationHeader = false;
		}else{
			isOnNStationHeader = true;
		}

		currentInputFilePath= inputFilePath;
		boolean stationWrited = false;
		indexRecord = 0;
		stationDatas.clear();


		// Local CDI ID
		String localCdiId = stationDomain.getLocalCdiId(cruiseDomain, Conversion.NETCDF, currentStation.getReference());
		setDataValue(ParameterNames.SDN_LOCAL_CDI_ID, new Value(localCdiId), indiceStation);
		// SDN CRUISE
		String cruise;
		if (isProcessType_CollectionDir) {
			cruise = new File(inputFilePath).getName();
		}else{
			cruise = Nemo.getInstance().getDomain().getCruise().getModel().getReference();
		}

		setDataValue(ParameterNames.SDN_CRUISE, new Value(cruise),	indiceStation);
		// SDN Station
		setDataValue(ParameterNames.SDN_STATION, new Value(currentStation.getReference().trim()), indiceStation);
		// stringMetadataLength: cruise reference, station number and local cdi id strings max length given the current station
		computeMaxStringMetadataLength(localCdiId, currentStation.getReference().trim(),Nemo.getInstance().getDomain().getCruise().getModel().getReference() );



		// SDN EDMO_CODE
		setDataValue(ParameterNames.SDN_EDMO_CODE , new Value(Nemo.getInstance().getConfig().getIdEdmoRespCdi().trim()), indiceStation);


		// SDN_REFERENCE
		if (forceReferences){
			ArrayList<SdnXLinkReference> references = getReferences(localCdiId, currentStation);

			if (references != null && !references.isEmpty()){

				setDataValue(ParameterNames.SDN_XLINK, new Value(references), indiceStation);
			}
			else{
				setDataValue(ParameterNames.SDN_XLINK, null,indiceStation);
			}
		}
		if (isOnNStationHeader){
			stationWrited=readStationHeader(currentStation);
		}
		long currentDate = 0 ;
		// scan the InputFile line by line
		while ((readedLine = bufferedReader.readLine()) != null) {
			//			System.out.println(readedLine);
			incrementLine();
			if (inFileHeader())
				continue;
			if (isStartStationHeader()) {

				if (!isOnNStationHeader){
					if (stationWrited){
						return true;
					}
					stationWrited=readStationHeader(currentStation);
					iDomain.getMedia().addStationInOutput(myCFPointWriterManager.getFileName(), currentStation.getLocal_cdi_id());
				}else{
					if (stationWrited){
						iDomain.getMedia().addStationInOutput(myCFPointWriterManager.getFileName(), currentStation.getLocal_cdi_id());
						return true;
					}
				}

			} else if (stationWrited) {

				// ligne de données : à ne prendre en compte que si on est sur
				// la bonne station
				
				if (mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY) {
					double currentLongitude ;
					double currentLatitude ;
					if (stationDomain.getModel().getProperty(Intern.S_LON).isInData()){
						currentLongitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOnLine(readedLine.toString())).doubleValue();
					}else{
						currentLongitude = currentStation.getLongitude();
					}
					setDataValue(ParameterNames.LONGITUDE,new Value( String.valueOf(currentLongitude),  
							currentStation.getMetaFlag(Intern.S_LON).toCharArray()[0]),indiceStation, indexRecord);
					if (stationDomain.getModel().getProperty(Intern.S_LAT).isInData()){
						currentLatitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOnLine(readedLine.toString())).doubleValue();
					}else{
						currentLatitude = currentStation.getLatitude();
					}
					setDataValue(ParameterNames.LATITUDE, new Value(String.valueOf(currentLatitude),  
							currentStation.getMetaFlag(Intern.S_LAT).toCharArray()[0]),indiceStation,indexRecord);
				}
				
				
				// DAteTime
				if (mediaDomain.getModel().getInputType() == E_InputType.TIMESERIES || mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY) {
					currentDate = getCurrentDate(currentStation);
					Double julianDay = DateTimeUtils.getJulianDay(currentDate);
					setDataValue( ParameterNames.TIME,new Value(julianDay.toString(),  
							currentStation.getMetaFlag(Intern.S_DATE).toCharArray()[0]),indiceStation,indexRecord);
				}
				// Depth, bottom depth
				if (mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY) {

					if (station.getModel().getProperty(Intern.S_DEPTH).isInData()) {
						Object botDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOnLine(readedLine.toString());
						if (!botDepth.equals("Default") && !botDepth.equals(Float.NaN)) {
							setDataValue(ParameterNames.SDN_BOT_DEPTH,  new Value(botDepth.toString()), indiceStation,indexRecord);
						}
					} else {
						String bottomDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).getUserValue();
						if (bottomDepth.isEmpty()) {
							SdnBotDepthVariable SdnBotDepth = (SdnBotDepthVariable) mapVariables.get(ParameterNames.SDN_BOT_DEPTH);
							setDataValue(ParameterNames.SDN_BOT_DEPTH, new Value(String.valueOf(SdnBotDepth.getFillValue())),indiceStation,indexRecord);
						} else
							setDataValue(ParameterNames.SDN_BOT_DEPTH,  new Value(bottomDepth),  indiceStation,indexRecord);
					}
				}
				// profile: no metadata to be read in data
				writeDataLine(currentStation, readedLine, currentDate);

			}
		}

		return false;
	}
	
	
	private boolean readStationHeader(Station currentStation) throws Exception {
		double currentLongitude;
		double currentLatitude;
		String[] inputStationHeader = null;
		boolean stationWrited=false;
		inputStationHeader = consumeStationHeader(mediaModel.getStationHeader() > 0);
		String stRef = null;
		// si inputStationHeader est null, c'est qu'on a terminé la
		// derniere station
		if (inputStationHeader != null) {
			if (isInputType_Trajectory)
				stRef = currentStation.getReference();
			else
				stRef = iDomain.getStation().getReference(currentStation.getFileName(), inputStationHeader, true);
		}

		if (inputStationHeader != null && currentStation.getReference().equals(stRef)) {
			//					iDomain.getStation().getHeader(myConversion, currentStation, inputStationHeader);

			// FAE 4673 Pb si une seule ligne données et pas d'entête
			// station
			if (!isEndData() || (mediaModel.getEndData() == E_EndData.NEXT_START_WITH && mediaModel.getStationHeader() == 0)) {
				
				if (mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY) {
					if (stationDomain.getModel().getProperty(Intern.S_LON).isInData()){
						currentLongitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LON).applyOnLine(readedLine.toString())).doubleValue();
					}else{
						currentLongitude = currentStation.getLongitude();
					}
					if (stationDomain.getModel().getProperty(Intern.S_LAT).isInData()){
						currentLatitude = ((Double) stationDomain.getModel().getProperty(Intern.S_LAT).applyOnLine(readedLine.toString())).doubleValue();
					}else{
						currentLatitude = currentStation.getLatitude();
					}
				}else{
					currentLongitude = currentStation.getLongitude();
					currentLatitude = currentStation.getLatitude();
				}
					
				Long currentDate = getCurrentDate(currentStation);
				
				// Longitude
				setDataValue(ParameterNames.LONGITUDE, new Value( String.valueOf(currentLongitude), currentStation.getMetaFlag(Intern.S_LON).toCharArray()[0]),indiceStation, indexRecord);
				// Latitude
				setDataValue( ParameterNames.LATITUDE, new Value(String.valueOf(currentLatitude),currentStation.getMetaFlag(Intern.S_LAT).toCharArray()[0]),indiceStation, indexRecord);
				// dateTime
				Double julianDay = DateTimeUtils.getJulianDay(currentDate);
				setDataValue(ParameterNames.TIME, new Value(julianDay.toString(),  currentStation.getMetaFlag(Intern.S_DATE).toCharArray()[0]),indiceStation, indexRecord);

				// SDN_BOT_DEPTH
				Object botDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).applyOn(inputStationHeader);
				if (!botDepth.equals("Default") && !botDepth.equals(Float.NaN)) {
					setDataValue(ParameterNames.SDN_BOT_DEPTH,  new Value(botDepth.toString()),indiceStation);
				} else {
					String bottomDepth = stationDomain.getModel().getProperty(Intern.S_DEPTH).getUserValue();
					if (bottomDepth.isEmpty()) {
						SdnBotDepthVariable SdnBotDepth = (SdnBotDepthVariable) mapVariables.get(ParameterNames.SDN_BOT_DEPTH);
						setDataValue(ParameterNames.SDN_BOT_DEPTH,new Value( String.valueOf(SdnBotDepth.getFillValue())), indiceStation, indexRecord);
					} else
						setDataValue(ParameterNames.SDN_BOT_DEPTH, new Value( bottomDepth), indiceStation, indexRecord);
				}

				writeDataLine(currentStation, readedLine, currentDate);
			}
			stationWrited = true;
		} 
		return stationWrited;
			}

	private Long getCurrentDate(Station currentStation) throws ParseException,	UnsupportedEncodingException {
		
		Long currentDate;
		if (mediaDomain.getModel().getInputType() == E_InputType.PROFILE){
			currentDate = DateTimeUtils.getCurrentDate(currentStation, indexRecord); // get current date (computed with sample rate)
		}
		else{
			// 	TIMESERIES AND TRAJECTORIES			
			if (stationDomain.getModel().getProperty(Intern.S_DATE).isInData()){
				currentDate = Long.valueOf(DateTimeUtils.convertTimestamp(readedLine));
				long currentDateWithOffset = DateTimeUtils.getGMTDate(currentDate, currentStation.getOffsetTU());
				currentDate = currentDateWithOffset;
			}else{
				currentDate = DateTimeUtils.getCurrentDate(currentStation, indexRecord);
			}

		}
		return currentDate;
	}


	private void computeMaxStringMetadataLength(String localCdiId, String stationRef, String dataSetRef) {
		if (stringMetadataLength< localCdiId.length()){
			stringMetadataLength = localCdiId.length();
		}
		if (stringMetadataLength <stationRef.length() )
		{
			stringMetadataLength = stationRef.length();
		}
		if (stringMetadataLength <dataSetRef.length() )
		{
			stringMetadataLength = dataSetRef.length();
		}
	}

	public void setDataValue(String variableName, Value value) throws CFPointException {
		//		System.out.println("st=0 / rec=0 / val = "+variableName+"="+ value.getValue());
		setDataValue(variableName, value, 0, 0);
	}

	public void setDataValue(String variableName, Value value, int indiceStation) throws CFPointException {
		//		System.out.println("st="+indiceStation+" / rec=0 / val = "+variableName+"="+ value.getValue());
		setDataValue(variableName, value, indiceStation, 0);
	}

	/**
	 * 
	 * @param variableName
	 * @param value
	 * @param indiceStation
	 * @param indiceRecord
	 * @throws Exception
	 */
	public void setDataValue(String variableName, Value value, int indiceStation, int indiceRecord) throws CFPointException{
		//		System.out.println("st="+indiceStation+" / rec="+indiceRecord+" / val = "+variableName+"="+ value.getValue());
		myCFPointWriterManager.setDataValue(dims, 
				mapVariables,
				stationData,
				variableName, value.getValue(), (byte)value.getQualityValue(),
				indiceStation, indiceRecord);
	}
	
	
	private void writeDataLine(Station currentStation, String inputLine, long currentDate) throws Exception {


		String varName;String defaultValue;String stringValue;String inputDefaultValue;
		// parametre ajouté
		for (ParameterModel pModel : dataDomain.getMyParameterManager().getNetCDFParameterModels()) {
			boolean compute=true;
			varName = pModel.getParameter().getCodeP011();
			if(map_P011_VarName.get(varName)!=null){
				varName=map_P011_VarName.get(varName);
			}else{
				varName = pModel.getParameter().getCodeP011();
			}
			
			// for variables with dimension INSTANCE only, do not compute on each line!
			if (mapVariables.get(varName).getDims().size()==1 && indexRecord>0){
				compute=false;
			}
			if (compute){
				pModel.computeAddedParameterValue(currentStation, inputLine, currentDate);

				defaultValue = pModel.getParameter().getDefault().trim();
				stringValue = pModel.getLineValue().getValue().trim();
				inputDefaultValue = pModel.getInputDefault().trim();
				if (!stringValue.equals(inputDefaultValue))
					setDataValue(varName,new Value( stringValue,  pModel.getLineValue().getQc().toCharArray()[0]), indiceStation, indexRecord);
				else {
					if (defaultValue != null && !defaultValue.isEmpty())
						setDataValue(varName, new Value(defaultValue, pModel.getLineValue().getQc().toCharArray()[0]), indiceStation,indexRecord);
					else
						setDataValue(varName,new Value( null,  pModel.getLineValue().getQc().toCharArray()[0]), indiceStation,indexRecord);
				}
			}
		}

		// paramètres mesurés
		for (ParameterModel pModel : dataDomain.getModel().getParameterModels()) {

			varName = pModel.getParameter().getCodeP011();
			if(map_P011_VarName.get(varName)!=null){
				varName=map_P011_VarName.get(varName);
			}else{
				varName = pModel.getParameter().getCodeP011();
			}

			pModel.computeParameterValue(inputLine);
			inputDefaultValue = pModel.getInputDefault().trim();
			defaultValue = pModel.getParameter().getDefault();
			stringValue = Global.EMPTY_STRING;
			if (!pModel.getLineValue().getValue().isEmpty()) {
				stringValue = Formatter.getSingleton().formatParameter(pModel.getParameter().getFormat(), pModel.getLineValue().getValue()).trim();
			}
			try{
				if (!stringValue.equals(inputDefaultValue)){
					// TODO BGT: pb si varName Z est modifié -> les valeurs sont incorrectes
					setDataValue(varName, new Value( stringValue, pModel.getLineValue().getQc().toCharArray()[0]), indiceStation,indexRecord);
					//				setDataValue(getZVarName(getZParameterModel()), new Value( stringValue, pModel.getLineValue().getQc().toCharArray()[0]), indiceStation);
				}
				else {
					if (defaultValue != null && !defaultValue.isEmpty())
						setDataValue(varName, new Value(defaultValue, pModel.getLineValue().getQc().toCharArray()[0]), indiceStation,indexRecord);
					else
						setDataValue(varName, new Value(null,  pModel.getLineValue().getQc().toCharArray()[0]),indiceStation,indexRecord);
				}
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
				throw new GenerationException("error on data for variable "+varName+ " data="+stringValue + " station nb="+indiceStation + "indexRecord " + indexRecord +". "+ e.getMessage());
			}
		}
		indexRecord++;
	}

	public void writeDataToNetCDFFile() throws Exception {

		for (Entry<String, Object> currentStationData : stationData.entrySet()) {
			myCFPointWriterManager.writeDataToVariable(dims, mapVariables.get(currentStationData.getKey()), currentStationData.getValue());
		}
		// close file after write all data
		myCFPointWriterManager.finish();
	}


	/**
	 *  SDN reference dimension is always (INSTANCE, REFMAX, STRING80)
	 * @return
	 */
	private List<Dimension> getSDNXlinkDimensions(){
		List<Dimension> listDimensions = new ArrayList<Dimension>();
		listDimensions.add(dims.getDimInstance());
		listDimensions.add(dims.getRefMax());
		listDimensions.add(dims.getDimension(dim_ID.STRING_SDNREF));
		return listDimensions;
	}
	/**
	 * initialise NetCDFWriter with station metadata
	 * ************************************************************* scheduling
	 * variable is important. it defines the order in the CFPoint file
	 * **************************************************************
	 * 
	 * @param inputFilePath
	 * @param st
	 * @throws Exception
	 */
	public void setNetCDFMetadata(String inputFilePath, Station st) throws Exception {

		// create Dimension
		setSdnDimension(inputFilePath, st);

		List<Dimension> instanceDim = new ArrayList<Dimension>();
		instanceDim.add(dims.getDimInstance());

		List<Dimension> geoDim = new ArrayList<Dimension>();
		geoDim.add(dims.getDimInstance());

		if (mediaModel.getInputType() == E_InputType.PROFILE)
			geoDim.add(dims.getDimMaxZ());
		else
			geoDim.add(dims.getDimMaxT());

		List<Dimension> sdnDim = new ArrayList<Dimension>();
		sdnDim.add(dims.getDimInstance());
		sdnDim.add(dims.getDimension(dim_ID.STRING_METADATA));


		UnitConversionManager unitConversion = UnitConversionManager.getInstance(unitsTranslationFileName);

		// add to net CDF
		myCFPointWriterManager.addMandatoryDimensions(dims);
		myCFPointWriterManager.addSdnReferenceDimension(dims);
		// create Global Attributes
		String title = "NetCDF " + mediaModel.getInputType() + " - Generated by NEMO, version " + Nemo.getInstance().getVersion(); //$NON-NLS-1$//$NON-NLS-2$
		SdnGlobalAttribute globattr = new SdnGlobalAttribute(getFeatureType(), title, new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").format(new Date())); //$NON-NLS-1$
		myCFPointWriterManager.addGlobalAttributes(globattr);
		SdnX longitude = null;
		SdnY latitude = null;
		SdnT time = null;
		SdnZ z = null;

		// z Variable
		ParameterModel zParameterModel = getZParameterModel();

		zVarName = getZVarName(zParameterModel);
		map_P011_VarName.put(zParameterModel.getParameter().getCodeP011(), zVarName);


		String standardName = getZStandardName(zParameterModel);
		String longName = NetCDFLongNameParameterManager.getInstance().getUserLongName(zParameterModel.getParameter().getCodeP011());
		if (longName == null) {
			//17499 + 25656
			if (zParameterModel.getParameter() instanceof AddedParam && zParameterModel.getParameter().getNameP011() != null) {
				longName = zParameterModel.getParameter().getNameP011();
			} else {
				throw new Exception(Messages.Etape_134 + Global.SPACE + zParameterModel.getParameter().getCodeP011());
			}
		}
		String unit = unitConversion.getUnitConversion(zParameterModel.getParameter().getUnit());
		String sdnParameterUrn = getURNSyntaxe(URNtype.object, zParameterModel.getParameter().getCodeP011(), null, Intern.mapping_objet, zParameterModel.isText());
		String sdnParameterName = zParameterModel.getParameter().getNameP011();

		String sdnUomUrn;
		if (zParameterModel.getParameter().getUnitCode() == null || zParameterModel.getParameter().getUnitCode().isEmpty()) {
			sdnUomUrn = getURNSyntaxe(URNtype.units, zParameterModel.getParameter().getCode(), Intern.mapping_subject, Intern.mapping_unit, zParameterModel.isText());
		} else {
			sdnUomUrn = getURNSyntaxe(URNtype.units, zParameterModel.getParameter().getUnitCode(), null, Intern.mapping_unit, zParameterModel.isText());
		}
		String sdnUomName = zParameterModel.getParameter().getUnit();
		String sdnBotDepthLongName = Global.EMPTY_STRING;

		SdnBotDepthVariable SdnBotDepth = new SdnBotDepthVariable(instanceDim, sdnBotDepthLongName);

		// create Co-ordinate Variable
		if (mediaModel.getInputType() == E_InputType.TRAJECTORY) {
			sdnBotDepthLongName = Messages.typeFile_trajectory;
			longitude = new SdnX(geoDim);
			latitude = new SdnY(geoDim);
			time = new SdnT(geoDim);
			z = new SdnZ(zVarName, geoDim, longName, standardName, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);
			SdnBotDepth = new SdnBotDepthVariable(geoDim, sdnBotDepthLongName);
		} else if (mediaModel.getInputType() == E_InputType.PROFILE) {
			sdnBotDepthLongName = Messages.typeFile_profile;
			longitude = new SdnX(instanceDim);
			latitude = new SdnY(instanceDim);
			time = new SdnT(instanceDim);
			z = new SdnZ(zVarName, geoDim, longName, standardName, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);
		} else {
			sdnBotDepthLongName = Messages.typeFile_timeseries;
			longitude = new SdnX(instanceDim);
			latitude = new SdnY(instanceDim);
			time = new SdnT(geoDim);
			z = new SdnZ(zVarName, instanceDim, longName, standardName, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);

		}


		String instrumentCode = zParameterModel.getParameter().getInstrumentCode() ;
		if (instrumentCode!=null && !instrumentCode.isEmpty()){
			z.setInstrument(zParameterModel.getParameter().getInstrument(), instrumentCode);
		}

		String fallrateCode = Nemo.getInstance().getDomain().getData().getModel().getFallrateCode();
		if (fallrateCode!=null && !fallrateCode.isEmpty()){

			z.setFall_rate(Nemo.getInstance().getDomain().getData().getModel().getFallrateLabel(), fallrateCode);
		}

		map_P011_VarName.put(longitude.getSdnParameterName(), longitude.getVarName());
		// add DefaultFillValue
		longitude.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));
		latitude.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));
		time.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));
		z.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));

		// add default value if existe
		if (!zParameterModel.getParameter().getDefault().isEmpty())
			z.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, zParameterModel.getParameter().getDefault()));


		// POSITIVE attribute is now automatically managed in cfpointLib

		// Sdn variable
		SdnEdmoCodeVariable sdnEdmoCode = new SdnEdmoCodeVariable(instanceDim);
		mapVariables.put(sdnEdmoCode.getVarName(), sdnEdmoCode);
		addVariableToNetCDF(sdnEdmoCode);

		SdnCruiseVariable sdnCruise = new SdnCruiseVariable(sdnDim, getlongNameCruise());
		addCruiseOptionalAttribute(sdnCruise);
		mapVariables.put(sdnCruise.getVarName(), sdnCruise);
		addVariableToNetCDF(sdnCruise);

		SdnStationVariable sdnStation = new SdnStationVariable(sdnDim);
		mapVariables.put(sdnStation.getVarName(), sdnStation);
		addVariableToNetCDF(sdnStation);

		SdnLocalCdiIdVariable SdnLocalCdi = new SdnLocalCdiIdVariable(sdnDim, getFeatureType().toLowerCase() + "_id");
		mapVariables.put(SdnLocalCdi.getVarName(), SdnLocalCdi);
		addVariableToNetCDF(SdnLocalCdi);
		if (forceReferences){
			SdnXLinkVariable srnRef = new SdnXLinkVariable(getSDNXlinkDimensions());
			mapVariables.put(srnRef.getVarName(), srnRef);
			addVariableToNetCDF(srnRef);
		}
		mapVariables.put(SdnBotDepth.getVarName(), SdnBotDepth);
		addVariableToNetCDF(SdnBotDepth);

		mapVariables.put(longitude.getVarName(), longitude);
		addVariableToNetCDF(longitude);
		mapVariables.put(latitude.getVarName(), latitude);
		addVariableToNetCDF(latitude);

		SdnCrs crs = new SdnCrs();
		mapVariables.put(crs.getVarName(), crs);
		addVariableToNetCDF(crs);
		// add default value for crs
		setDataValue(crs.getVarName(), new Value(ParameterValues.SDN_CRS_DEFAULT_VALUE));

		mapVariables.put(time.getVarName(), time);
		addVariableToNetCDF(time);
		//		mapVariables.put(zParameterModel.getParameter().getCodeP011(), z);
		mapVariables.put(z.getVarName(), z);

		addVariableToNetCDF(z);

		// Geophysical variable
		ParameterModel[] paramAdded = dataDomain.getMyParameterManager().getNetCDFParameterModels();
		ParameterModel[] paramMesured = dataDomain.getModel().getParameterModels();
		// concaténation
		ParameterModel[] params = new ParameterModel[paramAdded.length + paramMesured.length];
		System.arraycopy(paramAdded, 0, params, 0, paramAdded.length);
		System.arraycopy(paramMesured, 0, params, paramAdded.length, paramMesured.length);

		for (ParameterModel param : params) {
			// varName = param.getParameter().getLocalCode();
			String varName = param.getParameter().getCodeP011();
			longName = NetCDFLongNameParameterManager.getInstance().getUserLongName(param.getParameter().getCodeP011());
			if (longName == null) {//17499 + 25656
				if (param.getParameter() instanceof AddedParam &&   param.getParameter().getNameP011() != null) {
					longName = param.getParameter().getNameP011();
				} else {
					throw new Exception(Messages.Etape_134 + Global.SPACE + param.getParameter().getCodeP011());
				}
			}
			sdnParameterUrn = getURNSyntaxe(URNtype.object, param.getParameter().getCodeP011(), null, Intern.mapping_objet, param.isText());
			sdnParameterName = param.getParameter().getNameP011();
			if (param.getParameter().getUnitCode() == null || param.getParameter().getUnitCode().isEmpty()) {
				sdnUomUrn = getURNSyntaxe(URNtype.units, param.getParameter().getCode(), Intern.mapping_subject, Intern.mapping_unit, param.isText());
			} else {
				sdnUomUrn = getURNSyntaxe(URNtype.units, param.getParameter().getUnitCode(), null, Intern.mapping_unit, param.isText());
			}
			sdnUomName = param.getParameter().getUnit();

			unit = unitConversion.getUnitConversion(param.getParameter().getUnit());

			if (!sdnParameterUrn.equals(z.getSdnParameterUrn())) {
				String coordinates = myCFPointWriterManager.getCoordinatesAttributeString(longitude, latitude, z, time);
				GeophysicalVariable current = new GeophysicalVariable(varName, getUnits(param.getParameter().getFormat()), geoDim, longName, coordinates, unit, sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);

				// add default value if existe
				if (!param.getParameter().getDefault().isEmpty())
					current.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, param.getParameter().getDefault()));
				else
					current.addVariableAttribute(ParameterNames.FILLVALUE, convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));
				if (!param.getParameter().getStandardName().isEmpty())
					current.addVariableAttribute(ParameterNames.STANDARD_NAME, param.getParameter().getStandardName());



				instrumentCode = param.getParameter().getInstrumentCode() ;
				if (instrumentCode!=null && !instrumentCode.isEmpty()){
					current.setInstrument(param.getParameter().getInstrument() , instrumentCode);
				}

				mapVariables.put(current.getVarName(), current);
				addVariableToNetCDF(current);

			}
		}

	}

	private ParameterModel getZParameterModel() {
		ParameterModel zParameterModel = null;
		if (mediaModel.getInputType() != E_InputType.TIMESERIES || dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			zParameterModel = dataDomain.getModel().getParameterModelByP011(getZLocalName());

		// Si Parametre vertical non selectionné prendre le premiser parametre
		// mesuré
		if (zParameterModel == null) {
			zParameterModel = dataDomain.getMyParameterManager().getNetCDFParameterModels()[0];
		}
		return zParameterModel;
	}

	/**
	 * get longname from ProcessType and input Type
	 * 
	 * @return SDN_CRUISE Long Name
	 * @throws Exception
	 */
	private String getlongNameCruise() throws Exception {
		// 14926 et 14931
		// LongName suivant ProcessType
		String longNameCruise = Global.EMPTY_STRING;
		if (mediaModel.getProcessType() == E_ProcessType.COLLECTION_DIR) {
			longNameCruise = Nemo.getInstance().getDomain().getCruise().getModel().getReference();
		} else if (mediaModel.getProcessType() == E_ProcessType.COLLECTION_FILES) {
			// 18332
			switch (mediaModel.getInputType()) {
			case PROFILE:
				longNameCruise = ParameterValues.SDN_CRUISE_LONG_NAME_CRUISE_PROFILE;
				break;
			case TIMESERIES:
				longNameCruise = ParameterValues.SDN_CRUISE_LONG_NAME_CRUISE_TIME_SERIES;
				break;
			case TRAJECTORY:
				longNameCruise = ParameterValues.SDN_CRUISE_LONG_NAME_CRUISE_TRAJECTORY;
				break;
			}
		} else {
			longNameCruise = cruiseDomain.getModel().getName().trim();
		}
		return longNameCruise;
	}

	private void addVariableToNetCDF(ISdnVariable current) throws Exception {
		if (current.isSDNCompliant()) {
			myCFPointWriterManager.addSdnVariable(current);
			AncillaryVariable currentAncillary = myCFPointWriterManager.getAncillaryVariableFromSdnVariable(current);
			if (currentAncillary != null)
				mapVariables.put(currentAncillary.getVarName(), currentAncillary);
		} else
			throw new Exception("Variable " + current.getVarName() + " is not SDN Compliant");
	}

	/**
	 * @param sdnCruise
	 */
	private void addCruiseOptionalAttribute(SdnCruiseVariable sdnCruise) {
		SimpleDateFormat fmt = new SimpleDateFormat(CruiseModel.CSR_DATE_FORMAT);
		if (!(isProcessType_CollectionFiles || isProcessType_CollectionDir)) {
			// sous onglet Reference
			if (cruiseDomain.getModel().getShipCode() != null && !cruiseDomain.getModel().getShipCode().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_shipcode, cruiseDomain.getModel().getShipCode());
			if (cruiseDomain.getModel().getShipName() != null && !cruiseDomain.getModel().getShipName().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_shipname, cruiseDomain.getModel().getShipName());
		}

		if (!isProcessType_CollectionFiles) {
			// sous onglet Location
			try {
				sdnCruise.addVariableAttribute(ModelDescription.node_startdate, fmt.format(new Date(cruiseDomain.getModel().getStartDate())));
			} catch (Exception e) {
			}
			try {
				sdnCruise.addVariableAttribute(ModelDescription.node_enddate, fmt.format(new Date(cruiseDomain.getModel().getEndDate())));
			} catch (Exception e) {
			}
		}

		if (!(isProcessType_CollectionFiles || isProcessType_CollectionDir)) {
			if (cruiseDomain.getModel().getRegionName() != null && !cruiseDomain.getModel().getRegionName().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_region, cruiseDomain.getModel().getRegionName());

			// date source
			if (cruiseDomain.getModel().getCountryCode() != null && !cruiseDomain.getModel().getCountryCode().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_country, cruiseDomain.getModel().getCountryCode());
			if (cruiseDomain.getModel().getLaboratories() != null && !cruiseDomain.getModel().getLaboratories().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_laboratories, cruiseDomain.getModel().getLaboratories());
			if (cruiseDomain.getModel().getChiefScientist() != null && !cruiseDomain.getModel().getChiefScientist().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_chief, cruiseDomain.getModel().getChiefScientist());
			if (cruiseDomain.getModel().getProjectName() != null && !cruiseDomain.getModel().getProjectName().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_project, cruiseDomain.getModel().getProjectName());

			// archive center
			if (cruiseDomain.getModel().getDataCentre() != null && !cruiseDomain.getModel().getDataCentre().isEmpty())
				sdnCruise.addVariableAttribute(ModelDescription.node_datacenter, cruiseDomain.getModel().getDataCentre());
			if (cruiseDomain.getModel().getAvailability() != null)
				sdnCruise.addVariableAttribute(ModelDescription.node_availability, cruiseDomain.getModel().getAvailability().name());



		}
		// Comment
		if (cruiseDomain.getModel().getComment() != null && !cruiseDomain.getModel().getComment().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_comment, cruiseDomain.getModel().getComment());
	}

	private String getFeatureType() {
		return ParameterNames.getString(mediaModel.getInputType().toString());
	}

	private DataType getUnits(String format) {
		int size = format.length();

		if (size > 0) {
			char type = format.charAt(size - 1);
			if (type == 'f' || type == 'F')
				return DataType.DOUBLE;
			if (type == 'D' || type == 'd')
				return DataType.INT;
		}
		return DataType.STRING;
	}

	private String getZLocalName() {

		if (!dataDomain.getModel().getVerticalParametersList().getP_heightASLvl().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			return dataDomain.getModel().getVerticalParametersList().getP_heightASLvl();

		if (!dataDomain.getModel().getVerticalParametersList().getP_depthBSBed().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.BSBED)
			return dataDomain.getModel().getVerticalParametersList().getP_depthBSBed();

		if (!dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf();

		if (!dataDomain.getModel().getVerticalParametersList().getP_pressure().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return dataDomain.getModel().getVerticalParametersList().getP_pressure();

		return null;
	}

	private String getZVarName(ParameterModel zParameterModel) {
		if (!dataDomain.getModel().getVerticalParametersList().getP_depthBSBed().isEmpty() 
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSBED)
			return ParameterNames.DEPTH;

		if (!dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf().isEmpty() 
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return ParameterNames.DEPTH;

		if (!dataDomain.getModel().getVerticalParametersList().getP_heightASLvl().isEmpty() 
				&& dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			return ParameterNames.HEIGHT;

		if (!dataDomain.getModel().getVerticalParametersList().getP_pressure().isEmpty() 
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return ParameterNames.PRESS;

		// si parametre vertical non selection alors z peut etre un parametre
		// mesuré
		AP_ADEPZZ01 zADEPZZ01 = new AP_ADEPZZ01(Conversion.NETCDF);
		if (zADEPZZ01.getCode().equals(zParameterModel.getParameter().getCode()))
			return ParameterNames.DEPTH;

		AP_DEPHPR01 zDEPHPR01 = new AP_DEPHPR01(Conversion.NETCDF);
		if (zDEPHPR01.getCode().equals(zParameterModel.getParameter().getCode()))
			return ParameterNames.DEPTH;

		AP_AHGTZZ01 zAHGTZZ01 = new AP_AHGTZZ01(Conversion.NETCDF);
		if (zAHGTZZ01.getCode().equals(zParameterModel.getParameter().getCode()))
			return ParameterNames.HEIGHT;

		return null;
	}

	private String getZStandardName(ParameterModel zParameterModel) {
		if (!dataDomain.getModel().getVerticalParametersList().getP_pressure().isEmpty() && mediaModel.getInputType() != E_InputType.TIMESERIES)
			return ParameterValues.Z_PRESSURE;

		if (!dataDomain.getModel().getVerticalParametersList().getP_depthBSSurf().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return ParameterValues.Z_DEPTH;

		if (!dataDomain.getModel().getVerticalParametersList().getP_heightASLvl().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			return ParameterValues.Z_HEIGHT;

		if (!dataDomain.getModel().getVerticalParametersList().getP_depthBSBed().isEmpty() && dataDomain.getModel().getMesureType() == E_MesureType.BSBED)
			return ParameterValues.Z_DEPTH;

		// si parametre vertical non selection alors z peut etre un parametre
		// mesuré
		AP_ADEPZZ01 zADEPZZ01 = new AP_ADEPZZ01(Conversion.NETCDF);
		if (zADEPZZ01.getCode().equals(zParameterModel.getParameter().getCode()))
			return ParameterValues.Z_DEPTH;

		AP_DEPHPR01 zDEPHPR01 = new AP_DEPHPR01(Conversion.NETCDF);
		if (zDEPHPR01.getCode().equals(zParameterModel.getParameter().getCode()))
			return ParameterValues.Z_DEPTH;

		AP_AHGTZZ01 zAHSLZZ01 = new AP_AHGTZZ01(Conversion.NETCDF);
		if (zAHSLZZ01.getCode().equals(zParameterModel.getParameter().getCode()))
			return ParameterValues.Z_HEIGHT;

		return null;
	}

	private void setSdnDimension(String inputFilePath, Station st) {
		// create Dimension
		int instance = 1;
		int nbligne = 0;
		Integer maxZ = null;
		Integer maxT = null;

		if (isConversionTo_NetCDFMonostation) {
			nbligne = st.getNbRecords();
		} else {
			instance = stationDomain.getStations(inputFilePath).size();
			for (Station currentStation : stationDomain.getStations(inputFilePath)) {
				if (nbligne < currentStation.getNbRecords())
					nbligne = currentStation.getNbRecords();
			}
		}

		if (mediaModel.getInputType() == E_InputType.PROFILE)
			maxZ = nbligne; // nombre de ligne dans la station
		else
			maxT = nbligne; // nombre de ligne dans la station

		dims = new SdnDimensionManager(instance, maxZ, maxT);

	}

	private static String getURNSyntaxe(URNtype type, String code, String list1, String list2, Boolean isText) throws GenerationException {
		StringBuffer urnSyntaxe = new StringBuffer();

		switch (type) {
		case subject:
			urnSyntaxe.append(Global.SDN);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Intern.file_mapping_subject);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(code);
			break;
		case object:
			urnSyntaxe.append(Global.SDN);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodeobject;
			if (list1 != null)
				pdvCodeobject = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodeobject = code;

			if (pdvCodeobject==null) {

				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramcode, code, list1, list2));
			}
			urnSyntaxe.append(pdvCodeobject);
			break;
		case units:
			urnSyntaxe.append(Global.SDN);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodeunits;
			if (list1 != null || isText)
				pdvCodeunits = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodeunits = code;
			if (pdvCodeunits==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramunit, code, list1, list2));
			}
			// FAE 10755
			if (isText)
				urnSyntaxe.append(Intern.NemoLists_unknown);
			else
				urnSyntaxe.append(pdvCodeunits);
			break;
		case fallrate:
			urnSyntaxe.append(Global.SDN);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodefallrate;
			if (list1 != null)
				pdvCodefallrate = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodefallrate = code;
			if (pdvCodefallrate==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramcode, code, list1, list2));
			}
			urnSyntaxe.append(pdvCodefallrate);
			break;
		case instrument:
			urnSyntaxe.append(Global.SDN);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(list2);
			urnSyntaxe.append(Global.DBL_PT);
			urnSyntaxe.append(Global.DBL_PT);
			String pdvCodeinstrument;
			if (list1 != null)
				pdvCodeinstrument = VocabLists.getVocabMatch(list1, list2, code, Intern.NemoLists_unknown);
			else
				pdvCodeinstrument = code;
			if (pdvCodeinstrument==null) {
				throw new GenerationException(MessageFormat.format(Messages.error_convert_sdnmapping_paramcode, code, list1, list2));
			}
			urnSyntaxe.append(pdvCodeinstrument);
			break;
		}

		return urnSyntaxe.toString();
	}

	private static Object convertValue(DataType type, Object value) {

		if (value != null && value.toString().length() > 0) {
			if (type == DataType.FLOAT)
				return Float.valueOf(value.toString()).floatValue();
			if (type == DataType.DOUBLE)
				return Double.valueOf(value.toString()).doubleValue();
			if (type == DataType.INT)
				return Integer.valueOf(value.toString()).intValue();
			if (type == DataType.BYTE)
				return value.toString().getBytes()[0];
			if (type == DataType.CHAR) {
				return value.toString();
			}

		}
		return null;

	}

}
