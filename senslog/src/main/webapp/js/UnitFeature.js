MapLog.UnitFeature = OpenLayers.Class(OpenLayers.Feature.Vector, {
    /**
     * Constant: EVENT_TYPES
     * {Array(String)} Supported application event types.  Register a listener
     *     for a particular event with the following syntax:
     * Supported map event types:
     * visibilitychanged - Triggered when unit visbility changed
     * statechanged - Triggered when unit sate changed
     * moved - Trigger when unit moved
     */
    EVENT_TYPES: ["visibilitychanged", "statechanged", "moved","alertchange","positionupdated","attributesupdated"],

    /**
     * APIProperty: trac
     * {OpenLayers.Geometry}
     */
    trac: null,

    /**
     * APIProperty: marker
     * {OpenLayers.Marker}
     */
    marker: null,

    /**
     * APIProperty: followMap
     * always set center to this feature
     * {Boolean}
     */
    followMap: true,

    /**
     * feature attributes
     * {OpenLayers.Geometry}
     */
    attributes: null,

    /**
     * APIProperty: infoDiv
     * {DOMElement}
     */
    infoDiv: null,

    /**
     * APIProperty: active
     * {Boolean}
     */
    active: false,

    /**
     * APIProperty: detailMarker
     * {OpenLayers.Marker}
     * Marker in the detail map
     */
    detailMarker: null,

    /**
     * APIProperty: detailPanel
     * {Ext.Panel}
     * Panel with informations in the detailPanel
     */
    detailPanel: null,

    /**
     * APIProperty: state
     * {String}
     */
    state: null,

    /**
     * APIProperty: sensors
     * {Object}
     */
    sensors: {},

    /**
     * APIProperty: alertvalue
     * {Boolean}
     */
    alertValue: false,

    /**
     * APIProperty: time
     * {Date}
     */
    time: {},

    /**
     * APIProperty: hoverLabel label for mouse over
     * {OpenLayers.Label}
     */
    hoverLabel: null,


    /** 
     * Constructor: OpenLayers.UnitFeature
     * Constructor for units.
     *
     * Parameters:
     * geomtry - {<OpenLayers.Geometry>} 
     * attributes - {Object} 
     * style - {<OpenLayers.Style>} 
     * historyLayer - {OpenLayers.Layer.MapServer}
     * options
     * 
     * Returns:
     * {<OpenLayers.Feature>}
     */
    initialize: function(geometry, attributes, style, historyLayer,options) {

        OpenLayers.Feature.Vector.prototype.initialize.apply(this,arguments);
        OpenLayers.Util.extend(this, options);
        this.unit_id = attributes.position.unit_id;
        
        this.events = new OpenLayers.Events(this, this.div, 
                                            this.EVENT_TYPES);
        if(this.eventListeners instanceof Object) {
            this.events.on(this.eventListeners);
        }

        if (this.style) {
            var size = new OpenLayers.Size(this.style.pointRadius*2,this.style.pointRadius*2);
            var offset = new OpenLayers.Pixel(-(size.w/2), -size.h/2);
            var icon = new OpenLayers.Icon(this.style.externalGraphic,size,offset);
        }
        else {
            var size = new OpenLayers.Size(24,24);
            var offset = new OpenLayers.Pixel(-(size.w/2), -size.h/2);
            var icon = new OpenLayers.Icon("icons/car.png",size,offset);
        }

        this.detailMarker = new OpenLayers.Marker(new OpenLayers.LonLat(this.geometry.x, this.geometry.y), icon);
        this.detailMarker.feature = this;
    },


    /**
     * APIMethod: setState
     *
     * Parameters:
     * Returns:
     */
    setState: function() {

        return;
    },

    /**
     * APIMethod: getHoverLabelContent
     *
     * Parameters:
     * Returns: {String}
     */
    getHoverLabelContent: function() {
        if (this.attributes.holder) {
            return "<b>Jméno: </b>"+this.attributes.holder.fname +" "+this.attributes.holder.lname+"<br />"+
                    "<b>Telefon: </b>"+this.attributes.holder.phone;
        }
        else {
            return new String(this.unit_id);
        }
    },

    /**
     * APIMethod: moveTo
     *
     * Parameters:
     * lonlat - {OpenLayers.LonLat} new positon
     * Returns:
     */
    moveTo: function(lonlat) {

        var px = this.layer.map.getLayerPxFromLonLat(lonlat);

        var moved = false;

        if (this.hoverLabel) {
            this.hoverLabel.lonlat = lonlat;
            this.hoverLabel.updatePosition();
        }

        if (this.attributes.label) {
            this.displayLabel(this.labelAttributes,this.layer.map);
            //this.label.lonlat = lonlat;
            //this.label.updatePosition();
        }
        var lastPixel = this.layer.getViewPortPxFromLonLat(this.geometry.getBounds().getCenterLonLat());

        var res = this.layer.map.getResolution();

        var prevPixel = this.move(lonlat);
        
        this.events.triggerEvent("moved");

        if (this.detailMarker && this.detailMarker.map) {
            var px = this.detailMarkerLayer.map.getLayerPxFromLonLat(lonlat);
            this.detailMarker.moveTo(px);

            if (this.detailLabel) {
                this.detailLabel.lonlat = lonlat;
                this.detailLabel.updatePosition();
            }
        }


        return;
    },
    /**
     * APIMethod: getPositionHistory
     *
     * Parameters:
     * begin {Date}
     * end {Date}
     *
     * Returns:
     * {Object}
     */
    getPositionHistory: function(begin,end) {

        return;
    },

    /**
     * APIMethod: showPositionHistory
     *
     * Parameters:
     * begin - {Date}
     * end - {Date}
     *
     * Returns:
     */
    showPositionHistory: function(begin,end) {

        return;
    },

    /**
     * APIMethod: getSensorsHistory
     *
     * Parameters:
     * phenomenons - {Array} of {String}s
     * begin - {Date}
     * end - {Date}
     *
     * Returns:
     * {Object}
     */
    getSensorsHistory: function(phenomenons, begin, end) {

        return;
    },

    /**
     * APIMethod: showSensorsHistory
     *
     * Parameters:
     * phenomenons - {Array} of {String}s
     * begin - {Date}
     * end - {Date}
     *
     * Returns:
     */
    showSensorsHistory: function(phenomenons, begin, end) {

        return;
    },

    /**
     * APIMethod: getSensorValue
     *
     * Parameters:
     * phenomenon {String} 
     * time - {Date} - optional, 
     *
     * Returns:
     * {Object}
     */
    getSensorValue: function(phenomenon, time) {

        return;
    },

    /**
     * APIMethod: updatePosition
     *
     * Parameters:
     * position - {Object}
     *
     * Returns:
     */
    updatePosition: function(position) {
         
        var curPos = new OpenLayers.LonLat(this.geometry.x, this.geometry.y);
        if (!position.location.equals(curPos)) {
            this.moveTo(position.location);
            for (var i in position) {
                this.attributes.position[i] = position[i];
            }
        }

        if (this.label) {
            this.displayLabel(this.labelAttributes,this.layer.map);
        }
        //if (this.detailLabel) {
        //    this.displayDetailLabel(this.detailLabel.attributes,this.detailLabel.map);
        //}
        this.events.triggerEvent("positionupdated");
        return;
    },

    /**
     * APIMethod: updateAttributes
     *
     * Parameters:
     * attributes - {Object}
     * silent - {Boolean}
     *
     * Returns:
     */
    updateAttributes: function(attributes,silent) {
                          //
        if (!this.attributes) {
            this.attributes = {};
        }

        for (var i in attributes) {
            this.attributes[i] = attributes[i];
        }

        this.setAlert(this.attributes.hasAlert === "true");

        var style = this.layer.styleMap.styles["default"].createSymbolizer(this);
        if (this.detailMarker) {
            this.detailMarker.icon.setUrl(style.externalGraphic);
        }

        if (silent !== true) {
            this.events.triggerEvent("attributesupdated");
        }

        this.updateStyle();

        return;
    },

    /**
     * APIMethod: updateTrac
     *
     * Parameters:
     * Returns:
     */
    updateTrac: function() {

        return;
    },

    /**
     * APIMethod: setVisibility
     *
     * Parameters:
     * visibility
     *
     * Returns:
     */
    setVisibility: function(visibility,options) {
        visibility = (visibility ? "true" : "false");
        if (this.attributes.visibility != visibility){
            this.attributes.visibility = visibility;
            this.updateStyle();
            if (options && options.silently != true) {
                this.events.triggerEvent("visibilitychanged");
            }
        }

        return;
    },

    /**
     * APIMethod: toggleInfo
     * @deprecated
     *
     * Parameters:
     * display - {Boolean}
     *
     * Returns:
     */
    displayInfo: function(display) {
        var id = this.unit_id + "_label";
        var anchor = (this.marker) ? this.marker.icon : null;

        if (!this.label) {
            this.label = new OpenLayers.Popup(id, 
                                                new OpenLayers.LonLat(this.geometry.x, this.geometry.y),
                                                new OpenLayers.Size(42,14),
                                                this.unit_id, false);
            this.label.contentDiv.className = "MapLog-featurePopup";
            this.label.feature = this;
            this.layer.map.addPopup(this.label);
        }    

        // display
        if (display) {
            this.label.show();
        }
        // hide
        else {
            this.label.hide();
        }
        return;
    },

    /**
     * display attributes string in label
     * @param {String} attributes attributes to be displayed
     * @param {OpenLayers.Popup} label existing label (or null, if it was not created yet
     * @param {Boolean} use the label for detail map? default: false
     * @returns {OpenLayers.Popup} label
     */
    getLabel: function(attributes) {

        var id = this.unit_id + "_label";

        var str = "";

        for (var i = 0; i < attributes.length; i++) {
            switch(attributes[i]){
                case "name": str += this.getTitle() +" ";
                                if (attributes.length > 1) {
                                    str += "| ";
                                }
                                break;
                case "date": str += this.attributes.position.time_stamp.split("&nbsp;")[0]+" ";
                                break;
                case "time": str += this.attributes.position.time_stamp.split("&nbsp;")[1];
                                break;
            }
        }

        /*****************
         */ return str; /*
        *****************/

        //  if (!label) {
        //      label = new OpenLayers.Popup(id,new OpenLayers.LonLat(this.geometry.x, this.geometry.y),
        //                                      undefined,
        //                                      str, false);
        //      label.autoSize = true;
        //      label.maxSize = new OpenLayers.Size(100,35)
        //      label.calculateRelativePosition = function() {return "br"};

        //      label.contentDiv.className = "MapLog-featurePopup";
        //  }
        //  else {
        //      Ext.isIE || Ext.isOpera ? label.div.style.width = str.length*3.55+"px" : true;
        //      Ext.isIE  || Ext.isOpera? label.contentDiv.style.width = str.length*3.55+"px" : true;
        //      label.setContentHTML(str);
        //  }
        //  label.setOpacity(0.7);

        //  if (str === "") {
        //      label.contentDiv.style.display="none";
        //  }
        //  else {
        //      label.contentDiv.style.display="block";
        //  }
        //  label.feature = this;
        //  label.attributes = attributes;
        //  //label.anchor.offset = new OpenLayers.Pixel(-50,16);

        //  return label;
    },

    /**
     * display the label in the detail map
     * @param [{String}] attributes name,date,time
     * @param {OpenLayers.Map} map
     */
    displayDetailLabel: function(attributes,map) {
        var exists = (this.detailLabel ? true : false);
        this.detailLabel = this.getLabel(attributes,this.detailLabel,true);
        if (!exists) {
            map.addPopup(this.detailLabel);
        }
    },

    /**
     * display the label in the main map
     * @param [{String}] attributes name,date,time
     * @param {OpenLayers.Map} map
     */
    displayLabel: function(attributes,map) {
        
        this.labelAttributes = attributes;
        this.attributes.label = this.getLabel(this.labelAttributes);
        this.updateStyle();
    },

    /**
     * APIMethod: setActive
     *
     * activate/deactivate the marker, detailMarker, node in the switcher
     *
     * Parameters:
     * activate - {Boolean}
     */
    setActive: function(activate) {

        if (activate) {
            this.layer.deactivateAllUnits();
        }

        var oldState = this.attributes.active;

        activate = (activate ? "true" : "false");

        this.attributes.active = activate;

        this.updateStyle();

        if (activate == "true") {
            this.blick(5);

            if (this.node && !this.node.isSelected()) {
                var sm = this.node.getOwnerTree().getSelectionModel();
                sm.select(this.node, {}, true);
            }
            this.events.register("moved",this,this.setMapCenter);
            this.setMapCenter();
        }
        else {
            if (this.node) {
                this.node.unselect();
            }
            this.events.unregister("moved",this,this.setMapCenter);
        }

        if (oldState != activate) {
            this.events.triggerEvent("statechanged");
        }
    },

    /**
     * blick with the icon Ntimes
     * @function
     * @name MapLog.UnitFeature.blick
     * @param {Integer} how many times (default 3)
     */
    blick: function(times) {
        if (!times) {
            times = 3;
        }

        var interval  = 300;

        MapLog.UnitFeature[this.id] = this;

        for (var i = 0; i < times; i++) {
            this.layer.drawFeature(this,{});
            window.setTimeout("MapLog.UnitFeature['"+this.id+"'].layer.drawFeature(MapLog.UnitFeature['"+this.id+"'], {display:'none'});",
                    i*interval*2);
            window.setTimeout("MapLog.UnitFeature['"+this.id+"'].layer.drawFeature(MapLog.UnitFeature['"+this.id+"']);",
                    i*interval*2+interval);
        }

    },

    /**
     * Set alert
     * @param {Boolean} alertValue true or false
     */
    setAlert: function(alertValue) {

            if (this.layer) {
                this.layer.eraseFeatures([this]);
                this.layer.renderer.drawFeature(this,this.layer.styleMap.styles["default"]);
            }
            this.events.triggerEvent("alertchange");
    },

    /**
     * displayLabel
     * 
     */
    displayLabelHover: function(evt) {

        this.hoverLabel = new OpenLayers.Popup.Anchored(this.unit_id, 
                                            new OpenLayers.LonLat(this.geometry.x, this.geometry.y),
                                            new OpenLayers.Size(150,50),
                                            this.getHoverLabelContent());
        this.layer.map.addPopup(this.hoverLabel);
    },


    /**
     * hideLabelHover
     * 
     */
    hideLabelHover: function(evt) {
        if (this.hoverLabel) {
            this.hoverLabel.hide();
        }
    },

    /**
     * Method: setMapCenter
     *
     * Parameters:
     */
    setMapCenter: function() {
        if (this.followMap) {

            /*
             * if the  center fints into box of 80% size of the original
             * map extent, do not recenter the map
             *
             * Original box
             * +---------------+---------+
             * | Smaller bbox  | 0.1     |
             * |  +------------+------+  | 
             * |  |                   |  |
             * |.1| No rencenter -> x |.1|
             * +--+                   +--+
             * |  |                   |  |
             * |  x <- Recenter       |  |
             * |  |                   |  |
             * |  +-------+-----------+  | 
             * |          | 0.1          |
             * +----------+--------------+
             */
            var map = this.layer.map;
            var orbox = map.getExtent();
            //
            var delta = new OpenLayers.Size((orbox.right-orbox.left)*0.1,(orbox.top - orbox.bottom)*0.1);
            var smalbox = new OpenLayers.Bounds(orbox.left+delta.w, orbox.bottom+delta.h, orbox.right-delta.w, orbox.top-delta.h);
            if (!smalbox.containsLonLat(new OpenLayers.LonLat(this.geometry.x, this.geometry.y))) {
                this.layer.map.setCenter(new OpenLayers.LonLat(this.geometry.x, this.geometry.y));
            }
            else {
                this.layer.historyLayer.redraw(true);
            }
            if (this.detailMarkerLayer) {
                this.detailMarkerLayer.map.setCenter(new OpenLayers.LonLat(this.geometry.x, this.geometry.y));
            }
        }
    },

    /**
     * get title for label and switcher
     */
    getTitle: function() {
        return this.attributes.unit.description +" "+ (this.attributes.holder &&  this.attributes.holder.spz ? this.attributes.holder.spz : "");
    },

    /**
     * get style from layer
     */
    updateStyle: function(){
        // redraw
        if (this.layer) {
            this.layer.drawFeature(this);
        }

    },

    CLASS_NAME: "OpenLayers.UnitFeature"
});

MapLog.UnitFeature.detailTemplate = undefined;

MapLog.UnitFeature.setDetailTemplate = function(url) {
    OpenLayers.Request.GET({
                url:url,
                success: MapLog.UnitFeature._setDetailTemplateText
            });
};

MapLog.UnitFeature._setDetailTemplateText = function(xmlhttp) {
        MapLog.UnitFeature.detailTemplate = xmlhttp.responseText;
};

MapLog.UnitFeature.processDetailTemplate = function(values,template) {
    var out = template ? template : MapLog.UnitFeature.detailTemplate;
    var tpl = new Ext.XTemplate(out);
    out = tpl.apply(values);

    return out;
};

MapLog.UnitFeature.solveAlert = function(id,elem) {
    document.getElementById(id+"_button").type = "image";
    document.getElementById(id+"_button").src = OpenLayers.Util.getImagesLocation()+"/indicator.gif";

    OpenLayers.Request.GET({
            url: "/DBService/FeederServlet",
            params: {salt: Math.random(),Operation:"SolvingAlertEvent", alert_event_id: id},
            success: function(params){},
            failure: function(){},
            scope: {elem:elem}
    });
};
