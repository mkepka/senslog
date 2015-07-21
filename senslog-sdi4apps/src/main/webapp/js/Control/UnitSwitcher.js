MapLog.namespace("MapLog.Control");
MapLog.Control.UnitSwitcher = OpenLayers.Class(OpenLayers.Control,{
    /**
     * APIProperty:
     * container - {Object} or {String} if None, it will be separate
     * {Ext.Window}
     */
    container:null,

    /**
     * Property:
     * tree - {Ext.tree.TreePanel}
     */
    tree: null,

    /**
     * Property:
     * rootNode - {Ext.tree.TreeNode}
     */
    rootNode: null,

    /**
     * Property:
     * EVENT_TYPES
     */
    EVENT_TYPES: ["groupsadded"],

    /**
     * Property:
     * groupStructure - {Object}
     */
    groupStructure: {},

    /**
     * Property:
     * groupList - {Array}
     */
    groupList: [],

    /**
     * Property: selectedUnitsString
     * String, which identifies selected string
     */
    selectedUnitsString: "",

    /**
     * MapLog
     */
    maplog: null,

    /**
     * render sensors to the unit switcher
     */
    renderSensors: false,

    /**
     * Property:
     * groupNodes - {Object} of {Ext.tree.TreeNode}
     */
    groupNodes: {},

    /**
     * Property:
     * divisionNodes - {Object} of {Ext.tree.TreeNode}
     */
    divisionNodes: {},

    /**
     * Property:
     * features - {Object}
     */
    features: {},

    /**
     * Property:
     * historyButton - {Object}
     */
    historyButton: {},

    /**
     * Property: Center to selected units
     * centerToActive - {Ext.form.Checkbox}
     */
    centerToActive: undefined,

    /**
     * Property:
     * location - {String}
     */
    location: null,

    /**
     * Property:
     */
    unitSelectField: null,

    /**
     * Contructor: OpenLayers.Control.UnitSwitcher
     * Switcher for MapLog units
     *
     * Parameters:
     * location - {String}
     * layer - {OpenLayers.Layer.UnitLayer}
     * options - {Object} configuration options
     */
    initialize: function(location,layer,options) {
        this.EVENT_TYPES = this.EVENT_TYPES.concat(OpenLayers.Control.prototype.EVENT_TYPES);
        OpenLayers.Control.prototype.initialize.apply(this, [options]);
        this.location = location;

        // where to put the main window
        if (options.container) {
            if (typeof Ext.get(options.container) == "string") {
                this.container = new Ext.Panel({renderTo: options.container,layout:"fit"});
            }
            else if (options.container.nodeType && options.container.nodeType == 1) {
                this.container = new Ext.Panel({renderTo: options.container,layout:"fit"});
            }
            else if (options.container.body) {
                this.container = options.container;
            }
        }
        else {
            this.container = new Ext.window({title: layer.name});
        }

        this.mask = new Ext.LoadMask(this.container.body);
        this.mask.show();

        this.layer = layer;
    },

    /**
     * Method: getGroups
     */

    getGroups: function() {
        OpenLayers.Request.GET({
            url: this.location,
            success: this.makeGroupsStructure,
            failure: function(){console.log("error retrieving groups data",arguments)},
            scope: this
        });
    },

    /**
     * Method: destroy
     */
    destroy: function() {
        this.container.remove(this.tree);
        this.tree.destroy();
        this.tree = null;
        OpenLayers.Control.prototype.destroy.apply(this, arguments);
    },

    /**
     * Method: setMap
     *
     * Parameters:
     * map - {<OpenLayers.Map>} 
     */
    setMap: function(map) {
        OpenLayers.Control.prototype.setMap.apply(this, arguments);

    },

    /**
     * Method: draw
     */
    draw: function() {

        this.rootNode = new Ext.tree.TreeNode({
                expanded:true});

        this.unitSelectField  = new Ext.form.TextField({
                enableKeyEvents: true,
                listeners:{"keyup": this.selectUnits,
                          scope:this}
                });

        this.historyButton = new Ext.Button({text:OpenLayers.i18n("Display history"), 
                    handler: this.displayHistory,
                    scope: this,
                    cls: 'x-btn-text-icon',
                    icon: "icons/history.png"});

        this.centerCheckbox = new Ext.Button({
                    enableToggle: true,
                    handler: this.toggleCenterToActive,
                    pressed: true,
                    scope: this,
                    checked: true,
                    cls: 'x-btn-text-icon',
                    icon: "icons/center.png"});

        this.deactivateAllButton = new Ext.Button(
                    {
                    //handler: this.layer.deactivateAllUnits,
                    scope: this,
                    cls: 'x-btn-text-icon',
                    icon: "icons/stop.png"});


        this.zoomToActive = new Ext.Button(
                    { handler: this.zoomToActiveUnit,
                    scope: this,
                    cls: 'x-btn-icon',
                    icon: "icons/zoom.png"});

        this.tree = new Ext.tree.TreePanel({
            enableDD: false,
            useArrows: true,
            autoScroll: true,
            rootVisible: false,
            selModel: new Ext.tree.MultiSelectionModel(),
            tbar: [{text:OpenLayers.i18n("Select:")}, this.unitSelectField],
            bbar:[ this.deactivateAllButton,
                    // this.historyButton,
                    this.centerCheckbox,
                    this.zoomToActive
                   ],
            root: this.rootNode
        });
        this.container.add(this.tree);

        this.getGroupsStructure(); 
        if (this.layer) {
            this.setLayer(this.layer);
        }


        new Ext.ToolTip({
            target: this.deactivateAllButton.el.id,
            trackMouse: true,
            anchor: 'left',
            html: OpenLayers.i18n("Deactivate all")
        });

        new Ext.ToolTip({
            target: this.zoomToActive.el.id,
            trackMouse: true,
            anchor: 'left',
            html: OpenLayers.i18n("Zoom to active")
        });

        new Ext.ToolTip({
            target: this.centerCheckbox.el.id,
            trackMouse: true,
            anchor: 'left',
            html: OpenLayers.i18n("Center to active")
        });


        Ext.QuickTips.init();
    },

    /**
     * Method: drawNodes
     */
    drawNodes: function() {
       for (var i = 0; i < this.groupList.length; i++) {
       }
    },

    /**
     * Method: redraw
     * 
     * Parameters:
     * selectedFeature {String} only features with this name. Default: none
     */
    redraw: function() {

        if (!this.layer)  { return; }

        if (this.layer.features.length > 0) {
            this.mask.hide();
        }

        // colect informations about features
        for (var i = 0; i < this.layer.features.length; i++) {
            var feature = this.layer.features[i];
            // skip allready existing features

            // skip, if it is selected
            if (this.selectedUnitsString){
                var id = new String(feature.attributes.unit.description ? feature.attributes.unit.description : feature.unit_id)+" "+(feature.node ? feature.node.text: "");

                // if the feature.unit_id is similar to selectedUnitsString
                var regex = new RegExp(this.selectedUnitsString,"i");
                if (id.search(regex) == -1) {

                    // if there is feature.node
                    if (feature.node) {

                        // remove the feature node
                        feature.node.parentNode.removeChild(feature.node);
                        feature.node = null;
                    }

                    // remove feature from the list
                    if (this.features[feature.unit_id]) {
                        this.features[feature.unit_id] = undefined;
                    }

                    continue;
                }
            }
            

            // skip alrady existing features
            if (this.features[feature.unit_id] && this.features[feature.unit_id].group) {
                continue;
            }
            
            // register feature
            this.features[feature.unit_id] = feature;

            // create new node
            feature.node = new Ext.tree.TreeNode({
                                        text: feature.getTitle(),
                                        checked: feature.attributes.visibility === "true" ? true : false,
                                        feature: feature,
                                        listeners:{"checkchange":this.setUnitVisibility,scope:this},
                                        icon: "icons/car-small.png"
                                        });


            // append sensors as well
            if (this.renderSensors) {
                for (var j = 0; j < feature.data.sensors.length; j++) {
                    feature.data.sensors[j].feature = feature;
                    feature.data.sensors[j].node = new Ext.tree.TreeNode({
                                text: feature.data.sensors[j].sensorName,
                                sensor: feature.data.sensors[j]
                            });
                    feature.node.appendChild(feature.data.sensors[j].node);
                }
            }

            feature.node.on("click",this.activateUnit,this);

            feature.events.register("visibilitychanged", feature, this.featureVisibilityChanged);
            feature.events.register("attributesupdated", this, this.featureAttributesUpdated);
            this.featureAttributesUpdated({object:feature});

            // append to node
            this.appendFeatureToNode(feature);
        }

        this.container.doLayout();

        for (var i = 0; i < this.layer.features.length; i++) {
            var feature = this.layer.features[i];
            if (feature.node) {
                feature.node.ui.anchor.id = feature.node.id;
                Ext.QuickTips.unregister(feature.node.id);
                new Ext.ToolTip({target:feature.node.id,
                                title: feature.unit_id,
                                trackMouse: true,
                                html: feature.node.text.replace(/<img .[^>]*>/,"")});
            }
        }
        Ext.QuickTips.init();
    },

    /**
     * Method: appendFeatureToNode
     *
     * Parameters:
     * feature - {OpenLayers.UnitFeature}
     */
    appendFeatureToNode: function(feature) {
        var getGrp = function(feature) {
            for (var i = 0; i < this.groupList.length; i++) {
                if (this.groupList[i].id == feature.attributes.position.group_id) {
                    return this.groupList[i];
                }
            }
        }

        var grp = getGrp.apply(this,[feature]);

        if (!grp) {
            grp = this.groupStructure;
        }

        if (grp && grp.node) {
            grp.node.appendChild(feature.node);
            grp.features.push(feature);
            grp.node.expand();
            feature.group = grp;
        }
    },

    /**
     * Method: setUnitVisibility
     * On checkbox change
     *
     * Parameters:
     * node - {Ext.tree.TreeNode}
     * visibility - {Boolean}
     */
    setUnitVisibility: function(node,visibility) {
        node.attributes.feature.setVisibility.apply(node.attributes.feature,[visibility,{silently:true}]);

        if (node.isSelected() && visibility) {
            this.layer.events.triggerEvent("featureselected", {feature: node.attributes.feature});
        }
        if (visibility) {
            this.displayHistory();
        }
        else {
            this.hideHistory();
        }
    },

    /**
     * Method: activateUnit
     * On tree node activated
     *
     * Parameters:
     * node - {Ext.tree.TreeNode}
     * visibility - {Boolean}
     */
    activateUnit: function(node) {

        node.select();
        this.setUnitVisibility(node,true);
        //node.attributes.feature.setActive(true);
        return true;
    },

    /**
     * Method: displayHistory
     * Display history of selected feature
     *
     */
    displayHistory: function() {
        var nodes = this.getActiveNodes();
        if (nodes.length == 0) {
            return;
        }

        this.historyButton.setIcon("icons/clearhistory.png");
        this.historyButton.setText(OpenLayers.i18n("Hide history"));
        this.historyButton.handler = this.hideHistory;

        
    },

    /**
     * Method: toggleCenterToActive
     * Toggle always center the map to active unit
     */
    toggleCenterToActive: function(button, e) {
        for (var i = 0; i < this.layer.features.length; i++) {
            this.layer.features[i].followMap = button.pressed;
        }
    },

    /**
     * Method: getActiveNodes
     * Return list of active nodes
     */

    getActiveNodes: function() {
        var nodes = [];
        this.tree.root.cascade(
                function(nodes) {
                    if (this.isSelected()) {
                        nodes.push(this);
                    }
                }, 
                undefined,  [nodes]);
        return nodes;
    },


    /**
     * Method: hideHistory
     * Hide history map
     */
    hideHistory: function() {
        this.tree.root.cascade(function(){
                if (this.attributes  && this.attributes.feature) {
                try {
                    this.attributes.feature.displayInHistoryMap(false);
                    }catch(e){}
                    }
                });
        this.historyButton.setIcon("icons/history.png");
        this.historyButton.setText(OpenLayers.i18n("Display history"));
        this.historyButton.handler = this.displayHistory;
    },

    /**
     * Method: setGroupVisibility
     * On checkbox change
     *
     * Parameters:
     * node - {Ext.tree.TreeNode}
     * visibility - {Boolean}
     */
    setGroupVisibility: function(node,visibility) {
        for (var i = 0; i < node.childNodes.length; i++) {
            node.childNodes[i].getUI().toggleCheck(visibility);
        }
    },

    /**
     * Method: setGroupActive
     * On group activate
     *
     * Parameters:
     * node - {Ext.tree.TreeNode}
     */
    setGroupActive: function(node) {
        var feature=  node.attributes.feature;
        feature.layer.deactivateAllUnits();
        node.getUI().checkbox.checked = true;
        var sm = node.getOwnerTree().getSelectionModel();
        sm.unselect(node, true);
        return false;
    },

    /**
     * Method: featureVisibilityChanged
     * On visibility changed
     *
     * Note:
     *  Scope is feature
     *
     * Parameters:
     * feature - {Ext.tree.TreeNode}
     */
    featureVisibilityChanged: function(e) {
        var feature = e.object;
        if (feature.visibility != feature.node.getUI().isChecked()) {
            feature.node.getUI().toggleCheck(feature.visibility);
        }
    },

    /**
     * Method: featureAttributesUpdated
     * Feature attributes updated
     *
     * Parameters:
     * evt - {OpenLayers.Event}
     */
    featureAttributesUpdated: function(evt) {
        var feature = evt.object;
        this.displayMovingIcon(feature);
        this.displayAlertIcon(feature);
    },

    /**
     * Method: displayMovingIcon
     * set moving icon to feature.node text
     *
     * Parameters:
     * feature - {MapLog.UnitFeature}
     */
    displayMovingIcon: function(feature) {
        var imgstr ="<img src=\"icons/moving.png\" width=\"16\" height=\"16\" />";
        try {
            feature.node.setText(feature.node.text.replace(imgstr,""));
            if (feature.attributes.is_moving == "true" && feature.attributes.is_online == "true") {
                feature.node.setText(imgstr+" "+feature.node.text);
            }
        }catch(e){OpenLayers.Console.error(e)}
    },

    /**
     * Method: displayAlertIcon
     * set alert icon to feature.node text
     *
     * Parameters:
     * feature - {MapLog.UnitFeature}
     */
    displayAlertIcon: function(feature) {
        var imgstr ="<img src=\"icons/alert.gif\" width=\"16\" height=\"16\" />";
        try {
            feature.node.setText(feature.node.text.replace(imgstr,""));
            if (feature.attributes.hasAlert === "true") {
                feature.node.setText(imgstr+" "+feature.node.text);
            }
        }catch(e){OpenLayers.Console.error(e)}
    },

    /**
     * Method: getGroupsStructure
     * 
     * Parameters:
     * Returns
     */
    getGroupsStructure: function() {
        var onFail = function(e) {
            OpenLayers.Console.error("Loading the groups structure failed: ",e);
        }
    },

    /**
     * Method: makeGroupsStructure
     *
     * Parameters:
     * response - {Object}
     */
    makeGroupsStructure: function(response) {
        var text = response.responseText;  
        var format = new OpenLayers.Format.JSON();
        this.groupList = format.read(text);
        this.groupStructure = [];
        

        // search the parent group for this group id
        var getParentGroup = function(id) {
            for (var i = 0; i < this.groupList.length; i++) {
                var grp = this.groupList[i];
                if (grp.id  == id) {
                    return grp;
                }
            }
        }

        // create the group object
        for (var i = 0; i < this.groupList.length; i++) {
            this.groupList[i].child_groups = [];
            this.groupList[i].features = [];
            this.groupList[i].node = new Ext.tree.TreeNode({text:this.groupList[i].group_name,expanded:true,leaf:false,checked:true,
                                listeners:{"checkchange":this.setGroupVisibility,
                                           "click":this.setGroupActive,
                                           scope: this
                                }});
            this.groupList[i].node.on("contextmenu",this.showContextMenu,this);
        }

        // order the groups to structure
        for (var i = 0; i < this.groupList.length; i++) {
            var grp = this.groupList[i];
            var parentGrp =  getParentGroup.apply(this,[grp.parent_group_id]);
            if (parentGrp) {
                parentGrp.child_groups.push(grp);
                parentGrp.node.appendChild(grp.node);
            }
            else {
                this.groupStructure = grp;
                this.rootNode.appendChild(grp.node);
            }
        }
        this.redraw();
        this.container.doLayout();

        this.events.triggerEvent("groupsadded");
    },

    /**
     * Method: selectUnits
     * Select units identified by the string given to the text field
     */
    selectUnits: function(field,evt) {
        this.selectedUnitsString = field.getValue();
        this.redraw();
    },

    /**
     * Method: setLayer
     *
     * Parameters:
     * layer - {OpenLayers.Layer.UnitLayer}
     */
    setLayer: function(layer) {
        this.layer = layer;
        this.layer.events.register("featureadded",this, this.redraw);
        this.redraw();
        this.deactivateAllButton.setHandler(this.layer.deactivateAllUnits,this.layer);
    },
    
    /**
     * Method: showContextMenu
     * 
     */
    showContextMenu: function(node,e) {
        if (!node.attributes.menu) {
            node.attributes.menu = new Ext.menu.Menu({
                items:[{text:OpenLayers.i18n("Zoom to group"),
                        handler: this.zoomToGroup,
                                    scope: this,
                                    node:  node}]
            });
        }
        node.attributes.menu.show(e.target);
    },

    /**
     * zoom to track
     */
    zoomToActiveUnit: function() {
        this.zoomToActive.disable();
        var feature = this.tree.getSelectionModel().lastSelNode.attributes.feature;
        var hlayer = feature.layer.historyLayer;

        var map_projection = hlayer.params.map_projection;
        hlayer.params.map_projection = undefined;
        var qstring = hlayer.getFullRequestString({
                    mode: "itemquery",
                    qstring: "unit_id="+feature.unit_id,
                    qlayer: "qpositions",
                    EPSG: this.map.projection.proj.srsProjNumber
                });
        
        OpenLayers.Request.GET({
            url: qstring,
            success: this.coordsArrived,
            scope: this
        });
        hlayer.params.map_projection = map_projection;
    },
    
    /**
     * coordinates for zooming in arrived, use them
     */
    coordsArrived: function(r) {
        this.zoomToActive.enable();
        var json = eval('['+r.responseText+']');
        json = json[0];
        
        var wkt = new OpenLayers.Format.WKT();
        var envelope = wkt.read(json.bbox);
        
        if (envelope.geometry.CLASS_NAME == "OpenLayers.Geometry.Polygon") {
            this.map.zoomToExtent(envelope.geometry.getBounds());
        }
        else {
            map.setCenter(new OpenLayers.LonLat(envelope.geometry.x,envelope.geometry.y));
        }
    },
    
    /**
     * Zoom to group of units
     * @name MapLog.Control.UnitSwitcher.zoomToGroup
     * @function
     * @author jachym
     * @param {Ext.menu.Item} item
     */
    zoomToGroup: function(item){
    	var bbox = new OpenLayers.Bounds(null,null,null,null);
    	var addToBBox = function(node) {
    	
    		if (node.attributes && node.attributes.feature) {
    			if (node.attributes.feature.attributes.visibility == "true")
    			this.bbox.extend(node.attributes.feature.geometry);
    		}
    	};
    	
    	item.node.cascade(addToBBox,{bbox:bbox});
    	
    	this.map.zoomToExtent(bbox);
    },

    CLASS_NAME: "MapLog.Control.UnitSwitcher"

});
