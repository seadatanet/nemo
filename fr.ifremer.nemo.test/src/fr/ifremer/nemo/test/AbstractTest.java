package fr.ifremer.nemo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;

import fr.ifremer.medatlas.exceptions.MedatlasReaderException;
import fr.ifremer.medatlas.input.MedatlasInputFileManager;
import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.NemoShell;
import fr.ifremer.nemo.domain.LabelParameterManager;
import fr.ifremer.nemo.domain.NetCDFLongNameParameterManager;
import fr.ifremer.nemo.domain.StandardNameParameterManager;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.CFPointDriverImpl;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasSDNDriverImpl;
import fr.ifremer.octopus.io.driver.impl.OdvSDNDriverImpl;
import fr.ifremer.seadatanet.cfpoint.exceptions.CFPointException;
import fr.ifremer.seadatanet.cfpoint.input.CFReader;
import fr.ifremer.seadatanet.odv.input.OdvReader;
import fr.ifremer.seadatanet.odv.output.OdvException;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.Format;

public abstract class AbstractTest {
	// racine du repertoire contenant les tests
	public static String dataRootPath = "datatest";
	
	// default prefixes and suffixes
	public static final String cdiSummaryPrefix = "cdiSummary";
	public static final String junitSuffix = "_junit";
	public static final String refSuffix = "_REFERENCE";
	public static final String curveSuffix = "_CURVE";
	public static final String edmerpSuffix = "_EDMERP";
	public static final String gpxSuffix = "_GPX.gpx";
	
	// default file names
	public String inputName = "input";
	public String outputName = "output_junit";
	public String modelName = "model.xml";
	public static final String cdiSummaryFileName = cdiSummaryPrefix + junitSuffix;
	
	public NemoShell shell = new NemoShell();
	public boolean couplingTableUpdate = false;
	public String couplingTablePrefix = Global.EMPTY_STRING;
	public String cdiSummaryFile = Global.EMPTY_STRING;

	protected static String batchCommands;
	public boolean sortDatas = false;
	public String datasetRef = Global.EMPTY_STRING;
	public static Logger batchLogger;

	protected Message message;

	@BeforeClass
	public static void setupBeforeClass() {
		try {
			File extRes = new File("../fr.ifremer.nemo/resources/externalResources");
			if (!extRes.exists() || extRes.list() == null || extRes.list().length == 0) {
				extRes = new File("resources/externalResources");
			}
			FileInputStream extResourcesConf = new FileInputStream(new File("resources/externalResourcesConfiguration.yaml"));
			ExternalResourcesManager.getInstance(extResourcesConf, extRes.getPath());
			try {
				ExternalResourcesManager.getInstance().getSdnVocabularyManager().init(true);
			} catch (Throwable e) {
				// Catch Throwable (not just Exception) to handle NoClassDefFoundError
				// thrown when JDK internal classes (e.g. javax.xml.ws, CatalogManager)
				// are absent on Java 11+ builds.
				// Once a class fails to load (NCDFE), the JVM marks it permanently as
				// failed, so init(false) may also throw the same error if it touches
				// the same class hierarchy. We catch it too.
				Nemo.getInstance().getLogger().warn("Vocab online update failed, falling back to cached resources: " + e.getMessage());
				try {
					ExternalResourcesManager.getInstance().getSdnVocabularyManager().init(false);
				} catch (Throwable e2) {
					Nemo.getInstance().getLogger().warn("Vocab offline init also failed (JAX-WS class loading issue): " + e2.getMessage() + ". Tests will continue with potentially incomplete vocabulary.");
				}
			}
		} catch (Throwable e) {
			Nemo.getInstance().getLogger().error("setupBeforeClass failed: " + e.getMessage());
		}
	}
	public Message convertWithNemoBatch(Message message, Conversion conversion, E_TypeConversion convType, String inputAbsolutePath, String outputAbsolutePath, boolean isDateIso, boolean sort) {

		boolean res = false;
		String output = outputAbsolutePath + File.separator + outputName;
		String model = outputAbsolutePath + File.separator + modelName;
		couplingTablePrefix = outputAbsolutePath;
		couplingTableUpdate = false;
		cdiSummaryFile = outputAbsolutePath + File.separator + "cdiSummary_junit";
		File mod = new File(model);
		Nemo.getInstance().resetInstance();
		Nemo.getInstance().setBatchMode(true);
		Nemo.getInstance().getConfig().setIdEdmoRespCdi("486");// 486 is ifremer
		Nemo.getInstance().getConfig().setIdEdmoRespCsr("487");
		Nemo.getInstance().getConfig().setCodeEdmoDonnees("0");
		Nemo.getInstance().getConfig().setIdEdmoDistDonnees("0");
		Nemo.getInstance().getConfig().setIdEdmoRespDonnees("0");
		Nemo.getInstance().getConfig().setTypePlateForme("");

		Nemo.getInstance().getConfig().setDateISO(isDateIso);
		// add long Name for test
		NetCDFLongNameParameterManager.getInstance().getLongNamesMap().putAll(netCDFLongNamesMap());
		// add standard Name for test
		StandardNameParameterManager.getInstance().getStandardNamesMap().putAll(netCDFStandardNamesMap());
		LabelParameterManager.getInstance().cleanUSersLabels();
		// edmo code
		shell = new NemoShell();
		String batchCmd = "";
		if (!mod.exists()) {
			message.isModelFound = false;
		} else {
			message.isModelFound = true;

			try {
				String localCsrIdentifier="";
				String bshCsrReference="";
				String cruiseName="";
				String cruiseComment="";
				String shipCode="";
				
				res = shell.main(inputAbsolutePath, output, model, conversion, sortDatas, convType, datasetRef,
						localCsrIdentifier,  bshCsrReference,  cruiseName,  cruiseComment,
						cdiSummaryFile, couplingTableUpdate, couplingTablePrefix, shipCode, batchLogger);
			} catch (Exception e) {
				e.printStackTrace();
				message.conversionRes = res;
				message.conversionError = e.getMessage();
			}

			batchCmd = "nemo_batch";
			batchCmd += Global.SPACE + Global.MOINS + Global.optInputFile;
			batchCmd += Global.SPACE + inputAbsolutePath;

			batchCmd += Global.SPACE + Global.MOINS + Global.optModelFile;
			batchCmd += Global.SPACE + model;

			batchCmd += Global.SPACE + Global.MOINS + Global.optOutputFile;
			batchCmd += Global.SPACE + output + "_batch";

			batchCmd += Global.SPACE + Global.MOINS + Global.optConv;
			batchCmd += Global.SPACE + conversion;
			if (E_TypeConversion.ODV_MULTISTATIONS == convType) {
				batchCmd += Global.SPACE + Global.MOINS + Global.optOdvMulti;
			}
			batchCmd += Global.SPACE + Global.MOINS + Global.optCdiSummary;
			batchCmd += Global.SPACE + cdiSummaryFile + "_batch";

			if (couplingTableUpdate) {
				batchCmd += Global.SPACE + Global.MOINS + Global.optCouplingTableUpdate;
				batchCmd += Global.SPACE + Global.MOINS + Global.optCouplingTablePrefix;
				batchCmd += Global.SPACE + couplingTablePrefix;
			}
			batchCommands += Global.RN + batchCmd;
		}

		message.conversionRes = res;
		return message;
	}

