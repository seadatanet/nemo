package fr.ifremer.nemo.ui.edit.data;

import java.util.regex.Pattern;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.Fragment;
import fr.ifremer.nemo.input.parameters.Parameter;
import fr.ifremer.nemo.input.parameters.ParameterModel;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Global.E_TypeConversion;
import sdn.vocabulary.interfaces.IElement;

public class TypeData extends Object implements Comparable<TypeData> {

	// code P091 ou P021
	private String entryKey; // le vrai entryKey
	private String entryTerm; // l'ancien attribut nom

	private String unite;
	private String uniteCode;
	private String P06Label;
	private String conversion;
	private String format;
	private String entree;
	private String sortie;
	private String test;
	private String instrument;
	private String instrumentCode;

	/**
	 * This parameter stores the ISO date format if the parameter is a ISO1601
	 * date.
	 * 
	 * It is used to pass it to the Parameter object (FAE50999).
	 */
	private String effectiveIsoDateFormat;

	private int position, debut, fin;

	private String debutFlag, finFlag, testIn, testOut;

	private Boolean sensor;
	/**
	 * Definit si la valeur du parametre est du texte ou non.
	 */
	private Boolean isText;

	public static final String DEFAULT_CONVERT = "x*1"; //$NON-NLS-1$

	/**
	 * valeurs pour P011
	 */
	private String entryKeyP011;
	private String entryTermP011;
	private boolean isP011;
	private Integer indexP011;

	public TypeData(String theEntryKey, String theEntryTerm, String theCodeP01, String theNameP01, String theUniteCode,
			String theUniteLabel, String theP06Label, String theInstrumentCode, String theInstrumentLabel,
			boolean itheIsP011, boolean isText) {
		this.setEntryKey(theEntryKey);
		this.setEntryTerm(theEntryTerm);
		this.setEntryKeyP011(theCodeP01);
		this.setEntryTermP011(theNameP01);
		this.setUniteCode(theUniteCode);
		this.setUnite(theUniteLabel);
		this.setP06Label(theP06Label);
		this.setInstrumentCode(theInstrumentCode);
		this.setInstrument(theInstrumentLabel);
		this.setIsP011(itheIsP011);
		this.setIsText(isText);
	}

	public Integer getIndexP011() {
		return indexP011;
	}

	public void setIndexP011(Integer indexP011) {
		this.indexP011 = indexP011;
	}

	public TypeData(String theEntryKey, String theEntryTerm, String theCodeP01, String theNameP01, String theUniteCode,
			String theUniteLabel, String theInstrumentCode, String theInstrumentLabel, boolean itheIsP011) {
		this.setEntryKey(theEntryKey);
		this.setEntryTerm(theEntryTerm);
		this.setEntryKeyP011(theCodeP01);
		this.setEntryTermP011(theNameP01);
		this.setUniteCode(theUniteCode);
		this.setUnite(theUniteLabel);
		this.setInstrumentCode(theInstrumentCode);
		this.setInstrument(theInstrumentLabel);
		this.setIsP011(itheIsP011);
	}

	public TypeData(TypeData typeData) {
		this.setConversion(typeData.conversion);
		this.setEntree(typeData.entree);
		this.setFormat(typeData.format);
		// 11976
		if (isODV() || isNetCDF()) {
			this.setSortie(""); //$NON-NLS-1$
		} else {
			this.setSortie(typeData.sortie);
		}
		this.setInstrument(typeData.instrument);
		this.setInstrumentCode(typeData.instrumentCode);
		this.setUnite(typeData.unite);
		this.setP06Label(typeData.P06Label);
		this.setEntryKey(typeData.getEntryKey());
		this.setEntryTerm(typeData.getEntryTerm());
		this.setEntryTermP011(typeData.entryTermP011);
		this.setEntryKeyP011(typeData.entryKeyP011);
		this.setIsP011(typeData.isP011);
		this.setIndexP011(typeData.indexP011);
		this.setIsText(typeData.isText());
	}

	public TypeData(fr.ifremer.nemo.ws.model.Parameter wsParameter) {
		setEntryKey(wsParameter.getCode());
		setEntryTerm(wsParameter.getName());
		setUnite(wsParameter.getUnit());
		setP06Label(wsParameter.getUnitP06());
		setFormat(wsParameter.getFormat());
		setEntree(Global.EMPTY_STRING);
		setSortie(wsParameter.getDefaultValue());
		setConversion(DEFAULT_CONVERT);
		this.setIsText(false);
	}

