<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main"/>
  <title>Show RasterEntry</title>
</head>
<body>
<div class="nav">
  <span class="menuButton"><a class="home" href="${resource(dir: '')}">Home</a></span>
  <span class="menuButton"><g:link class="list" action="list">RasterEntry List</g:link></span>
  <g:ifAllGranted role="ROLE_ADMIN">
    <span class="menuButton"><g:link class="create" action="create">New RasterEntry</g:link></span>
  </g:ifAllGranted>
</div>
<div class="body">
  <h1>Show RasterEntry</h1>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog">
    <table>
      <tbody>

        <tr class="prop">
          <td valign="top" class="name">Id:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'id')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Entry Id:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'entryId')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Width:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'width')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Height:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'height')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Number Of Bands:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'numberOfBands')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Bit Depth:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'bitDepth')}</td>

        </tr>

        <tr class="prop">
          <td valign="top" class="name">Data Type:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'dataType')}</td>

        </tr>

        <%--
        <tr class="prop">
          <td valign="top" class="name">Ground Geom:</td>
          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'groundGeom')}</td>
        </tr>

        <tr class="prop">
          <td valign="top" class="name">Acquisition Date:</td>

          <td valign="top" class="value">${fieldValue(bean: rasterEntry, field: 'acquisitionDate')}</td>

        </tr>
        --%>

<%--
        <tr class="prop">
          <td valign="top" class="name">Metadata Tags:</td>

          <td valign="top" style="text-align:left;" class="value">
            <g:link controller="metadataTag" action="list" params="${[rasterEntryId: rasterEntry.id]}">Show MetadataTags</g:link>
          </td>

        </tr>
--%>
        <tr class="prop">
          <td valign="top" class="name">File Objects:</td>

          <td valign="top" style="text-align:left;" class="value">
            <g:if test="${rasterEntry.fileObjects}">
              <g:link controller="rasterEntryFile" action="list" params="${[rasterEntryId: rasterEntry.id]}">Show Raster Files</g:link>
            </g:if>
          </td>
        </tr>

        <tr class="prop">
          <td valign="top" class="name">Raster Data Set:</td>

          <td valign="top" class="value"><g:link controller="rasterDataSet" action="show" id="${rasterEntry?.rasterDataSet?.id}">${rasterEntry?.rasterDataSet?.encodeAsHTML()}</g:link></td>

        </tr>

      </tbody>
    </table>
  </div>
  <div class="buttons">
    <g:form>
      <input type="hidden" name="id" value="${rasterEntry?.id}"/>
      <g:ifAllGranted role="ROLE_ADMIN">
        <span class="button"><g:actionSubmit class="edit" value="Edit"/></span>
        <span class="button"><g:actionSubmit class="delete" onclick="return confirm('Are you sure?');" value="Delete"/></span>
      </g:ifAllGranted>
      <span class="menuButton">
        <a href="${createLink(controller: 'thumbnail', action: 'show', id: rasterEntry.id, params: [size: 512])}" >Show Thumbnail</a>
      </span>
    </g:form>
  </div>
</div>
</body>
</html>
