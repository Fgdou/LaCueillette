$(()=>{
    $("header .cart").click(()=> {
        if(user !== null) {
            actCart()
            openWindow("cart")
        }else{
            openWindow("login")
        }
    })
})

function actCart(){
    if(user === null)
        return

    let list = $(".window.cart .list")

    $.post(api + "cart/get", {
        user_token: token
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            list.html("")

            $("header .cart span").html("Panier ("+data.subProducts.length+")")

            for(let i=0; i<data.subProducts.length; i++){

                let sp = data.subProducts[i]
                let quantity = data.quantities[i]

                let e = $("<tr></tr>")
                let select = $("<input type='number' value='"+quantity+"' max='"+sp.quantity+"' min='0'>")
                let name = $("<span></span>").html(sp.product.name).addClass("clickable")
                let price = $("<span></span>").html(getPriceOnly(sp, quantity))

                name.click(()=>{
                    actProduct(sp.product)
                })
                select.change(()=> {
                    changeCart(sp, select.val())
                    price.html(getPriceOnly(sp, select.val()))
                })

                e.append($("<td></td>").html(name))
                e.append($("<td></td>").html(sp.special_tag))
                e.append($("<td></td>").html(select))
                e.append($("<td></td>").html(price))

                list.append(e)
            }
        }
    }, "json")

}
function getPriceOnly(subproduct, quantity){
    if(subproduct.product.priceKg){
        return subproduct.product.price * quantity/1000 + " €"
    }else{
        return subproduct.product.price * quantity + " €"
    }
}

function changeCart(sp, quantity){
    $.post(api + "/cart/modify", {
        user_token: token,
        subproduct_id: sp.id,
        quantity: quantity
    }, data=>{
        if(data.error)
            errorPopup(data.error)
        else{
            if(quantity == 0)
                actCart()
        }
    }, "json")
}