let city = "cesson"
let postalcode = 35510

$(()=>{
    $("header .search").submit((e)=>{
        let txt = $("header .search input").val()

        e.preventDefault()

        if(txt === "")
            return

        $.post(api + "product/searchTag", {
            tags: txt,
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