package fr.ifremer.nemo.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Table;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ifremer.nemo.ui.TypeData;

public class EdmoHandler extends DefaultHandler{
	
	private java.util.HashMap<Integer, String> _valeurs = new HashMap<Integer, String>();
	
	private Integer intCode;

	private Boolean blnCode = false;
	
	private Boolean blnName = false;
	
	public static Table filtreTable;
	
	public static ArrayList<String> listeOrigine;
	
	public static ArrayList<String> listeFiltree;
	
	public java.util.HashMap<Integer, String> get_valeurs() {
		return _valeurs;
	}

	public void startElement(String uri,
            String localName,
            String qName,
            Attributes attributes)
     throws SAXException
     {
		
		blnCode = false;
		blnName = false;
		
		if (qName.equals("n_code"))
		{
			blnCode = true;
		}
		
		if (qName.equals("name"))
		{
			blnName = true;
		}		
     }
	
	public void endElement(String uri,
            String localName,
            String qName)
     throws SAXException
     {
		
     }
	
	public void characters(char[] ch,
            int start,
            int length)
     throws SAXException
     {
		if (blnCode)
		{
			intCode = Integer.parseInt(String.copyValueOf(ch).substring(start, start+length));
			blnCode = false;
		}
		
		if (blnName)
		{
			_valeurs.put(intCode, String.copyValueOf(ch).substring(start, start+length));
			blnName = false;
		}
     }
}
