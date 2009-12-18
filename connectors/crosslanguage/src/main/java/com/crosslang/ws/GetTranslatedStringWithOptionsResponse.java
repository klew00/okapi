
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
 *         &lt;element name="GetTranslatedString_WithOptionsResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getTranslatedStringWithOptionsResult"
})
@XmlRootElement(name = "GetTranslatedString_WithOptionsResponse")
public class GetTranslatedStringWithOptionsResponse {

    @XmlElementRef(name = "GetTranslatedString_WithOptionsResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> getTranslatedStringWithOptionsResult;

    /**
     * Gets the value of the getTranslatedStringWithOptionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGetTranslatedStringWithOptionsResult() {
        return getTranslatedStringWithOptionsResult;
    }

    /**
     * Sets the value of the getTranslatedStringWithOptionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGetTranslatedStringWithOptionsResult(JAXBElement<String> value) {
        this.getTranslatedStringWithOptionsResult = ((JAXBElement<String> ) value);
    }

}
