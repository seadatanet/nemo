package fr.ifremer.nemo.input.parameters.addedParameters;

import fr.ifremer.nemo.util.Conversion;

/**
 * Description of a parameter based on DEPTH.
 */
public class AddedParamDepth extends AP_DEPH {

	/**
	 * Generate an AddedParamDepth.
	 * @param aParam
	 * @param conv
	 */
	public AddedParamDepth(DynamicAddedParamBean aParam, Conversion conv) {
		super();
		fillDataFromBean(aParam, conv);
	}
}
