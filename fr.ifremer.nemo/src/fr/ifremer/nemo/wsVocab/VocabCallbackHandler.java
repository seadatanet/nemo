
/**
 * VocabCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.4  Built on : Dec 19, 2010 (08:18:42 CET)
 */

    package fr.ifremer.nemo.wsVocab;

    /**
     *  VocabCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class VocabCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public VocabCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public VocabCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for searchVocab method
            * override this method for handling normal response from searchVocab operation
            */
           public void receiveResultsearchVocab(
                    fr.ifremer.nemo.wsVocab.vocabtoto.SearchVocabResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from searchVocab operation
           */
            public void receiveErrorsearchVocab(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for whatLists method
            * override this method for handling normal response from whatLists operation
            */
           public void receiveResultwhatLists(
                    fr.ifremer.nemo.wsVocab.vocabtoto.WhatListsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from whatLists operation
           */
            public void receiveErrorwhatLists(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getList method
            * override this method for handling normal response from getList operation
            */
           public void receiveResultgetList(
                    fr.ifremer.nemo.wsVocab.vocabtoto.GetListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getList operation
           */
            public void receiveErrorgetList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for verifyTerm method
            * override this method for handling normal response from verifyTerm operation
            */
           public void receiveResultverifyTerm(
                    fr.ifremer.nemo.wsVocab.vocabtoto.VerifyTermResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from verifyTerm operation
           */
            public void receiveErrorverifyTerm(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMap method
            * override this method for handling normal response from getMap operation
            */
           public void receiveResultgetMap(
                    fr.ifremer.nemo.wsVocab.vocabtoto.GetMapResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMap operation
           */
            public void receiveErrorgetMap(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getRelatedRecordByCriteria method
            * override this method for handling normal response from getRelatedRecordByCriteria operation
            */
           public void receiveResultgetRelatedRecordByCriteria(
                    fr.ifremer.nemo.wsVocab.vocabtoto.GetRelatedRecordByCriteriaResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getRelatedRecordByCriteria operation
           */
            public void receiveErrorgetRelatedRecordByCriteria(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getRelatedRecordByTerm method
            * override this method for handling normal response from getRelatedRecordByTerm operation
            */
           public void receiveResultgetRelatedRecordByTerm(
                    fr.ifremer.nemo.wsVocab.vocabtoto.GetRelatedRecordByTermResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getRelatedRecordByTerm operation
           */
            public void receiveErrorgetRelatedRecordByTerm(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for whatListsCat method
            * override this method for handling normal response from whatListsCat operation
            */
           public void receiveResultwhatListsCat(
                    fr.ifremer.nemo.wsVocab.vocabtoto.WhatListsCatResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from whatListsCat operation
           */
            public void receiveErrorwhatListsCat(java.lang.Exception e) {
            }
                


    }
    