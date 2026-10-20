/**
 * Edmo_webserviceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ns_ws_edmerp;

public interface Edmerp_webserviceSoap extends java.rmi.Remote {
    public java.lang.String get_detail(int n_code) throws java.rmi.RemoteException;
    public java.lang.String get_list() throws java.rmi.RemoteException;
    public java.lang.String get_list_collator(int collator_id)  throws java.rmi.RemoteException;
    public java.lang.String get_simple_list() throws java.rmi.RemoteException;

}
