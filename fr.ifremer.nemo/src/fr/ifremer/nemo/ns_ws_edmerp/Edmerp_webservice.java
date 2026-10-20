/**
 * Edmo_webservice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ns_ws_edmerp;

public interface Edmerp_webservice extends javax.xml.rpc.Service {
    public java.lang.String getedmerp_webserviceSoapAddress();

    public Edmerp_webserviceSoap getedmerp_webserviceSoap() throws javax.xml.rpc.ServiceException;

    public Edmerp_webserviceSoap getedmerp_webserviceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
