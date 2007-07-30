/**
 * NemoWSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ws;

public interface NemoWSService extends javax.xml.rpc.Service {
	public java.lang.String getNEMOAddress();

	public fr.ifremer.nemo.ws.NemoWS getNEMO()
			throws javax.xml.rpc.ServiceException;

	public fr.ifremer.nemo.ws.NemoWS getNEMO(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException;
}
