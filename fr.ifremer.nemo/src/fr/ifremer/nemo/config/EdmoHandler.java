package fr.ifremer.nemo.config;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Table;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class EdmoHandler
 * 
 * @author Herve Taniou
 */
public class EdmoHandler extends DefaultHandler{
	
	private java.util.HashMap<Integer, String> _valeurs = new HashMap<Integer, String>();
	
	private Integer intCode;
	
    //  buffer nous permettant de récupérer les données 
    private StringBuffer buffer;
	
	public static Table filtreTable;
	
	public static ArrayList<String> listeOrigine;
	
	public static ArrayList<String> listeFiltree;
	
	public HashMap<Integer, String> get_valeurs() {
		return _valeurs;
	}

	@Override
    public void startElement(String uri,
            String localName,
            String qName,
            Attributes attributes)
     throws SAXException {		
        
		if (qName.equals("n_code")){
            buffer = new StringBuffer();
		}
        else if (qName.equals("name")){
            buffer = new StringBuffer();
		}		
     }
	
	@Override
    public void endElement(String uri,
            String localName,
            String qName)
     throws SAXException {
        
        if (qName.equals("n_code")){
            intCode = Integer.parseInt(buffer.toString());
            buffer = null;
        }
        else if (qName.equals("name")){
            _valeurs.put(intCode, buffer.toString());
            buffer = null;
        }   
     }
	
	@Override
    public void characters(char[] ch,
            int start,
            int length)
     throws SAXException {
        
        if (buffer != null){
            String lecture = new String(ch, start, length);
            buffer.append(lecture);
        }
     }
}
