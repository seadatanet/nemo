package fr.ifremer.nemo.delete.ressources;

import java.util.Locale;

public class IzpackLocal {
	
	/**
	 * local of Izpack
	 */
	private static Locale locale;

	/**
	 * @return the locale
	 */
	public static Locale getLocale() {
		return locale;
	}

	/**
	 * @param locale the locale to set
	 */
	public static void setLocale(Locale alocale) {
		locale = alocale;
	}
	
	
	/**
	 * @param can only be "fra" or "eng"
	 */
	public static void setLocale(String alocale) {
		if("eng".equals(alocale))
		{
			locale = Locale.UK;
		}
		else if ("fra".equals(alocale))
		{
			locale = Locale.FRANCE;
		}
	}
	
	
	
}
