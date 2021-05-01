let shopmap = null
let shopmarkers = null

function openStore(store){

    openWindow("store")

    if(shopmap === null){
        shopmap = new OpenLayers.Map("shopmap")
        shopmap.addLayer(new OpenLayers.Layer.OSM())
        shopmarkers = new OpenLayers.Layer.Markers()
        shopmap.addLayer(shopmarkers)
    }

    clearMarkersShop()


    let name = $(".window.store .title")
    let owner = $(".window.store .owner")
    let address = $(".window.store .address")
    let category = $(".window.store .category")
    let description = $(".window.store .description")
    let products = $(".window.store .products")


    name.html(store.name)
    owner.html(store.seller.name + " " + store.seller.surname)
    address.html(getAddress(store.address))
    category.html(store.type.name)
    description.html(store.description)

    addMarkerShop(store.address)

    $.post(api + "/product/get/byStore", {store_id: store.id}, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            $(".window.store .products *").off()
            products.html("")
            for(let i=0; i<data.length; i++){
                let product = data[i]

                let div = $("<div></div>").addClass("element")
                let name = $("<span></span>").addClass("name").html(product.name)
                let price = $("<span></span>").addClass("price").html(getPrice(product))
                let category = $("<span></span>").addClass("category").html(product.category.name)

                div.append(name)
                div.append(price)
                div.append(category)
                div.addClass("clickable")
                div.attr("title", product.name)

                products.append(div)
                div.click(()=>actProduct(product))
            }
        }
    }, "json")
}

function clearMarkersShop(){
    shopmarkers.clearMarkers()
}
function addMarkerShop(address, e){
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

        shopmarkers.addMarker(marker)

        var newBound = shopmarkers.getDataExtent();
        shopmap.zoomToExtent(newBound);
    }
}