/**
 * Query.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class Query  implements java.io.Serializable {
	private static final long serialVersionUID = 7130298885295773742L;

	private java.lang.String id;
    private java.lang.String source;
    private java.lang.String source_lang;
    private java.lang.String target_lang;
    private java.lang.String subject;
    private java.lang.Integer mt;

    public Query() {
    }

    public Query(
           java.lang.String id,
           java.lang.String source,
           java.lang.String source_lang,
           java.lang.String target_lang,
           java.lang.String subject,
           java.lang.Integer mt) {
           this.id = id;
           this.source = source;
           this.source_lang = source_lang;
           this.target_lang = target_lang;
           this.subject = subject;
           this.mt = mt;
    }


    /**
     * Gets the id value for this Query.
     * 
     * @return id
     */
    public java.lang.String getId() {
        return id;
    }


    /**
     * Sets the id value for this Query.
     * 
     * @param id
     */
    public void setId(java.lang.String id) {
        this.id = id;
    }


    /**
     * Gets the source value for this Query.
     * 
     * @return source
     */
    public java.lang.String getSource() {
        return source;
    }


    /**
     * Sets the source value for this Query.
     * 
     * @param source
     */
    public void setSource(java.lang.String source) {
        this.source = source;
    }


    /**
     * Gets the source_lang value for this Query.
     * 
     * @return source_lang
     */
    public java.lang.String getSource_lang() {
        return source_lang;
    }


    /**
     * Sets the source_lang value for this Query.
     * 
     * @param source_lang
     */
    public void setSource_lang(java.lang.String source_lang) {
        this.source_lang = source_lang;
    }


    /**
     * Gets the target_lang value for this Query.
     * 
     * @return target_lang
     */
    public java.lang.String getTarget_lang() {
        return target_lang;
    }


    /**
     * Sets the target_lang value for this Query.
     * 
     * @param target_lang
     */
    public void setTarget_lang(java.lang.String target_lang) {
        this.target_lang = target_lang;
    }


    /**
     * Gets the subject value for this Query.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this Query.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }


    /**
     * Gets the mt value for this Query.
     * 
     * @return mt
     */
    public java.lang.Integer getMt() {
        return mt;
    }


    /**
     * Sets the mt value for this Query.
     * 
     * @param mt
     */
    public void setMt(java.lang.Integer mt) {
        this.mt = mt;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Query)) return false;
        Query other = (Query) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.id==null && other.getId()==null) || 
             (this.id!=null &&
              this.id.equals(other.getId()))) &&
            ((this.source==null && other.getSource()==null) || 
             (this.source!=null &&
              this.source.equals(other.getSource()))) &&
            ((this.source_lang==null && other.getSource_lang()==null) || 
             (this.source_lang!=null &&
              this.source_lang.equals(other.getSource_lang()))) &&
            ((this.target_lang==null && other.getTarget_lang()==null) || 
             (this.target_lang!=null &&
              this.target_lang.equals(other.getTarget_lang()))) &&
            ((this.subject==null && other.getSubject()==null) || 
             (this.subject!=null &&
              this.subject.equals(other.getSubject()))) &&
            ((this.mt==null && other.getMt()==null) || 
             (this.mt!=null &&
              this.mt.equals(other.getMt())));
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
        if (getId() != null) {
            _hashCode += getId().hashCode();
        }
        if (getSource() != null) {
            _hashCode += getSource().hashCode();
        }
        if (getSource_lang() != null) {
            _hashCode += getSource_lang().hashCode();
        }
        if (getTarget_lang() != null) {
            _hashCode += getTarget_lang().hashCode();
        }
        if (getSubject() != null) {
            _hashCode += getSubject().hashCode();
        }
        if (getMt() != null) {
            _hashCode += getMt().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Query.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "query"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("source");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "source"));
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
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mt");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "mt"));
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
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           @SuppressWarnings("unchecked")
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
           @SuppressWarnings("unchecked")
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
