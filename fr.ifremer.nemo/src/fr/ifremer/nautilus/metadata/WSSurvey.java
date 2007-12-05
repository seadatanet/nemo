/**
 * WSSurvey.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nautilus.metadata;

public class WSSurvey  implements java.io.Serializable {
    private java.lang.String chiefScientist;

    private java.lang.String comment;

    private java.lang.String countryCode;

    private java.lang.String dataCentre;

    private fr.ifremer.nautilus.metadata.WSCollectedData[] dataTypes;

    private long endDate;

    private java.lang.String laboratories;

    private java.lang.String name;

    private java.lang.String projectName;

    private java.lang.String reference;

    private java.lang.String regionName;

    private java.lang.String shipCode;

    private java.lang.String shipName;

    private long startDate;

    public WSSurvey() {
    }

    public WSSurvey(
           java.lang.String chiefScientist,
           java.lang.String comment,
           java.lang.String countryCode,
           java.lang.String dataCentre,
           fr.ifremer.nautilus.metadata.WSCollectedData[] dataTypes,
           long endDate,
           java.lang.String laboratories,
           java.lang.String name,
           java.lang.String projectName,
           java.lang.String reference,
           java.lang.String regionName,
           java.lang.String shipCode,
           java.lang.String shipName,
           long startDate) {
           this.chiefScientist = chiefScientist;
           this.comment = comment;
           this.countryCode = countryCode;
           this.dataCentre = dataCentre;
           this.dataTypes = dataTypes;
           this.endDate = endDate;
           this.laboratories = laboratories;
           this.name = name;
           this.projectName = projectName;
           this.reference = reference;
           this.regionName = regionName;
           this.shipCode = shipCode;
           this.shipName = shipName;
           this.startDate = startDate;
    }


    /**
     * Gets the chiefScientist value for this WSSurvey.
     * 
     * @return chiefScientist
     */
    public java.lang.String getChiefScientist() {
        return chiefScientist;
    }


    /**
     * Sets the chiefScientist value for this WSSurvey.
     * 
     * @param chiefScientist
     */
    public void setChiefScientist(java.lang.String chiefScientist) {
        this.chiefScientist = chiefScientist;
    }


    /**
     * Gets the comment value for this WSSurvey.
     * 
     * @return comment
     */
    public java.lang.String getComment() {
        return comment;
    }


    /**
     * Sets the comment value for this WSSurvey.
     * 
     * @param comment
     */
    public void setComment(java.lang.String comment) {
        this.comment = comment;
    }


    /**
     * Gets the countryCode value for this WSSurvey.
     * 
     * @return countryCode
     */
    public java.lang.String getCountryCode() {
        return countryCode;
    }


    /**
     * Sets the countryCode value for this WSSurvey.
     * 
     * @param countryCode
     */
    public void setCountryCode(java.lang.String countryCode) {
        this.countryCode = countryCode;
    }


    /**
     * Gets the dataCentre value for this WSSurvey.
     * 
     * @return dataCentre
     */
    public java.lang.String getDataCentre() {
        return dataCentre;
    }


    /**
     * Sets the dataCentre value for this WSSurvey.
     * 
     * @param dataCentre
     */
    public void setDataCentre(java.lang.String dataCentre) {
        this.dataCentre = dataCentre;
    }


    /**
     * Gets the dataTypes value for this WSSurvey.
     * 
     * @return dataTypes
     */
    public fr.ifremer.nautilus.metadata.WSCollectedData[] getDataTypes() {
        return dataTypes;
    }


    /**
     * Sets the dataTypes value for this WSSurvey.
     * 
     * @param dataTypes
     */
    public void setDataTypes(fr.ifremer.nautilus.metadata.WSCollectedData[] dataTypes) {
        this.dataTypes = dataTypes;
    }


    /**
     * Gets the endDate value for this WSSurvey.
     * 
     * @return endDate
     */
    public long getEndDate() {
        return endDate;
    }


    /**
     * Sets the endDate value for this WSSurvey.
     * 
     * @param endDate
     */
    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }


    /**
     * Gets the laboratories value for this WSSurvey.
     * 
     * @return laboratories
     */
    public java.lang.String getLaboratories() {
        return laboratories;
    }


    /**
     * Sets the laboratories value for this WSSurvey.
     * 
     * @param laboratories
     */
    public void setLaboratories(java.lang.String laboratories) {
        this.laboratories = laboratories;
    }


    /**
     * Gets the name value for this WSSurvey.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this WSSurvey.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the projectName value for this WSSurvey.
     * 
     * @return projectName
     */
    public java.lang.String getProjectName() {
        return projectName;
    }


    /**
     * Sets the projectName value for this WSSurvey.
     * 
     * @param projectName
     */
    public void setProjectName(java.lang.String projectName) {
        this.projectName = projectName;
    }


    /**
     * Gets the reference value for this WSSurvey.
     * 
     * @return reference
     */
    public java.lang.String getReference() {
        return reference;
    }


    /**
     * Sets the reference value for this WSSurvey.
     * 
     * @param reference
     */
    public void setReference(java.lang.String reference) {
        this.reference = reference;
    }


    /**
     * Gets the regionName value for this WSSurvey.
     * 
     * @return regionName
     */
    public java.lang.String getRegionName() {
        return regionName;
    }


    /**
     * Sets the regionName value for this WSSurvey.
     * 
     * @param regionName
     */
    public void setRegionName(java.lang.String regionName) {
        this.regionName = regionName;
    }


    /**
     * Gets the shipCode value for this WSSurvey.
     * 
     * @return shipCode
     */
    public java.lang.String getShipCode() {
        return shipCode;
    }


    /**
     * Sets the shipCode value for this WSSurvey.
     * 
     * @param shipCode
     */
    public void setShipCode(java.lang.String shipCode) {
        this.shipCode = shipCode;
    }


    /**
     * Gets the shipName value for this WSSurvey.
     * 
     * @return shipName
     */
    public java.lang.String getShipName() {
        return shipName;
    }


    /**
     * Sets the shipName value for this WSSurvey.
     * 
     * @param shipName
     */
    public void setShipName(java.lang.String shipName) {
        this.shipName = shipName;
    }


    /**
     * Gets the startDate value for this WSSurvey.
     * 
     * @return startDate
     */
    public long getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this WSSurvey.
     * 
     * @param startDate
     */
    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof WSSurvey)) return false;
        WSSurvey other = (WSSurvey) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.chiefScientist==null && other.getChiefScientist()==null) || 
             (this.chiefScientist!=null &&
              this.chiefScientist.equals(other.getChiefScientist()))) &&
            ((this.comment==null && other.getComment()==null) || 
             (this.comment!=null &&
              this.comment.equals(other.getComment()))) &&
            ((this.countryCode==null && other.getCountryCode()==null) || 
             (this.countryCode!=null &&
              this.countryCode.equals(other.getCountryCode()))) &&
            ((this.dataCentre==null && other.getDataCentre()==null) || 
             (this.dataCentre!=null &&
              this.dataCentre.equals(other.getDataCentre()))) &&
            ((this.dataTypes==null && other.getDataTypes()==null) || 
             (this.dataTypes!=null &&
              java.util.Arrays.equals(this.dataTypes, other.getDataTypes()))) &&
            this.endDate == other.getEndDate() &&
            ((this.laboratories==null && other.getLaboratories()==null) || 
             (this.laboratories!=null &&
              this.laboratories.equals(other.getLaboratories()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.projectName==null && other.getProjectName()==null) || 
             (this.projectName!=null &&
              this.projectName.equals(other.getProjectName()))) &&
            ((this.reference==null && other.getReference()==null) || 
             (this.reference!=null &&
              this.reference.equals(other.getReference()))) &&
            ((this.regionName==null && other.getRegionName()==null) || 
             (this.regionName!=null &&
              this.regionName.equals(other.getRegionName()))) &&
            ((this.shipCode==null && other.getShipCode()==null) || 
             (this.shipCode!=null &&
              this.shipCode.equals(other.getShipCode()))) &&
            ((this.shipName==null && other.getShipName()==null) || 
             (this.shipName!=null &&
              this.shipName.equals(other.getShipName()))) &&
            this.startDate == other.getStartDate();
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
        if (getChiefScientist() != null) {
            _hashCode += getChiefScientist().hashCode();
        }
        if (getComment() != null) {
            _hashCode += getComment().hashCode();
        }
        if (getCountryCode() != null) {
            _hashCode += getCountryCode().hashCode();
        }
        if (getDataCentre() != null) {
            _hashCode += getDataCentre().hashCode();
        }
        if (getDataTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getDataTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getDataTypes(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Long(getEndDate()).hashCode();
        if (getLaboratories() != null) {
            _hashCode += getLaboratories().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getProjectName() != null) {
            _hashCode += getProjectName().hashCode();
        }
        if (getReference() != null) {
            _hashCode += getReference().hashCode();
        }
        if (getRegionName() != null) {
            _hashCode += getRegionName().hashCode();
        }
        if (getShipCode() != null) {
            _hashCode += getShipCode().hashCode();
        }
        if (getShipName() != null) {
            _hashCode += getShipName().hashCode();
        }
        _hashCode += new Long(getStartDate()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(WSSurvey.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("nautilus", "WSSurvey"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("chiefScientist");
        elemField.setXmlName(new javax.xml.namespace.QName("", "chiefScientist"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comment");
        elemField.setXmlName(new javax.xml.namespace.QName("", "comment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("countryCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "countryCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataCentre");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataCentre"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dataTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("nautilus", "WSCollectedData"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "endDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("laboratories");
        elemField.setXmlName(new javax.xml.namespace.QName("", "laboratories"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("projectName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "projectName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reference");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reference"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "regionName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "shipCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shipName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "shipName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
