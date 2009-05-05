package fr.ifremer.nemo.util.format;

import java.text.Format;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;

/**
 * Class ExternalizableFormat
 * 
 * 
 * @author Gerard BUNEL
 */
public abstract class ExternalizableFormat extends Format implements Cloneable,
		SAXConsumer {

	public ExternalizableFormat() {
		super();
	}

	/**
	 * 
	 * @return Object
	 */
	public abstract Object clone();

	/**
	 * 
	 * @param a_Writer
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer) {

	}

} /* End of class: ExternalizableFormat */
