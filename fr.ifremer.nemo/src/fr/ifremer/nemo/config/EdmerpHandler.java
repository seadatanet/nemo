package fr.ifremer.nemo.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class EdmoHandler
 * 
 */
public class EdmerpHandler extends DefaultHandler {

//	private static String n_code = "n_code"; //$NON-NLS-1$
//	private static String name = "name"; //$NON-NLS-1$
	private static String id = "Id"; //$NON-NLS-1$
	private static String title = "Title"; //$NON-NLS-1$
//	private static String acronym = "Acronym"; //$NON-NLS-1$
//	private static String coordinator = "Coordinator"; //$NON-NLS-1$
//	private static String country = "Country"; //$NON-NLS-1$

	private final java.util.HashMap<Integer, String> _valeurs = new HashMap<Integer, String>();
	private final List<EdmerpEntity> edmerpList = new ArrayList<EdmerpEntity>();
	private Integer intCode;
	private String entityName;
	// buffer nous permettant de récupérer les données
	private StringBuffer buffer;

	public static Table filtreTable;

	public static ArrayList<String> listeOrigine;

	public static ArrayList<String> listeFiltree;

	public HashMap<Integer, String> get_valeurs() {
		return _valeurs;
	}

	public List<EdmerpEntity> getEdmerpList() {
		// if (edmoList.size() == 0) {
		// edmoList = new ArrayList<EdmoEntity>();
		// }
		// int i = 0;
		// for (int code : _valeurs.keySet()) {
		// edmoList.add(new EdmoEntity(String.valueOf(code), _valeurs
		// .get(code)));
		// }
		return edmerpList;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		if (qName.equals(id)) {
			buffer = new StringBuffer();
		} else if (qName.equals(title)) {
			buffer = new StringBuffer();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) {

		if (qName.equals(id)) {
			intCode = Integer.parseInt(buffer.toString());
			buffer = null;
		} else if (qName.equals(title)) {
			// _valeurs.put(intCode, buffer.toString());
//			entityName = buffer.toString();
			edmerpList.add(new EdmerpEntity(intCode, buffer.toString()));
			buffer = null;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) {

		if (buffer != null) {
			String lecture = new String(ch, start, length);
			buffer.append(lecture);
		}
	}

}
