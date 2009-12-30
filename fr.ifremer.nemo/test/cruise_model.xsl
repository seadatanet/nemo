<?xml version="1.0"?>

<!-- Projet: CONVMED / IFREMER /SISMER.                                         -->
<!-- Auteur: D.BOULANGER                                                        -->
<!-- Version: 1.0                                                               -->
<!-- Date: 07/12/2009                                                           -->
<!-- Description: conversion d un ancien modèle XML                             -->


<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dyn="http://exslt.org/dynamic"
                extension-element-prefixes="dyn">
    
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" cdata-section-elements="comment"/>
    
    <!-- on supprime tout les espaces -->
    <xsl:strip-space elements="*"/>

    
    <!-- point d entree sur fichier -->
    <xsl:template match="/">
    
        <!-- tag root -->
        <InputModel>
          <xsl:attribute name="version">1.2.20</xsl:attribute>
          <xsl:for-each select="InputModel/Media/*">
            
            
            <CruiseModel>
              <reference><xsl:value-of select="Survey/reference"/></reference>
              <name><xsl:value-of select="Survey/name"/></name>
              <shipCode><xsl:value-of select="Survey/shipCode"/></shipCode>
              <shipName><xsl:value-of select="Survey/shipName"/></shipName>
              <startDate><xsl:value-of select="Survey/startDate"/></startDate>
              <endDate><xsl:value-of select="Survey/endDate"/></endDate>
              <regionName><xsl:value-of select="Survey/regionName"/></regionName>
              <country><xsl:value-of select="Survey/country"/></country>
              <laboratories><xsl:value-of select="Survey/laboratories"/></laboratories>
              <chiefScientist><xsl:value-of select="Survey/chiefScientist"/></chiefScientist>
              <projectName><xsl:value-of select="Survey/projectName"/></projectName>
              <dataCentre><xsl:value-of select="Survey/dataCentre"/></dataCentre>
              <availability><xsl:value-of select="Survey/availability"/></availability>
              <comment><xsl:value-of select="Survey/comment"/></comment>
              <dataTypes>
                <xsl:for-each select="Survey/dataTypes/DataType">
                  <DataType>
                    <xsl:attribute name="code"><xsl:value-of select="@code"/></xsl:attribute>
                    <profiles><xsl:value-of select="profiles"/></profiles>
                    <qc><xsl:value-of select="qc"/></qc>
                    <sensor><xsl:value-of select="sensor"/></sensor>
                  </DataType>
                </xsl:for-each>
              </dataTypes>
            </CruiseModel>
            
            
          </xsl:for-each>
        </InputModel>
    </xsl:template>
    
</xsl:stylesheet>
