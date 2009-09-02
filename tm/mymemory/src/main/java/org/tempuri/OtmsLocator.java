/**
 * OtmsLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class OtmsLocator extends org.apache.axis.client.Service implements org.tempuri.Otms {

	private static final long serialVersionUID = 8639587061356723332L;

	public OtmsLocator() {
    }


    public OtmsLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public OtmsLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for otmsSoap
    private java.lang.String otmsSoap_address = "http://mymemory.translated.net/otms/";

    public java.lang.String getotmsSoapAddress() {
        return otmsSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String otmsSoapWSDDServiceName = "otmsSoap";

    public java.lang.String getotmsSoapWSDDServiceName() {
        return otmsSoapWSDDServiceName;
    }

    public void setotmsSoapWSDDServiceName(java.lang.String name) {
        otmsSoapWSDDServiceName = name;
    }

    public org.tempuri.OtmsSoap getotmsSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(otmsSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getotmsSoap(endpoint);
    }

    public org.tempuri.OtmsSoap getotmsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.OtmsSoapStub _stub = new org.tempuri.OtmsSoapStub(portAddress, this);
            _stub.setPortName(getotmsSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setotmsSoapEndpointAddress(java.lang.String address) {
        otmsSoap_address = address;
    }


    // Use to get a proxy class for otmsSoap12
    private java.lang.String otmsSoap12_address = "http://mymemory.translated.net/otms/";

    public java.lang.String getotmsSoap12Address() {
        return otmsSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String otmsSoap12WSDDServiceName = "otmsSoap12";

    public java.lang.String getotmsSoap12WSDDServiceName() {
        return otmsSoap12WSDDServiceName;
    }

    public void setotmsSoap12WSDDServiceName(java.lang.String name) {
        otmsSoap12WSDDServiceName = name;
    }

    public org.tempuri.OtmsSoap getotmsSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(otmsSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getotmsSoap12(endpoint);
    }

    public org.tempuri.OtmsSoap getotmsSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.tempuri.OtmsSoap12Stub _stub = new org.tempuri.OtmsSoap12Stub(portAddress, this);
            _stub.setPortName(getotmsSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setotmsSoap12EndpointAddress(java.lang.String address) {
        otmsSoap12_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    @SuppressWarnings("unchecked")
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.tempuri.OtmsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.OtmsSoapStub _stub = new org.tempuri.OtmsSoapStub(new java.net.URL(otmsSoap_address), this);
                _stub.setPortName(getotmsSoapWSDDServiceName());
                return _stub;
            }
            if (org.tempuri.OtmsSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                org.tempuri.OtmsSoap12Stub _stub = new org.tempuri.OtmsSoap12Stub(new java.net.URL(otmsSoap12_address), this);
                _stub.setPortName(getotmsSoap12WSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("unchecked")
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("otmsSoap".equals(inputPortName)) {
            return getotmsSoap();
        }
        else if ("otmsSoap12".equals(inputPortName)) {
            return getotmsSoap12();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "otms");
    }

    @SuppressWarnings("unchecked")
    private java.util.HashSet ports = null;

    @SuppressWarnings("unchecked")
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "otmsSoap"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "otmsSoap12"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("otmsSoap".equals(portName)) {
            setotmsSoapEndpointAddress(address);
        }
        else 
if ("otmsSoap12".equals(portName)) {
            setotmsSoap12EndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
