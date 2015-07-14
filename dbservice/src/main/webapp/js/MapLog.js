var MapLog = {};

MapLog.namespace = function() {
    var a=arguments, o=null, i, j, d, rt;

    for (var i=0; i < arguments.length; i++) {
        var names = arguments[i].split(".");
        var root = names[0];

        eval('if (typeof ' + root + ' == "undefined"){' + root + ' = {};} var out = ' + root + ';');

        for (var j=1; j < names.length; j++) {
            out[names[j]]=out[names[j]] || {};
            out=out[names[j]];
        }
    }
};

MapLog.getSessionCookie = function() {
    return Ext.util.Cookies.get("JSESSIONID");
};

Ext.namespace("Ext.MapLog");
/**
 * MapLog application
 * @contructor
 * @param {Object} configuration
 */
Ext.MapLog = function(config) {
    
    // initialization of properties
    this.mapPanel = new Ext.MapLog.MapPanel({region:"center",
            title: OpenLayers.i18n("Map")});


    this.switcherPanel = new Ext.Panel({
                title: OpenLayers.i18n("Units"),
                layout: "fit"
                });

    try {
        if (HS.getCookie("audio") == "true") {
            this.audio = new Audio("audio/sirena.ogg");
            this.audio.setAttribute("loop",true);
            this.audio.setAttribute("preload",true);
            if (this.mapPanel.playSoundCheckbox.checked === false ) {
                this.audio.volume = 0.0;
            }

            this.mapPanel.playSoundCheckbox.on("check",function(cb,checked) {
                            if (checked) {
                                this.audio.volume = 1.0;
                                HS.setCookie("audio_checked","on");
                                }
                            else {
                                this.audio.volume = 0.0;
                                HS.setCookie("audio_checked","off");
                            }
                            },this);
        }
    }catch(e) {
        if (window.console) {
            console.log(e);
        }
    }

    this.settingsPanel = new Ext.MapLog.SettingsPanel();

    this.toolsPanel = new Ext.TabPanel({
                region:"east",
                width: 300,
                activeTab : 0,
                items : [this.switcherPanel,
                         this.settingsPanel
                         ]
            });

    this.detailPanel = new Ext.MapLog.DetailPanel({title:"Detail"});

    this.mapAndDetailPanel = new Ext.TabPanel({
        region: "center",
        activeTab : 0,
        title : OpenLayers.i18n("MapLog"),
        deferredRender : false,
        items: [this.mapPanel , this.detailPanel]
      });

    config.layout = "border";
    config.items = [
        this.mapAndDetailPanel,
        this.toolsPanel
        ];
    
    // call parent constructor
    Ext.MapLog.superclass.constructor.call(this, config);
    this.mapAndDetailPanel.doLayout();
};

//extend
Ext.extend(Ext.MapLog,Ext.Panel, {
    /**
     * {OpenLayers.Layer.UnitLayer} to be observed
     */
    unitsLayer: undefined,

    /**
     * @type OpenLayers.Map
     */
    map: null,

    /**
     * {OpenLayers.Layer.LayerTracs} to be observed
     */
    trackLayer: undefined,

    /**
     * The marker was clicked event
     */
    markerClick: function(evt) {
        var marker = evt.object;
        marker.feature.setActive(true);
    },

    /**
     * Feature state changed
     */
    featureStateChanged: function(evt) {
        var feature = evt.object;

        if (feature.attributes.active == "true") {
            //this.mapAndDetailPanel.activate(1);
            this.detailPanel.displayDetail(feature);
            this.settingsPanel.enable();
            this.settingsPanel.setFeature(feature);
        }
        else {
            this.settingsPanel.setFeature(undefined);
        }
    },

    /**
     * Feature state changed
     */
    onFeaturePositionUpdated: function(evt) {
        var feature = evt.object;

        if (feature.attributes.active == "true") {
            //this.mapAndDetailPanel.activate(1);
            //this.detailPanel.updateDetail(feature.attributes);
            this.detailPanel.displayDetail(feature);
        }
    },

    /**
     * The marker was mouse overed event
     */
    markerIn: function(evt) {
        var marker = evt.object;
        marker.feature.displayInfo(true);
    },

    /**
     * The marker was mouse overed event
     */
    markerOut: function(evt) {
        var marker = evt.object;
        marker.feature.displayInfo(false);
    },

    /**
     * Register the layer to be observed by the detial panel
     * @param {OpenLayers.Layer.UnitLayer}
     */
    registerLayer: function(layer) {
        this.unitsLayer = layer;
        this.unitsLayer.events.register("loadend",this,this.registerMarkers);
        this.unitsLayer.events.register("loadend",this,function(){
            this.mapPanel.displayLabel();
        });

        this.unitsLayer.events.register("loadend",this.mapPanel, this.mapPanel.setUpdated);
        this.unitsLayer.events.register("updated",this.mapPanel, this.mapPanel.setUpdated);
        this.unitsLayer.events.register("ok",this.mapPanel, this.mapPanel.setUpdatedOkFailed);
        this.unitsLayer.events.register("failed",this.mapPanel, this.mapPanel.setUpdatedOkFailed);

        this.unitsLayer.events.register("hasalert",this,function(){
                if (maplog.audio) {
                    maplog.audio.play();
                }});
        
        this.unitsLayer.events.register("noalert",this,function(){
                if (maplog.audio) {
                    maplog.audio.pause();
            }});

        this.mapPanel.setUnitLayer(layer);

        //this.mapPanel.toolsPanel.addControls([new OpenLayers.Control.UnitSelector({layer:this.unitsLayer,scope:this,
        //           featuresSelected: this.onFeaturesSelected})]);

    },
    /**
     * Call everytime the layer is loaded again 
     */
    registerMarkers: function(layer) {
        for (var i = 0; i < this.unitsLayer.features.length; i++) {
            this.unitsLayer.features[i].events.register("statechanged",this,this.featureStateChanged);
            this.unitsLayer.features[i].events.register("positionupdated",this,this.onFeaturePositionUpdated);
        }
    },

    /**
     * UNregister the layer to be observed by the detial panel
     * @param {OpenLayers.Layer.UnitLayer}
     */
    unregisterLayer: function(layer) {
        this.unitsLayer.events.unregister("loadend",this,this.registerMarkers);
    },

    /**
     * Returns the active tab in maplogs mapAndDetail panel
     */
    getActiveTab: function(){
        return this.mapAndDetailPanel.getActiveTab();
    },

    /**
     * Add map to detail tab
     * @param map
     */
    addDetailMap: function(map,layer) {
        this.detailPanel.addDetailMap(map,layer);
    },

    /**
     * features selected in the map using selector
     * @param [{OpenLayers.UnitFeature}]
     * @param {Boolean} add
     */
    onFeaturesSelected: function(features,add) {
        for (var i = 0; i < features.length; i++) {
            features[i].setActive(true);
        }
    },

    /**
     * setmap
     * @param {OpenLayers.Map} map
     */
    setMap: function(map) {
        this.map = map;
        this.mapPanel.setMap(map);
        this.settingsPanel.setMap(map);
    }
});
