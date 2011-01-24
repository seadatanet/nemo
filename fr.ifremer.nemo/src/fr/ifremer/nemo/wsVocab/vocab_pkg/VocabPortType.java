/**
 * VocabPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.wsVocab.vocab_pkg;

public interface VocabPortType extends java.rmi.Remote {
    public void whatLists(java.lang.String categoryKey, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableTypeTypeArrayHolder codeTableType, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void getList(java.lang.String[] recordKey, java.util.Calendar earliestRecord, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void verifyTerm(java.lang.String listKey, java.lang.String entryTerm, java.lang.String termType, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeHolder verifiedTerm, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void searchVocab(java.lang.String listKey, java.lang.String searchTerm, java.lang.String termType, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void getMap(java.lang.String[] subjectList, int predicate, java.lang.String[] objectList, java.lang.String inference, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void getRelatedRecordByCriteria(java.lang.String[] subjectList, java.lang.String subjectField, java.lang.String subjectText, java.lang.Boolean caseSensitivity, java.lang.Boolean partialMatch, int predicate, java.lang.String[] objectList, java.lang.String inferences, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void getRelatedRecordByTerm(java.lang.String[] subjectTerm, int predicate, java.lang.String[] objectList, java.lang.String inferences, fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
    public void whatListsCat(fr.ifremer.nemo.wsVocab.vocab.holders.CodeTableRecordTypeArrayHolder codeTableRecord, javax.xml.rpc.holders.BooleanHolder error) throws java.rmi.RemoteException;
}
