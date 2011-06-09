

/**
 * Vocab.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.4  Built on : Dec 19, 2010 (08:18:42 CET)
 */

    package fr.ifremer.nemo.wsVocab;

    /*
     *  Vocab java interface
     */

    public interface Vocab {
          

        /**
          * Auto generated method signature
          * 
                    * @param searchVocab0
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.SearchVocabResponse searchVocab(

                        fr.ifremer.nemo.wsVocab.vocab.SearchVocab searchVocab0)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param searchVocab0
            
          */
        public void startsearchVocab(

            fr.ifremer.nemo.wsVocab.vocab.SearchVocab searchVocab0,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param whatLists2
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.WhatListsResponse whatLists(

                        fr.ifremer.nemo.wsVocab.vocab.WhatLists whatLists2)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param whatLists2
            
          */
        public void startwhatLists(

            fr.ifremer.nemo.wsVocab.vocab.WhatLists whatLists2,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getList4
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.GetListResponse getList(

                        fr.ifremer.nemo.wsVocab.vocab.GetList getList4)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getList4
            
          */
        public void startgetList(

            fr.ifremer.nemo.wsVocab.vocab.GetList getList4,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param verifyTerm6
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.VerifyTermResponse verifyTerm(

                        fr.ifremer.nemo.wsVocab.vocab.VerifyTerm verifyTerm6)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param verifyTerm6
            
          */
        public void startverifyTerm(

            fr.ifremer.nemo.wsVocab.vocab.VerifyTerm verifyTerm6,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getMap8
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.GetMapResponse getMap(

                        fr.ifremer.nemo.wsVocab.vocab.GetMap getMap8)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getMap8
            
          */
        public void startgetMap(

            fr.ifremer.nemo.wsVocab.vocab.GetMap getMap8,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getRelatedRecordByCriteria10
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.GetRelatedRecordByCriteriaResponse getRelatedRecordByCriteria(

                        fr.ifremer.nemo.wsVocab.vocab.GetRelatedRecordByCriteria getRelatedRecordByCriteria10)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getRelatedRecordByCriteria10
            
          */
        public void startgetRelatedRecordByCriteria(

            fr.ifremer.nemo.wsVocab.vocab.GetRelatedRecordByCriteria getRelatedRecordByCriteria10,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param getRelatedRecordByTerm12
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.GetRelatedRecordByTermResponse getRelatedRecordByTerm(

                        fr.ifremer.nemo.wsVocab.vocab.GetRelatedRecordByTerm getRelatedRecordByTerm12)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param getRelatedRecordByTerm12
            
          */
        public void startgetRelatedRecordByTerm(

            fr.ifremer.nemo.wsVocab.vocab.GetRelatedRecordByTerm getRelatedRecordByTerm12,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        /**
          * Auto generated method signature
          * 
                    * @param whatListsCat14
                
         */

         
                     public fr.ifremer.nemo.wsVocab.vocab.WhatListsCatResponse whatListsCat(

                        fr.ifremer.nemo.wsVocab.vocab.WhatListsCat whatListsCat14)
                        throws java.rmi.RemoteException
             ;

        
         /**
            * Auto generated method signature for Asynchronous Invocations
            * 
                * @param whatListsCat14
            
          */
        public void startwhatListsCat(

            fr.ifremer.nemo.wsVocab.vocab.WhatListsCat whatListsCat14,

            final fr.ifremer.nemo.wsVocab.VocabCallbackHandler callback)

            throws java.rmi.RemoteException;

     

        
       //
       }
    