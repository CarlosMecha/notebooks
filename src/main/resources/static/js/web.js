(function(document, window){

    /*
     * Web state and functions.
     */
    window.web = {
        document: document,
        window: window,
        jquery: window.jQuery,
        get: window.jQuery.get,
        errorPanel: function getErrorPanel() {
            return this.jquery("#errorPanel");
        },
        log: function log(message) {
            console.log(message);
        },
        error: function error(message) {
            console.error(message);
            var panel = this.errorPanel();
            panel.css("display", "block");
            panel.text(message);
        },
        hideError: function hideErrorPanel() {
            var panel = this.errorPanel();
            panel.css("display", "none");
            panel.text("");
        },
        started: false,
        init: function() {
            this.started = true;
        },
        register: function register() {
            var self = this;
            this.jquery(this.document).ready(function() {
                self.log("Loading web");
                self.hideError();
                self.init();
            });
        },
        activateNavigationLink: function activateNavigationLink(link) {
            this.log("Activating link " + link);
            this.jquery("#" + link + "Nav").addClass("active");
            this.jquery("#" + link + "Link").attr("href", "#");
        }
    };

})(document, window);