
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
 *         &lt;element name="GetTranslatedFileResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "getTranslatedFileResult"
})
@XmlRootElement(name = "GetTranslatedFileResponse")
public class GetTranslatedFileResponse {

    @XmlElementRef(name = "GetTranslatedFileResult", namespace = "http://tempuri.org/", type = JAXBElement.class)
    protected JAXBElement<String> getTranslatedFileResult;

    /**
     * Gets the value of the getTranslatedFileResult property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getGetTranslatedFileResult() {
        return getTranslatedFileResult;
    }

    /**
     * Sets the value of the getTranslatedFileResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setGetTranslatedFileResult(JAXBElement<String> value) {
        this.getTranslatedFileResult = ((JAXBElement<String> ) value);
    }

}
