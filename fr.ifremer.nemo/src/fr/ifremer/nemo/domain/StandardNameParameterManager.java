/*
 * ============================================================================
 * Nom du fichier : StandardNameParameterManager.java
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
public class StandardNameParameterManager {
	private static StandardNameParameterManager instance;
	private HashMap<String, String> standardNamesMap;
	String fileName =
			Intern.dir_resources + File.separator + "userParametersStandardNames"
					+ Global.EXT_XML;
	File file = new File(fileName);
	org.w3c.dom.Document document;

	/**
	 * 
	 */
	public StandardNameParameterManager() {
		super();
		standardNamesMap = new HashMap<String, String>();
		loadUserStandardNames();
		// TODO Auto-generated constructor stub
	}

	public static StandardNameParameterManager getInstance() {
		if (null == instance) { // Premier appel
			instance = new StandardNameParameterManager();
		}
		return instance;
	}

	public String getUserStandardName(String P011Code) {
		return standardNamesMap.get(P011Code);
	}

	public void updateStandardName(String StandardNameP011, String StandardNameUser) {
		String item = standardNamesMap.get(StandardNameP011);
		Element racine = document.getDocumentElement();
		if (item == null) {
			// Element child = document.createElement("StandardName");
			Element child = document.createElement(StandardNameP011);
			// child.setAttribute("P011", StandardNameP011);
			// child.setNodeValue(StandardNameUser);

			child.setTextContent(StandardNameUser);
			standardNamesMap.put(StandardNameP011, StandardNameUser);
			racine.appendChild(child);
			updateXML();
		} else {
			NodeList listChildren = racine.getElementsByTagName(StandardNameP011);
			Node node = listChildren.item(0);
			node.setTextContent(StandardNameUser);
			// node.setNodeValue(StandardNameUser);
			standardNamesMap.put(StandardNameP011, StandardNameUser);
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

	private void loadUserStandardNames() {
		standardNamesMap = new HashMap<String, String>();

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

				// String tag = "StandardName";
				// NodeList StandardNamesList = racine.getElementsByTagName(tag);
				NodeList StandardNamesList = racine.getChildNodes();
				for (int i = 0; i < StandardNamesList.getLength(); i++) {

					Node oneFormat = StandardNamesList.item(i);
					// Element oneFormatEl = (Element) oneFormat;
					// String P011 = oneFormatEl.getAttribute("P011");
					String P011 = oneFormat.getNodeName();
					// String userStandardName = oneFormatEl.getAttribute("user");
					String userStandardName = oneFormat.getTextContent();
					// System.out.println(" -- " + format + " -- " + example);

					standardNamesMap.put(P011, userStandardName);
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
	 * @return the standardNamesMap
	 */
	public HashMap<String, String> getStandardNamesMap() {
		return standardNamesMap;
	}

	/**
	 * @param standardNamesMap the standardNamesMap to set
	 */
	public void setStandardNamesMap(HashMap<String, String> standardNamesMap) {
		this.standardNamesMap = standardNamesMap;
	}
}
