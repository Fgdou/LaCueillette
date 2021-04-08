let city = "Cesson"
let postalcode = 35510

$(()=>{
    $("header .search").submit((e)=>{
        let txt = $("header .search input").val()

        e.preventDefault()

        if(txt === "")
            return

        $.post(api + "product/search", {
            search: txt,
            city: city,
            postalcode: postalcode
        }, data=>{
            if(data.error)
                errorPopup(data.error)
            else
                parseResult(data)
        }, "json")

    })
})

function parseResult(data){
    let list = $(".window.search .searchList")

    list.html("")

    for(let i=0; i<data.length; i++){
        let shop = data[i]

        let e = $("<div></div>").addClass("searchElement")
        e.append($("<img src='files/img/Shop.svg' alt='Shop'>"))

        let content = $("<div></div>").addClass("content")

        content.append(createElement("name", shop.name))
        content.append(createElement("name", shop.name))
        content.append(createElement("address", shop.address.number + " " + shop.address.way + " - " + shop.address.postalcode + " " + shop.address.city))
        content.append(createElement("name", shop.name))

        e.append(content)
    }

    function createElement(name, content){
        let div = $("<div></div>").addClass(name)
        let span = $("<span></span>")
        span.html(content)
        div.append(span)
        return div
    }
}