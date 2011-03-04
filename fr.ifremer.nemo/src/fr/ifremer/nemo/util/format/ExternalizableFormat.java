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
public abstract class ExternalizableFormat extends Format implements Cloneable, SAXConsumer
{

	protected String errorMessage;

	public ExternalizableFormat()
	{
		super();
	}

	/**
	 * 
	 * @return Object
	 */
	@Override
	public abstract Object clone();

	/**
	 * 
	 * @param a_Writer
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer)
	{

	}

	/**
	 * Getter sur errorMessage.
	 * 
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}
}