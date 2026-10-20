package fr.ifremer.nemo.input.parameters.addedParameters;

import fr.ifremer.nemo.util.Conversion;

/**
 * Description of a parameter based on HEIGHT.
 */
public class AddedParamHeight extends AP_HEIGHT {

	/**
	 * Generate an AddedParamHeight.
	 * @param aParam
	 * @param conv
	 */
	public AddedParamHeight(DynamicAddedParamBean aParam, Conversion conv) {
		super();
		fillDataFromBean(aParam, conv);
	}
}
