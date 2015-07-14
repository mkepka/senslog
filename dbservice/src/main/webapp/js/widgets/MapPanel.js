/**
 * MapLog.MapPanel
 * @contructor
 * @param {Object} configuration
 */
Ext.MapLog.MapPanel= function(config) {

    if (!config) {
        config = {};
    }

    var audio_checked = HS.getCookie("audio_checked");
    if (audio_checked == "off")  {
        audio_checked = false;
    }
    else {
        audio_checked = true;
    }

    this.displayNameCheckbox = new Ext.form.Checkbox({
        boxLabel: OpenLayers.i18n("Name"), name:"name",handler: this.displayLabel, checked:true, scope: this});
    this.displayDateCheckbox = new Ext.form.Checkbox({
        boxLabel: OpenLayers.i18n("Date"), name:"date",handler: this.displayLabel, checked:true, scope: this});
    this.displayTimeCheckbox = new Ext.form.Checkbox({
        boxLabel: OpenLayers.i18n("Time"), name:"time",handler: this.displayLabel, checked:true, scope: this});
    this.playSoundCheckbox = new Ext.form.Checkbox({
        boxLabel: OpenLayers.i18n("Audio"), name:"audio", /*handler: this.soundSettings, */ checked: audio_checked, scope: this});

    this.lastUpdateItem = new Ext.Toolbar.TextItem({
                        hideLabel: false,
                        //fieldLabel: OpenLayers.i18n("Last update"),
                        text: OpenLayers.i18n("Last update")+": "+"-00:-00:-00"
    });

    this.lastUpdateOK = new Ext.Toolbar.TextItem({text:""});

    config.bbar = [
        this.displayNameCheckbox,
        new Ext.Toolbar.Separator(),
        this.displayDateCheckbox,
        new Ext.Toolbar.Separator(),
        this.displayTimeCheckbox,
        new Ext.Toolbar.Separator(),
        new Ext.Toolbar.Separator()
    ];
    if (HS.getCookie("audio") == "true") {
        config.bbar.push(this.playSoundCheckbox);
    }

    config.bbar.push(new Ext.Toolbar.Fill());
    config.bbar.push(this.lastUpdateItem);
    config.bbar.push(this.lastUpdateOK);


    // call parent constructor
    Ext.MapLog.MapPanel.superclass.constructor.call(this, config);

};

//extend
Ext.extend(Ext.MapLog.MapPanel,Ext.Panel, {

    /**
     * map
     * @type OpenLayers.Map
     */
    map: null,

    /**
     * marker unit layer
     * @type OpenLayers.Layer.Marker
     */
    unitLayer: null,

    /**
     * scope for onHistoryLengthChanged
     * @type Object
     */
    scope: this,

    /**
     * displayNameCheckbox
     * @type Ext.form.Checkbox
     */
    displayNameCheckbox: null,

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
     * panel for map tools
     * @type OpenLayers.Control.Panel
     */
    toolsPanel: null,

    /**
     * navigation history control
     * @type OpenLayers.Control.NavigationHistory
     */
    navigationHistory: null,

    /**
     * Display popup label with informations about the units
     */
    displayLabel: function(checkbox,checked){
        var attributes = [];
        if (this.displayNameCheckbox.getValue()) {
            attributes.push("name");
        }
        if (this.displayDateCheckbox.getValue()) {
            attributes.push("date");
        }
        if (this.displayTimeCheckbox.getValue()) {
            attributes.push("time");
        }
        this.unitLayer.displayLabels(attributes);
    },

    /**
     * Set this.unitLayer object
     * @param {OpenLayers.Layer} layer
     */
    setUnitLayer: function(layer){
        this.unitLayer = layer;
    },

    /**
     * Set this.toolsPanel object
     * @param {OpenLayers.Control.Panel} panel
     */
    setToolsPanel: function(panel){
        this.toolsPanel = panel;
        this.map.addControl(this.toolsPanel);
    },

    /**
     * Set time to lastUpdateItem
     */
    setUpdated: function(){
        var date = new Date();
        var hour = date.getHours();
        var min = date.getMinutes();
        var sec = date.getSeconds();

        hour = (hour < 10 ? "0"+String(hour) : String(hour));
        min = (min < 10 ? "0"+String(min) : String(min));
        sec = (sec < 10 ? "0"+String(sec) : String(sec));

        this.lastUpdateItem.setText(OpenLayers.i18n("Last update")+": "+
                                    hour+":"+ min+":"+ sec);
    },

    /**
     * Set laoded status to OK or Failed
     */
    setUpdatedOkFailed: function(e){
        if (e.type == "ok") {
            this.lastUpdateOK.setText(OpenLayers.i18n("OK"));
            this.lastUpdateOK.getEl().applyStyles({color:"white"});
        }
        else {
            this.lastUpdateOK.setText(OpenLayers.i18n("FAILED"));
            this.lastUpdateOK.getEl().applyStyles({color: "red"});

        }
    },

    /**
     * Set this.map object
     * @param {OpenLayers.Map} map
     */
    setMap: function(map){
        this.map = map;

        // map tools panel and tools
        var vlayer = new OpenLayers.Layer.Vector();
        var measure = new HSLayers.Control.Measure({layer:vlayer});
        this.map.addLayer(vlayer);
        this.setToolsPanel(new OpenLayers.Control.Panel({displayClass: "hsControlPanel"}));
        this.toolsPanel.addControls([new OpenLayers.Control.Navigation({zoomBoxKeyMask: OpenLayers.Handler.MOD_CTRL}),
                            measure.distance
                            ]);

        this.navigationHistory = new OpenLayers.Control.NavigationHistory();
        this.map.addControl(this.navigationHistory);
        this.toolsPanel.addControls([this.navigationHistory.previous, this.navigationHistory.next]);
        this.toolsPanel.activateControl(this.toolsPanel.controls[0]);
    }
});
