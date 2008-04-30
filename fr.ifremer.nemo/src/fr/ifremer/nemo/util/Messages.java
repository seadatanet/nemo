package fr.ifremer.nemo.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import fr.ifremer.nemo.config.Configuration;
import fr.ifremer.nemo.ui.Utils;

public class Messages {
	private static final String BUNDLE_NAME_FR = "fr.ifremer.nemo.util.messages_fr_FR"; //$NON-NLS-1$

	private static final String BUNDLE_NAME_GB = "fr.ifremer.nemo.util.messages_en_GB";

	private static ResourceBundle RESOURCE_BUNDLE;

	private Messages() {
	}

	public static String getString(String key) {
		try {
			// check config de la langue

			// String langue = Utils.configuration.getLang();
			String langue = Utils.configuration.getLang();
			if (langue.matches("fr_FR")) {
				ResourceBundle.getBundle(BUNDLE_NAME_FR);
				RESOURCE_BUNDLE = ResourceBundle
						.getBundle(BUNDLE_NAME_FR);
			} else {
				ResourceBundle.getBundle(BUNDLE_NAME_GB);
				RESOURCE_BUNDLE = ResourceBundle
						.getBundle(BUNDLE_NAME_GB);
			}

			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
