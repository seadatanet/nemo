/**
 * NemoWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ws;

public class NemoWSServiceLocator extends org.apache.axis.client.Service implements fr.ifremer.nemo.ws.NemoWSService {

    public NemoWSServiceLocator() {
    }


    public NemoWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public NemoWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for NEMO
    private java.lang.String NEMO_address = "http://alboran5.ifremer.fr:8081/nemo/services/NEMO";

    public java.lang.String getNEMOAddress() {
        return NEMO_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String NEMOWSDDServiceName = "NEMO";

    public java.lang.String getNEMOWSDDServiceName() {
        return NEMOWSDDServiceName;
    }

    public void setNEMOWSDDServiceName(java.lang.String name) {
        NEMOWSDDServiceName = name;
    }

    public fr.ifremer.nemo.ws.NemoWS getNEMO() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(NEMO_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getNEMO(endpoint);
    }

    public fr.ifremer.nemo.ws.NemoWS getNEMO(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            fr.ifremer.nemo.ws.NEMOSoapBindingStub _stub = new fr.ifremer.nemo.ws.NEMOSoapBindingStub(portAddress, this);
            _stub.setPortName(getNEMOWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setNEMOEndpointAddress(java.lang.String address) {
        NEMO_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (fr.ifremer.nemo.ws.NemoWS.class.isAssignableFrom(serviceEndpointInterface)) {
                fr.ifremer.nemo.ws.NEMOSoapBindingStub _stub = new fr.ifremer.nemo.ws.NEMOSoapBindingStub(new java.net.URL(NEMO_address), this);
                _stub.setPortName(getNEMOWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("NEMO".equals(inputPortName)) {
            return getNEMO();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("nemo", "NemoWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("nemo", "NEMO"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("NEMO".equals(portName)) {
            setNEMOEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
