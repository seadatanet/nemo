package fr.ifremer.nemo.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.BODCResult;
import fr.ifremer.nemo.util.BodcChecker;
import fr.ifremer.nemo.util.BODCResult.BODC_RES_STATUS;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;

public class BodcTest extends AbstractTest{
	private static Logger testLogger=LogManager.getLogger("nemoJunits");
	
	private void logBODCTitle(String title) {
		testLogger.info("=========== " + title + "==========================");
	}

	private void logResBodc(BODCResult res) {
		if (res.getResStatus() == BODC_RES_STATUS.OK) {
			testLogger.info("   -> OK");
		} else if (res.getResStatus() == BODC_RES_STATUS.WARNING) {
			testLogger.warn("   -> WARN");
			testLogger.warn(res.getWarningMsg());
		} else {
			testLogger.error("   -> ERROR");
			testLogger.error(res.getErrorMsg());
		}
	}

	private void logResListBodc(List<BODCResult> bodcResList) {
		for (BODCResult res : bodcResList) {
			logResBodc(res);
		}
	}

	@Test
	public void testBODCSHip() {
		Nemo.getInstance().setLogger(testLogger);
		logBODCTitle("C17 OK");
		BODCResult res = BodcChecker.getInstance().checkShip("35AY", "Alis");
		logResBodc(res);

		logBODCTitle("C17 KO: code not exist");
		res = BodcChecker.getInstance().checkShip("zzzz", "Alis");
		logResBodc(res);

		logBODCTitle("C17 KO: bad label");
		res = BodcChecker.getInstance().checkShip("35AY", "zzzzz");
		logResBodc(res);

	}

	@Test
	public void testBODCRegion() {
		Nemo.getInstance().setLogger(testLogger);
		logBODCTitle("C19 OK");
		BODCResult res = BodcChecker.getInstance().checkRegion("8_3", "Coral Sea");
		logResBodc(res);

		logBODCTitle("C19 KO: code not exist");
		res = BodcChecker.getInstance().checkRegion("0_0", "Coral Sea");
		logResBodc(res);

		logBODCTitle("C19 KO: bad label");
		res = BodcChecker.getInstance().checkRegion("8_3", "zzzz");
		logResBodc(res);

	}

	@Test
	public void testBODCCountry() {
		Nemo.getInstance().setLogger(testLogger);
		logBODCTitle("C18 OK");
		BODCResult res = BodcChecker.getInstance().checkCountry("35");
		logResBodc(res);

		logBODCTitle("C18 KO: code not exist");
		res = BodcChecker.getInstance().checkCountry("00");
		logResBodc(res);

		logBODCTitle("C18 KO: bad label");
		res = BodcChecker.getInstance().checkCountry("35", "zzzz");
		logResBodc(res);

	}

	@Test
	public void testBODCFallrate() {
		Nemo.getInstance().setLogger(testLogger);
		logBODCTitle("L33 OK");
		BODCResult res = BodcChecker.getInstance().checkFallRate("904", "AML oceanographic CTD");
		logResBodc(res);

		logBODCTitle("L33 KO: code not exist");
		res = BodcChecker.getInstance().checkFallRate("0", "AML oceanographic CTD");
		logResBodc(res);

		logBODCTitle("L33 KO: bad label");
		res = BodcChecker.getInstance().checkFallRate("904", "zzz");
		logResBodc(res);

	}

