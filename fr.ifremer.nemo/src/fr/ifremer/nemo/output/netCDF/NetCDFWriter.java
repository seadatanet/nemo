/* ============================================================================
 * Nom du fichier	: NetCDFWriter.java
 * Auteur			: dcapitai
 * Date				: 15 oct. 2012
 * Copyright		: Altran
 * ============================================================================
 */
package fr.ifremer.nemo.output.netCDF;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import fr.ifremer.nemo.input.ModelDescription;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_ADEPZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_AHGTZZ01;
import fr.ifremer.nemo.input.parameters.addedParameters.AP_DEPHPR01;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
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
import fr.ifremer.seadatanet.cfpoint.SdnGlobalAttribute;
import fr.ifremer.seadatanet.cfpoint.util.ParameterNames;
import fr.ifremer.seadatanet.cfpoint.util.ParameterValues;
import fr.ifremer.seadatanet.cfpoint.util.UnitConversionManager;
import fr.ifremer.seadatanet.cfpoint.variable.AncillaryVariable;
import fr.ifremer.seadatanet.cfpoint.variable.CoOrdinateVariable;
import fr.ifremer.seadatanet.cfpoint.variable.GeophysicalVariable;
import fr.ifremer.seadatanet.cfpoint.variable.ISdnVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnBotDepthVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnCrs;
import fr.ifremer.seadatanet.cfpoint.variable.SdnCruiseVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnEdmoCodeVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnLocalCdiIdVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnReferenceVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnStationVariable;
import fr.ifremer.seadatanet.cfpoint.variable.SdnT;
import fr.ifremer.seadatanet.cfpoint.variable.SdnX;
import fr.ifremer.seadatanet.cfpoint.variable.SdnY;
import fr.ifremer.seadatanet.cfpoint.variable.SdnZ;


/**
 * Description: TODO
 * 
 * @author dcapitai
 * 
 */

public class NetCDFWriter extends NemoWriter {
	private enum URNtype {
		subject, object, units;
	}

	int indexRecord;
	CFPointWriterManager myCFPointWriterManager;
	SdnDimensionManager dims;
	Map<String, Object[][]> stationData;
	Map<String, ISdnVariable> variables;
	int nbStations;

