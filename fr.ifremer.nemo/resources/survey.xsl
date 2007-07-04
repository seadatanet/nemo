<?xml version="1.0"?>

<!-- Projet: CONVMED / IFREMER /SISMER.                                                -->
<!-- Auteur: G.BUNEL                                                        -->
<!-- Version: 1.0                                                               -->
<!-- Date: 07/11/20006                                                          -->
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
    <shipName><xsl:value-of select="Metadata/dataIdInfo/aggrInfo/aggrDSName/resTitle"/></shipName>
    <formattedStartDate><xsl:value-of select="Metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Period/begin"/></formattedStartDate>
    <formattedEndDate><xsl:value-of select="Metadata/dataIdInfo/dataExt/tempEle/TempExtent/exTemp/TM_GeometricPrimitive/TM_Period/end"/></formattedEndDate>
    <regionName><xsl:value-of select="Metadata/dataIdInfo/dataExt/geoEle/GeoDesc/geoId/identCode"/></regionName>
    <country/>
    <laboratory><xsl:value-of select="Metadata/dataIdInfo/idPoC/rpOrgNameId"/></laboratory>
    <chiefScientist><xsl:value-of select="Metadata/dataIdInfo/idPoC/rpIndName"/></chiefScientist>
    <projectName><xsl:value-of select="Metadata/dataIdInfo/idCitation/datasetSeries/seriesName"/></projectName>
    <comment><xsl:value-of select="Metadata/dataIdInfo/idAbs"/></comment>    
    <dataCentre/>
    <xsl:for-each select="Metadata/dataIdInfo/resConst/Consts">
      	<xsl:choose>
          <xsl:when test="useLimit='No'">
              <availability>confidential</availability>
          </xsl:when>
          <xsl:when test="useLimit='Yes'">
              <availability>publicly</availability>
          </xsl:when>
          <xsl:otherwise>
              <availability>restricted</availability>
          </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
    <dataTypes>
    <xsl:for-each select="Metadata/dataIdInfo/descKeys/keyTyp/KeyTypCd">
      	<xsl:choose>
          <xsl:when test="@value='number_element'">
              <DataType><xsl:attribute name="code"><xsl:value-of select="./../../../idCitation/resAltTitle"/> - <xsl:value-of select="./../../../idCitation/resTitle"/></xsl:attribute></DataType>
          </xsl:when>
        </xsl:choose>
    </xsl:for-each>
    </dataTypes>
  </Survey>
</xsl:template>

</xsl:stylesheet>
