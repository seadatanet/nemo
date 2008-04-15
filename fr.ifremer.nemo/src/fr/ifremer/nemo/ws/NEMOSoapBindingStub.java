/**
 * NEMOSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ws;

public class NEMOSoapBindingStub extends org.apache.axis.client.Stub implements fr.ifremer.nemo.ws.NemoWS {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[7];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getCountries");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSCountry"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSCountry[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getCountriesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataCentres");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSDataCentre"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSDataCentre[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDataCentresReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getDataTypes");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSDataType"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSDataType[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getDataTypesReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRegions");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSRegion"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSRegion[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getRegionsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getShips");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSShip"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSShip[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getShipsReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSurveyList");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSSurveyDescription"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSSurveyDescription[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getSurveyListReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getParameters");
        oper.setReturnType(new javax.xml.namespace.QName("nemo", "ArrayOfWSParameter"));
        oper.setReturnClass(fr.ifremer.nemo.ws.WSParameter[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getParametersReturn"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

    }

    public NEMOSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public NEMOSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public NEMOSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSCountry");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSCountry[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSCountry");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSDataCentre");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSDataCentre[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSDataCentre");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSDataType");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSDataType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSDataType");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSParameter");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSParameter[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSParameter");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSRegion");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSRegion[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSRegion");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSShip");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSShip[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSShip");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "ArrayOfWSSurveyDescription");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSSurveyDescription[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("nemo", "WSSurveyDescription");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("nemo", "WSCountry");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSCountry.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("nemo", "WSDataCentre");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSDataCentre.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("nemo", "WSDataType");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSDataType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("nemo", "WSParameter");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSParameter.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("nemo", "WSRegion");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSRegion.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("nemo", "WSShip");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSShip.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("nemo", "WSSurveyDescription");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.ws.WSSurveyDescription.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public fr.ifremer.nemo.ws.WSCountry[] getCountries() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getCountries"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSCountry[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSCountry[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSCountry[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fr.ifremer.nemo.ws.WSDataCentre[] getDataCentres() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getDataCentres"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSDataCentre[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSDataCentre[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSDataCentre[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fr.ifremer.nemo.ws.WSDataType[] getDataTypes() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getDataTypes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSDataType[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSDataType[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSDataType[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fr.ifremer.nemo.ws.WSRegion[] getRegions() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getRegions"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSRegion[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSRegion[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSRegion[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fr.ifremer.nemo.ws.WSShip[] getShips() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getShips"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSShip[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSShip[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSShip[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fr.ifremer.nemo.ws.WSSurveyDescription[] getSurveyList() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getSurveyList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSSurveyDescription[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSSurveyDescription[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSSurveyDescription[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public fr.ifremer.nemo.ws.WSParameter[] getParameters() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("nemo", "getParameters"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (fr.ifremer.nemo.ws.WSParameter[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (fr.ifremer.nemo.ws.WSParameter[]) org.apache.axis.utils.JavaUtils.convert(_resp, fr.ifremer.nemo.ws.WSParameter[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
