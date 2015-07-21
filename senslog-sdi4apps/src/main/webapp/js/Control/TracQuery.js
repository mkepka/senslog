MapLog.namespace("MapLog.Control");
MapLog.Control.TracQuery = OpenLayers.Class(OpenLayers.Control,{

    /**
     * default handler configuration options
     */
    defaultHandlerOptions: {
        'single': true,
        'double': false,
        'pixelTolerance': 0,
        'stopSingle': false,
        'stopDouble': false
    },

    /**
     * click handler
     */
    handler: undefined,

    /**
     * size
     */
    size:  new OpenLayers.Size(300,200),

    /**
     * query layer
     */
    qlayer: undefined,

    /**
     * ext container instead of popup
     */
    extContainer: undefined,

    /**
     * print results to popup
     */
    usePopup: true,

    /**
     * popup class
     */
    popupClass: OpenLayers.Popup.FramedCloud,

    /**
     * Contructor: OpenLayers.Control.UnitSwitcher
     * Switcher for MapLog units
     *
     * Parameters:
     * layer - {OpenLayers.Layer.UnitLayer}
     * qlayer - {String} query layer name
     * options - {Object} configuration options
     */
    initialize: function(layer,qlayer,options) {
        OpenLayers.Control.prototype.initialize.apply(this, [options]);
        
        this.handlerOptions = OpenLayers.Util.extend(
                        {}, this.defaultHandlerOptions
                    );

         this.handler = new OpenLayers.Handler.Click(
                        this, { 'click': this.trigger
                        }, this.handlerOptions);


        this.qlayer = qlayer;
        this.layer = layer;

    },

    /**
     * Triger click action
     */
    trigger: function(e) {
        var feature = this.unitsLayer.getFeatureFromEvent(e);
        var lonlat = this.map.getLonLatFromPixel(e.xy);
        var bounds = this.map.getExtent();
        var size = this.map.getSize();

        if (this.popup) {
            this.map.removePopup(this.popup);
            this.popup.destroy();
        }

        if (!feature) {
            if (!this.layer.params.IDS) {
                return;
            }
            // var url = this.layer.getFullRequestString({mode:"query",
            //                                             qlayer:(this.qlayer ? this.qlayer : this.layer.params.layers),
            //                                             "map_projection": this.map.projection.getCode(),
            //                                             "imgxy": e.xy.x+" "+e.xy.y,
            //                                             "mapsize": size.w+" "+size.h,
            //                                             "imgext":bounds.left+" "+bounds.bottom+" "+bounds.right+" "+bounds.top
            //                                             });

            var params = {
                IDS: this.layer.params.IDS,
                fromTime: this.layer.params.fromTime,
                toTime: this.layer.params.toTime,
                EPSG: this.map.projection.getCode().split(":")[1],
                xclick: lonlat.lon,
                yclick: lonlat.lat,
                imgext: bounds.left+" "+bounds.bottom+" "+bounds.right+" "+bounds.top
            };

            var url = OpenLayers.Util.urlAppend(MapLog.Control.TracQuery.PositionDetailURL,
                                OpenLayers.Util.getParameterString(params));

            this.popup = new this.popupClass(undefined, lonlat,
                                                        this.size,
                                                        "<img src=\""+OpenLayers.Util.getImagesLocation()+"/indicator.gif\" />",
                                                        null,
                                                        true);

            this.map.addPopup(this.popup);

            OpenLayers.Request.GET({
                url: url,
                scope: this,
                success: this.displayResult
            });

        }
        else {
            var temp_cze = '<dl>'+
                '<tpl for="holder">'+
                    '<dd style="background: #DEDEDE">Firma:</dd><dt style="background: #DEDEDE">{holderName} &nbsp;</dt>'+
                    '<dd>Telefon: </dd> <dt>{phone} &nbsp;</dt>'+
                '</tpl>'+
                '<tpl for="generalInfo"> <tpl for="properties">'+
                        '<dd style="background: #DEDEDE">SPZ: </dd> <dt style="background: #DEDEDE">{spz} &nbsp;</dt>'+
                        '<dd>Typ: </dd> <dt>{typ} &nbsp;</dt>'+
                        '<dd style="background: #DEDEDE">Model: </dd> <dt style="background: #DEDEDE">{model} &nbsp;</dt>'+
                '</tpl></tpl>'+
                '<tpl for="position">'+
                    '<dd>Rychlost: </dd> <dt>{speed} km/h &nbsp;</dt>'+
                    '<dd style="background: #DEDEDE">Poslední pozice: </dd> <dt style="background: #DEDEDE">{time_stamp} {xwgs84},{ywgs84} &nbsp;</dt>'+
                    '<dd>Klíček: </dd> <dt>{ignition_on} &nbsp;</dt>'+
                '</tpl>'+
                '<tpl for="attributes">'+
                    '<dd style="background: #DEDEDE">RFID: </dd> <dt style="background: #DEDEDE">{rfid_desc} &nbsp;</dt>'+
                '</tpl>'+
            '</dl>'+
            '<h4>Řidiči:</h4>'+
            '<ul>'+
                '<tpl for="drivers">'+
                    '<li><dl>'+
                            '<dd>Jméno: </dd><dt>{title} {fname} {lname} &nbsp;</dt>'+
                            '<dd>Telefon: </dd><dt>{phone} &nbsp;</dt>'+
                    '</dl></li>'+
                '</tpl>'+
            '</ul>';

            var temp_eng = '<dl>'+
            '<tpl for="holder">'+
                '<dd style="background: #DEDEDE">Company:</dd><dt style="background: #DEDEDE">{holderName} &nbsp;</dt>'+
                '<dd>Voice phone: </dd> <dt>{phone} &nbsp;</dt>'+
            '</tpl>'+
            '<tpl for="generalInfo"> <tpl for="properties">'+
                    '<dd style="background: #DEDEDE">Reg No.: </dd> <dt style="background: #DEDEDE">{spz} &nbsp;</dt>'+
                    '<dd>Type: </dd> <dt>{typ} &nbsp;</dt>'+
                    '<dd style="background: #DEDEDE">Model: </dd> <dt style="background: #DEDEDE">{model} &nbsp;</dt>'+
            '</tpl></tpl>'+
            '<tpl for="position">'+
                '<dd>Speed: </dd> <dt>{speed} km/h &nbsp;</dt>'+
                '<dd style="background: #DEDEDE">Last position: </dd> <dt style="background: #DEDEDE">{time_stamp} {xwgs84},{ywgs84} &nbsp;</dt>'+
                '<dd>Ignition: </dd> <dt>{ignition_on} &nbsp;</dt>'+
            '</tpl>'+
            '<tpl for="attributes">'+
                '<dd style="background: #DEDEDE">RFID: </dd> <dt style="background: #DEDEDE">{rfid_desc} &nbsp;</dt>'+
            '</tpl>'+
        '</dl>'+
        '<h4>Drivers:</h4>'+
        '<ul>'+
            '<tpl for="drivers">'+
                '<li><dl>'+
                        '<dd>Name: </dd><dt>{title} {fname} {lname} &nbsp;</dt>'+
                        '<dd>Voice phone: </dd><dt>{phone} &nbsp;</dt>'+
                '</dl></li>'+
            '</tpl>'+
        '</ul>';
            
            var template = temp_eng;
            if (HS.getLang() == "cze"){
                template = temp_cze;
            }
            if (this.usePopup) {
                this.popup = new this.popupClass(undefined, lonlat,
                                                            this.size,
                                                            MapLog.UnitFeature.processDetailTemplate(feature.attributes,template),
                                                            null,
                                                            true);

                this.map.addPopup(this.popup);
                this.popup.setSize(this.size);
            }
            else if (this.extContainer) {
                this.extContainer.body.update(MapLog.UnitFeature.processDetailTemplate(feature.attributes,template));
            }
        } // /if feature


        OpenLayers.Event.stop(e,true);
    },

    /**
     * Display Popup
     */

    displayResult: function(httprequest){
        //var jsonFormat = new OpenLayers.Format.JSON();
        //var resp = jsonFormat.read(httprequest.responseText);
        var resp = eval("("+httprequest.responseText+")");

        var template;
        if (resp[0].status == "success") {

            template = '<b>Údaje o bodu na trase</b><br/><dl>'+
                    '<dd style="background: #DEDEDE">Název:</dd><dt style="background: #DEDEDE">{unit_description} &nbsp;</dt>'+
                    '<dd>Čas: </dd> <dt>{time_stamp} &nbsp;</dt>'+
                    '<dd style="background: #DEDEDE">Pozice: </dd> <dt style="background: #DEDEDE">{lat} {lon}&nbsp;</dt>'+
                    '<dd>Rychlost: </dd> <dt>{speed} km/h &nbsp;</dt>'+
                    '<dd style="background: #DEDEDE">Klíček: </dd> <dt style="background: #DEDEDE">{ignition_status} &nbsp;</dt>'+
                '</dl>';

            resp[0].obj.ignition_status = (resp[0].obj.ignition_status === 0 ? "Vypnuto" : "Zapnuto");
        }
        else {
            template = "Nothing found";
        }

        if (this.popup) {
            this.popup.setContentHTML(MapLog.UnitFeature.processDetailTemplate(resp[0].obj,template));
            this.popup.setSize(new OpenLayers.Size(300,120));
            var px = this.map.getLayerPxFromLonLat(new OpenLayers.LonLat(resp[0].obj.x, resp[0].obj.y));
            this.popup.moveTo(px);
        }
        else if (this.extContainer) {
            this.extContainer.body.update(MapLog.UnitFeature.processDetailTemplate(resp[0].obj,template));
            this.extContainer.doLayout();
        }
    },

    CLASS_NAME: "MapLog.Control.TracQuery"

});

MapLog.Control.TracQuery.PositionDetailURL = "/maplog/tools/position_detail.php";
