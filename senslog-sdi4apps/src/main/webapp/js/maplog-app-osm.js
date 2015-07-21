
/**
 * Init map. Called  on body.onLoad
 */
function initializeMap() {

    /*
     * map
     */
    var options = {
            displayProjection: new OpenLayers.Projection("epsg:4326"),
                maxExtent: new OpenLayers.Bounds(
                    -128 * 156543.0339,
                    -128 * 156543.0339,
                    128 * 156543.0339,
                    128 * 156543.0339
                ),
                initialExtent: new OpenLayers.Bounds(
                    1057535,4509031,4842397,10299995
                ),
                maxResolution: 156543.0339,
                numZoomLevels: 19,
                units: "m",
                projection: new OpenLayers.Projection("epsg:3857")
    };
    options.controls=[
                new OpenLayers.Control.PanZoomBar(),
                new OpenLayers.Control.ArgParser(),
                new OpenLayers.Control.Attribution()
        ];

    maplog.mapPanel.body.dom.style.background = "#f1eee8";

    map = new OpenLayers.Map(maplog.mapPanel.body.dom.id,
            options);

    /*
     * layers
     */

    var mapnik = new OpenLayers.Layer.OSM();
    // new layers
    var carHistory = new MapLog.Layer.History("Car History",
            "http://"+window.location.hostname+"/cgi-bin/maploghistory",
            {
                layers: "longHistory longHistoryPoint",
                map_imagetype: "png",
                IDS:"",
                SESSIONID:"",
                map_transparent: "ON",
                map_projection: "init=epsg:3857"
            },
            {
                isBaseLayer: false,
                displayInLayerSwitcher: true,
                singleTile: true,
                visibility: false,
                projection: mapnik.projection,
                wrapDateLine: true,
                opacity: .50,
                ratio:1,
                resolutions: mapnik.resolutions,
                transitionEffect:"resize"
            });

    maplog.unitsLayer = new MapLog.Layer.UnitVector("Units2",dbservice+"/DataService",
            {visibility:true});

    map.addLayers([mapnik,carHistory,maplog.unitsLayer]);

    var lb = new OpenLayers.LonLat(9.5,37.5);
    var rt = new OpenLayers.LonLat(43.5,67.5);
    lb.transform(new OpenLayers.Projection("epsg:4326"),mapnik.projection);
    rt.transform(new OpenLayers.Projection("epsg:4326"),mapnik.projection);
    map.zoomToExtent(map.initialExtent);


    /*
     * controls
     */
    var mp = new OpenLayers.Control.MousePosition();
    maplog.tq = new MapLog.Control.TracQuery(carHistory,"points");
    maplog.tq.qlayer = "positions";
    maplog.tq.unitsLayer = maplog.unitsLayer;

    maplog.unitSwitcher = new MapLog.Control.UnitSwitcher(dbservice+"/GroupService?Operation=GetGroups",maplog.unitsLayer, {container: maplog.switcherPanel,maplog:maplog});
    var selectFeature = new OpenLayers.Control.SelectFeature(
                [maplog.unitsLayer],
                {
                    clickout: true, toggle: false,
                    multiple: false, hover: false,
                    toggleKey: "ctrlKey", // ctrl key removes from selection
                    multipleKey: "shiftKey", // shift key adds to selection
                    scope: maplog,
                    onBeforeSelect: function(){if (this.tq.active) {return false}}
                }
            );

    /*
     * final initializition
     */
    maplog.setMap(map);
    maplog.mapPanel.toolsPanel.addControls([maplog.tq]);

    map.addControls([mp,new OpenLayers.Control.ScaleLine(),maplog.unitSwitcher,selectFeature]);

    //var mask = new Ext.LoadMask(maplog.mapPanel.body);
    //mask.show();
    //mask.hide();

    maplog.unitsLayer.setHistoryLayer(carHistory);
    maplog.addDetailMap(map,carHistory);
    maplog.registerLayer(maplog.unitsLayer);
    maplog.settingsPanel.setHistoryLayer(carHistory);

    maplog.unitsLayer.events.register("loadend", this, onFeaturesLoaded);

    MapLog.UnitFeature.setDetailTemplate(document.location.href.replace("map-osm.jsp","detailTemplate-"+HS.getLang()+".html?"+new String(Math.random())));

    maplog.unitSwitcher.getGroups();
    maplog.unitSwitcher.events.register("groupsadded",this,function(){maplog.unitsLayer.update();});

    selectFeature.activate();

};
