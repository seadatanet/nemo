/*
 * ============================================================================
 * Nom du fichier : LabelParameterManager.java
 * Auteur : sbregent
 * Date : 27 avr. 2011
 * Copyright : Altran
 * ============================================================================
 */
package fr.ifremer.nemo.domain;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

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
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorBio;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorClassic;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorCyto;
import fr.ifremer.seadatanet.odv.input.validator.OdvSpecificValidatorMicro;
import fr.ifremer.seadatanet.odv.input.validator.SpecificParameter;

/**
 * Description: TODO
 * 
 * @author sbregent
 * 
 */
public class LabelParameterManager
{
	private static LabelParameterManager instance;
	private HashMap<String, String> labelsMap;
	private final HashMap<String, String> specificLabelsMap;
	
	String fileName =
			Intern.dir_resources + File.separator + "userParametersLabels"
					+ Global.EXT_XML;
	File file = new File(fileName);
	org.w3c.dom.Document document;

	/**
	 * 
	 */
	public LabelParameterManager()
	{
		super();
		labelsMap = new HashMap<String, String>();
		specificLabelsMap = new HashMap<String, String>();
		loadUserLabels();

	}

	public static LabelParameterManager getInstance()
	{
		if (null == instance)
		{ // Premier appel
			instance = new LabelParameterManager();
		}
		return instance;
	}
	public void cleanUSersLabels() {
		labelsMap = new HashMap<String, String>();
	}
	public String getUserLabel(String P011Code)
	{
		return labelsMap.get(P011Code);
	}
	public String getSpecificParameterLabel(String P011Code) {

		// TODO 42483: on stocke ici les labels des variants. Priorité user ou Nemo? actt user, sinon inverser les lignes
		loadSpecificLabels();
		return specificLabelsMap.get(P011Code);
	}
	public void updateLabel(String labelP011, String labelUser)
	{
		String item = labelsMap.get(labelP011);
		Element racine = document.getDocumentElement();
		if (item == null)
		{
			Element child = document.createElement(labelP011);
			child.setTextContent(labelUser);
			labelsMap.put(labelP011, labelUser);
			racine.appendChild(child);
			updateXML();
		} else
		{
			NodeList listChildren = racine.getElementsByTagName(labelP011);
			Node child = listChildren.item(0);
			if (child==null) {
				child = document.createElement(labelP011);
				racine.appendChild(child);
			}
			child.setTextContent(labelUser);
			labelsMap.put(labelP011, labelUser);
			updateXML();
		}

	}

	private void updateXML()
	{
		try
		{
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

		} catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadSpecificLabels()
	{
				HashMap<String, SpecificParameter> list = new OdvSpecificValidatorClassic(null, null, null).getSpecificParams();
				addSpecificLabelsList(list);
				list = new OdvSpecificValidatorBio(null, null, null).getSpecificParams();
				addSpecificLabelsList(list);
				list = new OdvSpecificValidatorCyto(null, null, null).getSpecificParams();
				addSpecificLabelsList(list);
				list = new OdvSpecificValidatorMicro(null, null, null).getSpecificParams();
				addSpecificLabelsList(list);

	}
		private void addSpecificLabelsList(HashMap<String, SpecificParameter> list ) {
			for (Entry<String, SpecificParameter> entry : list.entrySet()) {
				specificLabelsMap.put(entry.getKey(), entry.getValue().getCodeLocal());
			}
		}
	private void loadUserLabels()
	{
		labelsMap = new HashMap<String, String>();

		if (file.exists())
		{

			try
			{
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
				// NodeList labelsList = racine.getElementsByTagName(tag);
				NodeList labelsList = racine.getChildNodes();
				for (int i = 0; i < labelsList.getLength(); i++)
				{

					Node oneFormat = labelsList.item(i);
					// Element oneFormatEl = (Element) oneFormat;
					// String P011 = oneFormatEl.getAttribute("P011");
					String P011 = oneFormat.getNodeName();
					// String userLabel = oneFormatEl.getAttribute("user");
					String userLabel = oneFormat.getTextContent();
					// System.out.println(" -- " + format + " -- " + example);

					labelsMap.put(P011, userLabel);
				}
			} catch (ParserConfigurationException pce)
			{
				Nemo.getInstance().getLogger().error("Erreur de configuration du parseur DOM");
				Nemo.getInstance().getLogger().error("lors de l'appel à fabrique.newDocumentBuilder();");
			} catch (SAXException se)
			{
				Nemo.getInstance().getLogger().error("Erreur lors du parsing du document");
				Nemo.getInstance().getLogger().error("lors de l'appel à construteur.parse(xml)");
			} catch (IOException ioe)
			{
				Nemo.getInstance().getLogger().error("Erreur d'entrée/sortie");
				Nemo.getInstance().getLogger().error("lors de l'appel à construteur.parse(xml)");
			}

		}

	}
}
