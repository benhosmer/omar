<%--
  Created by IntelliJ IDEA.
  User: sbortman
  Date: 8/9/13
  Time: 1:36 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Viewer</title>
    <meta name="layout" content="main"/>
    <style type="text/css">
    #map {
        width: 1024px;
        height: 512px;
        border: #255b17 solid thin;
    }
    </style>
</head>

<body>
<div class="nav">
    <ul>
        <li><g:link class="home" uri="/">Home</g:link></li>
    </ul>
</div>

<div class="content">
    <h1>Chipper Viewer</h1>

    <div align='center'>
        <div id='map'></div>
    </div>
</div>
<r:external plugin='jquery' dir='js/jquery' file='jquery-1.8.3.min.js'/>
<r:external plugin='openlayers' dir='js' file='OpenLayers.js'/>

<r:script>
    $( document ).ready( function ()
    {
        var map = new OpenLayers.Map( 'map' );

        var layers = [
            new OpenLayers.Layer.WMS( "BMNG",
                    "http://omar.ngaiost.org/cgi-bin/mapserv.sh",
                    {map: '/data/omar/bmng.map', layers: 'Reference', format: 'image/jpeg'},
                    {buffer: 0} ),

            new OpenLayers.Layer.WMS( "Chipper",
                    "${createLink( action: 'getChip' )}",
                    {layers: '', format: 'image/png', transparent: true},
                    {buffer: 0, isBaseLayer: false} )

        ];
        map.addLayers( layers );

        var controls = [
            new OpenLayers.Control.LayerSwitcher()
        ];
        map.addControls( controls );

        var bounds = new OpenLayers.Bounds( -180, -90, 180, 90 );
        map.zoomToExtent( bounds );
    } );
</r:script>
</body>
</html>