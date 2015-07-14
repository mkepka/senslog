/**
 * MapLog.MapPanel
 * @constructor
 * @param {Object} configuration
 */
Ext.MapLog.SettingsPanel= function(config) {

    if (!config) {
        config = {};
    }

    var now = new Date();

    this.unitsStore = new Ext.data.ArrayStore({
            fields: ["text","feature"],
            data: []
            });

    /*
    this.unitSelectorCombo = new Ext.form.ComboBox({
                typeAhead: true,
                triggerAction: 'all',
                lazyRender:true,
                mode: 'local',
                width: 150,
                store: this.unitsStore,
                fieldLabel: OpenLayers.i18n("Unit"),
                valueField: 'feature',
                displayField: 'text'
            });
    */

    this.historyDateFieldFrom = new Ext.form.DateField({
                width: 120,
                format: "d.m.Y",
                fieldLabel: OpenLayers.i18n("Date History"),
                value: new Date(now.getFullYear(),now.getMonth(),now.getDate()),
                scope: this,
                listeners:{change:this.onHistoryDateOrTimeChange,
                           select:this.onHistoryDateOrTimeChange,
                           scope:this}
            });

    this.historyTimeFieldFrom = new Ext.form.TimeField({
                format: "H:i",
                width: 100,
                fieldLabel: OpenLayers.i18n("Time History"),
                scope: this,
                value: new Date(now.getFullYear(),now.getMonth(),now.getDate()),
                listeners:{change:this.onHistoryDateOrTimeChange,
                           select:this.onHistoryDateOrTimeChange,
                           scope:this},
                increment: 15
            });

    this.historyDateFieldTo = new Ext.form.DateField({
                width: 120,
                format: "d.m.Y",
                fieldLabel: OpenLayers.i18n("Date History"),
                value: new Date(now.getFullYear(), now.getMonth(), now.getDate(),24,0),
                scope: this,
                listeners:{change:this.onHistoryDateOrTimeChange,
                           select:this.onHistoryDateOrTimeChange,
                           scope:this}
            });

    this.historyTimeFieldTo = new Ext.form.TimeField({
                format: "H:i",
                width: 100,
                fieldLabel: OpenLayers.i18n("Time History"),
                scope: this,
                value: new Date(now.getFullYear(), now.getMonth(), now.getDate(),24,0),
                listeners:{change:this.onHistoryDateOrTimeChange,
                           select:this.onHistoryDateOrTimeChange,
                           scope:this},
                increment: 15
            });

    this.historyLengthField = new Ext.form.TextField({
                width: 100,
                format: "H:i",
                increment: 15,
                fieldLabel: OpenLayers.i18n("History Length"),
                value: this.getTimeFormat(this.historyLength),
                scope:this,
                listeners:{change:this.onHistoryDateOrTimeChange,
                           select:this.onHistoryDateOrTimeChange,
                           scope:this}
            });

    var historyFromSet = new Ext.form.FieldSet({
                title: OpenLayers.i18n("From"),
                items: [
                    this.historyDateFieldFrom,
                    this.historyTimeFieldFrom
                    ]
            });
    var historyToSet = new Ext.form.FieldSet({
                title: OpenLayers.i18n("To"),
                items: [
                    this.historyDateFieldTo,
                    this.historyTimeFieldTo
                ]
            });

    var historyFieldSet = new Ext.form.FieldSet({
            title: OpenLayers.i18n("History"),
            items: [
                historyFromSet, historyToSet,
                this.historyLengthField,
                new Ext.Panel({title: OpenLayers.i18n("Calendar")+" ...", items: [
                    new Ext.Button({text: OpenLayers.i18n("This")+" "+OpenLayers.i18n("day"),
                                    listeners:{click:this.onActualSetHistoryLengthClicked,scope:this}
                                    }),
                    //new Ext.Button({text: OpenLayers.i18n("This")+OpenLayers.i18n("days"),
                    //                listeners:{click:this.onActualSetHistoryLengthClicked,scope:this}
                    //    }),
                    //new Ext.Button({text: OpenLayers.i18n("3 days")}),
                    new Ext.Button({text: OpenLayers.i18n("This")+" "+OpenLayers.i18n("week"),
                                    listeners:{click:this.onActualSetHistoryLengthClicked,scope:this}
                        }),
                    new Ext.Button({text: OpenLayers.i18n("This")+" "+OpenLayers.i18n("month"),
                                    listeners:{click:this.onActualSetHistoryLengthClicked,scope:this}
                        })
                    ],
                layout:'hbox'}),
                new Ext.Panel({title: OpenLayers.i18n("Back")+" ...", items: [
                    new Ext.Button({text: "2 "+OpenLayers.i18n("hours"),
                                    listeners:{click:this.onSetHistoryLengthClicked,scope:this}
                                    }),
                    new Ext.Button({text: "24 "+OpenLayers.i18n("hours"),
                                    listeners:{click:this.onSetHistoryLengthClicked,scope:this}
                                    }),
                    new Ext.Button({text: "48 "+OpenLayers.i18n("hours"),
                                    listeners:{click:this.onSetHistoryLengthClicked,scope:this}
                        }),
                    //new Ext.Button({text: OpenLayers.i18n("3 days")}),
                    new Ext.Button({text: "7 "+OpenLayers.i18n("days"),
                                    listeners:{click:this.onSetHistoryLengthClicked,scope:this}
                        })
                    ],
                layout:'hbox'})
            ]
            });


    config.title=OpenLayers.i18n("History settings");
    config.frame=true;
    config.forceLayout = true;
    config.items = [
        historyFieldSet
    ];
    config.autoScroll=true;
    
    // call parent constructor
    Ext.MapLog.SettingsPanel.superclass.constructor.call(this, config);

};

