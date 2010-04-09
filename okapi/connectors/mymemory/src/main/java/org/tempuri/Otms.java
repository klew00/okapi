/**
 * Otms.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface Otms extends javax.xml.rpc.Service {
    public java.lang.String getotmsSoapAddress();

    public org.tempuri.OtmsSoap getotmsSoap() throws javax.xml.rpc.ServiceException;

    public org.tempuri.OtmsSoap getotmsSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getotmsSoap12Address();

    public org.tempuri.OtmsSoap getotmsSoap12() throws javax.xml.rpc.ServiceException;

    public org.tempuri.OtmsSoap getotmsSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