	public TypeData(ParameterModel parameterModel) {
		Parameter parameter = parameterModel.getParameter();
		Fragment frag = parameterModel.getFragment();
		Fragment flag = parameterModel.getFlag();
		setIsText(parameterModel.isText());

		setEntryKey(parameter.getCode());
		setEntryTerm(parameter.getFullName());

		if (parameterModel.getConversionFormula().equals(Global.EMPTY_STRING)) {
			setConversion(TypeData.DEFAULT_CONVERT);
		} else {
			setConversion(parameterModel.getConversionFormula());
		}
		setDebut(frag.getFirstCharOffset());
		setFin(frag.getLastCharOffset());
		if (flag != null) {
			setDebutFlag(String.valueOf(flag.getFirstCharOffset()));
			setFinFlag(String.valueOf(flag.getLastCharOffset()));
		}

		String inputDefault = parameterModel.getInputDefault();
		if (inputDefault == null || inputDefault.equals("null")) //$NON-NLS-1$
			inputDefault = Global.EMPTY_STRING;
		setEntree(inputDefault);
		if (parameter.isP011()) {
			setP06Label(parameter.getUnit());
		}
		setUnite(parameter.getUnit());
		setUniteCode(parameter.getUnitCode());

		setInstrument(parameter.getInstrument());
		setInstrumentCode(parameter.getInstrumentCode());

		setFormat(parameter.getFormat());
		// 11976
		if (isODV() || isNetCDF()) {
			setSortie(""); //$NON-NLS-1$
		} else {
			setSortie(parameter.getDefault());
		}
		setEntryKeyP011(parameter.getCodeP011());
		setEntryTermP011(parameter.getNameP011());
		setIsP011(parameter.isP011());
		setIndexP011(parameter.getIndexP011());

		// TODO check if it's pertinent
//		setEffectiveIsoDateFormat(parameter.getEffectiveIsoDateFormat());

	}

	public TypeData() {

		// this.setCode("Code"); //$NON-NLS-1$
		this.setConversion(DEFAULT_CONVERT);
		this.setEntree("Entree"); //$NON-NLS-1$
		this.setFormat("Format"); //$NON-NLS-1$
		this.setSortie("Sortie"); //$NON-NLS-1$
		this.setUnite("Unite"); //$NON-NLS-1$
		this.setInstrument(Global.EMPTY_STRING);
		this.setInstrumentCode(Global.EMPTY_STRING);
		this.setTest(Global.EMPTY_STRING);
		// this.setFullname(Global.EMPTY_STRING);
		this.setEntryKey(Global.EMPTY_STRING);
		this.setEntryTerm(Global.EMPTY_STRING);
		this.setEntryTermP011(Global.EMPTY_STRING);
		this.setEntryKeyP011(Global.EMPTY_STRING);
		this.setIsP011(false);
		this.setIsText(false);
	}

	public TypeData(IElement record) {
		setEntryTerm(record.getLabel());
		setEntryKey(record.getKey());

		setFormat(Global.EMPTY_STRING);
		setEntree(Global.EMPTY_STRING);
		setSortie(Global.EMPTY_STRING);
		setUnite(Global.EMPTY_STRING);
		setInstrument(Global.EMPTY_STRING);
		setConversion(DEFAULT_CONVERT);
		setIsText(false);
	}

	private boolean isODV() {

		E_TypeConversion conv = Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat();
		return (conv == E_TypeConversion.ODV_MONOSTATION || conv == E_TypeConversion.ODV_MULTISTATIONS);

	}

	private boolean isNetCDF() {

		E_TypeConversion conv = Nemo.getInstance().getDomain().getConversion().getModel().getOutputFormat();
		return (conv == E_TypeConversion.NETCDF_MONOSTATION || conv == E_TypeConversion.NETCDF_MULTISTATIONS);

	}

	public String getFormattedCodeAndName() {
		return entryKey + Global.SEP + entryTerm;
	}

	public String getFormattedCodeAndNameToUse() {
		if (isP011()) {
			return entryKeyP011 + Global.SEP + entryTermP011;
		}
		// code = getFormattedCodeAndName();//P021 ou P091 selon le contexte
		// d'utilisation
		return entryKey + Global.SEP + entryTerm;

	}

