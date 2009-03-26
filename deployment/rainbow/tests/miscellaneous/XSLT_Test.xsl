<?xml version="1.0" encoding="ISO-8859-1"?>
<!-- Edited by XMLSpy® -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="testParam1">parameterValue1</xsl:param>
<xsl:param name="testParam2">parameterValue2</xsl:param>
<xsl:template match="/">
  <html>
  <body>
    <h1>Hello 1 "<xsl:value-of select="$testParam1"/>"</h1>
    <h1>Hello 2 "<xsl:value-of select="$testParam2"/>"</h1>
    <table border="1">
      <tr bgcolor="#9acd32">
        <th>Title</th>
        <th>Artist</th>
      </tr>
      <xsl:for-each select="catalog/cd">
      <xsl:sort select="artist" />
      <tr>
        <td><xsl:value-of select="title"/></td>
        <td><xsl:value-of select="artist"/></td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>
</xsl:stylesheet>