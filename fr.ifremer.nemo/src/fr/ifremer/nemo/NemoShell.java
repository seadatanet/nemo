/*
 * ============================================================================
 * Nom du fichier : NemoShell.java
 * Application : NEMO version 1.2.20
 * Auteur : dboulang
 * Date de création : 29 déc. 09
 * Client : Ifremer
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import fr.ifremer.nemo.config.ManageLists;
import fr.ifremer.nemo.domain.ConversionDomain;
import fr.ifremer.nemo.domain.CruiseDomain;
import fr.ifremer.nemo.domain.InputDomain;
import fr.ifremer.nemo.domain.StationDomain;
import fr.ifremer.nemo.input.DataType;
import fr.ifremer.nemo.input.PropertyModel;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.mapping.Mapping;
import fr.ifremer.nemo.mapping.MappingTable;
import fr.ifremer.nemo.output.CDIResumeWriter;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.output.NemoWriter;
import fr.ifremer.nemo.output.NemoWriterFactory;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.BODCResult;
import fr.ifremer.nemo.util.BODCResult.BODC_RES_STATUS;
import fr.ifremer.nemo.util.BodcChecker;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.Preformat;
import fr.ifremer.nemo.ws.WSDataType;
import fr.ifremer.sismer_tools.SismerToolsException;
import fr.ifremer.sismer_tools.resources.ODVConstants.E_Variant;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

/**
 * Class NemoShell.java Description: Main class to launch nemo in batch mode.
 * 
 * @author ycaudal
 */
public class NemoShell {

