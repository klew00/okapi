/**
 * Insert.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class Insert  implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private java.lang.String subject;
    private java.lang.String source_lang;
    private java.lang.String target_lang;
    private java.lang.String segment;
    private java.lang.String translation;
    private java.lang.Integer reserved;

    public Insert() {
    }

    public Insert(
           java.lang.String subject,
           java.lang.String source_lang,
           java.lang.String target_lang,
           java.lang.String segment,
           java.lang.String translation,
           java.lang.Integer reserved) {
           this.subject = subject;
           this.source_lang = source_lang;
           this.target_lang = target_lang;
           this.segment = segment;
           this.translation = translation;
           this.reserved = reserved;
    }


    /**
     * Gets the subject value for this Insert.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this Insert.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }


    /**
     * Gets the source_lang value for this Insert.
     * 
     * @return source_lang
     */
    public java.lang.String getSource_lang() {
        return source_lang;
    }


    /**
     * Sets the source_lang value for this Insert.
     * 
     * @param source_lang
     */
    public void setSource_lang(java.lang.String source_lang) {
        this.source_lang = source_lang;
    }


    /**
     * Gets the target_lang value for this Insert.
     * 
     * @return target_lang
     */
    public java.lang.String getTarget_lang() {
        return target_lang;
    }


    /**
     * Sets the target_lang value for this Insert.
     * 
     * @param target_lang
     */
    public void setTarget_lang(java.lang.String target_lang) {
        this.target_lang = target_lang;
    }


    /**
     * Gets the segment value for this Insert.
     * 
     * @return segment
     */
    public java.lang.String getSegment() {
        return segment;
    }


    /**
     * Sets the segment value for this Insert.
     * 
     * @param segment
     */
    public void setSegment(java.lang.String segment) {
        this.segment = segment;
    }


    /**
     * Gets the translation value for this Insert.
     * 
     * @return translation
     */
    public java.lang.String getTranslation() {
        return translation;
    }


    /**
     * Sets the translation value for this Insert.
     * 
     * @param translation
     */
    public void setTranslation(java.lang.String translation) {
        this.translation = translation;
    }


    /**
     * Gets the reserved value for this Insert.
     * 
     * @return reserved
     */
    public java.lang.Integer getReserved() {
        return reserved;
    }


    /**
     * Sets the reserved value for this Insert.
     * 
     * @param reserved
     */
    public void setReserved(java.lang.Integer reserved) {
        this.reserved = reserved;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Insert)) return false;
        Insert other = (Insert) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject()))) &&
            ((this.source_lang==null && other.getSource_lang()==null) || 
             (this.source_lang!=null &&
              this.source_lang.equals(other.getSource_lang()))) &&
            ((this.target_lang==null && other.getTarget_lang()==null) || 
             (this.target_lang!=null &&
              this.target_lang.equals(other.getTarget_lang()))) &&
            ((this.segment==null && other.getSegment()==null) || 
             (this.segment!=null &&
              this.segment.equals(other.getSegment()))) &&
            ((this.translation==null && other.getTranslation()==null) || 
             (this.translation!=null &&
              this.translation.equals(other.getTranslation()))) &&
            ((this.reserved==null && other.getReserved()==null) || 
             (this.reserved!=null &&
              this.reserved.equals(other.getReserved())));
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
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        if (getSource_lang() != null) {
            _hashCode += getSource_lang().hashCode();
        }
        if (getTarget_lang() != null) {
            _hashCode += getTarget_lang().hashCode();
        }
        if (getSegment() != null) {
            _hashCode += getSegment().hashCode();
        }
        if (getTranslation() != null) {
            _hashCode += getTranslation().hashCode();
        }
        if (getReserved() != null) {
            _hashCode += getReserved().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Insert.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "insert"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source_lang");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "source_lang"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("target_lang");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "target_lang"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("segment");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "segment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("translation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "translation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reserved");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "reserved"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
