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
    
    public static String lang_fr;
    public static String lang_en;

    public static String settings_proxy;
    public static String proxy_use;
    public static String proxy_use_set;
    public static String proxy_adr;
    public static String proxy_adr_set;
    public static String proxy_port;
    public static String proxy_port_set;
    public static String proxy_login;
    public static String proxy_login_set;
    public static String proxy_pass;
    public static String proxy_pass_set;

    public static String ini_name;
    public static String ini_start;
    public static String ini_proxy_adr;
    public static String ini_proxy_port;
    public static String ini_proxy_login;
    public static String ini_proxy_pass;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Intern.class);
	}

	private Intern() {
	}
}
