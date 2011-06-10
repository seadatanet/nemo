
/**
 * ExtensionMapper.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.4  Built on : Dec 19, 2010 (08:19:26 CET)
 */

        
            package fr.ifremer.nemo.wsVocab.vocabtoto;
        
            /**
            *  ExtensionMapper class
            */
        
        public  class ExtensionMapper{

          public static java.lang.Object getTypeObject(java.lang.String namespaceURI,
                                                       java.lang.String typeName,
                                                       javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "verifyTermResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.VerifyTermResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "codeTableRecordType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.CodeTableRecordType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "searchVocabType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.SearchVocabType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "verifyTermType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.VerifyTermType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getRelatedRecordByTermResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetRelatedRecordByTermResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getRelatedRecordByTermType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetRelatedRecordByTermType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getListType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetListType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "whatListsResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.WhatListsResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getRelatedRecordByCriteriaType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetRelatedRecordByCriteriaType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getRelatedRecordByCriteriaResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetRelatedRecordByCriteriaResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "whatListsCatType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.WhatListsCatType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getMapType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetMapType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "whatListsType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.WhatListsType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "whatListsCatResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.WhatListsCatResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getMapResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetMapResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "searchVocabResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.SearchVocabResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "getListResponseType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.GetListResponseType.Factory.parse(reader);
                        

                  }

              
                  if (
                  "urn:vocab/types".equals(namespaceURI) &&
                  "codeTableTypeType".equals(typeName)){
                   
                            return  fr.ifremer.nemo.wsVocab.vocabtoto.CodeTableTypeType.Factory.parse(reader);
                        

                  }

              
             throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
          }

        }
    