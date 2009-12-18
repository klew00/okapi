
package com.crosslang.ws;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.crosslang.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetTranslatedFileFileType_QNAME = new QName("http://tempuri.org/", "fileType");
    private final static QName _GetTranslatedFileApiKey_QNAME = new QName("http://tempuri.org/", "apiKey");
    private final static QName _GetTranslatedFileUser_QNAME = new QName("http://tempuri.org/", "user");
    private final static QName _GetTranslatedFileUtf8Base64EncodedString_QNAME = new QName("http://tempuri.org/", "utf8_base64_EncodedString");
    private final static QName _GetTranslatedFileDepartmentId_QNAME = new QName("http://tempuri.org/", "departmentId");
    private final static QName _AnyURI_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyURI");
    private final static QName _Char_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "char");
    private final static QName _UnsignedByte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedByte");
    private final static QName _DateTime_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "dateTime");
    private final static QName _AnyType_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "anyType");
    private final static QName _UnsignedInt_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedInt");
    private final static QName _Int_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "int");
    private final static QName _QName_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "QName");
    private final static QName _UnsignedShort_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedShort");
    private final static QName _Float_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "float");
    private final static QName _Decimal_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "decimal");
    private final static QName _ArrayOfstring_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/Arrays", "ArrayOfstring");
    private final static QName _Double_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "double");
    private final static QName _Long_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "long");
    private final static QName _Short_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "short");
    private final static QName _Guid_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "guid");
    private final static QName _Base64Binary_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "base64Binary");
    private final static QName _Duration_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "duration");
    private final static QName _Byte_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "byte");
    private final static QName _String_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "string");
    private final static QName _UnsignedLong_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "unsignedLong");
    private final static QName _Boolean_QNAME = new QName("http://schemas.microsoft.com/2003/10/Serialization/", "boolean");
    private final static QName _GetSupportedLanguagesCompanyId_QNAME = new QName("http://tempuri.org/", "companyId");
    private final static QName _GetSupportedLanguagesResponseGetSupportedLanguagesResult_QNAME = new QName("http://tempuri.org/", "GetSupportedLanguagesResult");
    private final static QName _GetTranslatedFileResponseGetTranslatedFileResult_QNAME = new QName("http://tempuri.org/", "GetTranslatedFileResult");
    private final static QName _GetTranslatedStringWithOptionsResponseGetTranslatedStringWithOptionsResult_QNAME = new QName("http://tempuri.org/", "GetTranslatedString_WithOptionsResult");
    private final static QName _GetTranslatedStringSourceString_QNAME = new QName("http://tempuri.org/", "sourceString");
    private final static QName _GetTranslatedStringResponseGetTranslatedStringResult_QNAME = new QName("http://tempuri.org/", "GetTranslatedStringResult");
    private final static QName _GetTranslatedStringWithOptionsOptions_QNAME = new QName("http://tempuri.org/", "options");
    private final static QName _GetTranslatedFileWithOptionsResponseGetTranslatedFileWithOptionsResult_QNAME = new QName("http://tempuri.org/", "GetTranslatedFile_WithOptionsResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.crosslang.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTranslatedFile }
     * 
     */
    public GetTranslatedFile createGetTranslatedFile() {
        return new GetTranslatedFile();
    }

    /**
     * Create an instance of {@link GetSupportedLanguages }
     * 
     */
    public GetSupportedLanguages createGetSupportedLanguages() {
        return new GetSupportedLanguages();
    }

    /**
     * Create an instance of {@link ArrayOfstring }
     * 
     */
    public ArrayOfstring createArrayOfstring() {
        return new ArrayOfstring();
    }

    /**
     * Create an instance of {@link GetSupportedLanguagesResponse }
     * 
     */
    public GetSupportedLanguagesResponse createGetSupportedLanguagesResponse() {
        return new GetSupportedLanguagesResponse();
    }

    /**
     * Create an instance of {@link GetTranslatedFileResponse }
     * 
     */
    public GetTranslatedFileResponse createGetTranslatedFileResponse() {
        return new GetTranslatedFileResponse();
    }

    /**
     * Create an instance of {@link GetTranslatedStringWithOptionsResponse }
     * 
     */
    public GetTranslatedStringWithOptionsResponse createGetTranslatedStringWithOptionsResponse() {
        return new GetTranslatedStringWithOptionsResponse();
    }

    /**
     * Create an instance of {@link GetTranslatedString }
     * 
     */
    public GetTranslatedString createGetTranslatedString() {
        return new GetTranslatedString();
    }

    /**
     * Create an instance of {@link GetTranslatedStringResponse }
     * 
     */
    public GetTranslatedStringResponse createGetTranslatedStringResponse() {
        return new GetTranslatedStringResponse();
    }

    /**
     * Create an instance of {@link GetTranslatedStringWithOptions }
     * 
     */
    public GetTranslatedStringWithOptions createGetTranslatedStringWithOptions() {
        return new GetTranslatedStringWithOptions();
    }

    /**
     * Create an instance of {@link GetTranslatedFileWithOptionsResponse }
     * 
     */
    public GetTranslatedFileWithOptionsResponse createGetTranslatedFileWithOptionsResponse() {
        return new GetTranslatedFileWithOptionsResponse();
    }

    /**
     * Create an instance of {@link GetTranslatedFileWithOptions }
     * 
     */
    public GetTranslatedFileWithOptions createGetTranslatedFileWithOptions() {
        return new GetTranslatedFileWithOptions();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "fileType", scope = GetTranslatedFile.class)
    public JAXBElement<String> createGetTranslatedFileFileType(String value) {
        return new JAXBElement<String>(_GetTranslatedFileFileType_QNAME, String.class, GetTranslatedFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "apiKey", scope = GetTranslatedFile.class)
    public JAXBElement<String> createGetTranslatedFileApiKey(String value) {
        return new JAXBElement<String>(_GetTranslatedFileApiKey_QNAME, String.class, GetTranslatedFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "user", scope = GetTranslatedFile.class)
    public JAXBElement<String> createGetTranslatedFileUser(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUser_QNAME, String.class, GetTranslatedFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "utf8_base64_EncodedString", scope = GetTranslatedFile.class)
    public JAXBElement<String> createGetTranslatedFileUtf8Base64EncodedString(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUtf8Base64EncodedString_QNAME, String.class, GetTranslatedFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "departmentId", scope = GetTranslatedFile.class)
    public JAXBElement<String> createGetTranslatedFileDepartmentId(String value) {
        return new JAXBElement<String>(_GetTranslatedFileDepartmentId_QNAME, String.class, GetTranslatedFile.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyURI")
    public JAXBElement<String> createAnyURI(String value) {
        return new JAXBElement<String>(_AnyURI_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "char")
    public JAXBElement<Integer> createChar(Integer value) {
        return new JAXBElement<Integer>(_Char_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedByte")
    public JAXBElement<Short> createUnsignedByte(Short value) {
        return new JAXBElement<Short>(_UnsignedByte_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "dateTime")
    public JAXBElement<XMLGregorianCalendar> createDateTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_DateTime_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "anyType")
    public JAXBElement<Object> createAnyType(Object value) {
        return new JAXBElement<Object>(_AnyType_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedInt")
    public JAXBElement<Long> createUnsignedInt(Long value) {
        return new JAXBElement<Long>(_UnsignedInt_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "int")
    public JAXBElement<Integer> createInt(Integer value) {
        return new JAXBElement<Integer>(_Int_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "QName")
    public JAXBElement<QName> createQName(QName value) {
        return new JAXBElement<QName>(_QName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedShort")
    public JAXBElement<Integer> createUnsignedShort(Integer value) {
        return new JAXBElement<Integer>(_UnsignedShort_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Float }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "float")
    public JAXBElement<Float> createFloat(Float value) {
        return new JAXBElement<Float>(_Float_QNAME, Float.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigDecimal }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "decimal")
    public JAXBElement<BigDecimal> createDecimal(BigDecimal value) {
        return new JAXBElement<BigDecimal>(_Decimal_QNAME, BigDecimal.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays", name = "ArrayOfstring")
    public JAXBElement<ArrayOfstring> createArrayOfstring(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_ArrayOfstring_QNAME, ArrayOfstring.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "double")
    public JAXBElement<Double> createDouble(Double value) {
        return new JAXBElement<Double>(_Double_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Long }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "long")
    public JAXBElement<Long> createLong(Long value) {
        return new JAXBElement<Long>(_Long_QNAME, Long.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Short }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "short")
    public JAXBElement<Short> createShort(Short value) {
        return new JAXBElement<Short>(_Short_QNAME, Short.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "guid")
    public JAXBElement<String> createGuid(String value) {
        return new JAXBElement<String>(_Guid_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "base64Binary")
    public JAXBElement<byte[]> createBase64Binary(byte[] value) {
        return new JAXBElement<byte[]>(_Base64Binary_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "duration")
    public JAXBElement<Duration> createDuration(Duration value) {
        return new JAXBElement<Duration>(_Duration_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Byte }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "byte")
    public JAXBElement<Byte> createByte(Byte value) {
        return new JAXBElement<Byte>(_Byte_QNAME, Byte.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "unsignedLong")
    public JAXBElement<BigInteger> createUnsignedLong(BigInteger value) {
        return new JAXBElement<BigInteger>(_UnsignedLong_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.microsoft.com/2003/10/Serialization/", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "apiKey", scope = GetSupportedLanguages.class)
    public JAXBElement<String> createGetSupportedLanguagesApiKey(String value) {
        return new JAXBElement<String>(_GetTranslatedFileApiKey_QNAME, String.class, GetSupportedLanguages.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "user", scope = GetSupportedLanguages.class)
    public JAXBElement<String> createGetSupportedLanguagesUser(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUser_QNAME, String.class, GetSupportedLanguages.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "departmentId", scope = GetSupportedLanguages.class)
    public JAXBElement<String> createGetSupportedLanguagesDepartmentId(String value) {
        return new JAXBElement<String>(_GetTranslatedFileDepartmentId_QNAME, String.class, GetSupportedLanguages.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "companyId", scope = GetSupportedLanguages.class)
    public JAXBElement<String> createGetSupportedLanguagesCompanyId(String value) {
        return new JAXBElement<String>(_GetSupportedLanguagesCompanyId_QNAME, String.class, GetSupportedLanguages.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetSupportedLanguagesResult", scope = GetSupportedLanguagesResponse.class)
    public JAXBElement<ArrayOfstring> createGetSupportedLanguagesResponseGetSupportedLanguagesResult(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetSupportedLanguagesResponseGetSupportedLanguagesResult_QNAME, ArrayOfstring.class, GetSupportedLanguagesResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTranslatedFileResult", scope = GetTranslatedFileResponse.class)
    public JAXBElement<String> createGetTranslatedFileResponseGetTranslatedFileResult(String value) {
        return new JAXBElement<String>(_GetTranslatedFileResponseGetTranslatedFileResult_QNAME, String.class, GetTranslatedFileResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTranslatedString_WithOptionsResult", scope = GetTranslatedStringWithOptionsResponse.class)
    public JAXBElement<String> createGetTranslatedStringWithOptionsResponseGetTranslatedStringWithOptionsResult(String value) {
        return new JAXBElement<String>(_GetTranslatedStringWithOptionsResponseGetTranslatedStringWithOptionsResult_QNAME, String.class, GetTranslatedStringWithOptionsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sourceString", scope = GetTranslatedString.class)
    public JAXBElement<String> createGetTranslatedStringSourceString(String value) {
        return new JAXBElement<String>(_GetTranslatedStringSourceString_QNAME, String.class, GetTranslatedString.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "apiKey", scope = GetTranslatedString.class)
    public JAXBElement<String> createGetTranslatedStringApiKey(String value) {
        return new JAXBElement<String>(_GetTranslatedFileApiKey_QNAME, String.class, GetTranslatedString.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "user", scope = GetTranslatedString.class)
    public JAXBElement<String> createGetTranslatedStringUser(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUser_QNAME, String.class, GetTranslatedString.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "departmentId", scope = GetTranslatedString.class)
    public JAXBElement<String> createGetTranslatedStringDepartmentId(String value) {
        return new JAXBElement<String>(_GetTranslatedFileDepartmentId_QNAME, String.class, GetTranslatedString.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTranslatedStringResult", scope = GetTranslatedStringResponse.class)
    public JAXBElement<String> createGetTranslatedStringResponseGetTranslatedStringResult(String value) {
        return new JAXBElement<String>(_GetTranslatedStringResponseGetTranslatedStringResult_QNAME, String.class, GetTranslatedStringResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "options", scope = GetTranslatedStringWithOptions.class)
    public JAXBElement<ArrayOfstring> createGetTranslatedStringWithOptionsOptions(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetTranslatedStringWithOptionsOptions_QNAME, ArrayOfstring.class, GetTranslatedStringWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "sourceString", scope = GetTranslatedStringWithOptions.class)
    public JAXBElement<String> createGetTranslatedStringWithOptionsSourceString(String value) {
        return new JAXBElement<String>(_GetTranslatedStringSourceString_QNAME, String.class, GetTranslatedStringWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "apiKey", scope = GetTranslatedStringWithOptions.class)
    public JAXBElement<String> createGetTranslatedStringWithOptionsApiKey(String value) {
        return new JAXBElement<String>(_GetTranslatedFileApiKey_QNAME, String.class, GetTranslatedStringWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "user", scope = GetTranslatedStringWithOptions.class)
    public JAXBElement<String> createGetTranslatedStringWithOptionsUser(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUser_QNAME, String.class, GetTranslatedStringWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "departmentId", scope = GetTranslatedStringWithOptions.class)
    public JAXBElement<String> createGetTranslatedStringWithOptionsDepartmentId(String value) {
        return new JAXBElement<String>(_GetTranslatedFileDepartmentId_QNAME, String.class, GetTranslatedStringWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "GetTranslatedFile_WithOptionsResult", scope = GetTranslatedFileWithOptionsResponse.class)
    public JAXBElement<String> createGetTranslatedFileWithOptionsResponseGetTranslatedFileWithOptionsResult(String value) {
        return new JAXBElement<String>(_GetTranslatedFileWithOptionsResponseGetTranslatedFileWithOptionsResult_QNAME, String.class, GetTranslatedFileWithOptionsResponse.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "fileType", scope = GetTranslatedFileWithOptions.class)
    public JAXBElement<String> createGetTranslatedFileWithOptionsFileType(String value) {
        return new JAXBElement<String>(_GetTranslatedFileFileType_QNAME, String.class, GetTranslatedFileWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfstring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "options", scope = GetTranslatedFileWithOptions.class)
    public JAXBElement<ArrayOfstring> createGetTranslatedFileWithOptionsOptions(ArrayOfstring value) {
        return new JAXBElement<ArrayOfstring>(_GetTranslatedStringWithOptionsOptions_QNAME, ArrayOfstring.class, GetTranslatedFileWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "apiKey", scope = GetTranslatedFileWithOptions.class)
    public JAXBElement<String> createGetTranslatedFileWithOptionsApiKey(String value) {
        return new JAXBElement<String>(_GetTranslatedFileApiKey_QNAME, String.class, GetTranslatedFileWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "user", scope = GetTranslatedFileWithOptions.class)
    public JAXBElement<String> createGetTranslatedFileWithOptionsUser(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUser_QNAME, String.class, GetTranslatedFileWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "utf8_base64_EncodedString", scope = GetTranslatedFileWithOptions.class)
    public JAXBElement<String> createGetTranslatedFileWithOptionsUtf8Base64EncodedString(String value) {
        return new JAXBElement<String>(_GetTranslatedFileUtf8Base64EncodedString_QNAME, String.class, GetTranslatedFileWithOptions.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://tempuri.org/", name = "departmentId", scope = GetTranslatedFileWithOptions.class)
    public JAXBElement<String> createGetTranslatedFileWithOptionsDepartmentId(String value) {
        return new JAXBElement<String>(_GetTranslatedFileDepartmentId_QNAME, String.class, GetTranslatedFileWithOptions.class, value);
    }

}
