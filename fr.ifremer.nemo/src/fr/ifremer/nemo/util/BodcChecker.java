package fr.ifremer.nemo.util;

import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.ui.edit.data.TypeData;
import fr.ifremer.nemo.util.BODCResult.BODC_RES_STATUS;
import fr.ifremer.nemo.util.Global.DataListTypeEnum;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class BodcChecker {

	private static BodcChecker bodcChecker;
	private final SdnVocabularyManager sdnMgr;

	private BodcChecker(){
		sdnMgr = new SdnVocabularyManager(VocabLists.getCf());
	}
	public static BodcChecker getInstance(){
		if (bodcChecker == null ){
			bodcChecker = new BodcChecker();
		}
		return bodcChecker;
	}

	public BODCResult checkParameterCode(TypeData typeData){
		if (typeData.isP011()){
			return checkP01(typeData);
		}
		return checkP09(typeData);
	}
	
	public BODCResult checkP09(TypeData typeData){
		//  TODO 35230: check label if code is OK
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;

		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getEntryKey(), SdnVocabularyManager.LIST_P09);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_P09);
				replaceMsg+="Parameter "+ typeData.getFormattedCodeAndName() 
						+ " is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				typeData.setEntryKey(replacement);
				typeData.setEntryTerm(replacementLabel);
			}
		} catch (Exception e) {
			errorMsg="Parameter "+ typeData.getEntryKey() 
					+" ("+typeData.getEntryTerm()+") does not exist in BODC list " + SdnVocabularyManager.LIST_P09;// TODO 35230 message
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
		//  TODO 35230: check label if code is OK
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;


		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getEntryKeyP011(), SdnVocabularyManager.LIST_P01);// TODO 35230 message
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel( replacement, SdnVocabularyManager.LIST_P01);
				replaceMsg+="Parameter "+ typeData.getEntryKeyP011() 
						+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				typeData.setEntryKeyP011(replacement);
				typeData.setEntryTermP011(replacementLabel);
			}
		} catch (Exception e) {
			errorMsg="Parameter "+ typeData.getEntryKeyP011() 
					+" ("+typeData.getEntryTermP011()+") does not exist in BODC list " + SdnVocabularyManager.LIST_P01;// TODO 35230 message
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

	public BODCResult checkP06(TypeData typeData)
	{
		//		boolean p06replaced=false;
		String replaceMsg="";
		String errorMsg="";
		String p06Code;
		boolean ok=true;

		// CHECK P06 CODE AND LABEL
		if (RulesManager.getInstance().getMyDataListType()==DataListTypeEnum.P011_VIA_P021){
			p06Code=typeData.getUniteCode();
			String replacement;
			//			p06Code="UPDS";
			try {
				replacement = sdnMgr.getReplacedByCode(p06Code, SdnVocabularyManager.LIST_P06);
				if (replacement != null){
					//					p06replaced=true;
					String replacementLabel= sdnMgr.getP06UnitLabel(replacement);
					replaceMsg+="Parameter "+ typeData.getCodeToUse() 
							+ " : unit \""+typeData.getUnite()+"\" (P06 code: "+ p06Code+")"
							+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
					typeData.setUniteCode(replacement);
					typeData.setUnite(replacementLabel);
				}else{


					String label = sdnMgr.getLabel(p06Code, SdnVocabularyManager.LIST_P06);
					//					label="toto";
					if (!label.equals(typeData.getUnite())){
						//						p06replaced=true;
						replaceMsg+="Parameter "+ typeData.getCodeToUse() 
								+ " : unit label \""+typeData.getUnite()+"\" (P06 code: "+ p06Code+")"
								+ "\" has been replaced with \"" + label+"\""+Global.RN;// TODO 35230 message
						typeData.setUnite(label);
					}
				}
			}catch (Exception e) {
				ok=false;
				errorMsg="error on parameter "+ typeData.getCodeToUse() + " : unit "+typeData.getUniteCode()
						+" ("+typeData.getUnite()+") does not exist in BODC list " + SdnVocabularyManager.LIST_P06;// TODO 35230 message
			}
		}
		else if (RulesManager.getInstance().getMyDataListType()==DataListTypeEnum.P011_VIA_P091){
			try {
				p06Code= sdnMgr.getBestVocabMatchCode(
						SdnVocabularyManager.LIST_P06, 
						SdnVocabularyManager.LIST_P09, 
						typeData.getEntryKey()
						);


				if (!typeData.getUnite().equals(sdnMgr.getP06UnitLabel(p06Code))){

					replaceMsg+="Parameter P01 "+ typeData.getEntryKeyP011() + " selected from P09 " + typeData.getEntryKey()
							+ " : unit label \""+typeData.getUnite()+"\" does not match with unit found in BODC mapping " 
							+ SdnVocabularyManager.LIST_P06 + "/"+SdnVocabularyManager.LIST_P09+" for this P09 parameter"+Global.RN;
					replaceMsg+="\""+typeData.getUnite()+ "\" has been replaced with \"" + sdnMgr.getP06UnitLabel(p06Code)+"\" (P06 code: "+ p06Code+")"+Global.RN;// TODO 35230 message
					typeData.setUniteCode(p06Code);
					typeData.setUnite(sdnMgr.getP06UnitLabel(p06Code));
					//						p06replaced=true;
				}
			} catch (Exception e) {
				errorMsg="unable to get P06 unit label for parameter P01 " + typeData.getEntryKeyP011() + " selected from P09 " + typeData.getEntryKey()+" / " + e.getMessage();// TODO 35230 message
				typeData.setUnite("");
				typeData.setUniteCode("");
				ok=false;
			} 
		}else{

			try {
				p06Code= sdnMgr.getBestVocabMatchCode(
						SdnVocabularyManager.LIST_P06, 
						SdnVocabularyManager.LIST_P09, 
						typeData.getEntryKey()
						);


				if (!typeData.getUnite().equals(sdnMgr.getP06UnitLabel(p06Code))){

					replaceMsg+="Parameter "+ typeData.getEntryKey()
							+ " : unit label \""+typeData.getUnite()+"\" does not match with unit found in BODC mapping " 
							+ SdnVocabularyManager.LIST_P06 + "/"+SdnVocabularyManager.LIST_P09+" for this P09 parameter"+Global.RN;
					replaceMsg+="\""+typeData.getUnite()+ "\" has been replaced with \"" + sdnMgr.getP06UnitLabel(p06Code)+"\" (P06 code: "+ p06Code+")"+Global.RN;// TODO 35230 message
					typeData.setUniteCode(p06Code);
					typeData.setUnite(sdnMgr.getP06UnitLabel(p06Code));
					//					p06replaced=true;
				}
			} catch (Exception e) {
				errorMsg="unable to get P06 unit label for parameter " + typeData.getEntryKey()+" / " + e.getMessage();// TODO 35230 message
				typeData.setUnite("");
				typeData.setUniteCode("");
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
		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(typeData, BODC_RES_STATUS.OK);
		if (typeData.getInstrumentCode().isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;

		try {
			String replacement = sdnMgr.getReplacedByCode(typeData.getInstrumentCode(), SdnVocabularyManager.LIST_L22);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_L22);
				replaceMsg+="Parameter "+ typeData.getCodeToUse() 
						+ " : instrument \""+typeData.getInstrument()+"\" (L22 code: "+ typeData.getInstrumentCode()+")"
						+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				typeData.setInstrumentCode(replacement);
				typeData.setInstrument(replacementLabel);
			}
		} catch (Exception e) {
			errorMsg="Parameter "+typeData.getCodeToUse() +":"
					+" instrument code \""+typeData.getInstrumentCode()+"\" ("+typeData.getInstrument()+") does not exist in BODC list " + SdnVocabularyManager.LIST_L22;// TODO 35230 message
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
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setFallrateCode(fallrateCode);
		res.setFallrateLabel(fallrateLabel);
		
		//  TODO 35230: check label if code is OK
		if (fallrateCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(fallrateCode, SdnVocabularyManager.LIST_L33);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_L33);
				replaceMsg+=
						 "Fall rate \""+fallrateCode+"\" ( "+ fallrateLabel+")"
						+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				res.setFallrateCode(replacement);
				res.setFallrateLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+=" Fall rate \""+fallrateCode+"\" ("+fallrateLabel+") does not exist in BODC list " + SdnVocabularyManager.LIST_L33;// TODO 35230 message
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

		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setShipCode(shipCode);
		res.setShipLabel(shipLabel);
		if (shipCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(shipCode, SdnVocabularyManager.LIST_C17);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_C17);
				replaceMsg+=
						 "Ship \""+shipCode+"\" ( "+ shipLabel+")"
						+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				res.setShipCode(replacement);
				res.setShipLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+=" Ship \""+shipCode+"\" ("+shipLabel+") does not exist in BODC list " + SdnVocabularyManager.LIST_C17;// TODO 35230 message
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

		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setRegionCode(regionCode);
		res.setRegionLabel(regionLabel);
		
		if (regionCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(regionCode, SdnVocabularyManager.LIST_C19);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_C19);
				replaceMsg+=
						 "Region \""+regionCode+"\" ( "+ regionLabel+")"
						+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				res.setRegionCode(replacement);
				res.setRegionLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+=" Region \""+regionCode+"\" ("+regionLabel+") does not exist in BODC list " + SdnVocabularyManager.LIST_C19;// TODO 35230 message
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
		//  TODO 35230: check label if code is OK
		BODCResult res= new BODCResult(BODC_RES_STATUS.OK);
		res.setCountryCode(countryCode);
		res.setCountryLabel(countryLabel);
		
		if (countryCode.isEmpty()){
			return res;
		}
		String replaceMsg="";
		String errorMsg="";
		boolean ok=true;
		try {
			String replacement = sdnMgr.getReplacedByCode(countryCode, SdnVocabularyManager.LIST_C18);
			if (replacement != null){
				String replacementLabel= sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_C18);
				replaceMsg+=
						 "Country \""+countryCode+"\" ( "+ countryLabel+")"
						+ "\" is deprecated. It has been replaced with \"" + replacement+"\" ("+ replacementLabel+")"+Global.RN;// TODO 35230 message
				res.setCountryCode(replacement);
				res.setCountryLabel(replacementLabel);
				
			}
		} catch (Exception e) {
			errorMsg+="Country \""+countryCode+"\" ("+countryLabel+") does not exist in BODC list " + SdnVocabularyManager.LIST_C18;// TODO 35230 message
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
