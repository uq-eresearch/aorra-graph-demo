<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" encoding="utf-8"/>

<xsl:template match="/table/catchment/name">
<td class="theme1 region_caption"><span class="box_rotate"><xsl:value-of select="." /></span></td>
</xsl:template>

<xsl:template match="/table/aip">
<tr>
    <td class="caption">
    <xsl:attribute name="rowspan">
        <xsl:value-of select="count(*)" />
    </xsl:attribute>%<br/>adoption<br/>improved<br/>practices</td>
    <td><xsl:value-of select="name(*[1])" /></td>
    <td colspan="0">
        <xsl:attribute name="class">
            <xsl:value-of select="*[1]/@condition" />
        </xsl:attribute>
        <xsl:value-of select="*[1]/text()" />
    </td>
</tr>
<xsl:for-each select="*[position() &gt;= 2]">
    <tr>
        <td><xsl:value-of select="name()" /></td>
        <td colspan="0" class="verygood">
            <xsl:attribute name="class">
                <xsl:value-of select="./@condition" />
            </xsl:attribute>
        <xsl:value-of select="./text()" />
        </td>
    </tr>
</xsl:for-each>
</xsl:template>

<xsl:template match="/table/loss">
<tr>
    <td rowspan="2" class="caption">% loss</td>
    <td>Wetlands</td>
    <td style="border-right-width:2px;">
    <xsl:attribute name="class">
        <xsl:value-of select="./Wetlands/@condition" />
    </xsl:attribute>
<xsl:value-of select="./Wetlands/text()" /></td>
    <td class="theme2 fakeborder" style="border-right-width:2px;">
    <xsl:attribute name="rowspan">
        <xsl:choose>
            <xsl:when test="/table/groundcover">3</xsl:when>
            <xsl:otherwise>2</xsl:otherwise>
        </xsl:choose>
    </xsl:attribute>
    </td>
    <xsl:for-each select="/table/catchment/wetland">
        <td>
            <xsl:attribute name="class">
                <xsl:value-of select="@condition" />
            </xsl:attribute>
<xsl:value-of select="text()" /></td>
    </xsl:for-each>
</tr>
<tr>
    <td>Riparian</td>
    <td style="border-right-width:2px;">
    <xsl:attribute name="class">
        <xsl:value-of select="./Riparian/@condition" />
    </xsl:attribute>
<xsl:value-of select="./Riparian/text()" /></td>
    <xsl:for-each select="/table/catchment/riparian">
        <td>
            <xsl:attribute name="class">
                <xsl:value-of select="@condition" />
            </xsl:attribute>
<xsl:value-of select="text()" /></td>
    </xsl:for-each>
</tr>
</xsl:template>

<xsl:template match="/table/groundcover">
<tr>
    <td colspan="2" class="caption">% groundcover</td>
    <td class="verygood" style="border-right-width:2px;">
    <xsl:attribute name="class">
        <xsl:value-of select="@condition" />
    </xsl:attribute><xsl:value-of select="text()" /></td>
    <xsl:for-each select="/table/catchment/groundcover">
        <td>
            <xsl:attribute name="class">
                <xsl:value-of select="@condition" />
            </xsl:attribute>
<xsl:value-of select="text()" /></td>
    </xsl:for-each>
</tr>
</xsl:template>

<xsl:template match="/table/load">
<tr>
    <td rowspan="4" class="caption">% load<br/>reduction</td>
    <td>Nitrogen</td>
    <td colspan="0">
    <xsl:attribute name="class">
        <xsl:value-of select="./Nitrogen/@condition" />
    </xsl:attribute><xsl:value-of select="./Nitrogen/text()" /></td>
</tr>
<tr>
    <td>Phosphorus</td>
    <td colspan="0">
    <xsl:attribute name="class">
        <xsl:value-of select="./Phosphorus/@condition" />
    </xsl:attribute><xsl:value-of select="./Phosphorus/text()" /></td>
</tr>
<tr>
    <td>Sediment</td>
    <td colspan="0">
    <xsl:attribute name="class">
        <xsl:value-of select="./Sediment/@condition" />
    </xsl:attribute><xsl:value-of select="./Sediment/text()" /></td>
</tr>
<tr>
    <td>Pesticides</td>
    <td colspan="0" >
    <xsl:attribute name="class">
        <xsl:value-of select="./Pesticides/@condition" />
    </xsl:attribute><xsl:value-of select="./Pesticides/text()" /></td>
