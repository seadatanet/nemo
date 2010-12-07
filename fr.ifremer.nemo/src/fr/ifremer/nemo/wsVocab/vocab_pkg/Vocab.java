/**
 * Vocab.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package fr.ifremer.nemo.wsVocab.vocab_pkg;

public interface Vocab extends javax.xml.rpc.Service {
    public java.lang.String getvocabPortAddress();

    public fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType getvocabPort() throws javax.xml.rpc.ServiceException;

    public fr.ifremer.nemo.wsVocab.vocab_pkg.VocabPortType getvocabPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
