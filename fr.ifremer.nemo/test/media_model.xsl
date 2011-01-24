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
          
          
            <MediaModel>
              <xsl:attribute name="path">
                <xsl:choose>
                  <xsl:when test="/InputModel/Media/DirectoryBasedSurvey"><xsl:value-of select="@directory"/></xsl:when>
                  <xsl:otherwise><xsl:value-of select="@file"/></xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
              <processType><xsl:attribute name="multiStations">true</xsl:attribute></processType>
              <separator><xsl:value-of select="StationModel/separator"/></separator>
              <inputType><xsl:value-of select="StationModel/inputType"/></inputType>
              <fileHeader><xsl:value-of select="@lineNumber"/></fileHeader>
              <xsl:choose>
                <xsl:when test="StationModel/endStationHeader">
                  <stationHeader>
                    <xsl:attribute name="pattern"><xsl:value-of select="StationModel/endStationHeader"/></xsl:attribute>
                    <xsl:value-of select="StationModel/numberLinesBetweenStationData"/>
                  </stationHeader>
                </xsl:when>
                <xsl:otherwise>
                  <stationHeader>
                    <xsl:attribute name="pattern"></xsl:attribute>
                    <xsl:value-of select="StationModel/@lineNumber"/>
                  </stationHeader>
                </xsl:otherwise>
              </xsl:choose>
              <endStation><xsl:value-of select="StationModel/rowNumber"/></endStation>
              <endData>
                <xsl:attribute name="pattern"><xsl:value-of select="StationModel/endDataPattern"/></xsl:attribute>
                <xsl:choose>
                  <xsl:when test="StationModel/@endOnPattern = 'true'">END_WITH</xsl:when>
                  <xsl:when test="StationModel/@endOnNextStation = 'true'">NEXT_START_WITH</xsl:when>
                  <xsl:when test="StationModel/@endOnEmptyLine = 'true'">EMPTY_LINE</xsl:when>
                  <xsl:otherwise>EOF</xsl:otherwise>
                </xsl:choose>
              </endData>
            </MediaModel>
             
            
          </xsl:for-each>
        </InputModel>
    </xsl:template>
    
</xsl:stylesheet>
