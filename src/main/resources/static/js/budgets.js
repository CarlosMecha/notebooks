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

        // Navigation
        self.activateNavigationLink("budgets");

        // Default date
        self.jquery("#date").val(now());

        // Budget links
        self.jquery(".budgetLink").each(function(i) {
            var elem = self.jquery(this);
            var ref = elem.attr("href");
            var link = self.jquery("<a></a>").attr({href: ref}).text(elem.text());
            elem.text(""); // Remove text.
            elem.append(link);
        });

        self.started = true;
    }

    web.register();

})(window.web);