	protected boolean checkData(String filePath)	throws CFPointException, OdvException, MedatlasReaderException {

		boolean checkOk = true;
		DriverManager driverManager = new DriverManagerImpl();
		driverManager.registerNewDriver(new MedatlasSDNDriverImpl());
		driverManager.registerNewDriver(new OdvSDNDriverImpl());
		driverManager.registerNewDriver(new CFPointDriverImpl());
		Format format = Format.UNKNOWN;
		Driver d = null;
		try {
			d = driverManager.findDriverForFile(filePath);
			format=d.getFormat();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (format.equals(Format.CFPOINT)) {

			CFReader reader = null;
			try {
				reader = new CFReader(filePath);
				if (reader.getValidator().hasErrors()) {
					checkOk = false;
				}
			} catch (Exception e) {
				checkOk = false;
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

		} else if (format.equals(Format.ODV_SDN)) {
			OdvReader reader = null;
			try {
				reader = new OdvReader(filePath);
				if (reader.getValidator().hasErrors()) {
					checkOk = false;
				}
			} catch (Exception e) {


				checkOk = false;
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

		} else if (format.equals(Format.MEDATLAS_SDN)) {
			MedatlasInputFileManager reader = null;
			try {
				reader = new MedatlasInputFileManager(filePath);
				reader.close();
			} catch (Exception e) {
				checkOk = false;
			} finally {
				if (reader != null) {
					reader.close();
				}
			}

		}
		return checkOk;
	}

	// Long Names utilisé pour les tests de non regression
	// à completer si ajout de test
	protected HashMap<String, String> netCDFLongNamesMap() {
		HashMap<String, String> longNames = new HashMap<String, String>();

		longNames.put("NTRAYYDZ", "Concentration of Nitrate"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PRESPR01", "Pressure"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ADEPZZ01", "Depth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PSSTZZ01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ODSDM021", "Salinity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("DOXYZZ01", "Concentration of oxygen"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("DOXMZZXX", "Concentration of oxygen per unit mass of the water body"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("DOXYZZXX", "Concentration of oxygen per unit volume of the water body"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("FLUOZZZZ", "Fluorescence"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPET01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRIZZXX", "Concentration of nitrite"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PSLTZZ01", "Salinity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("COREDIST", "Depth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ASLVMSZZ", "Surface elevation"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MAGNFLDX", "Magnetic Field of the Earth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MAGNFL02", "Magnetic Field of the Earth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MAGDCORR", "Diurnal Varaition"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("GRAVFLDX", "Gravity Field of the Earth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("EOTVCORR", "Eotvoscorrection"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("FAGRVANM", "Gravity Field Anomaly"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PBXXISPW", "Concentration of lead"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("MZNSP012", "Concentration of zinc"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PRESCX01", "Pressure"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NO2FLTIC", "Concentration of nitrite"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRAAA04", "Concentration of nitrate"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PHOSAAD3", "Concentration of phosphate"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("AMONHOTX", "Concentration of ammonium"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CPHLFLP2", "Concentration of chlorophll-a"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHLBINTC", "Concentration of chlorophll-b"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHLAEPIM", "Concentration of chlorophll-a"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("SPHAFLPD", "Concentration of phaeopigments"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHLBMHP1", "Concentration of chlorophll-b"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CPHLHPP2", "Concentration of chlorophll-a"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PHPICP01", "Concentration of phosphorus"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ORGPMSZZ", "Concentration of phosphorus"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TPHSXFP2", "Concentration of phosphorus"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("DEPHPM01", "Depth"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRAZZXX", "Concentration of nitrite"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CNDCST0P1", "Electrical conductivity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPSZ01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("SSALSG01", "Salinity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPPR01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PBCNXTXT", "Concentration of lead"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("TEMPZZXX", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ATTNZZ01", "volume beam attenuation coefficient"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CPHLPM01", "mass concentration of chlorophyll a in sea water"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("AHSLZZ01", "Height above sea level"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("AHGTZZ01", "Height above sea level"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CJDY1101", "Julien Date"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CAPASS01", "Pressure"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CDTBSS01", "Temperature of the atmophere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CDTSZZ01", "Temperature standard deviation of the atmosphere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHMSZZ01", "Specific humidity standard of the atmosphere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CHUMSS01", "Specific humidity of the atmosphere"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ESEWZZ01", "Eastward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ESNSZZ01", "Northward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("EWEWSS01", "Eastward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("EWNSSS01", "Northward wind Velocity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CBTASS01", "Temperature"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CNDCST01", "Electrical Conductivity by CTD"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CNDCZZ01", "Electrical Conductivity"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CDTAPS01", "Temperature of the atmosphere by psychrometer dry bulb"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("SVELXXXX", "Sound velocity in the water body"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ASLVZZ01", "Surface elevation (unspecified datum) of the water body"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NTRIAAD1", "ntri"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CSERTFBL", "CSERTFBL"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ANTHHPP1", "ANTHHPP1"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("SSALST01", "psal"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("BCCDSP01", "flu2"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("PSALST02", "psal"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ALKGWX01", "alkg"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("NZTW0009", "NZTW0009"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("CORGWCDZ", "CORGWCDZ long name"); //$NON-NLS-1$//$NON-NLS-2$
		longNames.put("ODSDM010", "ODSDM010 long name"); //$NON-NLS-1$//$NON-NLS-2$

		return longNames;
	}

	// Standard Names utilisé pour les tests de non regression
	// à completer si ajout de test
	protected HashMap<String, String> netCDFStandardNamesMap() {
		HashMap<String, String> standardNames = new HashMap<String, String>();

		standardNames.put("PRESPR01", "sea_water_pressure"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRAYYDZ", "mole_concentration_of_nitrate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		// standardNames.put("ADEPZZ01", "depth"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PSSTZZ01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ODSDM021", "sea_water_salinity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("DOXYZZ01", "mole_concentration_of_dissolved_molecular_oxygen_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPET01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRIZZXX", "moles_of_nitrite_per_unit_mass_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PSLTZZ01", "sea_water_salinity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("COREDIST", "depth"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ASLVMSZZ", "sea_surface_height_above_sea_level"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PRESCX01", "sea_water_pressure"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NO2FLTIC", "moles_of_nitrite_per_unit_mass_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRAAA04", "mole_concentration_of_nitrate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PHOSAAD3", "mole_concentration_of_phosphate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("AMONHOTX", "mole_concentration_of_ammonium_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CPHLFLP2", "mass_concentration_of_chlorophyll_a_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CHLBINTC", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CHLAEPIM", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CHLBMHP1", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CPHLHPP2", "mass_concentration_of_chlorophyll_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("PHPICP01", "mole_concentration_of_particulate_organic_matter_expressed_as_phosphorus_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TPHSXFP2", "mole_concentration_of_particulate_organic_matter_expressed_as_phosphorus_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("DEPHPM01", "depth"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("NTRAZZXX", "mole_concentration_of_nitrate_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CNDCST01", "sea_water_electrical_conductivity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPSZ01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("SSALSG01", "sea_water_salinity"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPPR01", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("TEMPZZXX", "sea_water_temperature"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("ATTNZZ01", "volume_beam_attenuation_coefficient_of_radiative_flux_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("CPHLPM01", "mass_concentration_of_chlorophyll_a_in_sea_water"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("AHSLZZ01", "height"); //$NON-NLS-1$//$NON-NLS-2$
		standardNames.put("AHGTZZ01", "height"); //$NON-NLS-1$//$NON-NLS-2$
		return standardNames;
	}
}
