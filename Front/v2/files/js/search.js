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
                ;//TODO show products
        }, "json")

    })
})