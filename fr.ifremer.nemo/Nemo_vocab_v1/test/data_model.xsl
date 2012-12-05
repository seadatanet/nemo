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

            <DataModel>
              <parameters>
                <xsl:for-each select="StationModel/parameters/ParameterModel">
                  <ParameterModel>
                    <conversionFormula><xsl:value-of select="conversionFormula"/></conversionFormula>
                    <inputDefault><xsl:value-of select="inputDefault"/></inputDefault>
                    <LineFragment firstCharOffset="1" lastCharOffset="10"/>
                      <xsl:attribute name="firstCharOffset"><xsl:value-of select="LineFragment/@firstCharOffset"/></xsl:attribute>
                      <xsl:attribute name="lastCharOffset"><xsl:value-of select="LineFragment/@lastCharOffset"/></xsl:attribute>
                    <Parameter>
                      <xsl:attribute name="code"><xsl:value-of select="Parameter/@code"/></xsl:attribute>
                      <fullName><xsl:value-of select="Parameter/fullName"/></fullName>
                      <unit><xsl:value-of select="Parameter/unit"/></unit>
                      <default><xsl:value-of select="Parameter/default"/></default>
                      <format><xsl:value-of select="Parameter/format"/></format>
                    </Parameter>
                  </ParameterModel>
                </xsl:for-each>
              </parameters>
            </DataModel>            
            
          </xsl:for-each>
        </InputModel>
    </xsl:template>
    
</xsl:stylesheet>
