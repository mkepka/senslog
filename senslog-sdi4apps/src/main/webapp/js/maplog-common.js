window.addEventListener ? window.addEventListener('load', init, false) : window.attachEvent('onload', init);

var maplog;
var map;
var login;

var dbservice = "/DBService";
var MAPLOG_DEBUG = false;


function init () {

    dbservice = getDBService();

    var mask = new Ext.LoadMask(Ext.getBody());
    mask.show();

    maplog = new Ext.MapLog({
        region: "center"
    });

    var head = new Ext.BoxComponent({"region":"north",
        height: 98,
        "el": Ext.get("headding")
        });

    var viewPort = new Ext.Viewport({
        layout: "border", 
        items: [ head, maplog ]
    });

    initializeMap();
    mask.hide();
};

var onFeaturesLoaded = function(e) {

    var layer = e.object;
    // zmenit databazovou sluzbu na aktualizaci poloh
    // layer.url = dbservice + "/DataService?Operation=GetLastPositionsWithStatus";

    // naplnit vyber jednotek v nastavovacim panelu
    var data = [];
    for (var i = 0; i < layer.features.length; i++) {
        var feature = layer.features[i];
        var text = new String(feature.id);
        if (feature.node) {
            text = feature.node.text+" ..."+ text.substr(text.length-4);
            data.push(new Ext.data.Record({"text":text, "feature":feature})); 
        }
    }
    maplog.settingsPanel.unitsStore.add(data);

    // nazoomovat nebo aktivovat pozadovanou jednotku:
    var args = OpenLayers.Util.getParameters();

    if (args.zoomToUnit) {
        var feature = maplog.unitsLayer.getFeatureByUnitId(args.zoomToUnit);
        if (feature) {
            //maplog.map.setCenter(new OpenLayers.LonLat(feature.geometry.x, feature.geometry.y));
            maplog.map.zoomToExtent(new OpenLayers.Bounds(feature.geometry.x, feature.geometry.y, feature.geometry.x, feature.geometry.y));
        }
    }

    if (args.activateUnit) {
        var feature = maplog.unitsLayer.getFeatureByUnitId(args.activateUnit);
        if (feature) {
            feature.setActive(true);
        }
    }

};

var getDBService = function() {
    var pathname = window.location.pathname;
    if (pathname) {
        dbservice = pathname.replace(/map.*\.jsp/,"");
        dbservice = dbservice.replace(/\/$/,"");
    }
    return dbservice;
};