	/**
	 * @param args
	 */
	public static boolean main(String inputFile, String outputFile,
			String modelFile, Conversion conv, boolean sortDatas,
			E_TypeConversion odvConv, String datasetRef, String cdiSummaryFile,
			boolean couplingTableUpdate, String couplingTablePrefix,
			Logger logger) {

		try {
			
			ManageLists.initLists();
			Nemo nemo = Nemo.getInstance();
			if (!new File(inputFile).exists()) {
				logger.error("Input file not found");
				return false;
			}
			if (!new File(modelFile).exists()) {
				logger.error("Model file not found");
				return false;
			}

			boolean sort = sortDatas;
			// export CDI summary (FAE 4525)
			if (!cdiSummaryFile.equals(Global.EMPTY_STRING)) {
				CDIResumeWriter.getInstance().reset();
				CDIResumeWriter.getInstance().setNomFichierSortie(
						cdiSummaryFile);
			}

			List<String> resmsg = nemo.openModel(modelFile);
			if (!resmsg.isEmpty()) {
				for (String msg : resmsg){
					logger.warn(msg);
				}
			}
			InputDomain inputDomain = Nemo.getInstance().getDomain();
			if (!inputDomain.isValid()){
				logger.error("Error, model file not valid");
				return false;
			}
			//			if (inputDomain.getMedia().getModel().getSeparator() != Global.EMPTY_STRING)
			//				inputFile =
			// 33278: format only for models < 1.6.4
			if (inputDomain.getMedia().getModel().getSeparator() != Global.EMPTY_STRING && !inputDomain.getMedia().getModel().getUseTableForCsvData())
				inputFile = Preformat.run(new File(inputFile)).getAbsolutePath();

			nemo.getDomain().getMedia().getModel().setSourcePath(inputFile);

			E_TypeConversion convertModel =
					inputDomain.getConversion().getModel().getOutputFormat();
			E_TypeConversion convertArgument = odvConv;

			// les deux sont égaux et différents de UNKNOWN
			if (convertModel == convertArgument) {
				if (convertModel != E_TypeConversion.UNKNOWN) {
					nemo.getDomain().getConversion()
					.setCurrentConversion(convertArgument);
				} else {
					// ERREUR les deux sont UNKNOWN
					logger.error("Error, Conversion format must be set");
					return false;
				}
			}
			// Argument UNKNOWN Modele NON UNKNOWN
			else if (convertArgument == E_TypeConversion.UNKNOWN) {
				switch (convertModel) {
				case ODV_MONOSTATION:
				case ODV_MULTISTATIONS:
					conv = Conversion.ODV;
					break;
				case MEDATLAS:
					conv = Conversion.MEDATLAS;
					break;
				case NETCDF_MONOSTATION:
				case NETCDF_MULTISTATIONS:
					conv = Conversion.NETCDF;
					break;
				default:
					return false;
				}

				inputDomain.getConversion().setCurrentConversion(convertModel);
			}
			// Modele UNKNOWN Argument NON UNKNOWN
			else if (convertModel == E_TypeConversion.UNKNOWN) {
				inputDomain.getConversion().setCurrentConversion(
						convertArgument);
			} else {
				// ERREUR argument et modele sont différents !!
				logger
				.error("Error, Conversion format incompatible with the model");
				return false;
			}

			inputDomain.getConversion().setCurrentConversion(odvConv);

			inputDomain.getMedia().buildInputFilesNames();

			inputDomain.getMedia().setOutputFileName(outputFile);

			inputDomain.getConversion().setSortData(sort);

			// The default dataset ref
			String strDefault = datasetRef;

			if (strDefault.equals(Global.EMPTY_STRING))
				strDefault = inputDomain.getCruise().getModel().getReference();

			if (strDefault.equals(Global.EMPTY_STRING))
				strDefault = inputDomain.getCruise().getModel().getName();

			if (odvConv != E_TypeConversion.MEDATLAS){
				if (strDefault.isEmpty()){
					throw new GenerationException(Messages.error_missing_cruise_reference);
				}
				inputDomain.getCruise().getModel().setReference(strDefault);
			}

			// Is a station property in file
			int nbLignes =
					inputDomain.getMedia().getModel().getFileHeader()
					+ Nemo.getInstance().getDomain().getMedia()
					.getShellStationHeaderNumberLines();

			// FAE 7239
			PropertyModel prefModel =
					inputDomain.getStation().getModel().getReferenceProperty();
			if (prefModel.getFragment() != null) {

				// FAE 7138
				if (prefModel.getFragment().getLastLine()
						+ inputDomain.getMedia().getModel().getFileHeader() > nbLignes) {
					nemo.getDomain().getStation().setSomeInData(true);
					prefModel.setInData(true);
				}
			}

			for (PropertyModel pModel : inputDomain.getStation().getModel()
					.getProperties()) {
				if (pModel.getFragment() != null) {

					// FAE 7138
					if (pModel.getFragment().getLastLine()
							+ inputDomain.getMedia().getModel().getFileHeader() > nbLignes) {
						nemo.getDomain().getStation().setSomeInData(true);
						pModel.setInData(true);
					}
				}
			}

			if (odvConv == E_TypeConversion.NETCDF_MONOSTATION || odvConv == E_TypeConversion.NETCDF_MULTISTATIONS) {
				for (ParameterModel pModel : inputDomain.getData().getModel().getParameterModels()) {
					if (pModel.getParameter().getStandardName().equals(Global.EMPTY_STRING)) {
						logger.warn(Messages.Etape_126 + Global.SPACE + pModel.getParameter().getCodeP011());
					}
				}
			}

			// FAE 7138
			// on repasse sur la methode setAddedParameterModel car on n'avait
			// pas toutes les infos necessaires
			// la premiere fois (1er appel via openModel ci-dessus)
			// a ce stade, il manque notamment la taille du stationHeader si
			// celui-ci est variable
			nemo.getDomain().getData().getMyParameterManager()
			.setAddedParameterModel();
			File repertory = null;

			if (odvConv.equals(E_TypeConversion.ODV_MONOSTATION) || odvConv.equals(E_TypeConversion.NETCDF_MONOSTATION)) {
				repertory = new File(outputFile);
			} else {
				repertory = new File(outputFile).getParentFile();
			}

			if (!(repertory == null) && !repertory.exists())
				repertory.mkdirs();

			boolean ok =  checkBodc(logger, inputDomain);

			if (!ok){
				logger.error("errors in BODC parameters. Please check your model.");// TODO 35230 msg
				return false;
			}
			
			E_Variant variant = nemo.getDomain().getMedia().getModel().getVariant();
//			if (variant != E_Variant.PHYSICOCHEMICAL)
//			{
//				LabelParameterManager.getInstance().loadSpecificLabels();
//			}
			
			//*************************************************
			NemoWriter writer = NemoWriterFactory.createWriter(conv);

			writer.write(null);

			// export CDI summary (FAE 4525)
			if (!cdiSummaryFile.equals(Global.EMPTY_STRING)) {
				CDIResumeWriter.getInstance().writeResume(conv);
			}

			if (couplingTableUpdate) {
				// update coupling table (FAE 4525)
				MappingTable mappingTable = new MappingTable();
				mapping(logger, conv, mappingTable, couplingTablePrefix);

			}
			Nemo.getInstance().getLogger().info("Conversion ended with success at " + new Date() + "\n---------------");
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			Nemo.getInstance().getLogger().info("Conversion ended with errors at " + new Date() + "\n---------------");
			logger.error(Nemo.getInstance().getErrorMessageWithFileAndLineNumber());
			return false;
		}

	}