	@Test
	public void testBODCCheckParameterP09() {
		Nemo.getInstance().setLogger(testLogger);
		TypeData typeCourant;

		// typeCourant = new TypeData(theEntryKey, theEntryTerm, theCodeP01, theNameP01, theUniteCode, theUniteLabel, theInstrumentCode, theInstrumentLabel, itheIsP011)
		logBODCTitle("P09 OK");
		typeCourant = new TypeData("PHOS", "PHOSPHATE (PO4-P) CONTENT", null, null, null, "millimole/m3", null, null, false);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			testLogger.error(e.getMessage());
		}
		logBODCTitle("P09 KO: deprecated");
		typeCourant = new TypeData("TUR1", "LIGHT DIFFUSION COEFFICIENT", null, null, null, "millimole/m3", null, null, false);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			testLogger.error(e.getMessage());
		}

		logBODCTitle("P09 KO: code not exist");
		typeCourant = new TypeData("XXXX", "PHOSPHATE (PO4-P) CONTENT", null, null, null, "millimole/m3", null, null, false);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logBODCTitle("P09 KO: bad P09 label"); // TODO a tester, ou a corriger automatiquement dans le fichier de sortie?
		typeCourant = new TypeData("PHOS", "dddd", null, null, null, "millimole/m3", null, null, false);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logBODCTitle("P09 KO: bad P06 label");
		typeCourant = new TypeData("PHOS", "PHOSPHATE (PO4-P) CONTENT", null, null, null, "sss", null, null, false);
		BODCResult res = BodcChecker.getInstance().checkP06(typeCourant, DataListTypeEnum.P091);
		logResBodc(res);

		logBODCTitle("P09 KO: bad L22 instrument code");
		typeCourant = new TypeData("PHOS", "PHOSPHATE (PO4-P) CONTENT", null, null, null, "millimole/m3", "xxx", "xxx", false);
		res = BodcChecker.getInstance().checkInstrument(typeCourant);
		logResBodc(res);

		logBODCTitle("P09 KO: deprecated L22 instrument code");
		typeCourant = new TypeData("PHOS", "PHOSPHATE (PO4-P) CONTENT", null, null, null, "Micromoles per litre", "TOOL0697", "xxx", false);
		res = BodcChecker.getInstance().checkInstrument(typeCourant);
		logResBodc(res);

		logBODCTitle("P09 KO: bad L22 instrument label"); // TODO non testé
		typeCourant = new TypeData("PHOS", "PHOSPHATE (PO4-P) CONTENT", null, null, null, "millimole/m3", "TOOL000X", "xxx", false);
		res = BodcChecker.getInstance().checkInstrument(typeCourant);
		logResBodc(res);
	}

	@Test
	public void testBODCCheckParameterP01fromP09() {
		Nemo.getInstance().setLogger(testLogger);
		TypeData typeCourant;

		// typeCourant = new TypeData(theEntryKey, theEntryTerm, theCodeP01, theNameP01, theUniteCode, theUniteLabel, theInstrumentCode, theInstrumentLabel, itheIsP011)
		logBODCTitle("P01fromP09 OK");
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", null, null, true);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P011_VIA_P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			testLogger.error(e.getMessage());
		}

		logBODCTitle("P01fromP09 KO: code P09 TUR1 deprecated");
		typeCourant = new TypeData("TUR1", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", null, null, true);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P011_VIA_P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			testLogger.error(e.getMessage());
		}

		logBODCTitle("P01fromP09 KO: code P09 xxx not exist");
		typeCourant = new TypeData("xxx", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", null, null, true);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P011_VIA_P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logBODCTitle("P01fromP09 KO: code  P01 PSSTZZ01 deprecated");
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "PSSTZZ01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", null, null, true);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P011_VIA_P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logBODCTitle("P01fromP09 KO: code  P01 xxxxxxxx does not exist");
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "xxxxxxxx", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", null, null, true);
		try {
			List<BODCResult> bodcResList = BodcChecker.getInstance().checkParameterCode(typeCourant, DataListTypeEnum.P011_VIA_P091);
			logResListBodc(bodcResList);
		} catch (GenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logBODCTitle("P01fromP09 KO: bad P06 label xxx");
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "xxxxx", null, null, true);
		BODCResult res = BodcChecker.getInstance().checkP06(typeCourant, DataListTypeEnum.P011_VIA_P091);
		logResBodc(res);

		logBODCTitle("P01fromP09 KO: bad L22 instrument code");
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", "xxx", null, true);
		res = BodcChecker.getInstance().checkInstrument(typeCourant);
		logResBodc(res);

		logBODCTitle("P01fromP09 KO: deprecated L22 instrument code");
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", "TOOL0697", "xxx", true);
		res = BodcChecker.getInstance().checkInstrument(typeCourant);
		logResBodc(res);

		logBODCTitle("P01fromP09 KO: bad L22 instrument label"); // TODO non testé
		typeCourant = new TypeData("PRES", "SEA PRESSURE sea surface=0", "PRESCX01", "Pressure (spatial co-ordinate) exerted by the water body by computation of depth using unspecified drop rate algorithm converted to pressure using UNESCO algorithm", null, "millimole/m3", "xxx", "xxx", true);
		res = BodcChecker.getInstance().checkInstrument(typeCourant);
		logResBodc(res);
	}
}
