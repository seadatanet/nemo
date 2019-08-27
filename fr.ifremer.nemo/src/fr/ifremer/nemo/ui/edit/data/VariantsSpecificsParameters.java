package fr.ifremer.nemo.ui.edit.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.config.VocabLists;
import fr.ifremer.nemo.ui.RulesManager;
import fr.ifremer.nemo.util.BodcChecker;
import fr.ifremer.nemo.util.Global.E_Variant;
import fr.ifremer.nemo.util.Intern;
import fr.ifremer.seadatanet.odv.input.model.Parameter.ParameterType;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidator;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidator.PARAMETER_REQUIREMENT;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorBio;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorClassic;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorCyto;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorMicro;
import fr.ifremer.seadatanet.odv.input.validator.SpecificParameter;
import fr.ifremer.sismer_tools.SismerToolsException;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class VariantsSpecificsParameters {

	private static VariantsSpecificsParameters instance ;
//	private final HashMap<E_Variant, List<TypeData>> lists;


	public static VariantsSpecificsParameters getInstance() {
		if (instance==null) {
			instance = new VariantsSpecificsParameters();
		}
		return instance;
	}
	public VariantsSpecificsParameters() {
//		lists=new HashMap<>();
//		lists.put(E_Variant.PHYSICOCHEMICAL,null);
//		lists.put(E_Variant.BIOLOGICAL,null);
//		lists.put(E_Variant.FLOWCYTO,null);
//		lists.put(E_Variant.MICROLITTER,null);
	}

	public List<TypeData> getParameters(){
		return getParameters(Nemo.getInstance().getDomain().getMedia().getModel().getVariant());
	}
	public List<TypeData> getParameters(E_Variant variant){
		List<TypeData> specificList=new ArrayList<>();
//		specificList = lists.get(variant);
//		if (specificList!=null) {
//			return specificList;
//		}
//		lists.put(E_Variant.UNKNOWN,new ArrayList<>());
//		lists.put(E_Variant.PHYSICOCHEMICAL,null);
//		lists.put(E_Variant.BIOLOGICAL,null);
//		lists.put(E_Variant.FLOWCYTO,null);
//		lists.put(E_Variant.MICROLITTER,null);

		switch (variant) {
		case PHYSICOCHEMICAL:
			specificList=getSpecificParams(new OdvSpecificValidatorClassic(null, null, null));
//			lists.put(E_Variant.PHYSICOCHEMICAL,specificList);
			break;
		case BIOLOGICAL:
			specificList=getSpecificParams(new OdvSpecificValidatorBio(null, null, null));
//			lists.put(E_Variant.BIOLOGICAL,specificList);
			break;
		case MICROLITTER:
			specificList=getSpecificParams(new OdvSpecificValidatorMicro(null, null, null));
//			lists.put(E_Variant.MICROLITTER,specificList);
			break;
		case FLOWCYTO:
			specificList=getSpecificParams(new OdvSpecificValidatorCyto(null, null, null));
//			lists.put(E_Variant.FLOWCYTO,specificList);
			break;
		default:
			specificList=new ArrayList<>();
			break;
		}

		return specificList;
	}


	private List<TypeData> getSpecificParams(OdvSpecificValidator validator)	{ 
		List<SpecificParameter>specificParamsList=new ArrayList<>();
		List<TypeData>specificList=new ArrayList<>();
		HashMap<String[], PARAMETER_REQUIREMENT> expectedParams = validator.getExpectedParams();
		for (Entry<String[], PARAMETER_REQUIREMENT> p : expectedParams.entrySet()) {
			if (p.getValue()==PARAMETER_REQUIREMENT.MAND) {
				if (p.getKey()[0].equals(SdnVocabularyManager.P01_DTUT8601_ISODATE)){
					// do not add in data tab (date is managed in station tab)
				}else {
					String p01Code = p.getKey()[0];
//					specificList.add(specificParam2TypeData(validator.getSpecificParams().get(p01Code)));
					specificParamsList.add(validator.getSpecificParams().get(p01Code));
				}
			}else  if (p.getKey()[0].equals(SdnVocabularyManager.P01_ADEPZZ01)){
				// ADEPZZ01 mandatory for profiles and trajectories
				if (RulesManager.getInstance().isProfile()||RulesManager.getInstance().isTrajectory()) {
					specificParamsList.add(validator.getSpecificParams().get(p.getKey()[0]));
				}
			}
		}
		
		int position = 0;
		while(specificList.size()<specificParamsList.size()) {
			for (SpecificParameter sp : specificParamsList) {
				if (sp.getPosition()==position) {
					specificList.add(specificParam2TypeData(sp));
					break;
				}
			}
			position++;
		}
		
		
		return specificList;
	}



	/**
	 * TODO
	 * Check if specific parameters are still present when user validates
	 * @param allParameters
	 * @return
	 */
	public String checkSpecificParameters(List<TypeData> allParameters) {
		return "";
	}

	private TypeData specificParam2TypeData(SpecificParameter p) {
		TypeData td = new TypeData();

		String p02Code = VocabLists.getVocabMatch(
				Intern.list_P011, Intern.list_P021,
				p.getCodeP01(),
				Intern.NemoLists_unknown);
		td.setEntryKey(p02Code);
//		td.setEntryTerm(p.getCodeLocal());
		td.setEntryKeyP011(p.getCodeP01());
		td.setUniteCode(p.getCodeP06());
		td.setUnite(p.getUnitLocalLabel());

		//		td.setInstrumentCode();
		//		td.setInstrument();
		td.setIsP011(true);
		td.setIsText(p.getParameterType()==ParameterType.TEXT);
		td.setEntree("");
		td.setSortie("");
		td.setPosition(p.getPosition());
		td.setFormat("");
		try {
			String labelP01= BodcChecker.getInstance().getSdnMgr().getP01Label(p.getCodeP01());
			td.setEntryTermP011(labelP01);
			td.setP06Label(p.getUnitLocalLabel());
		} catch (SismerToolsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return td;
	}
}
