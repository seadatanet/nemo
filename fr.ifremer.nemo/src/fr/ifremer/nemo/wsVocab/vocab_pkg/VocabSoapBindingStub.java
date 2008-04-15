/**
 * VocabSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.wsVocab.vocab_pkg;

public class VocabSoapBindingStub extends org.apache.axis.client.Stub implements fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[8];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("whatLists");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "categoryKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableType"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableTypeType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableTypeType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "recordKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "earliestRecord"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("verifyTerm");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "listKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "entryTerm"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "termType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "verifiedTerm"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("searchVocab");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "listKey"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "searchTerm"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "termType"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getMap");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "subjectList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "objectList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "inference"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRelatedRecordByCriteria");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "subjectList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "subjectField"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "subjectText"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "caseSensitivity"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), java.lang.Boolean.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "partialMatch"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), java.lang.Boolean.class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "objectList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "inferences"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRelatedRecordByTerm");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "subjectTerm"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "predicate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "objectList"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "inferences"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("whatListsCat");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class, false, false);
        param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:vocab/types", "error"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

    }

    public VocabSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public VocabSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public VocabSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:vocab/types", "codeTableTypeType");
            cachedSerQNames.add(qName);
            cls = fr.ifremer.nemo.wsVocab.vocab.CodeTableTypeType.class;
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
                    _call.setEncodingStyle(null);
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

    public void whatLists(java.lang.String categoryKey, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableTypeTypeArrayHolder codeTableType, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "whatLists"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {categoryKey});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableType.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableTypeType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableType"));
            } catch (java.lang.Exception _exception) {
                codeTableType.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableTypeType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableType")), fr.ifremer.nemo.wsVocab.vocab.CodeTableTypeType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void getList(java.lang.String[] recordKey, java.util.Calendar earliestRecord, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "getList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {recordKey, earliestRecord});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"));
            } catch (java.lang.Exception _exception) {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void verifyTerm(java.lang.String listKey, java.lang.String entryTerm, java.lang.String termType, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeHolder verifiedTerm, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "verifyTerm"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {listKey, entryTerm, termType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                verifiedTerm.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "verifiedTerm"));
            } catch (java.lang.Exception _exception) {
                verifiedTerm.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "verifiedTerm")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void searchVocab(java.lang.String listKey, java.lang.String searchTerm, java.lang.String termType, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "searchVocab"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {listKey, searchTerm, termType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"));
            } catch (java.lang.Exception _exception) {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void getMap(java.lang.String[] subjectList, int predicate, java.lang.String[] objectList, java.lang.String inference, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "getMap"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {subjectList, new java.lang.Integer(predicate), objectList, inference});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"));
            } catch (java.lang.Exception _exception) {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void getRelatedRecordByCriteria(java.lang.String[] subjectList, java.lang.String subjectField, java.lang.String subjectText, java.lang.Boolean caseSensitivity, java.lang.Boolean partialMatch, int predicate, java.lang.String[] objectList, java.lang.String inferences, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "getRelatedRecordByCriteria"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {subjectList, subjectField, subjectText, caseSensitivity, partialMatch, new java.lang.Integer(predicate), objectList, inferences});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"));
            } catch (java.lang.Exception _exception) {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void getRelatedRecordByTerm(java.lang.String[] subjectTerm, int predicate, java.lang.String[] objectList, java.lang.String inferences, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "getRelatedRecordByTerm"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {subjectTerm, new java.lang.Integer(predicate), objectList, inferences});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"));
            } catch (java.lang.Exception _exception) {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public void whatListsCat(fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:vocab/defs");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:vocab/types", "whatListsCat"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord"));
            } catch (java.lang.Exception _exception) {
                codeTableRecord.value = (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecord")), fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[].class);
            }
            try {
                error.value = ((java.lang.Boolean) _output.get(new javax.xml.namespace.QName("urn:vocab/types", "error"))).booleanValue();
            } catch (java.lang.Exception _exception) {
                error.value = ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("urn:vocab/types", "error")), boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
