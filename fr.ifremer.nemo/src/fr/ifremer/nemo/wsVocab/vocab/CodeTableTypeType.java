/**
 * CodeTableTypeType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.wsVocab.vocab;

public class CodeTableTypeType  implements java.io.Serializable {
    private java.lang.String listKey;

    private java.lang.String listLongName;

    private java.lang.String listShortName;

    private java.lang.String listDefinition;

    private int listVersion;

    private java.util.Calendar listLastMod;

    public CodeTableTypeType() {
    }

    public CodeTableTypeType(
           java.lang.String listKey,
           java.lang.String listLongName,
           java.lang.String listShortName,
           java.lang.String listDefinition,
           int listVersion,
           java.util.Calendar listLastMod) {
           this.listKey = listKey;
           this.listLongName = listLongName;
           this.listShortName = listShortName;
           this.listDefinition = listDefinition;
           this.listVersion = listVersion;
           this.listLastMod = listLastMod;
    }


    /**
     * Gets the listKey value for this CodeTableTypeType.
     * 
     * @return listKey
     */
    public java.lang.String getListKey() {
        return listKey;
    }


    /**
     * Sets the listKey value for this CodeTableTypeType.
     * 
     * @param listKey
     */
    public void setListKey(java.lang.String listKey) {
        this.listKey = listKey;
    }


    /**
     * Gets the listLongName value for this CodeTableTypeType.
     * 
     * @return listLongName
     */
    public java.lang.String getListLongName() {
        return listLongName;
    }


    /**
     * Sets the listLongName value for this CodeTableTypeType.
     * 
     * @param listLongName
     */
    public void setListLongName(java.lang.String listLongName) {
        this.listLongName = listLongName;
    }


    /**
     * Gets the listShortName value for this CodeTableTypeType.
     * 
     * @return listShortName
     */
    public java.lang.String getListShortName() {
        return listShortName;
    }


    /**
     * Sets the listShortName value for this CodeTableTypeType.
     * 
     * @param listShortName
     */
    public void setListShortName(java.lang.String listShortName) {
        this.listShortName = listShortName;
    }


    /**
     * Gets the listDefinition value for this CodeTableTypeType.
     * 
     * @return listDefinition
     */
    public java.lang.String getListDefinition() {
        return listDefinition;
    }


    /**
     * Sets the listDefinition value for this CodeTableTypeType.
     * 
     * @param listDefinition
     */
    public void setListDefinition(java.lang.String listDefinition) {
        this.listDefinition = listDefinition;
    }


    /**
     * Gets the listVersion value for this CodeTableTypeType.
     * 
     * @return listVersion
     */
    public int getListVersion() {
        return listVersion;
    }


    /**
     * Sets the listVersion value for this CodeTableTypeType.
     * 
     * @param listVersion
     */
    public void setListVersion(int listVersion) {
        this.listVersion = listVersion;
    }


    /**
     * Gets the listLastMod value for this CodeTableTypeType.
     * 
     * @return listLastMod
     */
    public java.util.Calendar getListLastMod() {
        return listLastMod;
    }


    /**
     * Sets the listLastMod value for this CodeTableTypeType.
     * 
     * @param listLastMod
     */
    public void setListLastMod(java.util.Calendar listLastMod) {
        this.listLastMod = listLastMod;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CodeTableTypeType)) return false;
        CodeTableTypeType other = (CodeTableTypeType) obj;
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
            ((this.listLongName==null && other.getListLongName()==null) || 
             (this.listLongName!=null &&
              this.listLongName.equals(other.getListLongName()))) &&
            ((this.listShortName==null && other.getListShortName()==null) || 
             (this.listShortName!=null &&
              this.listShortName.equals(other.getListShortName()))) &&
            ((this.listDefinition==null && other.getListDefinition()==null) || 
             (this.listDefinition!=null &&
              this.listDefinition.equals(other.getListDefinition()))) &&
            this.listVersion == other.getListVersion() &&
            ((this.listLastMod==null && other.getListLastMod()==null) || 
             (this.listLastMod!=null &&
              this.listLastMod.equals(other.getListLastMod())));
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
        if (getListLongName() != null) {
            _hashCode += getListLongName().hashCode();
        }
        if (getListShortName() != null) {
            _hashCode += getListShortName().hashCode();
        }
        if (getListDefinition() != null) {
            _hashCode += getListDefinition().hashCode();
        }
        _hashCode += getListVersion();
        if (getListLastMod() != null) {
            _hashCode += getListLastMod().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CodeTableTypeType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:vocab/types", "codeTableTypeType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listKey");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listLongName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listLongName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listShortName");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listShortName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listDefinition");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listDefinition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listVersion");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listVersion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("listLastMod");
        elemField.setXmlName(new javax.xml.namespace.QName("urn:vocab/types", "listLastMod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
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
