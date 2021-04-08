let city = "Cesson"
let postalcode = 35510

$(()=>{
    $("header .search").submit((e)=>{
        let txt = $("header .search input").val()

        e.preventDefault()

        if(txt === "")
            return

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

    })
})

function parseShopResult(data){
    let list = $(".window.search .searchList")

    list.html("")

    for(let i=0; i<data.length; i++){
        let shop = data[i]

        let e = $("<div></div>").addClass("searchElement")
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

    list.html("")

    for(let i=0; i<data.length; i++){
        let product = data[i]
        let shop =

        let e = $("<div></div>").addClass("searchElement")
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