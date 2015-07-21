/**
 * MapLog.DetailPanel
 * @contructor
 * @param {Object} configuration
 */
Ext.MapLog.DetailPanel= function(config) {

    this.info = new Ext.Panel({columnWidth:1,
            //layout:'accordion',
            //layoutConfig: {fill:true}
            layout:'vbox',
            height: 350,
            flex:2,
            autoScroll: true,
            layoutConfig: {
                align : 'stretch',
                pack  : 'start'
            },
            region:"center"
        });


    this.map = new Ext.Panel({width:300,height:300,
        bbar : [/*
        this.displayNameCheckbox,
        {},
        this.displayDateCheckbox,
        {},
        this.displayTimeCheckbox
        */
    ]});
    var mapPanel  = new Ext.Panel({region:"east",items:[this.map]});

    if (!config) {
        config = {};
    }

    //// layout
    config.layout = "hbox";
    config.layoutConfig= {align: "stretch"};
    config.width = "100%";
    config.height = "100%";

    config.items = [this.info /*, this.queryPanel*/, mapPanel];

    //// toolbar
    //config.tbar = [{text:OpenLayers.i18n("Clear"), 
    //                handler: this.onClearClicked,
    //                scope: this,
    //                cls: 'x-btn-text-icon',
    //                icon: "icons/trash.gif"
    //            }];

    // call parent constructor
    Ext.MapLog.DetailPanel.superclass.constructor.call(this, config);

    // initialization of properties
    // ... nothing yet
};