	private static boolean checkBodc(Logger logger, InputDomain inputDomain) {
		// 35230********************
		boolean ok = true;
		// SHIP

		String shipCode = fr.ifremer.nemo.util.Utils.extraitCode(inputDomain.getCruise().getModel().getShipCode());
		String shipName = fr.ifremer.nemo.util.Utils.extraitCode(inputDomain.getCruise().getModel().getShipName());
		BODCResult bodcRes = BodcChecker.getInstance().checkShip(	shipCode, shipName);

		if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
			ok=false;
			logger.error(bodcRes.getErrorMsg());
		}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
			inputDomain.getCruise().getModel().setShipCode(bodcRes.getShipCode());
			inputDomain.getCruise().getModel().setShipName(bodcRes.getShipLabel());
			logger.warn(bodcRes.getWarningMsg());
		}

		// 35230 region
		if (isConversionToMedatlasOrCFPoint(inputDomain) ){
			String regionCode = fr.ifremer.nemo.util.Utils.extraitCode(inputDomain.getCruise().getModel().getRegionCode());
			String regionName = fr.ifremer.nemo.util.Utils.extraitValeur(inputDomain.getCruise().getModel().getRegionName());


			bodcRes = BodcChecker.getInstance().checkRegion(regionCode, regionName);
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				logger.error(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				inputDomain.getCruise().getModel().setRegionCode(bodcRes.getRegionCode());
				inputDomain.getCruise().getModel().setRegionName(bodcRes.getRegionLabel());
				logger.warn(bodcRes.getWarningMsg());
			}
		}
		// 35230 country
		if (isConversionToMedatlasOrCFPoint(inputDomain) ){
			String countryCode = fr.ifremer.nemo.util.Utils.extraitCode(inputDomain.getCruise().getModel().getCountryCode());


			bodcRes = BodcChecker.getInstance().checkCountry(countryCode);
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				logger.error(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				inputDomain.getCruise().getModel().setCountryCode(bodcRes.getCountryCode());
				inputDomain.getCruise().getModel().setCountryName(bodcRes.getCountryLabel());
				logger.warn(bodcRes.getWarningMsg());
			}
		}
		// 35230 data center
		if (isConversionToMedatlasOrCFPoint(inputDomain) ){
			String centerCode = fr.ifremer.nemo.util.Utils.extraitCode(inputDomain.getCruise().getModel().getDataCentre());

			bodcRes = BodcChecker.getInstance().checkDataCenter(centerCode);
			if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
				ok=false;
				logger.error(bodcRes.getErrorMsg());
			}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
				inputDomain.getCruise().getModel().setCountryCode(bodcRes.getCenterCode());
				inputDomain.getCruise().getModel().setCountryName(bodcRes.getCenterLabel());
				logger.warn(bodcRes.getWarningMsg());
			}
		}
		// 35230 data types in cruise
		DataType[] dataTypes = null;
		if (inputDomain.getConversion().getModel().getOutputFormat()==E_TypeConversion.MEDATLAS ){
			dataTypes = inputDomain.getCruise().getModel().getDataTypes();
			for (int i=0; i<dataTypes.length; i++){

				bodcRes = BodcChecker.getInstance().checkDataType(fr.ifremer.nemo.util.Utils.extraitCode(dataTypes[i].getCode()));
				if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
					ok=false;
					logger.error(bodcRes.getErrorMsg());
				}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
					try {
						WSDataType newdt = ManageLists.getDataType(bodcRes.getDataTypeCode());
						dataTypes[i].setCode(bodcRes.getDataTypeCode());
						dataTypes[i].setSensor(newdt.isSensor());
					} catch (Exception e) {
						logger.error(e.getMessage());
						ok=false;
					}

					logger.warn(bodcRes.getWarningMsg());
				}

			}
		}
		// 35230 data types in station
		boolean newCodeFoundInCruiseDataTypes=false;
		DataType dataType = inputDomain.getStation().getModel().getDataType();
		bodcRes = BodcChecker.getInstance().checkDataType(dataType.getCode());
		if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
			ok=false;
			logger.error(bodcRes.getErrorMsg());
		}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
			String newCode=bodcRes.getDataTypeCode();
			
			if (inputDomain.getConversion().getModel().getOutputFormat()==E_TypeConversion.MEDATLAS ){
				for (int i=0; i<dataTypes.length; i++){
					if (dataTypes[i].getCode().equals(newCode)){
						inputDomain.getStation().getModel().setDataType(dataTypes[i]);
						if (!dataTypes[i].isSensor()){
							logger.error("dataType "+ dataType.getCode() + " is deprecated and shall be replaced by " + newCode + " but this dataType has not sensor. Please check your model with Nemo GUI");
							ok = false;
						}
						newCodeFoundInCruiseDataTypes=true;
					}
				}
			}else{
				newCodeFoundInCruiseDataTypes=true; // IF conv is not medatlas, no datatypes in cruise tab, so no need to find them
			}
			if (newCodeFoundInCruiseDataTypes){
				logger.warn(bodcRes.getWarningMsg());
			}else{
				ok=false;
				logger.error("dataType "+ dataType.getCode() + " is deprecated and shall be replaced by " + newCode + " but this dataType has not been found in Cruise datatypes. Please check your model with Nemo GUI");
			}
		}

		for (ParameterModel pModel : inputDomain.getData().getModel().getParameterModels()) {
			TypeData typeCourant = new TypeData(pModel);

			// check parameters codes
			try{
				TypeData typeCourantOrig = new TypeData(typeCourant);
				List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, 	inputDomain.getData().getModel().getDataListType());
				for (BODCResult res :bodcResList){
					if (res.getResStatus() == BODC_RES_STATUS.KO){
						logger.error(res.getErrorMsg());
						ok=false;
					}else if (res.getResStatus() == BODC_RES_STATUS.WARNING){
						typeCourant = res.getTypeData();
						pModel.setParameter(typeCourant);
						logger.warn(res.getWarningMsg());

						replaceVerticalParameters(typeCourantOrig, typeCourant,inputDomain);

					}
				}
			}catch (Exception e) {
				logger.error(e.getMessage());
			}
			// 35230 check unit
			if (typeCourant.isText()) { //46457
				typeCourant.setUniteCode(SdnVocabularyManager.P06_XXXX);
				try {
					typeCourant.setUnite( BodcChecker.getInstance().getSdnMgr().getLabel(SdnVocabularyManager.P06_XXXX, SdnVocabularyManager.LIST_P06));
				} catch (SismerToolsException e) {
					logger.warn(e.getMessage());
				}
			}else {
				bodcRes = BodcChecker.getInstance().checkP06(typeCourant, inputDomain.getData().getModel().getDataListType());
				if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
					logger.error(bodcRes.getErrorMsg());
					ok=false;
				}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
					typeCourant = bodcRes.getTypeData();
					pModel.setParameter(typeCourant);
					logger.warn(bodcRes.getWarningMsg());
				}
				// 35230 check instrument
				bodcRes = BodcChecker.getInstance().checkInstrument(typeCourant);
				if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
					ok=false;
					logger.error(bodcRes.getErrorMsg());
				}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
					typeCourant = bodcRes.getTypeData();
					pModel.setParameter(typeCourant);
					logger.warn(bodcRes.getWarningMsg());
				}
			}
		}
		// 35230 check fall rate

		bodcRes = BodcChecker.getInstance().checkFallRate(inputDomain.getData().getModel().getFallrateCode(), inputDomain.getData().getModel().getFallrateLabel());
		if (bodcRes.getResStatus() == BODC_RES_STATUS.KO){
			ok=false;
			logger.error(bodcRes.getErrorMsg());
		}else if (bodcRes.getResStatus() == BODC_RES_STATUS.WARNING){
			inputDomain.getData().getModel().setFallrateCode(bodcRes.getFallrateCode());
			inputDomain.getData().getModel().setFallrateLabel(bodcRes.getFallrateLabel());
			logger.warn(bodcRes.getWarningMsg());
		}
		return ok;
	}

	private static void replaceVerticalParameters(TypeData typeCourantOrig,
			TypeData typeCourant, InputDomain inputDomain) {
		String oldParamCode;
		String newParamCode;
		if (inputDomain.getData().getModel().getDataListType()==DataListTypeEnum.P091){
			oldParamCode = typeCourantOrig.getEntryKey();
			newParamCode =  typeCourant.getEntryKey();
		}else{
			oldParamCode = typeCourantOrig.getEntryKeyP011();
			newParamCode =  typeCourant.getEntryKeyP011();
		}
		if (inputDomain.getData().getModel().getVerticalParametersList().getP_depthBSBed().equals(oldParamCode)){
			inputDomain.getData().getModel().getVerticalParametersList().setP_depthBSBed(newParamCode);
		}
		if (inputDomain.getData().getModel().getVerticalParametersList().getP_depthBSSurf().equals(oldParamCode)){
			inputDomain.getData().getModel().getVerticalParametersList().setP_depthBSSurf(newParamCode);
		}
		if (inputDomain.getData().getModel().getVerticalParametersList().getP_heightASLvl().equals(oldParamCode)){
			inputDomain.getData().getModel().getVerticalParametersList().setP_heightASLvl(newParamCode);
		}
		if (inputDomain.getData().getModel().getVerticalParametersList().getP_pressure().equals(oldParamCode)){
			inputDomain.getData().getModel().getVerticalParametersList().setP_pressure(newParamCode);
		}

	}

	private static void mapping(Logger logger, Conversion conv,
			MappingTable mappingTable, String couplingTablePrefix)
					throws Exception {
		Mapping mapping;

		InputDomain inputDomain = Nemo.getInstance().getDomain();
		CruiseDomain cDomain = inputDomain.getCruise();
		StationDomain sDomain = inputDomain.getStation();
		ConversionDomain convDomain =Nemo.getInstance().getDomain().getConversion();
		String localCdiId;
		try {

			String cheminComplet = convDomain.getOutputPath();
			String couplingTablValidPrefix = convDomain.getValidPrefix(couplingTablePrefix, cheminComplet);
			if (couplingTablValidPrefix!=null) {
				convDomain.setCouplingTablePrefix(couplingTablValidPrefix);
			}else{
				throw new Exception("Error: couplingTablePrefix is not consistent with output path");
			}

			for (String outputPath : inputDomain.getMedia().getOutputFiles().keySet()) {

				for (String local_cdi_id: inputDomain.getMedia().getOutputFiles().get(outputPath)) {
					mapping = new Mapping(local_cdi_id, conv.getFormatName());

					String modus = Mapping.MODUS_MULTISTATIONS;
					if (inputDomain.getConversion().getCurrent() == E_TypeConversion.ODV_MONOSTATION
							|| inputDomain.getConversion().getCurrent() == E_TypeConversion.NETCDF_MONOSTATION) {
						modus = Mapping.MODUS_MONOSTATION;
					}


					mapping.setFilename(convDomain.getFileNameInCoupling(outputPath));
					mapping.setDate(new Date());
					mapping.setModus(modus);

					mappingTable.persist(mapping);
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static boolean isConversionToMedatlasOrCFPoint(InputDomain inputDomain){
		return inputDomain.getConversion().getModel().getOutputFormat()==E_TypeConversion.MEDATLAS
				|| inputDomain.getConversion().getModel().getOutputFormat()==E_TypeConversion.NETCDF_MONOSTATION
				||inputDomain.getConversion().getModel().getOutputFormat()==E_TypeConversion.NETCDF_MULTISTATIONS ;
	}
}
