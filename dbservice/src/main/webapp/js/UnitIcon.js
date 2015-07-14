/**
 * Special marker icon with special features. The icon can have up to 9
 * subicons, orderd in the raster matris of size 3x3
 * @author Jachym
 */

MapLog.UnitIcon = OpenLayers.Class(OpenLayers.Icon, {
    /**
     * subIcons
     */
    subIcons: [],

    /**
     * backgroundIcon
     */
    alertIcon: null,

    /**
     * subIconSize
     * @type {Integer}
     */
    subIconSize: 13,

    /**
     * popup
     */
    popup: null,

    /**
     * configuration options
     * @type {Object}
     */
    options: {},

    /**
     * empty image
     * @type {String}
     */
    emptyImage: "icons/empty.png",

    /**
     * @constructor
     * @param {Object} options configuration for this icon
     */
    initialize: function(url, size, offset, calculateOffset,options) {
        this.subIcons = [];
        OpenLayers.Icon.prototype.initialize.apply(this,[url,size,offset,calculateOffset]);
        this.options = options;

        var id = OpenLayers.Util.createUniqueID("OL_AlertIcon_");
        this.alertIcon = OpenLayers.Util.createAlphaImageDiv(id,
                                                new OpenLayers.Pixel(0,0), // px - {<OpenLayers.Pixel>} left and top position
                                                new OpenLayers.Size(40,21), // sz - {<OpenLayers.Size>}
                                                "icons/alert.gif",                   // imgURL
                                                "absolute");        // position - {String}
        this.alertIcon.style.zIndex = -1;
        this.imageDiv.appendChild(this.alertIcon);
        this.toggleAlert(false);

        for (var i = 0; i < 9; i++) {
            var id = OpenLayers.Util.createUniqueID("OL_SubIcon_");
            this.subIcons.push(OpenLayers.Util.createAlphaImageDiv(id+""+i,
                                                this._getSubIconPosition(i), // px - {<OpenLayers.Pixel>} left and top position
                                                new OpenLayers.Size(this.subIconSize,this.subIconSize), // sz - {<OpenLayers.Size>}
                                                this.emptyImage,                   // imgURL
                                                "absolute"));        // position - {String}
            this.imageDiv.appendChild(this.subIcons[i]);
        }
    },

    /**
     * Display event
     * @name MapLog.UnitIcon.toggleDisplayEvent
     * @param {Boolean} display display or hide
     * @param {Integer} idx index of the event (0-9)
     * @param {ulr} url to the icon
     */
    toggleDisplayEvent: function(display,idx,url)  {
        if (display) {
            this.subIcons[idx].style.display = "block";
            OpenLayers.Util.modifyAlphaImageDiv(this.subIcons[idx],
                                    null,
                                    null,
                                    null,
                                    url);
        }
        else {
            this.subIcons[idx].style.display = "none";
        }
    },

    /**
     * return the right subicon position
     * @function
     * @private
     * @name MapLog.UnitIcon._getSubIconPosition
     * @param {Integer} idx
     * @returns {OpenLayers.Pixel}
     */
    _getSubIconPosition: function(idx) {
        switch(idx) {
            case 0: return new OpenLayers.Pixel(this.subIconSize*0,this.subIconSize*0);
               break;                                                                
            case 1: return new OpenLayers.Pixel(this.subIconSize*1,this.subIconSize*0);
               break;                                                                
            case 2: return new OpenLayers.Pixel(this.subIconSize*2,this.subIconSize*0);
               break;                                                                
            case 3: return new OpenLayers.Pixel(this.subIconSize*0,this.subIconSize*1);
               break;                                                                
            case 4: return new OpenLayers.Pixel(this.subIconSize*1,this.subIconSize*1);
               break;                                                                
            case 5: return new OpenLayers.Pixel(this.subIconSize*2,this.subIconSize*1);
               break;                                                                
            case 6: return new OpenLayers.Pixel(this.subIconSize*0,this.subIconSize*2);
               break;                                                                
            case 7: return new OpenLayers.Pixel(this.subIconSize*1,this.subIconSize*2);
               break;
            case 8: return new OpenLayers.Pixel(this.subIconSize*2,this.subIconSize*2);
               break;
            default: return new OpenLayers.Pixel(this.subIconSize*1,this.subIconSize*1);
        }
    },

    /**
     * toggle the icons alert state
     * @function
     * @private
     * @name MapLog.UnitIcon.toggleAlert
     * @param {Boolean} al
     */
    toggleAlert: function(al) {
        if(al) {
            this.alertIcon.style.display = "block";
        }
        else {
            this.alertIcon.style.display = "none";
        }
    },

    /**
     * blick with the icon Ntimes
     * @function
     * @name MapLog.UnitIcon.blick
     * @param {Integer} how many times (default 3)
     */
    blick: function(times) {
        if (!times) {
            times = 3;
        }

        var interval  = 300;

        for (var i = 0; i < times; i++) {
            window.setTimeout("document.getElementById(\""+this.imageDiv.id+"\").style.display=\"none\";",
                    i*interval*2);
            window.setTimeout("document.getElementById(\""+this.imageDiv.id+"\").style.display=\"block\";",
                    i*interval*2+interval);
        }
    },


    /**
     * clone this UnitIcon
     * @returns {MapLog.UnitIcon}
     */
    clone: function() {
        return new MapLog.UnitIcon(
                this.url,
                this.size,
                this.offset,
                this.calculateOffset,
                this.options
                );
    },

    CLASS_NAME: "MapLog.UnitIcon"
});
