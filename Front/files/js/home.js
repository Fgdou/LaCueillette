$(()=>{
    $(".window.home .search").submit((e)=>{
        e.preventDefault()
        $("header .search input").val($(".window.home .search input").val())
        search()
    })
    $(".window.home .search input").focus(()=>{
        $(".window.home .search input").select()
    })
    $(".window.home .search img").click(()=>{
        $("header .search input").val($(".window.home .search input").val())
        search()
    })
})

function actCategory(){
    let container = $(".window.home .categoryList")

    $.post(api + "/store/type/getAll", {user_token: token}, data=>{
        if(data.error)
            errorPopup(data)
        else{
            container.html("")

            for(let i=0; i<data.length; i++){
                let e = data[i]

                let div = $("<div></div>").addClass("element clickable").html(e.name)

                div.click(()=>{
                    $("header .search input").val(e.name)
                    search()
                })

                container.append(div)
            }
        }
    })
}
function actStores(){
    let container = $(".window.home .shopList")

    $.post(api + "/store/get/byCity", {city: city, postalcode: postalcode}, data=>{
        if(data.error)
            errorPopup(data)
        else{
            container.html("")

            for(let i=0; i<data.length; i++){
                let e = data[i]

                let div = $("<div></div>").addClass("element clickable")
                let name = $("<span></span>").addClass("name").html(e.name)
                let cat = $("<span></span>").addClass("category").html(e.type.name)

                div.append(name)
                div.append(cat)

                div.click(()=>{
                })

                container.append(div)
            }
        }
    })
}