</tr>
</xsl:template>

<xsl:template match="/table/marine">
<tr>
    <td colspan="2" class="theme2 caption" style="text-align: left;">Overall marine condition</td>
    <td colspan="0">
    <xsl:attribute name="class">
        <xsl:value-of select="@condition" />
    </xsl:attribute></td>
</tr>
<tr>
    <td colspan="2" class="caption">Water quality</td>
    <td colspan="0"><xsl:attribute name="class">
        <xsl:value-of select="Water/@condition" />
    </xsl:attribute></td>
</tr>
<tr>
    <td colspan="2" class="caption">Seagrass</td>
    <td colspan="0"><xsl:attribute name="class">
        <xsl:value-of select="Seagrass/@condition" />
    </xsl:attribute></td>
</tr>
<tr>
    <td colspan="2" class="caption">Corals</td>
    <td colspan="0"><xsl:attribute name="class">
        <xsl:value-of select="Corals/@condition" />
    </xsl:attribute></td>
</tr>
</xsl:template>

<xsl:template name="legendDefault">
<table>
<tr>
    <td colspan="2" class="legend" style="height: 5px;"></td>
</tr>
<tr class="legend">
    <td class="legend" style="width: 100px;"><span class="verygood legendbox"></span>Very good</td>
    <td class="legend"><span class="poor legendbox"></span>Poor</td>
</tr>
<tr class="legend">
    <td class="legend"><span id="debug" class="good legendbox"></span>Good</td>
    <td class="legend"><span class="verypoor legendbox"></span>Very poor</td>
</tr>
<tr class="legend">
    <td colspan="2" class="legend"><span class="moderate legendbox"></span>Moderate</td>
</tr>
</table>
</xsl:template>

<xsl:template name="legendHatched">
<table>
<tr>
<td colspan="3" class="legend" style="height: 5px;"></td>
</tr>
<tr class="legend">
<td rowspan="2" class="legend">
<table>
<tr><td class="legend" style="width: 100px;"><span class="verygood legendbox"></span>Very good</td></tr>
<tr><td class="legend"><span class="good legendbox"></span>Good</td></tr>
<tr><td class="legend"><span class="moderate legendbox"></span>Moderate</td></tr>
<tr><td class="legend"><span class="poor legendbox"></span>Poor</td></tr>
<tr><td class="legend"><span class="verypoor legendbox"></span>Very poor</td></tr>
</table>
</td>
<td rowspan="2" class="legend" style="vertical-align:top;"><span class="legend_hatching legendbox"></span></td>
<td class="legend" style="vertical-align:top;">
Hatching indicates low confidence due<br/>
to limited data availability or limited<br/>
validation for seagrass and water quality</td>
</tr>
<tr><td class="legend" style="vertical-align:bottom;">N/E Not Evaluated</td></tr>
</table>
</xsl:template>

<xsl:template match="/">
<html>
<head>
<title>Progress and status</title>
<link rel="stylesheet" type="text/css" href="status.css" />
<style type="text/css">
.theme1 {
    background-color: <xsl:value-of select="/table/theme1" />;
}
.theme2 {
    background-color: <xsl:value-of select="/table/theme2" />;
    color: white;
    text-align: center;
}
</style>
</head>
<body>
<table>
<tr><td colspan="0" class="theme1 heading">Progress and status</td></tr>
<tr>
    <td colspan="2" rowspan="2" class="theme2 heading">Targets</td>
    <td rowspan="2" class="theme1 region_caption" style="border-right-width:2px;"><span class="box_rotate">Region</span></td>
    <td rowspan="2" class="theme2 fakeborder" style="border-right-width:2px;"></td>
    <td colspan="0" class="theme2" style="font-size: 12pt;">Catchments</td>
</tr>
<tr>
    <xsl:apply-templates select="/table/catchment/name"/>
</tr>
<xsl:apply-templates select="/table/aip"/>
<xsl:apply-templates select="/table/loss"/>
<xsl:apply-templates select="/table/groundcover"/>
<xsl:apply-templates select="/table/load"/>
<xsl:apply-templates select="/table/marine"/>
<tr>
<td colspan="0" class="legend">
<xsl:choose>
    <xsl:when test="/table/legend='hatched'"><xsl:call-template name="legendHatched"/></xsl:when>
    <xsl:otherwise><xsl:call-template name="legendDefault"/></xsl:otherwise>
</xsl:choose>
</td>
</tr>
</table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>
