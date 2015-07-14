OpenLayers.ElementsIndexer.prototype.removeText = OpenLayers.Renderer.SVG.prototype.removeText = OpenLayers.Renderer.VML.prototype.removeText = function(featureId) {
        var label = document.getElementById(featureId + this.LABEL_ID_SUFFIX);
        if (label) {
            this.textRoot.removeChild(label);
        }
        var labelBackground = document.getElementById(featureId + this.LABEL_ID_SUFFIX + "_bg"); 
 	if (labelBackground) { 
 	    this.textRoot.removeChild(labelBackground); 
 	}
    };

OpenLayers.Renderer.VML.prototype.drawText =  function(featureId, style, location) {
        var label = this.nodeFactory(featureId + this.LABEL_ID_SUFFIX, "olv:rect");
        var textbox = this.nodeFactory(featureId + this.LABEL_ID_SUFFIX + "_textbox", "olv:textbox");
        
        var resolution = this.getResolution();
        label.style.left = ((location.x/resolution - this.offset.x) | 0) + "px";
        label.style.top = ((location.y/resolution - this.offset.y) | 0) + "px";
        label.style.flip = "y";

        textbox.innerText = style.label;

        if (style.fontColor) {
            textbox.style.color = style.fontColor;
        }
        if (style.fontOpacity) {
            textbox.style.filter = 'alpha(opacity=' + (style.fontOpacity * 100) + ')';
        }
 	if (style.labelBackgroundColor) { 
 	    textbox.style.backgroundColor = style.labelBackgroundColor; 
 	} 
 	if (style.labelBorderColor || style.labelBorderSize) { 
 	    textbox.style.border = "solid " + 
 	        (style.labelBorderSize ? style.labelBorderSize : "1px") + " " + 
 	        (style.labelBorderColor ? style.labelBorderColor : "#000000"); 
 	} 
 	 
        if (style.fontFamily) {
            textbox.style.fontFamily = style.fontFamily;
        }
        if (style.fontSize) {
            textbox.style.fontSize = style.fontSize;
        }
        if (style.fontWeight) {
            textbox.style.fontWeight = style.fontWeight;
        }
        if(style.labelSelect === true) {
            label._featureId = featureId;
            textbox._featureId = featureId;
            textbox._geometry = location;
            textbox._geometryClass = location.CLASS_NAME;
        }

        textbox.style.whiteSpace = "nowrap";
        // fun with IE: IE7 in standards compliant mode does not display any
        // text with a left inset of 0. So we set this to 1px and subtract one
        // pixel later when we set label.style.left
        textbox.inset = "1px,0px,0px,0px";

        if(!label.parentNode) {
            label.appendChild(textbox);
            this.textRoot.appendChild(label);
        }

        var align = style.labelAlign || "cm";
        var xshift = textbox.clientWidth *
            (OpenLayers.Renderer.VML.LABEL_SHIFT[align[0] || "c"]);
        var yshift = textbox.clientHeight *
            (OpenLayers.Renderer.VML.LABEL_SHIFT[align[1] || "m"]);
        label.style.left = parseInt(label.style.left)-xshift-1+"px";
        label.style.top = parseInt(label.style.top)+yshift+"px";
        
    };


OpenLayers.Renderer.SVG.prototype.drawText = function(featureId, style, location) {
        var resolution = this.getResolution();
        
        var x = (location.x / resolution + this.left);
        var y = (location.y / resolution - this.top);
        
        var label = this.nodeFactory(featureId + this.LABEL_ID_SUFFIX, "text");
        var tspan = this.nodeFactory(featureId + this.LABEL_ID_SUFFIX + "_tspan", "tspan");

        label.setAttributeNS(null, "x", x);
        label.setAttributeNS(null, "y", -y);
        
        if (style.fontColor) {
            label.setAttributeNS(null, "fill", style.fontColor);
        }
        if (style.fontOpacity) {
            label.setAttributeNS(null, "opacity", style.fontOpacity);
        }
        if (style.fontFamily) {
            label.setAttributeNS(null, "font-family", style.fontFamily);
        }
        if (style.fontSize) {
            label.setAttributeNS(null, "font-size", style.fontSize);
        }
        if (style.fontWeight) {
            label.setAttributeNS(null, "font-weight", style.fontWeight);
        }
        if(style.labelSelect === true) {
            label.setAttributeNS(null, "pointer-events", "visible");
            label._featureId = featureId;
            tspan._featureId = featureId;
            tspan._geometry = location;
            tspan._geometryClass = location.CLASS_NAME;
        } else {
            label.setAttributeNS(null, "pointer-events", "none");
        }
        var align = style.labelAlign || "cm";
        label.setAttributeNS(null, "text-anchor",
            OpenLayers.Renderer.SVG.LABEL_ALIGN[align[0]] || "middle");

        if (this.isGecko) {
            label.setAttributeNS(null, "dominant-baseline",
                OpenLayers.Renderer.SVG.LABEL_ALIGN[align[1]] || "central");
        } else {
            tspan.setAttributeNS(null, "baseline-shift",
                OpenLayers.Renderer.SVG.LABEL_VSHIFT[align[1]] || "-35%");
        }

        tspan.textContent = style.label;
        
        if(!label.parentNode) {
            label.appendChild(tspan);
            this.textRoot.appendChild(label);
        }   

 	if (style.labelBackgroundColor || 
 	        style.labelBorderColor || 
 	        style.labelBorderSize) { 

            var bg = this.nodeFactory(featureId + this.LABEL_ID_SUFFIX + "_bg", "rect"); 
 	    if (style.labelBackgroundColor) { 
 	        bg.setAttributeNS(null, "fill", style.labelBackgroundColor); 
 	    } 
 	    if (style.labelBorderColor || style.labelBorderSize) { 
 	        bg.setAttributeNS(null, "stroke",  
 	            (style.labelBorderColor ? style.labelBorderColor : "#000000")); 
 	        bg.setAttributeNS(null, "stroke-width",  
 	            (style.labelBorderSize ? style.labelBorderSize : "0.5")); 
 	    } 
            if (style.fontOpacity) {
                bg.setAttributeNS(null, "opacity", style.fontOpacity);
            }
            
            var bbox;
            try {
                bbox = label.getBBox(); 
            }catch(e){}

            if (bbox) {
                var labelWidth = bbox.width; 
                var labelHeight = bbox.height; 
                var padding = 2; 
                if (style.labelPadding) { 
                    var pos = style.labelPadding.indexOf("px"); 
                    if (pos == -1) { 
                        padding = style.labelPadding; 
                    } else { 
                        padding = parseInt(style.labelPadding.substr(0, pos)); 
                    } 
                } 
                bg.setAttributeNS(null, "x", bbox.x-padding); 
                bg.setAttributeNS(null, "y", bbox.y-padding); 
                bg.setAttributeNS(null, "height", (labelHeight+padding*2)+"px"); 
                bg.setAttributeNS(null, "width", (labelWidth+padding*2)+"px"); 

                this.textRoot.insertBefore(bg, label); 
            }
 	}       
};
