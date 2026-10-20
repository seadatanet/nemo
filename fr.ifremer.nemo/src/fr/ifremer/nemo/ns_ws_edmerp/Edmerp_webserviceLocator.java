/**
 * Edmo_webserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ns_ws_edmerp;

public class Edmerp_webserviceLocator extends org.apache.axis.client.Service implements Edmerp_webservice {

    public Edmerp_webserviceLocator() {
    }


    public Edmerp_webserviceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Edmerp_webserviceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }


    // Use to get a proxy class for edmerp_webserviceSoap
    private java.lang.String edmerp_webserviceSoap_address = "https://edmerp.seadatanet.org/ws_edmerp/edmerp.asmx";

    public java.lang.String getedmerp_webserviceSoapAddress() {
        return edmerp_webserviceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String edmerp_webserviceSoapWSDDServiceName = "EdmerpSoap";

    public java.lang.String getedmerp_webserviceSoapWSDDServiceName() {
        return edmerp_webserviceSoapWSDDServiceName;
    }

    public void setedmerp_webserviceSoapWSDDServiceName(java.lang.String name) {
        edmerp_webserviceSoapWSDDServiceName = name;
    }

    public Edmerp_webserviceSoap getedmerp_webserviceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(edmerp_webserviceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getedmerp_webserviceSoap(endpoint);
    }

    public Edmerp_webserviceSoap getedmerp_webserviceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            Edmerp_webserviceSoapStub _stub = new Edmerp_webserviceSoapStub(portAddress, this);
            _stub.setPortName(getedmerp_webserviceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setedmerp_webserviceSoapEndpointAddress(java.lang.String address) {
        edmerp_webserviceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    @Override
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Edmerp_webserviceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                Edmerp_webserviceSoapStub _stub = new Edmerp_webserviceSoapStub(new java.net.URL(edmerp_webserviceSoap_address), this);
                _stub.setPortName(getedmerp_webserviceSoapWSDDServiceName());
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
    @Override
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("edmerp_webserviceSoap".equals(inputPortName)) {
            return getedmerp_webserviceSoap();
        }
		java.rmi.Remote _stub = getPort(serviceEndpointInterface);
		((org.apache.axis.client.Stub) _stub).setPortName(portName);
		return _stub;
    }

    @Override
	public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://seadatanet.maris2.nl/webservices/edmerp/", "Edmerp");
    }

    private java.util.HashSet ports = null;

    @Override
	public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://seadatanet.maris2.nl/webservices/edmerp/", "EdmerpSoap"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

    	if ("edmerp_webserviceSoap".equals(portName)) {
    		setedmerp_webserviceSoapEndpointAddress(address);
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
