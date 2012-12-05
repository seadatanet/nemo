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
            
            <StationModel>
              <referenceProperty>
                <xsl:for-each select="StationModel/referenceProperty/PropertyModel">
                  <PropertyModel>
                    <xsl:attribute name="property"><xsl:value-of select="@property"/></xsl:attribute>
                    <xsl:attribute name="userProvided"><xsl:value-of select="@userProvided"/></xsl:attribute>
                    <xsl:choose>
                      <xsl:when test="@userProvided = 'false'">
                        <HeaderFragment>
                          <xsl:attribute name="isSingleLine"><xsl:value-of select="HeaderFragment/@isSingleLine"/></xsl:attribute>
                          <xsl:attribute name="isVariable"><xsl:value-of select="HeaderFragment/@isVariable"/></xsl:attribute>
                          <firstLine><xsl:value-of select="HeaderFragment/firstLine"/></firstLine>
                          <xsl:if test="HeaderFragment/@isSingleLine = 'false'">
                            <lastLine><xsl:value-of select="HeaderFragment/lastLine"/></lastLine>
                          </xsl:if>
                          <firstCharOffset><xsl:value-of select="HeaderFragment/firstCharOffset"/></firstCharOffset>
                          <lastCharOffset><xsl:value-of select="HeaderFragment/lastCharOffset"/></lastCharOffset>
                        </HeaderFragment>
                      </xsl:when>
                      <xsl:otherwise>
                        <userValue><xsl:value-of select="../../incrementNumber"/></userValue>
                      </xsl:otherwise>
                    </xsl:choose>
                  </PropertyModel>
                </xsl:for-each>
              </referenceProperty>
              
              <properties>
                <xsl:for-each select="StationModel/properties/PropertyModel">
                  <PropertyModel>
                    <xsl:attribute name="property"><xsl:value-of select="@property"/></xsl:attribute>
                    <xsl:attribute name="userProvided"><xsl:value-of select="@userProvided"/></xsl:attribute>
                      <xsl:if test="format">
                        <format>
                          <xsl:variable name="formatClass" select="local-name(format/*)"/>
                          <xsl:element name="{$formatClass}">
                            <xsl:attribute name="formatString"><xsl:value-of select="dyn:evaluate(concat('format/',$formatClass,'/@formatString'))"/></xsl:attribute>
                          </xsl:element>
                        </format>
                      </xsl:if>
                    <xsl:choose>
                      <xsl:when test="@userProvided = 'false'">
                        <HeaderFragment>
                          <xsl:attribute name="isSingleLine"><xsl:value-of select="HeaderFragment/@isSingleLine"/></xsl:attribute>
                          <xsl:attribute name="isVariable"><xsl:value-of select="HeaderFragment/@isVariable"/></xsl:attribute>
                          <firstLine><xsl:value-of select="HeaderFragment/firstLine"/></firstLine>
                          <xsl:if test="HeaderFragment/@isSingleLine = 'false'">
                            <lastLine><xsl:value-of select="HeaderFragment/lastLine"/></lastLine>
                          </xsl:if>
                          <firstCharOffset><xsl:value-of select="HeaderFragment/firstCharOffset"/></firstCharOffset>
                          <lastCharOffset><xsl:value-of select="HeaderFragment/lastCharOffset"/></lastCharOffset>
                        </HeaderFragment>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:variable name="propertyName" select="@property"/>
                        <userValue>
                          <xsl:value-of select="dyn:evaluate(concat('../../Station/',$propertyName))"/>
                        </userValue>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="FlagFragment">
                      <Flag>
                        <xsl:attribute name="isSingleLine"><xsl:value-of select="FlagFragment/@isSingleLine"/></xsl:attribute>
                        <xsl:attribute name="isVariable"><xsl:value-of select="FlagFragment/@isVariable"/></xsl:attribute>
                        <firstLine><xsl:value-of select="FlagFragment/firstLine"/></firstLine>
                        <xsl:if test="FlagFragment/@isSingleLine = 'false'">
                          <lastLine><xsl:value-of select="FlagFragment/lastLine"/></lastLine>
                        </xsl:if>
                        <firstCharOffset><xsl:value-of select="FlagFragment/firstCharOffset"/></firstCharOffset>
                        <lastCharOffset><xsl:value-of select="FlagFragment/lastCharOffset"/></lastCharOffset>
                      </Flag>
                    </xsl:if>
                  </PropertyModel>
                </xsl:for-each>
                
                <xsl:for-each select="StationModel/TimeSeriesModel/properties/PropertyModel">
                  <PropertyModel>
                    <xsl:variable name="propertyName" select="@property"/>
                    <xsl:attribute name="property"><xsl:value-of select="@property"/></xsl:attribute>
                    <xsl:attribute name="userProvided"><xsl:value-of select="@userProvided"/></xsl:attribute>
                    <xsl:if test="$propertyName = 'samplingRate' and StationModel/TimeSeriesModel/sampeRateConversion">
                      <conversionFormula><xsl:value-of select="StationModel/TimeSeriesModel/sampeRateConversion"/></conversionFormula>
                    </xsl:if>
                    <xsl:if test="format">
                      <format>
                        <xsl:variable name="formatClass" select="local-name(format/*)"/>
                        <xsl:element name="{$formatClass}">
                          <xsl:attribute name="formatString"><xsl:value-of select="dyn:evaluate(concat('format/',$formatClass,'/@formatString'))"/></xsl:attribute>
                        </xsl:element>
                      </format>
                    </xsl:if>
                    <xsl:choose>
                      <xsl:when test="@userProvided = 'false'">
                        <HeaderFragment>
                          <xsl:attribute name="isSingleLine"><xsl:value-of select="HeaderFragment/@isSingleLine"/></xsl:attribute>
                          <xsl:attribute name="isVariable"><xsl:value-of select="HeaderFragment/@isVariable"/></xsl:attribute>
                          <firstLine><xsl:value-of select="HeaderFragment/firstLine"/></firstLine>
                          <xsl:if test="HeaderFragment/@isSingleLine = 'false'">
                            <lastLine><xsl:value-of select="HeaderFragment/lastLine"/></lastLine>
                          </xsl:if>
                          <firstCharOffset><xsl:value-of select="HeaderFragment/firstCharOffset"/></firstCharOffset>
                          <lastCharOffset><xsl:value-of select="HeaderFragment/lastCharOffset"/></lastCharOffset>
                        </HeaderFragment>
                      </xsl:when>
                      <xsl:otherwise>
                        <userValue>
                          <xsl:value-of select="dyn:evaluate(concat('../../Station/TimeSeriesInformation/',$propertyName))"/>
                        </userValue>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="FlagFragment">
                      <Flag>
                        <xsl:attribute name="isSingleLine"><xsl:value-of select="FlagFragment/@isSingleLine"/></xsl:attribute>
                        <xsl:attribute name="isVariable"><xsl:value-of select="FlagFragment/@isVariable"/></xsl:attribute>
                        <firstLine><xsl:value-of select="FlagFragment/firstLine"/></firstLine>
                        <xsl:if test="FlagFragment/@isSingleLine = 'false'">
                          <lastLine><xsl:value-of select="FlagFragment/lastLine"/></lastLine>
                        </xsl:if>
                        <firstCharOffset><xsl:value-of select="FlagFragment/firstCharOffset"/></firstCharOffset>
                        <lastCharOffset><xsl:value-of select="FlagFragment/lastCharOffset"/></lastCharOffset>
                      </Flag>
                    </xsl:if>
                  </PropertyModel>
                </xsl:for-each>
              </properties>
              
              <DataType>
                <xsl:attribute name="code"><xsl:value-of select="StationModel/Station/DataType/@code"/></xsl:attribute>
                <profiles><xsl:value-of select="StationModel/Station/DataType/profiles"/></profiles>
                <qc><xsl:value-of select="StationModel/Station/DataType/qc"/></qc>
                <sensor><xsl:value-of select="StationModel/Station/DataType/sensor"/></sensor>
              </DataType>
              
              <FlagTable>
                <xsl:attribute name="code"><xsl:value-of select="StationModel/FlagTable/@code"/></xsl:attribute>
                <xsl:attribute name="label"><xsl:value-of select="StationModel/FlagTable/@label"/></xsl:attribute>
                <xsl:for-each select="StationModel/FlagTable/Flag">
                  <Flag>
                    <xsl:attribute name="local"><xsl:value-of select="@local"/></xsl:attribute>
                    <xsl:attribute name="SDN"><xsl:value-of select="@SDN"/></xsl:attribute>
                  </Flag>
                </xsl:for-each>
              </FlagTable>
            </StationModel>
            
            
          </xsl:for-each>
        </InputModel>
    </xsl:template>
    
</xsl:stylesheet>
