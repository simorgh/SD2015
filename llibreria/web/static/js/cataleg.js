/* 
 * WebService JavaScript
 * Get best price from all IP libraries of a specific item name.
 */

var search_item_name;
var products = [];

var addProduct = function(data) {
    products.push(data);
};

var requestService = function(item, key, callback) {
    var httpitem = "http://" + item;
    var videoUrl = httpitem+"/API/VIDEO/item/"+search_item_name;
    var audioUrl = httpitem+"/API/AUDIO/item/"+search_item_name;
    var bookUrl = httpitem+"/API/BOOK/item/"+search_item_name;
    
    //TODO $.ajax(videoUrl...complete: function() { $.ajax(audioUrl...complete:function() {$.ajax(bookUrl...sortProducts)
    async.parallel([
        function(cb) {
            $.ajax({
                url: videoUrl,
                method: "GET",
                dataType: "json",
                success: addProduct,
                error: function() {},
                complete: function() {cb(null);}
            });
        },
        function(cb) {
            $.ajax({
                url: audioUrl,
                method: "GET",
                dataType: "json",
                success: addProduct,
                error: function() {},
                complete: function() {cb(null);}
            });
        },
        function(cb) {
            $.ajax({
                url: bookUrl,
                method: "GET",
                dataType: "json",
                success: addProduct,
                error: function() {},
                complete: function() {cb(null);}
            });
        }
    ], function() {
        callback(null);
    });

};

// Results visualization
var createItemFunction = function(p) {
    var s;
    s = "<table>";
    s += "<tr>";
    s += "<td><b>"+p["NAME"]+"</b></td>";
    s += "<td>";
    s += "<div> Description: <b>"+ p["DESC"]+"</b></div>";
    s += "</td>";
    s += "<td>"+p["PRICE"]+ "€</td>";
    s += "<td>";
    s += "<a href=\""+p["LINK"]+"\">"+p["LINK"]+"</a>";
    s += "</td>";
    s += "</tr>";
    s += "</table>";
    return $(s);
};

var sortProducts = function(err) {
    var s = function(p1, p2){
        var keyA = parseFloat(p1["PRICE"]),
            keyB = parseFloat(p2["PRICE"]);
        // Compare the 2 dates
        if(keyA < keyB) return -1;
        if(keyA > keyB) return 1;
        return 0;
    };
    products.sort(s);
    var searchResult = $("#search-result");
    for (var i in products) {
        var p = products[i];
        var div = $("<div class='row'></div>");
        div.append(createItemFunction(p));
        searchResult.append(div);
    }
};

var onLlibreriesReceived = function(data, status) {
    var ips = data.split("\n");
    //TODO: here we should use jquery 'when', once for each IPs are succesfully processed on requestService
    async.forEachOf(ips, requestService, sortProducts);
};

var searchClick = function() {
    search_item_name = $("#search-product").val();
    $("#search-result").empty();
    products = [];
    $.ajax({
        url: "/static/llibreries.txt",
        method: "get",
        dataType: "text",
        success:onLlibreriesReceived
    });
};

$(document).ready(function() {
    $("#update-btn").click(searchClick);
});




