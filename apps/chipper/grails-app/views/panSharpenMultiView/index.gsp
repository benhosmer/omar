<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 12/4/13
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>PanSharpenMultiView</title>
    <r:require modules="easyui_scaffold"/>
    <r:layoutResources/>
</head>

<body class="easyui-layout">
</body>

<div data-options="region:'north',title:'North Title',split:true" style="height:100px;"></div>

<div data-options="region:'south',title:'South Title',split:true,collapsed:true" style="height:100px;"></div>

<div data-options="region:'east',title:'East',split:true,collapsed:true" style="width:100px;"></div>

<div data-options="region:'west',title:'West',split:true" style="width:100px;"></div>

<div data-options="region:'center',title:'center title'" style="padding:5px;background:#eee;">
    <div id="map"></div>
</div>
<r:external plugin='openlayers' file='OpenLayers.js' dir='js'/>
<r:script>
    $( document ).ready( function ()
    {

        var bbox = new OpenLayers.Bounds(${minX}, ${minY}, ${maxX}, ${maxY});
        var map, layers, controls;

        map = new OpenLayers.Map( 'map', {
            numZoomLevels: 32
        } );

        layers = [
            new OpenLayers.Layer.WMS(
                    "NASA BMNG",
                    "http://omar.ngaiost.org/cgi-bin/mapserv.sh",
                    {layers: 'Reference', map: '/data/omar/bmng.map'},
                    {buffer: 0}
            ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Color",
                    "${createLink( controller: 'chipper', action: 'getChip' )}",
                    {layers: '${colorImage}', format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: true} ),

            new OpenLayers.Layer.WMS( "Chipper - getChip - Pan",
                    "${createLink( controller: 'chipper', action: 'getChip' )}",
                    {layers: '${panImage}', format: 'image/png', transparent: true},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} ),

            new OpenLayers.Layer.WMS( "Chipper - getPSM - Product",
                    "${createLink( controller: 'chipper', action: 'getPSM' )}",
                    {layers: '${psmImage}', format: 'image/png', transparent: true, bands: '3,2,1'},
                    {buffer: 0, singleTile: true, ratio: 1.0, isBaseLayer: false, visibility: false} )

        ];
        map.addLayers( layers );

        controls = [
            new OpenLayers.Control.LayerSwitcher()
        ];
        map.addControls( controls );

        //map.zoomToExtent( bbox, true );

        map.setCenter(new OpenLayers.LonLat(147.258526555874, -42.8605875658741), map.getZoomForExtent(bbox));

        $( 'body' ).layout( 'panel', 'center' ).panel( {
            onResize: function ()
            {
                map.updateSize();
            }
        } );

    } );
</r:script>

<r:layoutResources/>
</html>