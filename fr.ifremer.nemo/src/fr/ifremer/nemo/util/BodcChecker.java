package fr.ifremer.nemo.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.output.GenerationException;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.BODCResult.BODC_RES_STATUS;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class BodcChecker {
	private static Logger logger;
	private static BodcChecker bodcChecker;
	private final SdnVocabularyManager sdnMgr;

	private BodcChecker(){
		logger = Nemo.getInstance().getLogger();
		sdnMgr = new SdnVocabularyManager(VocabLists.getCf());
	}
	public static BodcChecker getInstance(){
		if (bodcChecker == null ){
			bodcChecker = new BodcChecker();
		}
		return bodcChecker;
	}

	public List<BODCResult> checkParameterCode(TypeData typeData, DataListTypeEnum dataListTypeEnum) throws GenerationException{
		logger.debug(MessageFormat.format(Messages.BODC_check_param, dataListTypeEnum));
		List<BODCResult> results = new ArrayList<BODCResult>();
		if (dataListTypeEnum==DataListTypeEnum.P091){
			results.add( checkP09(typeData));
		}else if(dataListTypeEnum==DataListTypeEnum.P011_VIA_P091){
			results.add( checkP09(typeData));
			results.add( checkP01(typeData));
		}else if(dataListTypeEnum==DataListTypeEnum.P011_VIA_P021){
			results.add( checkP02(typeData));
			results.add( checkP01(typeData));
		}else{
			logger.error(MessageFormat.format(Messages.BODC_check_invalidDataTypeList,dataListTypeEnum));
			throw new GenerationException(MessageFormat.format(Messages.BODC_check_invalidDataTypeList,dataListTypeEnum)); 
		}
			
		return results;
	
	}
	
	public BODCResult checkP09(TypeData typeData){
		logger.debug(Messages.BODC_check_param_p09);
		//  TODO 35230: check label if code is OK
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getEntryKey(), SdnVocabularyManager.LIST_P09);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_P09);
				if (typeData.isP011()){
					replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p01fromp09_deprecated,
							typeData.getEntryKeyP011(),typeData.getFormattedCodeAndName() ,
							 typeData.getEntryKey(),
							 replacement, replacementLabel,
							 SdnVocabularyManager.LIST_P09)+Global.RN;
							
				}else{
					replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p09_deprecated, 
							typeData.getFormattedCodeAndName() ,  typeData.getEntryKey(),
							replacement, replacementLabel,
							SdnVocabularyManager.LIST_P09)+Global.RN;
				}
				
				typeData.setEntryKey(replacement);
				typeData.setEntryTerm(replacementLabel);
			}
		} catch (Exception e) {
			if (typeData.isP011()){
				errorMsg+=MessageFormat.format(Messages.BODC_check_param_p01fromp09_notExist,
						typeData.getEntryKeyP011(),typeData.getFormattedCodeAndName() ,
						 typeData.getEntryKey(),
						 SdnVocabularyManager.LIST_P09)+Global.RN;
			}else{
				errorMsg+=MessageFormat.format(Messages.BODC_check_param_p09_notExist, 
						typeData.getFormattedCodeAndName() ,  typeData.getEntryKey(),
						SdnVocabularyManager.LIST_P09)+Global.RN;
			}
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 
		BODCResult res= new BODCResult(typeData);
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}
		return res;
	}
	public BODCResult checkP02(TypeData typeData){
		logger.debug(Messages.BODC_check_param_p02);
		//  TODO 35230: check label if code is OK
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getEntryKey(), SdnVocabularyManager.LIST_P02);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_P02);
				
				replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p02_deprecated, 
						typeData.getFormattedCodeAndName(),
						typeData.getEntryKey(),
						replacement, replacementLabel,
						SdnVocabularyManager.LIST_P02
						)+Global.RN;
				typeData.setEntryKey(replacement);
				typeData.setEntryTerm(replacementLabel);
			}
		} catch (Exception e) {
			errorMsg+=MessageFormat.format(Messages.BODC_check_param_p02_notExist, 
					typeData.getFormattedCodeAndName(),
					typeData.getEntryKey(),
					SdnVocabularyManager.LIST_P02
					);
//			errorMsg=e.getMessage();		
			logger.error(errorMsg);
			ok=false;
		} 
		BODCResult res= new BODCResult(typeData);
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}
		return res;
	}
	
	public BODCResult checkP01(TypeData typeData){
		logger.debug(Messages.BODC_check_param_p01);
		//  TODO 35230: check label if code is OK
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
//		TypeData typeDataNew = new TypeData(typeData);

		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getEntryKeyP011(), SdnVocabularyManager.LIST_P01);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel( replacement, SdnVocabularyManager.LIST_P01);
				replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p01_deprecated, 
						typeData.getEntryKey() +"(\""+ typeData.getEntryTerm()+"\")",
						
						typeData.getEntryKeyP011(),
						replacement, replacementLabel,
						SdnVocabularyManager.LIST_P01
						)+Global.RN;
				
				typeData.setEntryKeyP011(replacement);
				typeData.setEntryTermP011(replacementLabel);
			}
		} catch (Exception e) {
			
			errorMsg+=MessageFormat.format(Messages.BODC_check_param_p01_notExist, 
					typeData.getEntryKey() , typeData.getEntryKeyP011(),
					typeData.getEntryTermP011(),
					SdnVocabularyManager.LIST_P01
					);
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 


		BODCResult res= new BODCResult(typeData);
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}
		return res;
	}

	public BODCResult checkP06(TypeData typeData, DataListTypeEnum dataListTypeEnum)
	{
		logger.debug(Messages.BODC_check_unit_p06);
		//		boolean p06replaced=false;
		String replaceMsg="";
		String errorMsg="";
		String p06Code;
		boolean ok=true;
		// CHECK P06 CODE AND LABEL
		if (dataListTypeEnum==DataListTypeEnum.P011_VIA_P021){
			p06Code=typeData.getUniteCode();
			String replacement;
			try {
				replacement = sdnMgr.getReplacedByCode(p06Code, SdnVocabularyManager.LIST_P06);
				if (replacement != null){
					String replacementLabel= sdnMgr.getP06UnitLabel(replacement);
					replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p06_deprecated, 
							typeData.getCodeToUse()  + "(P02  "+typeData.getEntryKey()+")",
							p06Code, typeData.getUnite(),
							replacement, replacementLabel,
							SdnVocabularyManager.LIST_P06
							)+Global.RN;
					
					
					typeData.setUniteCode(replacement);
					typeData.setUnite(replacementLabel);
				}else{

					String label = sdnMgr.getLabel(p06Code, SdnVocabularyManager.LIST_P06);
					if (!label.equals(typeData.getUnite())){
						
						replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p06_labelUpdate, 
								typeData.getCodeToUse()  + "(P02  "+typeData.getEntryKey()+")",
								p06Code, typeData.getUnite(),
								label,
								SdnVocabularyManager.LIST_P06
								)+Global.RN;
						
						typeData.setUnite(label);
					}
				}
			}catch (Exception e) {
				ok=false;
				errorMsg+=MessageFormat.format(Messages.BODC_check_param_p06_notExist,
						typeData.getCodeToUse()  + "(P02  "+typeData.getEntryKey()+")",
						p06Code, typeData.getUnite(),
						 SdnVocabularyManager.LIST_P06
						);
//				errorMsg=e.getMessage();
				logger.error(errorMsg);
			}
		}
		else if (dataListTypeEnum==DataListTypeEnum.P011_VIA_P091){
			try {
				p06Code= sdnMgr.getBestVocabMatchCode(
						SdnVocabularyManager.LIST_P06, 
						SdnVocabularyManager.LIST_P09, 
						typeData.getEntryKey()
						);

				String label = sdnMgr.getP06UnitLabel(p06Code);
				if (!typeData.getUnite().equals(label)){

					
					replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p06_labelUpdate, 
							typeData.getCodeToUse() + "(P09:  "+typeData.getEntryKey()+")",
							p06Code, typeData.getUnite(),
							label,
							SdnVocabularyManager.LIST_P06
							)+Global.RN;
					
					typeData.setUniteCode(p06Code);
					typeData.setUnite(label);
				}
			} catch (Exception e) {
				errorMsg=MessageFormat.format(Messages.BODC_check_param_p06_p01fromp09,
						typeData.getEntryKeyP011()+ "(P09 "+typeData.getEntryKey()+")",
						SdnVocabularyManager.LIST_P06+"/"+SdnVocabularyManager.LIST_P09)
						+ " - " + e.getMessage();
				
						
				typeData.setUnite("");
				typeData.setUniteCode("");
//				errorMsg=e.getMessage();
				logger.error(errorMsg);
				ok=false;
			} 
		}else{

			try {
				p06Code= sdnMgr.getBestVocabMatchCode(
						SdnVocabularyManager.LIST_P06, 
						SdnVocabularyManager.LIST_P09, 
						typeData.getEntryKey()
						);
				String label = sdnMgr.getP06UnitLabel(p06Code);

				if (!typeData.getUnite().equals(label)){

					
					replaceMsg+=MessageFormat.format(Messages.BODC_check_param_p06_labelUpdate, 
							typeData.getCodeToUse(),
							p06Code, typeData.getUnite(),
							label,
							SdnVocabularyManager.LIST_P06
							)+Global.RN;
					
					typeData.setUniteCode(p06Code);
					typeData.setUnite(label);
				}
			} catch (Exception e) {
				errorMsg=MessageFormat.format(Messages.BODC_check_param_p06_p01fromp09,
						typeData.getEntryKey(),
						SdnVocabularyManager.LIST_P06+"/"+SdnVocabularyManager.LIST_P09)
						+ " - " + e.getMessage();
				typeData.setUnite("");
				typeData.setUniteCode("");
//				errorMsg=e.getMessage();
				logger.error(errorMsg);
				ok=false;
			} 
		}


		BODCResult res= new BODCResult(typeData);
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}

		return res;
	}


	public BODCResult checkInstrument(TypeData typeData){
		logger.debug(Messages.BODC_check_instrument_l22);
		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(typeData, BODC_RES_STATUS.OK);
		if (typeData.getInstrumentCode()==null || typeData.getInstrumentCode().isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		res= new BODCResult(typeData, BODC_RES_STATUS.OK);
		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getInstrumentCode(), SdnVocabularyManager.LIST_L22);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_L22);
				replaceMsg+=MessageFormat.format(Messages.BODC_check_intrument_l22_deprecated, 
						typeData.getCodeToUse(), 
						typeData.getInstrumentCode(), typeData.getInstrument(),
						replacement, replacementLabel,
						SdnVocabularyManager.LIST_L22)+Global.RN;
				
				typeData.setInstrumentCode(replacement);
				typeData.setInstrument(replacementLabel);
			}
		} catch (Exception e) {
			errorMsg=MessageFormat.format(Messages.BODC_check_intrument_l22_notExist,typeData.getCodeToUse(),
					typeData.getInstrumentCode(), typeData.getInstrument(),
					SdnVocabularyManager.LIST_L22);
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 

		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}

		return res;
	}
	public BODCResult checkFallRate(String fallrateCode, String fallrateLabel) {
		logger.debug(Messages.BODC_check_fallrate_l33);
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setFallrateCode(fallrateCode);
		res.setFallrateLabel(fallrateLabel);
		
		//  TODO 35230: check label if code is OK
		if (fallrateCode==null || fallrateCode.isEmpty()){
			return res;
		}
		
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(fallrateCode, SdnVocabularyManager.LIST_L33);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_L33);
				replaceMsg+=MessageFormat.format(Messages.BODC_check_fallrate_l33_deprecated, fallrateCode,fallrateLabel, replacement, replacementLabel, SdnVocabularyManager.LIST_L33 )+Global.RN;
				res.setFallrateCode(replacement);
				res.setFallrateLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+=MessageFormat.format(Messages.BODC_check_fallrate_l33_notExist, fallrateCode, fallrateLabel, SdnVocabularyManager.LIST_L33);
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 

		
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}

		return res;
	}
	public BODCResult checkShip(String shipCode, String shipLabel) {
		logger.debug(Messages.BODC_check_ship_c17);
		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setShipCode(shipCode);
		res.setShipLabel(shipLabel);
		if (shipCode==null ||shipCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(shipCode, SdnVocabularyManager.LIST_C17);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_C17);
				replaceMsg+=MessageFormat.format(Messages.BODC_check_ship_c17_deprecated, shipCode,shipLabel, replacement, replacementLabel, SdnVocabularyManager.LIST_C17 )+Global.RN;
				res.setShipCode(replacement);
				res.setShipLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+=MessageFormat.format(Messages.BODC_check_ship_c17_notExist, shipCode, shipLabel, SdnVocabularyManager.LIST_C17);
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 

		
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}

		return res;
	}
	public BODCResult checkRegion(String regionCode, String regionLabel) {
		logger.debug(Messages.BODC_check_region_c19);
		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setRegionCode(regionCode);
		res.setRegionLabel(regionLabel);
		
		if (regionCode==null || regionCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(regionCode, SdnVocabularyManager.LIST_C19);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_C19);
				replaceMsg+=MessageFormat.format(Messages.BODC_check_region_c19_deprecated, regionCode,regionLabel, replacement, replacementLabel, SdnVocabularyManager.LIST_C19 )+Global.RN;
				res.setRegionCode(replacement);
				res.setRegionLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+=MessageFormat.format(Messages.BODC_check_country_c18_notExist, regionCode, regionLabel, SdnVocabularyManager.LIST_C19);
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 

		
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}

		return res;
	}
	public BODCResult checkCountry(String countryCode) {
		return checkCountry(countryCode, null);
	}
	public BODCResult checkCountry(String countryCode, String countryLabel) {
		logger.debug(Messages.BODC_check_country_c18);
		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setCountryCode(countryCode);
		res.setCountryLabel(countryLabel);
		
		if (countryCode==null || countryCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(countryCode, SdnVocabularyManager.LIST_C18);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_C18);
				if (countryLabel==null){
					replaceMsg+=MessageFormat.format(Messages.BODC_check_countryNoLabel_c18_deprecated, countryCode, replacement, replacementLabel, SdnVocabularyManager.LIST_C18 )+Global.RN;
				}else{
					replaceMsg+=MessageFormat.format(Messages.BODC_check_country_c18_deprecated, countryCode,countryLabel, replacement, replacementLabel, SdnVocabularyManager.LIST_C18 )+Global.RN;;
				}
				res.setCountryCode(replacement);
				res.setCountryLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			if (countryLabel==null){
				errorMsg+=MessageFormat.format(Messages.BODC_check_countryNoLabel_c18_notExist, countryCode, SdnVocabularyManager.LIST_C18);
			}else{
				errorMsg+=MessageFormat.format(Messages.BODC_check_country_c18_notExist, countryCode, countryLabel, SdnVocabularyManager.LIST_C18);
			}
//			errorMsg=e.getMessage();
			logger.error(errorMsg);
			ok=false;
		} 

		
		if (ok){
			if(replaceMsg.isEmpty()){
				res.setResStatus(BODC_RES_STATUS.OK);
			}else{
				res.setWarningMsg(replaceMsg);
				res.setResStatus(BODC_RES_STATUS.WARNING);
			}
		}else{
			res.setErrorMsg(errorMsg);
			res.setResStatus(BODC_RES_STATUS.KO);	
		}

		return res;
	}
}
