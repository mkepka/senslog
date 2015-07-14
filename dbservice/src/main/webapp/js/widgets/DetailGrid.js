/**
 * MapLog.DetailGrid
 * @contructor
 * @param {Object} configuration
 */
Ext.MapLog.DetailGrid= function(config) {
    
    // call parent constructor
    Ext.MapLog.MapPanel.superclass.constructor.call(this, config);

    // initialization of properties
    // ... nothing yet

};

//extend
Ext.extend(Ext.MapLog.DetailGrid,Ext.grid.GridPanel, {
    closable: true,
    closeAction: 'close',
    
    // private
    initEvents : function(){
        Ext.MapLog.DetailGrid.superclass.initEvents.call(this);
        if(this.closable){
            var km = this.getKeyMap();
            km.on(27, this.onEsc, this);
            km.disable();
        }
        this.initTools();
    },

    initTools : function(){
        if(this.closable){
            this.addTool({
                id: 'close',
                handler: this[this.closeAction].createDelegate(this, [])
            });
        }
    }, 
    /**
     * Closes the window, removes it from the DOM and destroys the window object.  The beforeclose event is fired
     * before the close happens and will cancel the close action if it returns false.
     */
    close : function(){
        if(this.fireEvent("beforeclose", this) !== false){
            this.hide(null, function(){
                this.fireEvent('close', this);
                this.destroy();
            }, this);
        }
    },

    /**
     * Hides the window, setting it to invisible and applying negative offsets.
     * @param {String/Element} animateTarget (optional) The target element or id to which the window should
     * animate while hiding (defaults to null with no animation)
     * @param {Function} callback (optional) A callback function to call after the window is hidden
     * @param {Object} scope (optional) The scope in which to execute the callback
     */
    hide : function(animateTarget, cb, scope){
        if(this.activeGhost){ // drag active?
            this.hide.defer(100, this, [animateTarget, cb, scope]);
            return;
        }
        if(this.hidden || this.fireEvent("beforehide", this) === false){
            return;
        }
        if(cb){
            this.on('hide', cb, scope, {single:true});
        }
        this.hidden = true;
        if(animateTarget !== undefined){
            this.setAnimateTarget(animateTarget);
        }
        if(this.animateTarget){
            this.animHide();
        }else{
            this.el.hide();
            this.afterHide();
        }
    },

    // private
    afterHide : function(){
        if(this.monitorResize || this.modal || this.constrain || this.constrainHeader){
            Ext.EventManager.removeResizeListener(this.onWindowResize, this);
        }
        if(this.modal){
            this.mask.hide();
            Ext.getBody().removeClass("x-body-masked");
        }
        if(this.keyMap){
            this.keyMap.disable();
        }
        this.fireEvent("hide", this);
    },

    // private
    animHide : function(){
        var tb = this.getBox(false);
        this.el.hide();
        var b = this.animateTarget.getBox();
        b.callback = this.afterHide;
        b.scope = this;
        b.duration = .25;
        b.easing = 'easeNone';
        b.block = true;
        b.opacity = 0;
    },

    /**
     * Sets the target element from which the window should animate while opening.
     * @param {String/Element} el The target element or id
     */
    setAnimateTarget : function(el){
        el = Ext.get(el);
        this.animateTarget = el;
    }

});
