<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="main8"/>
  <title>OMAR: Video Data Set List</title>

  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/fonts', file: 'fonts-min.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/paginator/assets/skins/sam', file: 'paginator.css')}"/>
  <link rel="stylesheet" type="text/css" href="${resource(plugin: 'richui', dir: 'js/yui/datatable/assets/skins/sam', file: 'datatable.css')}"/>

  <%--
  <link rel="stylesheet" type="text/css" href="${scratch.bundle(contentType: 'text/css', files: [
      resource(plugin: 'richui', dir: 'js/yui/fonts', file: 'fonts-min.css'),
      resource(plugin: 'richui', dir: 'js/yui/paginator/assets/skins/sam', file: 'paginator.css'),
      resource(plugin: 'richui', dir: 'js/yui/datatable/assets/skins/sam', file: 'datatable.css')
  ])}"/>
  --%>

</head>
<body class="yui-skin-sam">
<content tag="north">
  <div class="nav">
    <span class="menuButton"><g:link class="home" uri="/">OMAR™ Home</g:link></span>
    <g:ifAllGranted role="ROLE_ADMIN">
      <span class="menuButton"><g:link class="create" action="create">Create Video Set</g:link></span>
    </g:ifAllGranted>
    <span class="menuButton"><g:link action="search">Search Video</g:link></span>
    <span class="menuButton"><a href="javascript:toggleThumbnails()">Toggle Thumbnails</a></span>
    <span class="menuButton"><a href="javascript:toggleDetails()">Toggle Details</a></span>
  </div>
</content>
<content tag="center">
  <div class="body">
    %{--<h1><g:message code="default.list.label" args="[entityName]"/></h1>--}%
    <g:if test="${flash.message}">
      <div class="message">${flash.message}</div>
    </g:if>
    <div id="dynamicData"></div>
  </div>
</content>

<content tag="south">
    <div id="paging"></div>
  </content>

<script type="text/javascript" src="${omar.bundle(contentType: 'text/javascript', files: [
    resource(plugin: 'richui', dir: 'js/yui/yahoo-dom-event', file: 'yahoo-dom-event.js'),
    resource(plugin: 'richui', dir: 'js/yui/connection', file: 'connection-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/json', file: 'json-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/element', file: 'element-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/paginator', file: 'paginator-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/datasource', file: 'datasource-min.js'),
    resource(plugin: 'richui', dir: 'js/yui/datatable', file: 'datatable-min.js')
])}"></script>

<g:javascript>
    YAHOO.widget.DataTable.Formatter.thumbnail = function( elLiner, oRecord, oColumn, oData )
    {
      elLiner.innerHTML = "<a href='" + oData.href + "'><img src='" + oData.url + "'></a>";
    };

       // Column definitions
      var myColumnDefs = ${myColumnDefs};

      // Custom parser
      var stringToDate = function( sData )
      {
        var array = sData.split( "-" );
        return new Date( array[1] + " " + array[0] + ", " + array[2] );
      };

      // DataSource instance
      var myDataSource = new YAHOO.util.DataSource(
              "${createLink(action: 'query.json')}?" );

      myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
      myDataSource.responseSchema = {
        resultsList: "results",
        fields: ${fields},
        metaFields: {
          totalRecords: "totalRecords" // Access to value in the server response
        }
      };

var myPaginator = new YAHOO.widget.Paginator({
      containers: "paging",
      rowsPerPage: 10,
      template : "{FirstPageLink} {PreviousPageLink} <strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} {CurrentPageReport} <input name='pageNumber' value='1' size='14' type='text' onchange='gotoPage(this.value)'>"

    });

      // DataTable configuration
      var myConfigs = {
        // Initial request for first page of data
        initialRequest: "${initialRequest}",
        // Enables dynamic server-driven data
        dynamicData: true,
        // Sets UI initial sort arrow
        sortedBy : {key:"id", dir:YAHOO.widget.DataTable.CLASS_ASC},
        // Enables pagination

       // paginator: new YAHOO.widget.Paginator( { rowsPerPage: ${params.max} } ),
         paginator: myPaginator,


        // / Change query string to match service
        generateRequest: function(state) {

          var query = "offset=" + state.pagination.recordOffset +
                 "&max=" + state.pagination.rowsPerPage +
                 "&sort=" + state.sortedBy.key +
                 "&order=" + ((state.sortedBy.dir === YAHOO.widget.DataTable.CLASS_ASC) ? "asc" : "desc");

          if ("${initialRequest}" != null) {
              query += '&' + "${initialRequest}";
          }

/*
          if (this.customQueryString != null) {
              query += '&' + this.customQueryString;
              alert(query);
          }

          // tack on any user filters
          for (filterBy in this.userFilters) {
              query += '&filterBy=' + filterBy + '&filterOn=' + this.userFilters[filterBy];
          }
*/

          return query;
        }
      };

     function gotoPage(frm)
    {
      if (frm >= '1' && frm <= myPaginator.getTotalPages())
      {
        var frmInt = parseInt(frm);
        myPaginator.setPage(frmInt, false);
      }
      else
      {
        alert("ERROR: Invalid Input.\nAcceptable inputs are 1 through " + myPaginator.getTotalPages() + ".");
      }
    }

      // DataTable instance
      var myDataTable = new YAHOO.widget.DataTable( "dynamicData", myColumnDefs, myDataSource, myConfigs );

    YAHOO.example.DynamicData = function()
    {

      // Update totalRecords on the fly with value from server
      myDataTable.handleDataReturnPayload = function( oRequest, oResponse, oPayload )
      {
        oPayload.totalRecords = oResponse.meta.totalRecords;
        return oPayload;
      };

      return {
        ds: myDataSource,
        dt: myDataTable
      };


    }();

    var tn = "on";

    function toggleThumbnails()
    {
        if(tn == "on")
        {
            myDataTable.hideColumn(myDataTable.getColumn('thumbnail'));
            tn = "off";
        }
        else
        {
            myDataTable.showColumn(myDataTable.getColumn('thumbnail'));
            tn = "on";
        }
      }

        var td = "on";

    function toggleDetails()
    {
        if(td == "on")
        {
            myDataTable.hideColumn(myDataTable.getColumn('width'));
            myDataTable.hideColumn(myDataTable.getColumn('height'));
            myDataTable.hideColumn(myDataTable.getColumn('minLon'));
            myDataTable.hideColumn(myDataTable.getColumn('minLat'));
            myDataTable.hideColumn(myDataTable.getColumn('maxLon'));
            myDataTable.hideColumn(myDataTable.getColumn('maxLat'));
            td = "off";
        }
        else
        {
            myDataTable.showColumn(myDataTable.getColumn('width'));
            myDataTable.showColumn(myDataTable.getColumn('height'));
            myDataTable.showColumn(myDataTable.getColumn('minLon'));
            myDataTable.showColumn(myDataTable.getColumn('minLat'));
            myDataTable.showColumn(myDataTable.getColumn('maxLon'));
            myDataTable.showColumn(myDataTable.getColumn('maxLat'));
            td = "on";
        }
      }
</g:javascript>

</body>
</html>
