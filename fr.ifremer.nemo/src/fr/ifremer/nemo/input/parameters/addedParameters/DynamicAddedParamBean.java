package fr.ifremer.nemo.input.parameters.addedParameters;

import java.util.LinkedHashMap;

import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam.IdParameter;

/**
 * Bean representing an entire AddedParam, with every set for each conversion type.
 * @author Sebastien Crouzille
 *
 */
public class DynamicAddedParamBean {

	private String code;
	private String parent;
	private IdParameter type;
	private LinkedHashMap<String, DynamicAddedParamByConversionBean> dynamicAddedParamByConversionBeans;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public IdParameter getType() {
		return type;
	}

	public void setType(IdParameter type) {
		this.type = type;
	}

	public LinkedHashMap<String, DynamicAddedParamByConversionBean> getDynamicAddedParamByConversionBeans() {
		return dynamicAddedParamByConversionBeans;
	}

	public void setDynamicAddedParamByConversionBeans(
			LinkedHashMap<String, DynamicAddedParamByConversionBean> dynamicAddedParamByConversionBeans) {
		this.dynamicAddedParamByConversionBeans = dynamicAddedParamByConversionBeans;
	}

}
