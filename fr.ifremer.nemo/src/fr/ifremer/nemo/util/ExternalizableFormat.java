package fr.ifremer.nemo.util;

import com.ago.xml.SAXConsumer;
import com.ago.xml.XMLOutputWriter;
import java.text.Format;

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
