<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 1/31/12
  Time: 2:14 PM
  To change this template use File | Settings | File Templates.
--%>
<div class="list">
  <table>
    <thead>
    <tr>

      <th>Thumbnail</th>
      <g:sortableColumn property="id" title="Id" params="${queryParams?.toMap()}"/>
      <th>WMS GetCapabilities</th>
      <th>WMS GetMap</th>
      <th>Generate KML</th>
      <th>Generate Super Overlay</th>

    </tr>
    </thead>
    <tbody>
    <g:each in="${rasterEntries}" status="i" var="rasterEntry">
      <tr class="${( i % 2 ) == 0 ? 'odd' : 'even'}">
        <td height="${rasterEntry.height / ( Math.max( rasterEntry.width, rasterEntry.height ) / 128.0 )}">
          <a href="${createLink( controller: "mapView", params: [layers: rasterEntry.indexId], absolute: true, base: grailsApplication.config.serverURL )}">
            <img src="${createLink( controller: "thumbnail", action: "show", params:
                [id: rasterEntry.id, size: 128, projectionType: "imagespace"], absolute: true, base: grailsApplication.config.serverURL )}" alt="Show Thumbnail"/>
          </a>
        </td>
        <td>
          <g:link controller="rasterEntry" action="show"
                  id="${rasterEntry.id}">${rasterEntry.id?.encodeAsHTML()}</g:link>
        </td>
        <td>
          <a href="${createLink( controller: "ogc", action: "wms", params: [request: "GetCapabilities",
              layers: rasterEntry.indexId], absolute: true, base: grailsApplication.config.serverURL )}">WMS GetCapabilities</a>
        </td>
        <td>
          <g:set var="bounds" value="${rasterEntry?.groundGeom?.bounds}"/>
          <g:set var="calcHeight" value="${( Math.rint( rasterEntry.height / rasterEntry.width * 1024 ) as int )}"/>
          <a href="${createLink( controller: "ogc", action: "wms", params: [
              request: "GetMap",
              layers: rasterEntry.indexId,
              bbox: [bounds?.minLon, bounds?.minLat, bounds?.maxLon, bounds?.maxLat].join( "," ),
              srs: "epsg:4326", width: 1024, height: "${calcHeight}",
              format: "image/jpeg"], absolute: true, base: grailsApplication.config.serverURL )}">WMS GetMap</a>
        </td>
        <td>
          <a href="${createLink( controller: "ogc", action: "wms", params: [request: "GetKML",
              layers: rasterEntry.indexId, format: "image/png", transparent: "true"], absolute: true, base: grailsApplication.config.serverURL )}">Generate KML</a>
        </td>
        <td>
          <a href="${createLink( controller: "superOverlay", action: "createKml", params: [id: rasterEntry.indexId,
              stretch_mode: "linear_auto_min_max", stretch_mode_region: "global"], absolute: true, base: grailsApplication.config.serverURL )}">Generate Super Overlay</a>
        </td>
      </tr>
    </g:each>

    </tbody>
  </table>
</div>
