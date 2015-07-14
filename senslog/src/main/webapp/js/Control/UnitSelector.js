OpenLayers.Control.UnitSelector = new OpenLayers.Class(OpenLayers.Control,{

    /**
     * @name OpenLayers.Control.UnitSelector.EVENT_TYPES 
     * @type String[]
     */
    EVENT_TYPES : ["beforeQuery","afterQuery"],

    /**
     * scope for this onInfo
     * @name OpenLayers.Control.UnitSelector.scope
     * @type Object
     */
    scope: this,

    /**
     * @name OpenLayers.Control.UnitSelector.displayClass
     * @type String
     */
    displayClass: "mapLog-Selector",

    /**
     * options for the BoundingBox handler
     * @name OpenLayers.Control.UnitSelector.defaultHandlerOptions
     * @type Object
     */
    defaultHandlerOptions: {
        'single': true,
        'double': false,
        'pixelTolerance': 0,
        'stopSingle': false,
        'stopDouble': false
    },

    /**
     * @name OpenLayers.Control.UnitSelector.qlayers
     * @type <a href="http://dev.openlayers.org/releases/OpenLayers-2.8/doc/apidocs/files/OpenLayers/Layer-js.html">OpenLayers.Layer</a>[]
     */
    layer: null,

    /**
     * @name keyMask
     * @type Integer
     */
    //keyMask: OpenLayers.Handler.MOD_SHIFT /*| OpenLayers.Handler.MOD_CTRL*/,

    /**
     * @constructor
     * @param {Object} options 
     */
    initialize : function(options) {

        this.handlerOptions = OpenLayers.Util.extend(
            {}, this.defaultHandlerOptions
        );

        this.EVENT_TYPES = OpenLayers.Control.UnitSelector.prototype.EVENT_TYPES.concat(
                OpenLayers.Control.prototype.EVENT_TYPES);

        OpenLayers.Control.prototype.initialize.apply(
            this, [options]
        ); 

        this.handler = new OpenLayers.Handler.Box(
                this, {done: this.onBoxDrawed}, {keyMask: this.keyMask} );
    },

    /**
     * Called when the box is drawed. Box is cleared...
     * @name OpenLayers.Control.UnitSelector.onBoxDrawed
     * @function
     * @param {Event} evt 
     */
    onBoxDrawed: function(evt) {
        var feature = this.layer.getFeatureFromEvent(evt);
        console.log(feature);
        return;

        this.featuresSelected.apply(this.scope,[features]);
        OpenLayers.Event.stop(evt,true);
    },

    /**
     * feature was selected, this method should be redefined by high-level
     * classes
     * @param [{OpenLayers.UnitFeature}] feature
     */
    featuresSelected: function(features) {
        return;
    },

    /**
     * @name OpenLayers.Control.UnitSelector.CLASS_NAME
     * @type String
     */
    CLASS_NAME: "OpenLayers.Control.UnitSelector"
});
