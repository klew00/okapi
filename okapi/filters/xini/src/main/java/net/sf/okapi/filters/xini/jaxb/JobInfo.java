
package net.sf.okapi.filters.xini.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Contains arbitrary information that is stored in project_info
 * 
 * <p>Java class for JobInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="JobInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ValuePair" type="{}ValuePair" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JobInfo", propOrder = {
    "valuePair"
})
public class JobInfo {

    @XmlElement(name = "ValuePair")
    protected List<ValuePair> valuePair;

    /**
     * Gets the value of the valuePair property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valuePair property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValuePair().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValuePair }
     * 
     * 
     */
    public List<ValuePair> getValuePair() {
        if (valuePair == null) {
            valuePair = new ArrayList<ValuePair>();
        }
        return this.valuePair;
    }

}
