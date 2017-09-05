(function(web){

    web.init = function init() {
        var self = this;

        // Navigation
        self.activateNavigationLink("notebooks");

        // Expenses links
        self.jquery(".expensesLink").each(function(i) {
            var elem = self.jquery(this);
            var ref = elem.attr("href");
            var link = self.jquery("<a></a>").attr({href: ref}).text(elem.text());
            elem.text(""); // Remove text.
            elem.append(link);
        });

        // Pages links
        self.jquery(".pagesLink").each(function(i) {
            var elem = self.jquery(this);
            var ref = elem.attr("href");
            var link = self.jquery("<a></a>").attr({href: ref}).text(elem.text());
            elem.text(""); // Remove text.
            elem.append(link);
        });

        // Budgets links
        self.jquery(".budgetsLink").each(function(i) {
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