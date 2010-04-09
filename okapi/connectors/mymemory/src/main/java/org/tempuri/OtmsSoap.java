/**
 * OtmsSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface OtmsSoap extends java.rmi.Remote {
    public org.tempuri.GetResponse otmsGet(java.lang.String key, org.tempuri.Query q) throws java.rmi.RemoteException;
    public org.tempuri.InsertResponse otmsInsert(java.lang.String key, org.tempuri.Insert i) throws java.rmi.RemoteException;
}
