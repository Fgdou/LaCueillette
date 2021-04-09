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

        content.append(createElement("name", shop.name))

        let infos = $("<div></div>").addClass("infos")

        infos.append($("<span></span>").addClass("category").html(shop.type.name))
        infos.append($("<span></span>").addClass("distance").html("300m"))

        content.append(infos)
        content.append(createElement("address", shop.address.number + " " + shop.address.way + " - " + shop.address.postalcode + " " + shop.address.city))
        content.append(createElement("opening", shop.timeTable.isOpen))

        e.append(content)

        e.click(()=>actShop(shop))

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
function parseProductResult(data){
    let list = $(".window.search .searchList")

    for(let i=0; i<data.length; i++){
        let product = data[i]
        let shop = product.store

        let e = $("<div></div>").addClass("searchElement clickable")
        e.append($("<img src='files/img/Paniervide.svg' alt='Shop'>"))

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
    if(product.priceKg)
        return product.price.toFixed(2) + " €/kg"
    else
        return product.price.toFixed(2) + " €"
}