/**
 * NautilusSurveyWSService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nautilus.metadata;

public interface NautilusSurveyWSService extends javax.xml.rpc.Service {
    public java.lang.String getNAUTILUSAddress();

    public fr.ifremer.nautilus.metadata.NautilusSurveyWS getNAUTILUS() throws javax.xml.rpc.ServiceException;

    public fr.ifremer.nautilus.metadata.NautilusSurveyWS getNAUTILUS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
