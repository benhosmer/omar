OMAR.views.FederatedRasterSearch = Backbone.View.extend({
    el:"#rasterSearchPageId",
    bboxView:null,
    initialize:function(params){

        this.bboxView = new OMAR.views.BBOX();
        this.bboxModel = this.bboxView.model;
        this.dateTimeRangeView = new OMAR.views.SimpleDateRangeView();
        this.dateTimeRangeModel = this.dateTimeRangeView.model;
        this.omarServerCollectionView = new OMAR.views.OmarServerCollectionView(
            {model:new OMAR.models.OmarServerCollection()}
        );
        this.mapView = new OMAR.views.Map(params.map);
        this.mapView.setBboxModel(this.bboxModel);
        this.mapView.setServerCollection(this.omarServerCollectionView.model);
        this.setElement(this.el);

        this.dateTimeRangeModel.bind('change', this.updateFootprintCql, this)
        this.rasterEntryDataModelView = new OMAR.views.RasterEntryDataModelView();
    },
    events: {
        "click #SearchRasterId": "searchRaster"
    },
    showTab:function(event, ui){
        if(ui.index == 0)
        {
        }
        else if(ui.index == 1)
        {
            this.centerResize();
        }
        if(ui.index == 2)
        {
            this.rasterEntryDataModelView.resizeView();
        }
    },
    render:function(){
        if(this.bboxView)
        {
            this.bboxView.render();
        }
        if(this.pointView)
        {
            this.pointView.render();
        }
        if(this.dateTimeRangeView)
        {
            this.dateTimeRangeView.render();
        }

        if(this.mapView)
        {
            this.mapView.render();
        }

        if(this.rasterEntryDataModelView)
        {
            this.rasterEntryDataModelView.render();
        }
        // lets make sure that that the map object exists
        // before we start the AJAX calls for fetching the server lists
        //
        if(this.omarServerCollectionView)
        {
            var collection =  this.omarServerCollectionView;

            collection.model.fetch({success:function(){collection.render()},
                update: true, remove: false,date:{cache:false}});
            window.setTimeout(this.updateServers.bind(this),5000);
        }

        this.mapView.setCqlFilterToFootprintLayers(this.toFootprintCql());
    },
    updateFootprintCql:function(){
        this.mapView.setCqlFilterToFootprintLayers(this.toFootprintCql());
    },
    updateServers:function(){
        var collection =  this.omarServerCollectionView;
        collection.model.fetch({success:function(){},
            update: true, remove: false,date:{cache:false}});
        window.setTimeout(this.updateServers.bind(this),5000);
    },
    toCql:function(){
        var result = "";
        var timeQueryCql = this.dateTimeRangeModel.toCql("acquisition_date");
        var bboxQueryCql = this.bboxModel.toCql("ground_geom");
        if(timeQueryCql&&bboxQueryCql)
        {
            result = "(("+bboxQueryCql+")AND(" +timeQueryCql+"))";
        }
        else if(bboxQueryCql)
        {
            result=bboxQueryCql;
        }
        else
        {
            result = timeQueryCql;
        }

        return result;
    },
    centerResize:function(){
        this.rasterEntryDataModelView.resizeView();
        this.mapView.resizeView();
    },
    toFootprintCql:function(){
        var result = "";
        var timeQueryCql = this.dateTimeRangeModel.toCql("acquisition_date");

        // add all criteria here later.   Fo now we will just do time
        //
        result = timeQueryCql;

        return result;
    },
    searchRaster:function(){
        var wfs = new OMAR.models.Wfs({"resultType":"hits"});
        var cqlFilter = this.toCql();
        wfs.set("filter",cqlFilter);
        if(this.omarServerCollectionView.model.size()>0)
        {
            var model = this.omarServerCollectionView.model.at(0);
            this.rasterEntryDataModelView.wfsModel.set({"url":model.get("url")+"/wfs",
                                                        "filter":cqlFilter});
            //wfsDataTable.set("url",
            //    this.omarServerCollectionView.model.at(0).get("url") + "/wfs");
            //this.rasterEntryDataModelView.model.url = wfsDataTable.toUrl()+"&callback=?"
            //this.rasterEntryDataModelView.model.fetch();
        }
        for(var idx = 0; idx <this.omarServerCollectionView.model.size();++idx )
        {
            var model = this.omarServerCollectionView.model.at(idx);
            if(model.get("enabled"))
            {

                this.omarServerCollectionView.setBusy(model.id, true);
                wfs.set("url",model.get("url")+"/wfs");

                if(model.userDefinedData.ajaxCountQuery && model.userDefinedData.ajaxCountQuery.readyState != 4){
                    model.userDefinedData.ajaxCountQuery.abort();
                    model.userDefinedData.ajaxCountQuery = null;
                    this.omarServerCollectionView.setBusy(model.id, false);
                }
                model.userDefinedData.ajaxCountQuery = $.ajax({
                    url: wfs.toUrl()+"&callback=?",
                    cache:false,
                    type: "GET",
                    crossDomain:true,
                    dataType: "json",
                    timeout: 60000,
                    modelId:model.id,
                    scopePtr:this,
                    success: function(response) {
                        if(response.numberOfFeatures!=null)
                        {
                            var numberOfFeatures = response.numberOfFeatures;
                            this.scopePtr.omarServerCollectionView.setBusy(this.modelId, false);
                            var tempModel = this.scopePtr.omarServerCollectionView.model.get(this.modelId);
                            if(tempModel)
                            {
                                tempModel.set({"count":numberOfFeatures});
                            }
                        }
                    },
                    error: function(x, t, m) {
                        var count = "Error";
                        if(t==="timeout") {
                            count = "Timeout"
                        } else {
                            //alert(JSON.stringify(x)+ " " +t + " " + m);
                        }
                        var tempModel = this.scopePtr.omarServerCollectionView.model.get(this.modelId);
                        this.scopePtr.omarServerCollectionView.setBusy(this.modelId, false);
                        if(tempModel)
                        {
                            tempModel.set({"count":count});
                        }
                    }
                });
            }
            else
            {

            }
        }
    }
});

