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
            
            <!--nom campagne-->
            <name><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString"/></name>
        	
        	
			<xsl:for-each select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:descriptiveKeywords">
                <!--Navire-->
                <xsl:if test="gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'platform'">
					<shipCode><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_PlatformCode/@codeListValue"/></shipCode>
	                <shipName><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_PlatformCode"/></shipName> 
                </xsl:if>
                
                <!-- Region -->
                <xsl:if test="gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'place'">
                    <regionCode><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_WaterBodyCode/@codeListValue"/></regionCode>
                    <regionName><xsl:value-of select="gmd:MD_Keywords/gmd:keyword/sdn:SDN_WaterBodyCode"/></regionName> 
                </xsl:if>
                
    			<!-- project -->
                <xsl:if test="gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue =  'project'">
                    <projectName><xsl:value-of select="substring-before(gmd:MD_Keywords/gmd:keyword/sdn:SDN_EDMERPCode,'{')"/></projectName> 
                </xsl:if>
                
            </xsl:for-each>   
            
            <!-- Date de debut -->
        	<formattedStartDate><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:beginPosition"/></formattedStartDate>
            <!-- Date de fin -->
            <formattedEndDate><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:extent/gmd:EX_Extent/gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent/gml:TimePeriod/gml:endPosition"/></formattedEndDate>
			
			
			<!-- Pays TODO a VALIDER -->
			<countryName><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:country/sdn:SDN_CountryCode"/></countryName>
			
			<!-- Chef de mission TODO a VALIDER  -->
			<chiefScientist><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:individualName/gco:CharacterString"/></chiefScientist>
			
			<!-- Laboratoire TODO a VALIDER  -->
			<laboratories><xsl:value-of select="gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:citedResponsibleParty/gmd:CI_ResponsibleParty/gmd:organisationName/sdn:SDN_EDMOCode"/></laboratories>
	    
	    	
	    	<!-- Availability -->
	    	<xsl:choose>
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'CC')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'FE')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'LI')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'MO')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'NC')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'OG')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'RS')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'SR')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'UN')">
                        <availability>publicly</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'UK')">
                        <availability>publicly</availability>
                    </xsl:when> 
                                     
                    <xsl:when test="contains(gmi:MI_Metadata/gmd:identificationInfo/sdn:SDN_DataIdentification/gmd:resourceConstraints[2]/gmd:MD_LegalConstraints/gmd:otherConstraints/gmx:Anchor/@xlink:href ,'NA')">
                        <availability>confidential</availability>
                    </xsl:when>
                    
                    <xsl:otherwise>
                        <availability>restricted</availability>
                    </xsl:otherwise>
                </xsl:choose>
	    	
			<!-- Data Centre not use load from config-->
        	<dataCentre/>
        
			<!-- Data Type -->
        	<dataTypes>
        		<!-- parcours gmi:objective group by sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue  -->                
        		<xsl:for-each select="gmi:MI_Metadata/gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:objective[not( preceding::sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue = sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue )]">
                	<DataType>
                	<xsl:variable name="dataCategoryCode" select="sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue" />
                        	
                    	<xsl:attribute name="code">
                        	<xsl:value-of select="sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode/@codeListValue"/> - <xsl:value-of select="sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode"/>
                        </xsl:attribute>
                        
                        <!-- count number of current @codeListValue -->
                        <profiles><xsl:value-of select="count(//gmi:MI_Metadata/gmi:acquisitionInformation/gmi:MI_AcquisitionInformation/gmi:objective/sdn:SDN_Objective/gmi:sensingInstrument/gmi:MI_Instrument/gmi:type/sdn:SDN_DataCategoryCode[@codeListValue = $dataCategoryCode])"/></profiles>
                    </DataType>
                </xsl:for-each>
            </dataTypes>
            
        </CruiseModel>
    </xsl:template>
    
</xsl:stylesheet>
