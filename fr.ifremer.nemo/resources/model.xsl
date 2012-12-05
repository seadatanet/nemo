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
   <xsl:preserve-space elements="separator"/>
    
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
                        <firstLine><xsl:value-of select="FlagFragment/line"/></firstLine>
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
                    <xsl:if test="$propertyName = 'samplingRate' and ../../sampeRateConversion">
                      <conversionFormula><xsl:value-of select="../../sampeRateConversion"/></conversionFormula>
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
                          <lastLine><xsl:value-of select="HeaderFragment/lastLine"/></lastLine>
                          <firstCharOffset><xsl:value-of select="HeaderFragment/firstCharOffset"/></firstCharOffset>
                          <lastCharOffset><xsl:value-of select="HeaderFragment/lastCharOffset"/></lastCharOffset>
                        </HeaderFragment>
                      </xsl:when>
                      <xsl:otherwise>
                        <userValue>
                          <xsl:value-of select="dyn:evaluate(concat('../../../Station/TimeSeriesInformation/',$propertyName))"/>
                        </userValue>
                      </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="FlagFragment">
                      <Flag>
                        <xsl:attribute name="isSingleLine"><xsl:value-of select="FlagFragment/@isSingleLine"/></xsl:attribute>
                        <xsl:attribute name="isVariable"><xsl:value-of select="FlagFragment/@isVariable"/></xsl:attribute>
                        <firstLine><xsl:value-of select="FlagFragment/line"/></firstLine>
                        <lastLine><xsl:value-of select="FlagFragment/line"/></lastLine>
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
            

            <DataModel>
              <parameters>
                <xsl:for-each select="StationModel/parameters/ParameterModel">
                  <ParameterModel>
                    <conversionFormula><xsl:value-of select="conversionFormula"/></conversionFormula>
                    <isText><xsl:value-of select="isText"/></isText>
                    <inputDefault><xsl:value-of select="inputDefault"/></inputDefault>
                    <LineFragment>
                      <xsl:attribute name="firstCharOffset"><xsl:value-of select="LineFragment/@firstCharOffset"/></xsl:attribute>
                      <xsl:attribute name="lastCharOffset"><xsl:value-of select="LineFragment/@lastCharOffset"/></xsl:attribute>
                    </LineFragment>
                    <xsl:if test="Flag">
	                    <Flag>
	                      <xsl:attribute name="firstCharOffset"><xsl:value-of select="Flag/@firstCharOffset"/></xsl:attribute>
	                      <xsl:attribute name="lastCharOffset"><xsl:value-of select="Flag/@lastCharOffset"/></xsl:attribute>
	                    </Flag>
                    </xsl:if>
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
