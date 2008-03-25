/**
 * CodeTableRecordType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.wsVocab.vocab;

public class CodeTableRecordType  implements java.io.Serializable {
    private java.lang.String listKey;

    private java.lang.String entryKey;

    private java.lang.String entryTerm;

    private java.lang.String entryTermAbbr;

    private java.lang.String entryTermDef;

    private java.util.Calendar entryTermLastMod;

    private fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] exactMatch;

    private fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] narrowMatch;

    private fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] broadMatch;

    private fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] minorMatch;

    private fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] majorMatch;

    public CodeTableRecordType() {
    }

    public CodeTableRecordType(
           java.lang.String listKey,
           java.lang.String entryKey,
           java.lang.String entryTerm,
           java.lang.String entryTermAbbr,
           java.lang.String entryTermDef,
           java.util.Calendar entryTermLastMod,
           fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] exactMatch,
           fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] narrowMatch,
           fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] broadMatch,
           fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] minorMatch,
           fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] majorMatch) {
           this.listKey = listKey;
           this.entryKey = entryKey;
           this.entryTerm = entryTerm;
           this.entryTermAbbr = entryTermAbbr;
           this.entryTermDef = entryTermDef;
           this.entryTermLastMod = entryTermLastMod;
           this.exactMatch = exactMatch;
           this.narrowMatch = narrowMatch;
           this.broadMatch = broadMatch;
           this.minorMatch = minorMatch;
           this.majorMatch = majorMatch;
    }


    /**
     * Gets the listKey value for this CodeTableRecordType.
     * 
     * @return listKey
     */
    public java.lang.String getListKey() {
        return listKey;
    }


    /**
     * Sets the listKey value for this CodeTableRecordType.
     * 
     * @param listKey
     */
    public void setListKey(java.lang.String listKey) {
        this.listKey = listKey;
    }


    /**
     * Gets the entryKey value for this CodeTableRecordType.
     * 
     * @return entryKey
     */
    public java.lang.String getEntryKey() {
        return entryKey;
    }


    /**
     * Sets the entryKey value for this CodeTableRecordType.
     * 
     * @param entryKey
     */
    public void setEntryKey(java.lang.String entryKey) {
        this.entryKey = entryKey;
    }


    /**
     * Gets the entryTerm value for this CodeTableRecordType.
     * 
     * @return entryTerm
     */
    public java.lang.String getEntryTerm() {
        return entryTerm;
    }


    /**
     * Sets the entryTerm value for this CodeTableRecordType.
     * 
     * @param entryTerm
     */
    public void setEntryTerm(java.lang.String entryTerm) {
        this.entryTerm = entryTerm;
    }


    /**
     * Gets the entryTermAbbr value for this CodeTableRecordType.
     * 
     * @return entryTermAbbr
     */
    public java.lang.String getEntryTermAbbr() {
        return entryTermAbbr;
    }


    /**
     * Sets the entryTermAbbr value for this CodeTableRecordType.
     * 
     * @param entryTermAbbr
     */
    public void setEntryTermAbbr(java.lang.String entryTermAbbr) {
        this.entryTermAbbr = entryTermAbbr;
    }


    /**
     * Gets the entryTermDef value for this CodeTableRecordType.
     * 
     * @return entryTermDef
     */
    public java.lang.String getEntryTermDef() {
        return entryTermDef;
    }


    /**
     * Sets the entryTermDef value for this CodeTableRecordType.
     * 
     * @param entryTermDef
     */
    public void setEntryTermDef(java.lang.String entryTermDef) {
        this.entryTermDef = entryTermDef;
    }


    /**
     * Gets the entryTermLastMod value for this CodeTableRecordType.
     * 
     * @return entryTermLastMod
     */
    public java.util.Calendar getEntryTermLastMod() {
        return entryTermLastMod;
    }


    /**
     * Sets the entryTermLastMod value for this CodeTableRecordType.
     * 
     * @param entryTermLastMod
     */
    public void setEntryTermLastMod(java.util.Calendar entryTermLastMod) {
        this.entryTermLastMod = entryTermLastMod;
    }


    /**
     * Gets the exactMatch value for this CodeTableRecordType.
     * 
     * @return exactMatch
     */
    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getExactMatch() {
        return exactMatch;
    }


    /**
     * Sets the exactMatch value for this CodeTableRecordType.
     * 
     * @param exactMatch
     */
    public void setExactMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] exactMatch) {
        this.exactMatch = exactMatch;
    }

    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType getExactMatch(int i) {
        return this.exactMatch[i];
    }

    public void setExactMatch(int i, fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType _value) {
        this.exactMatch[i] = _value;
    }


    /**
     * Gets the narrowMatch value for this CodeTableRecordType.
     * 
     * @return narrowMatch
     */
    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getNarrowMatch() {
        return narrowMatch;
    }


    /**
     * Sets the narrowMatch value for this CodeTableRecordType.
     * 
     * @param narrowMatch
     */
    public void setNarrowMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] narrowMatch) {
        this.narrowMatch = narrowMatch;
    }

    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType getNarrowMatch(int i) {
        return this.narrowMatch[i];
    }

    public void setNarrowMatch(int i, fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType _value) {
        this.narrowMatch[i] = _value;
    }


    /**
     * Gets the broadMatch value for this CodeTableRecordType.
     * 
     * @return broadMatch
     */
    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getBroadMatch() {
        return broadMatch;
    }


    /**
     * Sets the broadMatch value for this CodeTableRecordType.
     * 
     * @param broadMatch
     */
    public void setBroadMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] broadMatch) {
        this.broadMatch = broadMatch;
    }

    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType getBroadMatch(int i) {
        return this.broadMatch[i];
    }

    public void setBroadMatch(int i, fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType _value) {
        this.broadMatch[i] = _value;
    }


    /**
     * Gets the minorMatch value for this CodeTableRecordType.
     * 
     * @return minorMatch
     */
    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getMinorMatch() {
        return minorMatch;
    }


    /**
     * Sets the minorMatch value for this CodeTableRecordType.
     * 
     * @param minorMatch
     */
    public void setMinorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] minorMatch) {
        this.minorMatch = minorMatch;
    }

    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType getMinorMatch(int i) {
        return this.minorMatch[i];
    }

    public void setMinorMatch(int i, fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType _value) {
        this.minorMatch[i] = _value;
    }


    /**
     * Gets the majorMatch value for this CodeTableRecordType.
     * 
     * @return majorMatch
     */
    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getMajorMatch() {
        return majorMatch;
    }


    /**
     * Sets the majorMatch value for this CodeTableRecordType.
     * 
     * @param majorMatch
     */
    public void setMajorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] majorMatch) {
        this.majorMatch = majorMatch;
    }

    public fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType getMajorMatch(int i) {
        return this.majorMatch[i];
    }

    public void setMajorMatch(int i, fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType _value) {
        this.majorMatch[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CodeTableRecordType)) return false;
        CodeTableRecordType other = (CodeTableRecordType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.listKey==null && other.getListKey()==null) || 
             (this.listKey!=null &&
              this.listKey.equals(other.getListKey()))) &&
            ((this.entryKey==null && other.getEntryKey()==null) || 
             (this.entryKey!=null &&
              this.entryKey.equals(other.getEntryKey()))) &&
            ((this.entryTerm==null && other.getEntryTerm()==null) || 
             (this.entryTerm!=null &&
              this.entryTerm.equals(other.getEntryTerm()))) &&
            ((this.entryTermAbbr==null && other.getEntryTermAbbr()==null) || 
             (this.entryTermAbbr!=null &&
              this.entryTermAbbr.equals(other.getEntryTermAbbr()))) &&
            ((this.entryTermDef==null && other.getEntryTermDef()==null) || 
             (this.entryTermDef!=null &&
              this.entryTermDef.equals(other.getEntryTermDef()))) &&
            ((this.entryTermLastMod==null && other.getEntryTermLastMod()==null) || 
             (this.entryTermLastMod!=null &&
              this.entryTermLastMod.equals(other.getEntryTermLastMod()))) &&
            ((this.exactMatch==null && other.getExactMatch()==null) || 
             (this.exactMatch!=null &&
              java.util.Arrays.equals(this.exactMatch, other.getExactMatch()))) &&
            ((this.narrowMatch==null && other.getNarrowMatch()==null) || 
             (this.narrowMatch!=null &&
              java.util.Arrays.equals(this.narrowMatch, other.getNarrowMatch()))) &&
            ((this.broadMatch==null && other.getBroadMatch()==null) || 
             (this.broadMatch!=null &&
              java.util.Arrays.equals(this.broadMatch, other.getBroadMatch()))) &&
            ((this.minorMatch==null && other.getMinorMatch()==null) || 
             (this.minorMatch!=null &&
              java.util.Arrays.equals(this.minorMatch, other.getMinorMatch()))) &&
            ((this.majorMatch==null && other.getMajorMatch()==null) || 
             (this.majorMatch!=null &&
              java.util.Arrays.equals(this.majorMatch, other.getMajorMatch())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getListKey() != null) {
            _hashCode += getListKey().hashCode();
        }
        if (getEntryKey() != null) {
            _hashCode += getEntryKey().hashCode();
        }
        if (getEntryTerm() != null) {
            _hashCode += getEntryTerm().hashCode();
        }
        if (getEntryTermAbbr() != null) {
            _hashCode += getEntryTermAbbr().hashCode();
        }
        if (getEntryTermDef() != null) {
            _hashCode += getEntryTermDef().hashCode();
        }
        if (getEntryTermLastMod() != null) {
            _hashCode += getEntryTermLastMod().hashCode();
        }
        if (getExactMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExactMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExactMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getNarrowMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getNarrowMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getNarrowMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getBroadMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getBroadMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getBroadMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMinorMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMinorMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMinorMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMajorMatch() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMajorMatch());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMajorMatch(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CodeTableRecordType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listKey");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entryKey");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "entryKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entryTerm");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "entryTerm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entryTermAbbr");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "entryTermAbbr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entryTermDef");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "entryTermDef"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("entryTermLastMod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "entryTermLastMod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exactMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "exactMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("narrowMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "narrowMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("broadMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "broadMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("minorMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "minorMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("majorMatch");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "majorMatch"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableRecordType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