OMAR.federatedRasterSearch = null;
OMAR.pages.FederatedRasterSearch = (function($, params){
    OMAR.federatedRasterSearch = new OMAR.views.FederatedRasterSearch(params);
    return OMAR.federatedRasterSearch;
});

$(document).ready(function () {

    // OUTER-LAYOUT

    $('body').layout({
            center__paneSelector:   ".outer-center"
        ,   west__paneSelector:     ".outer-west"
        ,   east__paneSelector:     ".outer-east"
        ,   west__size:             125
        ,   east__size:             125
        ,   spacing_open:           8  // ALL panes
        ,   spacing_closed:         12 // ALL panes
        //, north__spacing_open:    0
        //, south__spacing_open:    0
        ,   north__maxSize:         200
        ,   south__maxSize:         200

            // MIDDLE-LAYOUT (child of outer-center-pane)
        ,   center__childOptions: {
                center__paneSelector:   ".middle-center"
            ,   west__paneSelector:     ".middle-west"
            ,   east__paneSelector:     ".middle-east"
            ,   west__size:             100
            ,   east__size:             100
            ,   spacing_open:           0  // ALL panes
            ,   spacing_closed:         0 // ALL panes

                // INNER-LAYOUT (child of middle-center-pane)
            ,   center__childOptions: {
                    center__paneSelector:   ".inner-center"
                ,   west__paneSelector:     ".inner-west"
                ,   east__paneSelector:     ".inner-east"
                ,   west__size:             225
                ,   west__minSize:          225
                ,   east__size:             75
                ,   spacing_open:           8  // ALL panes
                ,   spacing_closed:         8  // ALL panes
                ,   west__spacing_closed:   12
                ,   east__spacing_closed:   12
                }
            }
        });

    init();
    OMAR.federatedRasterSearch.centerResize();
    $( "#accordion" ).accordion();

    $("#jMenu").jMenu({
                  openClick : false,
                  ulWidth : 100,
                  effects : {
                    effectSpeedOpen : 200,
                    effectSpeedClose : 200,
                    effectTypeOpen : 'slide',
                    effectTypeClose : 'hide',
                    effectOpen : 'linear',
                    effectClose : 'linear'
                  },
                  TimeBeforeOpening : 100,
                  TimeBeforeClosing : 100,
                  animatedText : true,
                  paddingLeft: 10
                });
  


});



function generateKmlQuery() {
    alert("kml query code goes here.")
}

function refreshFootprints() {
    alert("refresh footprints code goes here.")
}

function search() {
    alert("search code goes here.")
}

function foo() {
    alert("search code goes here.")
}
