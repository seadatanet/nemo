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
					.messageErreurAppend(Messages.Regexp_4 + nomSection + Messages.Regexp_5);
			ok = false;
		}

		return ok;
	}
}
