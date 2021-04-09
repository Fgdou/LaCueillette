function actCart(){
    let list = $(".window.cart .list")

    $.post(api + "cart/get", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            list.html("")

            for(let i=0; i<data.length; i++){

                let sp = data[i]

                let e = $("<tr></tr>")

                e.append($("<td></td>").html())
            }
        }
    }, "json")
}