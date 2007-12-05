/**
 * WSDataType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ws;

public class WSDataType  implements java.io.Serializable {
    private java.lang.String code;

    private java.lang.String libelle;

    private boolean sensor;

    public WSDataType() {
    }

    public WSDataType(
           java.lang.String code,
           java.lang.String libelle,
           boolean sensor) {
           this.code = code;
           this.libelle = libelle;
           this.sensor = sensor;
    }


    /**
     * Gets the code value for this WSDataType.
     * 
     * @return code
     */
    public java.lang.String getCode() {
        return code;
    }


    /**
     * Sets the code value for this WSDataType.
     * 
     * @param code
     */
    public void setCode(java.lang.String code) {
        this.code = code;
    }


    /**
     * Gets the libelle value for this WSDataType.
     * 
     * @return libelle
     */
    public java.lang.String getLibelle() {
        return libelle;
    }


    /**
     * Sets the libelle value for this WSDataType.
     * 
     * @param libelle
     */
    public void setLibelle(java.lang.String libelle) {
        this.libelle = libelle;
    }


    /**
     * Gets the sensor value for this WSDataType.
     * 
     * @return sensor
     */
    public boolean isSensor() {
        return sensor;
    }


    /**
     * Sets the sensor value for this WSDataType.
     * 
     * @param sensor
     */
    public void setSensor(boolean sensor) {
        this.sensor = sensor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSDataType)) return false;
        WSDataType other = (WSDataType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.code==null && other.getCode()==null) || 
             (this.code!=null &&
              this.code.equals(other.getCode()))) &&
            ((this.libelle==null && other.getLibelle()==null) || 
             (this.libelle!=null &&
              this.libelle.equals(other.getLibelle()))) &&
            this.sensor == other.isSensor();
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
        if (getCode() != null) {
            _hashCode += getCode().hashCode();
        }
        if (getLibelle() != null) {
            _hashCode += getLibelle().hashCode();
        }
        _hashCode += (isSensor() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSDataType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("nemo", "WSDataType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("code");
        elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("libelle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "libelle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sensor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sensor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
