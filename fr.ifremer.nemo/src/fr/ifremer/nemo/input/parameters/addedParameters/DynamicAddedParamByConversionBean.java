package fr.ifremer.nemo.input.parameters.addedParameters;

import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;

/**
 * Bean storing AddedParam data for a particular conversion type.
 * @author Sebastien Crouzille
 *
 */
public class DynamicAddedParamByConversionBean {

	private String fullName;
	private String unit;
	private Conversion cible;
	private Boolean p011;
	private String codeP011;
	private String unitCode;
	private String nameP011;
	private String defaultValue;
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Conversion getCible() {
		return cible;
	}
	public void setCible(Conversion cible) {
		this.cible = cible;
	}
	public Boolean getP011() {
		return p011;
	}
	public void setP011(Boolean p011) {
		this.p011 = p011;
	}
	public String getCodeP011() {
		return codeP011;
	}
	public void setCodeP011(String codeP011) {
		this.codeP011 = codeP011;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
	public String getNameP011() {
		return nameP011;
	}
	public void setNameP011(String nameP011) {
		this.nameP011 = nameP011;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		if (defaultValue != null && defaultValue.equals("EMPTY_STRING")) {
			this.defaultValue = Global.EMPTY_STRING;
		}
		else {
			
			this.defaultValue = defaultValue;
		}
	}
	
}
