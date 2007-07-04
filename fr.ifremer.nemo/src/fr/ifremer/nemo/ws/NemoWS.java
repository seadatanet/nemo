/**
 * NemoWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.ws;

public interface NemoWS extends java.rmi.Remote {
    public fr.ifremer.nemo.ws.WSParameter[] getParameters() throws java.rmi.RemoteException;
    public fr.ifremer.nemo.ws.WSCountry[] getCountries() throws java.rmi.RemoteException;
    public fr.ifremer.nemo.ws.WSDataCentre[] getDataCentres() throws java.rmi.RemoteException;
    public fr.ifremer.nemo.ws.WSDataType[] getDataTypes() throws java.rmi.RemoteException;
    public fr.ifremer.nemo.ws.WSRegion[] getRegions() throws java.rmi.RemoteException;
    public fr.ifremer.nemo.ws.WSShip[] getShips() throws java.rmi.RemoteException;
    public fr.ifremer.nemo.ws.WSSurveyDescription[] getSurveyList() throws java.rmi.RemoteException;
}
