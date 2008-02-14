<?xml version="1.0"?>

<!-- Projet: CONVMED / IFREMER /SISMER.                                                -->
<!-- Auteur: G.BUNEL, Y.CAUDAL                                                        -->
<!-- Version: 1.1                                                               -->
<!-- Date: 07/11/20006, Rvision: 06/12/2007                                                          -->
<!-- Description: conversion d un fichier XML de description de campagne        -->
<!--              provenant de l'ifremer en information campagne exploitable    -->


<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" cdata-section-elements="comment"/>
    
    <!-- on supprime tout les espaces -->
    <xsl:strip-space elements="*"/>
    
    <!-- point d entree sur fichier -->
    <xsl:template match="/">
        <!-- tag root -->
        <Survey>
            <reference><xsl:value-of select="Metadata/dataIdInfo/idCitation/resAltTitle"/></reference>
            <name><xsl:value-of select="Metadata/dataIdInfo/idCitation/resTitle"/></name>
			
			

            <!-- FAE021 Evolution : changement de format du fichier xml   -->
            <xsl:for-each select="Metadata/dataIdInfo/descKeys">
                <xsl:if test="keyTyp/KeyTypCd/@value =  'platform'">
                
                <!-- FAE030 Evolution : récupération du code navire   -->
				<shipCode><xsl:value-of select="substring-after(substring-after(substring-after(keyword/@SDNIdent,':'),':'),':')"/></shipCode>
                <!-- FAE030 Fin   -->
                 
                <shipName><xsl:value-of select="keyword"/></shipName> 
                </xsl:if>
            </xsl:for-each>      
            <!-- FAE021 Fin -->
            
            <formattedStartDate><xsl:value-of select="Metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Period/begin"/></formattedStartDate>
            <formattedEndDate><xsl:value-of select="Metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Period/end"/></formattedEndDate>
   
            <!-- FAE021 Evolution : changement de format du fichier xml   -->
            <xsl:for-each select="Metadata/dataIdInfo/descKeys">
                <xsl:if test="keyTyp/KeyTypCd/@value =  'place'">
                    <regionName><xsl:value-of select="keyword"/></regionName> 
                </xsl:if>
            </xsl:for-each>     
            <!-- FAE021 Fin -->      
                      
            <!-- FAE028 Evolution : code pays  -->     
            <country><xsl:value-of select="Metadata/dataIdInfo/idCitation/citRespParty/rpCntInfo/cntAddress/country"/></country>
            <countryName><xsl:value-of select="Metadata/dataIdInfo/idCitation/citRespParty/rpCntInfo/cntAddress/country"/></countryName>
    		<!-- FAE028 Fin -->  

            <!-- FAE021 Evolution : changement de format du fichier xml  -->
            <laboratories><xsl:if test="Metadata/dataIdInfo/idCitation/citRespParty/role/RoleCd/@value =  'originator'"><xsl:value-of select="Metadata/dataIdInfo/idCitation/citRespParty/rpOrgName"/></xsl:if><xsl:for-each select="Metadata/dataIdInfo/idCitation/citRespParty"><xsl:if test="role/RoleCd/@value =  'originator'"> - <xsl:value-of select="rpOrgName"/></xsl:if></xsl:for-each></laboratories> 
            <!-- FAE021 Fin -->   
                       
            <!-- FAE021 Evolution : changement de format du fichier xml   -->
            <!-- <xsl:for-each select="Metadata/dataIdInfo/idCitation/citRespParty"> -->
                <xsl:if test="Metadata/dataIdInfo/idCitation/citRespParty/role/RoleCd/@value =  'pointOfContact'">
                    <chiefScientist><xsl:value-of select="Metadata/dataIdInfo/idCitation/citRespParty/rpIndName"/></chiefScientist> 
                </xsl:if>
            <!-- </xsl:for-each> -->     
            <!-- FAE021 Fin -->      
            
            <!-- FAE021 Evolution : changement de format du fichier xml   -->
            <xsl:for-each select="Metadata/dataIdInfo/descKeys">
                <xsl:if test="keyTyp/KeyTypCd/@value =  'project'">
                    <projectName><xsl:value-of select="keyword"/></projectName> 
                </xsl:if>
            </xsl:for-each>     
            <!-- FAE021 Fin -->      
            
            <comment/>    
            <dataCentre/>
            
            <!-- FAE021 Evolution : changement de format du fichier xml   -->
                <xsl:choose>
                	<!-- FAE034 Evolution : changement de format du fichier xml   -->
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'CC')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'FE')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'LI')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'MO')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'NC')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'OG')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'RS')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'SR')">
                        <availability>limited</availability>
                    </xsl:when>
                    
                    <!-- FAE034 Fin --> 
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'UN')">
                        <availability>publicly</availability>
                    </xsl:when>
                    
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'UK')">
                        <availability>publicly</availability>
                    </xsl:when> 
                                     
                    <xsl:when test="contains(Metadata/dataIdInfo/resConst/Consts/useLimit/@SDNIdent ,'NA')">
                        <availability>confidential</availability>
                    </xsl:when>
                    
                    <xsl:otherwise>
                        <availability>restricted</availability>
                    </xsl:otherwise>
                </xsl:choose>
            <!-- FAE021 Fin -->  
            


            <dataTypes>
                <xsl:for-each select="Metadata/dataIdInfo">
                    <xsl:if test="aggrInfo/initType/InitTypCd/@value =  'operation'">
                        
                        <DataType>
                            <xsl:attribute name="code">
                                <xsl:value-of select="substring-after(substring-after(substring-after(idCitation/resTitle/@SDNIdent,':'),':'),':')"/> - <xsl:value-of select="idCitation/resTitle"/>
                            </xsl:attribute>
                            <profiles><xsl:value-of select="suppInfo"/></profiles>
                        </DataType>
                    </xsl:if>
                </xsl:for-each>
            </dataTypes>
            
        </Survey>
    </xsl:template>
    
</xsl:stylesheet>
