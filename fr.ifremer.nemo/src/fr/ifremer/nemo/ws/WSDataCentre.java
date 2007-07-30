/**
 * WSDataCentre.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ws;

public class WSDataCentre implements java.io.Serializable {
	private java.lang.String code;

	private java.lang.String countryName;

	private java.lang.String name;

	public WSDataCentre() {
	}

	public WSDataCentre(java.lang.String code, java.lang.String countryName,
			java.lang.String name) {
		this.code = code;
		this.countryName = countryName;
		this.name = name;
	}

	/**
	 * Gets the code value for this WSDataCentre.
	 * 
	 * @return code
	 */
	public java.lang.String getCode() {
		return code;
	}

	/**
	 * Sets the code value for this WSDataCentre.
	 * 
	 * @param code
	 */
	public void setCode(java.lang.String code) {
		this.code = code;
	}

	/**
	 * Gets the countryName value for this WSDataCentre.
	 * 
	 * @return countryName
	 */
	public java.lang.String getCountryName() {
		return countryName;
	}

	/**
	 * Sets the countryName value for this WSDataCentre.
	 * 
	 * @param countryName
	 */
	public void setCountryName(java.lang.String countryName) {
		this.countryName = countryName;
	}

	/**
	 * Gets the name value for this WSDataCentre.
	 * 
	 * @return name
	 */
	public java.lang.String getName() {
		return name;
	}

	/**
	 * Sets the name value for this WSDataCentre.
	 * 
	 * @param name
	 */
	public void setName(java.lang.String name) {
		this.name = name;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WSDataCentre))
			return false;
		WSDataCentre other = (WSDataCentre) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.code == null && other.getCode() == null) || (this.code != null && this.code
						.equals(other.getCode())))
				&& ((this.countryName == null && other.getCountryName() == null) || (this.countryName != null && this.countryName
						.equals(other.getCountryName())))
				&& ((this.name == null && other.getName() == null) || (this.name != null && this.name
						.equals(other.getName())));
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
		if (getCountryName() != null) {
			_hashCode += getCountryName().hashCode();
		}
		if (getName() != null) {
			_hashCode += getName().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			WSDataCentre.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("nemo",
				"WSDataCentre"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("code");
		elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("countryName");
		elemField.setXmlName(new javax.xml.namespace.QName("", "countryName"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("name");
		elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
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
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType,
				_xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(
			java.lang.String mechType, java.lang.Class _javaType,
			javax.xml.namespace.QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType,
				_xmlType, typeDesc);
	}

}
