package com.ago.xml;

public interface XMLExternalizable {

	/**
	 * Write the XML defintion using a XMLOutputWriter
	 * 
	 * @param a_Writer
	 *            XMLOutputWriter to use for externalization
	 */
	public void writeXMLOn(XMLOutputWriter a_Writer);
}