//extend
Ext.extend(Ext.MapLog.DetailPanel,Ext.Panel, {
    // methods should be defined here

    /**
     * {OpenLayers.Map}
     */
    detailMap: undefined,

    /**
     * {OpenLayers.Layer}
     */
    detailMarkerLayer: undefined,

    /**
     * displayNameCheckbox
     * @type Ext.form.Checkbox
     */
    displayNameCheckbox: null,

    /**
     * holderStore 
     * @type Ext.data.Store
     */
    holderStore: null,

    /**
     * sensorsStore 
     * @type Ext.data.Store
     */
    sensorsStore: null,

    /**
     * displayDateCheckbox
     * @type Ext.form.Checkbox
     */
    displayDateCheckbox: null,

    /**
     * displayTimeCheckbox
     * @type Ext.form.Checkbox
     */
    displayTimeCheckbox: null,

    /**
     * toolsPanel
     * @type OpenLayers.Control.Panel
     */
    toolsPanel: null,
    
    /**
     * Dislay detail informations about selected features
     * @param [{OpenLayers.UnitFeature}] features
     */
    displayDetails: function(features){
        for (var i = 0; i < features.length; i++) {
            this.displayDetail(features[i]);
        }
    },

    /**
     * Dislay detail informations about selected feature
     * @param {OpenLayers.UnitFeature}
     */
    displayDetail: function(feature){

        // clear detail informations
        this.onClearClicked();


        OpenLayers.Console.info("displayDetail",feature);


       
        var attributes = feature.attributes; //this.getFlatAttributes(feature.attributes);
        var pos = new OpenLayers.LonLat(attributes.position.x, attributes.position.y);
        pos = pos.transform(new OpenLayers.Projection("epsg:5514"),new OpenLayers.Projection("epsg:4326"));
        attributes.position.xwgs84 = Math.round(pos.lon*100000)/100000;
        attributes.position.ywgs84 = Math.round(pos.lat*100000)/100000;
        if (!attributes.is_moving) {
            attributes.is_moving = false;
        }

        if (!attributes.holder) {
            attributes.holder = {};
        }

        this.info.body.update(MapLog.UnitFeature.processDetailTemplate(attributes));

        feature.detailPanel = this.info;
        this.info.setTitle(OpenLayers.i18n("Detail of")+" "+feature.id);
        this.info.doLayout();

        this.displayUnitsInMap(feature);
    },

    ///**
    // * Update detail of active unit
    // * @param {Object} params
    // */
    //updateDetail: function(params) {
    //    var attributes = this.getFlatAttributes(feature.attributes);
    //    this.info.add({html: MapLog.UnitFeature.processDetailTemplate(attributes)});
    //},

    /**
     * Get flat key-value pairs from complicated structure
     */
    getFlatAttributes: function(attributes) {
        var outAttrs = {};
        for (var i in attributes) {
            if (i == "icon") {
                continue;
            }
            if (typeof(attributes[i]) == typeof({})) {
                for (var j  in attributes[i]) {
                    outAttrs[j] = attributes[i][j];
                }
            }
            else {
                outAttrs[i] = attributes[i];
            }
        }

        return outAttrs;
    },


    /**
     * Add the detail map to this panel
     * @param {OpenLayers.Map} map
     * @param {OpenLayers.Layer} historylayer
     */
    addDetailMap: function(map, historylayer){

        this.detailMap = new OpenLayers.Map(this.map.body.dom.id,{
                resolutions: map.resolutions,
                scales: map.scales,
                maxExtent: map.maxExtent,
                projection: map.projection
                });
        var baseLayer = map.baseLayer.clone();
        baseLayer.isBaseLayer = true;

        this.origHistory = historylayer;

        this.detailMap.addLayer(baseLayer);
        this.detailMap.zoomToMaxExtent();

        var now = new Date();
        var tomorrow = new Date(now.valueOf() + 1000*60*60*24);
        this.historyLayer = new MapLog.Layer.History("Car detail history",
                historylayer.url,
                {layers: historylayer.params.layers,
                 map_imagetype: historylayer.params.map_imagetype,
                 IDS: historylayer.params.IDS || "-1",
                 SESSIONID: historylayer.params.SESSIONID,
                 map_transparent: "ON",
                 fromTime : new String(now.getFullYear())+"-"+new String(now.getMonth()+1)+"-"+new String(now.getDate()-0)+" 00:00",
                 toTime : new String(tomorrow.getFullYear())+"-"+new String(tomorrow.getMonth()+1)+"-"+new String(tomorrow.getDate())+" 00:00"
                },
                {
                    isBaseLayer:false,
                    singleTile: true,
                    visibility: true,
                    opacity: 0.5,
                    ratio: 1,
                    resolutions: this.detailMap.resolutions,
                    transitionEffect:"resize"
                    });


        this.historyLayer.name = "Detail history map";
        historylayer.events.register("paramsupdated",this,this.updateHistory);


        this.detailMarkerLayer = new OpenLayers.Layer.Markers("Details");
        this.detailMap.addLayers([this.historyLayer,this.detailMarkerLayer]);

        this.toolsPanel = new OpenLayers.Control.Panel({displayClass: "hsControlPanel"});
        this.detailMap.addControl(this.toolsPanel);


        // history query tool
        //var tq = new MapLog.Control.TracQuery(this.historyLayer,"positions",{size: new OpenLayers.Size(150,150),popupClass: OpenLayers.Popup.AnchoredBubble,usePopup:false,
        //        extContainer: this.queryPanel});
        //this.toolsPanel.addControls([tq]);
    },


    /**
     * update history layer
     */
    updateHistory: function(e){
            // FIXME: too much recursion: this.historyLayer.setParams(e.object.params);
            this.historyLayer.params.IDS = e.object.params.IDS;
            this.historyLayer.params.SESSIONID = e.object.params.SESSIONID;
            this.historyLayer.redraw(true);
    },


    /**
     * Displays markers from selected units in the map
     */
    displayUnitsInMap: function(feature) {

        // clean existing markers
        // this.detailMarkerLayer.clearMarkers();
        //

        if (!feature.detailMarkerLayer) {
            feature.detailMarkerLayer = this.detailMarkerLayer;
        }

        feature.detailMarker.display(true);
        feature.detailMarker.map = this.detailMap;

        // create function which will synchronise the detail marker with
        // the real one
        var sync = function(evt) {
            this.moveTo(this.map.getLayerPxFromLonLat(this.lonlat));
        };

        feature.detailMarker.map = this.detailMap;

        //feature.events.register("moved",feature.detailMarkerLayer,sync);
        this.detailMarkerLayer.addMarker(feature.detailMarker);

        //feature.detailMarker.icon.toggleDisplayEvent(true,8,"icons/active.png");
        this.detailMap.setCenter(new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y));
        this.detailMap.zoomTo(5); // FIXME should be configurable

        //if (feature.detailLabel) {
        //    feature.detailLabel.show();
        //}
        //else {
        //    feature.displayDetailLabel(this.getLabelAttributes(),this.detailMap);
        //}
    },

    /**
     * display the label for each unit in the map
     */
    displayLabel: function(){
        for (var i = 0; i < this.detailMarkerLayer.markers.length; i++) {
            this.detailMarkerLayer.markers[i].feature.displayDetailLabel(this.getLabelAttributes(),this.detailMap);
        }
    },
    
    /**
     * Grid collapsed, - mark the marker as inactive
     * @note scope 'this' is feature {OpenLayers.UnitFeature}
     */
    onGridCollapse: function() {
        if (this.detailMap) {
            this.detailMarker.icon.toggleDisplayEvent(false,8,"icons/active.png");
        }
    },

    /**
     * get the attributes, which should be displayed in the detail map as
     * label for each unit
     */
    getLabelAttributes: function() {
        var attributes =  [];
        if  (this.displayNameCheckbox.getValue()) {
            attributes.push("name");
        }
        if  (this.displayDateCheckbox.getValue()) {
            attributes.push("date");
        }
        if  (this.displayTimeCheckbox.getValue()) {
            attributes.push("time");
        }
        return attributes;
    },

    /**
     * Grid expaned, - mark the marker as active
     * @note scope 'this' is feature {OpenLayers.UnitFeature}
     */
    onGridExpanded: function() {
        if (this.detailMarker) {
            this.detailMarker.icon.toggleDisplayEvent(true,8,"icons/active.png");
            this.detailMarker.map.setCenter(this.lonlat);
        }
    },
    
    /**
     * Grid closed, remove the marker as well
     * @note scope 'this' is feature {OpenLayers.UnitFeature}
     */
    onGridClose: function() {

        this.detailPanel = null;

        // this.detailMarker.destroy();
        // this.detailMarker = null;
        this.detailMarker.display(false);
        this.detailLabel.hide();
    },

    /**
     * clear clicled - erase all details
     * @note scope 'this' is feature {OpenLayers.UnitFeature}
     */
    onClearClicked: function() {
        this.info.removeAll();
        this.info.doLayout();
        this.detailMarkerLayer.clearMarkers();
    }

});
