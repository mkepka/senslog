MapLog.namespace("MapLog.Layer"); 
MapLog.Layer.History = new OpenLayers.Class(OpenLayers.Layer.MapServer, {
        EVENT_TYPES: ["paramsupdated"],
        displayOutsideMaxExtent: true,

        initialize: function(){

            this.EVENT_TYPES =
                MapLog.Layer.History.prototype.EVENT_TYPES.concat(
                OpenLayers.Layer.MapServer.prototype.EVENT_TYPES
            );

            OpenLayers.Layer.MapServer.prototype.initialize.apply(this, arguments);
        },

        setParams: function(params){
            this.params = params;
            this.events.triggerEvent("paramsupdated");
        },

        moveTo:function(bounds, zoomChanged, dragging) {
            OpenLayers.Layer.MapServer.prototype.moveTo.apply(this,arguments);
        },

        CLASS_NAME: "MapLog.Layer.History"
        });
