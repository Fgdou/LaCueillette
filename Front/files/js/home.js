$(()=>{

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