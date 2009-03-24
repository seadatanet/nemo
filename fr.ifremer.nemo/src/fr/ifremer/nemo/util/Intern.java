package fr.ifremer.nemo.util;

import org.eclipse.osgi.util.NLS;

/**
 * Intern application properties
 * @author IFREMER
 */
public class Intern extends NLS {
	private static final String BUNDLE_NAME = "fr.ifremer.nemo.util.intern"; //$NON-NLS-1$

	public static String id_nemo;
	public static String id_lists;
	public static String id_maps;
	public static String id_edmo;
    public static String nbnemolists;
    public static String vocabLists;
    public static String vocabMaps;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Intern.class);
	}

	private Intern() {
	}
}
