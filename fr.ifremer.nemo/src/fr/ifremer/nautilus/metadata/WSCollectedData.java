/**
 * WSCollectedData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nautilus.metadata;

public class WSCollectedData implements java.io.Serializable {
	private java.lang.String code;

	private int observationNumber;

	private boolean sensor;

	public WSCollectedData() {
	}

	public WSCollectedData(java.lang.String code, int observationNumber,
			boolean sensor) {
		this.code = code;
		this.observationNumber = observationNumber;
		this.sensor = sensor;
	}

	/**
	 * Gets the code value for this WSCollectedData.
	 * 
	 * @return code
	 */
	public java.lang.String getCode() {
		return code;
	}

	/**
	 * Sets the code value for this WSCollectedData.
	 * 
	 * @param code
	 */
	public void setCode(java.lang.String code) {
		this.code = code;
	}

	/**
	 * Gets the observationNumber value for this WSCollectedData.
	 * 
	 * @return observationNumber
	 */
	public int getObservationNumber() {
		return observationNumber;
	}

	/**
	 * Sets the observationNumber value for this WSCollectedData.
	 * 
	 * @param observationNumber
	 */
	public void setObservationNumber(int observationNumber) {
		this.observationNumber = observationNumber;
	}

	/**
	 * Gets the sensor value for this WSCollectedData.
	 * 
	 * @return sensor
	 */
	public boolean isSensor() {
		return sensor;
	}

	/**
	 * Sets the sensor value for this WSCollectedData.
	 * 
	 * @param sensor
	 */
	public void setSensor(boolean sensor) {
		this.sensor = sensor;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof WSCollectedData))
			return false;
		WSCollectedData other = (WSCollectedData) obj;
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
				&& this.observationNumber == other.getObservationNumber()
				&& this.sensor == other.isSensor();
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
		_hashCode += getObservationNumber();
		_hashCode += (isSensor() ? Boolean.TRUE : Boolean.FALSE).hashCode();
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
			WSCollectedData.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("nautilus",
				"WSCollectedData"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("code");
		elemField.setXmlName(new javax.xml.namespace.QName("", "code"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("observationNumber");
		elemField.setXmlName(new javax.xml.namespace.QName("",
				"observationNumber"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "int"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("sensor");
		elemField.setXmlName(new javax.xml.namespace.QName("", "sensor"));
		elemField.setXmlType(new javax.xml.namespace.QName(
				"http://www.w3.org/2001/XMLSchema", "boolean"));
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
