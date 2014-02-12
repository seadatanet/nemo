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
 * @author Herve Taniou
 */
public class EdmoHandler extends DefaultHandler {

	private static String n_code = "n_code"; //$NON-NLS-1$
	private static String name = "name"; //$NON-NLS-1$

	private final java.util.HashMap<Integer, String> _valeurs = new HashMap<Integer, String>();
	private final List<EdmoEntity> edmoList = new ArrayList<EdmoEntity>();
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

	public List<EdmoEntity> getEdmoList() {
		// if (edmoList.size() == 0) {
		// edmoList = new ArrayList<EdmoEntity>();
		// }
		// int i = 0;
		// for (int code : _valeurs.keySet()) {
		// edmoList.add(new EdmoEntity(String.valueOf(code), _valeurs
		// .get(code)));
		// }
		return edmoList;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {

		if (qName.equals(n_code)) {
			buffer = new StringBuffer();
		} else if (qName.equals(name)) {
			buffer = new StringBuffer();
		} else if (qName.equals("c_country")) {
			buffer = new StringBuffer();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) {

		if (qName.equals(n_code)) {
			intCode = Integer.parseInt(buffer.toString());
			buffer = null;
		} else if (qName.equals(name)) {
			_valeurs.put(intCode, buffer.toString());
			entityName = buffer.toString();
			buffer = null;
		} else if (qName.equals("c_country")) {
			// _valeurs.put(intCode, buffer.toString());
			edmoList.add(new EdmoEntity(intCode, entityName, buffer.toString()));
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
