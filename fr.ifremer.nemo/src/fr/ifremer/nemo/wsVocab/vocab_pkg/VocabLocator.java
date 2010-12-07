/**
 * VocabLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.wsVocab.vocab_pkg;

public class VocabLocator extends org.apache.axis.client.Service implements fr.ifremer.nemo.wsVocab.vocab_pkg.Vocab {

    public VocabLocator() {
    }


    public VocabLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public VocabLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for vocabPort
    private java.lang.String vocabPort_address = "http://vocab.ndg.nerc.ac.uk/axis2/services/vocab";

    public java.lang.String getvocabPortAddress() {
        return vocabPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String vocabPortWSDDServiceName = "vocabPort";

    public java.lang.String getvocabPortWSDDServiceName() {
        return vocabPortWSDDServiceName;
    }

    public void setvocabPortWSDDServiceName(java.lang.String name) {
        vocabPortWSDDServiceName = name;
    }

    public fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType getvocabPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(vocabPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getvocabPort(endpoint);
    }

    public fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType getvocabPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub _stub = new fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub(portAddress, this);
            _stub.setPortName(getvocabPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setvocabPortEndpointAddress(java.lang.String address) {
        vocabPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub _stub = new fr.ifremer.nemo.wsVocab.vocab_pkg.VocabSoapBindingStub(new java.net.URL(vocabPort_address), this);
                _stub.setPortName(getvocabPortWSDDServiceName());
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
        if ("vocabPort".equals(inputPortName)) {
            return getvocabPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:vocab/defs", "vocab");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:vocab/defs", "vocabPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("vocabPort".equals(portName)) {
            setvocabPortEndpointAddress(address);
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
