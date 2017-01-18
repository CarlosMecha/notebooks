(function(web){

    web.init = function init() {
        var self = this;

        // Navigation
        self.activateNavigationLink("categories");

        self.started = true;
    }

    web.register();

})(window.web);