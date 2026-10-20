package fr.ifremer.util.iso.version;

import java.io.File;
import java.io.FileReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Simple XML checker for ISO19115 and ISO19139 XML files.
 * 
 * @author mlarour
 */
public class IsoAnalyser {
	private static class Iso19115Found extends SAXException {
	}

	private static class Iso19139Found extends SAXException {
	}

	private static class IsoNotFound extends SAXException {
	}

	/**
	 * Main SAX handler.
	 */
	private static class IsoHandler extends DefaultHandler {
		private static String ISO19115_ROOT = "Metadata"; //$NON-NLS-1$
		private static String ISO19115_ROOTCHILD = "mdFileID"; //$NON-NLS-1$
		private static String ISO19139_ROOT = "gmi:MI_Metadata"; //$NON-NLS-1$
		private static String ISO19139_ROOTCHILD = "gmd:fileIdentifier"; //$NON-NLS-1$

		private boolean iso19115;
		private boolean iso19139;

		@Override
		public void startDocument() throws SAXException {
			iso19115 = false;
			iso19139 = false;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (!iso19115 && !iso19139) {
				if (qName.compareTo(ISO19115_ROOT) == 0) {
					iso19115 = true;
				} else if (qName.compareTo(ISO19139_ROOT) == 0) {
					iso19139 = true;
				} else {
					throw new IsoNotFound();
				}
			} else {
				if (iso19115 && qName.compareTo(ISO19115_ROOTCHILD) == 0) {
					throw new Iso19115Found();
				} else if (iso19139 && qName.compareTo(ISO19139_ROOTCHILD) == 0) {
					throw new Iso19139Found();
				} else {
					throw new IsoNotFound();
				}
			}
		}
	}

	/**
	 * Handled types.
	 */
	public enum ISO_TYPE {
		ISO19115, // file is ISO19115 format
		ISO19139, // file is ISO19139 format
		UNKNOWN, // file is not ISO19115 and not ISO19139
		FAILED // file check failed
	}

	private static SAXParser parser = null;

	/**
	 * Main API : checks the ISO type of a given XML file.
	 * 
	 * @param filePath a file path to the XML file to check.
	 * 
	 * @return the ISO type of the file.
	 */
	public static ISO_TYPE getType(File file) {
		FileReader fileReader = null;
		try {
			if (parser == null) {
				parser = SAXParserFactory.newInstance().newSAXParser();
			}
			IsoHandler isoHandler = new IsoHandler();
			fileReader = new FileReader(file);
			InputSource is = new InputSource(fileReader);
			parser.parse(is, isoHandler);
			fileReader.close();
		} catch (Exception e) {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (Exception lost) {
				}
			}
			if (e instanceof Iso19115Found) {
				return ISO_TYPE.ISO19115;
			} else if (e instanceof Iso19139Found) {
				return ISO_TYPE.ISO19139;
			} else if (e instanceof IsoNotFound) {
				return ISO_TYPE.UNKNOWN;
			} else {
				return ISO_TYPE.FAILED;
			}
		}
		return ISO_TYPE.UNKNOWN;
	}

}
