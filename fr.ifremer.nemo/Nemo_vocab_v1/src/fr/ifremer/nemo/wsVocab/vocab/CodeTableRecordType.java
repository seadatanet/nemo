
/**
 * CodeTableRecordType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.4  Built on : Dec 19, 2010 (08:19:26 CET)
 */
            
                package fr.ifremer.nemo.wsVocab.vocab;
            

            /**
            *  CodeTableRecordType bean class
            */
        
        public  class CodeTableRecordType
        implements org.apache.axis2.databinding.ADBBean{
        /* This type was generated from the piece of schema that had
                name = codeTableRecordType
                Namespace URI = urn:vocab/types
                Namespace Prefix = ns1
                */
            

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if(namespace.equals("urn:vocab/types")){
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        

                        /**
                        * field for ListKey
                        */

                        
                                    protected java.lang.String localListKey ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localListKeyTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getListKey(){
                               return localListKey;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param ListKey
                               */
                               public void setListKey(java.lang.String param){
                            
                                       if (param != null){
                                          //update the setting tracker
                                          localListKeyTracker = true;
                                       } else {
                                          localListKeyTracker = false;
                                              
                                       }
                                   
                                            this.localListKey=param;
                                    

                               }
                            

                        /**
                        * field for EntryKey
                        */

                        
                                    protected java.lang.String localEntryKey ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getEntryKey(){
                               return localEntryKey;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EntryKey
                               */
                               public void setEntryKey(java.lang.String param){
                            
                                            this.localEntryKey=param;
                                    

                               }
                            

                        /**
                        * field for EntryTerm
                        */

                        
                                    protected java.lang.String localEntryTerm ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getEntryTerm(){
                               return localEntryTerm;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EntryTerm
                               */
                               public void setEntryTerm(java.lang.String param){
                            
                                            this.localEntryTerm=param;
                                    

                               }
                            

                        /**
                        * field for EntryTermAbbr
                        */

                        
                                    protected java.lang.String localEntryTermAbbr ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getEntryTermAbbr(){
                               return localEntryTermAbbr;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EntryTermAbbr
                               */
                               public void setEntryTermAbbr(java.lang.String param){
                            
                                            this.localEntryTermAbbr=param;
                                    

                               }
                            

                        /**
                        * field for EntryTermDef
                        */

                        
                                    protected java.lang.String localEntryTermDef ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.lang.String
                           */
                           public  java.lang.String getEntryTermDef(){
                               return localEntryTermDef;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EntryTermDef
                               */
                               public void setEntryTermDef(java.lang.String param){
                            
                                            this.localEntryTermDef=param;
                                    

                               }
                            

                        /**
                        * field for EntryTermLastMod
                        */

                        
                                    protected java.util.Calendar localEntryTermLastMod ;
                                

                           /**
                           * Auto generated getter method
                           * @return java.util.Calendar
                           */
                           public  java.util.Calendar getEntryTermLastMod(){
                               return localEntryTermLastMod;
                           }

                           
                        
                            /**
                               * Auto generated setter method
                               * @param param EntryTermLastMod
                               */
                               public void setEntryTermLastMod(java.util.Calendar param){
                            
                                            this.localEntryTermLastMod=param;
                                    

                               }
                            

                        /**
                        * field for ExactMatch
                        * This was an Array!
                        */

                        
                                    protected fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] localExactMatch ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localExactMatchTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return vocab.CodeTableRecordType[]
                           */
                           public  fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getExactMatch(){
                               return localExactMatch;
                           }

                           
                        


                               
                              /**
                               * validate the array for ExactMatch
                               */
                              protected void validateExactMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param ExactMatch
                              */
                              public void setExactMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                              
                                   validateExactMatch(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localExactMatchTracker = true;
                                          } else {
                                             localExactMatchTracker = false;
                                                 
                                          }
                                      
                                      this.localExactMatch=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param vocab.CodeTableRecordType
                             */
                             public void addExactMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType param){
                                   if (localExactMatch == null){
                                   localExactMatch = new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]{};
                                   }

                            
                                 //update the setting tracker
                                localExactMatchTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localExactMatch);
                               list.add(param);
                               this.localExactMatch =
                             (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])list.toArray(
                            new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[list.size()]);

                             }
                             

                        /**
                        * field for NarrowMatch
                        * This was an Array!
                        */

                        
                                    protected fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] localNarrowMatch ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localNarrowMatchTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return vocab.CodeTableRecordType[]
                           */
                           public  fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getNarrowMatch(){
                               return localNarrowMatch;
                           }

                           
                        


                               
                              /**
                               * validate the array for NarrowMatch
                               */
                              protected void validateNarrowMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param NarrowMatch
                              */
                              public void setNarrowMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                              
                                   validateNarrowMatch(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localNarrowMatchTracker = true;
                                          } else {
                                             localNarrowMatchTracker = false;
                                                 
                                          }
                                      
                                      this.localNarrowMatch=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param vocab.CodeTableRecordType
                             */
                             public void addNarrowMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType param){
                                   if (localNarrowMatch == null){
                                   localNarrowMatch = new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]{};
                                   }

                            
                                 //update the setting tracker
                                localNarrowMatchTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localNarrowMatch);
                               list.add(param);
                               this.localNarrowMatch =
                             (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])list.toArray(
                            new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[list.size()]);

                             }
                             

                        /**
                        * field for BroadMatch
                        * This was an Array!
                        */

                        
                                    protected fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] localBroadMatch ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localBroadMatchTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return vocab.CodeTableRecordType[]
                           */
                           public  fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getBroadMatch(){
                               return localBroadMatch;
                           }

                           
                        


                               
                              /**
                               * validate the array for BroadMatch
                               */
                              protected void validateBroadMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param BroadMatch
                              */
                              public void setBroadMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                              
                                   validateBroadMatch(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localBroadMatchTracker = true;
                                          } else {
                                             localBroadMatchTracker = false;
                                                 
                                          }
                                      
                                      this.localBroadMatch=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param vocab.CodeTableRecordType
                             */
                             public void addBroadMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType param){
                                   if (localBroadMatch == null){
                                   localBroadMatch = new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]{};
                                   }

                            
                                 //update the setting tracker
                                localBroadMatchTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localBroadMatch);
                               list.add(param);
                               this.localBroadMatch =
                             (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])list.toArray(
                            new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[list.size()]);

                             }
                             

                        /**
                        * field for MinorMatch
                        * This was an Array!
                        */

                        
                                    protected fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] localMinorMatch ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMinorMatchTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return vocab.CodeTableRecordType[]
                           */
                           public  fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getMinorMatch(){
                               return localMinorMatch;
                           }

                           
                        


                               
                              /**
                               * validate the array for MinorMatch
                               */
                              protected void validateMinorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param MinorMatch
                              */
                              public void setMinorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                              
                                   validateMinorMatch(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localMinorMatchTracker = true;
                                          } else {
                                             localMinorMatchTracker = false;
                                                 
                                          }
                                      
                                      this.localMinorMatch=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param vocab.CodeTableRecordType
                             */
                             public void addMinorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType param){
                                   if (localMinorMatch == null){
                                   localMinorMatch = new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]{};
                                   }

                            
                                 //update the setting tracker
                                localMinorMatchTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localMinorMatch);
                               list.add(param);
                               this.localMinorMatch =
                             (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])list.toArray(
                            new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[list.size()]);

                             }
                             

                        /**
                        * field for MajorMatch
                        * This was an Array!
                        */

                        
                                    protected fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] localMajorMatch ;
                                
                           /*  This tracker boolean wil be used to detect whether the user called the set method
                          *   for this attribute. It will be used to determine whether to include this field
                           *   in the serialized XML
                           */
                           protected boolean localMajorMatchTracker = false ;
                           

                           /**
                           * Auto generated getter method
                           * @return vocab.CodeTableRecordType[]
                           */
                           public  fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] getMajorMatch(){
                               return localMajorMatch;
                           }

                           
                        


                               
                              /**
                               * validate the array for MajorMatch
                               */
                              protected void validateMajorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                             
                              }


                             /**
                              * Auto generated setter method
                              * @param param MajorMatch
                              */
                              public void setMajorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[] param){
                              
                                   validateMajorMatch(param);

                               
                                          if (param != null){
                                             //update the setting tracker
                                             localMajorMatchTracker = true;
                                          } else {
                                             localMajorMatchTracker = false;
                                                 
                                          }
                                      
                                      this.localMajorMatch=param;
                              }

                               
                             
                             /**
                             * Auto generated add method for the array for convenience
                             * @param param vocab.CodeTableRecordType
                             */
                             public void addMajorMatch(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType param){
                                   if (localMajorMatch == null){
                                   localMajorMatch = new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[]{};
                                   }

                            
                                 //update the setting tracker
                                localMajorMatchTracker = true;
                            

                               java.util.List list =
                            org.apache.axis2.databinding.utils.ConverterUtil.toList(localMajorMatch);
                               list.add(param);
                               this.localMajorMatch =
                             (fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])list.toArray(
                            new fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[list.size()]);

                             }
                             

     /**
     * isReaderMTOMAware
     * @return true if the reader supports MTOM
     */
   public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
        boolean isReaderMTOMAware = false;
        
        try{
          isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader.getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
        }catch(java.lang.IllegalArgumentException e){
          isReaderMTOMAware = false;
        }
        return isReaderMTOMAware;
   }
     
     
        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
       public org.apache.axiom.om.OMElement getOMElement (
               final javax.xml.namespace.QName parentQName,
               final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException{


        
               org.apache.axiom.om.OMDataSource dataSource =
                       new org.apache.axis2.databinding.ADBDataSource(this,parentQName){

                 public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
                       CodeTableRecordType.this.serialize(parentQName,factory,xmlWriter);
                 }
               };
               return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(
               parentQName,factory,dataSource);
            
       }

         public void serialize(final javax.xml.namespace.QName parentQName,
                                       final org.apache.axiom.om.OMFactory factory,
                                       org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
                           serialize(parentQName,factory,xmlWriter,false);
         }

         public void serialize(final javax.xml.namespace.QName parentQName,
                               final org.apache.axiom.om.OMFactory factory,
                               org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter,
                               boolean serializeType)
            throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException{
            
                


                java.lang.String prefix = null;
                java.lang.String namespace = null;
                

                    prefix = parentQName.getPrefix();
                    namespace = parentQName.getNamespaceURI();

                    if ((namespace != null) && (namespace.trim().length() > 0)) {
                        java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                        if (writerPrefix != null) {
                            xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                        } else {
                            if (prefix == null) {
                                prefix = generatePrefix(namespace);
                            }

                            xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                            xmlWriter.writeNamespace(prefix, namespace);
                            xmlWriter.setPrefix(prefix, namespace);
                        }
                    } else {
                        xmlWriter.writeStartElement(parentQName.getLocalPart());
                    }
                
                  if (serializeType){
               

                   java.lang.String namespacePrefix = registerPrefix(xmlWriter,"urn:vocab/types");
                   if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)){
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           namespacePrefix+":codeTableRecordType",
                           xmlWriter);
                   } else {
                       writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","type",
                           "codeTableRecordType",
                           xmlWriter);
                   }

               
                   }
                if (localListKeyTracker){
                                    namespace = "urn:vocab/types";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"listKey", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"listKey");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("listKey");
                                    }
                                

                                          if (localListKey==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("listKey cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localListKey);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             }
                                    namespace = "urn:vocab/types";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"entryKey", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"entryKey");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("entryKey");
                                    }
                                

                                          if (localEntryKey==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("entryKey cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localEntryKey);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "urn:vocab/types";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"entryTerm", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"entryTerm");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("entryTerm");
                                    }
                                

                                          if (localEntryTerm==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("entryTerm cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localEntryTerm);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "urn:vocab/types";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"entryTermAbbr", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"entryTermAbbr");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("entryTermAbbr");
                                    }
                                

                                          if (localEntryTermAbbr==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localEntryTermAbbr);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "urn:vocab/types";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"entryTermDef", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"entryTermDef");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("entryTermDef");
                                    }
                                

                                          if (localEntryTermDef==null){
                                              // write the nil attribute
                                              
                                                     writeAttribute("xsi","http://www.w3.org/2001/XMLSchema-instance","nil","1",xmlWriter);
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(localEntryTermDef);
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                             
                                    namespace = "urn:vocab/types";
                                    if (! namespace.equals("")) {
                                        prefix = xmlWriter.getPrefix(namespace);

                                        if (prefix == null) {
                                            prefix = generatePrefix(namespace);

                                            xmlWriter.writeStartElement(prefix,"entryTermLastMod", namespace);
                                            xmlWriter.writeNamespace(prefix, namespace);
                                            xmlWriter.setPrefix(prefix, namespace);

                                        } else {
                                            xmlWriter.writeStartElement(namespace,"entryTermLastMod");
                                        }

                                    } else {
                                        xmlWriter.writeStartElement("entryTermLastMod");
                                    }
                                

                                          if (localEntryTermLastMod==null){
                                              // write the nil attribute
                                              
                                                     throw new org.apache.axis2.databinding.ADBException("entryTermLastMod cannot be null!!");
                                                  
                                          }else{

                                        
                                                   xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEntryTermLastMod));
                                            
                                          }
                                    
                                   xmlWriter.writeEndElement();
                              if (localExactMatchTracker){
                                       if (localExactMatch!=null){
                                            for (int i = 0;i < localExactMatch.length;i++){
                                                if (localExactMatch[i] != null){
                                                 localExactMatch[i].serialize(new javax.xml.namespace.QName("urn:vocab/types","exactMatch"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("exactMatch cannot be null!!");
                                        
                                    }
                                 } if (localNarrowMatchTracker){
                                       if (localNarrowMatch!=null){
                                            for (int i = 0;i < localNarrowMatch.length;i++){
                                                if (localNarrowMatch[i] != null){
                                                 localNarrowMatch[i].serialize(new javax.xml.namespace.QName("urn:vocab/types","narrowMatch"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("narrowMatch cannot be null!!");
                                        
                                    }
                                 } if (localBroadMatchTracker){
                                       if (localBroadMatch!=null){
                                            for (int i = 0;i < localBroadMatch.length;i++){
                                                if (localBroadMatch[i] != null){
                                                 localBroadMatch[i].serialize(new javax.xml.namespace.QName("urn:vocab/types","broadMatch"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("broadMatch cannot be null!!");
                                        
                                    }
                                 } if (localMinorMatchTracker){
                                       if (localMinorMatch!=null){
                                            for (int i = 0;i < localMinorMatch.length;i++){
                                                if (localMinorMatch[i] != null){
                                                 localMinorMatch[i].serialize(new javax.xml.namespace.QName("urn:vocab/types","minorMatch"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("minorMatch cannot be null!!");
                                        
                                    }
                                 } if (localMajorMatchTracker){
                                       if (localMajorMatch!=null){
                                            for (int i = 0;i < localMajorMatch.length;i++){
                                                if (localMajorMatch[i] != null){
                                                 localMajorMatch[i].serialize(new javax.xml.namespace.QName("urn:vocab/types","majorMatch"),
                                                           factory,xmlWriter);
                                                } else {
                                                   
                                                        // we don't have to do any thing since minOccures is zero
                                                    
                                                }

                                            }
                                     } else {
                                        
                                               throw new org.apache.axis2.databinding.ADBException("majorMatch cannot be null!!");
                                        
                                    }
                                 }
                    xmlWriter.writeEndElement();
               

        }

         /**
          * Util method to write an attribute with the ns prefix
          */
          private void writeAttribute(java.lang.String prefix,java.lang.String namespace,java.lang.String attName,
                                      java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
              if (xmlWriter.getPrefix(namespace) == null) {
                       xmlWriter.writeNamespace(prefix, namespace);
                       xmlWriter.setPrefix(prefix, namespace);

              }

              xmlWriter.writeAttribute(namespace,attName,attValue);

         }

        /**
          * Util method to write an attribute without the ns prefix
          */
          private void writeAttribute(java.lang.String namespace,java.lang.String attName,
                                      java.lang.String attValue,javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException{
                if (namespace.equals(""))
              {
                  xmlWriter.writeAttribute(attName,attValue);
              }
              else
              {
                  registerPrefix(xmlWriter, namespace);
                  xmlWriter.writeAttribute(namespace,attName,attValue);
              }
          }


           /**
             * Util method to write an attribute without the ns prefix
             */
            private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                                             javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

                java.lang.String attributeNamespace = qname.getNamespaceURI();
                java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
                if (attributePrefix == null) {
                    attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
                }
                java.lang.String attributeValue;
                if (attributePrefix.trim().length() > 0) {
                    attributeValue = attributePrefix + ":" + qname.getLocalPart();
                } else {
                    attributeValue = qname.getLocalPart();
                }

                if (namespace.equals("")) {
                    xmlWriter.writeAttribute(attName, attributeValue);
                } else {
                    registerPrefix(xmlWriter, namespace);
                    xmlWriter.writeAttribute(namespace, attName, attributeValue);
                }
            }
        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname,
                                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix,namespaceURI);
                }

                if (prefix.trim().length() > 0){
                    xmlWriter.writeCharacters(prefix + ":" + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                } else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            } else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames,
                                 javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix,namespaceURI);
                        }

                        if (prefix.trim().length() > 0){
                            stringToWrite.append(prefix).append(":").append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        } else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                    } else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }


         /**
         * Register a namespace prefix
         */
         private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace) throws javax.xml.stream.XMLStreamException {
                java.lang.String prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = generatePrefix(namespace);

                    while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                        prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                    }

                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }

                return prefix;
            }


  
        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                    throws org.apache.axis2.databinding.ADBException{


        
                 java.util.ArrayList elementList = new java.util.ArrayList();
                 java.util.ArrayList attribList = new java.util.ArrayList();

                 if (localListKeyTracker){
                                      elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                      "listKey"));
                                 
                                        if (localListKey != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localListKey));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("listKey cannot be null!!");
                                        }
                                    }
                                      elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                      "entryKey"));
                                 
                                        if (localEntryKey != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEntryKey));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("entryKey cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                      "entryTerm"));
                                 
                                        if (localEntryTerm != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEntryTerm));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("entryTerm cannot be null!!");
                                        }
                                    
                                      elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                      "entryTermAbbr"));
                                 
                                         elementList.add(localEntryTermAbbr==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEntryTermAbbr));
                                    
                                      elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                      "entryTermDef"));
                                 
                                         elementList.add(localEntryTermDef==null?null:
                                         org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEntryTermDef));
                                    
                                      elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                      "entryTermLastMod"));
                                 
                                        if (localEntryTermLastMod != null){
                                            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localEntryTermLastMod));
                                        } else {
                                           throw new org.apache.axis2.databinding.ADBException("entryTermLastMod cannot be null!!");
                                        }
                                     if (localExactMatchTracker){
                             if (localExactMatch!=null) {
                                 for (int i = 0;i < localExactMatch.length;i++){

                                    if (localExactMatch[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                          "exactMatch"));
                                         elementList.add(localExactMatch[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("exactMatch cannot be null!!");
                                    
                             }

                        } if (localNarrowMatchTracker){
                             if (localNarrowMatch!=null) {
                                 for (int i = 0;i < localNarrowMatch.length;i++){

                                    if (localNarrowMatch[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                          "narrowMatch"));
                                         elementList.add(localNarrowMatch[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("narrowMatch cannot be null!!");
                                    
                             }

                        } if (localBroadMatchTracker){
                             if (localBroadMatch!=null) {
                                 for (int i = 0;i < localBroadMatch.length;i++){

                                    if (localBroadMatch[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                          "broadMatch"));
                                         elementList.add(localBroadMatch[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("broadMatch cannot be null!!");
                                    
                             }

                        } if (localMinorMatchTracker){
                             if (localMinorMatch!=null) {
                                 for (int i = 0;i < localMinorMatch.length;i++){

                                    if (localMinorMatch[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                          "minorMatch"));
                                         elementList.add(localMinorMatch[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("minorMatch cannot be null!!");
                                    
                             }

                        } if (localMajorMatchTracker){
                             if (localMajorMatch!=null) {
                                 for (int i = 0;i < localMajorMatch.length;i++){

                                    if (localMajorMatch[i] != null){
                                         elementList.add(new javax.xml.namespace.QName("urn:vocab/types",
                                                                          "majorMatch"));
                                         elementList.add(localMajorMatch[i]);
                                    } else {
                                        
                                                // nothing to do
                                            
                                    }

                                 }
                             } else {
                                 
                                        throw new org.apache.axis2.databinding.ADBException("majorMatch cannot be null!!");
                                    
                             }

                        }

                return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(), attribList.toArray());
            
            

        }

  

     /**
      *  Factory class that keeps the parse method
      */
    public static class Factory{

        
        

        /**
        * static method to create the object
        * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
        *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
        * Postcondition: If this object is an element, the reader is positioned at its end element
        *                If this object is a complex type, the reader is positioned at the end element of its outer element
        */
        public static CodeTableRecordType parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception{
            CodeTableRecordType object =
                new CodeTableRecordType();

            int event;
            java.lang.String nillableValue = null;
            java.lang.String prefix ="";
            java.lang.String namespaceuri ="";
            try {
                
                while (!reader.isStartElement() && !reader.isEndElement())
                    reader.next();

                
                if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","type")!=null){
                  java.lang.String fullTypeName = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance",
                        "type");
                  if (fullTypeName!=null){
                    java.lang.String nsPrefix = null;
                    if (fullTypeName.indexOf(":") > -1){
                        nsPrefix = fullTypeName.substring(0,fullTypeName.indexOf(":"));
                    }
                    nsPrefix = nsPrefix==null?"":nsPrefix;

                    java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":")+1);
                    
                            if (!"codeTableRecordType".equals(type)){
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (CodeTableRecordType)fr.ifremer.nemo.wsVocab.vocab.ExtensionMapper.getTypeObject(
                                     nsUri,type,reader);
                              }
                        

                  }
                

                }

                

                
                // Note all attributes that were handled. Used to differ normal attributes
                // from anyAttributes.
                java.util.Vector handledAttributes = new java.util.Vector();
                

                 
                    
                    reader.next();
                
                        java.util.ArrayList list7 = new java.util.ArrayList();
                    
                        java.util.ArrayList list8 = new java.util.ArrayList();
                    
                        java.util.ArrayList list9 = new java.util.ArrayList();
                    
                        java.util.ArrayList list10 = new java.util.ArrayList();
                    
                        java.util.ArrayList list11 = new java.util.ArrayList();
                    
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","listKey").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setListKey(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","entryKey").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEntryKey(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","entryTerm").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEntryTerm(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","entryTermAbbr").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEntryTermAbbr(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","entryTermDef").equals(reader.getName())){
                                
                                       nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance","nil");
                                       if (!"true".equals(nillableValue) && !"1".equals(nillableValue)){
                                    
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEntryTermDef(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));
                                            
                                       } else {
                                           
                                           
                                           reader.getElementText(); // throw away text nodes if any.
                                       }
                                      
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","entryTermLastMod").equals(reader.getName())){
                                
                                    java.lang.String content = reader.getElementText();
                                    
                                              object.setEntryTermLastMod(
                                                    org.apache.axis2.databinding.utils.ConverterUtil.convertToDateTime(content));
                                              
                                        reader.next();
                                    
                              }  // End of if for expected property start element
                                
                                else{
                                    // A start element we are not expecting indicates an invalid parameter was passed
                                    throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                                }
                            
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","exactMatch").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list7.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone7 = false;
                                                        while(!loopDone7){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone7 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("urn:vocab/types","exactMatch").equals(reader.getName())){
                                                                    list7.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone7 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setExactMatch((fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class,
                                                                list7));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","narrowMatch").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list8.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone8 = false;
                                                        while(!loopDone8){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone8 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("urn:vocab/types","narrowMatch").equals(reader.getName())){
                                                                    list8.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone8 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setNarrowMatch((fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class,
                                                                list8));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","broadMatch").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list9.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone9 = false;
                                                        while(!loopDone9){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone9 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("urn:vocab/types","broadMatch").equals(reader.getName())){
                                                                    list9.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone9 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setBroadMatch((fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class,
                                                                list9));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","minorMatch").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list10.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone10 = false;
                                                        while(!loopDone10){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone10 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("urn:vocab/types","minorMatch").equals(reader.getName())){
                                                                    list10.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone10 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setMinorMatch((fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class,
                                                                list10));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                
                                    
                                    while (!reader.isStartElement() && !reader.isEndElement()) reader.next();
                                
                                    if (reader.isStartElement() && new javax.xml.namespace.QName("urn:vocab/types","majorMatch").equals(reader.getName())){
                                
                                    
                                    
                                    // Process the array and step past its final element's end.
                                    list11.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                
                                                        //loop until we find a start element that is not part of this array
                                                        boolean loopDone11 = false;
                                                        while(!loopDone11){
                                                            // We should be at the end element, but make sure
                                                            while (!reader.isEndElement())
                                                                reader.next();
                                                            // Step out of this element
                                                            reader.next();
                                                            // Step to next element event.
                                                            while (!reader.isStartElement() && !reader.isEndElement())
                                                                reader.next();
                                                            if (reader.isEndElement()){
                                                                //two continuous end elements means we are exiting the xml structure
                                                                loopDone11 = true;
                                                            } else {
                                                                if (new javax.xml.namespace.QName("urn:vocab/types","majorMatch").equals(reader.getName())){
                                                                    list11.add(fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.Factory.parse(reader));
                                                                        
                                                                }else{
                                                                    loopDone11 = true;
                                                                }
                                                            }
                                                        }
                                                        // call the converter utility  to convert and set the array
                                                        
                                                        object.setMajorMatch((fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType[])
                                                            org.apache.axis2.databinding.utils.ConverterUtil.convertToArray(
                                                                fr.ifremer.nemo.wsVocab.vocab.CodeTableRecordType.class,
                                                                list11));
                                                            
                              }  // End of if for expected property start element
                                
                                    else {
                                        
                                    }
                                  
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            
                                if (reader.isStartElement())
                                // A start element we are not expecting indicates a trailing invalid property
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement " + reader.getLocalName());
                            



            } catch (javax.xml.stream.XMLStreamException e) {
                throw new java.lang.Exception(e);
            }

            return object;
        }

        }//end of factory class

        

        }
           
          