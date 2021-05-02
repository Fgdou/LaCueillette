$(()=>{
    $("header .cart").click(()=> {
        if(user !== null) {
            actCart()
            openWindow("cart")
        }else{
            openWindow("login")
        }
    })
    $(".window.cart .validate").click(()=>{
        $(".window.chooseAddress").css("display", "block")
        actAddresses()
    })
    $(".window.chooseAddress form").click(e=>e.preventDefault())
    $(".window.chooseAddress .cancel").click((e)=>{
        e.preventDefault()
        $(".window.chooseAddress").css("display", "none")
    })
    $(".window.chooseAddress .submit").click(()=>{
        validateCart()
    })
})
function validateCart(){

    let addr = $(".window.chooseAddress select").val()

    $.post(api + "cart/validate", {
        user_token: token,
        address_id: addr
    }, data=>{
        if(data.error())
            errorPopup(data.error)
        else{
            $(".window.chooseAddress").css("display", "none")
            editUser()
        }
    }, "json")
}
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
            $(".window.cart .list *").off()
            list.html("")

            $("header .cart span").html("Panier ("+data.subProducts.length+")")
            $(".window.cart .totalht").html("Total HT: " + data.priceHT.toFixed(2) + " €")
            $(".window.cart .totalttc").html("Total TTC: " + data.priceTTC.toFixed(2) + " €")

            for(let i=0; i<data.subProducts.length; i++){


                let sp = data.subProducts[i]
                let quantity = data.quantities[i]

                let e = $("<tr></tr>")
                let select = $("<select></select>").addClass("dropDown")
                let name = $("<span></span>").html(sp.product.name).addClass("clickable")
                let price = $("<span></span>").html(getPriceOnly(sp, quantity))

                fillSelect(select, sp.quantity)
                select.val(quantity)

                name.click(()=>{
                    actProduct(sp.product)
                })
                select.change(()=> {
                    changeCart(sp, select.val())
                })

                e.append($("<td></td>").html(name))
                e.append($("<td></td>").html(sp.special_tag))
                e.append($("<td></td>").addClass("number").html(select))
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
            actCart()
        }
    }, "json")
}