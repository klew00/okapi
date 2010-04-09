
package com.crosslang.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetTranslatedFile_WithOptionsResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getTranslatedFileWithOptionsResult"
})
@XmlRootElement(name = "GetTranslatedFile_WithOptionsResponse")
public class GetTranslatedFileWithOptionsResponse {

    @XmlElementRef(name = "GetTranslatedFile_WithOptionsResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> getTranslatedFileWithOptionsResult;

    /**
     * Gets the value of the getTranslatedFileWithOptionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGetTranslatedFileWithOptionsResult() {
        return getTranslatedFileWithOptionsResult;
    }

    /**
     * Sets the value of the getTranslatedFileWithOptionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGetTranslatedFileWithOptionsResult(JAXBElement<String> value) {
        this.getTranslatedFileWithOptionsResult = ((JAXBElement<String> ) value);
    }

}
