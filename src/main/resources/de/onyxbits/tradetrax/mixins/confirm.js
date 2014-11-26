
// Based on http://wiki.apache.org/tapestry/Tapestry5AndJavaScriptExplained

// A class that attaches a confirmation box (with logic)  to
// the 'onclick' event of any HTML element.
// @author Chris Lewis Apr 18, 2008 <chris@thegodcode.net>

Confirm = Class.create({
        
    initialize: function(elementId, message) {
        this.message = message;
        Event.observe($(elementId), 'click', this.doConfirm.bindAsEventListener(this));
    },
    
    doConfirm: function(e) {
        
        // Pop up a javascript Confirm Box (see http://www.w3schools.com/js/js_popup.asp)
        
        if (!confirm(this.message)) {
                e.stop();
        }
    }
        
})

// Extend the Tapestry.Initializer with a static method that instantiates a Confirm.

Tapestry.Initializer.confirm = function(spec) {
    new Confirm(spec.elementId, spec.message);
}

