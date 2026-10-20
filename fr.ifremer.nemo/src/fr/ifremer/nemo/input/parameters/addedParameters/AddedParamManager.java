package fr.ifremer.nemo.input.parameters.addedParameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.input.parameters.addedParameters.AddedParam.IdParameter;
import fr.ifremer.nemo.util.Conversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;
import sdn.vocabulary.interfaces.IElement;

/**
 * Handles the dynamic added params, loaded from config file.
 * @author Sebastien Crouzille
 *
 */
public class AddedParamManager {

	private final static String configPath = "resources/addedParams.yaml";

	/**
	 * Load YAML file and parse AddedParams.
	 * @return The map of beans, stored in Nemo during runtime.
	 * @throws FileNotFoundException
	 */
	public static LinkedHashMap<String, DynamicAddedParamBean> loadConfiguration() throws FileNotFoundException {

		LinkedHashMap<String, DynamicAddedParamBean> addedParams = new LinkedHashMap<String, DynamicAddedParamBean>();
		Path confFilePath = Paths.get(configPath).toAbsolutePath();
		FileInputStream confFileStream = new FileInputStream(Paths.get(configPath).toAbsolutePath().toFile());

//		InputStream inputStream = AddedParamManager.class.getClassLoader().getResourceAsStream(configPath);

		Yaml yaml = new Yaml();
		LinkedHashMap<String, Object> addedParamsObj = (LinkedHashMap<String, Object>) yaml.load(confFileStream);

		// Loop to create each AddedParam object.
		Set<String> keys = addedParamsObj.keySet();

		for (String key : keys) {

			DynamicAddedParamBean addedParam = getDynAddedParamFromYamlObject(key, addedParamsObj.get(key));

			addedParams.put(key, addedParam);
		}

		return addedParams;
	}

	/**
	 * Parse one AddedParam beanfrom raw data.
	 * @param paramKey the main key of the object ; P01 code.
	 * @param obj the raw object.
	 * @return the bean from the raw object
	 */
	private static DynamicAddedParamBean getDynAddedParamFromYamlObject(String paramKey, Object obj) {

		DynamicAddedParamBean addedParam = new DynamicAddedParamBean();

		LinkedHashMap<String, Object> theParam = (LinkedHashMap<String, Object>) obj;

		addedParam.setParent((String) theParam.get("parent"));
		addedParam.setType(IdParameter.valueOf((String) theParam.get("type")));
		addedParam.setCode(paramKey);

		LinkedHashMap<String, DynamicAddedParamByConversionBean> addedParamsByConversion = new LinkedHashMap<String, DynamicAddedParamByConversionBean>();

		// Check if deprecated
		try {
			SdnVocabularyManager sdnMgr = ExternalResourcesManager.getInstance().getSdnVocabularyManager();
			String replacement = sdnMgr.getReplacedByCode(paramKey, SdnVocabularyManager.LIST_P01);
			if (replacement != null) {
				String replacementLabel = sdnMgr.getLabel(replacement, SdnVocabularyManager.LIST_P01);

				// replace data
				addedParam.setCode(replacement);

				DynamicAddedParamByConversionBean byConversion = new DynamicAddedParamByConversionBean();

				byConversion.setFullName(replacementLabel);

				// retrieve PO6 data
				IElement unitElt = sdnMgr.getP06OfP01(replacement);

				if (unitElt != null) {
					byConversion.setUnit(unitElt.getShortLabel());
					byConversion.setUnitCode(unitElt.getKey());
				}

				byConversion.setCible(Conversion.ODV);
				byConversion.setP011(true);
				byConversion.setCodeP011(replacement);
				byConversion.setNameP011(replacementLabel);
				byConversion.setDefaultValue(Global.EMPTY_STRING);

				addedParamsByConversion.put("default", byConversion);
				addedParam.setDynamicAddedParamByConversionBeans(addedParamsByConversion);

				Nemo.getInstance().getLogger().warn(String.format(Messages.warning_addedParameters_update_deprecated, paramKey, replacement, configPath));
				
				return addedParam;
			}
		} catch (Exception e) {
			// fallback to the deprecated param
		}

		LinkedHashMap<String, Object> paramsByConversion = (LinkedHashMap<String, Object>) theParam.get("byConversion");
		Set<String> keys = paramsByConversion.keySet();

		for (String key : keys) {
			LinkedHashMap<String, Object> params = (LinkedHashMap<String, Object>) paramsByConversion.get(key);
			DynamicAddedParamByConversionBean byConversion = new DynamicAddedParamByConversionBean();

			byConversion.setFullName((String) params.get("fullName"));
			byConversion.setUnit((String) params.get("unit"));
			byConversion.setCible(Conversion.valueOf((String) params.get("cible")));
			byConversion.setP011((Boolean) params.get("p011"));
			byConversion.setCodeP011((String) params.get("codeP011"));
			byConversion.setNameP011((String) params.get("nameP011"));
			byConversion.setUnitCode((String) params.get("unitCode"));
			byConversion.setDefaultValue((String) params.get("defaultValue"));

			addedParamsByConversion.put(key, byConversion);
		}

		addedParam.setDynamicAddedParamByConversionBeans(addedParamsByConversion);

		return addedParam;
	}
}