	public NetCDFWriter() {
		super();
		myConversion = Conversion.NETCDF;
		variables = new HashMap<String, ISdnVariable>();
		stationData = new HashMap<String, Object[][]>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getOutputFileName(String inputFilePath) {
		outputFileName = iDomain.getMedia().getOutputFileName();
		if (isProcessType_CollectionDir) {
			cruiseDomain.setDataSetRef(new File(inputFilePath).getName());
			outputFileName += File.separator + cruiseDomain.getDataSetRef();
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

			if (isConversionTo_NetCDFMonostation) {
				// Conversion NETCDF - fichiers monostation => N fichiers de
				// sortie
				Integer ref = 0;
				nbStations = 1;

				for (Station st : stationDomain.getStations(inputFilePath)) {

					if (isProcessType_CollectionDir)
						new File(outputFileName).mkdir();

					if (isInputType_Trajectory) {
						ref++;
						st.setReference(ref.toString());
					}

					String localCdiId = stationDomain.getAndCheckLocalCDI(
							cruiseDomain, Conversion.ODV, st.getReference());
					String mOutputFileName = outputFileName + File.separator
							+ localCdiId + Conversion.NETCDF.getExt();

					myCFPointWriterManager = new CFPointWriterManager(mOutputFileName);
					variables = new HashMap<String, ISdnVariable>();
					stationData = new HashMap<String, Object[][]>();

					// écriture de l'entête campagne
					setNetCDFHeader(inputFilePath, st);

					if (isProcessType_CruiseFile) {
						iDomain.getStation().updateRef();
					}
					writeStation(st, inputFilePath, 0);
					writeDataToNetCDFFile();
					// TODO for debug
					// System.out.println(myCFPointWriterManager);
				}
			} else {
				// conversion NETCDF multistations => 1 fichier de sortie
				nbStations = stationDomain.getStations(inputFilePath).size();
				if (isProcessType_CollectionDir) {
					outputFileName += Conversion.NETCDF.getExt();
					iDomain.getStation().updateRef();
				}
				if (myCFPointWriterManager == null
						|| !outputFileName.equals(myCFPointWriterManager.getFileName())) {
					myCFPointWriterManager = new CFPointWriterManager(outputFileName);
					// écriture de l'entête campagne
					variables = new HashMap<String, ISdnVariable>();
					stationData = new HashMap<String, Object[][]>();
					setNetCDFHeader(inputFilePath, null);
				}
				int indiceStation = 0;
				for (Station st : stationDomain.getStations(inputFilePath)) {
					if (isProcessType_CruiseFile) {
						iDomain.getStation().updateRef();
					}
					writeStation(st, inputFilePath, indiceStation);
					indiceStation++;
				}

				writeDataToNetCDFFile();
				myCFPointWriterManager.finish();
			}

		} catch (Exception e) {
			throw new GenerationException(e);
		}

	}

	/**
	 * @param st
	 * @param inputFilePath
	 * @param outputFileName
	 * @throws ParseException
	 * @throws FileNotFoundException
	 */
	private void writeStation(Station currentStation, String inputFilePath,
			int indiceStation) throws Exception {
		StationDomain station = iDomain.getStation();

		bufferedReader = getBufferReader(inputFilePath, currentStation);
		boolean stationWrited = false;
		indexRecord = 0;
		stationDatas.clear();
		StringBuffer stationHeader = station.getOdvEmptyStationHeader();

		// Local CDI ID
		String localCdiId = stationDomain.getLocalCdiId(cruiseDomain,
				Conversion.NETCDF, currentStation.getReference());
		setDataValue(indiceStation, localCdiId,
				ParameterNames.SDN_LOCAL_CDI_ID, null);
		// SDN CRUISE
		setDataValue(indiceStation, cruiseDomain.getDataSetRef(),
				ParameterNames.SDN_CRUISE, null);
		// SDN Station
		setDataValue(indiceStation, currentStation.getReference().trim(),
				ParameterNames.SDN_STATION, null);
		// SDN EDMO_CODE
		setDataValue(indiceStation, Nemo.getInstance().getConfig()
				.getIdEdmoRespCdi().trim(), ParameterNames.SDN_EDMO_CODE, null);
		// TIME
		long currentDate = DateTimeUtils.getCurrentDate(currentStation,
				indexRecord);

		double currentLongitude = currentStation.getLongitude();
		double currentLatitude = currentStation.getLatitude();

		// SDN_REFERENCE
		if (!cruiseDomain.getModel().getCorrespondingURL().isEmpty())
			setDataValue(indiceStation, cruiseDomain.getModel()
					.getCorrespondingURL(), ParameterNames.SDN_REFERENCE, null);
		else
			setDataValue(indiceStation, null, ParameterNames.SDN_REFERENCE,
					null);

		// scan the InputFile line by line
		while ((readedLine = bufferedReader.readLine()) != null) {
			currentDate = DateTimeUtils.getCurrentDate(currentStation,
					indexRecord);
			incrementLine();
			if (inFileHeader())
				continue;
			if (isStartStationHeader()) {
				String[] inputStationHeader = consumeStationHeader(mediaModel
						.getStationHeader() > 0);

				String stRef = null;
				// si inputStationHeader est null, c'est qu'on a terminé la
				// derniere station
				if (inputStationHeader != null) {
					if (isInputType_Trajectory)
						stRef = currentStation.getReference();
					else
						stRef = iDomain.getStation().getReference(
								currentStation.getFileName(),
								inputStationHeader, true);
				}

				if (inputStationHeader != null
						&& currentStation.getReference().equals(stRef)) {

					stationHeader = iDomain.getStation().getHeader(
							myConversion, currentStation, inputStationHeader);

					// FAE 4673 Pb si une seule ligne données et pas d'entête
					// station
					if (!isEndData()
							|| (mediaModel.getEndData() == E_EndData.NEXT_START_WITH && mediaModel
									.getStationHeader() == 0)) {
						if (mediaDomain.getModel().getInputType() == E_InputType.TIMESERIES) {
							if (stationDomain.getModel()
									.getProperty(Intern.S_DATE).isInData())
								currentDate = Long.valueOf(DateTimeUtils
										.convertTimestamp(readedLine));
						}

						if (mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY) {
							if (stationDomain.getModel()
									.getProperty(Intern.S_LON).isInData())
								currentLongitude = ((Double) stationDomain
										.getModel().getProperty(Intern.S_LON)
										.applyOnLine(readedLine.toString()))
										.doubleValue();
							if (stationDomain.getModel()
									.getProperty(Intern.S_LAT).isInData())
								currentLatitude = ((Double) stationDomain
										.getModel().getProperty(Intern.S_LAT)
										.applyOnLine(readedLine.toString()))
										.doubleValue();
							if (stationDomain.getModel()
									.getProperty(Intern.S_DATE).isInData())
								currentDate = Long.valueOf(DateTimeUtils
										.convertTimestamp(readedLine));
						}

						// Longitude
						setDataValue(indiceStation,
								String.valueOf(currentLongitude),
								ParameterNames.LONGITUDE,
								currentStation.getMetaFlag(Intern.S_LON));
						// Latitude
						setDataValue(indiceStation,
								String.valueOf(currentLatitude),
								ParameterNames.LATITUDE,
								currentStation.getMetaFlag(Intern.S_LAT));

						Double julianDay = DateTimeUtils
								.getJulianDay(currentDate);
						setDataValue(indiceStation, julianDay.toString(),
								ParameterNames.TIME,
								currentStation.getMetaFlag(Intern.S_DATE));

						// SDN_BOT_DEPTH
						Object botDepth = stationDomain.getModel()
								.getProperty(Intern.S_DEPTH)
								.applyOn(inputStationHeader);
						if (!botDepth.equals("Default")
								&& !botDepth.equals(Float.NaN)) {
							setDataValue(indiceStation, botDepth.toString(),
									ParameterNames.SDN_BOT_DEPTH, null);
						} else {
							String bottomDepth = stationDomain.getModel()
									.getProperty(Intern.S_DEPTH).getUserValue();
							if (bottomDepth.isEmpty()) {
								SdnBotDepthVariable SdnBotDepth = (SdnBotDepthVariable) variables
										.get(ParameterNames.SDN_BOT_DEPTH);
								setDataValue(indiceStation,
										String.valueOf(SdnBotDepth
												.getFillValue()),
										ParameterNames.SDN_BOT_DEPTH, null);
							} else
								setDataValue(indiceStation, bottomDepth,
										ParameterNames.SDN_BOT_DEPTH, null);
						}

						writeDataLine(currentStation, readedLine, indiceStation);
						stationHeader = station.getOdvEmptyStationHeader();
					}
					stationWrited = true;
				} else if (stationWrited || inputStationHeader == null) {
					break;
				}
			} else if (stationWrited) {
				// ligne de données : à ne prendre en compte que si on est sur
				// la bonne station
				if (mediaDomain.getModel().getInputType() == E_InputType.TIMESERIES) {
					if (stationDomain.getModel().getProperty(Intern.S_DATE)
							.isInData())
						currentDate = Long.valueOf(DateTimeUtils
								.convertTimestamp(readedLine));
				}

				if (mediaDomain.getModel().getInputType() == E_InputType.TRAJECTORY) {
					if (stationDomain.getModel().getProperty(Intern.S_LON)
							.isInData())
						currentLongitude = ((Double) stationDomain.getModel()
								.getProperty(Intern.S_LON)
								.applyOnLine(readedLine.toString()))
								.doubleValue();
					if (stationDomain.getModel().getProperty(Intern.S_LAT)
							.isInData())
						currentLatitude = ((Double) stationDomain.getModel()
								.getProperty(Intern.S_LAT)
								.applyOnLine(readedLine.toString()))
								.doubleValue();
					if (stationDomain.getModel().getProperty(Intern.S_DATE)
							.isInData())
						currentDate = Long.valueOf(DateTimeUtils
								.convertTimestamp(readedLine));

					if (station.getModel().getProperty(Intern.S_DEPTH)
							.isInData()) {
						Object botDepth = stationDomain.getModel()
								.getProperty(Intern.S_DEPTH)
								.applyOnLine(readedLine.toString());
						if (!botDepth.equals("Default")
								&& !botDepth.equals(Float.NaN)) {
							setDataValue(indiceStation, botDepth.toString(),
									ParameterNames.SDN_BOT_DEPTH, null);
						}
					} else {
						String bottomDepth = stationDomain.getModel()
								.getProperty(Intern.S_DEPTH).getUserValue();
						if (bottomDepth.isEmpty()) {
							SdnBotDepthVariable SdnBotDepth = (SdnBotDepthVariable) variables
									.get(ParameterNames.SDN_BOT_DEPTH);
							setDataValue(indiceStation,
									String.valueOf(SdnBotDepth.getFillValue()),
									ParameterNames.SDN_BOT_DEPTH, null);
						} else
							setDataValue(indiceStation, bottomDepth,
									ParameterNames.SDN_BOT_DEPTH, null);
					}
				}

				// longitude
				setDataValue(indiceStation, String.valueOf(currentLongitude),
						ParameterNames.LONGITUDE,
						currentStation.getMetaFlag(Intern.S_LON));
				// Latitude
				setDataValue(indiceStation, String.valueOf(currentLatitude),
						ParameterNames.LATITUDE,
						currentStation.getMetaFlag(Intern.S_LAT));
				// TIME
				Double julianDay = DateTimeUtils.getJulianDay(currentDate);
				setDataValue(indiceStation, julianDay.toString(),
						ParameterNames.TIME,
						currentStation.getMetaFlag(Intern.S_DATE));

				writeDataLine(currentStation, readedLine, indiceStation);
				stationHeader = station.getOdvEmptyStationHeader();
			}

		}

		if (stationWrited) {
			// enregistrement de la station
			iDomain.getMedia().addStationInOutput(myCFPointWriterManager.getFileName(),
					currentStation.getReference());
		}

		numLine = 0;
	}

	public void setDataValue(int indiceStation, String stringValue,
			String variableName, String qcValue) {
		ISdnVariable var = variables.get(variableName);
		Object[][] paramData = stationData.get(variableName);
		String ancillaryName = null;
		// creation du conteneur de donnée si non existant
		if (paramData == null) {
			if (var.getDims().size() > 1)
				paramData = new Object[nbStations][var.getDims().get(1)
						.getLength()];
			else
				paramData = new Object[nbStations][1];
		}

		if (var != null) {
			if (var.getDims().size() > 1)
				paramData[indiceStation][indexRecord] = convertValue(
						var.getDataType(), stringValue);
			else
				paramData[indiceStation][0] = convertValue(var.getDataType(),
						stringValue);

			if (var instanceof CoOrdinateVariable)
				ancillaryName = ((CoOrdinateVariable) var)
						.getAncillaryVariables();
			if (var instanceof GeophysicalVariable)
				ancillaryName = ((GeophysicalVariable) var)
						.getAncillaryVariables();

		}
		stationData.put(variableName, paramData);

		if (ancillaryName != null)
			addAncillaryData(ancillaryName, qcValue, indiceStation);
	}

	private void writeDataLine(Station currentStation, String inputLine,
			int indiceStation) throws Exception {
		long currentDate = DateTimeUtils.getCurrentDate(currentStation,
				indexRecord);

		if (mediaDomain.getModel().getInputType() == E_InputType.TIMESERIES
				&& Nemo.getInstance().getDomain().getStation().getModel()
						.getProperty(Intern.S_DATE).isInData())
			currentDate = Long.valueOf(DateTimeUtils
					.convertTimestamp(inputLine));

		Object[][] paramData;
		String varName;
		// parametre ajouté
		for (ParameterModel pModel : dataDomain.getMyParameterManager()
				.getNetCDFParameterModels()) {
			varName = pModel.getParameter().getCodeP011();

			pModel.computeAddedParameterValue(currentStation, inputLine,
					currentDate);

			String defaultValue = pModel.getParameter().getDefault().trim();
			String stringValue = pModel.getLineValue().getValue().trim();
			String inputDefaultValue = pModel.getInputDefault().trim();
			if (!stringValue.equals(inputDefaultValue))
				setDataValue(indiceStation, stringValue, varName, pModel
						.getLineValue().getQc());
			else {
				if (defaultValue != null && !defaultValue.isEmpty())
					setDataValue(indiceStation, defaultValue, varName, pModel
							.getLineValue().getQc());
				else
					setDataValue(indiceStation, null, varName, pModel
							.getLineValue().getQc());
			}

		}

		// paramètres mesurés
		for (ParameterModel pModel : dataDomain.getModel().getParameterModels()) {

			varName = pModel.getParameter().getCodeP011();
			paramData = stationData.get(varName);
			// if paramData == null => no data for Parameter
			if (paramData == null)
				paramData = new Object[nbStations][dims.getDimMax().getLength()];

			pModel.computeParameterValue(inputLine);
			String inputDefaultValue = pModel.getInputDefault().trim();
			String defaultValue = pModel.getParameter().getDefault();
			String stringValue = Global.EMPTY_STRING;
			if (!pModel.getLineValue().getValue().isEmpty()) {
				stringValue = Formatter
						.getSingleton()
						.formatParameter(pModel.getParameter().getFormat(),
								pModel.getLineValue().getValue()).trim();
			}

			if (!stringValue.equals(inputDefaultValue))
				setDataValue(indiceStation, stringValue, varName, pModel
						.getLineValue().getQc());
			else {
				if (defaultValue != null && !defaultValue.isEmpty())
					setDataValue(indiceStation, defaultValue, varName, pModel
							.getLineValue().getQc());
				else
					setDataValue(indiceStation, null, varName, pModel
							.getLineValue().getQc());
			}

		}
		indexRecord++;
	}

	public void writeDataToNetCDFFile() throws Exception {
		
		for (Entry<String, Object[][]> currentStationData : stationData.entrySet()) {

				myCFPointWriterManager.writeDataToVariable(
						variables.get(currentStationData.getKey()).getVarName(), 
						currentStationData.getValue());

		}
		// close file after write all data
		myCFPointWriterManager.finish();
	}

	private void addAncillaryData(String ancillaryName, String qc,
			int indiceStation) {
		Object[][] paramData;

		paramData = stationData.get(ancillaryName);
		AncillaryVariable currentAncillary = (AncillaryVariable) variables
				.get(ancillaryName);
		// if paramData == null => no data for Parameter
		if (paramData == null) {
			if (currentAncillary.getDims().size() > 1)
				paramData = new Object[nbStations][dims.getDimMax().getLength()];
			else
				paramData = new Object[nbStations][1];
		}

		if (currentAncillary != null) {
			int qcIndexRecord = ((currentAncillary.getDims().size() > 1) ? indexRecord
					: 0);
			if (qc != null)
				paramData[indiceStation][qcIndexRecord] = convertValue(
						currentAncillary.getDataType(), qc);
			else
				paramData[indiceStation][qcIndexRecord] = currentAncillary
						.getFillvalue();

			stationData.put(ancillaryName, paramData);
		}

	}

	public void setNetCDFHeader(String inputFilePath, Station st)
			throws Exception {

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
		sdnDim.add(dims.getString80());

		UnitConversionManager unitConversion = UnitConversionManager.getInstance();

		// add to net CDF
		myCFPointWriterManager.addDimensions(dims);

		// create Global Attributes
		String title = "NetCDF " + mediaModel.getInputType() + " - Generated by NEMO, version " + Nemo.getInstance().getVersion(); //$NON-NLS-1$//$NON-NLS-2$
		SdnGlobalAttribute globattr = new SdnGlobalAttribute(getFeatureType(),
				title,
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").format(new Date())); //$NON-NLS-1$
		myCFPointWriterManager.addGlobalAttributes(globattr);
		SdnX longitude = null;
		SdnY latitude = null;
		SdnT time = null;
		SdnZ z = null;

		// z Variable
		ParameterModel zParameterModel = null;
		if (mediaModel.getInputType() != E_InputType.TIMESERIES
				|| dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			zParameterModel = dataDomain.getModel().getParameterModelByP011(
					getZLocalName());

		// Si Parametre vertical non selectionné prendre le premiser parametre
		// mesuré
		if (zParameterModel == null) {
			zParameterModel = dataDomain.getMyParameterManager()
					.getNetCDFParameterModels()[0];
		}

		String varName = getZVarName(zParameterModel);
		String standardName = getZStandardName(zParameterModel);
		String longName = NetCDFLongNameParameterManager.getInstance()
				.getUserLongName(zParameterModel.getParameter().getCodeP011());
		if (longName == null) {
			if (zParameterModel.getParameter().getNameP011() != null) {
				longName = zParameterModel.getParameter().getNameP011();
			} else {
				throw new Exception(Messages.Etape_134 + Global.SPACE
						+ zParameterModel.getParameter().getCodeP011());
			}
		}
		String unit = unitConversion.getUnitConversion(zParameterModel
				.getParameter().getUnit());
		String sdnParameterUrn = getURNSyntaxe(URNtype.object, zParameterModel
				.getParameter().getCodeP011(), null, Intern.mapping_objet,
				zParameterModel.isText());
		String sdnParameterName = zParameterModel.getParameter().getNameP011();

		String sdnUomUrn;
		if (zParameterModel.getParameter().getUnitCode() == null
				|| zParameterModel.getParameter().getUnitCode().isEmpty()) {
			sdnUomUrn = getURNSyntaxe(URNtype.units, zParameterModel
					.getParameter().getCode(), Intern.mapping_subject,
					Intern.mapping_unit, zParameterModel.isText());
		} else {
			sdnUomUrn = getURNSyntaxe(URNtype.units, zParameterModel
					.getParameter().getUnitCode(), null, Intern.mapping_unit,
					zParameterModel.isText());
		}
		String sdnUomName = zParameterModel.getParameter().getUnit();
		String sdnBotDepthLongName = Global.EMPTY_STRING;

		SdnBotDepthVariable SdnBotDepth = new SdnBotDepthVariable(instanceDim,
				sdnBotDepthLongName);

		// create Co-ordinate Variable
		if (mediaModel.getInputType() == E_InputType.TRAJECTORY) {
			sdnBotDepthLongName = Messages.typeFile_trajectory;
			longitude = new SdnX(geoDim);
			latitude = new SdnY(geoDim);
			time = new SdnT(geoDim);
			z = new SdnZ(varName, geoDim, longName, standardName, unit,
					sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);
			SdnBotDepth = new SdnBotDepthVariable(geoDim, sdnBotDepthLongName);
		} else if (mediaModel.getInputType() == E_InputType.PROFILE) {
			sdnBotDepthLongName = Messages.typeFile_profile;
			longitude = new SdnX(instanceDim);
			latitude = new SdnY(instanceDim);
			time = new SdnT(instanceDim);
			z = new SdnZ(varName, geoDim, longName, standardName, unit,
					sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);
		} else {
			sdnBotDepthLongName = Messages.typeFile_timeseries;
			longitude = new SdnX(instanceDim);
			latitude = new SdnY(instanceDim);
			time = new SdnT(geoDim);
			z = new SdnZ(varName, instanceDim, longName, standardName, unit,
					sdnParameterUrn, sdnParameterName, sdnUomUrn, sdnUomName);

		}

		// add DefaultFillValue
		longitude
				.addVariableAttribute(
						ParameterNames.FILLVALUE,
						convertValue(DataType.DOUBLE,
								ParameterValues.DEFAULT_FILLVALUE));
		latitude.addVariableAttribute(
				ParameterNames.FILLVALUE,
				convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));
		time.addVariableAttribute(
				ParameterNames.FILLVALUE,
				convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));
		z.addVariableAttribute(
				ParameterNames.FILLVALUE,
				convertValue(DataType.DOUBLE, ParameterValues.DEFAULT_FILLVALUE));

		// add default value if existe
		if (!zParameterModel.getParameter().getDefault().isEmpty())
			z.addVariableAttribute(
					ParameterNames.FILLVALUE,
					convertValue(DataType.DOUBLE, zParameterModel
							.getParameter().getDefault()));

		// 14771
		if (varName.equals(ParameterNames.DEPTH)
				|| varName == ParameterNames.PRESS)
			z.addVariableAttribute(ParameterNames.POSITIVE,
					ParameterValues.Z_POSITIVE_DOWN);
		else if (varName == ParameterNames.HEIGHT)
			z.addVariableAttribute(ParameterNames.POSITIVE,
					ParameterValues.Z_POSITIVE_UP);

		// Sdn variable
		SdnEdmoCodeVariable sdnEdmoCode = new SdnEdmoCodeVariable(instanceDim);
		variables.put(sdnEdmoCode.getVarName(), sdnEdmoCode);
		addVariableToNetCDF(sdnEdmoCode);

		

		SdnCruiseVariable sdnCruise = new SdnCruiseVariable(sdnDim,
				getlongNameCruise());
		addCruiseOptionalAttribute(sdnCruise);
		variables.put(sdnCruise.getVarName(), sdnCruise);
		addVariableToNetCDF(sdnCruise);

		SdnStationVariable sdnStation = new SdnStationVariable(sdnDim);
		variables.put(sdnStation.getVarName(), sdnStation);
		addVariableToNetCDF(sdnStation);

		SdnLocalCdiIdVariable SdnLocalCdi = new SdnLocalCdiIdVariable(sdnDim,
				getFeatureType().toLowerCase() + "_id");
		variables.put(SdnLocalCdi.getVarName(), SdnLocalCdi);
		addVariableToNetCDF(SdnLocalCdi);

		if (!cruiseDomain.getDataSetRef().isEmpty()) {
			SdnReferenceVariable srnRef = new SdnReferenceVariable(sdnDim);
			variables.put(srnRef.getVarName(), srnRef);
			addVariableToNetCDF(srnRef);
		}

		variables.put(SdnBotDepth.getVarName(), SdnBotDepth);
		addVariableToNetCDF(SdnBotDepth);

		variables.put(longitude.getVarName(), longitude);
		addVariableToNetCDF(longitude);
		variables.put(latitude.getVarName(), latitude);
		addVariableToNetCDF(latitude);

		SdnCrs crs = new SdnCrs();
		variables.put(crs.getVarName(), crs);
		addVariableToNetCDF(crs);
		// add default value for crs
		setDataValue(0, ParameterValues.SDN_CRS_DEFAULT_VALUE,
				crs.getVarName(), null);

		variables.put(time.getVarName(), time);
		addVariableToNetCDF(time);
		variables.put(zParameterModel.getParameter().getCodeP011(), z);
		addVariableToNetCDF(z);

		// Geophysical variable
		ParameterModel[] paramAdded = dataDomain.getMyParameterManager()
				.getNetCDFParameterModels();
		ParameterModel[] paramMesured = dataDomain.getModel()
				.getParameterModels();
		// concaténation
		ParameterModel[] params = new ParameterModel[paramAdded.length
				+ paramMesured.length];
		System.arraycopy(paramAdded, 0, params, 0, paramAdded.length);
		System.arraycopy(paramMesured, 0, params, paramAdded.length,
				paramMesured.length);

		String coordinates = longitude.getVarName() + Global.SPACE
				+ latitude.getVarName() + Global.SPACE + time.getVarName()
				+ Global.SPACE + z.getVarName();
		for (ParameterModel param : params) {
			// varName = param.getParameter().getLocalCode();
			varName = param.getParameter().getCodeP011();
			longName = NetCDFLongNameParameterManager.getInstance()
					.getUserLongName(param.getParameter().getCodeP011());
			if (longName == null) {
				if (zParameterModel.getParameter().getNameP011() != null) {
					longName = zParameterModel.getParameter().getNameP011();
				} else {
					throw new Exception(Messages.Etape_134 + Global.SPACE
							+ zParameterModel.getParameter().getCodeP011());
				}
			}
			sdnParameterUrn = getURNSyntaxe(URNtype.object, param
					.getParameter().getCodeP011(), null, Intern.mapping_objet,
					param.isText());
			sdnParameterName = param.getParameter().getNameP011();
			if (param.getParameter().getUnitCode() == null
					|| param.getParameter().getUnitCode().isEmpty()) {
				sdnUomUrn = getURNSyntaxe(URNtype.units, param.getParameter()
						.getCode(), Intern.mapping_subject,
						Intern.mapping_unit, param.isText());
			} else {
				sdnUomUrn = getURNSyntaxe(URNtype.units, param.getParameter()
						.getUnitCode(), null, Intern.mapping_unit,
						param.isText());
			}
			sdnUomName = param.getParameter().getUnit();

			unit = unitConversion.getUnitConversion(param.getParameter()
					.getUnit());

			if (!sdnParameterUrn.equals(z.getSdnParameterUrn())) {
				GeophysicalVariable current = new GeophysicalVariable(varName,
						getUnits(param.getParameter().getFormat()), geoDim,
						longName, coordinates, unit, sdnParameterUrn,
						sdnParameterName, sdnUomUrn, sdnUomName);

				// add default value if existe
				if (!param.getParameter().getDefault().isEmpty())
					current.addVariableAttribute(
							ParameterNames.FILLVALUE,
							convertValue(DataType.DOUBLE, param.getParameter()
									.getDefault()));
				else
					current.addVariableAttribute(
							ParameterNames.FILLVALUE,
							convertValue(DataType.DOUBLE,
									ParameterValues.DEFAULT_FILLVALUE));
				if (!param.getParameter().getStandardName().isEmpty())
					current.addVariableAttribute(
							ParameterNames.STANDARD_NAME, param.getParameter()
									.getStandardName());
				variables.put(current.getVarName(), current);
				addVariableToNetCDF(current);

			}
		}

	}

	/**
	 * get longname from  ProcessType and input Type
	 * @return SDN_CRUISE Long Name
	 */
	private String getlongNameCruise() {
		// 14926 et 14931
		// LongName suivant ProcessType
		String longNameCruise = Global.EMPTY_STRING;
		if (mediaModel.getProcessType() == E_ProcessType.COLLECTION_DIR) {
			longNameCruise = cruiseDomain.getDataSetRef();
		} else if (mediaModel.getProcessType() == E_ProcessType.COLLECTION_FILES) {
			//TODO CPT MODIF LIB?
			switch(mediaModel.getInputType())
			{
				case PROFILE:
					longNameCruise= ParameterValues.SDN_CRUISE_LONG_NAME_CRUISE_PROFILE;
					break;
				case TIMESERIES:
					longNameCruise= ParameterValues.SDN_CRUISE_LONG_NAME_CRUISE_TIME_SERIES;
					break;
				case TRAJECTORY:
					longNameCruise= ParameterValues.SDN_CRUISE_LONG_NAME_CRUISE_TRAJECTORY;
					break;
				default:
					longNameCruise = Intern.unknown;
			}
		} else {
			longNameCruise = cruiseDomain.getModel().getName().trim();
		}
		return longNameCruise;
	}

	private void addVariableToNetCDF(ISdnVariable current) throws Exception {
		if (current.isSDNCompliant()) {
			myCFPointWriterManager.addSdnVariable(current);
			AncillaryVariable currentAncillary = myCFPointWriterManager
					.getAncillaryVariableFromSdnVariable(current);
			if (currentAncillary != null)
				variables.put(currentAncillary.getVarName(), currentAncillary);
		} else
			throw new Exception("Variable " + current.getVarName()
					+ " is not SDN Compliant");
	}

	/**
	 * @param sdnCruise
	 */
	private void addCruiseOptionalAttribute(SdnCruiseVariable sdnCruise) {
		// sous onglet Reference
		if (cruiseDomain.getModel().getShipCode() != null
				&& !cruiseDomain.getModel().getShipCode().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_shipcode,
					cruiseDomain.getModel().getShipCode());
		if (cruiseDomain.getModel().getShipName() != null
				&& !cruiseDomain.getModel().getShipName().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_shipname,
					cruiseDomain.getModel().getShipName());

		// sous onglet Location
		try {
			sdnCruise.addVariableAttribute(ModelDescription.node_startdate,
					DateTimeUtils.getVariantDateTime(cruiseDomain.getModel()
							.getStartDate()));
		} catch (Exception e) {
		}
		try {
			sdnCruise.addVariableAttribute(ModelDescription.node_enddate,
					DateTimeUtils.getVariantDateTime(cruiseDomain.getModel()
							.getEndDate()));
		} catch (Exception e) {
		}
		if (cruiseDomain.getModel().getRegionName() != null
				&& !cruiseDomain.getModel().getRegionName().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_region,
					cruiseDomain.getModel().getRegionName());

		// date source
		if (cruiseDomain.getModel().getCountryCode() != null
				&& !cruiseDomain.getModel().getCountryCode().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_country,
					cruiseDomain.getModel().getCountryCode());
		if (cruiseDomain.getModel().getLaboratories() != null
				&& !cruiseDomain.getModel().getLaboratories().isEmpty())
			sdnCruise.addVariableAttribute(
					ModelDescription.node_laboratories, cruiseDomain.getModel()
							.getLaboratories());
		if (cruiseDomain.getModel().getChiefScientist() != null
				&& !cruiseDomain.getModel().getChiefScientist().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_chief,
					cruiseDomain.getModel().getChiefScientist());
		if (cruiseDomain.getModel().getProjectName() != null
				&& !cruiseDomain.getModel().getProjectName().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_project,
					cruiseDomain.getModel().getProjectName());

		// archive center
		if (cruiseDomain.getModel().getDataCentre() != null
				&& !cruiseDomain.getModel().getDataCentre().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_datacenter,
					cruiseDomain.getModel().getDataCentre());
		if (cruiseDomain.getModel().getAvailability() != null)
			sdnCruise.addVariableAttribute(
					ModelDescription.node_availability, cruiseDomain.getModel()
							.getAvailability().name());

		// Comment
		if (cruiseDomain.getModel().getComment() != null
				&& !cruiseDomain.getModel().getComment().isEmpty())
			sdnCruise.addVariableAttribute(ModelDescription.node_comment,
					cruiseDomain.getModel().getComment());
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

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_heightASLvl().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			return dataDomain.getModel().getVerticalParametersList()
					.getP_heightASLvl();

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_depthBSBed().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSBED)
			return dataDomain.getModel().getVerticalParametersList()
					.getP_depthBSBed();

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_depthBSSurf().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return dataDomain.getModel().getVerticalParametersList()
					.getP_depthBSSurf();

		if (!dataDomain.getModel().getVerticalParametersList().getP_pressure()
				.isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return dataDomain.getModel().getVerticalParametersList()
					.getP_pressure();

		return null;
	}

	private String getZVarName(ParameterModel zParameterModel) {
		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_depthBSBed().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSBED)
			return ParameterNames.DEPTH;

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_depthBSSurf().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return ParameterNames.DEPTH;

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_heightASLvl().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			return ParameterNames.HEIGHT;

		if (!dataDomain.getModel().getVerticalParametersList().getP_pressure()
				.isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return ParameterNames.PRESS;

		// si parametre vertical non selection alors z peut etre un parametre
		// mesuré
		AP_ADEPZZ01 zADEPZZ01 = new AP_ADEPZZ01(Conversion.NETCDF);
		if (zADEPZZ01.getCode()
				.equals(zParameterModel.getParameter().getCode()))
			return ParameterNames.DEPTH;

		AP_DEPHPR01 zDEPHPR01 = new AP_DEPHPR01(Conversion.NETCDF);
		if (zDEPHPR01.getCode()
				.equals(zParameterModel.getParameter().getCode()))
			return ParameterNames.DEPTH;

		AP_AHGTZZ01 zAHGTZZ01 = new AP_AHGTZZ01(Conversion.NETCDF);
		if (zAHGTZZ01.getCode()
				.equals(zParameterModel.getParameter().getCode()))
			return ParameterNames.HEIGHT;

		return null;
	}

	private String getZStandardName(ParameterModel zParameterModel) {
		if (!dataDomain.getModel().getVerticalParametersList().getP_pressure()
				.isEmpty()
				&& mediaModel.getInputType() != E_InputType.TIMESERIES)
			return ParameterValues.Z_PRESSURE;

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_depthBSSurf().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSSURF)
			return ParameterValues.Z_DEPTH;

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_heightASLvl().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.ASLVL)
			return ParameterValues.Z_HEIGHT;

