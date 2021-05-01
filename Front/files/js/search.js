var markers = null
var map = null
var marker_cache = []

$(()=>{
    $("header .search").submit((e)=>{
        e.preventDefault()
        search()
    })
    $("header .search input").focus(()=>{
        $("header .search input").select()
    })
    $("header .search img").click(()=>{
        search()
    })
})

function search(){


    let txt = $("header .search input").val()
    if(txt === "")
        return

    $(".window.search .searchList").html("")
    openWindow("search")

    if(map === null){
        map = new OpenLayers.Map("map")
        map.addLayer(new OpenLayers.Layer.OSM())
        markers = new OpenLayers.Layer.Markers()
        map.addLayer(markers)
    }

    clearMarkers()

    $.post(api + "Store/search", {
        search: txt,
        city: city,
        postalcode: postalcode
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            parseShopResult(data)
    }, "json")
    $.post(api + "product/search", {
        search: txt,
        city: city,
        postalcode: postalcode
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else
            parseProductResult(data)
    }, "json")
}

function parseShopResult(data){
    let list = $(".window.search .searchList")

    for(let i=0; i<data.length; i++){
        let shop = data[i]

        let e = $("<div></div>").addClass("searchElement clickable")
        e.append($("<img src='files/img/Shop.svg' alt='Shop'>"))

        let content = $("<div></div>").addClass("content")

        e.attr("title", shop.name)

        content.append(createElement("name", shop.name))

        let infos = $("<div></div>").addClass("infos")

        infos.append($("<span></span>").addClass("category").html(shop.type.name))
        infos.append($("<span></span>").addClass("distance").html("300m"))

        content.append(infos)
        content.append(createElement("address", shop.address.number + " " + shop.address.way + " - " + shop.address.postalcode + " " + shop.address.city))
        content.append(createElement("opening", shop.timeTable.isOpen))

        e.append(content)

        e.click(()=>actShop(shop))

        addMarker(shop.address, e)

        list.append(e)
    }

    function createElement(name, content){
        let div = $("<div></div>").addClass(name)
        let span = $("<span></span>")
        span.html(content)
        div.append(span)
        return div
    }
}
function clearMarkers(){
    markers.clearMarkers()
}
function addMarker(address, e){
    let str = address.number + ' ' + address.way + ', ' + address.postalcode + ' ' + address.city + ' , France'

    if(marker_cache[str] === "")
    {}
    else if(marker_cache[str])
        putMarker(marker_cache[str])
    else {
        marker_cache[str] = ""
        $.get("https://nominatim.openstreetmap.org/search?q=" + str + "&format=json", {}, data => {
            if (data.length >= 1) {
                var fromProjection = new OpenLayers.Projection("EPSG:4326");   // Transform from WGS 1984
                var toProjection = new OpenLayers.Projection("EPSG:900913"); // to Spherical Mercator Projection

                position = new OpenLayers.LonLat(data[0].lon, data[0].lat).transform(fromProjection, toProjection)

                marker_cache[str] = position

                putMarker(position)
            }
        }, "json")
    }

    function putMarker(position){
        var marker = new OpenLayers.Marker(position)

        markers.addMarker(marker)

        var newBound = markers.getDataExtent();
        map.zoomToExtent(newBound);

        marker.events.register("click", map, hoverMarker)
    }
}
function hoverMarker(e){
    console.log(e)
}
function parseProductResult(data){
    let list = $(".window.search .searchList")

    for(let i=0; i<data.length; i++){
        let product = data[i]
        let shop = product.store

        let e = $("<div></div>").addClass("searchElement clickable")
        e.append($("<img src='files/img/Paniervide.svg' alt='Shop'>"))

        e.attr("title", product.name)

        let content = $("<div></div>").addClass("content")

        content.append(createElement("name", getPrice(product) + " - " + product.name))

        let infos = $("<div></div>").addClass("infos")

        infos.append($("<span></span>").addClass("category").html(shop.name))
        infos.append($("<span></span>").addClass("distance").html(shop.type.name))

        content.append(infos)
        content.append(createElement("address", getAddress(shop.address)))
        content.append(createElement("opening", shop.timeTable.isOpen))

        e.append(content)

        e.click(()=>actProduct(product))


        addMarker(shop.address, e)

        list.append(e)
    }


    function createElement(name, content){
        let div = $("<div></div>").addClass(name)
        let span = $("<span></span>")
        span.html(content)
        div.append(span)
        return div
    }
}
function getAddress(address){
    return address.number + " " + address.way + " - " + address.postalcode + " " + address.city
}
function getPrice(product){
    let price = product.price * (1 + product.tva)
    if(product.priceKg)
        return price.toFixed(2) + " €/kg"
    else
        return price.toFixed(2) + " €"
}