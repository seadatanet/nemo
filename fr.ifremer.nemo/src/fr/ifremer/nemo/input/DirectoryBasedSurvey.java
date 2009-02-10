package fr.ifremer.nemo.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

import fr.ifremer.nemo.domain.Station;
import fr.ifremer.nemo.domain.Survey;
import fr.ifremer.nemo.output.ProgressNotification;
import fr.ifremer.nemo.ui.EditConversion;
import fr.ifremer.nemo.util.Global;
import fr.ifremer.nemo.util.Messages;
import fr.ifremer.nemo.util.SizeBasedProgressNotifier;

/**
 * Class DirectoryBasedSurvey
 * 
 * Represente une campagne dont les informations sont stockees dans une
 * repertoire sous la forme d'un ensemble de fichiers. Chaqque fichier
 * correspond alors aux donnees d'une station
 * 
 * @author Gerard BUNEL
 */
public class DirectoryBasedSurvey extends SurveyMedia implements Cloneable,
		SAXConsumer {

	/**
	 * Nom du repertoire dans lequel se trouvent les fichiers station
	 */
	protected String _directory;

	/**
	 * Campagne servant de modele pour stocker les informations de l'entete
	 * campagne
	 */
	protected Survey _model;

	/**
	 * Modele de station
	 */
	protected StationModel _stationModel;

	public DirectoryBasedSurvey() {
		super();
		_directory = "";
		_model = null;
		_stationModel = null;
	}

	/**
	 * 
	 * @param isTimeSeries
	 */
	public DirectoryBasedSurvey(String typeFichier) {
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
		DirectoryBasedSurvey tmp = new DirectoryBasedSurvey();
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
		if ((tmp = a_Attributes.getValue("directory")) != null) {
			setDirectory(tmp);
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
		a_Writer.putAttribute("directory", getDirectory());
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
		a_Writer.openTag("DirectoryBasedSurvey");
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
		DirectoryBasedSurvey copy = null;
		copy = (DirectoryBasedSurvey) super.clone();
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
	 * Read accessor (getter) for attribute _directory
	 * 
	 * @return String
	 * @see DirectoryBasedSurvey#_directory
	 */
	public String getDirectory() {
		return _directory;
	}

	/**
	 * Write accessor (setter) for attribute _directory
	 * 
	 * @param x
	 *            new value for _directory
	 * @see DirectoryBasedSurvey#_directory
	 */
	public void setDirectory(String x) {
		_directory = x;
	}

	/**
	 * Remove any link for role model
	 */
	public void releaseModel() {
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
	public void releaseStationModel() {
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
	 * @param isRefDateBased
	 * @param a_RefDate
	 * @param a_Progress
	 * @return Survey
	 * @throws ParseException
	 * @throws IOException
	 * @throws Exception
	 */
	public Survey convert(boolean isRefDateBased, long a_RefDate,
			ProgressNotification a_Progress) throws IOException, ParseException, InterruptedException {
		try {
			Survey survey = (Survey) _model.clone();
			File dir = new File(_directory);
			String[] files = dir.list();
			_stationModel.initRef();
			if ((files != null)) {
				long offset = 0;
				long size = 0;
				for (int i = 0; i < files.length; i++) {
					File file = new File(dir, files[i]);
					size += file.length();
				}
				
				SizeBasedProgressNotifier notifierStation = new SizeBasedProgressNotifier(size, offset, a_Progress);
								
				for (int i = 0; i < files.length; i++) {

					if (SurveyMedia.interrompu) {
						// demande d'annulation
						throw new InterruptedException("Interruption");
					} 
                    //else {
					File file = new File(dir, files[i]);
					EditConversion.setErreurFichier(file.getName());
					EditConversion.setErreurLigne(0);
					_stationModel.setTaille((int) file.length());
				    notifierStation.setOffset(offset);
					offset += file.length();
				//	RandomAccessFile reader = null;
					BufferedReader reader2 = null;
					try {
						// reader = new RandomAccessFile(file, "r");
						// test CDL FAE01
						FileReader reader  = new FileReader(file);
						reader2 = new BufferedReader(reader);
								
                        // TAG FAE 1386
//							if (_stationModel.isMetaInData2() || (_stationModel.getInputType().equals(Global.TRAJECTORY) && _stationModel.getTrajectory().getOutputType().equals(Global.ODV))) {
//								_stationModel.consumeHeaderPlus(reader2);
//							}
						
						Station station = _stationModel.convert(reader2,
								files[i], isRefDateBased, a_RefDate,
								notifierStation);
						
						// Tri des donnees de la station par ordre croissant
						station.tri(true);
						
						survey.addStation(station);
					} catch (Exception e) {
						throw new InterruptedException (e.getMessage() + Messages.getString("SingleFileSurvey.1") + file.getName());
					} finally {
						if (reader2 != null) {
							try {
								reader2.close();
							} catch (Exception ioExc) {
							}
						}
					}

				}

			}
			//}
			return survey;
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

} /* End of class: DirectoryBasedSurvey */
