package fr.ifremer.nemo.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.ui.Utils;
import fr.ifremer.nemo.ui.edit.conversion.EditConversion;
import fr.ifremer.nemo.ui.view.ViewFile;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.SizeBasedProgressNotifier;

/**
 * Class SingleFileSurvey
 * 
 * Represente une campagne dont les informations sont stockees dans un fichier
 * unique.
 * 
 * @author Gerard BUNEL
 */
public class SingleFileSurvey extends SurveyMedia implements Cloneable,
		SAXConsumer {

	/**
	 * Nom du fichier campagne
	 */
	protected String _file;

	/**
	 * Nombre de lignes du fichier occupees par l'entete campagne
	 */
	protected int _headerLinesNumber;

	/**
	 * Campagne servant de modele pour stocker les informations de l'entete
	 * campagne
	 */
	protected Survey _model;

	/**
	 * Modele de station
	 */
	protected StationModel _stationModel;

	public SingleFileSurvey() {
		super();
		_file = Global.EMPTY_STRING;
		_model = null;
		_stationModel = null;
	}

	/**
	 * 
	 * @param isTimeSeries
	 */
	public SingleFileSurvey(String typeFichier) {
		this();
		setModel(new Survey());
		setStationModel(new StationModel(typeFichier));
	}

	/**
	 * 
	 * Build object from its XML defintion
	 * 
	 * @param a_URI
	 *            Element Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public static SAXConsumer fromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		SingleFileSurvey tmp = new SingleFileSurvey();
		tmp.buildFromElement(a_URI, a_LocalName, a_QName, a_Attributes);
		return (SAXConsumer) tmp;
	}

	/**
	 * 
	 * Retrieve attributes values from their XML defintion
	 * 
	 * @param a_URI
	 *            Element Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * 
	 */
	public void buildFromElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		String tmp;
		if ((tmp = a_Attributes.getValue("file")) != null) {
			setFile(tmp);
		}
		if ((tmp = a_Attributes.getValue("lineNumber")) != null) {
			setHeaderLinesNumber(tmp);
		}
	}

	/**
	 * 
	 * A new XML Element as been found. Parse it.
	 * 
	 * @param a_URI
	 *            XML Namespace (SAX 2)
	 * @param a_LocalName
	 *            Tag name
	 * @param a_QName
	 *            Qualified tag name
	 * @param a_Attributes
	 *            XML Element attributes
	 * @return SAXConsumer
	 * 
	 */
	public SAXConsumer consumeElement(String a_URI, String a_LocalName,
			String a_QName, Attributes a_Attributes) throws SAXException {
		if (a_LocalName.equals("Survey")) {
			Survey l = (Survey) Survey.fromElement(a_URI, a_LocalName, a_QName,
					a_Attributes);
			setModel(l);
			return (SAXConsumer) l;
		}
		if (a_LocalName.equals("StationModel")) {
			StationModel l = (StationModel) StationModel.fromElement(a_URI,
					a_LocalName, a_QName, a_Attributes);
			setStationModel(l);
			return (SAXConsumer) l;
		}
		return null;
	}

	/**
	 * 
	 * The XML Element is a CDATA filled element
	 * 
	 * @param a_Characters
	 *            The CDATA content
	 * 
	 */
	public void consumeCharacters(String a_Characters) throws SAXException {
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLBodyOn(XMLOutputWriter a_Writer) {
		a_Writer.putAttribute("file", getFile());
		a_Writer.putAttribute("lineNumber", getHeaderLinesNumber());
		if (getModel() != null) {
			getModel().writeXMLOn(a_Writer);
		}
		if (getStationModel() != null) {
			getStationModel().writeXMLOn(a_Writer);
		}
	}

	/**
	 * 
	 * Generate XML defintion on a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            the XMLOutputWriter to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer) {
		a_Writer.openTag("SingleFileSurvey");
		writeXMLBodyOn(a_Writer);
		a_Writer.closeTag();
	}

	/**
	 * 
	 * Generate XML defintion on a Stream
	 * 
	 * @param a_Stream
	 *            the OutptStream to which to write the XML Definition
	 * 
	 */
	public void writeXMLOn(OutputStream a_Stream) {
		XMLOutputWriter writer = new XMLOutputWriter(a_Stream);
		writeXMLOn(writer);
	}

	/**
	 * Returns a copy of this Object
	 * 
	 * @return a copy of this object
	 */
	public Object clone() throws CloneNotSupportedException {
		SingleFileSurvey copy = null;
		copy = (SingleFileSurvey) super.clone();
		copy._model = null;
		if (getModel() != null) {
			copy.setModel((Survey) getModel().clone());
		}
		copy._stationModel = null;
		if (getStationModel() != null) {
			copy.setStationModel((StationModel) getStationModel().clone());
		}
		return copy;
	}

	/**
	 * Remove any associated links
	 */
	@Override
    public void release() {
        super.release();
        _file = Global.EMPTY_STRING;
		releaseModel();
		releaseStationModel();
	}

	/**
	 * Read accessor (getter) for attribute _file
	 * 
	 * @return String
	 * @see SingleFileSurvey#_file
	 */
	public String getFile() {
		return _file;
	}

	/**
	 * Write accessor (setter) for attribute _file
	 * 
	 * @param x
	 *            new value for _file
	 * @see SingleFileSurvey#_file
	 */
	public void setFile(String x) {
		_file = x;
	}

	/**
	 * Read accessor (getter) for attribute _headerLinesNumber
	 * 
	 * @return int
	 * @see SingleFileSurvey#_headerLinesNumber
	 */
	public int getHeaderLinesNumber() {
		return _headerLinesNumber;
	}

	/**
	 * Write accessor (setter) for attribute _headerLinesNumber
	 * 
	 * @param x
	 *            new value for _headerLinesNumber
	 * @see SingleFileSurvey#_headerLinesNumber
	 */
	public void setHeaderLinesNumber(int x) {
		_headerLinesNumber = x;
	}

	/**
	 * Write accessor (setter) for attribute _headerLinesNumber
	 * 
	 * @param a_String
	 *            new value for _headerLinesNumber as String
	 * @see SingleFileSurvey#_headerLinesNumber
	 */
	public void setHeaderLinesNumber(String a_String) {
		try {
			setHeaderLinesNumber(Integer.parseInt(a_String));
		} catch (Exception e) {
		}
	}

	/**
	 * Remove any link for role model
	 */
	public void releaseModel() {
        if (_model != null) _model.reset();
		setModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public Survey getModel() {
		return _model;
	}

	/**
	 * Create a new link with x for role model
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setModel(Survey x) {
		if (_model == x)
			return;
		_model = x;
	}

	/**
	 * Remove any link for role stationModel
	 */
	private void releaseStationModel() {
        if (_stationModel != null) _stationModel.release();
		setStationModel(null);
	}

	/**
	 * returns the linked object for role <% aRole name%>
	 * 
	 * @return the object linked
	 */
	public StationModel getStationModel() {
		return _stationModel;
	}

	/**
	 * Create a new link with x for role stationModel
	 * 
	 * @param x
	 *            the object to link with
	 */
	public void setStationModel(StationModel x) {
		if (_stationModel == x)
			return;
		_stationModel = x;
	}

	/**
	 * 
	 * @param a_Reader
	 * @return String[]
	 * @exception java.io.IOException
	 */
	public String[] consumeHeader(BufferedReader a_Reader) throws IOException {
		Vector<String> v = new Vector<String>();
		for (int i = 0; i < _headerLinesNumber; i++) {
			String line = a_Reader.readLine();
			EditConversion.incrementErreurLigne();
			if (line != null) {
				v.add(line);
			} else {
				return null;
			}
		}
		String[] lines = new String[v.size()];
		return v.toArray(lines);
	}

	/**
	 * 
	 * @param isRefDateBased
	 * @param a_RefDate
	 * @param a_Progress
	 * @return Survey
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	@Override
    public Survey convert(/*long a_RefDate,*/
                          IProgressMonitor a_monitor, 
                          ProgressNotification a_Progress) throws InterruptedException {
		try {
			Survey survey = (Survey) _model.clone();
            // FAE 1661 : réinitialiser les stations
            survey.releaseStations();
			_stationModel.initRef();
            
			File file = new File(_file);
			BufferedReader reader2 = null;
			SizeBasedProgressNotifier notifier = new SizeBasedProgressNotifier(
					file.length(), 0, a_Progress);

			_stationModel.setTaille((int) file.length());
			EditConversion.setErreurFichier(file.getName());
			EditConversion.setErreurLigne(0);

			Station station = null;

			try {

				// test CDL FAE01
				FileReader reader = new FileReader(file);
				reader2 = new BufferedReader(reader);

				// Calcul du nombre de lignes du fichier
				EditConversion.setTotalLigne(Utils.calculNbLigne(file));

				consumeHeader(reader2);

				while ((station = _stationModel.convert(reader2, 
                                                        Utils.extraitFichierChemin(ViewFile.nomFichier),
                                                        /*a_RefDate,*/ 
                                                        a_monitor, 
                                                        notifier)) != null) {
                    
                    if (a_monitor.isCanceled()) {
                        // demande d'annulation
                        throw new InterruptedException(Global.EMPTY_STRING); 
                    } 

					// Tri des donnees de la station par ordre croissant
					station.tri(true);

                    station.setFichierSource(_file);
					survey.addStation(station);
				}

			} 
            catch (InterruptedException e) {
                EditConversion.setErreurFichier(null);
                EditConversion.setErreurLigne(0);
                EditConversion.setErreurChaine(null);
                throw e;
            } 
            catch (Exception e) {
				e.printStackTrace();
				throw new InterruptedException(e.getMessage() + Messages.SingleFileSurvey_1 + file.getName());
			} 
            finally {
				if (reader2 != null) {
					try {
						reader2.close();
					} catch (Exception ioExc) {
					}
				}
			}
			return survey;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

} /* End of class: SingleFileSurvey */
