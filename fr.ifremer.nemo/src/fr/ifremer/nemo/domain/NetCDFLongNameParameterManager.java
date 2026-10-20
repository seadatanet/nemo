/*
 * ============================================================================
 * Nom du fichier : LongNameParameterManager.java
 * Auteur : sbregent
 * Date : 27 avr. 2011
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ifremer.nemo.Nemo;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Intern;

/**
 * Description: TODO
 * 
 * @author sbregent
 * 
 */
public class NetCDFLongNameParameterManager {
	private static NetCDFLongNameParameterManager instance;
	private HashMap<String, String> longNamesMap;

	String fileName =
			Intern.dir_resources + File.separator + "userParametersNetCDFLongNames"
					+ Global.EXT_XML;
	File file ;
	org.w3c.dom.Document document;

	/**
	 * 
	 */
	public NetCDFLongNameParameterManager() {
		super();
		file = new File(fileName);
		longNamesMap = new HashMap<String, String>();
		loadUserlongNames();
	}

	public static NetCDFLongNameParameterManager getInstance() {
		if (null == instance) { // Premier appel
			instance = new NetCDFLongNameParameterManager();
		}
		return instance;
	}

	public String getUserLongName(String P011Code) {
		return longNamesMap.get(P011Code);
	}

	public void updateLongName(String labelP011, String labelUser) {
		String item = longNamesMap.get(labelP011);
		Element racine = document.getDocumentElement();
		if (item == null) {
			// Element child = document.createElement("label");
			Element child = document.createElement(labelP011);
			// child.setAttribute("P011", labelP011);
			// child.setNodeValue(labelUser);

			child.setTextContent(labelUser);
			longNamesMap.put(labelP011, labelUser);
			racine.appendChild(child);
			updateXML();
		} else {
			NodeList listChildren = racine.getElementsByTagName(labelP011);
			Node node = listChildren.item(0);
			node.setTextContent(labelUser);
			// node.setNodeValue(labelUser);
			longNamesMap.put(labelP011, labelUser);
			updateXML();
		}

	}

	private void updateXML() {
		try {
			// Création de la source DOM
			Source source = new DOMSource(document);

			// Création du fichier de sortie
			Result resultat = new StreamResult(fileName);
			// Configuration du transformer
			TransformerFactory fabrique = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = fabrique.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, Global.NEMO_ENCODING);

			// Transformation
			transformer.transform(source, resultat);

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadUserlongNames() {
		longNamesMap = new HashMap<String, String>();

		if (file.exists()) {

			try {
				// création d'une fabrique de documents
				DocumentBuilderFactory fabrique =
						DocumentBuilderFactory.newInstance();

				// création d'un constructeur de documents
				DocumentBuilder constructeur = fabrique.newDocumentBuilder();

				// lecture du contenu d'un fichier XML avec DOM
				document = constructeur.parse(file);
				Element racine = document.getDocumentElement();
				// System.out.println(racine.getNodeName());

				// String tag = "label";
				// NodeList longNamesList = racine.getElementsByTagName(tag);
				NodeList longNamesList = racine.getChildNodes();
				for (int i = 0; i < longNamesList.getLength(); i++) {

					Node oneFormat = longNamesList.item(i);
					// Element oneFormatEl = (Element) oneFormat;
					// String P011 = oneFormatEl.getAttribute("P011");
					String P011 = oneFormat.getNodeName();
					// String userLongName = oneFormatEl.getAttribute("user");
					String userLongName = oneFormat.getTextContent();
					// System.out.println(" -- " + format + " -- " + example);

					longNamesMap.put(P011, userLongName);
				}
			} catch (ParserConfigurationException pce) {
//				System.out.println("Erreur de configuration du parseur DOM");
//				System.out.println("lors de l'appel à fabrique.newDocumentBuilder();");
				Nemo.getInstance().getLogger().error("Erreur de configuration du parseur DOM");
				Nemo.getInstance().getLogger().error("lors de l'appel à fabrique.newDocumentBuilder();");
				
			} catch (SAXException se) {
//				System.out.println("Erreur lors du parsing du document");
//				System.out.println("lors de l'appel à construteur.parse(xml)");
				Nemo.getInstance().getLogger().error("Erreur lors du parsing du document");
				Nemo.getInstance().getLogger().error("lors de l'appel à construteur.parse(xml)");
			} catch (IOException ioe) {
//				System.out.println("Erreur d'entrée/sortie");
//				System.out.println("lors de l'appel à construteur.parse(xml)");
				Nemo.getInstance().getLogger().error("Erreur d'entrée/sortie");
				Nemo.getInstance().getLogger().error("lors de l'appel à construteur.parse(xml)");
			}

		}

	}

	/**
	 * @return the longNamesMap
	 */
	public HashMap<String, String> getLongNamesMap() {
		return longNamesMap;
	}

	/**
	 * @param longNamesMap the longNamesMap to set
	 */
	public void setLongNamesMap(HashMap<String, String> longNamesMap) {
		this.longNamesMap = longNamesMap;
	}
}
