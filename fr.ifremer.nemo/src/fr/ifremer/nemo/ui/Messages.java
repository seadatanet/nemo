package fr.ifremer.nemo.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME_FR = "fr.ifremer.nemo.ui.messages_fr_FR"; //$NON-NLS-1$
	private static final String BUNDLE_NAME_GB = "fr.ifremer.nemo.ui.messages_en_GB";
	
	private static ResourceBundle RESOURCE_BUNDLE;// = ResourceBundle.getBundle(BUNDLE_NAME_FR);
//	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
//	.getBundle(BUNDLE_NAME);
	
	private Messages() {
	}

	public static String getString(String key) {
		try {
			//check config de la langue
			String langue = Utils.configuration.getLang();
			if (langue.matches("fr_FR")){
			ResourceBundle.getBundle("fr.ifremer.nemo.ui.messages_fr_FR");
			RESOURCE_BUNDLE = ResourceBundle.getBundle("fr.ifremer.nemo.ui.messages_fr_FR");
			}else{
			ResourceBundle.getBundle("fr.ifremer.nemo.ui.messages_en_GB");
			RESOURCE_BUNDLE = ResourceBundle.getBundle("fr.ifremer.nemo.ui.messages_en_GB");
			}
			
			
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
