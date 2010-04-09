/**
 * Match.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class Match  implements java.io.Serializable {
	private static final long serialVersionUID = 2192859075314843032L;

	private java.lang.String subject;
    private int quality;
    private int score;
    private java.lang.String translator;
    private java.util.Calendar create_date;
    private java.lang.String job_id;
    private java.lang.String segment;
    private java.lang.String translation;
    private java.lang.String source_lang;
    private java.lang.String target_lang;

    public Match() {
    }

    public Match(
           java.lang.String subject,
           int quality,
           int score,
           java.lang.String translator,
           java.util.Calendar create_date,
           java.lang.String job_id,
           java.lang.String segment,
           java.lang.String translation,
           java.lang.String source_lang,
           java.lang.String target_lang) {
           this.subject = subject;
           this.quality = quality;
           this.score = score;
           this.translator = translator;
           this.create_date = create_date;
           this.job_id = job_id;
           this.segment = segment;
           this.translation = translation;
           this.source_lang = source_lang;
           this.target_lang = target_lang;
    }


    /**
     * Gets the subject value for this Match.
     * 
     * @return subject
     */
    public java.lang.String getSubject() {
        return subject;
    }


    /**
     * Sets the subject value for this Match.
     * 
     * @param subject
     */
    public void setSubject(java.lang.String subject) {
        this.subject = subject;
    }


    /**
     * Gets the quality value for this Match.
     * 
     * @return quality
     */
    public int getQuality() {
        return quality;
    }


    /**
     * Sets the quality value for this Match.
     * 
     * @param quality
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }


    /**
     * Gets the score value for this Match.
     * 
     * @return score
     */
    public int getScore() {
        return score;
    }


    /**
     * Sets the score value for this Match.
     * 
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }


    /**
     * Gets the translator value for this Match.
     * 
     * @return translator
     */
    public java.lang.String getTranslator() {
        return translator;
    }


    /**
     * Sets the translator value for this Match.
     * 
     * @param translator
     */
    public void setTranslator(java.lang.String translator) {
        this.translator = translator;
    }


    /**
     * Gets the create_date value for this Match.
     * 
     * @return create_date
     */
    public java.util.Calendar getCreate_date() {
        return create_date;
    }


    /**
     * Sets the create_date value for this Match.
     * 
     * @param create_date
     */
    public void setCreate_date(java.util.Calendar create_date) {
        this.create_date = create_date;
    }


    /**
     * Gets the job_id value for this Match.
     * 
     * @return job_id
     */
    public java.lang.String getJob_id() {
        return job_id;
    }


    /**
     * Sets the job_id value for this Match.
     * 
     * @param job_id
     */
    public void setJob_id(java.lang.String job_id) {
        this.job_id = job_id;
    }


    /**
     * Gets the segment value for this Match.
     * 
     * @return segment
     */
    public java.lang.String getSegment() {
        return segment;
    }


    /**
     * Sets the segment value for this Match.
     * 
     * @param segment
     */
    public void setSegment(java.lang.String segment) {
        this.segment = segment;
    }


    /**
     * Gets the translation value for this Match.
     * 
     * @return translation
     */
    public java.lang.String getTranslation() {
        return translation;
    }


    /**
     * Sets the translation value for this Match.
     * 
     * @param translation
     */
    public void setTranslation(java.lang.String translation) {
        this.translation = translation;
    }


    /**
     * Gets the source_lang value for this Match.
     * 
     * @return source_lang
     */
    public java.lang.String getSource_lang() {
        return source_lang;
    }


    /**
     * Sets the source_lang value for this Match.
     * 
     * @param source_lang
     */
    public void setSource_lang(java.lang.String source_lang) {
        this.source_lang = source_lang;
    }


    /**
     * Gets the target_lang value for this Match.
     * 
     * @return target_lang
     */
    public java.lang.String getTarget_lang() {
        return target_lang;
    }


    /**
     * Sets the target_lang value for this Match.
     * 
     * @param target_lang
     */
    public void setTarget_lang(java.lang.String target_lang) {
        this.target_lang = target_lang;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Match)) return false;
        Match other = (Match) obj;
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
            this.quality == other.getQuality() &&
            this.score == other.getScore() &&
            ((this.translator==null && other.getTranslator()==null) || 
             (this.translator!=null &&
              this.translator.equals(other.getTranslator()))) &&
            ((this.create_date==null && other.getCreate_date()==null) || 
             (this.create_date!=null &&
              this.create_date.equals(other.getCreate_date()))) &&
            ((this.job_id==null && other.getJob_id()==null) || 
             (this.job_id!=null &&
              this.job_id.equals(other.getJob_id()))) &&
            ((this.segment==null && other.getSegment()==null) || 
             (this.segment!=null &&
              this.segment.equals(other.getSegment()))) &&
            ((this.translation==null && other.getTranslation()==null) || 
             (this.translation!=null &&
              this.translation.equals(other.getTranslation()))) &&
            ((this.source_lang==null && other.getSource_lang()==null) || 
             (this.source_lang!=null &&
              this.source_lang.equals(other.getSource_lang()))) &&
            ((this.target_lang==null && other.getTarget_lang()==null) || 
             (this.target_lang!=null &&
              this.target_lang.equals(other.getTarget_lang())));
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
        _hashCode += getQuality();
        _hashCode += getScore();
        if (getTranslator() != null) {
            _hashCode += getTranslator().hashCode();
        }
        if (getCreate_date() != null) {
            _hashCode += getCreate_date().hashCode();
        }
        if (getJob_id() != null) {
            _hashCode += getJob_id().hashCode();
        }
        if (getSegment() != null) {
            _hashCode += getSegment().hashCode();
        }
        if (getTranslation() != null) {
            _hashCode += getTranslation().hashCode();
        }
        if (getSource_lang() != null) {
            _hashCode += getSource_lang().hashCode();
        }
        if (getTarget_lang() != null) {
            _hashCode += getTarget_lang().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Match.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "match"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("subject");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "subject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("quality");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "quality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("score");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "score"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("translator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "translator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("create_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "create_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("job_id");
        elemField.setXmlName(new javax.xml.namespace.QName("http://tempuri.org/", "job_id"));
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
