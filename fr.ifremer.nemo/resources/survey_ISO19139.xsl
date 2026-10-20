<?xml version="1.0"?>

<!-- Auteur: D.CAPITAINE                                                       -->
<!-- Version: 1.0                                                               -->
<!-- Date: 12/12/2013, Revision: 12/12/2013                                    -->
<!-- Description: conversion d un fichier XML de description de campagne        -->
<!--              provenant de l'ifremer en information campagne exploitable    -->



<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	xmlns:gmd="http://www.isotc211.org/2005/gmd"  
  	xmlns:gmi="http://www.isotc211.org/2005/gmi"  
  	xmlns:srv="http://www.isotc211.org/2005/srv"  
  	xmlns:gco="http://www.isotc211.org/2005/gco"  
  	xmlns:gts="http://www.isotc211.org/2005/gts"  
  	xmlns:gmx="http://www.isotc211.org/2005/gmx"  
  	xmlns:xlink="http://www.w3.org/1999/xlink"  
  	xmlns:sdn="http://www.seadatanet.org"  
  	xmlns:gml="http://www.opengis.net/gml"  
  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" cdata-section-elements="comment"/>
    
    <!-- on supprime tout les espaces -->
    <xsl:strip-space elements="*"/>
    
    <!-- point d entree sur fichier -->
    <xsl:template match="/">
        <!-- tag root -->
        <CruiseModel>
        	<!--reference campagne-->
            <reference><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle/gco:CharacterString"/></reference>
              <localCSRIdentifier><xsl:value-of select="gmi:MI_Metadata/gmd:fileIdentifier/gco:CharacterString"/></localCSRIdentifier>
            <!--nom campagne-->
            <name><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/></name>
        	
        	<!--Navire-->
			<xsl:for-each select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:descriptiveKeywords[gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'platform'][1]">
				<shipCode><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_PlatformCode/@codeListValue"/></shipCode>
	            <shipName><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_PlatformCode"/></shipName> 
            </xsl:for-each>  
              
            <!-- Region -->
            <xsl:for-each select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:descriptiveKeywords[gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'place'][1]">   
                <regionCode><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_WaterBodyCode/@codeListValue"/></regionCode>
                <regionName><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_WaterBodyCode"/></regionName> 
            </xsl:for-each> 
            
            <!-- project --> 
            <xsl:for-each select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:descriptiveKeywords[gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'project'][1]">   
                 <projectName>SDN:EDMERP::<xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_EDMERPCode/@codeListValue"/> (<xsl:value-of select="substring-before(substring-after(gmd:MD_Keywords/gmd:keyword/sdn:SDN_EDMERPCode,'{acronym=&quot;'), '&quot; organisation')"/>)</projectName> 
            </xsl:for-each>  

          <!-- edmerp --> 
           <!--  <xsl:for-each select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:descriptiveKeywords[gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'project']">   
                 <edmerpCodes><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_EDMERPCode/@codeListValue"/></edmerpCodes>
            </xsl:for-each>   -->
			
			<xsl:for-each select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword">   
                 <edmerpCodes><xsl:value-of select="sdn:SDN_EDMERPCode/@codeListValue"/></edmerpCodes>
            </xsl:for-each>	
            
            <!-- Date de debut -->
        	<formattedStartDate><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition"/></formattedStartDate>
            <!-- Date de fin -->
            <formattedEndDate><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition"/></formattedEndDate>
			
			
			<!-- Pays -->
			<countryName><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/sdn:SDN_CountryCode"/></countryName>
			
			<!-- Chef de mission -->
			<chiefScientist><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:individualName/gco:CharacterString"/></chiefScientist>
			
			<!-- Laboratoire  -->
			<laboratories><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:organisationName/sdn:SDN_EDMOCode"/></laboratories>
	    
	    	
	    	<!-- Availability -->
	    	<xsl:choose>
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'CC')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'FE')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'LI')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'MO')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'NA')">
                        <availability>confidential</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'NC')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'OG')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'RS')">
                        <availability>confidential</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'SR')">
                        <availability>publicly</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'UK')">
                        <availability>limited</availability>
                    </xsl:when> 
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href  ,'UN')">
                        <availability>publicly</availability>
                    </xsl:when>
                    
                    <xsl:otherwise>
                        <availability>restricted</availability>
                    </xsl:otherwise>
                </xsl:choose>
	    	
			<!-- Data Centre not use load from config-->
        	<dataCentre/>
        
			<!-- Data Type -->
        	<dataTypes>
        		<xsl:for-each select="gmi:MI_Metadata/gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:objective">
                	<xsl:if test="sdn:SDN_Objective/gmi:type/gmi:MI_ObjectiveTypeCode/@codeListValue = 'instantaneousCollection'">
	                	<DataType>
	                	<xsl:variable name="dataCategoryCode" select="sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue" />
	                        	
	                    	<xsl:attribute name="code">
	                        	<xsl:value-of select="sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue"/> - <xsl:value-of select="sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode"/>
	                        </xsl:attribute>
	                        
	                       	<xsl:choose>
         						<xsl:when test="sdn:SDN_Objective/sdn:SDN_SamplingActivity/sdn:quantity">
	                        		<profiles><xsl:value-of select="sdn:SDN_Objective/sdn:SDN_SamplingActivity/sdn:quantity"/></profiles>
	                        	</xsl:when>
        						<xsl:otherwise>
        							<profiles></profiles>
        						</xsl:otherwise>
        					</xsl:choose>
	                    </DataType>
                   	</xsl:if>
                </xsl:for-each>
            </dataTypes>
            
        </CruiseModel>
    </xsl:template>
    
</xsl:stylesheet>