	public void setEntryKeyAndEntryTerm(String formattedCodeAndName) {
		Pattern p = Pattern.compile(Global.SEP);
		String mots[] = p.split(formattedCodeAndName);
		setEntryKey(mots[0]);
		setEntryTerm(mots[1]);
	}

	@Override
	public int compareTo(TypeData other) {
		return entryKey.compareTo(other.getEntryKey());
	}

	// FAE 6537
	public String getCodeToUse() {
		String code;
		if (isP011()) {
			code = getEntryKeyP011();
		} else {
			// code = getFormattedCodeAndName();//P021 ou P091 selon le contexte
			// d'utilisation
			code = getEntryKey();
		}
		return code;
	}

	// public void setCode(String code) {
	// this.code = code;
	// }

	public String getConversion() {
		return conversion;
	}

	public void setConversion(String conversion) {
		this.conversion = conversion;
	}

	public int getDebut() {
		return debut;
	}

	public void setDebut(int debut) {
		this.debut = debut;
	}

	public String getEntree() {
		return entree;
	}

	public void setEntree(String entree) {
		this.entree = entree;
	}

	public int getFin() {
		return fin;
	}

	public void setFin(int fin) {
		this.fin = fin;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getSortie() {
		return sortie;
	}

	public void setSortie(String sortie) {
		this.sortie = sortie;
	}

	public String getUnite() {
		return unite;
	}

	public void setUnite(String unite) {
		this.unite = unite;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	// public String getFullname() {
	// return fullname;
	// }
	//
	// public void setFullname(String nom) {
	// this.fullname = nom;
	// }

	public String getDebutFlag() {
		if (debutFlag == null) {
			return Global.EMPTY_STRING;
		}
		return debutFlag;
	}

	public void setDebutFlag(String debutFlag) {
		this.debutFlag = debutFlag;
	}

	public String getFinFlag() {
		if (finFlag == null) {
			return Global.EMPTY_STRING;
		}
		return finFlag;
	}

	public void setFinFlag(String finFlag) {
		this.finFlag = finFlag;
	}

	public String getTestIn() {
		return testIn;
	}

	public void setTestIn(String testIn) {
		this.testIn = testIn;
	}

	public String getTestOut() {
		return testOut;
	}

	public void setTestOut(String testOut) {
		this.testOut = testOut;
	}

	// P011************************************************
	public String getEntryKeyP011() {
		return entryKeyP011;
	}

	public void setEntryKeyP011(String codeP011) {
		this.entryKeyP011 = codeP011;
	}

	public String getEntryTermP011() {
		return entryTermP011;
	}

	public void setEntryTermP011(String nomP011) {
		this.entryTermP011 = nomP011;
	}

	public boolean isP011() {
		return isP011;
	}

	public void setIsP011(boolean isP011) {
		this.isP011 = isP011;
	}

	public boolean isText() {
		return isText;
	}

	public void setIsText(boolean isText) {
		this.isText = isText;
	}

	// FAE 6542
	public String getUniteCode() {
		return uniteCode;
	}

	public void setUniteCode(String uniteCode) {
		this.uniteCode = uniteCode;
	}

	public String getEntryKey() {
		return entryKey;
	}

	public void setEntryKey(String entryKey) {
		this.entryKey = entryKey;
	}

	public String getEntryTerm() {
		return entryTerm;
	}

	public void setEntryTerm(String entryTerm) {
		this.entryTerm = entryTerm;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getInstrumentCode() {
		return instrumentCode;
	}

	public void setInstrumentCode(String instrumentCode) {
		this.instrumentCode = instrumentCode;
	}

	public Boolean getSensor() {
		return sensor;
	}

	public void setSensor(Boolean sensor) {
		this.sensor = sensor;
	}

	public String getP06Label() {
		return P06Label;
	}

	public void setP06Label(String p06Label) {
		P06Label = p06Label;
	}

	public String getEffectiveIsoDateFormat() {
		return effectiveIsoDateFormat;
	}

	public void setEffectiveIsoDateFormat(String effectiveIsoDateFormat) {
		this.effectiveIsoDateFormat = effectiveIsoDateFormat;
	}
}
