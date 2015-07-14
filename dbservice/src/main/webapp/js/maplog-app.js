/**
 * Init map. Called  on body.onLoad
 */
function initializeMap() {
    OpenLayers.DOTS_PER_INCH = 90.714236728598;

    /*
     * map
     */
//    var options = HSLayers.Util.getProjectionOptions("epsg:5514",2000000,1000);
  var options = {
    projection: new OpenLayers.Projection('EPSG:5514'),
    maxExtent: new OpenLayers.Bounds(-925000,-1444353.5,-400646.5,-920000),
    initialExtent: new OpenLayers.Bounds(-575000,-1150000,-515000,-1062000),
    units: 'm',
    allOverlays: true,
    scales: [446.484375,892.96875,1785.9375,3571.875,7143.75,14287.5,28575,57150,114300,228600,457200/*,914400,1828800,3657600,7315200*/]
  };


    //    options.displayProjection = new OpenLayers.Projection("epsg:4326");

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
    var backgroundTopo = new OpenLayers.Layer.WMTS({
                name: "cuzk-zm",
                url: "http://geoportal.cuzk.cz/WMTS_ZM/WMTService.aspx",
                //title: "Základní mapy - ČÚZK",
                displayInLayerSwitcher: false,
                layer: "zm",
                style: "default",
                matrixSet: "jtsk:epsg:5514",
                format: "image/png",
                transparent: true,
                visibility: true,
                //buffer: 1,
                transitionEffect: "resize",
                isBaseLayer: false,
                hsSwitch: "podklad",
                zoomOffset: 4,
            attribution : { // copyright
                logo: {
                        href: "/app/common/img/logo_cuzk.png"
                    },
                href: "http://geoportal.cuzk.cz/%28S%28eor5r5vr1qvcfbmfnsj2n4sj%29%29/Default.aspx?menu=3150&mode=TextMeta&side=wmts.uvod&metadataID=CZ-CUZK-WMTS-ZM-P&metadataXSL=metadata.sluzba",
                title: "Český úřad zeměměřický a katastrální"
            }
    });
    var backgroundOrtho = new OpenLayers.Layer.WMTS({
                name: "cuzk-orto",
                url: "http://geoportal.cuzk.cz/WMTS_ORTOFOTO/WMTService.aspx",
                //title: "Ortofoto - ČÚZK",
                displayInLayerSwitcher: false,
                layer: "orto",
                style: "default",
                matrixSet: "jtsk:epsg:5514",
                format: "image/png",
                transparent: true,
                visibility: false,
                //buffer: 1,
                transitionEffect: "resize",
                isBaseLayer: false,
                hsSwitch: "podklad",
                zoomOffset: 4,
            metadataURL: {  // metadata
                        href: "http://geoportal.cuzk.cz/SDIProCSW/service.svc/get?REQUEST=GetRecordById&SERVICE=CSW&VERSION=2.0.2&OUTPUTFORMAT=application/xml&OUTPUTSCHEMA=http://www.isotc211.org/2005/gmd&ELEMENTSETNAME=full&Id=CZ-CUZK-WMTS-ORTOFOTO-P",
                        type: "ISO19115:2003",
                        format:"application/xml"
            },
            attribution : { // copyright
                logo: {
                        //width: 241,
                        //height: 81,
                        href: "/app/common/img/logo_cuzk.png"
                    },
                href: "http://geoportal.cuzk.cz/%28S%28eor5r5vr1qvcfbmfnsj2n4sj%29%29/Default.aspx?menu=3151&mode=TextMeta&side=wmts.uvod&metadataID=CZ-CUZK-WMTS-ORTOFOTO-P&metadataXSL=metadata.sluzba",
                title: "Český úřad zeměměřický a katastrální"
            }
    });

/*    var backgroundOrtho = new OpenLayers.Layer.WMS(OpenLayers.i18n("Ortho"),
            "http://apps.esdi-humboldt.cz/cgi-bin/tilecache/tilecache.cgi",
            {
                layers: "ceniaJTSK",
                format: "image/jpeg"
            },
            {
                isBaseLayer: true,
                displayInLayerSwitcher: true,
                visibility:false,
                buffer:1,
        wrapDateLine: true,
                resolutions: map.resolutions,
                transitionEffect:"resize"
            });*/

/*    var XXbackgroundTopo = new OpenLayers.Layer.WMS("Základní",
            "http://apps.esdi-humboldt.cz/tilecache/tilecache.py",
            {
                layers: "topoJTSK",
                format: "image/png"
            },
            {
                isBaseLayer: true,
                displayInLayerSwitcher: true,
                visibility:true,
                buffer:1,
        wrapDateLine: true,
                resolutions: map.resolutions,
                transitionEffect:"resize"
            });*/

/*    var backgroundTopoWMS = new OpenLayers.Layer.WMS(
         "Základní","http://apps.esdi-humboldt.cz/cgi-bin/tilecache/tilecache.cgi",
         {layers: 'ceniaTopoJTSK',format:"image/png"},
         {
             isBaseLayer: false,
             format: "image/png",
             displayInLayerSwitcher: true,
             visibility:false,
             buffer:0,
             transitionEffect:"resize",
             resolutions: map.resolutions.slice(7,25),
             saveWMC:false
         });*/

/*    var backgroundOrthoWMS = new OpenLayers.Layer.WMS(
         "Letecká","http://apps.esdi-humboldt.cz/cgi-bin/tilecache/tilecache.cgi",
         {layers: 'ceniaJTSK',format:"image/jpeg"},
         {
             displayInLayerSwitcher: true,
             isBaseLayer: false,
             format: "image/jpeg",
             visibility:false,
             buffer:0,
             transitionEffect:"resize",
             saveWMC:false,
             resolutions: map.resolutions.slice(6,25)
         });*/


    var labelsTopo = new OpenLayers.Layer.WMS(OpenLayers.i18n("Labels"),
            "http://apps.esdi-humboldt.cz/cgi-bin/tilecache/tilecache.cgi",
            {
                layers: "topoOverlayJTSK",
                format: "image/gif"
            },
            {
                isBaseLayer: false,
                displayInLayerSwitcher: true,
                visibility:true,
                buffer:1,
                resolutions: map.resolutions,
                transitionEffect:"resize"
            });

    var cuzk_kn = new OpenLayers.Layer.WMS(OpenLayers.i18n("Kadaster"),"http://wms.cuzk.cz/wms.asp",
                {layers: 'RST_KN,RST_KMD,dalsi_p_mapy,hranice_parcel,obrazy_parcel,parcelni_cisla', format:"image/gif", transparent:true},
                {visibility:false, buffer:0,
                displayInLayerSwitcher: true,
                transitionEffect:"resize", isBaseLayer: false, maxResolution: 5
            });

    // new layers
    var carHistory = new MapLog.Layer.History("Car History",
            "http://"+window.location.hostname+"/cgi-bin/maploghistory",
            {
                layers: "longHistory longHistoryPoint",
                map_imagetype: "png",
                IDS:"",
                SESSIONID:"",
                map_transparent: "ON"
            },
            {
                isBaseLayer: false,
                displayInLayerSwitcher: true,
                singleTile: true,
                visibility: false,
                opacity: 0.60,
                ratio:1,
                resolutions: map.resolutions,
                transitionEffect:"resize"
            });

    maplog.unitsLayer = new MapLog.Layer.UnitVector("Units2",dbservice+"/DataService",
            {visibility:true});


    map.addLayers([backgroundTopo,  backgroundOrtho, /*:labelsTopo,*/ cuzk_kn, carHistory, maplog.unitsLayer]);
    map.zoomToExtent(map.initialExtent);


    /*
     * controls
     */
    var ls = new HSLayers.Control.BoxLayerSwitcher();
    var mp = new OpenLayers.Control.MousePosition();
    map.addControl(ls);
    ls.div.style.width="300px"; // IE je zlo!!
    ls.add(OpenLayers.i18n("Topo"),[backgroundTopo],[], {});
//    ls.add(OpenLayers.i18n("Ortho"),[backgroundOrtho],[labelsTopo], {active: true});
    ls.add(OpenLayers.i18n("Ortho"),[backgroundOrtho],[], {active: true});
    ls.add(OpenLayers.i18n("More maps"),[],[cuzk_kn],{base:false,active:true});
//    ls.groups[OpenLayers.i18n("Ortho")].sublayers[0].activeWhenGroupActive = true;
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

    MapLog.UnitFeature.setDetailTemplate(document.location.href.replace("map.jsp","detailTemplate-"+HS.getLang()+".html?"+new String(Math.random())));

    maplog.unitSwitcher.getGroups();
    maplog.unitSwitcher.events.register("groupsadded",this,function(){maplog.unitsLayer.update();});

    selectFeature.activate();
};

