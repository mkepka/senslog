MapLog.namespace("MapLog.Layer"); 
MapLog.Layer.UnitVector = OpenLayers.Class(OpenLayers.Layer.Vector, {
    /** APIProperty: interval
     * refresh the layer in N seconds
     * {Integer}
     */
    interval: 10,

    /** APIProperty: inAlert
     *  some unit indicated alert
     * {Boolean}
     */
    inAlert: false,

    /** Property: globalName
     * {String}
     */
    globalName: null,

    /**
     * form, which will send error message to pastebin 
     * @private
     * @type HTMLDOM
     */
    _pasteForm: undefined,

    /**
     * number of failed attemps for loading the data
     * @type {Integer}
     */
    _failedAttempts: 0,

    /**
     * Operation parameter for the request URL
     * @type {String}
     */
    operation: "GetUnits",

    /**
     * maximal number of failed attemps for loading the data
     * @type {Integer}
     */
    maxFailedAttempts: 2,
    
    /** Property: loadEndEvent
     * {String}
     * "loadend" first "updated" later
     */
    loadEndEvent: "loadend",

    /** Property: historyLayer
     * {OpenLayers.Layer.MapServer}
     * mapserver map with car history
     */
    historyLayer: undefined,

    /**
     * Propety: styleMap
     */
    styleMap:  undefined,

    /** Property: url
     * {String}
     * url where to get data from
     */
    url: undefined,

    /**
     * Constant: EVENT_TYPES
     */
    EVENT_TYPES: ["updated","loadend","loadstart","hasalert","noalert","ok","failed"],

    /**
     * Constant: RFID_SENSOR
     */
    RFID_SENSOR: 680010000,
    
    /**
     * Constructor: OpenLayers.Layer.Vector
     * Create a new vector layer
     *
     * Parameters:
     * name - {String} A name for the layer
     * options - {Object} Optional object with non-default properties to set on
     *           the layer.
     *
     * Returns:
     * {<OpenLayers.Layer.Vector>} A new vector layer
     */
    initialize: function(name, url, options) {

        // concatenate events specific to vector with those from the base
        this.EVENT_TYPES = MapLog.Layer.UnitVector.prototype.EVENT_TYPES.concat(
            OpenLayers.Layer.Vector.prototype.EVENT_TYPES
        );


        if (!options.styleMap) {
            options.styleMap=new OpenLayers.StyleMap({
                    pointRadius: "12", // sized according to type attribute
                    backgroundGraphic: undefined,
                    externalGraphic: "icons/car.png",
                    smallIcon: "icons/car-small.png",
                    fillColor: "#ffcc66",
                    strokeColor: "#ff9933",
                    strokeWidth: 2,

                    label:"${label}",
                    labelBackgroundColor: "#fefefe",
                    fontOpacity: 0.4,
                    labelAlign: "l",
                    labelYOffset: 0,
                    labelXOffset: 16,
                    fontColor: "#000000",
                    fontSize: "10px",
                    fontFamily: "sans-serif",
                    fontWeight: "normal",

                    backgroundXOffset: -12,
                    backgroundYOffset: -24,
                    backgroundGraphicZIndex: 2
                    }); 

             options.styleMap.addUniqueValueRules("default","active",{
                     "true": { pointRadius: "16", // sized according to type attribute
                             externalGraphic: "icons/car-selected.png",
                             fontOpacity: 0.9,
                            fontWeight: "bolder",
                            fontSize: "11px",
                             smallIcon: "icons/car-small.png"}
                     });

             options.styleMap.addUniqueValueRules("select","active",{
                     "true": { pointRadius: "16", // sized according to type attribute
                             externalGraphic: "icons/car-selected.png",
                             fontOpacity: 0.9,
                            fontWeight: "bolder",
                                fontSize: "11px",
                             smallIcon: "icons/car-small.png"}
                     });

             options.styleMap.addUniqueValueRules("default","hasAlert",{
                     "true": { pointRadius: "12", // sized according to type attribute
                             backgroundGraphic: "icons/alert.gif"},
                     "false": {
                             pointRadius: "12", // sized according to type attribute
                             backgroundGraphic: null }

                     });

             options.styleMap.addUniqueValueRules("select","hasAlert",{
                     "true": { pointRadius: "12", // sized according to type attribute
                             backgroundGraphic: "icons/alert.gif"},
                     "false": {
                             pointRadius: "12", // sized according to type attribute
                             backgroundGraphic: null }

                     });


            options.styleMap.addUniqueValueRules("default","is_moving",{
                     "true": { pointRadius: "12", // sized according to type attribute
                             externalGraphic: "icons/car-moving.png",
                             smallIcon: "icons/car-small-moving.png"}
                     });

            options.styleMap.addUniqueValueRules("select","is_moving",{
                     "true": { pointRadius: "12", // sized according to type attribute
                             externalGraphic: "icons/car-moving.png",
                             smallIcon: "icons/car-small-moving.png"}
                     });

            options.styleMap.addUniqueValueRules("default","is_online",{
                     "false": { pointRadius: "12", // sized according to type attribute
                             externalGraphic: "icons/car-offline.png",
                             smallIcon: "icons/car-small-offline.png"}
                     });

            options.styleMap.addUniqueValueRules("select","is_moving",{
                     "false": { pointRadius: "12", // sized according to type attribute
                             externalGraphic: "icons/car-offline.png",
                             smallIcon: "icons/car-small-offline.png"}
                     });

            options.styleMap.addUniqueValueRules("default","visibility",{
                     "false": { display: "none" } // sized according to type attribute
                     });

            options.styleMap.addUniqueValueRules("select","visibility",{
                     "false": { display: "none" } // sized according to type attribute
                     });

        }

        if (!options.renderOptions) {
            options.renderOptions = {yOrdering: true}
        }

        OpenLayers.Layer.Vector.prototype.initialize.apply(this, [name,options]);
        this.events.addEventType("noalert");
        this.events.addEventType("hasalert");
        this.events.addEventType("ok");
        this.events.addEventType("failed");
        this.events.addEventType("updated");
        this.events.addEventType("loadend");
        this.events.addEventType("loadstart");

        this.url = url;

        this.sourceProjection = new OpenLayers.Projection("epsg:5514");

    },

    /**
     * setMap
     */
    setMap: function(map) {

        OpenLayers.Layer.prototype.setMap.apply(this, arguments);

        if (!this.renderer) {
            this.map.removeLayer(this);
        } else {
            this.renderer.map = this.map;
            this.renderer.setSize(this.map.getSize());
        } 


        this.map.events.register("addlayer",this,function() {
                this.map.setLayerIndex(this,this.map.layers.length);
                });
        
        this.events.on({"featureselected": this.onFeatureSelected,
                              "featureunselected": this.onFeatureUnSelected,
                            scope:this});
    },

    /**
     * Method: update
     */
    update: function () {

        this.events.triggerEvent("loadstart");
        OpenLayers.Request.GET({
            url: this.url,
            params: {salt: Math.random(),
                Operation: this.operation
            },
            success: this.parseData,
            failure: function(xhr) {
                this._reload();
                this._onFail(xhr);
            },
            scope: this
        });
    },

    destroy: function() {

        this.events.un({"featureselected": maplog.unitsLayer.onFeatureSelected,
                              "featureunselected": maplog.unitsLayer.onFeatureUnSelected,
                            scope:this});

        OpenLayers.Feature.Vector.prototype.destroy.apply(this,arguments);

    },

    /**
     * Method: parseData
     *
     * Parameters:
     * ajaxRequest - {<OpenLayers.Request.XMLHttpRequest>} 
     */
    parseData: function(ajaxRequest) {
        
        var options = {};
        this.inAlert = false;
        
        OpenLayers.Util.extend(options, this.formatOptions);

        if (this.map && !this.projection.equals(this.map.getProjectionObject())) {
            options.externalProjection = this.projection;
            options.internalProjection = this.map.getProjectionObject();
        }    


        var units = this._getUnits(ajaxRequest);
        if (units && units.length && units.length > 0) {
            this._failedAttempts = 0;
            for (var i=0, len=units.length; i<len; i++) {
                var unit = units[i];
                
                // new feature -> unit.lastpos.attributes
                // old feature -> unit.position AND unit.attributes
                var position = unit.lastpos ? unit.lastpos.position : unit.position;
                var attributes = unit.lastpos ? unit.lastpos.attributes : unit.attributes;
                var alertEvents = unit.lastpos ? unit.lastpos.alertEvents : unit.alertEvents;

                var id = position.unit_id;
                var feature = this.getFeatureByUnitId(id);

                // prvni nacteni
                if (feature === undefined) {

                    // skip units withnout position
                    if (!position) {
                        continue;
                    }

                    unit.active = "false";
                    unit.select = "false";
                    unit.attributes = attributes;
                    unit.attributes.is_moving = attributes.is_moving ? "true" : "false";
                    unit.attributes.ignition_on = attributes.ignition_on ? "true" : "false";
                    if (MAPLOG_DEBUG === true) {
                        console.log("new",attributes.is_online);
                    }
                    unit.attributes.is_online = attributes.is_online ? "true" : "false";
                    unit.attributes.hasAlert = "false";
                    unit.attributes.alertEvents = alertEvents;
                    unit.attributes.unit = unit.unit;
                    unit.position = position;
                    if (unit.attributes.alertEvents && unit.attributes.alertEvents.length) {
                        for (var j = 0; j < unit.attributes.alertEvents.length;j++) {
                            if (unit.attributes.alertEvents[j].solving === false) {
                                unit.attributes.hasAlert = "true";
                                this.inAlert = true;
                                this.events.triggerEvent("hasalert");
                                break;
                            }
                        }
                    }

                    // try to find if unit has RFID sensor
                    if(unit.sensors && unit.sensors.length){
                        for(var k = 0; k<unit.sensors.length ; k++){
                            var sensor = unit.sensors[k];
                            if (sensor.sensorId == MapLog.Layer.UnitVector.prototype.RFID_SENSOR){
                                unit.attributes.has_rfid = "true";
                            }
                            else{
                                unit.attributes.has_rfid = "false";
                            }
                        }
                    }
                    else{
                        unit.attributes.has_rfid = "false";
                    }
                    
                    unit.visibility = "true";

                    var geometry = new OpenLayers.Geometry.Point(position.x, position.y);

                    // data are comming in EPSG:5514 , we have to transform
                    // them to map projection
                    if (this.map) {
                        if (this.map.baseLayer.projection.getCode() != this.sourceProjection.getCode()) {
                            geometry.transform(this.sourceProjection,this.map.baseLayer.projection);
                        }
                    }

                    feature = new MapLog.UnitFeature(geometry,unit,null,this.historyLayer);
                    feature.events.register("statechanged",this,this.onFeatureStateChanged);

                    this.addFeatures([feature]);

                    unit.position.time_stamp = this.formatTimeStamp(position.time_stamp);
                    unit.attributes.alertEvents.reverse();
                    feature.updateAttributes(attributes);

                }
                else {
                    var lonlat = new OpenLayers.LonLat(unit.position.x, unit.position.y);
                    if (this.map) {
                        if (this.map.baseLayer.projection.getCode() != this.sourceProjection.getCode()) {
                            lonlat.transform(this.sourceProjection,this.map.baseLayer.projection);
                        }
                    }

                    var unitPosition = {location : lonlat,
                                    speed: unit.position.speed,
                                    time_stamp : this.formatTimeStamp(unit.position.time_stamp)};

                    unit.attributes.is_moving = unit.attributes.is_moving ? "true" : "false";
                    unit.attributes.ignition_on = unit.attributes.ignition_on ? "true" : "false";
                    if (MAPLOG_DEBUG === true) {
                        console.log("existing",unit.attributes.is_online);
                    }
                    unit.attributes.is_online = unit.attributes.is_online ? "true" : "false";
                    unit.attributes.alertEvents = unit.alertEvents;
                    unit.attributes.alertEvents.reverse();
                    unit.attributes.hasAlert = "false";
                    
                    //description of RFID devices
                    //unit.attributes.rfid_name = "vehicle";

                    for (var j = 0; j < unit.alertEvents.length;j++) {
                        if (unit.alertEvents[j].solving === false) {
                            unit.attributes.hasAlert = "true";
                            this.inAlert = true;
                            this.events.triggerEvent("hasalert");
                            break;
                        }
                    }
                    if (unit.attributes) {
                        feature.updateAttributes(unit.attributes);
                    }
                    feature.updatePosition(unitPosition);
                }

            }

        }

        if (this.inAlert === false) {
            this.events.triggerEvent("noalert");
        }

        this.events.triggerEvent(this.loadEndEvent);
        this.loadEndEvent = "updated";

        // if ther eis only one feature, activate
        if (this.featuers && this.features.length == 1 && this.features[0].active == false) {
            this.features[0].setActive(true);
        }

        this.loaded = true;

        // if everything OK for the first time load, change Operation
        // parameter
        if (units && units.length && units.length > 0) {
            this.operation ="GetLastPositionsWithStatus";
            this.events.triggerEvent("ok");
        }
        else {
            this.events.triggerEvent("failed");
        }
        // reload after N seconds
        this._reload();
    },

    /**
     * APIMethod: formatTimeStamp
     *
     * Parameters:
     * time - {String}
     *
     * Returns
     * {String} german time format
     */
    formatTimeStamp: function(time) {
        var d = [parseInt(time.split("-")[0],10),parseInt(time.split("-")[1],10),parseInt(time.split("-")[2],10)];
        var str = d[2]+"."+d[1]+"."+d[0]+"&nbsp;";
        str += time.split(" ")[1].split("+")[0].replace(/\..*/,"")+"&nbsp;";
        return str;
    },

    /**
     * APIMethod: deactivateAllUnits
     */
    deactivateAllUnits: function() {
        for (var i = 0; i < this.features.length; i++) {
            this.features[i].setActive(false);
        }
    },


    /**
     * APIMethod: addMarker
     *
     * Parameters:
     * marker - {<OpenLayers.Marker>} 
     */
    addMarker: function(marker) {
        OpenLayers.Layer.Text.prototype.addMarker.apply(this, arguments);

        this.events.triggerEvent("markeradded");
    },

    /**
     * APIMethod: getFeatureById
     * GetFeature byId
     * Parameters:
     * id - {String}
     * 
     * Returns:
     * {Object} feature
     */
    getFeatureByUnitId: function(id) {
        for (var i = 0; i < this.features.length; i++) {
            if (this.features[i].unit_id == id)
                return this.features[i];
        }
        return;

    },

    /**
     * APIMethod: markerClick
     * Marker clicked
     *
     * Parameters:
     * Returns:
     */
    markerClick: function(e) {

        return;
    },

    /**
     * display the popups to each unit with attributes
     * @name OpenLayers.Layer.UnitLayer.displayLabels
     * @param [String] attributes list of attributes, accepted values are name, date,
     * time
     */
    displayLabels: function(attributes) {
        for (var i = 0; i < this.features.length; i++) {    
            this.features[i].displayLabel(attributes,this.map);
        }
        return;
    },


    /**
     * Set history layer
     * @name MapLog.Layer.UnitLayer.setHistoryLayer
     * @param {MapLog.Layer.CarHistory} layer
     */
    setHistoryLayer: function(layer) {
        this.historyLayer = layer;
    },

    /**
     * activateUnit
     */
    activateUnit: function(feature) {
        feature.setActive(true);
    },

    onFeatureSelected: function(e){
        this.activateUnit(e.feature);
    },

    onFeatureStateChanged: function(e) {
        var feature = e["object"];
        if (feature.attributes.active === "true")  {
            var idx = this.features.indexOf(feature);
            // selected feature first
            this.features = [this.features[idx]].concat(this.features.slice(0,idx),this.features.slice(idx+1,this.features.length));
            this.redraw();
        }
    },

    /**
     * parse units from input string
     * @function
     * @private
     */
    _getUnits: function(xhr) {

        var format = new OpenLayers.Format.JSON({keepData: true});
        var units = format.read(xhr.responseText);

        if (units === null) {
            this._failedAttempts += 1;
        }

        else if (units.length && units.length === 0) {
            this._failedAttempts += 1;

        }
       
        // if there are too many failed attemps
        if (this._failedAttempts > this.maxFailedAttempts) {
            this._failedAttempts = 0;
            this._onFail(xhr);
        }
        // give it another try
        else {
            if (units && units.length && units.length > 0) {
                this._failedAttempts = 0;
            }
            else {
                this._submitErrorReport(xhr);
            }
            return units;
        }
    },

    /**
     * on request failed
     * @function
     * @private
     */
    _onFail : function(xhr) {
    
        this._submitErrorReport(xhr);

        var handleWindow = function(id, text, opt) {
            if (id == "ok") {
                window.location.reload(true);
            }
            else {
            }
        };

        this.events.triggerEvent("updated");
        this.events.triggerEvent("failed");

        Ext.Msg.show({
            title: "Chyba načítán pozic",
            icon: Ext.Msg.ERROR,
            msg: "Při načítání pozic došlo k chybě mezi serverem a mapou. <br />"+
                    "Pravděpodobně došlo k odhlášení",
            fn: handleWindow,
            scope:this,
            buttons: {
                ok: "Obnovit",
                cancel: "Pokračovat"
            }
            
        });
    },

    /**
     * create paste form, which will submit the ERROR message data to
     * paste2.org
     * @private
     * @function
     */
    _submitErrorReport: function(xhr) {

        if (!this._pasteForm) {

            var frame = document.createElement("iframe");
            frame.style.display = "none";
            frame.id="_pasteFormFrame";
            document.body.appendChild(frame);

            this._pasteForm = document.createElement("form");
            this._pasteForm.setAttribute("target","_pasteFormFrame");
            this._pasteForm.setAttribute("action","/cgi-bin/maplogFailLog.cgi");
            this._pasteForm.setAttribute("method","POST");
            this._pasteForm.style.display = "none";

            var time = document.createElement("input");
            time.setAttribute("name","time");

            var code = document.createElement("input");
            code.setAttribute("name", "code");

            var headers = document.createElement("input");
            headers.setAttribute("name","headers");

            this._pasteForm.appendChild(code);
            this._pasteForm.appendChild(time);
            this._pasteForm.appendChild(headers);
            document.body.appendChild(this._pasteForm);
        }

        var date = new Date();
        this._pasteForm.time.value = date.toString();
        this._pasteForm.code.value = xhr.responseText;
        this._pasteForm.headers.value = xhr.getAllResponseHeaders();
        this._pasteForm.submit();
    },


    /**
     * reload units
     * @function
     * @private
     */
    _reload: function() {
        /* refresh after N seconds*/
        if (!this.globalName) {
            var ar = [];
            for (var i in OpenLayers.Layer.UnitLayers) {
                ar.push(i);
            }
            this.globalName = "UnitLayer"+ar.length;
            OpenLayers.Layer.UnitLayers[this.globalName] = this;
        }
        // reload in ANY case
        window.setTimeout("OpenLayers.Layer.UnitLayers['"+this.globalName+"'].loaded = false;"+
                        "OpenLayers.Layer.UnitLayers['"+this.globalName+"'].update()",this.interval*1000);
    },

    CLASS_NAME: "MapLog.Layer.UnitLayer"

});

OpenLayers.Layer.UnitLayers = {};
