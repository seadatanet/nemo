package fr.ifremer.nemo.ui;

import fr.ifremer.nemo.util.Messages;

public class Regexp {
	/*
	 * Test des valeurs des champs de l'IHM par utilisation de regexp
	 * 
	 */



	public static Boolean checkPresenceValeurObligatoire(String value,
			String nomSection) {

		Boolean ok = true;

		if (value.equals("")) { //$NON-NLS-1$
			Utils
					.messageErreurAppend(Messages.getString("Regexp.4") + nomSection + Messages.getString("Regexp.5")); //$NON-NLS-1$ //$NON-NLS-2$
			ok = false;
		}

		return ok;
	}
}