		if (!dataDomain.getModel().getVerticalParametersList()
				.getP_depthBSBed().isEmpty()
				&& dataDomain.getModel().getMesureType() == E_MesureType.BSBED)
			return ParameterValues.Z_DEPTH;

		// si parametre vertical non selection alors z peut etre un parametre
		// mesuré
		AP_ADEPZZ01 zADEPZZ01 = new AP_ADEPZZ01(Conversion.NETCDF);
		if (zADEPZZ01.getCode()
				.equals(zParameterModel.getParameter().getCode()))
			return ParameterValues.Z_DEPTH;

		AP_DEPHPR01 zDEPHPR01 = new AP_DEPHPR01(Conversion.NETCDF);
		if (zDEPHPR01.getCode()
				.equals(zParameterModel.getParameter().getCode()))
			return ParameterValues.Z_DEPTH;

		AP_AHGTZZ01 zAHSLZZ01 = new AP_AHGTZZ01(Conversion.NETCDF);
		if (zAHSLZZ01.getCode()
				.equals(zParameterModel.getParameter().getCode()))
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
			for (Station currentStation : stationDomain
					.getStations(inputFilePath)) {
				instance = stationDomain.getStations(inputFilePath).size();
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

	private static String getURNSyntaxe(URNtype type, String code,
			String list1, String list2, Boolean isText) {
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
				pdvCodeobject = VocabLists.getVocabMatch(list1, list2, code,
						Intern.NemoLists_unknown);
			else
				pdvCodeobject = code;
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
				pdvCodeunits = VocabLists.getVocabMatch(list1, list2, code,
						Intern.NemoLists_unknown);
			else
				pdvCodeunits = code;
			// FAE 10755
			if (isText)
				urnSyntaxe.append(Intern.NemoLists_unknown);
			else
				urnSyntaxe.append(pdvCodeunits);
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