//extend
Ext.extend(Ext.MapLog.SettingsPanel,Ext.form.FormPanel, {

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
     * UnitFeature activated in the map
     * @type MapLog.UnitFeature
     */
    feature: null,

    /**
     * history date form field
     * @type Ext.form.Field
     */
    historyDateField:null,

    /**
     * history time form field
     * @type Ext.form.Field
     */
    historyTimeField: null,

    /**
     * history length
     * @type Ext.form.Field
     */
    historyLengthField: null,

    /**
     * history layer
     * @type OpenLayers.Layer.MapServer
     */
    layer: null,

    /**
     * lenghtof the history
     * @type String
     */
    historyLength: 24*60*60*1000,

    /**
     * scope for onHistoryLengthChanged
     * @type Object
     */
    scope: this,

    /**
     * history time selection field
     * @type Ext.form.TimeField
     */
    historyTimeField: null,

    /**
     * panel for map tools
     * @type OpenLayers.Control.Panel
     */
    toolsPanel: null,


    /**
     * Set this.historyLayer object
     * @param {OpenLayers.Layer} layer
     */
    setHistoryLayer: function(layer){
        this.layer = layer;
    },

    /**
     * history length change - redraw
     * @param {Ext.Form.Field} field
     * @param {String} newValue
     * @param {String} oldValue
     */
    onHistoryLengthChange: function(field, newValue, oldValue){

        var ms = null;
        if (newValue.search(":") === -1) {
            ms = this.getMsFromTime(new String(newValue)+":0");
        }
        else {
            ms = this.getMsFromTime(newValue);
        }

        this.setTime(new Date(this.historyDateFieldTo.getValue().valueOf() + this.getMsFromTime(this.historyTimeFieldTo.getValue().valueOf())-ms),
                     new Date(this.historyDateFieldTo.getValue().valueOf() + this.getMsFromTime(this.historyTimeFieldTo.getValue().valueOf())));
    },
                           
    /**
     * show history checkbox changed
     * @param {Ext.Form.Field} field
     * @param {Boolean} checked
     */
    redraw: function(){

        if (!this.feature) {
            this.layer.setVisibility(false);
            return;
        }
        
        this.layer.setVisibility(true);
        var fromDate = new Date(this.historyDateFieldFrom.getValue().valueOf()+this.getMsFromTime(this.historyTimeFieldFrom.getValue()));
        var toDate = new Date(this.historyDateFieldTo.getValue().valueOf()+this.getMsFromTime(this.historyTimeFieldTo.getValue()));

        var params = {
            SESSIONID: MapLog.getSessionCookie(),
            layers: "positions longHistory",
            IDS: this.feature.unit_id,
            map_imagetype: "png",
            map_transparent: "ON",
            fromTime: new String(fromDate.getFullYear())+"-"+new String(1+fromDate.getMonth())+"-"+new String(fromDate.getDate())+" "+ new String(fromDate.getHours())+":"+new String(fromDate.getMinutes()),
            toTime: new String(toDate.getFullYear())+"-"+new String(1+toDate.getMonth())+"-"+new String(toDate.getDate())+" "+ new String(toDate.getHours())+":"+new String(toDate.getMinutes())
        };

        if (!this.layer) {
            this.layer = new OpenLayers.Layer.MapServer(OpenLayers.i18n("Long history"),
                    "http://"+window.location.hostname+"/cgi-bin/maploghistory",
                    params,
                    {
                        isBaseLayer:false,
                        visibility:true,
                        ratio: 1,
                        transitionEffect: "resize",
                        singleTile:true
                    });
            this.map.addLayer(this.layer);
        }
        else {
            this.layer.setParams(OpenLayers.Util.extend(this.layer.params, params));
            this.layer.redraw(true);
        }
    },





    /**
     * get miliseconds from date value
     * @param {String} value date
     */
    getMsFromTime: function(value){
        var hours =  0;
        var minutes = 0;
        if (value.split(":").length > 1) {
            hours = parseInt(value.split(":")[0],10);
            minutes = parseInt(value.split(":")[1],10);
        }
        else {
            minutes = parseInt(value,10);
        }
        return hours*3600*1000+minutes*60*1000;
    },

    /**
     * history date change - redraw
     * @param {Ext.Form.Field} field
     * @param {String} newValue
     * @param {String} oldValue
     */
    onHistoryDateOrTimeChange: function(field, newValue, oldValue){

        this.setTime(new Date(this.historyDateFieldFrom.getValue().valueOf() + this.getMsFromTime(this.historyTimeFieldFrom.getValue().valueOf())),
                     new Date(this.historyDateFieldTo.getValue().valueOf() + this.getMsFromTime(this.historyTimeFieldTo.getValue().valueOf())));
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
     * set history time field
     * @param {Integer} length [ms]
     */
    getTimeFormat: function(length) {
        var hours = length/3600/1000;
        var minutes = parseInt(hours%1*60);
        hours = hours-hours%1

        if (hours <10) {
            hours = "0"+new String(hours);
        }
        if (minutes <10) {
            minutes = "0"+new String(minutes);
        }

        return hours + ":"+minutes;
    },

    /**
     * history length is changed, do something (to be redefined by other
     * methods
     * @param {Date} from
     * @param {Date} to
     * @function
     */
    onHistoryLengthChanged: function(from,to) {

    },

    /**
     * Set history layer
     * @param {OpenLayers.Layer} layer
     */
    setLayer: function(layer) {
        this.layer = layer;
    },

    /**
     * Set feature
     * @param {MapLog.Feature} feature
     */
    setFeature: function(feature) {
        this.feature = feature;
        if (feature) {
            this.redraw();
        }
    },

    /**
     * On set history length clicked, set predefined history
     * @param button
     * @param event
     */
    onActualSetHistoryLengthClicked: function(button,evt) {
        var front = null; 
        var back = null; 
        var now = new Date();

        switch(button.getText()) {
            case OpenLayers.i18n("This")+" "+OpenLayers.i18n("day"):
                            back = new Date(now.getFullYear(),now.getMonth(),now.getDate()); 
                            front =  new Date(now.getFullYear(),now.getMonth(), now.getDate()+1);
                            break;
            //case OpenLayers.i18n("This")+" "+OpenLayers.i18n("days"):
            //                back = new Date(now.getFullYear(),now.getMonth(),now.getDate()-1); 
            //                front =  new Date(now.getFullYear(),now.getMonth(), now.getDate()+1);
            //                break;
            case OpenLayers.i18n("This")+" "+OpenLayers.i18n("week"):
                            var dow = now.getDay();
                            back = new Date(now.getFullYear(),now.getMonth(),now.getDate()-dow); 
                            front =  new Date(now.getFullYear(),now.getMonth(), now.getDate()+(7-dow));
                            break;
            case OpenLayers.i18n("This")+" "+OpenLayers.i18n("month"):
                            back = new Date(now.getFullYear(),now.getMonth()); 
                            front =  new Date(now.getFullYear(),now.getMonth()+1);
                            break;
        }
        if (front && back) {
            this.setTime(back,front);
        }
    },

    /**
     * On set history length clicked, set predefined history
     * @param button
     * @param event
     */
    onSetHistoryLengthClicked: function(button,evt) {
        var now = new Date();
        var to = now.valueOf(); //this.historyDateFieldTo.getValue().valueOf() + this.getMsFromTime(this.historyTimeFieldTo.getValue());
        var back = null;
        var front = new Date(to); 

        switch(button.getText()) {
            case "2 "+OpenLayers.i18n("hours"):
                            back = new Date(to-2*60*60*1000); 
                            break;
            case "24 "+OpenLayers.i18n("hours"):
                            back = new Date(to-24*60*60*1000); 
                            break;
            case "48 "+OpenLayers.i18n("hours"):
                            back = new Date(to-2*24*60*60*1000); 
                            break;
            case "7 "+OpenLayers.i18n("days"):
                            back = new Date(to-7*24*60*60*1000); 
                            break;
        }
        if (front && back) {
            this.setTime(back,front);
        }
    },

    /**
     * setTime
     * @param {Date} from time
     * @param {Date} to time
     */
    setTime: function(from,to) {

        var now = new Date();
        if (from > now) {
            from = now;
        }

        this.historyDateFieldFrom.setValue(from);
        this.historyDateFieldTo.setValue(to);

        this.historyTimeFieldFrom.setValue(from);
        this.historyTimeFieldTo.setValue(to);

        var diff = new Date(to.valueOf()-from.valueOf());
        var hours = Math.round(diff/3600/1000);
        var minutes = parseInt((diff/3600/1000)%1*60,10);

        if (hours <10) {
            hours = "0"+new String(hours);
        }
        if (minutes <10) {
            minutes = "0"+new String(minutes);
        }

        this.historyLengthField.setValue(hours + ":" + minutes);

        this.onHistoryLengthChanged.apply(this.scope,[from,to]);
        this.redraw();
    },
    
    /**
     * Set this.map object
     * @param {OpenLayers.Map} map
     */
    setMap: function(map){
        this.map = map;
    }
});
