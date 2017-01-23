(function(web){


    web.init = function init() {

        function now() {
            var now = new Date();
            var month = now.getMonth() + 1;
            if(month < 10) {
                month = "0" + month;
            }
            var day = now.getDate();
            if(day < 10) {
                day = "0" + day;
            }
            return [now.getFullYear(), month, day].join('-');
        }

        var self = this;
        if(self.started) {
            return;
        }

        // Notebook code.
        self.notebook = self.jquery("#notebookCode").val();

        // Navigation
        self.activateNavigationLink("expenses");

        // Default date
        self.jquery("#date").val(now());

        self.categories = [];
        self.tags = [];

        // Load categories
        self.log("Loading categories.");
        self.get("/api/v1/notebooks/" + self.notebook + "/categories", {}, function(data) {
            self.log("Retrieved " + data.length + " categories.");
            var selector = self.jquery("#category");

            // TODO: Validation
            self.jquery.each(data, function(index, category) {
                self.categories.push(category);
                selector.append(self.jquery("<option>", {value: category.id, text: category.name}));
            });

            selector.prop("selectedIndex", -1);
        }).fail(function(){
            self.error("Error requesting categories");
        });

        // Load tags
        self.log("Loading tags.");
        self.get("/api/v1/notebooks/" + self.notebook + "/tags", {}, function(data){
            self.log("Retrieved " + data.length + " tags.");
            var selector = self.jquery("#tags");

            // TODO: Validation
            self.jquery.each(data, function(index, tag) {
                self.tags.push(tag);
                selector.append(self.jquery("<option>", {value: tag.code, text: tag.code}));
            });

            selector.prop("selectedIndex", -1);
            selector.select2({tags: true});

        }).fail(function(){
            console.error("Error requesting tags");
            self.displayError("Error requesting tags to the server. Please retry it.");
        });

        // Load budgets
        var selector = self.jquery("#budgets");
        selector.prop("selectedIndex", -1);
        selector.select2();

        self.started = true;

    };

    web.register();

})(window.web);