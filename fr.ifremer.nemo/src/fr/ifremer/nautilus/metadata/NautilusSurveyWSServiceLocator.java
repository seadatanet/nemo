/**
 * NautilusSurveyWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nautilus.metadata;

public class NautilusSurveyWSServiceLocator extends
		org.apache.axis.client.Service implements
		fr.ifremer.nautilus.metadata.NautilusSurveyWSService {

	public NautilusSurveyWSServiceLocator() {
	}

	public NautilusSurveyWSServiceLocator(
			org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public NautilusSurveyWSServiceLocator(java.lang.String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for NAUTILUS
	private java.lang.String NAUTILUS_address = "http://localhost:8888/nautilus/services/NAUTILUS";

	public java.lang.String getNAUTILUSAddress() {
		return NAUTILUS_address;
	}

	// The WSDD service name defaults to the port name.
	private java.lang.String NAUTILUSWSDDServiceName = "NAUTILUS";

	public java.lang.String getNAUTILUSWSDDServiceName() {
		return NAUTILUSWSDDServiceName;
	}

	public void setNAUTILUSWSDDServiceName(java.lang.String name) {
		NAUTILUSWSDDServiceName = name;
	}

	public fr.ifremer.nautilus.metadata.NautilusSurveyWS getNAUTILUS()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(NAUTILUS_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getNAUTILUS(endpoint);
	}

	public fr.ifremer.nautilus.metadata.NautilusSurveyWS getNAUTILUS(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			fr.ifremer.nautilus.metadata.NAUTILUSSoapBindingStub _stub = new fr.ifremer.nautilus.metadata.NAUTILUSSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getNAUTILUSWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setNAUTILUSEndpointAddress(java.lang.String address) {
		NAUTILUS_address = address;
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		try {
			if (fr.ifremer.nautilus.metadata.NautilusSurveyWS.class
					.isAssignableFrom(serviceEndpointInterface)) {
				fr.ifremer.nautilus.metadata.NAUTILUSSoapBindingStub _stub = new fr.ifremer.nautilus.metadata.NAUTILUSSoapBindingStub(
						new java.net.URL(NAUTILUS_address), this);
				_stub.setPortName(getNAUTILUSWSDDServiceName());
				return _stub;
			}
		} catch (java.lang.Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException(
				"There is no stub implementation for the interface:  "
						+ (serviceEndpointInterface == null ? "null"
								: serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
			Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		java.lang.String inputPortName = portName.getLocalPart();
		if ("NAUTILUS".equals(inputPortName)) {
			return getNAUTILUS();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("nautilus",
				"NautilusSurveyWSService");
	}

	private java.util.HashSet ports = null;

	public java.util.Iterator getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet();
			ports.add(new javax.xml.namespace.QName("nautilus", "NAUTILUS"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(java.lang.String portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {

		if ("NAUTILUS".equals(portName)) {
			setNAUTILUSEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(
					" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName,
			java.lang.String address) throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
